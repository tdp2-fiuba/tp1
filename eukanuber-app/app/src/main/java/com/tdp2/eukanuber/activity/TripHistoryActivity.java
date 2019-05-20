package com.tdp2.eukanuber.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.adapter.ListTripsAdapter;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.TripStatus;
import com.tdp2.eukanuber.services.TripService;
import com.tdp2.eukanuber.services.UserService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripHistoryActivity extends SecureActivity {
    Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);
        mActivity = this;
        this.createMenu(userLogged);
        UserService userService = new UserService(mActivity);
        Call<List<Trip>> call = userService.getFinishedTrips();
        ProgressDialog dialog = new ProgressDialog(TripHistoryActivity.this);
        dialog.setMessage("Cargando los viajes. Espere un momento por favor.");
        dialog.show();

        call.enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Call<List<Trip>> call, Response<List<Trip>> response) {
                dialog.dismiss();
                List<Trip> trips = response.body();
                ListView list = findViewById(R.id.listTrips);
                ListTripsAdapter adapter= new ListTripsAdapter(mActivity, (ArrayList<Trip>) trips, userLogged);
                list.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Trip>> call, Throwable t) {
                dialog.dismiss();
                Log.v("TRIP", t.getMessage());
            }
        });

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
