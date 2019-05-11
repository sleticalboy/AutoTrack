package com.sleticalboy.autotrack

import android.app.Application

/**
 * Created on 19-5-8.
 * @author leebin
 */
open class DefaultAutoTrackApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AutoTrack.init(this)
    }
}