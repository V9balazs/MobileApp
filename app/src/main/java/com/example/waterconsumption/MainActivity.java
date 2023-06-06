package com.example.waterconsumption;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.waterconsumption.login.SignIn;
import com.example.waterconsumption.login.SignUp;
import com.example.waterconsumption.notification.NotificationReceiver;
import com.example.waterconsumption.pages.Plans;
import com.example.waterconsumption.pages.Profile;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    EditText editTextMainMorning, editTextMainAfternoon;
    Button bttnMainSave;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMainMorning = (EditText) findViewById(R.id.editTextMainMorning);
        editTextMainAfternoon = (EditText) findViewById(R.id.editTextMainAfternoon);

        bttnMainSave = (Button) findViewById(R.id.bttnMainSave);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //Notification
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("Plan").child(userId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean notificationBoolean = dataSnapshot.child("NotificationBoolean").getValue(Boolean.class);
                    if (notificationBoolean != null && notificationBoolean) {
                        scheduleNotification();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("MainActivity", "Error fetching NotificationBoolean", databaseError.toException());
                }
            });
        }

        bttnMainSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = getCurrentDate();
                double morningIntake = Double.parseDouble(editTextMainMorning.getText().toString());
                double afternoonIntake = Double.parseDouble(editTextMainAfternoon.getText().toString());
                saveWaterIntakeData(date, "morning", morningIntake);
                saveWaterIntakeData(date, "afternoon", afternoonIntake);
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_drawer_open, R.string.menu_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_statistics:
                        break;
                    case R.id.nav_plans:
                        Log.i("MENU_DRAWER_TAG", "Plans item is clicked");
                        Intent intentplan = new Intent(MainActivity.this, Plans.class);
                        startActivity(intentplan);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_profile:
                        Log.i("MENU_DRAWER_TAG", "Profile item is clicked");
                        Intent intentprofile = new Intent(MainActivity.this, Profile.class);
                        startActivity(intentprofile);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });
    }

    private void scheduleNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the alarm to trigger at 12 PM and 6 PM
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 18);
        calendar2.set(Calendar.MINUTE, 0);
        calendar2.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void saveWaterIntakeData(String date, String timeOfDay, double amount) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference.child("waterIntake").child(date).child(timeOfDay).setValue(amount);
        }
    }

    public void onDiagramClicked(View view) {
        Intent intentdiagram = new Intent(MainActivity.this, LineChartDiagram.class);
        startActivity(intentdiagram);
    }
}