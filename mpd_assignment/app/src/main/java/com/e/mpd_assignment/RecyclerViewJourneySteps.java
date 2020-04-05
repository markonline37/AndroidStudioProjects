package com.e.mpd_assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//Recycler view which holds the current journey steps(from 'plan a journey' or 'load a saved journey'), i.e. #1 roadwork, #2 incident (along route)
public class RecyclerViewJourneySteps extends RecyclerView.Adapter<RecyclerViewJourneySteps.ViewHolder>{

    private DataRepository dataRepository;
    private LayoutInflater mInflater;

    RecyclerViewJourneySteps(Context context, DataRepository dataRepository){
        this.mInflater = LayoutInflater.from(context);
        this.dataRepository = dataRepository;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_row_journey, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(dataRepository.getRecyclerJourney().size() == 0){
            holder.increment.setVisibility(View.INVISIBLE);
            holder.title.setText("Lucky you!\nNo Roadworks or Incidents to display");
        }else{
            holder.increment.setVisibility(View.VISIBLE);
            String temp = position+1+": ";
            holder.increment.setText(temp);
            holder.title.setText(dataRepository.getRecyclerJourney().get(position));
        }
    }

    @Override
    public int getItemCount(){
        int size = dataRepository.getRecyclerJourney().size();
        if(size == 0){
            return 1;
        }else{
            return size;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView increment;
        TextView title;

        ViewHolder(View itemView){
            super(itemView);
            increment = itemView.findViewById(R.id.journeyPosition);
            title = itemView.findViewById(R.id.journeyTitle);
        }
    }
}
