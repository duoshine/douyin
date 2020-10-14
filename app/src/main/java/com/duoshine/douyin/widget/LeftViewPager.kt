package com.duoshine.douyin.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 *Created by chen on 2020
 * 是否拦截move事件交由外部决定
 */
class LeftViewPager : ViewPager {
    private val TAG = "LeftViewPager"

    private var startX = 0f
    private var endX = 0f

    private var listener: InterceptTouchEventListener? = null

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "onInterceptTouchEvent: 0")
                endX = ev.x
                val offsetX = endX - startX
                return if (listener?.intercept() == 1 && offsetX < 0) {
                    Log.d(TAG, "onInterceptTouchEvent: 1")
                    true
                } else if (listener?.intercept() == 0) {
                    Log.d(TAG, "onInterceptTouchEvent: 2")
                    super.onInterceptTouchEvent(ev)
                } else {
                    Log.d(TAG, "onInterceptTouchEvent: 3")
                    false
                }
            }
        }
        Log.d(TAG, "onInterceptTouchEvent: 4:${ev.action}")
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        Log.d(TAG, "onInterceptTouchEvent: 5:${ev.action}")
        if (listener?.currentItem() == 0) {
            return super.onTouchEvent(ev)
        }
        return false
    }

    interface InterceptTouchEventListener {
        /**
         * 0 处于用户信息界面 不可以直接拦截事件 用户信息界面有ViewPager 交于ViewPager自己处理
         * 1 根据方向拦截
         */
        fun intercept(): Int
        fun currentItem(): Int
    }

    fun setInterceptListener(listener: InterceptTouchEventListener) {
        this.listener = listener
    }
}