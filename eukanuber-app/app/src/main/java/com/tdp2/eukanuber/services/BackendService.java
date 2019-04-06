package com.tdp2.eukanuber.services;

import com.tdp2.eukanuber.model.NewTripRequest;
import com.tdp2.eukanuber.model.Trip;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BackendService {
    String API_PATH = "http://10.0.2.2:3000";

    // Trips
    @POST("trips")
    Call<Trip> createTrip(@Body NewTripRequest request);
}
