package com.e.mpd_assignment;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

//serializable because journeyRepository stores saved journeys.
public class Journey implements Serializable {

    private String name;
    private double startLat;
    private double startLng;
    private double endLat;
    private double endLng;
    private Calendar travelDate;
    private String startLocation;
    private String endLocation;
    private List<List<HashMap<String, String>>> journeyResult;

    Journey(String name, double startLat, double startLng, double endLat, double endLng, Calendar date, String start, String end, List<List<HashMap<String, String>>> result){
        setName(name);
        setStartLat(startLat);
        setStartLng(startLng);
        setEndLat(endLat);
        setEndLng(endLng);
        setTravelDate(date);
        setStartLocation(start);
        setEndLocation(end);
        setJourneyResult(result);
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    private void setStartLat(double startLat){
        this.startLat = startLat;
    }

    double getStartLat(){
        return this.startLat;
    }

    private void setStartLng(double startLng){
        this.startLng = startLng;
    }

    double getStartLng(){
        return this.startLng;
    }

    private void setEndLat(double endLat){
        this.endLat = endLat;
    }

    double getEndLat(){
        return this.endLat;
    }

    private void setEndLng(double endLng){
        this.endLng = endLng;
    }

    double getEndLng(){
        return this.endLng;
    }

    private void setTravelDate(Calendar travelDate){
        this.travelDate = travelDate;
    }

    Calendar getTravelDate(){
        return this.travelDate;
    }

    String getEndLocation() {
        return endLocation;
    }

    private void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    String getStartLocation() {
        return startLocation;
    }

    private void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    List<List<HashMap<String, String>>> getJourneyResult(){
        return journeyResult;
    }

    private void setJourneyResult(List<List<HashMap<String, String>>> journeyResult) {
        this.journeyResult = journeyResult;
    }
}
