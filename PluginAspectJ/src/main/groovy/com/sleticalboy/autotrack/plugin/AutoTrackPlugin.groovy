package com.sleticalboy.autotrack.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class AutoTrackPlugin implements Plugin<Project> {

    private static final String TAG = 'AutoTrack '

    @Override
    void apply(Project project) {
        project.logger.info(TAG + 'this my first gradle plugin.')

        // use to help user config
        project.extensions.create('autoTrack', AutoTrackPluginConfig)

        // default task
        project.task('testTaskForAutoTrack') {
            project.logger.info(TAG + 'this my first task: Auto-Track.')
        }

        project.afterEvaluate {
            project.logger.debug(TAG + 'debug: ' + project.autoTrack.debug)
        }
    }
}