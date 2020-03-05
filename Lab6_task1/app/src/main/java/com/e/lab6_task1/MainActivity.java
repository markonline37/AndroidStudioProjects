package com.e.lab6_task1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar progressBar;
    private Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b = findViewById(R.id.btnStart);
        b.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(10);
    }

    @Override
    public void onClick(View v){
        if(v == b){
            LoadResource task = new LoadResource();
            progressBar.setProgress(0);
            task.onPreExecute(progressBar, b);
            task.execute(10);
        }
    }
}
