package com.e.mpd_assignment;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class DataRepository implements Serializable {

    private JourneyRepository journeyRepository;

    SharedPreferences sharedPreferences = GlobalContext.getContext().getSharedPreferences("MPDSharedPref", MODE_PRIVATE);
    SharedPreferences.Editor myEdit = sharedPreferences.edit();

    String filename = "mpd_assignment";

    private ArrayList<Incident> incidentArrayList;
    private ArrayList<Roadwork> roadworkArrayList;
    private ArrayList<PlannedRoadwork> plannedRoadworkArrayList;


    //recycler view keeps a separate array list because it can be updated by searches.
    private ArrayList<Incident> recyclerIncident;
    private ArrayList<Roadwork> recyclerRoadwork;
    private ArrayList<PlannedRoadwork> recyclerPlanned;

    private ArrayList<String> recyclerJourney;

    public void addRecyclerJourneyStep(String input){
        this.recyclerJourney.add(input);
    }

    public void resetRecyclerJourney(){
        this.recyclerJourney = new ArrayList<>();
    }

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

    public ArrayList<String> getRecyclerJourney(){ return this.recyclerJourney; }

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

    public DataRepository(JourneyRepository jr){
        this.journeyRepository = jr;
        this.incidentArrayList = new ArrayList<>();
        this.roadworkArrayList = new ArrayList<>();
        this.plannedRoadworkArrayList = new ArrayList<>();
        this.recyclerJourney = new ArrayList<>();
    }

    //-------------------------------------------------------------------
    //Journey

    public void storeJourneys(){
        /*StringBuilder sb = new StringBuilder();
        Journey journey = null;
        for(int i = 0, j = journeyArrayList.size(); i<j; i++){
            if(i != 0){
                sb.append(",");
            }
            journey = journeyArrayList.get(i);
            sb.append(journey.getName());
            sb.append("-+-");
            sb.append(journey.getStartLat());
            sb.append("-+-");
            sb.append(journey.getStartLng());
            sb.append("-+-");
            sb.append(journey.getEndLat());
            sb.append("-+-");
            sb.append(journey.getEndLng());
            sb.append("-+-");
            sb.append(journey.getTravelDateMilli());
            sb.append("-+-");
            sb.append(journey.getStartLocation());
            sb.append("-+-");
            sb.append(journey.getEndLocation());
        }
        myEdit.putString("Journeys",sb.toString());
        myEdit.commit();*/
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

    public void deleteJourney(int position){
        this.journeyRepository.deleteJourney(position);
        storeJourneys();
    }

    public void saveJourney(Journey j){
        this.journeyRepository.saveJourney(j);
        storeJourneys();
    }

    public JourneyRepository loadJourneys(){
        /*String initial = sharedPreferences.getString("Journeys", "");
        System.out.println("/////////////////initial: "+initial);
        String[] delimitedString = initial.split(",");
        List<String> templist = Arrays.asList(delimitedString);
        ArrayList<String> tempArrayList = new ArrayList<>(templist);
        for(int i = 0, j = tempArrayList.size(); i<j; i++){
            String[] elements = tempArrayList.get(i).split(Pattern.quote("-+-"));
            if(elements.length == 8){
                String name = elements[0];
                double startLat = Double.parseDouble(elements[1]);
                double startLng = Double.parseDouble(elements[2]);
                double endLat = Double.parseDouble(elements[3]);
                double endLng = Double.parseDouble(elements[4]);
                Calendar date = new GregorianCalendar();
                date.setTimeInMillis(Long.parseLong(elements[5]));
                String start = elements[6];
                String end = elements[7];

                Journey tempJourney = new Journey(name, startLat, startLng, endLat, endLng, date, start, end);
                journeyArrayList.add(tempJourney);
            }
        }*/
        try{
            FileInputStream fis = GlobalContext.getContext().openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            System.out.println(journeyRepository.getJourneyArrayList().size());
            System.out.println("--------------------------------Loading journeyRepository now");
            this.journeyRepository = (JourneyRepository) is.readObject();
            System.out.println(journeyRepository.getJourneyArrayList().size());
            is.close();
            fis.close();
        }catch(Exception e){
            System.out.println("Error-DataRepository-loadJourneys(): "+e);
        }
        return this.journeyRepository;
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
            myEdit.putString("Journeys", "");
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
