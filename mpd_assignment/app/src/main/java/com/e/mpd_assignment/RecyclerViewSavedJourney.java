package com.e.mpd_assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewSavedJourney extends RecyclerView.Adapter<RecyclerViewSavedJourney.ViewHolder>{

    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemClickListener2 mClickListener2;
    private JourneyRepository journeyRepository;

    private View view;

    RecyclerViewSavedJourney(Context context, JourneyRepository journeyRepository) {
        //cant use GlobalContext.getContext() here.
        this.mInflater = LayoutInflater.from(context);
        this.journeyRepository = journeyRepository;
    }

    public void updateRepo(JourneyRepository jr){
        this.journeyRepository = jr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.recycler_row_saved_journey, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(this.journeyRepository.getJourneyArrayList().size() == 0){
            holder.name.setText("No saved journeys to display");
            holder.icon.setVisibility(View.INVISIBLE);
        }else{
            holder.name.setText(journeyRepository.getJourneyArrayList().get(position).getName());
            holder.icon.setVisibility(View.VISIBLE);
        }
    }

    // total number of rows
    //return 1 if empty to display the "No Incident (etc) to display"
    @Override
    public int getItemCount() {
        int size = journeyRepository.getJourneyArrayList().size();
        if(size == 0){
            return 1;
        }else{
            return size;
        }
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.savedJourneyName);
            name.setOnClickListener(this);
            icon = itemView.findViewById(R.id.savedJourneyIcon);
            icon.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                if(view == icon){
                    //add yes/no confirmation
                    mClickListener.onItemClick(getAdapterPosition());
                }else if(view == name){
                    mClickListener2.onItemClick2(getAdapterPosition());
                }
            }
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    void setClickListener2(ItemClickListener2 itemClickListener){
        this.mClickListener2 = itemClickListener;
    }

    public interface ItemClickListener2 {
        void onItemClick2(int position);
    }
}
