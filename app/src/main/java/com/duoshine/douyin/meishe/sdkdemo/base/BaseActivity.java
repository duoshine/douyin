package com.duoshine.douyin.meishe.sdkdemo.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.duoshine.douyin.meishe.sdkdemo.utils.AppManager;
import com.duoshine.douyin.meishe.sdkdemo.utils.Logger;
import com.duoshine.douyin.meishe.sdkdemo.utils.Util;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.meicam.sdk.NvsStreamingContext;


/**
 * @author Newnet
 * @date 2017/1/6
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "BaseActivity";
    private NvAssetManager mAssetManager;
    public NvsStreamingContext mStreamingContext;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            Logger.e(TAG,"onCreate1111111111");
            finish();
            return;
        }
        mStreamingContext = getStreamingContext();
        mStreamingContext.stop();
//        mAssetManager = getNvAssetManager();
        //把当前初始化的activity加入栈中
        AppManager.getInstance().addActivity(this);
        //设置视图
        setContentView(initRootView());
        initViews();
        initTitle();
        initData();
        initListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        boolean isBackgroud = Util.isBackground(this);
        if(isBackgroud){
            mStreamingContext.stop();
        }
    }

    public NvsStreamingContext getStreamingContext() {
        if (mStreamingContext == null) {
            synchronized (NvsStreamingContext.class) {
                if (mStreamingContext == null) {
                    mStreamingContext = NvsStreamingContext.getInstance();
                    if (mStreamingContext == null) {
                        String licensePath = "assets:/meishesdk.lic";
                        mStreamingContext = NvsStreamingContext.init(getApplicationContext(), licensePath, NvsStreamingContext.STREAMING_CONTEXT_FLAG_SUPPORT_4K_EDIT);
                    }
                }
            }
        }
        return mStreamingContext;
    }

    public NvAssetManager getNvAssetManager() {
        synchronized (NvAssetManager.class) {
            if (mAssetManager == null) {
                mAssetManager = NvAssetManager.sharedInstance();
                if (mAssetManager == null) {
                    mAssetManager = NvAssetManager.init(this);
                }
            }
        }
        return mAssetManager;
    }
    /*
    * 初始化页面布局Id
    * Initialize activity layout Id.
    */
    protected abstract int initRootView();

    /*
     * 初始化视图组件
     * Initialize the view component.
     */
    protected abstract void initViews();

    /*
     * 初始化头布局
     * Initialize the Title .
     */
    protected abstract void initTitle();

    /*
     * 数据处理
     * Data processing.
     */
    protected abstract void initData();

    /*
     * 视图监听事件处理
     * View listen event handling.
     */
    protected abstract void initListener();

    /*
     * Activity结束,从堆栈中移除
     * Activity ends, removed from stack.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    /*
     * onResume中，注册umeng统计页面跳转和应用时长
     * OnResume, register umeng statistics page jump and application duration.
     */
    @Override
    protected void onResume() {
        super.onResume();
//        统计页面跳转
//        统计应用时长
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}