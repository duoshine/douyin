package com.duoshine.douyin.meishe.sdkdemo.edit.transition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.GridSpacingItemDecoration;
import com.duoshine.douyin.meishe.sdkdemo.base.BaseActivity;
import com.duoshine.douyin.meishe.sdkdemo.download.AssetDownloadActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.VideoFragment;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.FilterItem;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.duoshine.douyin.meishe.sdkdemo.utils.AppManager;
import com.duoshine.douyin.meishe.sdkdemo.utils.OnSeekBarChangeListenerAbs;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;
import com.duoshine.douyin.meishe.sdkdemo.utils.TimelineUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.Util;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TransitionInfo;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;
import com.meicam.sdk.NvsVideoTransition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static com.duoshine.douyin.meishe.sdkdemo.utils.TimelineUtil.TIME_BASE;


public class TransitionActivity extends BaseActivity {
    private static final String TAG = "TransitionActivity";
    private static final int TRANSITIONREQUESTLIST = 105;
    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;
    private NvsVideoTrack mVideoTrack;
    private VideoFragment mVideoFragment;
    private CustomTitleBar mTitleBar;
    private LinearLayout mBottomLayout;

    private RecyclerView mThumbRecyclerView;
    private RecyclerView mTransitionRecyclerView;
    private RelativeLayout mFinishLayout;
    private RelativeLayout mMoreDownload;
    private ImageView mDowanloadImage;
    private TextView mDowanloadMoreText;
    private TransitionAdapter mTransitionAdapter;
    private ThumbAdapter mThumbAdapter;
    /**
     * 媒体数据路径集合，用来生成缩略图
     * Collection of media data paths used to generate thumbnails
     */
    private List<String> mFileData = new ArrayList<>();
    private ArrayList<FilterItem> mFilterList = new ArrayList<>();
    /**
     * 当前选中转场
     */
    private TransitionInfo mTransitionInfo = new TransitionInfo();
    /**
     * 转场集合
     * Currently selected transition
     */
    private ArrayList<TransitionInfo> mTransitionInfoArray;

    private int mAssetType = NvAsset.ASSET_VIDEO_TRANSITION;
    private NvAssetManager mAssetManager;
    /**
     * 转场时间
     * Transition time
     */
    private RecyclerView mTimeChangeRecyclerView;
    private IntervalChangeAdapter mTimeChangeAdapter;
    private RelativeLayout mParameterAdjustLayout;
    private EditText mIntervalChangeEdittext;
    private ImageView mIntervalChangeIdentify;
    private ImageView mIntervalChangeCancel;
    private int[] mIntervalArray = {1, 3, 5, 7};
    private int mBeforeCount;
    private LinearLayout mCustomInputLayout;
    private boolean mHasAddThemeIcon;

    private RelativeLayout mIntervalSettingLayout;
    private SeekBar mIntervalSettingSeekBar;
    private TextView mIntervalSettingTip;
    private float mIntervalTime;
    private ImageView mIntervalIdentify;
    private ImageView mIntervalCancel;


    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_transition;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);
        mThumbRecyclerView = (RecyclerView) findViewById(R.id.thumbRecyclerView);
        mTransitionRecyclerView = (RecyclerView) findViewById(R.id.transitionRecyclerView);
        mFinishLayout = (RelativeLayout) findViewById(R.id.finishLayout);
        mMoreDownload = (RelativeLayout) findViewById(R.id.download_more_btn);
        mDowanloadImage = (ImageView) findViewById(R.id.dowanloadImage);
        mDowanloadMoreText = (TextView) findViewById(R.id.dowanloadMoreText);
        /*
        * 转场时间修改
        * Transition time modification
        * */
        mParameterAdjustLayout = (RelativeLayout) findViewById(R.id.bottom_parameter_adjust_layout);
        mCustomInputLayout = (LinearLayout) findViewById(R.id.custom_input_layout);
        mTimeChangeRecyclerView = (RecyclerView) findViewById(R.id.transition_interval_change_rv);
        mIntervalChangeEdittext = (EditText) findViewById(R.id.transition_interval_change_edittext);
        mIntervalChangeIdentify = (ImageView) findViewById(R.id.transition_change_interval_identify_iv);
        mIntervalChangeIdentify.setOnClickListener(this);
        mIntervalChangeCancel = (ImageView) findViewById(R.id.transition_change_interval_cancel_iv);
        mIntervalChangeCancel.setOnClickListener(this);

        mIntervalSettingLayout = (RelativeLayout) findViewById(R.id.transition_interval_set_layout);
        mIntervalSettingSeekBar = (SeekBar) findViewById(R.id.transition_seek_bar);
        mIntervalSettingTip = (TextView) findViewById(R.id.tv_transition_set_tip);
        mIntervalIdentify = (ImageView) findViewById(R.id.iv_transition_change_interval_identify);
        mIntervalIdentify.setOnClickListener(this);
        mIntervalCancel = (ImageView) findViewById(R.id.iv_transition_change_interval_cancel);
        mIntervalCancel.setOnClickListener(this);

    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.transition);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        mTimeline = TimelineUtil.createTimeline();
        if (mTimeline != null) {
            mVideoTrack = mTimeline.getVideoTrackByIndex(0);
        }
        mAssetManager = NvAssetManager.sharedInstance();
        mAssetManager.searchLocalAssets(mAssetType);
        String bundlePath = "transition";
        mAssetManager.searchReservedAssets(mAssetType, bundlePath);

        initVideoFragment();
        initTransitionDataList();
        handleTransitions();
        initThumbRecyclerView();
        initTransitionRecyclerView();
        initTimeChangeRecyclerView();
    }

    private void handleTransitions() {
        /*
        * 添加当前所有转场到临时的集合中
        * Add all current transitions to the temporary collection
        * */
        mTransitionInfoArray = new ArrayList<>();
        int clipCount = mVideoTrack.getClipCount();
        for (int i = 0; i < clipCount - 1; i++) {
            NvsVideoTransition transition = mVideoTrack.getTransitionBySourceClipIndex(i);
            TransitionInfo transitionInfo = new TransitionInfo();
            transitionInfo.setTransitionId("");
            if (transition != null) {
                transitionInfo.setTransitionInterval(transition.getVideoTransitionDuration());
                transitionInfo.setVideoTransition(transition);
                int videoTransitionType = transition.getVideoTransitionType();
                transitionInfo.setTransitionMode(videoTransitionType);
                if (videoTransitionType == NvsVideoTransition.VIDEO_TRANSITION_TYPE_BUILTIN) {
                    String builtinVideoTransitionName = transition.getBuiltinVideoTransitionName();
                    transitionInfo.setTransitionId(builtinVideoTransitionName);
                } else if (videoTransitionType == NvsVideoTransition.VIDEO_TRANSITION_TYPE_PACKAGE) {
                    String videoTransitionPackageId = transition.getVideoTransitionPackageId();
                    transitionInfo.setTransitionId(videoTransitionPackageId);
                }
            }
            mTransitionInfoArray.add(transitionInfo);
        }

        if (!mTransitionInfoArray.isEmpty()) {
            /*
            * 生成FilterItem集合
            * Generate FilterItem collection
            * */
            HashSet<String> hashSetFilterId = new HashSet<>();
            for (FilterItem filterItem : mFilterList) {
                int filterMode = filterItem.getFilterMode();
                if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                    hashSetFilterId.add(filterItem.getFilterName());
                } else {
                    /*
                    * 注意：转场中使用的id为 filtername 和 packageid
                    * Note: the ids used in the transition are filtername and packageid
                    * */
                    hashSetFilterId.add(filterItem.getPackageId());
                }
            }

            /*
            * 建立两种图片的映射关系
            * Establish a mapping relationship between two pictures
            * */
            HashMap<String, Integer> buildInMap = new HashMap<>();
            HashMap<String, String> packageMap = new HashMap<>();
            for (FilterItem filterItem : mFilterList) {
                int filterMode = filterItem.getFilterMode();
                if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                    buildInMap.put(filterItem.getFilterName(), filterItem.getImageId());
                } else {
                    packageMap.put(filterItem.getPackageId(), filterItem.getImageUrl());
                }
            }
            /*
            * 遍历是否在FilterItem集合中存在(并处理图片)
            * Iterate over whether the FilterItem collection exists (and process the picture)
            * */
            for (TransitionInfo transitionInfo : mTransitionInfoArray) {
                int transitionMode = transitionInfo.getTransitionMode();
                /*
                *  判断是否添加主题ICON, 并修改不存在的packageId为theme
                * Determine whether to add the theme ICON, and modify the non-existing packageId to theme
                * */
                judgeIfAddThemeIcon(hashSetFilterId, transitionInfo);
                if (transitionMode == TransitionInfo.TRANSITIONMODE_BUILTIN) {
                    Integer integer = buildInMap.get(transitionInfo.getTransitionId());
                    if (integer != null) {
                        transitionInfo.setM_imageId(integer);
                    }
                    /*
                    * 处理theme图标
                    * Handle theme icon
                    * */
                    if (TextUtils.equals("theme", transitionInfo.getTransitionId())) {
                        transitionInfo.setM_imageId(R.mipmap.default_theme);
                    }
                } else {
                    String imageUrl = packageMap.get(transitionInfo.getTransitionId());
                    transitionInfo.setM_imageUrl(imageUrl);
                }

                if (TextUtils.equals("theme", transitionInfo.getTransitionId())) {
                    transitionInfo.setM_imageUrl("file:///android_asset/default_theme.png");
                }
            }
            mTransitionInfo = mTransitionInfoArray.get(0);
        }
    }

    private void judgeIfAddThemeIcon(HashSet<String> hashSetFilterId, TransitionInfo transitionInfo) {
        if ((hashSetFilterId == null) || hashSetFilterId.isEmpty() || (transitionInfo == null)) {
            return;
        }

        String transitionId = transitionInfo.getTransitionId();
        if (!(hashSetFilterId.contains(transitionId))) {
            /*
            * 修改themeID
            * Modify themeID
            * */
            transitionInfo.setTransitionId("theme");
            /*
            * 添加themeIcon
            * Add themeIcon
            * */
            if (!mHasAddThemeIcon) {
                FilterItem filterItem = new FilterItem();
                filterItem.setFilterMode(FilterItem.FILTERMODE_PACKAGE);
                filterItem.setPackageId("theme");
                filterItem.setFilterDesc(getResources().getString(R.string.theme));
                filterItem.setImageUrl("file:///android_asset/default_theme.png");
                mFilterList.add(1, filterItem);
                mHasAddThemeIcon = true;
            }
        }
    }

    private void initTimeChangeRecyclerView() {
        mTimeChangeAdapter = new IntervalChangeAdapter(this);
        GridLayoutManager layoutManager = new GridLayoutManager(TransitionActivity.this, mIntervalArray.length);
        mTimeChangeRecyclerView.setLayoutManager(layoutManager);
        mTimeChangeRecyclerView.addItemDecoration(new GridSpacingItemDecoration(TransitionActivity.this, mIntervalArray.length, (int) getResources().getDimension(R.dimen.dp62)));
        mTimeChangeAdapter.setTimeChangeSet(mIntervalArray);
        mTimeChangeRecyclerView.setAdapter(mTimeChangeAdapter);
        mTimeChangeAdapter.setOnItemClickListener(new IntervalChangeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, int intervalValue) {
                if (mIntervalChangeEdittext != null) {
                    mIntervalChangeEdittext.setText("");
                    mIntervalChangeEdittext.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) TransitionActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        LinearLayout.MarginLayoutParams params = (LinearLayout.MarginLayoutParams) mCustomInputLayout.getLayoutParams();
        params.setMargins(getTimeChangeViewMargin(), params.topMargin, params.rightMargin, params.bottomMargin);
        mCustomInputLayout.setLayoutParams(params);
    }

    private int getTimeChangeViewMargin() {
        int screenWidth = ScreenUtils.getScreenWidth(TransitionActivity.this);
        return screenWidth / 4 - getResources().getDimensionPixelSize(R.dimen.dp50);
    }

    @Override
    protected void initListener() {
        mDowanloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMoreDownload.callOnClick();
            }
        });
        mDowanloadMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMoreDownload.callOnClick();
            }
        });

        mFinishLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimelineData.instance().setTransitionInfoArray(mTransitionInfoArray);
                removeTimeline();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                AppManager.getInstance().finishActivity();
            }
        });
        mMoreDownload.setOnClickListener(this);
        // 监听EditText的文本变化
        mIntervalChangeEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mBeforeCount = count;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ((mTimeChangeAdapter != null) && (mBeforeCount == 0) && (count > mBeforeCount)) {
                    mTimeChangeAdapter.setSelectPos(-1);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mIntervalSettingSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListenerAbs() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mIntervalTime = (float)(Math.round((progress + 30) / 100.00f * 10))/10 ;
                mIntervalSettingTip.setText(mIntervalTime + "s");
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.download_more_btn:
                mMoreDownload.setClickable(false);
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreTransition);
                bundle.putInt("assetType", NvAsset.ASSET_VIDEO_TRANSITION);
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(),
                        AssetDownloadActivity.class, bundle, TRANSITIONREQUESTLIST);
                break;
            case R.id.transition_change_interval_identify_iv:
                // FIXME: 2019/10/17 0017 临时修改转场
                // 应用转场时间
/*                int transitionPos = mThumbAdapter.getSelectPos( );
                TransitionInfo transitionInfo = mTransitionInfoArray.get(transitionPos);
                NvsVideoTransition videoTransition = transitionInfo.getVideoTransition( );
                if (videoTransition != null) {
                    // 设置转场时间
                    int selectPos = mTimeChangeAdapter.getSelectPos( );
                    boolean hasSelect = (selectPos > 0 && selectPos < mIntervalArray.length);
                    if (hasSelect) {
                        videoTransition.setVideoTransitionDuration(mIntervalArray[selectPos] * TIME_BASE);
                        transitionInfo.setTransitionInterval(mIntervalArray[selectPos] * TIME_BASE);
                    } else {
                        String inputValue = mIntervalChangeEdittext.getText( ).toString( ).trim( );
                        boolean hasInput = !TextUtils.isEmpty(inputValue);
                        if (hasInput) {
                            videoTransition.setVideoTransitionDuration((long) (Float.parseFloat(inputValue) * TIME_BASE));
                            transitionInfo.setTransitionInterval((long) (Float.parseFloat(inputValue) * TIME_BASE));
                        }
                    }
                }
                hideParameterChangeLayout( );*/
                break;

            case R.id.transition_change_interval_cancel_iv:
//                hideParameterChangeLayout();
                break;
            case R.id.iv_transition_change_interval_identify:
                if (mIntervalSettingLayout.getVisibility() == View.VISIBLE) {
                    mIntervalSettingLayout.setVisibility(View.GONE);
                }
                mTransitionInfo.setTransitionInterval((long) (mIntervalTime * 1000000));
                TimelineUtil.setTransition(mTimeline, mTransitionInfo, mThumbAdapter.getSelectPos());
                break;
            case R.id.iv_transition_change_interval_cancel:
                if (mIntervalSettingLayout.getVisibility() == View.VISIBLE) {
                    mIntervalSettingLayout.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    private boolean hideParameterChangeLayout() {
        boolean needHide = false;
        if (mParameterAdjustLayout.getVisibility() == View.VISIBLE) {
            mParameterAdjustLayout.setVisibility(View.GONE);
            needHide = true;
        }
        if (mBottomLayout.getVisibility() == View.GONE) {
            mBottomLayout.setVisibility(View.VISIBLE);
            needHide = true;
        }
        return needHide;
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case TRANSITIONREQUESTLIST:
                initTransitionDataList();
                mTransitionAdapter.setFilterList(mFilterList);
                mTransitionAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline),
                        NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
            }
        });
        mVideoFragment.setTimeline(mTimeline);
        Bundle bundle = new Bundle();
        bundle.putInt("titleHeight", mTitleBar.getLayoutParams().height);
        bundle.putInt("bottomHeight", mBottomLayout.getLayoutParams().height);
        bundle.putBoolean("playBarVisible", true);
        bundle.putInt("ratio", TimelineData.instance().getMakeRatio());
        mVideoFragment.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .add(R.id.video_layout, mVideoFragment)
                .commit();
        getFragmentManager().beginTransaction().show(mVideoFragment);
    }

    private void initThumbRecyclerView() {
        if (mVideoTrack == null) {
            return;
        }

        for (int i = 0, count = mVideoTrack.getClipCount(); i < count; i++) {
            mFileData.add(mVideoTrack.getClipByIndex(i).getFilePath());
        }

        mThumbAdapter = new ThumbAdapter(this, mFileData, mTransitionInfoArray);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mThumbRecyclerView.setLayoutManager(linearLayoutManager);
        mThumbRecyclerView.setAdapter(mThumbAdapter);
        mThumbAdapter.setOnItemClickListener(new ThumbAdapter.OnItemClickListener() {
            @Override
            public void onThumbItemClick(View view, int position) {
                if (mVideoTrack == null) {
                    return;
                }

                int videoCount = mVideoTrack.getClipCount();
                if (videoCount < position) {
                    return;
                }

                NvsVideoClip clip = mVideoTrack.getClipByIndex(position);
                if (clip == null) {
                    return;
                }

                long inPoint = clip.getInPoint();
                mVideoFragment.playVideo(inPoint, -1);
            }

            @Override
            public void onTransitionItemClick(View view, int position) {
                if (mVideoTrack == null) {
                    return;
                }
//                playTransition(position);
                mTransitionInfo = mTransitionInfoArray.get(position);
                if (mTransitionAdapter != null) {
                    mTransitionAdapter.setSelectPos(getSelectedFilterPos());
                }
                if (mIntervalSettingLayout.getVisibility() != View.VISIBLE) {
                    mIntervalSettingLayout.setVisibility(View.VISIBLE);
                }

                int progress = (int)mTransitionInfo.getTransitionInterval() / 10000 - 30;
                mIntervalTime = (float)(Math.round((progress + 30) / 100.00f * 10))/10 ;
                mIntervalSettingTip.setText(mIntervalTime + "s");
                mIntervalSettingSeekBar.setProgress(progress);
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

    private void initTransitionDataList() {
        int[] resImages = {
                R.mipmap.fade, R.mipmap.turning, R.mipmap.swap, R.mipmap.stretch_in,
                R.mipmap.page_curl, R.mipmap.lens_flare, R.mipmap.star, R.mipmap.dip_to_black,
                R.mipmap.dip_to_white, R.mipmap.push_to_right, R.mipmap.push_to_left, R.mipmap.upper_left_into
        };

        int[] resNames = {
                R.string.trans_fade, R.string.trans_turning, R.string.trans_swap, R.string.trans_stretch_in,
                R.string.trans_page_curl, R.string.trans_lens_flare, R.string.trans_star, R.string.trans_dip_to_black,
                R.string.trans_dip_to_white, R.string.trans_push_to_right, R.string.trans_push_to_left, R.string.trans_upper_left_into
        };

        mFilterList.clear();
        FilterItem filterItem = new FilterItem();
        filterItem.setFilterMode(FilterItem.FILTERMODE_BUILTIN);
        filterItem.setFilterName("");
        filterItem.setImageId(R.mipmap.no);
        mFilterList.add(filterItem);
        /*
        * 添加内置转场信息到列表中
        * Add built-in transition information to the list
        * */
        List<String> builtinTransitionList = mStreamingContext.getAllBuiltinVideoTransitionNames();
        for (int i = 0, size = builtinTransitionList.size(); i < size; i++) {
            String transitionName = builtinTransitionList.get(i);
            FilterItem newFilterItem = new FilterItem();
            newFilterItem.setFilterName(transitionName);
            if (i < resImages.length) {
                int imageId = resImages[i];
                newFilterItem.setImageId(imageId);
            }
            newFilterItem.setFilterDesc(getResources().getString(resNames[i]));
            newFilterItem.setFilterMode(FilterItem.FILTERMODE_BUILTIN);
            mFilterList.add(newFilterItem);
        }
        /*
        * 添加info.txt转场适用比例到Asset列表中
        * Add info.txt transition applicable ratio to Asset list
        * */
        ArrayList<NvAsset> transitionList = getLocalData();
        if (isZh(this)) {
            String bundlePath = "transition/info_Zh.txt";
            Util.getBundleFilterInfo(this, transitionList, bundlePath);
        } else {
            String bundlePath = "transition/info.txt";
            Util.getBundleFilterInfo(this, transitionList, bundlePath);
        }
        int ratio = TimelineData.instance().getMakeRatio();
        for (NvAsset asset : transitionList) {
            /*
            * 去除不适用当前编辑视频比例的转场特效
            * Remove transition effects that do not apply to the current editing video scale
            * */
            if ((ratio & asset.aspectRatio) == 0) {
                continue;
            }

            FilterItem newFilterItem = new FilterItem();
            if (asset.isReserved) {
                newFilterItem.setFilterMode(FilterItem.FILTERMODE_BUNDLE);
                String coverPath = "file:///android_asset/transition/";
                coverPath += asset.uuid;
                coverPath += ".png";
                asset.coverUrl = coverPath;
            } else {
                newFilterItem.setFilterMode(FilterItem.FILTERMODE_PACKAGE);
            }
            newFilterItem.setFilterName(asset.name);
            newFilterItem.setFilterDesc(asset.name);
            newFilterItem.setPackageId(asset.uuid);
            newFilterItem.setImageUrl(asset.coverUrl);
            mFilterList.add(newFilterItem);
        }

        int position = getSelectedFilterPos();
        if (mTransitionAdapter != null) {
            mTransitionAdapter.setSelectPos(position);
        }
    }

    /**
     * 获取当前选中转场的位置。注意：FilterItem 需要根据资源类型区分getFilterName和getFilterId。
     * Gets the position of the currently selected transition. Note: FilterItem needs to distinguish getFilterName and getFilterId according to the resource type.
     */
    private int getSelectedFilterPos() {
        if ((mFilterList == null) || (mFilterList.size() == 0)) {
            return -1;
        }

        if (mTransitionInfo != null) {
            String transitionName = mTransitionInfo.getTransitionId();
            if (TextUtils.isEmpty(transitionName)) {
                return 0;
            }

            for (int i = 0, size = mFilterList.size(); i < size; i++) {
                FilterItem newFilterItem = mFilterList.get(i);
                if (newFilterItem == null) {
                    continue;
                }

                int filterMode = newFilterItem.getFilterMode();
                String filterName;
                if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                    filterName = newFilterItem.getFilterName();
                } else {
                    filterName = newFilterItem.getPackageId();
                }

                if (transitionName.equals(filterName)) {
                    return i;
                }
            }
        }
        return 0;
    }

    private void initTransitionRecyclerView() {
        mTransitionAdapter = new TransitionAdapter(this);
        mTransitionAdapter.setFilterList(mFilterList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTransitionRecyclerView.setLayoutManager(linearLayoutManager);
        mTransitionRecyclerView.setAdapter(mTransitionAdapter);

        int selectedFilterPos = getSelectedFilterPos();
        mTransitionAdapter.setSelectPos(selectedFilterPos);

        mTransitionAdapter.setOnItemClickListener(new TransitionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if ((position < 0) || (position >= mFilterList.size())) {
                    return;
                }
                if (position == 0) {
                    mTransitionInfo.setTransitionMode(TransitionInfo.TRANSITIONMODE_BUILTIN);
                    mTransitionInfo.setTransitionId("");
                } else {
                    FilterItem filterItem = mFilterList.get(position);
                    int filterMode = filterItem.getFilterMode();
                    if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                        String filterName = filterItem.getFilterName();
                        mTransitionInfo.setTransitionMode(TransitionInfo.TRANSITIONMODE_BUILTIN);
                        mTransitionInfo.setTransitionId(filterName);
                    } else if (filterMode == FilterItem.FILTERMODE_BUNDLE) {
                        String packageId = filterItem.getPackageId();
                        mTransitionInfo.setTransitionMode(TransitionInfo.TRANSITIONMODE_PACKAGE);
                        mTransitionInfo.setTransitionId(packageId);
                    } else {
                        String packageId = filterItem.getPackageId();
                        mTransitionInfo.setTransitionMode(TransitionInfo.TRANSITIONMODE_PACKAGE);
                        mTransitionInfo.setTransitionId(packageId);
                    }
                }
                if (mThumbAdapter != null) {
                    TimelineUtil.setTransition(mTimeline, mTransitionInfo, mThumbAdapter.getSelectPos());
                    playTransition(mThumbAdapter.getSelectPos());
                }
            }

            @Override
            public void onResetTransition(FilterItem filterItem) {
                /*
                * 点击替换转场之后，更新mTransitionInfoArray中保留的转场数据
                * After clicking Replace Transition, update the transition data retained in mTransitionInfoArray
                * */
                TransitionInfo tempItem = mTransitionInfoArray.get(mThumbAdapter.getSelectPos());
                //
                int filterMode = filterItem.getFilterMode();
                if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                    String filterName = filterItem.getFilterName();
                    tempItem.setTransitionMode(TransitionInfo.TRANSITIONMODE_BUILTIN);
                    tempItem.setTransitionId(filterName);
                } else if (filterMode == FilterItem.FILTERMODE_BUNDLE) {
                    String packageId = filterItem.getPackageId();
                    tempItem.setTransitionMode(TransitionInfo.TRANSITIONMODE_PACKAGE);
                    tempItem.setTransitionId(packageId);
                } else {
                    String packageId = filterItem.getPackageId();
                    tempItem.setTransitionMode(TransitionInfo.TRANSITIONMODE_PACKAGE);
                    tempItem.setTransitionId(packageId);
                }
                //
                if (filterItem.getFilterMode() == FilterItem.FILTERMODE_BUILTIN) {
                    tempItem.setM_imageId(filterItem.getImageId());
                } else {
                    tempItem.setM_imageUrl(filterItem.getImageUrl());
                }
                /*
                * 更新新的转场信息到clone列表中
                * Update new transition information to clone list
                * */
                if (mTransitionInfo != null) {
                    tempItem.setTransitionInterval(mTransitionInfo.getTransitionInterval());
                    tempItem.setVideoTransition(mTransitionInfo.getVideoTransition());
                }
                mThumbAdapter.setTransitionFilterItem(mTransitionInfoArray);
                mThumbAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSameItemClick(int position) {
                if (position == 0) {
                    return;
                }
                // FIXME: 2019/10/17 0017 临时修改转场
                /*mTimeChangeAdapter.setSelectPos(-1);
                String intervalValue = ((float) (mTransitionInfoArray.get(mThumbAdapter.getSelectPos( )).getTransitionInterval( ))) / TIME_BASE + "";
                mIntervalChangeEdittext.setText(intervalValue);
                // 修改转场时间页面
                if (mParameterAdjustLayout.getVisibility( ) == View.GONE) {
                    mParameterAdjustLayout.setVisibility(View.VISIBLE);
                }
                if (mBottomLayout.getVisibility( ) == View.VISIBLE) {
                    mBottomLayout.setVisibility(View.GONE);
                }*/
            }
        });
    }

    private ArrayList<NvAsset> getBundleData() {
        return mAssetManager.getReservedAssets(mAssetType, NvAsset.AspectRatio_All, 0);
    }

    private ArrayList<NvAsset> getLocalData() {
        return mAssetManager.getUsableAssets(mAssetType, NvAsset.AspectRatio_All, 0);
    }

    @Override
    public void onBackPressed() {
        if (hideParameterChangeLayout()) {
            return;
        }
        removeTimeline();
        super.onBackPressed();
        AppManager.getInstance().finishActivity();
    }

    private void removeTimeline() {
        TimelineUtil.removeTimeline(mTimeline);
        mTimeline = null;
    }

    private void playTransition(int index) {
        long halfInterval = TIME_BASE;
        if (mTransitionInfoArray != null && index < mTransitionInfoArray.size()) {
            TransitionInfo transitionInfo = mTransitionInfoArray.get(index);
            if (transitionInfo != null) {
                long transitionInterval = transitionInfo.getTransitionInterval();
                /*
                * 转场预览时间变为前后各预览transitionInterval的时间，避免出现转场预览不全的问题
                * The transition preview time becomes the transition Interval time before and
                * after each transition to avoid the problem of incomplete transition preview
                * */
                halfInterval = transitionInterval;
            }
        }
        int videoCount = mVideoTrack.getClipCount();
        if (videoCount < index + 1) {
            return;
        }

        NvsVideoClip clip = mVideoTrack.getClipByIndex(index);
        if (clip == null) {
            return;
        }

        long inPointBefore = clip.getInPoint();
        long outPointBefore = clip.getOutPoint();

        NvsVideoClip nextClip = mVideoTrack.getClipByIndex(index + 1);
        if (nextClip == null) {
            return;
        }

        long inPointNext = nextClip.getInPoint();
        long outPointNext = nextClip.getOutPoint();
        outPointBefore -= halfInterval;
        inPointNext += halfInterval;
        if (outPointBefore < inPointBefore) {
            outPointBefore = inPointBefore;
        }
        if (inPointNext > outPointNext) {
            inPointNext = outPointNext;
        }

        mVideoFragment.playVideo(outPointBefore, inPointNext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMoreDownload.setClickable(true);
    }
}
