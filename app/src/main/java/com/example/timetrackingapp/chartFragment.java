package com.example.timetrackingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class chartFragment extends Fragment {
private TextView category_Name;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        category_Name=rootView.findViewById(R.id.CatgeoryName);
        String xyz = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
             String categoryName = bundle.getString("categoryName");
             category_Name.setText(categoryName);
             xyz=bundle.getString("categoryId");
             

        }

        Toast.makeText(requireContext(), xyz, Toast.LENGTH_SHORT).show();
        PieChart pieChart=rootView.findViewById(R.id.pieChart_view);
        ArrayList<PieEntry>  entries=new ArrayList<>();
        entries.add(new PieEntry(80,"ninja"));
        entries.add(new PieEntry(100,"Naveed"));
        entries.add(new PieEntry(10,"Sohail"));
        entries.add(new PieEntry(5,"hammid"));
        entries.add(new PieEntry(5,"cgma"));


        PieDataSet pieDataSet =new PieDataSet(entries,"subjects");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData=new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);
        pieChart.invalidate();
        return rootView;
    }
}
