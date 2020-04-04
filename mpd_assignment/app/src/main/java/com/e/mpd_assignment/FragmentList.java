package com.e.mpd_assignment;

import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.INPUT_METHOD_SERVICE;

import com.google.android.gms.maps.GoogleMap;

//used by incident, roadwork, and planned roadwork lists to display & search the appropriate data.
public class FragmentList extends Fragment implements  RecyclerViewAdapter.ItemClickListener, View.OnClickListener, View.OnTouchListener{

    private View view;
    private FragmentListener callback;
    private DataRepository dataRepository;

    private ScrollView group;
    private Button backButton;
    private Button searchButton;
    private Spinner spinnerLatLong;
    private Spinner spinnerPlace;
    private TextView textLat;
    private TextView textLng;
    private TextView textOr;
    private TextView textMiles;
    private TextView textErrorLatLong;
    private TextView textErrorPlace;
    private EditText editLat;
    private EditText editLng;
    private EditText editPlace;
    private Button buttonLatLong;
    private Button buttonPlace;
    private Button buttonReset;

    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private boolean shouldScroll = false;
    private int scrollValue = 0;

    private boolean typeIncident = false;
    private boolean typeRoadwork = false;
    private boolean typePlannedRoadwork = false;

    GoogleMap googleMap;
    MapView mapView;

    //FragmentList is used by incident, roadwork, and planned roadwork (each have their own) to display the list of data so sets the state at creation.
    public FragmentList(DataRepository dataRepository, RecyclerViewAdapter recyclerViewAdapter, String type, FragmentListener callback){
        if(type.equalsIgnoreCase("Incident")){
            typeIncident = true;
        }else if(type.equalsIgnoreCase("Roadwork")){
            typeRoadwork = true;
        }else if(type.equalsIgnoreCase("PlannedRoadwork")){
            typePlannedRoadwork = true;
        }
        this.recyclerViewAdapter = recyclerViewAdapter;
        this.dataRepository = dataRepository;
        this.setFragmentListener(callback);
    }

    //callback used to switch fragment back to map
    public void setFragmentListener(FragmentListener callback){
        this.callback = callback;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        int orientation = this.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            //portrait
            view = inflater.inflate(R.layout.fragment_list, container, false);
        }else{
            //landscape
            view = inflater.inflate(R.layout.fragment_list_landscape, container, false);
        }

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(GlobalContext.getContext()));
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        //all the buttons and fields used in the list view
        backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        buttonReset = view.findViewById(R.id.resetView);
        buttonReset.setOnClickListener(this);
        group = view.findViewById(R.id.searchGroup);
        spinnerLatLong = view.findViewById(R.id.spinnerLatLongMiles);
        spinnerPlace = view.findViewById(R.id.spinnerMiles);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(GlobalContext.getContext(), R.array.miles_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLatLong.setAdapter(adapter);
        spinnerPlace.setAdapter(adapter);
        editLat = view.findViewById(R.id.editLat);
        editLng = view.findViewById(R.id.editLng);
        editPlace = view.findViewById(R.id.editPlace);
        buttonLatLong = view.findViewById(R.id.latLongButton);
        buttonLatLong.setOnClickListener(this);
        buttonPlace = view.findViewById(R.id.searchPlaceButton);
        buttonPlace.setOnClickListener(this);

        textLat = view.findViewById(R.id.textLat);
        textLng = view.findViewById(R.id.textLng);
        textOr = view.findViewById(R.id.textOr);
        textMiles = view.findViewById(R.id.textMiles);
        textErrorLatLong = view.findViewById(R.id.textErrorLatLong);
        textErrorPlace = view.findViewById(R.id.textErrorPlace);

        buttonReset.setVisibility(View.INVISIBLE);

        group.setVisibility(View.INVISIBLE);
        group.setOnClickListener(this);
        group.setOnTouchListener(this);

        //hide the search fields initially
        toggleSearch(true);

        view.setOnTouchListener(this);

        //set the icon and title based on state.
        ImageView temp = view.findViewById(R.id.imageIcon);
        TextView title = view.findViewById(R.id.title);
        if(typeIncident){
            temp.setImageDrawable(GlobalContext.getContext().getResources().getDrawable(R.drawable.incidenticon));
            title.setText("Incidents");
        }else if(typeRoadwork){
            temp.setImageDrawable(GlobalContext.getContext().getResources().getDrawable(R.drawable.roadworksicon));
            title.setText("Roadworks");
        }else if(typePlannedRoadwork){
            temp.setImageDrawable(GlobalContext.getContext().getResources().getDrawable(R.drawable.plannedroadworksicon));
            title.setText("Planned\nRoadworks");
        }

        return view;
    }

    //the soft keyboard gets in the way, click off elements to get rid of it.
    @Override
    public boolean onTouch(View v, MotionEvent e){
        InputMethodManager imm = (InputMethodManager)GlobalContext.getContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return false;
    }

    //imaginary 'touch' to programmatically hide the keyboard
    public void hideKeyBoard(){
        this.onTouch(view, GlobalContext.obtainMotionEvent());
    }

    public void resetSearch(){
        editLat.setText("");
        editLat.setText("");
        editPlace.setText("");
    }

    @Override
    public void onClick(View v) {
        textErrorLatLong.setText("");
        textErrorPlace.setText("");
        hideKeyBoard();
        if(v == backButton){
            //switch fragment to map
            callback.backButton();
        }else if(v == searchButton){
            //show the search fields
            toggleSearch(false);
        }else if(v == buttonReset){
            //reset & reload the items in list
            buttonReset.setVisibility(View.INVISIBLE);
            if(typeIncident){
                dataRepository.resetRecyclerIncident();
            }else if(typeRoadwork){
                dataRepository.resetRecyclerRoadwork();
            }else if(typePlannedRoadwork){
                dataRepository.resetRecyclerPlanned();
            }
            recyclerViewAdapter.notifyDataSetChanged();
            if(searchButton.getText().equals("Hide Search")){
                //hide the search fields
                toggleSearch(false);
            }
            recyclerView.scrollToPosition(0);
        }else if(v == buttonLatLong){
            //search for lat/lng based on selected distance
            String lat = editLat.getText().toString();
            String lng = editLng.getText().toString();
            try{
                LatLng newLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                if(newLatLng != null){
                    if(lat.length() > 0 && lng.length() > 0){
                        if(lat.contains(",") || lng.contains(",")){
                            //shouldn't happen because keyboard won't allow it as input
                            textErrorLatLong.setText("Can't use comma in latitude/longitude");
                        }else{
                            //location found
                            //creates a temporary ArrayList and updates the dataRepository recycler ArrayList with it, then tells recycler to redraw.
                            toggleSearch(false);
                            if(typeIncident){
                                ArrayList<Incident> incidentArrayList = dataRepository.getIncidentArrayList();
                                Incident temp = null;
                                ArrayList<Incident> tempArrayList = new ArrayList<>();
                                TextView textView = (TextView)spinnerLatLong.getSelectedView();
                                int distance = Integer.parseInt(textView.getText().toString());
                                for(int i = 0, j = incidentArrayList.size(); i<j; i++){
                                    temp = incidentArrayList.get(i);
                                    double returnedDistance = calculateDistance(temp.getLat(), temp.getLon(), Double.parseDouble(lat), Double.parseDouble(lng), distance);
                                    if(returnedDistance != -1){
                                        tempArrayList.add(incidentArrayList.get(i));
                                        tempArrayList.get(tempArrayList.size()-1).setDistance(returnedDistance);
                                    }
                                }
                                dataRepository.setRecyclerIncident(tempArrayList);
                            }else if(typeRoadwork){
                                ArrayList<Roadwork> roadworkArrayList = dataRepository.getRoadworkArrayList();
                                Roadwork temp = null;
                                ArrayList<Roadwork> tempArrayList = new ArrayList<>();
                                TextView textView = (TextView)spinnerLatLong.getSelectedView();
                                int distance = Integer.parseInt(textView.getText().toString());
                                for(int i = 0, j = roadworkArrayList.size(); i<j; i++){
                                    temp = roadworkArrayList.get(i);
                                    double returnedDistance = calculateDistance(temp.getLat(), temp.getLon(), Double.parseDouble(lat), Double.parseDouble(lng), distance);
                                    if(returnedDistance != -1){
                                        tempArrayList.add(roadworkArrayList.get(i));
                                        tempArrayList.get(tempArrayList.size()-1).setDistance(returnedDistance);
                                    }
                                }
                                dataRepository.setRecyclerRoadwork(tempArrayList);
                            }else if(typePlannedRoadwork){
                                ArrayList<PlannedRoadwork> plannedRoadworkArrayList = dataRepository.getPlannedRoadworkArrayList();
                                PlannedRoadwork temp = null;
                                ArrayList<PlannedRoadwork> tempArrayList = new ArrayList<>();
                                TextView textView = (TextView)spinnerLatLong.getSelectedView();
                                int distance = Integer.parseInt(textView.getText().toString());
                                for(int i = 0, j = plannedRoadworkArrayList.size(); i<j; i++){
                                    temp = plannedRoadworkArrayList.get(i);
                                    double returnedDistance = calculateDistance(temp.getLat(), temp.getLon(), Double.parseDouble(lat), Double.parseDouble(lng), distance);
                                    if(returnedDistance != -1){
                                        tempArrayList.add(plannedRoadworkArrayList.get(i));
                                        tempArrayList.get(tempArrayList.size()-1).setDistance(returnedDistance);
                                    }
                                }
                                dataRepository.setRecyclerPlanned(tempArrayList);
                            }
                            recyclerViewAdapter.notifyDataSetChanged();
                            buttonReset.setVisibility(View.VISIBLE);
                            resetSearch();
                            recyclerView.scrollToPosition(0);
                        }
                    }else{
                        textErrorLatLong.setText("Need to enter both fields");
                    }
                }else{
                    textErrorLatLong.setText("Error in latitude /longitude");
                }
            }catch(Exception e){
                textErrorLatLong.setText("Error in latitude /longitude");
            }
        }else if(v == buttonPlace){
            //if place button pressed
            String place = editPlace.getText().toString();
            if(place.length() > 0){
                LatLng foundLocation = getLatLngFromAddress(place);
                if(foundLocation != null){
                    //if location found
                    toggleSearch(false);
                    //creates a temporary ArrayList which is set in the dataRepository, tells the recycler to redraw list
                    if(typeIncident){
                        ArrayList<Incident> incidentArrayList= dataRepository.getIncidentArrayList();
                        Incident temp = null;
                        ArrayList<Incident> tempArrayList = new ArrayList<>();
                        TextView textview = (TextView)spinnerPlace.getSelectedView();
                        int distance = Integer.parseInt(textview.getText().toString());
                        for(int i = 0, j = incidentArrayList.size(); i<j; i++){
                            temp = incidentArrayList.get(i);
                            double returnedDistance = calculateDistance(temp.getLat(), temp.getLon(), foundLocation.latitude, foundLocation.longitude, distance);
                            if(returnedDistance != -1){
                                tempArrayList.add(incidentArrayList.get(i));
                                tempArrayList.get(tempArrayList.size()-1).setDistance(returnedDistance);
                            }
                        }
                        dataRepository.setRecyclerIncident(tempArrayList);
                    }else if(typeRoadwork){
                        ArrayList<Roadwork> roadworkArrayList= dataRepository.getRoadworkArrayList();
                        Roadwork temp = null;
                        ArrayList<Roadwork> tempArrayList = new ArrayList<>();
                        TextView textview = (TextView)spinnerPlace.getSelectedView();
                        int distance = Integer.parseInt(textview.getText().toString());
                        for(int i = 0, j = roadworkArrayList.size(); i<j; i++){
                            temp = roadworkArrayList.get(i);
                            double returnedDistance = calculateDistance(temp.getLat(), temp.getLon(), foundLocation.latitude, foundLocation.longitude, distance);
                            if(returnedDistance != -1){
                                tempArrayList.add(roadworkArrayList.get(i));
                                tempArrayList.get(tempArrayList.size()-1).setDistance(returnedDistance);
                            }
                        }
                        dataRepository.setRecyclerRoadwork(tempArrayList);
                    }else if(typePlannedRoadwork){
                        ArrayList<PlannedRoadwork> plannedRoadworkArrayList = dataRepository.getPlannedRoadworkArrayList();
                        PlannedRoadwork temp = null;
                        ArrayList<PlannedRoadwork> tempArrayList = new ArrayList<>();
                        TextView textview = (TextView)spinnerPlace.getSelectedView();
                        int distance = Integer.parseInt(textview.getText().toString());
                        for(int i = 0, j = plannedRoadworkArrayList.size(); i<j; i++){
                            temp = plannedRoadworkArrayList.get(i);
                            double returnedDistance = calculateDistance(temp.getLat(), temp.getLon(), foundLocation.latitude, foundLocation.longitude, distance);
                            if(returnedDistance != -1){
                                tempArrayList.add(plannedRoadworkArrayList.get(i));
                                tempArrayList.get(tempArrayList.size()-1).setDistance(returnedDistance);
                            }
                        }
                        dataRepository.setRecyclerPlanned(tempArrayList);
                    }
                    recyclerViewAdapter.notifyDataSetChanged();
                    buttonReset.setVisibility(View.VISIBLE);
                    resetSearch();
                    recyclerView.scrollToPosition(0);
                }else{
                    textErrorPlace.setText("No address found for: "+place);
                }
            }else{
                textErrorPlace.setText("Need to enter a place");
                hideKeyBoard();
            }
        }
    }

    //calculate the distance between two points, returns boolean if within input range
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2, int distance){
        double temp = lon1-lon2;
        double dist = Math.sin(dtor(lat1))*Math.sin(dtor(lat2))+
                Math.cos(dtor(lat1))*Math.cos(dtor(lat2))*Math.cos(dtor(temp));
        dist = Math.acos(dist);
        dist = dist*180/Math.PI;
        dist = dist * 1.609;
        dist = dist * 60 * 1.1515;
        if(dist < distance){
            return dist;
        }
        return -1;
    }

    private double dtor(double deg){
        return (deg * Math.PI/180);
    }

    private void toggleSearch(boolean init){
        boolean hide = false;
        if(searchButton.getText().equals("Hide Search")){
            hide = true;
        }
        ConstraintSet constraintset = new ConstraintSet();
        ConstraintLayout parentGroup = view.findViewById(R.id.parentGroup);
        constraintset.clone(parentGroup);
        if(hide || init){
            constraintset.connect(R.id.recycler, ConstraintSet.TOP, R.id.searchButton, ConstraintSet.BOTTOM);
        }else{
            constraintset.connect(R.id.recycler, ConstraintSet.TOP, R.id.searchGroup, ConstraintSet.BOTTOM);
        }
        constraintset.applyTo(parentGroup);

        if(hide || init){
            searchButton.setText("Show Search");
            group.setVisibility(View.INVISIBLE);
            textLat.setVisibility(View.INVISIBLE);
            editLat.setVisibility(View.INVISIBLE);
            textLng.setVisibility(View.INVISIBLE);
            editLng.setVisibility(View.INVISIBLE);
            textOr.setVisibility(View.INVISIBLE);
            textMiles.setVisibility(View.INVISIBLE);
            spinnerPlace.setVisibility(View.INVISIBLE);
            spinnerLatLong.setVisibility(View.INVISIBLE);
            buttonPlace.setVisibility(View.INVISIBLE);
            buttonLatLong.setVisibility(View.INVISIBLE);
            textErrorLatLong.setText("");
            textErrorPlace.setText("");
        }else{
            searchButton.setText("Hide Search");
            group.setVisibility(View.VISIBLE);
            textLat.setVisibility(View.VISIBLE);
            editLat.setVisibility(View.VISIBLE);
            textLng.setVisibility(View.VISIBLE);
            editLng.setVisibility(View.VISIBLE);
            textOr.setVisibility(View.VISIBLE);
            textMiles.setVisibility(View.VISIBLE);
            spinnerPlace.setVisibility(View.VISIBLE);
            spinnerLatLong.setVisibility(View.VISIBLE);
            buttonLatLong.setVisibility(View.VISIBLE);
            buttonPlace.setVisibility(View.VISIBLE);
        }
    }

    public void scrollTo(int position){
        shouldScroll = true;
        scrollValue = position;
    }

    public interface FragmentListener{
        void backButton();
    }

    @Override
    public void onItemClick(View view, int position) {
        //can implement additional functionality when an incident etc is clicked.
    }

    @Override
    public void onResume(){
        super.onResume();
        if(typeIncident){
            recyclerViewAdapter.setType("Incidents");
        }else if(typeRoadwork){
            recyclerViewAdapter.setType("Roadworks");
        }else if(typePlannedRoadwork){
            recyclerViewAdapter.setType("PlannedRoadworks");
        }
        if(shouldScroll){
            recyclerView.scrollToPosition(scrollValue);
            shouldScroll = false;
        }else{
            recyclerView.scrollToPosition(0);
        }
    }

    public LatLng getLatLngFromAddress(String input){
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
}