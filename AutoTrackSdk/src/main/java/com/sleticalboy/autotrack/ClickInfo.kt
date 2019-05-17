package com.sleticalboy.autotrack

abstract class ClickInfo : Trackable() {

    override fun label(): CharSequence = desc() ?: ""

    abstract fun desc(): CharSequence?

    override fun type(): CharSequence {
        return "WidgetClick"
    }
}