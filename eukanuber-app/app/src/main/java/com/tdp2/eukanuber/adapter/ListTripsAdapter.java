package com.tdp2.eukanuber.adapter;

import android.app.Activity;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.model.Trip;

import java.util.ArrayList;
import java.util.List;

public class ListTripsAdapter extends ArrayAdapter<Trip> implements View.OnClickListener{

    private ArrayList<Trip> trips;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;
        TextView txtVersion;
    }

    public ListTripsAdapter(Context context, ArrayList<Trip> data) {
        super(context, R.layout.list_item_trip_history, data);
        this.trips = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Trip trip=(Trip)object;

        Snackbar.make(v, "Release date ", Snackbar.LENGTH_LONG)
                .setAction("No action", null).show();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Trip trip = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_trip_history, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            viewHolder.txtVersion = (TextView) convertView.findViewById(R.id.version_heading);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText("sarsa");
        viewHolder.txtType.setText("sarsa");
        viewHolder.txtVersion.setText("sarsa");
        // Return the completed view to render on screen
        return convertView;
    }
}
