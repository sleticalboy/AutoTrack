package com.binlee.plugin.extractor;

import com.example.parser.ApiParser;
import com.example.parser.Entry;
import com.example.parser.Util;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
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
    // 过滤静态代码块
    Pattern.compile(".*<clinit>.*"),
    // 过滤匿名内部类 MainActivity$2
    Pattern.compile(".*\\$\\d.*"),
    Pattern.compile(".*com/afollestad/materialdialogs/.*"),
    Pattern.compile(".*com/androidnetworking/.*"),
    Pattern.compile(".*me/zhanghai/android/materialprogressbar/.*"),
    Pattern.compile(".*androidx/.*"),
    Pattern.compile(".*kotlinx?/.*"),
    Pattern.compile(".*io/reactivex/.*"),
    Pattern.compile(".*retrofit2?/"),
    Pattern.compile(".*okhttp3?/")
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

  public void doFirst() {
    final File file = mFile == null ? mProject.file("build/tracked-methods.txt") : mFile;
    try {
      if (file.exists() && file.delete()) {
        Utils.log("BaseMethodRecorder.doFirst() delete old file");
      }
      if (file.createNewFile()) {
        Utils.log("BaseMethodRecorder.doFirst() create new file");
      }
      mFile = file;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public final void doLast() {
    Utils.log("BaseMethodRecorder#doLast() " + mFile);
    try {
      final List<Entry> entries = new ApiParser().parseFile(mFile.getAbsolutePath());
      Utils.log("entries.size: " + entries.size());
      // Utils.log("BaseMethodRecorder#doLast() \n" + array);
      final File file = mProject.file(mFile.getAbsolutePath() + ".json");
      if (file.exists()) file.delete();
      if (!file.exists()) file.createNewFile();
      FileUtils.copyInputStreamToFile(
        new ByteArrayInputStream(Util.toString(entries).getBytes(StandardCharsets.UTF_8)), file);
      Utils.log(file + " size: " + file.length());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
