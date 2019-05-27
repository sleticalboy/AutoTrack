package com.sleticalboy.autotrack

/**
 * Created on 19-5-27.
 * @author leebin
 */
interface ITrackable {

    /**
     * 格式化信息，便于做数据分析
     */
    fun format(): CharSequence
}