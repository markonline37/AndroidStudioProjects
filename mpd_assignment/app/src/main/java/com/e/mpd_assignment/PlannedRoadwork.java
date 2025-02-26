package com.e.mpd_assignment;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/*
    Mark Cottrell - S1627662
 */
class PlannedRoadwork implements Serializable {
    private String title;
    private String description;
    private Calendar startDate;
    private Calendar endDate;
    private double latitude;
    private double longitude;
    private double distance = 0;

    void setDistance(double distance){
        this.distance = distance;
    }

    double getDistance(){
        return this.distance;
    }

    void setTitle(String input){
        this.title = input;
    }

    void setDescription(String input){
        this.description = input;
    }

    void setDate(String input){
        //start date
        String temp = input.split("<br />")[0];
        temp = temp.split(", ")[1];
        temp = temp.split(" - ")[0];
        String startDateDay = temp.split(" ")[0];
        String startDateMonth = temp.split(" ")[1];
        startDateMonth = startDateMonth.substring(0,3);
        String startDateYear = temp.split(" ")[2];

        startDate = new GregorianCalendar();
        startDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(startDateDay));
        int month = getMonthInt(startDateMonth);
        startDate.set(Calendar.MONTH, month);
        startDate.set(Calendar.YEAR, Integer.parseInt(startDateYear));


        //end date
        String temp1 = input.split("<br />")[1];
        temp1 = temp1.split(", ")[1];
        temp1 = temp1.split(" - ")[0];
        String endDateDay = temp1.split(" ")[0];
        String endDateMonth = temp1.split(" ")[1];
        endDateMonth = endDateMonth.substring(0,3);
        String endDateYear = temp1.split(" ")[2];

        endDate = new GregorianCalendar();
        month = getMonthInt(endDateMonth);
        endDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(endDateDay));
        endDate.set(Calendar.MONTH, month);
        endDate.set(Calendar.YEAR, Integer.parseInt(endDateYear));
    }

    void setLatLon(String input){
        String tempLat = input.split(" ")[0];
        String tempLon = input.split(" ")[1];
        this.latitude = Double.parseDouble(tempLat);
        this.longitude = Double.parseDouble(tempLon);
    }

    String getTitle(){
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

    long getStartDate(){
        return this.startDate.getTimeInMillis();
    }

    long getEndDate(){
        return this.endDate.getTimeInMillis();
    }

    String getStartDatePretty(){
        StringBuilder sb = new StringBuilder();
        sb.append("Start Date: ");
        Date date = startDate.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault());
        String temp = sdf.format(date);
        sb.append(temp);
        return sb.toString();
    }

    String getEndDatePretty(){
        StringBuilder sb = new StringBuilder();
        sb.append("End Date: ");
        Date date = endDate.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault());
        String temp = sdf.format(date);
        sb.append(temp);
        return sb.toString();
    }

    //calendar uses jan = 0 for month.
    private int getMonthInt(String input){
        int month = 0;
        switch(input){
            case "Jan":
                month = 0;
                break;
            case "Feb":
                month = 1;
                break;
            case "Mar":
                month = 2;
                break;
            case "Apr":
                month = 3;
                break;
            case "May":
                month = 4;
                break;
            case "Jun":
                month = 5;
                break;
            case "Jul":
                month = 6;
                break;
            case "Aug":
                month = 7;
                break;
            case "Sep":
                month = 8;
                break;
            case "Oct":
                month = 9;
                break;
            case "Nov":
                month = 10;
                break;
            case "Dec":
                month = 11;
                break;
            default:
                System.out.println("Error with month: "+ input);
        }
        return month;
    }
}
