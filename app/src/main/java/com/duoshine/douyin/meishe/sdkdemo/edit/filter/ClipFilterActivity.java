package com.duoshine.douyin.meishe.sdkdemo.edit.filter;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.BackupData;
import com.duoshine.douyin.meishe.sdkdemo.utils.Constants;
import com.duoshine.douyin.meishe.sdkdemo.utils.Logger;
import com.duoshine.douyin.meishe.sdkdemo.utils.TimelineUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.VideoClipFxInfo;
import com.meicam.sdk.NvsFx;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author tell
 * @date 2019/9/28
 */

public class ClipFilterActivity extends BaseFilterActivity {
    private static final String TAG = "ClipFilterActivity";

    private static final String FILTER_INTENSITY = "Filter Intensity";
    private ClipInfo mClipInfo;
    private ArrayList<ClipInfo> mClipArrayList;
    private int mClipIndex;
    private RelativeLayout mAddKeyFrameBtn;
    private RelativeLayout mFilterPanel;
    private RelativeLayout mFilterKeyFramePanel;
    private FilterKeyFrameView mFilterKeyFrameView;
    private ImageView mKeyFrameFinishView;
    private ImageView mAddKeyFrameTitleView;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_filter_clip;
    }

    @Override
    protected VideoClipFxInfo initClipFxInfo() {
        VideoClipFxInfo videoClipFxData = mClipInfo.getVideoClipFxInfo();
        if (videoClipFxData == null) {
            videoClipFxData = new VideoClipFxInfo();
        }
        return videoClipFxData;
    }

    @Override
    protected void initSubViews() {
        super.initSubViews();
        mAddKeyFrameBtn = findViewById(R.id.add_keyframe_title_view);
        mAddKeyFrameBtn.setOnClickListener(this);
        //
        mFilterPanel = findViewById(R.id.filter_panel_rv);
        mFilterKeyFramePanel = findViewById(R.id.filter_key_frame_panel_rv);
        mFilterKeyFrameView = findViewById(R.id.filter_key_frame_view);
        mKeyFrameFinishView = findViewById(R.id.filter_key_frame_finish_view);
        mKeyFrameFinishView.setOnClickListener(this);
        mAddKeyFrameTitleView = findViewById(R.id.inline_add_keyframe_text);
    }

    private void changeAddKeyFrameTitleViewIcon(boolean hasKeyFrame) {
        if (hasKeyFrame) {
            mAddKeyFrameTitleView.setImageResource(R.mipmap.eidt_key_frame_title_icon);
        } else {
            mAddKeyFrameTitleView.setImageResource(R.mipmap.add_key_frame_title_icon);
        }
    }

    @Override
    protected NvsTimeline initTimeLine() {
        mClipArrayList = BackupData.instance().cloneClipInfoData();
        mClipIndex = BackupData.instance().getClipIndex();
        Logger.e(TAG, "initTimeLine ->  mClipIndex = " + mClipIndex);
        mClipInfo = mClipArrayList.get(mClipIndex);
        NvsTimeline timeline = TimelineUtil.createSingleClipTimeline(mClipInfo, true);
        TimelineUtil.buildSingleClipFilter(timeline, mClipInfo, mClipInfo.getVideoClipFxInfo());
        return timeline;
    }

    //
    @Override
    protected void afterIntentInit() {
        super.afterIntentInit();
        initKeyFrameView(mTimeline);
        if (mSelectedPos == 0) {
            mAddKeyFrameBtn.setVisibility(View.INVISIBLE);
        } else {
            mAddKeyFrameBtn.setVisibility(View.VISIBLE);
        }
        if ((mVideoClipFxInfo != null) && (mVideoClipFxInfo.getKeyFrameInfoMap() != null) && (mVideoClipFxInfo.getKeyFrameInfoMap().size() > 0)) {
            mFilterView.setIntensityLayoutVisible(View.INVISIBLE);
            changeAddKeyFrameTitleViewIcon(true);
        } else {
            changeAddKeyFrameTitleViewIcon(false);
        }
    }

    @Override
    protected void playbackTimelinePositionFromParent(NvsTimeline timeline, long stamp) {
        super.playbackTimelinePositionFromParent(timeline, stamp);
        mFilterKeyFrameView.scrollSequenceViewTo(stamp);
    }

    @Override
    protected void streamingEngineStateChangedFromParent(int state) {
        super.streamingEngineStateChangedFromParent(state);
        if (state != NvsStreamingContext.STREAMING_ENGINE_STATE_SEEKING) {
            mFilterKeyFrameView.setSequenceViewIsSeekingStatus(false);
        }
    }

    private void initKeyFrameView(NvsTimeline timeline) {
        // TODO: 2020/6/16 获取关键帧位置集合
        mFilterKeyFrameView.initKeyFrameView(mClipInfo.getFilePath(), timeline, mVideoClipFxInfo.getKeyFrameInfoMap());
        // 点击事件操作
        mFilterKeyFrameView.setOnKeyFrameViewClickListener(new FilterKeyFrameView.OnKeyFrameViewClickListener() {
            @Override
            public void addFrameClick() {
                mStreamingContext.stop();
                mVideoClipFxInfo.putKeyFrameInfo(mStreamingContext.getTimelineCurrentPosition(mTimeline), getFxIntensityAndApplyKeyFrame());
                mFilterKeyFrameView.updateKeyFramePointView(mVideoClipFxInfo.getKeyFrameInfoMap());
            }

            @Override
            public void nextFrameClick() {
                NvsFx fxFromClip = getFxFromClip();
                if (fxFromClip != null) {
                    long nextKeyFrame = fxFromClip.findKeyframeTime(FILTER_INTENSITY, mStreamingContext.getTimelineCurrentPosition(mTimeline),
                            NvsFx.KEY_FRAME_FIND_MODE_INPUT_TIME_AFTER);
                    if (nextKeyFrame == -1) {
                        mFilterKeyFrameView.setNextViewEnable(false);
                    } else {
                        mFilterKeyFrameView.setNextViewEnable(true);
                        mFilterKeyFrameView.setSequenceViewIsSeekingStatus(true);
                        mStreamingContext.seekTimeline(mTimeline, nextKeyFrame, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, 0);
                        mFilterKeyFrameView.scrollSequenceViewTo(nextKeyFrame);
                    }
                }
            }

            @Override
            public void beforeFrameClick() {
                NvsFx fxFromClip = getFxFromClip();
                if (fxFromClip != null) {
                    long beforeKeyFrame = fxFromClip.findKeyframeTime(FILTER_INTENSITY, mStreamingContext.getTimelineCurrentPosition(mTimeline),
                            NvsFx.KEY_FRAME_FIND_MODE_INPUT_TIME_BEFORE);
                    if (beforeKeyFrame == -1) {
                        mFilterKeyFrameView.setBeforeViewEnable(false);
                    } else {
                        mFilterKeyFrameView.setBeforeViewEnable(true);
                        mFilterKeyFrameView.setSequenceViewIsSeekingStatus(true);
                        mStreamingContext.seekTimeline(mTimeline, beforeKeyFrame, NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE, 0);
                        mFilterKeyFrameView.scrollSequenceViewTo(beforeKeyFrame);
                    }
                }
            }

            @Override
            public void deleteFrameClick(long deleteStamp) {
                NvsFx fxFromClip = getFxFromClip();
                if (fxFromClip != null) {
                    fxFromClip.removeKeyframeAtTime(FILTER_INTENSITY, deleteStamp);
                    mVideoClipFxInfo.getKeyFrameInfoMap().remove(deleteStamp);
                    mFilterKeyFrameView.updateKeyFramePointView(mVideoClipFxInfo.getKeyFrameInfoMap());
                }
            }

            @Override
            public void onProgressChanged(boolean needAddKeyFrame, long currentStamp, double intensity) {
                if (needAddKeyFrame) {
                    mVideoClipFxInfo.putKeyFrameInfo(currentStamp, intensity);
                    mFilterKeyFrameView.updateKeyFramePointView(mVideoClipFxInfo.getKeyFrameInfoMap());
                } else {
                    mVideoClipFxInfo.putKeyFrameInfo(currentStamp, intensity);
                    NvsFx fxFromClip = getFxFromClip();
                    fxFromClip.setFloatValAtTime(FILTER_INTENSITY, intensity, currentStamp);
                }
                mVideoFragment.seekTimeline(currentStamp, 0);
            }
        });
        // 滑动事件处理
        mFilterKeyFrameView.setOnSequenceScrollChangeListener(new FilterKeyFrameView.OnSequenceScrollChangeListener() {
            @Override
            public void onScrollX(long currentTimeStamp) {
                mVideoFragment.seekTimeline(currentTimeStamp, 0);
            }
        });
    }

    private NvsFx getFxFromClip() {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        for (int i = 0; i < videoTrack.getClipCount(); i++) {
            NvsVideoClip videoClip = videoTrack.getClipByIndex(i);
            if (videoClip == null) {
                continue;
            }

            int fxCount = videoClip.getFxCount();
            for (int j = 0; j < fxCount; j++) {
                NvsVideoFx fx = videoClip.getFxByIndex(j);
                if (fx == null) {
                    continue;
                }

                String name = fx.getBuiltinVideoFxName();
                if (name == null) {
                    continue;
                }
                if (name.equals(Constants.FX_COLOR_PROPERTY) || name.equals(Constants.FX_VIGNETTE) ||
                        name.equals(Constants.FX_SHARPEN) || name.equals(Constants.FX_TRANSFORM_2D)) {
                    continue;
                }
                return fx;
            }
        }
        return null;
    }

    private float getFxIntensityAndApplyKeyFrame() {
        NvsFx fx = getFxFromClip();
        if (fx != null) {
            float filter_intensity = (float) fx.getFloatValAtTime(FILTER_INTENSITY, mStreamingContext.getTimelineCurrentPosition(mTimeline));
            fx.setFloatValAtTime(FILTER_INTENSITY, filter_intensity, mStreamingContext.getTimelineCurrentPosition(mTimeline));
            return filter_intensity;
        }
        return 1.0f;
    }

    // click
    @Override
    protected void onFilterChanged(int position) {
        super.onFilterChanged(position);
        if (position == 0) {
            mAddKeyFrameBtn.setVisibility(View.INVISIBLE);
        } else {
            mAddKeyFrameBtn.setVisibility(View.VISIBLE);
            changeAddKeyFrameTitleViewIcon(false);
            mFilterView.setIntensityLayoutVisible(View.VISIBLE);
        }
        // 切换滤镜 移除之前的关键帧信息
        mVideoClipFxInfo.getKeyFrameInfoMap().clear();
        mFilterKeyFrameView.updateKeyFramePointView(mVideoClipFxInfo.getKeyFrameInfoMap());
    }

    // click
    @Override
    protected void onFilterChanged(NvsTimeline timeline, VideoClipFxInfo changedClipFilter) {
        TimelineUtil.buildSingleClipFilter(timeline, mClipInfo, changedClipFilter);
    }

    @Override
    protected boolean isNeedShowSeekBarWhenChangeFilterFromParent() {
        if (mVideoClipFxInfo != null && mVideoClipFxInfo.getKeyFrameInfoMap() != null && mVideoClipFxInfo.getKeyFrameInfoMap().keySet().size() > 0) {
            return false;
        } else {
            return super.isNeedShowSeekBarWhenChangeFilterFromParent();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_keyframe_title_view:
                // 处理 关键帧布局的显示和隐藏
                mFilterKeyFrameView.setCurrentFx(getFxFromClip());
                mFilterPanel.setVisibility(View.INVISIBLE);
                mFilterKeyFramePanel.setVisibility(View.VISIBLE);
                mVideoFragment.setPlaySeekVisiable(false);
                break;
            case R.id.filterAssetFinish:
                // 写回数据库
                mClipInfo.setVideoClipFxInfo(mVideoClipFxInfo);
                mClipArrayList.set(mClipIndex, mClipInfo);
                BackupData.instance().setClipInfoData(mClipArrayList);
                TimelineData.instance().setClipInfoData(mClipArrayList);
                removeTimeline();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                quitActivity();
                break;
                // 处理完关键帧
            case R.id.filter_key_frame_finish_view:
                mFilterPanel.setVisibility(View.VISIBLE);
                mFilterKeyFramePanel.setVisibility(View.INVISIBLE);
                //
                HashMap<Long, Double> keyFrameInfoMap = mVideoClipFxInfo.getKeyFrameInfoMap();
                if (keyFrameInfoMap.size() > 0) {
                    mFilterView.setIntensityLayoutVisible(View.INVISIBLE);
                    changeAddKeyFrameTitleViewIcon(true);
                } else {
                    mFilterView.setIntensityLayoutVisible(View.VISIBLE);
                    changeAddKeyFrameTitleViewIcon(false);
                }
                mVideoFragment.setPlaySeekVisiable(true);
                break;
            default:
                break;
        }
    }
}
