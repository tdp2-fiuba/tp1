package com.tdp2.eukanuber.manager;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.tdp2.eukanuber.model.User;


public class AppSecurityManager {
    public static final String USER_SECURITY_SETTINGS = "USER_SECURITY";
    public static final String FB_TOKEN_KEY = "fb_token";
    public static final String FB_USER_ID = "fb_user_id";
    public static final String APP_TOKEN_KEY = "app_token";
    public static final String APP_USER_LOGGED = "app_user_logged";

    public static final Boolean isUserLogged(SharedPreferences settings) {
        String fbToken = settings.getString(FB_TOKEN_KEY, null);
        String fbUserId = settings.getString(FB_USER_ID, null);
        String appToken = settings.getString(APP_TOKEN_KEY, null);
        User userLogged = getUserLogged(settings);
        return (fbToken != null && fbUserId != null && appToken != null && userLogged != null);
    }

    public static final void login(SharedPreferences settings, String fbTokenKey, String fbUserId, String appTokenKey, User user) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(AppSecurityManager.FB_TOKEN_KEY, fbTokenKey);
        editor.putString(AppSecurityManager.FB_USER_ID, fbUserId);
        editor.putString(AppSecurityManager.APP_TOKEN_KEY, appTokenKey);
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        editor.putString(AppSecurityManager.APP_USER_LOGGED, userJson);
        editor.commit();
    }

    public static final void logout(SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(AppSecurityManager.FB_TOKEN_KEY);
        editor.remove(AppSecurityManager.FB_USER_ID);
        editor.remove(AppSecurityManager.APP_TOKEN_KEY);
        editor.remove(AppSecurityManager.APP_USER_LOGGED);
        editor.commit();
    }

    public static final User getUserLogged(SharedPreferences settings){
        User userLogged = null;
        String userJson = settings.getString(APP_USER_LOGGED, null);
        if(userJson != null){
            Gson gson = new Gson();
            userLogged = gson.fromJson(userJson, User.class);
        }
        return userLogged;
    }
}
