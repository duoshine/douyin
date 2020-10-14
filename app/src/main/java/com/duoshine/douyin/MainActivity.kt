package com.duoshine.douyin

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.duoshine.douyin.adapter.HomeAdapter
import com.duoshine.douyin.constants.UserConstants
import com.duoshine.douyin.ui.fragment.*
import com.duoshine.douyin.widget.LeftViewPager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var leftFragment: LeftGroupFragment? = null

    private val fragments: ArrayList<Fragment> by lazy {
        leftFragment = LeftGroupFragment()
        ArrayList<Fragment>().apply {
            add(leftFragment!!)
            add(UserInfoFragment.getUserInfoFragment(UserConstants.userId))
        }
    }
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)//设置屏幕常亮
        initStatus()
        initView()
    }

    private fun initView() {
        view_pager.adapter =
            HomeAdapter(supportFragmentManager, fragments)

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                Log.d(TAG, " view_pager.adapter.count：${view_pager.adapter!!.count}")
            }

            override fun onPageSelected(position: Int) {

            }
        })

        view_pager.setInterceptListener(object : LeftViewPager.InterceptTouchEventListener {
            override fun intercept(): Int {
                //当位于首页时  当首页处于显示推荐时
                if (view_pager.currentItem == 1) {
                    return 0
                } else if (leftFragment!!.getPageIndex()) {
                    return 1
                }
                return 2
            }

            override fun currentItem(): Int {
                return leftFragment!!.getPagePosition()
            }
        })
    }

    private fun initStatus() {
        //沉浸式
        val decorView: View = window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        decorView.systemUiVisibility = option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(Color.TRANSPARENT)
        }
    }
}