package com.tdp2.eukanuber.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.manager.AppSecurityManager;

import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class RegisterClientActivity extends BaseActivity {
    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client);
        mActivity = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registro");
        AccessToken accessTokenFacebook = AccessToken.getCurrentAccessToken();
        GraphRequest request = GraphRequest.newMeRequest(
                accessTokenFacebook,
                (object, response) -> {
                    try {
                        EditText name = mActivity.findViewById(R.id.inputRegisterName);
                        EditText lastname = mActivity.findViewById(R.id.inputRegisterLastname);
                        name.setText(object.getString("first_name"));
                        lastname.setText(object.getString("last_name"));
                        URL imageURL = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?type=large");
                        AsyncTask asyncTask = new AsyncTask() {
                            @Override
                            protected Object doInBackground(Object[] objects) {
                                try {
                                    Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                                    mActivity.runOnUiThread(() -> {
                                        CircularImageView profilePicture = mActivity.findViewById(R.id.profilePicture);
                                        profilePicture.setImageBitmap(bitmap);
                                    });
                                } catch (Exception ex) {
                                    System.out.print(ex.getMessage());
                                                      }

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object o) {
                            }
                        };
                        asyncTask.execute();
                    } catch (Exception ex) {
                        System.out.print(ex.getMessage());
                    }

                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void submitRegisterClient(View view) {
    }

}
