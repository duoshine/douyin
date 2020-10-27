package com.duoshine.douyin.meishe.sdkdemo.edit.anim.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;

import java.math.BigDecimal;


/**
 * Created by MZ008064 on 2020/6/4.
 *
 * @author zcy
 * @Date 2020/6/4.
 */
public class MagicSeekBar extends View {
    private int progressBackGround;//滚动条背景色
    private int progressWidth;//滚动条宽度
    private int progressForwardGround;//滚动条颜色
    private int thumbColor;//指示器背景色
    private int pointColor;//吸附点颜色
    private int thumbRadios;//指示器半径
    private int pointRadios;//圆点半径
    private long max;//最大值
    private long min;//最小值
    private long progress;//当前值
    private long pointProgress;//吸附点位置
    private long pointRange;//距离吸附点多远吸附
    private boolean pointEnable = true;//是否有显示断点功能
    private boolean isAuto = true;//是否有吸附功能
    private int viewWidth;//布局宽
    private int viewHeight;//布局高
    private Paint backGroundPaint;
    private Paint pointPaint;//断点画笔
    private Paint progressPaint;//滚动条画笔
    private Paint thumbPaint;//指示器画笔
    private float thumbX;//当前指示器的坐标x
    private float thumbY;//当前指示器的坐标y
    private boolean canMoveThumb = false;//是否可以移动指示器
    private boolean showTextEnable = true;//是否可以显示文字
    private Paint textPaint;
    private float textSize;
    private int textColor;
    private float textSpace;
    private float paddingSpace;
    private float progressLength;
    private int textAlpha = 0;//显示字体透明度
    private int shadowColor;
    private float shadowRadios;
    private float touchRatio = 2;//调节偏移的触摸偏移量（按压灵敏度）

    public void setPointEnable(boolean pointEnable) {
        this.pointEnable = pointEnable;
        setAuto(pointEnable);
    }

    /**
     * 调节按压灵敏度
     *
     * @param touchRatio
     */
    public void setTouchRatio(float touchRatio) {
        this.touchRatio = touchRatio;
    }

    public void setShowTextEnable(boolean showTextEnable) {
        this.showTextEnable = showTextEnable;
    }

    private OnProgressChangeListener listener;

    public void setOnProgressChangeListener(OnProgressChangeListener listener) {
        this.listener = listener;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    public MagicSeekBar(Context context) {
        this(context, null);
    }

    public MagicSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MagicSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initData();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MagicProgress);
        progressBackGround = array.getColor(R.styleable.MagicProgress_progressBackGround, Color.parseColor("#80ffffff"));
        progressWidth = array.getDimensionPixelSize(R.styleable.MagicProgress_progressWidthMagic, ScreenUtils.dip2px(context, 2));
        progressForwardGround = array.getColor(R.styleable.MagicProgress_progressBackGround, Color.parseColor("#ffffffff"));
        thumbColor = array.getColor(R.styleable.MagicProgress_progressBackGround, Color.parseColor("#ffffffff"));
        pointColor = array.getColor(R.styleable.MagicProgress_progressBackGround, Color.parseColor("#ffffffff"));
        thumbRadios = array.getDimensionPixelSize(R.styleable.MagicProgress_progressWidthMagic, ScreenUtils.dip2px(context, 8));
        pointRadios = array.getDimensionPixelSize(R.styleable.MagicProgress_progressWidthMagic, ScreenUtils.dip2px(context, 3));
        max = array.getInteger(R.styleable.MagicProgress_max, 100);
        min = array.getInteger(R.styleable.MagicProgress_min, 0);
        progress = array.getInteger(R.styleable.MagicProgress_progress_a, 50);
        pointProgress = array.getInteger(R.styleable.MagicProgress_pointProgress, 80);
        pointRange = array.getInteger(R.styleable.MagicProgress_pointRange, 3);
        textSize = array.getDimension(R.styleable.MagicProgress_textSizeMagic, 22);
        textColor = array.getColor(R.styleable.MagicProgress_textColorMagic, Color.parseColor("#ffffff"));
        textSpace = ScreenUtils.dip2px(context, 5);
        paddingSpace = ScreenUtils.dip2px(context, 5);
        shadowColor = array.getColor(R.styleable.MagicProgress_shadowColor, Color.parseColor("#aaaaaa"));
        if (progress < min) {
            progress = min;
        }
        if (progress > max) {
            progress = max;
        }
        if (pointProgress < min || pointProgress > max) {
            pointEnable = false;
        }
        array.recycle();
    }

    boolean animalAlpha = false;//渐隐动画

    private void initData() {
        //初始化背景画图
        backGroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backGroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backGroundPaint.setStyle(Paint.Style.STROKE);
        backGroundPaint.setAntiAlias(true);
        backGroundPaint.setColor(progressBackGround);
        backGroundPaint.setStrokeWidth(progressWidth);
        backGroundPaint.setShadowLayer(20, 0, 0, shadowColor);
        //初始化小圆点
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(pointColor);
        pointPaint.setAntiAlias(true);
        pointPaint.setShadowLayer(2, 0, 0, shadowColor);
        //初始化progress
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressForwardGround);
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeWidth(progressWidth);
        //初始化Thumb
        thumbPaint = new Paint();
        thumbPaint.setColor(thumbColor);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setAntiAlias(true);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        thumbPaint.setShadowLayer(2, 0, 0, shadowColor);
        //数字显示
        textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setStrokeWidth(2f);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setShadowLayer(2, 0, 0, shadowColor);
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
            thumbY = viewHeight - thumbRadios * 2;
            progressLength = viewWidth - paddingSpace * 2 - thumbRadios * 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackGround(canvas);
        if (pointEnable) {
            drawPoint(canvas);
        }
        drawProgress(canvas);
        drawThumb(canvas);
        if (showTextEnable) {
            drawText(canvas);
        }
    }

    private void drawText(Canvas canvas) {
        BigDecimal b = new BigDecimal(progress*1.0f/1000/1000);
        float f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        if(f1 < 0.1){
            f1 = 0.1f;
        }
        String text =  f1 + "s";
        float textWidth = textPaint.measureText(text);
        Rect rect = new Rect();
        if (canMoveThumb) {
            animalAlpha = false;
            textAlpha = 255;
            shadowRadios = 2f;
        }
        if (animalAlpha) {
            textAlpha -= 5;
            shadowRadios = 0;
            if (textAlpha <= 0) {
                shadowRadios = 0;
                textAlpha = 0;
                animalAlpha = false;
            }
            postInvalidateDelayed(5);
        }
        textPaint.setShadowLayer(shadowRadios, 0, 0, shadowColor);
        textPaint.setAlpha(textAlpha);
        textPaint.getTextBounds(text, 0, text.length(), rect);
        float textBoundsHeight = rect.bottom - rect.top;
        float x = (float) progress / (float) (max - min) * progressLength + paddingSpace + thumbRadios - textWidth / 2;
        float y = thumbY - textSpace - textBoundsHeight;
        canvas.drawText(text, x, y, textPaint);

    }

    /**
     * 画指示器
     *
     * @param canvas
     */
    private void drawThumb(Canvas canvas) {
        thumbX = progress / (float) (max - min) * progressLength + thumbRadios + paddingSpace;
        //canvas.drawBitmap(thumbX, thumbY, thumbRadios, thumbPaint);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.magic_seekbar_thumb);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float left =  progress / (float) (max - min) * progressLength + width/2;
        canvas.drawBitmap(bitmap,left,thumbY-height/2,thumbPaint);
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
     * 画小圆点
     *
     * @param canvas
     */
    private void drawPoint(Canvas canvas) {
        if (progress < pointProgress) {
            float x = pointProgress / (float) (max - min) * progressLength + thumbRadios + paddingSpace;
            float y = thumbY;
            canvas.drawCircle(x, y, pointRadios, pointPaint);
        }
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


    /**
     * 设置断点圆点的位置点
     *
     * @param pointProgress
     */
    public void setPointProgress(int pointProgress) {
        this.pointProgress = pointProgress;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (touchOnThumb(event.getX(), event.getY())) {
                    canMoveThumb = true;
                    invalidate();
                } else {
                    //跳转thumb 到指定位置
                    canMoveThumb = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (canMoveThumb) {
                    float moveX = event.getX();
                    /*float minLeft = progressLength*((0.1f*1000*1000/max))+ScreenUtils.dip2px(getContext(),88);
                    if(moveX>minLeft){

                        moveThumb(moveX);
                    }*/
                    moveThumb(moveX);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (canMoveThumb) {
                    startAnimal();
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
        long nowProgress = (int) (((float) (max - min) * (nowX / (float) (viewWidth - 2 * thumbRadios))) + 0.5f);
        if (nowProgress < min) {
            nowProgress = min;
        }
        if (nowProgress > max) {
            nowProgress = max;
        }

        boolean adsorbEnable = false;
        if (isAuto) {
            adsorbEnable = Math.abs(nowProgress - pointProgress) > pointRange ? false : true;
        }
        if (nowProgress != progress) {
            if (adsorbEnable) {
                if (progress == pointProgress) {
                    return;
                }
                progress = pointProgress;
                if (progress < 10 || progress > 90)
                if (listener != null) {
                    if(progress>=0.1*1000*1000){

                        listener.onProgressChange(progress, false);
                    }
                }
            } else {
                progress = nowProgress;
                if (listener != null) {
                    if(progress>=0.1*1000*1000){

                        listener.onProgressChange(progress, false);
                    }
                }
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

    public void setProgress(long progress) {
        this.progress = progress;
        if (listener != null) {
            if(progress>=0.1*1000*1000){

                listener.onProgressChange(progress, true);
            }
        }
        invalidate();
    }

    public void setPointProgressByNewMax(int maxProgress) {
        this.pointProgress = (int) ((float) this.pointProgress / (float) this.max * (float) maxProgress);
    }


    /**
     * 当状态改变时回调
     */
    public interface OnProgressChangeListener {
        /**
         *
         * @param progress
         * @param fromUser  代码设置返回true 其他返回false
         */
        void onProgressChange(long progress, boolean fromUser);
    }

    public void startAnimal() {
        if (animalAlpha) {
            return;
        }
        textAlpha = 255;
        animalAlpha = true;
        postInvalidateDelayed(10);
    }

    public long getProgress(){
        return progress;
    }
}
