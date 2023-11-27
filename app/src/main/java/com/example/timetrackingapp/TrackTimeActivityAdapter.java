package com.example.timetrackingapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.CollectionReference;
import java.util.List;

public class TrackTimeActivityAdapter extends RecyclerView.Adapter<TrackTimeActivityAdapter.ViewHolder> {

    private CollectionReference itemsCollection;
    private List<Activity_Modal> itemList;
    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public TrackTimeActivityAdapter(List<Activity_Modal> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.clickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemText;
        public ImageView deleteButton;

        public ViewHolder(View view) {
            super(view);
            itemText = view.findViewById(R.id.timeTrack_ActivityText);
            deleteButton = view.findViewById(R.id.delete_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_track_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Activity_Modal item = itemList.get(position);

        String itemName = item.getName();

        holder.itemText.setText(itemName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemClick(position);
                    FragmentManager fragmentManager;
                    Activity_Modal clickedActivity = itemList.get(position);

                    fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
                    StopWatchFragment nextFragment = new StopWatchFragment();
                    Bundle args = new Bundle();
                    args.putString("activityName",  clickedActivity.getName());
                    nextFragment.setArguments(args);

                    fragmentManager.beginTransaction()
                            .replace(R.id.box1, nextFragment)
                            .commit();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }
}