package com.tdp2.eukanuber.services;

import android.content.Context;

import com.tdp2.eukanuber.model.FeedbackRequest;
import com.tdp2.eukanuber.model.FirebaseTokenRequest;
import com.tdp2.eukanuber.model.LoginResponse;
import com.tdp2.eukanuber.model.Rating;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.UpdateUserPositionRequest;
import com.tdp2.eukanuber.model.UpdateUserStatusRequest;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.model.UserPositionResponse;
import com.tdp2.eukanuber.model.UserRegisterRequest;
import com.tdp2.eukanuber.model.UserStatusResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserService extends ClientService {

    public UserService(Context context) {
        super(context);
    }

    public Call<User> updatePositionUser(UpdateUserPositionRequest updateUserPositionRequest) {
        Retrofit retrofit = buildClientSecured();
        BackendService postService = retrofit.create(BackendService.class);
        return postService.updatePositionUser(updateUserPositionRequest);

    }

    public Call<UserPositionResponse> getPositionUser(String userId) {
        Retrofit retrofit = buildClientSecured();
        BackendService postService = retrofit.create(BackendService.class);
        return postService.getPositionUser(userId);

    }

    public Call<User> getUser() {
        Retrofit retrofit = buildClientSecured();
        BackendService postService = retrofit.create(BackendService.class);
        return postService.getUser();

    }

    public Call<UserStatusResponse> getUserStatus(String userId) {
        Retrofit retrofit = buildClientSecured();
        BackendService postService = retrofit.create(BackendService.class);
        return postService.getUserStatus(userId);
    }




    public Call<LoginResponse> login(String fbId) {
        Retrofit retrofit = buildClient();
        BackendService postService = retrofit.create(BackendService.class);
        return postService.loginUser(fbId);

    }

    public Call<LoginResponse> register(UserRegisterRequest userRegisterRequest) {
        Retrofit retrofit = buildClient();
        BackendService postService = retrofit.create(BackendService.class);
        return postService.registerUser(userRegisterRequest);

    }
    public Call<User> updateStatusUser(UpdateUserStatusRequest updateUserStatusRequest) {
        Retrofit retrofit = buildClientSecured();
        BackendService postService = retrofit.create(BackendService.class);
        return postService.updateStatusUser(updateUserStatusRequest);

    }
    public Call<Void> logout() {
        Retrofit retrofit = buildClientSecured();
        BackendService postService = retrofit.create(BackendService.class);
        return postService.logoutUser();
    }

    public Call<Trip> getLastTrip() {
        Retrofit retrofit = buildClientSecured();
        BackendService getService = retrofit.create(BackendService.class);
        return getService.getLastTrip();
    }
    public Call<Trip> getPendingTrips() {
        Retrofit retrofit = buildClientSecured();
        BackendService getService = retrofit.create(BackendService.class);
        return getService.getPendingTrips();
    }

    public Call<Void> sendFeedback(FeedbackRequest feedbackRequest) {
        Retrofit retrofit = buildClientSecured();
        BackendService getService = retrofit.create(BackendService.class);
        return getService.sendFeedback(feedbackRequest);
    }

    public Call<List<Trip>> getFinishedTrips() {
        Retrofit retrofit = buildClientSecured();
        BackendService getService = retrofit.create(BackendService.class);
        return getService.getFinishedTrips();
    }

    public Call<Rating> getUserRating(String userId) {
        Retrofit retrofit = buildClientSecured();
        BackendService getService = retrofit.create(BackendService.class);
        return getService.getUserRating(userId);
    }

    public Call<Void> updateFirebaseToken(FirebaseTokenRequest firebaseTokenRequest) {
        Retrofit retrofit = buildClientSecured();
        BackendService getService = retrofit.create(BackendService.class);
        return getService.updateFirebaseToken(firebaseTokenRequest);
    }
}
