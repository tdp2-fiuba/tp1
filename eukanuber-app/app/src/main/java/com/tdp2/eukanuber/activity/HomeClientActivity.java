package com.tdp2.eukanuber.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.interfaces.ShowMessageInterface;
import com.tdp2.eukanuber.manager.AppSecurityManager;
import com.tdp2.eukanuber.manager.MapManager;
import com.tdp2.eukanuber.model.User;

public class HomeClientActivity extends SecureActivity implements OnMapReadyCallback, ShowMessageInterface {
    private GoogleMap mMap;
    private MapManager mapManager;
    private User userLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_client);
        SharedPreferences settings = getSharedPreferences(AppSecurityManager.USER_SECURITY_SETTINGS, 0);
        userLogged = AppSecurityManager.getUserLogged(settings);
        if(userLogged == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }
        this.createMenu();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initClientHome();


    }


    private void initClientHome() {
        LinearLayout layoutMap = findViewById(R.id.layoutMap);
        layoutMap.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 10f));
        FloatingActionButton newTripButton = findViewById(R.id.newTripButton);
        FloatingActionsMenu fam = findViewById(R.id.menu_fab);
        newTripButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeClientActivity.this, NewTripActivity.class);
            fam.collapse();
            startActivity(intent);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MapManager.PERMISSION_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mapManager.setCurrentLocation();

                } else {
                    Toast.makeText(getApplicationContext(), "Sin permisos necesarios para utilizar la aplicacion",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapManager = new MapManager(mMap, this);
        mapManager.setCurrentLocation();
      /*  LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                mapManager.moveCamera(currentPosition);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        mapManager.setListener(locationListener);*/
    }

    public void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}
