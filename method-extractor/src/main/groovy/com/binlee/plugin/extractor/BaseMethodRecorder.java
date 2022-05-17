package com.binlee.plugin.extractor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
  private static final Set<Pattern> PATTERNS = new HashSet<>(Arrays.asList(
    // 过滤 R 或者 BR 类
    Pattern.compile(".*/B?R[$|#].*"),
    // 过滤 BuildConfig 类
    Pattern.compile(".*/BuildConfig.*"),
    Pattern.compile(".*com/afollestad/materialdialogs/.*"),
    Pattern.compile(".*com/androidnetworking/.*"),
    Pattern.compile(".*me/zhanghai/android/materialprogressbar/.*"),
    Pattern.compile(".*androidx/.*"),
    Pattern.compile(".*kotlinx?/.*"),
    Pattern.compile(".*io/reactivex/.*"),
    Pattern.compile(".*retrofit2?/")
    ));
  private final Set<String> mRecordedMethods = new HashSet<>();
  private File mFile;
  private boolean hasConfigured = false;

  public BaseMethodRecorder(Project project) {
    mProject = project;
    final MethodExtractorExtension extension = project.getExtensions()
      .getByType(MethodExtractorExtension.class);
    Utils.setDebug(extension.getDebug());
    Utils.log("BaseMethodRecorder#<init> " + extension);
    final List<String> excludes = extension.getExcludes();
    if (excludes != null && excludes.size() != 0) {
      for (String exclude : excludes) {
        PATTERNS.add(Pattern.compile(exclude));
      }
    }
    mFile = extension.getOutputFile();
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
      if (!hasConfigured) {
        // final File file = mProject.file("build/tracked-methods.txt");
        final File file = mFile;
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
        hasConfigured = true;
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
