package com.duoshine.douyin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager

import com.duoshine.douyin.adapter.HomeAdapter
import com.duoshine.douyin.constants.UserConstants
import com.duoshine.douyin.ui.activity.ChatActivity
import com.duoshine.douyin.ui.fragment.LeftGroupFragment
import com.duoshine.douyin.ui.fragment.UserInfoFragment
import com.duoshine.douyin.widget.LeftViewPager
import com.google.android.material.snackbar.Snackbar
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private var leftFragment: LeftGroupFragment? = null

    private var msgListener: EMMessageListener? = null

    private val fragments: ArrayList<Fragment> by lazy {
        leftFragment = LeftGroupFragment()
        ArrayList<Fragment>().apply {
            add(leftFragment!!)
            add(UserInfoFragment.getUserInfoFragment(UserConstants.userId))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)//设置屏幕常亮
        initStatus()
        initView()
        requestPermission()
    }

    /**
     * 请求授权
     */
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val permissionslist = java.util.ArrayList<String>()
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionslist.add(permission)
                }
            }
            if (permissionslist.size != 0) {
                val permissionsArray = permissionslist.toTypedArray()
                ActivityCompat.requestPermissions(
                    this, permissionsArray,
                    22
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        //为了防止三个权限在循环中开启多个设置界面
        var isShow = false
        when (requestCode) {
            22 -> {
                for (i in grantResults.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED && !isShow) {
                        isShow = true
                        startPermissionActivity()
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /*
   跳转到设置授权的界面
    */
    private fun startPermissionActivity() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 23)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //不管是否进行了授权 这边都结束运行
        if (requestCode == 23) {
            finish()
        }
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

    /**
     * 这个Activity都是沉浸式，主要是为了用户信息的图片沉浸式，其他fragment使用占位符代替状态栏位置
     * 占位符使用StatusBarView 它会兼容不同机型高度的状态栏
     */
    private fun initStatus() {
        //沉浸式
        val decorView: View = window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        decorView.systemUiVisibility = option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        记得在不需要的时候移除listener，如在activity的onDestroy()时
        EMClient.getInstance().chatManager().removeMessageListener(msgListener)
    }
}