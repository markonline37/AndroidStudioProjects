package com.e.mpd_assignment;

import java.io.Serializable;
import java.util.ArrayList;

public class JourneyRepository implements Serializable {

    private ArrayList<Journey> journeyArrayList;

    JourneyRepository(ArrayList<Journey> j){
        this.journeyArrayList = j;
    }

    public void setJourneyArrayList(ArrayList<Journey> j){
        this.journeyArrayList = j;
    }

    JourneyRepository(){
        journeyArrayList = new ArrayList<>();
    }

    public ArrayList<Journey> getJourneyArrayList(){
        return this.journeyArrayList;
    }

    public void deleteJourney(int i){
        journeyArrayList.remove(i);
    }

    public void saveJourney(Journey j){
        this.journeyArrayList.add(j);
    }
}
