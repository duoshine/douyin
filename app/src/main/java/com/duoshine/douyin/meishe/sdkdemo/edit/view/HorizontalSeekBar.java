package com.duoshine.douyin.meishe.sdkdemo.edit.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.duoshine.douyin.R;


/**
 * 横向SeekBar 支持刻度、左右双向移动、左->右移动
 **/
public class HorizontalSeekBar extends View {
    private final String TAG = "HorizontalSeekBar";
    private final int IMAGE_NONE = 0;//没有一张拖动图
    private final int IMAGE_LEFT = 1;//只有左拖动图
    private final int IMAGE_RIGHT = 2;//只有右拖动图
    private final int IMAGE_LEFT_RIGHT = 3;//左右拖动图都有
    /**
     * 拖动图片的状态
     */
    private int imageState = IMAGE_NONE;
    /**
     * 线条（进度条）的宽度
     */
    private int lineWidth;
    /**
     * 线条（进度条）的长度
     */
    private int lineLength = 400;
    /**
     * 字所在的高度 100$
     */
    private int textHeight;
    /**
     * 游标 图片宽度
     */
    private int imageWidth;
    /**
     * 游标 图片距离中轴线下边的距离，默认居中，可往下调节
     */
    private int imageLowPadding;
    /**
     * 游标 图片高度
     */
    private int imageHeight;
    /**
     * 是否有刻度线
     */
    private boolean hasRule;
    /**
     * 左边的游标是否在动
     */
    private boolean isLowerMoving;
    /**
     * 右边的游标是否在动
     */
    private boolean isUpperMoving;
    /**
     * 左侧字的大小 100$
     */
    private int leftTextSize;
    /**
     * 左侧的颜色 100$
     */
    private int leftTextColor;
    /**
     * 右侧字的大小 100$
     */
    private int rightTextSize;
    /**
     * 右侧字的颜色 100$
     */
    private int rightTextColor;
    /**
     * 两个游标内部 线（进度条）的颜色
     */
    private int inColor = Color.BLUE;
    /**
     * 两个游标外部 线（左边进度条）的颜色
     */
    private int leftOutColor = Color.BLUE;
    /**
     * 两个游标外部 线（右边进度条）的颜色
     */
    private int rightOutColor = Color.BLUE;
    /**
     * 刻度的颜色
     */
    private int ruleColor = Color.BLUE;
    /**
     * 刻度上边的字 的颜色
     */
    private int ruleTextColor = Color.BLUE;
    /**
     * 左边图标的图片
     */
    private Bitmap bitmapLow;
    /**
     * 右边图标 的图片
     */
    private Bitmap bitmapBig;
    /**
     * 左边图标所在X轴的位置
     */
    private float slideLowX;
    /**
     * 右边图标所在X轴的位置
     */
    private float slideBigX;

    /**
     * 左边的进度值
     */
    private int leftValue;
    /**
     * 右边的进度值
     */
    private int rightValue;

    /**
     * 进度文字缩放因子
     **/
    private int minification;
    /**
     * 进度文字精度
     */
    private int accuracy;

    /**
     * 加一些padding 大小酌情考虑 为了我们的自定义view可以显示完整
     */
    private int paddingLeft = 100;
    private int paddingRight = 100;
    private int paddingTop = 50;
    private int paddingBottom = 50;
    /**
     * 线（进度条） 开始的位置
     */
    private int lineStart = paddingLeft;
    /**
     * 线的Y轴位置
     */
    private int lineY;
    /**
     * 线（进度条）的结束位置
     */
    private int lineEnd = lineLength + paddingLeft;
    /**
     * 选择器的最大值
     */
    private int bigValue = 100;
    /**
     * 选择器的最小值
     */
    private int smallValue = 0;
    /**
     * 选择器的当前最小值
     */
    private float smallRange;
    /**
     * 选择器的当前最大值
     */
    private float bigRange;
    /**
     * 单位 元
     */
    private String unit = " ";
    /**
     * 单位份数
     */
    private int equal = 20;
    /**
     * 刻度单位 $
     */
    private String ruleUnit = " ";
    /**
     * 刻度上边文字的size
     */
    private int ruleTextSize = 20;
    /**
     * 刻度线的高度
     */
    private int ruleLineHeight = 20;
    private Paint linePaint;
    private Paint bitmapPaint;
    private Paint textPaint;
    private Paint paintRule;

    public HorizontalSeekBar(Context context) {
        this(context, null);
    }

    public HorizontalSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HorizontalSeekBar, defStyleAttr, 0);
        int size = typedArray.getIndexCount();
        for (int i = 0; i < size; i++) {
            int type = typedArray.getIndex(i);
            switch (type) {
                case R.styleable.HorizontalSeekBar_inColor:
                    inColor = typedArray.getColor(type, Color.BLACK);
                    break;
                case R.styleable.HorizontalSeekBar_lineHeight:
                    lineWidth = (int) typedArray.getDimension(type, dip2px(getContext(), 10));
                    break;
                case R.styleable.HorizontalSeekBar_leftOutColor:
                    leftOutColor = typedArray.getColor(type, Color.YELLOW);
                    break;
                case R.styleable.HorizontalSeekBar_rightOutColor:
                    rightOutColor = typedArray.getColor(type, Color.YELLOW);
                    break;
                case R.styleable.HorizontalSeekBar_leftTextColor:
                    leftTextColor = typedArray.getColor(type, Color.BLUE);
                    break;
                case R.styleable.HorizontalSeekBar_leftTextSize:
                    leftTextSize = typedArray.getDimensionPixelSize(type, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.HorizontalSeekBar_rightTextColor:
                    rightTextColor = typedArray.getColor(type, Color.BLUE);
                    break;
                case R.styleable.HorizontalSeekBar_rightTextSize:
                    rightTextSize = typedArray.getDimensionPixelSize(type, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.HorizontalSeekBar_imageLeft:
                    bitmapLow = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(type, 0));
                    break;
                case R.styleable.HorizontalSeekBar_imageRight:
                    bitmapBig = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(type, 0));
                    break;
                case R.styleable.HorizontalSeekBar_imageHeight:
                    imageHeight = (int) typedArray.getDimension(type, dip2px(getContext(), 20));
                    break;
                case R.styleable.HorizontalSeekBar_imageWidth:
                    imageWidth = (int) typedArray.getDimension(type, dip2px(getContext(), 20));
                    break;
                case R.styleable.HorizontalSeekBar_imageLowPadding:
                    imageLowPadding = (int) typedArray.getDimension(type, dip2px(getContext(), 0));
                    break;
                case R.styleable.HorizontalSeekBar_hasRule:
                    hasRule = typedArray.getBoolean(type, false);
                    break;
                case R.styleable.HorizontalSeekBar_ruleColor:
                    ruleColor = typedArray.getColor(type, Color.BLUE);
                    break;
                case R.styleable.HorizontalSeekBar_ruleTextColor:
                    ruleTextColor = typedArray.getColor(type, Color.BLUE);
                    break;
                case R.styleable.HorizontalSeekBar_unit:
                    unit = typedArray.getString(type);
                    break;
                case R.styleable.HorizontalSeekBar_equal:
                    equal = typedArray.getInt(type, 10);
                    break;
                case R.styleable.HorizontalSeekBar_ruleUnit:
                    ruleUnit = typedArray.getString(type);
                    break;
                case R.styleable.HorizontalSeekBar_ruleTextSize:
                    ruleTextSize = typedArray.getDimensionPixelSize(type, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.HorizontalSeekBar_ruleLineHeight:
                    ruleLineHeight = (int) typedArray.getDimension(type, dip2px(getContext(), 10));
                    break;
                case R.styleable.HorizontalSeekBar_bigValue:
                    bigValue = typedArray.getInteger(type, 100);
                    break;
                case R.styleable.HorizontalSeekBar_smallValue:
                    smallValue = typedArray.getInteger(type, 100);
                    break;


                default:
                    break;
            }
        }
        typedArray.recycle();
        init();
    }

    private void init() {
        /**初始化两个游标的位置*/
        slideLowX = lineStart;
        slideBigX = lineEnd;
        smallRange = smallValue;
        bigRange = bigValue;
        int tempSize = Math.max(leftTextSize, rightTextSize);
        if (hasRule) {
            //有刻度时 paddingTop 要加上（text高度）和（刻度线高度加刻度线上边文字的高度和） 之间的最大值
            paddingTop = paddingTop + Math.max(tempSize, ruleLineHeight + ruleTextSize);
        } else {
            //没有刻度时 paddingTop 加上 text的高度
            paddingTop = paddingTop + tempSize;
        }
        if (bitmapLow == null && bitmapBig == null) {
            return;
        }
        if (bitmapBig != null && bitmapLow != null) {
            imageState = IMAGE_LEFT_RIGHT;
        } else if (bitmapLow != null) {
            imageState = IMAGE_LEFT;
        } else {
            imageState = IMAGE_RIGHT;
        }
        /**缩放图片*/
        if (bitmapLow != null) {
            bitmapLow = createBitmap(bitmapLow, imageWidth, imageHeight);
        }
        if (bitmapBig != null) {
            bitmapBig = createBitmap(bitmapBig, imageWidth, imageHeight);
        }
    }

    private Bitmap createBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap == null) {
            return null;
        }
        // Log.d("lhz", "width=" + width + "**height=" + height + "**bitmap.width=" + bitmap.getWidth() + "**bitmap.height=" + bitmap.getHeight());
        // 计算缩放比例
        float scaleWidth = ((float) width) / bitmap.getWidth();
        float scaleHeight = ((float) height) / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 设置移动图标的宽高
     **/
    public void setMoveIconSize(int width, int height) {
        imageWidth = dip2px(getContext(), width);
        imageHeight = dip2px(getContext(), height);
    }

    /**
     * 重置状态
     **/
    public void reset() {
        lastLeftProgress = 0;
        lastRightProgress = 0;
        leftValue = 0;
        rightValue = 0;
        lastLeftIconId = 0;
        lastRightIconId = 0;
        imageState = IMAGE_NONE;
        imageLowPadding = 0;
        bitmapLow = null;
        bitmapBig = null;
        init();
    }

    private int lastLeftIconId = 0;

    /**
     * 设置左侧侧拖动图标
     * 注意:资源不能是shape之类的drawable
     *
     * @param resourceId 图片资源id，为0则不显示该图标
     **/
    public void setLeftMoveIcon(int resourceId) {
        if (lastLeftIconId == resourceId) {
            return;
        }
        lastLeftIconId = resourceId;
        bitmapLow = BitmapFactory.decodeResource(getResources(), resourceId);
        if (bitmapLow == null) {
            //如果右侧图标没有了，则重置slideLowX，防止右侧图标无法拖动到尽头
            slideLowX = lineStart;
            leftValue = 0;
        } else {
            if (imageState == IMAGE_NONE || imageState == IMAGE_LEFT) {
                //如果没有设置过拖动图，或者左边的拖动图变动了，才更改宽高并重新测量
                imageState = IMAGE_LEFT;
            } else if (imageState == IMAGE_RIGHT) {
                imageState = IMAGE_LEFT_RIGHT;
            }
            // Log.d(TAG, "setLeftMoveIcon,imageWidth=" + imageWidth + "**imageHeight=" + imageHeight);
            if (imageWidth > 0 && imageHeight > 0) {
                if (bitmapLow != null) {
                    bitmapLow = createBitmap(bitmapLow, imageWidth, imageHeight);
                }
            }
            requestLayout();
        }
        // Log.d(TAG, "setLeftMoveIcon,imageState=" + imageState + "**bitmapLow=" + bitmapLow);
        invalidate();
    }

    private int lastRightIconId = 0;

    /**
     * 设置右侧拖动图标
     * 注意:资源不能是shape之类的drawable
     *
     * @param resourceId 图片资源id,为0则不显示该图标
     **/
    public void setRightMoveIcon(int resourceId) {
        if (lastRightIconId == resourceId) {
            return;
        }
        lastRightIconId = resourceId;
        bitmapBig = BitmapFactory.decodeResource(getResources(), resourceId);
        if (bitmapBig == null) {
            //如果右侧图标没有了，则重置slideBigX，防止左侧图标无法拖动到尽头
            slideBigX = lineEnd;
            rightValue = 0;
        } else {
            if (imageState == IMAGE_NONE) {
                //如果没有设置过拖动图，才更改宽高并重新测量
                imageState = IMAGE_RIGHT;
            } else if (imageState == IMAGE_LEFT) {
                imageState = IMAGE_LEFT_RIGHT;
            }
            // Log.d(TAG,"lhz,setRightMoveIcon,imageWidth=" + imageWidth + "**imageHeight=" + imageHeight);
            if (imageWidth > 0 && imageHeight > 0) {
                if (bitmapBig != null) {
                    bitmapBig = createBitmap(bitmapBig, imageWidth, imageHeight);
                }
            }
            requestLayout();
        }
        invalidate();
    }

    /**
     * 距离中轴线上下距离，默认为0则居中，有值则往下
     *
     * @param padding int 距离值 dp
     **/
    public void setMoveIconLowPadding(int padding) {
        imageLowPadding = dip2px(getContext(), padding);
    }

    /**
     * 设置最大进度
     *
     * @param maxProgress int 最大进度值
     **/
    public void setMaxProgress(int maxProgress) {
        bigValue = maxProgress;
        requestLayout();
    }

    /**
     * 获取最大进度
     **/
    public int getMaxProgress() {
        return bigValue;
    }

    private int lastLeftProgress = -1;

    /**
     * 设置左边的进度
     *
     * @param progress int 进度值
     **/
    public void setLeftProgress(int progress) {
        if (lastLeftProgress == progress) {
            float tempX = ((float) progress / bigValue * lineLength + lineStart);
            if (Math.abs((tempX - slideLowX)) <= 10) {
                //做最后的挣扎，如果误差较大，说明不是同一个进度
                Log.d(TAG, "left same progress " + progress);
                return;
            }
        }
        if (progress > bigValue) {
            progress = bigValue;
        }
        if (rightValue + progress >= bigValue) {
            //如果右边有进度，并且当前要设置的值少于剩余的值，则设置剩余的值。
            progress = bigValue - rightValue;
            leftIsLastMove = false;
        } else {
            leftIsLastMove = true;
        }
        lastLeftProgress = progress;
        slideLowX = ((float) progress / bigValue * lineLength + lineStart);
        // Log.d(TAG, "setLeftProgress,slideLowX=" + slideLowX + "**lineLength=" + lineLength + "**lineStart=" + lineStart + "**progress=" + progress + "**bigValue=" + bigValue);
        updateRange(false);//setLeftProgress
        postInvalidate();
    }

    /**
     * 获取左边的进度
     **/
    public int getLeftProgress() {
        return leftValue;
    }

    private int lastRightProgress = -1;

    /**
     * 设置左边的进度
     *
     * @param progress int 进度值
     **/
    public void setRightProgress(int progress) {
        if (lastRightProgress == progress) {
            Log.d(TAG, "right same progress");
            return;
        }
        if (progress > bigValue) {
            progress = bigValue;
        }
        if (leftValue + progress >= bigValue) {
            //如果左边有进度，并且当前要设置的值少于剩余的值，则设置剩余的值。
            progress = bigValue - leftValue;
            leftIsLastMove = true;
        } else {
            leftIsLastMove = false;
        }
        lastRightProgress = progress;
        slideBigX = ((float) (bigValue - progress) / bigValue * lineLength + lineStart);
        //Log.d(TAG, "setRightProgress,slideBigX=" + slideBigX + "**lineLength=" + lineLength + "**lineStart=" + lineStart);
        updateRange(false);//setRightProgress
        postInvalidate();
    }

    /**
     * 获取右边的进度
     **/
    public int getRightProgress() {
        return rightValue;
    }

    /**
     * 设置进度文字转化倍数与精度
     *
     * @param minification int 缩小倍数（如果放大传入小于1的小数即可）
     * @param accuracy     int 精度，x代表小数点后的位数
     **/
    public void setTransformText(int minification, int accuracy) {
        if (minification == 0) {
            minification = 1;
        }
        this.minification = Math.abs(minification);
        this.accuracy = Math.abs(accuracy);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMyMeasureWidth(widthMeasureSpec);
        int height = getMyMeasureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
        //Log.d(TAG, "onMeasure,width=" + width + "**height=" + height);
    }

    private int getMyMeasureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            // matchparent 或者 固定大小 view最小应为 paddingBottom + paddingTop + bitmapHeight + 10 否则显示不全
            size = Math.max(size, paddingBottom + paddingTop + imageHeight + 10);
        } else {
            //wrap content
            int height = paddingBottom + paddingTop + imageHeight + 10;
            size = Math.min(size, height);
        }
        return size;
    }

    private int getMyMeasureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            size = Math.max(size, paddingLeft + paddingRight + imageWidth * 2);
        } else {
            //wrap content
            int width = paddingLeft + paddingRight + imageWidth * 2;
            size = Math.min(size, width);
        }
        // match parent 或者 固定大小 此时可以获取线（进度条）的长度
        lineLength = size - paddingLeft - paddingRight - imageWidth;
        //线（进度条）的结束位置
        lineEnd = lineLength + paddingLeft + imageWidth / 2;
        //线（进度条）的开始位置
        lineStart = paddingLeft + imageWidth / 2;
        //初始化 游标位置
        if (leftValue > 0) {
            slideLowX = ((float) leftValue / bigValue * lineLength + lineStart);
        } else {
            slideLowX = lineStart;
        }
        // Log.d(TAG, "getMyMeasureWidth,leftValue=" + leftValue + "**slideLowX=" + slideLowX + "**lineStart=" + lineStart + "**lineLength=" + lineLength + "**lineEnd=" + lineEnd + "**lineStart=" + lineStart);
        if (rightValue > 0) {
            slideBigX = ((float) (bigValue - rightValue) / bigValue * lineLength + lineStart);
        } else {
            slideBigX = lineEnd;
        }
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //  Log.d(TAG, "onDraw,lineY=" + lineY + "**textHeight=" + textHeight + "**slideLowX=" + slideLowX + "**slideBigX=" + slideBigX + "**lineLength=" + lineLength + "**lineEnd=" + lineEnd + "**lineStart=" + lineStart);
        // Y轴 坐标
        lineY = getHeight() - paddingBottom - imageHeight / 2;
        // 字所在高度 100$
        textHeight = lineY - imageHeight / 2 - 10;
        //是否画刻度
        if (hasRule) {
            drawRule(canvas);
        }
        if (linePaint == null) {
            linePaint = new Paint();
        }
        //画内部线
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setColor(inColor);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(slideLowX, lineY, slideBigX, lineY, linePaint);
        if (bitmapLow != null) {
            linePaint.setColor(leftOutColor);
            linePaint.setStrokeCap(Paint.Cap.ROUND);
            //画 外部线,左边
            canvas.drawLine(lineStart, lineY, slideLowX, lineY, linePaint);
        }
        if (bitmapBig != null) {
            linePaint.setColor(rightOutColor);
            linePaint.setStrokeCap(Paint.Cap.ROUND);
            //画 外部线,右边边
            canvas.drawLine(slideBigX, lineY, lineEnd, lineY, linePaint);
        }
        //画游标
        if (bitmapPaint == null) {
            bitmapPaint = new Paint();
        }
        if (leftIsLastMove) {
            //先画右边再画左边的拖动图标，后画的处于最上层
            if (bitmapBig != null) {
                canvas.drawBitmap(bitmapBig, slideBigX - imageWidth / 2, lineY - imageHeight / 2 + imageLowPadding, bitmapPaint);
            }
            if (bitmapLow != null) {
                canvas.drawBitmap(bitmapLow, slideLowX - imageWidth / 2, lineY - imageHeight / 2 + imageLowPadding, bitmapPaint);
            }
        } else {
            //先画左边再画右边的拖动图标,后画的处于最上层
            if (bitmapLow != null) {
                canvas.drawBitmap(bitmapLow, slideLowX - imageWidth / 2, lineY - imageHeight / 2 + imageLowPadding, bitmapPaint);
            }
            if (bitmapBig != null) {
                canvas.drawBitmap(bitmapBig, slideBigX - imageWidth / 2, lineY - imageHeight / 2 + imageLowPadding, bitmapPaint);
            }
        }

        //画 游标上边的字
        if (textPaint == null) {
            textPaint = new Paint();
        }
        textPaint.setAntiAlias(true);
        if (leftIsLastMove) {
            if (bitmapLow != null) {
                textPaint.setColor(leftTextColor);
                textPaint.setTextSize(leftTextSize);
                canvas.drawText(String.format("%." + accuracy + "f" + unit, smallRange), slideLowX - imageWidth / 2, textHeight, textPaint);
            }
        } else {
            if (bitmapBig != null) {
                textPaint.setColor(rightTextColor);
                textPaint.setTextSize(rightTextSize);
                canvas.drawText(String.format("%." + accuracy + "f" + unit, bigRange), slideBigX - imageWidth / 2, textHeight, textPaint);
            }
        }
    }

    private boolean leftEnd;
    private boolean rightEnd;
    private boolean leftIsLastMove = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //事件机制
        super.onTouchEvent(event);
        float nowX = event.getX();
        float nowY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下 在线（进度条）范围上
                boolean rightY = Math.abs(nowY - lineY) < imageHeight;
                //按下 在左边游标上
                boolean lowSlide = false;
                if (bitmapLow != null) {
                    lowSlide = Math.abs(nowX - slideLowX) < imageWidth;
                }
                boolean bigSlide = false;
                //按下 在右边游标上
                if (bitmapBig != null) {//如果有右边的图标
                    bigSlide = Math.abs(nowX - slideBigX) < imageWidth;
                }
                if (bigSlide && lowSlide) {
                    //如果重合（即都在点击范围内）则上一次滑动过得可以被拖动
                    lowSlide = leftIsLastMove;
                    bigSlide = !leftIsLastMove;
                }
                //Log.d(TAG, "ACTION_DOWN,lowSlide=" + lowSlide + "**bigSlide=" + bigSlide + "**leftIsLastMove="+leftIsLastMove);
                if (rightY && lowSlide) {
                    isLowerMoving = true;
                    leftIsLastMove = true;
                } else if (rightY && bigSlide) {
                    isUpperMoving = true;
                    leftIsLastMove = false;
                    //点击了游标外部 的线上
                } else if (nowX >= lineStart && nowX <= slideLowX - imageWidth / 2 && rightY) {
                    slideLowX = nowX;
                    updateRange(true);//"ACTION_DOWN,nowX >= lineStart"
                    postInvalidate();
                } else if (nowX <= lineEnd && nowX >= slideBigX + imageWidth / 2 && rightY) {
                    slideBigX = nowX;
                    updateRange(true);//ACTION_DOWN,nowX <= lineEnd
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //左边游标是运动状态
                if (isLowerMoving) {
                    //当前 X坐标在线上 且在右边游标的左边
                    int tempWith = 0;
                   /*
                     //左右图标可以重合，所以注释了
                   if (bitmapBig != null) {
                        //如果有右边的图标
                        tempWith = imageWidth;
                    }*/

                    //Log.d(TAG, "ACTION_MOVE,nowX=" + nowX + "**slideLowX=" + slideLowX + "**slideBigX=" + slideBigX+"**rightEnd="+rightEnd+"**leftEnd="+leftEnd);
                    if (nowX < slideBigX - tempWith && nowX > lineStart - tempWith / 2) {
                        slideLowX = nowX;
                        rightEnd = false;
                        leftEnd = false;
                        //更新进度
                        updateRange(true);//ACTION_MOVE,nowX < slideBigX
                        postInvalidate();
                    } else if (!rightEnd && nowX >= slideBigX - tempWith) {
                        //右边尽头处理
                        slideLowX = slideBigX - tempWith;
                        rightEnd = true;
                        updateRange(true);//ACTION_MOVE,!rightEnd
                        postInvalidate();
                    } else if (!leftEnd && nowX <= lineStart - tempWith / 2) {
                        //  Log.d(TAG,"leftEnd,slideLowX="+slideLowX+"**lineStart="+lineStart);
                        //左边尽头处理
                        leftEnd = true;
                        slideLowX = lineStart;
                        updateRange(true);//ACTION_MOVE,!leftEnd
                        postInvalidate();
                    }

                } else if (isUpperMoving) {
                    //当前 X坐标在线上 且在左边游标的右边
                    int tempWith = 0;
                   /*
                   左右图标可以重合，所以注释了
                   if (bitmapLow != null) {
                        //如果有右边的图标
                        tempWith = imageWidth;
                    }*/
                    if (nowX > slideLowX + tempWith && nowX < lineEnd + tempWith / 2) {
                        slideBigX = nowX;
                        rightEnd = false;
                        leftEnd = false;
                        if (slideBigX > lineEnd) {
                            slideBigX = lineEnd;
                        }
                        //更新进度
                        updateRange(true);//ACTION_MOVE,nowX > slideLowX
                        postInvalidate();

                    } else if (!leftEnd && nowX <= slideLowX + tempWith) {
                        //左边尽头处理
                        slideBigX = slideLowX + tempWith;
                        leftEnd = true;
                        updateRange(true);//ACTION_MOVE,!leftEnd
                        postInvalidate();
                    } else if (!rightEnd && nowX >= lineEnd + tempWith / 2) {
                        //右边尽头处理
                        slideBigX = lineEnd;
                        rightEnd = true;
                        updateRange(true);//ACTION_MOVE,!rightEnd
                        postInvalidate();
                    }
                }
                break;
            //手指抬起
            case MotionEvent.ACTION_UP:
                leftEnd = false;
                rightEnd = false;
                isUpperMoving = false;
                isLowerMoving = false;
                break;
            default:
                break;
        }

        return true;
    }

    private void updateRange(boolean callback) {
        leftValue = (int) ((slideLowX - lineStart) * bigValue / lineLength);
        rightValue = (int) (bigValue - (slideBigX - lineStart) * bigValue / lineLength);
        //当前 左边游标数值
        smallRange = computeRange(slideLowX) / minification;
        //当前 右边游标数值
        bigRange = computeRange(slideBigX);
        bigRange = (bigValue - bigRange) / minification;
       // Log.d(TAG, "updateRange,leftValue=" + leftValue + "**rightValue=" + rightValue + "**slideLowX=" + slideLowX+"**smallRange="+smallRange+"**bigRange="+bigRange);
        //接口 实现值的传递
        if (onRangeListener != null && callback) {
            onRangeListener.onRange(smallRange, bigRange);
            lastLeftProgress = leftValue;
            lastRightProgress = rightValue;
        }
    }

    /**
     * 获取当前值
     */
    private float computeRange(float range) {
        return (range - lineStart) * (bigValue - smallValue) / lineLength + smallValue;
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 画刻度
     */
    protected void drawRule(Canvas canvas) {
        if (paintRule == null) {
            paintRule = new Paint();
        }
        paintRule.setStrokeWidth(1);
        paintRule.setTextSize(ruleTextSize);
        paintRule.setTextAlign(Paint.Align.CENTER);
        paintRule.setAntiAlias(true);
        //遍历 equal份,画刻度
        for (int i = smallValue; i <= bigValue; i += (bigValue - smallValue) / equal) {
            float degX = lineStart + i * lineLength / (bigValue - smallValue);
            int degY = lineY - ruleLineHeight;
            paintRule.setColor(ruleColor);
            canvas.drawLine(degX, lineY, degX, degY, paintRule);
            paintRule.setColor(ruleTextColor);
            canvas.drawText(String.valueOf(i) + ruleUnit, degX, degY, paintRule);
        }
    }

    /**
     * 写个接口 用来传递最大最小值
     */
    public interface onRangeListener {
        void onRange(float low, float big);
    }

    private onRangeListener onRangeListener;

    public void setOnRangeListener(onRangeListener onRangeListener) {
        this.onRangeListener = onRangeListener;
    }
}
