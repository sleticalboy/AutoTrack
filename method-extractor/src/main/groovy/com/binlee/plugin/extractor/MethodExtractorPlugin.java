package com.binlee.plugin.extractor;

import com.android.build.gradle.AppExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public final class MethodExtractorPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        Utils.setDebug(true);
        final AppExtension extension = target.getExtensions().findByType(AppExtension.class);
        if (extension == null) throw new RuntimeException("Can't find AppExtension");
        target.getExtensions().create("methodExtractor", MethodExtractorExtension.class);
        final MethodScanTransform transform = new MethodScanTransform();
        Utils.log("MethodExtractorPlugin#apply() ----> transform: " + transform);
        extension.registerTransform(transform);
        // 必须在此回调方法中才可获取到所有的 task 和插件配置
        target.afterEvaluate(project -> {
            transform.init(project);
            // task: transformClassesWithMethodExtractorForDebug
            // task: transformClassesWithMethodExtractorForDebugAndroidTest
            // task: transformClassesWithMethodExtractorForRelease
            for (Task task : project.getTasks()) {
                if (task.getName().contains(transform.getName())) {
                    Utils.log("MethodExtractorPlugin#afterEvaluate() --> " + task.getName());
                    // 永不过时，不可以重用
                    task.getOutputs().upToDateWhen(t -> false);
                    task.doFirst(t -> MethodRecorder.get().doFirst());
                    task.doLast(t -> MethodRecorder.get().doLast());
                }
            }
        });
    }
}