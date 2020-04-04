package com.e.mpd_assignment;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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

    public Journey(String name, double startLat, double startLng, double endLat, double endLng, Calendar date, String start, String end, List<List<HashMap<String, String>>> result){
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

    public void setStartLat(double startLat){
        this.startLat = startLat;
    }

    public double getStartLat(){
        return this.startLat;
    }

    public void setStartLng(double startLng){
        this.startLng = startLng;
    }

    public double getStartLng(){
        return this.startLng;
    }

    public void setEndLat(double endLat){
        this.endLat = endLat;
    }

    public double getEndLat(){
        return this.endLat;
    }

    public void setEndLng(double endLng){
        this.endLng = endLng;
    }

    public double getEndLng(){
        return this.endLng;
    }

    public void setTravelDate(Calendar travelDate){
        this.travelDate = travelDate;
    }

    public Calendar getTravelDate(){
        return this.travelDate;
    }

    public long getTravelDateMilli(){
        return this.travelDate.getTimeInMillis();
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public List<List<HashMap<String, String>>> getJourneyResult(){
        return journeyResult;
    }

    public void setJourneyResult(List<List<HashMap<String, String>>> journeyResult) {
        this.journeyResult = journeyResult;
    }
}
