package com.e.mpd_assignment;

import java.io.Serializable;

/*
    Mark Cottrell - S1627662
 */
class Incident implements Serializable {
    private String title;
    private String description;
    private double latitude;
    private double longitude;
    private double distance = 0;

    void setDistance(double distance){
        this.distance = distance;
    }

    double getDistance(){
        return this.distance;
    }

    public void setTitle(String input){
        this.title = input;
    }

    void setDescription(String input){
        this.description = input;
    }

    void setLatLon(String input){
        this.latitude = Double.parseDouble(input.split(" ")[0]);
        this.longitude = Double.parseDouble(input.split(" ")[1]);
    }

    public String getTitle(){
        return this.title;
    }

    String getDescription(){
        return this.description;
    }

    double getLat(){
        return this.latitude;
    }

    double getLon(){
        return this.longitude;
    }
}
