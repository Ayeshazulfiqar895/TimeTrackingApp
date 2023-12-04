package com.example.timetrackingapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Html;
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
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class chartFragment extends Fragment {
    PieChart pieChart;
    Button selectDateButton,overAllButton;
    private FirebaseAuth auth;
    private TextView category_Name;
    TextView message,TotalTime;


    private String categoryId;
    private ArrayList<String> activityNames;
    long totalConsumingTime = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        category_Name = rootView.findViewById(R.id.CatgeoryName);

        pieChart = rootView.findViewById(R.id.pieChart_view);
        auth = FirebaseAuth.getInstance();
        message=rootView.findViewById(R.id.noDataText);
        selectDateButton = rootView.findViewById(R.id.selectDateButton);
        overAllButton=rootView.findViewById(R.id.overAll);
        TotalTime=rootView.findViewById(R.id.TotalTime);

        pieChart.setNoDataText("");
        Bundle bundle = getArguments();
        if (bundle != null) {
            String categoryName = bundle.getString("categoryName");
            category_Name.setText(categoryName);
            categoryId = bundle.getString("categoryId");

            fetchActivityData();
        }

        selectDateButton.setOnClickListener(v -> showDatePickerDialog());
        overAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchActivityData();
                message.setVisibility(View.GONE);

            }
        });
        return rootView;
    }

    private void fetchActivityData() {
        String userId = auth.getCurrentUser().getUid();
        activityNames = new ArrayList<>();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("categories")
                .document(categoryId)
                .collection("activities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String activityName = document.getString("name");
                            activityNames.add(activityName);
                        }

                        fetchConsumingTimes(userId);
                    } else {
                        Toast.makeText(requireContext(), "Failed to fetch activity names", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchConsumingTimes(String userId) {
        totalConsumingTime=0;
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
                                    consumingTime += time;
                                    totalConsumingTime+=consumingTime;

                                } else {
                                    Toast.makeText(requireContext(), "Not fetched", Toast.LENGTH_SHORT).show();
                                }
                            }

                            entries.add(new PieEntry(consumingTime, activityName));

                            if (activitiesProcessed.incrementAndGet() == activityNames.size()) {
                                updatePieChart(entries);
                                showTotalTime(totalConsumingTime);
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to fetch consuming times", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updatePieChart(ArrayList<PieEntry> entries) {
        PieDataSet pieDataSet = new PieDataSet(entries, "Activities");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieDataSet.setValueTextSize(8f);
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
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + yearSelected;
                    Toast.makeText(requireContext(), "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();

                    fetchSelectiveConsumingTimes(auth.getCurrentUser().getUid(), selectedDate);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }
    private void fetchSelectiveConsumingTimes(String userId, String selectedDate) {
        totalConsumingTime=0;

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
                    .whereEqualTo("dateAndMonth", selectedDate)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            long consumingTime = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Long time = document.getLong("consumingTime");

                                if (time != null) {
                                    consumingTime += time;
                                    totalConsumingTime+=consumingTime;
                                } else {
                                    Toast.makeText(requireContext(), "No chart data available for " + activityName, Toast.LENGTH_SHORT).show();
                                }
                            }

                            if (consumingTime > 0) {
                                entries.add(new PieEntry(consumingTime, activityName));
                            }

                            if (activitiesProcessed.incrementAndGet() == activityNames.size()) {
                                if (entries.isEmpty()) {
                                    showDefaultChart();
                                } else {
                                    message.setVisibility(View.GONE);
                                    showTotalTime(totalConsumingTime);
                                    updatePieChart(entries);
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to fetch consuming times", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showDefaultChart() {

        ArrayList<PieEntry> entries = null;
        PieDataSet pieDataSet = new PieDataSet(null, "Activity Data Not Found");

        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);

        message.setVisibility(View.VISIBLE);
        pieChart.invalidate();
    }




    private void showTotalTime(long totalConsumingTime) {
        String formattedTime = formatTime(totalConsumingTime);
        TotalTime.setText("Total Consumed:"+formattedTime);
        String boldText = "<b>"+formattedTime+"</b>";
        TotalTime.setText(Html.fromHtml("Total Consumed: "+ boldText));
    }
    private String formatTime(long totalConsumingTime) {
        long hours = totalConsumingTime / 3600;
        long minutes = (totalConsumingTime % 3600) / 60;
        long seconds = totalConsumingTime % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


}
