package com.e.mpd_assignment;

import android.app.Application;
import android.content.Context;
import android.view.MotionEvent;

/*
    Mark Cottrell - S1627662
 */
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
        return MotionEvent.obtain(1, 1, 1, 1, 1, 1);
    }
}
