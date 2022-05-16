package com.example.parser;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/**
 * Created on 2022/5/12
 *
 * @author binlee
 */
final class Entry {

  private final Map<String, Object> mFields;

  private Entry(String cls, String method, List<String> paramList, String returns) {
    mFields = new HashMap<String, Object>() {
      {
        put("cls", cls.replace("$", "%24"));
        put("method", method);
        put("params", paramList);
        put("returns", returns);
      }

      @Override public Object put(String key, Object value) {
        return key == null ? null : super.put(key, value);
      }
    };
  }

  @Override public String toString() {
    return new JSONObject(mFields).toString();
  }

  static Entry get(String line) {
    int index, start;
    // 访问修饰符
    index = line.indexOf('#');
    if (index != 1) return null;
    final int access = line.charAt(0) - '0';
    if ((access & (IParser.ACC_NATIVE | IParser.ACC_ABSTRACT)) != 0) return null;
    // 类名
    start = index + 1;
    index = line.indexOf('#', index + 1);
    final String cls = line.substring(start, index).replace('/', '.');
    System.out.println("cls = " + cls);
    // 方法名
    start = index + 1;
    index = line.indexOf('(', start);
    final String method = line.substring(start, index);
    System.out.println("method = " + method);
    final List<String> paramList = new ArrayList<>();
    // 方法参数列表：Landroid/content/Context;Ljava/lang/CharSequence;
    // void foo(char c, int i, short s, byte b, boolean bool, float f, double d, long l) {}
    // 10#com/sleticalboy/transform/ToastUtils#foo4()Ljava/lang/Void;
    // 10#com/sleticalboy/transform/ToastUtils#foo3(Ljava/lang/Void;)[I
    // 10#com/sleticalboy/transform/ToastUtils#foo2()[Ljava/util/List;
    // 10#com/sleticalboy/transform/ToastUtils#foo(CISBZFDJ[Ljava/lang/String;[I)Ljava/util/List;
    start = index + 1;
    index = line.indexOf(')', start);
    if (start != index) {
      final String params = line.substring(start, index);
      System.out.println("params = " + params);
      for (int i = 0; i < params.length(); i++) {
        char c = params.charAt(i);
        if (c == '[') {
          // 数组类型
          if (params.charAt(i + 1) == 'L') {
            // 引用类型，从当前位置直接找下一个 ';'
            final int endIndex = params.indexOf(';', i);
            String clazz = params.substring(i, endIndex + 1).replace('/', '.');
            paramList.add(clazz);
            System.out.println("param clazz = " + clazz);
            i = endIndex;
          } else {
            paramList.add(params.substring(i, i + 2));
            i = i + 1;
          }
        } else {
          if (c == 'L') {
            // 引用类型，从当前位置直接找下一个 ';'
            final int endIndex = params.indexOf(';', i);
            String clazz = params.substring(i, endIndex).replace('/', '.');
            paramList.add(clazz);
            System.out.println("param clazz = " + clazz);
            i = endIndex;
          } else {
            // 基本数据类型
            paramList.add(params.substring(i, i + 1));
          }
        }
      }
    }
    // 方法返回值类型
    final String returns = line.substring(index + 1).replace('/', '.');
    System.out.println("returns = " + returns);
    return new Entry(cls, method, paramList, returns);
  }
}
