package com.fi.uba.ar.tdp2.eukanuber.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.fi.uba.ar.tdp2.eukanuber.R;
import com.fi.uba.ar.tdp2.eukanuber.adapter.PlaceAutocompleteAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;

public class NewTripActivity extends MenuActivity implements PlaceAutocompleteAdapter.PlaceAutoCompleteInterface {

    private PlacesClient placesClient;
    private RectangularBounds rectangularBounds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);
        this.createMenu();
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
        rectangularBounds = RectangularBounds.newInstance(
                new LatLng(-34.409003, -58.753123),
                new LatLng(-34.338157, -57.942612));
        initFromInput();
        initToInput();

    }

    private void initFromInput(){
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
    private void initToInput(){
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
