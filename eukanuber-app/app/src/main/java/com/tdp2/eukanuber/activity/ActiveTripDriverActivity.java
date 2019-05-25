package com.tdp2.eukanuber.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.PolyUtil;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.interfaces.ShowMessageInterface;
import com.tdp2.eukanuber.manager.AppSecurityManager;
import com.tdp2.eukanuber.manager.MapManager;
import com.tdp2.eukanuber.model.GetRouteRequest;
import com.tdp2.eukanuber.model.MapRoute;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.TripStatus;
import com.tdp2.eukanuber.model.UpdateStatusTripRequest;
import com.tdp2.eukanuber.model.UpdateUserPositionRequest;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.services.TripService;
import com.tdp2.eukanuber.services.UserService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveTripDriverActivity extends SecureActivity implements OnMapReadyCallback, ShowMessageInterface {
    private GoogleMap mMap;
    private MapManager mapManager;
    private Trip currentTrip;
    private LocationManager locationManager;
    private Marker markerCar;
    private Activity mActivity;
    private Integer timeSimulationStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_trip_driver);
        this.createMenu(userLogged);
        mActivity = this;
        Intent intent = getIntent();

        currentTrip = (Trip) intent.getSerializableExtra("currentTrip");
        timeSimulationStep = 10000;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        initDriverRoute();
    }

    private void initDriverRoute() {
        TripService tripService = new TripService(mActivity);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        String driverLocationStr = currentTrip.getOriginCoordinates();
        if(location != null){
            driverLocationStr = location.getLatitude() + ", " + location.getLongitude();
        }
        GetRouteRequest getRouteRequest = new GetRouteRequest(driverLocationStr, currentTrip.getOriginCoordinates());
        Call<MapRoute> call = tripService.getRoutes(getRouteRequest);
        call.enqueue(new Callback<MapRoute>() {
            @Override
            public void onResponse(Call<MapRoute> call, Response<MapRoute> response) {
                MapRoute route = response.body();
                if (route != null) {
                    mapManager.drawPath(route.getOverviewPolyline());
                    if(currentTrip.getStatus() == TripStatus.DRIVER_GOING_ORIGIN.ordinal()){
                        stepGoToOrigin(route);
                    }
                    if(currentTrip.getStatus() == TripStatus.IN_TRAVEL.ordinal()){
                        stepGoToDestination(currentTrip.getRoutes().get(0));
                    }
                }
            }

            @Override
            public void onFailure(Call<MapRoute> call, Throwable t) {
                Log.v("TRIP", t.getMessage());
            }
        });
    }

    private void stepGoToOrigin(MapRoute route){
        List<LatLng> pointsPolyline = PolyUtil.decode(route.getOverviewPolyline().getPoints());
        Handler handler = new Handler();
        LatLng initialPositionDriver = pointsPolyline.get(0);
        moveCar(initialPositionDriver);
        refreshDriverPosition(initialPositionDriver);
        Runnable runnable = () -> driverOnOrigin();
        handler.postDelayed(runnable, timeSimulationStep);
    }

    private void moveCar(LatLng position){
        if (markerCar == null) {
            markerCar = mapManager.addMarkerCar(position);
        }
        mapManager.moveMarker(markerCar, position);
        mapManager.moveCamera(position);

    }
    private void stepGoToDestination(MapRoute route){
        mapManager.clearMap();
        markerCar = null;
        List<LatLng> pointsRouteTripPolyline = PolyUtil.decode(route.getOverviewPolyline().getPoints());
        LatLng positionOrigin = pointsRouteTripPolyline.get(0);
        moveCar(positionOrigin);
        mapManager.drawPath(route.getOverviewPolyline());
        Handler handler = new Handler();
        Runnable runnable = () -> driverOnDestination();
        handler.postDelayed(runnable, timeSimulationStep);
    }

    private void refreshDriverPosition(LatLng position) {
        UpdateUserPositionRequest updateUserPositionRequest = new UpdateUserPositionRequest(String.valueOf(position.latitude), String.valueOf(position.longitude));
        UserService userService = new UserService(this);
        Call<User> call = userService.updatePositionUser(updateUserPositionRequest);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userUpdated = response.body();
                if(userUpdated != null){
                    Log.d("USER UPDATED", userUpdated.getId());
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("UPDATE USER POSITION", t.getMessage());
            }
        });
    }


    private void driverOnOrigin() {
        Button buttonStatusStart = findViewById(R.id.button_status_start);
        buttonStatusStart.setVisibility(View.VISIBLE);
        Button buttonStatusFinish = findViewById(R.id.button_status_finish);
        buttonStatusFinish.setVisibility(View.GONE);
        showStatus();
        MapRoute tripRoute = currentTrip.getRoutes().get(0);
        List<LatLng> pointsRouteTripPolyline = PolyUtil.decode(tripRoute.getOverviewPolyline().getPoints());
        LatLng positionOrigin = pointsRouteTripPolyline.get(0);
        moveCar(positionOrigin);
        refreshDriverPosition(positionOrigin);
        buttonStatusStart.setOnClickListener(v -> {
            hideStatus();
            UpdateStatusTripRequest updateStatusTripRequest = new UpdateStatusTripRequest(TripStatus.IN_TRAVEL.ordinal());
            TripService tripService = new TripService(mActivity);
            Call<Trip> call = tripService.updateStatusTrip(currentTrip.getId(), updateStatusTripRequest);
            call.enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    currentTrip = response.body();
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    Log.d("UPDATE STATUS TRIP", t.getMessage());
                }
            });
            stepGoToDestination(tripRoute);
        });

    }

    private void driverOnDestination() {
        Button buttonStatusStart = findViewById(R.id.button_status_start);
        buttonStatusStart.setVisibility(View.GONE);
        Button buttonStatusFinish = findViewById(R.id.button_status_finish);
        buttonStatusFinish.setVisibility(View.VISIBLE);
        MapRoute tripRoute = currentTrip.getRoutes().get(0);
        List<LatLng> pointsRouteTripPolyline = PolyUtil.decode(tripRoute.getOverviewPolyline().getPoints());
        LatLng positionDestination = pointsRouteTripPolyline.get(pointsRouteTripPolyline.size()-1);
        moveCar(positionDestination);
        refreshDriverPosition(positionDestination);
        showStatus();
        buttonStatusFinish.setOnClickListener(v -> {
            UpdateStatusTripRequest updateStatusTripRequest = new UpdateStatusTripRequest(TripStatus.COMPLETED.ordinal());
            TripService tripService = new TripService(mActivity);
            Call<Trip> call = tripService.updateStatusTrip(currentTrip.getId(), updateStatusTripRequest);
            call.enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    currentTrip = response.body();
                    Intent intent = new Intent(mActivity, FeedbackActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("currentTrip", currentTrip);
                    showMessage("Viaje finalizado con exito!");
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    Log.d("UPDATE STATUS TRIP", t.getMessage());
                }
            });

        });

    }

    private void showStatus() {
        LinearLayout layoutMap = findViewById(R.id.layoutMap);
        LinearLayout layoutStatus = findViewById(R.id.layoutStatus);
        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) layoutMap.getLayoutParams();
        lParams.weight = 8;
        layoutMap.setLayoutParams(lParams);
        LinearLayout.LayoutParams lParamsStatus = (LinearLayout.LayoutParams) layoutStatus.getLayoutParams();
        lParamsStatus.weight = 2;
        layoutStatus.setLayoutParams(lParamsStatus);
    }

    private void hideStatus() {
        LinearLayout layoutMap = findViewById(R.id.layoutMap);
        LinearLayout layoutStatus = findViewById(R.id.layoutStatus);
        LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) layoutMap.getLayoutParams();
        lParams.weight = 10;
        layoutMap.setLayoutParams(lParams);
        LinearLayout.LayoutParams lParamsStatus = (LinearLayout.LayoutParams) layoutStatus.getLayoutParams();
        lParamsStatus.weight = 0;
        layoutStatus.setLayoutParams(lParamsStatus);
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        moveCar(position);
        mapManager.moveCamera(position);
    }

    public void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }


}
