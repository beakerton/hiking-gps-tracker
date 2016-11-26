package net.herchenroether.hikinggps.dagger;

import android.app.Application;
import android.support.annotation.NonNull;

import net.herchenroether.hikinggps.location.AppLocationManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Provides the application context
 *
 * Created by Adam Herchenroether on 11/26/2016.
 */
@Module
public class AppModule {
    private Application mApplication;

    public AppModule(@NonNull Application application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApplication;
    }
}
