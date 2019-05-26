package com.sleticalboy.plugin.transform

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class MyPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        target.extensions.findByType(AppExtension.class)
                .registerTransform(new TrackTransform(target))
    }
}