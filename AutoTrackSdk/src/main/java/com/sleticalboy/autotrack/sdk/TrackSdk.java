package com.sleticalboy.autotrack.sdk;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private static final Object NULL = new Object();
    private static final int MSG_TRACK = 1;
    private static Handler sTrackHandler;

    public static void prepare() {
        if (sTrackHandler != null) {
            throw new UnsupportedOperationException("TrackSdk.prepare() has already been prepared.");
        }
        final HandlerThread trackThread = new HandlerThread("TrackThread");
        trackThread.start();
        sTrackHandler = new Handler(trackThread.getLooper(), new TrackCallback());
    }

    @SuppressWarnings("UsedForASM")
    public static void autoTrack(Object obj) {
        sendTrackMessage(new WrappedItem(obj, NULL, NULL, NULL));
    }

    @SuppressWarnings("UsedForASM")
    public static void autoTrack(Object obj, MenuItem menuItem) {
        sendTrackMessage(new WrappedItem(obj, menuItem, NULL, NULL));
    }

    @SuppressWarnings("UsedForASM")
    public static void autoTrack(DialogInterface dialog, int which) {
        sendTrackMessage(new WrappedItem(dialog, which, NULL, NULL));
    }

    @SuppressWarnings("UsedForASM")
    public static void autoTrack(AdapterView adapterView, View view, int position) {
        sendTrackMessage(new WrappedItem(adapterView, view, position, NULL));
    }

    @SuppressWarnings("UsedForASM")
    public static void autoTrack(DialogInterface dialog, int which, boolean isChecked) {
        sendTrackMessage(new WrappedItem(dialog, which, isChecked, NULL));
    }

    private static void onClickMenuItem(Activity activity, MenuItem item) {
        final View view = item.getActionView();
        doTrack(new ClickInfo() {
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

    private static void onPageView(Activity activity) {
        doTrack(new PageInfo() {
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
        doTrack(new ClickInfo() {
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

    private static void sendTrackMessage(WrappedItem item) {
        if (sTrackHandler == null) {
            throw new IllegalThreadStateException("You must call TrackSdk.prepare() first.");
        }
        sTrackHandler.obtainMessage(MSG_TRACK, item).sendToTarget();
    }

    private static void doTrack(@NonNull Trackable trackable) {
        Log.d(TAG, "" + trackable.toJson());
    }

    private static void trackInternal(Object obj, Object arg1, Object arg2, Object arg3) {
        if (arg1 == NULL && arg2 == NULL && arg3 == NULL) {
            if (obj instanceof Activity) {
                onPageView((Activity) obj);
            } else if (obj instanceof View) {
                onClick(((View) obj));
            }
        } else if (arg2 == NULL && arg3 == NULL) {
            if (obj instanceof DialogInterface && arg1 instanceof Integer) {
                CharSequence text = null;
                if (obj instanceof AlertDialog) {
                    text = ((AlertDialog) obj).getButton((Integer) arg1).getText();
                } else if (obj instanceof android.app.AlertDialog) {
                    text = ((android.app.AlertDialog) obj).getButton((Integer) arg1).getText();
                }
                Log.d(TAG, "onClickDialogButton() called with: dialog = [" + obj + "], which = [" + arg1 + "], text = [" + text + ']');
            } else if (arg1 instanceof MenuItem) {
                onClickMenuItem(ActivityHelper.findActivity(obj), (MenuItem) arg1);
            }
        } else if (arg3 == NULL) {
            if (obj instanceof AdapterView && arg1 instanceof View && arg2 instanceof Integer) {
                final Object item = ((AdapterView) obj).getAdapter().getItem((Integer) arg2);
                Log.d(TAG, "onClickAdapterViewItem() called with: parent = [" + obj + "], view = [" + arg1 + "], position = [" + arg2 + "], item = [" + item + ']');
            } else if (obj instanceof DialogInterface && arg1 instanceof Integer && arg2 instanceof Boolean) {
                Object item = null;
                if (obj instanceof AlertDialog) {
                    item = ((AlertDialog) obj).getListView().getAdapter().getItem((Integer) arg1);
                } else if (obj instanceof android.app.AlertDialog) {
                    item = ((android.app.AlertDialog) obj).getListView().getAdapter().getItem((Integer) arg1);
                }
                Log.d(TAG, "onClickDialogItem() called with: dialog = [" + obj + "], which = [" + arg1 + "], item = [" + item + "], isChecked = [" + arg2 + "]");
            }
        }
    }

    private static class TrackCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_TRACK && msg.obj instanceof WrappedItem) {
                final WrappedItem item = (WrappedItem) msg.obj;
                trackInternal(item.obj, item.arg1, item.arg2, item.arg3);
            }
            return true;
        }
    }

    private static class WrappedItem {

        final Object obj;
        final Object arg1;
        final Object arg2;
        final Object arg3;

        WrappedItem(Object obj, Object arg1, Object arg2, Object arg3) {
            this.obj = obj;
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
        }
    }
}
