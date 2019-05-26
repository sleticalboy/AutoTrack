package com.sleticalboy.autotrack;

import android.app.Activity;
import android.app.Application;
import androidx.annotation.NonNull;
import com.sleticalboy.autotrack.sdk.TrackSdk;
import org.jetbrains.annotations.NotNull;

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
                // 进入一个页面 10 秒以上，我们才认为浏览了这个页面，否则不认为
                TrackSdk.autoTrack(activity/*Object*/);
            }

            @Override
            public void onActivityPaused(@NotNull Activity activity) {
                // 进入一个页面 10 秒以上，我们才认为浏览了这个页面，否则不认为
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
