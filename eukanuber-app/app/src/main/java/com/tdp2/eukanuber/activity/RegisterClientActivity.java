package com.tdp2.eukanuber.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.model.UserImage;
import com.tdp2.eukanuber.model.UserRegisterRequest;

import java.io.ByteArrayOutputStream;
import java.net.URL;

public class RegisterClientActivity extends BaseActivity {
    Activity mActivity;
    Bitmap bitmapImageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client);
        mActivity = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registro");
        AccessToken accessTokenFacebook = AccessToken.getCurrentAccessToken();
        bitmapImageProfile = BitmapFactory.decodeResource(getResources(),
                R.drawable.empty_profile);
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
                                    bitmapImageProfile = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                                    mActivity.runOnUiThread(() -> {
                                        CircularImageView profilePicture = mActivity.findViewById(R.id.profilePicture);
                                        profilePicture.setImageBitmap(bitmapImageProfile);
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
        EditText name = mActivity.findViewById(R.id.inputRegisterName);
        EditText lastname = mActivity.findViewById(R.id.inputRegisterLastname);
        if (name.getText().toString().isEmpty()) {
            showMessage("El nombre es obligatorio");
            return;
        }
        if (lastname.getText().toString().isEmpty()) {
            showMessage("El apellido es obligatorio");
            return;
        }
        if (bitmapImageProfile == null) {
            showMessage("La imagen de perfil es obligatoria");
            return;
        }
        AccessToken accessTokenFacebook = AccessToken.getCurrentAccessToken();
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setFbId(accessTokenFacebook.getUserId());
        userRegisterRequest.setFbAccessToken(accessTokenFacebook.getToken());
        userRegisterRequest.setFirstName(name.getText().toString());
        userRegisterRequest.setLastName(lastname.getText().toString());
        UserImage profileImage = new UserImage();
        profileImage.setFile(User.PROFILE_IMAGE_NAME);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapImageProfile.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String profileImageB64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        profileImage.setFileName(profileImageB64);
        userRegisterRequest.addImage(profileImage);

    }
    public void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}
