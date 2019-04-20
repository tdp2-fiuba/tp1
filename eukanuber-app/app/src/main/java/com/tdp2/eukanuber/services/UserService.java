package com.tdp2.eukanuber.services;

import com.tdp2.eukanuber.model.AssignDriverToTripRequest;
import com.tdp2.eukanuber.model.GetRouteRequest;
import com.tdp2.eukanuber.model.MapRoute;
import com.tdp2.eukanuber.model.NewTripRequest;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.UpdateStatusTripRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserService {

    public Call<User> updatePosition(NewTripRequest newTripRequest) {
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
    public Call<Trip> updateStatusTrip(String tripId, UpdateStatusTripRequest updateStatusTripRequest) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.updateStatusTrip(tripId, updateStatusTripRequest);
    }
    public Call<MapRoute> getRoutes(GetRouteRequest getRouteRequest) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.getRoute(getRouteRequest);
    }
}
