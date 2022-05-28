package com.example.parser;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created on 2022/5/12
 *
 * @author binlee
 */
public final class Util {

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

  public static String toString(List<?> list) {
    if (list == null || list.size() == 0) return "[]";
    final Iterator<?> it = list.iterator();
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    while (true) {
      final Object obj = it.next();
      if (obj instanceof List) {
        sb.append(Util.toString((List<?>) obj));
      } else if (obj instanceof Map<?, ?>) {
        sb.append(Util.toString(((Map<?, ?>) obj)));
      } else {
        sb.append('"').append(obj).append('"');
      }
      if (!it.hasNext()) {
        return sb.append(']').toString();
      }
      sb.append(',');
    }
  }

  public static String toString(Map<?, ?> map) {
    if (map == null || map.size() == 0) return "{}";
    Iterator<? extends Map.Entry<?, ?>> it = map.entrySet().iterator();
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    while (true) {
      Map.Entry<?, ?> e = it.next();
      sb.append('"').append(e.getKey()).append('"').append(':');
      final Object obj = e.getValue();
      if (obj instanceof List) {
        sb.append(Util.toString((List<?>) obj));
      } else if (obj instanceof Map) {
        sb.append(Util.toString(((Map<?, ?>) obj)));
      } else {
        sb.append('"').append(obj).append('"');
      }
      if (!it.hasNext()) {
        return sb.append('}').toString();
      }
      sb.append(',');
    }
  }
}
