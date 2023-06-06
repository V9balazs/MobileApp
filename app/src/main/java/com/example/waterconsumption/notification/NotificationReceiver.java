package com.example.waterconsumption.notification;

import static android.content.ContentValues.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.waterconsumption.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotificationReceiver extends BroadcastReceiver{

    private static final String CHANNEL_ID = "your_channel_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        showNotification(context, uid);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Your channel name";
            String description = "Your channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(final Context context, String uid) {
        fetchWaterAmount(uid, new OnDataFetchedListener() {
            @Override
            public void onDataFetched(String waterAmount) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.water)
                        .setContentTitle("Reminder!")
                        .setContentText("Stay hydrated! Drink " + waterAmount + " l of water a day!")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.notify(1, builder.build());
                }
            }
        });
    }


    private void fetchWaterAmount(final String uid, final OnDataFetchedListener listener) {
        DatabaseReference waterAmountRef = FirebaseDatabase.getInstance().getReference("Plan").child(uid).child("WaterAmount");
        waterAmountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String waterAmount = dataSnapshot.getValue(String.class);
                    listener.onDataFetched(waterAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read WaterAmount.", databaseError.toException());
            }
        });
    }

    public interface OnDataFetchedListener {
        void onDataFetched(String waterAmount);
    }
}
