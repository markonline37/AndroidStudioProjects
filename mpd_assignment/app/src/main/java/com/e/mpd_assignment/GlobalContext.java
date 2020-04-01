package com.e.mpd_assignment;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class GlobalContext extends Application {
    private static Context appContext;

    @Override
    public void onCreate(){
        super.onCreate();
        appContext = getApplicationContext();
    }

    //can get context from anywhere, handy for accessing resources and using in fragments.
    public static Context getContext(){
        return appContext;
    }

    static public MotionEvent obtainMotionEvent() {
        MotionEvent event = MotionEvent.obtain(1, 1, 1, 1, 1, 1);
        return event;
    }
}
