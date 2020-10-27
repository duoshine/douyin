package com.duoshine.douyin.meishe.sdkdemo.edit;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.DrawRect;
import com.duoshine.douyin.meishe.sdkdemo.utils.Constants;
import com.duoshine.douyin.meishe.sdkdemo.utils.Logger;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;
import com.duoshine.douyin.meishe.sdkdemo.view.ClipImageView;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yyj on 2018/5/29 0029.
 * VideoFragment，封装liveWindow,供多个页面使用，避免代码重复
 * VideoFragment, encapsulate liveWindow for multiple pages, avoid code duplication
 */

public class VideoFragment extends Fragment {
    private final String TAG = "VideoFragment";
    private static final float DEFAULT_SCALE_VALUE = 1.0f;
    private static final long BASE_VALUE = 100000;

    private static final int RESETPLATBACKSTATE = 100;
    private RelativeLayout mPlayerLayout;
    private NvsLiveWindow mLiveWindow;
    private DrawRect mDrawRect;
    private LinearLayout mPlayBarLayout;
    private RelativeLayout mPlayButton;
    private ImageView mPlayImage;
    private TextView mCurrentPlayTime;
    private SeekBar mPlaySeekBar;
    private TextView mTotalDuration;
    private RelativeLayout mVoiceButton;
    private ClipImageView mClipImageView;

    private NvsStreamingContext mStreamingContext = NvsStreamingContext.getInstance();
    private NvsTimeline mTimeline;
    private boolean mPlayBarVisibleState = true, mVoiceButtonVisibleState = false, mAutoPlay = false, mRecording = false;
    private OnFragmentLoadFinisedListener mFragmentLoadFinisedListener;
    private VideoFragmentListener mVideoFragmentCallBack;
    private AssetEditListener mAssetEditListener;
    private WaterMarkChangeListener waterMarkChangeListener;
    private VideoVolumeListener mVideoVolumeListener;
    private OnLiveWindowClickListener mLiveWindowClickListener;
    private OnStickerMuteListener mStickerMuteListener;
    private VideoCaptionTextEditListener mCaptionTextEditListener;
    private OnThemeCaptionSeekListener mThemeCaptionSeekListener;
    private NvsTimelineCaption mCurCaption;
    private int mEditMode = 0;
    private NvsTimelineAnimatedSticker mCurAnimateSticker;
    private int mStickerMuteIndex = 0;
    private NvsTimelineCompoundCaption mCurCompoundCaption;
    private OnCompoundCaptionListener mCompoundCaptionListener;
    /*
     * 播放开始标识
     * Play start identification
     * */
    private long mPlayStartFlag = -1;
    private boolean mShowSeekbar = true;
    //liveWindow 实际view中坐标点
    /*
     * liveWindow 实际view中坐标点
     * coordinate point in the actual view of liveWindow
     * */
    private List<PointF> pointFListLiveWindow;

    /*
     * 第一次添加水印时的原始坐标列表，用于计算偏移量
     * A list of the original coordinates when the watermark was first added, used to calculate the offset
     * */
    private List<PointF> pointFListToFirstAddWaterMark;
    private IBeforeAnimateStickerEditListener mBeforeAnimateStickerEditListener;
    //动画页面调用时传入，其他位置忽略即可
    private long mPlayStartPoint = 0;
    private long mPlayEndPoint = 0;
    private boolean mIsAnimationView = false;

    public void setIsAnimationView(boolean mIsAnimationView) {
        this.mIsAnimationView = mIsAnimationView;
        mPlayStartPoint = 0;
        mPlayEndPoint = mTimeline.getDuration();
    }

    /*
     * Fragment加载完成回调
     * Fragment loading completion callback
     * */
    public interface OnFragmentLoadFinisedListener {
        void onLoadFinished();
    }

    /*
     * 视频播放相关回调
     * Video playback related callbacks
     * */
    public interface VideoFragmentListener {
        //video play
        void playBackEOF(NvsTimeline timeline);

        void playStopped(NvsTimeline timeline);

        void playbackTimelinePosition(NvsTimeline timeline, long stamp);

        void streamingEngineStateChanged(int state);
    }

    /*
     * 贴纸和字幕编辑对应的回调，其他素材不用
     * Callbacks for stickers and subtitle editing, other materials are not used
     * */
    public abstract static class AssetEditListener {
        public abstract void onAssetDelete();

        public void onAssetSelected(PointF curPoint){}

        public void onAssetTranstion(){}

        public void onAssetScale(){}

        /*
         * 字幕专用
         * Subtitle only
         * */
        public void onAssetAlign(int alignVal){}

        //字幕横竖屏切换
        public void onOrientationChange(boolean isHorizontal){}

        /*
         * 贴纸使用
         * Stickers only
         * */
        public void onAssetHorizFlip(boolean isHorizFlip){}
    }

    public interface WaterMarkChangeListener {
        void onDrag(List<PointF> list);

        void onScaleAndRotate(List<PointF> curPoint);
    }

    /*
     * 音量回调
     * Volume callback
     * */
    public interface VideoVolumeListener {
        void onVideoVolume();
    }

    /*
     * 字幕文本修改回调
     * Subtitle text modification callback
     * */
    public interface VideoCaptionTextEditListener {
        void onCaptionTextEdit();
    }

    /*
     * 组合字幕索引回调
     * Combined subtitle index callback
     * */
    public interface OnCompoundCaptionListener {
        void onCaptionIndex(int captionIndex);
    }

    /*
     * LiveWindowd点击回调
     * LiveWindowd click callback
     * */
    public interface OnLiveWindowClickListener {
        void onLiveWindowClick();
    }

    /*
     * 贴纸静音点击回调
     * Sticker mute click callback
     * */
    public interface OnStickerMuteListener {
        void onStickerMute();
    }

    public interface OnThemeCaptionSeekListener {
        void onThemeCaptionSeek(long stamp);
    }

    public void setThemeCaptionSeekListener(OnThemeCaptionSeekListener themeCaptionSeekListener) {
        mThemeCaptionSeekListener = themeCaptionSeekListener;
    }

    public void setLiveWindowClickListener(OnLiveWindowClickListener liveWindowClickListener) {
        this.mLiveWindowClickListener = liveWindowClickListener;
    }

    public void setCaptionTextEditListener(VideoCaptionTextEditListener captionTextEditListener) {
        this.mCaptionTextEditListener = captionTextEditListener;
    }

    public void setFragmentLoadFinisedListener(OnFragmentLoadFinisedListener fragmentLoadFinisedListener) {
        this.mFragmentLoadFinisedListener = fragmentLoadFinisedListener;
    }

    public void setVideoFragmentCallBack(VideoFragmentListener videoFragmentCallBack) {
        this.mVideoFragmentCallBack = videoFragmentCallBack;
    }

    public void setAssetEditListener(AssetEditListener assetEditListener) {
        this.mAssetEditListener = assetEditListener;
    }

    public void setWaterMarkChangeListener(WaterMarkChangeListener waterMarkChangeListener) {
        this.waterMarkChangeListener = waterMarkChangeListener;
    }

    public void setVideoVolumeListener(VideoVolumeListener videoVolumeListener) {
        this.mVideoVolumeListener = videoVolumeListener;
    }

    public void setStickerMuteListener(OnStickerMuteListener stickerMuteListener) {
        this.mStickerMuteListener = stickerMuteListener;
    }

    public void setCompoundCaptionListener(OnCompoundCaptionListener compoundCaptionListener) {
        this.mCompoundCaptionListener = compoundCaptionListener;
    }

    private Handler m_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case RESETPLATBACKSTATE:
                    updateCurPlayTime(0);
                    seekTimeline(mPlayStartPoint, 0);
                    /*
                     * 播放进度条显示
                     * Play progress bar display
                     * */
                    if (mPlayBarVisibleState) {
                        mPlayStartFlag = -1;
                        mPlayBarLayout.setVisibility(View.VISIBLE);
                        startHidePlayBarTimer(true);
                    }
                    break;
            }
            return false;
        }
    });

    private CountDownTimer m_hidePlayBarTimer = new CountDownTimer(4000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            /*
             * 播放进度条显示
             * Play progress bar display
             * */
            if (mPlayBarVisibleState && !mShowSeekbar) {
                mPlayStartFlag = -1;
                mPlayBarLayout.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
        mPlayerLayout = (RelativeLayout) rootView.findViewById(R.id.player_layout);
        mLiveWindow = (NvsLiveWindow) rootView.findViewById(R.id.liveWindow);
        mDrawRect = (DrawRect) rootView.findViewById(R.id.draw_rect);
        mPlayBarLayout = (LinearLayout) rootView.findViewById(R.id.playBarLayout);
        mPlayButton = (RelativeLayout) rootView.findViewById(R.id.playLayout);
        mPlayImage = (ImageView) rootView.findViewById(R.id.playImage);
        mCurrentPlayTime = (TextView) rootView.findViewById(R.id.currentPlaytime);
        mPlaySeekBar = (SeekBar) rootView.findViewById(R.id.play_seekBar);
        mTotalDuration = (TextView) rootView.findViewById(R.id.totalDuration);
        mVoiceButton = (RelativeLayout) rootView.findViewById(R.id.voiceLayout);
        mClipImageView = (ClipImageView) rootView.findViewById(R.id.clip_image_view);
        controllerOperation();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
        initData();
        //
        mPlayBarLayout.setVisibility(mPlayBarVisibleState ? View.VISIBLE : View.GONE);
        mVoiceButton.setVisibility(mVoiceButtonVisibleState ? View.VISIBLE : View.GONE);
        if (mFragmentLoadFinisedListener != null) {
            mFragmentLoadFinisedListener.onLoadFinished();
        }
    }

    private void initData() {
        setLivewindowRatio();
        updateTotalDuarationText();
        updateCurPlayTime(0);
        initDrawRectListener();
    }

    private void setLivewindowRatio() {
        Bundle bundle = getArguments();
        int ratio = 0, titleHeight = 0, bottomHeight = 0;
        if (bundle != null) {
            ratio = bundle.getInt("ratio", NvAsset.AspectRatio_16v9);
            titleHeight = bundle.getInt("titleHeight");
            bottomHeight = bundle.getInt("bottomHeight");
            mPlayBarVisibleState = bundle.getBoolean("playBarVisible", true);
            mVoiceButtonVisibleState = bundle.getBoolean("voiceButtonVisible", false);
        }

        if (null == mTimeline) {
            Log.e(TAG, "mTimeline is null!");
            return;
        }
        setLiveWindowRatio(ratio, titleHeight, bottomHeight);
        connectTimelineWithLiveWindow();
    }

    public void updateCurPlayTime(long time) {
        mCurrentPlayTime.setText(formatTimeStrWithUs(time));
        mPlaySeekBar.setProgress((int) (time / BASE_VALUE));
    }

    public void updateTotalDuarationText() {
        if (mTimeline != null) {
            mTotalDuration.setText(formatTimeStrWithUs(mTimeline.getDuration()));
            mPlaySeekBar.setMax((int) (mTimeline.getDuration() / BASE_VALUE));
        }
    }

    public void setTimeline(NvsTimeline timeline) {
        mTimeline = timeline;
    }

    public Point getLiveWindowSize() {
        Logger.e(TAG, "mLiveWindow宽高获取  " + mLiveWindow.getWidth() + "    " + mLiveWindow.getHeight());
        return new Point(mLiveWindow.getWidth(), mLiveWindow.getHeight());
    }

    private void initDrawRectListener() {
        mDrawRect.setOnTouchListener(new DrawRect.OnTouchListener() {
            @Override
            public void onDrag(PointF prePointF, PointF nowPointF) {

                /* 坐标转换
                 *
                 * SDK接口所使用的坐标均是Canonical坐标系内的坐标，而我们在程序中所用是的
                 * 一般是Android View 坐标系里面的坐标，所以在使用接口的时候需要使用SDK所
                 * 提供的mapViewToCanonical函数将View坐标转换为Canonical坐标，相反的，
                 * 如果想要将Canonical坐标转换为View坐标，则可以使用mapCanonicalToView函数进行转换。
                 *
                 *
                 * Coordinate transformation
                 * The coordinates used by the DK interface are the coordinates in the Canonical coordinate system,
                 * and the coordinates we use in the program are generally the coordinates in the Android View coordinate system,
                 * so when using the interface, you need to use the mapViewToCanonical function provided by the SDK to view the coordinates Convert to Canonical coordinates.
                 * Conversely, if you want to convert Canonical coordinates to View coordinates, you can use the mapCanonicalToView function to convert them.
                 * */
                PointF pre = mLiveWindow.mapViewToCanonical(prePointF);
                PointF p = mLiveWindow.mapViewToCanonical(nowPointF);
                PointF timeLinePointF = new PointF(p.x - pre.x, p.y - pre.y);
                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    /*
                     * 移动字幕
                     * Moving captions
                     * */
                    if (mCurCaption != null) {
                        if (mBeforeAnimateStickerEditListener != null) {
                            boolean b = mBeforeAnimateStickerEditListener.beforeTransitionCouldDo();
                            if (!b) {
                                return;
                            }
                        }

                        //mTimeline.setupInputCacheForCaption(mCurCaption,mStreamingContext.getTimelineCurrentPosition(mTimeline));//解决拖拽字幕跟不上拖拽框的问题
                        mCurCaption.translateCaption(timeLinePointF);
                        updateCaptionCoordinate(mCurCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_STICKER) {
                    /*
                     * 移动贴纸
                     * Mobile stickers
                     * */
                    if (mCurAnimateSticker != null) {
                        if (mBeforeAnimateStickerEditListener != null) {
                            boolean b = mBeforeAnimateStickerEditListener.beforeTransitionCouldDo();
                            if (!b) {
                                return;
                            }
                        }
                        mCurAnimateSticker.translateAnimatedSticker(timeLinePointF);
                        updateAnimateStickerCoordinate(mCurAnimateSticker);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_WATERMARK) {
                    updateWaterMarkPositionOnDrag(timeLinePointF.x, timeLinePointF.y, mDrawRect.getDrawRect());
                } else if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (mCurCompoundCaption != null) {
                        mCurCompoundCaption.translateCaption(timeLinePointF);
                        updateCompoundCaptionCoordinate(mCurCompoundCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                }
                if (mAssetEditListener != null) {
                    mAssetEditListener.onAssetTranstion();
                }
            }

            @Override
            public void onScaleAndRotate(float scaleFactor, PointF anchor, float angle) {
                /* 坐标转换
                 *
                 * SDK接口所使用的坐标均是Canonical坐标系内的坐标，而我们在程序中所用是的
                 * 一般是Android View 坐标系里面的坐标，所以在使用接口的时候需要使用SDK所
                 * 提供的mapViewToCanonical函数将View坐标转换为Canonical坐标，相反的，
                 * 如果想要将Canonical坐标转换为View坐标，则可以使用mapCanonicalToView函数进行转换。
                 *
                 *
                 * Coordinate transformation
                 * The coordinates used by the DK interface are the coordinates in the Canonical coordinate system,
                 * and the coordinates we use in the program are generally the coordinates in the Android View coordinate system,
                 * so when using the interface, you need to use the mapViewToCanonical function provided by the SDK to view the coordinates Convert to Canonical coordinates.
                 * Conversely, if you want to convert Canonical coordinates to View coordinates, you can use the mapCanonicalToView function to convert them.
                 * */
                PointF assetAnchor = mLiveWindow.mapViewToCanonical(anchor);
                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    if (mCurCaption != null) {
                        if (mBeforeAnimateStickerEditListener != null) {
                            boolean b = mBeforeAnimateStickerEditListener.beforeScaleCouldDo();
                            if (!b) {
                                return;
                            }
                        }

                        //mTimeline.setupInputCacheForCaption(mCurCaption,mStreamingContext.getTimelineCurrentPosition(mTimeline));//解决拖拽字幕跟不上拖拽框的问题
                        /*
                         * 放缩字幕
                         * Shrink captions
                         * */
                        mCurCaption.scaleCaption(scaleFactor, assetAnchor);
                        /*
                         * 旋转字幕
                         * Spin subtitles
                         * */
                        mCurCaption.rotateCaption(angle);
                        updateCaptionCoordinate(mCurCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_STICKER) {
                    /*
                     *  放缩贴纸
                     * Scale stickers
                     * */
                    if (mCurAnimateSticker != null) {
                        if (mBeforeAnimateStickerEditListener != null) {
                            boolean b = mBeforeAnimateStickerEditListener.beforeScaleCouldDo();
                            if (!b) {
                                return;
                            }
                        }
                        mCurAnimateSticker.scaleAnimatedSticker(scaleFactor, assetAnchor);
                        /*
                         * 旋转贴纸
                         * Rotate stickers
                         * */
                        mCurAnimateSticker.rotateAnimatedSticker(angle);
                        updateAnimateStickerCoordinate(mCurAnimateSticker);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                    }
                } else if (mEditMode == Constants.EDIT_MODE_WATERMARK) {
                    updateWaterMarkPositionOnScaleAndRotate(scaleFactor, anchor, angle, mDrawRect.getDrawRect());
                } else if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (mCurCompoundCaption != null) {
                        mCurCompoundCaption.scaleCaption(scaleFactor, assetAnchor);
                        /*
                         * 旋转字幕
                         * Spin subtitles
                         * */
                        mCurCompoundCaption.rotateCaption(angle, assetAnchor);
                        float scaleX = mCurCompoundCaption.getScaleX();
                        float scaleY = mCurCompoundCaption.getScaleY();
                        if (scaleX <= DEFAULT_SCALE_VALUE && scaleY <= DEFAULT_SCALE_VALUE) {
                            mCurCompoundCaption.setScaleX(DEFAULT_SCALE_VALUE);
                            mCurCompoundCaption.setScaleY(DEFAULT_SCALE_VALUE);
                        }
                        updateCompoundCaptionCoordinate(mCurCompoundCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                }
                if (mAssetEditListener != null) {
                    mAssetEditListener.onAssetScale();
                }
            }

            @Override
            public void onDel() {
                if (mAssetEditListener != null) {
                    mAssetEditListener.onAssetDelete();
                }
            }

            @Override
            public void onTouchDown(PointF curPoint) {
                if (mAssetEditListener != null) {
                    mAssetEditListener.onAssetSelected(curPoint);
                }
            }

            @Override
            public void onAlignClick(boolean isHorizontal) {
                if (mEditMode == Constants.EDIT_MODE_CAPTION
                        && mCurCaption != null) {
                    switch (mCurCaption.getTextAlignment()) {
                        case NvsTimelineCaption.TEXT_ALIGNMENT_LEFT:
                            mCurCaption.setTextAlignment(NvsTimelineCaption.TEXT_ALIGNMENT_CENTER);  //居中对齐
                            setAlignIndex(1);
                            break;
                        case NvsTimelineCaption.TEXT_ALIGNMENT_CENTER:
                            mCurCaption.setTextAlignment(NvsTimelineCaption.TEXT_ALIGNMENT_RIGHT);  //居右/上对齐
                            setAlignIndex(2);
                            break;
                        case NvsTimelineCaption.TEXT_ALIGNMENT_RIGHT:
                            mCurCaption.setTextAlignment(NvsTimelineCaption.TEXT_ALIGNMENT_LEFT);  //左或者下对齐
                            setAlignIndex(0);
                            break;
                    }
                    updateCaptionCoordinate(mCurCaption);
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    if (mAssetEditListener != null) {
                        mAssetEditListener.onAssetAlign(mCurCaption.getTextAlignment());
                    }
                }
            }

            @Override
            public void onOrientationChange(boolean isHorizontal) {
                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    //切换横竖字幕
                    if (mCurCaption != null) {
                        mCurCaption.setVerticalLayout(!isHorizontal);
                        updateCaptionCoordinate(mCurCaption);
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                    }
                    if (mAssetEditListener != null) {
                        mAssetEditListener.onOrientationChange(isHorizontal);
                    }
                }
            }

            @Override
            public void onHorizontalFlipClick() {
                if (mEditMode == Constants.EDIT_MODE_STICKER) {
                    if (mCurAnimateSticker == null)
                        return;
                    /*
                     * 贴纸水平翻转
                     * Sticker flips horizontally
                     * */
                    boolean isHorizontalFlip = !mCurAnimateSticker.getHorizontalFlip();
                    mCurAnimateSticker.setHorizontalFlip(isHorizontalFlip);
                    updateAnimateStickerCoordinate(mCurAnimateSticker);
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_ANIMATED_STICKER_POSTER);
                    if (mAssetEditListener != null) {
                        mAssetEditListener.onAssetHorizFlip(isHorizontalFlip);
                    }
                }
            }

            @Override
            public void onBeyondDrawRectClick() {
                mPlayButton.callOnClick();
            }
        });

        mDrawRect.setDrawRectClickListener(new DrawRect.onDrawRectClickListener() {
            @Override
            public void onDrawRectClick(int captionIndex) {
                if (mEditMode == Constants.EDIT_MODE_CAPTION) {
                    if (mCaptionTextEditListener != null) {
                        mCaptionTextEditListener.onCaptionTextEdit();
                    }
                } else if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
                    if (mCompoundCaptionListener != null) {
                        mCompoundCaptionListener.onCaptionIndex(captionIndex);
                    }
                }
            }
        });

        mDrawRect.setStickerMuteListenser(new DrawRect.onStickerMuteListenser() {
            @Override
            public void onStickerMute() {
                if (mCurAnimateSticker == null)
                    return;
                mStickerMuteIndex = mStickerMuteIndex == 0 ? 1 : 0;
                float volumeGain = mStickerMuteIndex == 0 ? 1.0f : 0.0f;
                mCurAnimateSticker.setVolumeGain(volumeGain, volumeGain);
                setStickerMuteIndex(mStickerMuteIndex);
                if (mStickerMuteListener != null)
                    mStickerMuteListener.onStickerMute();
            }
        });
    }

    public void setEditMode(int mode) {
        mEditMode = mode;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    /*
     * 更新字幕在视图上的坐标
     * Update the coordinates of the subtitle on the view
     * */
    public void updateThemeCaptionCoordinate(NvsTimelineCaption caption) {
        if (caption != null) {
            /*
             * 获取字幕的原始包围矩形框变换后的顶点位置
             * Get the transformed vertex position of the original bounding rectangle of the caption
             * */
            List<PointF> list = caption.getBoundingRectangleVertices();
            if (list == null || list.size() < 4)
                return;

            List<PointF> newList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                PointF pointF = mLiveWindow.mapCanonicalToView(list.get(i));
                newList.add(pointF);
            }
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_THEMECAPTION);
        }
    }

    public void changeThemeCaptionRectVisible() {
        if (mEditMode == Constants.EDIT_MODE_THEMECAPTION) {
            setDrawRectVisible(isSelectedCaption() ? View.VISIBLE : View.GONE);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setAlignIndex(int index) {
        mDrawRect.setAlignIndex(index);
    }

    /*
     * 设置字幕
     * Set subtitles
     * */
    public void setCurCaption(NvsTimelineCaption caption) {
        mCurCaption = caption;
        if (null != caption) {

            Log.e("mCurCaption", "===" + mCurCaption.getBackgroundRadius());
        }
    }

    public NvsTimelineCaption getCurCaption() {
        return mCurCaption;
    }

    /*
     * 更新字幕在视图上的坐标
     * Update the coordinates of the subtitle on the view
     * */
    public void updateCaptionCoordinate(NvsTimelineCaption caption) {
        if (caption != null) {
            /*
             * 获取字幕的原始包围矩形框变换后的顶点位置
             * Get the transformed vertex position of the original bounding rectangle of the caption
             * */
            List<PointF> list = caption.getBoundingRectangleVertices();
            if (list == null || list.size() < 4)
                return;

            List<PointF> newList = getAssetViewVerticesList(list);
            mDrawRect.setHorizontal(!caption.getVerticalLayout());
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_CAPTION);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setCurCompoundCaption(NvsTimelineCompoundCaption caption) {
        mCurCompoundCaption = caption;
    }

    public NvsTimelineCompoundCaption getCurrCompoundCaption() {
        return mCurCompoundCaption;
    }

    /*
     * 更新组合字幕在视图上的坐标
     * Update the coordinates of the combined subtitles on the view
     * */
    public void updateCompoundCaptionCoordinate(NvsTimelineCompoundCaption caption) {
        if (caption != null) {
            /*
             * 获取字幕的原始包围矩形框变换后的顶点位置
             * Get the transformed vertex position of the original bounding rectangle of the caption
             * */
            List<PointF> list = caption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME);
            if (list == null || list.size() < 4) {
                return;
            }

            List<PointF> newList = getAssetViewVerticesList(list);
            List<List<PointF>> newSubCaptionList = new ArrayList<>();
            int subCaptionCount = caption.getCaptionCount();
            for (int index = 0; index < subCaptionCount; index++) {
                List<PointF> subList = caption.getCaptionBoundingVertices(index, NvsTimelineCompoundCaption.BOUNDING_TYPE_TEXT);
                if (subList == null || subList.size() < 4) {
                    continue;
                }
                List<PointF> newSubList = getAssetViewVerticesList(subList);
                newSubCaptionList.add(newSubList);
            }
            mDrawRect.setCompoundDrawRect(newList, newSubCaptionList, Constants.EDIT_MODE_COMPOUND_CAPTION);
        }
    }

    public void changeCompoundCaptionRectVisible() {
        if (mEditMode == Constants.EDIT_MODE_COMPOUND_CAPTION) {
            setDrawRectVisible(isSelectedCompoundCaption() ? View.VISIBLE : View.GONE);
        }
    }

    /*
     * 在liveWindow上手动选择字幕
     * Manually select subtitles on liveWindow
     * */
    public void selectCompoundCaptionByHandClick(PointF curPoint) {
        if (mTimeline == null) {
            return;
        }
        List<NvsTimelineCompoundCaption> captionList = mTimeline.getCompoundCaptionsByTimelinePosition(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        if (captionList.size() <= 1)
            return;

        for (int j = 0; j < captionList.size(); j++) {
            NvsTimelineCompoundCaption caption = captionList.get(j);
            List<PointF> list = caption.getCompoundBoundingVertices(NvsTimelineCompoundCaption.BOUNDING_TYPE_FRAME);
            List<PointF> newList = getAssetViewVerticesList(list);
            boolean isSelected = mDrawRect.clickPointIsInnerDrawRect(newList, (int) curPoint.x, (int) curPoint.y);
            if (isSelected) {
                mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_COMPOUND_CAPTION);
                mCurCompoundCaption = caption;
                break;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    public Point getPictureSize(String filePath) {
        Point point = mDrawRect.getPicturePoint(filePath);
        int defaultWidth = (int) getActivity().getResources().getDimension(R.dimen.edit_waterMark_width);
        int defaultHeight = (int) getActivity().getResources().getDimension(R.dimen.edit_waterMark_height);
        if (point != null) {
            defaultHeight = defaultWidth * point.y / point.x;
        }
        return new Point(defaultWidth, defaultHeight);
    }

    /**
     * 第一次添加水印
     * Add watermark for the first time
     *
     * @param w livewindow宽度；livewindow width
     * @param h livewindow高度；livewindow height
     */
    public void firstSetWaterMarkPosition(int w, int h, String filePath) {
        setPointFListLiveWindow(w, h);
        Point point = getPictureSize(filePath);
        int defaultWidth = point.x;
        int defaultHeight = point.y;
        int x0 = w - defaultWidth;
        int x1 = w;
        int y0 = 0;
        List<PointF> newList = setFourPointToList(x0, x1, y0, defaultHeight);
        setPointFListToFirstAddWaterMark(newList);

        /*
         * 初始位置需将操作框按钮都放置在画面内
         * The initial position needs to place the operation box buttons in the screen
         * */
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.delete);
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        bitmap.recycle();
        x0 = x0 - bitmapWidth / 2;
        x1 = x1 - bitmapWidth / 2;
        defaultHeight = defaultHeight + bitmapHeight / 2;
        y0 = y0 + bitmapHeight / 2;

        newList = setFourPointToList(x0, x1, y0, defaultHeight);
        mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_WATERMARK);
    }

    private List<PointF> getAssetViewVerticesList(List<PointF> verticesList) {
        List<PointF> newList = new ArrayList<>();
        for (int i = 0; i < verticesList.size(); i++) {
            PointF pointF = mLiveWindow.mapCanonicalToView(verticesList.get(i));
            newList.add(pointF);
        }
        return newList;
    }

    /**
     * 根据宽高获取livewindow的四个角坐标
     * Get the four corner coordinates of livewindow according to the width and height
     */
    private void setPointFListLiveWindow(int w, int h) {
//        int x0 = Math.abs(w - h) / 2;
        int x0 = 0;
        int x1 = w;
        int y0 = 0;
        int y1 = h;
        Logger.e(TAG, "liveWindow的四个角坐标  " + x0 + "  " + x1 + "  " + y0 + "  " + y1);
        pointFListLiveWindow = setFourPointToList(x0, x1, y0, y1);
    }

    /**
     * 四个点就能确定一个矩形 (x0,y0) (x0,y1) (x1,y1) (x1,y0)
     * Four points can determine a rectangle (x0, y0) (x0, y1) (x1, y1) (x1, y0)
     */
    private void refreshWaterMarkByFourPoint(float x0, float x1, float y0, float y1) {
        List<PointF> newList = setFourPointToList(x0, x1, y0, y1);
        if (checkInLiveWindow(newList)) {
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_WATERMARK);
            if (waterMarkChangeListener != null) {
                waterMarkChangeListener.onScaleAndRotate(newList);
            }
        }
    }

    public void setDrawRect(List<PointF> newList) {
        mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_WATERMARK);
    }

    public List<PointF> getDrawRect() {
        return mDrawRect.getDrawRect();
    }

    /**
     * 四个点坐标转化到list，从左上逆时针
     * Four point coordinates converted to list, counterclockwise from top left
     */
    private List<PointF> setFourPointToList(float x0, float x1, float y0, float y1) {
        List<PointF> newList = new ArrayList<>();
        newList.add(new PointF(x0, y0));
        newList.add(new PointF(x0, y1));
        newList.add(new PointF(x1, y1));
        newList.add(new PointF(x1, y0));
        return newList;
    }

    private boolean checkInLiveWindow(List<PointF> newList) {
        if (pointFListLiveWindow != null) {
            float minX = pointFListLiveWindow.get(0).x;
            float maxX = pointFListLiveWindow.get(2).x;
            float minY = pointFListLiveWindow.get(0).y;
            float maxY = pointFListLiveWindow.get(2).y;
            for (PointF pointF : newList) {
                if (pointF.x < minX || pointF.x > maxX || pointF.y < minY || pointF.y > maxY) {
                    Logger.e(TAG, "checkInLiveWindow " + minX + "       " + pointF.x + "      " + maxX);
                    Logger.e(TAG, "checkInLiveWindow " + minY + "       " + pointF.y + "      " + maxY);
                    return false;
                }
            }
        }
        return true;
    }

    private void updateWaterMarkPositionOnDrag(float x, float y, List<PointF> list) {
        List<PointF> newList = new ArrayList<>();
        for (PointF pointF : list) {
            newList.add(new PointF(pointF.x + x, pointF.y - y));
        }
        if (checkInLiveWindow(newList)) {
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_WATERMARK);
            if (waterMarkChangeListener != null) {
                waterMarkChangeListener.onDrag(newList);
            }
        }
    }

    /**
     * @param scaleDregree 缩放比例；scaling ratio
     * @param centerPoint  中心坐标；Center coordinates
     * @param angle        旋转角度；Rotation angle
     * @param list         方框坐标点；Box coordinates
     */
    private void updateWaterMarkPositionOnScaleAndRotate(float scaleDregree, PointF centerPoint, float angle, List<PointF> list) {
        float width = Math.abs(list.get(0).x - list.get(3).x);
        float height = Math.abs(list.get(0).y - list.get(1).y);
        float x0 = centerPoint.x - width / 2 * scaleDregree;
        float x1 = centerPoint.x + width / 2 * scaleDregree;
        float y0 = centerPoint.y - height / 2 * scaleDregree;
        float y1 = centerPoint.y + height / 2 * scaleDregree;
        refreshWaterMarkByFourPoint(x0, x1, y0, y1);
    }


    public void setDrawRectVisible(int visibility) {
        mDrawRect.setVisibility(visibility);
    }

    public void setPicturePath(String path) {
        mDrawRect.setPicturePath(path);
    }

    public void changeCaptionRectVisible() {
        if (mEditMode == Constants.EDIT_MODE_CAPTION) {
            setDrawRectVisible(isSelectedCaption() ? View.VISIBLE : View.GONE);
        }
    }

    /*
     * 在liveWindow上手动选择字幕
     * Manually select subtitles on liveWindow
     * */
    public void selectCaptionByHandClick(PointF curPoint) {
        if (mTimeline == null) {
            return;
        }
        List<NvsTimelineCaption> captionList = mTimeline.getCaptionsByTimelinePosition(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        if (captionList.size() <= 1)
            return;

        for (int j = 0; j < captionList.size(); j++) {
            NvsTimelineCaption caption = captionList.get(j);
            List<PointF> list = caption.getBoundingRectangleVertices();
            List<PointF> newList = getAssetViewVerticesList(list);
            boolean isSelected = mDrawRect.clickPointIsInnerDrawRect(newList, (int) curPoint.x, (int) curPoint.y);
            if (isSelected) {
                mDrawRect.setHorizontal(!caption.getVerticalLayout());
                mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_CAPTION);
                mCurCaption = caption;
                break;
            }
        }
    }

    public boolean curPointIsInnerDrawRect(int xPos, int yPos) {
        return mDrawRect.curPointIsInnerDrawRect(xPos, yPos);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void setAutoPlay(boolean flag) {
        mAutoPlay = flag;
    }

    public void setRecording(boolean record_state) {
        mRecording = record_state;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void setCurAnimateSticker(NvsTimelineAnimatedSticker animateSticker) {
        mCurAnimateSticker = animateSticker;
    }

    public void setStickerMuteIndex(int index) {
        mStickerMuteIndex = index;
        mDrawRect.setStickerMuteIndex(index);
    }

    public NvsTimelineAnimatedSticker getCurAnimateSticker() {
        return mCurAnimateSticker;
    }

    /*
     * 更新贴纸在视图上的坐标
     * Update the coordinates of the sticker on the view
     * */
    public void updateAnimateStickerCoordinate(NvsTimelineAnimatedSticker animateSticker) {
        if (animateSticker != null) {
            /*
             * 获取贴纸的原始包围矩形框变换后的顶点位置
             * Get the transformed vertex position of the original bounding rectangle of the sticker
             * */
            List<PointF> list = animateSticker.getBoundingRectangleVertices();
            if (list == null || list.size() < 4)
                return;
            boolean isHorizonFlip = animateSticker.getHorizontalFlip();
            if (isHorizonFlip) {
                /*
                 * 如果已水平翻转，需要对顶点数据进行处理
                 * If flipped horizontally, you need to process the vertex data
                 * */
                Collections.swap(list, 0, 3);
                Collections.swap(list, 1, 2);
            }
            List<PointF> newList = getAssetViewVerticesList(list);
            mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_STICKER);
        }
    }

    /*
     * 设置贴纸选择框显隐
     * Set the sticker selection box to be hidden
     * */
    public void changeStickerRectVisible() {
        if (mEditMode == Constants.EDIT_MODE_STICKER) {
            setDrawRectVisible(isSelectedAnimateSticker() ? View.VISIBLE : View.GONE);
        }
    }

    /*
     * 在liveWindow上手动选择贴纸
     * Manually select stickers on liveWindow
     * */
    public void selectAnimateStickerByHandClick(PointF curPoint) {
        if (mTimeline == null) {
            return;
        }
        List<NvsTimelineAnimatedSticker> stickerList = mTimeline.getAnimatedStickersByTimelinePosition(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        if (stickerList.size() <= 1)
            return;

        for (int j = 0; j < stickerList.size(); j++) {
            NvsTimelineAnimatedSticker sticker = stickerList.get(j);
            List<PointF> list = sticker.getBoundingRectangleVertices();
            List<PointF> newList = getAssetViewVerticesList(list);
            boolean isSelected = mDrawRect.clickPointIsInnerDrawRect(newList, (int) curPoint.x, (int) curPoint.y);
            if (isSelected) {
                mDrawRect.setDrawRect(newList, Constants.EDIT_MODE_STICKER);
                mCurAnimateSticker = sticker;
                break;
            }
        }
    }

    public void setMuteVisible(boolean hasAudio) {
        mDrawRect.setMuteVisible(hasAudio);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * 连接时间线跟liveWindow
     * Connect timeline with liveWindow
     * */
    public void connectTimelineWithLiveWindow() {
        if (mStreamingContext == null || mTimeline == null || mLiveWindow == null)
            return;
        mStreamingContext.setPlaybackCallback(new NvsStreamingContext.PlaybackCallback() {
            @Override
            public void onPlaybackPreloadingCompletion(NvsTimeline nvsTimeline) {

            }

            @Override
            public void onPlaybackStopped(NvsTimeline nvsTimeline) {
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.playStopped(nvsTimeline);
                }
            }

            @Override
            public void onPlaybackEOF(NvsTimeline nvsTimeline) {
                if (mPlayBarVisibleState) {
                    //m_handler.sendEmptyMessage(RESETPLATBACKSTATE);
                }
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.playBackEOF(nvsTimeline);
                }
            }
        });

        mStreamingContext.setPlaybackCallback2(new NvsStreamingContext.PlaybackCallback2() {
            @Override
            public void onPlaybackTimelinePosition(NvsTimeline nvsTimeline, long cur_position) {
                if (mPlayBarVisibleState) {
                    updateCurPlayTime(cur_position);
                }
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.playbackTimelinePosition(nvsTimeline, cur_position);
                }
                /*
                 *  播放进度条消失
                 * The playback progress bar disappears
                 * */
                if (mPlayBarVisibleState) {
                    if (mPlayStartFlag != -1) {
                        if (cur_position - mPlayStartFlag >= 4000000)
                            mPlayBarLayout.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        mStreamingContext.setStreamingEngineCallback(new NvsStreamingContext.StreamingEngineCallback() {
            @Override
            public void onStreamingEngineStateChanged(int i) {
                if (i == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    mPlayImage.setBackgroundResource(R.mipmap.icon_edit_pause);
                    startHidePlayBarTimer(false);
                } else {
                    mPlayImage.setBackgroundResource(R.mipmap.icon_edit_play);
                    mPlayBarLayout.setVisibility(mPlayBarVisibleState ? View.VISIBLE : View.GONE);
                    startHidePlayBarTimer(true);
                }
                if (mVideoFragmentCallBack != null) {
                    mVideoFragmentCallBack.streamingEngineStateChanged(i);
                }
            }

            @Override
            public void onFirstVideoFramePresented(NvsTimeline nvsTimeline) {

            }
        });

        mStreamingContext.connectTimelineWithLiveWindow(mTimeline, mLiveWindow);
    }

    private boolean isSelectedCompoundCaption() {
        long curPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        if (mCurCompoundCaption != null
                && curPosition >= mCurCompoundCaption.getInPoint()
                && curPosition <= mCurCompoundCaption.getOutPoint()) {
            return true;
        }
        return false;
    }

    private boolean isSelectedCaption() {
        long curPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        if (mCurCaption != null
                && curPosition >= mCurCaption.getInPoint()
                && curPosition <= mCurCaption.getOutPoint()) {
            return true;
        }
        return false;
    }

    private boolean isSelectedAnimateSticker() {
        long curPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        if (mCurAnimateSticker != null
                && curPosition >= mCurAnimateSticker.getInPoint()
                && curPosition <= mCurAnimateSticker.getOutPoint()) {
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopEngine();
    }

    @Override
    public void onResume() {
        super.onResume();
        connectTimelineWithLiveWindow();
        long stamp = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        updateCurPlayTime(stamp);
        Log.e(TAG, "onResume");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAutoPlay && mPlayImage != null) {
                    playVideoButtonCilck();
                }
            }
        }, 100);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDrawRect.cleanUp();
        Log.e(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        mVideoFragmentCallBack = null;
        m_handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged: " + hidden);
    }

    /*
     * 播放视频
     * Play video
     * */
    public void playVideo(long startTime, long endTime) {
        mStreamingContext.playbackTimeline(mTimeline, startTime, endTime, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, true, 0);
    }

    /*
     * 预览
     * Seek
     * */
    public void seekTimeline(long timestamp, int seekShowMode) {
        mStreamingContext.seekTimeline(mTimeline, timestamp, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, seekShowMode);
    }

    /*
     * 获取当前引擎状态
     * Get the current engine status
     * */
    public int getCurrentEngineState() {
        return mStreamingContext.getStreamingEngineState();
    }

    /*
     * 停止引擎
     * Stop the engine
     * */
    public void stopEngine() {
        if (mStreamingContext != null) {
            mStreamingContext.stop();
        }
    }

    public void playVideoButtonCilck() {
        if (mTimeline == null) {
            return;
        }
        long endTime = mTimeline.getDuration();
        playVideoButtonCilck(0, endTime);
    }

    public void playVideoButtonCilck(long inPoint, long outPoint) {
        playVideo(inPoint, outPoint);
        /*
         * 更新播放进度条显示标识
         * Update the playback progress bar indicator
         * */
        if (mPlayBarVisibleState) {
            mPlayStartFlag = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            mPlayBarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setLiveWindowRatio(int ratio, int titleHeight, int bottomHeight) {
        ViewGroup.LayoutParams layoutParams = mPlayerLayout.getLayoutParams();
        int statusHeight = ScreenUtils.getStatusBarHeight(getActivity());
        int screenWidth = ScreenUtils.getScreenWidth(getActivity());
        int screenHeight = ScreenUtils.getScreenHeight(getActivity());
        int newHeight = screenHeight - titleHeight - bottomHeight - statusHeight;
        switch (ratio) {
            case NvAsset.AspectRatio_16v9: // 16:9
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 9.0 / 16);
                break;
            case NvAsset.AspectRatio_1v1: //1:1
                layoutParams.width = screenWidth;
                layoutParams.height = screenWidth;
                if (newHeight < screenWidth) {
                    layoutParams.width = newHeight;
                    layoutParams.height = newHeight;
                }
                break;
            case NvAsset.AspectRatio_9v16: //9:16
                layoutParams.width = (int) (newHeight * 9.0 / 16);
                layoutParams.height = newHeight;
                break;
            case NvAsset.AspectRatio_3v4: // 3:4
                layoutParams.width = (int) (newHeight * 3.0 / 4);
                layoutParams.height = newHeight;
                break;
            case NvAsset.AspectRatio_4v3: //4:3
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 3.0 / 4);
                break;
            case NvAsset.AspectRatio_21v9: //21:9
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 9.0 / 21);
                break;
            case NvAsset.AspectRatio_9v21: //9:21
                layoutParams.width = (int) (newHeight * 9.0 / 21);
                layoutParams.height = newHeight;
                break;
            case NvAsset.AspectRatio_18v9: //18:9
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (newHeight * 9.0 / 18);
                break;
            case NvAsset.AspectRatio_9v18: //9:18
                layoutParams.width = (int) (newHeight * 9.0 / 18);
                layoutParams.height = newHeight;
                break;
            default: // 16:9
                layoutParams.width = screenWidth;
                layoutParams.height = (int) (screenWidth * 9.0 / 16);
                break;
        }
        mPlayerLayout.setLayoutParams(layoutParams);
        mLiveWindow.setFillMode(NvsLiveWindow.FILLMODE_PRESERVEASPECTFIT);
    }

    //formate time
    private String formatTimeStrWithUs(long us) {
        int second = (int) (us / 1000000.0);
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        return hh > 0 ? String.format("%02d:%02d:%02d", hh, mm, ss) : String.format("%02d:%02d", mm, ss);
    }

    private void controllerOperation() {
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
                    stopEngine();
                    /*
                     * 更新播放进度条显示标识
                     * Update the playback progress bar indicator
                     * */
                    if (mPlayBarVisibleState) {
                        mPlayStartFlag = -1;
                    }
                } else {
                    if (mTimeline == null) {
                        return;
                    }
                    //播放进度，如果是动画的页面，播放的可能是片段或者是整体，指定了播放的范围
                    long startTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                    long endTime = mTimeline.getDuration();
                    if(mIsAnimationView){
                        endTime = mPlayEndPoint;
                        if(startTime < mPlayStartPoint){
                            startTime = mPlayStartPoint;
                        }
                    }
                    playVideo(startTime, endTime);
                    /*
                     * 更新播放进度条显示标识
                     * Update the playback progress bar indicator
                     * */
                    if (mPlayBarVisibleState) {
                        mPlayStartFlag = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                    }
                }
            }
        });

        mPlaySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long currentTimeStamp = progress * BASE_VALUE;
                   /* if(mIsAnimationView){
                        currentTimeStamp = currentTimeStamp+mPlayStartPoint;
                    }*/
                    seekTimeline(currentTimeStamp, 0);
                    updateCurPlayTime(progress * BASE_VALUE);
                    if (mThemeCaptionSeekListener != null) {
                        mThemeCaptionSeekListener.onThemeCaptionSeek(progress * BASE_VALUE);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mShowSeekbar = true;
                startHidePlayBarTimer(false);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mShowSeekbar = false;
                startHidePlayBarTimer(true);
            }
        });
        mVoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoVolumeListener != null) {
                    mVideoVolumeListener.onVideoVolume();
                }
            }
        });

        mLiveWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLiveWindowClickListener != null) {
                    mLiveWindowClickListener.onLiveWindowClick();
                }
                /*
                 * 如果正在录音，禁止操作
                 * Prohibit operation if recording
                 * */
                if (mRecording) {
                    return;
                }

                /*
                 * 播放进度条显示
                 * Play progress bar display
                 * */
                if (mPlayBarVisibleState) {
                    if (mPlayBarLayout.getVisibility() == View.INVISIBLE) {
                        mPlayStartFlag = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                        mPlayBarLayout.setVisibility(View.VISIBLE);
                        startHidePlayBarTimer(true);
                        return;
                    }
                }
                mPlayButton.callOnClick();
            }
        });
    }

    public NvsLiveWindow getLiveWindow() {
        return mLiveWindow;
    }

    public List<PointF> getPointFListToFirstAddWaterMark() {
        if (pointFListToFirstAddWaterMark == null) {
            return new ArrayList<>();
        }
        return pointFListToFirstAddWaterMark;
    }

    public void setPointFListToFirstAddWaterMark(List<PointF> pointFListToFirstAddWaterMark) {
        this.pointFListToFirstAddWaterMark = pointFListToFirstAddWaterMark;
    }

    public void setClipImageViewBitmap(Bitmap bitmap, boolean reset) {
        if (bitmap == null) {
            return;
        }
        if (reset) {
            mClipImageView.setVisibility(View.VISIBLE);
        }
        mClipImageView.changeImageBitmap(bitmap, reset);
    }

    public void resetClipImageView() {
        mClipImageView.resetClipImageView();
    }

    public Bitmap getCoverImageBitmap() {
        return mClipImageView.clip();
    }

    public interface IBeforeAnimateStickerEditListener {
        boolean beforeTransitionCouldDo();

        boolean beforeScaleCouldDo();
    }

    public void setBeforeAnimateStickerEditListener(IBeforeAnimateStickerEditListener beforeAnimateStickerEditListener) {
        this.mBeforeAnimateStickerEditListener = beforeAnimateStickerEditListener;
    }

    public void setPlaySeekVisiable(boolean visiable) {
        if (visiable) {
            mPlayBarLayout.setVisibility(View.VISIBLE);
        } else {
            mPlayBarLayout.setVisibility(View.INVISIBLE);
        }
        mPlayBarVisibleState = visiable;
    }

    public void startHidePlayBarTimer(boolean start) {
        if (mPlayBarVisibleState) {
            m_hidePlayBarTimer.cancel();
            if (start) {
                m_hidePlayBarTimer.start();
            }
        }
    }

    /**
     * 设置播放进度调的最大值和当前的进度 (动画页面使用)
     * @param playStartPoint 当前播放的片段的起点,相对timeline来讲
     * @param currentPoint 当前进度
     * @param duration 总长度
     */
    public void setmPlaySeekBarMaxAndCurrent(long playStartPoint,long mPlayEndPoint,long currentPoint,long duration){
        this.mPlayStartPoint = playStartPoint;
        this.mPlayEndPoint = mPlayEndPoint;
        mPlaySeekBar.setProgress((int) (currentPoint/BASE_VALUE));
        mPlaySeekBar.setMax((int) (duration/BASE_VALUE));
        mTotalDuration.setText(formatTimeStrWithUs(duration));
    }
}
