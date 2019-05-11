package com.sleticalboy.autotrack.helper;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.view.View;
import android.view.Window;

/**
 * Created on 19-5-11.
 *
 * @author leebin
 */
public final class WindowHelper {

    private WindowHelper() {
        throw new AssertionError();
    }

    public static Window findWindow(Object obj) {
        if (obj instanceof Window) {
            return (Window) obj;
        } else if (obj instanceof Dialog) {
            return ((Dialog) obj).getWindow();
        } else if (obj instanceof Activity) {
            return ((Activity) obj).getWindow();
        } else if (obj instanceof DialogFragment) {
            return findWindow(((DialogFragment) obj).getDialog());
        } else if (obj instanceof androidx.fragment.app.DialogFragment) {
            return findWindow(((androidx.fragment.app.DialogFragment) obj).getDialog());
        } else if (obj instanceof View) {
            return findWindow(ActivityHelper.findActivity(obj));
        }
        return null;
    }

    public static View findDecorView(Object obj) {
        final Window win = findWindow(obj);
        if (win != null) {
            return win.getDecorView();
        }
        return null;
    }
}
