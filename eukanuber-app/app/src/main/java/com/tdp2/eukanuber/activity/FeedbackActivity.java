package com.tdp2.eukanuber.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.manager.AppSecurityManager;
import com.tdp2.eukanuber.model.FeedbackRequest;
import com.tdp2.eukanuber.model.LoginResponse;
import com.tdp2.eukanuber.model.Review;
import com.tdp2.eukanuber.model.ReviewRequest;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.TripStatus;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.model.UserImage;
import com.tdp2.eukanuber.model.UserRegisterRequest;
import com.tdp2.eukanuber.services.TripService;
import com.tdp2.eukanuber.services.UserService;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends SecureActivity {
    Activity mActivity;
    Integer score;
    Trip currentTrip;
    User userToScore;
    Boolean fromDetailTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mActivity = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Feedback");
        Intent intent = getIntent();
        this.score = 0;
        currentTrip = (Trip) intent.getSerializableExtra("currentTrip");
        fromDetailTrip = intent.getBooleanExtra("fromDetailTrip", false);
        initTripData();
    }

    private void initTripData() {
        String userType;
        String commentHint;
        if (userLogged.getUserType().equals(User.USER_TYPE_CLIENT)) {
            userToScore = currentTrip.getDriverDetail();
            userType = "Conductor";
            commentHint = "Escriba comentarios sobre su experiencia con el conductor";
        } else {
            userToScore = currentTrip.getClientDetail();
            userType = "Cliente";
            commentHint = "Escriba comentarios sobre su experiencia con el cliente y sus mascotas";
        }
        String imageProfileB64 = userToScore.getImageByType(User.PROFILE_IMAGE_NAME);
        byte[] decodedString = Base64.decode(imageProfileB64, Base64.DEFAULT);
        Bitmap imageProfile = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        String fullName = userToScore.getFullName();

        TextView userTypeView = findViewById(R.id.userType);
        userTypeView.setText(userType);
        CircularImageView profilePicture = mActivity.findViewById(R.id.profilePicture);
        profilePicture.setImageBitmap(imageProfile);
        TextView nameView = findViewById(R.id.nameUser);
        nameView.setText(fullName);

        TextView tripTimeView = findViewById(R.id.tripTime);
        tripTimeView.setText(currentTrip.getDuration());
        TextView tripDistanceView = findViewById(R.id.tripDistance);
        tripDistanceView.setText(currentTrip.getDistance());

        EditText feedbackComment = findViewById(R.id.feedbackComment);
        feedbackComment.setHint(commentHint);
        feedbackComment.setImeOptions(EditorInfo.IME_ACTION_DONE);
        feedbackComment.setRawInputType(InputType.TYPE_CLASS_TEXT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void showMessage(String message) {
        Toast.makeText(
                mActivity,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

    public void checkScore(View view) {

        ImageView imageViewScore1 = findViewById(R.id.score1);
        ImageView imageViewScore2 = findViewById(R.id.score2);
        ImageView imageViewScore3 = findViewById(R.id.score3);
        ImageView imageViewScore4 = findViewById(R.id.score4);
        ImageView imageViewScore5 = findViewById(R.id.score5);
        switch (view.getId()) {
            case R.id.score1:
                this.score = 1;
                imageViewScore1.setImageResource(R.drawable.full_star);
                imageViewScore2.setImageResource(R.drawable.empty_star);
                imageViewScore3.setImageResource(R.drawable.empty_star);
                imageViewScore4.setImageResource(R.drawable.empty_star);
                imageViewScore5.setImageResource(R.drawable.empty_star);
                break;
            case R.id.score2:
                this.score = 2;
                imageViewScore1.setImageResource(R.drawable.full_star);
                imageViewScore2.setImageResource(R.drawable.full_star);
                imageViewScore3.setImageResource(R.drawable.empty_star);
                imageViewScore4.setImageResource(R.drawable.empty_star);
                imageViewScore5.setImageResource(R.drawable.empty_star);
                break;
            case R.id.score3:
                this.score = 3;
                imageViewScore1.setImageResource(R.drawable.full_star);
                imageViewScore2.setImageResource(R.drawable.full_star);
                imageViewScore3.setImageResource(R.drawable.full_star);
                imageViewScore4.setImageResource(R.drawable.empty_star);
                imageViewScore5.setImageResource(R.drawable.empty_star);
                break;
            case R.id.score4:
                this.score = 4;
                imageViewScore1.setImageResource(R.drawable.full_star);
                imageViewScore2.setImageResource(R.drawable.full_star);
                imageViewScore3.setImageResource(R.drawable.full_star);
                imageViewScore4.setImageResource(R.drawable.full_star);
                imageViewScore5.setImageResource(R.drawable.empty_star);
                break;
            case R.id.score5:
                this.score = 5;
                imageViewScore1.setImageResource(R.drawable.full_star);
                imageViewScore2.setImageResource(R.drawable.full_star);
                imageViewScore3.setImageResource(R.drawable.full_star);
                imageViewScore4.setImageResource(R.drawable.full_star);
                imageViewScore5.setImageResource(R.drawable.full_star);
                break;
        }

    }

    public void submitFeedback(View view) {
        EditText feedbackComment = findViewById(R.id.feedbackComment);
        String comment = feedbackComment.getText().toString();
        if (this.score == 0) {
            showMessage("Debe realizar una calificación");
            return;
        }
        UserService userService = new UserService(mActivity);
        FeedbackRequest feedbackRequest = new FeedbackRequest(
                userToScore.getId(),
                currentTrip.getId(),
                new ReviewRequest(this.score, comment));
        Call<Void> call = userService.sendFeedback(feedbackRequest);
        ProgressDialog dialog = new ProgressDialog(FeedbackActivity.this);
        dialog.setMessage("Espere un momento por favor");
        dialog.show();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(fromDetailTrip){
                    TripService tripService = new TripService(mActivity);
                    Call<Trip> callFullTrip = tripService.getFull(currentTrip.getId());
                    callFullTrip.enqueue(new Callback<Trip>() {
                        @Override
                        public void onResponse(Call<Trip> call, Response<Trip> response) {
                            dialog.dismiss();
                            currentTrip = response.body();
                            Intent intentTripDetail = new Intent(mActivity, TripDetailActivity.class);
                            intentTripDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intentTripDetail.putExtra("currentTrip", currentTrip);
                            startActivity(intentTripDetail);
                            return;
                        }
                        @Override
                        public void onFailure(Call<Trip> call, Throwable t) {
                            dialog.dismiss();
                            Log.v("TRIP", t.getMessage());
                        }
                    });

                }else{
                    dialog.dismiss();
                    if (userLogged.getUserType().equals(User.USER_TYPE_DRIVER)) {
                        Intent intent = new Intent(mActivity, HomeDriverActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        return;

                    } else {
                        Intent intent = new Intent(mActivity, HomeClientActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        return;
                    }
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                dialog.dismiss();
                Log.v("Feedback Error", t.getMessage());
                showMessage("Ha ocurrido un error. Intente luego.");
            }
        });

    }
}
