package com.tdp2.eukanuber.services;

import android.content.Context;

import com.tdp2.eukanuber.model.GetRouteRequest;
import com.tdp2.eukanuber.model.MapRoute;
import com.tdp2.eukanuber.model.NewTripRequest;
import com.tdp2.eukanuber.model.RefuseDriverTripRequest;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.UpdateStatusTripRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TripService extends ClientService {

    public TripService(Context context) {
        super(context);
    }

    public Call<Trip> create(NewTripRequest newTripRequest) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.createTrip(newTripRequest);
    }

    public Call<Trip> get(String tripId) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.get(tripId);
    }

    public Call<Trip> getFull(String tripId) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.getFull(tripId);
    }
    public Call<List<Trip>> getAll() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService getService = retrofit.create(BackendService.class);
        return getService.getAll();
    }
    public Call<List<Trip>> getAll(String status) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService getService = retrofit.create(BackendService.class);
        return getService.getAll(status);
    }

    public Call<Trip> updateStatusTrip(String tripId, UpdateStatusTripRequest updateStatusTripRequest) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.updateStatusTrip(tripId, updateStatusTripRequest);
    }
    public Call<MapRoute> getRoutes(GetRouteRequest getRouteRequest) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.getRoute(getRouteRequest);
    }


    public Call<Void> refuseDriverTrip(String tripId) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.refuseDriverTrip(tripId);
    }

    public Call<Trip> confirmDriverTrip(String tripId) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.confirmDriverTrip(tripId);
    }
}
