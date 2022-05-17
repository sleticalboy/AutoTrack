package com.binlee.plugin.extractor;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;

import java.util.ArrayList;
import java.util.List;
import org.gradle.api.Project;

import java.io.File;
import java.util.Set;

public final class MethodScanTransform extends Transform {

    public void init(Project project) {
        MethodRecorder.init(project);
    }

    @Override
    public String getName() {
        return "MethodExtractor";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        // 需要处理的数据类型
        // class 和 resource
        return TransformManager.CONTENT_CLASS;
    }

    @Override public Set<? super QualifiedContent.Scope> getReferencedScopes() {
        // 引用类型的 transform，不要将上一个 transform 的 inputs 处理(outputs)作为下一个 transform 的 inputs
        // 引用类型的 transform，getScopes() 需放回空
        return TransformManager.SCOPE_FULL_PROJECT;
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
        return TransformManager.EMPTY_SCOPES;
    }

    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override public void transform(TransformInvocation invocation) {
        Utils.log("-----------com.binlee.plugin--------------");
        // Transform 的 inputs 有两种类型，一种是目录，一种是 jar 包，要分开遍历
        for (TransformInput input : invocation.getReferencedInputs()) {
            // 遍历目录
            Utils.log("input dirs: " + input.getDirectoryInputs());
            final List<File> targets = new ArrayList<>();
            for (DirectoryInput di : input.getDirectoryInputs()) {
                if (di.getFile() == null || !di.getFile().isDirectory()) continue;
                searchFilesInFolder(di.getFile(), targets);
            }
            for (File file : targets) {
                // key 为包名 + 类名，如：/cn/sensorsdata/autotrack/android/app/MainActivity.class
                String key = file.getAbsolutePath().replace(file.getAbsolutePath(), "");
                Utils.log("might modify " + file);
                if (Utils.includes(key)) {
                    MethodScanner.scanClass(file);
                }
            }

            // 遍历 jar 包
            Utils.log("input jars: " + input.getJarInputs());
            for (JarInput jar : input.getJarInputs()) {
                MethodScanner.scanJar(jar.getFile());
            }
        }
    }

    // 遍历所有 .class 文件
    private void searchFilesInFolder(File file, List<File> targets) {
        if (file == null) return;
        if (file.isDirectory()) {
            final File[] children = file.listFiles();
            if (children == null || children.length == 0) return;
            for (File child : children) {
                searchFilesInFolder(child, targets);
            }
        } else if (file.isFile() && file.getName().endsWith(".class")){
            targets.add(file);
        }
    }

    @Override public String toString() {
        return getName();
    }
}
