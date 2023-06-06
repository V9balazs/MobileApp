package com.example.waterconsumption.start;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.waterconsumption.MainActivity;
import com.example.waterconsumption.R;
import com.example.waterconsumption.login.Login;

import java.util.Timer;
import java.util.TimerTask;

public class Start extends AppCompatActivity {

    ProgressBar progressBar;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getSupportActionBar().hide();

        TextView textView = findViewById(R.id.TextW);
        ImageView imageView = findViewById(R.id.ImgV);
        prog();

        Thread thread = new Thread(){
            public void run(){
                try{
                    Thread.sleep(5500);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                finally{
                    Intent intent = new Intent(Start.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        thread.start();
    }

    public void prog(){
        progressBar = (ProgressBar) findViewById(R.id.ProgressB);

        Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                counter = counter + 2;
                progressBar.setProgress(counter);

                if(counter == 100) t.cancel();
            }
        };

        t.schedule(tt, 0, 100);
    }
}