package com.tdp2.eukanuber.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.model.MapRoute;
import com.tdp2.eukanuber.model.MapRouteLeg;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.services.TripService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends MenuActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private final int PERMISSION_FINE_LOCATION = 1;
    private final String DRIVER_TYPE = "driver";
    private final String CLIENT_TYPE = "client";
    private final Integer TRIP_ACCEPTED = 1;
    private final Integer TRIP_CANCELLED = 4;
    private final String DEFAULT_TRIP_ID = "58ce748f-d68b-40db-a5b7-9598806a1d9a";


    private LocationListener locationListener;
    private LocationManager locationManager;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.createMenu();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences settings = getSharedPreferences("USER_INFO", 0);
        userType = settings.getString("userType", "client");

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                if (mMap != null) {
                    // mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
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
            showPath();
        }
        if (userType.equals(DRIVER_TYPE)) {
            initDriverHome();
        }
    }
    private void showPath(){
        String tripExampleId = "8fb570de-45b1-4be2-a466-e682533baaa1";
        TripService tripService = new TripService();
        Call<Trip> call = tripService.get(tripExampleId);
        ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
        dialog.setMessage("Espere un momento por favor");
        dialog.show();
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                dialog.dismiss();
                Trip trip = response.body();
                MapRoute route = trip.getRoutes().get(0);
                List<LatLng> pointsPolyline = PolyUtil.decode(route.getOverviewPolyline().getPoints());
                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(Color.BLUE);
                polyOptions.width(10);
                polyOptions.addAll(pointsPolyline);
                mMap.clear();
                mMap.addPolyline(polyOptions);
                /*MapRouteLeg leg = route.getLegs().get(0);
                LatLng locationOrigin = new LatLng(Double.valueOf(leg.getStartLocation().getLat()), Double.valueOf(leg.getStartLocation().getLng()));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(locationOrigin.latitude, locationOrigin.longitude))
                        .zoom(12.0f)
                        .tilt(40).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);*/
                int padding = 100;
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : pointsPolyline) {
                    builder.include(latLng);
                }

                final LatLngBounds bounds = builder.build();

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                Log.v("TRIP", t.getMessage());
                dialog.dismiss();
                showMessage("Ha ocurrido un error al solicitar el viaje.");

            }
        });
    }

    private void initClientHome() {
        LinearLayout layoutMap = findViewById(R.id.layoutMap);
        layoutMap.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 10f));
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
            // Llega id del trip en el push para ir a buscar info del trip
            SharedPreferences settings = getSharedPreferences(NewTripActivity.PREFS_NAME, 0);
            String newTripId = settings.getString("newTripId", DEFAULT_TRIP_ID);
            TripService tripService = new TripService();
            Call<Trip> call = tripService.get(newTripId);
            ProgressDialog dialog = new ProgressDialog(HomeActivity.this);
            dialog.setMessage("Espere un momento por favor");
            dialog.show();
            call.enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    dialog.dismiss();
                    Trip trip = response.body();
                    if (trip == null) {
                        trip = new Trip();
                        trip.setDestination("Santa Fe 3329 Entre Bulnes y Vidt");
                        trip.setOrigin("Paseo Colon 850 Esquina Independencia");
                        trip.setEscort(true);
                        trip.setPayment("cash");
                        Collection<String> pets = new ArrayList<>();
                        pets.add("S");
                        pets.add("M");
                        pets.add("L");
                        trip.setPets(pets);
                    }
                    openPopupNewTripDriver(view, trip);
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    Log.v("TRIP", t.getMessage());
                    dialog.dismiss();
                    showMessage("Ha ocurrido un error al solicitar el viaje.");

                }
            });
        });

    }

    private void openPopupNewTripDriver(View v, Trip trip) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.new_trip_popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setAnimationStyle(R.style.popup_window_animation);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, -100);
        String petsString = getPetStringFromArray(trip.getPets());

        String escortText = trip.getEscort() ? "Si" : "No";
        trip.setDuration("1h 04m");
        trip.setPrice("$456,90");
        ((TextView) popupView.findViewById(R.id.tripOriginText)).setText(trip.getOrigin());
        ((TextView) popupView.findViewById(R.id.tripDestinationText)).setText(trip.getDestination());
        ((TextView) popupView.findViewById(R.id.tripDurationText)).setText(trip.getDuration());
        ((TextView) popupView.findViewById(R.id.tripPriceText)).setText(trip.getPrice());

        ((TextView) popupView.findViewById(R.id.petsText)).setText(petsString);
        ((TextView) popupView.findViewById(R.id.escortText)).setText(escortText);
        ImageButton buttonCancel = popupView.findViewById(R.id.buttonCancelTrip);
        ImageButton buttonConfirm = popupView.findViewById(R.id.buttonConfirmTrip);
        buttonCancel.setOnClickListener(view -> {
            popupWindow.dismiss();

            showMessage("El viaje ha sido cancelado.");
        });
        buttonConfirm.setOnClickListener(view -> {

            /*TripService tripService = new TripService();
            ChangeTripStatusRequest changeTripStatusRequest = new ChangeTripStatusRequest();
            changeTripStatusRequest.setId(trip.getId());
            changeTripStatusRequest.setStatus(TRIP_ACCEPTED);
            Call<Trip> call = tripService.updateStatus(changeTripStatusRequest);
            call.enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    popupWindow.dismiss();
                    showMessage("El viaje ha sido confirmado.");
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    popupWindow.dismiss();
                    showMessage("Ha ocurrido un error al confirmar el viaje.");
                }
            });*/
            popupWindow.dismiss();
            showMessage("El viaje ha sido confirmado.");
        });
    }

    private String getPetStringFromArray(Collection<String> pets) {
        String petsString = "";
        Integer quantSmall = Collections.frequency(pets, "S");
        Integer quantMedium = Collections.frequency(pets, "M");
        Integer quantLarge = Collections.frequency(pets, "L");
        if(quantSmall > 0){
            petsString += quantSmall.toString() + " chica";
            if(quantMedium > 0 || quantLarge > 0){
                petsString += " - ";
            }
        }
        if(quantMedium > 0){
            petsString += quantMedium.toString() + " mediana ";
            if(quantLarge > 0){
                petsString += " - ";
            }
        }
        if(quantLarge > 0){
            petsString += quantLarge.toString() + " grande";
        }
        return petsString;
    }

    public void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    private void checkPermissionsLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_FINE_LOCATION);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMapLocation();
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
        initMapLocation();
    }

    private void initMapLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setMinZoomPreference(10.0f);
        mMap.setMaxZoomPreference(20.0f);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(18.0f)
                    .tilt(40).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
        }
    }
}
