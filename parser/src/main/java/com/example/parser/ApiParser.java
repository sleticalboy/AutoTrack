package com.example.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2022/4/29
 *
 * @author binlee
 */
public final class ApiParser {

  public static void main(String[] args) throws Exception {
    try {
      final List<Entry> list = new ApiParser().parse(args[0]);
      System.out.println(list);
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      // 在 java 层通过这种方式可以找到类
      System.out.println("(\"[I\") = " + Class.forName("[I"));
      System.out.println("(\"[B\") = " + Class.forName("[B"));
      System.out.println("(\"[Ljava.lang.String;\") = " + Class.forName("[Ljava.lang.String;"));
      System.out.println("(\"java.util.List\") = " + Class.forName("java.util.List"));
      System.out.println(String[].class);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public List<Entry> parse(String path) throws IOException, ClassNotFoundException {
    final List<Entry> entries = new ArrayList<>();
    // 解析文件，逐行读取
    final BufferedReader reader = new BufferedReader(new FileReader(path));
    String line;
    // 9#com/sleticalboy/transform/ToastUtils#toast(Landroid/content/Context;Ljava/lang/CharSequence;)V
    Entry entry;
    while ((line = reader.readLine()) != null && (line = line.trim()).length() != 0) {
      System.out.println("line = " + line);
      if ((entry = Entry.get(line)) != null) entries.add(entry);
    }
    return entries;
  }
}
