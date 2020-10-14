package com.duoshine.douyin.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import cn.jzvd.JzvdStd

/**
 *Created by chen on 2020
 */
class CJzvdStd : JzvdStd {

    private val TAG = "CJzvdStd"

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)

    override fun clickPoster() {
        Log.d(TAG, "clickPoster")
    }
}