package net.herchenroether.hikinggps.location;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.location.Location;
import android.support.annotation.NonNull;

import net.herchenroether.hikinggps.R;

import java.util.Locale;

/**
 * Parses location data and creates strings to display it
 *
 * Created by Adam on 11/5/2016.
 */
public class LocationViewModel extends BaseObservable implements LocationUpdatedListener {
    private final Context mContext;
    private double mLatitude = 0;
    private double mLongitude = 0;
    private double mAltitude = 0;
    private float mAccuracy = 0;

    public LocationViewModel(@NonNull Context context) {
        mContext = context;
    }

    @Bindable
    public String getLatLongString() {
        return String.format(mContext.getString(R.string.lat_long_display), mLatitude, mLongitude);
    }

    @Bindable
    public String getAltitudeString() {
        return String.format(mContext.getString(R.string.elevation_display), mAltitude);
    }

    @Bindable
    public String getAccuracyString() {
        return String.format(mContext.getString(R.string.accuracy), mAccuracy);
    }

    @Override
    public void onLocationUpdated(@NonNull Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        mAltitude = location.getAltitude();
        mAccuracy = location.getAccuracy();
        notifyChange();
    }
}
