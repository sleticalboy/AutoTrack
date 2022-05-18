package com.binlee.javadoc.extractor;

/**
 * Created on 2022-02-16.
 *
 * @author binlee
 */
public final class Log {

  // copy from com.binlee.util.Logger
  private static final String VERBOSE = "V/%s: %s\n";
  private static final String INFO = "\033[34mI/%s: %s\033[0m\n";
  private static final String DEBUG = "\033[36mD/%s: %s\033[0m\n";
  private static final String WARN = "\033[33mW/%s: %s\033[0m\n";
  private static final String ERROR = "\033[31mE/%s: %s\033[0m\n";

  public static void v(String tag, String msg) {
    printf(VERBOSE, tag, msg);
  }

  public static void i(String tag, String msg) {
    printf(INFO, tag, msg);
  }

  public static void d(String tag, String msg) {
    printf(DEBUG, tag, msg);
  }

  public static void w(String tag, String msg) {
    printf(WARN, tag, msg);
  }

  public static void e(String tag, String msg) {
    e(tag, msg, null);
  }

  public static void e(String tag, String msg, Throwable tr) {
    printf(ERROR, tag, msg);
    if (tr == null) return;
    for (StackTraceElement ste : tr.getStackTrace()) {
      printf(ERROR, tag, ste.toString());
    }
  }

  private static void printf(String format, Object... args) {
    System.out.printf(format, args);
  }
}
