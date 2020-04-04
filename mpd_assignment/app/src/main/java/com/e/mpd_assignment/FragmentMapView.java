package com.e.mpd_assignment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.LocationListener;

import java.util.ArrayList;

public class FragmentMapView extends Fragment implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback, View.OnClickListener, LocationListener  {

    private DataRepository dataRepository;
    private View view;

    private MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private GoogleMap map;


    private ArrayList<Incident> storedIncidentArrayList;
    private ArrayList<Roadwork> storedRoadworkArrayList;
    private ArrayList<PlannedRoadwork> storedPlannedRoadworkArrayList;
    private ArrayList<Marker> incidentMarkers = new ArrayList<>();
    private ArrayList<Marker> roadworksMarkers = new ArrayList<>();
    private ArrayList<Marker> plannedRoadworksMarkers = new ArrayList<>();
    private Marker mymarker;
    private LatLng storedLatLng;

    private boolean gpsEnabled = false;
    private boolean initialLoad = true;

    private boolean resumeState = false;
    private float stateZoom;
    private LatLng stateLatLng;

    private LoadResourcePeriodic loadResourcePeriodic;


    //not exact, can update later.
    private LatLngBounds Scotland = new LatLngBounds(new LatLng(54.39, -7.83), new LatLng(58.66, -0.67));

    private LocationManager lm;
    private int MAP_UPDATE_TIME = 5000;

    Location location;

    private LatLng latLng;

    private LoadResourcePeriodic.LoadResourcePeriodicListener tempCallback;

    private void saveState(){
        this.resumeState = true;
        this.stateZoom = map.getCameraPosition().zoom;
        this.stateLatLng = map.getCameraPosition().target;
    }

    public FragmentMapView(DataRepository dataRepository, LoadResourcePeriodic loadResourcePeriodic, LoadResourcePeriodic.LoadResourcePeriodicListener callback) {
        this.tempCallback = callback;
        this.dataRepository = dataRepository;
        this.loadResourcePeriodic = loadResourcePeriodic;
    }

    @Override
    public void onClick(View v) {

    }

    private void checkGPSPermissions(){
        if((ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this.getActivity(), new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 100);
        }else{
            gpsEnabled = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            gpsEnabled = true;
            setLocationListener();
        }else{
            gpsEnabled = false;
            //center of Scotland
            latLng = new LatLng(56.8169, -4.1827);
            Toast.makeText(GlobalContext.getContext(), "GPS Permissions not enabled, full functionality disabled", Toast.LENGTH_LONG).show();
        }
        redrawMap(false);
    }

    public void redrawMap(boolean booleanReload ) {

        if(initialLoad){
            initialLoad = false;
            //map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(Scotland, 0));
        }

        //if view incidents checkbox is checked
        if(dataRepository.getDefaultMapCheckboxViewIncident().equals("checked")){
            ArrayList<Incident> incidentArrayList = dataRepository.getIncidentArrayList();
            if(!incidentArrayList.equals(storedIncidentArrayList) || booleanReload){
                for(int i = 0, j = incidentMarkers.size(); i<j; i++){
                    incidentMarkers.get(i).remove();
                }
                Incident incident = null;
                double tempLat = 0;
                double tempLon = 0;
                String tempTitle = "";
                String tempDescription = "";
                for(int i = 0, j = incidentArrayList.size(); i<j; i++){
                    incident = incidentArrayList.get(i);
                    tempTitle = incident.getTitle();
                    tempDescription = incident.getDescription();
                    tempDescription = tempDescription.replace("<br />", "\n");
                    tempLat = incident.getLat();
                    tempLon = incident.getLon();
                    Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(tempLat, tempLon)).title(tempTitle)
                            .snippet(tempDescription).icon(BitmapDescriptorFactory.fromResource(R.drawable.incidenticon)));
                    marker.setTag("Incident - "+i);
                    incidentMarkers.add(marker);
                }
                storedIncidentArrayList = incidentArrayList;
            }
        }else{
            for(int i = 0, j = incidentMarkers.size(); i<j; i++){
                incidentMarkers.get(i).remove();
            }
        }
        //if view roadworks checkbox is checked
        if(dataRepository.getDefaultMapCheckboxViewRoadworks().equals("checked")){
            ArrayList<Roadwork> roadworkArrayList = dataRepository.getRoadworkArrayList();
            if(!roadworkArrayList.equals(storedRoadworkArrayList) || booleanReload){
                for (int i = 0, j = roadworksMarkers.size(); i < j; i++) {
                    roadworksMarkers.get(i).remove();
                }
                Roadwork roadwork = null;
                double tempLat = 0;
                double tempLon = 0;
                String tempTitle = "";
                String tempDescription = "";
                for (int i = 0, j = roadworkArrayList.size(); i < j; i++) {
                    roadwork = roadworkArrayList.get(i);
                    tempTitle = roadwork.getTitle();
                    tempDescription = roadwork.getDescription();
                    tempDescription = tempDescription.replace("<br />", "\n");
                    tempLat = roadwork.getLat();
                    tempLon = roadwork.getLon();
                    Marker marker = null;
                    if(roadwork.getDelay().equalsIgnoreCase("No Delay")){
                        marker = map.addMarker(new MarkerOptions().position(new LatLng(tempLat, tempLon)).title(tempTitle).snippet(tempDescription)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.roadworksiconnodelay)));
                    }else if(roadwork.getDelay().equalsIgnoreCase("Medium Delay")){
                        marker = map.addMarker(new MarkerOptions().position(new LatLng(tempLat, tempLon)).title(tempTitle).snippet(tempDescription)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.roadworksiconmediumdelay)));
                    }else if(roadwork.getDelay().equalsIgnoreCase("High Delay")){
                        marker = map.addMarker(new MarkerOptions().position(new LatLng(tempLat, tempLon)).title(tempTitle).snippet(tempDescription)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.roadworksiconhighdelay)));

                    }
                    marker.setTag("Roadworks - "+i);
                    roadworksMarkers.add(marker);
                }
                storedRoadworkArrayList = roadworkArrayList;
            }
        }else{
            for (int i = 0, j = roadworksMarkers.size(); i < j; i++) {
                roadworksMarkers.get(i).remove();
            }
        }
        //if view planned roadworks checkbox is checked
        if(dataRepository.getDefaultMapCheckboxViewPlannedRoadworks().equals("checked")){
            ArrayList<PlannedRoadwork> plannedRoadworkArrayList = dataRepository.getPlannedRoadworkArrayList();
            if(!plannedRoadworkArrayList.equals(storedPlannedRoadworkArrayList) || booleanReload){
                for(int i = 0, j = plannedRoadworksMarkers.size(); i<j; i++){
                    plannedRoadworksMarkers.get(i).remove();
                }
                PlannedRoadwork plannedRoadwork = null;
                double tempLat = 0;
                double tempLon = 0;
                String tempTitle = "";
                String tempDescription = "";
                for(int i = 0, j = plannedRoadworkArrayList.size(); i<j; i++){
                    plannedRoadwork = plannedRoadworkArrayList.get(i);
                    tempTitle = plannedRoadwork.getTitle();
                    tempDescription = plannedRoadwork.getDescription();
                    tempDescription = tempDescription.replace("<br />", "\n");
                    tempLat = plannedRoadwork.getLat();
                    tempLon = plannedRoadwork.getLon();
                    Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(tempLat, tempLon)).title(tempTitle).snippet(tempDescription)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.plannedroadworksicon)));
                    marker.setTag("PlannedRoadworks - "+i);
                    plannedRoadworksMarkers.add(marker);
                }
                storedPlannedRoadworkArrayList = plannedRoadworkArrayList;
            }
        }else{
            for(int i = 0, j = plannedRoadworksMarkers.size(); i<j; i++){
                plannedRoadworksMarkers.get(i).remove();
            }
        }
        if(dataRepository.getDefaultMapCheckboxViewFollowMe().equals("checked")){
            if((!latLng.equals(storedLatLng) && gpsEnabled) || booleanReload){
                if(mymarker != null){
                    mymarker.remove();
                }
                mymarker = map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.mymarker)));
                storedLatLng = latLng;
                if(dataRepository.getFollowMe()){
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }
        }else{
            if(mymarker != null){
                mymarker.remove();
            }
        }
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        lm = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        int orientation = this.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            //portrait
            view = inflater.inflate(R.layout.fragment_map, container, false);
        }else{
            //landscape
            view = inflater.inflate(R.layout.fragment_map_landscape, container, false);
        }

        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.getMapAsync(this);


        checkGPSPermissions();
        if(gpsEnabled){
            setLocationListener();
        }

        return view;
    }

    private void setLocationListener(){
        try{
            if(gpsEnabled){
                if(ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && lm != null){
                    if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MAP_UPDATE_TIME, 0, this);
                    }
                    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    }else{
                        latLng = new LatLng(56.8169, -4.1827);
                    }
                }
            }
        }catch(Exception e){
            System.out.println("Error-FragmentMapView-updateLatLon(): "+e);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        redrawMap(false);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //needed for location listener callback
    }

    @Override
    public void onProviderEnabled(String provider) {
        //needed for location listener callback
    }

    @Override
    public void onProviderDisabled(String provider) {
        //needed for location listener callback
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        loadResourcePeriodic = new LoadResourcePeriodic(dataRepository, tempCallback);
        loadResourcePeriodic.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    private MapStateListener callback;

    public void setMapStateListener(MapStateListener callback){
        this.callback = callback;
    }

    public interface MapStateListener{
        void changeState(String type, int position);
    }

    //if user clicks a tag, calls a method in main activity to switch fragments
    @Override
    public boolean onMarkerClick(final Marker marker) {

        String temp = marker.getTag().toString();
        String title = temp.split(" - ")[0];
        int position = Integer.parseInt(temp.split(" - ")[1]);

        saveState();
        callback.changeState(title, position);

        return false;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        //set the map to Scotland
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(Scotland, 0));
        //change the default appearance of map marker snippet.
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(GlobalContext.getContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(GlobalContext.getContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(GlobalContext.getContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        map.setOnMarkerClickListener(this);
        redrawMap(true);

        if(resumeState){
            resumeState = false;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(stateLatLng, stateZoom));
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
        saveState();
        loadResourcePeriodic.cancel(true);
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
