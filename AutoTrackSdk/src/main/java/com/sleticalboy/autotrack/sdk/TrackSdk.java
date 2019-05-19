package com.sleticalboy.autotrack.sdk;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.sleticalboy.autotrack.ClickInfo;
import com.sleticalboy.autotrack.PageInfo;
import com.sleticalboy.autotrack.Trackable;
import com.sleticalboy.autotrack.helper.ActivityHelper;
import com.sleticalboy.autotrack.helper.ResHelper;
import com.sleticalboy.autotrack.helper.ViewHelper;

/**
 * Created on 19-5-11.
 *
 * @author leebin
 */
public final class TrackSdk {

    private static final String TAG = "TrackSdk";

    @SuppressWarnings("UsedForASM")
    public static void autoTrack(Object obj) {
        if (obj instanceof Activity) {
            onPageView((Activity) obj);
        } else if (obj instanceof View) {
            onClick(((View) obj));
        }
    }

    @SuppressWarnings("UsedForASM")
    public static void autoTrack(Object obj, MenuItem arg1) {
        onClickMenuItem(ActivityHelper.findActivity(obj), arg1);
    }

    @SuppressWarnings("UsedForASM")
    public static void autoTrack(AdapterView parent, View view, int position) {
        final Object item = parent.getAdapter().getItem(position);
        Log.d(TAG, "onClickAdapterViewItem() called with: parent = [" + parent + "], view = [" + view + "], position = [" + position + "], item = [" + item + ']');
    }

    @SuppressWarnings("UsedForASM")
    public static void autoTrack(DialogInterface di, int which) {
        CharSequence text = null;
        if (di instanceof AlertDialog) {
            text = ((AlertDialog) di).getButton(which).getText();
        } else if (di instanceof android.app.AlertDialog) {
            text = ((android.app.AlertDialog) di).getButton(which).getText();
        }
        Log.d(TAG, "onClickDialogButton() called with: dialog = [" + di + "], which = [" + which + "], text = [" + text + ']');
    }

    @SuppressWarnings("UsedForASM")
    public static void autoTrack(DialogInterface di, int which, boolean isChecked) {
        Object item = null;
        if (di instanceof AlertDialog) {
            final AlertDialog dialog = (AlertDialog) di;
            item = dialog.getListView().getAdapter().getItem(which);
        } else if (di instanceof android.app.AlertDialog) {
            final android.app.AlertDialog dialog = (android.app.AlertDialog) di;
            item = dialog.getListView().getAdapter().getItem(which);
        }
        Log.d(TAG, "onClickDialogItem() called with: dialog = [" + di + "], which = [" + which + "], item = [" + item + "], isChecked = [" + isChecked + "]");
    }

    private static void onClickMenuItem(Activity activity, MenuItem item) {
        final View view = item.getActionView();
        trackInner(new ClickInfo() {
            @Override
            public CharSequence desc() {
                if (view != null) {
                    return ViewHelper.getWidgetDesc(view) + ", title: " + item.getTitle();
                } else {
                    return item.getClass().getName() + ", title: " + item.getTitle();
                }
            }

            @Override
            public CharSequence path() {
                if (view == null) {
                    return "title: " + item.getTitle() + ", id: " + ResHelper.getEntryName(item.getItemId());
                } else {
                    return ViewHelper.findViewPath(view);
                }
            }
        });
    }

    private static void onCheckedChange(CompoundButton button) {
        onClick(button);
    }

    private static void onPageView(Activity activity) {
        trackInner(new PageInfo() {
            @NonNull
            @Override
            public CharSequence title() {
                return ActivityHelper.getTitle(activity);
            }

            @Override
            public CharSequence path() {
                return activity.getClass().getName();
            }
        });
    }

    private static void onClick(View widget) {
        trackInner(new ClickInfo() {
            @Override
            public CharSequence desc() {
                // 类名 + 文字(如果有)
                return ViewHelper.getWidgetDesc(widget);
            }

            @Override
            public CharSequence path() {
                return ViewHelper.findViewPath(widget);
            }
        });
    }

    private static void trackInner(@NonNull Trackable trackable) {
        Log.d(TAG, "" + trackable);
    }
}
