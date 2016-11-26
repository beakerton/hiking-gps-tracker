package net.herchenroether.hikinggps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import net.herchenroether.hikinggps.activity.SettingsActivity;
import net.herchenroether.hikinggps.databinding.ActivityMainBinding;
import net.herchenroether.hikinggps.location.AppLocationManager;
import net.herchenroether.hikinggps.location.LocationViewModel;
import net.herchenroether.hikinggps.utils.Logger;

import java.util.Locale;

import javax.inject.Inject;

/**
 * Entry activity to the application
 */
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    @Inject
    AppLocationManager mAppLocationManager;

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

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
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
        mAppLocationManager.addListener(mLocationViewModel);
    }

    protected void onStop() {
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
                    Logger.warn("Location permission denied");
                }
                return;
            }
            default: {
                Logger.info(String.format(Locale.US, "Got unexpected requestCode %d for permissions request", requestCode));
            }
        }
    }

    private void startLocationTracking() {
        mAppLocationManager.connect();
    }
}
