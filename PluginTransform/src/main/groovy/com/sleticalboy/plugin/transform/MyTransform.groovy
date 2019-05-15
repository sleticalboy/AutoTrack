package com.sleticalboy.plugin.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class MyTransform extends Transform {

    private Project project

    MyTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "AutoTrack"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        // 需要处理的数据类型
        // class 和 resource
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        // 需要处理的内容范围
        // external_libraries 外部库
        // project 项目内容
        // project_local_deps 项目本地依赖
        // provided_only 只提供本地或远程依赖
        // sub_projects
        // sub_projects_local_deps
        // tested_code
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation invocation) throws TransformException,
            InterruptedException, IOException {
        someCrInfo()
        invocation.inputs.each { input ->
            // 遍历目录
            input.directoryInputs.each { dir ->
                final File dest = invocation.outputProvider.getContentLocation(
                        dir.name, dir.contentTypes, dir.scopes, Format.DIRECTORY)
                println('file name: ' + dir.file)
                FileUtils.copyDirectory(dir.file, dest)
            }
            // 遍历 jar
            input.jarInputs.each { jar ->
                String realName = jar.name
                println('jar name: ' + realName)
                if (jar.name.endsWith('.jar')) {
                    realName = jar.name.substring(0, jar.name.length() - 4)
                }
                realName += DigestUtils.md5Hex(jar.file.absolutePath)
                final File dest = invocation.outputProvider.getContentLocation(
                        realName, jar.contentTypes, jar.scopes, Format.JAR)
                FileUtils.copyFile(jar.file, dest)
            }
        }
    }

    static void someCrInfo() {
        println('-----------com.sleticalboy.plugin--------------')
    }
}
