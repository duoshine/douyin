package com.duoshine.douyin.widget

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 *Created by chen on 2020
 */
class CommentReplyRecyclerView : RecyclerView {

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return false
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        return false
    }
}