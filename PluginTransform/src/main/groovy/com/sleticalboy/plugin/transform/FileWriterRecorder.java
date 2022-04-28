package com.sleticalboy.plugin.transform;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import org.gradle.api.Project;

/**
 * Created on 2022/4/28
 *
 * @author binlee
 */
public final class FileWriterRecorder implements IMethodRecorder {

  private FileWriter mWriter;
  private Project mProject;

  public void prepare(Project project) {
    mProject = project;
  }

  private void ensureWriter() {
    if (mWriter != null) return;
    final File file = mProject.file("build/tracked-methods.txt");
    System.out.println("FileWriterRecorder.prepare() " + file + ", exist: " + file.exists());
    try {
      if (file.exists() && file.delete()) {
        System.out.println("FileWriterRecorder.prepare() delete old file");
      }
      if (file.createNewFile()) {
        System.out.println("FileWriterRecorder.prepare() create new file");
        mWriter = new FileWriter(file);
      }
    } catch (IOException e) {
      e.printStackTrace();
      mWriter = null;
    }
  }

  public void record(Set<String> trackedMethods) {
    ensureWriter();
    System.out.println("FileWriterRecorder.record() " + mWriter);
    if (mWriter == null) return;
    final StringBuilder lines = new StringBuilder();
    for (String method : trackedMethods) {
      lines.append(method).append('\n');
    }
    try {
      mWriter.write(lines.toString());
      mWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
      Utils.log(e.getMessage());
    }
  }
}
