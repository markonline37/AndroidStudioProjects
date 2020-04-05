package com.e.mpd_assignment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class FragmentLoadingScreen extends Fragment implements LoadResource.LoadResourceListener{

    private FragmentListener callback;
    private DataRepository dataRepository;

    //constructor, used to pass dataRepository to fragment from MainActivity.
    FragmentLoadingScreen(DataRepository dataRepository){
        this.dataRepository = dataRepository;
    }

    //MainActivity listener
    void setFragmentListener(FragmentListener callback){
        this.callback = callback;
    }

    //interface that MainActivity implements to create a listener this fragment can call
    public interface FragmentListener{
        void loadingComplete();
    }

    @Override
    public void dataFullyLoaded(){
        callback.loadingComplete();

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        int orientation = this.getResources().getConfiguration().orientation;
        View view;
        if(orientation == Configuration.ORIENTATION_PORTRAIT){
            //portrait
            view = inflater.inflate(R.layout.fragment_loading_screen, container, false);
        }else{
            //landscape
            view = inflater.inflate(R.layout.fragment_loading_screen_landscape, container, false);
        }
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TextView loadingText = view.findViewById(R.id.loadingText);
        LoadResource task = new LoadResource(this);
        task.onPreExecute(progressBar, loadingText, dataRepository);
        task.execute();
        return view;
    }
}
