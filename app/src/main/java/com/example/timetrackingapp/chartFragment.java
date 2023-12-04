package com.example.timetrackingapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class chartFragment extends Fragment {
    PieChart pieChart;
    Button selectDateButton;
    private FirebaseAuth auth;
    private TextView category_Name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        category_Name = rootView.findViewById(R.id.CatgeoryName);
        pieChart = rootView.findViewById(R.id.pieChart_view);
        auth = FirebaseAuth.getInstance();
        selectDateButton = rootView.findViewById(R.id.selectDateButton);
        selectDateButton.setOnClickListener(v -> showDatePickerDialog());
        String categoryId = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            String categoryName = bundle.getString("categoryName");
            category_Name.setText(categoryName);
            categoryId = bundle.getString("categoryId");

            fetchActivityData(categoryId);
        }

        return rootView;
    }

    private void fetchActivityData(String categoryId) {
        String userId = auth.getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("categories")
                .document(categoryId)
                .collection("activities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> activityNames = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String activityName = document.getString("name");
                            activityNames.add(activityName);
                        }

                        fetchConsumingTimes(userId, categoryId, activityNames);
                    } else {
                        Toast.makeText(requireContext(), "Failed to fetch activity names", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchConsumingTimes(String userId, String categoryId, ArrayList<String> activityNames) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        AtomicInteger activitiesProcessed = new AtomicInteger(0);

        for (String activityName : activityNames) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("categories")
                    .document(categoryId)
                    .collection("activities")
                    .document(activityName)
                    .collection("newDataCollection")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            long consumingTime = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Long time = document.getLong("consumingTime");

                                if (time != null) {
                                    consumingTime += time.longValue();
                                }else {
                                    Toast.makeText(requireContext(), "Not fetched", Toast.LENGTH_SHORT).show();
                                }
                            }

                            entries.add(new PieEntry(consumingTime, activityName));

                            if (activitiesProcessed.incrementAndGet() == activityNames.size()) {
                                updatePieChart(entries);
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to fetch consuming times", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updatePieChart(ArrayList<PieEntry> entries) {
        PieDataSet pieDataSet = new PieDataSet(entries, "Activity Data");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);

        pieChart.getDescription().setEnabled(false);

        pieChart.animateY(1000);

        pieChart.invalidate();
    }

    public void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    // Handle the selected date
                    String selectedDate = yearSelected + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    Toast.makeText(requireContext(), "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();

//                    // Fetch consuming times for the selected date
//                    fetchConsumingTimes(auth.getCurrentUser().getUid(), categoryId, activityNames, selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

}
