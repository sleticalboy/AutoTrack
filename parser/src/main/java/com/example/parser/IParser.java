package com.example.parser;

/**
 * Created on 2022/4/29
 *
 * @author binlee
 */
public interface IParser {
  int ACC_PUBLIC = 0x0001; // class, field, method
  int ACC_PRIVATE = 0x0002; // class, field, method
  int ACC_PROTECTED = 0x0004; // class, field, method
  int ACC_STATIC = 0x0008; // field, method
  int ACC_NATIVE = 0x0100; // method
  int ACC_ABSTRACT = 0x0400; // class, method
}
