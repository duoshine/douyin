package com.duoshine.douyin.meishe.sdkdemo.edit.Caption;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.base.BaseActivity;
import com.duoshine.douyin.meishe.sdkdemo.download.AssetDownloadActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.VideoFragment;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.AssetItem;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.BackupData;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.CaptionColorInfo;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.ParseJsonFile;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.CustomViewPager;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.HorizontalSeekBar;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.InputDialog;
import com.duoshine.douyin.meishe.sdkdemo.utils.AppManager;
import com.duoshine.douyin.meishe.sdkdemo.utils.ColorUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.Constants;
import com.duoshine.douyin.meishe.sdkdemo.utils.TimelineUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.ToastUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.Util;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.KeyFrameInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.google.android.material.tabs.TabLayout;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCaption;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.duoshine.douyin.meishe.sdkdemo.utils.Constants.*;
import static com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset.ASSET_CAPTION_ANIMATION;
import static com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset.ASSET_CAPTION_IN_ANIMATION;
import static com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset.ASSET_CAPTION_OUT_ANIMATION;


public class CaptionStyleActivity extends BaseActivity {
    private static final String TAG = "CaptionStyleActivity";
    private int CATEGORY_RICH_WORD = 5;
    private int CATEGORY_BUBBLE = 6;
    private int CATEGORY_IN_ANIMATION = 7;
    private int CATEGORY_OUT_ANIMATION = 8;
    private int CATEGORY_ANIMATION = 9;
    private final String PATH_RICH_WORD = "captionrichword";
    private final String PATH_BUBBLE = "captionbubble";
    private final String PATH_ANIMATION = "captionanimation/combination";
    private final String PATH_IN_ANIMATION = "captionanimation/in";
    private final String PATH_OUT_ANIMATION = "captionanimation/out";

    private static final int CAPTIONSTYLEREQUESTLIST = 103;
    private static final int VIDEOPLAYTOEOF = 105;
    private static final int CAPTION_RICH_WORD = 106;
    private static final int CAPTION_ANIMATION = 107;
    private static final int CAPTION_IN_ANIMATION = 108;
    private static final int CAPTION_OUT_ANIMATION = 109;
    private static final int CAPTION_BUBBLE = 110;
    private static final int VIDEO_PLAY_STOP = 112;

    private static final int CAPTION_ALIGNLEFT = 0;
    private static final int CAPTION_ALIGNHORIZCENTER = 1;
    private static final int CAPTION_ALIGNRIGHT = 2;
    private static final int CAPTION_ALIGNTOP = 3;
    private static final int CAPTION_ALIGNVERTCENTER = 4;
    private static final int CAPTION_ALIGNBOTTOM = 5;

    private static final int PLAY_VIDEO_FORM_START = 1130;
    //这里的值是字幕的字间距 字间距支持设置百分比或绝对值
    //todo 字间距这还需要确认值，临时写了个90
    private static final float CAPTION_SMALL_SPACING = 90;
    private static final float CAPTION_STANDARD_SPACING = 100;
    private static final float CAPTION_MORE_LARGE_SPACING = 150;
    private static final float CAPTION_LARGE_SPACEING = 200;
    //字幕的行间距 行间距目前支持设置绝对值
    //todo 这几个值需要待定
    private static final float CAPTION_SMALL_LINE_SPACING = -10;
    private static final float CAPTION_STANDARD_LINE_SPACING = 0;
    private static final float CAPTION_MORE_LARGE_LINE_SPACING = 20;
    private static final float CAPTION_LARGE_LINE_SPACEING = 40;

    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;

    private TabLayout mCaptionStyleTab;
    private CustomViewPager mViewPager;
    private ImageView mCaptionAssetFinish;
    private HorizontalSeekBar mSeekBar;
    private VideoFragment mVideoFragment;
    /*
     * 总的字幕样式列表
     * List of total caption styles
     * */
    private ArrayList<AssetItem> mTotalCaptionStyleList;
    private ArrayList<AssetItem> mRichWordList;//花字
    private ArrayList<AssetItem> mAnimationList;//组合动画
    private ArrayList<AssetItem> mMarchInAniList;//入场动画
    private ArrayList<AssetItem> mMarchOutAniList;//出场动画
    private ArrayList<AssetItem> mBubbleList;//气泡
    private ArrayList<Fragment> mAssetFragmentsArray;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;

    private NvAssetManager mAssetManager;
    private int mCaptionStyleType = NvAsset.ASSET_CAPTION_STYLE;
    private int mFontType = NvAsset.ASSET_FONT;

    private long mMaxDuration;
    private int mSelectedStylePos = 0;
    private int mSelectedRichPos = 0;
    private int mSelectedBubblePos = 0;
    private int mSelectedAnimationPos = 0;
    private int mSelectedInAnimationPos = 0;
    private int mSelectedOutAnimationPos = 0;
    private int mSelectedType = -1;
    private int mSelectedColorPos = -1;
    private int mSelectedOutlinePos = 0;
    private int mSelectedBackgroundPos = 0;
    private int mSelectedFontPos = 0;
    NvsTimelineCaption mCurAddCaption = null;
    private int mAlignType = -1;
    private CaptionRichWordFragment mRichWordFragment;
    private CaptionBubbleFragment mBubbleFragment;
    private CaptionAnimationFragment mAnimationFragment;
    private CaptionStyleFragment mCaptionStyleFragment;
    private CaptionColorFragment mCaptionColorFragment;
    private CaptionOutlineFragment mCaptionOutlineFragment;
    private CaptionBackgroundFragment mCaptionBackgroundFragment;
    private CaptionFontFragment mCaptionFontFragment;
    private CaptionSizeFragment mCaptionSizeFragment;
    private CaptionPositionFragment mCaptionPositionFragment;
    private CaptionLetterSpacingFragment mCaptionLetterSpacingFragment;
    private ArrayList<CaptionColorInfo> mCaptionColorList;
    private ArrayList<CaptionColorInfo> mCaptionOutlineColorList;
    private ArrayList<CaptionColorInfo> mCaptionBackgroundList;
    private ArrayList<AssetItem> mCaptionFontList;
    private int mCaptionColorOpacityValue = 100;
    private int mCaptionBackgroundOpacityValue = 100;
    private float mCaptionBackgroundCornerValue = 0;
    private int mCaptionOutlineWidthValue = 100;
    private int mCaptionOutlineOpacityValue = 100;
    //    private int mCaptionSizeValue = 72;
    ArrayList<CaptionInfo> mCaptionDataListClone;
    private int mCurCaptionZVal = 0;

    private boolean bIsStyleUuidApplyToAll = false;
    private boolean bIsCaptionColorApplyToAll = false;
    private boolean bIsOutlineApplyToAll = false;
    private boolean bIsCaptionBackgroundApplyToAll = false;
    private boolean bIsFontApplyToAll = false;
    private boolean bIsSizeApplyToAll = false;
    private boolean bIsPositionApplyToAll = false;
    private boolean bIsLetterSpacingApplyToAll = false;


    private boolean isCaptionStyleItemClick = false;
    boolean m_waitFlag = false;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private int mFontCurClickPos = 0;
    private boolean isTraditionCaption = true;
    private boolean hasCombineAnimation;
    private int IN_OUT_ANIMATION_DEFAULT_DURATION = 500;
    private int ANIMATION_DEFAULT_DURATION = 600;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_caption_style;
    }

    private CaptionStyleActivity.CaptionStyleHandler m_handler = new CaptionStyleActivity.CaptionStyleHandler(this);

    static class CaptionStyleHandler extends Handler {
        WeakReference<CaptionStyleActivity> mWeakReference;

        public CaptionStyleHandler(CaptionStyleActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final CaptionStyleActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case VIDEOPLAYTOEOF:
                    case VIDEO_PLAY_STOP:
                        activity.updateCaption();
                        break;
                    case ASSET_LIST_REQUEST_SUCCESS:
                        activity.updateFontList();
                        break;
                    case ASSET_LIST_REQUEST_FAILED:
                        activity.fontListRequestFail();
                        break;
                    case ASSET_DOWNLOAD_START_TIMER:
                        activity.startProgressTimer();
                        String progressUuid = (String) msg.obj;
                        activity.fontItemCopy(progressUuid);
                        break;
                    case ASSET_DOWNLOAD_SUCCESS:
                        String successUuid = (String) msg.obj;
                        activity.fontItemCopy(successUuid);
                        activity.applyLastSelFont(successUuid);
                        activity.updateFontItem();
                        break;
                    case ASSET_DOWNLOAD_FAILED:
                        activity.fontDownloadFail();
                        activity.updateFontItem();
                        break;
                    case ASSET_DOWNLOAD_INPROGRESS:
                        activity.updateFontDownloadProgress();
                        break;
                    case PLAY_VIDEO_FORM_START:
                        activity.playVideoFormStart();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mCaptionStyleTab = (TabLayout) findViewById(R.id.captionStyleTab);
        mViewPager = (CustomViewPager) findViewById(R.id.viewPager);
        mViewPager.setPagingEnabled(false);
        mCaptionAssetFinish = (ImageView) findViewById(R.id.captionAssetFinish);
        mSeekBar = findViewById(R.id.seek_bar);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        initAssetData();
        initVideoFragment();
        initTabLayout();
    }

    @Override
    protected void initListener() {
        mCaptionAssetFinish.setOnClickListener(this);
        mSeekBar.setOnRangeListener(new HorizontalSeekBar.onRangeListener() {
            @Override
            public void onRange(float left, float right) {
                if (mCurAddCaption == null) {
                    return;
                }
                int index = getCaptionIndex(mCurCaptionZVal);
                CaptionInfo captionInfo = mCaptionDataListClone.get(index);
                int leftValue = (int) (Float.parseFloat(String.format(getString(R.string.format_1f), left)) * 1000);
                int rightValue = (int) (Float.parseFloat(String.format(getString(R.string.format_1f), right)) * 1000);
                //组合动画与出入动画互斥(出入动画不互斥)。前者默认时长0.5s后者0.6s
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionAnimationPackageId())) {
                    if (leftValue <= 100) {
                        leftValue = 100;
                        mSeekBar.setLeftProgress(leftValue);
                        //组合动画最小值可设置成100ms
                    }
                    mCurAddCaption.setModularCaptionAnimationPeroid(leftValue);
                    if (captionInfo != null) {
                        captionInfo.setCombinationAnimationDuration(leftValue);
                    }
                } else {
                    if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId())) {
                        if (mSeekBar.getMaxProgress() - leftValue < IN_OUT_ANIMATION_DEFAULT_DURATION && TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                            //如果设置入动画后，剩余的时间小于默认时间500毫秒（出入动画默认时长500ms，不论设置不设置出动画），且此时没有出动画，则把出动画时长设置成0
                            mCurAddCaption.setModularCaptionOutAnimationDuration(0);
                        }
                        if (captionInfo != null) {
                            captionInfo.setMarchInAnimationDuration(leftValue);
                        }
                        mCurAddCaption.setModularCaptionInAnimationDuration(leftValue);
                    }
                    if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                        //如果设置出动画后，剩余的时间小于默认时间500毫秒（出入动画默认时长500ms，不论设置不设置入动画），且此时没有入动画，则把入动画时长设置成0
                        if (mSeekBar.getMaxProgress() - right < IN_OUT_ANIMATION_DEFAULT_DURATION && TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId())) {
                            mCurAddCaption.setModularCaptionInAnimationDuration(0);
                        }
                        if (captionInfo != null) {
                            captionInfo.setMarchOutAnimationDuration(rightValue);
                        }
                        mCurAddCaption.setModularCaptionOutAnimationDuration(rightValue);
                    }

                }
                if (mVideoFragment != null) {
                    mVideoFragment.stopEngine();
                }
                m_handler.removeMessages(PLAY_VIDEO_FORM_START);
                m_handler.sendEmptyMessageDelayed(PLAY_VIDEO_FORM_START, 500);
                //Log.d("lhz", "leftValue=" + leftValue + "**rightValue=" + rightValue+"**maxValue="+mSeekBar.getMaxProgress());
            }
        });
        if (mVideoFragment != null) {
            mVideoFragment.setCaptionTextEditListener(new VideoFragment.VideoCaptionTextEditListener() {
                @Override
                public void onCaptionTextEdit() {
                    /*
                     * 字幕编辑
                     * Caption editing
                     * */
                    InputDialog inputDialog = new InputDialog(CaptionStyleActivity.this, R.style.dialog, new InputDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean ok) {
                            if (ok) {
                                InputDialog d = (InputDialog) dialog;
                                String userInputText = d.getUserInputText();
                                mCurAddCaption.setText(userInputText);
                                updateCaption();
                                int index = getCaptionIndex(mCurCaptionZVal);
                                if (index >= 0) {
                                    mCaptionDataListClone.get(index).setText(userInputText);
                                }
                            }
                        }
                    });

                    if (mCurAddCaption != null) {
                        inputDialog.setUserInputText(mCurAddCaption.getText());
                    }
                    inputDialog.show();
                }
            });
            mVideoFragment.setAssetEditListener(new VideoFragment.AssetEditListener() {
                @Override
                public void onAssetDelete() {
                    mTimeline.removeCaption(mCurAddCaption);
                    mCurAddCaption = null;
                    mVideoFragment.setCurCaption(mCurAddCaption);
                    mVideoFragment.changeCaptionRectVisible();
                    int index = getCaptionIndex(mCurCaptionZVal);
                    if (index >= 0) {
                        mCaptionDataListClone.remove(index);
                        BackupData.instance().setCaptionData(mCaptionDataListClone);
                        removeTimeline();
                        Intent intent = new Intent();
                        intent.putExtra("isSelectCurCaption", false);
                        setResult(RESULT_OK, intent);
                        AppManager.getInstance().finishActivity();
                    }
                }

                @Override
                public void onAssetSelected(PointF curPoint) {
                }

                @Override
                public void onAssetTranstion() {
                    if (mCurAddCaption == null)
                        return;
                    //Log.e(TAG,"captionTranslation.x = " + captionTranslation.x + "pointF.y =" + captionTranslation.y);
                    int zVal = (int) mCurAddCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setTranslation(mCurAddCaption.getCaptionTranslation());
                    }
                }

                @Override
                public void onAssetScale() {
                    int zVal = (int) mCurAddCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setUsedScaleRotationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setScaleFactorX(mCurAddCaption.getScaleX());
                        mCaptionDataListClone.get(index).setScaleFactorY(mCurAddCaption.getScaleY());
                        mCaptionDataListClone.get(index).setAnchor(mCurAddCaption.getAnchorPoint());
                        mCaptionDataListClone.get(index).setRotation(mCurAddCaption.getRotationZ());
//                        mCaptionDataListClone.get(index).setCaptionSize(mCurAddCaption.getFontSize());
                        mCaptionDataListClone.get(index).setTranslation(mCurAddCaption.getCaptionTranslation());
                    }
                }

                @Override
                public void onAssetAlign(int alignVal) {
                    int zVal = (int) mCurAddCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0)
                        mCaptionDataListClone.get(index).setAlignVal(alignVal);
                }

                @Override
                public void onAssetHorizFlip(boolean isHorizFlip) {

                }

                @Override
                public void onOrientationChange(boolean isHorizontal) {
                    int zVal = (int) mCurAddCaption.getZValue();
                    int index = getCaptionIndex(zVal);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setOrientationType(isHorizontal ? CaptionInfo.O_HORIZONTAL : CaptionInfo.O_VERTICAL);
                    }
                }
            });
            mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
                @Override
                public void playBackEOF(NvsTimeline timeline) {
                    m_handler.sendEmptyMessage(VIDEOPLAYTOEOF);
                }

                @Override
                public void playStopped(NvsTimeline timeline) {
                    if (isCaptionStyleItemClick)
                        return;
                    m_handler.sendEmptyMessage(VIDEO_PLAY_STOP);
                }

                @Override
                public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                    mVideoFragment.setDrawRectVisible(View.GONE);
                }

                @Override
                public void streamingEngineStateChanged(int state) {

                }
            });
            mVideoFragment.setLiveWindowClickListener(new VideoFragment.OnLiveWindowClickListener() {
                @Override
                public void onLiveWindowClick() {
                    isCaptionStyleItemClick = false;
                }
            });
        }


        mVideoFragment.setBeforeAnimateStickerEditListener(new VideoFragment.IBeforeAnimateStickerEditListener() {
            @Override
            public boolean beforeTransitionCouldDo() {
                if (mCurAddCaption == null) {
                    return false;
                }
                boolean b = ifCouldEditCaption();
                if (!b) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public boolean beforeScaleCouldDo() {
                if (mCurAddCaption == null) {
                    return false;
                }
                boolean b = ifCouldEditCaption();
                if (!b) {
                    return false;
                } else {
                    return true;
                }
            }
        });

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

    private CaptionInfo getCurrentCaptionInfo() {
        if (mCurAddCaption == null) {
            return null;
        }
        int zValue = (int) mCurAddCaption.getZValue();
        int captionIndex = getCaptionIndex(zValue);
        CaptionInfo captionInfo = mCaptionDataListClone.get(captionIndex);
        return captionInfo;
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.captionAssetFinish:
                applyToAllCaption();
                BackupData.instance().setCaptionData(mCaptionDataListClone);
                removeTimeline();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                intent.putExtra("isSelectCurCaption", true);
                AppManager.getInstance().finishActivity();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        removeTimeline();
        AppManager.getInstance().finishActivity();
        super.onBackPressed();
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case CAPTIONSTYLEREQUESTLIST:
                if (isTraditionCaption) {
                    initCaptionStyleList();
                    mCaptionStyleFragment.setAssetInfolist(mTotalCaptionStyleList);
                    mSelectedStylePos = getCaptionStyleSelectedIndex();
                    mCaptionStyleFragment.setSelectedPos(mSelectedStylePos);
                    mCaptionStyleFragment.notifyDataSetChanged();
                    updateCaption();
                }
                break;
            case CAPTION_RICH_WORD:
                mRichWordList.clear();
                //花字
                dealAssetData(NvAsset.ASSET_CAPTION_RICH_WORD, PATH_RICH_WORD, mRichWordList);
                changeAssemblyCaption(NvAsset.ASSET_CAPTION_RICH_WORD);
                updateCaption();
                break;
            case CAPTION_BUBBLE:
                mBubbleList.clear();
                //气泡
                dealAssetData(NvAsset.ASSET_CAPTION_BUBBLE, PATH_BUBBLE, mBubbleList);
                changeAssemblyCaption(NvAsset.ASSET_CAPTION_BUBBLE);
                updateCaption();
                break;
            case CAPTION_IN_ANIMATION:
                mMarchInAniList.clear();
                //入场动画
                dealAssetData(ASSET_CAPTION_IN_ANIMATION, PATH_IN_ANIMATION, mMarchInAniList);
                changeAssemblyCaption(ASSET_CAPTION_IN_ANIMATION);
                updateCaption();
                break;
            case CAPTION_OUT_ANIMATION:
                mMarchOutAniList.clear();
                //出场动画
                dealAssetData(ASSET_CAPTION_OUT_ANIMATION, PATH_OUT_ANIMATION, mMarchOutAniList);
                changeAssemblyCaption(ASSET_CAPTION_OUT_ANIMATION);
                updateCaption();
                break;
            case CAPTION_ANIMATION:
                mAnimationList.clear();
                //组合动画
                dealAssetData(ASSET_CAPTION_ANIMATION, PATH_ANIMATION, mAnimationList);
                changeAssemblyCaption(ASSET_CAPTION_ANIMATION);
                updateCaption();
                break;

            default:
                break;
        }
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
        /*
         * 存储素材数据线程
         * Store material data thread
         * */
        new Thread(new Runnable() {
            @Override
            public void run() {
                NvAssetManager.sharedInstance().setAssetInfoToSharedPreferences(mFontType);
            }
        }).start();
    }

    private void playVideoFormStart() {
        if (mCurAddCaption != null && mVideoFragment != null) {
            mVideoFragment.stopEngine();
            long startTime = mCurAddCaption.getInPoint();
            long endTime = mCurAddCaption.getOutPoint();
            mVideoFragment.setDrawRectVisible(View.GONE);
            mVideoFragment.playVideo(startTime, endTime);
        }
    }

    private void applyLastSelFont(String uuid) {
        String curClickUuid = mCaptionFontList.get(mFontCurClickPos).getAsset().uuid;
        if (!TextUtils.isEmpty(curClickUuid) && curClickUuid.equals(uuid)) {
            String fontPath = mCaptionFontList.get(mFontCurClickPos).getAsset().localDirPath;
            applyCaptionFont(fontPath);
            mCaptionFontFragment.setSelectedPos(mFontCurClickPos);
            mSelectedFontPos = mFontCurClickPos;
        }
    }

    private void startProgressTimer() {
        if (mTimer == null)
            mTimer = new Timer();
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    m_handler.sendEmptyMessage(ASSET_DOWNLOAD_INPROGRESS);
                }
            };
            mTimer.schedule(mTimerTask, 0, 50);
        }
    }

    private void stopProgressTimer() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void updateFontList() {
        initCaptionFontList();
        mSelectedFontPos = getCaptionFontSelectedIndex();
        mCaptionFontFragment.setFontInfolist(mCaptionFontList);
        mCaptionFontFragment.setSelectedPos(mSelectedFontPos);
        mCaptionFontFragment.notifyDataSetChanged();
    }

    private void updateFontDownloadProgress() {
        boolean isDownloadState = false;
        for (int i = 0; i < mCaptionFontList.size(); ++i) {
            NvAsset asset = mCaptionFontList.get(i).getAsset();
            if (asset == null)
                continue;
            if (asset.downloadStatus == NvAsset.DownloadStatusInProgress
                    || asset.downloadStatus == NvAsset.DownloadStatusPending) {
                isDownloadState = true;
            }
        }
        if (isDownloadState) {
            /*
             * 下载状态，通知更新数据
             * Download status, notify update data
             * */
            updateFontItem();
        }
    }

    private void updateFontItem() {
        mCaptionFontFragment.notifyDataSetChanged();
    }

    private void fontItemCopy(String uuid) {
        NvAsset curAsset = null;
        ArrayList<NvAsset> usableAsset = getFontAssetsDataList();
        for (int index = 0; index < usableAsset.size(); ++index) {
            NvAsset asset = usableAsset.get(index);
            if (asset == null)
                continue;
            if (!TextUtils.isEmpty(asset.uuid) && uuid.equals(asset.uuid)) {
                curAsset = asset;
                break;
            }
        }

        for (int i = 0; i < mCaptionFontList.size(); ++i) {
            NvAsset asset = mCaptionFontList.get(i).getAsset();
            if (asset == null)
                continue;
            if (curAsset != null && !TextUtils.isEmpty(asset.uuid) && asset.uuid.equals(uuid)) {
                mCaptionFontList.get(i).getAsset().copyAsset(curAsset);
            }
        }
    }

    private void fontListRequestFail() {
        ToastUtil.showToast(this, this.getResources().getString(R.string.check_network));
    }

    private void fontDownloadFail() {
        ToastUtil.showToast(this, this.getResources().getString(R.string.download_failed));
    }

    private void applyToAllCaption() {
        int index = getCaptionIndex(mCurCaptionZVal);
        if (index < 0)
            return;
        int count = mCaptionDataListClone.size();
        CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
        for (int i = 0; i < count; ++i) {
            if (i == index)
                continue;
            if (bIsStyleUuidApplyToAll) {
                mCaptionDataListClone.get(i).setCaptionStyleUuid(curCaptionInfo.getCaptionStyleUuid());
            }
            if (bIsCaptionColorApplyToAll) {
                mCaptionDataListClone.get(i).setCaptionColor(curCaptionInfo.getCaptionColor());
                mCaptionDataListClone.get(i).setCaptionColorAlpha(curCaptionInfo.getCaptionColorAlpha());
                mCaptionDataListClone.get(i).setUsedColorFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
            }
            if (bIsOutlineApplyToAll) {
                mCaptionDataListClone.get(i).setHasOutline(curCaptionInfo.isHasOutline());
                mCaptionDataListClone.get(i).setOutlineColor(curCaptionInfo.getOutlineColor());
                mCaptionDataListClone.get(i).setOutlineColorAlpha(curCaptionInfo.getOutlineColorAlpha());
                mCaptionDataListClone.get(i).setOutlineWidth(curCaptionInfo.getOutlineWidth());
                mCaptionDataListClone.get(i).setUsedOutlineFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
            }
            if (bIsFontApplyToAll) {
                mCaptionDataListClone.get(i).setCaptionFont(curCaptionInfo.getCaptionFont());
                mCaptionDataListClone.get(i).setBold(curCaptionInfo.isBold());
                mCaptionDataListClone.get(i).setItalic(curCaptionInfo.isItalic());
                mCaptionDataListClone.get(i).setShadow(curCaptionInfo.isShadow());
                mCaptionDataListClone.get(i).setUsedIsBoldFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                mCaptionDataListClone.get(i).setUsedIsItalicFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                mCaptionDataListClone.get(i).setUsedShadowFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
            }
//            if (bIsSizeApplyToAll) {
//                mCaptionDataListClone.get(i).setCaptionSize(curCaptionInfo.getCaptionSize());
//            }

            if (bIsLetterSpacingApplyToAll) {
                mCaptionDataListClone.get(i).setUsedLetterSpacingFlag(curCaptionInfo.getUsedLetterSpacingFlag());
                mCaptionDataListClone.get(i).setLetterSpacing(curCaptionInfo.getLetterSpacing());
                mCaptionDataListClone.get(i).setUsedLetterSpacingFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                //同时设置行间距
                mCaptionDataListClone.get(i).setLineSpacing(curCaptionInfo.getLineSpacing());
            }
            if (bIsCaptionBackgroundApplyToAll) {
                mCaptionDataListClone.get(i).setCaptionBackground(curCaptionInfo.getCaptionBackground());
                mCaptionDataListClone.get(i).setCaptionBackgroundAlpha(curCaptionInfo.getCaptionBackgroundAlpha());
                mCaptionDataListClone.get(i).setCaptionBackgroundRadius(curCaptionInfo.getCaptionBackgroundRadius());
                mCaptionDataListClone.get(i).setUsedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                mCaptionDataListClone.get(i).setUsedBackgroundRadiusFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
            }
        }
        if (bIsPositionApplyToAll) {
            updateCaptionPosition();
        }
    }

    private void updateCaptionPosition() {
        NvsTimelineCaption caption = mTimeline.getFirstCaption();
        while (caption != null) {
            if (caption.getCategory() == NvsTimelineCaption.THEME_CATEGORY
                    && caption.getRoleInTheme() != NvsTimelineCaption.ROLE_IN_THEME_GENERAL) {//主题字幕不作处理
                caption = mTimeline.getNextCaption(caption);
                continue;
            }
            int zVal = (int) caption.getZValue();
            if (mCurCaptionZVal == zVal) {
                caption = mTimeline.getNextCaption(caption);
                continue;
            }
            List<PointF> list = caption.getBoundingRectangleVertices();
            if (list == null || list.size() < 4) {
                caption = mTimeline.getNextCaption(caption);
                continue;
            }

            /*
             * 字幕对齐方式，包括左对齐，右对齐，水平居中，上对齐，底部对齐，垂直居中
             * Caption alignment, including left, right, centered horizontally，top, bottom, centered vertically
             * */
            int index = getCaptionIndex(zVal);
            switch (mAlignType) {
                case CAPTION_ALIGNLEFT:
                    Collections.sort(list, new Util.PointXComparator());
                    float xLeftOffset = -(mTimeline.getVideoRes().imageWidth / 2 + list.get(0).x);
                    caption.translateCaption(new PointF(xLeftOffset, 0));
                    break;
                case CAPTION_ALIGNHORIZCENTER:
                    Collections.sort(list, new Util.PointXComparator());
                    float xHorizCenterOffset = -((list.get(3).x - list.get(0).x) / 2 + list.get(0).x);
                    caption.translateCaption(new PointF(xHorizCenterOffset, 0));
                    break;
                case CAPTION_ALIGNRIGHT:
                    Collections.sort(list, new Util.PointXComparator());
                    float xRightOffset = mTimeline.getVideoRes().imageWidth / 2 - list.get(3).x;
                    caption.translateCaption(new PointF(xRightOffset, 0));
                    break;
                case CAPTION_ALIGNTOP:
                    Collections.sort(list, new Util.PointYComparator());
                    float yTopdis = list.get(3).y - list.get(0).y;
                    float yTopOffset = mTimeline.getVideoRes().imageHeight / 2 - list.get(0).y - yTopdis;
                    caption.translateCaption(new PointF(0, yTopOffset));
                    break;
                case CAPTION_ALIGNVERTCENTER:
                    Collections.sort(list, new Util.PointYComparator());
                    float yVertCenterOffset = -((list.get(3).y - list.get(0).y) / 2 + list.get(0).y);
                    caption.translateCaption(new PointF(0, yVertCenterOffset));
                    break;
                case CAPTION_ALIGNBOTTOM:
                    Collections.sort(list, new Util.PointYComparator());
                    float yBottomdis = list.get(3).y - list.get(0).y;
                    float yBottomOffset = -(mTimeline.getVideoRes().imageHeight / 2 + list.get(3).y - yBottomdis);
                    caption.translateCaption(new PointF(0, yBottomOffset));
                    break;
                default:
                    break;
            }
            if (index >= 0) {

                mCaptionDataListClone.get(index).setTranslation(caption.getCaptionTranslation());
                mCaptionDataListClone.get(index).setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
            }
            caption = mTimeline.getNextCaption(caption);
        }
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mCurAddCaption = null;
        mTimeline = null;
        m_handler.removeCallbacksAndMessages(null);
        stopProgressTimer();
    }

    private void initTabLayout() {
        String[] assetName;
        if (isTraditionCaption) {
            //传统字幕
            assetName = getResources().getStringArray(R.array.captionEdit);
            mCaptionStyleTab.setSelectedTabIndicatorColor(getResources().getColor(R.color.blue_4a));
        } else {
            //拼装字幕
            assetName = getResources().getStringArray(R.array.pieced_caption);
            mCaptionStyleTab.setSelectedTabIndicatorColor(getResources().getColor(R.color.red_ff64));
        }
        for (int i = 0; i < assetName.length; i++) {
            TextView textView = new TextView(this);
            textView.setText(assetName[i]);
            textView.setTextSize(12);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.gray_90));
            mCaptionStyleTab.addTab(mCaptionStyleTab.newTab().setCustomView(textView));
        }
        initCaptionTabFragment();
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mAssetFragmentsArray.get(position);
            }

            @Override
            public int getCount() {
                return mAssetFragmentsArray.size();
            }
        });
        /*
         * 添加tab切换的监听事件
         * Add a tab switch to listen for events
         * */
        mCaptionStyleTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                /*
                 * 当前选中的tab的位置，切换到相应的fragment
                 * Position of the currently selected tab, switch to the corresponding fragment
                 * */
                int nowPosition = tab.getPosition();
                if (nowPosition == 1 && mCurAddCaption != null) {
                    animationFragmentSelect();
                } else {
                    displaySeekBar(false);
                }
                TextView textView = (TextView) tab.getCustomView();
                if (isTraditionCaption && textView != null) {
                    textView.setTextColor(getResources().getColor(R.color.blue_4a));
                } else if (textView != null) {
                    textView.setTextColor(getResources().getColor(R.color.red_ff64));
                }
                mViewPager.setCurrentItem(nowPosition);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView textView = (TextView) tab.getCustomView();
                if (textView != null) {
                    textView.setTextColor(getResources().getColor(R.color.gray_90));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private boolean animationFragmentFirstSelect = true;

    /**
     * 动画fragment被选中的处理逻辑。
     */
    private void animationFragmentSelect() {
        if (animationFragmentFirstSelect) {
            //如果是第一次进入且被选中动画的fragment
            if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionAnimationPackageId())) {
                displayAnimationProgress(true, ASSET_CAPTION_ANIMATION);
            } else {
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId()) &&
                        !TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId())) {
                    int maxDuration = (int) ((mCurAddCaption.getOutPoint() - mCurAddCaption.getInPoint()) / 1000);
                    if (maxDuration - mCurAddCaption.getModularCaptionInAnimationDuration() < IN_OUT_ANIMATION_DEFAULT_DURATION) {
                        //如果设置入动画后，剩余的时间小于默认时间500毫秒（出入动画默认时长500ms，不论设置不设置出动画），且此时没有出动画，则把出动画时长设置成0
                        displayAnimationProgress(true, ASSET_CAPTION_OUT_ANIMATION);
                        displayAnimationProgress(true, ASSET_CAPTION_IN_ANIMATION);
                    } else {
                        displayAnimationProgress(true, ASSET_CAPTION_IN_ANIMATION);
                        displayAnimationProgress(true, ASSET_CAPTION_OUT_ANIMATION);
                    }
                    //第一次SeekBar设置左右进度的时候，如果存在遮盖，则后设置的会被显示也已被拖动。
                    return;
                }
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                    displayAnimationProgress(true, ASSET_CAPTION_OUT_ANIMATION);
                }
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId())) {
                    displayAnimationProgress(true, ASSET_CAPTION_IN_ANIMATION);
                }
            }
            animationFragmentFirstSelect = false;
        } else {
            //动画tab被选中
            displaySeekBar(!TextUtils.isEmpty(mCurAddCaption.getModularCaptionAnimationPackageId()) ||
                    !TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())
                    || !TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId()));
        }

    }

    private void initCaptionTabFragment() {
        if (isTraditionCaption) {
            mCaptionStyleFragment = initCaptionStyleFragment();//字幕样式
            mAssetFragmentsArray.add(mCaptionStyleFragment);
        } else {
            mAssetFragmentsArray.add(initRichWordFragment());//花字
            mAssetFragmentsArray.add(initAnimationFragment());//动画
            mAssetFragmentsArray.add(initBubbleFragment());//气泡
        }
        mCaptionColorFragment = initCaptionColorFragment();//填充
        mAssetFragmentsArray.add(mCaptionColorFragment);
        mCaptionOutlineFragment = initCaptionOutlineFragment();//描边
        mAssetFragmentsArray.add(mCaptionOutlineFragment);
        mCaptionBackgroundFragment = initCaptionBackgroundFragment();//背景
        mAssetFragmentsArray.add(mCaptionBackgroundFragment);


        mCaptionFontFragment = initCaptionFontFragment();//字体
        mAssetFragmentsArray.add(mCaptionFontFragment);
        mCaptionSizeFragment = initCaptionSizeFragment();
        mCaptionLetterSpacingFragment = initCaptionLetterSpacingFragment();//间距
        mAssetFragmentsArray.add(mCaptionLetterSpacingFragment);
        mCaptionPositionFragment = initCaptionPositionFragment();//位置
        mAssetFragmentsArray.add(mCaptionPositionFragment);
    }

    private void initAssetData() {
        Intent intent = getIntent();
        if (intent != null) {
            isTraditionCaption = intent.getBooleanExtra("tradition_caption", true);
        }
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline == null)
            return;
        mCurCaptionZVal = BackupData.instance().getCaptionZVal();
        mCaptionDataListClone = BackupData.instance().cloneCaptionData();

        TimelineUtil.setCaption(mTimeline, mCaptionDataListClone);
        selectCaption();
        mAssetManager = NvAssetManager.sharedInstance();
        if (isTraditionCaption) {
            mTitleBar.setTextCenter(R.string.traditional_caption);
            mTotalCaptionStyleList = new ArrayList<>();
            mAssetManager.searchLocalAssets(mCaptionStyleType);
            String bundlePath = "captionstyle";
            mAssetManager.searchReservedAssets(mCaptionStyleType, bundlePath);
            initCaptionStyleList();
        } else {
            mTitleBar.setTextCenter(R.string.pieced_together_caption);
            initPiecedTogetherCaption();
        }
        mCaptionBackgroundList = new ArrayList<>();
        mAssetFragmentsArray = new ArrayList<>();
        mCaptionColorList = new ArrayList<>();
        mCaptionOutlineColorList = new ArrayList<>();
        mCaptionFontList = new ArrayList<>();

        mAssetManager.searchLocalAssets(mFontType);//查找字体文件
        mAssetManager.searchReservedAssets(mFontType, "font");
        assetDataRequest();
        initCaptionBackgroundList();
        initCaptionColorList();
        initCaptionOutlineColorList();
    }

    private void checkInit() {
        if (mCurAddCaption != null) {
            long duration = mCurAddCaption.getOutPoint() - mCurAddCaption.getInPoint();
            mSeekBar.setMaxProgress((int) (duration / 1000));
            mMaxDuration = duration;
            mSelectedRichPos = getTargetPosition(mRichWordList, mCurAddCaption.getModularCaptionRendererPackageId());
            mSelectedBubblePos = getTargetPosition(mBubbleList, mCurAddCaption.getModularCaptionContextPackageId());
            mSelectedAnimationPos = getTargetPosition(mAnimationList, mCurAddCaption.getModularCaptionAnimationPackageId());
            mSelectedInAnimationPos = getTargetPosition(mMarchInAniList, mCurAddCaption.getModularCaptionInAnimationPackageId());
            mSelectedOutAnimationPos = getTargetPosition(mMarchOutAniList, mCurAddCaption.getModularCaptionOutAnimationPackageId());
        } else {
            //Log.d("lhz", "mCurAddCaption is null,mCurCaptionZVal=" + mCurCaptionZVal);
        }
        mSeekBar.setTransformText(1000, 1);
    }

    private void initCaptionStyleList() {
        mTotalCaptionStyleList.clear();
        ArrayList<NvAsset> usableAsset = getAssetsDataList(mCaptionStyleType);
        String jsonBundlePath = "captionstyle/info.json";
        ArrayList<ParseJsonFile.FxJsonFileInfo.JsonFileInfo> infoLists = ParseJsonFile.readBundleFxJsonFile(this, jsonBundlePath);
        if (infoLists != null) {
            for (ParseJsonFile.FxJsonFileInfo.JsonFileInfo jsonFileInfo : infoLists) {
                for (NvAsset asset : usableAsset) {
                    if (asset == null)
                        continue;
                    if (TextUtils.isEmpty(asset.uuid))
                        continue;

                    /*
                     * assets路径下的字幕样式包
                     * Caption style package under assets path
                     * */
                    if (asset.isReserved && asset.uuid.equals(jsonFileInfo.getFxPackageId())) {
                        asset.name = jsonFileInfo.getName();
                        asset.aspectRatio = Integer.parseInt(jsonFileInfo.getFitRatio());
                        StringBuilder coverPath = new StringBuilder("file:///android_asset/captionstyle/");
                        coverPath.append(jsonFileInfo.getImageName());
                        asset.coverUrl = coverPath.toString();
                    }
                }
            }
        }

        int ratio = TimelineData.instance().getMakeRatio();
        for (NvAsset asset : usableAsset) {
            if (asset == null)
                continue;
            if (TextUtils.isEmpty(asset.uuid))
                continue;
            if ((ratio & asset.aspectRatio) == 0) {
                /*
                 * 制作比例不适配，不加载
                 * Production proportions do not fit, do not load
                 * */
                continue;
            }
            AssetItem assetItem = new AssetItem();
            assetItem.setAsset(asset);
            assetItem.setAssetMode(AssetItem.ASSET_LOCAL);
            mTotalCaptionStyleList.add(assetItem);
        }
        AssetItem assetItem = new AssetItem();
        NvAsset asset = new NvAsset();
        asset.name = getString(R.string.makeup_null);
        assetItem.setImageRes(R.mipmap.captionstyle_no);
        assetItem.setAssetMode(AssetItem.ASSET_NONE);
        assetItem.setAsset(asset);
        mTotalCaptionStyleList.add(0, assetItem);
    }

    private void initPiecedTogetherCaption() {
        if (mRichWordList == null) {
            mRichWordList = new ArrayList<>();
        }
        mRichWordList.clear();
        //花字
        dealAssetData(NvAsset.ASSET_CAPTION_RICH_WORD, PATH_RICH_WORD, mRichWordList);
        if (mBubbleList == null) {
            mBubbleList = new ArrayList<>();
        }
        mBubbleList.clear();
        //气泡
        dealAssetData(NvAsset.ASSET_CAPTION_BUBBLE, PATH_BUBBLE, mBubbleList);
        if (mAnimationList == null) {
            mAnimationList = new ArrayList<>();
        }
        mAnimationList.clear();
        //组合动画
        dealAssetData(ASSET_CAPTION_ANIMATION, PATH_ANIMATION, mAnimationList);
        if (mMarchInAniList == null) {
            mMarchInAniList = new ArrayList<>();
        }
        mMarchInAniList.clear();
        //入场动画
        dealAssetData(ASSET_CAPTION_IN_ANIMATION, PATH_IN_ANIMATION, mMarchInAniList);
        if (mMarchOutAniList == null) {
            mMarchOutAniList = new ArrayList<>();
        }
        mMarchOutAniList.clear();
        //出场动画
        dealAssetData(ASSET_CAPTION_OUT_ANIMATION, PATH_OUT_ANIMATION, mMarchOutAniList);
    }

    /**
     * 处理asset资源数据
     *
     * @param assetType int 资源类型
     * @param assetPath String 资源路径
     * @param assetList List<AssetItem> 资源列表
     */
    private void dealAssetData(int assetType, String assetPath, List<AssetItem> assetList) {
        mAssetManager.searchLocalAssets(assetType);
        mAssetManager.searchReservedAssets(assetType, assetPath);
        ArrayList<NvAsset> usableAsset = getAssetsDataList(assetType);
        String jsonBundlePath = assetPath + "/info.json";
        boolean isAnimation = false;
        if (assetType == ASSET_CAPTION_OUT_ANIMATION || assetType == ASSET_CAPTION_IN_ANIMATION
                || assetType == ASSET_CAPTION_ANIMATION) {
            isAnimation = true;
        }
        ArrayList<ParseJsonFile.FxJsonFileInfo.JsonFileInfo> infoLists = ParseJsonFile.readBundleFxJsonFile(this, jsonBundlePath);
        if (infoLists != null) {
            for (ParseJsonFile.FxJsonFileInfo.JsonFileInfo jsonFileInfo : infoLists) {
                for (NvAsset asset : usableAsset) {
                    if (asset == null)
                        continue;
                    if (TextUtils.isEmpty(asset.uuid))
                        continue;
                    if (asset.isReserved && asset.uuid.equals(jsonFileInfo.getFxPackageId())) {
                        if (isZh(this)) {
                            asset.name = jsonFileInfo.getName_Zh();
                        } else {
                            asset.name = jsonFileInfo.getName();
                        }
                        if (!TextUtils.isEmpty(jsonFileInfo.getFitRatio())) {
                            try {
                                asset.aspectRatio = Integer.parseInt(jsonFileInfo.getFitRatio());
                            } catch (Exception e) {
                                Log.e(TAG, "Exception=" + e);
                            }
                        }
                        StringBuilder coverPath = new StringBuilder();
                        if (isAnimation) {
                            coverPath.append("asset://android_asset/");
                        } else {
                            coverPath.append("file:///android_asset/");
                        }
                        coverPath.append(assetPath);
                        coverPath.append("/");
                        coverPath.append(jsonFileInfo.getImageName());
                        asset.coverUrl = coverPath.toString();
                    }
                }
            }
        }
        if (assetList == null) {
            return;
        }
        AssetItem firstItem = new AssetItem();
        NvAsset firstAsset = new NvAsset();
        firstAsset.name = getString(R.string.timeline_fx_none);
        if (isAnimation) {
            firstItem.setImageRes(R.mipmap.square_clear);
        } else {
            firstItem.setImageRes(R.mipmap.captionstyle_no);
        }

        firstItem.setAssetMode(AssetItem.ASSET_NONE);
        firstItem.setAsset(firstAsset);
        assetList.add(firstItem);
        int ratio = TimelineData.instance().getMakeRatio();
        for (NvAsset asset : usableAsset) {
            if (asset == null)
                continue;
            if (TextUtils.isEmpty(asset.uuid))
                continue;
            if ((ratio & asset.aspectRatio) == 0) {
                /*
                 * 制作比例不适配，不加载
                 * Production proportions do not fit, do not load
                 * */
                continue;
            }
            AssetItem assetItem = new AssetItem();
            assetItem.setAsset(asset);
            assetItem.setAssetMode(AssetItem.ASSET_LOCAL);
            assetList.add(assetItem);
        }
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                seekTimeline(BackupData.instance().getCurSeekTimelinePos());
                if (mCurAddCaption == null) {
                    selectCaption();
                }

                int captionIndex = getCaptionIndex((int) mCurAddCaption.getZValue());
                reloadKeyFrame(captionIndex);

                mCaptionAssetFinish.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (mCurAddCaption != null) {
                            int alignVal = mCurAddCaption.getTextAlignment();
                            mVideoFragment.setAlignIndex(alignVal);

                            long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurAddCaption.getInPoint();
                            mCurAddCaption.setCurrentKeyFrameTime(duration);

                            mVideoFragment.setCurCaption(mCurAddCaption);
                            mVideoFragment.updateCaptionCoordinate(mCurAddCaption);
                            mVideoFragment.changeCaptionRectVisible();
//
                            mCurAddCaption.removeKeyframeAtTime(TRANS_X, duration);
                            mCurAddCaption.removeKeyframeAtTime(TRANS_Y, duration);
                            mCurAddCaption.removeKeyframeAtTime(SCALE_X, duration);
                            mCurAddCaption.removeKeyframeAtTime(SCALE_Y, duration);
                            mCurAddCaption.removeKeyframeAtTime(ROTATION_Z, duration);


                        }
                    }
                }, 100);
            }
        });
        mVideoFragment.setTimeline(mTimeline);
        /*
         * 设置字幕模式
         * Set caption mode
         * */
        mVideoFragment.setEditMode(Constants.EDIT_MODE_CAPTION);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        bundle.putBoolean("playBarVisible", false);
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.spaceLayout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
    }

    /*
     * 获取下载到手机路径下的素材，包括assets路径下自带的素材
     * Get the material downloaded to the mobile phone path,
     * including the material that comes with the assets path
     * */
    private ArrayList<NvAsset> getAssetsDataList(int assetType) {
        return mAssetManager.getUsableAssets(assetType, NvAsset.AspectRatio_All, 0);
    }

    /*
     * 获取字体数据列表
     * Get font data list
     * */
    private ArrayList<NvAsset> getFontAssetsDataList() {
        return mAssetManager.getRemoteAssetsWithPage(mFontType, NvAsset.AspectRatio_All, 0, 0, 10);
    }

    private void assetDataRequest() {
        mAssetManager.downloadRemoteAssetsInfo(mFontType, NvAsset.AspectRatio_All, 0, 0, 10);
        mAssetManager.setManagerlistener(new NvAssetManager.NvAssetManagerListener() {
            @Override
            public void onRemoteAssetsChanged(boolean hasNext) {
                m_handler.sendEmptyMessage(ASSET_LIST_REQUEST_SUCCESS);
            }

            @Override
            public void onGetRemoteAssetsFailed() {
                m_handler.sendEmptyMessage(ASSET_LIST_REQUEST_FAILED);
            }

            @Override
            public void onDownloadAssetProgress(String uuid, int progress) {
                sendHandleMsg(uuid, ASSET_DOWNLOAD_START_TIMER);
            }

            @Override
            public void onDonwloadAssetFailed(String uuid) {
                sendHandleMsg(uuid, ASSET_DOWNLOAD_FAILED);
            }

            @Override
            public void onDonwloadAssetSuccess(String uuid) {
                sendHandleMsg(uuid, ASSET_DOWNLOAD_SUCCESS);
            }

            @Override
            public void onFinishAssetPackageInstallation(String uuid) {

            }

            @Override
            public void onFinishAssetPackageUpgrading(String uuid) {

            }
        });
    }

    private void sendHandleMsg(String uuid, int what) {
        Message sendMsg = m_handler.obtainMessage();
        if (sendMsg == null)
            sendMsg = new Message();
        sendMsg.what = what;
        sendMsg.obj = uuid;
        m_handler.sendMessage(sendMsg);
    }

    private void applyCaptionFont(String fontPath) {
        if (mCurAddCaption != null) {
            mCurAddCaption.setFontByFilePath(fontPath);
            int index = getCaptionIndex(mCurCaptionZVal);
            if (index >= 0)
                mCaptionDataListClone.get(index).setCaptionFont(fontPath);
            updateCaption();
        }
    }

    private void initCaptionColorList() {
        for (int index = 0; index < CaptionColors.length; ++index) {
            mCaptionColorList.add(new CaptionColorInfo(CaptionColors[index], false));
        }
    }

    private void initCaptionOutlineColorList() {
        for (int index = 0; index < CaptionColors.length; ++index) {
            mCaptionOutlineColorList.add(new CaptionColorInfo(CaptionColors[index], false));
        }
        mCaptionOutlineColorList.add(0, new CaptionColorInfo("", false));
    }

    private void initCaptionBackgroundList() {
        for (int index = 0; index < CaptionColors.length; ++index) {
            mCaptionBackgroundList.add(new CaptionColorInfo(CaptionColors[index], false));
        }
        mCaptionBackgroundList.add(0, new CaptionColorInfo("", false));
    }

    private void initCaptionFontList() {
        mCaptionFontList.clear();
        AssetItem noneFontInfo = new AssetItem();
        noneFontInfo.setAsset(new NvAsset());
        noneFontInfo.setImageRes(R.mipmap.captionstyle_no);
        noneFontInfo.setAssetMode(AssetItem.ASSET_NONE);
        mCaptionFontList.add(noneFontInfo);

        ArrayList<NvAsset> usableAsset = getFontAssetsDataList();
        NvAsset item;
        for (int index = 0; index < usableAsset.size(); ++index) {
            item = usableAsset.get(index);
            // Log.d("lhz,1", "uid=" + item.uuid + "**url=" + item.coverUrl + "**localpath=" + item.localDirPath + "**bundPath=" + item.bundledLocalDirPath);
            AssetItem localFontInfo = new AssetItem();
            localFontInfo.setAsset(item);
            localFontInfo.setAssetMode(AssetItem.ASSET_LOCAL);
            mCaptionFontList.add(localFontInfo);
        }
        usableAsset = getAssetsDataList(mFontType);
        for (int index = 0; index < usableAsset.size(); ++index) {
            item = usableAsset.get(index);
            if (!hasAddFont(item.uuid)) {
                //Log.d("lhz,2", "uid=" + item.uuid + "**url=" + item.coverUrl + "**localpath=" + item.localDirPath + "**bundPath=" + item.bundledLocalDirPath);
                item.coverUrl = "file:///android_asset/font/" + item.uuid + ".png";
                AssetItem localFontInfo = new AssetItem();
                localFontInfo.setAsset(item);
                localFontInfo.setAssetMode(AssetItem.ASSET_BUILTIN);
                mCaptionFontList.add(localFontInfo);
            }
        }
    }

    private boolean hasAddFont(String fontUuid) {
        if (TextUtils.isEmpty(fontUuid)) {
            return true;
        }
        if (mCaptionFontList != null) {
            for (AssetItem item : mCaptionFontList) {
                if (fontUuid.equals(item.getAsset().uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    private CaptionRichWordFragment initRichWordFragment() {
        mRichWordFragment = new CaptionRichWordFragment();

        mRichWordFragment.setAssetInfoList(mRichWordList);
        if (mCurAddCaption != null) {
            mSelectedRichPos = getTargetPosition(mRichWordList, mCurAddCaption.getModularCaptionRendererPackageId());
        }
        mRichWordFragment.setCaptionStateListener(new CaptionRichWordFragment.OnCaptionStateListener() {
            @Override
            public void onFragmentLoadFinished() {
                mRichWordFragment.setSelectedPos(mSelectedRichPos);
            }

            @Override
            public void onLoadMore() {
                if (m_waitFlag)
                    return;
                mVideoFragment.stopEngine();
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.pieced_together_caption);
                bundle.putInt("assetType", NvAsset.ASSET_CAPTION_RICH_WORD);
                bundle.putInt("categoryId", CATEGORY_RICH_WORD);
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, CAPTION_RICH_WORD);
                m_waitFlag = true;
            }

            @Override
            public void onItemClick(int pos) {
                if (pos < 0 || pos >= mRichWordList.size())
                    return;
                applyAssemblyCaption(pos, NvAsset.ASSET_CAPTION_RICH_WORD, mRichWordList.get(pos).getAsset());
            }
        });
        return mRichWordFragment;
    }

    private CaptionBubbleFragment initBubbleFragment() {
        mBubbleFragment = new CaptionBubbleFragment();
        mBubbleFragment.setAssetInfoList(mBubbleList);
        if (mCurAddCaption != null) {
            mSelectedBubblePos = getTargetPosition(mBubbleList, mCurAddCaption.getModularCaptionContextPackageId());
        }
        mBubbleFragment.setCaptionStateListener(new CaptionBubbleFragment.OnCaptionStateListener() {
            @Override
            public void onFragmentLoadFinished() {
                mBubbleFragment.setSelectedPos(mSelectedBubblePos);
            }

            @Override
            public void onLoadMore() {
                if (m_waitFlag)
                    return;
                mVideoFragment.stopEngine();
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.more_caption_bubble);
                bundle.putInt("assetType", NvAsset.ASSET_CAPTION_BUBBLE);
                bundle.putInt("categoryId", CATEGORY_BUBBLE);
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, CAPTION_BUBBLE);
                m_waitFlag = true;
            }

            @Override
            public void onItemClick(int pos) {
                if (pos < 0 || pos >= mBubbleList.size())
                    return;
                applyAssemblyCaption(pos, NvAsset.ASSET_CAPTION_BUBBLE, mBubbleList.get(pos).getAsset());
            }
        });
        return mBubbleFragment;
    }

    private CaptionAnimationFragment initAnimationFragment() {
        mAnimationFragment = new CaptionAnimationFragment();
        mAnimationFragment.setAssetList(mAnimationList, mMarchInAniList, mMarchOutAniList);
        if (mCurAddCaption != null) {
            mSelectedAnimationPos = getTargetPosition(mAnimationList, mCurAddCaption.getModularCaptionAnimationPackageId());
            mSelectedInAnimationPos = getTargetPosition(mMarchInAniList, mCurAddCaption.getModularCaptionInAnimationPackageId());
            mSelectedOutAnimationPos = getTargetPosition(mMarchOutAniList, mCurAddCaption.getModularCaptionOutAnimationPackageId());
        }
        mAnimationFragment.setCaptionStateListener(new CaptionAnimationFragment.OnCaptionStateListener() {
            @Override
            public void onFragmentLoadFinished() {
                mAnimationFragment.setSelectedPos(mSelectedAnimationPos, mSelectedInAnimationPos, mSelectedOutAnimationPos);
                mAnimationFragment.checkSelectedTab();
            }

            @Override
            public void onLoadMore(int typ) {
                if (m_waitFlag)
                    return;
                mVideoFragment.stopEngine();
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreCaptionStyle);
                bundle.putInt("assetType", typ);
                int requestCode;
                if (typ == ASSET_CAPTION_IN_ANIMATION) {
                    bundle.putInt("categoryId", CATEGORY_IN_ANIMATION);
                    requestCode = CAPTION_IN_ANIMATION;
                } else if (typ == ASSET_CAPTION_OUT_ANIMATION) {
                    bundle.putInt("categoryId", CATEGORY_OUT_ANIMATION);
                    requestCode = CAPTION_OUT_ANIMATION;
                } else {
                    bundle.putInt("categoryId", CATEGORY_ANIMATION);
                    requestCode = CAPTION_ANIMATION;
                }
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, requestCode);
                m_waitFlag = true;
            }

            @Override
            public void onItemClick(int pos, int type) {
                if (pos < 0)
                    return;
                if (type == ASSET_CAPTION_ANIMATION && pos < mAnimationList.size()) {
                    applyAssemblyCaption(pos, ASSET_CAPTION_ANIMATION, mAnimationList.get(pos).getAsset());
                } else if (type == ASSET_CAPTION_OUT_ANIMATION && pos < mMarchOutAniList.size()) {
                    applyAssemblyCaption(pos, ASSET_CAPTION_OUT_ANIMATION, mMarchOutAniList.get(pos).getAsset());
                } else if (pos < mMarchInAniList.size()) {
                    applyAssemblyCaption(pos, ASSET_CAPTION_IN_ANIMATION, mMarchInAniList.get(pos).getAsset());
                }
                // displaySeekBar(pos > 0);
                displayAnimationProgress(pos > 0, type);

            }
        });
        return mAnimationFragment;
    }

    /**
     * 应用拼装字幕
     */
    private void applyAssemblyCaption(int pos, int type, NvAsset asset) {
        if (mCurAddCaption == null || asset == null)
            return;
        isCaptionStyleItemClick = true;
        long startTime = mCurAddCaption.getInPoint();
        long endTime = mCurAddCaption.getOutPoint();
        mVideoFragment.setDrawRectVisible(View.GONE);
        int index = getCaptionIndex(mCurCaptionZVal);
        CaptionInfo captionInfo = mCaptionDataListClone.get(index);
        mSelectedType = type;
        //Log.d("lhz", "type=" + type + "**asset.uuid=" + asset.uuid + "**pos=" + pos);
        switch (type) {
            case NvAsset.ASSET_CAPTION_RICH_WORD:
                /*if (mSelectedRichPos == pos) {
                    mVideoFragment.playVideo(startTime, endTime);
                    return;
                }*/
                mSelectedRichPos = pos;
                mSelectedAnimationPos = 0;
                mCurAddCaption.applyModularCaptionRenderer(asset.uuid);
                if (captionInfo != null) {
                    captionInfo.setRichWordUuid(asset.uuid);
                }
                break;
            case NvAsset.ASSET_CAPTION_BUBBLE:
               /* if (mSelectedBubblePos == pos) {
                    mVideoFragment.playVideo(startTime, endTime);
                    return;
                }*/
                mSelectedBubblePos = pos;
                mSelectedAnimationPos = 0;
                mCurAddCaption.applyModularCaptionContext(asset.uuid);
                if (captionInfo != null) {
                    captionInfo.setBubbleUuid(asset.uuid);
                }
                break;
            case ASSET_CAPTION_ANIMATION:
                mSelectedAnimationPos = pos;
                mSelectedInAnimationPos = 0;
                mSelectedOutAnimationPos = 0;
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                    mCurAddCaption.applyModularCaptionOutAnimation("");
                    //恢复默认值
                    mCurAddCaption.setModularCaptionOutAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
                }
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId())) {
                    mCurAddCaption.applyModularCaptionInAnimation("");
                    mCurAddCaption.setModularCaptionInAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
                }
                if (mCurAddCaption.getModularCaptionAnimationPeroid() == 0) {
                    //如果切换的时候，设置的动画时长是0，则恢复默认，产品需求。
                    mCurAddCaption.setModularCaptionAnimationPeroid(ANIMATION_DEFAULT_DURATION);
                }
                mCurAddCaption.applyModularCaptionAnimation(asset.uuid);
                if (captionInfo != null) {
                    captionInfo.setCombinationAnimationUuid(asset.uuid);
                    captionInfo.setCombinationAnimationDuration(mCurAddCaption.getModularCaptionAnimationPeroid());
                    captionInfo.setMarchOutAnimationUuid("");
                    captionInfo.setMarchInAnimationUuid("");
                }
                break;
            case ASSET_CAPTION_IN_ANIMATION:
                mSelectedInAnimationPos = pos;
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionAnimationPackageId())) {
                    mCurAddCaption.applyModularCaptionAnimation("");
                    mCurAddCaption.setModularCaptionAnimationPeroid(ANIMATION_DEFAULT_DURATION);
                }
                if (mCurAddCaption.getModularCaptionInAnimationDuration() == 0) {
                    //如果切换的时候，设置的动画时长是0，则恢复默认,产品需求。
                    mCurAddCaption.setModularCaptionInAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
                }
                mCurAddCaption.applyModularCaptionInAnimation(asset.uuid);
                if (captionInfo != null) {
                    captionInfo.setCombinationAnimationUuid("");
                    captionInfo.setCombinationAnimationDuration(0);
                    captionInfo.setMarchInAnimationUuid(asset.uuid);
                    captionInfo.setMarchInAnimationDuration(mCurAddCaption.getModularCaptionInAnimationDuration());
                    if (TextUtils.isEmpty(asset.uuid)) {
                        captionInfo.setMarchInAnimationDuration(0);
                    }
                }
                break;
            case ASSET_CAPTION_OUT_ANIMATION:
                mSelectedOutAnimationPos = pos;
                if (!TextUtils.isEmpty(mCurAddCaption.getModularCaptionAnimationPackageId())) {
                    mCurAddCaption.applyModularCaptionAnimation("");
                    mCurAddCaption.setModularCaptionAnimationPeroid(ANIMATION_DEFAULT_DURATION);
                }
                if (mCurAddCaption.getModularCaptionOutAnimationDuration() == 0) {
                    //如果切换的时候，设置的动画时长是0，则恢复默认，产品需求。
                    mCurAddCaption.setModularCaptionOutAnimationDuration(IN_OUT_ANIMATION_DEFAULT_DURATION);
                }
                mCurAddCaption.applyModularCaptionOutAnimation(asset.uuid);
                if (captionInfo != null) {
                    captionInfo.setCombinationAnimationUuid("");
                    captionInfo.setCombinationAnimationDuration(0);
                    captionInfo.setMarchOutAnimationUuid(asset.uuid);
                    captionInfo.setMarchOutAnimationDuration(mCurAddCaption.getModularCaptionOutAnimationDuration());
                    if (TextUtils.isEmpty(asset.uuid)) {
                        captionInfo.setMarchOutAnimationDuration(0);
                    }
                }
                break;
            default:
                break;
        }
        mVideoFragment.playVideo(startTime, endTime);
        float captionSize = mCurAddCaption.getFontSize();
        float scaleX = mCurAddCaption.getScaleX();
        float scaleY = mCurAddCaption.getScaleY();
        PointF pointF = mCurAddCaption.getCaptionTranslation();
        float rotateAngle = mCurAddCaption.getRotationZ();
        if (captionInfo != null) {
            captionInfo.setTranslation(pointF);
            captionInfo.setCaptionSize(captionSize);
            captionInfo.setScaleFactorX(scaleX);
            captionInfo.setScaleFactorY(scaleY);
            captionInfo.setRotation(rotateAngle);
        }

    }

    private void displaySeekBar(boolean visible) {
        if (visible) {
            if (mSeekBar.getVisibility() != View.VISIBLE) {
                mSeekBar.setVisibility(View.VISIBLE);
            }
        } else {
            if (mSeekBar.getVisibility() == View.VISIBLE) {
                mSeekBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 展示动画的进度
     * 注意:出入动画和组合动画互斥
     */
    private void displayAnimationProgress(boolean visible, int type) {
        if (visible) {
            if (mSeekBar.getVisibility() != View.VISIBLE) {
                mSeekBar.setVisibility(View.VISIBLE);
            }
        } else {
            if (TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId()) &&
                    TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                mSeekBar.setVisibility(View.INVISIBLE);
                return;
            }
        }

        if (type == ASSET_CAPTION_IN_ANIMATION) {
            if (TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId())) {
                mSeekBar.setLeftMoveIcon(0);
            } else {
                if (hasCombineAnimation) {
                    mSeekBar.reset();
                    mSeekBar.setMoveIconSize(20, 35);
                }
                if (TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                    mSeekBar.setRightMoveIcon(0);
                }
                int duration = mCurAddCaption.getModularCaptionInAnimationDuration();
                //Log.d("lhz", "duration=" + duration + "**type=" + type);
                mSeekBar.setMoveIconLowPadding(10);
                mSeekBar.setLeftMoveIcon(R.mipmap.bar_left);
                mSeekBar.setLeftProgress(duration);
                hasCombineAnimation = false;
            }
        } else if (type == ASSET_CAPTION_OUT_ANIMATION) {
            if (TextUtils.isEmpty(mCurAddCaption.getModularCaptionOutAnimationPackageId())) {
                mSeekBar.setRightMoveIcon(0);
            } else {
                if (hasCombineAnimation) {
                    mSeekBar.reset();
                    mSeekBar.setMoveIconSize(20, 35);
                }
                if (TextUtils.isEmpty(mCurAddCaption.getModularCaptionInAnimationPackageId())) {
                    mSeekBar.setLeftMoveIcon(0);
                }
                int duration = mCurAddCaption.getModularCaptionOutAnimationDuration();
                mSeekBar.setMoveIconLowPadding(10);
                mSeekBar.setRightMoveIcon(R.mipmap.bar_right);
                mSeekBar.setRightProgress(duration);
                hasCombineAnimation = false;
                //  Log.d("lhz", "duration=" + duration + "**type=" + type);
            }
        } else {
            if (TextUtils.isEmpty(mCurAddCaption.getModularCaptionAnimationPackageId())) {
                mSeekBar.reset();
                mSeekBar.setVisibility(View.INVISIBLE);
                return;
            }
            int duration = mCurAddCaption.getModularCaptionAnimationPeroid();
            mSeekBar.reset();
            mSeekBar.setMoveIconSize(20, 20);
            mSeekBar.setLeftMoveIcon(R.mipmap.round_white);
            mSeekBar.setLeftProgress(duration);
            hasCombineAnimation = true;
            //Log.d("lhz", "duration=" + duration + "**type=" + type);
        }
    }

    private CaptionStyleFragment initCaptionStyleFragment() {
        CaptionStyleFragment captionStyleFragment = new CaptionStyleFragment();
        captionStyleFragment.setAssetInfolist(mTotalCaptionStyleList);
        captionStyleFragment.setCaptionStyleListener(new CaptionStyleFragment.OnCaptionStyleListener() {
            @Override
            public void onFragmentLoadFinished() {
                mCaptionStyleFragment.applyToAllCaption(bIsStyleUuidApplyToAll);
                mSelectedStylePos = getCaptionStyleSelectedIndex();
                mCaptionStyleFragment.setSelectedPos(mSelectedStylePos);
                mCaptionStyleFragment.notifyDataSetChanged();
            }

            @Override
            public void OnDownloadCaptionStyle() {
                if (m_waitFlag)
                    return;
                mVideoFragment.stopEngine();
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreCaptionStyle);
                bundle.putInt("assetType", NvAsset.ASSET_CAPTION_STYLE);
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, CAPTIONSTYLEREQUESTLIST);
                m_waitFlag = true;
            }

            @Override
            public void onItemClick(int pos) {
                if (pos < 0 || pos >= mTotalCaptionStyleList.size())
                    return;
                if (mCurAddCaption == null)
                    return;
                isCaptionStyleItemClick = true;
                long startTime = mCurAddCaption.getInPoint();
                long endTime = mCurAddCaption.getOutPoint();
                mVideoFragment.setDrawRectVisible(View.GONE);
                if (mSelectedStylePos == pos) {
                    mVideoFragment.playVideo(startTime, endTime);
                    return;
                }
                NvAsset asset = mTotalCaptionStyleList.get(pos).getAsset();
                if (asset == null)
                    return;
                mSelectedStylePos = pos;
                /*
                 * 应用字幕样式
                 * Apply caption style
                 * */
                mCurAddCaption.applyCaptionStyle(asset.uuid);

//                long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurAddCaption.getInPoint();
//                mCurAddCaption.setCurrentKeyFrameTime(duration);

                float captionSize = mCurAddCaption.getFontSize();
                float scaleX = mCurAddCaption.getScaleX();
                float scaleY = mCurAddCaption.getScaleY();
                PointF pointF = mCurAddCaption.getCaptionTranslation();
                float rotateAngle = mCurAddCaption.getRotationZ();
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setCaptionStyleUuid(asset.uuid);
                    mCaptionDataListClone.get(index).setTranslation(pointF);
                    mCaptionDataListClone.get(index).setCaptionSize(captionSize);
                    mCaptionDataListClone.get(index).setScaleFactorX(scaleX);
                    mCaptionDataListClone.get(index).setScaleFactorY(scaleY);
                    mCaptionDataListClone.get(index).setRotation(rotateAngle);
                }

                reloadKeyFrame(index);

                mVideoFragment.playVideo(startTime, endTime);

            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
                bIsStyleUuidApplyToAll = isApplyToAll;
            }
        });
        return captionStyleFragment;
    }

    private void reloadKeyFrame(int index) {
        Map<Long, KeyFrameInfo> keyFrameInfoMap = mCaptionDataListClone.get(index).getKeyFrameInfo();
        Set<Long> keySet = keyFrameInfoMap.keySet();
        for (long currentTime : keySet) {
            KeyFrameInfo keyFrameInfo = keyFrameInfoMap.get(currentTime);
            long duration = currentTime - mCurAddCaption.getInPoint();
            mCurAddCaption.removeKeyframeAtTime(TRANS_X, duration);
            mCurAddCaption.removeKeyframeAtTime(TRANS_Y, duration);
            mCurAddCaption.removeKeyframeAtTime(SCALE_X, duration);
            mCurAddCaption.removeKeyframeAtTime(SCALE_Y, duration);
            mCurAddCaption.removeKeyframeAtTime(ROTATION_Z, duration);

            mCurAddCaption.setCurrentKeyFrameTime(duration);
            mCurAddCaption.setScaleX(keyFrameInfo.getScaleX());
            mCurAddCaption.setScaleY(keyFrameInfo.getScaleY());
            mCurAddCaption.setCaptionTranslation(keyFrameInfo.getTranslation());
            mCurAddCaption.setRotationZ(keyFrameInfo.getRotationZ());

        }
    }

    private CaptionColorFragment
    initCaptionColorFragment() {
        CaptionColorFragment captionColorFragment = new CaptionColorFragment();
        captionColorFragment.setCaptionColorInfolist(mCaptionColorList);
        captionColorFragment.setCaptionColorListener(new CaptionColorFragment.OnCaptionColorListener() {
            @Override
            public void onFragmentLoadFinished() {
                mCaptionColorFragment.applyToAllCaption(bIsCaptionColorApplyToAll);
                mSelectedColorPos = getCaptionColorSelectedIndex();
                if (mSelectedColorPos >= 0) {
                    mCaptionColorList.get(mSelectedColorPos).mSelected = true;
                    mCaptionColorFragment.setCaptionColorInfolist(mCaptionColorList);
                    mCaptionColorFragment.notifyDataSetChanged();
                }
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionColorOpacityValue = mCaptionDataListClone.get(index).getCaptionColorAlpha();
                    mCaptionColorFragment.updateCaptionOpacityValue(mCaptionColorOpacityValue);
                }
            }

            @Override
            public void onCaptionColor(int pos) {
                if (pos < 0 || pos > mCaptionColorList.size())
                    return;
                if (mCurAddCaption == null)
                    return;
                if (mSelectedColorPos == pos)
                    return;
                if (mSelectedColorPos >= 0)
                    mCaptionColorList.get(mSelectedColorPos).mSelected = false;
                mCaptionColorList.get(pos).mSelected = true;
                mCaptionColorFragment.notifyDataSetChanged();
                mSelectedColorPos = pos;
                mCaptionColorOpacityValue = 100;
                mCaptionColorFragment.updateCaptionOpacityValue(mCaptionColorOpacityValue);
                /*
                 * 设置字体颜色
                 * Set font color
                 * */
                NvsColor color = ColorUtil.colorStringtoNvsColor(mCaptionColorList.get(pos).mColorValue);
                mCurAddCaption.setTextColor(color);
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedColorFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setCaptionColor(mCaptionColorList.get(pos).mColorValue);
                }
                updateCaption();
            }

            @Override
            public void onCaptionOpacity(int progress) {
                if (mCurAddCaption == null)
                    return;
                /*
                 * 设置字体的不透明度
                 * Set the opacity of the font
                 * */
                NvsColor curColor = mCurAddCaption.getTextColor();
                curColor.a = progress / 100.0f;
                String strColor = ColorUtil.nvsColorToHexString(curColor);
                mCurAddCaption.setTextColor(curColor);
                mCaptionColorOpacityValue = progress;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedColorFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setCaptionColor(strColor);
                    mCaptionDataListClone.get(index).setCaptionColorAlpha(progress);
                }

                updateCaption();
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
                bIsCaptionColorApplyToAll = isApplyToAll;
            }
        });
        return captionColorFragment;
    }


    private CaptionOutlineFragment initCaptionOutlineFragment() {
        CaptionOutlineFragment captionOutlineFragment = new CaptionOutlineFragment();
        captionOutlineFragment.setCaptionOutlineInfolist(mCaptionOutlineColorList);
        captionOutlineFragment.setCaptionOutlineListener(new CaptionOutlineFragment.OnCaptionOutlineListener() {
            @Override
            public void onFragmentLoadFinished() {
                mCaptionOutlineFragment.applyToAllCaption(bIsOutlineApplyToAll);
                mSelectedOutlinePos = getOutlineColorSelectedIndex();
                mCaptionOutlineColorList.get(mSelectedOutlinePos).mSelected = true;
                mCaptionOutlineFragment.setCaptionOutlineInfolist(mCaptionOutlineColorList);
                mCaptionOutlineFragment.notifyDataSetChanged();
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    boolean isDrawOutline = mCaptionDataListClone.get(index).isHasOutline();
                    if (isDrawOutline) {
                        mCaptionOutlineWidthValue = (int) mCaptionDataListClone.get(index).getOutlineWidth();
                        mCaptionOutlineOpacityValue = mCaptionDataListClone.get(index).getOutlineColorAlpha();
                    }
                    mCaptionOutlineFragment.updateCaptionOutlineWidthValue(mCaptionOutlineWidthValue);
                    mCaptionOutlineFragment.updateCaptionOutlineOpacityValue(mCaptionOutlineOpacityValue);
                }
            }

            @Override
            public void onCaptionOutlineColor(int pos) {
                if (pos < 0 || pos > mCaptionOutlineColorList.size())
                    return;
                if (mCurAddCaption == null)
                    return;
                if (mSelectedOutlinePos == pos)
                    return;
                mCaptionOutlineColorList.get(mSelectedOutlinePos).mSelected = false;
                mCaptionOutlineColorList.get(pos).mSelected = true;
                mCaptionOutlineFragment.notifyDataSetChanged();
                mSelectedOutlinePos = pos;

                mCaptionOutlineOpacityValue = 100;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (pos == 0) {
                    mCurAddCaption.setDrawOutline(false);
                    mCaptionOutlineWidthValue = 0;
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setUsedOutlineFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setHasOutline(false);
                    }

                } else {
                    mCaptionOutlineWidthValue = 8;
                    /*
                     * 设置字幕描边标识
                     * Set caption stroke flag
                     * */
                    mCurAddCaption.setDrawOutline(true);
                    /*
                     * 设置描边颜色
                     * Set outline color
                     * */
                    NvsColor color = ColorUtil.colorStringtoNvsColor(mCaptionOutlineColorList.get(pos).mColorValue);
                    mCurAddCaption.setOutlineColor(color);
                    /*
                     * 字幕描边宽度
                     * Caption stroke width
                     * */
                    mCurAddCaption.setOutlineWidth(mCaptionOutlineWidthValue);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setUsedOutlineFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setHasOutline(true);
                        mCaptionDataListClone.get(index).setOutlineColor(mCaptionOutlineColorList.get(pos).mColorValue);
                        mCaptionDataListClone.get(index).setOutlineWidth(mCaptionOutlineWidthValue);
                        mCaptionDataListClone.get(index).setOutlineColorAlpha(mCaptionOutlineOpacityValue);
                    }
                }
                mCaptionOutlineFragment.updateCaptionOutlineWidthValue(mCaptionOutlineWidthValue);
                mCaptionOutlineFragment.updateCaptionOutlineOpacityValue(mCaptionOutlineOpacityValue);
                updateCaption();
            }

            @Override
            public void onCaptionOutlineWidth(int width) {
                if (mCurAddCaption == null)
                    return;
                if (mSelectedOutlinePos == 0)
                    return;
                /*
                 * 字幕描边宽度
                 * Caption stroke width
                 * */
                mCurAddCaption.setOutlineWidth(width);
                mCaptionOutlineWidthValue = width;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0)
                    mCaptionDataListClone.get(index).setOutlineWidth(mCaptionOutlineWidthValue);
                updateCaption();
            }

            @Override
            public void onCaptionOutlineOpacity(int opacity) {
                if (mCurAddCaption == null)
                    return;
                if (mSelectedOutlinePos == 0)
                    return;
                /*
                 * 设置字幕描边的不透明度
                 * Set the opacity of the caption stroke
                 * */
                NvsColor curColor = mCurAddCaption.getOutlineColor();
                curColor.a = opacity / 100.0f;
                mCurAddCaption.setOutlineColor(curColor);
                mCaptionOutlineOpacityValue = opacity;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0)
                    mCaptionDataListClone.get(index).setOutlineColorAlpha(opacity);
                updateCaption();
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
                bIsOutlineApplyToAll = isApplyToAll;
            }
        });
        return captionOutlineFragment;
    }

    private CaptionBackgroundFragment initCaptionBackgroundFragment() {
        CaptionBackgroundFragment captionBackgroundFragment = new CaptionBackgroundFragment();
        captionBackgroundFragment.setCaptionBackgroundInfolist(mCaptionBackgroundList);
        captionBackgroundFragment.setCaptionBackgroundListener(new CaptionBackgroundFragment.OnCaptionBackgroundListener() {
            @Override
            public void onFragmentLoadFinished() {

                mCaptionBackgroundFragment.applyToAllCaption(bIsCaptionBackgroundApplyToAll);
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionBackgroundOpacityValue = mCaptionDataListClone.get(index).getCaptionBackgroundAlpha();
                    mCaptionBackgroundFragment.updateCaptionOpacityValue(mCaptionBackgroundOpacityValue);
                }
                mSelectedBackgroundPos = getBackgroundSelectedIndex();
                if (mSelectedBackgroundPos >= 0) {
                    mCaptionBackgroundList.get(mSelectedBackgroundPos).mSelected = true;
                    NvsColor curColor = mCurAddCaption.getBackgroundColor();
                    String strColor = ColorUtil.nvsColorToHexString(curColor);
                    mCaptionBackgroundList.get(mSelectedBackgroundPos).mColorValue = strColor;
                    mCaptionBackgroundFragment.setCaptionBackgroundInfolist(mCaptionBackgroundList);
                    mCaptionBackgroundFragment.notifyDataSetChanged();
                }

                //设置背景圆角的最大值和当前的值
                RectF rectF = mCurAddCaption.getTextBoundingRect();
                float height = Math.abs(rectF.top - rectF.bottom);
                float width = Math.abs(rectF.right - rectF.left);
                float maxRadius = height >= width ? width / 2 : height / 2;
                //设置圆角的值
                mCaptionBackgroundFragment.initCaptionMaxCorner((int) (maxRadius));
                if (mCurAddCaption != null) {
                    mCaptionBackgroundCornerValue = mCurAddCaption.getBackgroundRadius();
                    mCaptionBackgroundFragment.updateCaptionCornerValue((int) mCaptionBackgroundCornerValue);
                }
            }

            @Override
            public void onCaptionColor(int pos) {
                if (pos < 0 || pos > mCaptionBackgroundList.size())

                    return;
                if (mCurAddCaption == null)
                    return;
                if (mSelectedBackgroundPos == pos)
                    return;
                //刷新页面
                mCaptionBackgroundList.get(mSelectedBackgroundPos).mSelected = false;
                mCaptionBackgroundList.get(pos).mSelected = true;
                mCaptionBackgroundFragment.notifyDataSetChanged();
                mSelectedBackgroundPos = pos;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (pos == 0) {
                    //设置无背景色
                    // 黑色透明#00000000
                    String noColor = "#00000000";
                    NvsColor color = ColorUtil.colorStringtoNvsColor(noColor);
                    mCurAddCaption.setBackgroundColor(color);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setUsedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setCaptionBackground(noColor);
                    }
                } else {
                    //背景色透明度
                    mCaptionBackgroundOpacityValue = 100;
                    /*
                     * 设置背景色
                     * Set font color
                     * */
                    NvsColor color = ColorUtil.colorStringtoNvsColor(mCaptionBackgroundList.get(pos).mColorValue);
                    mCurAddCaption.setBackgroundColor(color);
                    if (index >= 0) {
                        mCaptionDataListClone.get(index).setUsedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                        mCaptionDataListClone.get(index).setCaptionBackground(mCaptionBackgroundList.get(pos).mColorValue);
                    }
                }
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setCaptionBackground(mCaptionBackgroundList.get(pos).mColorValue);
                }
                mCaptionBackgroundFragment.updateCaptionOpacityValue(mCaptionBackgroundOpacityValue);
                mCaptionBackgroundCornerValue = mCurAddCaption.getBackgroundRadius();
                mCaptionBackgroundFragment.updateCaptionCornerValue((int) mCaptionBackgroundCornerValue);
                updateCaption();

            }

            @Override
            public void onCaptionOpacity(int progress) {
                if (mCurAddCaption == null)
                    return;
                /*
                 * 设置背景的不透明度
                 * Set the opacity of the font
                 * */
                NvsColor curColor = mCurAddCaption.getBackgroundColor();
                curColor.a = progress / 100.0f;
                String strColor = ColorUtil.nvsColorToHexString(curColor);
                mCurAddCaption.setBackgroundColor(curColor);
                mCaptionBackgroundOpacityValue = progress;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedBackgroundFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setCaptionBackground(strColor);
                    mCaptionDataListClone.get(index).setCaptionBackgroundAlpha(progress);
                }

                updateCaption();
            }

            @Override
            public void onCaptionCorner(int progress) {
                if (mCurAddCaption == null)
                    return;
                /*
                 * 设置背景圆角
                 * */
                //设置背景圆角的最大值和当前的值
                //设置背景圆角的最大值和当前的值
                RectF rectF = mCurAddCaption.getTextBoundingRect();
                float height = Math.abs(rectF.top - rectF.bottom);
                float width = Math.abs(rectF.right - rectF.left);
                float maxRadius = height >= width ? width / 2 : height / 2;
                //设置圆角的最大值
                //mCaptionBackgroundFragment.initCaptionMaxCorner((int) (maxRadius));
                mCaptionBackgroundCornerValue = progress;
                mCurAddCaption.setBackgroundRadius(mCaptionBackgroundCornerValue);
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setCaptionBackgroundRadius(mCaptionBackgroundCornerValue);
                    mCaptionDataListClone.get(index).setUsedBackgroundRadiusFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                }

                updateCaption();
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
                bIsCaptionBackgroundApplyToAll = isApplyToAll;
            }
        });
        return captionBackgroundFragment;
    }

    private CaptionFontFragment initCaptionFontFragment() {
        CaptionFontFragment captionFontFragment = new CaptionFontFragment();
        captionFontFragment.setCaptionFontListener(new CaptionFontFragment.OnCaptionFontListener() {
            @Override
            public void onFragmentLoadFinished() {
                updateFontList();
                mCaptionFontFragment.applyToAllCaption(bIsFontApplyToAll);
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionFontFragment.updateBoldButton(mCaptionDataListClone.get(index).isBold());
                    mCaptionFontFragment.updateItalicButton(mCaptionDataListClone.get(index).isItalic());
                    mCaptionFontFragment.updateShadowButton(mCaptionDataListClone.get(index).isShadow());
                }
            }

            @Override
            public void onItemClick(int pos) {
                if (pos < 0 || pos >= mCaptionFontList.size())
                    return;
                mSelectedFontPos = pos;
                NvAsset asset = mCaptionFontList.get(pos).getAsset();
                if (asset == null)
                    return;
                if (!TextUtils.isEmpty(asset.localDirPath)) {
                    applyCaptionFont(asset.localDirPath);
                } else if (!TextUtils.isEmpty(asset.bundledLocalDirPath)) {
                    applyCaptionFont(asset.bundledLocalDirPath);
                } else if (pos == 0) {
                    applyCaptionFont("");
                }

            }

            @Override
            public void onBold() {
                if (mCurAddCaption == null)
                    return;
                boolean isBold = mCurAddCaption.getBold();
                isBold = !isBold;
                /*
                 * 字幕加粗
                 * Caption bold
                 * */
                mCurAddCaption.setBold(isBold);
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedIsBoldFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setBold(isBold);
                }
                updateCaption();
            }

            @Override
            public void onItalic() {
                if (mCurAddCaption == null)
                    return;
                boolean isItalic = mCurAddCaption.getItalic();
                isItalic = !isItalic;
                /*
                 * 字幕斜体
                 * Caption italics
                 * */
                mCurAddCaption.setItalic(isItalic);
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedIsItalicFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setItalic(isItalic);
                }
                updateCaption();
            }

            @Override
            public void onShadow() {
                if (mCurAddCaption == null)
                    return;
                boolean isShadow = mCurAddCaption.getDrawShadow();
                isShadow = !isShadow;
                if (isShadow) {
                    PointF point = new PointF(7, -7);
                    NvsColor shadowColor = new NvsColor(0, 0, 0, 0.5f);
                    /*
                     * 设置字幕阴影偏移量
                     * Set the caption shadow offset
                     * */
                    mCurAddCaption.setShadowOffset(point);
                    /*
                     * 设置字幕阴影颜色
                     * Set the caption shadow color
                     * */
                    mCurAddCaption.setShadowColor(shadowColor);
                }
                mCurAddCaption.setDrawShadow(isShadow);

                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedShadowFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setShadow(isShadow);
                }
                updateCaption();
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
                bIsFontApplyToAll = isApplyToAll;
            }

            @Override
            public void onFontDownload(int pos) {
                int count = mCaptionFontList.size();
                if (pos <= 0 || pos >= count)
                    return;
                if (mFontCurClickPos == pos) {
                    /*
                     * 重复点击，不作处理；防止素材多次下载
                     * Double click without processing; prevent material from downloading multiple times
                     * */
                    return;
                }
                mFontCurClickPos = pos;
                mAssetManager.downloadAsset(mFontType, mCaptionFontList.get(pos).getAsset().uuid);
            }
        });
        return captionFontFragment;
    }

    private CaptionSizeFragment initCaptionSizeFragment() {
        CaptionSizeFragment captionSizeFragment = new CaptionSizeFragment();
        captionSizeFragment.setCaptionSizeListener(new CaptionSizeFragment.OnCaptionSizeListener() {
            @Override
            public void onFragmentLoadFinished() {
                mCaptionSizeFragment.applyToAllCaption(bIsSizeApplyToAll);
                int index = getCaptionIndex(mCurCaptionZVal);
//                if (index >= 0) {
//                    int captionSizeVal = (int) mCaptionDataListClone.get(index).getCaptionSize();
//                    if (captionSizeVal >= 0)
//                        mCaptionSizeValue = captionSizeVal;
//                    mCaptionSizeFragment.updateCaptionSizeValue(mCaptionSizeValue);
//                }
            }

            @Override
            public void OnCaptionSize(int size) {
                if (mCurAddCaption == null)
                    return;
                mCurAddCaption.setFontSize(size);
//                mCaptionSizeValue = size;
//                int index = getCaptionIndex(mCurCaptionZVal);
//                if (index >= 0)
//                    mCaptionDataListClone.get(index).setCaptionSize(size);
                updateCaption();
            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
                bIsSizeApplyToAll = isApplyToAll;
            }
        });
        return captionSizeFragment;
    }

    private CaptionLetterSpacingFragment initCaptionLetterSpacingFragment() {
        CaptionLetterSpacingFragment captionLetterSpacingFragment = new CaptionLetterSpacingFragment();
        captionLetterSpacingFragment.setCaptionLetterSpacingListener(new CaptionLetterSpacingFragment.OnCaptionLetterSpacingListener() {
            @Override
            public void onFragmentLoadFinished() {
                mCaptionLetterSpacingFragment.applyToAllCaption(bIsLetterSpacingApplyToAll);
                if (mCurAddCaption == null) {
                    selectCaption();
                }

                if (mCurAddCaption != null) {

                    //设置字间距
                    float letterSpaceing = mCurAddCaption.getLetterSpacing();
                    if (letterSpaceing == CAPTION_STANDARD_SPACING) {
                        mCaptionLetterSpacingFragment.setSelectedStandard(CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER, true);
                    } else if (letterSpaceing == CAPTION_MORE_LARGE_SPACING) {
                        mCaptionLetterSpacingFragment.setSelectedMore(CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER, true);
                    } else if (letterSpaceing == CAPTION_LARGE_SPACEING) {
                        mCaptionLetterSpacingFragment.setSelectedLarge(CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER, true);
                    } else if (letterSpaceing == CAPTION_SMALL_SPACING) {
                        mCaptionLetterSpacingFragment.setSelectedSmall(CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER, true);
                    } else {
                        mCaptionLetterSpacingFragment.setSelectedStandard(CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER, true);
                    }
                    //设置行间距
                    float lineSpacing = mCurAddCaption.getLineSpacing();
                    if (lineSpacing == CAPTION_SMALL_LINE_SPACING) {
                        mCaptionLetterSpacingFragment.setSelectedSmall(CaptionLetterSpacingFragment.CAPTION_SPACING_LINE, true);
                    } else if (lineSpacing == CAPTION_STANDARD_LINE_SPACING) {
                        mCaptionLetterSpacingFragment.setSelectedStandard(CaptionLetterSpacingFragment.CAPTION_SPACING_LINE, true);
                    } else if (lineSpacing == CAPTION_MORE_LARGE_LINE_SPACING) {
                        mCaptionLetterSpacingFragment.setSelectedMore(CaptionLetterSpacingFragment.CAPTION_SPACING_LINE, true);
                    } else if (lineSpacing == CAPTION_LARGE_LINE_SPACEING) {
                        mCaptionLetterSpacingFragment.setSelectedLarge(CaptionLetterSpacingFragment.CAPTION_SPACING_LINE, true);
                    } else {
                        mCaptionLetterSpacingFragment.setSelectedStandard(CaptionLetterSpacingFragment.CAPTION_SPACING_LINE, true);
                    }
                }
            }

            @Override
            public void onSmallBtnClicked(int spacingMode) {
                if (mCurAddCaption == null) {
                    return;
                }
                if (spacingMode == CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER) {
                    mCurAddCaption.setLetterSpacing(CAPTION_SMALL_SPACING);
                    updateCaption();
                    int index = getCaptionIndex(mCurCaptionZVal);
                    if (index >= 0) {
                        CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                        if (curCaptionInfo != null) {
                            curCaptionInfo.setUsedLetterSpacingFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            curCaptionInfo.setLetterSpacing(CAPTION_SMALL_SPACING);
                        }
                    }
                } else {
                    //设置行间距
                    mCurAddCaption.setLineSpacing(CAPTION_SMALL_LINE_SPACING);
                    updateCaption();
                    int index = getCaptionIndex(mCurCaptionZVal);
                    if (index >= 0) {
                        CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                        if (curCaptionInfo != null) {
                            curCaptionInfo.setLineSpacing(CAPTION_SMALL_LINE_SPACING);
                        }
                    }
                }
            }

            @Override
            public void onStandardBtnClicked(int spacingMode) {
                if (mCurAddCaption == null) {
                    return;
                }
                if (spacingMode == CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER) {


                    mCurAddCaption.setLetterSpacing(CAPTION_STANDARD_SPACING);
                    updateCaption();
                    int index = getCaptionIndex(mCurCaptionZVal);
                    if (index >= 0) {
                        CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                        if (curCaptionInfo != null) {
                            curCaptionInfo.setUsedLetterSpacingFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            curCaptionInfo.setLetterSpacing(CAPTION_STANDARD_SPACING);
                        }
                    }
                } else {
                    //设置行间距
                    mCurAddCaption.setLineSpacing(CAPTION_STANDARD_LINE_SPACING);
                    updateCaption();
                    int index = getCaptionIndex(mCurCaptionZVal);
                    if (index >= 0) {
                        CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                        if (curCaptionInfo != null) {
                            curCaptionInfo.setLineSpacing(CAPTION_STANDARD_LINE_SPACING);
                        }
                    }
                }
            }

            @Override
            public void onMoreBtnClicked(int spacingMode) {
                if (mCurAddCaption == null) {
                    return;
                }
                if (spacingMode == CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER) {

                    mCurAddCaption.setLetterSpacing(CAPTION_MORE_LARGE_SPACING);
                    updateCaption();
                    int index = getCaptionIndex(mCurCaptionZVal);
                    if (index >= 0) {
                        CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                        if (curCaptionInfo != null) {
                            curCaptionInfo.setUsedLetterSpacingFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            curCaptionInfo.setLetterSpacing(CAPTION_MORE_LARGE_SPACING);
                        }
                    }
                } else {
                    //设置行间距
                    mCurAddCaption.setLineSpacing(CAPTION_MORE_LARGE_LINE_SPACING);
                    updateCaption();
                    int index = getCaptionIndex(mCurCaptionZVal);
                    if (index >= 0) {
                        CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                        if (curCaptionInfo != null) {
                            curCaptionInfo.setLineSpacing(CAPTION_MORE_LARGE_LINE_SPACING);
                        }
                    }
                }
            }

            @Override
            public void onLargeBtnClicked(int spacingMode) {
                if (mCurAddCaption == null) {
                    return;
                }
                if (spacingMode == CaptionLetterSpacingFragment.CAPTION_SPACING_LETTER) {

                    mCurAddCaption.setLetterSpacing(CAPTION_LARGE_SPACEING);
                    updateCaption();
                    int index = getCaptionIndex(mCurCaptionZVal);
                    if (index >= 0) {
                        CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                        if (curCaptionInfo != null) {
                            curCaptionInfo.setUsedLetterSpacingFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                            curCaptionInfo.setLetterSpacing(CAPTION_LARGE_SPACEING);
                        }
                    }
                } else {
                    //设置行间距
                    mCurAddCaption.setLineSpacing(CAPTION_LARGE_LINE_SPACEING);
                    updateCaption();
                    int index = getCaptionIndex(mCurCaptionZVal);
                    if (index >= 0) {
                        CaptionInfo curCaptionInfo = mCaptionDataListClone.get(index);
                        if (curCaptionInfo != null) {
                            curCaptionInfo.setLineSpacing(CAPTION_LARGE_LINE_SPACEING);
                        }
                    }
                }
            }


            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
                bIsLetterSpacingApplyToAll = isApplyToAll;
            }
        });

        return captionLetterSpacingFragment;
    }

    private CaptionPositionFragment initCaptionPositionFragment() {
        CaptionPositionFragment captionPositionFragment = new CaptionPositionFragment();
        captionPositionFragment.setCaptionPostionListener(new CaptionPositionFragment.OnCaptionPositionListener() {
            @Override
            public void onFragmentLoadFinished() {
                mCaptionPositionFragment.applyToAllCaption(bIsPositionApplyToAll);
            }

            @Override
            public void OnAlignLeft() {
                if (mCurAddCaption == null)
                    return;

                List<PointF> list = mCurAddCaption.getBoundingRectangleVertices();
                if (list == null || list.size() < 4)
                    return;
                Collections.sort(list, new Util.PointXComparator());

                float xOffset = -(mTimeline.getVideoRes().imageWidth / 2 + list.get(0).x);
                mCurAddCaption.translateCaption(new PointF(xOffset, 0));

                mAlignType = CAPTION_ALIGNLEFT;
                updateCaption();

                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setTranslation(mCurAddCaption.getCaptionTranslation());
                }

            }

            @Override
            public void OnAlignCenterHorizontal() {
                if (mCurAddCaption == null)
                    return;

                List<PointF> list = mCurAddCaption.getBoundingRectangleVertices();
                if (list == null || list.size() < 4)
                    return;
                Collections.sort(list, new Util.PointXComparator());

                float xOffset = -((list.get(3).x - list.get(0).x) / 2 + list.get(0).x);
                mCurAddCaption.translateCaption(new PointF(xOffset, 0));
                updateCaption();
                mAlignType = CAPTION_ALIGNHORIZCENTER;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setTranslation(mCurAddCaption.getCaptionTranslation());
                }

            }

            @Override
            public void OnAlignRight() {
                if (mCurAddCaption == null)
                    return;

                List<PointF> list = mCurAddCaption.getBoundingRectangleVertices();
                if (list == null || list.size() < 4)
                    return;
                Collections.sort(list, new Util.PointXComparator());

                float xOffset = mTimeline.getVideoRes().imageWidth / 2 - list.get(3).x;

                mCurAddCaption.translateCaption(new PointF(xOffset, 0));


                updateCaption();

                mAlignType = CAPTION_ALIGNRIGHT;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setTranslation(mCurAddCaption.getCaptionTranslation());
                }

            }

            @Override
            public void OnAlignTop() {
                if (mCurAddCaption == null)
                    return;

                List<PointF> list = mCurAddCaption.getBoundingRectangleVertices();
                if (list == null || list.size() < 4)
                    return;
                Collections.sort(list, new Util.PointYComparator());
                float y_dis = list.get(3).y - list.get(0).y;

                float yOffset = mTimeline.getVideoRes().imageHeight / 2 - list.get(0).y - y_dis;

                mCurAddCaption.translateCaption(new PointF(0, yOffset));


                updateCaption();


                mAlignType = CAPTION_ALIGNTOP;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setTranslation(mCurAddCaption.getCaptionTranslation());
                }

            }

            @Override
            public void OnAlignCenterVertical() {
                if (mCurAddCaption == null)
                    return;
                List<PointF> list = mCurAddCaption.getBoundingRectangleVertices();
                if (list == null || list.size() < 4)
                    return;
                Collections.sort(list, new Util.PointYComparator());

                float yOffset = -((list.get(3).y - list.get(0).y) / 2 + list.get(0).y);
                mCurAddCaption.translateCaption(new PointF(0, yOffset));


                updateCaption();


                mAlignType = CAPTION_ALIGNVERTCENTER;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setTranslation(mCurAddCaption.getCaptionTranslation());
                }

            }

            @Override
            public void OnAlignBottom() {
                if (mCurAddCaption == null)
                    return;

                List<PointF> list = mCurAddCaption.getBoundingRectangleVertices();
                if (list == null || list.size() < 4)
                    return;
                Collections.sort(list, new Util.PointYComparator());
                float y_dis = list.get(3).y - list.get(0).y;

                float yOffset = -(mTimeline.getVideoRes().imageHeight / 2 + list.get(3).y - y_dis);
                mCurAddCaption.translateCaption(new PointF(0, yOffset));

                updateCaption();

                mAlignType = CAPTION_ALIGNBOTTOM;
                int index = getCaptionIndex(mCurCaptionZVal);
                if (index >= 0) {
                    mCaptionDataListClone.get(index).setUsedTranslationFlag(CaptionInfo.ATTRIBUTE_USED_FLAG);
                    mCaptionDataListClone.get(index).setTranslation(mCurAddCaption.getCaptionTranslation());
                }

            }

            @Override
            public void onIsApplyToAll(boolean isApplyToAll) {
                bIsPositionApplyToAll = isApplyToAll;
            }
        });
        return captionPositionFragment;
    }

    private int getCaptionStyleSelectedIndex() {
        int selectIndex = 0;
        if (mCurAddCaption != null) {
            String uuid = mCurAddCaption.getCaptionStylePackageId();
            for (int index = 0; index < mTotalCaptionStyleList.size(); ++index) {
                NvAsset asset = mTotalCaptionStyleList.get(index).getAsset();
                if (asset == null)
                    continue;
                if (asset.uuid.compareTo(uuid) == 0) {
                    selectIndex = index;
                    break;
                }
            }
        }

        return selectIndex;
    }

    /**
     * 根据不同类型获取改变拼装字幕相关
     */
    private void changeAssemblyCaption(int type) {
        if (mCurAddCaption != null) {
            switch (type) {
                case NvAsset.ASSET_CAPTION_RICH_WORD:
                    String uuid = mCurAddCaption.getModularCaptionRendererPackageId();
                    mSelectedRichPos = getTargetPosition(mRichWordList, uuid);
                    break;
                case NvAsset.ASSET_CAPTION_BUBBLE:
                    mSelectedBubblePos = getTargetPosition(mBubbleList, mCurAddCaption.getModularCaptionContextPackageId());
                    break;
                case ASSET_CAPTION_ANIMATION:
                    mSelectedAnimationPos = getTargetPosition(mAnimationList, mCurAddCaption.getModularCaptionAnimationPackageId());
                    mSelectedInAnimationPos = 0;
                    mSelectedOutAnimationPos = 0;
                    break;
                case ASSET_CAPTION_IN_ANIMATION:
                    mSelectedInAnimationPos = getTargetPosition(mMarchInAniList, mCurAddCaption.getModularCaptionInAnimationPackageId());
                    mSelectedAnimationPos = 0;
                    break;
                case ASSET_CAPTION_OUT_ANIMATION:
                    mSelectedOutAnimationPos = getTargetPosition(mMarchOutAniList, mCurAddCaption.getModularCaptionOutAnimationPackageId());
                    mSelectedAnimationPos = 0;
                    break;
                default:
                    break;
            }
        }
        int position = mCaptionStyleTab.getSelectedTabPosition();
        if (position == 0) {
            mRichWordFragment.setAssetInfoList(mRichWordList);
            mRichWordFragment.setSelectedPos(mSelectedRichPos);
        } else if (position == 1) {
            mAnimationFragment.setAssetList(mAnimationList, mMarchInAniList, mMarchOutAniList);
            mAnimationFragment.setSelectedPos(mSelectedAnimationPos, mSelectedInAnimationPos, mSelectedOutAnimationPos);
        } else if (position == 2) {
            mBubbleFragment.setAssetInfoList(mBubbleList);
            mBubbleFragment.setSelectedPos(mSelectedBubblePos);
        }
    }

    /**
     * 获取所给集合中某一个条目的索引
     */
    private int getTargetPosition(List<AssetItem> targetList, String uuid) {
        int index = 0;
        if (targetList != null && !TextUtils.isEmpty(uuid)) {
            for (int i = 0; i < targetList.size(); i++) {
                AssetItem assetItem = targetList.get(i);
                if (assetItem == null || assetItem.getAsset() == null) {
                    continue;
                }
                if (uuid.equals(assetItem.getAsset().uuid)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    private int getCaptionColorSelectedIndex() {
        int selectedPos = -1;
        int captionIndex = getCaptionIndex(mCurCaptionZVal);
        if (captionIndex >= 0) {
            String captionColor = mCaptionDataListClone.get(captionIndex).getCaptionColor();
            for (int i = 0; i < mCaptionColorList.size(); ++i) {
                if (mCaptionColorList.get(i).mColorValue.compareTo(captionColor) == 0) {
                    selectedPos = i;
                    break;
                }
            }
        }
        return selectedPos;
    }

    private int getOutlineColorSelectedIndex() {
        int selectedPos = 0;
        int captionIndex = getCaptionIndex(mCurCaptionZVal);
        if (captionIndex >= 0) {
            String outlineColor = mCaptionDataListClone.get(captionIndex).getOutlineColor();
            for (int i = 0; i < mCaptionOutlineColorList.size(); ++i) {
                if (mCaptionOutlineColorList.get(i).mColorValue.compareTo(outlineColor) == 0) {
                    selectedPos = i;
                    break;
                }
            }
        }
        return selectedPos;
    }

    private int getBackgroundSelectedIndex() {
        int selectedPos = 0;
        int captionIndex = getCaptionIndex(mCurCaptionZVal);
        if (captionIndex >= 0) {
            String backgroundColor = mCaptionDataListClone.get(captionIndex).getCaptionBackground();
            for (int i = 0; i < mCaptionBackgroundList.size(); ++i) {
                //去掉透明度比较
                String initColor = mCaptionBackgroundList.get(i).mColorValue;
                //判断要改一下，这个里不比较透明度
                if (!TextUtils.isEmpty(backgroundColor) && backgroundColor.length() == 9) {

                    if (!TextUtils.isEmpty(initColor)) {
                        String initColorWithoutAlpha = initColor.substring(3);
                        String backgroundColorWithoutAlpha = backgroundColor.substring(3);
                        if (initColorWithoutAlpha.compareTo(backgroundColorWithoutAlpha) == 0) {
                            selectedPos = i;
                            break;
                        }
                    }
                }
            }
        }
        return selectedPos;
    }

    private int getCaptionFontSelectedIndex() {
        int selectedPos = 0;
        int captionIndex = getCaptionIndex(mCurCaptionZVal);
        if (captionIndex >= 0) {
            String captionFont = mCaptionDataListClone.get(captionIndex).getCaptionFont();
            for (int i = 0; i < mCaptionFontList.size(); ++i) {
                NvAsset asset = mCaptionFontList.get(i).getAsset();
                if (asset == null)
                    continue;
                if (TextUtils.isEmpty(asset.localDirPath))
                    continue;
                if (asset.localDirPath.compareTo(captionFont) == 0) {
                    selectedPos = i;
                    break;
                }
            }
        }
        return selectedPos;
    }

    private void updateCaption() {
        seekTimeline(BackupData.instance().getCurSeekTimelinePos());
        updateDrawRect();
    }

    private void updateDrawRect() {
        if (mCurAddCaption != null) {
            int alignVal = mCurAddCaption.getTextAlignment();
            mVideoFragment.setAlignIndex(alignVal);
            int captionIndex = getCaptionIndex((int) mCurAddCaption.getZValue());
            CaptionInfo captionInfo = mCaptionDataListClone.get(captionIndex);
            if (captionInfo != null) {
                Map<Long, KeyFrameInfo> keyFrameInfo = captionInfo.getKeyFrameInfo();
                if (keyFrameInfo.isEmpty()) {
                    mVideoFragment.setCurCaption(mCurAddCaption);
                    mVideoFragment.updateCaptionCoordinate(mCurAddCaption);
                    mVideoFragment.changeCaptionRectVisible();
                } else {
                    long duration = mStreamingContext.getTimelineCurrentPosition(mTimeline) - mCurAddCaption.getInPoint();
                    mCurAddCaption.setCurrentKeyFrameTime(duration);

                    mVideoFragment.setCurCaption(mCurAddCaption);
                    mVideoFragment.updateCaptionCoordinate(mCurAddCaption);
                    mVideoFragment.changeCaptionRectVisible();

                    mCurAddCaption.removeKeyframeAtTime(TRANS_X, duration);
                    mCurAddCaption.removeKeyframeAtTime(TRANS_Y, duration);
                    mCurAddCaption.removeKeyframeAtTime(SCALE_X, duration);
                    mCurAddCaption.removeKeyframeAtTime(SCALE_Y, duration);
                    mCurAddCaption.removeKeyframeAtTime(ROTATION_Z, duration);
                }
            }
        }
    }

    private void seekTimeline(long timestamp) {
        mVideoFragment.seekTimeline(timestamp, NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
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

    private void selectCaption() {
        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        List<NvsTimelineCaption> captionList = mTimeline.getCaptionsByTimelinePosition(curPos);
        int captionCount = captionList.size();
        for (int index = 0; index < captionCount; index++) {
            int tmpZVal = (int) captionList.get(index).getZValue();
            if (mCurCaptionZVal == tmpZVal) {
                mCurAddCaption = captionList.get(index);
                checkInit();
                break;
            }
        }
    }

    public boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        m_waitFlag = false;
    }
}
