package com.tdp2.eukanuber.services;

import com.tdp2.eukanuber.model.AssignDriverToTripRequest;
import com.tdp2.eukanuber.model.GetRouteRequest;
import com.tdp2.eukanuber.model.MapRoute;
import com.tdp2.eukanuber.model.NewTripRequest;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.UpdateStatusTripRequest;
import com.tdp2.eukanuber.model.UpdateUserPositionRequest;
import com.tdp2.eukanuber.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BackendService {
    String API_PATH = "https://eukanuber-backend.herokuapp.com/";

    // Trips
    @POST("trips")
    Call<Trip> createTrip(@Body NewTripRequest request);

    @GET("trips/{tripId}")
    Call<Trip> get(@Path("tripId") String tripId);

    @PUT("trips/{tripId}")
    Call<Trip> assignDriverToTrip(@Path("tripId") String tripId, @Body AssignDriverToTripRequest assignDriverToTripRequest);

    @PUT("trips/{tripId}")
    Call<Trip> updateStatusTrip(@Path("tripId") String tripId, @Body UpdateStatusTripRequest updateStatusTripRequest);

    @GET("trips")
    Call<List<Trip>> getAll(@Query("status") String status);

    @POST("trips/routes")
    Call<MapRoute> getRoute(@Body GetRouteRequest getRouteRequest);

    //USERS
    @PUT("users/{userId}/position")
    Call<User> updatePositionUser(@Path("userId") String tripId, @Body UpdateUserPositionRequest updateUserPositionRequest);

    @GET("users/{userId}")
    Call<User> getUser(@Path("userId") String userId);
}
