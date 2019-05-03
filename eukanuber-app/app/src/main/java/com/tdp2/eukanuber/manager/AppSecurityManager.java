package com.tdp2.eukanuber.manager;

import android.content.SharedPreferences;


public class AppSecurityManager {
    public static final String USER_SECURITY_SETTINGS = "USER_SECURITY";
    public static final String FB_TOKEN_KEY = "fb_token";
    public static final String FB_USER_ID = "fb_user_id";
    public static final String APP_TOKEN_KEY = "app_token";

    public static final Boolean isUserLogged(SharedPreferences settings) {
        String fbToken = settings.getString(FB_TOKEN_KEY, null);
        String fbUserId = settings.getString(FB_USER_ID, null);
        String appToken = settings.getString(APP_TOKEN_KEY, null);
        return (fbToken != null && fbUserId != null &&appToken != null);
    }

    public static final void login(SharedPreferences settings, String fbTokenKey, String fbUserId, String appTokenKey) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(AppSecurityManager.FB_TOKEN_KEY, fbTokenKey);
        editor.putString(AppSecurityManager.FB_USER_ID, fbUserId);
        editor.putString(AppSecurityManager.APP_TOKEN_KEY, appTokenKey);
        editor.commit();
    }

    public static final void logout(SharedPreferences settings) {
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(AppSecurityManager.FB_TOKEN_KEY);
        editor.remove(AppSecurityManager.FB_USER_ID);
        editor.remove(AppSecurityManager.APP_TOKEN_KEY);
        editor.commit();
    }
}
