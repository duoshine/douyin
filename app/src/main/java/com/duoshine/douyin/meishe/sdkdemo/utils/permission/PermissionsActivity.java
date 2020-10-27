package com.duoshine.douyin.meishe.sdkdemo.utils.permission;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PermissionsActivity extends AppCompatActivity {
    /*
    * 权限授权
    * Authorization
    * */
    public static final int PERMISSIONS_GRANTED = 0;
    /*
    *  权限拒绝
    * Permission denied
    * */
    public static final int PERMISSIONS_DENIED = 1;
    /*
    * 权限不再提示
    * Permission is no longer prompted
    * */
    public static final int PERMISSIONS_No_PROMPT = 2;
    /*
    *  系统权限管理页面的参数
    * Parameters of the system rights management page
    * */
    private static final int PERMISSION_REQUEST_CODE = 0; //
    public static final String EXTRA_PERMISSIONS =
            "com.meicam.sdkdemo.utils.permission.extra_permission";
    /*
    * 权限检测器
    * Permission detector
    * */
    private PermissionsChecker mChecker;


    /*
    *  启动当前权限页面的公开接口
    * Launch the public interface of the current permission page
    * */
    public static void startActivityForResult(Activity activity, int requestCode, String... permissions) {
        Intent intent = new Intent(activity, PermissionsActivity.class);
        intent.putExtra(EXTRA_PERMISSIONS, permissions);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSIONS)) {
            throw new RuntimeException("PermissionsActivity需要使用静态startActivityForResult方法启动!");
        }
        mChecker = new PermissionsChecker(this);
        String[] permissions = getPermissions();
        if (mChecker.lacksPermissions(permissions)) {
            /*
            * 请求权限
            * Request permission
            * */
            requestPermissions(permissions);
        } else {
            /*
            * 全部权限都已获取
            * All permissions have been acquired
            * */
            allPermissionsGranted();
        }
    }

    /*
    * 返回传递的权限参数
    * Returns passed permission parameters
    * */
    private String[] getPermissions() {
        return getIntent().getStringArrayExtra(EXTRA_PERMISSIONS);
    }

    /*
    * 请求权限兼容低版本
    * Request permission compatible with lower version
    * */
    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    /*
    * 全部权限均已获取
    * All permissions have been acquired
    * */
    private void allPermissionsGranted() {
        setResult(PERMISSIONS_GRANTED);
        finish();
    }

    /**
     * 用户权限处理。如果全部获取, 则直接过，如果权限缺失, 则提示Dialog.
     * .
     * User rights processing. If all of them are obtained, they will be passed directly. If the permissions are missing, Dialog will be prompted.
     *
     * @param requestCode  Request code
     * @param permissions  Permission list
     * @param grantResults result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int mResultCode = -1;
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        /*
                        * 判断是否勾选禁止后不再询问
                        * Don't ask again after judging whether to check
                        * */
                        boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                        if (showRequestPermission) {
                            mResultCode = PERMISSIONS_DENIED;
                        } else {
                            /*
                            *  被禁止，不再访问
                            * Banned and no longer accessible
                            * */
                            mResultCode = PERMISSIONS_No_PROMPT;
                            break;
                        }
                    }
                }
                if (mResultCode == -1) {
                    mResultCode = PERMISSIONS_GRANTED;
                }
                setActivityResult(mResultCode);
                break;
        }

    }

    private void setActivityResult(int code) {
        setResult(code);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }
}
