package com.duoshine.douyin.meishe.sdkdemo.edit.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.duoshine.douyin.R;


public class CircleProgressBar2 extends View {
    private Paint paint_bg;
    private Paint paint_progress;
    private Paint textPaint;
    /*
    * 圆环颜色
    * Ring color
    * */
    private int ringColor;
    /*
    * 字体颜色
    * font color
    * */
    private int textColor;
    /*
    * 圆环背景颜色
    * Ring background color
    * */
    private int ringBgColor;
    /*
    * 半径
    * radius
    * */
    private float radius;
    /*
    * 圆环宽度
    * Ring width
    * */
    private float strokeWidth;
    /*
    * 文字的长度
    * Text length
    * */
    private float txtWidth;
    /*
    * 文字的高度
    * Text height
    * */
    private float txtHeight;
    /*
    * 字体大小
    * font size
    * */
    private float txtSize;
    /*
    * 总进度
    * total progress
    * */
    private int totalProgress = 100;
    /*
    * 当前进度
    * Current progress
    * */
    private int currentProgress;
    /*
    * 是否绘制中心进度文字
    * Whether to draw center progress text
    * */
    private boolean mPaintText = true;
    /*
    * 超出背景圆环的宽度
    * Beyond the width of the background ring
    * */
    //
    private int span = 1;

    public CircleProgressBar2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initVariable();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressbar, 0, 0);
        radius = typeArray.getDimension(R.styleable.CircleProgressbar_radius2, 80);
        strokeWidth = typeArray.getDimension(R.styleable.CircleProgressbar_strokeWidth, 10);
        ringColor = typeArray.getColor(R.styleable.CircleProgressbar_ringColor, 0xFF0000);
        ringBgColor = typeArray.getColor(R.styleable.CircleProgressbar_ringBgColor, 0xFF0000);
        textColor = typeArray.getColor(R.styleable.CircleProgressbar_textColor, 0xFFFFFF);
        mPaintText = typeArray.getBoolean(R.styleable.CircleProgressbar_centerText, true);
        txtSize = typeArray.getDimension(R.styleable.CircleProgressbar_textSize, radius / 2);
        typeArray.recycle();
    }

    private void initVariable() {
        paint_bg = new Paint();
        paint_bg.setAntiAlias(true);
        paint_bg.setDither(true);
        paint_bg.setColor(ringColor);
        paint_bg.setStyle(Paint.Style.STROKE);
        paint_bg.setStrokeCap(Paint.Cap.ROUND);
        paint_bg.setStrokeWidth(strokeWidth);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(textColor);
        textPaint.setTextSize(txtSize);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        txtHeight = fm.descent + Math.abs(fm.ascent);

        paint_progress = new Paint();
        paint_progress.setAntiAlias(true);
        paint_progress.setDither(true);
        paint_progress.setColor(ringColor);
        paint_progress.setStyle(Paint.Style.STROKE);
        paint_progress.setStrokeCap(Paint.Cap.ROUND);
        paint_progress.setStrokeWidth(strokeWidth + 2 * span);
    }

    /**
     * 当layout大小变化后会回调次方法，通过这方法获取宽高
     * When the size of the layout changes, the secondary method will be called back, and the width and height will be obtained by this method.
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (currentProgress >= 0) {
            /*
            * 绘制背景圆环
            * Draw background ring
            * */
            paint_bg.setColor(ringBgColor);
            int width = getWidth() / 2;
            int height = getHeight() / 2;
            canvas.drawCircle(width, height, radius, paint_bg);
            System.out.println("radius  " +radius);

            /*
            * 绘制进度条
            * Draw progress bar
            * */
            paint_progress.setColor(ringColor);
            RectF oval = new RectF(width - radius, height - radius, width + radius, height + radius);
//            RectF oval = new RectF(width - radius + span, height - radius + span, width + radius - span, height + radius - span);
            canvas.drawArc(oval, 0, 0, false, paint_progress);
            canvas.drawArc(oval, -90, ((float) currentProgress / totalProgress) * 360, false, paint_progress);
            /*
            * 绘制进度值
            * Draw progress value
            * */
            if(mPaintText) {
                String txt = currentProgress + "%";
                txtWidth = textPaint.measureText(txt, 0, txt.length());
                canvas.drawText(txt, width - txtWidth / 2, height + txtHeight / 4, textPaint);
            }
        }
    }

    public void setProgress(int progress) {
        currentProgress = progress;
        postInvalidate();
    }
}
