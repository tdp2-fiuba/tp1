package com.tdp2.eukanuber.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.interfaces.ShowMessageInterface;
import com.tdp2.eukanuber.manager.MapManager;
import com.tdp2.eukanuber.model.MapRoute;
import com.tdp2.eukanuber.model.Trip;

public class SummaryTripActivity extends MenuActivity implements OnMapReadyCallback, ShowMessageInterface {
    private GoogleMap mMap;
    private MapManager mapManager;
    private Trip currentTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_trip);
        this.createMenu();
        Intent intent = getIntent();
        currentTrip = (Trip) intent.getSerializableExtra("currentTrip");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        TextView textDistance = this.findViewById(R.id.textDistance);
        TextView textDuration = this.findViewById(R.id.textDuration);
        TextView textPrice = this.findViewById(R.id.textPrice);
        textDistance.setText(currentTrip.getDistance());
        textDuration.setText(currentTrip.getDuration());
        textPrice.setText(currentTrip.getPrice());
        ImageButton buttonCancel = this.findViewById(R.id.buttonCancelTrip);
        ImageButton buttonConfirm = this.findViewById(R.id.buttonConfirmTrip);
        buttonCancel.setOnClickListener(view -> {
            Intent intentHome = new Intent(this, HomeClientActivity.class);
            intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            showMessage("Viaje cancelado");
            startActivity(intentHome);
        });
        buttonConfirm.setOnClickListener(view -> {
            Intent intentTrackingTrip = new Intent(this, TrackingTripActivity.class);
            intentTrackingTrip.putExtra("currentTrip", currentTrip);
            intentTrackingTrip.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentTrackingTrip);
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

    }

    private void drawSummaryPath() {
        if(currentTrip != null){
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
