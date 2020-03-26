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

    public DataRepository(){
        this.incidentArrayList = new ArrayList<>();
        this.roadworkArrayList = new ArrayList<>();
        this.plannedRoadworkArrayList = new ArrayList<>();
    }

    public void addIncident(Incident input){

        incidentArrayList.add(input);
    }

    public ArrayList<Incident> getIncidentArrayList(){
        return incidentArrayList;
    }

    public void addRoadwork(Roadwork roadwork){
        roadworkArrayList.add(roadwork);
    }

    public ArrayList<Roadwork> getRoadworkArrayList(){
        return roadworkArrayList;
    }

    public void addPlannedRoadwork(PlannedRoadwork plannedRoadwork){
        plannedRoadworkArrayList.add(plannedRoadwork);
    }

    public ArrayList<PlannedRoadwork> getPlannedRoadworkArrayList(){
        return plannedRoadworkArrayList;
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

    public void storePlannedRoadworksData(String input){
        myEdit.putString("PlannedRoadworks", input);
        myEdit.commit();
    }

    public String getStoredPlannedRoadworksData(){
        return sharedPreferences.getString("PlannedRoadworks", "");
    }

    public void clearIncidentData(){
        incidentArrayList.clear();
    }

    public void clearRoadworkData(){
        roadworkArrayList.clear();
    }

    public void clearPlannedRoadworkData(){
        plannedRoadworkArrayList.clear();
    }
}
