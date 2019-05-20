package com.tdp2.eukanuber.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.manager.AppSecurityManager;
import com.tdp2.eukanuber.model.Rating;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.services.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

abstract class SecureActivity extends BaseActivity {
    protected User userLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences(AppSecurityManager.USER_SECURITY_SETTINGS, 0);
        userLogged = AppSecurityManager.getUserLogged(settings);
        if(userLogged == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshRating(userLogged);
    }
}
