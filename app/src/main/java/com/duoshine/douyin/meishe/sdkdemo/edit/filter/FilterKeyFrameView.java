package com.duoshine.douyin.meishe.sdkdemo.edit.filter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.utils.OnSeekBarChangeListenerAbs;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;
import com.meicam.sdk.NvsFx;
import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * key frame panel for filter
 *
 * @author ms
 */
public class FilterKeyFrameView extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "FilterKeyFrameView";
    private static final float TIMEBASE = 1000000F;
    private static final String FILTER_INTENSITY = "Filter Intensity";

    private SeekBar mKeyFrameSeekBar;
    private NvsMultiThumbnailSequenceView mKeyFrameSequenceView;
    private RelativeLayout mKeyFrameWrapperView;
    private View mKeyFrameLeftPaddingView;
    private TextView mZoomInView;
    private TextView mBeforeKeyFrameView;
    private TextView mAddDeleteFrameView;
    private TextView mNextKeyFrameView;
    private TextView mZoomOutView;

    private NvsStreamingContext mStreamingContext;
    private Context mContext;
    private double mPixelPerMicrosecond;
    private double mMaxPixelPerMicrosecond;
    private double mMinPixelPerMicrosecond;
    /**
     * 设置view的状态 1.seeking 2.playing
     */
    private boolean mSequenceViewIsSeekingStatus = true;

    private KeyFramePointView mKeyFramePointView;
    private NvsTimeline mTimeline;
    private NvsFx mCurrentFx;
    private OnKeyFrameViewClickListener mOnKeyFrameViewClickListener;
    private OnSequenceScrollChangeListener mOnSequenceScrollChangeListener;

    public void setZoomInViewEnable(boolean enable) {
        mZoomInView.setEnabled(enable);
    }

    public void setBeforeViewEnable(boolean enable) {
        mBeforeKeyFrameView.setEnabled(enable);
    }

    public void setAddDeleteViewEnable(boolean enable) {
        mAddDeleteFrameView.setEnabled(enable);
    }

    public void setCurrentFx(NvsFx currentFx) {
        this.mCurrentFx = currentFx;
        // 关键帧页面打开 重新触发绘制点的操作
        if (mKeyFramePointView != null) {
            mKeyFramePointView.setCurrentKeyFramePosition(LengthAndStampUtils.duringToLength(mStreamingContext.getTimelineCurrentPosition(mTimeline), mPixelPerMicrosecond));
        }
    }

    public void setAddDeleteViewStatus(boolean isAddStatus) {
        if (isAddStatus) {
            mAddDeleteFrameView.setTag(0);
            mAddDeleteFrameView.setText(R.string.key_frame_add_frame_text);
            mAddDeleteFrameView.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.key_frame_add_frame_selector), null, null);
            //
        } else {
            mAddDeleteFrameView.setTag(1);
            mAddDeleteFrameView.setText(R.string.key_frame_delete_frame_text);
            mAddDeleteFrameView.setCompoundDrawablesWithIntrinsicBounds(null,
                    getResources().getDrawable(R.drawable.key_frame_delete_frame_selector), null, null);

        }
    }

    public void setNextViewEnable(boolean enable) {
        mNextKeyFrameView.setEnabled(enable);
    }

    public void setZoomOutView(boolean enable) {
        mZoomInView.setEnabled(enable);
    }

    public FilterKeyFrameView(Context context) {
        this(context, null);
    }

    public FilterKeyFrameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mStreamingContext = NvsStreamingContext.getInstance();
        initLayout(context);
    }

    private void initLayout(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_filter_key_frame_panel, this);
        mKeyFrameSeekBar = findViewById(R.id.key_frame_seekbar);
        mKeyFrameSequenceView = findViewById(R.id.key_frame_sequence_view);
        mKeyFrameWrapperView = findViewById(R.id.key_frame_wrapper_view);
        mKeyFrameLeftPaddingView = findViewById(R.id.key_frame_left_padding_view);
        mZoomInView = findViewById(R.id.zoom_in_view);
        mZoomInView.setOnClickListener(this);
        mBeforeKeyFrameView = findViewById(R.id.before_key_frame_view);
        mBeforeKeyFrameView.setOnClickListener(this);
        mAddDeleteFrameView = findViewById(R.id.add_delete_frame_view);
        mAddDeleteFrameView.setTag(0);
        mAddDeleteFrameView.setOnClickListener(this);
        mNextKeyFrameView = findViewById(R.id.next_key_frame_view);
        mNextKeyFrameView.setOnClickListener(this);
        mZoomOutView = findViewById(R.id.zoom_out_view);
        mZoomOutView.setOnClickListener(this);
        mKeyFramePointView = findViewById(R.id.key_frame_points_view);
    }

    public void initKeyFrameView(String mediaPath, NvsTimeline timeline, HashMap<Long, Double> keyFramePoints) {
        mTimeline = timeline;
        // 缩略图
        mKeyFrameSequenceView.setStartPadding(ScreenUtils.getScreenWidth(mContext) / 2);
        mKeyFrameSequenceView.setEndPadding(ScreenUtils.getScreenWidth(mContext) / 2);
        ArrayList<NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc> sequenceDesArray = new ArrayList<>();
        NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc sequenceDes = new NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc();
        NvsVideoTrack track = timeline.getVideoTrackByIndex(0);
        NvsVideoClip clip = track.getClipByIndex(0);
        sequenceDes.mediaFilePath = mediaPath;
        sequenceDes.trimIn = clip.getTrimIn();
        sequenceDes.trimOut = clip.getTrimOut();
        sequenceDes.inPoint = 0;
        sequenceDes.outPoint = timeline.getDuration();
        sequenceDes.stillImageHint = false;
        sequenceDesArray.add(sequenceDes);
        // 初始化单位
        initPixelPerMicrosecond();
        mKeyFrameSequenceView.setThumbnailSequenceDescArray(sequenceDesArray);
        mKeyFrameSequenceView.setPixelPerMicrosecond(mPixelPerMicrosecond);
        // 初始化点布局
        mKeyFramePointView.initKeyFramePointView(keyFramePoints, mTimeline, mPixelPerMicrosecond);

        //不要用padding left 滑动的时候 会造成滑动异常
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.width = ScreenUtils.getScreenWidth(mContext) / 2;
        mKeyFrameLeftPaddingView.setLayoutParams(params);
        initListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        mKeyFrameSequenceView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mSequenceViewIsSeekingStatus = true;
                return false;
            }
        });
        mKeyFrameSequenceView.setOnScrollChangeListenser(new NvsMultiThumbnailSequenceView.OnScrollChangeListener() {
            @Override
            public void onScrollChanged(NvsMultiThumbnailSequenceView nvsMultiThumbnailSequenceView, int i, int i1) {
                mKeyFrameWrapperView.scrollTo(i, 0);
                // 更新所有按钮的状态
                long currentTimeStamp = (long) Math.floor(i / mPixelPerMicrosecond + 0.5D);
                HashMap<Point, Long> circleToStampMap = mKeyFramePointView.getCircleToStampMap();
                updateAllKeyFrameViewStatusWhenScroll(currentTimeStamp, circleToStampMap);
                if (mOnSequenceScrollChangeListener != null) {
                    // 对画面进行seek
                    if (mSequenceViewIsSeekingStatus) {
                        mOnSequenceScrollChangeListener.onScrollX(currentTimeStamp);
                    }
                }

            }
        });
        mKeyFrameSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAbs() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    // 手动调整强度
                    mStreamingContext.stop();
                    long timelineCurrentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                    int length = LengthAndStampUtils.duringToLength(timelineCurrentPosition, mPixelPerMicrosecond);
                    HashMap<Point, Long> circleToStampMap = mKeyFramePointView.getCircleToStampMap();
                    Set<Point> points = circleToStampMap.keySet();
                    boolean needAddKeyFrame = true;
                    for (Point point : points) {
                        if (length >= point.x && length <= point.y) {
                            needAddKeyFrame = false;
                            if (mOnKeyFrameViewClickListener != null) {
                                mOnKeyFrameViewClickListener.onProgressChanged(needAddKeyFrame, circleToStampMap.get(point), progress * 1.0f / 100);
                            }
                            break;
                        }
                    }
                    if (needAddKeyFrame) {
                        if (mOnKeyFrameViewClickListener != null) {
                            mOnKeyFrameViewClickListener.onProgressChanged(needAddKeyFrame, timelineCurrentPosition, progress * 1.0f / 100);
                        }
                    }
                }
            }
        });
    }

    private void updateAllKeyFrameViewStatusWhenScroll(long currentTimeStamp, HashMap<Point, Long> circleToStampMap) {
        boolean hasKeyframeList = mCurrentFx.hasKeyframeList(FILTER_INTENSITY);
        if (hasKeyframeList) {
            long hasBeforeKeyFrame = mCurrentFx.findKeyframeTime(FILTER_INTENSITY, currentTimeStamp, NvsFx.KEY_FRAME_FIND_MODE_INPUT_TIME_BEFORE);
            if (hasBeforeKeyFrame == -1) {
                setBeforeViewEnable(false);
            } else {
                setBeforeViewEnable(true);
            }
            long hasNextKeyFrame = mCurrentFx.findKeyframeTime(FILTER_INTENSITY, currentTimeStamp, NvsFx.KEY_FRAME_FIND_MODE_INPUT_TIME_AFTER);
            if (hasNextKeyFrame == -1) {
                setNextViewEnable(false);
            } else {
                setNextViewEnable(true);
            }
            boolean hasKeyFrame = false;
            int currentPos = LengthAndStampUtils.duringToLength(currentTimeStamp, mPixelPerMicrosecond);
            Set<Point> points = circleToStampMap.keySet();
            for (Point point : points) {
                if (currentPos >= point.x && currentPos <= point.y) {
                    hasKeyFrame = true;
                    break;
                }
            }
            //刷新当前选中的关键帧图标
            //滚动到关键帧位置，关键帧圆点修改
            mKeyFramePointView.setCurrentKeyFramePosition(currentPos);
            setAddDeleteViewStatus(!hasKeyFrame);
        } else {
            setBeforeViewEnable(false);
            setAddDeleteViewStatus(true);
            setNextViewEnable(false);
        }
        // 更新seekBar强度
        double intensityOfCurrentPos = mCurrentFx.getFloatValAtTime(FILTER_INTENSITY, currentTimeStamp);
        mKeyFrameSeekBar.setProgress((int) (intensityOfCurrentPos * 100));
    }

    private void initPixelPerMicrosecond() {
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        mPixelPerMicrosecond = screenWidth / 20 / TIMEBASE;
        mMaxPixelPerMicrosecond = mPixelPerMicrosecond * Math.pow(1.25, 5);
        mMinPixelPerMicrosecond = mPixelPerMicrosecond * Math.pow(0.8, 5);
    }

    public void setSequenceViewIsSeekingStatus(boolean sequenceViewIsSeekingStatus) {
        this.mSequenceViewIsSeekingStatus = sequenceViewIsSeekingStatus;
    }

    public void updateKeyFramePointView(HashMap<Long, Double> hashMap) {
        mKeyFramePointView.updateKeyFrameList(hashMap);
    }

    public void setOnSequenceScrollChangeListener(OnSequenceScrollChangeListener onSequenceScrollChangeListener) {
        this.mOnSequenceScrollChangeListener = onSequenceScrollChangeListener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.zoom_in_view:
                if (mPixelPerMicrosecond > mMaxPixelPerMicrosecond) {
                    return;
                }
                double scaleFactorIn = 1.25;
                updateSequence(scaleFactorIn);
                break;
            case R.id.before_key_frame_view:
                if (mOnKeyFrameViewClickListener != null) {
                    mOnKeyFrameViewClickListener.beforeFrameClick();
                }
                break;
            case R.id.add_delete_frame_view:
                if (mOnKeyFrameViewClickListener != null) {
                    if ((int) view.getTag() == 0) {
                        mOnKeyFrameViewClickListener.addFrameClick();
                        mKeyFramePointView.setCurrentKeyFramePosition(LengthAndStampUtils.duringToLength(mStreamingContext.getTimelineCurrentPosition(mTimeline), mPixelPerMicrosecond));
                        setAddDeleteViewStatus(false);
                    } else {
                        long deleteStamp = -1;
                        long timelineCurrentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                        HashMap<Point, Long> circleToStampMap = mKeyFramePointView.getCircleToStampMap();
                        Set<Point> points = circleToStampMap.keySet();
                        for (Point point : points) {
                            int i = LengthAndStampUtils.duringToLength(timelineCurrentPosition, mPixelPerMicrosecond);
                            if (i >= point.x && i <= point.y) {
                                deleteStamp = circleToStampMap.get(point);
                                break;
                            }
                        }
                        mOnKeyFrameViewClickListener.deleteFrameClick(deleteStamp);
                        setAddDeleteViewStatus(true);
                    }
                }
                break;
            case R.id.next_key_frame_view:
                if (mOnKeyFrameViewClickListener != null) {
                    mOnKeyFrameViewClickListener.nextFrameClick();
                }
                break;
            case R.id.zoom_out_view:
                if (mPixelPerMicrosecond < mMinPixelPerMicrosecond) {
                    return;
                }
                double scaleFactorOut = 0.8;
                updateSequence(scaleFactorOut);
                break;
            default:
                break;
        }
    }

    private void updateSequence(double scaleFactor) {
        mKeyFrameSequenceView.scaleWithAnchor(scaleFactor, ScreenUtils.getScreenWidth(mContext) / 2);
        mPixelPerMicrosecond = mKeyFrameSequenceView.getPixelPerMicrosecond();
        mKeyFramePointView.setPixelPerMicrosecond(mPixelPerMicrosecond);

    }

    // 滚动关键帧缩略图动画
    public void scrollSequenceViewTo(long stamp) {
        if (mKeyFrameSequenceView != null) {
            int x = Math.round((stamp / (float) mTimeline.getDuration() * getSequenceWidth()));
            mKeyFrameSequenceView.scrollTo(x, 0);
        }
    }

    public int getSequenceWidth() {
        return (int) Math.floor(mTimeline.getDuration() * mPixelPerMicrosecond + 0.5D);
    }

    public interface OnSequenceScrollChangeListener {
        void onScrollX(long currentTimeStamp);
    }


    public interface OnKeyFrameViewClickListener {
        void addFrameClick();

        void nextFrameClick();

        void beforeFrameClick();

        void deleteFrameClick(long deleteStamp);

        void onProgressChanged(boolean needAddKeyFrame, long currentStamp, double intensity);
    }

    public void setOnKeyFrameViewClickListener(OnKeyFrameViewClickListener onKeyFrameViewClickListener) {
        this.mOnKeyFrameViewClickListener = onKeyFrameViewClickListener;
    }
}
