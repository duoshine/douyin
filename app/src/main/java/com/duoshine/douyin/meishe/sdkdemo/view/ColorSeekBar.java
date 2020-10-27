package com.duoshine.douyin.meishe.sdkdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;


public class ColorSeekBar extends View {
    private Paint mColorPaint = new Paint();
    private OnColorChangedListener mOnColorChangedListener;
    //private int[] mColors = {Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW};
    private int top;
    private int topMargin;
    private int left;
    private int mHalfStrokeWidth;
    float rawX;
    private int[] mColors = new int[]{0xFFFF0000, 0xFFFF00FF,
            0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000};
    private int mColor;
    private int mStartX;

    public void setOnColorChangedListener(OnColorChangedListener onColorChangedListener) {
        this.mOnColorChangedListener = onColorChangedListener;
    }

    public ColorSeekBar(Context context) {
        super(context);
    }


    public void setColors(int[] mColors, float colorsProgress) {
        //this.mColors = mColors;
        mStartX = left;
        if (colorsProgress < 0) {
            rawX = ScreenUtils.dip2px(getContext(), 5.0f);
        } else {
            rawX = colorsProgress;
        }
        invalidate();
    }

    public ColorSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.TRANSPARENT);
        initData();
        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        top = ScreenUtils.dip2px(getContext(), 12f);
        topMargin = ScreenUtils.dip2px(getContext(), 2.0f);
        left = ScreenUtils.dip2px(getContext(), 7.0f);
        mHalfStrokeWidth = ScreenUtils.dip2px(getContext(), 12f) / 2;
        rawX = left;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mStartX = left;
        mColorPaint.setAntiAlias(true);
        mColorPaint = new Paint();
        LinearGradient linearGradient = new LinearGradient(left, 0, getMeasuredWidth() - left, 0, mColors, null, Shader.TileMode.CLAMP);
        mColorPaint.setShader(linearGradient);
        mColorPaint.setStrokeWidth(mHalfStrokeWidth * 2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(left, top, getMeasuredWidth() - left, getMeasuredHeight() - top, mHalfStrokeWidth, mHalfStrokeWidth, mColorPaint);
        } else {
            canvas.drawLine(left, top + mHalfStrokeWidth + topMargin, getMeasuredWidth() - left, top + mHalfStrokeWidth + topMargin, mColorPaint);
        }

        float startX = rawX;
        float startY = top + mHalfStrokeWidth * 2 + topMargin;
        Path path = new Path();
        path.lineTo(startX, startY);
        path.lineTo(startX - left, startY + top);
        path.lineTo(startX + left, startY + top);
        path.lineTo(startX, startY);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                getColorInPosition(event);
                break;

        }
        return true;
    }

    private void getColorInPosition(MotionEvent event) {
        rawX = event.getX();
        if (rawX <= left) {
            rawX = left;
        }
        if (rawX >= getMeasuredWidth() - left) {
            rawX = getMeasuredWidth() - left;
        }
        float unit = (rawX - mStartX) / (getMeasuredWidth() - mHalfStrokeWidth);
        mColor = interpColor(mColors, unit);
        if (mOnColorChangedListener != null) {
            mOnColorChangedListener.onColorChanged(mColor);
        }
        invalidate();
    }

    private void initView() {

    }


    private int ave(int s, int d, float p) {
        return s + Math.round(p * (d - s));
    }

    private int interpColor(int colors[], float unit) {
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {
            return colors[colors.length - 1];
        }

        float p = unit * (colors.length - 1);
        int i = (int) p;
        p -= i;

        int c0 = colors[i];
        int c1 = colors[i + 1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        return Color.argb(a, r, g, b);
    }

    private void initData() {
    }

    public interface OnColorChangedListener {
        void onColorChanged(int color);
    }
}
