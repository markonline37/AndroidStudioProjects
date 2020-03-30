package com.e.mpd_assignment;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FragmentPlannedRoadworks extends Fragment implements  RecyclerViewAdapter.ItemClickListener, View.OnClickListener {

    private View view;
    private FragmentPlannedRoadworksListener callback;
    private DataRepository dataRepository;
    private ArrayList<PlannedRoadwork> plannedRoadworkArrayList;
    private Button backButton;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private boolean shouldScroll = false;
    private int scrollValue = 0;

    public FragmentPlannedRoadworks(DataRepository dataRepository, RecyclerViewAdapter recyclerViewAdapter){
        this.dataRepository = dataRepository;
        this.recyclerViewAdapter = recyclerViewAdapter;
    }

    public void setFragmentPlannedRoadworksListener(FragmentPlannedRoadworksListener callback){
        this.callback = callback;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        int orientation = this.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            //portrait
            view = inflater.inflate(R.layout.fragment_planned_roadworks, container, false);
        }else{
            //landscape
            view = inflater.inflate(R.layout.fragment_planned_roadworks_landscape, container, false);
        }

        recyclerView = view.findViewById(R.id.recyclerPlannedRoadworks);
        recyclerView.setLayoutManager(new LinearLayoutManager(GlobalContext.getContext()));
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);

        backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == backButton){
            callback.backButton();
        }
    }

    public void scrollTo(int position){
        shouldScroll = true;
        scrollValue = position;
    }

    public interface FragmentPlannedRoadworksListener{
        void backButton();
    }

    @Override
    public void onItemClick(View view, int position) {
        //System.out.println("//////////Callback plannedRoadworks: "+recyclerViewAdapter.getType() + " on element: "+position);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(shouldScroll){
            recyclerView.scrollToPosition(scrollValue);
            shouldScroll = false;
        }else{
            recyclerView.scrollToPosition(0);
        }
        recyclerViewAdapter.setType("PlannedRoadworks");
    }
}