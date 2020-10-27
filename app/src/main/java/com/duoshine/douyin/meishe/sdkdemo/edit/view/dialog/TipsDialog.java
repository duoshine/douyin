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


public class TipsDialog extends Dialog {
    private Context mContext;
    private TextView mTipsTextView;
    private Button mCandelBtn, mConfirmBtn;
    private OnBtnClickListener mClickListener;

    public TipsDialog(Context context) {
        super(context, R.style.dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tips);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        mCandelBtn = (Button) findViewById(R.id.cancelBtn);
        mConfirmBtn = (Button) findViewById(R.id.confirmBtn);
        mTipsTextView = (TextView) findViewById(R.id.tipsText);

        mCandelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mClickListener != null) {
                    mClickListener.OnCancelBtnClicked();
                }
            }
        });

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mClickListener != null) {
                    mClickListener.OnConfirmBtnClicked();
                }
            }
        });
    }

    public void setTipsText(int resid) {
        mTipsTextView.setText(resid);
    }

    public void setTipsText(String text) {
        mTipsTextView.setText(text);
    }

    public void setOnBtnClickListener(OnBtnClickListener listener) {
        mClickListener = listener;
    }

    public interface OnBtnClickListener {
        void OnConfirmBtnClicked();
        void OnCancelBtnClicked();
    }
}

