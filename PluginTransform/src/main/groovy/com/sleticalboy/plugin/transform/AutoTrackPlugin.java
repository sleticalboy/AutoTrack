package com.sleticalboy.plugin.transform;

import com.android.build.gradle.AppExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public final class AutoTrackPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        final AppExtension extension = target.getExtensions().findByType(AppExtension.class);
        if (extension == null) throw new RuntimeException("Can't find AppExtension");
        extension.registerTransform(new TrackTransform(target));
    }
}