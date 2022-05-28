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
import com.sleticalboy.autotrack.ITrackable;
import com.sleticalboy.autotrack.PageInfo;
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

  private TrackSdk() {
    throw new AssertionError("Utility class can not be initialized");
  }

  public static void prepare() {
    if (sTrackHandler != null) {
      throw new UnsupportedOperationException("TrackSdk has already been prepared.");
    }
    final HandlerThread trackThread = new HandlerThread("TrackThread");
    trackThread.start();
    sTrackHandler = new Handler(trackThread.getLooper(), new TrackCallback());
  }

  @SuppressWarnings("UsedForASM")
  public static void autoTrack(Object obj) {
    sendTrackMessage(new SomeArgs(obj, NULL, NULL, NULL));
  }

  @SuppressWarnings("UsedForASM")
  public static void autoTrack(Object obj, MenuItem menuItem) {
    sendTrackMessage(new SomeArgs(obj/*Context*/, menuItem, NULL, NULL));
  }

  @SuppressWarnings("UsedForASM")
  public static void autoTrack(DialogInterface dialog, int which) {
    sendTrackMessage(new SomeArgs(dialog, which, NULL, NULL));
  }

  @SuppressWarnings("UsedForASM")
  public static void autoTrack(AdapterView<?> adapterView, View view, int position) {
    sendTrackMessage(new SomeArgs(adapterView, view, position, NULL));
  }

  @SuppressWarnings("UsedForASM")
  public static void autoTrack(DialogInterface dialog, int which, boolean isChecked) {
    sendTrackMessage(new SomeArgs(dialog, which, isChecked, NULL));
  }

  private static void sendTrackMessage(SomeArgs args) {
    if (sTrackHandler == null) {
      throw new IllegalThreadStateException("You must call TrackSdk.prepare() first.");
    }
    sTrackHandler.obtainMessage(MSG_TRACK, args).sendToTarget();
  }

  private static void trackInternal(Object obj, Object arg1, Object arg2, Object arg3) {
    if (arg1 == NULL && arg2 == NULL && arg3 == NULL) {
      if (obj instanceof Activity) {
        onPageVisited((Activity) obj/*activity*/);
      } else if (obj instanceof View) {
        onViewClicked((View) obj/*view*/);
      }
    } else if (arg2 == NULL && arg3 == NULL) {
      if (obj instanceof DialogInterface && arg1 instanceof Integer) {
        onDialogButton$((DialogInterface) obj/*dialog*/, (Integer) arg1/*which*/);
      } else if (arg1 instanceof MenuItem) {
        onMenuItem$(ActivityHelper.findActivity(obj), (MenuItem) arg1);
      }
    } else if (arg3 == NULL) {
      if (obj instanceof AdapterView && arg1 instanceof View && arg2 instanceof Integer) {
        onAdapterViewItem$((AdapterView<?>) obj/*adapterVew*/, (View) arg1/*View*/,
          (Integer) arg2/*position*/);
      } else if (obj instanceof DialogInterface
        && arg1 instanceof Integer
        && arg2 instanceof Boolean) {
        onDialogItem$((DialogInterface) obj/*dialog*/, (Integer) arg1/*which*/,
          (Boolean) arg2/*isChecked*/);
      }
    }
  }

  private static void onAdapterViewItem$(AdapterView<?> parent, View view, int position) {
    final Object item = parent.getAdapter().getItem(position);
    final CharSequence text;
    if (item instanceof ITrackable) {
      text = ((ITrackable) item).format();
    } else {
      text = String.valueOf(item);
    }
    Log.d(TAG, "onAdapterViewItem$() parent = ["
      + parent
      + "], view = ["
      + view
      + "], position = ["
      + position
      + "], item = ["
      + text
      + ']');
  }

  private static void onDialogItem$(DialogInterface dialog, int which, boolean isChecked) {
    Object item = null;
    if (dialog instanceof AlertDialog) {
      item = ((AlertDialog) dialog).getListView().getAdapter().getItem(which);
    } else if (dialog instanceof android.app.AlertDialog) {
      item = ((android.app.AlertDialog) dialog).getListView().getAdapter().getItem(which);
    }
    final CharSequence text;
    if (item instanceof ITrackable) {
      text = ((ITrackable) item).format();
    } else {
      text = String.valueOf(item);
    }
    Log.d(TAG, "onDialogItem$() dialog = ["
      + dialog
      + "], which = ["
      + which
      + "], item = ["
      + text
      + "], isChecked = ["
      + isChecked
      + "]");
  }

  private static void onDialogButton$(DialogInterface dialog, int which) {
    CharSequence text = null;
    if (dialog instanceof AlertDialog) {
      text = ((AlertDialog) dialog).getButton(which).getText();
    } else if (dialog instanceof android.app.AlertDialog) {
      text = ((android.app.AlertDialog) dialog).getButton(which).getText();
    }
    Log.d(TAG, "onDialogButton$() dialog = ["
      + dialog
      + "], which = ["
      + which
      + "], text = ["
      + text
      + ']');
  }

  private static void onMenuItem$(Activity activity, MenuItem item) {
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

  private static void onViewClicked(View widget) {
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

  private static void onPageVisited(Activity activity) {
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

  private static void doTrack(@NonNull ITrackable trackable) {
    Log.d(TAG, "" + trackable.format());
  }

  private static final class TrackCallback implements Handler.Callback {

    @Override
    public boolean handleMessage(Message msg) {
      if (msg.what == MSG_TRACK && msg.obj instanceof SomeArgs) {
        final SomeArgs item = (SomeArgs) msg.obj;
        trackInternal(item.obj, item.arg1, item.arg2, item.arg3);
      }
      return true;
    }
  }

  private static final class SomeArgs {

    final Object obj;
    final Object arg1;
    final Object arg2;
    final Object arg3;

    SomeArgs(Object obj, Object arg1, Object arg2, Object arg3) {
      this.obj = obj;
      this.arg1 = arg1;
      this.arg2 = arg2;
      this.arg3 = arg3;
    }

    @Override
    public String toString() {
      return "SomeArgs{"
        + "obj="
        + obj
        + ", arg1="
        + arg1
        + ", arg2="
        + arg2
        + ", arg3="
        + arg3
        + '}';
    }
  }
}
