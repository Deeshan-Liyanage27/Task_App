package com.example.task_app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class Notification extends BroadcastReceiver {
    private static final String CHANNEL_ID = "channel1";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_task_alt_24)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), PendingIntent.FLAG_IMMUTABLE))
                .setAutoCancel(true)
                .setContentTitle((intent.getStringExtra("Task")))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
