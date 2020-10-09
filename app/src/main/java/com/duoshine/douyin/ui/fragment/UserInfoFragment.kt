package com.duoshine.douyin.ui.fragment

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.UserInfoTabAdapter
import com.duoshine.douyin.constants.UserConstants
import com.duoshine.douyin.widget.CRecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
import kotlinx.android.synthetic.main.fragment_userinfo.*
import kotlin.math.abs


/**
 *Created by chen on 2020 个人信息和作者信息复用 简称为用户信息
 */
class UserInfoFragment : BaseFragment() {

    private val TAG = "UserInfoFragment"

    private var userId: String? = null

    //    zoomView原本的宽高  在VieW恢复时使用
    private var zoomViewWidth = -1
    private var zoomViewHeight = -1

    //    最大的放大倍数
    private val mScaleTimes = 2f

    //    顶部区域CollapsingToolbarLayout的偏移值 当为0时表示滑动到顶部
    private var verticalOffset = 0

    //    回弹时间系数，系数越小，回弹越快
    private val mReplyRatio = 0.2f

    private var startY = 0f
    private var endY = 0f

    //    滑动放大系数，系数越大，滑动时放大程度越大
    private val mScaleRatio = 0.4f

    private var velocityTracker: VelocityTracker? = null

    //    每秒内的Y轴滑动速度 用来作为判断是否触发回弹
    private var yVelocity: Float? = 0f

    // 速度追踪时 顶部回弹的单次偏移值 值越大 放大的动画抖动的越厉害
    private var conut = 10f

    //速度追踪 当手指滑动RecyclerView的速度超过这个阈值时触发回弹效果 这个值是我自己在测试机上使用的 不具备权威
    private val SCROLL_ANIMATION_THRESHOLD = 6000

    //    回弹任务
    private var handler = Handler()

    //    回弹任务
    private var runnable: Runnable? = null

    private val fragments: MutableList<Fragment> by lazy {
        ArrayList<Fragment>().apply {
            add(WorksFragment())
            add(DynamicFragment())
            add(WorksFragment())
            add(PhotoFragment())
        }
    }

    private val titles: MutableList<String> by lazy {
        ArrayList<String>().apply {
            add(getString(R.string.home_user_works))
            add(getString(R.string.home_user_dynamic))
            add(getString(R.string.home_user_like))
            add(getString(R.string.home_user_photo))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //获取用户id  使用用户id加载用户信息
        userId = arguments?.getString(UserConstants.TO_USER_FRAGMENT_KEY, "-1")
        //速度追踪
        velocityTracker = VelocityTracker.obtain()
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_userinfo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        tab_layout.setupWithViewPager(view_pager)
        view_pager.offscreenPageLimit = 4
        view_pager.adapter = UserInfoTabAdapter(childFragmentManager, fragments, titles)

//        添加AppBarLayout的偏移事件监听 目的是在达到阈值时显示和隐藏ToolBar的背景颜色 记录偏移值 在弹性滑动时作为一个条件,当AppBarLayout达到顶部时 偏移值为0
        appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            this.verticalOffset = verticalOffset
            //根据appbarLayout的偏移值 动态隐藏和显示ToolBar
            if (verticalOffset < -100) {
                tool_bar.setBackgroundColor(Color.BLACK)
            } else {
                tool_bar.setBackgroundColor(Color.TRANSPARENT)
            }
        })

//       下拉回弹效果 为什么要给RecyclerView也添加一个呢，因为它接收不到啊。。。。
        coordinator.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                Log.d(TAG, "coordinator.setOnTouchListener")
                show(event)
                return false
            }
        })

        //加载圆形头像
        Glide
            .with(context!!)
            .load(R.mipmap.avatar2)
            .circleCrop()
            .into(avatar_view)
    }

    /**
     * 给子fragment调用 用来在滑动时做惯性回弹效果
     */
    fun addRecyclerViewScrollListener(recyclerView: CRecyclerView) {
        //监听RecyclerView接收到的Down事件 仅获取点击的坐标 在onTouch方法中 down事件会被它的item接收
        recyclerView.setDownListener(object : CRecyclerView.DownListener {
            override fun down(x: Float, y: Float) {
                startY = y
            }
        })

        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                Log.d(TAG, "recyclerView.setOnTouchListener")
                show(event)
                return false
            }
        })

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                /**
                 * 滚动结束后判断TOPView偏移值
                 * 如果偏移值为0
                 * 根据滑动的速度的值来决定TopView是否放大 比如轻轻的滑动就不放大View
                 */
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { //滑动结束
                    if (verticalOffset == 0 && yVelocity!!.toInt() > SCROLL_ANIMATION_THRESHOLD) {
                        //滑动的总距离 滑动的速度
                        animation()
                    }
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    /**
     * 惯性滑动时 做回弹动画 放大-缩放
     */
    private fun animation() {
        if (runnable == null) {
            runnable = Runnable {
                conut += 200f
                val value1 = setZoom(conut, top_bg)
                val value2 = setZoom(conut, top_bg1)
                if (value1 == -1 || value2 == -1) {
                    conut = 10f
                    //恢复默认宽高
                    replyView(top_bg)
                    replyView(top_bg1)
                    handler.removeCallbacks(runnable)
                } else {
                    handler.post(runnable)
                }
            }
        }
        handler.post(runnable)
    }

    private fun show(event: MotionEvent) {
        //需要放大的View本次事件仅获取一次宽高 记录下来在UP时便于回复默认图片大小
        if (zoomViewWidth == -1 && zoomViewHeight == -1) {
            zoomViewWidth = top_bg.measuredWidth
            zoomViewHeight = top_bg.measuredHeight
        }
        velocityTracker?.addMovement(event)
        /**
         * 当偏移量为0 且滑动值为正数时 执行图片放大
         * 当松手时 图片回复
         */
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //记录下按下的位置
                startY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                endY = event.y
                if (startY == 0f) { //为防止点击TabLayout时获取不到DOWN事件的问题 放大动画会闪烁
                    startY = endY
                }
                val value = endY - startY
                if (value > 0 && verticalOffset == 0) {
                    setZoom(abs(value) * mScaleRatio, top_bg)
                    setZoom(abs(value) * mScaleRatio, top_bg1)
                }
            }
            MotionEvent.ACTION_UP -> {
                //手指抬起时恢复默认值
                replyView(top_bg)
                replyView(top_bg1)
                //获取速度追踪
                velocityTracker?.computeCurrentVelocity(1000)
                yVelocity = velocityTracker?.yVelocity //计算1秒内手指滑动的速度
                velocityTracker?.clear()
                startY = 0f //一定要恢复默认值 下一次正常执行回弹动画
            }
        }
    }

    /**放大view */
    private fun setZoom(s: Float, view: View): Int {
        val scaleTimes = (zoomViewWidth + s) / (zoomViewWidth * 1.0)
        //如超过最大放大倍数，直接返回
        if (scaleTimes > mScaleTimes) return -1
        val layoutParams: ViewGroup.LayoutParams = view.layoutParams
        layoutParams.width = zoomViewWidth + s.toInt()
        layoutParams.height = (zoomViewHeight * ((zoomViewWidth + s) / zoomViewWidth)).toInt()
        //设置控件水平居中
        (layoutParams as ViewGroup.MarginLayoutParams).setMargins(
            -(layoutParams.width - zoomViewWidth) / 2,
            0,
            0,
            0
        )
        view.layoutParams = layoutParams
        return 0
    }

    /**回弹*/
    private fun replyView(view: ImageView) {
        val distance: Float = view.measuredWidth - zoomViewWidth.toFloat()
        // 设置动画
        val anim = ObjectAnimator.ofFloat(distance, 0.0f)
            .setDuration((distance * mReplyRatio).toLong())
        anim.addUpdateListener { animation -> setZoom(animation.animatedValue as Float, view) }
        anim.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        velocityTracker?.recycle()
    }

    companion object {
        /**
         * 它是一个通用的fragment 个人信息与作者信息都可以加载 通过传入用户id加载用户信息
         */
        fun getUserInfoFragment(userId: String): UserInfoFragment {
            val userInfoFragment = UserInfoFragment()
            val bun = Bundle()
            bun.putString(UserConstants.TO_USER_FRAGMENT_KEY, userId)
            userInfoFragment.arguments = bun
            return userInfoFragment
        }
    }
}