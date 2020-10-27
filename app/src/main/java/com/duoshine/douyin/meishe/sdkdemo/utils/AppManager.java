package com.duoshine.douyin.meishe.sdkdemo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Stack;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Newnet on 2017/1/9.
 */

public class AppManager {

    private static Stack<Activity> activityStack = new Stack<>();
    private volatile static AppManager instance = new AppManager();

    private AppManager() {
    }

    /**
     * 单一实例
     *
     * Single instance
     */
    public static AppManager getInstance() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     *
     * Add Activity to the stack
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     *
     * Get the current Activity (the last one pushed in the stack)
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     *
     * End the current Activity (the last one pushed in the stack)
     */
    public void finishActivity() {
        if(activityStack.empty())
            return;

        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }


    /**
     * 待结果回调的activity
     *
     * Activity to be called back as a result
     *
     * @param intent
     * @param activity
     * @param bundle
     */
    public void finishActivityByCallBack(Intent intent, Activity activity, Bundle bundle) {
        intent.putExtras(bundle);
        activity.setResult(RESULT_OK, intent);
        AppManager.getInstance().finishActivity(activity);
    }


    /**
     * 结束指定的Activity
     *
     * End the specified Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     * End Activity with specified class name
     *
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     * End all Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    public int activietySizes() {
        if (null != activityStack) {
            return activityStack.size();
        }
        return 0;
    }


    /**
     * 页面跳转
     * Page jump
     * @param activity
     * @param cls
     * @param bundle
     */
    public void jumpActivity(Activity activity, Class<? extends Activity> cls, Bundle bundle) {
        Intent intent = new Intent(activity, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);
    }

    public void jumpActivity(Activity activity, Class<? extends Activity> cls) {
        jumpActivity(activity,cls,null);
    }

    /**
     * 页面跳转，有结果返回
     * Page jump, return with results
     * @param activity
     * @param cls
     * @param bundle
     * @param requstcode
     */
    public void jumpActivityForResult(Activity activity, Class<? extends Activity> cls, Bundle bundle, int requstcode) {
        Intent intent = new Intent(activity, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requstcode);
    }


    /**
     * 返回当前stack的大小
     * Returns the size of the current stack
     */
    public int getActivitySize() {
        if (null != activityStack) {
            return activityStack.size();
        }
        return 0;
    }


    /**
     * 退出应用程序
     * Quit the application
     */
    private long exitTime = 0;

    @SuppressWarnings("deprecation")
    public void safetyExitApp(Context context) {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(context.getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            try {
                finishAllActivity();
                // 杀死该应用进程
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            } catch (Exception e) {
            }
        }
    }
}