package com.duoshine.douyin.meishe.sdkdemo.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class AutoFocusTextView extends AppCompatTextView {
    public AutoFocusTextView(Context context) {
        super(context);
    }

    public AutoFocusTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFocusTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public boolean isFocused() {
        // TODO Auto-generated method stub
        //textView 在recyclerview中实现滚动效果，需要获取焦点，
        return true;
    }

}
