package net.herchenroether.hikinggps;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import net.herchenroether.hikinggps.activity.SettingsActivity;
import net.herchenroether.hikinggps.databinding.ActivityMainBinding;
import net.herchenroether.hikinggps.location.AppLocationManager;
import net.herchenroether.hikinggps.location.LocationUpdatedListener;
import net.herchenroether.hikinggps.location.LocationViewModel;
import net.herchenroether.hikinggps.utils.Logger;

import java.util.Locale;

import javax.inject.Inject;

/**
 * Entry activity to the application
 */
public class MainActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        OnMapReadyCallback,
        LocationUpdatedListener,
        PopupMenu.OnMenuItemClickListener {

    @Inject
    AppLocationManager mAppLocationManager;

    private boolean mInitialLocationSet;
    private GoogleMap mMap;
    private LocationViewModel mLocationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // dependency injection
        ((HikingApplication)getApplication()).getLocationComponent().inject(this);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        // Set up databinding
        final ActivityMainBinding mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mLocationViewModel = new LocationViewModel(this);
        mainBinding.locationInfo.setLocation(mLocationViewModel);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // When creating the activity, add a listener so we can center the map after initialization
        mAppLocationManager.addListener(this);
    }

    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Logger.info("Requesting location permissions");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return;
        }

        // We already have the permission, start location tracking
        startLocationTracking();
    }

    protected void onStop() {
        mAppLocationManager.removeListener(this);
        mAppLocationManager.removeListener(mLocationViewModel);
        mAppLocationManager.disconnect();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Logger.info("Location permission granted");
                    startLocationTracking();
                } else {
                    // TODO: have some sort of fallback if the user says no
                    Logger.warn("Location permission denied");
                }
                return;
            }
            default: {
                Logger.info(String.format(Locale.US, "Got unexpected requestCode %d for permissions request", requestCode));
            }
        }
    }

    /**
     * Shows the popup menu when tapped
     *
     * @param v the view that was tapped
     */
    public void showMenu(View v) {
        final PopupMenu popup = new PopupMenu(this, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_main);
        popup.show();
    }

    /**
     * Executes whatever option the user picked from the menu
     *
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return false;
        }
    }

    /**
     * Does everything necessary to start tracking location:
     * -Reads shared pref for imperial vs. metric system
     * -Adds listener for location updates
     * -Connects to google API for location updates
     */
    private void startLocationTracking() {
        final SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String imperialValue = getString(R.string.imperial_value);
        final boolean isImperial = appPreferences.getString("unit_system", imperialValue).equals(imperialValue);
        mLocationViewModel.setUnitSystem(isImperial);
        mAppLocationManager.addListener(mLocationViewModel);
        mAppLocationManager.connect();
    }

    /**
     * Called when the map fragment is done loading
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Logger.info("Map loaded");
        mMap = googleMap;
        mMap.setPadding(0,200,0,0);

        // Enable user location if we have the permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    /**
     * Center the map on the location whenever we get an update
     *
     * @param location - the new location
     */
    @Override
    public void onLocationUpdated(@NonNull Location location) {
        // TODO: Should deregister the listener after we set the initial location, but that
        // can't be done in this listener because of a ConcurrentModificationException
        // For now use a boolean so we don't keep resetting the map center point
        if (!mInitialLocationSet) {
            final CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
            mMap.moveCamera(update);
            mInitialLocationSet = true;
        }
    }
}
