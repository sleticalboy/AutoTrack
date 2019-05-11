package com.sleticalboy.autotrack

abstract class ClickInfo : Trackable() {

    override fun label(): CharSequence = desc()

    abstract fun desc(): String

    override fun type(): String {
        return "WidgetClick"
    }
}