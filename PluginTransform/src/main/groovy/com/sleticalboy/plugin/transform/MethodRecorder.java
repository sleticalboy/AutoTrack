package com.sleticalboy.plugin.transform;

/**
 * Created on 2022/4/28
 *
 * @author binlee
 */
public final class MethodRecorder {

  private static IMethodRecorder sRecorder;

  public static IMethodRecorder get() {
    if (sRecorder == null) {
      // sRecorder = new FileWriterRecorder();
      sRecorder = new FileChannelRecorder();
    }
    return sRecorder;
  }
}
