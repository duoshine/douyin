package com.duoshine.douyin.test


import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import cn.jzvd.Jzvd
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.WorkAdapter
import kotlinx.android.synthetic.main.activity_recycler_test.*
import kotlinx.android.synthetic.main.fragment_userinfo.*


class RecyclerActivity : AppCompatActivity() {

    private val TAG = "RecyclerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_test)

        recyclerView.setOnClickListener {
            val top = IntArray(2)
            recyclerView.getLocationInWindow(top)
            val height = recyclerView.height
            val y = top[1]

            Log.d(TAG, "top:${top[0]}-${top[1]}")
            Log.d(TAG, "height:$height")
        }
    }
}
