package com.tdp2.eukanuber.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.manager.AppSecurityManager;
import com.tdp2.eukanuber.model.LoginResponse;
import com.tdp2.eukanuber.model.NewTripRequest;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.services.TripService;
import com.tdp2.eukanuber.services.UserService;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.HTTP;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    Activity mLoginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginActivity = this;
        SharedPreferences settings = getSharedPreferences(AppSecurityManager.USER_SECURITY_SETTINGS, 0);
        if (AppSecurityManager.isUserLogged(settings)) {
            User user = AppSecurityManager.getUserLogged(settings);

            if (user.getUserType().equals(User.USER_TYPE_DRIVER)) {
                Intent intent = new Intent(mLoginActivity, HomeDriverActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(mLoginActivity, HomeClientActivity.class);
                startActivity(intent);
            }
            return;
        }

        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email, user_friends");
        loginButton.setVisibility(View.GONE);
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessTokenFacebook = AccessToken.getCurrentAccessToken();
                appLoginAction(accessTokenFacebook.getToken(), accessTokenFacebook.getUserId());
            }

            @Override
            public void onCancel() {
                showMessage("El login con Facebook ha sido cancelado");
            }

            @Override
            public void onError(FacebookException exception) {
                showMessage("Ha ocurrido un error con el login con Facebook");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void completeLoginAction(View view) {
        LoginButton loginButton = findViewById(R.id.login_button);

       AccessToken accessTokenFacebook = AccessToken.getCurrentAccessToken();
        boolean isLoggedInFacebook = accessTokenFacebook != null && !accessTokenFacebook.isExpired();
        if (isLoggedInFacebook) {
            LoginManager.getInstance().logOut();
        }
        loginButton.performClick();
    }

    private void appLoginAction(String fbTokenKey, String fbUserId) {
        SharedPreferences settings = getSharedPreferences(AppSecurityManager.USER_SECURITY_SETTINGS, 0);

        UserService userService = new UserService(this);

        Call<LoginResponse> call = userService.login(fbUserId);
        ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Espere un momento por favor");
        dialog.show();
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                dialog.dismiss();
                if (response.code() == HttpURLConnection.HTTP_CONFLICT) {
                    Intent intent = new Intent(mLoginActivity, RegisterSelectTypeActivity.class);
                    startActivity(intent);
                    return;
                }
                LoginResponse loginResponse = response.body();
                AppSecurityManager.login(settings, fbTokenKey, fbUserId, loginResponse.getToken(), loginResponse.getUser());
                if (loginResponse.getUser().getUserType().equals(User.USER_TYPE_DRIVER)) {
                    Intent intent = new Intent(mLoginActivity, HomeDriverActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(mLoginActivity, HomeClientActivity.class);
                    startActivity(intent);
                }
                return;
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                dialog.dismiss();
                Log.v("Login Error", t.getMessage());
                showMessage("Ha ocurrido un error. Intente luego.");
            }
        });

    }


    public void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
