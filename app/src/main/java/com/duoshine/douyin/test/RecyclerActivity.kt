package com.duoshine.douyin.test


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import cn.jzvd.Jzvd
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.WorkAdapter
import kotlinx.android.synthetic.main.activity_recycler_test.*


class RecyclerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(true)
//        supportActionBar?.setDisplayUseLogoEnabled(false)
//        supportActionBar?.title = "列表视频"
        setContentView(R.layout.activity_recycler_test)
        init()
    }

    private fun init() {
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val adapterVideoList = WorkAdapter(this,0)
        recyclerView.adapter = adapterVideoList
        recyclerView.addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {}
            override fun onChildViewDetachedFromWindow(view: View) {
                val jzvd: Jzvd = view.findViewById(R.id.videoplayer)
                if (jzvd != null && Jzvd.CURRENT_JZVD != null &&
                    jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.currentUrl)
                ) {
                    if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                        Jzvd.releaseAllVideos()
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
