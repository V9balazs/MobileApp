package com.example.waterconsumption.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.example.waterconsumption.MainActivity;
import com.example.waterconsumption.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;

public class Plans extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;

    EditText editTextPlansAmount, editTextPlansWeight, editTextPlansExercise, editTextPlansNeedWater;
    String textAmount, textWeight, textExercise, textNeedWater;
    double dbAmount = 0, dbWeight = 0, dbWater = 0, dbExercise = 0;
    Switch switchPlansNotification;
    Button bttnPlansSave;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

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
        setContentView(R.layout.activity_plans);

        editTextPlansAmount = (EditText) findViewById(R.id.editTextPlansAmount);
        editTextPlansWeight = (EditText) findViewById(R.id.editTextPlansWeight);
        editTextPlansExercise = (EditText) findViewById(R.id.editTextPlansExercise);
        editTextPlansNeedWater = (EditText) findViewById(R.id.editTextPlansNeedWater);

        switchPlansNotification = (Switch) findViewById(R.id.switchPlansNotification);
        bttnPlansSave = (Button) findViewById(R.id.bttnPlansSave);

        editTextPlansWeight.addTextChangedListener(textWatcher);
        editTextPlansExercise.addTextChangedListener(textWatcher);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Plan");

        bttnPlansSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirebase();
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
                        Log.i("MENU_DRAWER_TAG", "Statistics item is clicked");
                        Intent intentstatistic = new Intent(Plans.this, MainActivity.class);
                        startActivity(intentstatistic);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_plans:
                        break;
                    case R.id.nav_profile:
                        Log.i("MENU_DRAWER_TAG", "Profile item is clicked");
                        Intent intentprofile = new Intent(Plans.this, Profile.class);
                        startActivity(intentprofile);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });
    }

    private void saveDataToFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String editTextValue = editTextPlansAmount.getText().toString();
            boolean switchValue = switchPlansNotification.isChecked();

            DatabaseReference userReference = databaseReference.child(userId);
            userReference.child("WaterAmount").setValue(editTextValue);
            userReference.child("NotificationBoolean").setValue(switchValue);
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!editTextPlansWeight.getText().toString().isEmpty()) {
                dbWeight = Double.parseDouble(editTextPlansWeight.getText().toString());
            }
            if (!editTextPlansExercise.getText().toString().isEmpty()) {
                dbExercise = Double.parseDouble(editTextPlansExercise.getText().toString());
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            dbWater = (((dbWeight * 2.20462262) * 0.5) + ((dbExercise / 30) * 12)) * 0.0295735296;
            editTextPlansNeedWater.setText(String.valueOf(decimalFormat.format(dbWater)));
        }
    };
}