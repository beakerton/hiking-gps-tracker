package net.herchenroether.hikinggps.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import net.herchenroether.hikinggps.utils.Logger;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

/**
 *
 *
 * Created by Adam Herchenroether on 11/5/2016.
 */
public class AppLocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private final long LOCATION_UPDATE_INTERVAL_MILLIS = 5000;

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;

    private static class SingletonHelper{
        private static final AppLocationManager INSTANCE = new AppLocationManager();
    }

    public static AppLocationManager getInstance(){
        return SingletonHelper.INSTANCE;
    }

    private AppLocationManager() {}

    /**
     * Connects to the GoogleApiClient and starts location updates
     *
     * @param context
     */
    public void connect(@NonNull Context context) {
        mContext = context;
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * Disconnects from the GoogleApiClient, and stops location tracking
     */
    public void disconnect() {
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (location != null) {
                Logger.info("Latitude = " + String.valueOf(location.getLatitude()));
                Logger.info("Longitude = " + String.valueOf(location.getLongitude()));
                Logger.info("Altitude = " + String.valueOf(location.getAltitude()));
                Logger.info("Accuracy = " + String.valueOf(location.getAccuracy()));
            }
            final LocationRequest locationRequest = new LocationRequest().setPriority(PRIORITY_HIGH_ACCURACY).setInterval(LOCATION_UPDATE_INTERVAL_MILLIS);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.info("Connection suspended, " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.error("Connection failed! " + connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        Logger.info("Latitude = " + String.valueOf(location.getLatitude()));
        Logger.info("Longitude = " + String.valueOf(location.getLongitude()));
        Logger.info("Altitude = " + String.valueOf(location.getAltitude()));
        Logger.info("Accuracy = " + String.valueOf(location.getAccuracy()));
    }
}