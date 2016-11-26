package net.herchenroether.hikinggps.dagger;

import net.herchenroether.hikinggps.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Used to inject different parts of the app
 *
 * Created by Adam Herchenroether on 11/26/2016.
 */
@Singleton
@Component(modules={AppModule.class, LocationModule.class})
public interface LocationComponent {
    void inject(MainActivity activity);
}
