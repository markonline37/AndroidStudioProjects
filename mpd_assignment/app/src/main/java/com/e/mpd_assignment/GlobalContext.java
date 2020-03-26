package com.e.mpd_assignment;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

public class GlobalContext extends Application {
    private static Context appContext;

    @Override
    public void onCreate(){
        super.onCreate();
        appContext = getApplicationContext();
    }

    //can get context from anywhere, handy for accessing resources etc.
    public static Context getContext(){
        return appContext;
    }
}
