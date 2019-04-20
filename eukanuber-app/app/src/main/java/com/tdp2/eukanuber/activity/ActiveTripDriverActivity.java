package com.tdp2.eukanuber.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
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
import com.tdp2.eukanuber.model.GetRouteRequest;
import com.tdp2.eukanuber.model.MapRoute;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.TripStatus;
import com.tdp2.eukanuber.services.TripService;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveTripDriverActivity extends MenuActivity implements OnMapReadyCallback, ShowMessageInterface {
    private GoogleMap mMap;
    private MapManager mapManager;
    private Trip currentTrip;
    private LocationManager locationManager;
    private Marker markerCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_trip_driver);
        this.createMenu();
        Intent intent = getIntent();
        currentTrip = (Trip) intent.getSerializableExtra("currentTrip");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        initDriverRoute();
    }

    private void initDriverRoute() {
        TripService tripService = new TripService();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        String driverLocationStr = location.getLatitude() + ", " + location.getLongitude();
        GetRouteRequest getRouteRequest = new GetRouteRequest(driverLocationStr, currentTrip.getOriginCoordinates());
        Call<MapRoute> call = tripService.getRoutes(getRouteRequest);
        call.enqueue(new Callback<MapRoute>() {
            @Override
            public void onResponse(Call<MapRoute> call, Response<MapRoute> response) {
                MapRoute route = response.body();
                if (route != null) {
                    mapManager.drawPath(route.getOverviewPolyline());
                    //mapManager.zoomToPath(route.getOverviewPolyline());
                    initSimulateDriver(route);
                }
            }

            @Override
            public void onFailure(Call<MapRoute> call, Throwable t) {
                Log.v("TRIP", t.getMessage());
            }
        });
    }

    private void initSimulateDriver(MapRoute route) {
        List<LatLng> pointsPolyline = PolyUtil.decode(route.getOverviewPolyline().getPoints());
        Handler handler = new Handler();
        Integer speedCar = 2;
        Integer delay = 1000/speedCar;
        Runnable runnable = new Runnable() {
            Integer index = 0;
            Integer next = 0;
            LatLng startPosition;
            LatLng endPosition;

            @Override
            public void run() {
                if (index < pointsPolyline.size()) {
                    if (index == pointsPolyline.size() - 1) {
                        next = index;
                    }else{
                        next = index + 1;
                    }
                    startPosition = pointsPolyline.get(index);
                    endPosition = pointsPolyline.get(next);
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
                    index++;
                    handler.postDelayed(this, delay);
                }
            }
        };
        handler.postDelayed(runnable, delay);
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
        markerCar = mapManager.addMarkerCar(position);
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
