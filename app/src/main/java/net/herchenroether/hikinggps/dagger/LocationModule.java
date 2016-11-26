package net.herchenroether.hikinggps.dagger;

import android.app.Application;
import android.support.annotation.NonNull;

import net.herchenroether.hikinggps.location.AppLocationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provides the AppLocationManager
 *
 * Created by Adam Herchenroether on 11/26/2016.
 */
@Module
public class LocationModule {
    @Provides
    @Singleton
    AppLocationManager providesAppLocationManager(@NonNull Application application) {
        return new AppLocationManager(application);
    }
}
