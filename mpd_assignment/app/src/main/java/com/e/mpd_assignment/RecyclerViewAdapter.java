package com.e.mpd_assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private DataRepository dataRepository;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private boolean typeIncident = false;
    private boolean typeRoadworks = false;
    private boolean typePlannedRoadworks = false;

    private static DecimalFormat df = new DecimalFormat("0.00");


    RecyclerViewAdapter(Context context, DataRepository dataRepository) {
        this.mInflater = LayoutInflater.from(context);
        this.dataRepository = dataRepository;
    }

    void setType(String input){
        if(input.equals("Incident")){
            typeIncident = true;
            typeRoadworks = false;
            typePlannedRoadworks = false;
        }else if(input.equals("Roadworks")){
            typeIncident = false;
            typeRoadworks = true;
            typePlannedRoadworks = false;
        }else if(input.equals("PlannedRoadworks")){
            typeIncident = false;
            typeRoadworks = false;
            typePlannedRoadworks = true;
        }
    }

    String getType(){
        if(typeIncident){
            return "Incident";
        }else if(typeRoadworks){
            return "Roadworks";
        }else if(typePlannedRoadworks){
            return "PlannedRoadworks";
        }
        return "";
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(typeIncident){
            if(dataRepository.getRecyclerIncident().size() == 0){
                holder.title.setText("No Incidents to display");
                holder.description.setVisibility(View.INVISIBLE);
                holder.startDate.setVisibility(View.INVISIBLE);
                holder.endDate.setVisibility(View.INVISIBLE);
                holder.latLng.setVisibility(View.INVISIBLE);
                holder.delay.setVisibility(View.INVISIBLE);
                holder.distance.setVisibility(View.INVISIBLE);
            }else{
                Incident incident = dataRepository.getRecyclerIncident().get(position);
                holder.title.setText(incident.getTitle());
                holder.description.setText(incident.getDescription());
                holder.startDate.setVisibility(View.INVISIBLE);
                holder.endDate.setVisibility(View.INVISIBLE);
                holder.latLng.setText(incident.getLat()+ " "+incident.getLon());
                holder.delay.setVisibility(View.INVISIBLE);
                if(incident.getDistance() != 0){
                    holder.distance.setText("Distance: "+df.format(incident.getDistance())+" Miles");
                }
            }
        }else if(typeRoadworks){
            if(dataRepository.getRecyclerRoadwork().size() == 0){
                holder.title.setText("No Roadworks to display");
                holder.description.setVisibility(View.INVISIBLE);
                holder.startDate.setVisibility(View.INVISIBLE);
                holder.endDate.setVisibility(View.INVISIBLE);
                holder.latLng.setVisibility(View.INVISIBLE);
                holder.delay.setVisibility(View.INVISIBLE);
                holder.distance.setVisibility(View.INVISIBLE);
            }else {
                Roadwork roadwork = dataRepository.getRecyclerRoadwork().get(position);
                holder.title.setText(roadwork.getTitle());
                holder.description.setVisibility(View.INVISIBLE);
                holder.startDate.setText(roadwork.getStartDate());
                holder.endDate.setText(roadwork.getEndDate());
                holder.latLng.setText(roadwork.getLat() + " " + roadwork.getLon());
                holder.delay.setText(roadwork.getDelay());
                if(roadwork.getDistance() != 0){
                    holder.distance.setText("Distance: "+df.format(roadwork.getDistance())+" Miles");
                }
            }
        }else if(typePlannedRoadworks){
            if(dataRepository.getRecyclerPlanned().size() == 0){
                holder.title.setText("No Planned Roadworks to display");
                holder.description.setVisibility(View.INVISIBLE);
                holder.startDate.setVisibility(View.INVISIBLE);
                holder.endDate.setVisibility(View.INVISIBLE);
                holder.latLng.setVisibility(View.INVISIBLE);
                holder.delay.setVisibility(View.INVISIBLE);
                holder.distance.setVisibility(View.INVISIBLE);
            }else{
                PlannedRoadwork plannedRoadwork = dataRepository.getRecyclerPlanned().get(position);
                holder.title.setText(plannedRoadwork.getTitle());
                holder.description.setVisibility(View.INVISIBLE);
                holder.startDate.setText(plannedRoadwork.getStartDate());
                holder.endDate.setText(plannedRoadwork.getEndDate());
                holder.latLng.setText(plannedRoadwork.getLat()+ " " + plannedRoadwork.getLon());
                holder.delay.setVisibility(View.INVISIBLE);
                if(plannedRoadwork.getDistance() != 0){
                    holder.distance.setText("Distance: "+df.format(plannedRoadwork.getDistance())+" Miles");
                }
            }
        }
    }

    // total number of rows
    //return 1 if empty to display the "No Incident (etc) to display"
    @Override
    public int getItemCount() {
        int size;
        if(typeIncident){
            size = dataRepository.getRecyclerIncident().size();
            if(size == 0){
                return 1;
            }else{
                return size;
            }
        }else if(typeRoadworks){
            size = dataRepository.getRecyclerRoadwork().size();
            if(size == 0){
                return 1;
            }else{
                return size;
            }
        }else if(typePlannedRoadworks){
            size = dataRepository.getRecyclerPlanned().size();
            if(size == 0){
                return 1;
            }else{
                return size;
            }
        }
        return 0;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView description;
        TextView startDate;
        TextView endDate;
        TextView latLng;
        TextView delay;
        TextView distance;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.rowTitle);
            description = itemView.findViewById(R.id.rowDescription);
            startDate = itemView.findViewById(R.id.rowStartDate);
            endDate = itemView.findViewById(R.id.rowEndDate);
            latLng = itemView.findViewById(R.id.rowLatLng);
            delay = itemView.findViewById(R.id.rowDelay);
            distance = itemView.findViewById(R.id.rowDistance);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
