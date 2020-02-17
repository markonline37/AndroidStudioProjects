package com.e.lab3_task2;

public class WidgetClass
{
    private String bolt;
    private String washer;
    private String nut;

    public WidgetClass()
    {
        bolt = "";
        washer = "";
        nut = "";
    }

    public WidgetClass(String abolt,String awasher,String anut)
    {
        bolt = abolt;
        washer = awasher;
        nut = anut;
    }

    public String getBolt()
    {
        return bolt;
    }

    public void setBolt(String abolt)
    {
        bolt = abolt;
    }

    public String getWasher()
    {
        return washer;
    }

    public void setWasher(String awasher)
    {
        washer = awasher;
    }

    public String getNut()
    {
        return nut;
    }

    public void setNut(String anut)
    {
        nut = anut;
    }

    public String toString()
    {
        String temp;

        temp = bolt + " " + washer + " " + nut;

        return temp;
    }

} // End of class
