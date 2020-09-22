package com.duoshine.douyin.ui.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.duoshine.douyin.MainActivity
import com.duoshine.douyin.MainViewModel
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.HomeAdapter
import com.duoshine.douyin.widget.CViewPager
import kotlinx.android.synthetic.main.fragment_home.*
import kotlin.collections.ArrayList

/**
 *Created by chen on 2020 首页
 */
class HomeFragment : BaseFragment() {

    private val TAG = "HomeFragment"
    private var followFragment: PlayerFragment? = null
    private var recommendFragment: PlayerFragment? = null
    private var mainViewModel: MainViewModel? = null

    private var currentPosition = 1

    private val fragments: ArrayList<Fragment> by lazy {
        ArrayList<Fragment>().apply {
            followFragment = PlayerFragment.getPlayerFragment(0)
            recommendFragment = PlayerFragment.getPlayerFragment(1)
            add(followFragment!!)
            add(recommendFragment!!)
            add(UserInfoFragment())
        }
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //设置加载的json文件所用到的资源目录
        animation_view.imageAssetsFolder = "images/"
        initView()
        initViewModel()
    }

    private fun initViewModel() {
        mainViewModel = ViewModelProvider(context as MainActivity).get(MainViewModel::class.java)
        addRefreshListener()
    }

    /**
     * 通过点击底部的Tab触发的刷新  todo 模拟数据加载
     */
    private fun addRefreshListener() {
        /**
         * 推荐刷新事件
         */
        mainViewModel!!.getRefreshState().observe(context as MainActivity,
            Observer<MainViewModel.RefreshState> {
                if (it == MainViewModel.RefreshState.START) {
                    tabLoadingStart()
                } else if (it == MainViewModel.RefreshState.COMPLETE) {
                    tabLoadingEnd()
                }
            })

//        关注刷新事件
        mainViewModel!!.getFollowRefreshState().observe(context as MainActivity,
            Observer<MainViewModel.RefreshState> {
                if (it == MainViewModel.RefreshState.START) {
                    tabLoadingStart()
                } else if (it == MainViewModel.RefreshState.COMPLETE) {
                    tabLoadingEnd()
                }
            })
    }

    //获取当前ViewPage显示索引
    fun getPageIndex() = currentPosition

    private fun initView() {
        //设置隐藏的页面不销毁
        homeViewPager.offscreenPageLimit = 3
//        监听下拉刷新
        homeViewPager.setRefreshListener(object : CViewPager.RefreshListener {
            override fun refreshPrepare(offsetY: Float) {
                pullLoadingStart(offsetY)
            }

            override fun refresh(offsetY: Float) {
                pullLoadingEnd(offsetY)
            }
        })

        homeViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                //TabLayout不通过适配器关联 因为Tab只有两个
                tabLayout.setScrollPosition(position, positionOffset, false)
                if (position == 1) {
                    animation(positionOffsetPixels.toFloat())
                }
            }

            override fun onPageSelected(position: Int) {
                currentPosition = position
            }
        })
        homeViewPager.adapter =
            HomeAdapter(childFragmentManager, fragments)
        homeViewPager.currentItem = 1
    }

    /**
     * 当home显示或隐藏时 视频的播放处理
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        //true隐藏 false显示 传递给子View实现切换时停止恢复视频播放
        if (getPageIndex() == 0) {
            if (hidden) {
                followFragment?.playWhenUnready()
            } else {
                followFragment?.playWhenReady()
            }
        } else if (getPageIndex() == 1) {
            if (hidden) {
                recommendFragment?.playWhenUnready()
            } else {
                recommendFragment?.playWhenReady()
            }
        }
    }

    /**
     * 向个人信息页面滑动时 TabLayout应该执行平移动画
     */
    private fun animation(to: Float) {
        val a = ObjectAnimator.ofFloat(tabLayout, "translationX", -to)
        a.duration = 0
        a.start()
    }

    private fun tabLoadingStart() {
        search_view.alpha = 0f
        pull_text_view.alpha = 0f
        animation_view.alpha = 1f
    }

    private fun tabLoadingEnd() {
        animation_view.alpha = 0f
        pull_text_view.alpha = 0f
        search_view.alpha = 1f
    }

    /**
     * 页面在下拉时触发的动画
     */
    private fun pullLoadingStart(offset: Float) {
        if (offset > 150) {
            return
        }
        val translationY =
            ObjectAnimator.ofFloat(header_tab_layout, "translationY", offset) //移动整个Tab 跟随偏移值
        val alpha =
            ObjectAnimator.ofFloat(
                header_tab_layout,
                "alpha",
                1 - (offset * 0.01).toFloat()
            ) //整个Tab逐渐透明 跟随偏移值
        val refreshAnimationViewTranslationY =
            ObjectAnimator.ofFloat(animation_view, "translationY", offset)//json动画平移 跟随偏移值
        val pullTextViewTranslationY =
            ObjectAnimator.ofFloat(pull_text_view, "translationY", offset)//刷新提示的文字平移 跟随偏移值
        val refreshAnimationViewAlpha =
            ObjectAnimator.ofFloat(
                animation_view,
                "alpha",
                -0.5f + (offset * 0.01).toFloat()
            ) //json动画逐渐显示 跟随偏移值
        val pullTextViewAlpha =
            ObjectAnimator.ofFloat(
                pull_text_view,
                "alpha",
                -0.5f + (offset * 0.01).toFloat()
            )//刷新提示的文字逐渐显示 跟随偏移值
        val animator = AnimatorSet()
        animator.playTogether(
            translationY,
            alpha,
            refreshAnimationViewTranslationY,
            refreshAnimationViewAlpha,
            pullTextViewTranslationY,
            pullTextViewAlpha
        )
        animator.duration = 0
        animator.start()
    }

    /**
     * 下拉松手时触发的动画 并触发刷新
     */
    private fun pullLoadingEnd(offsetY: Float) {
        //显示动画 隐藏下拉刷新
        val translationY = ObjectAnimator.ofFloat(header_tab_layout, "translationY", 0f)//将tab页恢复原点
        val alpha = ObjectAnimator.ofFloat(header_tab_layout, "alpha", 1.0f)//将tab页显示
        val refreshAnimationViewTranslationY =
            ObjectAnimator.ofFloat(animation_view, "translationY", 0f)//将刷新的json动画恢复原点
        val pullTextViewTranslationY =
            ObjectAnimator.ofFloat(pull_text_view, "translationY", 0f)//将刷新的文字提示恢复原点
        val animator = AnimatorSet()
        animator.playTogether(
            translationY,
            alpha,
            refreshAnimationViewTranslationY,
            pullTextViewTranslationY
        )
        animator.duration = 200
        animator.start()
        //触发刷新 此时滑动距离超过阈值才满足刷新条件
        if (offsetY > 150) {
            //开启刷新之前上一次刷新必须处于完成状态
            Log.d(TAG, "可以开启刷新")
            mainViewModel?.startRefresh(getPageIndex())
        } else {
            //不满足下拉刷新的条件 显示默认界面
            tabLoadingEnd()
        }
    }
}