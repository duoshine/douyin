package com.duoshine.douyin.meishe.sdkdemo.edit.Caption;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.base.BaseActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.VideoFragment;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.BackupData;
import com.duoshine.douyin.meishe.sdkdemo.edit.timelineEditor.NvsTimelineEditor;
import com.duoshine.douyin.meishe.sdkdemo.edit.timelineEditor.NvsTimelineTimeSpan;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.InputDialog;
import com.duoshine.douyin.meishe.sdkdemo.utils.AppManager;
import com.duoshine.douyin.meishe.sdkdemo.utils.Constants;
import com.duoshine.douyin.meishe.sdkdemo.utils.Logger;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;
import com.duoshine.douyin.meishe.sdkdemo.utils.TimeFormatUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.TimelineUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.ToastUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.Util;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.KeyFrameInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meicam.sdk.NvsMultiThumbnailSequenceView;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.duoshine.douyin.meishe.sdkdemo.utils.Constants.*;


public class CaptionActivity extends BaseActivity {
    private static final String TAG = "CaptionActivity";
    private static final int VIDEOPLAYTOEOF = 105;
    private static final int REQUESTCAPTIONSTYLE = 103;
    private CustomTitleBar mTitleBar;
    private TextView mPlayCurTime;
    private ImageView mIvZoomIn, mIvZoomOut;
    private TextView mTvKeyFrame;
    private Button mCaptionStyleButton;
    private NvsTimelineEditor mTimelineEditor;
    private Button mPlayBtn;
    private LinearLayout mLlKeyFrame;
    private TextView mTvLastFrame, mTvAddDeleteFrame, mTvNextFrame;
    private LinearLayout mLlAddTraditional;
    private LinearLayout mLlAddPieced;
    private Button mOkBtn;
    private VideoFragment mVideoFragment;
    private RelativeLayout mBottomRelativeLayout;
    private RelativeLayout mPlayBtnLayout;
    private NvsMultiThumbnailSequenceView mMultiSequenceView;

    private NvsTimeline mTimeline;
    private boolean mIsSeekTimeline = true;
    private boolean mIsPlaying = false;
    private NvsTimelineCaption mCurCaption;
    private NvsStreamingContext mStreamingContext;
    private List<CaptionTimeSpanInfo> mTimeSpanInfoList = new ArrayList<>();
    private CaptionActivity.CaptionHandler m_handler = new CaptionActivity.CaptionHandler(this);
    private ArrayList<CaptionInfo> mCaptionDataListClone;
    private boolean mIsInnerDrawRect = false;
    private StringBuilder mShowCurrentDuration = new StringBuilder();
    private boolean isTraditionCaption;
    private boolean mAddKeyFrame;

    static class CaptionHandler extends Handler {
        WeakReference<CaptionActivity> mWeakReference;

        public CaptionHandler(CaptionActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final CaptionActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case VIDEOPLAYTOEOF:
                        activity.resetView();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void resetView() {
        updatePlaytimeText(0);
        seekTimeline(0);
        mMultiSequenceView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
    }

    private void seekTimeline(long timeStamp) {
        mVideoFragment.seekTimeline(timeStamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
    }

    private void selectCaptionAndTimeSpan() {
        selectCaption();
        mVideoFragment.setCurCaption(mCurCaption);
        mVideoFragment.updateCaptionCoordinate(mCurCaption);
        mVideoFragment.changeCaptionRectVisible();

        if (mCurCaption != null) {
            int alignVal = mCurCaption.getTextAlignment();
            mVideoFragment.setAlignIndex(alignVal);
        }

        if (mCurCaption != null) {
            selectTimeSpan();
        } else {
            mTimelineEditor.unSelectAllTimeSpan();
        }
    }


    private void handlePlayStop() {
        selectCaption();

        if (mCurCaption != null) {
            int alignVal = mCurCaption.getTextAlignment();
            mVideoFragment.setAlignIndex(alignVal);
        }

        changeRectVisible();

        if (mCurCaption != null) {
            selectTimeSpan();
        } else {
            mTimelineEditor.unSelectAllTimeSpan();
        }
    }

    @Override
    protected int initRootView() {
        return R.layout.activity_caption;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mPlayCurTime = (TextView) findViewById(R.id.play_cur_time);
        mIvZoomIn = findViewById(R.id.iv_zoom_in);
        mIvZoomOut = findViewById(R.id.iv_zoom_out);
        mTvKeyFrame = findViewById(R.id.tv_key_frame);
        mCaptionStyleButton = (Button) findViewById(R.id.captionStyleButton);
        mTimelineEditor = (NvsTimelineEditor) findViewById(R.id.caption_timeline_editor);
        mPlayBtn = (Button) findViewById(R.id.play_btn);
        mLlKeyFrame = findViewById(R.id.ll_key_frame);
        mTvLastFrame = findViewById(R.id.tv_last_frame);
        mTvAddDeleteFrame = findViewById(R.id.tv_add_or_delete_frame);
        mTvNextFrame = findViewById(R.id.tv_next_frame);
        mLlAddTraditional = findViewById(R.id.ll_traditional_caption);
        mLlAddPieced = findViewById(R.id.ll_pieced_caption);
        mOkBtn = (Button) findViewById(R.id.ok_btn);
        mBottomRelativeLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mPlayBtnLayout = (RelativeLayout) findViewById(R.id.play_btn_layout);
        mMultiSequenceView = mTimelineEditor.getMultiThumbnailSequenceView();
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.caption);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        mStreamingContext = NvsStreamingContext.getInstance();
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null) {
            return;
        }
        mCaptionDataListClone = TimelineData.instance().cloneCaptionData();
        initVideoFragment();
        updatePlaytimeText(0);
        initMultiSequence();
        addAllTimeSpan();
        selectCaption();
        selectTimeSpan();
    }

    @Override
    protected void initListener() {
        mIvZoomIn.setOnClickListener(this);
        mIvZoomOut.setOnClickListener(this);
        mTvKeyFrame.setOnClickListener(this);
        mCaptionStyleButton.setOnClickListener(this);
        mTvLastFrame.setOnClickListener(this);
        mTvAddDeleteFrame.setOnClickListener(this);
        mTvNextFrame.setOnClickListener(this);
        mLlAddTraditional.setOnClickListener(this);
        mLlAddPieced.setOnClickListener(this);
        mOkBtn.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);

        mTimelineEditor.setOnScrollListener(new NvsTimelineEditor.OnScrollChangeListener() {
            @Override
            public void onScrollX(long timeStamp) {
                if (timeStamp < 0) {
                    return;
                }
                CaptionInfo captionInfo = null;
                Map<Long, KeyFrameInfo> keyFrameMap = null;
                if (mCurCaption != null) {
                    captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
                    if (captionInfo != null) {
                        keyFrameMap = captionInfo.getKeyFrameInfo();
                        if (!mIsPlaying) {
                            updateKeyFrameView(timeStamp, keyFrameMap);
                        }
                        mVideoFragment.setDrawRectVisible(View.GONE);
                    }
                    if (mLlKeyFrame.getVisibility() == View.VISIBLE) {
                        //如果是关键帧模式,不允许平移缩放
                        if (mIsSeekTimeline) {
                            mVideoFragment.setDrawRectVisible(View.VISIBLE);
                        } else {
                            mVideoFragment.setDrawRectVisible(View.INVISIBLE);
                        }
                    }
                }
                if (!mIsSeekTimeline) {
                    return;
                }
                if (mCurCaption != null && (timeStamp > mCurCaption.getOutPoint() || timeStamp < mCurCaption.getInPoint())) {
                    // 当前字幕时间之外隐藏框 禁用所有关键帧按钮
                    mVideoFragment.setDrawRectVisible(View.GONE);
                    displayEnable(false);
                } else if (mCurCaption != null) {
                    // seek到关键帧的位置的时候，选中一下当前的关键帧，使以后所有的操作，在当前帧下操作.
                    boolean hasKeyFrame = false;
                    long currentKeyFrameStamp = -1;
                    if (keyFrameMap != null) {
                        for (Map.Entry<Long, KeyFrameInfo> entry : keyFrameMap.entrySet()) {
                            if (timeStamp >= entry.getKey() - 100000 && timeStamp <= entry.getKey() + 100000) {
                                hasKeyFrame = true;
                                currentKeyFrameStamp = entry.getKey();
                                break;
                            }
                        }
                    }
                    if (hasKeyFrame) {
                        mCurCaption.setCurrentKeyFrameTime(currentKeyFrameStamp - mCurCaption.getInPoint());
                        mVideoFragment.setDrawRectVisible(View.VISIBLE);
                        updateCaptionBoundingRect();
                    } else {
                        long duration = timeStamp - mCurCaption.getInPoint();
                        mCurCaption.setCurrentKeyFrameTime(duration);
                        updateCaptionBoundingRect();

                        mCurCaption.removeKeyframeAtTime(TRANS_X, duration);
                        mCurCaption.removeKeyframeAtTime(TRANS_Y, duration);
                        mCurCaption.removeKeyframeAtTime(SCALE_X, duration);
                        mCurCaption.removeKeyframeAtTime(SCALE_Y, duration);
                        mCurCaption.removeKeyframeAtTime(ROTATION_Z, duration);
                    }
                    updateCaptionBoundingRect();
                    mTvAddDeleteFrame.setEnabled(true);
                    mVideoFragment.setDrawRectVisible(View.VISIBLE);
                }
                if (mTimeline != null) {
                    updatePlaytimeText(timeStamp);
                    selectCaptionAndTimeSpan();//setOnScrollListener
                    seekTimeline(timeStamp);
                }
            }
        });


        mMultiSequenceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mIsSeekTimeline = true;
                return false;
            }
        });

        if (mVideoFragment != null) {
            mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
                @Override
                public void playBackEOF(NvsTimeline timeline) {
                    m_handler.sendEmptyMessage(VIDEOPLAYTOEOF);
                }

                @Override
                public void playStopped(NvsTimeline timeline) {
                    handlePlayStop();
                }

                @Override
                public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                    updatePlaytimeText(stamp);
                    mVideoFragment.setDrawRectVisible(View.GONE);
                    mTimelineEditor.unSelectAllTimeSpan();
                    selectCaption();
                    if (mMultiSequenceView != null) {
                        int x = Math.round((stamp / (float) mTimeline.getDuration() * mTimelineEditor.getSequenceWidth()));
                        mMultiSequenceView.smoothScrollTo(x, 0);
                    }
                }

                @Override
                public void streamingEngineStateChanged(int state) {
                    if (NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK == state) {
                        mIsSeekTimeline = false;
                        mIsPlaying = true;
                        mPlayBtn.setBackgroundResource(R.mipmap.icon_edit_pause);
                        displayEnable(false);
                    } else {
                        mPlayBtn.setBackgroundResource(R.mipmap.icon_edit_play);
                        mIsSeekTimeline = true;
                        mIsPlaying = false;
                        if (mCurCaption != null) {
                            displayEnable(true);
                            CaptionInfo captionInfo = getCurrentCaptionInfo();
                            if (captionInfo != null) {
                                Map<Long, KeyFrameInfo> keyFrameInfo = captionInfo.getKeyFrameInfo();
                                updateKeyFrameView(mStreamingContext.getTimelineCurrentPosition(mTimeline), keyFrameInfo);
                            }
                        } else {
                            displayEnable(false);
                        }
                    }
                }
            });
            mVideoFragment.setAssetEditListener(new VideoFragment.AssetEditListener() {
                @Override
                public void onAssetDelete() {
                    if (mAddKeyFrame) {
                        ToastUtil.showToastCenter(getApplicationContext(), getResources().getString(R.string.tips_when_delete_caption));
                        return;
                    }

                    deleteCurCaptionTimeSpan();
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.remove(index);
                    }
                    mTimeline.removeCaption(mCurCaption);
                    mCurCaption = null;
                    selectCaptionAndTimeSpan();//onAssetDelete
                    seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                }

                @Override
                public void onAssetSelected(PointF curPoint) {
                    /*
                     * 判断若没有选中当前字幕框则选中，选中则不处理
                     * Judge if the current subtitle box is not selected, select it, do not process
                     * */
                    mIsInnerDrawRect = mVideoFragment.curPointIsInnerDrawRect((int) curPoint.x, (int) curPoint.y);
                    if (!mIsInnerDrawRect) {
                        mVideoFragment.selectCaptionByHandClick(curPoint);
                        mCurCaption = mVideoFragment.getCurCaption();
                        selectTimeSpan();//onAssetSelected
                        if (mCurCaption != null) {
                            int alignVal = mCurCaption.getTextAlignment();
                            mVideoFragment.setAlignIndex(alignVal);
                        }
                    }
                }

                @Override
                public void onAssetTranstion() {
                    if (mCurCaption == null) {
                        return;
                    }
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setTranslation(mCurCaption.getCaptionTranslation());
                    }
                    if (mAddKeyFrame) {
                        updateOrAddKeyFrameInfo();
                    }
                }

                @Override
                public void onAssetScale() {
                    if (mCurCaption == null) {
                        return;
                    }
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setUsedScaleRotationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setScaleFactorX(mCurCaption.getScaleX());
                        mCaptionDataListClone.get(index).setScaleFactorY(mCurCaption.getScaleY());
                        mCaptionDataListClone.get(index).setAnchor(mCurCaption.getAnchorPoint());
                        mCaptionDataListClone.get(index).setRotation(mCurCaption.getRotationZ());
//                        mCaptionDataListClone.get(index).setCaptionSize(mCurCaption.getFontSize());
                        PointF pointF = mCurCaption.getCaptionTranslation();
                        //Log.e(TAG,"pointF.x = " + pointF.x + "pointF.y =" + pointF.y);
                        mCaptionDataListClone.get(index).setTranslation(pointF);
                    }
                    if (mAddKeyFrame) {
                        updateOrAddKeyFrameInfo();
                    }
                }

                @Override
                public void onAssetAlign(int alignVal) {
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setAlignVal(alignVal);
                    }
                }

                @Override
                public void onOrientationChange(boolean isHorizontal) {
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setOrientationType(isHorizontal ? CaptionInfo.O_HORIZONTAL : CaptionInfo.O_VERTICAL);
                    }
                }

                @Override
                public void onAssetHorizFlip(boolean isHorizFlip) {

                }
            });
            mVideoFragment.setCaptionTextEditListener(new VideoFragment.VideoCaptionTextEditListener() {
                @Override
                public void onCaptionTextEdit() {
                    if (!mIsInnerDrawRect) {
                        return;
                    }
                    InputDialog inputDialog = new InputDialog(CaptionActivity.this, R.style.dialog, new InputDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean ok) {
                            if (ok) {
                                InputDialog d = (InputDialog) dialog;
                                String userInputText = d.getUserInputText();
                                mCurCaption.setText(userInputText);
                                seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                                mVideoFragment.updateCaptionCoordinate(mCurCaption);
                                mVideoFragment.changeCaptionRectVisible();
                                int zVal = (int) mCurCaption.getZValue();
                                int index = getCaptionIndex(zVal);
                                if (index >= 0) {
                                    mCaptionDataListClone.get(index).setText(userInputText);
                                }

                            }
                        }
                    });
                    if (mCurCaption != null) {
                        inputDialog.setUserInputText(mCurCaption.getText());
                    }
                    inputDialog.show();
                    mIsInnerDrawRect = false;
                }
            });
        }


        mVideoFragment.setBeforeAnimateStickerEditListener(new VideoFragment.IBeforeAnimateStickerEditListener() {
            @Override
            public boolean beforeTransitionCouldDo() {
                if (mCurCaption == null) {
                    return false;
                }
                if (!mAddKeyFrame) {
                    boolean b = ifCouldEditCaption();
                    if (!b) {
                        return false;
                    } else {
                        return true;
                    }
                }
                return true;
            }

            @Override
            public boolean beforeScaleCouldDo() {
                if (mCurCaption == null) {
                    return false;
                }
                if (!mAddKeyFrame) {
                    boolean b = ifCouldEditCaption();
                    if (!b) {
                        return false;
                    } else {
                        return true;
                    }
                }
                return true;
            }
        });

    }

    private void displayEnable(boolean enable) {
        mTvAddDeleteFrame.setEnabled(enable);
        mTvLastFrame.setEnabled(enable);
        mTvNextFrame.setEnabled(enable);
    }

    private boolean ifCouldEditCaption() {
        CaptionInfo captionInfo = getCurrentCaptionInfo();
        if (captionInfo != null) {
            Map<Long, KeyFrameInfo> keyFrameInfoHashMap = captionInfo.getKeyFrameInfo();
            if (!keyFrameInfoHashMap.isEmpty()) {
                // give tips
                ToastUtil.showToastCenter(getApplicationContext(), getResources().getString(R.string.tips_when_move_caption));
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    private void updateCaptionBoundingRect() {
        mVideoFragment.setCurCaption(mCurCaption);
        mVideoFragment.updateCaptionCoordinate(mCurCaption);
        if (mCurCaption == null) {
            mVideoFragment.setDrawRectVisible(View.GONE);
        } else {
            mVideoFragment.changeStickerRectVisible();
        }
    }


    private void updateKeyFrameView(long timeStamp, Map<Long, KeyFrameInfo> keyFrameMap) {
        if (keyFrameMap == null || keyFrameMap.isEmpty()) {
            mTvLastFrame.setEnabled(false);
            mTvNextFrame.setEnabled(false);
            changeAddOrDeleteView(true);
        } else {
            Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameMap.entrySet();
            Set<Long> keyFrameKeySet = keyFrameMap.keySet();
            Object[] objects = keyFrameKeySet.toArray();
            //上一帧
            long beforeKeyFrame = -1;
            for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                Long key = entry.getKey();
                if (key < timeStamp) {
                    // 找到距离当前位置 向前最近的一个时间点
                    beforeKeyFrame = key;
                }
            }
            if (beforeKeyFrame == -1 || ((objects != null) && ((long) (objects[0]) == timeStamp))) {
                mTvLastFrame.setEnabled(false);
            } else {
                mTvLastFrame.setEnabled(true);
            }

            // 下一帧
            long nextKeyFrame = -1;
            for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                Long key = entry.getKey();
                if (key > timeStamp) {
                    // 找到距离当前位置 向后最近的一个时间点
                    nextKeyFrame = key;
                    break;
                }
            }

            if (nextKeyFrame == -1 || ((objects != null) && ((long) (objects[objects.length - 1]) == timeStamp))) {
                mTvNextFrame.setEnabled(false);
            } else {
                mTvNextFrame.setEnabled(true);
            }

            // 增加或者删除
            boolean hasKeyFrame = false;
            for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                if (timeStamp >= entry.getKey() - 100000 && timeStamp <= entry.getKey() + 100000) {
                    hasKeyFrame = true;
                    break;
                }
            }
            changeAddOrDeleteView(!hasKeyFrame);
            // keyFramePoint
            CaptionTimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
            if (spanInfo != null && spanInfo.mTimeSpan != null) {
                if (mAddKeyFrame) {
                    spanInfo.mTimeSpan.setCurrentTimelinePosition(timeStamp, keyFrameMap);
                } else {
                    spanInfo.mTimeSpan.setCurrentTimelinePosition(timeStamp, null);
                }
            }
        }
    }


    private void updateOrAddKeyFrameInfo() {
        if (mCurCaption == null) {
            return;
        }
        int zValue = (int) mCurCaption.getZValue();
        int captionIndex = getCaptionIndex(zValue);
        CaptionInfo captionInfo = null;
        if (captionIndex >= 0) {
            captionInfo = mCaptionDataListClone.get(captionIndex);
        }

        if (captionInfo == null) {
            return;
        }
        Map<Long, KeyFrameInfo> keyFrameInfo = captionInfo.getKeyFrameInfo();
        if (keyFrameInfo.isEmpty()) {
            mAddKeyFrame = true;
            addFrame();
            return;
        }
        long timelineCurrentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        boolean hasKeyFrame = false;
        long currentKeyFrameStamp = -1;
        Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfo.entrySet();

        for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
            if (timelineCurrentPosition > (entry.getKey() - 100000) && timelineCurrentPosition < (entry.getKey() + 100000)) {
                hasKeyFrame = true;
                currentKeyFrameStamp = entry.getKey();
                break;
            }
        }

        if (hasKeyFrame) {
            captionInfo.putKeyFrameInfo(currentKeyFrameStamp, generateKeyFrameInfo(mCurCaption));
        } else {
            mAddKeyFrame = true;
            addFrame();
        }

    }


    private KeyFrameInfo generateKeyFrameInfo(NvsTimelineCaption caption) {
        return new KeyFrameInfo().setScaleX(caption.getScaleX())
                .setScaleY(caption.getScaleY())
                .setRotationZ(caption.getRotationZ())
                .setTranslation(caption.getCaptionTranslation());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.zoom_in_btn:
                mIsSeekTimeline = false;
                mTimelineEditor.ZoomInSequence();
                break;

            case R.id.zoom_out_btn:
                mIsSeekTimeline = false;
                mTimelineEditor.ZoomOutSequence();
                break;

            case R.id.captionStyleButton:
                mCaptionStyleButton.setClickable(false);
                CaptionInfo currentCaptionInfo1 = getCurrentCaptionInfo();
                if (currentCaptionInfo1 != null) {
                    currentCaptionInfo1.setTranslation(mCurCaption.getCaptionTranslation());
                    currentCaptionInfo1.setScaleFactorX(mCurCaption.getScaleX());
                    currentCaptionInfo1.setScaleFactorY(mCurCaption.getScaleY());
                    currentCaptionInfo1.setRotation(mCurCaption.getRotationZ());
                }
                TimelineData.instance().setCaptionData(mCaptionDataListClone);
                BackupData.instance().setCaptionData(mCaptionDataListClone);
                BackupData.instance().setCaptionZVal((int) mCurCaption.getZValue());
                BackupData.instance().setCurSeekTimelinePos(mCurCaption.getInPoint());
                Bundle bundle = new Bundle();
                bundle.putBoolean("tradition_caption", isTraditionCaption);
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), CaptionStyleActivity.class, bundle, REQUESTCAPTIONSTYLE);
                break;
            case R.id.ll_traditional_caption:
                new InputDialog(this, R.style.dialog, new InputDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean ok) {
                        if (ok) {
                            InputDialog d = (InputDialog) dialog;
                            String userInputText = d.getUserInputText();
                            addCaption(userInputText, true);
                        }
                    }
                }).show();
                break;

            case R.id.ll_pieced_caption:
                new InputDialog(this, R.style.dialog, new InputDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean ok) {
                        if (ok) {
                            InputDialog d = (InputDialog) dialog;
                            String userInputText = d.getUserInputText();
                            addCaption(userInputText, false);
                        }
                    }
                }).show();

                break;

            case R.id.ok_btn:
                if (mLlKeyFrame.getVisibility() == View.VISIBLE) {
                    //如果是关键帧模式
                    displayKeyFrameLayout(false);
                    CaptionInfo currentCaptionInfo = getCurrentCaptionInfo();
                    if (currentCaptionInfo == null) {
                        displayKeyFrame(true, false);
                    } else {
                        Map<Long, KeyFrameInfo> keyFrameInfo = currentCaptionInfo.getKeyFrameInfo();
                        if (keyFrameInfo.isEmpty()) {
                            displayKeyFrame(true, false);
                        } else {
                            displayKeyFrame(true, true);
                        }
                    }
                    CaptionTimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
                    if (spanInfo != null && spanInfo.mTimeSpan != null) {
                        spanInfo.mTimeSpan.setKeyFrameInfo(null);
                    }
                    mAddKeyFrame = false;
                    mCaptionStyleButton.setVisibility(View.VISIBLE);
                } else {
                    mStreamingContext.stop();
                    removeTimeline();
                    TimelineData.instance().setCaptionData(mCaptionDataListClone);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    AppManager.getInstance().finishActivity();
                }
                break;
            case R.id.play_btn:
                playVideo();
                break;
            case R.id.tv_key_frame:
                displayKeyFrameLayout(true);
                displayKeyFrame(true, false);
                mCaptionStyleButton.setVisibility(View.GONE);
                CaptionTimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
                CaptionInfo currentCaptionInfo = getCurrentCaptionInfo();
                if (spanInfo != null && spanInfo.mTimeSpan != null && currentCaptionInfo != null) {
                    spanInfo.mTimeSpan.setKeyFrameInfo(currentCaptionInfo.getKeyFrameInfo());
                }
                break;
            case R.id.tv_last_frame:
                toOneKeyFrame(false);
                break;
            case R.id.tv_add_or_delete_frame:
                CharSequence text = mTvAddDeleteFrame.getText();
                if (text == null) {
                    return;
                }
                if (text.toString().equals(getString(R.string.key_frame_add_frame_text))) {
                    addFrame();
                } else {
                    deleteFrame();
                }
                break;
            case R.id.tv_next_frame:
                toOneKeyFrame(true);
                break;
            default:
                break;
        }
    }

    private CaptionInfo getCurrentCaptionInfo() {
        if (mCurCaption == null) {
            return null;
        }
        int zValue = (int) mCurCaption.getZValue();
        int captionIndex = getCaptionIndex(zValue);
        CaptionInfo captionInfo = mCaptionDataListClone.get(captionIndex);
        return captionInfo;
    }

    /**
     * 展示关键帧的显示布局
     *
     * @param display boolean true显示关键帧布局，进入关键帧编辑模式，false不显示
     */
    private void displayKeyFrameLayout(boolean display) {
        if (display) {
            mLlKeyFrame.setVisibility(View.VISIBLE);
            mLlAddTraditional.setVisibility(View.INVISIBLE);
            mLlAddPieced.setVisibility(View.INVISIBLE);
            mAddKeyFrame = true;
        } else {
            mLlKeyFrame.setVisibility(View.INVISIBLE);
            mLlAddTraditional.setVisibility(View.VISIBLE);
            mLlAddPieced.setVisibility(View.VISIBLE);
            mAddKeyFrame = false;
        }
    }

    /**
     * 展示关键帧的显示
     *
     * @param visible boolean true 显示关键帧文字以及按钮 false 不显示
     * @param isEdit  boolean true 显示编辑 false 不显示
     */
    private void displayKeyFrame(boolean visible, boolean isEdit) {
        if (visible && mTvKeyFrame.getVisibility() != View.VISIBLE) {
            mTvKeyFrame.setVisibility(View.VISIBLE);
        } else if (!visible && mTvKeyFrame.getVisibility() == View.VISIBLE) {
            mTvKeyFrame.setVisibility(View.INVISIBLE);
        }
        if (mAddKeyFrame) {
            mTvKeyFrame.setVisibility(View.INVISIBLE);
        }
        if (isEdit && !getString(R.string.key_frame_edit_text).equals(mTvKeyFrame.getText().toString())) {
            mTvKeyFrame.setText(getString(R.string.key_frame_edit_text));
            mTvKeyFrame.setTextColor(getResources().getColor(R.color.color_yellow_a5));
            mTvKeyFrame.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.mipmap.key_frame_edit), null, null, null);
        } else if (!isEdit && !getString(R.string.key_frame_text).equals(mTvKeyFrame.getText().toString())) {
            mTvKeyFrame.setText(getString(R.string.key_frame_text));
            mTvKeyFrame.setTextColor(getResources().getColor(R.color.white));
            mTvKeyFrame.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(this, R.mipmap.key_frame), null, null, null);
        }
    }

    /**
     * 处理某一帧
     *
     * @param next boolean true下一帧，false上一帧.
     */
    private void toOneKeyFrame(boolean next) {
        if (mCurCaption == null) {
            return;
        }
        mVideoFragment.stopEngine();
        CaptionInfo captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
        if (captionInfo != null) {
            Map<Long, KeyFrameInfo> keyFrameMap = captionInfo.getKeyFrameInfo();
            long currentTimelinePosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            long nextKeyFrame = -1;
            for (Map.Entry<Long, KeyFrameInfo> entry : keyFrameMap.entrySet()) {
                Long key = entry.getKey();
                if (next && key > currentTimelinePosition) {
                    nextKeyFrame = key;
                    break;
                } else if (!next && key < currentTimelinePosition) {
                    nextKeyFrame = key;
                }
            }
            if (nextKeyFrame == -1) {
                if (next) {
                    mTvNextFrame.setEnabled(false);
                } else {
                    mTvLastFrame.setEnabled(false);
                }

            } else {
                if (next) {
                    mTvNextFrame.setEnabled(true);
                } else {
                    mTvLastFrame.setEnabled(true);
                }
                seekTimeline(nextKeyFrame);
                seekMultiThumbnailSequenceView();
            }
        }

    }


    /**
     * 添加关键帧
     */
    private void addFrame() {
        if (mCurCaption == null) {
            return;
        }
        mVideoFragment.stopEngine();
        CaptionInfo captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
        mVideoFragment.stopEngine();
        long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurCaption.getInPoint();
        mCurCaption.setCurrentKeyFrameTime(duration);
        mCurCaption.setCaptionTranslation(mCurCaption.getCaptionTranslation());
        mCurCaption.setScaleX(mCurCaption.getScaleX());
        mCurCaption.setScaleY(mCurCaption.getScaleY());
        mCurCaption.setRotationZ(mCurCaption.getRotationZ());

        if (captionInfo != null) {
            KeyFrameInfo keyFrameInfo = new KeyFrameInfo()
                    .setScaleX(mCurCaption.getScaleX())
                    .setScaleY(mCurCaption.getScaleY())
                    .setRotationZ(mCurCaption.getRotationZ())
                    .setTranslation(mCurCaption.getCaptionTranslation());
            captionInfo.putKeyFrameInfo(mStreamingContext.getTimelineCurrentPosition(mTimeline), keyFrameInfo);
        }


        int alignVal = mCurCaption.getTextAlignment();
        mVideoFragment.setAlignIndex(alignVal);
        updateCaptionBoundingRect();

        // 显示出操作框来
        mIsSeekTimeline = true;
        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
        seekMultiThumbnailSequenceView();

        CaptionTimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
        if (captionInfo != null && spanInfo != null && spanInfo.mTimeSpan != null) {
            spanInfo.mTimeSpan.setKeyFrameInfo(captionInfo.getKeyFrameInfo());
        }
        changeAddOrDeleteView(false);
    }


    /**
     * 删除关键帧
     */
    private void deleteFrame() {
        if (mCurCaption == null) {
            return;
        }
        mVideoFragment.stopEngine();
        CaptionInfo captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
        if (captionInfo != null) {
            Map<Long, KeyFrameInfo> keyFrameMap = captionInfo.getKeyFrameInfo();
            long timelineCurrentPosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            for (Long aLong : keyFrameMap.keySet()) {
                if (timelineCurrentPosition >= aLong - 100000 && timelineCurrentPosition <= aLong + 100000) {
                    keyFrameMap.get(aLong);
                    keyFrameMap.remove(aLong);
                    mCurCaption.removeKeyframeAtTime(TRANS_X, aLong - mCurCaption.getInPoint());
                    mCurCaption.removeKeyframeAtTime(TRANS_Y, aLong - mCurCaption.getInPoint());
                    mCurCaption.removeKeyframeAtTime(SCALE_X, aLong - mCurCaption.getInPoint());
                    mCurCaption.removeKeyframeAtTime(SCALE_Y, aLong - mCurCaption.getInPoint());
                    mCurCaption.removeKeyframeAtTime(ROTATION_Z, aLong - mCurCaption.getInPoint());
                    break;
                }
            }
        }
        CaptionTimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
        if (captionInfo != null && spanInfo != null && spanInfo.mTimeSpan != null) {
            spanInfo.mTimeSpan.setKeyFrameInfo(captionInfo.getKeyFrameInfo());
        }
        changeAddOrDeleteView(true);
    }

    /**
     * 改变关键帧模式中添加或者删除关键帧的显示
     */
    private void changeAddOrDeleteView(boolean add) {
        if (add) {
            mTvAddDeleteFrame.setText(R.string.key_frame_add_frame_text);
            mTvAddDeleteFrame.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.key_frame_add_frame_selector), null, null);
        } else {
            mTvAddDeleteFrame.setText(R.string.key_frame_delete_frame_text);
            mTvAddDeleteFrame.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.key_frame_delete_frame_selector), null, null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case REQUESTCAPTIONSTYLE:
                mCaptionDataListClone = BackupData.instance().getCaptionData();
                mCurCaption = null;
                TimelineUtil.setCaption(mTimeline, mCaptionDataListClone);
                mTimelineEditor.deleteAllTimeSpan();
                mTimeSpanInfoList.clear();
                addAllTimeSpan();
                long curSeekPos = BackupData.instance().getCurSeekTimelinePos();
                seekTimeline(curSeekPos);
                seekMultiThumbnailSequenceView();
                boolean isSelectCurCaption = data.getBooleanExtra("isSelectCurCaption", true);
                if (!isSelectCurCaption) {
                    selectCaptionAndTimeSpan();//REQUESTCAPTIONSTYLE
                } else {
                    int curZVal = BackupData.instance().getCaptionZVal();
                    selectCaptionByZVal(curZVal);

                    int captionIndex = getCaptionIndex((int) mCurCaption.getZValue());
                    reloadKeyFrame(captionIndex);

                    long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurCaption.getInPoint();
                    mCurCaption.setCurrentKeyFrameTime(duration);

                    mVideoFragment.setCurCaption(mCurCaption);
                    mVideoFragment.updateCaptionCoordinate(mCurCaption);
                    mVideoFragment.changeCaptionRectVisible();

                    mCurCaption.removeKeyframeAtTime(TRANS_X, duration);
                    mCurCaption.removeKeyframeAtTime(TRANS_Y, duration);
                    mCurCaption.removeKeyframeAtTime(SCALE_X, duration);
                    mCurCaption.removeKeyframeAtTime(SCALE_Y, duration);
                    mCurCaption.removeKeyframeAtTime(ROTATION_Z, duration);


                    if (mCurCaption != null) {
                        int alignVal = mCurCaption.getTextAlignment();
                        mVideoFragment.setAlignIndex(alignVal);
                    }
                    selectTimeSpan();//REQUESTCAPTIONSTYLE
                }
                break;
            default:
                break;
        }
    }

    private void reloadKeyFrame(int index) {
        Map<Long, KeyFrameInfo> keyFrameInfoMap = mCaptionDataListClone.get(index).getKeyFrameInfo();
        Set<Long> keySet = keyFrameInfoMap.keySet();
        for (long currentTime : keySet) {
            KeyFrameInfo keyFrameInfo = keyFrameInfoMap.get(currentTime);
            long duration = currentTime - mCurCaption.getInPoint();

            mCurCaption.removeKeyframeAtTime(TRANS_X, duration);
            mCurCaption.removeKeyframeAtTime(TRANS_Y, duration);
            mCurCaption.removeKeyframeAtTime(SCALE_X, duration);
            mCurCaption.removeKeyframeAtTime(SCALE_Y, duration);
            mCurCaption.removeKeyframeAtTime(ROTATION_Z, duration);

            mCurCaption.setCurrentKeyFrameTime(duration);
            mCurCaption.setScaleX(keyFrameInfo.getScaleX());
            mCurCaption.setScaleY(keyFrameInfo.getScaleY());
            mCurCaption.setCaptionTranslation(keyFrameInfo.getTranslation());
            mCurCaption.setRotationZ(keyFrameInfo.getRotationZ());

        }
    }

    private void selectCaptionByZVal(int curZVal) {
        if (mTimeline != null) {
            long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            List<NvsTimelineCaption> captionList = mTimeline.getCaptionsByTimelinePosition(curPos);
            int captionCount = captionList.size();
            if (captionCount > 0) {
                for (int i = 0; i < captionCount; i++) {
                    int zVal = (int) captionList.get(i).getZValue();
                    if (curZVal == zVal) {
                        mCurCaption = captionList.get(i);
                        break;
                    }
                }
                if (mCurCaption != null) {
                    mCaptionStyleButton.setVisibility(View.VISIBLE);
                    CaptionInfo captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
                    if (captionInfo != null) {
                        Map<Long, KeyFrameInfo> keyFrameInfo = captionInfo.getKeyFrameInfo();
                        displayKeyFrame(true, !keyFrameInfo.isEmpty());
                    }
                }
            } else {
                mCurCaption = null;
                mCaptionStyleButton.setVisibility(View.GONE);
                displayKeyFrame(false, false);
            }
        }
    }

    private void playVideo() {
        if (mVideoFragment.getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK) {
            long startTime = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            long endTime = mTimeline.getDuration();
            mVideoFragment.playVideo(startTime, endTime);
        } else {
            mVideoFragment.stopEngine();
        }
    }


    private void updatePlaytimeText(long playTime) {
        if (mTimeline != null) {
            long totalDuaration = mTimeline.getDuration();
            String strTotalDuration = TimeFormatUtil.formatUsToString1(totalDuaration);
            String strCurrentDuration = TimeFormatUtil.formatUsToString1(playTime);
            mShowCurrentDuration.setLength(0);
            mShowCurrentDuration.append(strCurrentDuration);
            mShowCurrentDuration.append("/");
            mShowCurrentDuration.append(strTotalDuration);
            mPlayCurTime.setText(mShowCurrentDuration.toString());
        }
    }

    private float getCurCaptionZVal() {
        float zVal = 0.0f;
        NvsTimelineCaption caption = mTimeline.getFirstCaption();
        while (caption != null) {
            float tmpZVal = caption.getZValue();
            if (tmpZVal > zVal) {
                zVal = tmpZVal;
            }
            caption = mTimeline.getNextCaption(caption);
        }
        zVal += 1.0;
        return zVal;
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mVideoFragment.setCurCaption(mCurCaption);
                mOkBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVideoFragment.updateCaptionCoordinate(mCurCaption);
                        mVideoFragment.changeCaptionRectVisible();
                        seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline));
                    }
                }, 100);
            }
        });
        /*
         * 设置字幕模式
         * Set caption mode
         * */
        mVideoFragment.setEditMode(Constants.EDIT_MODE_CAPTION);
        mVideoFragment.setTimeline(mTimeline);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomRelativeLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", false);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.video_layout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
    }


    private void initMultiSequence() {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return;
        }
        int clipCount = videoTrack.getClipCount();
        ArrayList<NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc> sequenceDescsArray = new ArrayList<>();
        for (int index = 0; index < clipCount; ++index) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(index);
            if (videoClip == null) {
                continue;
            }
            NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc sequenceDescs = new NvsMultiThumbnailSequenceView.ThumbnailSequenceDesc();
            sequenceDescs.mediaFilePath = videoClip.getFilePath();
            sequenceDescs.trimIn = videoClip.getTrimIn();
            sequenceDescs.trimOut = videoClip.getTrimOut();
            sequenceDescs.inPoint = videoClip.getInPoint();
            sequenceDescs.outPoint = videoClip.getOutPoint();
            sequenceDescs.stillImageHint = false;
            sequenceDescsArray.add(sequenceDescs);
        }

        long duration = mTimeline.getDuration();
        int halfScreenWidth = ScreenUtils.getScreenWidth(this) / 2;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPlayBtnLayout.getLayoutParams();
        int playBtnTotalWidth = layoutParams.width + layoutParams.leftMargin + layoutParams.rightMargin;
        int sequenceLeftPadding = halfScreenWidth - playBtnTotalWidth;
        mTimelineEditor.setSequencLeftPadding(sequenceLeftPadding);
        mTimelineEditor.setSequencRightPadding(halfScreenWidth);
        mTimelineEditor.setTimeSpanLeftPadding(sequenceLeftPadding);
        mTimelineEditor.initTimelineEditor(sequenceDescsArray, duration);
    }

    /*
     * 添加字幕
     * Add captions
     * */
    private void addCaption(String caption, boolean traditional) {
        long inPoint = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        long captionDuration = 4 * Constants.NS_TIME_BASE;
        long outPoint = inPoint + captionDuration;
        long duration = mTimeline.getDuration();

        if (outPoint > duration) {
            captionDuration = duration - inPoint;
            if (captionDuration <= Constants.NS_TIME_BASE) {
                captionDuration = Constants.NS_TIME_BASE;
                inPoint = duration - captionDuration;
                if (duration <= Constants.NS_TIME_BASE) {
                    captionDuration = duration;
                    inPoint = 0;
                }
            }
            outPoint = duration;
        }
        if (traditional) {
            mCurCaption = mTimeline.addCaption(caption, inPoint, captionDuration, null);
        } else {
            mCurCaption = mTimeline.addModularCaption(caption, inPoint, captionDuration);
        }
        if (mCurCaption == null) {
            Log.e(TAG, "addCaption: " + " 添加字幕失败！");
            return;
        }
        float zVal = getCurCaptionZVal();
        mCurCaption.setZValue(zVal);
        NvsTimelineTimeSpan timeSpan = addTimeSpan(inPoint, outPoint);
        if (timeSpan == null) {
            Log.e(TAG, "addCaption: " + " 添加TimeSpan失败!");
            return;
        }
        mCaptionStyleButton.setVisibility(View.VISIBLE);
        displayKeyFrame(true, false);
        CaptionTimeSpanInfo info = new CaptionTimeSpanInfo(mCurCaption, timeSpan);
        info.setTraditional(traditional);
        if (!traditional) {
            timeSpan.getTimeSpanshadowView().setBackgroundColor(getResources().getColor(R.color.red_4fea));
        }
        mTimeSpanInfoList.add(info);
        mVideoFragment.setCurCaption(mCurCaption);
        mVideoFragment.updateCaptionCoordinate(mCurCaption);
        int alignVal = mCurCaption.getTextAlignment();
        mVideoFragment.setAlignIndex(alignVal);
        mVideoFragment.changeCaptionRectVisible();
        seekTimeline(inPoint);
        /*
         * 选择timeSpan
         * Select timeSpan
         * */
        selectTimeSpan();//addCaption
        CaptionInfo captionInfo = Util.saveCaptionData(mCurCaption);
        if (captionInfo != null) {
            captionInfo.setTraditionCaption(traditional);
            mCaptionDataListClone.add(captionInfo);
        }
    }

    private NvsTimelineTimeSpan addTimeSpan(long inPoint, long outPoint) {
        /*
         * warning: 使用addTimeSpanExt()之前必须设置setTimeSpanType()
         * warning: setTimeSpanType () must be set before using addTimeSpanExt ()
         * */
        mTimelineEditor.setTimeSpanType("NvsTimelineTimeSpan");
        NvsTimelineTimeSpan timelineTimeSpan = mTimelineEditor.addTimeSpan(inPoint, outPoint);
        if (timelineTimeSpan == null) {
            Log.e(TAG, "addTimeSpan: " + " 添加TimeSpan失败!");
            return null;
        }
        timelineTimeSpan.setOnChangeListener(new NvsTimelineTimeSpan.OnTrimInChangeListener() {
            @Override
            public void onTrimInChange(long timeStamp, boolean isDragEnd) {

                seekTimeline(timeStamp);
                updatePlaytimeText(timeStamp);
                mVideoFragment.changeCaptionRectVisible();

                NvsTimelineTimeSpan currentTimeSpan = getCurrentTimeSpan();
                CaptionInfo currentCaptionInfo = getCurrentCaptionInfo();
                if (currentCaptionInfo != null && mAddKeyFrame) {
                    currentTimeSpan.setKeyFrameInfo(currentCaptionInfo.getKeyFrameInfo());
                }

                if (isDragEnd && mCurCaption != null) {
                    Logger.e(TAG, "TrimInChange1212->" + timeStamp);
                    mCurCaption.changeInPoint(timeStamp);
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setInPoint(timeStamp);
                    }
                    seekMultiThumbnailSequenceView();

                    // 移动左边缘 松手之后 1.更新上层数据结构中记录的关键帧信息  2.更新底层关键帧位置信息
                    if (mCurCaption != null && currentCaptionInfo != null) {
                        // 1.step one
                        Map<Long, KeyFrameInfo> keyFrameInfoHashMap = currentCaptionInfo.getKeyFrameInfo();
                        Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfoHashMap.entrySet();
                        long currentTimeLinePosition = mStreamingContext.getTimelineCurrentPosition(mTimeline);
                        Iterator<Map.Entry<Long, KeyFrameInfo>> iterator = entries.iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Long, KeyFrameInfo> next = iterator.next();
                            if (next.getKey() < currentTimeLinePosition) {
                                iterator.remove();
                            }
                        }
                        if (mAddKeyFrame) {
                            currentTimeSpan.setKeyFrameInfo(currentCaptionInfo.getKeyFrameInfo());
                        }
                        // 2.step two 底层移除之前添加的关键帧信息 根据上层数据结构重新添加关键帧
                        boolean removeStickerTransXSuccess = mCurCaption.removeAllKeyframe(TRANS_X);
                        boolean removeStickerTransYSuccess = mCurCaption.removeAllKeyframe(TRANS_Y);
                        boolean removeStickerScaleX = mCurCaption.removeAllKeyframe(SCALE_X);
                        boolean removeStickerScaleY = mCurCaption.removeAllKeyframe(SCALE_Y);
                        boolean removeStickerRotZ = mCurCaption.removeAllKeyframe(ROTATION_Z);
                        if (removeStickerTransXSuccess && removeStickerTransYSuccess && removeStickerScaleX && removeStickerScaleY && removeStickerRotZ) {
                            Log.d(TAG, "timelineTimeSpan.setOnChangeListener onChangeLeft  removeAllKeyframe success");
                        }
                        Map<Long, KeyFrameInfo> keyFrameInfoHashMapAfter = currentCaptionInfo.getKeyFrameInfo();
                        Set<Map.Entry<Long, KeyFrameInfo>> entriesAfter = keyFrameInfoHashMapAfter.entrySet();
                        for (Map.Entry<Long, KeyFrameInfo> longCaptionKeyFrameInfoEntry : entriesAfter) {
                            KeyFrameInfo captionKeyFrameInfo = longCaptionKeyFrameInfoEntry.getValue();
                            mCurCaption.setCurrentKeyFrameTime(longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(TRANS_X, captionKeyFrameInfo.getTranslation().x, longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(TRANS_Y, captionKeyFrameInfo.getTranslation().y, longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(SCALE_X, captionKeyFrameInfo.getScaleX(), longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(SCALE_Y, captionKeyFrameInfo.getScaleX(), longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(ROTATION_Z, captionKeyFrameInfo.getRotationZ(), longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                        }
                    }

                }
                changeRectVisible();
            }
        });
        timelineTimeSpan.setOnChangeListener(new NvsTimelineTimeSpan.OnTrimOutChangeListener() {
            @Override
            public void onTrimOutChange(long timeStamp, boolean isDragEnd) {
                /*
                 * outPoint是开区间，seekTimeline时，需要往前平移一帧即0.04秒，转换成微秒即40000微秒
                 * outPoint is an open interval. In seekTimeline, you need to pan one frame, that is, 0.04 seconds, and convert it to microseconds, that is, 40,000 microseconds.
                 * */
                seekTimeline(timeStamp - 40000);
                updatePlaytimeText(timeStamp);
                mVideoFragment.changeCaptionRectVisible();
                if (isDragEnd && mCurCaption != null) {
                    Logger.e(TAG, "TrimInChange5454->" + timeStamp);
                    mCurCaption.changeOutPoint(timeStamp);
                    int zVal = (int) mCurCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setOutPoint(timeStamp);
                    }
                    seekMultiThumbnailSequenceView();

                    // 若覆盖了关键帧 则移除覆盖的关键帧信息
                    CaptionInfo captionInfo = getCurrentCaptionInfo();
                    if (captionInfo != null) {
                        Map<Long, KeyFrameInfo> keyFrameInfoHashMap = captionInfo.getKeyFrameInfo();
                        Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfoHashMap.entrySet();
                        Iterator<Map.Entry<Long, KeyFrameInfo>> iterator = entries.iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Long, KeyFrameInfo> next = iterator.next();
                            // 这里比较的是timeline上的时间
                            if (next.getKey() > mStreamingContext.getTimelineCurrentPosition(mTimeline)) {
                                iterator.remove();
                            }
                        }

                        // 2.step two 底层移除之前添加的关键帧信息 根据上层数据结构重新添加关键帧
                        boolean removeStickerTransXSuccess = mCurCaption.removeAllKeyframe(TRANS_X);
                        boolean removeStickerTransYSuccess = mCurCaption.removeAllKeyframe(TRANS_Y);
                        boolean removeStickerScaleX = mCurCaption.removeAllKeyframe(SCALE_X);
                        boolean removeStickerScaleY = mCurCaption.removeAllKeyframe(SCALE_Y);
                        boolean removeStickerRotZ = mCurCaption.removeAllKeyframe(ROTATION_Z);
                        if (removeStickerTransXSuccess && removeStickerTransYSuccess && removeStickerScaleX && removeStickerScaleY && removeStickerRotZ) {
                            Log.d(TAG, "timelineTimeSpan.setOnChangeListener onChangeLeft  removeAllKeyframe success");
                        }
                        Map<Long, KeyFrameInfo> keyFrameInfoHashMapAfter = captionInfo.getKeyFrameInfo();
                        Set<Map.Entry<Long, KeyFrameInfo>> entriesAfter = keyFrameInfoHashMapAfter.entrySet();
                        for (Map.Entry<Long, KeyFrameInfo> longCaptionKeyFrameInfoEntry : entriesAfter) {
                            KeyFrameInfo captionKeyFrameInfo = longCaptionKeyFrameInfoEntry.getValue();
                            mCurCaption.setCurrentKeyFrameTime(longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(TRANS_X, captionKeyFrameInfo.getTranslation().x, longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(TRANS_Y, captionKeyFrameInfo.getTranslation().y, longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(SCALE_X, captionKeyFrameInfo.getScaleX(), longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(SCALE_Y, captionKeyFrameInfo.getScaleX(), longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                            mCurCaption.setFloatValAtTime(ROTATION_Z, captionKeyFrameInfo.getRotationZ(), longCaptionKeyFrameInfoEntry.getKey() - mCurCaption.getInPoint());
                        }
                    }
                }

                changeRectVisible();
            }
        });

        return timelineTimeSpan;
    }

    private void changeRectVisible() {
        if (mCurCaption != null) {
            long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurCaption.getInPoint();
            mCurCaption.setCurrentKeyFrameTime(duration);

            mVideoFragment.setCurCaption(mCurCaption);
            mVideoFragment.updateCaptionCoordinate(mCurCaption);
            mVideoFragment.changeCaptionRectVisible();

            mCurCaption.removeKeyframeAtTime(TRANS_X, duration);
            mCurCaption.removeKeyframeAtTime(TRANS_Y, duration);
            mCurCaption.removeKeyframeAtTime(SCALE_X, duration);
            mCurCaption.removeKeyframeAtTime(SCALE_Y, duration);
            mCurCaption.removeKeyframeAtTime(ROTATION_Z, duration);
        } else {
            mVideoFragment.setCurCaption(mCurCaption);
            mVideoFragment.updateCaptionCoordinate(mCurCaption);
            mVideoFragment.changeCaptionRectVisible();
        }
    }


    private NvsTimelineTimeSpan getCurrentTimeSpan() {
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            if (mTimeSpanInfoList.get(i).mCaption == mCurCaption) {
                return mTimeSpanInfoList.get(i).mTimeSpan;
            }
        }
        return null;
    }


    private void seekMultiThumbnailSequenceView() {
        if (mMultiSequenceView != null) {
            long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            long duration = mTimeline.getDuration();
            mMultiSequenceView.scrollTo(Math.round(((float) curPos) / (float) duration * mTimelineEditor.getSequenceWidth()), 0);
        }
    }

    private void addAllTimeSpan() {
        NvsTimelineCaption caption = mTimeline.getFirstCaption();
        while (caption != null) {
            int capCategory = caption.getCategory();
            int roleTheme = caption.getRoleInTheme();
            Logger.e(TAG, "capCategoryCp = " + capCategory + "**isModular=" + caption.isModular());
            /*
             * capCategory值为0是默认字幕即未使用字幕样式的字幕，
             * 值为1表示是用户自定义种类即使用字幕样式的字幕，值为2是主题字幕
             * A capCategory value of 0 is the default caption, that is, a caption with no subtitle style.
             * A value of 1 is a user-defined category, that is, a caption with subtitle style.
             * A value of 2 is the theme caption
             * */
            if (capCategory == NvsTimelineCaption.THEME_CATEGORY
                    && roleTheme != NvsTimelineCaption.ROLE_IN_THEME_GENERAL) {//主题字幕不作编辑处理
                caption = mTimeline.getNextCaption(caption);
                continue;
            }
            long inPoint = caption.getInPoint();
            long outPoint = caption.getOutPoint();
            NvsTimelineTimeSpan timeSpan = addTimeSpan(inPoint, outPoint);

            if (timeSpan != null) {
                CaptionTimeSpanInfo timeSpanInfo = new CaptionTimeSpanInfo(caption, timeSpan);
                timeSpanInfo.setTraditional(!caption.isModular());
                if (!timeSpanInfo.isTraditional) {
                    timeSpan.getTimeSpanshadowView().setBackgroundColor(getResources().getColor(R.color.red_4fea));
                }
                mTimeSpanInfoList.add(timeSpanInfo);
            }
            caption = mTimeline.getNextCaption(caption);
        }
    }

    private void selectCaption() {
        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        List<NvsTimelineCaption> captionList = mTimeline.getCaptionsByTimelinePosition(curPos);
        //Logger.e(TAG, "captionList => " + captionList.size());
        int captionCount = captionList.size();
        if (captionCount > 0) {
            float zVal = captionList.get(0).getZValue();
            int index = 0;
            for (int i = 0; i < captionCount; i++) {
                float tmpZVal = captionList.get(i).getZValue();
                if (tmpZVal >= zVal) {
                    index = i;
                    break;
                }
            }
            CaptionTimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
            if (spanInfo != null && spanInfo.mTimeSpan != null) {
                spanInfo.mTimeSpan.setKeyFrameInfo(null);
            }

            mCurCaption = captionList.get(index);

            if (mAddKeyFrame) {
                CaptionInfo currentCaptionInfo = getCurrentCaptionInfo();
                spanInfo = getCurrentTimeSpanInfo();
                if (spanInfo != null && spanInfo.mTimeSpan != null) {
                    spanInfo.mTimeSpan.setKeyFrameInfo(currentCaptionInfo.getKeyFrameInfo());
                }
            }

            if (mCurCaption.getCategory() == NvsTimelineCaption.THEME_CATEGORY
                    && mCurCaption.getRoleInTheme() != NvsTimelineCaption.ROLE_IN_THEME_GENERAL) {
                mCurCaption = null;
                mCaptionStyleButton.setVisibility(View.GONE);
                displayKeyFrame(false, false);
            } else {
                boolean isEdit = false;
                if (mLlKeyFrame.getVisibility() != View.VISIBLE) {
                    //如果没有进入编辑关键帧
                    CaptionInfo captionInfo = getCaptionInfo((int) mCurCaption.getZValue());
                    if (captionInfo != null) {
                        Map<Long, KeyFrameInfo> keyFrameInfo = captionInfo.getKeyFrameInfo();
                        if (keyFrameInfo != null && !keyFrameInfo.isEmpty()) {
                            //只要有关键帧就显示编辑关键帧
                            isEdit = true;
                        }
                    }
                }
                if (!mAddKeyFrame) {
                    mCaptionStyleButton.setVisibility(View.VISIBLE);
                }
                displayKeyFrame(true, isEdit);
            }
        } else {
            CaptionTimeSpanInfo spanInfo = getCurrentTimeSpanInfo();
            if (spanInfo != null && spanInfo.mTimeSpan != null) {
                spanInfo.mTimeSpan.setKeyFrameInfo(null);
            }
            mCurCaption = null;
            mCaptionStyleButton.setVisibility(View.GONE);
            displayKeyFrame(false, false);
        }
    }

    private void selectTimeSpan() {
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            CaptionTimeSpanInfo captionTimeSpanInfo = mTimeSpanInfoList.get(i);
            if (mCurCaption != null && captionTimeSpanInfo != null) {
                if (captionTimeSpanInfo.mCaption == mCurCaption) {
                    NvsTimelineTimeSpan timeSpan = mTimeSpanInfoList.get(i).mTimeSpan;
                    if (timeSpan != null) {
                        mTimelineEditor.selectTimeSpan(timeSpan);
                    }
                    isTraditionCaption = captionTimeSpanInfo.isTraditional;
                    break;
                }

            }
        }
    }

    private CaptionTimeSpanInfo getCurrentTimeSpanInfo() {
        CaptionTimeSpanInfo captionTimeSpanInfo;
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            captionTimeSpanInfo = mTimeSpanInfoList.get(i);
            if (mCurCaption != null && captionTimeSpanInfo != null) {
                if (captionTimeSpanInfo.mCaption == mCurCaption) {
                    return captionTimeSpanInfo;
                }
            }
        }
        return null;
    }

    private void deleteCurCaptionTimeSpan() {
        for (int i = 0; i < mTimeSpanInfoList.size(); i++) {
            if (mTimeSpanInfoList.get(i).mCaption == mCurCaption) {
                mTimelineEditor.deleteSelectedTimeSpan(mTimeSpanInfoList.get(i).mTimeSpan);
                mTimeSpanInfoList.remove(i);
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        mStreamingContext.stop();
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
        m_handler.removeCallbacksAndMessages(null);
    }

    private class CaptionTimeSpanInfo {
        private boolean isTraditional = true;
        public NvsTimelineCaption mCaption;
        public NvsTimelineTimeSpan mTimeSpan;

        public CaptionTimeSpanInfo(NvsTimelineCaption caption, NvsTimelineTimeSpan timeSpan) {
            this.mCaption = caption;
            this.mTimeSpan = timeSpan;
        }

        public boolean isTraditionanl() {
            return isTraditional;
        }

        public void setTraditional(boolean traditional) {
            isTraditional = traditional;
        }
    }

    private int getCaptionIndex(int curZValue) {
        int index = -1;
        int count = mCaptionDataListClone.size();
        for (int i = 0; i < count; ++i) {
            int zVal = mCaptionDataListClone.get(i).getCaptionZVal();
            if (curZValue == zVal) {
                index = i;
                break;
            }
        }
        return index;
    }

    private CaptionInfo getCaptionInfo(int curZValue) {
        int count = mCaptionDataListClone.size();
        for (int i = 0; i < count; ++i) {
            CaptionInfo captionInfo = mCaptionDataListClone.get(i);
            if (captionInfo != null && captionInfo.getCaptionZVal() == curZValue) {
                return captionInfo;
            }
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCaptionStyleButton.setClickable(true);
    }
}
