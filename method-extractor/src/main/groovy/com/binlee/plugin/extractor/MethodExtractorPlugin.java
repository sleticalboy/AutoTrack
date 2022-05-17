package com.binlee.plugin.extractor;

import com.android.build.gradle.AppExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public final class MethodExtractorPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        final AppExtension extension = target.getExtensions().findByType(AppExtension.class);
        if (extension == null) throw new RuntimeException("Can't find AppExtension");
        extension.registerTransform(new MethodScanTransform(target));
    }
}