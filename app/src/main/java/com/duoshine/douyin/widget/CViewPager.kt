package com.duoshine.douyin.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager

/**
 * 实现下拉刷新
 */
class CViewPager : ViewPager {
    private val TAG = "HomeFragment"

    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        //注意第三页是用户信息页面 不参与下拉刷新  不传递任何事件
        if (this.currentItem == 2) {
            return super.onInterceptTouchEvent(ev)
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                endX = ev.x
                endY = ev.y
                val offsetY = endY - startY
                val offsetX = endX - startX
                if (!canScrollVertically() && offsetY > 0 && Math.abs(offsetX) < Math.abs(offsetY)) {
                    return true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        //注意第三页是用户信息页面 不参与下拉刷新  不传递任何事件
        if (this.currentItem == 2) {
            return super.onTouchEvent(ev)
        }
        when (ev.action) {
            MotionEvent.ACTION_MOVE -> {
                endX = ev.x
                endY = ev.y
                val offsetY = endY - startY
                val offsetX = endX - startX
                if (!canScrollVertically() && offsetY > 0 && Math.abs(offsetX) < Math.abs(offsetY)) {
                    //将满足下拉刷新的状态回调给外部 也就是HomeFragment  再由HomeFragment触发下拉刷新的动画及ViewModel的刷新数据的监听
                    refreshListener?.refreshPrepare(offsetY)
                }
            }
            else -> {
                val offsetY = endY - startY
                refreshListener?.refresh(offsetY)
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun canScrollVertically(): Boolean {
        if (this.childCount > 0) {
            val child = this.getChildAt(0) as FrameLayout
            val viewPager = child.getChildAt(0)
            return viewPager.canScrollVertically(-1)
        }
        return true
    }

    private var refreshListener: RefreshListener? = null

    interface RefreshListener {
        fun refreshPrepare(offsetY: Float)
        fun refresh(offsetY: Float)
    }

    fun setRefreshListener(listener: RefreshListener) {
        refreshListener = listener
    }
}