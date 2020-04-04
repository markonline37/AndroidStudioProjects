package com.e.mpd_assignment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PlannedRoadwork {
    private String title;
    private String description;
    private Calendar startDate;
    private Calendar endDate;
    private double latitude;
    private double longitude;
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
        String temp = input.split("<br />")[0];
        temp = temp.split(", ")[1];
        temp = temp.split(" - ")[0];
        String startDateDay = temp.split(" ")[0];
        String startDateMonth = temp.split(" ")[1];
        startDateMonth = startDateMonth.substring(0,3);
        String startDateYear = temp.split(" ")[2];

        startDate = new GregorianCalendar();
        startDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(startDateDay));
        int month = 0;
        switch(startDateMonth){
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
                System.out.println("Error with month: "+ startDateMonth);
        }
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
        month = 0;
        switch(endDateMonth){
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
                System.out.println("Error with month: "+ endDateMonth);
        }
        endDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(endDateDay));
        endDate.set(Calendar.MONTH, month);
        endDate.set(Calendar.YEAR, Integer.parseInt(endDateYear));
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

    public long getStartDate(){
        return this.startDate.getTimeInMillis();
    }

    public long getEndDate(){
        return this.endDate.getTimeInMillis();
    }

    public String getStartDatePretty(){
        StringBuilder sb = new StringBuilder();
        sb.append("Start Date: ");
        Date date = startDate.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String temp = sdf.format(date);
        sb.append(temp);
        return sb.toString();
    }

    public String getEndDatePretty(){
        StringBuilder sb = new StringBuilder();
        sb.append("End Date: ");
        Date date = endDate.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String temp = sdf.format(date);
        sb.append(temp);
        return sb.toString();
    }
}
