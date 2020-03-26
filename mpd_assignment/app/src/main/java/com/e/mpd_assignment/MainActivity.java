package com.e.mpd_assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements FragmentLoadingScreen.FragmentListener, LoadResourcePeriodic.LoadResourcePeriodicListener {

    private DataRepository dataRepository;
    private Fragment fragmentLoadingScreen;
    private Fragment fragmentMapView;
    private LoadResourcePeriodic loadResourcePeriodic;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataRepository = new DataRepository();

        fragmentLoadingScreen = new FragmentLoadingScreen(dataRepository);
        switchFragments(fragmentLoadingScreen);
        fragmentMapView = new FragmentMapView(dataRepository, this);
    }

    //implement the listener so fragment can communicate with MainActivity
    @Override
    public void onAttachFragment(Fragment fragment){
        if(fragment instanceof FragmentLoadingScreen){
            FragmentLoadingScreen fragmentLoadingScreen = (FragmentLoadingScreen) fragment;
            fragmentLoadingScreen.setFragmentListener(this);
        }
    }

    public void loadingComplete(){
        switchFragments(fragmentMapView);
    }

    @Override
    public void dataUpdated(){
        mainCallback.updateMap();
    }

    private void switchFragments(Fragment fragment){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
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

    //callback that FragmentMapView implements so that MainActivity can tell it to update the map
    //when LoadResourcePeriodic uses it's callback to MainActivity... isn't asynchronous java such fun.
    private MainActivityListener mainCallback;

    public interface MainActivityListener{
        void updateMap();
    }

    public void setMainActivityListener(MainActivityListener callback){ this.mainCallback = callback;}
}