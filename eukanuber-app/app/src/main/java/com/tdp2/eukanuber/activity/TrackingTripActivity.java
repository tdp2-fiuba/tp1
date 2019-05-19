package com.tdp2.eukanuber.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.interfaces.ShowMessageInterface;
import com.tdp2.eukanuber.manager.MapManager;
import com.tdp2.eukanuber.model.MapRoute;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.TripStatus;
import com.tdp2.eukanuber.model.UserPositionResponse;
import com.tdp2.eukanuber.services.TripService;
import com.tdp2.eukanuber.services.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingTripActivity extends SecureActivity implements OnMapReadyCallback, ShowMessageInterface {
    private GoogleMap mMap;
    private MapManager mapManager;
    private Trip currentTrip;
    private Integer currentStatus;
    private Marker markerCar;
    private List<LatLng> driverPath;
    private Context mContext;
    private Boolean initializeDriver;
    private Handler updateDriverPositionHandler;
    private Runnable updateDriverPositionRunnable;
    private Handler checkDriverPositionHandler;
    private Runnable checkDriverPositionRunnable;
    private Handler checkTripStatusHandler;
    private Runnable checkTripStatusRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_trip);
        this.createMenu(userLogged);
        mContext = this;
        Intent intent = getIntent();
        currentTrip = (Trip) intent.getSerializableExtra("currentTrip");
        initializeDriver = intent.getBooleanExtra("fromHome", false);
        currentStatus = currentTrip.getStatus();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        this.driverPath = new ArrayList<>();
        checkTripStatus();
        checkDriverPosition();
    }

    private void checkDriverPosition() {
        checkDriverPositionHandler = new Handler();
        Integer delay = 2500;
        checkDriverPositionRunnable = new Runnable() {
            @Override
            public void run() {
                UserService userService = new UserService(mContext);
                Call<UserPositionResponse> call = userService.getPositionUser(currentTrip.getDriverId());
                call.enqueue(new Callback<UserPositionResponse>() {
                    @Override
                    public void onResponse(Call<UserPositionResponse> call, Response<UserPositionResponse> response) {
                        UserPositionResponse userPositionResponse = response.body();
                        if (userPositionResponse != null && userPositionResponse.getPosition() != null) {
                            String[] positionSplit = userPositionResponse.getPosition().split(",");
                            LatLng position = new LatLng(Double.valueOf(positionSplit[0]), Double.valueOf(positionSplit[1]));
                            if (!driverPath.contains(position)) {
                                driverPath.add(position);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserPositionResponse> call, Throwable t) {
                        Log.v("TRIP", t.getMessage());

                    }

                });
                if (currentTrip.getStatus() != TripStatus.COMPLETED.ordinal()) {
                    checkDriverPositionHandler.postDelayed(this, delay);
                }
            }
        };
        checkDriverPositionHandler.postDelayed(checkDriverPositionRunnable, delay);
    }

    private void updateDriverPosition() {
        updateDriverPositionHandler = new Handler();
        Integer delay = 2000;
        updateDriverPositionRunnable = new Runnable() {
            Integer index = 0;
            Integer next = 0;
            LatLng startPosition;
            LatLng endPosition;

            @Override
            public void run() {
                if (index < driverPath.size()) {
                    if (index == driverPath.size() - 1) {
                        next = index;
                    } else {
                        next = index + 1;
                    }

                    startPosition = driverPath.get(index);
                    endPosition = driverPath.get(next);
                    if (markerCar == null) {
                        markerCar = mapManager.addMarkerCar(startPosition);
                    }
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                    valueAnimator.setDuration(delay);
                    valueAnimator.setInterpolator(new LinearInterpolator());
                    valueAnimator.addUpdateListener(valueAnimator1 -> {
                        float v = valueAnimator1.getAnimatedFraction();
                        double lng = v * endPosition.longitude + (1 - v)
                                * startPosition.longitude;
                        double lat = v * endPosition.latitude + (1 - v)
                                * startPosition.latitude;
                        LatLng newPos = new LatLng(lat, lng);
                        mapManager.moveMarker(markerCar, newPos);
                    });
                    valueAnimator.start();
                    if (index != driverPath.size() - 1) {
                        index++;
                    }
                }
                if (currentStatus != TripStatus.COMPLETED.ordinal()) {
                    updateDriverPositionHandler.postDelayed(this, delay);
                }

            }
        };
        updateDriverPositionHandler.postDelayed(updateDriverPositionRunnable, delay);
    }

    private void checkTripStatus() {
        checkTripStatusHandler = new Handler();
        Integer delay = 2500;
        checkTripStatusRunnable = new Runnable() {
            @Override
            public void run() {
                TripService tripService = new TripService(mContext);
                Call<Trip> call = tripService.get(currentTrip.getId());
                call.enqueue(new Callback<Trip>() {
                    @Override
                    public void onResponse(Call<Trip> call, Response<Trip> response) {
                        currentTrip = response.body();
                        if (currentStatus != currentTrip.getStatus()) {
                            currentStatus = currentTrip.getStatus();
                            if (currentStatus == TripStatus.DRIVER_GOING_ORIGIN.ordinal()) {
                                initTripDriverGoingOrigin();
                            }
                            if (currentStatus == TripStatus.IN_TRAVEL.ordinal()) {
                                initTripInTravel();
                            }
                            if (currentStatus == TripStatus.COMPLETED.ordinal()) {
                                initTripCompleted();
                            }
                            if (currentStatus == TripStatus.TRIP_CANCELLED.ordinal()) {
                                initTripCancelled();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Trip> call, Throwable t) {
                        Log.v("TRIP", t.getMessage());
                       // showMessage("Ha ocurrido un error al solicitar el viaje.");

                    }
                });
                if (currentStatus != TripStatus.COMPLETED.ordinal()) {
                    checkTripStatusHandler.postDelayed(this, delay);
                }


            }
        };
        checkTripStatusHandler.postDelayed(checkTripStatusRunnable, delay);
    }

    private void initTripDriverGoingOrigin() {
        TextView textStatus = findViewById(R.id.tripStatus);
        textStatus.setText("En Camino");
        checkDriverPosition();
        updateDriverPosition();
        initializeDriver = false;
    }


    private void initTripInTravel() {
        if (initializeDriver) {
            checkDriverPosition();
            updateDriverPosition();
        }
        initializeDriver = false;

        TextView textStatus = findViewById(R.id.tripStatus);
        textStatus.setText("En Viaje");
    }

    private void initTripCompleted() {
        if (initializeDriver) {
            checkDriverPosition();
            updateDriverPosition();
        }
        initializeDriver = false;
        TripService tripService = new TripService(mContext);
        Call<Trip> call = tripService.getFull(currentTrip.getId());
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                currentTrip = response.body();
                Intent intent = new Intent(mContext, FeedbackActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("currentTrip", currentTrip);
                showMessage("Viaje finalizado con exito!");
                startActivity(intent);
            }
            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                Log.v("TRIP", t.getMessage());
                // showMessage("Ha ocurrido un error al solicitar el viaje.");

            }
        });

    }
    private void initTripCancelled() {
        Intent intent = new Intent(this, HomeClientActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showMessage("No se encontraron conductores para su viaje.");
        startActivity(intent);
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
        drawSummaryPath();
    }


    private void drawSummaryPath() {
        if (currentTrip != null) {
            MapRoute route = currentTrip.getRoutes().get(0);
            mapManager.drawPath(route.getOverviewPolyline());
            mapManager.zoomToPath(route.getOverviewPolyline());
        }
    }

    public void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(checkTripStatusHandler != null && checkTripStatusRunnable != null){
            checkTripStatusHandler.removeCallbacks(checkTripStatusRunnable);
        }
        if(checkDriverPositionHandler!= null && checkDriverPositionRunnable != null){
            checkDriverPositionHandler.removeCallbacks(checkDriverPositionRunnable);
        }
        if(updateDriverPositionHandler != null && updateDriverPositionRunnable != null){
            updateDriverPositionHandler.removeCallbacks(updateDriverPositionRunnable);
        }
    }
}
