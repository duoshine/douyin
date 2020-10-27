package com.duoshine.douyin.meishe.sdkdemo.selectmedia.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

/**
 * 自定义PopWindow类，封装了PopWindow的一些常用属性，用Builder模式支持链式调用，参考：https://github.com/pinguo-zhouwei/CustomPopwindow
 *
 *Custom PopWindow class, which encapsulates some common properties of PopWindow, and supports chained calls in Builder mode.
 *  Reference: https://github.com/pinguo-zhouwei/CustomPopwindow
 */

public class CustomPopWindow implements PopupWindow.OnDismissListener {
    private static final String TAG = "CustomPopWindow";
    private static final float DEFAULT_ALPHA = 0.7f;
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private boolean mIsFocusable = true;
    private boolean mIsOutside = true;
    private int mResLayoutId = -1;
    private View mContentView;
    private PopupWindow mPopupWindow;
    private int mAnimationStyle = -1;

    private boolean mClippEnable = true;//default is true
    private boolean mIgnoreCheekPress = false;
    private int mInputMode = -1;
    private PopupWindow.OnDismissListener mOnDismissListener;
    private int mSoftInputMode = -1;
    private boolean mTouchable = true;//default is ture
    private View.OnTouchListener mOnTouchListener;

    /*
    * 当前Activity 的窗口
    * Window of current activity
    * */
    private Window mWindow;
    /**
     * 弹出PopWindow 背景是否变暗，默认不会变暗。
     * PopPopWindow Whether the background is darkened, it will not be darkened by default.
     */
    private boolean mIsBackgroundDark = false;

    /*
    * 背景变暗的值，0 - 1
    * Background dimming value, 0-1
    * */
    private float mBackgroundDrakValue = 0;
    /**
     * 设置是否允许点击 PopupWindow之外的地方，关闭PopupWindow
     * Set whether to allow clicking outside of PopupWindow, close PopupWindow
     */
    private boolean enableOutsideTouchDisMiss = true;

    /**
     * 要监听的控件id
     * Id of the control to listen to
     */
    private int[] viewIds;
    private OnViewClickListener listener;

    private CustomPopWindow(Context context) {
        mContext = context;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    /**
     * @param anchor
     * @param xOff
     * @param yOff
     * @return
     */
    public CustomPopWindow showAsDropDown(View anchor, int xOff, int yOff) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(anchor, xOff, yOff);
        }
        return this;
    }

    public CustomPopWindow showAsDropDown(View anchor) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(anchor);
        }
        return this;
    }

    public boolean isShowing(){
        if (mPopupWindow != null) {
            return mPopupWindow.isShowing();
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public CustomPopWindow showAsDropDown(View anchor, int xOff, int yOff, int gravity) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(anchor, xOff, yOff, gravity);
        }
        return this;
    }


    /**
     * 相对于父控件的位置（通过设置Gravity.CENTER，下方Gravity.BOTTOM等 ），可以设置具体位置坐标
     *
     * Relative to the position of the parent control (by setting Gravity.CENTER, below Gravity.BOTTOM, etc.), you can set specific position coordinates
     *
     * @param parent  父控件；Parent control
     * @param gravity
     * @param x       the popup's x location offset
     * @param y       the popup's y location offset
     * @return
     */
    public CustomPopWindow showAtLocation(View parent, int gravity, int x, int y) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(parent, gravity, x, y);
        }
        return this;
    }

    /**
     * 添加一些属性设置
     *
     * Add some property settings
     *
     * @param popupWindow
     */
    private void apply(PopupWindow popupWindow) {
        popupWindow.setClippingEnabled(mClippEnable);
        if (mIgnoreCheekPress) {
            popupWindow.setIgnoreCheekPress();
        }
        if (mInputMode != -1) {
            popupWindow.setInputMethodMode(mInputMode);
        }
        if (mSoftInputMode != -1) {
            popupWindow.setSoftInputMode(mSoftInputMode);
        }
        if (mOnDismissListener != null) {
            popupWindow.setOnDismissListener(mOnDismissListener);
        }
        if (mOnTouchListener != null) {
            popupWindow.setTouchInterceptor(mOnTouchListener);
        }
        popupWindow.setTouchable(mTouchable);


    }

    private void setChildViewClick(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            if (vp.getId() != -1) {
                vp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onViewClick(v);
                    }
                });
            }
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                if (checkViewCanClick(viewchild)) {
                    viewchild.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onViewClick(v);
                        }
                    });
                }
                setChildViewClick(viewchild);
            }
        }
    }

    private void onViewClick(View v) {
        /*
        * 注意：只要按任何一个控件的id,弹窗都会消失，不管是确定还是取消。
        * Note: As long as you press the id of any control, the pop-up window will disappear, whether it is OK or canceled.
        * */
        dissmiss();
        listener.onViewClick(CustomPopWindow.this, v);
    }

    private boolean checkViewCanClick(View viewchild) {
        if (viewchild.getId() == -1) {
            return false;
        }
        if (viewchild instanceof TextView) {
            return true;
        }
        if (viewchild instanceof Button) {
            return true;
        }
        if (viewchild instanceof LinearLayout) {
            return true;
        }
        return false;
    }

    private PopupWindow build() {

        if (mContentView == null) {
            mContentView = LayoutInflater.from(mContext).inflate(mResLayoutId, null);
        }
        if (listener != null) {
            setChildViewClick(mContentView);
        }

        Activity activity = (Activity) mContentView.getContext();
        if (activity != null && mIsBackgroundDark) {
            /*
            * 如果设置的值在0 - 1的范围内，则用设置的值，否则用默认值
            * If the set value is in the range of 0-1, the set value is used, otherwise the default value is used
            * */
            final float alpha = (mBackgroundDrakValue > 0 && mBackgroundDrakValue < 1) ? mBackgroundDrakValue : DEFAULT_ALPHA;
            mWindow = activity.getWindow();
            WindowManager.LayoutParams params = mWindow.getAttributes();
            params.alpha = alpha;
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mWindow.setAttributes(params);
        }


        if (mWidth != 0 && mHeight != 0) {
            mPopupWindow = new PopupWindow(mContentView, mWidth, mHeight);
        } else {
            mPopupWindow = new PopupWindow(mContentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        if (mAnimationStyle != -1) {
            mPopupWindow.setAnimationStyle(mAnimationStyle);
        }

        /*
        * 设置一些属性
        * Set some properties
        * */
        apply(mPopupWindow);

        try {
            if (mWidth == 0 || mHeight == 0) {
                mPopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                /*
                * 如果外面没有设置宽高的情况下，计算宽高并赋值
                * If no width and height are set outside, calculate the width and height and assign a value
                * */
                mWidth = mPopupWindow.getContentView().getMeasuredWidth();
                mHeight = mPopupWindow.getContentView().getMeasuredHeight();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "build: "+e.toString());
        }

        /*
        * 添加dissmiss 监听
        * Add dissmiss listener
        * */
        mPopupWindow.setOnDismissListener(this);

        /*
        * 判断是否点击PopupWindow之外的地方关闭 popWindow
        * Determine whether to close popWindow by clicking outside of PopupWindow
        * */
        if (!enableOutsideTouchDisMiss) {
            /*
            * 注意这三个属性必须同时设置，不然不能disMiss，以下三行代码在Android 4.4 上是可以，
            * 然后在Android 6.0以上，下面的三行代码就不起作用了，就得用下面的方法
            *
            *
            * Note that these three properties must be set at the same time, otherwise they cannot be disMiss. The following three lines of code are available on Android 4.4, and then on Android 6.0 and above,
            * the following three lines of code will not work. You must use the following method
            * */
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(false);
            mPopupWindow.setBackgroundDrawable(null);
            /*
            * 注意下面这三个是contentView 不是PopupWindow
            *
            * Note that these three are contentView, not PopupWindow
            * */
            mPopupWindow.getContentView().setFocusable(true);
            mPopupWindow.getContentView().setFocusableInTouchMode(true);
            mPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mPopupWindow.dismiss();

                        return true;
                    }
                    return false;
                }
            });
            /*
            * 在Android 6.0以上 ，只能通过拦截事件来解决
            * On Android 6.0 and above, it can only be solved by intercepting events
            *
            * */
            mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    final int x = (int) event.getX();
                    final int y = (int) event.getY();

                    if ((event.getAction() == MotionEvent.ACTION_DOWN)
                            && ((x < 0) || (x >= mWidth) || (y < 0) || (y >= mHeight))) {
                        Log.e(TAG, "out side ");
                        Log.e(TAG, "width:" + mPopupWindow.getWidth() + "height:" + mPopupWindow.getHeight() + " x:" + x + " y  :" + y);
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        Log.e(TAG, "out side ...");
                        return true;
                    }
                    return false;
                }
            });
        } else {
            mPopupWindow.setFocusable(mIsFocusable);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mPopupWindow.setOutsideTouchable(mIsOutside);
        }
        // update
        mPopupWindow.update();

        return mPopupWindow;
    }

    @Override
    public void onDismiss() {
        dissmiss();
    }

    /**
     * 关闭popWindow
     */
    public void dissmiss() {

        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss();
        }

        /*
        * 如果设置了背景变暗，那么在dissmiss的时候需要还原
        * If the background is set to darken, then it needs to be restored when dissmiss
        * */
        if (mWindow != null) {
            WindowManager.LayoutParams params = mWindow.getAttributes();
            params.alpha = 1.0f;
            mWindow.setAttributes(params);
        }
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public static class PopupWindowBuilder {
        private CustomPopWindow mCustomPopWindow;

        public PopupWindowBuilder(Context context) {
            mCustomPopWindow = new CustomPopWindow(context);
        }

        public PopupWindowBuilder size(int width, int height) {
            mCustomPopWindow.mWidth = width;
            mCustomPopWindow.mHeight = height;
            return this;
        }


        public PopupWindowBuilder setFocusable(boolean focusable) {
            mCustomPopWindow.mIsFocusable = focusable;
            return this;
        }


        public PopupWindowBuilder setView(int resLayoutId) {
            mCustomPopWindow.mResLayoutId = resLayoutId;
            mCustomPopWindow.mContentView = null;
            return this;
        }

        public PopupWindowBuilder setView(View view) {
            mCustomPopWindow.mContentView = view;
            mCustomPopWindow.mResLayoutId = -1;
            return this;
        }

        public PopupWindowBuilder setOutsideTouchable(boolean outsideTouchable) {
            mCustomPopWindow.mIsOutside = outsideTouchable;
            return this;
        }

        /**
         * 设置弹窗动画
         * Animate the popup
         * @param animationStyle
         * @return
         */
        public PopupWindowBuilder setAnimationStyle(int animationStyle) {
            mCustomPopWindow.mAnimationStyle = animationStyle;
            return this;
        }


        public PopupWindowBuilder setClippingEnable(boolean enable) {
            mCustomPopWindow.mClippEnable = enable;
            return this;
        }


        public PopupWindowBuilder setIgnoreCheekPress(boolean ignoreCheekPress) {
            mCustomPopWindow.mIgnoreCheekPress = ignoreCheekPress;
            return this;
        }

        public PopupWindowBuilder setInputMethodMode(int mode) {
            mCustomPopWindow.mInputMode = mode;
            return this;
        }

        public PopupWindowBuilder setOnDissmissListener(PopupWindow.OnDismissListener onDissmissListener) {
            mCustomPopWindow.mOnDismissListener = onDissmissListener;
            return this;
        }

        public PopupWindowBuilder setViewClickListener(OnViewClickListener onDissmissListener) {
            mCustomPopWindow.listener = onDissmissListener;
            return this;
        }


        public PopupWindowBuilder setSoftInputMode(int softInputMode) {
            mCustomPopWindow.mSoftInputMode = softInputMode;
            return this;
        }


        public PopupWindowBuilder setTouchable(boolean touchable) {
            mCustomPopWindow.mTouchable = touchable;
            return this;
        }

        public PopupWindowBuilder setTouchIntercepter(View.OnTouchListener touchIntercepter) {
            mCustomPopWindow.mOnTouchListener = touchIntercepter;
            return this;
        }

        /**
         * 设置背景变暗是否可用
         *
         * Set whether background dimming is available
         *
         * @param isDark
         * @return
         */
        public PopupWindowBuilder enableBackgroundDark(boolean isDark) {
            mCustomPopWindow.mIsBackgroundDark = isDark;
            return this;
        }

        /**
         * 设置背景变暗的值
         *
         * Set the background dimming value
         *
         * @param darkValue
         * @return
         */
        public PopupWindowBuilder setBgDarkAlpha(float darkValue) {
            mCustomPopWindow.mBackgroundDrakValue = darkValue;
            return this;
        }

        /**
         * 设置是否允许点击 PopupWindow之外的地方，关闭PopupWindow
         *
         * Set whether to allow clicking outside of PopupWindow, close PopupWindow
         * @param disMiss
         * @return
         */
        public PopupWindowBuilder enableOutsideTouchableDissmiss(boolean disMiss) {
            mCustomPopWindow.enableOutsideTouchDisMiss = disMiss;
            return this;
        }


        public CustomPopWindow create() {
            /*
            * 构建PopWindow
            * Building PopWindow
            * */
            mCustomPopWindow.build();
            return mCustomPopWindow;
        }

    }

    public interface OnViewClickListener {
        void onViewClick(CustomPopWindow popWindow, View view);
    }
}
