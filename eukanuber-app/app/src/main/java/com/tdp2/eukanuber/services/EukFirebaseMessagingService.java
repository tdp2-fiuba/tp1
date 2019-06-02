package com.tdp2.eukanuber.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.HomeClientActivity;
import com.tdp2.eukanuber.activity.HomeDriverActivity;
import com.tdp2.eukanuber.manager.AppSecurityManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class EukFirebaseMessagingService extends FirebaseMessagingService {
    private LocalBroadcastManager broadcaster;

    private static final String NOTIFICATION_ID_EXTRA = "notificationId";
    private static final String ADMIN_CHANNEL_ID = "admin_channel";
    private NotificationManager notificationManager;


    @Override
    public void onCreate() {
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent notificationIntent = new Intent(this, HomeDriverActivity.class);
        int notificationId = new Random().nextInt(60000);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        notificationIntent.putExtra("notificationTripId", remoteMessage.getData().get("tripId"));
        notificationIntent.putExtra("currentDateTime", dateFormat.format(date));

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0 /* Request code */, notificationIntent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, remoteMessage.getData().get("title"));
        contentView.setTextViewText(R.id.description, remoteMessage.getData().get("driverName"));
        contentView.setTextViewText(R.id.score, remoteMessage.getData().get("driverScore"));
        contentView.setTextViewText(R.id.quantPets, remoteMessage.getData().get("pets"));
        contentView.setTextViewText(R.id.textInfo, remoteMessage.getData().get("distance") + " - " + remoteMessage.getData().get("duration") );
        contentView.setTextViewText(R.id.price, remoteMessage.getData().get("price"));

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_pets_black)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setContent(contentView)
                        .setTimeoutAfter(20000);

        notificationManager.notify(notificationId, notificationBuilder.build());
        Intent intent = new Intent("NewTrip");
        intent.putExtra("tripId", remoteMessage.getData().get("tripId"));
        broadcaster.sendBroadcast(intent);


    }

      @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        CharSequence adminChannelName = getString(R.string.popup_waiting);
        String adminChannelDescription = getString(R.string.facebook_app_id);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
}