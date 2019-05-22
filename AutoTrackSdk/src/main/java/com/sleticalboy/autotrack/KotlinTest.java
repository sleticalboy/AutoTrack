package com.sleticalboy.autotrack;

import android.content.pm.PackageManager;

/**
 * Created on 19-5-11.
 *
 * @author leebin
 */
public class KotlinTest {

    private static String appVersion;
    private static String appName;

    static {
        checkDeviceInfo();
    }

    public String getAppName() {
        return appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    private static void checkDeviceInfo() {
        if (appName == null && AutoTrack.sharedApp() != null) {
            try {
                appVersion = AutoTrack.sharedApp().getPackageManager()
                        .getPackageInfo(AutoTrack.sharedApp().getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                appVersion = "Unknown";
            }
        }
        if (appName == null && AutoTrack.sharedApp() != null) {
            final int label = AutoTrack.sharedApp().getApplicationInfo().labelRes;
            appName = AutoTrack.sharedApp().getString(label);
        }
    }
}
