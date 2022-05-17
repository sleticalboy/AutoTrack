package com.binlee.plugin.extractor;

import org.gradle.api.Project;

/**
 * Created on 2022/4/28
 *
 * @author binlee
 */
public final class MethodRecorder {

  private static BaseMethodRecorder sRecorder;

  public static synchronized void init(Project project) {
    Utils.log("MethodRecorder#init() " + project);
    if (sRecorder != null) return;
    // sRecorder = new FileWriterRecorder(project);
    sRecorder = new FileChannelRecorder(project);
  }

  public static BaseMethodRecorder get() {
    if (sRecorder == null) {
      throw new RuntimeException("You must call #init(Project) first!");
    }
    return sRecorder;
  }
}
