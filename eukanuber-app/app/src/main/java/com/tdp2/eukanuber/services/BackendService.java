package com.tdp2.eukanuber.services;

import com.tdp2.eukanuber.model.AssignDriverToTripRequest;
import com.tdp2.eukanuber.model.GetRouteRequest;
import com.tdp2.eukanuber.model.LoginResponse;
import com.tdp2.eukanuber.model.MapRoute;
import com.tdp2.eukanuber.model.NewTripRequest;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.UpdateStatusTripRequest;
import com.tdp2.eukanuber.model.UpdateUserPositionRequest;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.model.UserPositionResponse;
import com.tdp2.eukanuber.model.UserRegisterRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BackendService {

    //String API_PATH = "http://192.168.0.27:3000/";
    //String API_PATH = "http://10.0.2.2:3000/";
    String API_PATH = "https://eukanuber-backend.herokuapp.com/";

    @GET("trips")
    Call<List<Trip>> getAll(@Query("status") String status);

    @GET("trips/{tripId}")
    Call<Trip> get(@Path("tripId") String tripId);

    // Trips
    @POST("trips")
    Call<Trip> createTrip(@Body NewTripRequest request);

    @PUT("trips/{tripId}")
    Call<Trip> assignDriverToTrip(@Path("tripId") String tripId, @Body AssignDriverToTripRequest assignDriverToTripRequest);

    @PUT("trips/{tripId}")
    Call<Trip> updateStatusTrip(@Path("tripId") String tripId, @Body UpdateStatusTripRequest updateStatusTripRequest);

    @POST("trips/routes")
    Call<MapRoute> getRoute(@Body GetRouteRequest getRouteRequest);

    // Users
    @PUT("users/position")
    Call<User> updatePositionUser(@Body UpdateUserPositionRequest updateUserPositionRequest);

    @GET("users/position/{userId}")
    Call<UserPositionResponse> getPositionUser(@Path("userId") String userId);

    @GET("users/")
    Call<User> getUser();

    @POST("users/login/{fbId}")
    Call<LoginResponse> loginUser(@Path("fbId") String fbId);

    @POST("users/register")
    Call<LoginResponse> registerUser(@Body UserRegisterRequest userRegisterRequest);

    @POST("users/logout")
    Call<Void> logoutUser();

}
