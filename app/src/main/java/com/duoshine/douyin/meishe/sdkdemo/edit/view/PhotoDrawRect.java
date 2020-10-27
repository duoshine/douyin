package com.duoshine.douyin.meishe.sdkdemo.edit.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.utils.Constants;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;


/**
 * Created by czl on 2018/8/6.
 */
public class PhotoDrawRect extends View {
    private static final int POSITION_CENTER = 0x14;
    private static final int POSITION_RIGHT_BOTTOM = 0x15;
    private RectF mViewRectF = new RectF();
    private RectF scaleRectF = new RectF();
    private Paint mTextPaint = new Paint();
    private Paint mRectPaint = new Paint();
    private Context mContext;
    private String mDrawText = "";
    private int mViewMode = 0;
    private Bitmap scaleImgBtn;
    private int mScaleImgBtnWidth;
    private int mScaleImgBtnHeight;
    private int mImgWidth;
    private int mImgHeight;

    private int originLeft = 0;
    private int originRight = 0;
    private int originTop = 0;
    private int originBottom = 0;
    private float prevRawX = 0;
    private float prevRawY = 0;
    private OnDrawRectListener mDrawRectListener;
    private int mClickPosition = POSITION_CENTER;
    /*
    * 默认值是16:9，此变量仅对图片运动编辑有效，比例值的含义是width / height。
    * The default value is 16: 9. This variable is only effective for image motion editing. The meaning of the scale value is width / height.
    * */
    private float mCurMakeRatio = 16 * 1.0f / 9;
    /*
    * 可能是最小宽度值或者是最小高度值
    * May be minimum width value or minimum height value
    * */
    private int mMinPixelValue = 1280 / 4;

    public void setCurMakeRatio(int curMakeRatio) {
        /*
        * 时间线宽度
        * Timeline width
        * */
        int timelineWidth = 1280;
        switch (curMakeRatio){
            case 1:
                timelineWidth = 1280;
                mCurMakeRatio = 16 * 1.0f / 9;
                mMinPixelValue = timelineWidth / 4;
                break;
            case 2:
                timelineWidth = 720;
                mCurMakeRatio = 1.0f;
                mMinPixelValue = timelineWidth / 3;
                break;
            case 4:
                timelineWidth = 1280;
                mCurMakeRatio = 9 * 1.0f / 16;
                mMinPixelValue = timelineWidth / 4;
                break;
            case 8:
                timelineWidth = 960;
                mCurMakeRatio = 3 * 1.0f / 4;
                mMinPixelValue = timelineWidth / 3;
                break;
            case 16:
                timelineWidth = 960;
                mCurMakeRatio = 4 * 1.0f / 3;
                mMinPixelValue = timelineWidth / 3;
                break;
        }
    }
    public void setImgSize(int imgWidth,int imgHeight) {
        this.mImgWidth = imgWidth;
        this.mImgHeight = imgHeight;
    }

    public PhotoDrawRect(Context context) {
        this(context, null);
    }

    public PhotoDrawRect(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        scaleImgBtn = BitmapFactory.decodeResource(getResources(), R.mipmap.photo_scale);
        mScaleImgBtnWidth = scaleImgBtn.getWidth();
        mScaleImgBtnHeight = scaleImgBtn.getHeight();
        initRectPaint();
        initTextPaint();
    }

    public void setDrawRect(String drawText,int mode) {
        mViewMode = mode;
        mDrawText = drawText;
    }

    public void setOnDrawRectListener(OnDrawRectListener drawRectListener) {
        mDrawRectListener = drawRectListener;
    }

    private void initRectPaint(){
        /*
         * 设置颜色
         * Set color
         * */
        mRectPaint.setColor(Color.parseColor("#D0021B"));
        /*
         * 设置抗锯齿
         * Setting up anti-aliasing
         * */
        mRectPaint.setAntiAlias(true);
        /*
         * 设置线宽
         * Set line width
         * */
        mRectPaint.setStrokeWidth(ScreenUtils.dip2px(mContext,3));
        /*
         * 设置非填充
         * Set non-fill
         * */
        mRectPaint.setStyle(Paint.Style.STROKE);
    }
    private void initTextPaint(){
        mTextPaint.setTextSize(ScreenUtils.sp2px(mContext,12));
        mTextPaint.setColor(Color.parseColor("#D0021B"));
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
        * 绘制矩形框
        * Draw rectangle
        * */
        mViewRectF.set(0,0,getWidth(),getHeight());
        canvas.drawRect(mViewRectF, mRectPaint);
        if(mViewMode == Constants.EDIT_MODE_PHOTO_AREA_DISPLAY){
            /*
            * 绘制放缩按钮
            * Draw zoom button
            * */
            canvas.drawBitmap(scaleImgBtn,mViewRectF.right - scaleImgBtn.getWidth(),mViewRectF.bottom - scaleImgBtn.getHeight() ,mRectPaint);
            scaleRectF.set(mViewRectF.right - scaleImgBtn.getWidth(),mViewRectF.bottom - scaleImgBtn.getHeight(), mViewRectF.right,mViewRectF.bottom);
            /*
            * 绘制文本
            * Draw text
            * */
            if(!mDrawText.isEmpty())
                canvas.drawText(mDrawText, mViewRectF.left + ScreenUtils.dip2px(mContext,8),mViewRectF.bottom - ScreenUtils.dip2px(mContext,8), mTextPaint);
        }else if(mViewMode == Constants.EDIT_MODE_PHOTO_TOTAL_DISPLAY){//图片运动-全图显示
            //handle heres
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                originLeft = getLeft();
                originRight = getRight();
                originTop = getTop();
                originBottom = getBottom();
                prevRawX = event.getRawX();
                prevRawY = event.getRawY();
                mClickPosition = getClickPosition((int) Math.floor(event.getX() + 0.5D),(int) Math.floor(event.getY() + 0.5D));
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                float tempRawX = event.getRawX();
                float tempRawY = event.getRawY();
                int dx = (int) Math.floor(tempRawX - prevRawX + 0.5D);
                int dy = (int) Math.floor(tempRawY - prevRawY + 0.5D);
                prevRawX = tempRawX;
                prevRawY = tempRawY;
                if(mViewMode == Constants.EDIT_MODE_PHOTO_AREA_DISPLAY){
                    if(mClickPosition == POSITION_RIGHT_BOTTOM) {
                        if(dx > dy) {
                            dy = (int) Math.floor(dx / mCurMakeRatio + 0.5D);
                        } else {
                            dx = (int) Math.floor(dy * mCurMakeRatio + 0.5D);
                        }
                        scaleRec(dx,dy);
                    }else {
                        moveView(dx,dy);
                    }

                }else if(mViewMode == Constants.EDIT_MODE_PHOTO_TOTAL_DISPLAY){
                    //handle here
                }

                this.layout(originLeft, originTop, originRight, originBottom);
                break;
            }

            case MotionEvent.ACTION_UP:{
                mClickPosition = POSITION_CENTER;
                if(mDrawRectListener != null){
                    mDrawRectListener.onDrawRect(new RectF(getLeft(),getTop(),getRight(),getBottom()));
                }
                break;
            }
        }
        invalidate();
        return true;
    }

    private void moveView(int dx,int dy){
        originLeft += dx;
        originRight += dx;
        originTop += dy;
        originBottom += dy;
        if (originLeft < 0) {
            originLeft = 0;
            originRight = originLeft + getWidth();
        }
        if (originRight > mImgWidth) {
            originRight = mImgWidth;
            originLeft = originRight - getWidth();
        }
        if (originTop < 0) {
            originTop = 0;
            originBottom = originTop + getHeight();
        }
        if (originBottom > mImgHeight) {
            originBottom = mImgHeight;
            originTop = originBottom - getHeight();
        }
    }

    private void scaleRec(int dx,int dy){
        originRight += dx;
        originBottom += dy;
        if (originRight > mImgWidth) {
            originRight = mImgWidth;
            originBottom = (int)Math.floor((originRight - originLeft) / mCurMakeRatio + 0.5D) + originTop;
        }

        if (originBottom > mImgHeight) {
            originBottom = mImgHeight;
            originRight = (int)Math.floor((originBottom - originTop) * mCurMakeRatio + 0.5D) + originLeft;
        }

        if(mCurMakeRatio >= 1.0){
            if (originRight - originLeft < mMinPixelValue) {
                originRight = originLeft + mMinPixelValue;
                originBottom = (int)Math.floor(mMinPixelValue / mCurMakeRatio + 0.5D) + originTop;
            }
        }else{
            if (originBottom - originTop < mMinPixelValue) {
                originBottom = originTop + mMinPixelValue;
                originRight = (int)Math.floor(mMinPixelValue * mCurMakeRatio + 0.5D) + originLeft;
            }
        }
    }

    private int getClickPosition(int x, int y) {
        if (originRight - originLeft - x < mScaleImgBtnWidth && originBottom - originTop - y < mScaleImgBtnHeight) {
            return POSITION_RIGHT_BOTTOM;
        }
        return POSITION_CENTER;
    }
    public interface OnDrawRectListener {
        void onDrawRect(RectF rectF);
    }
}
