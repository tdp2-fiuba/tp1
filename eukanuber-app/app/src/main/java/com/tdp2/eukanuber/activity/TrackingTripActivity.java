package com.tdp2.eukanuber.activity;

import android.animation.ValueAnimator;
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
import com.google.maps.android.PolyUtil;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.interfaces.ShowMessageInterface;
import com.tdp2.eukanuber.manager.MapManager;
import com.tdp2.eukanuber.model.MapRoute;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.TripStatus;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.services.TripService;
import com.tdp2.eukanuber.services.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingTripActivity extends MenuActivity implements OnMapReadyCallback, ShowMessageInterface {
    private GoogleMap mMap;
    private MapManager mapManager;
    private Trip currentTrip;
    private Integer currentStatus;
    private Marker markerCar;
    private List<LatLng> driverPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_trip);
        this.createMenu();
        Intent intent = getIntent();
        currentTrip = (Trip) intent.getSerializableExtra("currentTrip");
        currentStatus = currentTrip.getStatus();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.driverPath = new ArrayList<>();
        checkTripStatus();
    }

    private void checkDriverPosition() {
        Handler handler = new Handler();
        Integer delay = 2500;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                UserService userService = new UserService();
                Call<User> call = userService.getUser(currentTrip.getDriverId());
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User driver = response.body();
                        String[] positionSplit = driver.getPosition().split(",");
                        LatLng position = new LatLng(Double.valueOf(positionSplit[0]), Double.valueOf(positionSplit[1]));
                        if(!driverPath.contains(position)){
                            driverPath.add(position);
                        }

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.v("TRIP", t.getMessage());
                        showMessage("Ha ocurrido un error al obtener el driver.");

                    }

                });
                if (currentTrip.getStatus() != TripStatus.ARRIVED_DESTINATION.ordinal()) {
                    handler.postDelayed(this, delay);
                }
            }
        };
        handler.postDelayed(runnable, delay);
    }

    private void updateDriverPosition() {
        Handler handler = new Handler();
        Integer delay = 2000;
        Runnable runnable = new Runnable() {
            Integer index = 0;
            Integer next = 0;
            LatLng startPosition;
            LatLng endPosition;

            @Override
            public void run() {
                if(index < driverPath.size()){
                    if (index == driverPath.size() - 1) {
                        next = index;
                    } else {
                        next = index + 1;
                    }

                    startPosition = driverPath.get(index);
                    endPosition = driverPath.get(next);
                    if(markerCar == null) {
                        markerCar = mapManager.addMarkerCar(startPosition);
                    }
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                    valueAnimator.setDuration(delay);
                    valueAnimator.setInterpolator(new LinearInterpolator());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            float v = valueAnimator.getAnimatedFraction();
                            double lng = v * endPosition.longitude + (1 - v)
                                    * startPosition.longitude;
                            double lat = v * endPosition.latitude + (1 - v)
                                    * startPosition.latitude;
                            LatLng newPos = new LatLng(lat, lng);
                            mapManager.moveMarker(markerCar, newPos);
                        }
                    });
                    valueAnimator.start();
                    if (index != driverPath.size() - 1) {
                        index++;
                    }
                }
                if (currentStatus != TripStatus.ARRIVED_DESTINATION.ordinal()) {
                    handler.postDelayed(this, delay);
                }

            }
        };
        handler.postDelayed(runnable, delay);
    }

    private void checkTripStatus() {
        Handler handler = new Handler();
        Integer delay = 2500;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                TripService tripService = new TripService();
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
                            if (currentStatus == TripStatus.ARRIVED_DESTINATION.ordinal()) {
                                initTripArrivedDestination();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Trip> call, Throwable t) {
                        Log.v("TRIP", t.getMessage());
                        showMessage("Ha ocurrido un error al solicitar el viaje.");

                    }
                });
                if (currentStatus != TripStatus.ARRIVED_DESTINATION.ordinal()) {
                    handler.postDelayed(this, delay);
                }


            }
        };
        handler.postDelayed(runnable, delay);
    }

    private void initTripDriverGoingOrigin() {
        TextView textStatus = findViewById(R.id.tripStatus);
        textStatus.setText("En Camino");
        checkDriverPosition();
        updateDriverPosition();
    }


    private void initTripInTravel() {
        TextView textStatus = findViewById(R.id.tripStatus);
        textStatus.setText("En Viaje");
    }

    private void initTripArrivedDestination() {
        Intent intent = new Intent(this, HomeClientActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showMessage("Viaje finalizado con exito!");
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


}
