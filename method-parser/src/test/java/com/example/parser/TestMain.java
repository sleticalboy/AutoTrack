package com.example.parser;

/**
 * Created on 2022/5/27
 *
 * @author binlee
 */
public class TestMain {

  public static void main(String[] args) {
    final Entry entry = Entry.parse(
      "1#com/sleticalboy/transform/ToastUtils#foo(CISBZFDJ[Ljava/lang/String;[I)Ljava/util/List;");
    System.out.println(entry);
  }
}
