package com.mattkula.guesswhom.networking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.mattkula.guesswhom.R;
import com.mattkula.guesswhom.ui.MainActivity;

/**
 * Created by matt on 2/28/14.
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {

    public static final String KEY_ASKER = "asker";
    public static final String KEY_QUESTION = "question";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("GCM", intent.getExtras().getString("message"));
        String asker = intent.getExtras().getString(KEY_ASKER);
        String question = intent.getExtras().getString(KEY_QUESTION);

        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(asker + " asked")
                        .setContentText(question)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        manager.notify(0, mBuilder.build());

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(1000);
    }
}
