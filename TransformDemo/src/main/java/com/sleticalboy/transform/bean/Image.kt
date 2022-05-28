package com.sleticalboy.transform.bean

import android.net.Uri
import com.sleticalboy.autotrack.ITrackable

/**
 * Created on 19-5-27.
 * @author leebin
 */
data class Image(
  var uri: Uri?,
  var desc: String,
  var res: Int?
) : ITrackable {

  override fun format(): CharSequence {
    return "Image(uri=$uri, desc='$desc', res=$res)"
  }

  override fun toString(): String = format().toString()
}