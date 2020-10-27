package com.duoshine.douyin.meishe.sdkdemo.edit.clipEdit.photo;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.base.BaseActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.clipEdit.SingleClipFragment;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.BackupData;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.PhotoDrawRect;
import com.duoshine.douyin.meishe.sdkdemo.utils.AppManager;
import com.duoshine.douyin.meishe.sdkdemo.utils.Constants;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;
import com.duoshine.douyin.meishe.sdkdemo.utils.TimelineUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.Util;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;

import java.util.ArrayList;

public class PhotoMovementActivity extends BaseActivity {
    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;
    private ImageView mPhotoDisplay;
    private Button mOpenMoveButton;
    private Button mPhotoSeekButton;
    private ImageView mAreaDisplayImg;
    private View mAreaDisplay_Select;
    private ImageView mTotalDisplayImg;
    private View mTotalDisplaySelect;
    private PhotoDrawRect mAreaStartDrawROI;
    private PhotoDrawRect mAreaEndDrawROI;
    private PhotoDrawRect mCloseMoveDrawROI;
    private PhotoDrawRect mTotalDrawROI;
    private ImageView mMovementFinish;
    private LinearLayout mSeekPhotoVideoLayout;
    private Button mCancelSeekPhotoButton;

    private SingleClipFragment mClipFragment;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    ArrayList<ClipInfo> mClipArrayList;
    private int mCurClipIndex = 0;
    /*
    * 是否开启运动
    * Whether to start exercise
    * */
    private boolean mIsOpenMove = true;//是否开启运动
    private int mImgDispalyMode = Constants.EDIT_MODE_PHOTO_AREA_DISPLAY;

    /*
    * 开始画面RectF
    * Start screen RectF
    * */
    private RectF mNewStartRectF;
    /*
    * 结束画面RectF
    * End screen RectF
    * */
    private RectF mNewEndRectF;
    /*
    * 画面静止RectF
    * Picture still RectF
    * */
    private RectF mCloseMoveNewRectF;

    private int mImageViewWidth;
    private int mImageViewHeight;
    private TextView mAreaDisplayText;
    private TextView mTotalDisplayText;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_photo_movement;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mPhotoDisplay = (ImageView) findViewById(R.id.photoDisplay);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottomLayout);
        mOpenMoveButton = (Button) findViewById(R.id.openMoveButton);
        mPhotoSeekButton = (Button) findViewById(R.id.photoSeekButton);
        mAreaDisplayImg = (ImageView) findViewById(R.id.areaDisplayImg);
        mAreaDisplay_Select = findViewById(R.id.areaDisplay_Select);
        mTotalDisplayImg = (ImageView) findViewById(R.id.totalDisplayImg);
        mTotalDisplaySelect = findViewById(R.id.totalDisplay_Select);
        mAreaStartDrawROI = (PhotoDrawRect) findViewById(R.id.areaStartDrawROI);
        mAreaEndDrawROI = (PhotoDrawRect) findViewById(R.id.areaEndDrawROI);
        mCloseMoveDrawROI = (PhotoDrawRect) findViewById(R.id.closeMoveDrawROI);
        mTotalDrawROI = (PhotoDrawRect) findViewById(R.id.totalDrawROI);
        mMovementFinish = (ImageView) findViewById(R.id.movementFinish);
        mSeekPhotoVideoLayout = (LinearLayout) findViewById(R.id.seekPhotoVideoLayout);
        mCancelSeekPhotoButton = (Button) findViewById(R.id.cancelSeekButton);
        mAreaDisplayText = (TextView) findViewById(R.id.areaDisplayText);
        mTotalDisplayText = (TextView) findViewById(R.id.totalDisplayText);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.movement);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        mClipArrayList = BackupData.instance().cloneClipInfoData();
        mCurClipIndex = BackupData.instance().getClipIndex();
        if (mCurClipIndex < 0 || mCurClipIndex >= mClipArrayList.size())
            return;
        ClipInfo clipInfo = mClipArrayList.get(mCurClipIndex);
        if (clipInfo == null)
            return;
        mImgDispalyMode = clipInfo.getImgDispalyMode();
        mIsOpenMove = clipInfo.isOpenPhotoMove();
        if (!setDisplayImage(clipInfo.getFilePath()))
            return;

        mTimeline = TimelineUtil.createSingleClipTimeline(clipInfo, true);
        if (mTimeline == null)
            return;
        initDrawRect();
        updateOpenMoveButton(mIsOpenMove);
        initVideoFragment();
    }

    @Override
    protected void initListener() {
        mOpenMoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsOpenMove = !mIsOpenMove;
                updateOpenMoveButton(mIsOpenMove);
                setDrawRectVisible(mIsOpenMove);
                mClipArrayList.get(mCurClipIndex).setOpenPhotoMove(mIsOpenMove);
            }
        });
        mAreaDisplayImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImgDispalyMode == Constants.EDIT_MODE_PHOTO_AREA_DISPLAY)
                    return;
                mImgDispalyMode = Constants.EDIT_MODE_PHOTO_AREA_DISPLAY;
                setDrawRectVisible(mIsOpenMove);
                mClipArrayList.get(mCurClipIndex).setImgDispalyMode(mImgDispalyMode);
            }
        });
        mTotalDisplayImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImgDispalyMode == Constants.EDIT_MODE_PHOTO_TOTAL_DISPLAY)
                    return;
                mImgDispalyMode = Constants.EDIT_MODE_PHOTO_TOTAL_DISPLAY;
                setDrawRectVisible(mIsOpenMove);
                mClipArrayList.get(mCurClipIndex).setImgDispalyMode(mImgDispalyMode);
            }
        });

        mPhotoSeekButton.setOnClickListener(this);
        mMovementFinish.setOnClickListener(this);
        mCancelSeekPhotoButton.setOnClickListener(this);
        mSeekPhotoVideoLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.photoSeekButton:
                NvsVideoClip videoClip = getVideoClip();
                if (videoClip == null)
                    return;
                if (mImgDispalyMode == Constants.EDIT_MODE_PHOTO_AREA_DISPLAY) {
                    /*
                    * 区域显示
                    * Area display
                    * */
                    videoClip.setImageMotionMode(NvsVideoClip.IMAGE_CLIP_MOTIONMMODE_ROI);
                    RectF startRectF = viewToNormalized(mIsOpenMove ? mNewStartRectF : mCloseMoveNewRectF);
                    RectF endRectF = viewToNormalized(mIsOpenMove ? mNewEndRectF : mCloseMoveNewRectF);
                    videoClip.setImageMotionROI(startRectF, endRectF);
                } else if (mImgDispalyMode == Constants.EDIT_MODE_PHOTO_TOTAL_DISPLAY) {
                    /*
                     * 全图显示
                     * Full image display
                     * */
                    videoClip.setImageMotionMode(NvsVideoClip.CLIP_MOTIONMODE_LETTERBOX_ZOOMIN);
                }
                videoClip.setImageMotionAnimationEnabled(mIsOpenMove);
                mClipFragment.seekTimeline(0, 0);
                mSeekPhotoVideoLayout.setVisibility(View.VISIBLE);
                mClipFragment.playVideo(0, mTimeline.getDuration());
                break;
            case R.id.movementFinish:
                if (mImgDispalyMode == Constants.EDIT_MODE_PHOTO_AREA_DISPLAY) {
                    RectF startRectF = viewToNormalized(mIsOpenMove ? mNewStartRectF : mCloseMoveNewRectF);
                    RectF endRectF = viewToNormalized(mIsOpenMove ? mNewEndRectF : mCloseMoveNewRectF);
                    mClipArrayList.get(mCurClipIndex).setNormalStartROI(startRectF);
                    mClipArrayList.get(mCurClipIndex).setNormalEndROI(endRectF);
                }

                BackupData.instance().setClipInfoData(mClipArrayList);
                removeTimeline();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                AppManager.getInstance().finishActivity();
                break;
            case R.id.cancelSeekButton:
                if (mClipFragment != null) {
                    mClipFragment.stopEngine();
                }
                mSeekPhotoVideoLayout.setVisibility(View.GONE);
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

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    private boolean setDisplayImage(String imageFilePath) {
        if (imageFilePath.isEmpty())
            return false;
        Glide.with(PhotoMovementActivity.this).load(imageFilePath).into(mPhotoDisplay);
        setDispalyImageSize(imageFilePath);
        return true;
    }

    private void setDispalyImageSize(String imagePath) {
        int statusHeight = ScreenUtils.getStatusBarHeight(this);
        int screenWidth = ScreenUtils.getScreenWidth(this);
        int screenHeight = ScreenUtils.getScreenHeight(this);
        int titleHeight = mTitleBar.getLayoutParams().height;
        int bottomHeight = mBottomLayout.getLayoutParams().height;
        NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(imagePath);
        if (avFileInfo != null) {
            ViewGroup.LayoutParams layoutParams = mPhotoDisplay.getLayoutParams();
            NvsSize videoSize = avFileInfo.getVideoStreamDimension(0);
            if (Util.getBitmapDegree(imagePath) == 90 || Util.getBitmapDegree(imagePath) == 270) {
                /*
                * 图片带有旋转角度  交换宽和高
                * Picture with rotation angle swap width and height
                * */
                int tempSize = videoSize.width;
                videoSize.width = videoSize.height;
                videoSize.height = tempSize;
            }
            float newRatio = videoSize.width / (videoSize.height * 1.0f);
            if (newRatio >= 1.0f) {
                /*
                * 水平图片
                * Horizontal picture
                * */
                layoutParams.width = screenWidth;
                layoutParams.height = (int) Math.floor(screenWidth / newRatio + 0.5D);
            } else {
                /*
                * 垂直图片
                * Vertical picture
                * */
                int newHeight = screenHeight - statusHeight - titleHeight - bottomHeight;
                layoutParams.width = (int) Math.floor(newHeight * newRatio + 0.5D);
                layoutParams.height = newHeight;
            }
            mImageViewWidth = layoutParams.width;
            mImageViewHeight = layoutParams.height;
            mPhotoDisplay.setLayoutParams(layoutParams);
        }
    }

    private void initVideoFragment() {
        mClipFragment = new SingleClipFragment();
        mClipFragment.setFragmentLoadFinisedListener(new SingleClipFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mClipFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
            }
        });
        mClipFragment.setTimeline(mTimeline);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        mClipFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.spaceLayout, mClipFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mClipFragment);
    }

    private void updateOpenMoveButton(boolean isOpenMove) {
        mOpenMoveButton.setBackgroundResource(isOpenMove ? R.mipmap.switch_on : R.mipmap.switch_off);
    }

    private void initDrawRect() {
        NvsVideoClip videoClip = getVideoClip();
        if (videoClip == null)
            return;
        RectF startROI = videoClip.getStartROI();
        RectF endROI = videoClip.getEndROI();
        RectF originStartRectF = normalizedToView(startROI);
        RectF originEndRectF = normalizedToView(endROI);
        mNewStartRectF = originStartRectF;
        mCloseMoveNewRectF = originStartRectF;
        mNewEndRectF = originEndRectF;

        /*
        * 开始画面
        * Start screen
        * */
        int makeRatio = TimelineData.instance().getMakeRatio();
        mAreaStartDrawROI.setCurMakeRatio(makeRatio);
        mAreaStartDrawROI.setImgSize(mImageViewWidth, mImageViewHeight);
        String startRect = getResources().getString(R.string.start_rect);
        mAreaStartDrawROI.setDrawRect(startRect, Constants.EDIT_MODE_PHOTO_AREA_DISPLAY);
        setDrawRectLayoutParams(mAreaStartDrawROI, originStartRectF);
        mAreaStartDrawROI.setOnDrawRectListener(new PhotoDrawRect.OnDrawRectListener() {
            @Override
            public void onDrawRect(RectF rectF) {
                mNewStartRectF = rectF;
            }
        });
        /*
        * 结束画面
        * End screen
        * */
        mAreaEndDrawROI.setCurMakeRatio(makeRatio);
        mAreaEndDrawROI.setImgSize(mImageViewWidth, mImageViewHeight);
        String endRect = getResources().getString(R.string.end_rect);
        mAreaEndDrawROI.setDrawRect(endRect, Constants.EDIT_MODE_PHOTO_AREA_DISPLAY);
        setDrawRectLayoutParams(mAreaEndDrawROI, originEndRectF);
        mAreaEndDrawROI.setOnDrawRectListener(new PhotoDrawRect.OnDrawRectListener() {
            @Override
            public void onDrawRect(RectF rectF) {
                mNewEndRectF = rectF;
            }
        });
        /*
        * 区域显示下关闭画面运动
        * Off screen motion in area display
        * */
        mCloseMoveDrawROI.setCurMakeRatio(makeRatio);
        mCloseMoveDrawROI.setImgSize(mImageViewWidth, mImageViewHeight);
        mCloseMoveDrawROI.setDrawRect("", Constants.EDIT_MODE_PHOTO_AREA_DISPLAY);
        setDrawRectLayoutParams(mCloseMoveDrawROI, mCloseMoveNewRectF);
        mCloseMoveDrawROI.setOnDrawRectListener(new PhotoDrawRect.OnDrawRectListener() {
            @Override
            public void onDrawRect(RectF rectF) {
                mCloseMoveNewRectF = rectF;
            }
        });
        /*
        * 全图显示
        * Full image display
        * */
        mTotalDrawROI.setImgSize(mImageViewWidth, mImageViewHeight);
        mTotalDrawROI.setDrawRect("", Constants.EDIT_MODE_PHOTO_TOTAL_DISPLAY);
        RectF totalDisplayRectF = new RectF(0, 0, mImageViewWidth, mImageViewHeight);
        setDrawRectLayoutParams(mTotalDrawROI, totalDisplayRectF);
        /*
        * 设置当前状态
        * Set current status
        * */
        setDrawRectVisible(mIsOpenMove);
    }

    private NvsVideoClip getVideoClip() {
        NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
        if (videoTrack != null)
            return videoTrack.getClipByIndex(0);
        return null;
    }

    private void setDrawRectLayoutParams(PhotoDrawRect photoDrawRect, RectF rectF) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) photoDrawRect.getLayoutParams();
        layoutParams.width = (int) Math.floor(rectF.right - rectF.left + 0.5D);
        layoutParams.height = (int) Math.floor(rectF.bottom - rectF.top + 0.5D);
        layoutParams.leftMargin = (int) Math.floor(rectF.left + 0.5D);
        layoutParams.topMargin = (int) Math.floor(rectF.top + 0.5D);
        photoDrawRect.setLayoutParams(layoutParams);
    }

    /*
    * 归一化坐标转换为控件坐标
    * Normalized coordinates converted to control coordinates
    * */
    private RectF normalizedToView(RectF normalRectF) {
        float left = (normalRectF.left + 1) / 2 * mImageViewWidth;
        float top = (1 - normalRectF.top) / 2 * mImageViewHeight;
        float right = (normalRectF.right + 1) / 2 * mImageViewWidth;
        float bottom = (1 - normalRectF.bottom) / 2 * mImageViewHeight;
        return new RectF(left, top, right, bottom);
    }

    /*
    * 将控件坐标转换为归一化坐标
    * Convert control coordinates to normalized coordinates
    * */
    private RectF viewToNormalized(RectF viewRectF) {
        float left = viewRectF.left / mImageViewWidth * 2 - 1;
        float top = 1 - viewRectF.top / mImageViewHeight * 2;
        float right = viewRectF.right / mImageViewWidth * 2 - 1;
        float bottom = 1 - viewRectF.bottom / mImageViewHeight * 2;
        return new RectF(left, top, right, bottom);
    }

    private void setDrawRectVisible(boolean isOpenMove) {
        if (mImgDispalyMode == Constants.EDIT_MODE_PHOTO_AREA_DISPLAY) {
            mAreaStartDrawROI.setVisibility(isOpenMove ? View.VISIBLE : View.GONE);
            mAreaEndDrawROI.setVisibility(isOpenMove ? View.VISIBLE : View.GONE);
            mCloseMoveDrawROI.setVisibility(isOpenMove ? View.GONE : View.VISIBLE);
            mTotalDrawROI.setVisibility(View.GONE);
            mAreaDisplayText.setTextColor(getResources().getColor(R.color.ms994a90e2));
            mTotalDisplayText.setTextColor(getResources().getColor(R.color.ccffffff));
            mAreaDisplay_Select.setVisibility(View.VISIBLE);
            mTotalDisplaySelect.setVisibility(View.GONE);
            if (mIsOpenMove) {
                setDrawRectLayoutParams(mAreaStartDrawROI, mNewStartRectF);
                setDrawRectLayoutParams(mAreaEndDrawROI, mNewEndRectF);
            } else {
                setDrawRectLayoutParams(mCloseMoveDrawROI, mCloseMoveNewRectF);
            }
        } else if (mImgDispalyMode == Constants.EDIT_MODE_PHOTO_TOTAL_DISPLAY) {
            mAreaStartDrawROI.setVisibility(View.GONE);
            mAreaEndDrawROI.setVisibility(View.GONE);
            mCloseMoveDrawROI.setVisibility(View.GONE);
            mTotalDrawROI.setVisibility(View.VISIBLE);
            mAreaDisplayText.setTextColor(getResources().getColor(R.color.ccffffff));
            mTotalDisplayText.setTextColor(getResources().getColor(R.color.ms994a90e2));
            mAreaDisplay_Select.setVisibility(View.GONE);
            mTotalDisplaySelect.setVisibility(View.VISIBLE);
        }
    }
}

