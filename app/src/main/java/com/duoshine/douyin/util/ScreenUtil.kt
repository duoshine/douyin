package com.duoshine.douyin.util

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager




class ScreenUtil {
    companion object {
        //获取屏幕高度
        fun getScreenHeight(context: Context): Int {
            //从系统服务中获取窗口管理器

            //从系统服务中获取窗口管理器
            val wm =
                context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            //从默认显示显示器中获取显示参数保存到dm对象中
            //从默认显示显示器中获取显示参数保存到dm对象中
            wm.defaultDisplay.getMetrics(dm)
            return dm.heightPixels //返回屏幕的高度数值

        }
    }
}