package com.duoshine.douyin.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.jzvd.Jzvd
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.WorkAdapter
import kotlinx.android.synthetic.main.fragment_city.*
import kotlinx.android.synthetic.main.fragment_city.recyclerView
import kotlinx.android.synthetic.main.works_fragment.*

/**
 *Created by chen on 2020 同城
 */
class CityFragment : BaseFragment() {

    private val TAG = "CityFragment"

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_city, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initJzvd()
    }

    private fun initJzvd() {
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        val adapterVideoList = WorkAdapter(context,1)
        recyclerView.adapter = adapterVideoList
        val parent = parentFragment as? UserInfoFragment
        parent?.addRecyclerViewScrollListener(recyclerView)
        recyclerView.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {
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
}