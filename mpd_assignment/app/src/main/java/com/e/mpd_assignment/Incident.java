package com.e.mpd_assignment;


public class Incident {
    private String title;
    private String description;
    private double latitude;
    private double longitude;

    public void setTitle(String input){
        this.title = input;
    }

    public void setDescription(String input){
        this.description = input;
    }

    public void setLatLon(String input){
        this.latitude = Double.parseDouble(input.split(" ")[0]);
        this.longitude = Double.parseDouble(input.split(" ")[1]);
    }

    public String getTitle(){
        return this.title;
    }

    public String getDescription(){
        return this.description;
    }

    public double getLat(){
        return this.latitude;
    }

    public double getLon(){
        return this.longitude;
    }
}
