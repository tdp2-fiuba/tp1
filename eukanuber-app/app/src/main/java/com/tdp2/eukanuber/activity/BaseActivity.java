package com.tdp2.eukanuber.activity;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.manager.AppSecurityManager;
import com.tdp2.eukanuber.model.LoginResponse;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.services.UserService;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String PREFS_NAME = "Eukanuber";
    Activity baseActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = this;
    }

    protected void createMenu(User userLogged) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(userLogged != null && navigationView.getHeaderCount() > 0){
            View headerView = navigationView.getHeaderView(0);

            ImageView imageView = headerView.findViewById(R.id.imageUserView);
            TextView nameUserView  = headerView.findViewById(R.id.nameUserView);
            if(imageView != null){
                String imageB64 = userLogged.getImageByType(User.PROFILE_IMAGE_NAME);
                if(imageB64 != null){
                    byte[] decodedString = Base64.decode(imageB64, Base64.DEFAULT);
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(imageBitmap);
                }

            }
            if(nameUserView != null){
                nameUserView.setText(userLogged.getFullName());
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            SharedPreferences settings = getSharedPreferences(AppSecurityManager.USER_SECURITY_SETTINGS, 0);
            User user = AppSecurityManager.getUserLogged(settings);
            if (user.getUserType().equals(User.USER_TYPE_DRIVER)) {
                Intent intent = new Intent(baseActivity, HomeDriverActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(baseActivity, HomeClientActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_trips) {
            Intent intent = new Intent(baseActivity, TripHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.logout){
            SharedPreferences settings = getSharedPreferences(AppSecurityManager.USER_SECURITY_SETTINGS, 0);
            UserService userService = new UserService(this);
            Call<Void> call = userService.logout();
            ProgressDialog dialog = new ProgressDialog(BaseActivity.this);
            dialog.setMessage("Espere un momento por favor");
            dialog.show();
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    dialog.dismiss();
                    AppSecurityManager.logout(settings);
                    LoginManager.getInstance().logOut();
                    Intent intent = new Intent(baseActivity, LoginActivity.class);

                    startActivity(intent);
                    return;
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    dialog.dismiss();
                    Log.v("Logout Error", t.getMessage());
                    showMessage("Ha ocurrido un error. Intente luego.");
                }
            });

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}
