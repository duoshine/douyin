package com.duoshine.douyin.meishe.sdkdemo.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.VerticalSeekBar;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;


public class VerticalIndicatorSeekBar extends RelativeLayout {
    private Context mContext;
    private View mTextViewLayout;
    private OnSeekBarChangedListener mOnSeekBarChangedListener;
    private VerticalSeekBar mSeekBar;
    private TextView mTextView;
    private int mSeekBarWidth = 0;

    public void setOnSeekBarChangedListener(OnSeekBarChangedListener listener) {
        mOnSeekBarChangedListener = listener;
    }

    public void setProgress(final int progress) {
        mSeekBar.setProgress(progress);
        mTextView.setText(String.valueOf(progress));
        if(mSeekBarWidth == 0){
            mSeekBar.post(new Runnable() {
                @Override
                public void run() {
                    setTextLocation(mSeekBar, progress);
                }
            });
        }else{
            setTextLocation(mSeekBar, progress);
        }
        //mSeekBar.onSizeChanged(mSeekBar.getWidth(), mSeekBar.getHeight(), 0, 0);
    }

    public VerticalIndicatorSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_indicator_vertical_seek_bar, this);
        mTextViewLayout = rootView.findViewById(R.id.seek_text_layout);
        mTextView = rootView.findViewById(R.id.seek_text);
        mSeekBar = rootView.findViewById(R.id.seekBar);
        mSeekBar.setThumb(R.mipmap.round_white);
        mSeekBar.setThumbSizeDp(18, 18);
        mSeekBar.setSelectColor(Color.parseColor("#ffffffff"));
        mSeekBar.setmInnerProgressWidthDp(3);
        mSeekBar.setOnSlideChangeListener(new VerticalSeekBar.SlideChangeListener() {
            @Override
            public void onStart(VerticalSeekBar slideView, int progress) {
                if (mOnSeekBarChangedListener != null) {
                    mOnSeekBarChangedListener.onStartTrackingTouch(slideView);
                }
            }

            @Override
            public void onProgress(VerticalSeekBar slideView, int progress) {
                int text = progress;
                //设置文本显示
                mTextView.setText(String.valueOf(text));
                setTextLocation(slideView, progress);

                if (mOnSeekBarChangedListener != null) {
                    mOnSeekBarChangedListener.onProgressChanged(slideView, progress, true);
                }
            }

            @Override
            public void onStop(VerticalSeekBar slideView, int progress) {
                if (mOnSeekBarChangedListener != null) {
                    mOnSeekBarChangedListener.onStopTrackingTouch(slideView);
                }
            }
        });
    }

    private void setTextLocation(VerticalSeekBar seekBar, int progress) {
        //获取文本宽度
        float textHeight = mTextView.getHeight();

        //获取seekbar最底端位置
        float bottom = seekBar.getBottom();

        //进度条的刻度值
        float max = Math.abs(seekBar.getMaxProgress());

        //这不叫thumb的宽度,叫seekbar距左边宽度,seekbar 不是顶格的，两头都存在一定空间，所以xml 需要用paddingStart 和 paddingEnd 来确定具体空了多少值,
        float thumb = ScreenUtils.dip2px(mContext, 15);

        //每移动1个单位，text应该变化的距离 = (seekBar的高度 - 两头空的空间) / 总的progress长度
        float average = (((float) seekBar.getHeight()) - 2 * thumb) / max;
        float currentProgress = progress;

        //textview 应该所处的位置 = seekbar最左端 + seekbar左端空的空间 + 当前progress应该加的长度 - textview宽度的一半(保持居中作用)
        float poy = bottom - textHeight / 2 - thumb - average * currentProgress;
        mTextViewLayout.setY(poy);
    }

    public interface OnSeekBarChangedListener {
        void onProgressChanged(VerticalSeekBar seekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(VerticalSeekBar seekBar);

        void onStopTrackingTouch(VerticalSeekBar seekBar);
    }
}