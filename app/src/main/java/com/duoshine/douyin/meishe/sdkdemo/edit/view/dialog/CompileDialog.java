package com.duoshine.douyin.meishe.sdkdemo.edit.view.dialog;

/**
 * Created by ms on 2019/9/20.
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.CircleProgressBar2;


public class CompileDialog extends Dialog {
    private Context mContext;
    private TextView mTipsTextView;
    private Button mCandelBtn;
    private CircleProgressBar2 mCircleProgressBar;
    private OnBtnClickListener mClickListener;

    public CompileDialog(Context context) {
        super(context, R.style.dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_compile);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        mCandelBtn = (Button) findViewById(R.id.cancelCompileBtn);
        mCircleProgressBar = (CircleProgressBar2) findViewById(R.id.circleProgressBar);
        mTipsTextView = (TextView) findViewById(R.id.tipsText);

        mCandelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mClickListener != null) {
                    mClickListener.OnCancelBtnClicked(view);
                }
            }
        });
    }

    public void setTipsText(String text) {
        mTipsTextView.setText(text);
    }

    public void setOnBtnClickListener(OnBtnClickListener listener) {
        mClickListener = listener;
    }

    public void setProgress(int progress) {
        mCircleProgressBar.setProgress(progress);
    }

    public interface OnBtnClickListener {
        void OnCancelBtnClicked(View view);
    }
}

