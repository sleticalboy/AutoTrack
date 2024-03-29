package com.binlee.plugin.extractor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import org.gradle.api.Project;

/**
 * Created on 2022/4/28
 *
 * @author binlee
 */
public class FileChannelRecorder extends BaseMethodRecorder {

  private FileChannel mChannel;
  private long mPos;

  public FileChannelRecorder(Project project) {
    super(project);
  }

  @Override protected void writeToFile(String content, File file) {
    ensureChannel(file);
    // Utils.log("FileChannelRecorder.writeToFile() " + mChannel);
    try {
      MappedByteBuffer buf = mChannel.map(FileChannel.MapMode.READ_WRITE, mPos, content.length());
      buf.put(content.getBytes(StandardCharsets.UTF_8));
      mPos += content.length();
    } catch (IOException e) {
      e.printStackTrace();
      Utils.log(e.getMessage());
    }
  }

  private void ensureChannel(File file) {
    if (mChannel != null) return;
    Utils.log("FileChannelRecorder.ensureChannel() " + file + ", exist: " + file.exists());
    try {
      if (file.exists() && file.delete()) {
        Utils.log("FileChannelRecorder.ensureChannel() delete old file");
      }
      if (file.createNewFile()) {
        mChannel = FileChannel.open(file.toPath());
        mChannel = new RandomAccessFile(file, "rw").getChannel();
        Utils.log("FileChannelRecorder.ensureChannel() create new file");
      }
    } catch (IOException e) {
      e.printStackTrace();
      mChannel = null;
    }
  }
}
