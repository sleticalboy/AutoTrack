package com.sleticalboy.autotrack;

import android.app.Activity;
import android.app.Application;
import androidx.annotation.NonNull;
import com.sleticalboy.autotrack.sdk.TrackSdk;

/**
 * Created on 19-5-8.
 *
 * @author leebin
 */
public final class AutoTrack {

    public static Application sApp;

    private AutoTrack() {
        throw new AssertionError();
    }

    public static void init(@NonNull Application app) {
        if (sApp == null) {
            sApp = app;
        }
        app.registerActivityLifecycleCallbacks(new LifecycleAdapter() {

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                TrackSdk.autoTrack(activity);
            }
        });
    }
}
