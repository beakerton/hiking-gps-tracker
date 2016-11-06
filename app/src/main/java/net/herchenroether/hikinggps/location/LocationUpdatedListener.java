package net.herchenroether.hikinggps.location;

import android.location.Location;

/**
 * Implement this interface to get informed about location updates
 *
 * Created by Adam Herchenroether on 11/5/2016.
 */
public interface LocationUpdatedListener {

    /**
     * Called when there is a new location
     *
     * @param location - the new location
     */
    public void onLocationUpdated(Location location);
}
