package com.tdp2.eukanuber.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.TrackingTripActivity;
import com.tdp2.eukanuber.activity.TripDetailActivity;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.model.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListTripsAdapter extends ArrayAdapter<Trip> {

    private ArrayList<Trip> trips;
    private Context mContext;
    private User userLogged;

    private static class ViewHolder {
        CircularImageView imageProfile;
        TextView price;
        TextView userName;
        TextView date;
        TextView pets;

    }

    public ListTripsAdapter(Context context, ArrayList<Trip> data, User userLogged) {
        super(context, R.layout.list_item_trip_history, data);
        this.trips = data;
        this.mContext = context;
        this.userLogged = userLogged;

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
        viewHolder.imageProfile = convertView.findViewById(R.id.profileImage);
        viewHolder.price = convertView.findViewById(R.id.price);
        viewHolder.userName = convertView.findViewById(R.id.userName);
        viewHolder.date = convertView.findViewById(R.id.date);
        viewHolder.pets = convertView.findViewById(R.id.pets);
        convertView.setTag(viewHolder);

        User userToShow;
        if (userLogged.getUserType().equals(User.USER_TYPE_CLIENT)) {
            userToShow = trip.getDriverDetail();
        } else {
            userToShow = trip.getClientDetail();
        }
        String imageProfileB64 = userToShow.getImageByType(User.PROFILE_IMAGE_NAME);
        byte[] decodedString = Base64.decode(imageProfileB64, Base64.DEFAULT);
        Bitmap imageProfile = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        String fullName = userToShow.getFullName();
        viewHolder.imageProfile.setImageBitmap(imageProfile);
        viewHolder.userName.setText(fullName);
        viewHolder.price.setText(trip.getPrice());

        try {
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = df1.parse(trip.getCreatedDate());
            DateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            viewHolder.date.setText(df2.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.pets.setText(String.valueOf(trip.getPets().size()));
        convertView.setOnClickListener(v -> {
            Intent intentTripDetail = new Intent(mContext, TripDetailActivity.class);
            intentTripDetail.putExtra("currentTrip", trip);
            mContext.startActivity(intentTripDetail);
        });
        return convertView;
    }
}
