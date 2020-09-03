package com.duoshine.douyin.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.duoshine.douyin.R
import kotlinx.android.synthetic.main.fragment_city.*

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
    }
}