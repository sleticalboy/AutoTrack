package com.sleticalboy.transform;

import android.content.Context;
import android.widget.Toast;

/**
 * Created on 19-5-15.
 *
 * @author leebin
 */
public final class ToastUtils {

    private ToastUtils() {
        throw new AssertionError();
    }

    public static void toast(Context context, CharSequence text) {
        toastInternal(context, text, Toast.LENGTH_SHORT);
    }

    public static void toastLong(Context context, CharSequence text) {
        toastInternal(context, text, Toast.LENGTH_LONG);
    }

    private static void toastInternal(Context context, CharSequence text, int duration) {
        Toast.makeText(context, "" + text, duration).show();
    }
}
