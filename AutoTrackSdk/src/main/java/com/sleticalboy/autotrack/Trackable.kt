package com.sleticalboy.autotrack

import android.os.Build
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Created on 19-5-8.
 * @author leebin
 */
abstract class Trackable {

    companion object {
        private var appName: String? = null
        private var appVersion: String? = null
        private var osVersion: String? = null
        private var screenWidth: Int? = null
        private var screenHeight: Int? = null
        private val dataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)

        init {
            initDeviceInfo()
        }

        private fun initDeviceInfo() {
            if (appVersion == null) {
                appVersion =
                    AutoTrack.sApp?.packageManager?.getPackageInfo(AutoTrack.sApp?.packageName, 0)?.versionName
            }
            if (appName == null) {
                val label: Int? = AutoTrack.sApp?.applicationInfo?.labelRes
                appName = label?.let { AutoTrack.sApp?.getString(it) } ?: "AutoTrack"
            }
            if (screenWidth == null) {
                screenWidth = AutoTrack.sApp?.resources?.displayMetrics?.widthPixels ?: 1080
            }
            if (screenHeight == null) {
                screenHeight = AutoTrack.sApp?.resources?.displayMetrics?.heightPixels ?: 1980
            }
            if (osVersion == null) {
                osVersion = when (Build.VERSION.SDK_INT) {
                    Build.VERSION_CODES.P -> "9.0"
                    Build.VERSION_CODES.O_MR1 -> "8.1"
                    Build.VERSION_CODES.O -> "8.0"
                    Build.VERSION_CODES.N_MR1 -> "7.1"
                    Build.VERSION_CODES.N -> "7.0"
                    Build.VERSION_CODES.M -> "6.0"
                    Build.VERSION_CODES.LOLLIPOP_MR1 -> "5.1"
                    Build.VERSION_CODES.LOLLIPOP -> "5.0"
                    Build.VERSION_CODES.KITKAT_WATCH -> "4.4W"
                    Build.VERSION_CODES.KITKAT -> "4.4"
                    Build.VERSION_CODES.JELLY_BEAN_MR2 -> "4.3"
                    Build.VERSION_CODES.JELLY_BEAN_MR1 -> "4.2"
                    Build.VERSION_CODES.JELLY_BEAN -> "4.1"
                    Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 -> "4.0.3"
                    Build.VERSION_CODES.ICE_CREAM_SANDWICH -> "4.0"
                    else -> "Unknown"
                }
            }

        }

    }

    private fun sdkVersion(): String = "Android 1.0.0"

    private fun os(): String = "Android"

    private fun apiVersion(): Int = Build.VERSION.SDK_INT

    private fun model(): String = Build.BRAND

    private fun manufacture(): String = Build.MANUFACTURER

    /**
     * 事件标签
     */
    abstract fun label(): CharSequence

    /**
     * 事件类型
     */
    abstract fun type(): CharSequence

    /**
     * 当是控件点击事件的时候启用
     */
    abstract fun path(): CharSequence?

    fun format(): String {
        val map = HashMap<String, Any>()
        map["sdk_version"] = sdkVersion()

        map["label"] = label()
        map["event_type"] = type()
        map["event_time"] = dataFormat.format(System.currentTimeMillis())

        map["app_name"] = appName as String
        map["app_version"] = appVersion as String
        map["os"] = os() + " " + osVersion + " api " + apiVersion()

        map["model"] = model()
        map["manufacture"] = manufacture()
        map["screen_size"] = screenHeight.toString() + "x" + screenWidth
        return JSONObject(map).toString()
    }

    override fun toString(): String {
        val sb = StringBuilder("{")
        sb.append("\"sdk_info\":\"").append(sdkVersion()).append("\",")

        sb.append("\"event_info\":{")
            .append("\"type\":\"").append(type()).append("\",")
            .append("\"label\":\"").append(label()).append("\",")
            .append("\"path\":\"").append(path()).append("\",")
            .append("\"time\":\"").append(dataFormat.format(System.currentTimeMillis())).append("\"")
            .append("},")

        sb.append("\"device_info\":{")
            .append("\"app_name\":\"").append(appName).append("\",")
            .append("\"app_version\":\"").append(appVersion).append("\",")
            .append("\"os\":\"").append(os()).append(" ").append(osVersion).append("\",")
            .append("\"sdk_version\":\"").append(apiVersion()).append("\",")
            .append("\"model\":\"").append(model()).append("\",")
            .append("\"manufacture\":\"").append(manufacture()).append("\",")
            .append("\"screen_size\":\"").append(screenHeight).append("x").append(screenWidth).append("\"")
            .append("}")

        sb.append("}")
        return sb.toString()
    }
}