package com.e.mpd_assignment;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/*
    main storage Repository, has arrays and appropriate access methods
    also loads the preferences (for map checkboxes (show incidents etc))
    also loads/saves the serialized journeyRepository.
*/
public class DataRepository implements Serializable {

    private JourneyRepository journeyRepository;

    private SharedPreferences sharedPreferences = GlobalContext.getContext().getSharedPreferences("MPDSharedPref", MODE_PRIVATE);
    private SharedPreferences.Editor myEdit = sharedPreferences.edit();

    private String filename = "mpd_assignment";

    private ArrayList<Incident> incidentArrayList;
    private ArrayList<Roadwork> roadworkArrayList;
    private ArrayList<PlannedRoadwork> plannedRoadworkArrayList;


    //recycler view keeps a separate array list because it can be updated by searches.
    private ArrayList<Incident> recyclerIncident;
    private ArrayList<Roadwork> recyclerRoadwork;
    private ArrayList<PlannedRoadwork> recyclerPlanned;
    //recycler used to store searched for journey roadworks/incidents along route
    private ArrayList<String> recyclerJourney;

    void addRecyclerJourneyStep(String input){
        this.recyclerJourney.add(input);
    }

    void resetRecyclerJourney(){
        this.recyclerJourney = new ArrayList<>();
    }

    void resetRecyclerRoadwork(){
        this.recyclerRoadwork = roadworkArrayList;
    }

    void resetRecyclerIncident(){
        this.recyclerIncident = incidentArrayList;
    }

    void resetRecyclerPlanned(){
        this.recyclerPlanned = plannedRoadworkArrayList;
    }

    ArrayList<Roadwork> getRecyclerRoadwork(){
        return this.recyclerRoadwork;
    }

    ArrayList<Incident> getRecyclerIncident(){
        return this.recyclerIncident;
    }

    ArrayList<PlannedRoadwork> getRecyclerPlanned(){
        return this.recyclerPlanned;
    }

    ArrayList<String> getRecyclerJourney(){ return this.recyclerJourney; }

    void setRecyclerRoadwork(ArrayList<Roadwork> temp){
        this.recyclerRoadwork = temp;
    }

    void setRecyclerIncident(ArrayList<Incident> temp){
        this.recyclerIncident = temp;
    }

    void setRecyclerPlanned(ArrayList<PlannedRoadwork> temp){
        this.recyclerPlanned = temp;
    }

    private boolean followMe = false;

    DataRepository(JourneyRepository jr){
        this.journeyRepository = jr;
        this.incidentArrayList = new ArrayList<>();
        this.roadworkArrayList = new ArrayList<>();
        this.plannedRoadworkArrayList = new ArrayList<>();
        this.recyclerJourney = new ArrayList<>();
    }

    //-------------------------------------------------------------------
    //Journey

    private void storeJourneys(){
        try{
            FileOutputStream fos = GlobalContext.getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(journeyRepository);
            os.close();
            fos.close();
        }catch(Exception e){
            System.out.println("Error-DataRepository-storeJourneys(): "+e);
        }
    }

    void deleteJourney(int position){
        this.journeyRepository.deleteJourney(position);
        storeJourneys();
    }

    void saveJourney(Journey j){
        this.journeyRepository.saveJourney(j);
        storeJourneys();
    }

    JourneyRepository loadJourneys(){
        try{
            FileInputStream fis = GlobalContext.getContext().openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            this.journeyRepository = (JourneyRepository) is.readObject();
            is.close();
            fis.close();
        }catch(Exception e){
            System.out.println("Error-DataRepository-loadJourneys(): "+e);
        }
        return this.journeyRepository;
    }



    //-------------------------------------------------------------------
    //Preferences

    void checkFirstLoad(){
        //if empty data must be first load so set some default values - assuming there will always be roadworks
        if(sharedPreferences.getString("Roadworks", "").equals("")){
            myEdit.putString("Incidents", "");
            myEdit.putString("Roadworks", "");
            myEdit.putString("Planned Roadworks", "");
            myEdit.putString("defaultViewDistanceIncident", "20");
            myEdit.putString("defaultViewDistanceRoadworks", "20");
            myEdit.putString("defaultViewDistancePlannedRoadworks", "20");
            myEdit.putString("defaultMapCheckboxViewIncident", "checked");
            myEdit.putString("defaultMapCheckboxViewRoadworks", "checked");
            myEdit.putString("defaultMapCheckboxViewPlannedRoadworks", "unchecked");
            myEdit.putString("defaultMapCheckboxViewFollowMe", "checked");
            myEdit.putString("Journeys", "");
            myEdit.commit();
        }
    }

    void storeIncidentsData(String input){
        myEdit.putString("Incidents", input);
        myEdit.commit();
    }

    String getStoredIncidentsData(){
        return sharedPreferences.getString("Incidents", "");
    }

    void storeRoadworksData(String input){
        myEdit.putString("Roadworks", input);
        myEdit.commit();
    }

    String getStoredRoadworksData(){
        return sharedPreferences.getString("Roadworks", "");
    }

    String getStoredPlannedRoadworksData(){
        return sharedPreferences.getString("PlannedRoadworks", "");
    }

    void clearPlannedRoadworkData(){
        plannedRoadworkArrayList.clear();
    }

    String getDefaultMapCheckboxViewIncident(){
        return sharedPreferences.getString("defaultMapCheckboxViewIncident", "");
    }

    void setDefaultMapCheckboxViewIncident(String input){
        myEdit.putString("defaultMapCheckboxViewIncident", input);
        myEdit.commit();
    }

    String getDefaultMapCheckboxViewRoadworks(){
        return sharedPreferences.getString("defaultMapCheckboxViewRoadworks", "");
    }

    void setDefaultMapCheckboxViewRoadworks(String input){
        myEdit.putString("defaultMapCheckboxViewRoadworks", input);
        myEdit.commit();
    }

    String getDefaultMapCheckboxViewPlannedRoadworks(){
        return sharedPreferences.getString("defaultMapCheckboxViewPlannedRoadworks", "");
    }

    void setDefaultMapCheckboxViewPlannedRoadworks(String input){
        myEdit.putString("defaultMapCheckboxViewPlannedRoadworks", input);
        myEdit.commit();
    }

    String getDefaultMapCheckboxViewFollowMe(){
        return sharedPreferences.getString("defaultMapCheckboxViewFollowMe", "");
    }

    boolean getFollowMe(){
        return followMe;
    }

    void setFollowMe(String input){
        myEdit.putString("defaultMapCheckboxViewFollowMe", input);
        myEdit.commit();
        followMe = input.equals("checked");
    }

    //-------------------------------------------------------------------
    //Incident - ArrayList

    void addIncident(Incident input){

        incidentArrayList.add(input);
    }

    ArrayList<Incident> getIncidentArrayList(){
        return incidentArrayList;
    }

    void clearIncidentData(){
        incidentArrayList.clear();
    }

    //-------------------------------------------------------------------
    //Roadwork - ArrayList

    void addRoadwork(Roadwork roadwork){
        roadworkArrayList.add(roadwork);
    }

    ArrayList<Roadwork> getRoadworkArrayList(){
        return roadworkArrayList;
    }

    void clearRoadworkData(){
        roadworkArrayList.clear();
    }

    //-------------------------------------------------------------------
    //Planned Roadwork - ArrayList

    void addPlannedRoadwork(PlannedRoadwork plannedRoadwork){
        plannedRoadworkArrayList.add(plannedRoadwork);
    }

    ArrayList<PlannedRoadwork> getPlannedRoadworkArrayList(){
        return plannedRoadworkArrayList;
    }

    void storePlannedRoadworksData(String input){
        myEdit.putString("PlannedRoadworks", input);
        myEdit.commit();
    }
}
