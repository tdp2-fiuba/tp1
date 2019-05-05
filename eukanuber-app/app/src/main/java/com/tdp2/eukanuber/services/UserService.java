package com.tdp2.eukanuber.services;

import android.content.Context;

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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserService extends ClientService{

    public UserService(Context context) {
        super(context);
    }

    public Call<User> updatePositionUser(UpdateUserPositionRequest updateUserPositionRequest) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.updatePositionUser(updateUserPositionRequest);
    }

    public Call<UserPositionResponse> getPositionUser(String userId) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.getPositionUser(userId);
    }
    public Call<User> getUser() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.getUser();
    }

    public Call<LoginResponse> login(String fbId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.loginUser(fbId);
    }

    public Call<LoginResponse> register(UserRegisterRequest userRegisterRequest) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BackendService.API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BackendService postService = retrofit.create(BackendService.class);
        return postService.registerUser(userRegisterRequest);
    }
}
