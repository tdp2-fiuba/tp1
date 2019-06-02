package com.tdp2.eukanuber.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.tdp2.eukanuber.model.FeedbackRequest;
import com.tdp2.eukanuber.model.MapRoute;
import com.tdp2.eukanuber.model.ReviewRequest;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.TripStatus;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.model.UserPositionResponse;
import com.tdp2.eukanuber.services.TripService;
import com.tdp2.eukanuber.services.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingTripActivity extends SecureActivity implements OnMapReadyCallback, ShowMessageInterface {
    private GoogleMap mMap;
    private MapManager mapManager;
    private Trip currentTrip;
    private Integer currentStatus;
    private Marker markerCar;
    private Context mContext;
    private Handler checkDriverPositionHandler;
    private Runnable checkDriverPositionRunnable;
    private Handler checkTripStatusHandler;
    private Runnable checkTripStatusRunnable;
    private Integer timeSimulationStep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_trip);
        this.createMenu(userLogged);
        timeSimulationStep = 5000;
        mContext = this;
        Intent intent = getIntent();
        currentTrip = (Trip) intent.getSerializableExtra("currentTrip");
        currentStatus = currentTrip.getStatus();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        showCancelButton();
        checkTripStatus();
        checkDriverPosition();
    }

    private void checkDriverPosition() {
        checkDriverPositionHandler = new Handler();
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
                            if (currentStatus == TripStatus.DRIVER_GOING_ORIGIN.ordinal() ||
                                    currentStatus == TripStatus.IN_TRAVEL.ordinal() ||
                                    currentStatus == TripStatus.ARRIVED_DESTINATION.ordinal()) {
                                if (markerCar == null) {
                                    markerCar = mapManager.addMarkerCar(position);
                                    mapManager.moveCamera(position);
                                }
                                mapManager.moveMarker(markerCar, position);
                                mapManager.moveCamera(position);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserPositionResponse> call, Throwable t) {
                        Log.v("TRIP", t.getMessage());

                    }

                });
                if (currentTrip.getStatus() != TripStatus.COMPLETED.ordinal()) {
                    checkDriverPositionHandler.postDelayed(this, timeSimulationStep/2);
                }
            }
        };
        checkDriverPositionHandler.postDelayed(checkDriverPositionRunnable, 0);
    }

    private void checkTripStatus() {
        checkTripStatusHandler = new Handler();
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
                    checkTripStatusHandler.postDelayed(this, timeSimulationStep);
                }


            }
        };
        checkTripStatusHandler.postDelayed(checkTripStatusRunnable, 0);
    }

    private void initTripDriverGoingOrigin() {
        TextView textStatus = findViewById(R.id.tripStatus);
        textStatus.setText("En Camino");
        showCancelButton();
    }


    private void initTripInTravel() {
        TextView textStatus = findViewById(R.id.tripStatus);
        textStatus.setText("En Viaje");
        hideCancelButton();
    }

    private void initTripCompleted() {
        hideCancelButton();
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



    private void showCancelButton() {
        LinearLayout layoutMap = findViewById(R.id.layoutMap);
        LinearLayout layoutStatus = findViewById(R.id.layoutStatusTrip);
        LinearLayout layoutCancel = findViewById(R.id.layoutCancel);
        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) layoutMap.getLayoutParams();
        lParams.weight = 7;
        layoutMap.setLayoutParams(lParams);
        LinearLayout.LayoutParams lParamsStatus = (LinearLayout.LayoutParams) layoutStatus.getLayoutParams();
        lParamsStatus.weight = 3;
        layoutStatus.setLayoutParams(lParamsStatus);
        layoutCancel.setVisibility(View.VISIBLE);
    }

    private void hideCancelButton() {
        LinearLayout layoutMap = findViewById(R.id.layoutMap);
        LinearLayout layoutStatus = findViewById(R.id.layoutStatusTrip);
        LinearLayout layoutCancel = findViewById(R.id.layoutCancel);
        TextView tripStatus = findViewById(R.id.tripStatus);
        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) layoutMap.getLayoutParams();
        lParams.weight = 8;
        layoutMap.setLayoutParams(lParams);
        LinearLayout.LayoutParams lParamsStatus = (LinearLayout.LayoutParams) layoutStatus.getLayoutParams();
        lParamsStatus.weight = 2;
        layoutStatus.setLayoutParams(lParamsStatus);
        layoutCancel.setVisibility(View.GONE);
        tripStatus.setTextSize(30);
        tripStatus.setPadding(0, 20, 0, 0);
    }

    public void cancelTrip(View view) {
        


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (checkTripStatusHandler != null && checkTripStatusRunnable != null) {
            checkTripStatusHandler.removeCallbacks(checkTripStatusRunnable);
        }
        if (checkDriverPositionHandler != null && checkDriverPositionRunnable != null) {
            checkDriverPositionHandler.removeCallbacks(checkDriverPositionRunnable);
        }
    }
}
