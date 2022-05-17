package com.binlee.plugin.extractor;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.gradle.api.Project;

/**
 * Created on 2022/4/28
 *
 * @author binlee
 */
public abstract class BaseMethodRecorder {

  private final Project mProject;
  // 过滤哪些类
  private static final Pattern[] PATTERNS = {
    // 过滤 R 或者 BR 类
    Pattern.compile(".*/B?R[$|#].*"),
    // 过滤 BuildConfig 类
    Pattern.compile(".*/BuildConfig.*"),
  };
  private final Set<String> mRecordedMethods = new HashSet<>();
  private File mFile;

  public BaseMethodRecorder(Project project) {
    mProject = project;
  }

  public void record(String method) {
    for (Pattern pattern : PATTERNS) {
      if (pattern.matcher(method).matches()) return;
    }
    synchronized (mRecordedMethods) {
      mRecordedMethods.add(method);
    }
  }

  public void flush() {
    synchronized (mRecordedMethods) {
      if (mFile == null) {
        final File file = mProject.file("build/tracked-methods.txt");
        System.out.println("BaseMethodRecorder.flush() " + file + ", exist: " + file.exists());
        try {
          if (file.exists() && file.delete()) {
            System.out.println("BaseMethodRecorder.flush() delete old file");
          }
          if (file.createNewFile()) {
            System.out.println("BaseMethodRecorder.flush() create new file");
          }
          mFile = file;
        } catch (IOException e) {
          e.printStackTrace();
          return;
        }
      }
    }
    final StringBuilder lines = new StringBuilder();
    synchronized (mRecordedMethods) {
      for (String method : mRecordedMethods) {
        lines.append(method).append('\n');
      }
      mRecordedMethods.clear();
    }
    writeToFile(lines.toString(), mFile);
  }

  protected abstract void writeToFile(String content, File file);
}
