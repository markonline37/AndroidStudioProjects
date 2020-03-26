package com.e.mpd_assignment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Roadwork {
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private String latitude;
    private String longitude;

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

        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
        try {
            this.startDate = format.parse(startDateDay + " " + startDateMonth + " " + startDateYear);
            this.endDate = format.parse(endDateDay + " " + endDateMonth + " " + endDateYear);
        }catch(Exception e){
            System.out.println("Error-Roadwork-setDate(): "+e);
        }
    }

    public void setLatLon(String input){
        this.latitude = input.split(" ")[0];
        this.longitude = input.split(" ")[1];
    }
}
