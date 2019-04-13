package com.tdp2.eukanuber.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.interfaces.ShowMessageInterface;
import com.tdp2.eukanuber.manager.MapManager;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.services.TripService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeDriverActivity extends MenuActivity implements OnMapReadyCallback, ShowMessageInterface {
    private GoogleMap mMap;
    private final String DEFAULT_TRIP_ID = "58ce748f-d68b-40db-a5b7-9598806a1d9a";
    private MapManager mapManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_driver);
        this.createMenu();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initDriverHome();
    }


    private void initDriverHome() {
        TextView driverStatusView = findViewById(R.id.driverStatus);
        driverStatusView.setOnClickListener(view -> {
            // Llega id del trip en el push para ir a buscar info del trip
            SharedPreferences settings = getSharedPreferences(NewTripActivity.PREFS_NAME, 0);
            String newTripId = settings.getString("currentTripId", DEFAULT_TRIP_ID);
            TripService tripService = new TripService();
            Call<Trip> call = tripService.get(newTripId);
            ProgressDialog dialog = new ProgressDialog(HomeDriverActivity.this);
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
                        List<String> pets = new ArrayList<>();
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

            /*
            AssignDriverToTripRequest assignDriverToTripRequest = new AssignDriverToTripRequest("dummyDriverId");
            TripService tripService = new TripService();
            Call<Trip> call = tripService.assignDriverToTrip(trip.getId(), assignDriverToTripRequest);
            call.enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    popupWindow.dismiss();
                    showMessage("El viaje ha sido confirmado.");
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    popupWindow.dismiss();
                    // TODO: mostrar error de que el viaje ha sido tomado y/o seguir de largo
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
        if (quantSmall > 0) {
            petsString += quantSmall.toString() + " chica";
            if (quantMedium > 0 || quantLarge > 0) {
                petsString += " - ";
            }
        }
        if (quantMedium > 0) {
            petsString += quantMedium.toString() + " mediana ";
            if (quantLarge > 0) {
                petsString += " - ";
            }
        }
        if (quantLarge > 0) {
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
        LocationListener locationListener = new LocationListener() {
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
        mapManager.setListener(locationListener);
    }

}
