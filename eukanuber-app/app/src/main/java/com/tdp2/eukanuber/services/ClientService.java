package com.tdp2.eukanuber.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.tdp2.eukanuber.manager.AppSecurityManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ClientService {
    Context context;
    OkHttpClient client;

    public ClientService(Context context) {
        this.context = context;
        SharedPreferences settings = context.getSharedPreferences(AppSecurityManager.USER_SECURITY_SETTINGS, 0);
        String appToken = settings.getString(AppSecurityManager.APP_TOKEN_KEY, null);
        client = null;
        if(appToken != null){
            client = new OkHttpClient.Builder().addInterceptor(chain -> {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + appToken)
                        .build();
                return chain.proceed(newRequest);
            }).build();
        }

    }
}
