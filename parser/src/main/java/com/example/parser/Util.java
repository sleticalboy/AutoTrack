package com.example.parser;

/**
 * Created on 2022/5/12
 *
 * @author binlee
 */
final class Util {

  static Class<?> getPrimitive(char signature) {
    switch (signature) {
      case 'C':
        return char.class;
      case 'I':
        return int.class;
      case 'S':
        return short.class;
      case 'B':
        return byte.class;
      case 'Z':
        return boolean.class;
      case 'F':
        return float.class;
      case 'D':
        return double.class;
      case 'J':
        return long.class;
      case 'V':
        return void.class;
      default:
        throw new IllegalArgumentException("" + signature);
    }
  }

}
