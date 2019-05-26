package com.sleticalboy.plugin.transform

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * Created on 19-5-15.
 *
 * @author leebin
 */
class TrackClassModifier {

    static File modifyClass(File dir, File clsFile, File tempDir) {
        File modified = null
        try {
            final String clsName = TrackUtils.toClassName(clsFile.getAbsolutePath()
                    .replace(dir.getAbsolutePath() + File.separator, ""))
            final byte[] srcClsBytes = IOUtils.toByteArray(new FileInputStream(clsFile))
            final byte[] modifiedClsBytes = modifyInternal(srcClsBytes)
            if (modifiedClsBytes != null) {
                modified = new File(tempDir, clsName.replace(".", "") + ".class")
                if (modified.exists()) {
                    modified.delete()
                }
                modified.createNewFile()
                new FileOutputStream(modified).write(modifiedClsBytes)
            }
        } catch (IOException ignored) {
            modified = clsFile
        }
        return modified
    }

    static File modifyJar(File jar, File tempDir, boolean hexName) {
        File out
        try {
            final JarFile jarFile = new JarFile(jar)
            final String name = hexName ? DigestUtils.md5Hex(jar.getAbsolutePath()).substring(0, 8) : ""
            out = new File(tempDir, name + jar.getName())
            final JarOutputStream jarOs = new JarOutputStream(new FileOutputStream(out))
            final Enumeration<JarEntry> entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement()
                final InputStream is = jarFile.getInputStream(jarEntry)
                String clsName
                jarOs.putNextEntry(new ZipEntry(jarEntry.getName()))
                final byte[] srcClsBytes = IOUtils.toByteArray(is)
                byte[] modifiedClsBytes = null
                if (jarEntry.getName().endsWith(".class")) {
                    clsName = jarEntry.getName().replace("/", ".").replace(".class", "")
                    if (!TrackUtils.excludes(clsName)) {
                        modifiedClsBytes = modifyInternal(srcClsBytes)
                    }
                }
                if (modifiedClsBytes == null) {
                    modifiedClsBytes = srcClsBytes
                }
                jarOs.write(modifiedClsBytes)
                jarOs.closeEntry()
            }
            jarOs.close()
            jarFile.close()
        } catch (IOException ignored) {
            return jar
        }
        return out
    }

    private static byte[] modifyInternal(byte[] src) {
        final ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        new ClassReader(src).accept(new TrackClassVisitor(cw), ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }
}
