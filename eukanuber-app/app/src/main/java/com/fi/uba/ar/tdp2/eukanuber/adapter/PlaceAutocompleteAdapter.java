package com.fi.uba.ar.tdp2.eukanuber.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fi.uba.ar.tdp2.eukanuber.R;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;

public class PlaceAutocompleteAdapter extends RecyclerView.Adapter<PlaceAutocompleteAdapter.PlaceViewHolder> implements Filterable {

    public interface PlaceAutoCompleteInterface {
        public void onPlaceClick(ArrayList<PlaceAutocomplete> mResultList, int position, EditText editText, RecyclerView recyclerView);
    }

    private String TAG = "PlaceAutocompleteAdapter";
    Context mContext;
    PlaceAutoCompleteInterface mListener;
    ArrayList<PlaceAutocomplete> mResultList;
    private PlacesClient mPlacesClient;
    private RectangularBounds mBounds;
    private int layout;
    private AutocompleteSessionToken autocompleteSessionToken;
    private EditText mEditText;
    private RecyclerView mRecyclerView;
    public PlaceAutocompleteAdapter(Context context, int resource, PlacesClient placesClient,
                                    RectangularBounds bounds, EditText editText, RecyclerView recyclerView) {
        this.mContext = context;
        layout = resource;
        mPlacesClient = placesClient;
        mBounds = bounds;
        this.mListener = (PlaceAutoCompleteInterface) mContext;
        autocompleteSessionToken = AutocompleteSessionToken.newInstance();
        mEditText= editText;
        mRecyclerView = recyclerView;

    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getAutocomplete(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    //notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private ArrayList<PlaceAutocomplete> getAutocomplete(CharSequence constraint) {
        ArrayList<PlaceAutocomplete> resultList = new ArrayList<>();

        try {
            FindAutocompletePredictionsResponse responseAutocomplete = getAutocompleteResponse(constraint.toString());
            for (AutocompletePrediction prediction : responseAutocomplete.getAutocompletePredictions()) {
                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                        prediction.getFullText(null)));
            }
        } catch (Exception ex) {
            Log.v(TAG, ex.getMessage());
        }
        return resultList;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(layout, viewGroup, false);
        PlaceViewHolder mPredictionHolder = new PlaceViewHolder(convertView);
        return mPredictionHolder;
    }


    @Override
    public void onBindViewHolder(PlaceViewHolder mPredictionHolder, final int i) {
        mPredictionHolder.mAddress.setText(mResultList.get(i).description);

        mPredictionHolder.mParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onPlaceClick(mResultList, i, mEditText, mRecyclerView);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mResultList != null)
            return mResultList.size();
        else
            return 0;
    }

    private FindAutocompletePredictionsResponse getAutocompleteResponse(String query) {
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(mBounds)
                .setCountry("ar")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(autocompleteSessionToken)
                .setQuery(query)
                .build();
        FindAutocompletePredictionsResponse response = null;
        try {
            response = Tasks.await(mPlacesClient.findAutocompletePredictions(request));
        } catch (Exception ex) {
            Log.v(TAG, ex.getMessage());
        }
        return response;
    }

    /*
    View Holder For Trip History
     */
    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        //        CardView mCardView;
        public RelativeLayout mParentLayout;
        public TextView mAddress;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            mParentLayout = itemView.findViewById(R.id.predictedRow);
            mAddress = itemView.findViewById(R.id.address);
        }

    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;

        PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }
}