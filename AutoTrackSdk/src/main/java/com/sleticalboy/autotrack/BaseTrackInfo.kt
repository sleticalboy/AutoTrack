package com.sleticalboy.autotrack

import android.os.Build
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created on 19-5-8.
 * @author leebin
 */
abstract class BaseTrackInfo : ITrackable {

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
      val app = AutoTrack.sharedApp()
      if (appVersion == null) {
        appVersion = app.packageManager?.getPackageInfo(app.packageName, 0)?.versionName
      }
      if (appName == null) {
        val label: Int? = app.applicationInfo?.labelRes
        appName = label?.let { app.getString(it) } ?: "AutoTrack"
      }
      if (screenWidth == null) {
        screenWidth = app.resources?.displayMetrics?.widthPixels ?: 1080
      }
      if (screenHeight == null) {
        screenHeight = app.resources?.displayMetrics?.heightPixels ?: 1980
      }
      if (osVersion == null) {
        osVersion = when (Build.VERSION.SDK_INT) {
          32/*Build.VERSION_CODES.Q*/ -> "13"
          31/*Build.VERSION_CODES.Q*/ -> "12"
          30/*Build.VERSION_CODES.R*/ -> "11"
          Build.VERSION_CODES.Q -> "10"
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

  override fun toString(): String = toJson()

  private fun toJson(): String {
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
      .append("\"screen_size\":\"").append(screenHeight).append("x").append(screenWidth)
      .append("\"")
      .append("}")

    sb.append("}")
    return sb.toString()
  }

  final override fun format(): CharSequence = toJson()
}