package com.sleticalboy.transform

import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_track.*

/**
 * Created on 19-5-26.
 *
 * @author leebin
 */
class TrackFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.activity_track, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    // 普通点击事件 测试通过
    normalClick.setOnClickListener { toast("normal click") }
    // lambda 表达式添加点击事件 测试通过
    lambdaClick.setOnClickListener { toast("lambda click") }
    // CheckBox 点击事件 测试通过
    cbNormal.setOnCheckedChangeListener { _, _ -> toast("cb normal checked") }
    // 测试通过
    cbLambda.setOnCheckedChangeListener { _, _ -> toast("cb lambda checked") }
    // 测试通过
    showDialog.setOnClickListener { createDialog() }

    // 动态添加 View
    dynamicAdd.setOnClickListener { dynamicAddView() }
  }

  private fun dynamicAddView() {
    val listView = ListView(requireContext())
    listView.setOnItemClickListener { _, _, _, _ -> }
    listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
      //
    }
    dynamicContainer.removeAllViews()
    val params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
    params.weight = 1f
    params.gravity = Gravity.CENTER_VERTICAL
    for (i in 0..2) {
      val child = Button(requireContext())
      child.isAllCaps = false
      child.text = "Button $i"
      // 普通匿名内部类可以添加埋点代码，测试通过
      if (i % 2 == 0) {
        child.setOnClickListener { toast("this is " + child.text) }
      } else {
        // lambda 表达式可以添加埋点代码，测试通过
        child.setOnClickListener { toast("this is " + child.text) }
      }
      dynamicContainer.addView(child, i, params)
    }
  }

  private fun createDialog() {
    // showMultiChoiceDialog(this);
    val builder = AlertDialog.Builder(requireContext())
    builder.setTitle("Auto Track Dialog")
    // multiChoiceItems 和 message 不能同时存在
    // builder.setMessage("track the click event automatically");
    val items = arrayOf<CharSequence>("Activity/Fragment", "Widget", "Dialog", "Menu")
    val checkedItems = booleanArrayOf(false, false, false, false)
    // 测试通过
    builder.setMultiChoiceItems(items, checkedItems) { _, which, isChecked ->
      if (isChecked) {
        toast(items[which])
      }
    }
    // 测试通过
    builder.setNegativeButton("Normal Cancel") { _, _ -> toast("Normal Cancel") }
    builder.setPositiveButton("Lambda Ok") { _, _ -> toast("Lambda Ok") }
    val dialog = builder.create()
    dialog.getButton(DialogInterface.BUTTON_POSITIVE)
    dialog.show()
  }

  private fun toast(msg: CharSequence) {
    ToastUtils.toast(requireContext(), msg)
  }

  companion object {
    const val TAG = "TrackFragment"
  }
}
