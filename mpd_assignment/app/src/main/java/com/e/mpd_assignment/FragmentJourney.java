package com.e.mpd_assignment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/*
    Handles the entire journey menu, switches 'state' based on what the user is doing;
    View default (journey homescreen, can load/delete saved journeys)
        -> Plan a journey
            -> View planned journey
                -> Save planned journey
 */
import static android.content.Context.INPUT_METHOD_SERVICE;

public class FragmentJourney extends Fragment implements View.OnClickListener,
        RecyclerViewSavedJourney.ItemClickListener, RecyclerViewSavedJourney.ItemClickListener2,
        OnMapReadyCallback, LoadResourceJSON.LoadResourceJSONListener {

    private View view;
    private Button buttonPlanJourney;
    private Button buttonBack;
    private Button buttonSaveJourney;
    private Button buttonReset;
    private Button buttonCreateJourney;

    private RecyclerViewSavedJourney recyclerViewSavedJourney;
    private RecyclerViewJourneySteps recyclerViewJourneySteps;
    private DataRepository dataRepository;

    private EditText startLocation;
    private EditText endLocation;
    private DatePicker datePicker;
    private TextView errorText;

    private ConstraintLayout parentGroup;

    private JourneyListener callback;
    private Polyline polyline;

    private GoogleMap map;

    private double journeyStartLat;
    private double journeyStartLng;
    private double journeyEndLat;
    private double journeyEndLng;
    private Calendar journeyDate;
    private String journeyStartLocation;
    private String journeyEndLocation;
    private List<List<HashMap<String, String>>> journeyResult;

    private Context context;

    private LayoutInflater inflater;

    private boolean loadJourney = false;
    private Calendar loadCalendar;

    private JourneyRepository journeyRepository;

    FragmentJourney(DataRepository dataRepository, RecyclerViewSavedJourney recyclerViewSavedJourney, Context context, JourneyRepository journeyRepository){
        this.journeyRepository = journeyRepository;
        this.dataRepository = dataRepository;
        this.recyclerViewSavedJourney = recyclerViewSavedJourney;
        this.context = context;
    }

    //Because the dataRepository loads the serialized journeyRepository, reload it to the loaded version.
    void updateRepo(JourneyRepository jr){
        this.journeyRepository = jr;
    }

    //asynctask callback, once the data is gathered from maps and parsed, draws routes on screen.
    @Override
    public void dataLoaded(List<List<HashMap<String, String>>> result){

        journeyResult = result;
        dataRepository.resetRecyclerJourney();

        //since data is now loaded, we can save journey.
        buttonSaveJourney.setVisibility(View.VISIBLE);

        //use a latlngbounds builder which can be used to automatically position the map based on input LatLng's
        LatLngBounds.Builder builder = LatLngBounds.builder();

        ArrayList<Incident> incidents = dataRepository.getIncidentArrayList();
        ArrayList<Roadwork> roadworks = dataRepository.getRoadworkArrayList();
        ArrayList<PlannedRoadwork> planned = dataRepository.getPlannedRoadworkArrayList();

        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;

        long picked;
        if(loadJourney){
            loadJourney = false;
            picked = loadCalendar.getTimeInMillis();
            journeyDate = loadCalendar;
        }else{
            Calendar calendar = new GregorianCalendar();
            calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
            calendar.set(Calendar.MONTH, datePicker.getMonth() -1);
            calendar.set(Calendar.YEAR, datePicker.getYear());
            picked = calendar.getTimeInMillis();
            journeyDate = calendar;
        }

        // Traversing through all the routes
        for(int i=0;i<result.size();i++){
            points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));

                LatLng position = new LatLng(lat, lng);

                builder.include(position);

                //These 3 loops check to see if the route passes within 10meters of a roadwork. if it does it gets added to the journey steps and drawn on map
                //quite CPU intensive, can update if the app is to be published to app store.
                Date today = new Date();
                if(today.getYear() == datePicker.getYear() && today.getMonth() == datePicker.getMonth() && today.getDay() == datePicker.getDayOfMonth()){
                    for(int k = 0, l = incidents.size(); k<l; k++){
                        Incident incident = incidents.get(k);
                        if(calculateDistance(incident.getLat(), incident.getLon(), position.latitude, position.longitude)){
                            map.addMarker(new MarkerOptions().position(new LatLng(incident.getLat(), incident.getLon()))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.incidenticon)));
                            dataRepository.addRecyclerJourneyStep("Incident: "+incident.getTitle());
                        }
                    }
                }
                for(int n = 0, m = roadworks.size(); n < m; n++){
                    Roadwork roadwork = roadworks.get(n);
                    long start = roadwork.getStartDate();
                    long end = roadwork.getEndDate();
                    if(checkPosition(position.latitude, position.longitude, roadwork.getLat(), roadwork.getLon(), picked, start, end)){
                        map.addMarker(new MarkerOptions().position(new LatLng(roadwork.getLat(), roadwork.getLon()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.roadworksicon)));
                        dataRepository.addRecyclerJourneyStep("Roadworks: "+roadwork.getTitle());
                    }
                }
                for(int y = 0, u = planned.size(); y<u; y++){
                    PlannedRoadwork plannedRoadwork = planned.get(y);
                    long start = plannedRoadwork.getStartDate();
                    long end = plannedRoadwork.getEndDate();
                    if(checkPosition(position.latitude, position.longitude, plannedRoadwork.getLat(), plannedRoadwork.getLon(), picked, start, end)){
                        map.addMarker(new MarkerOptions().position(new LatLng(plannedRoadwork.getLat(), plannedRoadwork.getLon()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.plannedroadworksicon)));
                        dataRepository.addRecyclerJourneyStep("Roadworks: "+plannedRoadwork.getTitle());
                    }
                }

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(1);
            lineOptions.color(Color.RED);
        }

        // Drawing polyline in the Google Map for the i-th route
        if(lineOptions != null) {
            if(polyline != null){
                polyline.remove();
            }
            polyline = map.addPolyline(lineOptions);
        }

        //finish the builder
        LatLngBounds bounds = builder.build();
        //move the camera to builder bounds
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        //tell the recycler to update
        recyclerViewJourneySteps.notifyDataSetChanged();
    }

    //calculate distance within 10m, and then check if the departure date is within the roadworks range.
    private boolean checkPosition(double lat1, double lng1, double lat2, double lng2, long picked, long start, long end){
        if(calculateDistance(lat1, lng1, lat2, lng2)){
            return picked >= start && picked <= end;
        }
        return false;
    }

    //calculate the distance between two points, returns boolean if within input range
    private boolean calculateDistance(double lat1, double lon1, double lat2, double lon2){
        double temp = lon1-lon2;
        double dist = Math.sin(dtor(lat1))*Math.sin(dtor(lat2))+
                Math.cos(dtor(lat1))*Math.cos(dtor(lat2))*Math.cos(dtor(temp));
        dist = Math.acos(dist);
        dist = dist*180/Math.PI;
        dist = dist * 60 * 1.1515;
        //if within 10meters
        return dist < 0.01;
    }

    private double dtor(double deg){
        return (deg * Math.PI/180);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.inflater = inflater;

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //portrait
            view = inflater.inflate(R.layout.fragment_journey, container, false);
        } else {
            //landscape
            view = inflater.inflate(R.layout.fragment_journey_landscape, container, false);
        }

        buttonPlanJourney = view.findViewById(R.id.buttonPlan);
        buttonPlanJourney.setOnClickListener(this);
        buttonBack = view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(this);
        buttonSaveJourney = view.findViewById(R.id.buttonSaveJourney);
        buttonSaveJourney.setOnClickListener(this);
        buttonReset = view.findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(this);
        buttonCreateJourney = view.findViewById(R.id.buttonCreateJourney);
        buttonCreateJourney.setOnClickListener(this);
        startLocation = view.findViewById(R.id.editStartLocation);
        endLocation = view.findViewById(R.id.editEndLocation);
        datePicker = view.findViewById(R.id.datePicker);
        errorText = view.findViewById(R.id.planJourneyError);
        datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);

        Date date = new Date();
        long now = date.getTime();
        long year = 365;
        long hour = 24;
        long minute = 60;
        long second = 60;
        long millisecond = 1000;
        //have to avoid an overflow expression.
        long future = now+year*hour*minute*second*millisecond;
        //datepicker uses milliseconds as time, so set the min to now, and max to 1 year from now.
        datePicker.setMinDate(now);
        datePicker.setMaxDate(future);

        parentGroup = view.findViewById(R.id.parentGroup);

        //inflate the recycler(s) with the correct views and attach the correct adapter(s)
        RecyclerView recyclerView = view.findViewById(R.id.recyclerSavedJourney);
        recyclerView.setLayoutManager(new LinearLayoutManager(GlobalContext.getContext()));
        recyclerViewSavedJourney.setClickListener(this);
        recyclerViewSavedJourney.setClickListener2(this);
        recyclerView.setAdapter(recyclerViewSavedJourney);

        RecyclerView recyclerJourney = view.findViewById(R.id.recyclerResult);
        recyclerJourney.setLayoutManager(new LinearLayoutManager(GlobalContext.getContext()));
        recyclerViewJourneySteps = new RecyclerViewJourneySteps(this.context, this.dataRepository);
        recyclerJourney.setAdapter(recyclerViewJourneySteps);

        //load the journey homescreen 'state'
        changeState("default");

        MapView mapView = view.findViewById(R.id.mapLiteJourney);
        if(mapView != null){
            mapView.onCreate(null);
            mapView.getMapAsync(this);
        }

        return view;
    }

    //load saved journey
    @Override
    public void onItemClick2(int position) {
        changeState("result");
        Journey journey = journeyRepository.getJourneyArrayList().get(position);
        //set the class variables to be used in the view result state.
        //this way the data is saved locally instead of having to get it from the asynctask each time.
        loadJourney = true;
        loadCalendar = journey.getTravelDate();
        journeyStartLocation = journey.getStartLocation();
        journeyEndLocation = journey.getEndLocation();
        journeyStartLat = journey.getStartLat();
        journeyStartLng = journey.getStartLng();
        journeyEndLat = journey.getEndLat();
        journeyEndLng = journey.getEndLng();
        journeyResult = journey.getJourneyResult();
        //pass the saved local data to the dataLoaded method - bypassing the asynctask
        this.dataLoaded(journey.getJourneyResult());
    }

    //trashcan Icon (delete saved journey)
    @Override
    public void onItemClick(int position) {
        dataRepository.deleteJourney(position);
        recyclerViewSavedJourney.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v){
        if(v == buttonPlanJourney){
            resetFields();
            changeState("plan");
        }else if(v == buttonBack){
            changeState("default");
        }else if(v == buttonSaveJourney){
            showPopUp();
            recyclerViewSavedJourney.notifyDataSetChanged();
        }else if(v == buttonReset){
            resetFields();
        }else if(v == buttonCreateJourney){
            planJourney();
        }else if(v == parentGroup){
            //click off editable to hide keyboard
            hideKeyBoard();
        }
    }

    //popup to confirm save journey and change the name.
    //names do not have to be unique.
    private void showPopUp(){
        final View popupView;
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            //portrait
            popupView = inflater.inflate(R.layout.popup_save_journey, null);
        } else {
            //landscape
            popupView = inflater.inflate(R.layout.popup_save_journey_landscape, null);
        }

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        EditText temp = popupView.findViewById(R.id.editText);
        //load a prepared name into the name for convience.
        String temp5 = journeyStartLocation + " to "+journeyEndLocation;
        temp.setText(temp5);
        final Button saveButton = popupView.findViewById(R.id.buttonJourneySave);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(v == saveButton){
                    EditText temp = popupView.findViewById(R.id.editText);
                    saveJourney(temp.getText().toString());
                    popupWindow.dismiss();
                }
            }
        });
        final Button cancelButton = popupView.findViewById(R.id.buttonJourneyCancel);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(v == cancelButton){
                    popupWindow.dismiss();
                }
            }
        });
        popupView.setOnTouchListener(new View.OnTouchListener(){
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //if click anywhere apart from the saveButton or name edittext close the popup.
                popupWindow.dismiss();
                return true;
            }
        });
    }

    //save the journey and update the saved journey recycler
    private void saveJourney(String name){
        Journey journey = new Journey(name, journeyStartLat, journeyStartLng, journeyEndLat, journeyEndLng, journeyDate, journeyStartLocation, journeyEndLocation, journeyResult);
        dataRepository.saveJourney(journey);
        recyclerViewSavedJourney.notifyDataSetChanged();

    }

    //checks fields are completed properly, displays errors if not.
    private void planJourney(){
        boolean error = false;
        if(startLocation.getText().toString().length() == 0 && endLocation.getText().toString().length() == 0){
            error = true;
            errorText.setText(R.string.both_must);
        }else if(startLocation.getText().toString().length() == 0){
            error = true;
            errorText.setText(R.string.start_must);
        }else if(endLocation.getText().toString().length() == 0){
            error = true;
            errorText.setText(R.string.end_must);
        }
        if(!error){
            //geocoder, get LatLng from location
            LatLng start = getLatLngFromAddress(startLocation.getText().toString());
            LatLng end = getLatLngFromAddress(endLocation.getText().toString());
            if(start == null && end == null){
                error = true;
                errorText.setText(R.string.niether_found);
            }else if(start == null){
                error = true;
                errorText.setText(R.string.start_not_found);
            }else if(end == null){
                error = true;
                errorText.setText(R.string.end_not_found);
            }
            if(!error){
                //if no errors set the class variables and change state to view results
                journeyStartLat = start.latitude;
                journeyStartLng = start.longitude;
                journeyEndLat = end.latitude;
                journeyEndLng = end.longitude;
                journeyStartLocation = startLocation.getText().toString();
                journeyEndLocation = endLocation.getText().toString();
                resetFields();
                changeState("result");
                //asynctask gets the results and loads them into dataLoaded() (above) using callback
                LoadResourceJSON task = new LoadResourceJSON();
                task.onPreExecute(this, start, end);
                task.execute();
            }
        }
        if(error){
            errorText.setVisibility(View.VISIBLE);
        }
    }

    //geocoder to get LatLng from location name
    private LatLng getLatLngFromAddress(String input){
        Geocoder geocoder = new Geocoder(GlobalContext.getContext(), Locale.getDefault());
        LatLng latLng;
        try{
            List<Address> addressList = geocoder.getFromLocationName(input, 1);
            if(addressList.size() > 0) {
                Address address = addressList.get(0);
                if (address != null) {
                    latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    return latLng;
                }
            }
        }catch(Exception e){
            System.out.println("Error-FragmentList-getLatLngFromAddress(): "+e);
        }
        return null;
    }

    //reset the plan journey fields
    private void resetFields(){
        errorText.setText("");
        errorText.setVisibility(View.INVISIBLE);
        startLocation.setText("");
        endLocation.setText("");
        Calendar now = Calendar.getInstance();
        datePicker.updateDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
    }

    //change the 'state' of fragment
    private void changeState(String input){
        hideKeyBoard();
        if(input.equalsIgnoreCase("default")){
            showDefault();
            hidePlanJourney();
            hideJourneyResult();
        }else if(input.equalsIgnoreCase("plan")){
            hideDefault();
            hideJourneyResult();
            showPlanJourney();
        }else if(input.equalsIgnoreCase("result")){
            hideDefault();
            hidePlanJourney();
            showJourneyResult();
        }
    }

    //modify the constraint set and visibility to change 'state'
    private void showDefault(){
        callback.changeTitle("Journey Planner");
        buttonCreateJourney.setVisibility(View.INVISIBLE);
        buttonBack.setVisibility(View.INVISIBLE);
        buttonSaveJourney.setVisibility(View.INVISIBLE);
        buttonReset.setVisibility(View.INVISIBLE);
        buttonPlanJourney.setVisibility(View.VISIBLE);

        view.findViewById(R.id.recyclerSavedJourney).setVisibility(View.VISIBLE);

        ConstraintSet constraintset = new ConstraintSet();
        ConstraintLayout parentGroup = view.findViewById(R.id.parentGroup);
        constraintset.clone(parentGroup);
        constraintset.connect(R.id.recyclerSavedJourney, ConstraintSet.TOP, R.id.buttonPlan, ConstraintSet.BOTTOM);
        constraintset.applyTo(parentGroup);
    }

    //modify the constraint set and visibility to change 'state'
    private void hideDefault(){
        ConstraintSet constraintset = new ConstraintSet();
        ConstraintLayout parentGroup = view.findViewById(R.id.parentGroup);
        constraintset.clone(parentGroup);
        constraintset.connect(R.id.recyclerSavedJourney, ConstraintSet.TOP, R.id.parentGroup, ConstraintSet.BOTTOM);
        constraintset.applyTo(parentGroup);

        view.findViewById(R.id.recyclerSavedJourney).setVisibility(View.INVISIBLE);
    }

    //modify the constraint set and visibility to change 'state'
    private void showPlanJourney(){
        callback.changeTitle("Plan a Journey");
        buttonCreateJourney.setVisibility(View.VISIBLE);
        buttonBack.setVisibility(View.VISIBLE);
        buttonSaveJourney.setVisibility(View.INVISIBLE);
        buttonReset.setVisibility(View.VISIBLE);
        buttonPlanJourney.setVisibility(View.INVISIBLE);

        view.findViewById(R.id.planJourney).setVisibility(View.VISIBLE);

        ConstraintSet constraintset = new ConstraintSet();
        ConstraintLayout parentGroup = view.findViewById(R.id.parentGroup);
        constraintset.clone(parentGroup);
        constraintset.connect(R.id.planJourney, ConstraintSet.TOP, R.id.buttonBack, ConstraintSet.BOTTOM);
        constraintset.applyTo(parentGroup);
    }

    //modify the constraint set and visibility to change 'state'
    private void hidePlanJourney(){
        ConstraintSet constraintset = new ConstraintSet();
        ConstraintLayout parentGroup = view.findViewById(R.id.parentGroup);
        constraintset.clone(parentGroup);
        constraintset.connect(R.id.planJourney, ConstraintSet.TOP, R.id.parentGroup, ConstraintSet.BOTTOM);
        constraintset.applyTo(parentGroup);

        view.findViewById(R.id.planJourney).setVisibility(View.INVISIBLE);
    }

    //modify the constraint set and visibility to change 'state'
    private void showJourneyResult(){
        callback.changeTitle("Journey Result");

        buttonCreateJourney.setVisibility(View.INVISIBLE);
        buttonBack.setVisibility(View.VISIBLE);
        buttonSaveJourney.setVisibility(View.INVISIBLE);
        buttonReset.setVisibility(View.INVISIBLE);
        buttonPlanJourney.setVisibility(View.VISIBLE);

        view.findViewById(R.id.journeyResult).setVisibility(View.VISIBLE);

        ConstraintSet constraintset = new ConstraintSet();
        ConstraintLayout parentGroup = view.findViewById(R.id.parentGroup);
        constraintset.clone(parentGroup);
        constraintset.connect(R.id.journeyResult, ConstraintSet.TOP, R.id.buttonBack, ConstraintSet.BOTTOM);
        constraintset.applyTo(parentGroup);
    }

    //modify the constraint set and visibility to change 'state'
    private void hideJourneyResult(){
        ConstraintSet constraintset = new ConstraintSet();
        ConstraintLayout parentGroup = view.findViewById(R.id.parentGroup);
        constraintset.clone(parentGroup);
        constraintset.connect(R.id.journeyResult, ConstraintSet.TOP, R.id.parentGroup, ConstraintSet.BOTTOM);
        constraintset.applyTo(parentGroup);

        view.findViewById(R.id.journeyResult).setVisibility(View.INVISIBLE);
    }

    public interface JourneyListener{
        void changeTitle(String title);
    }

    void setJourneyListener(JourneyListener callback){
        this.callback = callback;
    }

    @Override
    public void onMapReady(GoogleMap map){
        MapsInitializer.initialize(Objects.requireNonNull(getActivity()));
        this.map = map;
    }

    private void hideKeyBoard(){
        InputMethodManager imm = (InputMethodManager)GlobalContext.getContext().getSystemService(INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("journeyState", "test");

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }
}
