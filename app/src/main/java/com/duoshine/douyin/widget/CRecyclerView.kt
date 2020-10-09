package com.duoshine.douyin.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 *Created by chen on 2020
 */
class CRecyclerView : RecyclerView {

    private var TAG = "CRecyclerView"

    private var listener: DownListener? = null

    interface DownListener {
        fun down(x: Float, y: Float)
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0){
        init()
    }

    private fun init() {
        this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Log.d(TAG, "onScrolled")
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                Log.d(TAG, "onScrollStateChanged")
                /**
                 * 滚动结束后判断TOPView偏移值
                 * 如果偏移值为0
                 * 根据滑动的速度的值来决定TopView放大的值，滑动的速度越快,View放大的值越大
                 *
                 */
                Log.d(TAG, "newState:$newState")
                if (newState == SCROLL_STATE_IDLE) {

                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    constructor(context: Context, attrs: AttributeSet?, defAttr: Int) : super(
        context,
        attrs,
        defAttr
    )

    fun setDownListener(listener: DownListener) {
        this.listener = listener
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        /**
         * 将DOWN事件获取的坐标发出去由外部使用
         */
        if (e.action == MotionEvent.ACTION_DOWN) {
            listener?.down(e.x, e.y)
        }
        return super.onInterceptTouchEvent(e)
    }



}