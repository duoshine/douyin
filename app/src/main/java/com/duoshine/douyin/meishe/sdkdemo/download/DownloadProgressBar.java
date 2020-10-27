package com.duoshine.douyin.meishe.sdkdemo.download;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;


/**
 * Created by czl on 2018/6/25.
 * 素材下载进度条
 * Material download progress bar
 */

public class DownloadProgressBar extends ProgressBar {
    private Paint mPaint;
    private String mProgress;
    private Context mContext;
    public DownloadProgressBar(Context context) {
        super(context);
        mContext = context;
        initText();
    }
    public DownloadProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initText();
    }


    public DownloadProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initText();
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        setText(progress);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        this.mPaint.getTextBounds(mProgress, 0, mProgress.length(), rect);
        float textSize = ScreenUtils.sp2px(mContext,12);
        mPaint.setTextSize(textSize);
        int x = getWidth() / 2 - rect.centerX();
        int y = getHeight() / 2 - rect.centerY();
        canvas.drawText(mProgress, x, y, mPaint);
    }

    private void initText(){
        this.mPaint = new Paint();
        this.mPaint.setColor(Color.WHITE);
        mProgress = "";
    }

    /*
    * 设置文字进度值
    * Set text progress value
    * */
    private void setText(int progress){
        int i = (progress * 100)/this.getMax();
        mProgress = String.valueOf(i) + "%";
    }
}
