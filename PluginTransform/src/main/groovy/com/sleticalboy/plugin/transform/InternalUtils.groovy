package com.sleticalboy.plugin.transform

class InternalUtils {

    static void log(String msg) {
        System.out.println(msg)
    }

    static String toClassName(String path) {
        return path.replace(File.separator, ".").replace(".class", "")
    }
}