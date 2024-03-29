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
      final List<Entry> list = new ApiParser().parseFile(args[0]);
      Util.log(list);
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      // 在 java 层通过这种方式可以找到类
      Util.log("(\"[I\") = " + Class.forName("[I"));
      Util.log("(\"[B\") = " + Class.forName("[B"));
      Util.log("(\"[Ljava.lang.String;\") = " + Class.forName("[Ljava.lang.String;"));
      Util.log("(\"java.util.List\") = " + Class.forName("java.util.List"));
      Util.log(String[].class);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public List<Entry> parseFile(String path) throws IOException {
    final List<Entry> entries = new ArrayList<>();
    // 解析文件，逐行读取
    final BufferedReader reader = new BufferedReader(new FileReader(path));
    String line;
    // 9#com/sleticalboy/transform/ToastUtils#toast(Landroid/content/Context;Ljava/lang/CharSequence;)V
    Entry entry;
    while ((line = reader.readLine()) != null && (line = line.trim()).length() != 0) {
      Util.log("line = " + line);
      if ((entry = Entry.parse(line)) != null) entries.add(entry);
    }
    return entries;
  }
}
