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

    private static Application sApp;

    private AutoTrack() {
        throw new AssertionError("Utility class can not be initialized");
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
        TrackSdk.prepare();
    }

    @NonNull
    public static Application sharedApp() {
        if (sApp == null) {
            throw new IllegalStateException("You must call AutoTrack.init() first.");
        }
        return sApp;
    }
}
