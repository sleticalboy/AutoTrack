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
        if (appName == null && AutoTrack.sApp != null) {
            try {
                appVersion = AutoTrack.sApp.getPackageManager()
                        .getPackageInfo(AutoTrack.sApp.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                appVersion = "Unknown";
            }
        }
        if (appName == null && AutoTrack.sApp != null) {
            final int label = AutoTrack.sApp.getApplicationInfo().labelRes;
            appName = AutoTrack.sApp.getString(label);
        }
    }
}
