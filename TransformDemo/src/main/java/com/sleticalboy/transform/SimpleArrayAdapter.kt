package com.sleticalboy.transform

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sleticalboy.transform.bean.Image
import kotlinx.android.synthetic.main.recycler_image_item.view.*

/**
 * Created on 19-5-27.
 * @author leebin
 */
class SimpleArrayAdapter(private val data: List<Image>) : RecyclerView.Adapter<SimpleViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
    val view =
      LayoutInflater.from(parent.context).inflate(R.layout.recycler_image_item, parent, false)
    return SimpleViewHolder(view)
  }

  override fun getItemCount(): Int = data.size

  override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
    val image = data[position]
    if (image.uri != null) {
      holder.image.setImageURI(image.uri)
    } else {
      image.res?.let { holder.image.setImageResource(it) }
    }
    holder.desc.text = image.desc

    holder.itemView.setOnClickListener {
      Log.d("SimpleArrayAdapter", "image: " + image.format())
    }
  }
}

class SimpleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  val image: ImageView = itemView.image
  val desc: TextView = itemView.desc
}