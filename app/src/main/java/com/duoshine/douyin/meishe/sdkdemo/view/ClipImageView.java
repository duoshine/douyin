package com.duoshine.douyin.meishe.sdkdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;

/**
 *  图片裁剪view
 *  Image crop view
 */
public class ClipImageView extends AppCompatImageView implements
        ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {
    private final Paint mPaint;

    private  int mMaskColor;
    private String mTipText;
    private  int mClipPadding;

    private float mScaleMax = 4.0f;
    private float mScaleMin = 2.0f;
    private int mMode;

    private boolean mResetImageClip = false;

    /**
     * 初始化时的缩放比例
     * Scaling during initialization
     */
    private float mInitScale = 1.0f;

    /**
     * 用于存放矩阵
     * Used to store the matrix
     */
    private final float[] mMatrixValues = new float[9];

    /**
     * 缩放的手势检查
     * Zoom gesture check
     */
    private ScaleGestureDetector mScaleGestureDetector = null;
    private final Matrix mScaleMatrix = new Matrix(), mScaleMatrixClone = new Matrix();

    /**
     * 用于双击
     * For double clicking
     */
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;

    private float mLastX;
    private float mLastY;

    private boolean isCanDrag;
    private int lastPointerCount;

    private Rect mClipBorder = new Rect();
    private int mMaxOutputWidth = 0;

    private boolean mDrawCircleFlag =false;
    private float mRoundCorner;

    public ClipImageView(Context context) {
        this(context, null);
    }

    public ClipImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setScaleType(ImageView.ScaleType.MATRIX);
        mGestureDetector = new GestureDetector(context,
                new SimpleOnGestureListener() {  
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (isAutoScale)
                            return true;

                        float x = e.getX();
                        float y = e.getY();
                        if (getScale() < mScaleMin) {
                            ClipImageView.this.postDelayed(new AutoScaleRunnable(mScaleMin, x, y), 16);
                        } else {
                            ClipImageView.this.postDelayed(new AutoScaleRunnable(mInitScale, x, y), 16);
                        }
                        isAutoScale = true;

                        return true;
                    }
                });
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mDrawCircleFlag = false;
        mRoundCorner = 0;
        mPaint.setDither(true);
    }

    public void setMaskColor(int color) {
        mMaskColor = color;
    }

    public void setClipPadding(int padding) {
        mClipPadding = padding;
    }

    /**
     * 自动缩放的任务
     * Auto-scaling tasks
     */
    private class AutoScaleRunnable implements Runnable {
        static final float BIGGER = 1.07f;
        static final float SMALLER = 0.93f;
        private float mTargetScale;
        private float tmpScale;

        /**
         * 缩放的中心
         * Zoom center
         */
        private float x;
        private float y;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         * Pass in the target zoom value, and determine whether to zoom in or out based on the target value and the current value
         *
         * @param targetScale
         */
        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALLER;
            }

        }

        @Override
        public void run() {
            /*
            * 进行缩放
            * Zoom
            * */
            isCanDrag = false;
            mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorder();
            setImageMatrix(mScaleMatrix);

            final float currentScale = getScale();
            /*
            * 如果值在合法范围内，继续缩放
            * If the value is within the legal range, continue scaling
            * */
            if (((tmpScale > 1f) && (currentScale < mTargetScale))
                    || ((tmpScale < 1f) && (mTargetScale < currentScale))) {
                ClipImageView.this.postDelayed(this, 16);
            } else {
                /*
                * 设置为目标的缩放比例
                * Set as the target's zoom ratio
                * */
                final float deltaScale = mTargetScale / currentScale;
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y);
                checkBorder();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }

        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        isCanDrag = false;
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null)
            return true;

        /**
         * 缩放的范围控制
         * Zoom range control
         */
        if ((scale < mScaleMax && scaleFactor > 1.0f)
                || (scale > mInitScale && scaleFactor < 1.0f)) {

            /**
             * 缩放阙值最小值判断
             * Judging the minimum value of the zoom threshold
             */
            if (scaleFactor * scale < mInitScale) {
                scaleFactor = mInitScale / scale;
            }
            if (scaleFactor * scale > mScaleMax) {
                scaleFactor = mScaleMax / scale;
            }

            /**
             * 设置缩放比例
             * Set the zoom ratio
             */
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());
            checkBorder();
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     * Get the range of the picture according to the matrix of the current picture
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        mScaleMatrixClone.set(mScaleMatrix);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (mGestureDetector.onTouchEvent(event))
            return true;
        mScaleGestureDetector.onTouchEvent(event);

        float x = 0, y = 0;
        /*
        * 获取触摸点的个数
        * Get the number of touch points
        * */
        final int pointerCount = event.getPointerCount();

        /*
        * 得到多个触摸点的x与y均值
        * Get the average x and y of multiple touch points
        * */
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x /= pointerCount;
        y /= pointerCount;

        /**
         * 每当触摸点发生变化时，重置mLasX , mLastY
         * Whenever the touch point changes, reset mLasX, mLastY
         */
        if (pointerCount != lastPointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        lastPointerCount = pointerCount;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                if (isCanDrag) {
                    if (getDrawable() != null) {

                        RectF rectF = getMatrixRectF();
                        /*
                        * 如果宽度小于屏幕宽度，则禁止左右移动
                        * If the width is smaller than the screen width, it is prohibited to move left and right
                        * */
                        if (rectF.width() <= mClipBorder.width()) {
                            dx = 0;
                        }

                        /*
                        * 如果高度小于屏幕高度，则禁止上下移动
                        * If the height is less than the screen height, it is prohibited to move up and down
                        * */
                        if (rectF.height() <= mClipBorder.height()) {
                            dy = 0;
                        }
                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorder();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                break;
        }
        mScaleMatrixClone.set(mScaleMatrix);
        return true;
    }

    /**
     * 获得当前的缩放比例
     * Get the current zoom ratio
     * @return
     */
    public final float getScale() {
        mScaleMatrix.getValues(mMatrixValues);
        return mMatrixValues[Matrix.MSCALE_X];
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateBorder();
    }

    private void updateBorder() {
        final int width = getWidth();
        final int height = getHeight();
        mClipBorder.left = mClipPadding;
        mClipBorder.right = width - mClipPadding;

        if (mDrawCircleFlag == true) {
            /*
            * 如果是圆形,宽高比例是1:1
            * If it is circular, the aspect ratio is 1: 1
            * */
            final int borderTempHeight = mClipBorder.width() * 1 / 1;
            mClipBorder.top = (height - borderTempHeight) / 2;
            mClipBorder.bottom = mClipBorder.top + borderTempHeight;
        } else {
            /*
            * 如果不是圆形,根据宽高比例
            * If not circular, according to the aspect ratio
            * */
            mClipBorder.top = 0;
            mClipBorder.bottom = mClipBorder.top + height;
        }
    }

    public void setTip(String tip) {
        mTipText = tip;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        postResetImageMatrix();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if(mResetImageClip) {
            postResetImageMatrix();
        } else {
            mScaleMatrix.set(mScaleMatrixClone);
            setImageMatrix(mScaleMatrix);
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        postResetImageMatrix();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        postResetImageMatrix();
    }

    public void setOffsetMode(int mode) {
        mMode = mode;

    }

    /**
     * 这里没有使用post方式,因为图片会有明显的从初始位置移动到需要缩放的位置
     * There is no post method used here, because the picture will obviously move from the initial position to the position that needs to be scaled.
     */
    private void postResetImageMatrix() {
        if (getWidth() != 0) {
            resetImageMatrix();
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    resetImageMatrix();
                    mScaleMatrixClone.set(mScaleMatrix);
                }
            });
        }
    }

    /**
     * 垂直方向与View的边矩
     * Vertical direction and view's edge moment
     */
    public void resetImageMatrix() {
        final Drawable d = getDrawable();
        if (d == null) {
            return;
        }
        final int dWidth = d.getIntrinsicWidth();
        final int dHeight = d.getIntrinsicHeight();
        final int cWidth = mClipPadding;
        final int cHeight = getHeight();
        final int vWidth = getWidth();
        final int vHeight = getHeight();

        final float scale;
        final float dx;
        final float dy;

        if (dWidth * cHeight > cWidth * dHeight) {
            scale = cHeight / (float) dHeight;
        } else {
            scale = cWidth / (float) dWidth;
        }

        dx = (vWidth - dWidth * scale) * 0.5f;
        dy = (vHeight - dHeight * scale) * 0.5f;

        mScaleMatrix.setScale(scale, scale);
        mScaleMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));

        setImageMatrix(mScaleMatrix);

        mInitScale = scale;
        mScaleMin = mInitScale * 2;
        mScaleMax = mInitScale * 4;
    }

    /**
     * 剪切图片
     * Crop picture
     *
     * @return bitmap object
     */
    public Bitmap clip() {
        final Drawable drawable = getDrawable();
        final Bitmap originalBitmap = ((BitmapDrawable) drawable).getBitmap();

        final float[] matrixValues = new float[9];
        mScaleMatrix.getValues(matrixValues);
        final float scale = matrixValues[Matrix.MSCALE_X] * drawable.getIntrinsicWidth() / originalBitmap.getWidth();
        final float transX = matrixValues[Matrix.MTRANS_X];
        final float transY = matrixValues[Matrix.MTRANS_Y];

        float cropX = (-transX + mClipBorder.left) / scale;
        float cropY = (-transY + mClipBorder.top) / scale;
        float cropWidth = mClipBorder.width() / scale;
        float cropHeight = mClipBorder.height() / scale;

        Matrix outputMatrix = null;
        if (mMaxOutputWidth > 0 && cropWidth > mMaxOutputWidth) {
            final float outputScale = mMaxOutputWidth / cropWidth;
            outputMatrix = new Matrix();
            outputMatrix.setScale(outputScale, outputScale);
        }
        if ((int)cropX < 0)
            cropX = 0f;

        if ((int) cropWidth > originalBitmap.getWidth())
            cropWidth = originalBitmap.getWidth();

        if ((int) cropHeight > originalBitmap.getHeight())
            cropHeight = originalBitmap.getHeight();

        Bitmap bit = Bitmap.createBitmap(originalBitmap,
                (int) cropX, (int) cropY, (int) cropWidth, (int) cropHeight,
                outputMatrix, false);
        return bit;
    }

    /**
     * 边界检查
     * Boundary check
     */
    private void checkBorder() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;
        /*
        * 如果宽或高大于屏幕，则控制范围
        * If the width or height is larger than the screen, the control range
        * */
        if (rect.width() >= mClipBorder.width()) {
            if (rect.left > mClipBorder.left) {
                deltaX = -rect.left + mClipBorder.left;
            }

            if (rect.right < mClipBorder.right) {
                deltaX = mClipBorder.right - rect.right;
            }
        }else {
           deltaX  = mClipBorder.left - rect.left;
        }

        if (rect.height() >= mClipBorder.height()) {
            if (rect.top > mClipBorder.top) {
                deltaY = -rect.top + mClipBorder.top;
            }

            if (rect.bottom < mClipBorder.bottom) {
                deltaY = mClipBorder.bottom - rect.bottom;
            }
        }else {
            deltaY = -rect.top + mClipBorder.top;
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 是否是拖动行为
     * Whether it is a drag behavior
     * @param dx
     * @param dy
     * @return
     */
    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= 0;
    }

    public Rect getClipBorder() {
        return mClipBorder;
    }

    public void setMaxOutputWidth(int maxOutputWidth) {
        mMaxOutputWidth = maxOutputWidth;
    }

    public float[] getClipMatrixValues() {
        final float[] matrixValues = new float[9];
        mScaleMatrix.getValues(matrixValues);
        return matrixValues;
    }


    /**
     * 参考showtipsview的做法
     * See the practice of showtipsview
     */
    public void drawRectangleOrCircle(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(bitmap);
        Paint transparentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        transparentPaint.setColor(Color.TRANSPARENT);
        temp.drawRect(0, 0, temp.getWidth(), temp.getHeight(), mPaint);
        transparentPaint.setXfermode(porterDuffXfermode);
        if (mDrawCircleFlag) {
            float cx = mClipBorder.left + mClipBorder.width() / 2f;
            float cy = mClipBorder.top + mClipBorder.height() / 2f;
            float radius = mClipBorder.height() / 2f;
            /*
            * 绘制圆
            * Draw circle
            * */
            temp.drawCircle(cx, cy, radius, transparentPaint);
        } else {
            /*
            * 画矩形(可以设置矩形的圆角)
            * Draw a rectangle (you can set the rounded corners of the rectangle)
            * */
            RectF rectF = new RectF(mClipBorder.left , mClipBorder.top, mClipBorder.right, mClipBorder.bottom);
            temp.drawRoundRect(rectF, mRoundCorner, mRoundCorner, transparentPaint);

            Paint colorPaint = new Paint();
            colorPaint.setAntiAlias(true);
            colorPaint.setColor(0x00000000);
            colorPaint.setStyle(Paint.Style.STROKE);
            colorPaint.setStrokeWidth(5);
            canvas.drawRect(mClipBorder.left, mClipBorder.top, mClipBorder.right, mClipBorder.bottom, colorPaint);
        }
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int width = getWidth();
        final int height = getHeight();

        mPaint.setColor(mMaskColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        drawRectangleOrCircle(canvas);

        if (mTipText != null) {
            final float textWidth = mPaint.measureText(mTipText);
            final float startX = (width - textWidth) / 2;
            final Paint.FontMetrics fm = mPaint.getFontMetrics();
            final float startY = mClipBorder.bottom + mClipBorder.top / 2 - (fm.descent - fm.ascent) / 2;
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(mTipText, startX, startY, mPaint);
        }
    }

    public void resetClipImageView() {
        postResetImageMatrix();
        mScaleMatrixClone.set(mScaleMatrix);
    }

    public void changeImageBitmap(Bitmap bitmap, boolean reset) {
        if(bitmap == null) {
            return;
        }
        mResetImageClip = reset;
        setImageBitmap(bitmap);
    }
}