package com.e.lab3_task1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText input1;
    private EditText input2;
    private Button convertButton;
    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input1 = findViewById(R.id.input1);
        input2 = findViewById(R.id.input2);
        convertButton = findViewById(R.id.convertButton);
        convertButton.setOnClickListener(this);
        output = findViewById(R.id.output);


    }

    @Override
    public void onClick(View v) {
        try{
            String temp1 = input1.getText().toString();
            String temp2 = input2.getText().toString();
            if(v == convertButton){
                if(temp1.length() > 0 && input2.length() > 0) {
                    if(temp1.matches("[0-9.]*") && temp2.matches("[0-9.]*")){
                        double width = new Double(temp1).doubleValue();
                        double height = new Double(temp2).doubleValue();
                        double total = width*height;
                        double scale = Math.pow(10, 2);
                        output.setText("Volume is: "+Math.round(total*scale)/scale);
                        input1.setText("");
                        input1.requestFocus();
                        input2.setText("");
                    }else{
                        output.setText("Digits and decimal points only.");
                        boolean temp3 = false;
                        boolean temp4 = false;
                        if(!temp1.matches("[0-9.]*")){
                            input1.setText("");
                            temp3 = true;
                        }
                        if(!temp2.matches("[0-9.]*")){
                            input2.setText("");
                            temp4 = true;
                        }
                        if(temp3 && temp4){
                            input1.requestFocus();
                        }else if(temp3){
                            input1.requestFocus();
                        }else if(temp4){
                            input2.requestFocus();
                        }else{
                            input1.requestFocus();
                        }
                    }
                }else{
                    output.setText("You must enter 2 values");
                    if(temp1.length() == 0){
                        input1.requestFocus();
                    }else{
                        input2.requestFocus();
                    }
                }
            }
        }catch(Exception e) {
            System.out.println("Error: "+e);
        }
    }
}