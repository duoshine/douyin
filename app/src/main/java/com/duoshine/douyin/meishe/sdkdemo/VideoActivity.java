package com.duoshine.douyin.meishe.sdkdemo;

import android.content.Intent;
import android.view.View;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.base.BasePermissionActivity;
import com.duoshine.douyin.meishe.sdkdemo.capture.CaptureActivity;

import java.util.List;

public class VideoActivity extends BasePermissionActivity {


    @Override
    protected int initRootView() {
        return R.layout.activity_video;
    }

    @Override
    protected void initViews() {
        findViewById(R.id.skip_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VideoActivity.this, CaptureActivity.class));
            }
        });
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected List<String> initPermissions() {
        return null;
    }

    @Override
    protected void hasPermission() {

    }

    @Override
    protected void nonePermission() {

    }

    @Override
    protected void noPromptPermission() {

    }

    @Override
    public void onClick(View v) {

    }
}