package com.tdp2.eukanuber.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

public class EukFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        Log.d("FIREBASE NEW TOKEN", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        System.out.print(token);
        Log.d("TOKEN FIREBASE NEW TOKEN", token);
    }
}
