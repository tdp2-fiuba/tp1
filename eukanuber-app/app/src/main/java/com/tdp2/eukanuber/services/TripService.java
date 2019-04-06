package com.tdp2.eukanuber.services;

import com.tdp2.eukanuber.model.NewTripRequest;
import com.tdp2.eukanuber.model.Trip;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TripService {

    public Call<Trip> createTrip(NewTripRequest newTripRequest) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.createTrip(newTripRequest);
    }
}
