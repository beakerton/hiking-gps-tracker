package net.herchenroether.hikinggps.location;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import net.herchenroether.hikinggps.R;

/**
 * Parses location data and creates strings to display it
 *
 * Created by Adam Herchenroether on 11/5/2016.
 */
public class LocationViewModel extends BaseObservable implements LocationUpdatedListener {
    private static final double METERS_TO_FEET_CONVERSION = 3.28084;

    private final Context mContext;
    private final String mLatLongDisplay;
    private boolean mIsUsingImperial;

    private double mLatitude = 0;
    private double mLongitude = 0;
    private double mAltitude = 0;
    private float mAccuracy = 0;

    public LocationViewModel(@NonNull Context context) {
        mContext = context;
        mIsUsingImperial = false;
        mLatLongDisplay = mContext.getString(R.string.lat_long_display);
    }

    @Bindable
    public String getLatLongString() {
        return String.format(mLatLongDisplay, mLatitude, mLongitude);
    }

    @Bindable
    public String getAltitudeString() {
        final String elevationDisplay = mIsUsingImperial ?
                mContext.getString(R.string.elevation_display_imperial) :
                mContext.getString(R.string.elevation_display);
        final double altitude = mIsUsingImperial ?
                mAltitude * METERS_TO_FEET_CONVERSION :
                mAltitude;
        return String.format(elevationDisplay, altitude);
    }

    @Bindable
    public String getAccuracyString() {
        final String accuracyDisplay = mIsUsingImperial ?
                mContext.getString(R.string.accuracy_imperial) :
                mContext.getString(R.string.accuracy);
        final double accuracy = mIsUsingImperial ?
                mAccuracy * METERS_TO_FEET_CONVERSION :
                mAccuracy;
        return String.format(accuracyDisplay, accuracy);
    }

    @Override
    public void onLocationUpdated(@NonNull Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        mAltitude = location.getAltitude();
        mAccuracy = location.getAccuracy();
        notifyChange();
    }

    public void setUnitSystem(boolean isImperial) {
        mIsUsingImperial = isImperial;
    }
}
