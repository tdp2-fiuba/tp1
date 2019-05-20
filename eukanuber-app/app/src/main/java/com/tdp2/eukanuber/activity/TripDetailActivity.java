package com.tdp2.eukanuber.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.interfaces.ShowMessageInterface;
import com.tdp2.eukanuber.adapter.ListTripsAdapter;
import com.tdp2.eukanuber.manager.MapManager;
import com.tdp2.eukanuber.model.MapRoute;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.TripStatus;
import com.tdp2.eukanuber.model.UpdateStatusTripRequest;
import com.tdp2.eukanuber.model.User;
import com.tdp2.eukanuber.services.TripService;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripDetailActivity extends SecureActivity implements OnMapReadyCallback, ShowMessageInterface {
    private Activity mActivity;
    private Trip currentTrip;
    private GoogleMap mMap;
    private MapManager mapManager;
    private final String PAYMENT_CASH = "cash";
    private final String PAYMENT_CARD = "card";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        mActivity = this;
        this.createMenu(userLogged);
        Intent intent = getIntent();
        currentTrip = (Trip) intent.getSerializableExtra("currentTrip");
        getSupportActionBar().setTitle("Detalle de viaje");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initTrip();
    }

    private void initTrip() {
        try {
            TextView dateView = findViewById(R.id.tripDate);
            TextView tripPet = findViewById(R.id.tripPets);
            TextView tripEscort = findViewById(R.id.tripEscort);
            TextView tripPrice = findViewById(R.id.tripPrice);
            TextView tripCar = findViewById(R.id.tripCar);
            TextView tripDistanceTime = findViewById(R.id.tripDistanceTime);
            TextView tripPaymentText = findViewById(R.id.tripPaymentText);
            ImageView tripPaymentIcon = findViewById(R.id.tripPaymentIcon);
            TextView tripFrom = findViewById(R.id.tripFrom);
            TextView tripTo = findViewById(R.id.tripTo);
            CircularImageView imageProfile = findViewById(R.id.profileImage);
            TextView tripUserType = findViewById(R.id.tripUserType);
            TextView tripUserName = findViewById(R.id.tripUserName);
            TextView tripClientScore = findViewById(R.id.tripClientScore);
            ImageView tripClientScoreIcon = findViewById(R.id.tripClientScoreIcon);
            TextView tripClientScoreComment = findViewById(R.id.tripClientScoreComment);
            TextView tripDriverScore = findViewById(R.id.tripDriverScore);
            ImageView tripDriverScoreIcon = findViewById(R.id.tripDriverScoreIcon);
            TextView tripDriverScoreComment = findViewById(R.id.tripDriverScoreComment);

            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = null;

            date = df1.parse(currentTrip.getCreatedDate());

            DateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            dateView.setText(df2.format(date));
            tripPet.setText(String.valueOf(currentTrip.getPets().size()));
            if (currentTrip.getEscort()) {
                tripEscort.setText("1");
            } else {
                tripEscort.setText("0");
            }
            tripPrice.setText(currentTrip.getPrice());
            String carData = currentTrip.getDriverDetail().getCar().getBrand() + " " + currentTrip.getDriverDetail().getCar().getModel();
            tripCar.setText(carData);
            String distanceTime = currentTrip.getDistance() + " - " + currentTrip.getDuration();
            tripDistanceTime.setText(distanceTime);
            if (currentTrip.getPayment().equals(PAYMENT_CASH)) {
                tripPaymentIcon.setImageResource(R.drawable.ic_cash_black);
                tripPaymentText.setText("Efectivo");
            } else {
                tripPaymentIcon.setImageResource(R.drawable.ic_credit_card_black);
                tripPaymentText.setText("Tarjeta");
            }
            tripFrom.setText(currentTrip.getOrigin());
            tripTo.setText(currentTrip.getDestination());

            User userToShow;
            String userType;
            Integer clientScore = 0;
            String clientScoreComment = "";
            Integer driverScore = 0;
            String driverScoreComment = "";
            if (userLogged.getUserType().equals(User.USER_TYPE_CLIENT)) {
                userToShow = currentTrip.getDriverDetail();
                userType = "Conductor";
            } else {
                userToShow = currentTrip.getClientDetail();
                userType = "Cliente";
            }
            if (currentTrip.getReviewToDriver() != null) {
                driverScore = currentTrip.getReviewToDriver().getStars();
                driverScoreComment = currentTrip.getReviewToDriver().getComment();
            }
            if(currentTrip.getReviewToClient() != null){
                clientScore = currentTrip.getReviewToClient().getStars();
                clientScoreComment = currentTrip.getReviewToClient().getComment();
            }
            String imageProfileB64 = userToShow.getImageByType(User.PROFILE_IMAGE_NAME);
            byte[] decodedString = Base64.decode(imageProfileB64, Base64.DEFAULT);
            Bitmap imageProfileBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageProfile.setImageBitmap(imageProfileBitmap);
            tripUserType.setText(userType);
            tripUserName.setText(userToShow.getFullName());
            if (clientScore > 0) {
                tripClientScore.setText(String.valueOf(clientScore));
             //   tripClientScoreComment.setText(clientScoreComment);
            }else{
                if(userLogged.getUserType().equals(User.USER_TYPE_DRIVER)){
                    tripClientScore.setText("Puntuar al Cliente");
                    tripClientScore.setTypeface(null, Typeface.BOLD);
                    tripClientScoreIcon.setVisibility(View.GONE);
                    tripClientScore.setOnClickListener(view -> {
                        Intent intent = new Intent(mActivity, FeedbackActivity.class);
                        intent.putExtra("currentTrip", currentTrip);
                        intent.putExtra("fromDetailTrip", true);
                        startActivity(intent);
                    });
                }else{
                    tripClientScore.setText("No ha sido calificado por el Conductor" );
                    tripClientScoreIcon.setVisibility(View.GONE);
                }

            }
            if (driverScore > 0) {
                tripDriverScore.setText(String.valueOf(driverScore));
               // tripDriverScoreComment.setText(driverScoreComment);
            }else{
                if(userLogged.getUserType().equals(User.USER_TYPE_CLIENT)){
                    tripDriverScore.setText("Puntuar al Conductor");
                    tripDriverScore.setTypeface(null, Typeface.BOLD);
                    tripDriverScoreIcon.setVisibility(View.GONE);
                    tripDriverScore.setOnClickListener(view -> {
                        Intent intent = new Intent(mActivity, FeedbackActivity.class);
                        intent.putExtra("currentTrip", currentTrip);
                        intent.putExtra("fromDetailTrip", true);
                        startActivity(intent);
                    });
                }else{
                    tripDriverScore.setText("No ha sido calificado por el Cliente" );
                    tripDriverScoreIcon.setVisibility(View.GONE);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapManager = new MapManager(mMap, this);
        drawSummaryPath();
        mMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    private void drawSummaryPath() {
        if (currentTrip != null) {
            MapRoute route = currentTrip.getRoutes().get(0);
            mapManager.drawPath(route.getOverviewPolyline());
            mapManager.zoomInstantPath(route.getOverviewPolyline());
        }

    }


    public void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}
