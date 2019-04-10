package com.tdp2.eukanuber.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.interfaces.ShowMessageInterface;
import com.tdp2.eukanuber.adapter.PlaceAutocompleteAdapter;
import com.tdp2.eukanuber.model.NewTripRequest;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.services.TripService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewTripActivity extends MenuActivity implements
        PlaceAutocompleteAdapter.PlaceAutoCompleteInterface, ShowMessageInterface {
    private final String PAYMENT_CASH = "cash";
    private final String PAYMENT_CARD = "card";
    public static final String PREFS_NAME = "NewTrip";

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
                new LatLng(-34.338157, -57.942612)
        );
    }

    private void initFromInput() {
        RecyclerView mRecyclerView = findViewById(R.id.list_search_from);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(NewTripActivity.this);
        mRecyclerView.setLayoutManager(llm);

        EditText inputFrom = findViewById(R.id.input_search_from);
        ImageView inputFromClear = findViewById(R.id.input_search_from_clear);

        PlaceAutocompleteAdapter mAdapter = new PlaceAutocompleteAdapter(this, R.layout.view_placesearch, placesClient, rectangularBounds, inputFrom, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setCurrentLocation();
        inputFrom.addTextChangedListener(new TextWatcher() {
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
        TextView inputFromCurrentLocation = findViewById(R.id.input_search_from_current_location);
        inputFromCurrentLocation.setOnClickListener(v -> mAdapter.setCurrentLocation());
        inputFromClear.setOnClickListener(v -> inputFrom.setText(""));

    }

    private void initToInput() {
        RecyclerView mRecyclerView = findViewById(R.id.list_search_to);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(NewTripActivity.this);
        mRecyclerView.setLayoutManager(llm);

        EditText mSearchEditText = findViewById(R.id.input_search_to);
        PlaceAutocompleteAdapter mAdapter = new PlaceAutocompleteAdapter(this, R.layout.view_placesearch, placesClient, rectangularBounds, mSearchEditText, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
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
            if (rowPets == 1) {
                petRow1.setVisibility(View.VISIBLE);
                removePet.setVisibility(View.VISIBLE);
                rowPets++;
                return;
            }
            if (rowPets == 2) {
                petRow2.setVisibility(View.VISIBLE);
                removePet.setVisibility(View.VISIBLE);
                rowPets++;
                return;
            }
        });
        removePet.setOnClickListener(v -> {
            if (rowPets == 3) {
                petRow2.setVisibility(View.GONE);
                cleanAnimalBlock(2, R.id.button_pet_small_2, R.id.button_pet_medium_2, R.id.button_pet_big_2);
                rowPets--;
                return;
            }
            if (rowPets == 2) {
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
            pets.put(row, "L");
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

    private void initEscort() {
    }

    private void initPayment() {
        ImageButton paymentCash = findViewById(R.id.button_payment_cash);
        ImageButton paymentCard = findViewById(R.id.button_payment_card);
        paymentCash.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
        paymentCard.setBackgroundColor(getResources().getColor(R.color.colorLightSecondary));
        payment = PAYMENT_CASH;

        paymentCash.setOnClickListener(v -> {
            paymentCash.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
            paymentCard.setBackgroundColor(getResources().getColor(R.color.colorLightSecondary));
            payment = PAYMENT_CASH;
        });
        paymentCard.setOnClickListener(v -> {
            paymentCash.setBackgroundColor(getResources().getColor(R.color.colorLightSecondary));
            paymentCard.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
            payment = PAYMENT_CARD;
        });

    }

    private void initSubmit() {
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
            if(newTripRequest.getOrigin().isEmpty()){
                Toast.makeText(this, "Ingrese el origen",Toast.LENGTH_SHORT).show();
                return;
            }
            if(newTripRequest.getDestination().isEmpty()){
                Toast.makeText(this, "Ingrese el destino",Toast.LENGTH_SHORT).show();
                return;
            }
            if(newTripRequest.getOrigin().equals(newTripRequest.getDestination())){
                Toast.makeText(this, "Origen y destino no pueden ser iguales",Toast.LENGTH_SHORT).show();
                return;
            }
            TripService tripService = new TripService();
            Call<Trip> call = tripService.create(newTripRequest);
            ProgressDialog dialog = new ProgressDialog(NewTripActivity.this);
            dialog.setMessage("Espere un momento por favor");
            dialog.show();
            call.enqueue(new Callback<Trip>() {
                @Override
                public void onResponse(Call<Trip> call, Response<Trip> response) {
                    dialog.dismiss();
                    Trip trip = response.body();
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("newTripId", trip.getId());
                    editor.commit();
                    showMessage("El viaje ha sido solicitado.");
                    Intent intent = new Intent(NewTripActivity.this, HomeDriverActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<Trip> call, Throwable t) {
                    dialog.dismiss();
                    Log.v("TRIP", t.getMessage());
                    showMessage("Ha ocurrido un error al solicitar el viaje.");
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
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }

}
