package com.sleticalboy.plugin.transform

import java.util.regex.Pattern

class TrackUtils {

    private static final String INFLATER = 'AppCompatViewInflater$DeclaredOnClickListener'
    private static final String ANDROIDX_INFLATER = 'androidx.appcompat.app.' + INFLATER
    private static final String SUPPORT_INFLATER = 'android.support.v7.app.' + INFLATER
    // BuildConfig.class R.class R$attr.class R2.class ...
    private static final Pattern R_BUILD_CONFIG = Pattern.compile("^(BuildConfig|R(2)?|R\\\$.*)\\.class")

    private static Set<String> sExcludes

    static {
        sExcludes = new HashSet<>()
        sExcludes.add('android.support.')
        sExcludes.add('androidx.')
        sExcludes.add('kotlin.')
        sExcludes.add('kotlinx.android.')
        sExcludes.add('org.intellij.')
        sExcludes.add('org.jetbrains.')
        sExcludes.add('com.google.android.')
        sExcludes.add('com.sleticalboy.autotrack.')
    }

    static boolean excludes(String name) {
        if (name == null) {
            return true
        }
        for (String exclude : sExcludes) {
            if (name.startsWith(exclude)) {
                if (name == ANDROIDX_INFLATER || name == SUPPORT_INFLATER) {
                    log('excludes: ' + false + ' ' + name)
                    return false
                }
                log('excludes: ' + true + ' ' + name)
                return true
            }
        }
        if (R_BUILD_CONFIG.matcher(name).matches()) {
            log('excludes: ' + true + ' ' + name)
            return true
        }
        log('excludes: ' + false + ' ' + name)
        return false
    }

    static void log(String msg) {
        println(msg)
    }

    static String toClassName(String path) {
        return path.replace(File.separator, ".").replace(".class", "")
    }
}