package com.fi.uba.ar.tdp2.eukanuber.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fi.uba.ar.tdp2.eukanuber.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void goHomeClient(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("user", "client");
        startActivity(intent);
    }
    public void goHomeDriver(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("user", "driver");
        startActivity(intent);
    }


}
