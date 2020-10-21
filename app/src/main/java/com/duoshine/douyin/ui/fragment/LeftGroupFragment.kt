package com.duoshine.douyin.ui.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.duoshine.douyin.MainViewModel
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.HomeAdapter
import com.duoshine.douyin.constants.UserConstants
import com.google.android.material.tabs.TabLayout
import com.hyphenate.EMContactListener
import com.hyphenate.chat.EMClient
import kotlinx.android.synthetic.main.fragment_home_parent.*

/**
 *Created by chen on 2020
 */
class LeftGroupFragment : BaseFragment() {
    private val TAG = "HomeParentFragment"

    private var homeFragment: HomeFragment? = null
    private var cityFragment: Fragment? = null
    private var videoFragment: Fragment? = null
    private var messageFragment: Fragment? = null
    private var meFragment: Fragment? = null

    private var mainViewModel: MainViewModel? = null

    private var animator: ObjectAnimator? = null

    private var currentPosition = 0

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_home_parent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initFragment()
        initViewModel()
    }

    private fun initView() {
        //第一个item透明度为0 播放视频 当然也是应用的默认显示界面
        tabLayout.background.alpha = 0
        /**
         * 第三个item使用自定义的Tab
         */
        val tab = tabLayout.getTabAt(2)
        val customView =
            LayoutInflater.from(context).inflate(R.layout.main_tabview_tab, null, false)
        tab!!.customView = customView

        /**
         * 监听TabLayout的选择事件 隐藏和显示对应fragment
         */
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                val position = tab.position
                if (position == 0 || position == 1) {
                    tabReselected(tab)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val tabView = tab.view
                /**
                 * 它的回调发生在onTabSelected之前
                 */
                when (tab.position) {
                    0 -> hideUnSelectedFragment(homeFragment!!, tabView)
                    1 -> hideUnSelectedFragment(cityFragment!!, tabView)
                    2 -> hideUnSelectedFragment(videoFragment!!, tabView)
                    3 -> hideUnSelectedFragment(messageFragment!!, tabView)
                    4 -> hideUnSelectedFragment(meFragment!!, tabView)
                }
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                val tabView = tab.view
                currentPosition = tab.position
                when (tab.position) {
                    0 -> {
                        showSelectedFragment(homeFragment!!, tabView)
                        tabLayout.background.alpha = 0
                    }
                    1 -> {
                        showSelectedFragment(cityFragment!!, tabView)
                        tabLayout.background.alpha = 200
                    }
                    2 -> {
                        showSelectedFragment(videoFragment!!, tabView)
                        tabLayout.background.alpha = 200
                    }
                    3 -> {
                        showSelectedFragment(messageFragment!!, tabView)
                        tabLayout.background.alpha = 200
                    }
                    4 -> {
                        showSelectedFragment(meFragment!!, tabView)
                        tabLayout.background.alpha = 200
                    }
                }
            }
        })
    }

    private fun initFragment() {
        homeFragment = HomeFragment()
        cityFragment = CityFragment()
        videoFragment = VideoFragment()
        messageFragment = MessageFragment()
        meFragment = UserInfoFragment.getUserInfoFragment(UserConstants.userId)

        //默认显示homeFragment
        showSelectedFragment(homeFragment!!, tabLayout.getTabAt(0)!!.view)
    }

    private fun initViewModel() {
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        /**
         * 监听下拉刷新的状态变化，对动画执行显示和隐藏操作
         */
        mainViewModel!!.getRefreshState().observe(this,
            Observer<MainViewModel.RefreshState> {
                if (it == MainViewModel.RefreshState.START) {

                } else if (it == MainViewModel.RefreshState.COMPLETE) {//错误或成功都需要结束动画
                    animator?.end()
                }
            })

        //监听关注的刷新事件
        mainViewModel!!.getFollowRefreshState().observe(this,
            Observer<MainViewModel.RefreshState> {
                if (it == MainViewModel.RefreshState.START) {

                } else if (it == MainViewModel.RefreshState.COMPLETE) {//错误或成功都需要结束动画
                    animator?.end()
                }
            })
    }

    /**
     * 显示点击的fragment页面
     */
    private fun showSelectedFragment(fragment: Fragment, tab: TabLayout.TabView) {
        /**
         * 对显示的TabView执行放大动作
         */
        tab.apply {
            val x = ObjectAnimator.ofFloat(this, "scaleX", 1.1f)
            val y = ObjectAnimator.ofFloat(this, "scaleY", 1.1f)
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(x, y)
            animatorSet.duration = 150
            animatorSet.start()
        }

        fragmentManager!!.beginTransaction().apply {
            if (!fragment.isAdded) {
                add(R.id.frameLayout, fragment)
            } else {
                show(fragment)
            }
            commit()
        }
    }

    /**
     * 隐藏当前正在显示的fragment页面
     */
    private fun hideUnSelectedFragment(
        fragment: Fragment,
        tab: TabLayout.TabView
    ) {
        /**
         * 对隐藏的TabView执行缩小动画
         */
        val x = ObjectAnimator.ofFloat(tab, "scaleX", 1.0f)
        val y = ObjectAnimator.ofFloat(tab, "scaleY", 1.0f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(x, y)
        animatorSet.duration = 150
        animatorSet.start()

        fragmentManager!!.beginTransaction().apply {
            hide(fragment).commit()
        }
    }

    /**
     * 重复点击触发下拉刷新 在重复动画的回调中结束动画
     * @see[Animator.AnimatorListener.onAnimationRepeat] 它一直被回调
     */
    private fun tabReselected(tab: TabLayout.Tab) {
        val position = tab.position
        tab.text = ""
        tab.setIcon(R.mipmap.main_footer_refresh)
        val tabView = tab.view
        if (animator?.isRunning == true) {
            Log.d(TAG, "已处于刷新状态")
            return
        }
        animator = ObjectAnimator.ofFloat(tabView, "rotation", 0f, +360f)
        animator?.apply {
            repeatCount = -1
            interpolator = LinearInterpolator()
            duration = 700
            start()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator) {
                    //等待网络加载的结果 成功或失败后停止动画 将icon替换为文本

                }

                override fun onAnimationEnd(animation: Animator?) {
                    val position = tab.position
                    if (position == 0) {
                        tab.text = resources.getString(R.string.main_footer_home)
                    } else {
                        tab.text = resources.getString(R.string.main_footer_city)
                    }
                    tab.icon = null
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationStart(animation: Animator?) {
                }
            })
            if (position == 0) {
                Log.d(TAG, "触发下拉刷新")
                homeRefresh()
            }
        }
    }

    /**
     * 通过点击底部的Tab触发的刷新  todo 模拟数据加载
     */
    private fun homeRefresh() {
        mainViewModel!!.startRefresh(homeFragment?.getPageIndex())
    }

    fun getPageIndex(): Boolean {
        return currentPosition == 0 && homeFragment?.getPageIndex() == 1
    }

    fun getPagePosition(): Int {
        return currentPosition
    }
}