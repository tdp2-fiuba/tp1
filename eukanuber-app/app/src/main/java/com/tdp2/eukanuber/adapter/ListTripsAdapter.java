package com.tdp2.eukanuber.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.TrackingTripActivity;
import com.tdp2.eukanuber.activity.TripDetailActivity;
import com.tdp2.eukanuber.model.Trip;

import java.util.ArrayList;

public class ListTripsAdapter extends ArrayAdapter<Trip> {

    private ArrayList<Trip> trips;
    Context mContext;

    private static class ViewHolder {
        TextView txtName;
    }

    public ListTripsAdapter(Context context, ArrayList<Trip> data) {
        super(context, R.layout.list_item_trip_history, data);
        this.trips = data;
        this.mContext = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Trip trip = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;
        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.list_item_trip_history, parent, false);
        viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
        convertView.setTag(viewHolder);

        viewHolder.txtName.setText("Nuevo trip");
        convertView.setOnClickListener(v -> {
            Trip tripSelected = trip;
            tripSelected.getId();
            Intent intentTripDetail = new Intent(mContext, TripDetailActivity.class);
            intentTripDetail.putExtra("currentTrip", tripSelected);
            mContext.startActivity(intentTripDetail);
        });
        return convertView;
    }
}
