package com.tdp2.eukanuber.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.manager.AppSecurityManager;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    Activity mLoginActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginActivity = this;
        SharedPreferences settings = getSharedPreferences(AppSecurityManager.USER_SECURITY_SETTINGS, 0);
        if(AppSecurityManager.isUserLogged(settings)){
            Intent intent = new Intent(this, HomeClientActivity.class);
            startActivity(intent);
            return;
        }

        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
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
        if(!isLoggedInFacebook){
            loginButton.performClick();
            return;
        }
        appLoginAction(accessTokenFacebook.getToken(), accessTokenFacebook.getUserId());

    }

    private void appLoginAction(String fbTokenKey, String fbUserId) {
        SharedPreferences settings = getSharedPreferences(AppSecurityManager.USER_SECURITY_SETTINGS, 0);
        AppSecurityManager.login(settings, fbTokenKey, fbUserId, "APP_TOKEN");
        Intent intent = new Intent(mLoginActivity, RegisterDriverUserActivity.class);
        startActivity(intent);
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
