package com.tdp2.eukanuber.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
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
import com.tdp2.eukanuber.model.TripStatus;
import com.tdp2.eukanuber.model.UpdateStatusTripRequest;
import com.tdp2.eukanuber.services.TripService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SummaryTripActivity extends SecureActivity implements OnMapReadyCallback, ShowMessageInterface {
    private GoogleMap mMap;
    private MapManager mapManager;
    private Trip currentTrip;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_trip);
        this.createMenu(userLogged);
        mContext = this;
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
            UpdateStatusTripRequest updateStatusTripRequest = new UpdateStatusTripRequest(TripStatus.CANCELLED.ordinal());
            TripService tripService = new TripService(mContext);
            Call<Trip> call = tripService.updateStatusTrip(currentTrip.getId(), updateStatusTripRequest);
            ProgressDialog dialog = new ProgressDialog(SummaryTripActivity.this);
            dialog.setMessage("Espere un momento por favor");
            dialog.show();
            call.enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    dialog.dismiss();

                    Intent intentHome = new Intent(mContext, HomeClientActivity.class);
                    intentHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    showMessage("Viaje cancelado");
                    startActivity(intentHome);
                }
                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("UPDATE STATUS TRIP", t.getMessage());
                    showMessage("Ha ocurrido un error. Intente luego.");
                }
            });

        });
        buttonConfirm.setOnClickListener(view -> {
            UpdateStatusTripRequest updateStatusTripRequest = new UpdateStatusTripRequest(TripStatus.CLIENT_ACCEPTED.ordinal());
            TripService tripService = new TripService(mContext);
            Call<Trip> call = tripService.updateStatusTrip(currentTrip.getId(), updateStatusTripRequest);
            ProgressDialog dialog = new ProgressDialog(SummaryTripActivity.this);
            dialog.setMessage("Espere un momento por favor");
            dialog.show();
            call.enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    dialog.dismiss();
                    Intent intentTrackingTrip = new Intent(mContext, TrackingTripActivity.class);
                    intentTrackingTrip.putExtra("currentTrip", currentTrip);
                    intentTrackingTrip.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    showMessage("Viaje confirmado");
                    startActivity(intentTrackingTrip);
                }
                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    dialog.dismiss();
                    Log.d("UPDATE STATUS TRIP", t.getMessage());
                    showMessage("Ha ocurrido un error. Intente luego.");
                }
            });



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
      //  mapManager.setCurrentLocation();

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
