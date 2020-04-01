package com.e.mpd_assignment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Roadwork {
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private double latitude;
    private double longitude;
    private String delay = "No Delay";
    private double distance = 0;

    public void setDistance(double distance){
        this.distance = distance;
    }

    public double getDistance(){
        return this.distance;
    }

    public void setTitle(String input){
        this.title = input;
    }

    public void setDescription(String input){
        this.description = input;
    }

    public void setDate(String input){
        //start date
        String startDate = input.split("<br />")[0];
        startDate = startDate.split(", ")[1];
        startDate = startDate.split(" - ")[0];
        String startDateDay = startDate.split(" ")[0];
        String startDateMonth = startDate.split(" ")[1];
        startDateMonth = startDateMonth.substring(0,3);
        String startDateYear = startDate.split(" ")[2];

        //end date
        String endDate = input.split("<br />")[1];
        endDate = endDate.split(", ")[1];
        endDate = endDate.split(" - ")[0];
        String endDateDay = endDate.split(" ")[0];
        String endDateMonth = endDate.split(" ")[1];
        endDateMonth = endDateMonth.substring(0,3);
        String endDateYear = endDate.split(" ")[2];

        //Roadworks delay information.
        if(input.split("<br />").length == 3){
            String temp = input.split("<br />")[2];
            temp = temp.split(": ")[1];
            if(temp.equalsIgnoreCase("No reported delay.") || temp.equalsIgnoreCase("No reported delay")){
                delay = "No Delay";
            }
            //due to coronavirus there is no delays on any roads - assuming it says medium delay etc, can update later.
            else if(temp.equalsIgnoreCase("Medium delay.") || temp.equalsIgnoreCase("Medium delay")){
                delay = "Medium Delay";
            }else if(temp.equalsIgnoreCase("High delay.") || temp.equalsIgnoreCase("High delay")){
                delay = "High Delay";
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
        try {
            this.startDate = format.parse(startDateDay + " " + startDateMonth + " " + startDateYear);
            this.endDate = format.parse(endDateDay + " " + endDateMonth + " " + endDateYear);
        }catch(Exception e){
            System.out.println("Error-Roadwork-setDate(): "+e);
        }
    }

    public void setLatLon(String input){
        String tempLat = input.split(" ")[0];
        String tempLon = input.split(" ")[1];
        this.latitude = Double.parseDouble(tempLat);
        this.longitude = Double.parseDouble(tempLon);
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

    public String getDelay(){ return this.delay; }

    public String getStartDate(){
        return this.startDate.toString();
    }

    public String getEndDate(){
        return this.endDate.toString();
    }
}
