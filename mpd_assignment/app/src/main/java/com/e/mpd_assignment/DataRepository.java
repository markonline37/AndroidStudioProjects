package com.e.mpd_assignment;

import android.content.SharedPreferences;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class DataRepository {

    SharedPreferences sharedPreferences = GlobalContext.getContext().getSharedPreferences("MPDSharedPref", MODE_PRIVATE);
    SharedPreferences.Editor myEdit = sharedPreferences.edit();

    private ArrayList<Incident> incidentArrayList;
    private ArrayList<Roadwork> roadworkArrayList;
    private ArrayList<PlannedRoadwork> plannedRoadworkArrayList;

    //recycler view keeps a separate array list because it can be updated by searches.
    private ArrayList<Incident> recyclerIncident;
    private ArrayList<Roadwork> recyclerRoadwork;
    private ArrayList<PlannedRoadwork> recyclerPlanned;

    public void resetRecyclerRoadwork(){
        this.recyclerRoadwork = roadworkArrayList;
    }

    public void resetRecyclerIncident(){
        this.recyclerIncident = incidentArrayList;
    }

    public void resetRecyclerPlanned(){
        this.recyclerPlanned = plannedRoadworkArrayList;
    }

    public ArrayList<Roadwork> getRecyclerRoadwork(){
        return this.recyclerRoadwork;
    }

    public ArrayList<Incident> getRecyclerIncident(){
        return this.recyclerIncident;
    }

    public ArrayList<PlannedRoadwork> getRecyclerPlanned(){
        return this.recyclerPlanned;
    }

    public void setRecyclerRoadwork(ArrayList<Roadwork> temp){
        this.recyclerRoadwork = temp;
    }

    public void setRecyclerIncident(ArrayList<Incident> temp){
        this.recyclerIncident = temp;
    }

    public void setRecyclerPlanned(ArrayList<PlannedRoadwork> temp){
        this.recyclerPlanned = temp;
    }

    private boolean followMe = false;

    public DataRepository(){
        this.incidentArrayList = new ArrayList<>();
        this.roadworkArrayList = new ArrayList<>();
        this.plannedRoadworkArrayList = new ArrayList<>();
    }

    //-------------------------------------------------------------------
    //Preferences

    public void checkFirstLoad(){
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
            myEdit.commit();
        }
    }

    public void storeIncidentsData(String input){
        myEdit.putString("Incidents", input);
        myEdit.commit();
    }

    public String getStoredIncidentsData(){
        return sharedPreferences.getString("Incidents", "");
    }

    public void storeRoadworksData(String input){
        myEdit.putString("Roadworks", input);
        myEdit.commit();
    }

    public String getStoredRoadworksData(){
        return sharedPreferences.getString("Roadworks", "");
    }

    public String getStoredPlannedRoadworksData(){
        return sharedPreferences.getString("PlannedRoadworks", "");
    }

    public void clearPlannedRoadworkData(){
        plannedRoadworkArrayList.clear();
    }

    public int getDefaultViewDistanceIncident(){
        String temp = sharedPreferences.getString("defaultViewDistanceIncident", "");
        int tempInt = 0;
        try{
            tempInt = Integer.parseInt(temp);
        }catch(Exception e){
            System.out.println("Error-DataRepository-getDefaultViewDistanceIncident(): "+e);
        }
        return tempInt;
    }

    public void setDefaultViewDistanceIncidient(int input){
        myEdit.putString("defaultViewDistanceIncident", String.valueOf(input));
        myEdit.commit();
    }

    public int getDefaultViewDistanceRoadworks(){
        String temp = sharedPreferences.getString("defaultViewDistanceRoadworks", "");
        int tempInt = 0;
        try{
            tempInt = Integer.parseInt(temp);
        }catch(Exception e){
            System.out.println("Error-DataRepository-getDefaultViewDistanceRoadworks(): "+e);
        }
        return tempInt;
    }

    public void setDefaultViewDistanceRoadworks(int input){
        myEdit.putString("defaultViewDistanceRoadworks", String.valueOf(input));
        myEdit.commit();
    }

    public int getDefaultViewDistancePlannedRoadworks(){
        String temp = sharedPreferences.getString("defaultViewDistancePlannedRoadworks", "");
        int tempInt = 0;
        try{
            tempInt = Integer.parseInt(temp);
        }catch(Exception e){
            System.out.println("Error-DataRepository-getDefaultViewDistancePlannedRoadworks(): "+e);
        }
        return tempInt;
    }

    public void setDefaultViewDistancePlannedRoadworks(int input){
        myEdit.putString("defaultViewDistancePlannedRoadworks", String.valueOf(input));
        myEdit.commit();
    }

    public String getDefaultMapCheckboxViewIncident(){
        return sharedPreferences.getString("defaultMapCheckboxViewIncident", "");
    }

    public void setDefaultMapCheckboxViewIncident(String input){
        myEdit.putString("defaultMapCheckboxViewIncident", input);
        myEdit.commit();
    }

    public String getDefaultMapCheckboxViewRoadworks(){
        return sharedPreferences.getString("defaultMapCheckboxViewRoadworks", "");
    }

    public void setDefaultMapCheckboxViewRoadworks(String input){
        myEdit.putString("defaultMapCheckboxViewRoadworks", input);
        myEdit.commit();
    }

    public String getDefaultMapCheckboxViewPlannedRoadworks(){
        return sharedPreferences.getString("defaultMapCheckboxViewPlannedRoadworks", "");
    }

    public void setDefaultMapCheckboxViewPlannedRoadworks(String input){
        myEdit.putString("defaultMapCheckboxViewPlannedRoadworks", input);
        myEdit.commit();
    }

    public String getDefaultMapCheckboxViewFollowMe(){
        return sharedPreferences.getString("defaultMapCheckboxViewFollowMe", "");
    }

    public void setDefaultMapCheckboxViewFollowMe(String input){
        myEdit.putString("defaultMapCheckboxViewFollowMe", input);
        myEdit.commit();
    }

    public boolean getFollowMe(){
        return followMe;
    }

    public void setFollowMe(String input){
        myEdit.putString("defaultMapCheckboxViewFollowMe", input);
        myEdit.commit();
        if (input.equals("checked")) {
            followMe = true;
        }else{
            followMe = false;
        }
    }

    //-------------------------------------------------------------------
    //Incident - ArrayList

    public void addIncident(Incident input){

        incidentArrayList.add(input);
    }

    public ArrayList<Incident> getIncidentArrayList(){
        return incidentArrayList;
    }

    public void clearIncidentData(){
        incidentArrayList.clear();
    }

    //-------------------------------------------------------------------
    //Roadwork - ArrayList

    public void addRoadwork(Roadwork roadwork){
        roadworkArrayList.add(roadwork);
    }

    public ArrayList<Roadwork> getRoadworkArrayList(){
        return roadworkArrayList;
    }

    public void clearRoadworkData(){
        roadworkArrayList.clear();
    }

    //-------------------------------------------------------------------
    //Planned Roadwork - ArrayList

    public void addPlannedRoadwork(PlannedRoadwork plannedRoadwork){
        plannedRoadworkArrayList.add(plannedRoadwork);
    }

    public ArrayList<PlannedRoadwork> getPlannedRoadworkArrayList(){
        return plannedRoadworkArrayList;
    }

    public void storePlannedRoadworksData(String input){
        myEdit.putString("PlannedRoadworks", input);
        myEdit.commit();
    }


}
