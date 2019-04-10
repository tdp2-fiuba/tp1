package com.tdp2.eukanuber.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.tdp2.eukanuber.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void goHomeClient(View view) {
        Intent intent = new Intent(this, HomeClientActivity.class);
        /*SharedPreferences settings = getSharedPreferences("USER_INFO", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userType", "client");
        editor.commit();
        */
        startActivity(intent);
    }

    public void goHomeDriver(View view) {
        Intent intent = new Intent(this, HomeDriverActivity.class);
        startActivity(intent);
    }
}
