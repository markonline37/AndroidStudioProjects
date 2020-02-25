package a.lab5_task3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button red;
    private Button blue;
    private Button green;
    private TextView purple;
    private TextView yellow;
    private TextView orange;
    private TextView pink;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        red = findViewById(R.id.red);
        blue = findViewById(R.id.blue);
        green = findViewById(R.id.green);

        red.setOnClickListener(this);
        blue.setOnClickListener(this);
        green.setOnClickListener(this);

        purple = findViewById(R.id.purple);
        yellow = findViewById(R.id.yellow);
        orange = findViewById(R.id.orange);
        pink = findViewById(R.id.pink);

        purple.setOnClickListener(this);
        yellow.setOnClickListener(this);
        orange.setOnClickListener(this);
        pink.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        defaultColor();
        if(v == red){
            red.setBackgroundColor(getResources().getColor(R.color.red));
        }else if(v == blue){
            blue.setBackgroundColor(getResources().getColor(R.color.blue));
        }else if(v == green){
            green.setBackgroundColor(getResources().getColor(R.color.green));
        }else if(v == purple){
            purple.setBackgroundResource(R.color.purple);
        }else if(v == yellow){
            yellow.setBackgroundResource(R.color.yellow);
        }else if(v == orange){
            orange.setBackgroundResource(R.color.orange);
        }else if(v == pink){
            pink.setBackgroundResource(R.color.pink);
        }
    }

    private void defaultColor(){
        red.setBackgroundResource(android.R.drawable.btn_default);
        green.setBackgroundResource(android.R.drawable.btn_default);
        blue.setBackgroundResource(android.R.drawable.btn_default);
        purple.setBackgroundResource(0);
        yellow.setBackgroundResource(0);
        orange.setBackgroundResource(0);
        pink.setBackgroundResource(0);
    }
}
