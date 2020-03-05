package com.e.lab6_task1;

import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ProgressBar;


public class LoadResource extends AsyncTask<Integer, Integer, String>{

    ProgressBar p;
    Button b;
    int status;

    protected void onPreExecute(ProgressBar p, Button b){
        this.p = p;
        this.b = b;
        b.setText("Restart");
        status = 0;
    }

    @Override
    protected String doInBackground(Integer... objects){
        for(; status < objects[0]; status++){
            try{
                publishProgress(status);
                Thread.sleep(1000);
            }catch(InterruptedException e){
                System.out.println("doInBackground() error: "+e);
            }
        }
        return "Done";
    }

    @Override
    protected void onProgressUpdate(Integer... values){
        p.setProgress(values[0]);
        //super.onProgressUpdate(values);
    }
}
