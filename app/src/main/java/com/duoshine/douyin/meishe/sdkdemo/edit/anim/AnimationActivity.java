package com.duoshine.douyin.meishe.sdkdemo.edit.anim;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.base.BaseActivity;
import com.duoshine.douyin.meishe.sdkdemo.download.AssetDownloadActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.VideoFragment;
import com.duoshine.douyin.meishe.sdkdemo.edit.anim.view.AnimationBottomView;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.FilterItem;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.duoshine.douyin.meishe.sdkdemo.utils.AppManager;
import com.duoshine.douyin.meishe.sdkdemo.utils.TimelineUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.ToastUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.AnimationInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * create by jml  2020-08-24
 */

public class AnimationActivity extends BaseActivity {

    private static final String TAG = "AnimationActivity";
    private static final int ANIMATION_REQUESTLIST = 101;
    private VideoFragment mVideoFragment;
    private CustomTitleBar mTitleBar;
    private RecyclerView mAnimationClipRecyclerView;
    private ImageView mAnimationAssetFinish;
    private RelativeLayout mBottomLayout;
    private AnimationClipAdapter mAnimationClipAdapter;
    private AnimationBottomView mAnimationBottomView;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;

    private NvsVideoTrack mVidoeTrack;

    /**
     * 动画特效的数据结构,保存给videoEdit页面使用 和 第一次初始化时候使用
     * 期间修改动画或者修改动画时间 保存数据
     */
    private ConcurrentHashMap<Integer, AnimationInfo> mAnimationFxMap;
    private int mAssetType = NvAsset.ASSET_ANIMATION_IN;
    private LinearLayout mAnimationInLayout,mAnimationOutLayout,mAnimationCompanyLayout;
    //当前的片段列表选择的位置
    private int mSelectedClipPosition = 0;
    /**
     * 每个片段对应的特效对象 当前页面使用，设置的动画
     */
    private Map<Integer, NvsVideoFx> mNvsVideoFxMap;
    /**
     * 每个片段--对应的进度集合
     * key -- 片段坐标
     * value -- 该片段选择过的所有特效的id -- 和对应的 value
     */
    private ConcurrentHashMap<Integer,ConcurrentHashMap<String, Long>>mClipVideoFxAnimationDurationMap ;
    private boolean mShowAnimationList = false;
    private static final float DEFAULT_DURATION_IN = 0.5f;
    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_animation;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mAnimationClipRecyclerView = (RecyclerView) findViewById(R.id.clip_list);
        mAnimationAssetFinish = (ImageView) findViewById(R.id.animationAssetFinish);
        mAnimationOutLayout = findViewById(R.id.ll_animation_out);
        mAnimationCompanyLayout = findViewById(R.id.ll_animation_company);
        mAnimationInLayout = findViewById(R.id.ll_animation_in);
        mAnimationOutLayout = findViewById(R.id.ll_animation_out);
        mAnimationCompanyLayout = findViewById(R.id.ll_animation_company);
        mAnimationBottomView = findViewById(R.id.animation_bottom);
        mBottomLayout = findViewById(R.id.bottom_layout);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.animation);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        init();
        initVideoFragment();
        initAnimationClipList();
        initAnimationViewList();

    }


    protected void initListener() {
        mAnimationAssetFinish.setOnClickListener(this);
        mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
            }

            @Override
            public void playStopped(NvsTimeline timeline) {
            }

            @Override
            public void playbackTimelinePosition(NvsTimeline timeline, long stamp) {
                mVideoFragment.setDrawRectVisible(View.GONE);
            }

            @Override
            public void streamingEngineStateChanged(int state) {
            }
        });
        mVideoFragment.setAssetEditListener(new VideoFragment.AssetEditListener() {
            @Override
            public void onAssetDelete() {
            }

            @Override
            public void onAssetSelected(PointF curPoint) {
                if (mVideoFragment.getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_PLAYBACK)
                    return;

            }

            @Override
            public void onAssetTranstion() {
            }

            @Override
            public void onAssetScale() {
            }

            @Override
            public void onAssetAlign(int alignVal) {
            }

            @Override
            public void onAssetHorizFlip(boolean isHorizFlip) {
            }
        });
        mVideoFragment.setThemeCaptionSeekListener(new VideoFragment.OnThemeCaptionSeekListener() {
            @Override
            public void onThemeCaptionSeek(long stamp) {
            }
        });
        mAnimationInLayout.setOnClickListener(this);
        mAnimationOutLayout.setOnClickListener(this);
        mAnimationCompanyLayout.setOnClickListener(this);
        mBottomLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ll_animation_in:
                if(mSelectedClipPosition == -1){
                    ToastUtil.showToast(AnimationActivity.this,"请先选择添加动画的视频片段");
                    return;
                }
                mAnimationBottomView.setVisibility(View.VISIBLE);
                mAnimationBottomView.setAssetType(NvAsset.ASSET_ANIMATION_IN);
                setAnimationListViewSelected(NvAsset.ASSET_ANIMATION_IN);
                mAssetType = NvAsset.ASSET_ANIMATION_IN;
                mShowAnimationList = true;
                break;
            case R.id.ll_animation_out:
                if(mSelectedClipPosition == -1){
                    ToastUtil.showToast(AnimationActivity.this,"请先选择添加动画的视频片段");
                    return;
                }
                mAnimationBottomView.setVisibility(View.VISIBLE);
                mAnimationBottomView.setAssetType(NvAsset.ASSET_ANIMATION_OUT);
                setAnimationListViewSelected(NvAsset.ASSET_ANIMATION_OUT);
                mAssetType = NvAsset.ASSET_ANIMATION_OUT;
                mShowAnimationList = true;
                break;
            case R.id.ll_animation_company:
                if(mSelectedClipPosition == -1){
                    ToastUtil.showToast(AnimationActivity.this,"请先选择添加动画的视频片段");
                    return;
                }
                mAnimationBottomView.setVisibility(View.VISIBLE);
                mAnimationBottomView.setAssetType(NvAsset.ASSET_ANIMATION_COMPANY);
                setAnimationListViewSelected(NvAsset.ASSET_ANIMATION_COMPANY);
                mAssetType = NvAsset.ASSET_ANIMATION_COMPANY;
                mShowAnimationList = true;
                break;
            case R.id.animationAssetFinish:
                if(mShowAnimationList){
                    return;
                }
                TimelineData.instance().setmAnimationFxMap(mAnimationFxMap);
                removeTimeline();
                quitActivity();
                break;
            case R.id.bottom_layout:
                //当底部的动画列表显示的时候 不处理取消选中
                if(mAnimationBottomView.getVisibility() == View.VISIBLE){
                    return;
                }
                //点击空白处播放全部视频
                playCurrentClip(-1,0,mAssetType);
                //不设置选中片段
                mSelectedClipPosition = -1;
                mAnimationClipAdapter.setSelectPos(mSelectedClipPosition);
                mAnimationBottomView.setSelectedClipPosition(mSelectedClipPosition);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case ANIMATION_REQUESTLIST:
                if(null != mAnimationBottomView){
                    mAnimationBottomView.initAnimationDataList(this);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        mTimeline = TimelineUtil.createTimeline();
        //去掉专场
        //clearBuildInTransform(mTimeline);
        if (mTimeline == null)
            return;
        mVidoeTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVidoeTrack == null) {
            return;
        }
        mAnimationFxMap = TimelineData.instance().getmAnimationFxMap();
        mNvsVideoFxMap = new HashMap<>();
        mClipVideoFxAnimationDurationMap = new ConcurrentHashMap<>();
    }

    private void clearBuildInTransform(NvsTimeline mTimeline) {
        if(null == mTimeline){
            return;
        }
        NvsVideoTrack mVidoeTrack = mTimeline.getVideoTrackByIndex(0);
        if(null == mVidoeTrack){
            return;
        }
        int videoClipCount = mVidoeTrack.getClipCount();
        if(videoClipCount<=1 ){
            return;
        }
        for(int i =0 ;i<videoClipCount ; i++){
            mVidoeTrack.setBuiltinTransition(i,"");
        }
    }

    private void quitActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        AppManager.getInstance().finishActivity();
    }


    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mAnimationAssetFinish.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //播放片段，通过片段时间控制
                        if(null != mVideoFragment){
                            playCurrentClip(mSelectedClipPosition,getClipDuration(mSelectedClipPosition),mAssetType);
                        }
                        mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
                    }
                }, 100);
            }
        });
        mVideoFragment.setTimeline(mTimeline);
        mVideoFragment.setIsAnimationView(true);
        Bundle bundle = new Bundle();
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.video_layout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);

    }

    /**
     * 视频片段列表
     */
    private void initAnimationClipList() {

        mAnimationClipAdapter = new AnimationClipAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mAnimationClipRecyclerView.setLayoutManager(linearLayoutManager);
        mAnimationClipRecyclerView.setAdapter(mAnimationClipAdapter);
        final ArrayList<ClipInfo> clipInfoData = TimelineData.instance().getClipInfoData();
        //设置蒙层
        buildClipAnimationDuration(clipInfoData);
        mAnimationClipAdapter.setClipInfoList(clipInfoData);
        mAnimationClipAdapter.setTimeLine(mTimeline);
        mAnimationClipAdapter.setOnItemClickListener(new AnimationClipAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //选中
                if(position>=0 && position<clipInfoData.size()){

                    //播放片段，通过片段时间控制
                    if(null != mVideoFragment){
                        //获取到动画时长
                        AnimationInfo animationInfo = mAnimationFxMap.get(position);
                        if(null != animationInfo){

                            long duration = animationInfo.getmAnimationOut() - animationInfo.getmAnimationIn();
                            int assetType = animationInfo.getmAssetType();
                            playCurrentClip(position,duration,assetType);
                        }else{
                            playCurrentClip(position,getClipDuration(position),mAssetType);
                        }
                    }
                    //如果当前显示的是动画列表 切换到显示动画类型
                    if(mSelectedClipPosition == position){
                        return;
                    }
                    //当前选择的是第几个片段
                    mSelectedClipPosition = position;
                    //修改选中的片段的位置信息
                    mAnimationBottomView.setSelectedClipPosition(mSelectedClipPosition);
                    //切换片段隐藏动画列表，
                    //修改为不隐藏动画列表直接切换片段

                   /* if(mAnimationBottomView.getVisibility() == View.VISIBLE){
                        mAnimationBottomView.setVisibility(View.GONE);
                    }*/
                    if(mAnimationBottomView.getVisibility() == View.VISIBLE){

                        if(mAssetType == NvAsset.ASSET_ANIMATION_IN){
                            mAnimationInLayout.performClick();
                        }else if(mAssetType == NvAsset.ASSET_ANIMATION_OUT){
                            mAnimationOutLayout.performClick();
                        }else{
                            mAnimationCompanyLayout.performClick();
                        }
                        //修改已选中的动画的时间长度
                    }
                }
            }


        });
    }

    /**
     * 构建视频片段的蒙层效果 初始化
     * @param clipInfoData
     */
    private void buildClipAnimationDuration(ArrayList<ClipInfo> clipInfoData) {
        if(null != clipInfoData && clipInfoData.size() > 0){

            if(null != mAnimationFxMap && mAnimationFxMap.size()>0 ){
                for(int i=0 ; i<clipInfoData.size() ; i++){
                    ClipInfo clipInfo = clipInfoData.get(i);
                    if(mAnimationFxMap.containsKey(i)){
                        AnimationInfo animationInfo = mAnimationFxMap.get(i);
                        clipInfo.setmAnimationType(animationInfo.getmAssetType());
                        clipInfo.setmAnimationDuration(animationInfo.getmAnimationOut()-animationInfo.getmAnimationIn());
                    }
                }
            }
        }
    }

    /**
     * 动画特效列表
     */
    private void initAnimationViewList() {
        mAnimationBottomView.setFunctionListener(new AnimationBottomView.OnFunctionListener() {
            @Override
            public void onItemClick(FilterItem filterItem, int position, float duration, int animationType) {
                //获取到动画Id
                String mAnimationId = filterItem.getPackageId();

                //获取动画的时长
                long value = getAnimationDurationValue(mAnimationId,animationType);

                //设置特效
                appendAnimationFxToVideoPackage(mSelectedClipPosition,mAnimationId,value);

                //设置进度
                mAnimationBottomView.setSelectedProgress(value);
                //播放片段，如果选择无，播放完整的片段
                if(position == 0){
                    //移除特效
                    //removeVideoFx(mSelectedClipPosition,mNvsVideoFxMap.get(mSelectedClipPosition));
                    //播放完整片段
                    playCurrentClip(mSelectedClipPosition,getClipDuration(mSelectedClipPosition),mAssetType);

                    //把当前选择的片段对应的默认设置的所有动画时长设为默认值，不在保存之前的值
                    if(mClipVideoFxAnimationDurationMap.containsKey(mSelectedClipPosition)){
                        mClipVideoFxAnimationDurationMap.remove(mSelectedClipPosition);
                    }
                    //这个赋值为设置蒙层使用
                    value = 0;
                }else{

                    playCurrentClip(mSelectedClipPosition,value,mAssetType);
                    //保存到本地
                    saveCurrentClipVideoFxAnimationValue(mAnimationId,value);
                }

                //设置视频片段列表显示蒙层
                mAnimationClipAdapter.setmAnimationDuration(mSelectedClipPosition,value,mAssetType);

            }

            @Override
            public void onConfirm() {
                //隐藏当前的view
                mAnimationBottomView.setVisibility(View.GONE);
                mShowAnimationList = false;
            }

            @Override
            public void onLoadMore(int categoryId) {
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreAnimation);
                bundle.putInt("assetType", mAssetType);
                bundle.putInt("categoryId",categoryId);
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, ANIMATION_REQUESTLIST);
            }

            @Override
            public void onSeekChanged(String packageId,long progress) {
                //改变动画的时长 progress 为对应的秒数
                NvsVideoFx mPositionerVideoFx = mNvsVideoFxMap.get(mSelectedClipPosition);
                if(null != mPositionerVideoFx){
                    if(mAssetType == NvAsset.ASSET_ANIMATION_OUT ){
                        long duration = getClipDuration(mSelectedClipPosition);
                        mPositionerVideoFx.setFloatVal("Package Effect In", duration-progress);
                    }else{
                        mPositionerVideoFx.setFloatVal("Package Effect Out", progress);
                    }
                }else{
                    appendAnimationFxToVideoPackage(mSelectedClipPosition,packageId,progress);
                }
                //重新播放
                if(null != mVideoFragment){
                    playCurrentClip(mSelectedClipPosition,progress,mAssetType);
                }
                //保存到本地
                saveCurrentClipVideoFxAnimationValue(packageId,progress);
                //保存设置的时间
                AnimationInfo animationInfo = mAnimationFxMap.get(mSelectedClipPosition);
                if(null != animationInfo){
                    //出动画如动画设置时间不同
                    if(mAssetType == NvAsset.ASSET_ANIMATION_OUT){
                        animationInfo.setmAnimationIn(getClipDuration(mSelectedClipPosition)-progress);
                    }else{
                        animationInfo.setmAnimationOut(progress);
                    }
                }
                //设置视频片段列表显示蒙层
                mAnimationClipAdapter.setmAnimationDuration(mSelectedClipPosition,progress,mAssetType);
            }
        });
    }


    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     *
     * 设置当前选中的片段 选中的特效 并设置其动画时长
     * @param animationType 动画类型不同，默认的动画的时间不同
     */
    private void setAnimationListViewSelected(int animationType){
        AnimationInfo animationInfo = mAnimationFxMap.get(mSelectedClipPosition);
        String packageId = "";

        long value = 0;
        if(animationType == NvAsset.ASSET_ANIMATION_COMPANY){

            value = getClipDuration(mSelectedClipPosition);
        }else{
            value = (long) (DEFAULT_DURATION_IN*1000*1000);
        }/*else{
            value = (long) (DEFAULT_DURATION_IN*1000*1000);
        }*/
        if(null != animationInfo){
            //如果设置了特效的话，就拿上次设置的时间显示
            packageId = animationInfo.getmPackageId();
            if(!TextUtils.isEmpty(packageId)){
                value = animationInfo.getmAnimationOut()-animationInfo.getmAnimationIn();
            }

        }else{
            //获取动画时长
            value = getAnimationDurationValue(packageId,animationType);
        }

        mAnimationBottomView.setSelectedPackageId(packageId);
        mAnimationBottomView.setSelectedProgress(value);
        mAnimationBottomView.setMaxProgress(getClipDuration(mSelectedClipPosition));
        //如果选中的特效类型和当前的类型一致，保存值
        /*if(mAssetType == animationType){
            saveCurrentClipVideoFxAnimationValue(packageId,value);
        }*/
    }
    /**
     * 添加特效
     * @param clipPosition 给第几个片段添加特效
     * @param packageId 特效Id
     * @param duration 特效时长
     */
    private void appendAnimationFxToVideoPackage(int clipPosition,String packageId,long duration){
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            Log.i(TAG, "timeline get video track is null");
            return;
        }
        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(clipPosition);
        if (nvsVideoClip == null) {
            Log.i(TAG, "timeline get video clip is null");
            return;
        }
        nvsVideoClip.enablePropertyVideoFx(true);
        NvsVideoFx mPositionerVideoFx;
        if(mNvsVideoFxMap.containsKey(mSelectedClipPosition)){
            mPositionerVideoFx = mNvsVideoFxMap.get(mSelectedClipPosition);
        }else{
            mPositionerVideoFx = nvsVideoClip.getPropertyVideoFx();
        }
        if (mPositionerVideoFx == null) {
            return;
        }
        //默认设置为入动画对应的时间节点
        long in = 0;
        long out = duration;
        //如果是空，选择无动画
        if(TextUtils.isEmpty(packageId)){
            out = 0;
            packageId="";
        }
        //设置动画的起始结束时间点,如动画从0 —— end ,出动画从 duration-start——end  组合动画当前默认和入动画相同
        if(mAssetType == NvAsset.ASSET_ANIMATION_OUT){
            //获取片段长度
            long clipDuration = nvsVideoClip.getOutPoint()-nvsVideoClip.getInPoint();
            in =  clipDuration - duration;
            out = clipDuration;
        }
        mPositionerVideoFx.setStringVal("Post Package Id", packageId);
        mPositionerVideoFx.setBooleanVal("Is Post Storyboard 3D", false);
        //设置锯齿
        mPositionerVideoFx.setBooleanVal("Enable MutliSample", true);

        //设置背景是否旋转
        //mPositionerVideoFx.setBooleanVal("Enable Background Rotation", true);

        //模糊背景
        //mPositionerVideoFx.setMenuVal("Background Mode", "Blur");
        //mPositionerVideoFx.setFloatVal("Background Blur Radius", 40);


        Log.e(TAG, "---in:" + in + "   out:" + out +" packageId ="+packageId);
        mPositionerVideoFx.setFloatVal("Package Effect In", in );
        mPositionerVideoFx.setFloatVal("Package Effect Out", out);
        AnimationInfo animationInfo = new AnimationInfo();
        animationInfo.setmAnimationIn(in);
        animationInfo.setmAnimationOut(out);
        animationInfo.setmPackageId(packageId);
        animationInfo.setmAssetType(mAssetType);
        mAnimationFxMap.put(clipPosition,animationInfo);
        mNvsVideoFxMap.put(mSelectedClipPosition,mPositionerVideoFx);
    }

    /**
     * 移除特效
     * @param clipPosition
     * @param mPositionerVideoFx
     */
    private void removeVideoFx(int clipPosition, NvsVideoFx mPositionerVideoFx){
        if(null == mPositionerVideoFx){
            return;
        }
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            Log.i(TAG, "timeline get video track is null");
            return;
        }
        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(clipPosition);
        if (nvsVideoClip == null) {
            Log.i(TAG, "timeline get video clip is null");
            return;
        }
        int videoFxCount = nvsVideoClip.getFxCount();
        if(videoFxCount>0){
            int targetPosition = -1;
            for(int i = 0; i<videoFxCount ; i++){
                NvsVideoFx videoFx = nvsVideoClip.getFxByIndex(i);
                String packageId = videoFx.getBuiltinVideoFxName();
                if(!TextUtils.isEmpty(packageId) && packageId.equals(mPositionerVideoFx.getVideoFxPackageId())){
                    targetPosition = i;
                    break;
                }
            }
            if(targetPosition>=0){
                nvsVideoClip.removeFx(targetPosition);

                mAnimationFxMap.remove(clipPosition);
                mNvsVideoFxMap.remove(clipPosition);
            }
        }

    }
    /**
     * 获取片段的时长
     * @param mSelectedClipPosition
     * @return
     */
    private long getClipDuration(int mSelectedClipPosition) {
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            Log.i(TAG, "timeline get video track is null");
            return 0;
        }
        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(mSelectedClipPosition);
        if (nvsVideoClip == null) {
            Log.i(TAG, "timeline get video clip is null");
            return 0;
        }
        long clipInPoint = nvsVideoClip.getInPoint();
        long clipOutPoint = nvsVideoClip.getOutPoint();
        return (clipOutPoint-clipInPoint);
    }

    /**
     * 获取当前选择的片段的起始位置
     * @param mSelectedClipPosition
     * @return
     */
    private long getClipStartTime(int mSelectedClipPosition) {
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            Log.i(TAG, "timeline get video track is null");
            return 0;
        }
        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(mSelectedClipPosition);
        if (nvsVideoClip == null) {
            Log.i(TAG, "timeline get video clip is null");
            return 0;
        }
        long clipInPoint = nvsVideoClip.getInPoint();
        return clipInPoint;
    }
    /**
     * 获取当前选择的片段的起始位置
     * @param mSelectedClipPosition
     * @return
     */
    private long getClipEndTime(int mSelectedClipPosition) {
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            Log.i(TAG, "timeline get video track is null");
            return 0;
        }
        NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(mSelectedClipPosition);
        if (nvsVideoClip == null) {
            Log.i(TAG, "timeline get video clip is null");
            return 0;
        }
        long clipOutPoint = nvsVideoClip.getOutPoint();
        return clipOutPoint;
    }
    /**
     * 保存选择的特效 和 片段 及对应的值
     * @param packageId
     * @param value
     */
    private void saveCurrentClipVideoFxAnimationValue(String packageId,Long value){
        //判断是否创建过该片段的集合
        ConcurrentHashMap<String,Long> mNvsVideoFxAnimationDurationMap=null;
        if(mClipVideoFxAnimationDurationMap.containsKey(mSelectedClipPosition)){
            mNvsVideoFxAnimationDurationMap = mClipVideoFxAnimationDurationMap.get(mSelectedClipPosition);
        }else{
            mNvsVideoFxAnimationDurationMap = new ConcurrentHashMap<>();

            mClipVideoFxAnimationDurationMap.put(mSelectedClipPosition,mNvsVideoFxAnimationDurationMap);

        }
        if(!TextUtils.isEmpty(packageId)){
            mNvsVideoFxAnimationDurationMap.put(packageId,value);
        }
        //遍历集合，该视频所有的动画时长修改为刚才选择的时长
        if(mNvsVideoFxAnimationDurationMap.size()>0){
            //遍历该片段设置过的所有动画item
            Set<String> keySet = mNvsVideoFxAnimationDurationMap.keySet();
            for(String key : keySet){
                //把该【片段设置过的当前类型的所有动画的时长设置为value
                if(!key.equals(packageId) && containAnimationId(key)){
                    mNvsVideoFxAnimationDurationMap.put(key,value);
                }
            }
        }

    }

    /**
     * 获取动画对应的时长情况
     * 1.先拿到默认的动画时长 不同动画类型市场不懂
     * 2.如果该动画类型之前设置过时长，则拿到这个时长作为动画的时长
     * @param mAnimationId
     * @param animationType
     * @return
     */
    private long getAnimationDurationValue(String mAnimationId,int animationType) {
        //获取到特效对应的值,如果手动设置过 ，会在回调中保存，没设置过，则使用默认值
        //默认值 组合动画为最大时长，其他未0.5s
        long value = 0;
        if(animationType == NvAsset.ASSET_ANIMATION_COMPANY){

            value = getClipDuration(mSelectedClipPosition);
        }else{
            value = (long) (DEFAULT_DURATION_IN*1000*1000);
        }/*else{
            value = (long) (DEFAULT_DURATION_IN*1000*1000);
        }*/

        //mAnimationId 非空代表选择有效动画效果, 拿到这个packageId之前选择的动画时长设置
        if(!TextUtils.isEmpty(mAnimationId)){
            //如果该片段设置过动画，此时去获取动画的时长
            if(mClipVideoFxAnimationDurationMap.containsKey(mSelectedClipPosition)){
                //拿到该片段设置过的当前类型的动画的时长作为返回值
                ConcurrentHashMap<String, Long>map =  mClipVideoFxAnimationDurationMap.get(mSelectedClipPosition);

                //遍历这个片段设置过的动画列表 ， 如果 当前类型的动画列表中包含这个，直接返回这个动画对应的时长,否则不处理value
                if(null != map && map.size() > 0){

                    Set<String>keySet = map.keySet();
                    for(String key : keySet){
                        if(!TextUtils.isEmpty(key) && containAnimationId(key)){
                            value = map.get(key);
                            break;
                        }
                    }
                }
            }
        }

        return value;
    }

    /**
     * 该动画id 是否在当前的选择的动画类型列表中
     * @param animationId
     * @return
     */
    private boolean containAnimationId(String animationId){
        //获取该片段之前设置过的动画时长,只修改当前选择的动画类型 出 入 组合
        List<FilterItem> packageList = mAnimationBottomView.getCurrentAnimationList();
        if(null == packageList || packageList.size() <= 0){
            return false;
        }
        if(TextUtils.isEmpty(animationId)){
            return false;
        }
        for(FilterItem item : packageList){
            String packageId = item.getPackageId();
            if(!TextUtils.isEmpty(packageId) && packageId.equals(animationId)){
                return true;
            }
        }
        return false;
    }

    /**
     * 播放视频
     * Play video
     *@param animationDuration 动画时长 只播放动画部分
     * @param animationType  动画类型
     * */
    private void playCurrentClip(int mSelectedClipPosition,long animationDuration,int animationType){
        int clipCount = mTimeline.getVideoTrackByIndex(0).getClipCount();
        long playStartPoint = 0;
        long playEndPoint = 0;
        if(mSelectedClipPosition>=0 && mSelectedClipPosition<clipCount){
            playStartPoint = getClipStartTime(mSelectedClipPosition);
            playEndPoint = getClipEndTime(mSelectedClipPosition);
            //播放时针对timeline 的
            if(playEndPoint>playStartPoint){
                //出动画只播放结尾部分 入动画播放开始部分
                if(animationType ==  NvAsset.ASSET_ANIMATION_OUT){
                    playStartPoint = playEndPoint - animationDuration;
                }else{
                    playEndPoint = playStartPoint + animationDuration;
                }
                //除了第一条片段，其他片段延0.5s播放时间，从开始向前
                //设置进度条显示 0 - duration
                if(mSelectedClipPosition>0){
                    playStartPoint -= 0.5*1000*1000;
                    playEndPoint -= 0.5*1000*1000;
                }
                mVideoFragment.setmPlaySeekBarMaxAndCurrent(playStartPoint,playEndPoint,playStartPoint,mTimeline.getDuration());
                //只播放动画部分
                mVideoFragment.playVideoButtonCilck(playStartPoint,playEndPoint);

            }
        }
        //播放全部视频
        else if(mSelectedClipPosition == -1){
            playStartPoint = mStreamingContext.getTimelineCurrentPosition(mTimeline);
            playEndPoint = mTimeline.getDuration();
            //播放全部视频时 duration = end
            //设置进度条显示
            mVideoFragment.setmPlaySeekBarMaxAndCurrent(0,playEndPoint,playStartPoint,playEndPoint);
            if(playEndPoint>playStartPoint){
                mVideoFragment.playVideoButtonCilck(playStartPoint,playEndPoint);
            }
        }


    }
}
