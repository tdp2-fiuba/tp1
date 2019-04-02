package com.fi.uba.ar.tdp2.eukanuber.activity;

import com.fi.uba.ar.tdp2.eukanuber.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import android.os.Bundle;

public class NewTripActivity extends MenuActivity {

    private PlacesClient placesClient;
    private AutocompleteSessionToken autocompleteSessionToken;
    private RectangularBounds rectangularBounds;
    private String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);
        this.createMenu();


        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        autocompleteSessionToken = AutocompleteSessionToken.newInstance();
        rectangularBounds = RectangularBounds.newInstance(
                new LatLng(  -34.409003, -58.753123),
                new LatLng(  -34.338157, -57.942612));
        country = "ar";
        String query = "3 de febrero 2306";

        getAutocompleteList(query).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                System.out.print(prediction.getFullText(null).toString());
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
            }
        });

    }

    private Task<FindAutocompletePredictionsResponse> getAutocompleteList(String query){
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(rectangularBounds)
                .setCountry(country)
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(autocompleteSessionToken)
                .setQuery(query)
                .build();

        return placesClient.findAutocompletePredictions(request);
    }
}
