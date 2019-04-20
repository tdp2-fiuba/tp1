package com.tdp2.eukanuber.manager;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.tdp2.eukanuber.R;
import com.tdp2.eukanuber.activity.interfaces.ShowMessageInterface;
import com.tdp2.eukanuber.model.MapRoute;
import com.tdp2.eukanuber.model.MapRoutePolyline;
import com.tdp2.eukanuber.model.Trip;
import com.tdp2.eukanuber.services.TripService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapManager {
    public static final int PERMISSION_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private Context mContext;
    private Activity mActivity;
    private LocationManager mLocationManager;

    public MapManager(GoogleMap mMap, Context mContext) {
        this.mMap = mMap;
        this.mContext = mContext;
        this.mActivity = (Activity) mContext;
        this.mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        LatLngBounds bounds = new LatLngBounds(
                new LatLng(-34.594404, -58.424455),
                new LatLng(-34.586650, -58.375858));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 12));

        checkPermissionsLocation();
    }

    public void checkPermissionsLocation() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_FINE_LOCATION);
            return;
        }
    }

    public void setCurrentLocation() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMinZoomPreference(10.0f);
        mMap.setMaxZoomPreference(20.0f);
        Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(18)
                    .tilt(40).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
        }
    }

    public void moveCamera(LatLng position) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(18)
                .tilt(40).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
    }

    public Marker addMarkerCar(LatLng position) {
        return mMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(bitmapDescriptorFromVector(mActivity, R.drawable.ic_car_map))
        );
    }

    public void moveMarker(Marker marker, LatLng position) {
        marker.setPosition(position);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

    }

    public void setListener(LocationListener locationListener) {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    public void drawPath(MapRoutePolyline mapRoutePolyline) {


        try {
            List<LatLng> pointsPolyline = PolyUtil.decode(mapRoutePolyline.getPoints());
            if (pointsPolyline.size() > 0) {
                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(Color.rgb(100, 149, 237));
                polyOptions.width(20);
                polyOptions.addAll(pointsPolyline);
                PolylineOptions polyOptionsInside = new PolylineOptions();
                polyOptionsInside.color(Color.rgb(30, 144, 255));
                polyOptionsInside.width(16);
                polyOptionsInside.addAll(pointsPolyline);
                mMap.addPolyline(polyOptions);
                mMap.addPolyline(polyOptionsInside);
            }
        } catch (Exception ex) {
            Log.d("DRAW PATH", ex.getMessage());
        }

    }

    public void clearMap() {
        mMap.clear();
    }

    public void zoomToPath(MapRoutePolyline mapRoutePolyline) {
        int padding = 50;
        List<LatLng> pointsPolyline = PolyUtil.decode(mapRoutePolyline.getPoints());
        try {
            if (pointsPolyline.size() > 0) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : pointsPolyline) {
                    builder.include(latLng);
                }

                final LatLngBounds bounds = builder.build();

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
                mMap.animateCamera(cu);
            }
        } catch (Exception ex) {
            Log.d("ZOOM PATH", ex.getMessage());
        }


    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
