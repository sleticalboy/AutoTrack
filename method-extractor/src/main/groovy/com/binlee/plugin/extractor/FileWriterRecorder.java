package com.binlee.plugin.extractor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.gradle.api.Project;

/**
 * Created on 2022/4/28
 *
 * @author binlee
 */
public final class FileWriterRecorder extends BaseMethodRecorder {

  private FileWriter mWriter;

  public FileWriterRecorder(Project project) {
    super(project);
  }

  @Override protected void writeToFile(String content, File file) {
    ensureWriter(file);
    System.out.println("FileWriterRecorder.record() " + mWriter);
    if (mWriter == null) return;
    try {
      mWriter.write(content);
      mWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
      Utils.log(e.getMessage());
    }
  }

  private void ensureWriter(File file) {
    if (mWriter != null) return;
    try {
      mWriter = new FileWriter(file);
    } catch (IOException e) {
      e.printStackTrace();
      mWriter = null;
    }
  }
}
