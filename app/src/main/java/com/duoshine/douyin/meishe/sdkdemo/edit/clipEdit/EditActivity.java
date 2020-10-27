package com.duoshine.douyin.meishe.sdkdemo.edit.clipEdit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.base.BaseActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.adapter.AssetRecyclerViewAdapter;
import com.duoshine.douyin.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.duoshine.douyin.meishe.sdkdemo.edit.clipEdit.adjust.AdjustActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.clipEdit.correctionColor.CorrectionColorActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.clipEdit.photo.DurationActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.clipEdit.photo.PhotoMovementActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.clipEdit.speed.SpeedActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.clipEdit.spilt.SpiltActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.clipEdit.trim.TrimActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.clipEdit.volume.VolumeActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.AssetInfoDescription;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.BackupData;
import com.duoshine.douyin.meishe.sdkdemo.edit.filter.ClipFilterActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.grallyRecyclerView.GrallyAdapter;
import com.duoshine.douyin.meishe.sdkdemo.edit.grallyRecyclerView.GrallyScaleHelper;
import com.duoshine.douyin.meishe.sdkdemo.edit.interfaces.OnGrallyItemClickListener;
import com.duoshine.douyin.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.duoshine.douyin.meishe.sdkdemo.selectmedia.SelectMediaActivity;
import com.duoshine.douyin.meishe.sdkdemo.utils.AppManager;
import com.duoshine.douyin.meishe.sdkdemo.utils.Constants;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;
import com.duoshine.douyin.meishe.sdkdemo.utils.Util;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.AnimationInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TransitionInfo;
import com.meicam.sdk.NvsAVFileInfo;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EditActivity extends BaseActivity {
    /*
     * 裁剪
     * Clip crop
     * */
    public static final int CLIPTRIM_REQUESTCODE = 101;
    /*
     * 分割
     * Clip spilt
     * */
    public static final int CLIPSPILTPOINT_REQUESTCODE = 102;
    /*
     * 校色
     * Color correction
     * */
    public static final int CLIPCORRECTIONCOLOR_REQUESTCODE = 103;
    /*
     * 调整
     * Adjustment
     * */
    public static final int CLIPADJUST_REQUESTCODE = 104;
    /*
     * 速度
     * speed
     * */
    public static final int CLIPSPEED_REQUESTCODE = 105;
    /*
     * 音量
     * volume
     * */
    public static final int CLIPVOLUME_REQUESTCODE = 106;
    /*
     * 添加视频
     * Add video
     * */
    public static final int ADDVIDEO_REQUESTCODE = 107;
    /*
     * 图片时长
     * Picture duration
     * */
    public static final int PHOTODURATION_REQUESTCODE = 108;
    /*
     * 图片运动
     * Picture movement
     * */
    public static final int PHOTOMOVE_REQUESTCODE = 109;

    private CustomTitleBar mEditCustomTitleBar;
    private RecyclerView mGrallyRecyclerView;
    private RecyclerView mEffectRecyclerView;
    private int[] ImageId_Video = {R.drawable.capture, R.drawable.division,
            R.drawable.amend, R.drawable.filter, R.drawable.adjust, R.drawable.speed,
            R.drawable.volume, R.drawable.copy, R.drawable.delete};
    private int[] ImageId_Image = {R.drawable.speed, R.drawable.adjust, R.drawable.amend, R.drawable.filter, R.drawable.copy, R.drawable.delete};
    private AssetRecyclerViewAdapter mAssetRecycleAdapter;
    private ArrayList<AssetInfoDescription> mArrayAssetInfoVideo = new ArrayList<>();
    private ArrayList<AssetInfoDescription> mArrayAssetInfoImage = new ArrayList<>();
    private GrallyAdapter mGrallyAdapter;
    private ArrayList<ClipInfo> mClipInfoArray = new ArrayList<>();
    private GrallyScaleHelper mGrallyScaleHelper;
    private int mCurrentPos = 0;
    private ImageView mEditCommitButton;
    private boolean m_waitFlag;
    private int mAddVideoPostion = 0;
    private boolean mIsImage = false;
    private ArrayList<TransitionInfo> mTransitionInfoArray;
    /**
     *
     * clip 对应的动画集合 当前添加或删除 等操作 需要同步操作这个集合 以保证数据的同步
     * 这个集合是当前页面级的
     */
    private ConcurrentHashMap<Integer, AnimationInfo> mVideoClipFxMap = new ConcurrentHashMap<>();
    @Override
    protected int initRootView() {
        return R.layout.activity_edit;
    }

    @Override
    protected void initViews() {
        mEditCustomTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mGrallyRecyclerView = (RecyclerView) findViewById(R.id.editClipRecyclerView);
        mEffectRecyclerView = (RecyclerView) findViewById(R.id.effectRecyclerView);
        mEditCommitButton = (ImageView) findViewById(R.id.edit_commitButton);

    }

    @Override
    protected void initTitle() {
        mEditCustomTitleBar.setTextCenter(getResources().getString(R.string.edit));
        mEditCustomTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        mTransitionInfoArray = TimelineData.instance().cloneTransitionsData();
        mClipInfoArray = TimelineData.instance().cloneClipInfoData();
        BackupData.instance().setClipIndex(0);
        BackupData.instance().setClipInfoData(mClipInfoArray);
        String[] AssetNameVideo = getResources().getStringArray(R.array.effectNamesVideo);
        for (int i = 0; i < AssetNameVideo.length; i++) {
            mArrayAssetInfoVideo.add(new AssetInfoDescription(AssetNameVideo[i], ImageId_Video[i]));
        }
        String[] AssetNameImage = getResources().getStringArray(R.array.effectNamesImage);
        for (int i = 0; i < AssetNameImage.length; i++) {
            mArrayAssetInfoImage.add(new AssetInfoDescription(AssetNameImage[i], ImageId_Image[i]));
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mGrallyRecyclerView.setLayoutManager(linearLayoutManager);
        mGrallyAdapter = new GrallyAdapter(getApplicationContext());
        mGrallyAdapter.setClipInfoArray(mClipInfoArray);
        mGrallyRecyclerView.setAdapter(mGrallyAdapter);

        ItemTouchHelper.Callback callback = new com.duoshine.douyin.meishe.sdkdemo.edit.grallyRecyclerView.ItemTouchHelper(mGrallyAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mGrallyRecyclerView);
        mGrallyScaleHelper = new GrallyScaleHelper();
        mGrallyScaleHelper.attachToRecyclerView(mGrallyRecyclerView);
        /*
         * 效果列表
         * Effect list
         * */
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mEffectRecyclerView.setLayoutManager(layoutManager);
        mAssetRecycleAdapter = new AssetRecyclerViewAdapter(this);
        mEffectRecyclerView.setAdapter(mAssetRecycleAdapter);
        mEffectRecyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtils.dip2px(this, 6), ScreenUtils.dip2px(this, 8)));
        if (mClipInfoArray.size() > 0) {
            ClipInfo clipInfo = mClipInfoArray.get(0);
            updateOperateMenu(clipInfo);
        }
        //clip 对应的动画集合
        ConcurrentHashMap<Integer, AnimationInfo> fxMap = TimelineData.instance().getmAnimationFxMap();
        mVideoClipFxMap.putAll(fxMap);
    }

    @Override
    protected void initListener() {
        mAssetRecycleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (m_waitFlag) {
                    return;
                }

                if (!mIsImage) {
                    switch (pos) {
                        case 0://trim
                            m_waitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    TrimActivity.class, null, EditActivity.CLIPTRIM_REQUESTCODE);
                            break;
                        case 1: // spilt
                            m_waitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    SpiltActivity.class, null, EditActivity.CLIPSPILTPOINT_REQUESTCODE);
                            break;
                        case 2: // Color correction
                            m_waitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    CorrectionColorActivity.class, null, EditActivity.CLIPCORRECTIONCOLOR_REQUESTCODE);
                            break;
                        case 3: // filter
                            m_waitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    ClipFilterActivity.class, null, EditActivity.CLIPCORRECTIONCOLOR_REQUESTCODE);
                            break;
                        case 4: // Adjustment
                            m_waitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    AdjustActivity.class, null, EditActivity.CLIPADJUST_REQUESTCODE);
                            break;
                        case 5: // speed
                            m_waitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    SpeedActivity.class, null, EditActivity.CLIPSPEED_REQUESTCODE);
                            break;
                        case 6: // volume
                            m_waitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    VolumeActivity.class, null, EditActivity.CLIPVOLUME_REQUESTCODE);
                            break;
                        case 7: // copy
                            copyMediaAsset();
                            break;
                        case 8: // delete
                            deleteMediaAsset();
                            break;
                        default:
                            break;
                    }
                } else {
                    switch (pos) {
                        case 0: // Duration
                            m_waitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    DurationActivity.class, null, EditActivity.PHOTODURATION_REQUESTCODE);
                            break;
                        case 1: // Movement
                            m_waitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    PhotoMovementActivity.class, null, EditActivity.PHOTOMOVE_REQUESTCODE);
                            break;
                        case 2: // Color correction
                            m_waitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    CorrectionColorActivity.class, null, EditActivity.CLIPCORRECTIONCOLOR_REQUESTCODE);
                            break;
                        case 3: // Filter
                            m_waitFlag = true;
                            AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                                    ClipFilterActivity.class, null, EditActivity.CLIPCORRECTIONCOLOR_REQUESTCODE);
                            break;
                        case 4: // copy
                            copyMediaAsset();
                            break;
                        case 5: // delete
                            deleteMediaAsset();
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        mGrallyAdapter.setOnItemSelectedListener(new OnGrallyItemClickListener() {
            @Override
            public void onLeftItemClick(View view, int pos) {
                reAddMediaAsset(pos);
            }

            @Override
            public void onRightItemClick(View view, int pos) {
                reAddMediaAsset(pos);
            }

            @Override
            public void onItemMoved(int fromPosition, int toPosition) {
                // Collections.swap(mClipInfoArray,fromPosition,toPosition);
                swapAnimationInfo(fromPosition,toPosition);
            }

            @Override
            public void onItemDismiss(int position) {
                mClipInfoArray.remove(position);
            }

            @Override
            public void removeall() {
                mClipInfoArray.clear();
            }
        });

        mGrallyScaleHelper.setOnItemSelectedListener(new GrallyScaleHelper.OnGrallyItemSelectListener() {
            @Override
            public void onItemSelect(int pos) {
                if (pos < 0 || pos >= mClipInfoArray.size()) {
                    return;
                }
                mCurrentPos = pos;
                BackupData.instance().setClipIndex(mCurrentPos);
                ClipInfo clipInfo = mClipInfoArray.get(pos);
                updateOperateMenu(clipInfo);
            }
        });

        mEditCommitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimelineData.instance().setClipInfoData(mClipInfoArray);
                TimelineData.instance().setTransitionInfoArray(mTransitionInfoArray);
                TimelineData.instance().setmAnimationFxMap(mVideoClipFxMap);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                AppManager.getInstance().finishActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }

        ArrayList<ClipInfo> addCipInfoList = BackupData.instance().getAddClipInfoList();
        switch (requestCode) {
            case CLIPTRIM_REQUESTCODE:
                break;
            case CLIPSPILTPOINT_REQUESTCODE:
                int spiltPosition = data.getIntExtra("spiltPosition", -1);
                if (spiltPosition != -1) {
                    if ((mTransitionInfoArray != null) && (!mTransitionInfoArray.isEmpty())) {
                        if (spiltPosition <= mTransitionInfoArray.size()) {
                            mTransitionInfoArray.add(spiltPosition, new TransitionInfo());
                        }
                    }
                }
                break;
            case CLIPCORRECTIONCOLOR_REQUESTCODE:
                break;
            case CLIPADJUST_REQUESTCODE:
                break;
            case CLIPSPEED_REQUESTCODE:
                break;
            case CLIPVOLUME_REQUESTCODE:
                break;
            case ADDVIDEO_REQUESTCODE:
                break;
            case PHOTODURATION_REQUESTCODE:
                break;
            case PHOTOMOVE_REQUESTCODE:
                break;
            default:
                break;
        }

        mClipInfoArray = BackupData.instance().getClipInfoData();
        if (addCipInfoList.size() > 0) {
            //此处是从媒体库在选择资源  ， 修改存储的动画的特效数据结构
            addNewAnimationInfo(false,mAddVideoPostion,addCipInfoList.size());
            mClipInfoArray.addAll(mAddVideoPostion, addCipInfoList);
            BackupData.instance().setClipInfoData(mClipInfoArray);
            BackupData.instance().clearAddClipInfoList();
            /*
             * 为新增的素材添加默认转场
             * Add default transitions for new material
             * */
            if (mTransitionInfoArray != null) {
                ArrayList<TransitionInfo> temp = new ArrayList<>();
                int maxTransitionCount = mClipInfoArray.size() - mTransitionInfoArray.size() - 1;
                for (int i = 0; i < maxTransitionCount; i++) {
                    TransitionInfo transitionInfo = new TransitionInfo();
                    temp.add(transitionInfo);
                }
                if (mAddVideoPostion <= mTransitionInfoArray.size()) {
                    mTransitionInfoArray.addAll(mAddVideoPostion, temp);
                }
            }
        }
        mGrallyAdapter.setClipInfoArray(mClipInfoArray);
        mGrallyAdapter.notifyDataSetChanged();
        ClipInfo clipInfo = mClipInfoArray.get(mCurrentPos);
        updateOperateMenu(clipInfo);



    }

    @Override
    protected void onResume() {
        super.onResume();
        m_waitFlag = false;
    }

    private void reAddMediaAsset(int pos) {
        mAddVideoPostion = pos;
        Bundle bundle = new Bundle();
        bundle.putInt("visitMethod", Constants.FROMCLIPEDITACTIVITYTOVISIT);
        BackupData.instance().clearAddClipInfoList();
        AppManager.getInstance().jumpActivityForResult(EditActivity.this, SelectMediaActivity.class, bundle, ADDVIDEO_REQUESTCODE);
    }

    private void copyMediaAsset() {
        if (mClipInfoArray.size() == 0) {
            return;
        }
        int count = mClipInfoArray.size();
        if (mCurrentPos < 0 || mCurrentPos > count) {
            return;
        }
        mClipInfoArray.add(mCurrentPos, mClipInfoArray.get(mCurrentPos).clone());

        //添加一个动画的对象
        addNewAnimationInfo(true,mCurrentPos,1);

        /*
         * 添加转场
         * Add transition
         * */
        if (mTransitionInfoArray != null && mTransitionInfoArray.size()>=mCurrentPos) {
            mTransitionInfoArray.add(mCurrentPos, new TransitionInfo());
        }
        mGrallyAdapter.setSelectPos(mCurrentPos);
        mGrallyAdapter.setClipInfoArray(mClipInfoArray);
        mGrallyAdapter.notifyDataSetChanged();
        /*
         * 复制移动到下一个位置
         * Copy move to next position
         * */
        mGrallyRecyclerView.smoothScrollBy(mGrallyScaleHelper.getmOnePageWidth(), 0);
        //添加一份

    }

    private void deleteMediaAsset() {
        if (mClipInfoArray.size() == 0) {
            return;
        }

        if (mClipInfoArray.size() == 1) {
            String[] deleteVideoTips = getResources().getStringArray(R.array.video_delete_tips);
            Util.showDialog(EditActivity.this, deleteVideoTips[0], deleteVideoTips[1]);
            return;
        }
        int clipCount = mClipInfoArray.size();
        if (mCurrentPos < 0 || mCurrentPos >= clipCount) {
            return;
        }
        /*
         * 删除素材和转场
         * delete material and transition
         * */
        mClipInfoArray.remove(mCurrentPos);

        //删除一项动画item
        removeNewAnimationInfo(mCurrentPos);

        if ((mTransitionInfoArray != null) && !mTransitionInfoArray.isEmpty() && mTransitionInfoArray.size()>mCurrentPos) {
            mTransitionInfoArray.remove((mCurrentPos - 1 >= 0) ? (mCurrentPos - 1) : 0);
        }
        if (mCurrentPos == clipCount - 1) {
            mCurrentPos--;
        }
        mGrallyAdapter.setClipInfoArray(mClipInfoArray);
        mGrallyAdapter.setSelectPos(mCurrentPos);
        mGrallyAdapter.notifyDataSetChanged();
        mGrallyScaleHelper.resetCurrentOffset(mCurrentPos);
        BackupData.instance().setClipIndex(mCurrentPos);
        /*
         * 更新操作菜单
         * Update operation menu
         * */
        ClipInfo clipInfo = mClipInfoArray.get(mCurrentPos);
        updateOperateMenu(clipInfo);
    }

    private boolean isImage(ClipInfo clipInfo) {
        if (clipInfo != null) {
            NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(clipInfo.getFilePath());
            if (avFileInfo != null) {
                if (avFileInfo.getAVFileType() == NvsAVFileInfo.AV_FILE_TYPE_IMAGE) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateOperateMenu(ClipInfo clipInfo) {
        if (mStreamingContext != null && clipInfo != null) {
            mIsImage = isImage(clipInfo);
            if (mAssetRecycleAdapter != null) {
                mAssetRecycleAdapter.updateData(mIsImage ? mArrayAssetInfoImage : mArrayAssetInfoVideo);
            }
        }
    }

    /**
     * 添加一个动画item
     * 添加的逻辑就是构建一个新的集合，遍历旧的集合的数据，key+size 作为新的key
     * 如果需要添加一个动画特效对象，则添加到mCurrentPosition 位置
     *
     * @param addItem 是否需要添加特效
     * @param mCurrentPos 当前选择要添加的位置
     * @param size 操作的数量
     */
    private void addNewAnimationInfo(boolean addItem ,int mCurrentPos,int size) {
        //如果当前就有动画效果，则复制一份
        if(null != mVideoClipFxMap && mVideoClipFxMap.size()>0 ){
            //构建一个新的集合存储新的数据;
            ConcurrentHashMap<Integer, AnimationInfo> tempMap = new ConcurrentHashMap<>();
            Set<Integer>keySet = mVideoClipFxMap.keySet();
            for(Integer key : keySet){
                if(key >= mCurrentPos){
                    //遍历旧的集合的数据，key+size 作为新的key
                    AnimationInfo animationInfo = mVideoClipFxMap.get(key);
                    tempMap.put((key+size),animationInfo);
                }else{
                    AnimationInfo animationInfo = mVideoClipFxMap.get(key);
                    tempMap.put(key,animationInfo);
                }
            }
            if(addItem){
                AnimationInfo animationInfo = mVideoClipFxMap.get(mCurrentPos);
                AnimationInfo newOne = new AnimationInfo();
                newOne.setmAssetType(animationInfo.getmAssetType());
                newOne.setmAnimationIn(animationInfo.getmAnimationIn());
                newOne.setmAnimationOut(animationInfo.getmAnimationOut());
                newOne.setmPackageId(animationInfo.getmPackageId());
                tempMap.put(mCurrentPos,newOne);
            }
            //然后重新赋值
            mVideoClipFxMap.clear();
            mVideoClipFxMap.putAll(tempMap);
        }

    }

    /**
     * 移除一个动画item
     * 移除的逻辑就是遍历一遍所有的key  因为现在key对应着 mCurrentPos
     * 然后删除 key = 所有比mCurrentPos 的item
     * 所有比mCurrentPos 大 的key  都执行-1 操作
     * @param mCurrentPos 要删除的项的index
     */
    private void removeNewAnimationInfo(int mCurrentPos) {
        //如果当前就有动画效果，则复制一份
        if(null != mVideoClipFxMap && mVideoClipFxMap.size()>0){
            //构建一个新的集合存储新的数据;
            ConcurrentHashMap<Integer, AnimationInfo> tempMap = new ConcurrentHashMap<>();

            //先移除对应项
            if(mVideoClipFxMap.containsKey(mCurrentPos)){
                 mVideoClipFxMap.remove(mCurrentPos);
             }

            //循环遍历 ，把所有的大于当前mCurrentPos的键值对的 key-1
            Set<Integer>keySet = mVideoClipFxMap.keySet();
            for(Integer key : keySet){
                //key 循环一遍map
                //这项以后的item 数据 key-1存储
                if(key > mCurrentPos){
                    AnimationInfo animationInfo = mVideoClipFxMap.get(key);
                    tempMap.put((key-1),animationInfo);
                }else{
                    AnimationInfo animationInfo = mVideoClipFxMap.get(key);
                    tempMap.put(key,animationInfo);
                }
            }
            //重新赋值
            mVideoClipFxMap.clear();
            mVideoClipFxMap.putAll(tempMap);
        }

    }

    /**
     * 交换两个item 位置
     * @param fromPosition 起点位置
     * @param toPosition 终点位置
     */
    private void swapAnimationInfo(int fromPosition,int toPosition) {
        //如果当前就有动画效果，则复制一份
        if(null != mVideoClipFxMap && mVideoClipFxMap.size()>0 && mVideoClipFxMap.containsKey(fromPosition) &&mVideoClipFxMap.containsKey(toPosition) ){
            AnimationInfo animationInfoFrom = mVideoClipFxMap.get(fromPosition);
            AnimationInfo animationInfoTo = mVideoClipFxMap.get(toPosition);
            mVideoClipFxMap.put(fromPosition,animationInfoTo);
            mVideoClipFxMap.put(toPosition,animationInfoFrom);
        }

    }
}
