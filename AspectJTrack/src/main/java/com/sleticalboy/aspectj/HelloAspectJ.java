package com.sleticalboy.aspectj;

import android.view.View;

/**
 * Created on 19-5-12.
 *
 * @author leebin
 */
public class HelloAspectJ {

    private HelloAspectJ() {
        throw new AssertionError();
    }

    public static String helloAspectJ(View view) {
        return "Hello AspectJ " + view.getClass().getSimpleName();
    }
}
