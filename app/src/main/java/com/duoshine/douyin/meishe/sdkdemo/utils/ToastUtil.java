package com.duoshine.douyin.meishe.sdkdemo.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;


public class ToastUtil {

    /*
     * 文本的toast
     * Text toast
     * */
    private static Toast mTextToast;

    /*
     *  自定义view的 toast
     * Custom view toast
     * */
    private static Toast mViewToast;
    private static Toast mShowCenterToast;


    /*****  Android 7.0  toast crash  解决方案: http://www.10tiao.com/html/223/201801/2651232846/1.html  ****/
//    private static Field sField_TN;
//    private static Field sField_TN_Handler;

//    static {
//        try {
//            sField_TN = Toast.class.getDeclaredField("mTN");
//            sField_TN.setAccessible(true);
//            sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
//            sField_TN_Handler.setAccessible(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 显示Toast
     * Show Toast
     *
     * @param msg
     */
    public static void showToast(Context context, int msg) {
        String str = context.getResources().getString(msg);
        if (TextUtils.isEmpty(str)) {
            return;
        }
        showToast(context, str);
    }

    /**
     * 居中显示Toast
     * Center Toast
     *
     * @param applicationContext
     * @param s
     */
    public static void showToastCenter(Context applicationContext, String s) {
        if (mShowCenterToast == null) {
            mShowCenterToast = Toast.makeText(applicationContext.getApplicationContext(), s, Toast.LENGTH_SHORT);
            mShowCenterToast.setGravity(Gravity.CENTER, 0, 0);
            int tvToastId = Resources.getSystem().getIdentifier("message", "id", "android");
            TextView tvToast = ((TextView) mShowCenterToast.getView().findViewById(tvToastId));
            if (tvToast != null) {
                tvToast.setGravity(Gravity.CENTER);
            }
        } else {
            mShowCenterToast.setText(s);
        }
        mShowCenterToast.show();
    }

    /**
     * 居中颜色背景显示Toast
     * <p>
     * Centered color background shows Toast
     *
     * @param applicationContext
     * @param s
     */
    public static void showToastCenterWithBg(Context applicationContext, String s, String colorTxtString, int colorBgResId) {
        Toast toast = Toast.makeText(applicationContext.getApplicationContext(), "", Toast.LENGTH_SHORT);
        toast.setText(s);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.getView().setBackgroundResource(colorBgResId);
        int tvToastId = Resources.getSystem().getIdentifier("message", "id", "android");
        TextView tvToast = ((TextView) toast.getView().findViewById(tvToastId));
        tvToast.setTextColor(Color.parseColor(colorTxtString));
        tvToast.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        if (tvToast != null) {
            tvToast.setGravity(Gravity.CENTER);
        }
        toast.show();
    }

    /**
     * 显示Toast
     * Show Toast
     *
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        if (context == null) {
            return;
        }
        if (TextUtils.isEmpty(msg)) {
            return;
        }

        if (mTextToast == null)
            mTextToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        mTextToast.setText(msg);
        // 如果是7.0 才去hook
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            hook(mTextToast);
//        }
        mTextToast.show();
    }

//    private static void hook(Toast toast) {
//        try {
//            Object tn = sField_TN.get(toast);
//            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
//            sField_TN_Handler.set(tn, new SafelyHandlerWarpper(preHandler));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static Toast getToast() {
        return mTextToast == null ? mViewToast : mTextToast;
    }

    public static void releaseInstance() {
        mTextToast = null;
        mViewToast = null;
    }


    private static class SafelyHandlerWarpper extends Handler {

        private Handler impl;

        public SafelyHandlerWarpper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);//需要委托给原Handler执行
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
            }
        }
    }
}
