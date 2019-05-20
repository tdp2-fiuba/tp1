package com.tdp2.eukanuber.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.interfaces.ShowMessageInterface;
import com.tdp2.eukanuber.manager.MapManager;
import com.tdp2.eukanuber.model.RefuseDriverTripRequest;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.UpdateUserPositionRequest;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.services.TripService;
import com.tdp2.eukanuber.services.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeDriverActivity extends SecureActivity implements OnMapReadyCallback, ShowMessageInterface {
    private GoogleMap mMap;
    private MapManager mapManager;
    private List<String> tripsOpened;
    private Activity mActivity;
    private Handler handlerRequestTrips;
    private Runnable runnableRequestTrips;
    private Integer delayRequestTrips;
    private Boolean popupOpen;
    private Integer secondsPopup;
    private Handler secondsCounterHandler;
    private Runnable secondsCounterRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tripsOpened = new ArrayList<>();
        mActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_driver);
        this.createMenu(userLogged);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        popupOpen = false;
        initDriverHome();
    }

    private void initDriverHome() {
        handlerRequestTrips = new Handler();
        delayRequestTrips = 500;
        runnableRequestTrips = new Runnable() {
            @Override
            public void run() {
                if (!popupOpen) {
                    UserService userService = new UserService(mActivity);
                    Call<Trip> call = userService.getPendingTrips();
                    call.enqueue(new Callback<Trip>() {
                        @Override
                        public void onResponse(Call<Trip> call, Response<Trip> response) {
                            Trip trip = response.body();
                            if (trip != null && trip.getId() != null) {
                                if (!tripsOpened.contains(trip.getId())) {
                                    tripsOpened.add(trip.getId());
                                    View view = mActivity.findViewById(R.id.layoutMap);
                                    openPopupNewTripDriver(view, trip);
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<Trip> call, Throwable t) {
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
        secondsPopup = 20;
        String escortText = trip.getEscort() ? "Si" : "No";
        trip.setDuration(trip.getDuration());
        trip.setPrice(trip.getPrice());
        ((TextView) popupView.findViewById(R.id.tripOriginText)).setText(trip.getOrigin());
        ((TextView) popupView.findViewById(R.id.tripDestinationText)).setText(trip.getDestination());
        ((TextView) popupView.findViewById(R.id.tripDurationText)).setText(trip.getDuration());
        ((TextView) popupView.findViewById(R.id.tripPriceText)).setText(trip.getPrice());

        String pets = this.getPetsString(trip.getPets());
        ((TextView) popupView.findViewById(R.id.petsText)).setText(pets);
        ((TextView) popupView.findViewById(R.id.escortText)).setText(escortText);

        TextView secondsCounterView = popupView.findViewById(R.id.secondsCounter);
        secondsPopup = 20;
        secondsCounterView.setText(String.valueOf(secondsPopup));
        secondsCounterHandler = new Handler();
        secondsCounterRunnable = new Runnable() {

            @Override
            public void run() {
                if (secondsPopup > 0) {
                    secondsPopup--;
                    secondsCounterHandler.postDelayed(this, 1000);
                    secondsCounterView.setText(String.valueOf(secondsPopup));
                } else {
                    secondsCounterView.setText(String.valueOf(secondsPopup));
                    if(popupOpen){
                        popupWindow.dismiss();
                        popupOpen = false;
                        showMessage("El viaje ha sido rechazado.");
                    }

                }

            }
        };
        secondsCounterHandler.postDelayed(secondsCounterRunnable, 1000);
        ImageButton buttonCancel = popupView.findViewById(R.id.buttonCancelTrip);
        ImageButton buttonConfirm = popupView.findViewById(R.id.buttonConfirmTrip);

        buttonCancel.setOnClickListener(view ->

        {
            TripService tripService = new TripService(mActivity);
            Call<Trip> call = tripService.refuseDriverTrip(trip.getId(), new RefuseDriverTripRequest(secondsPopup));
            call.enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    popupWindow.dismiss();
                    popupOpen = false;
                    showMessage("El viaje ha sido rechazado.");
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    popupWindow.dismiss();
                    popupOpen = false;
                    showMessage("Ha ocurrido un error al rechazar el viaje.");
                }
            });
        });
        buttonConfirm.setOnClickListener(view ->

        {
            TripService tripService = new TripService(mActivity);
            Call<Trip> call = tripService.confirmDriverTrip(trip.getId());
            call.enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    popupWindow.dismiss();
                    Trip trip = response.body();
                    Intent intentActiveTripDriver = new Intent(mActivity, ActiveTripDriverActivity.class);
                    intentActiveTripDriver.putExtra("currentTrip", trip);
                    intentActiveTripDriver.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    popupOpen = false;
                    showMessage("El viaje ha sido confirmado.");
                    startActivity(intentActiveTripDriver);
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    popupWindow.dismiss();
                    popupOpen = false;
                    showMessage("Ha ocurrido un error al confirmar el viaje.");
                }
            });
        });
    }

    private String getPetsString(List<String> pets) {
        String toReturn = "";
        Integer small = Collections.frequency(pets, "S");
        Integer medium = Collections.frequency(pets, "M");
        Integer large = Collections.frequency(pets, "L");

        if (small > 0) {
            toReturn += small.toString() + " chica/s";

            if (medium > 0 || large > 0) {
                toReturn += ", ";
            }
        }

        if (medium > 0) {
            toReturn += medium.toString() + " mediana/s";
            if (large > 0) {
                toReturn += ", ";
            }
        }

        if (large > 0) {
            toReturn += large.toString() + " grande/s";
        }

        return toReturn;
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
        Location location = this.getLastKnownLocation();

        if (location == null) {
            return;
        }

        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        UpdateUserPositionRequest updateUserPositionRequest = new UpdateUserPositionRequest(String.valueOf(position.latitude), String.valueOf(position.longitude));
        UserService userService = new UserService(this);
        Call<User> call = userService.updatePositionUser(updateUserPositionRequest);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User userUpdated = response.body();
                if (userUpdated != null) {
                    Log.d("USER UPDATED", userUpdated.getId());
                }


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

    private Location getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {
            Location location = locationManager.getLastKnownLocation(provider);

            if (location == null) {
                continue;
            }

            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = location;
            }
        }

        return bestLocation;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (handlerRequestTrips != null && runnableRequestTrips != null) {
            handlerRequestTrips.removeCallbacks(runnableRequestTrips);
        }
    }


}
