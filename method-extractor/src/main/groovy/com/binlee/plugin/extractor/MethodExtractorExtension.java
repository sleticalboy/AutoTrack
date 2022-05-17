package com.binlee.plugin.extractor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created on 2022/5/17
 *
 * @author binlee
 */
public abstract class MethodExtractorExtension {

  /** 是否开启调试 */
  private boolean mDebug = false;
  /** 输出文件 */
  private File mOutput;
  /** 不包括的类 */
  private List<String> mExcludes = new ArrayList<>();

  public void setDebug(boolean debug) {
    mDebug = debug;
  }

  public boolean getDebug() {
    return mDebug;
  }

  public void setOutputFile(File outputFile) {
    mOutput = outputFile;
  }

  public File getOutputFile() {
    return mOutput;
  }

  public void setExcludes(String... excludes) {
    mExcludes = Arrays.asList(excludes);
  }

  public List<String> getExcludes() {
    return mExcludes;
  }

  @Override public String toString() {
    return "MethodExtractorExtension{" +
      "mDebug=" + mDebug +
      ", mOutput=" + mOutput +
      ", mExcludes=" + mExcludes +
      '}';
  }
}
