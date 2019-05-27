package com.sleticalboy.transform

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sleticalboy.transform.bean.Image
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private val fragments = SparseArray<Fragment>()
    private val fragId = 1
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // 测试通过
        fab.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            var fragment = fragments.get(fragId)
            if (fragment == null) {
                fragment = TrackFragment()
                transaction.add(R.id.flContainer, fragment)
                fragments.put(fragId, fragment)
            }
            if (currentFragment == fragment) {
                return@setOnClickListener
            }
            transaction.show(fragment)
            currentFragment = fragment
            transaction.commitNow()
            it.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // 测试通过
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                addTextView()
                return true
            }
            R.id.list_view -> {
                addListView()
                return true
            }
            R.id.grid_view -> {
                addGridView()
                return true
            }
            R.id.track_demo -> {
                startActivity(Intent(application, TrackActivity::class.java))
                return true
            }
            R.id.recycler_view -> {
                addRecyclerView()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addRecyclerView() {
        flContainer.removeAllViews()
        val recyclerView = RecyclerView(this)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = SimpleArrayAdapter(getImages())
        val params = FrameLayout.LayoutParams(-1, -1)
        params.gravity = Gravity.CENTER
        flContainer.addView(recyclerView, params)
    }

    private fun addGridView() {
        flContainer.removeAllViews()
        val gv = GridView(this)
        gv.numColumns = 3
        val data = getImageRes()
        gv.adapter = object : BaseAdapter() {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                var recycle = convertView
                if (recycle == null) {
                    Log.d("getView", "new ImageView")
                    recycle = ImageView(this@MainActivity)
                }
                if (recycle is ImageView) {
                    recycle.setImageResource(getItem(position))
                }
                return recycle
            }

            override fun getItem(position: Int): Int = data[position]

            override fun getItemId(position: Int): Long = position.toLong()

            override fun getCount(): Int = data.size
        }
        gv.setOnItemClickListener { _, _, position, _ ->
            ToastUtils.toast(this, "position: $position")
        }
        val params = FrameLayout.LayoutParams(-1, -1)
        params.gravity = Gravity.CENTER
        flContainer.addView(gv, params)
    }

    private fun getImages(): List<Image> {
        return listOf(
            Image(null, "Image 00", R.mipmap.ic_launcher),
            Image(null, "Image 01", R.mipmap.ic_launcher),
            Image(null, "Image 02", R.mipmap.ic_launcher),
            Image(null, "Image 03", R.mipmap.ic_launcher),
            Image(null, "Image 04", R.mipmap.ic_launcher),
            Image(null, "Image 05", R.mipmap.ic_launcher),
            Image(null, "Image 06", R.mipmap.ic_launcher),
            Image(null, "Image 07", R.mipmap.ic_launcher),
            Image(null, "Image 08", R.mipmap.ic_launcher),
            Image(null, "Image 09", R.mipmap.ic_launcher),
            Image(null, "Image 10", R.mipmap.ic_launcher),
            Image(null, "Image 11", R.mipmap.ic_launcher),
            Image(null, "Image 12", R.mipmap.ic_launcher),
            Image(null, "Image 13", R.mipmap.ic_launcher),
            Image(null, "Image 14", R.mipmap.ic_launcher),
            Image(null, "Image 15", R.mipmap.ic_launcher),
            Image(null, "Image 16", R.mipmap.ic_launcher),
            Image(null, "Image 17", R.mipmap.ic_launcher),
            Image(null, "Image 18", R.mipmap.ic_launcher),
            Image(null, "Image 19", R.mipmap.ic_launcher),
            Image(null, "Image 20", R.mipmap.ic_launcher),
            Image(null, "Image 21", R.mipmap.ic_launcher),
            Image(null, "Image 22", R.mipmap.ic_launcher),
            Image(null, "Image 23", R.mipmap.ic_launcher),
            Image(null, "Image 24", R.mipmap.ic_launcher),
            Image(null, "Image 25", R.mipmap.ic_launcher)
        )
    }

    private fun getImageRes(): Array<Int> = arrayOf(
        R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
        R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
        R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
        R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
        R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
        R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
        R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
        R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
        R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
        R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher
    )

    private fun addListView() {
        flContainer.removeAllViews()
        val lv = ListView(this)
        val data = getData()
        lv.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data)
        lv.setOnItemClickListener { _, _, position, _ ->
            ToastUtils.toast(this, "data: ${data[position]} pos: $position")
        }
        val params = FrameLayout.LayoutParams(-1, -1)
        params.gravity = Gravity.CENTER
        flContainer.addView(lv, params)
    }

    private fun getData(): Array<String> = arrayOf(
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
        "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"
    )

    private fun addTextView() {
        flContainer.removeAllViews()
        val helloWorld = TextView(this)
        helloWorld.text = "Hello World"
        helloWorld.textSize = 36F
        helloWorld.setTextColor(resources.getColor(R.color.colorPrimary))
        helloWorld.setOnClickListener {
            ToastUtils.toast(this, helloWorld.text)
        }
        val params = FrameLayout.LayoutParams(-2, -2)
        params.gravity = Gravity.CENTER
        flContainer.addView(helloWorld, params)
    }

    // 不需要特殊处理，因为 View 在初始化的过程中会通过反射得到声明在布局文件中的 android:onClick="xxx" 方法
    // 并设置给 View， 详情参见源码 androidx.appcompat.app.AppCompatViewInflater.DeclaredOnClickListener
    // 或者 android.support.v7.app.AppCompatViewInflater.DeclaredOnClickListener
    // 测试通过
    fun xmlClick(view: View) {
        ToastUtils.toast(view.context, "xml click")
    }
}
