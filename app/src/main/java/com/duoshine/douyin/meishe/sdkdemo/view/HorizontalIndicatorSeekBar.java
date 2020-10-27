package com.duoshine.douyin.meishe.sdkdemo.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;


public class HorizontalIndicatorSeekBar extends LinearLayout {
    private Context mContext;
    private View mTextViewLayout;
    private OnSeekBarChangedListener mOnSeekBarChangedListener;
    private SeekBar mSeekBar;
    private TextView mTextView;
    private int mSeekBarWidth = 0;

    public HorizontalIndicatorSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_indicator_seek_bar, this);
        mTextViewLayout = rootView.findViewById(R.id.seek_text_layout);
        mTextView = findViewById(R.id.seek_text);
        mSeekBar = findViewById(R.id.seekBar);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int text = progress;
                //设置文本显示
                mTextView.setText(String.valueOf(text));
                setTextLocation(seekBar, progress);

                if (mOnSeekBarChangedListener != null) {
                    mOnSeekBarChangedListener.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mOnSeekBarChangedListener != null) {
                    mOnSeekBarChangedListener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mOnSeekBarChangedListener != null) {
                    mOnSeekBarChangedListener.onStopTrackingTouch(seekBar);
                }
            }
        });
    }

    public void setMaxProgress(int maxProgress) {
        mSeekBar.setMax(maxProgress);
    }

    public void setProgressDrawable(Drawable drawable) {
        mSeekBar.setProgressDrawable(drawable);
    }

    public void setThumb(Drawable drawable) {
        mSeekBar.setThumb(drawable);
    }

    public void setOnSeekBarChangedListener(OnSeekBarChangedListener listener) {
        mOnSeekBarChangedListener = listener;
    }

    public void setProgress(final int progress) {
        mSeekBar.setProgress(progress);
        mTextView.setText(String.valueOf(progress));
        if (mSeekBarWidth == 0) {
            mSeekBar.post(new Runnable() {
                @Override
                public void run() {
                    setTextLocation(mSeekBar, progress);
                }
            });
        } else {
            setTextLocation(mSeekBar, progress);
        }
    }

    private void setTextLocation(SeekBar seekBar, int progress) {
        //获取文本宽度
        float textWidth = mTextView.getWidth();

        //获取seekbar最左端的x位置
        float left = seekBar.getLeft();

        //进度条的刻度值
        float max = Math.abs(seekBar.getMax());

        //这不叫thumb的宽度,叫seekbar距左边宽度,seekbar 不是顶格的，两头都存在一定空间，所以xml 需要用paddingStart 和 paddingEnd 来确定具体空了多少值,
        float thumb = ScreenUtils.dip2px(mContext, 15);

        //每移动1个单位，text应该变化的距离 = (seekBar的宽度 - 两头空的空间) / 总的progress长度
        float average = (((float) seekBar.getWidth()) - 2 * thumb) / max;
        float currentProgress = progress;

        //textview 应该所处的位置 = seekbar最左端 + seekbar左端空的空间 + 当前progress应该加的长度 - textview宽度的一半(保持居中作用)
        float pox = left - textWidth / 2 + thumb + average * currentProgress;
        mTextViewLayout.setX(pox);
    }
    public int getProgress(){
        return mSeekBar.getProgress();
    }


    public interface OnSeekBarChangedListener {
        void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(SeekBar seekBar);

        void onStopTrackingTouch(SeekBar seekBar);
    }
}