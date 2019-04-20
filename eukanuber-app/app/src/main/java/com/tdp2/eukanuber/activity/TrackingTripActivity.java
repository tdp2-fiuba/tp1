package com.tdp2.eukanuber.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.interfaces.ShowMessageInterface;
import com.tdp2.eukanuber.manager.MapManager;
import com.tdp2.eukanuber.model.MapRoute;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.services.TripService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingTripActivity extends MenuActivity implements OnMapReadyCallback, ShowMessageInterface {
    private GoogleMap mMap;
    private MapManager mapManager;
    private Trip currentTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_trip);
        this.createMenu();
        /*Intent intent = getIntent();
        currentTrip = (Trip) intent.getSerializableExtra("currentTrip");*/

        getTrip();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getTrip() {
        String tripId = "348d3a0c-9804-4eec-837b-e36a61cbc191";
        TripService tripService = new TripService();
        Call<Trip> call = tripService.get(tripId);
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                currentTrip = response.body();
                drawSummaryPath();
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                Log.v("TRIP", t.getMessage());
                showMessage("Ha ocurrido un error al solicitar el viaje.");

            }
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
        drawSummaryPath();
        //initDriverPosition();
    }

    private void initDriverPosition() {
        LatLng positionDriver = new LatLng(-34.800714, -58.278466);
        mapManager.addMarkerCar(positionDriver);
        mapManager.moveCamera(positionDriver);

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
