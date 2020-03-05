package com.e.lab6_task2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FragmentOrderDetails extends Fragment {
    View view;
    TextView text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.frag_order_details, container, false);
        text = view.findViewById(R.id.order_details);
        text.setText(MainActivity.orderDetails);
        return view;
    }
}
