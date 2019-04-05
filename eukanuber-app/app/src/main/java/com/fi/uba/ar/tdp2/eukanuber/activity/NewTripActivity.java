package com.fi.uba.ar.tdp2.eukanuber.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.fi.uba.ar.tdp2.eukanuber.R;
import com.fi.uba.ar.tdp2.eukanuber.adapter.PlaceAutocompleteAdapter;
import com.fi.uba.ar.tdp2.eukanuber.model.NewTripRequest;
import com.fi.uba.ar.tdp2.eukanuber.model.Trip;
import com.fi.uba.ar.tdp2.eukanuber.provider.TripProvider;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewTripActivity extends MenuActivity implements PlaceAutocompleteAdapter.PlaceAutoCompleteInterface {

    private PlacesClient placesClient;
    private RectangularBounds rectangularBounds;
    private Map<Integer, String> pets = new HashMap<>();
    private Integer rowPets;
    private String payment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);
        this.createMenu();
        initPlacesApi();
        initFromInput();
        initToInput();
        initAnimals();
        initEscort();
        initPayment();
        initSubmit();
    }

    private void initPlacesApi() {
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
        rectangularBounds = RectangularBounds.newInstance(
                new LatLng(-34.409003, -58.753123),
                new LatLng(-34.338157, -57.942612));
    }

    private void initFromInput() {
        RecyclerView mRecyclerView = findViewById(R.id.list_search_from);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(NewTripActivity.this);
        mRecyclerView.setLayoutManager(llm);

        EditText mSearchEdittext = findViewById(R.id.input_search_from);
        PlaceAutocompleteAdapter mAdapter = new PlaceAutocompleteAdapter(this, R.layout.view_placesearch,
                placesClient, rectangularBounds, mSearchEdittext, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(mAdapter);
                        if (mRecyclerView.getVisibility() == View.GONE)
                            mRecyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                }
                mAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private void initToInput() {
        RecyclerView mRecyclerView = findViewById(R.id.list_search_to);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(NewTripActivity.this);
        mRecyclerView.setLayoutManager(llm);

        EditText mSearchEdittext = findViewById(R.id.input_search_to);
        PlaceAutocompleteAdapter mAdapter = new PlaceAutocompleteAdapter(this, R.layout.view_placesearch,
                placesClient, rectangularBounds, mSearchEdittext, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    if (mAdapter != null) {
                        mRecyclerView.setAdapter(mAdapter);
                        if (mRecyclerView.getVisibility() == View.GONE)
                            mRecyclerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                }
                mAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private void initAnimals() {
        initAnimalBlock(0, R.id.button_pet_small_0, R.id.button_pet_medium_0, R.id.button_pet_big_0);
        initAnimalBlock(1, R.id.button_pet_small_1, R.id.button_pet_medium_1, R.id.button_pet_big_1);
        initAnimalBlock(2, R.id.button_pet_small_2, R.id.button_pet_medium_2, R.id.button_pet_big_2);
        LinearLayout petRow0 = findViewById(R.id.pet_row_0);
        LinearLayout petRow1 = findViewById(R.id.pet_row_1);
        LinearLayout petRow2 = findViewById(R.id.pet_row_2);
        ImageView addPet = findViewById(R.id.button_add_pet);
        ImageView removePet = findViewById(R.id.button_remove_pet);
        petRow0.setVisibility(View.VISIBLE);
        petRow1.setVisibility(View.GONE);
        petRow2.setVisibility(View.GONE);
        rowPets = 1;
        pets.put(0, "S");
        addPet.setOnClickListener(v -> {
            if(rowPets == 1){
                petRow1.setVisibility(View.VISIBLE);
                removePet.setVisibility(View.VISIBLE);
                rowPets++;
                return;
            }
            if(rowPets == 2){
                petRow2.setVisibility(View.VISIBLE);
                rowPets++;
                return;
            }
        });
        removePet.setOnClickListener(v -> {
            if(rowPets == 3){
                petRow2.setVisibility(View.GONE);
                cleanAnimalBlock(2, R.id.button_pet_small_2, R.id.button_pet_medium_2, R.id.button_pet_big_2);
                rowPets--;
                return;
            }
            if(rowPets == 2){
                petRow1.setVisibility(View.GONE);
                removePet.setVisibility(View.GONE);
                cleanAnimalBlock(1, R.id.button_pet_small_1, R.id.button_pet_medium_1, R.id.button_pet_big_1);
                rowPets--;
                return;
            }
        });

    }
    private void initAnimalBlock(int row, int rSmall, int rMedium, int rBig) {
        ImageButton buttonPetSmall = findViewById(rSmall);
        ImageButton buttonPetMedium = findViewById(rMedium);
        ImageButton buttonPetBig = findViewById(rBig);
        buttonPetSmall.setOnClickListener(v -> {
            buttonPetSmall.setBackgroundColor(getResources().getColor(R.color.colorTertiary));
            buttonPetMedium.setBackgroundColor(getResources().getColor(R.color.colorLightTertiary));
            buttonPetBig.setBackgroundColor(getResources().getColor(R.color.colorLightTertiary));
            pets.put(row, "S");
        });
        buttonPetMedium.setOnClickListener(v -> {
            buttonPetSmall.setBackgroundColor(getResources().getColor(R.color.colorLightTertiary));
            buttonPetMedium.setBackgroundColor(getResources().getColor(R.color.colorTertiary));
            buttonPetBig.setBackgroundColor(getResources().getColor(R.color.colorLightTertiary));
            pets.put(row, "M");
        });
        buttonPetBig.setOnClickListener(v -> {
            buttonPetSmall.setBackgroundColor(getResources().getColor(R.color.colorLightTertiary));
            buttonPetMedium.setBackgroundColor(getResources().getColor(R.color.colorLightTertiary));
            buttonPetBig.setBackgroundColor(getResources().getColor(R.color.colorTertiary));
            pets.put(row, "B");
        });
    }
    private void cleanAnimalBlock(int row, int rSmall, int rMedium, int rBig) {
        ImageButton buttonPetSmall = findViewById(rSmall);
        ImageButton buttonPetMedium = findViewById(rMedium);
        ImageButton buttonPetBig = findViewById(rBig);
        buttonPetSmall.setBackgroundColor(getResources().getColor(R.color.colorTertiary));
        buttonPetMedium.setBackgroundColor(getResources().getColor(R.color.colorLightTertiary));
        buttonPetBig.setBackgroundColor(getResources().getColor(R.color.colorLightTertiary));
        pets.remove(row);
    }
    private void initEscort(){}
    private void initPayment(){
        ImageButton paymentCash = findViewById(R.id.button_payment_cash);
        ImageButton paymentCard = findViewById(R.id.button_payment_card);
        paymentCash.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        paymentCard.setBackgroundColor(getResources().getColor(R.color.colorLightSecondary));
        payment = "cash";

        paymentCash.setOnClickListener(v -> {
            paymentCash.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
            paymentCard.setBackgroundColor(getResources().getColor(R.color.colorLightSecondary));
            payment = "cash";
        });
        paymentCard.setOnClickListener(v -> {
            paymentCash.setBackgroundColor(getResources().getColor(R.color.colorLightSecondary));
            paymentCard.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
            payment = "card";
        });

    }
    private void initSubmit(){
        EditText inputFrom = findViewById(R.id.input_search_from);
        EditText inputTo = findViewById(R.id.input_search_to);
        Switch switchEscort = findViewById(R.id.switch_escort);
        Button buttonSubmit = findViewById(R.id.button_submit);
        buttonSubmit.setOnClickListener(v -> {
            NewTripRequest newTripRequest = new NewTripRequest();
            newTripRequest.setOrigin(inputFrom.getText().toString());
            newTripRequest.setDestination(inputTo.getText().toString());
            newTripRequest.setEscort(switchEscort.isChecked());
            newTripRequest.setPayment(payment);
            newTripRequest.setPets(pets.values());
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.rest_api_path))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            TripProvider postService = retrofit.create(TripProvider.class);
            Call<Trip> call = postService.createTrip(newTripRequest);
            call.enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    response.body();
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    Log.v("TRIP", t.getMessage());
                }
            });
        });

    }

    @Override
    public void onPlaceClick(ArrayList<PlaceAutocompleteAdapter.PlaceAutocomplete> mResultList, int position, EditText editTextInput, RecyclerView recyclerView) {
        if (mResultList != null) {
            try {
                final String description = String.valueOf(mResultList.get(position).description);
                editTextInput.setText(description);
                recyclerView.setVisibility(View.GONE);
            } catch (Exception e) {
            }
        }
    }

}