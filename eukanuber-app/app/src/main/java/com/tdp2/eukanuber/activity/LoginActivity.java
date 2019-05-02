package com.tdp2.eukanuber.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.tdp2.eukanuber.R;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                System.out.print("accessToken: " + accessToken.getToken());

            }

            @Override
            public void onCancel() {
                System.out.print("cancell");
            }

            @Override
            public void onError(FacebookException exception) {
                System.out.print("error: " + exception.toString());
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
  /*

    public void goHomeClient(View view) {
        Intent intent = new Intent(this, HomeClientActivity.class);
        SharedPreferences settings = getSharedPreferences("USER_INFO", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userType", "client");
        editor.commit();

        startActivity(intent);
    }

    public void goHomeDriver(View view) {
        Intent intent = new Intent(this, HomeDriverActivity.class);
        startActivity(intent);
    }*/
}
