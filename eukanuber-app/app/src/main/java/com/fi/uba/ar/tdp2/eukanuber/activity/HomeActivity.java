package com.fi.uba.ar.tdp2.eukanuber.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.fi.uba.ar.tdp2.eukanuber.R;
import com.fi.uba.ar.tdp2.eukanuber.model.Post;
import com.fi.uba.ar.tdp2.eukanuber.service.PostService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends MenuActivity
        implements OnMapReadyCallback {
    private GoogleMap mMap;
    private final int PERMISSION_FINE_LOCATION = 1;
    private LocationListener locationListener;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.createMenu();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Intent intent = getIntent();
        String user = intent.getStringExtra("user");
        System.out.print("user is: " + user);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {

            }

            public void onProviderDisabled(String provider) {
            }
        };
        this.checkPermissionsLocation();
    }

    private void checkPermissionsLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_FINE_LOCATION);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    updateMapLocation();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Sin permisos necesarios para utilizar la aplicacion",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                return;
            }
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateMapLocation();
        getPosts();
    }

    private void updateMapLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentPosition).title("Marker in current position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
    }

    private void getPosts() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.rest_api_path))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PostService postService = retrofit.create(PostService.class);
        Call<List<Post>> call = postService.getPost();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                System.out.println(response.body());
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
            }
        });
    }

}
