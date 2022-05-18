package com.example.parser;

/**
 * Created on 2022/5/12
 *
 * @author binlee
 */
final class Util {

  static boolean sDebug = false;

  public static void setDebug(boolean debug) {
    sDebug = debug;
  }

  public static void log(Object msg) {
    if (sDebug) System.out.println(msg);
  }

  public static String substring(String s, int start, int end) {
    return s.substring(start, end).replace('/', '.').replace("$", "%24");
  }
}
