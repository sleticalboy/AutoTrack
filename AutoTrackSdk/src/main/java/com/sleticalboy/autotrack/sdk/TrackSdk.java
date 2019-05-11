package com.sleticalboy.autotrack.sdk;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import com.sleticalboy.autotrack.ClickInfo;
import com.sleticalboy.autotrack.PageInfo;
import com.sleticalboy.autotrack.Trackable;
import com.sleticalboy.autotrack.helper.ActivityHelper;
import com.sleticalboy.autotrack.helper.ViewHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 19-5-11.
 *
 * @author leebin
 */
public final class TrackSdk {

    private static final String TAG = "TrackSdk";

    private static List<String> sIgnoredPages = new ArrayList<>();
    private static List<Integer> sIgnoredWidgets = new ArrayList<>();

    public static void ignore(Object obj) {
        if (obj instanceof Activity) {
            final String name = obj.getClass().getName();
            if (!sIgnoredPages.contains(name)) {
                sIgnoredPages.add(name);
            }
        } else if (obj instanceof View) {
            final int hashCode = obj.hashCode();
            if (!sIgnoredWidgets.contains(hashCode)) {
                sIgnoredWidgets.add(hashCode);
            }
        }
    }

    public static void autoTrack(Object obj) {
        if (obj instanceof Activity) {
            pageView((Activity) obj);
        } else if (obj instanceof View) {
            widgetClick(((View) obj));
        }
    }

    private static void pageView(Activity activity) {
        if (activity == null || sIgnoredPages.contains(activity.getClass().getName())) {
            return;
        }
        trackInner(new PageInfo() {
            @NotNull
            @Override
            public CharSequence title() {
                final CharSequence title = ActivityHelper.getTitle(activity);
                return title == null ? path() : title;
            }

            @NotNull
            @Override
            public CharSequence path() {
                return activity.getClass().getName();
            }
        });
    }

    private static void widgetClick(View widget) {
        if (widget == null || sIgnoredWidgets.contains(widget.hashCode())) {
            return;
        }
        trackInner(new ClickInfo() {
            @NotNull
            @Override
            public String path() {
                return ViewHelper.findViewPath(widget);
            }

            @NotNull
            @Override
            public String desc() {
                // 类名 + 文字(如果有)
                return widget.getClass().getSimpleName();
            }
        });
    }

    private static void trackInner(@NonNull Trackable trackable) {
        Log.d(TAG, "" + trackable);
    }
}
