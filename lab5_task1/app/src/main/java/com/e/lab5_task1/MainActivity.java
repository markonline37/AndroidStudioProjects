package com.e.lab5_task1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity{

    private ViewFlipper flip;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flip=findViewById(R.id.flip);

        flip.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight(){
                flip.showNext();
            }
            public void onSwipeLeft(){
                flip.showPrevious();
            }
        });

        //when a view is displayed
        flip.setInAnimation(this,android.R.anim.fade_in);
        //when a view disappears
        flip.setOutAnimation(this, android.R.anim.fade_out);
    }
}