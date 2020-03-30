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
                                                                FragmentIncidents.FragmentIncidentsListener,
                                                                FragmentRoadworks.FragmentRoadworksListener,
                                                                FragmentPlannedRoadworks.FragmentPlannedRoadworksListener,
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
            switchFragments(fragmentIncidents);
            FragmentIncidents fragment = (FragmentIncidents) getSupportFragmentManager().findFragmentByTag("incident");
            fragment.scrollTo(position);
        }else if(type.equals("Roadworks")){
            switchFragments(fragmentRoadworks);
            FragmentRoadworks fragment = (FragmentRoadworks) getSupportFragmentManager().findFragmentByTag("roadwork");
            fragment.scrollTo(position);
        }else if(type.equals("PlannedRoadworks")){
            switchFragments(fragmentPlannedRoadworks);
            FragmentPlannedRoadworks fragment = (FragmentPlannedRoadworks) getSupportFragmentManager().findFragmentByTag("plannedRoadwork");
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

        drawerLayout = findViewById(R.id.activity_main);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.nv);
        navigationView.setItemIconTintList(null);
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
                    FragmentMapView fragment = (FragmentMapView) getSupportFragmentManager().findFragmentById(R.id.fragment);
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
                    FragmentMapView fragment = (FragmentMapView) getSupportFragmentManager().findFragmentById(R.id.fragment);
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
                    FragmentMapView fragment = (FragmentMapView) getSupportFragmentManager().findFragmentById(R.id.fragment);
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
                    FragmentMapView fragment = (FragmentMapView) getSupportFragmentManager().findFragmentById(R.id.fragment);
                    fragment.redrawMap(true);
                }else if(id == R.id.buttonIncidents){
                    recyclerViewAdapter.setType("Incident");
                    switchFragments(fragmentIncidents);
                    drawerLayout.closeDrawers();
                }else if(id == R.id.buttonRoadworks){
                    recyclerViewAdapter.setType("Roadworks");
                    switchFragments(fragmentRoadworks);
                    drawerLayout.closeDrawers();
                }else if(id == R.id.buttonPlannedRoadworks){
                    recyclerViewAdapter.setType("PlannedRoadworks");
                    switchFragments(fragmentPlannedRoadworks);
                    drawerLayout.closeDrawers();
                }

                return false;
            }
        });

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

        recyclerViewAdapter = new RecyclerViewAdapter(this, dataRepository);

        fragmentLoadingScreen = new FragmentLoadingScreen(dataRepository);

        fragmentMapView = new FragmentMapView(dataRepository);
        fragmentIncidents = new FragmentIncidents(dataRepository, recyclerViewAdapter);
        fragmentRoadworks = new FragmentRoadworks(dataRepository, recyclerViewAdapter);
        fragmentPlannedRoadworks = new FragmentPlannedRoadworks(dataRepository, recyclerViewAdapter);

        //need to load the unused fragments into the backstacktrace so that they can be searched for later.
        switchFragments(fragmentIncidents);
        switchFragments(fragmentRoadworks);
        switchFragments(fragmentPlannedRoadworks);
        //then display the loading screen.
        switchFragments(fragmentLoadingScreen);

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
        }else if(fragment instanceof  FragmentIncidents){
            FragmentIncidents fragmentIncidents = (FragmentIncidents) fragment;
            fragmentIncidents.setFragmentIncidentsListener(this);
        }else if(fragment instanceof  FragmentRoadworks){
            FragmentRoadworks fragmentRoadworks = (FragmentRoadworks) fragment;
            fragmentRoadworks.setFragmentRoadworksListener(this);
        }else if(fragment instanceof  FragmentPlannedRoadworks){
            FragmentPlannedRoadworks fragmentPlannedRoadworks = (FragmentPlannedRoadworks) fragment;
            fragmentPlannedRoadworks.setFragmentPlannedRoadworksListener(this);
        }else if(fragment instanceof FragmentMapView){
            FragmentMapView fragmentMapView = (FragmentMapView) fragment;
            fragmentMapView.setMapStateListener(this);
        }
    }

    public void loadingComplete(){
        switchFragments(fragmentMapView);
        this.getSupportActionBar().show();
    }

    //callback that FragmentMapView implements so that MainActivity can tell it to update the map
    //when LoadResourcePeriodic uses it's callback to MainActivity... isn't asynchronous java such fun.


    @Override
    public void backButton(){
        switchFragments(fragmentMapView);
        FragmentMapView fragment = (FragmentMapView) getSupportFragmentManager().findFragmentByTag("mapView");
        fragment.redrawMap(true);
    }

    @Override
    public void dataUpdated(){
        FragmentMapView fragment = (FragmentMapView) getSupportFragmentManager().findFragmentById(R.id.fragment);
        fragment.redrawMap(true);
    }

    private void switchFragments(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if(fragment instanceof FragmentLoadingScreen){
            transaction.replace(R.id.fragment, fragment, "loadingScreen");
        }else if(fragment instanceof  FragmentIncidents){
            transaction.replace(R.id.fragment, fragment, "incident");
        }else if(fragment instanceof  FragmentRoadworks){
            transaction.replace(R.id.fragment, fragment, "roadwork");
        }else if(fragment instanceof  FragmentPlannedRoadworks){
            transaction.replace(R.id.fragment, fragment, "plannedRoadwork");
        }else if(fragment instanceof FragmentMapView) {
            transaction.replace(R.id.fragment, fragment, "mapView");
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadResourcePeriodic = new LoadResourcePeriodic(dataRepository, this);
        loadResourcePeriodic.execute();
    }

    @Override
    protected void onPause(){
        super.onPause();
        loadResourcePeriodic.cancel();
    }
}