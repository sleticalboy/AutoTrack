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

    public static void shortToast(Context context, CharSequence text) {
        Toast.makeText(context, "" + text, Toast.LENGTH_SHORT).show();
    }
}
