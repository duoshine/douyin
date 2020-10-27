package com.duoshine.douyin.meishe.sdkdemo.utils.permission;

import android.content.Context;
import android.view.View;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.CommonDialog;
import com.duoshine.douyin.meishe.sdkdemo.utils.AppManager;
import com.duoshine.douyin.meishe.sdkdemo.utils.Util;


/**
 * Created by CaoZhiChao on 2019/1/15 16:20
 */
public class PermissionDialog {
    public static void noPermissionDialog(Context context) {
        String[] permissionsTips = context.getResources().getStringArray(R.array.permissions_tips);
        Util.showDialog(context, permissionsTips[0], permissionsTips[1], new CommonDialog.TipsButtonClickListener() {
            @Override
            public void onTipsButtoClick(View view) {
                AppManager.getInstance().finishActivity();
            }
        });
    }
}
