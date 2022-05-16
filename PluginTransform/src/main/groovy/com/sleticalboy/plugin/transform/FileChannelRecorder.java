package com.sleticalboy.plugin.transform;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;
import org.gradle.api.Project;

/**
 * Created on 2022/4/28
 *
 * @author binlee
 */
public class FileChannelRecorder implements IMethodRecorder {

  private Project mProject;
  private FileChannel mChannel;
  private long mPos;
  // 过滤 R 或者 BR 类
  private static final Pattern PATTERN = Pattern.compile(".*/B?R[$|#].*");

  @Override public void prepare(Project project) {
    mProject = project;
  }

  private void ensureChannel() {
    if (mChannel != null) return;
    final File file = mProject.file("build/tracked-methods.txt");
    System.out.println("FileChannelRecorder.prepare() " + file + ", exist: " + file.exists());
    try {
      if (file.exists() && file.delete()) {
        System.out.println("FileChannelRecorder.prepare() delete old file");
      }
      if (file.createNewFile()) {
        mChannel = FileChannel.open(file.toPath());
        mChannel = new RandomAccessFile(file, "rw").getChannel();
        System.out.println("FileChannelRecorder.prepare() create new file");
      }
    } catch (IOException e) {
      e.printStackTrace();
      mChannel = null;
    }
  }

  @Override public void record(Set<String> records) {
    ensureChannel();
    System.out.println("FileChannelRecorder.record() " + mChannel);
    final StringBuilder lines = new StringBuilder();
    for (String method : records) {
      if (PATTERN.matcher(method).matches()) continue;
      lines.append(method).append('\n');
    }
    try {
      MappedByteBuffer buffer = mChannel.map(FileChannel.MapMode.READ_WRITE, mPos, lines.length());
      buffer.put(lines.toString().getBytes(StandardCharsets.UTF_8));
      mPos += lines.length();
    } catch (IOException e) {
      e.printStackTrace();
      Utils.log(e.getMessage());
    }
  }
}
