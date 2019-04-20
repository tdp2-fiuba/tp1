package com.tdp2.eukanuber.services;

import com.tdp2.eukanuber.model.AssignDriverToTripRequest;
import com.tdp2.eukanuber.model.NewTripRequest;
import com.tdp2.eukanuber.model.Trip;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TripService {

    public Call<Trip> create(NewTripRequest newTripRequest) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.createTrip(newTripRequest);
    }

    public Call<Trip> get(String tripId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.get(tripId);
    }
    public Call<List<Trip>> getAll(String status) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService getService = retrofit.create(BackendService.class);
        return getService.getAll(status);
    }
    public Call<Trip> assignDriverToTrip(String tripId, AssignDriverToTripRequest assignDriverToTripRequest) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.assignDriverToTrip(tripId, assignDriverToTripRequest);
    }

}
