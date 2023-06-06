package com.example.waterconsumption;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LineChartDiagram extends AppCompatActivity {

    LineChart chart;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        mAuth = FirebaseAuth.getInstance();

        chart = (LineChart) findViewById(R.id.chart);
        initChart();
        setupPeriodButtons();
    }

    private void initChart() {
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setDrawGridLines(false);

        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        chart.getLegend().setEnabled(false);
    }

    private void fetchData(String period) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String uid = currentUser.getUid();
        DatabaseReference waterIntakeRef = FirebaseDatabase.getInstance().getReference("Plan").child(uid).child("waterIntake");

        waterIntakeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Entry> morningEntries = new ArrayList<>();
                List<Entry> afternoonEntries = new ArrayList<>();

                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    if (isDateInPeriod(date, period)) {
                        DataSnapshot morningSnapshot = dateSnapshot.child("morning");
                        DataSnapshot afternoonSnapshot = dateSnapshot.child("afternoon");
                        float morningValue = morningSnapshot.getValue(Float.class);
                        float afternoonValue = afternoonSnapshot.getValue(Float.class);

                        long timeInMillis = convertDateToMillis(date);
                        morningEntries.add(new Entry(timeInMillis, morningValue));
                        afternoonEntries.add(new Entry(timeInMillis, afternoonValue));
                    }
                }

                updateChartData(morningEntries, afternoonEntries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private boolean isDateInPeriod(String date, String period) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = new Date();
        Date parsedDate;

        try {
            parsedDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        if (parsedDate == null) {
            return false;
        }

        Calendar startPeriod = Calendar.getInstance();
        Calendar endPeriod = Calendar.getInstance();

        switch (period) {
            case "daily":
                startPeriod.set(Calendar.HOUR_OF_DAY, 0);
                startPeriod.set(Calendar.MINUTE, 0);
                startPeriod.set(Calendar.SECOND, 0);
                startPeriod.set(Calendar.MILLISECOND, 0);

                endPeriod.set(Calendar.HOUR_OF_DAY, 23);
                endPeriod.set(Calendar.MINUTE, 59);
                endPeriod.set(Calendar.SECOND, 59);
                endPeriod.set(Calendar.MILLISECOND, 999);
                break;

            case "weekly":
                startPeriod.set(Calendar.DAY_OF_WEEK, startPeriod.getFirstDayOfWeek());
                startPeriod.set(Calendar.HOUR_OF_DAY, 0);
                startPeriod.set(Calendar.MINUTE, 0);
                startPeriod.set(Calendar.SECOND, 0);
                startPeriod.set(Calendar.MILLISECOND, 0);

                endPeriod.set(Calendar.DAY_OF_WEEK, endPeriod.getFirstDayOfWeek() + 6);
                endPeriod.set(Calendar.HOUR_OF_DAY, 23);
                endPeriod.set(Calendar.MINUTE, 59);
                endPeriod.set(Calendar.SECOND, 59);
                endPeriod.set(Calendar.MILLISECOND, 999);
                break;

            case "monthly":
                startPeriod.set(Calendar.DAY_OF_MONTH, 1);
                startPeriod.set(Calendar.HOUR_OF_DAY, 0);
                startPeriod.set(Calendar.MINUTE, 0);
                startPeriod.set(Calendar.SECOND, 0);
                startPeriod.set(Calendar.MILLISECOND, 0);

                endPeriod.set(Calendar.DAY_OF_MONTH, endPeriod.getActualMaximum(Calendar.DAY_OF_MONTH));
                endPeriod.set(Calendar.HOUR_OF_DAY, 23);
                endPeriod.set(Calendar.MINUTE, 59);
                endPeriod.set(Calendar.SECOND, 59);
                endPeriod.set(Calendar.MILLISECOND, 999);
                break;
            default:
                return false;
        }

        return parsedDate.compareTo(startPeriod.getTime()) >= 0 && parsedDate.compareTo(endPeriod.getTime()) <= 0;
    }

    private long convertDateToMillis(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date parsedDate = sdf.parse(date);
            if (parsedDate != null) {
                return parsedDate.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void updateChartData(List<Entry> morningEntries, List<Entry> afternoonEntries) {
        LineDataSet morningDataSet = new LineDataSet(morningEntries, "Morning");
        LineDataSet afternoonDataSet = new LineDataSet(afternoonEntries, "Afternoon");

        // Customize the appearance of the datasets
        // ...

        LineData lineData = new LineData(morningDataSet, afternoonDataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    private void setupPeriodButtons() {
        Button dailyButton = findViewById(R.id.dailyButton);
        Button weeklyButton = findViewById(R.id.weeklyButton);
        Button monthlyButton = findViewById(R.id.monthlyButton);

        dailyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData("daily");
            }
        });

        weeklyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData("weekly");
            }
        });

        monthlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData("monthly");
            }
        });
    }
}