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

public class MainActivity extends AppCompatActivity implements FragmentLoadingScreen.FragmentListener,
                                                                LoadResourcePeriodic.LoadResourcePeriodicListener,
                                                                FragmentList.FragmentListener,
                                                                FragmentMapView.MapStateListener{

    private DataRepository dataRepository;
    private Fragment fragmentLoadingScreen;
    private Fragment fragmentMapView;
    private Fragment fragmentIncidents;
    private Fragment fragmentRoadworks;
    private Fragment fragmentPlannedRoadworks;
    private LoadResourcePeriodic loadResourcePeriodic;
    private RecyclerViewAdapter recyclerViewAdapter;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    @Override
    public void changeState(String type, int position){
        if(type.equals("Incident")){
            switchFragments(fragmentIncidents, "Incident");
            FragmentList fragment = (FragmentList) getSupportFragmentManager().findFragmentByTag("incident");
            fragment.scrollTo(position);
        }else if(type.equals("Roadworks")){
            switchFragments(fragmentRoadworks, "Roadwork");
            FragmentList fragment = (FragmentList) getSupportFragmentManager().findFragmentByTag("roadwork");
            fragment.scrollTo(position);
        }else if(type.equals("PlannedRoadworks")){
            switchFragments(fragmentPlannedRoadworks, "PlannedRoadwork");
            FragmentList fragment = (FragmentList) getSupportFragmentManager().findFragmentByTag("plannedRoadwork");
            fragment.scrollTo(position);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        dataRepository = new DataRepository();
        dataRepository.checkFirstLoad();

        //create the navigation menu
        drawerLayout = findViewById(R.id.activity_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.nv);
        navigationView.setItemIconTintList(null);
        //setup the navigation menu listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.mapIncidents){
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
                    fragment.redrawMap(true);
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
                    fragment.redrawMap(true);
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
                    fragment.redrawMap(true);
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
                    fragment.redrawMap(true);
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
                }

                return false;
            }
        });

        //load and set the user preferences
        if(dataRepository.getDefaultMapCheckboxViewIncident().equalsIgnoreCase("checked")){
            MenuItem item = navigationView.getMenu().getItem(0);
            item.setChecked(true);
            setMenuItemColor(item, true);
        }else{
            navigationView.getMenu().getItem(0).setIcon(R.drawable.incidenticongray);
        }
        if(dataRepository.getDefaultMapCheckboxViewRoadworks().equalsIgnoreCase("checked")){
            MenuItem item = navigationView.getMenu().getItem(1);
            item.setChecked(true);
            setMenuItemColor(item, true);
        }else{
            navigationView.getMenu().getItem(1).setIcon(R.drawable.roadworksicongray);
        }
        if(dataRepository.getDefaultMapCheckboxViewPlannedRoadworks().equalsIgnoreCase("checked")){
            MenuItem item =  navigationView.getMenu().getItem(2);
            item.setChecked(true);
            setMenuItemColor(item, true);
        }else{
            navigationView.getMenu().getItem(2).setIcon(R.drawable.plannedroadworksicongray);
        }
        if(dataRepository.getDefaultMapCheckboxViewFollowMe().equalsIgnoreCase("checked")){
            MenuItem item = navigationView.getMenu().getItem(3);
            item.setChecked(true);
            dataRepository.setFollowMe("checked");
            setMenuItemColor(item, true);
        }else{
            navigationView.getMenu().getItem(3).setIcon(R.drawable.mymarkergray);
            dataRepository.setFollowMe("unchecked");
        }

        /*
            create the fragments and recycler view.
            FragmentList and RecyclerViewAdapter are shared between incidents, roadworks, plannedRoadworks to reduce code duplication
            FragmentList sets the state at start, RecyclerView updates state with set methods
        */
        recyclerViewAdapter = new RecyclerViewAdapter(this, dataRepository);

        fragmentLoadingScreen = new FragmentLoadingScreen(dataRepository);

        fragmentMapView = new FragmentMapView(dataRepository);
        fragmentIncidents = new FragmentList(dataRepository, recyclerViewAdapter, "Incident", this);
        fragmentRoadworks = new FragmentList(dataRepository, recyclerViewAdapter, "Roadwork", this);
        fragmentPlannedRoadworks = new FragmentList(dataRepository, recyclerViewAdapter, "PlannedRoadwork", this);

        //need to load the unused fragments into the backstacktrace so that they can be searched for later.
        switchFragments(fragmentIncidents, "Incident");
        switchFragments(fragmentRoadworks, "Roadwork");
        switchFragments(fragmentPlannedRoadworks, "PlannedRoadwork");
        //then display the loading screen.
        switchFragments(fragmentLoadingScreen, "LoadingScreen");
        //a callback is used to let MainActivity know loading is complete
        //MainActivity switches fragment to map view and user can now interact
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
    public boolean onOptionsItemSelected(MenuItem item) {
        //uses a toggle because the navigation bar isn't always showing.
        if(actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //implement the listener so fragment can communicate with MainActivity
    @Override
    public void onAttachFragment(Fragment fragment){
        if(fragment instanceof FragmentLoadingScreen){
            FragmentLoadingScreen fragmentLoadingScreen = (FragmentLoadingScreen) fragment;
            fragmentLoadingScreen.setFragmentListener(this);
        }else if(fragment instanceof FragmentMapView){
            FragmentMapView fragmentMapView = (FragmentMapView) fragment;
            fragmentMapView.setMapStateListener(this);
        }
    }

    //callback method for when data has fully loaded, sets the recycler ArrayLists, changes to map view, and enables the navigation bar.
    public void loadingComplete(){
        dataRepository.resetRecyclerRoadwork();
        dataRepository.resetRecyclerIncident();
        dataRepository.resetRecyclerPlanned();
        switchFragments(fragmentMapView, "Map");
        this.getSupportActionBar().show();
    }

    //callback that FragmentMapView implements so that MainActivity can tell it to update the map
    //when LoadResourcePeriodic uses it's callback to MainActivity... isn't asynchronous java such fun.


    @Override
    public void backButton(){
        switchFragments(fragmentMapView, "Map");
        //every time the fragment with the map resumes it uses the OnMapReady function so don't need to redraw.
        //FragmentMapView fragment = (FragmentMapView) getSupportFragmentManager().findFragmentByTag("mapView");
        //fragment.redrawMap(true);
    }

    @Override
    public void dataUpdated(){
        FragmentMapView fragment = (FragmentMapView) getSupportFragmentManager().findFragmentByTag("mapView");
        fragment.redrawMap(true);
    }

    //replaces the fragments, sets a tag and adds to back stack so that they (including inactive fragments) can be searched for.
    private void switchFragments(Fragment fragment, String type){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if(type.equalsIgnoreCase("LoadingScreen")){
            transaction.replace(R.id.fragment, fragment, "loadingScreen");
        }else if(type.equalsIgnoreCase("Incident")){
            transaction.replace(R.id.fragment, fragment, "incident");
        }else if(type.equalsIgnoreCase("Roadwork")){
            transaction.replace(R.id.fragment, fragment, "roadwork");
        }else if(type.equalsIgnoreCase("PlannedRoadwork")){
            transaction.replace(R.id.fragment, fragment, "plannedRoadwork");
        }else if(type.equalsIgnoreCase("Map")) {
            transaction.replace(R.id.fragment, fragment, "mapView");
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onResume(){
        super.onResume();
        //resume the periodic checking of different data
        loadResourcePeriodic = new LoadResourcePeriodic(dataRepository, this);
        loadResourcePeriodic.execute();
    }

    @Override
    protected void onPause(){
        super.onPause();
        //if the app is paused stop the periodic checking for different data
        loadResourcePeriodic.cancel();
    }
}