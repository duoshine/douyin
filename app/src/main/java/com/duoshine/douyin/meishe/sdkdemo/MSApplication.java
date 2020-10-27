package com.duoshine.douyin.meishe.sdkdemo;


import android.app.Application;
import android.content.Context;

import com.duoshine.douyin.meishe.sdkdemo.utils.Logger;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.meicam.sdk.NvsStreamingContext;

/**
 * Created by ${gexinyu} on 2018/5/24.
 */

public class MSApplication extends Application {
    private static Context mContext;

    public static Context getmContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate( );
        //初始化环信
        initHuanXin();
        Logger.e("MSApplication", "onCreate");
        mContext = getApplicationContext( );
        /*
        * 初始化
        * initialization
        * */
        String licensePath = "assets:/meishesdk.lic";
        NvsStreamingContext.init(mContext, licensePath, NvsStreamingContext.STREAMING_CONTEXT_FLAG_SUPPORT_4K_EDIT);
        NvAssetManager.init(mContext);

        /*
        * 友盟初始化
        * Umeng initialization
        * */
//        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
        /*
        *  组件化的Log是否输出 默认关闭Log输出。和集成测试是一个开关，release要关闭
        * Whether the componentized log is output The log output is turned off by default.
        *  And integration testing is a switch, release should be closed
        * */
//        UMConfigure.setLogEnabled(true);
        // isEnable: false-关闭错误统计功能；true-打开错误统计功能（默认打开）
//        public static void setCatchUncaughtExceptions(boolean isEnable)
        /*
        * 场景类型设置
        * Scene type settings
        * */
//        MobclickAgent.setScenarioType(mContext, MobclickAgent.EScenarioType.E_UM_NORMAL);
//        Fresco.initialize(this);
    }

    private void initHuanXin() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 是否自动将消息附件上传到环信服务器，默认为True是使用环信服务器上传下载，如果设为 false，需要开发者自己处理附件消息的上传和下载
        options.setAutoTransferMessageAttachments(true);
        // 是否自动下载附件类消息的缩略图等，默认为 true 这里和上边这个参数相关联
        options.setAutoDownloadThumbnail(true);
        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
    }
}
