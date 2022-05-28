package com.sleticalboy.plugin.transform;

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
  private static final Pattern R_BUILD_CONFIG =
    Pattern.compile("^(BuildConfig|R(2)?|R\\$.*)\\.class");

  private static final Set<String> EXCLUDES = new HashSet<>(Arrays.asList(
    "android.support.", "androidx.", "kotlin.", "kotlinx.android.", "org.intellij.",
    "org.jetbrains.", "com.google.android.", "com.sleticalboy.autotrack."
  ));

  private Utils() {
    //no instance
  }

  public static boolean includes(String name) {
    if (name == null) return false;
    // R 文件编译出的 class，忽略
    if (R_BUILD_CONFIG.matcher(name).matches()) return false;
    // 两个 inflater 不能忽略，兼容在 xml 文件中声明的点击事件
    if (name.equals(ANDROIDX_INFLATER) || name.equals(SUPPORT_INFLATER)) {
      log("white list includes: " + name);
      return true;
    }
    // 黑名单中的全部忽略
    for (String exclude : EXCLUDES) {
      if (name.startsWith(exclude)) return false;
    }
    return true;
  }

  public static void log(String msg) {
    // DefaultGroovyMethods.println(msg);
    System.out.println(msg);
  }

  public static String toClassName(String path) {
    return path.replace(File.separator, ".").replace(".class", "");
  }
}
