package com.sleticalboy.autotrack

abstract class PageInfo : BaseTrackInfo() {

    override fun label(): CharSequence = title()

    abstract fun title(): CharSequence

    override fun type(): String {
        return "PageView"
    }
}