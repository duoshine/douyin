package com.duoshine.douyin.meishe.sdkdemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;


/**
 * Created by MZ008064 on 2020/6/4.
 *
 * @author zcy
 * @Date 2020/6/4.
 */
public class ThemeProgress extends View {
    private int progressBackGround;//滚动条背景色
    private int progressWidth;//滚动条宽度
    private int progressForwardGround;//滚动条颜色
    private int thumbColor;//指示器背景色
    private int thumbRadios;//指示器半径
    private int max;//最大值
    private int min;//最小值
    private int progress;//当前值
    private int viewWidth;//布局宽
    private int viewHeight;//布局高
    private Paint progressPaint;//滚动条画笔
    private Paint thumbPaint;//指示器画笔
    private float thumbX;//当前指示器的坐标x
    private float thumbY;//当前指示器的坐标y
    private boolean canMoveThumb = false;//是否可以移动指示器
    private float paddingSpace;
    private float progressLength;
    private float touchRatio = 2;//调节偏移的触摸偏移量（按压灵敏度）
    private Paint backGroundPaint;

    /**
     * 调节按压灵敏度
     *
     * @param touchRatio
     */
    public void setTouchRatio(float touchRatio) {
        this.touchRatio = touchRatio;
    }


    private OnProgressChangeListener listener;

    public void setOnProgressChangeListener(OnProgressChangeListener listener) {
        this.listener = listener;
    }

    public void setMax(int max) {
        this.max = max;
    }


    public ThemeProgress(Context context) {
        this(context, null);
    }

    public ThemeProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThemeProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initData();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ThemeProgress);
        progressBackGround = array.getColor(R.styleable.ThemeProgress_progress2BackGround, Color.parseColor("#80ffffff"));
        progressWidth = array.getDimensionPixelSize(R.styleable.ThemeProgress_progress2WidthMagic, ScreenUtils.dip2px(context, 2));
        progressForwardGround = array.getColor(R.styleable.ThemeProgress_progress2ForwardGround, Color.parseColor("#ffffffff"));
        thumbColor = array.getColor(R.styleable.ThemeProgress_thumb2Color, Color.parseColor("#ffffffff"));
        thumbRadios = array.getDimensionPixelSize(R.styleable.ThemeProgress_thumb2Radios, ScreenUtils.dip2px(context, 8));
        max = array.getInteger(R.styleable.ThemeProgress_max2, 100);
        min = array.getInteger(R.styleable.ThemeProgress_min2, 0);
        progress = array.getInteger(R.styleable.ThemeProgress_progress2, 50);
        paddingSpace = ScreenUtils.dip2px(context, 5);
        if (progress < min) {
            progress = min;
        }
        if (progress > max) {
            progress = max;
        }
        array.recycle();
    }

    private void initData() {
        //初始化背景画图
        backGroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backGroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backGroundPaint.setStyle(Paint.Style.STROKE);
        backGroundPaint.setAntiAlias(true);
        backGroundPaint.setColor(progressBackGround);
        backGroundPaint.setStrokeWidth(progressWidth);

        //初始化progress
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(progressForwardGround);
        progressPaint.setStrokeWidth(progressWidth);
        //初始化Thumb
        thumbPaint = new Paint();
        thumbPaint.setColor(thumbColor);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setAntiAlias(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            viewWidth = w;
            viewHeight = h;
            thumbY = viewHeight / 2;
            progressLength = viewWidth - paddingSpace * 2 - thumbRadios * 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackGround(canvas);
        drawProgress(canvas);
        drawThumb(canvas);
    }

    /**
     * 画指示器
     *
     * @param canvas
     */
    private void drawThumb(Canvas canvas) {
        thumbX = progress / (float) (max - min) * progressLength + thumbRadios + paddingSpace;
        canvas.drawCircle(thumbX, thumbY, thumbRadios, thumbPaint);
    }

    /**
     * 画进度条
     *
     * @param canvas
     */
    private void drawProgress(Canvas canvas) {
        float stopX = progress / (float) (max - min) * progressLength + thumbRadios + paddingSpace;
        canvas.drawLine(thumbRadios + paddingSpace, thumbY, stopX, thumbY, progressPaint);
    }

    /**
     * 画背景
     *
     * @param canvas
     */
    private void drawBackGround(Canvas canvas) {
        canvas.drawLine(thumbRadios + paddingSpace, thumbY, thumbRadios +
                paddingSpace + progressLength, thumbY, backGroundPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (touchOnThumb(event.getX(), event.getY())) {
                    canMoveThumb = true;
                    if (listener != null) {
                        listener.onThumbTouchDown();
                    }
                    invalidate();
                } else {
                    canMoveThumb = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (canMoveThumb) {
                    moveThumb(event.getX());
                }
                break;
            case MotionEvent.ACTION_UP:
                if (canMoveThumb) {
                    if (listener != null) {
                        listener.onThumbTouchUp();
                    }
                }
                canMoveThumb = false;
                break;
        }
        return true;
    }

    /**
     * 移动指示器
     *
     * @param x
     */
    private void moveThumb(float x) {
        float nowX = x - thumbRadios;
        int nowProgress = (int) (((float) (max - min) * (nowX / (float) (viewWidth - 2 * thumbRadios))) + 0.5f);
        if (nowProgress < min) {
            nowProgress = min;
        }
        if (nowProgress > max) {
            nowProgress = max;
        }

        if (nowProgress != progress) {
            progress = nowProgress;
            if (listener != null) {
                listener.onProgressChange(progress, false);
            }
            invalidate();
        }

    }

    private boolean touchOnThumb(float x, float y) {
        //判断如果在圆点之外return false
        float spaceX = Math.abs(thumbX - x);
        float spaceY = Math.abs(thumbY - y);
        if (spaceX > thumbRadios * touchRatio) {
            return false;
        }
        if (spaceY > thumbRadios * touchRatio) {
            return false;
        }
        return true;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (listener != null) {
            listener.onProgressChange(progress, true);
        }
        invalidate();
    }


    /**
     * 当状态改变时回调
     */
    public interface OnProgressChangeListener {
        void onProgressChange(int progress, boolean fromUser);

        void onThumbTouchDown();

        void onThumbTouchUp();
    }
}
