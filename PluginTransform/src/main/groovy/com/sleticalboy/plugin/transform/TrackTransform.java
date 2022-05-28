package com.sleticalboy.plugin.transform;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;

public final class TrackTransform extends Transform {

  @SuppressWarnings("unused")
  private final Project project;

  TrackTransform(Project project) {
    this.project = project;
    MethodRecorder.get().prepare(project);
  }

  @Override
  public String getName() {
    return "autoTrack";
  }

  @Override
  public Set<QualifiedContent.ContentType> getInputTypes() {
    // 需要处理的数据类型
    // class 和 resource
    return TransformManager.CONTENT_CLASS;
  }

  @Override
  public Set<? super QualifiedContent.Scope> getScopes() {
    // 需要处理的内容范围
    // external_libraries 外部库
    // project 项目内容
    // project_local_deps 项目本地依赖
    // provided_only 只提供本地或远程依赖
    // sub_projects
    // sub_projects_local_deps
    // tested_code
    return TransformManager.SCOPE_FULL_PROJECT;
  }

  @Override
  public boolean isIncremental() {
    return true;
  }

  @Override public void transform(TransformInvocation invocation)
    throws TransformException, InterruptedException, IOException {
    final Context context = invocation.getContext();
    final boolean isIncremental = invocation.isIncremental();
    final TransformOutputProvider outputProvider = invocation.getOutputProvider();
    final Collection<TransformInput> inputs = invocation.getInputs();
    if (!isIncremental) outputProvider.deleteAll();
    Utils.log("-----------com.sleticalboy.plugin--------------");
    // Transform 的 inputs 有两种类型，一种是目录，一种是 jar 包，要分开遍历
    for (TransformInput input : inputs) {
      // 遍历目录
      Utils.log("input dirs: " + input.getDirectoryInputs());
      for (DirectoryInput di : input.getDirectoryInputs()) {
        System.out.println("input dir: " + di.getFile());
        if (di.getFile() == null || !di.getFile().isDirectory()) continue;
        final List<File> targets = new ArrayList<>();
        searchFiles(di.getFile(), targets);
        HashMap<String, File> modifyMap = new HashMap<>();
        for (File file : targets) {
          // key 为包名 + 类名，如：/cn/sensorsdata/autotrack/android/app/MainActivity.class
          String key = file.getAbsolutePath().replace(di.getFile().getAbsolutePath(), "");
          Utils.log("might modify " + key);
          if (Utils.includes(key)) {
            File modified =
              TrackClassModifier.modifyClass(di.getFile(), file, context.getTemporaryDir());
            Utils.log(" -> " + modified);
            if (modified != null) {
              modifyMap.put(key, modified);
            }
          }
        }
        // 当前这个 Transform 输出目录
        File dest = outputProvider.getContentLocation(di.getName(), di.getContentTypes(),
          di.getScopes(), Format.DIRECTORY);
        FileUtils.copyDirectory(di.getFile(), dest);
        for (String key : modifyMap.keySet()) {
          File file = modifyMap.get(key);
          File target = new File(dest, key);
          if (target.exists()) target.delete();
          FileUtils.copyFile(file, target);
          file.delete();
        }
      }

      // 遍历 jar 包
      Utils.log("input jars: " + input.getJarInputs());
      for (JarInput jar : input.getJarInputs()) {
        String name = jar.getFile().getName();
        // 获取 jar 名字
        if (name.endsWith(".jar")) {
          name = name.substring(0, name.length() - 4);
        }
        // 截取文件路径的 md5 值重命名输出文件,因为可能同名,会覆盖
        String hex = DigestUtils.md5Hex(jar.getFile().getAbsolutePath()).substring(0, 8);
        // 获得输出文件
        File dest = outputProvider.getContentLocation(name + "_" + hex,
          jar.getContentTypes(), jar.getScopes(), Format.JAR);
        File modified = TrackClassModifier.modifyJar(jar.getFile(),
          context.getTemporaryDir(), true);
        if (modified == null) modified = jar.getFile();
        FileUtils.copyFile(modified, dest);
      }
    }
  }

  // 遍历所有 .class 文件
  private void searchFiles(File root, List<File> targets) {
    if (root == null) return;
    if (root.isDirectory()) {
      final File[] files = root.listFiles();
      if (files == null || files.length == 0) return;
      for (File file : files) {
        searchFiles(file, targets);
      }
    } else if (root.isFile() && root.getName().endsWith(".class")) {
      targets.add(root);
    }
  }
}
