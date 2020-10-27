package com.duoshine.douyin.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.duoshine.douyin.R
import com.duoshine.douyin.meishe.sdkdemo.capture.CaptureActivity

/**
 *Created by chen on 2020  录制视频
 */
class VideoFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(context, CaptureActivity::class.java))
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_video1, container, false)
    }
}