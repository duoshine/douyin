package com.duoshine.douyin.meishe.sdkdemo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.lang.reflect.Method;

/**
 * Created by ${gexinyu} on 2018/5/28.
 */

public class ScreenUtils {

    public static int creenRealHeight;

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager mWm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWm.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return screenWidth;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager mWm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWm.getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        return screenHeight;
    }

    /**
     * 根据手机的分辨率。从dp转为px(像素)
     *
     * According to the resolution of the phone. From dp to px (pixels)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从px(像素)转为dp
     * From px (pixels) to dp according to the resolution of the phone
     *
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * Convert px value to sp value, keep text size unchanged
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * Convert sp value to px value, keep the text size unchanged
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取系统栏的高度
     *
     * Get the height of the system bar
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 设置占比屏幕宽度9/16
     *
     * Set the ratio screen width 9/16
     *
     * @param context
     * @param view
     */
    public static void setNine_Sixteenth(Context context, View view) {
        int width = getWindowWidth(context);
        ViewGroup.LayoutParams para1;
        para1 = view.getLayoutParams();
        para1.height = width * 9 / 16;
        view.setLayoutParams(para1);
    }

    /**
     * 获取屏幕的高度
     *
     * Get screen height
     *
     * @param context
     * @return
     */
    public static final int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    /**
     * 设置占比屏幕宽度1/4
     * Set the ratio of screen width to 1/4
     *
     * @param context
     * @param view
     */
    public static void setOne_Four_H(Context context, View view) {
        int width = getWindowWidth(context);
        ViewGroup.LayoutParams para1;
        para1 = view.getLayoutParams();
        para1.width = width * 1 / 4;
        view.setLayoutParams(para1);
    }

    /**
     * 设置占比屏幕宽度1/4
     * Set the ratio of screen width to 1/4
     *
     * @param context
     * @param view
     */
    public static void setOne_Four_V(Context context, View view) {
        int width = getWindowWidth(context);
        ViewGroup.LayoutParams para1;
        para1 = view.getLayoutParams();
        para1.height = width * 1 / 4;
        view.setLayoutParams(para1);
    }

    /**
     * 设置占比屏幕宽度16/23
     * Set the ratio screen width
     * @param context
     * @param view
     */
    public static void setSixteen_TwentyThree(Context context, View view) {
        int width = getWindowWidth(context);
        ViewGroup.LayoutParams para1;
        para1 = view.getLayoutParams();
        para1.height = width * 23 / 16;
        view.setLayoutParams(para1);
    }

    /**
     * 获取虚拟按键的高度
     * Get the height of a virtual key
     * @param context
     * @return
     */
    public static int getBottomStatusHeight(Context context) {
        int totalHeight = getDpi(context);
        int contentHeight = getWindowHeight(context);
        return totalHeight - contentHeight;
    }

    /*
    * 获取屏幕原始尺寸高度，包括虚拟功能键高度
    * Get the original screen height, including the height of the virtual function keys
    * */
    public static int getDpi(Context context) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    /**
     * 获取屏幕的高度
     * Get screen height
     * @param context
     * @return
     */
    public static final int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    /**
     * 判断手机是否有虚拟按键功能：
     * Determine if the phone has a virtual button function
     * @param activity
     * @return
     */
    public static boolean hasNavigationBar(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        float density = dm.density;

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(realDisplayMetrics);
        } else {
            Class c;
            try {
                c = Class.forName("android.view.Display");
                Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(display, realDisplayMetrics);
            } catch (Exception e) {
                realDisplayMetrics.setToDefaults();
                e.printStackTrace();
            }
        }

        creenRealHeight = realDisplayMetrics.heightPixels;
        int creenRealWidth = realDisplayMetrics.widthPixels;

        float diagonalPixels = (float) Math.sqrt(Math.pow(creenRealWidth, 2) + Math.pow(creenRealHeight, 2));
        float screenSize = (diagonalPixels / (160f * density)) * 1f;

        Resources rs = activity.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        boolean hasNavBarFun = false;
        if (id > 0) {
            hasNavBarFun = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavBarFun = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavBarFun = true;
            }
        } catch (Exception e) {
            hasNavBarFun = false;
        }
        return hasNavBarFun;
    }

    /**
     * 检测虚拟键盘是否开启的
     * Check if the virtual keyboard is enabled
     * @param windowManager
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(WindowManager windowManager) {
        DisplayMetrics dm = new DisplayMetrics();
        Display display = windowManager.getDefaultDisplay();
        display.getMetrics(dm);
        int screenWidth = dm.widthPixels;
        return (creenRealHeight - screenWidth) > 0;//screenRealHeight上面方法中有计算
    }


    /*
    * 获取虚拟按键的高度
    * Get the height of a virtual key
    * */
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 检查是否存在虚拟按键栏
     * Check if there is a virtual key bar
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        boolean hasNav = false;
        if (resourceId != 0) {
            hasNav = res.getBoolean(resourceId);
        }
        return hasNav;
    }

    /**
     * 判断虚拟按键栏是否重写
     * Determine whether the virtual key bar is rewritten
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getMethod("get", String.class);
                sNavBarOverride = (String) m.invoke(c, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }
    public static void setBackGroundGray(Activity context) {
        //产生背景变暗效果
        WindowManager.LayoutParams lp = context.getWindow( ).getAttributes( );
        lp.alpha = 0.4f;
        context.getWindow( ).setAttributes(lp);
    }

    public static void setBackGroundLight(Activity context) {
        WindowManager.LayoutParams lp = context.getWindow( ).getAttributes( );
        lp.alpha = 1f;
        context.getWindow( ).setAttributes(lp);
    }
}
