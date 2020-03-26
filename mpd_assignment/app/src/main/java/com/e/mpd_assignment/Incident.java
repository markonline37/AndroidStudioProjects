package com.e.mpd_assignment;


public class Incident {
    private String title;
    private String description;
    private String latitude;
    private String longitude;

    public void setTitle(String input){
        this.title = input;
    }

    public void setDescription(String input){
        this.description = input;
    }

    public void setLatLon(String input){
        this.latitude = input.split(" ")[0];
        this.longitude = input.split(" ")[1];
    }
}
