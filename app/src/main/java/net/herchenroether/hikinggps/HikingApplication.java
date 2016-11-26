package net.herchenroether.hikinggps;

import android.app.Application;

import net.herchenroether.hikinggps.dagger.AppModule;
import net.herchenroether.hikinggps.dagger.DaggerLocationComponent;
import net.herchenroether.hikinggps.dagger.LocationComponent;

/**
 * Creates a provides the LocationComponent
 *
 * Created by Adam Herchenroether on 11/26/2016.
 */
public class HikingApplication extends Application {

    private LocationComponent mLocationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mLocationComponent = DaggerLocationComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public LocationComponent getLocationComponent() {
        return mLocationComponent;
    }
}
