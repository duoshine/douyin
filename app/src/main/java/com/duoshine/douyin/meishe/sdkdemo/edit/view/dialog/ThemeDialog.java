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


public class ThemeDialog extends Dialog {
    private Context mContext;
    private TextView mTvTittle;
    private Button mBtCancel;
    private Button mBtConfirm;
    OnBtnClickListener mClickListener;

    public ThemeDialog(Context context) {
        super(context, R.style.dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_theme);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        mTvTittle = findViewById(R.id.tv_dialog_tittle);
        mBtCancel = findViewById(R.id.bt_dialog_cancel);
        mBtConfirm = findViewById(R.id.bt_dialog_confirm);
    }

    public void setTittleText(String text) {
        mTvTittle.setText(text);
        mBtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mBtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.OnConfirmClick(view);
                }
                dismiss();
            }
        });
    }

    public void setOnBtnClickListener(OnBtnClickListener listener) {
        mClickListener = listener;
    }


    public interface OnBtnClickListener {
        void OnConfirmClick(View view);
    }
}

