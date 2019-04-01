package com.fi.uba.ar.tdp2.eukanuber.activity;

import android.content.Intent;
import android.os.Bundle;

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
        System.out.print("user is: "+ user);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        getPosts();
    }

    private void getPosts() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.rest_api_path))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PostService postService = retrofit.create(PostService.class);
        Call< List<Post> > call = postService.getPost();

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
