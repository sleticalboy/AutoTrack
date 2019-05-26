package com.sleticalboy.plugin.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class TrackTransform extends Transform {

    private Project project

    TrackTransform(Project project) {
        this.project = project
    }

    @Override
    String getName() {
        return "autoTrack"
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
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        if (!incremental) {
            outputProvider.deleteAll()
        }

        someCrInfo()

        /**Transform 的 inputs 有两种类型，一种是目录，一种是 jar 包，要分开遍历 */
        inputs.each {
            /**遍历目录*/
            it.directoryInputs.each { DirectoryInput dirInput ->
                /**当前这个 Transform 输出目录*/
                File dest = outputProvider.getContentLocation(dirInput.name, dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                File dir = dirInput.file

                if (dir) {
                    HashMap<String, File> modifyMap = new HashMap<>()
                    /**遍历以某一扩展名结尾的文件*/
                    dir.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) { File classFile ->
                        if (!TrackUtils.excludes(classFile.name)) {
                            File modified = TrackClassModifier.modifyClass(dir, classFile, context.getTemporaryDir())
                            if (modified != null) {
                                /**key 为包名 + 类名，如：/cn/sensorsdata/autotrack/android/app/MainActivity.class*/
                                String ke = classFile.absolutePath.replace(dir.absolutePath, "")
                                modifyMap.put(ke, modified)
                            }
                        }
                    }
                    FileUtils.copyDirectory(dirInput.file, dest)
                    modifyMap.entrySet().each { Map.Entry<String, File> en ->
                        File target = new File(dest.absolutePath + en.getKey())
                        if (target.exists()) {
                            target.delete()
                        }
                        FileUtils.copyFile(en.getValue(), target)
                        en.getValue().delete()
                    }
                }
            }

            /**遍历 jar*/
            it.jarInputs.each { JarInput jarInput ->
                String destName = jarInput.file.name

                /**截取文件路径的 md5 值重命名输出文件,因为可能同名,会覆盖*/
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8)
                /** 获取 jar 名字*/
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4)
                }

                /** 获得输出文件*/
                File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                def modifiedJar = TrackClassModifier.modifyJar(jarInput.file, context.getTemporaryDir(), true)
                if (modifiedJar == null) {
                    modifiedJar = jarInput.file
                }
                FileUtils.copyFile(modifiedJar, dest)
            }
        }
    }

    static void someCrInfo() {
        println('-----------com.sleticalboy.plugin--------------')
    }
}
