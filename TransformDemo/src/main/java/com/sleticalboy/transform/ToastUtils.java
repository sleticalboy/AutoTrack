package com.sleticalboy.transform;

import android.content.Context;
import android.widget.Toast;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private static List<Map<String, Set<String>>> foo(char c, int i, short s, byte b, boolean bool,
      float f, double d, long l, String[] sa, int[] ia) {
        return null;
    }

    private static List<String>[] foo2() {
        return null;
    }

    private static int[] foo3(Void unused) {
        return null;
    }

    private static Void foo4() {
        return null;
    }
}
