package com.e.lab6_task2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Fragment fr1;
    private Fragment fr2;

    Button submitOrderButton;
    Button confirmOrder;
    Button cancelOrder;
    static String orderDetails;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        submitOrderButton = findViewById(R.id.submitOrderButton);
        submitOrderButton.setOnClickListener(this);
        confirmOrder = findViewById(R.id.confirmButton);
        confirmOrder.setOnClickListener(this);
        cancelOrder = findViewById(R.id.cancelButton);
        cancelOrder.setOnClickListener(this);
        confirmOrder.setVisibility(View.GONE);
        cancelOrder.setVisibility(View.GONE);

        fr1 = new FragmentOrder();
        fr2 = new FragmentOrderDetails();

        switchFragments(fr1);
    }

    @Override
    public void onClick(View v){
        if(v == cancelOrder){
            Toast.makeText(context, "Order cancelled", Toast.LENGTH_LONG).show();
            switchFragments(fr1);
            submitOrderButton.setVisibility(View.VISIBLE);
            confirmOrder.setVisibility(View.GONE);
            cancelOrder.setVisibility(View.GONE);

        }else if(v == submitOrderButton){
            FragmentOrder.bakePizza();
            switchFragments(fr2);
            submitOrderButton.setVisibility(View.GONE);
            confirmOrder.setVisibility(View.VISIBLE);
            cancelOrder.setVisibility(View.VISIBLE);
        }else if(v == confirmOrder){
            Toast.makeText(context, "Order confirmed", Toast.LENGTH_LONG).show();
            switchFragments(fr1);
            submitOrderButton.setVisibility(View.VISIBLE);
            confirmOrder.setVisibility(View.GONE);
            cancelOrder.setVisibility(View.GONE);
        }
    }

    private void switchFragments(Fragment f){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment,f);
        transaction.commit();
    }
}
