package com.fi.uba.ar.tdp2.eukanuber.provider;

import com.fi.uba.ar.tdp2.eukanuber.model.NewTripRequest;
import com.fi.uba.ar.tdp2.eukanuber.model.Trip;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TripProvider {
    String API_ROUTE = "/trips";

    @POST(API_ROUTE)
    Call<Trip> createTrip(@Body NewTripRequest request);
}
