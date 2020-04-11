package com.e.mpd_assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;

/*
    Mark Cottrell - S1627662
 */
//used by recycler view which works with incidents, roadworks, and plannedRoadworks.
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private DataRepository dataRepository;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private boolean typeIncident = false;
    private boolean typeRoadworks = false;
    private boolean typePlannedRoadworks = false;

    private View view;

    //not exact, can update later.
    private LatLngBounds Scotland = new LatLngBounds(new LatLng(54.39, -7.83), new LatLng(58.66, -0.67));

    //pretty the double to 2 decimal places
    private static DecimalFormat df = new DecimalFormat("0.00");

    RecyclerViewAdapter(Context context, DataRepository dataRepository) {
        this.mInflater = LayoutInflater.from(context);
        this.dataRepository = dataRepository;
    }

    //only 1 recyclerviewadapter exists, so change object state.
    void setType(String input){
        switch (input) {
            case "Incident":
                typeIncident = true;
                typeRoadworks = false;
                typePlannedRoadworks = false;
                break;
            case "Roadworks":
                typeIncident = false;
                typeRoadworks = true;
                typePlannedRoadworks = false;
                break;
            case "PlannedRoadworks":
                typeIncident = false;
                typeRoadworks = false;
                typePlannedRoadworks = true;
                break;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.recycler_row, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(typeIncident){
            //if no incidents let the user know
            if(dataRepository.getRecyclerIncident().size() == 0){
                holder.title.setText(R.string.n_incidents);
                holder.description.setVisibility(View.INVISIBLE);
                holder.startDate.setVisibility(View.INVISIBLE);
                holder.endDate.setVisibility(View.INVISIBLE);
                holder.latLng.setVisibility(View.INVISIBLE);
                holder.delay.setVisibility(View.INVISIBLE);
                holder.distance.setVisibility(View.INVISIBLE);
            }else{
                //change the constraints - incident, roadwork, and plannedRoadwork have different visibility.
                ConstraintSet constraintset = new ConstraintSet();
                ConstraintLayout parentGroup = view.findViewById(R.id.recyclerConstraint);
                constraintset.clone(parentGroup);
                constraintset.connect(R.id.rowStartDate, ConstraintSet.TOP, R.id.searchGroup, ConstraintSet.TOP);
                constraintset.connect(R.id.rowEndDate, ConstraintSet.TOP, R.id.searchGroup, ConstraintSet.TOP);
                constraintset.connect(R.id.rowLatLng, ConstraintSet.TOP, R.id.searchGroup, ConstraintSet.TOP);
                constraintset.connect(R.id.rowDelay, ConstraintSet.TOP, R.id.searchGroup, ConstraintSet.TOP);
                constraintset.connect(R.id.rowDistance, ConstraintSet.TOP, R.id.rowDescription, ConstraintSet.BOTTOM);

                //populate the item with data from the respository based on position in arraylist
                Incident incident = dataRepository.getRecyclerIncident().get(position);
                holder.title.setText(incident.getTitle());
                holder.description.setText(incident.getDescription());
                holder.startDate.setVisibility(View.INVISIBLE);
                holder.endDate.setVisibility(View.INVISIBLE);
                holder.latLng.setVisibility(View.INVISIBLE);
                holder.delay.setVisibility(View.INVISIBLE);
                if(incident.getDistance() != 0){
                    String temp = GlobalContext.getContext().getString(R.string.dist)+df.format(incident.getDistance())+" Miles";
                    holder.distance.setText(temp);
                    constraintset.connect(R.id.rowDistance, ConstraintSet.TOP, R.id.rowDescription, ConstraintSet.BOTTOM, 8);
                }else{
                    constraintset.connect(R.id.rowDistance, ConstraintSet.TOP, R.id.searchGroup, ConstraintSet.TOP);
                }

                constraintset.applyTo(parentGroup);
            }
        }else if(typeRoadworks){
            if(dataRepository.getRecyclerRoadwork().size() == 0){
                holder.title.setText(R.string.no_roadworks);
                holder.description.setVisibility(View.INVISIBLE);
                holder.startDate.setVisibility(View.INVISIBLE);
                holder.endDate.setVisibility(View.INVISIBLE);
                holder.latLng.setVisibility(View.INVISIBLE);
                holder.delay.setVisibility(View.INVISIBLE);
                holder.distance.setVisibility(View.INVISIBLE);
            }else {
                //change the constraints - incident, roadwork, and plannedRoadwork have different visibility.
                ConstraintSet constraintset = new ConstraintSet();
                ConstraintLayout parentGroup = view.findViewById(R.id.recyclerConstraint);
                constraintset.clone(parentGroup);
                constraintset.connect(R.id.rowDescription, ConstraintSet.TOP, R.id.searchGroup, ConstraintSet.TOP);
                constraintset.connect(R.id.rowStartDate, ConstraintSet.TOP, R.id.rowTitle, ConstraintSet.BOTTOM);
                constraintset.connect(R.id.rowLatLng, ConstraintSet.TOP, R.id.searchGroup, ConstraintSet.TOP);
                constraintset.connect(R.id.rowDelay, ConstraintSet.TOP, R.id.rowEndDate, ConstraintSet.BOTTOM);

                Roadwork roadwork = dataRepository.getRecyclerRoadwork().get(position);
                holder.title.setText(roadwork.getTitle());
                holder.description.setVisibility(View.INVISIBLE);
                holder.startDate.setText(roadwork.getStartDatePretty());
                holder.endDate.setText(roadwork.getEndDatePretty());
                holder.latLng.setVisibility(View.INVISIBLE);
                holder.delay.setText(roadwork.getDelay());
                if(roadwork.getDistance() != 0){
                    String temp = GlobalContext.getContext().getString(R.string.dist)+df.format(roadwork.getDistance())+" Miles";
                    holder.distance.setText(temp);
                    constraintset.connect(R.id.rowDistance, ConstraintSet.TOP, R.id.rowDelay, ConstraintSet.BOTTOM, 8);
                }else{
                    constraintset.connect(R.id.rowDistance, ConstraintSet.TOP, R.id.searchGroup, ConstraintSet.TOP);
                }

                constraintset.applyTo(parentGroup);
            }
        }else if(typePlannedRoadworks){
            if(dataRepository.getRecyclerPlanned().size() == 0){
                holder.title.setText(R.string.no_planned_roadworks);
                holder.description.setVisibility(View.INVISIBLE);
                holder.startDate.setVisibility(View.INVISIBLE);
                holder.endDate.setVisibility(View.INVISIBLE);
                holder.latLng.setVisibility(View.INVISIBLE);
                holder.delay.setVisibility(View.INVISIBLE);
                holder.distance.setVisibility(View.INVISIBLE);
            }else{
                //change the constraints - incident, roadwork, and plannedRoadwork have different visibility.
                ConstraintSet constraintset = new ConstraintSet();
                ConstraintLayout parentGroup = view.findViewById(R.id.recyclerConstraint);
                constraintset.clone(parentGroup);
                constraintset.connect(R.id.rowDescription, ConstraintSet.TOP, R.id.searchGroup, ConstraintSet.TOP);
                constraintset.connect(R.id.rowStartDate, ConstraintSet.TOP, R.id.rowTitle, ConstraintSet.BOTTOM);
                constraintset.connect(R.id.rowDelay, ConstraintSet.TOP, R.id.searchGroup, ConstraintSet.TOP);
                constraintset.connect(R.id.rowLatLng, ConstraintSet.TOP, R.id.searchGroup, ConstraintSet.TOP);
                constraintset.connect(R.id.rowDistance, ConstraintSet.TOP, R.id.rowEndDate, ConstraintSet.BOTTOM);

                PlannedRoadwork plannedRoadwork = dataRepository.getRecyclerPlanned().get(position);
                holder.title.setText(plannedRoadwork.getTitle());
                holder.description.setVisibility(View.INVISIBLE);
                holder.startDate.setText(plannedRoadwork.getStartDatePretty());
                holder.endDate.setText(plannedRoadwork.getEndDatePretty());
                holder.latLng.setVisibility(View.INVISIBLE);
                holder.delay.setVisibility(View.INVISIBLE);
                if(plannedRoadwork.getDistance() != 0){
                    String temp = GlobalContext.getContext().getString(R.string.dist)+df.format(plannedRoadwork.getDistance())+" Miles";
                    holder.distance.setText(temp);
                    constraintset.connect(R.id.rowDistance, ConstraintSet.TOP, R.id.rowEndDate, ConstraintSet.BOTTOM, 8);
                }else{
                    constraintset.connect(R.id.rowDistance, ConstraintSet.TOP, R.id.searchGroup, ConstraintSet.TOP);
                }

                constraintset.applyTo(parentGroup);
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnMapReadyCallback {

        MapView map;
        TextView title;
        TextView description;
        TextView startDate;
        TextView endDate;
        TextView latLng;
        TextView delay;
        TextView distance;

        ViewHolder(View itemView) {
            super(itemView);
            //uses a google map lite version to display the incident etc on map.
            map = itemView.findViewById(R.id.mapLite);
            if(map != null){
                map.onCreate(null);
                map.onResume();
                map.getMapAsync(this);
            }
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

        @Override
        public void onMapReady(GoogleMap googleMap){
            MapsInitializer.initialize(GlobalContext.getContext());

            int position = getAdapterPosition();
            if(typeIncident){
                //position can be -1 sometimes.
                if(dataRepository.getRecyclerIncident().size() == 0){
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(Scotland, 0));
                }else if(position >= 0 && position < dataRepository.getRecyclerIncident().size()) {
                    Incident temp = dataRepository.getRecyclerIncident().get(position);
                    LatLng latLng = new LatLng(temp.getLat(), temp.getLon());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                    googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.incidenticon)));
                }
            }else if(typeRoadworks){
                if(dataRepository.getRecyclerRoadwork().size() == 0){
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(Scotland, 0));
                }else if(position >= 0 && position < dataRepository.getRecyclerRoadwork().size()){
                    Roadwork temp = dataRepository.getRecyclerRoadwork().get(position);
                    LatLng latLng = new LatLng(temp.getLat(), temp.getLon());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                    googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.roadworksicon)));
                }
            }else if(typePlannedRoadworks){
                if(dataRepository.getRecyclerPlanned().size() == 0){
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(Scotland, 0));
                }else if(position >= 0 && position < dataRepository.getRecyclerPlanned().size()){
                    PlannedRoadwork temp = dataRepository.getRecyclerPlanned().get(position);
                    LatLng latLng = new LatLng(temp.getLat(), temp.getLon());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                    googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.plannedroadworksicon)));
                }
            }
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
