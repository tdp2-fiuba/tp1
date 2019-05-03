package com.tdp2.eukanuber.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.manager.AppSecurityManager;

abstract class SecureActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences userSecuritySettings = getSharedPreferences(AppSecurityManager.USER_SECURITY_SETTINGS, 0);
        if (!AppSecurityManager.isUserLogged(userSecuritySettings)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

    }


}
