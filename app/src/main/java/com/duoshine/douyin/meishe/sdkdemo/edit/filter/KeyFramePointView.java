package com.duoshine.douyin.meishe.sdkdemo.edit.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.duoshine.douyin.R;
import com.meicam.sdk.NvsTimeline;

import java.util.HashMap;

/**
 * @author ms
 */
public class KeyFramePointView extends View {
    private static final String TAG = "KeyFramePointView";

    private Context mContext;
    private double mPixelPerMicrosecond;
    private HashMap<Long, Double> mKeyFramePointList;
    private Paint mPaint = new Paint();
    private Bitmap mKeyFrameBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.key_frame_point_icon);
    private Bitmap mKeyFrameBitmapCurrent = BitmapFactory.decodeResource(getResources(), R.mipmap.key_frame_point_icon_selected);
    private NvsTimeline mTimeline;
    //当前指针的位置对应的timeLine的长度位置
    private long currentKeyFramePosition = -1;
    /**
     * 使用Point 代表从左到右的范围
     */
    private HashMap<Point, Long> mCircleToStampMap = new HashMap<>();


    public KeyFramePointView(Context context) {
        this(context, null);
    }

    public KeyFramePointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setBackground(getResources().getDrawable(android.R.color.transparent));
        mPaint.setAntiAlias(true);
    }

    public HashMap<Point, Long> getCircleToStampMap() {
        return mCircleToStampMap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取宽度高度的数据和测量模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        widthSize = duringToLength(mTimeline.getDuration());
        setMeasuredDimension(widthSize,heightSize);

    }

    public void setCurrentKeyFramePosition(long currentKeyFramePosition) {
        this.currentKeyFramePosition = currentKeyFramePosition;
        invalidate();
    }

    public void setPixelPerMicrosecond(double pixelPerMicrosecond) {
        mPixelPerMicrosecond = pixelPerMicrosecond;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        if (mTimeline != null) {
            layoutParams.width = duringToLength(mTimeline.getDuration());
            setLayoutParams(layoutParams);
            updateKeyFrameList(mKeyFramePointList);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e("onSizeChanged","width ="+w+"  oldWidth="+oldw);
    }

    public void initKeyFramePointView(HashMap<Long, Double> keyFramePointList, NvsTimeline timeline, double pixelPerMicrosecond) {
        mPixelPerMicrosecond = pixelPerMicrosecond;
        mKeyFramePointList = keyFramePointList;
        mTimeline = timeline;
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = duringToLength(timeline.getDuration());
        setLayoutParams(layoutParams);
        updateKeyFrameList(mKeyFramePointList);
    }

    public void updateKeyFrameList(HashMap<Long, Double> keyFrameList) {
        mKeyFramePointList = keyFrameList;
        invalidate();
    }

    public int duringToLength(long during) {
        return (int) Math.floor(during * mPixelPerMicrosecond + 0.5D);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCircleToStampMap.clear();
        if ((mKeyFramePointList == null) || (mKeyFramePointList.size() == 0)) {
            return;
        }
        for (Long aLong : mKeyFramePointList.keySet()) {
            int iconPosition = duringToLength(aLong);
            int left = iconPosition - mKeyFrameBitmap.getWidth() / 2;
            if (currentKeyFramePosition >= left && currentKeyFramePosition <= left+mKeyFrameBitmap.getWidth()){
                canvas.drawBitmap(mKeyFrameBitmapCurrent, iconPosition - mKeyFrameBitmapCurrent.getWidth() / 2,
                        getHeight() / 2 - mKeyFrameBitmapCurrent.getHeight() / 2, mPaint);
                mCircleToStampMap.put(new Point(iconPosition - mKeyFrameBitmapCurrent.getWidth() / 2, iconPosition + mKeyFrameBitmapCurrent.getWidth() / 2), aLong);

            }else{
                canvas.drawBitmap(mKeyFrameBitmap, iconPosition - mKeyFrameBitmap.getWidth() / 2,
                        getHeight() / 2 - mKeyFrameBitmap.getHeight() / 2, mPaint);
                mCircleToStampMap.put(new Point(iconPosition - mKeyFrameBitmap.getWidth() / 2, iconPosition + mKeyFrameBitmap.getWidth() / 2), aLong);
            }
        }
    }
}

