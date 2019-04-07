package com.tdp2.eukanuber.services;

import com.tdp2.eukanuber.model.NewTripRequest;
import com.tdp2.eukanuber.model.Trip;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BackendService {
    String API_PATH = "https://quiet-ravine-53171.herokuapp.com";

    // Trips
    @POST("trips")
    Call<Trip> createTrip(@Body NewTripRequest request);

    @GET("trips/{tripId}")
    Call<Trip> get(@Path("tripId") String tripId);
}
