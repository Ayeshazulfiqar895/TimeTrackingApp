package com.example.timetrackingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;

public class TrackTimeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_track_time, container, false);

//        ImageView imageView4 = rootView.findViewById(R.id.imageView4);
//
//        imageView4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle the click event here, e.g., navigate to the home page
//                Intent intent = new Intent(getActivity(), Home.class);
//                startActivity(intent);
//            }
//        });

        return rootView;
    }
}
