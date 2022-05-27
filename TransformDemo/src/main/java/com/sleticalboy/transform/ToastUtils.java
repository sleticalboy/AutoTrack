package com.sleticalboy.transform;

import android.content.Context;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created on 19-5-15.
 * <br/>
 * toast 工具类，用于显示 toast
 *
 * @author leebin
 */
public final class ToastUtils {

    /** int 类型字段 */
    private int iField;
    /** String 类型字段 */
    private String sField;
    /** 泛型字段 */
    private static List<Map<String, Object>> sGenericField;

    private ToastUtils() {
        throw new AssertionError();
    }

    /**
     * toast 工具类
     *
     * @param unused 未使用
     */
    private ToastUtils(Void unused) {}

    /**
     * 显示 toast
     *
     * @param context 上下文
     * @param text 文本
     */
    public static void toast(Context context, CharSequence text) {
        toastInternal(context, text, Toast.LENGTH_SHORT);
    }

    /**
     * 显示长 toast
     *
     * @param context 上下文
     * @param text 文本
     */
    public static void toastLong(Context context, CharSequence text) {
        toastInternal(context, text, Toast.LENGTH_LONG);
    }

    private static void toastInternal(Context context, CharSequence text, int duration) {
        Toast.makeText(context, "" + text, duration).show();
    }

    /**
     * 测试方法
     *
     * @param c char 参数
     * @param i int 参数
     * @param s short 参数
     * @param b byte 参数
     * @param bool boolean 参数
     * @param f float 参数
     * @param d double 参数
     * @param l long 参数
     * @param sa string array 参数
     * @param ia int array 参数
     * @return {@link List}<{@link Map}<{@link String}, {@link Set}<{@link String}>>>
     */
    private static List<Map<String, Set<String>>> foo(char c, int i, short s, byte b, boolean bool,
      float f, double d, long l, String[] sa, int[] ia) {
        return null;
    }

    /**
     * 测试函数 2
     *
     * @return {@link List}<{@link String}>{@link []}
     */
    private static List<String>[] foo2() {
        return null;
    }

    /**
     * 测试函数 3
     *
     * @param unused 未使用
     * @return {@link int[]}
     */
    private static int[] foo3(Void unused) {
        return null;
    }

    /**
     * 测试函数 4
     *
     * @return {@link Void}
     */
    private static Void foo4() throws IOException {
        return null;
    }

    public interface InnerInterfaceFoo {}

    public static class InnerStaticClassFoo {}

    public enum InnerEnumFoo {}

    public class InnerClassFoo {}
}
