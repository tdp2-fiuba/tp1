package com.tdp2.eukanuber.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.interfaces.ShowMessageInterface;
import com.tdp2.eukanuber.manager.MapManager;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.TripStatus;
import com.tdp2.eukanuber.model.UpdateUserPositionRequest;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.services.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeClientActivity extends SecureActivity implements OnMapReadyCallback, ShowMessageInterface {
    private GoogleMap mMap;
    private MapManager mapManager;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_client);
        this.createMenu(userLogged);
        mActivity = this;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initClientHome();
        getLastTrip();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String showMessageIntent = getIntent().getStringExtra("showMessage");
        if(showMessageIntent != null){
            showMessage(showMessageIntent);
        }
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

    private void getLastTrip() {
        UserService userService = new UserService(this);
        Call<Trip> call = userService.getLastTrip();
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                Trip trip = response.body();
                if (trip != null && trip.getId() != null &&
                    (trip.getStatus() == TripStatus.DRIVER_GOING_ORIGIN.ordinal() ||
                    trip.getStatus() == TripStatus.IN_TRAVEL.ordinal())) {

                    Intent trackingTripActivity = new Intent(mActivity, TrackingTripActivity.class);
                    trackingTripActivity.putExtra("currentTrip", trip);
                    trackingTripActivity.putExtra("fromHome", true);
                    trackingTripActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(trackingTripActivity);
                }

            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                System.out.print("Ha ocurrido un error al recuperar el viaje.");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MapManager.PERMISSION_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mapManager.setCurrentLocation(location);
                LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                if (mMap != null) {
                    UpdateUserPositionRequest updateUserPositionRequest = new UpdateUserPositionRequest(String.valueOf(currentPosition.latitude), String.valueOf(currentPosition.longitude));
                    UserService userService = new UserService(mActivity);
                    Call<User> call = userService.updatePositionUser(updateUserPositionRequest);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            User userUpdated = response.body();
                            if (userUpdated != null) {
                                Log.d("USER UPDATED", userUpdated.getId());
                            }


                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.d("UPDATE USER POSITION", t.getMessage());
                        }
                    });

                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        mapManager.setListener(locationListener);
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
