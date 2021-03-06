package com.tdp2.eukanuber.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.tdp2.eukanuber.model.TripStatus;
import com.tdp2.eukanuber.model.UpdateUserPositionRequest;
import com.tdp2.eukanuber.model.UpdateUserStatusRequest;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.model.UserStatus;
import com.tdp2.eukanuber.model.UserStatusResponse;
import com.tdp2.eukanuber.services.TripService;
import com.tdp2.eukanuber.services.UserService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeDriverActivity extends SecureActivity implements OnMapReadyCallback, ShowMessageInterface {
    private GoogleMap mMap;
    private MapManager mapManager;
    private Activity mActivity;
    private Boolean popupOpen;
    private Integer secondsPopup;
    private Handler secondsCounterHandler;
    private Runnable secondsCounterRunnable;
    private Boolean userActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_driver);
        this.createMenu(userLogged);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String tripId = intent.getExtras().getString("tripId");
            if (!popupOpen) {
                initNewTrip(tripId, null);
            }

        }
    };

    @Override
    protected void onStart() {
        popupOpen = false;
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("NewTrip")
        );
        UserService userService = new UserService(this);
        Call<UserStatusResponse> callStatus = userService.getUserStatus(userLogged.getId());
        callStatus.enqueue(new Callback<UserStatusResponse>() {
            @Override
            public void onResponse(Call<UserStatusResponse> UserStatusResponse, Response<UserStatusResponse> response) {
                UserStatusResponse userStatusResponse = response.body();
                userActive = true;
                if (userStatusResponse != null && userStatusResponse.getState() == UserStatus.UNAVAILABLE.ordinal()) {
                    userActive = false;
                }

                updateUserStatus();
            }

            @Override
            public void onFailure(Call<UserStatusResponse> call, Throwable t) {
                System.out.print("Ha ocurrido un error al recuperar el viaje.");
            }
        });


        String notificationTripId = getIntent().getStringExtra("notificationTripId");
        if (notificationTripId != null) {
            if (!popupOpen) {
                initNewTrip(notificationTripId, getIntent().getStringExtra("currentDateTime"));
            }
        } else {
            Call<Trip> call = userService.getLastTrip();
            call.enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    Trip trip = response.body();
                    if (trip != null && trip.getId() != null &&
                            (trip.getStatus() == TripStatus.DRIVER_GOING_ORIGIN.ordinal() ||
                                    trip.getStatus() == TripStatus.IN_TRAVEL.ordinal() ||
                                    trip.getStatus() == TripStatus.ARRIVED_DESTINATION.ordinal())) {
                        beginTrip(trip);
                    }
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    System.out.print("Ha ocurrido un error al recuperar el viaje.");
                }
            });
        }
    }


    private void initNewTrip(String tripId, String currentDateTime) {
        TripService tripService = new TripService(mActivity);
        Call<Trip> call = tripService.get(tripId);
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                Trip trip = response.body();
                if (trip != null && trip.getId() != null &&
                        trip.getStatus() != TripStatus.CLIENT_ACCEPTED.ordinal()) {
                    View view = mActivity.findViewById(R.id.layoutMap);
                    openPopupNewTripDriver(view, trip, currentDateTime);

                } else {
                    showMessage("El viaje ha caducado.");

                }

            }

            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                Log.v("TRIP", t.getMessage());
                showMessage("El viaje ha caducado.");
            }
        });


    }


    private void openPopupNewTripDriver(View v, Trip trip, String currentDateTime) {
        secondsPopup = 40;
        if (currentDateTime != null) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date dateNotification = dateFormat.parse(currentDateTime);
                Date currentDate = new Date();
                long diff = currentDate.getTime() - dateNotification.getTime();
                int diffSeconds = (int) (diff / 1000 % 60);
                secondsPopup = 40 - diffSeconds;
                if (secondsPopup <= 0) {
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        popupOpen = true;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.new_trip_popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setAnimationStyle(R.style.popup_window_animation);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, -100);
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
                    if (popupOpen) {
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
            Call<Void> call = tripService.refuseDriverTrip(trip.getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    System.out.print(response.body());
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    System.out.print(t.getMessage());

                }
            });
            popupWindow.dismiss();
            popupOpen = false;
            showMessage("El viaje ha sido rechazado.");
        });
        buttonConfirm.setOnClickListener(view ->

        {
            TripService tripService = new TripService(mActivity);
            Call<Trip> call = tripService.confirmDriverTrip(trip.getId());
            ProgressDialog dialog = new ProgressDialog(HomeDriverActivity.this);
            dialog.setMessage("Espere un momento por favor");
            dialog.show();
            call.enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    dialog.dismiss();
                    popupWindow.dismiss();
                    Trip trip = response.body();
                    showMessage("El viaje ha sido confirmado.");
                    popupOpen = false;
                    beginTrip(trip);
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    dialog.dismiss();
                    popupWindow.dismiss();
                    popupOpen = false;
                    showMessage("Ha ocurrido un error al confirmar el viaje.");
                }
            });
        });
    }

    private void beginTrip(Trip trip) {
        Intent intentActiveTripDriver = new Intent(mActivity, ActiveTripDriverActivity.class);
        intentActiveTripDriver.putExtra("currentTrip", trip);
        intentActiveTripDriver.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        popupOpen = false;
        startActivity(intentActiveTripDriver);
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
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mapManager.setCurrentLocation(location);
                LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                if (mMap != null) {
                    UpdateUserPositionRequest updateUserPositionRequest = new UpdateUserPositionRequest(String.valueOf(currentPosition.latitude), String.valueOf(currentPosition.longitude));
                    UserService userService = new UserService(mActivity);
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

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private void updateUserStatus() {
        TextView status = findViewById(R.id.status);
        TextView labelStatus = findViewById(R.id.labelStatus);
        TextView labelButtonStatus = findViewById(R.id.labelButtonStatus);
        ImageView buttonStatus = findViewById(R.id.buttonStatus);
        if (userActive) {
            status.setText("Activo");
            status.setTextColor(getResources().getColor(R.color.colorSuccess));
            labelStatus.setText("En este estado puede recibir viajes");
            buttonStatus.setImageResource(R.drawable.icon_power_off);
            labelButtonStatus.setText("Ponerse como inactivo");
        } else {
            status.setText("Inactivo");
            status.setTextColor(getResources().getColor(R.color.colorAccent));
            labelStatus.setText("En este estado no puede recibir viajes");
            buttonStatus.setImageResource(R.drawable.icon_power_on);
            labelButtonStatus.setText("Ponerse como activo");
        }
    }

    public void toggleStatus(View view) {
        UserService userService = new UserService(this);
        String statusUser = String.valueOf(UserStatus.IDLE.ordinal());
        if (userActive) {
            statusUser = String.valueOf(UserStatus.UNAVAILABLE.ordinal());
        }
        UpdateUserStatusRequest updateUserStatusRequest = new UpdateUserStatusRequest(statusUser);
        Call<User> call = userService.updateStatusUser(updateUserStatusRequest);
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
                Log.d("UPDATE USER STATUS", t.getMessage());
            }
        });
        userActive = !userActive;
        updateUserStatus();


    }

}
