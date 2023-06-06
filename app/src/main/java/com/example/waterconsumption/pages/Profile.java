package com.example.waterconsumption.pages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.waterconsumption.MainActivity;
import com.example.waterconsumption.R;
import com.example.waterconsumption.login.SignIn;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;

    TextView textViewProfileUsername, textViewProfileEmail;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();

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
        setContentView(R.layout.activity_profile);

        textViewProfileEmail = (TextView) findViewById(R.id.textViewProfileEmail);
        textViewProfileUsername = (TextView) findViewById(R.id.textViewProfileUsername);

        String userId = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("Users").child(userId);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Itt dolgozd fel a lekért adatokat
                // Például:
                String name = dataSnapshot.child("userName").getValue(String.class);
                String email = dataSnapshot.child("userEmail").getValue(String.class);

                textViewProfileUsername.setText(name);
                textViewProfileEmail.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Ha hiba történik az adatok lekérése során, kezeld le itt
                Toast.makeText(Profile.this, "Data request ERROR", Toast.LENGTH_SHORT).show();
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
                        Log.i("MENU_DRAWER_TAG", "Statistisc item is clicked");
                        Intent intentstatistic = new Intent(Profile.this, MainActivity.class);
                        startActivity(intentstatistic);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_plans:
                        Log.i("MENU_DRAWER_TAG", "Plans item is clicked");
                        Intent intentplan = new Intent(Profile.this, Plans.class);
                        startActivity(intentplan);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.nav_profile:
                        break;
                }
                return true;
            }
        });
    }

    public void onLogoutClicked(View view) {
        mAuth.signOut();
        Toast.makeText(Profile.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
        Intent intentsignout = new Intent(Profile.this, SignIn.class);
        startActivity(intentsignout);
        finish();
    }
}