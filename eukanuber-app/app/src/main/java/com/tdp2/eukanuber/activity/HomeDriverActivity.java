package com.tdp2.eukanuber.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.interfaces.ShowMessageInterface;
import com.tdp2.eukanuber.manager.MapManager;
import com.tdp2.eukanuber.model.AssignDriverToTripRequest;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.TripStatus;
import com.tdp2.eukanuber.model.UpdateUserPositionRequest;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.services.TripService;
import com.tdp2.eukanuber.services.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeDriverActivity extends MenuActivity implements OnMapReadyCallback, ShowMessageInterface {
    private GoogleMap mMap;
    private MapManager mapManager;
    private List<String> tripsOpened;
    private Activity mActivity;
    private Handler handlerRequestTrips;
    private Runnable runnableRequestTrips;
    private Integer delayRequestTrips;
    private Boolean popupOpen;
    public static final String driverId = "981db688-ddf0-404a-8461-50fb8675a9cc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tripsOpened = new ArrayList<>();
        mActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_driver);
        this.createMenu();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        popupOpen = false;
        initDriverHome();
    }


    private void initDriverHome() {
        handlerRequestTrips = new Handler();
        delayRequestTrips = 5000;
        runnableRequestTrips = new Runnable() {
            @Override
            public void run() {
                if (!popupOpen) {
                    TripService tripService = new TripService();
                    Call<List<Trip>> call = tripService.getAll(String.valueOf(TripStatus.CLIENT_ACCEPTED.ordinal()));
                    call.enqueue(new Callback<List<Trip>>() {
                        @Override
                        public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                            List<Trip> trips = response.body();
                            if (trips.size() > 0) {
                                Trip trip = trips.get(0);
                                Log.v("CLIENT ACCEPTED TRIP", trip.getId());
                                if (!tripsOpened.contains(trip.getId())) {
                                    tripsOpened.add(trip.getId());
                                    View view = mActivity.findViewById(R.id.layoutMap);
                                    openPopupNewTripDriver(view, trip);
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<List<Trip>> call, Throwable t) {
                            Log.v("TRIP", t.getMessage());
                        }
                    });
                }


                handlerRequestTrips.postDelayed(this, delayRequestTrips);

            }
        };
        handlerRequestTrips.postDelayed(runnableRequestTrips, delayRequestTrips);

    }

    private void openPopupNewTripDriver(View v, Trip trip) {
        popupOpen = true;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.new_trip_popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setAnimationStyle(R.style.popup_window_animation);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, -100);
        //   String petsString = getPetStringFromArray(trip.getPets());

        String escortText = trip.getEscort() ? "Si" : "No";
        trip.setDuration("1h 04m");
        trip.setPrice("$456,90");
        ((TextView) popupView.findViewById(R.id.tripOriginText)).setText(trip.getOrigin());
        ((TextView) popupView.findViewById(R.id.tripDestinationText)).setText(trip.getDestination());
        ((TextView) popupView.findViewById(R.id.tripDurationText)).setText(trip.getDuration());
        ((TextView) popupView.findViewById(R.id.tripPriceText)).setText(trip.getPrice());

        //((TextView) popupView.findViewById(R.id.petsText)).setText(petsString);
        ((TextView) popupView.findViewById(R.id.escortText)).setText(escortText);
        ImageButton buttonCancel = popupView.findViewById(R.id.buttonCancelTrip);
        ImageButton buttonConfirm = popupView.findViewById(R.id.buttonConfirmTrip);
        buttonCancel.setOnClickListener(view -> {
            popupWindow.dismiss();
            popupOpen = false;
            showMessage("No ha aceptado el viaje.");
        });
        buttonConfirm.setOnClickListener(view -> {
            AssignDriverToTripRequest assignDriverToTripRequest = new AssignDriverToTripRequest(driverId);
            TripService tripService = new TripService();
            Call<Trip> call = tripService.assignDriverToTrip(trip.getId(), assignDriverToTripRequest);
            call.enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    popupWindow.dismiss();
                    Trip trip = response.body();
                    Intent intentActiveTripDriver = new Intent(mActivity, ActiveTripDriverActivity.class);
                    intentActiveTripDriver.putExtra("currentTrip", trip);
                    intentActiveTripDriver.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    showMessage("El viaje ha sido confirmado.");
                    handlerRequestTrips.removeCallbacks(runnableRequestTrips);
                    startActivity(intentActiveTripDriver);
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    popupWindow.dismiss();
                    popupOpen = false;
                    // TODO: mostrar error de que el viaje ha sido tomado y/o seguir de largo
                    showMessage("Ha ocurrido un error al confirmar el viaje.");
                }
            });
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
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        UpdateUserPositionRequest updateUserPositionRequest = new UpdateUserPositionRequest(String.valueOf(position.latitude), String.valueOf(position.longitude));
        UserService userService = new UserService();
        Call<User> call = userService.updatePositionUser(HomeDriverActivity.driverId, updateUserPositionRequest);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userUpdated = response.body();
                Log.d("USER UPDATED", userUpdated.getId());

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("UPDATE USER POSITION", t.getMessage());
            }
        });
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
