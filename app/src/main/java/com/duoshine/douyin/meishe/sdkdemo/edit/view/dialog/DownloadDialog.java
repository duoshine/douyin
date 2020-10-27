package com.duoshine.douyin.meishe.sdkdemo.edit.view.dialog;

/**
 * Created by ms on 2019/9/20.
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.duoshine.douyin.R;


public class DownloadDialog extends Dialog {
    private Context mContext;
    private TextView mTipsTextView;
    private OnBtnClickListener mClickListener;
    private ProgressBar mProgressBar;

    public DownloadDialog(Context context) {
        super(context, R.style.dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_download);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        mTipsTextView = (TextView) findViewById(R.id.tipsText);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public void setTipsText(String text) {
        mTipsTextView.setText(text);
    }

    public void setOnBtnClickListener(OnBtnClickListener listener) {
        mClickListener = listener;
    }

    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

    public void setMax(int max) {
        mProgressBar.setMax(max);
    }

    public interface OnBtnClickListener {
        void OnCancelBtnClicked(View view);
    }
}

