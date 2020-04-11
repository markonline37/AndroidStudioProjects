package com.e.mpd_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

/*
    Mark Cottrell - S1627662
 */
//MainActivity creates all of the fragments, objects, and menu.
//Fragments call callbacks in MainActivity to change functionality of app.
public class MainActivity extends AppCompatActivity implements FragmentLoadingScreen.FragmentListener,
        LoadResourcePeriodic.LoadResourcePeriodicListener, FragmentList.FragmentListener,
        FragmentMapView.MapStateListener, FragmentJourney.JourneyListener,
        NavigationView.OnNavigationItemSelectedListener{

    private DataRepository dataRepository;
    private JourneyRepository journeyRepository;
    private Fragment fragmentMapView;
    private Fragment fragmentIncidents;
    private Fragment fragmentRoadworks;
    private Fragment fragmentPlannedRoadworks;
    private Fragment fragmentJourney;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerViewSavedJourney recyclerViewSavedJourney;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private String state;

    @Override
    public void changeTitle(String input){
        setTitle(input);
    }

    @Override
    public void changeState(String type, int position){
        switch (type) {
            case "Incident": {
                dataRepository.resetRecyclerIncident();
                switchFragments(fragmentIncidents, "Incident");
                FragmentList fragment = (FragmentList) getSupportFragmentManager().findFragmentByTag("incident");
                if (fragment != null) {
                    fragment.scrollTo(position);
                }
                break;
            }
            case "Roadworks": {
                switchFragments(fragmentRoadworks, "Roadwork");
                FragmentList fragment = (FragmentList) getSupportFragmentManager().findFragmentByTag("roadwork");
                if (fragment != null) {
                    fragment.scrollTo(position);
                }
                break;
            }
            case "PlannedRoadworks": {
                switchFragments(fragmentPlannedRoadworks, "PlannedRoadwork");
                FragmentList fragment = (FragmentList) getSupportFragmentManager().findFragmentByTag("plannedRoadwork");
                if (fragment != null) {
                    fragment.scrollTo(position);
                }
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(null);

        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            //hide the nav bar
            Objects.requireNonNull(getSupportActionBar()).hide();
            journeyRepository = new JourneyRepository();
            dataRepository = new DataRepository(journeyRepository);
            dataRepository.checkFirstLoad();
        }else{
            if(Objects.requireNonNull(savedInstanceState.getString("state")).equals("LoadingScreen")){
                Objects.requireNonNull(getSupportActionBar()).hide();
                journeyRepository = new JourneyRepository();
                dataRepository = new DataRepository(journeyRepository);
                dataRepository.checkFirstLoad();
            }else{
                dataRepository = (DataRepository)savedInstanceState.getSerializable("repository");
                journeyRepository = (JourneyRepository)savedInstanceState.getSerializable("journey");
            }
        }


        //create the navigation menu
        drawerLayout = findViewById(R.id.activity_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nv);
        navigationView.setItemIconTintList(null);
        //setup the navigation menu listener
        navigationView.setNavigationItemSelectedListener(this);

        //load and set the user preferences
        if(dataRepository.getDefaultMapCheckboxViewIncident().equalsIgnoreCase("checked")){
            MenuItem item = navigationView.getMenu().getItem(1);
            item.setChecked(true);
            setMenuItemColor(item, true);
        }else{
            navigationView.getMenu().getItem(1).setIcon(R.drawable.incidenticongray);
        }
        if(dataRepository.getDefaultMapCheckboxViewRoadworks().equalsIgnoreCase("checked")){
            MenuItem item = navigationView.getMenu().getItem(2);
            item.setChecked(true);
            setMenuItemColor(item, true);
        }else{
            navigationView.getMenu().getItem(2).setIcon(R.drawable.roadworksicongray);
        }
        if(dataRepository.getDefaultMapCheckboxViewPlannedRoadworks().equalsIgnoreCase("checked")){
            MenuItem item =  navigationView.getMenu().getItem(3);
            item.setChecked(true);
            setMenuItemColor(item, true);
        }else{
            navigationView.getMenu().getItem(3).setIcon(R.drawable.plannedroadworksicongray);
        }
        if(dataRepository.getDefaultMapCheckboxViewFollowMe().equalsIgnoreCase("checked")){
            MenuItem item = navigationView.getMenu().getItem(4);
            item.setChecked(true);
            dataRepository.setFollowMe("checked");
            setMenuItemColor(item, true);
        }else{
            navigationView.getMenu().getItem(4).setIcon(R.drawable.mymarkergray);
            dataRepository.setFollowMe("unchecked");
        }

        /*
            create the fragments and recycler view.
            FragmentList and RecyclerViewAdapter are shared between incidents, roadworks, plannedRoadworks to reduce code duplication
            FragmentList sets the state at start, RecyclerView updates state with set methods
        */
        recyclerViewAdapter = new RecyclerViewAdapter(this, dataRepository);
        recyclerViewSavedJourney = new RecyclerViewSavedJourney(this, journeyRepository);
        Fragment fragmentLoadingScreen = new FragmentLoadingScreen(dataRepository);
        fragmentJourney = new FragmentJourney(dataRepository, recyclerViewSavedJourney, this, journeyRepository);
        fragmentMapView = new FragmentMapView(dataRepository, this);
        fragmentIncidents = new FragmentList(dataRepository, recyclerViewAdapter, "Incident", this);
        fragmentRoadworks = new FragmentList(dataRepository, recyclerViewAdapter, "Roadwork", this);
        fragmentPlannedRoadworks = new FragmentList(dataRepository, recyclerViewAdapter, "PlannedRoadwork", this);

        //need to load the unused fragments into the backstacktrace so that they can be searched for later.
        switchFragments(fragmentIncidents, "Incident");
        switchFragments(fragmentRoadworks, "Roadwork");
        switchFragments(fragmentPlannedRoadworks, "PlannedRoadwork");
        switchFragments(fragmentJourney, "journey");
        //then display the loading screen.
        if(savedInstanceState == null){
            switchFragments(fragmentLoadingScreen, "LoadingScreen");
        }else{
            String state = savedInstanceState.getString("state");
            assert state != null;
            switch(state){
                case "LoadingScreen":
                    switchFragments(fragmentLoadingScreen, "LoadingScreen");
                    break;
                case "Incident":
                    recyclerViewAdapter.setType("Incident");
                    recyclerViewAdapter.notifyDataSetChanged();
                    switchFragments(fragmentIncidents, "Incident");
                    break;
                case "Roadwork":
                    recyclerViewAdapter.setType("Roadworks");
                    recyclerViewAdapter.notifyDataSetChanged();
                    switchFragments(fragmentRoadworks, "Roadwork");
                    break;
                case "PlannedRoadwork":
                    recyclerViewAdapter.setType("PlannedRoadworks");
                    recyclerViewAdapter.notifyDataSetChanged();
                    switchFragments(fragmentPlannedRoadworks, "PlannedRoadwork");
                    break;
                case "Journey":
                    switchFragments(fragmentJourney, "journey");
                    break;
                case "Map":
                default:
                    switchFragments(fragmentMapView, "Map");
            }
        }

        //a callback is used to let MainActivity know loading is complete
        //MainActivity switches fragment to map view and user can now interact
    }

    //MainActivity controls the main menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.buttonMapView){
            switchFragments(fragmentMapView, "Map");
            drawerLayout.closeDrawers();
        } else if(id == R.id.mapIncidents){
            if(item.isChecked()){
                item.setChecked(false);
                item.setIcon(R.drawable.incidenticongray);
                setMenuItemColor(item, false);
                dataRepository.setDefaultMapCheckboxViewIncident("unchecked");
            }else{
                item.setChecked(true);
                item.setIcon(R.drawable.incidenticon);
                setMenuItemColor(item, true);
                dataRepository.setDefaultMapCheckboxViewIncident("checked");
            }
            FragmentMapView fragment = (FragmentMapView) getSupportFragmentManager().findFragmentByTag("mapView");
            if(fragment != null){
                fragment.redrawMap(true);
            }
        }else if(id == R.id.mapRoadworks){
            if(item.isChecked()){
                item.setChecked(false);
                item.setIcon(R.drawable.roadworksicongray);
                setMenuItemColor(item, false);
                dataRepository.setDefaultMapCheckboxViewRoadworks("unchecked");
            }else{
                item.setChecked(true);
                item.setIcon(R.drawable.roadworksicon);
                setMenuItemColor(item, true);
                dataRepository.setDefaultMapCheckboxViewRoadworks("checked");
            }
            FragmentMapView fragment = (FragmentMapView) getSupportFragmentManager().findFragmentByTag("mapView");
            if(fragment != null){
                fragment.redrawMap(true);
            }
        }else if(id == R.id.mapPlannedRoadworks){
            if(item.isChecked()){
                item.setChecked(false);
                item.setIcon(R.drawable.plannedroadworksicongray);
                setMenuItemColor(item, false);
                dataRepository.setDefaultMapCheckboxViewPlannedRoadworks("unchecked");
            }else{
                item.setChecked(true);
                item.setIcon(R.drawable.plannedroadworksicon);
                setMenuItemColor(item, true);
                dataRepository.setDefaultMapCheckboxViewPlannedRoadworks("checked");
            }
            FragmentMapView fragment = (FragmentMapView) getSupportFragmentManager().findFragmentByTag("mapView");
            if(fragment != null){
                fragment.redrawMap(true);
            }
        }else if(id == R.id.mapFollowMe){
            if(item.isChecked()){
                item.setChecked(false);
                item.setIcon(R.drawable.mymarkergray);
                setMenuItemColor(item, false);
                dataRepository.setFollowMe("unchecked");
            }else{
                item.setChecked(true);
                item.setIcon(R.drawable.mymarker);
                setMenuItemColor(item, true);
                dataRepository.setFollowMe("checked");
            }
            FragmentMapView fragment = (FragmentMapView) getSupportFragmentManager().findFragmentByTag("mapView");
            if(fragment != null){
                fragment.redrawMap(true);
            }
        }else if(id == R.id.buttonIncidents){
            recyclerViewAdapter.setType("Incident");
            switchFragments(fragmentIncidents, "Incident");
            drawerLayout.closeDrawers();
        }else if(id == R.id.buttonRoadworks){
            recyclerViewAdapter.setType("Roadworks");
            switchFragments(fragmentRoadworks, "Roadwork");
            drawerLayout.closeDrawers();
        }else if(id == R.id.buttonPlannedRoadworks){
            recyclerViewAdapter.setType("PlannedRoadworks");
            switchFragments(fragmentPlannedRoadworks, "PlannedRoadwork");
            drawerLayout.closeDrawers();
        }else if(id == R.id.buttonJourneyPlanner){
            switchFragments(fragmentJourney, "Journey");
            drawerLayout.closeDrawers();
        }

        return false;
    }

    //adds/removes an underline to menu items to aid colourblind users.
    private void setMenuItemColor(MenuItem item, boolean underline){
        SpannableString string = new SpannableString(item.getTitle());
        if(underline){
            string.setSpan(new UnderlineSpan(), 0, string.length(), 0);
        }else{
            string = new SpannableString(string.toString());
        }
        item.setTitle(string);
    }

    //navigation menu listener
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //uses a toggle because the navigation bar isn't always showing.
        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //implement the listener so fragment can communicate with MainActivity
    @Override
    public void onAttachFragment(@NonNull Fragment fragment){
        if(fragment instanceof FragmentLoadingScreen){
            FragmentLoadingScreen fragmentLoadingScreen = (FragmentLoadingScreen) fragment;
            fragmentLoadingScreen.setFragmentListener(this);
        }else if(fragment instanceof FragmentMapView){
            FragmentMapView fragmentMapView = (FragmentMapView) fragment;
            fragmentMapView.setMapStateListener(this);
        }else if(fragment instanceof FragmentJourney){
            FragmentJourney fragmentJourney = (FragmentJourney) fragment;
            fragmentJourney.setJourneyListener(this);
        }
    }

    //callback method for when data has fully loaded, sets the recycler ArrayLists, changes to map view, and enables the navigation bar.
    public void loadingComplete(){
        dataRepository.resetRecyclerRoadwork();
        dataRepository.resetRecyclerIncident();
        dataRepository.resetRecyclerPlanned();
        journeyRepository = dataRepository.loadJourneys();
        recyclerViewSavedJourney.updateRepo(journeyRepository);
        FragmentJourney fragment = (FragmentJourney) getSupportFragmentManager().findFragmentByTag("journey");
        if(fragment != null){
            fragment.updateRepo(this.journeyRepository);
        }
        recyclerViewSavedJourney.notifyDataSetChanged();
        switchFragments(fragmentMapView, "Map");
        Objects.requireNonNull(this.getSupportActionBar()).show();
    }

    //callback that FragmentMapView implements so that MainActivity can tell it to update the map
    //when LoadResourcePeriodic uses it's callback to MainActivity... isn't asynchronous java such fun.


    @Override
    public void backButton(){
        switchFragments(fragmentMapView, "Map");
    }

    @Override
    public void dataUpdated(){

        FragmentMapView fragment = (FragmentMapView) getSupportFragmentManager().findFragmentByTag("mapView");
        if(fragment != null){
            fragment.redrawMap(false);
        }
    }

    //replaces the fragments, sets a tag and adds to back stack so that they (including inactive fragments) can be searched for.
    private void switchFragments(Fragment fragment, String type){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if(type.equalsIgnoreCase("LoadingScreen")){
            transaction.replace(R.id.fragment, fragment, "loadingScreen");
            state = "LoadingScreen";
        }else if(type.equalsIgnoreCase("Incident")){
            transaction.replace(R.id.fragment, fragment, "incident");
            setTitle("Incidents");
            state = "Incident";
        }else if(type.equalsIgnoreCase("Roadwork")){
            transaction.replace(R.id.fragment, fragment, "roadwork");
            setTitle("Roadworks");
            state = "Roadwork";
        }else if(type.equalsIgnoreCase("PlannedRoadwork")){
            transaction.replace(R.id.fragment, fragment, "plannedRoadwork");
            setTitle("Planned Roadworks");
            state = "PlannedRoadwork";
        }else if(type.equalsIgnoreCase("Map")) {
            transaction.replace(R.id.fragment, fragment, "mapView");
            setTitle("Map");
            state = "Map";
        }else if(type.equalsIgnoreCase("Journey")){
            transaction.replace(R.id.fragment, fragment, "journey");
            setTitle("Journey Planner");
            state = "Journey";
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("repository", dataRepository);
        outState.putSerializable("journey", journeyRepository);
        outState.putString("state", state);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }
}