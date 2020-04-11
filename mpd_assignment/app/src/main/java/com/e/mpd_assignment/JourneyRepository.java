package com.e.mpd_assignment;

import java.io.Serializable;
import java.util.ArrayList;

/*
    Mark Cottrell - S1627662
 */
//separate from dataRepository to serialize only the saved journey.
class JourneyRepository implements Serializable {

    private ArrayList<Journey> journeyArrayList;

    JourneyRepository(){
        journeyArrayList = new ArrayList<>();
    }

    ArrayList<Journey> getJourneyArrayList(){
        return this.journeyArrayList;
    }

    void deleteJourney(int i){
        journeyArrayList.remove(i);
    }

    void saveJourney(Journey j){
        this.journeyArrayList.add(j);
    }
}
