package com.example.lab1_task3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button jumbleButton;
    private EditText inputText;
    private TextView outputText;
    private JumbleText jt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jumbleButton = findViewById(R.id.jumbleButton);
        jumbleButton.setOnClickListener(this);
        inputText = findViewById(R.id.inputText);
        outputText = findViewById(R.id.outputText);
        jt = new JumbleText();
    }

    @Override
    public void onClick(View v){
        String temp = inputText.getText().toString();
        if(v == jumbleButton && temp.length() > 0){
            String jumbled = jt.jumbleString((temp));
            outputText.setText(jumbled);
            inputText.setText("");
        }
    }
}
