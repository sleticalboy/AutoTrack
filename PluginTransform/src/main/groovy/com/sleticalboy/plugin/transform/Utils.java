package com.sleticalboy.plugin.transform;


import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * Created on 21-3-9.
 *
 * @author binlee sleticalboy@gmail.com
 */
public class Utils {

    private static final String INFLATER = "AppCompatViewInflater$DeclaredOnClickListener";
    private static final String ANDROIDX_INFLATER = "androidx.appcompat.app." + INFLATER;
    private static final String SUPPORT_INFLATER = "android.support.v7.app." + INFLATER;
    // BuildConfig.class R.class R$attr.class R2.class ...
    private static final Pattern R_BUILD_CONFIG = Pattern.compile("^(BuildConfig|R(2)?|R\\$.*)\\.class");

    private static final Set<String> EXCLUDES = new HashSet<>(Arrays.asList(
            "android.support.", "androidx.", "kotlin.", "kotlinx.android.", "org.intellij.",
            "org.jetbrains.", "com.google.android.", "com.sleticalboy.autotrack."
    ));

    private Utils() {
        //no instance
    }

    public static boolean excludes(String name) {
        if (name == null) return true;
        boolean result;
        for (String exclude : EXCLUDES) {
            if (name.startsWith(exclude)) continue;
            result = !name.equals(ANDROIDX_INFLATER) && !name.equals(SUPPORT_INFLATER);
            log("excludes: " + result + ' ' + name);
            return result;
        }
        result = R_BUILD_CONFIG.matcher(name).matches();
        log("excludes: " + result + ' ' + name);
        return false;
    }

    public static void log(String msg) {
        DefaultGroovyMethods.println(msg);
    }

    public static String toClassName(String path) {
        return path.replace(File.separator, ".").replace(".class", "");
    }
}
