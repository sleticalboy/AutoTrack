package com.sleticalboy.plugin.transform;

/**
 * Created on 2022/4/28
 *
 * @author binlee
 */
public final class MethodRecorder {

  private static final IMethodRecorder sRecorder;

  static {
    // sRecorder = new FileWriterRecorder();
    sRecorder = new FileChannelRecorder();
  }

  public static IMethodRecorder get() {
    return sRecorder;
  }
}
