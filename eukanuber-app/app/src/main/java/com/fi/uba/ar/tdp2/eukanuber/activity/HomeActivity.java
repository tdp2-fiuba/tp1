package com.fi.uba.ar.tdp2.eukanuber.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.fi.uba.ar.tdp2.eukanuber.R;
import com.fi.uba.ar.tdp2.eukanuber.model.Trip;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;

public class HomeActivity extends MenuActivity
        implements OnMapReadyCallback {
    private GoogleMap mMap;
    private final int PERMISSION_FINE_LOCATION = 1;
    private final String DRIVER_TYPE = "driver";
    private final String CLIENT_TYPE = "client";
    private LocationListener locationListener;
    private LocationManager locationManager;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.createMenu();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        userType = intent.getStringExtra("userType");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                if (mMap != null) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        this.checkPermissionsLocation();

        if (userType.equals(CLIENT_TYPE)) {
            initClientHome();
        }
        if (userType.equals(DRIVER_TYPE)) {
            initDriverHome();
        }
    }

    private void initClientHome() {
        LinearLayout layoutMap = findViewById(R.id.layoutMap);
        layoutMap.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 10f));
        FloatingActionButton newTripButton = findViewById(R.id.newTripButton);
        FloatingActionsMenu fam = findViewById(R.id.menu_fab);
        findViewById(R.id.driverStatus).setVisibility(View.GONE);
        newTripButton.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, NewTripActivity.class);
            fam.collapse();
            startActivity(intent);
        });
    }

    private void initDriverHome() {
        findViewById(R.id.menu_fab).setVisibility(View.GONE);
        TextView driverStatusView = findViewById(R.id.driverStatus);
        driverStatusView.setOnClickListener(view -> {
            openPopupNewTripDriver(view);


        });

    }
    private void openPopupNewTripDriver(View v){
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.new_trip_popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                false);
        popupWindow.setAnimationStyle(R.style.popup_window_animation);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        Trip trip = new Trip();
        trip.setDestination("Santa Fe 3329 Entre Bulnes y Vidt");
        trip.setOrigin("Paseo Colon 850 Esquina Independencia");
        trip.setDuration("1h 04m");
        trip.setEscort(true);
        trip.setPrice("456,90");
        trip.setPayment("cash");
        Collection<String> pets = new ArrayList<>();
        pets.add("S");
        pets.add("M");
        pets.add("B");
        trip.setPets(pets);
        ((TextView) popupView.findViewById(R.id.tripOriginText)).setText(trip.getOrigin());
        ((TextView) popupView.findViewById(R.id.tripDestinationText)).setText(trip.getDestination());
        ((TextView) popupView.findViewById(R.id.tripDurationText)).setText(trip.getDuration());
        ((TextView) popupView.findViewById(R.id.tripPriceText)).setText(trip.getPrice());
    }

    private void checkPermissionsLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_FINE_LOCATION);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMapLocation();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Sin permisos necesarios para utilizar la aplicacion",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                return;
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initMapLocation();
    }

    private void initMapLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setMinZoomPreference(14.0f);
        mMap.setMaxZoomPreference(20.0f);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null)
        {
            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

            //mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Centra el mapa en locacion del usuario
                    .zoom(18.0f)                   // seteo el zoom
                    //.bearing(90)                // Orientacion de la camara al este - no queda bien
                    .tilt(40)                   // tiltea la camara 40 grados
                    .build();                   // builder

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
        }
    }
}
