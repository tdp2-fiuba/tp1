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
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.manager.AppSecurityManager;
import com.tdp2.eukanuber.model.LoginResponse;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.model.UserImage;
import com.tdp2.eukanuber.model.UserRegisterRequest;
import com.tdp2.eukanuber.services.UserService;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends BaseActivity {
    Activity mActivity;
    Bitmap bitmapImageProfile;
    Integer score;
    Trip currentTrip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        mActivity = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Feedback");
        Intent intent = getIntent();
        currentTrip = (Trip) intent.getSerializableExtra("currentTrip");

        bitmapImageProfile = BitmapFactory.decodeResource(getResources(),
                R.drawable.empty_profile);

        CircularImageView profilePicture = mActivity.findViewById(R.id.profilePicture);
        profilePicture.setImageBitmap(bitmapImageProfile);
        this.score = 0;
        EditText editText = findViewById(R.id.feedbackComment);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
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

    }
}
