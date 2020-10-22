package com.duoshine.douyin.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.duoshine.douyin.R

/**
 *Created by chen on 2020  录制视频
 */
class VideoFragment : BaseFragment() {
    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_video, container, false)
    }
}