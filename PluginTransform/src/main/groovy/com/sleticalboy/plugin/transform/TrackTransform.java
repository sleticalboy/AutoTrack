package com.sleticalboy.plugin.transform;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public final class TrackTransform extends Transform {

    @SuppressWarnings("unused")
    private final Project project;

    TrackTransform(Project project) {
        this.project = project;
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
        return false;
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException {
        if (!isIncremental) outputProvider.deleteAll();
        Utils.log("-----------com.sleticalboy.plugin--------------");
        // Transform 的 inputs 有两种类型，一种是目录，一种是 jar 包，要分开遍历
        for (TransformInput input : inputs) {
            // 遍历目录
            for (DirectoryInput di : input.getDirectoryInputs()) {
                // 当前这个 Transform 输出目录
                File dest = outputProvider.getContentLocation(di.getName(), di.getContentTypes(),
                        di.getScopes(), Format.DIRECTORY);

                if (di.getFile() == null || !di.getFile().isDirectory()) continue;
                // 遍历以某一扩展名结尾的文件
                File[] files = di.getFile().listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String name) {
                        return name.endsWith(".class");
                    }
                });
                HashMap<String, File> modifyMap = new HashMap<>();
                for (File file : files) {
                    if (!Utils.excludes(file.getName())) {
                        File modified = TrackClassModifier.modifyClass(di.getFile(), file, context.getTemporaryDir());
                        if (modified != null) {
                            // key 为包名 + 类名，如：/cn/sensorsdata/autotrack/android/app/MainActivity.class
                            modifyMap.put(file.getAbsolutePath().replace(di.getFile().getAbsolutePath(), ""), modified);
                        }
                    }
                }
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
}
