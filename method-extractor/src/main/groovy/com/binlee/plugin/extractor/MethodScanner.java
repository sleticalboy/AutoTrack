package com.binlee.plugin.extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * Created on 19-5-15.
 *
 * @author leebin
 */
public final class MethodScanner {

  static void scanClass(File clsFile) {
    try {
      final byte[] srcClsBytes = IOUtils.toByteArray(new FileInputStream(clsFile));
      scanInternal(srcClsBytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static void scanJar(File jar) {
    try {
      final JarFile jarFile = new JarFile(jar);
      final Enumeration<JarEntry> entries = jarFile.entries();
      while (entries.hasMoreElements()) {
        final JarEntry jarEntry = entries.nextElement();
        final InputStream is = jarFile.getInputStream(jarEntry);
        final byte[] srcClsBytes = IOUtils.toByteArray(is);
        if (jarEntry.getName().endsWith(".class")) {
          String clsName = jarEntry.getName().replace("/", ".").replace(".class", "");
          if (Utils.includes(clsName)) {
            scanInternal(srcClsBytes);
          }
        }
      }
      jarFile.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void scanInternal(byte[] src) {
    new ClassReader(src)
      .accept(new MethodExtractClassVisitor(new ClassWriter(ClassWriter.COMPUTE_MAXS)),
        ClassReader.EXPAND_FRAMES
      );
  }
}
