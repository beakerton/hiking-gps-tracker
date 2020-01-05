package net.herchenroether.hikinggps.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import net.herchenroether.hikinggps.utils.Logger;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

/**
 * Keeps the rest of the app informed about location changes
 *
 * Created by Adam Herchenroether on 11/5/2016.
 */
@Singleton
public class AppLocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final long LOCATION_UPDATE_INTERVAL_MILLIS = 5000;

    private final Set<LocationUpdatedListener> mListeners = new HashSet<>();

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private volatile Location mLocation;

    public AppLocationManager(@NonNull Context context) {
        mContext = context;
    }

    /**
     * Connects to the GoogleApiClient and starts location updates
     */
    public void connect() {
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
            Logger.info("AppLocationManager: Stopping location updates");
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Logger.info("AppLocationManager: Starting location updates");
            final LocationRequest locationRequest = new LocationRequest()
                    .setPriority(PRIORITY_HIGH_ACCURACY)
                    .setInterval(LOCATION_UPDATE_INTERVAL_MILLIS);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.info("FusedLocationApi connection suspended, " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.error("FusedLocationApi connection failed! " + connectionResult.getErrorMessage());
    }

    /**
     * Inform all listeners of the location change
     *
     * @param location - new location
     */
    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        synchronized(mListeners) {
            for (LocationUpdatedListener listener : mListeners) {
                listener.onLocationUpdated(mLocation);
            }
        }
    }

    /**
     * @param listener Adds the specified listener
     */
    public void addListener(@NonNull LocationUpdatedListener listener) {
        synchronized(mListeners) {
            if (!mListeners.contains(listener)) {
                mListeners.add(listener);
            }
        }
    }

    /**
     * @param listener Removes the specified listener
     */
    public void removeListener(@NonNull LocationUpdatedListener listener) {
        synchronized(mListeners) {
            if (mListeners.contains(listener)) {
                mListeners.remove(listener);
            }
        }
    }

    /**
     * @return the current location
     */
    @Nullable
    public Location getLocation() {
        return mLocation;
    }
}
