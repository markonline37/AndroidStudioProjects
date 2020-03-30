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

public class FragmentRoadworks extends Fragment implements  RecyclerViewAdapter.ItemClickListener, View.OnClickListener {

    private View view;
    private FragmentRoadworksListener callback;
    private DataRepository dataRepository;
    private ArrayList<Roadwork> roadworkArrayList;
    private Button backButton;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private boolean shouldScroll = false;
    private int scrollValue = 0;

    public FragmentRoadworks(DataRepository dataRepository, RecyclerViewAdapter recyclerViewAdapter){
        this.recyclerViewAdapter = recyclerViewAdapter;
        this.dataRepository = dataRepository;
    }

    public void setFragmentRoadworksListener(FragmentRoadworksListener callback){
        this.callback = callback;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        int orientation = this.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            //portrait
            view = inflater.inflate(R.layout.fragment_roadworks, container, false);
        }else{
            //landscape
            view = inflater.inflate(R.layout.fragment_roadworks_landscape, container, false);
        }

        recyclerView = view.findViewById(R.id.recyclerRoadworks);
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

    public interface FragmentRoadworksListener{
        void backButton();
    }

    @Override
    public void onItemClick(View view, int position) {
        //System.out.println("//////////Callback roadworks: "+recyclerViewAdapter.getType() + " on element: "+position);
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
        recyclerViewAdapter.setType("Roadworks");
    }
}