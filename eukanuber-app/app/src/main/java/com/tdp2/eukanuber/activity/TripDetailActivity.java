package com.tdp2.eukanuber.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.adapter.ListTripsAdapter;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.services.TripService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripDetailActivity extends SecureActivity {
    Activity mActivity;
    Trip currentTrip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        mActivity = this;
        this.createMenu(userLogged);
        Intent intent = getIntent();
        currentTrip = (Trip) intent.getSerializableExtra("currentTrip");
        currentTrip.getId();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}
