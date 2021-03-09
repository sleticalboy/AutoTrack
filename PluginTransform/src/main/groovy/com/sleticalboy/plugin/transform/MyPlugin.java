package com.sleticalboy.plugin.transform;

import com.android.build.gradle.AppExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public final class MyPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getExtensions()
                .findByType(AppExtension.class)
                .registerTransform(new TrackTransform(target));
    }
}