package com.duoshine.douyin.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import java.lang.Math.abs

/**
 *Created by chen on 2020
 * 是否拦截move事件交由外部决定
 */
class LeftViewPager : ViewPager {
    private val TAG = "LeftViewPager"

    private var startX = 0f
    private var endX = 0f

    private var startY = 0f
    private var endY = 0f

    private var listener: InterceptTouchEventListener? = null

    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "------1------")
                startX = ev.x
                startY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                endX = ev.x
                endY = ev.y
                val offsetX = endX - startX
                val offsetY = endY - startY
                //处于首页-推荐时 此时用户可以上下滑动视频，此时当用户往左滑动 且x值大于y值时 拦截事件往用户信息滑动
                return if (listener?.intercept() == 1 && offsetX < 0 && kotlin.math.abs(offsetX) > kotlin.math.abs(
                        offsetY
                    )
                ) {
                    //表示当前处于推荐 且往左滑动 此时事件拦截自己消费
                    true
                    //处于用户信息界面时，由于用户信息界面内嵌套的还有ViewPager，所以不可以拦截事件，不然嵌套的
                    //ViewPager将无法滑动，此时事件交由ViewPager自己去判断是否拦截
                } else if (listener?.intercept() == 0) {
                    super.onInterceptTouchEvent(ev)
                } else {
                    //此时即处于非首页页面 或处于首页但是不是左右滑动而是上下滑动，此时不可以拦截事件，
                    //如果处于首页，嵌套的Viewpager2需要上下滑动显示视频，如果是其他页面那么它们也不需要滑动此ViewPager
                    false
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
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