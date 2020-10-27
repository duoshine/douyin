package com.duoshine.douyin.meishe.sdkdemo.capture;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duoshine.douyin.BuildConfig;
import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.base.BasePermissionActivity;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.BeautyData;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.BeautyFxArgs;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.ComposeEffectContent;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.ComposeMakeup;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.FilterArgs;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.MakeupData;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.MakeupEffectContent;
import com.duoshine.douyin.meishe.sdkdemo.dialog.PropsEffectDialog;
import com.duoshine.douyin.meishe.sdkdemo.dialog.TopMoreDialog;
import com.duoshine.douyin.meishe.sdkdemo.download.AssetDownloadActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.VideoEditActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.FilterItem;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.Props;
import com.duoshine.douyin.meishe.sdkdemo.utils.AppManager;
import com.duoshine.douyin.meishe.sdkdemo.utils.AssetFxUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.Constants;
import com.duoshine.douyin.meishe.sdkdemo.utils.Logger;
import com.duoshine.douyin.meishe.sdkdemo.utils.MediaScannerUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.ParameterSettingValues;
import com.duoshine.douyin.meishe.sdkdemo.utils.PathUtils;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;
import com.duoshine.douyin.meishe.sdkdemo.utils.TimeFormatUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.ToastUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.Util;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.VideoClipFxInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.permission.PermissionDialog;
import com.duoshine.douyin.meishe.sdkdemo.view.FilterView;
import com.duoshine.douyin.meishe.sdkdemo.view.HorizontalIndicatorSeekBar;
import com.duoshine.douyin.meishe.sdkdemo.view.MagicProgress;
import com.duoshine.douyin.meishe.sdkdemo.view.MakeUpView;
import com.meicam.sdk.NvsAVFileInfo;
import com.meicam.sdk.NvsAssetPackageManager;
import com.meicam.sdk.NvsCaptureVideoFx;
import com.meicam.sdk.NvsLiveWindow;
import com.meicam.sdk.NvsMakeupEffectInfo;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsVideoFrameRetriever;
import com.meicam.sdk.NvsVideoStreamInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.duoshine.douyin.meishe.sdkdemo.utils.Constants.BUILD_HUMAN_AI_TYPE_MS;
import static com.duoshine.douyin.meishe.sdkdemo.utils.Constants.HUMAN_AI_TYPE_MS;
import static com.duoshine.douyin.meishe.sdkdemo.utils.Constants.HUMAN_AI_TYPE_NONE;


/**
 * CaptureActivity class
 *
 * @author zd
 * @date 2018-06-05
 */
public class CaptureActivity extends BasePermissionActivity implements NvsStreamingContext.CaptureDeviceCallback, NvsStreamingContext.CaptureRecordingDurationCallback, NvsStreamingContext.CaptureRecordingStartedCallback {

    private static final String TAG = CaptureActivity.class.getSimpleName();

    private static final int REQUEST_FILTER_LIST_CODE = 110;
    private static final int ARFACE_LIST_REQUES_CODE = 111;
    private static final boolean OPEN_ALL_SWITCH = true; //打开所有开关,需要直接查看效果时，打开

    private NvsLiveWindow mLiveWindow;
    private NvsStreamingContext mStreamingContext;
    private LinearLayout mFunctionButtonLayout;
    private ImageView mIvExit, mIvMore, mIvChangeCamera;

    private TextView mTvMakeup, mTvBeauty, mTvProps, mTvFilter;
    private ImageView mIvMakeup, mIvBauty, mIvProps, mIvFilter;
    private LinearLayout mExposureLayout;
    private LinearLayout mLlMakeupLayout;
    private LinearLayout mBeautyLayout;
    private LinearLayout mFilterLayout;
    private LinearLayout mFuLayout;
    private RelativeLayout mStartLayout;
    private FrameLayout mFlStartRecord;
    private TextView mStartText;
    private ImageView mDelete;
    private ImageView mNext;
    private TextView mRecordTime;

    private ImageView mImageAutoFocusRect;
    //private RelativeLayout mBottomLayout;
    private LinearLayout mAdjustColorLayout, mSharpenLayout;
    private Switch mAdjustColorSwitch, mSharpenSwitch;

    /**
     * 拍照or视频
     * Photo or video
     */
    private RelativeLayout mSelectLayout, mRlPhotosLayout;
    private LinearLayout mRecordTypeLayout;
    private FrameLayout mFlMiddleParent;
    private FrameLayout mFlBottomParent;
    private View mTypeRightView;
    private TextView mTvChoosePicture, mTvChooseVideo;
    private View mVideoTimeDot;
    private ImageView mIvTakePhotoBg;
    private Button mPictureCancel, mPictureOk;
    private int mRecordType = Constants.RECORD_TYPE_PICTURE;
    private ImageView mPictureImage;
    private Bitmap mPictureBitmap;

    /**
     * 录制
     * Record
     */
    private ArrayList<Long> mRecordTimeList = new ArrayList<>();
    private ArrayList<String> mRecordFileList = new ArrayList<>();
    private long mEachRecodingVideoTime = 0, mEachRecodingImageTime = 4000000;
    private long mAllRecordingTime = 0;
    private String mCurRecordVideoPath;
    private NvAssetManager mAssetManager;
    private int mCurrentDeviceIndex;
    private boolean mIsSwitchingCamera;
    NvsStreamingContext.CaptureDeviceCapability mCapability = null;
    private AlphaAnimation mFocusAnimation;

    /**
     * 变焦以及曝光dialog
     * Zoom and exposure dialog
     */
    private boolean m_supportAutoFocus;
    private TopMoreDialog mMoreDialog;
    /**
     * 美颜Dialog
     * Beauty Dialog
     */
    private AlertDialog mCaptureBeautyDialog;
    private View mBeautyView;
    private TextView mBeautyTabButton;
    private View mVSkinBeautyLine, mVShapeBeautyLine;
    private TextView mShapeTabButton;
    private RelativeLayout mBeautySelectRelativeLayout;
    private RelativeLayout mShapeSelectRelativeLayout;
    private HorizontalIndicatorSeekBar mBeautySeekBar;
    private MagicProgress mShapeSeekBar;

    /**
     * 美颜
     * Beauty
     */
    private Switch mBeauty_switch;
    private TextView mBeauty_switch_text;
    private Boolean mIsBeautyType = true;
    private RecyclerView mBeautyRecyclerView;
    private BeautyShapeAdapter mBeautyAdapter;
    private String mCurBeautyId;
    private NvsCaptureVideoFx mBeautyFx;
    private boolean mBeautySwitchIsOpend;
    private double mDefaultBeautyIntensity = 1.0;

    /**
     * 美型id 检索数组
     * Retrieve an array of beauty ids
     */
    private String[] mShapeIdArray = {
            "Face Size Warp Degree",
            "Face Length Warp Degree",
            "Face Width Warp Degree",
            "Nose Width Warp Degree"
    };
    List<String> mShapeIdList = new ArrayList<>(Arrays.asList(mShapeIdArray));

    /**
     * 是否初始化完成
     * Whether initialization is complete
     */
    private boolean initArScene;

    /**
     * 美型
     */
    private Switch mBeauty_shape_switch;
    private TextView mBeauty_shape_switch_text;
    private LinearLayout mBeautyShapeResetLayout;
    private ImageView mBeautyShapeResetIcon;
    private TextView mBeautyShapeResetTxt;
    private RecyclerView mShapeRecyclerView;
    private RecyclerView mShapeKindRecyclerView;
    private BeautyShapeAdapter mShapeAdapter;
    private BeautyShapeKindAdapter mShapeKindAdapter;
    private boolean mShapeSwitchIsOpen;
    //美型中两种类型的子选项的数据源，分别保存对应的默认值和选中后的设置的值
    private ArrayList<BeautyShapeDataItem> defaultBeautyShapeDataItemArrayList;
    private ArrayList<BeautyShapeDataItem> specialBeautyShapeDataItemArrayList;
    //当前选中的美型类型的position
    private int currentSelectBeautyShapePosition;
    //当前选中类别美型1的item位置
    private int currentSelectedBeautyShapeKind1ItemPosition = -1;
    //当前选中类别美型2的item位置
    private int currentSelectedBeautyShapeKind2ItemPosition = -1;
    /**
     * 滤镜
     * filter
     */
    private AlertDialog mFilterDialog, mMakeUpDialog;
    private FilterView mFilterView;
    private MakeUpView mMakeUpView;
    private NvsCaptureVideoFx mCurCaptureVideoFx;
    private ArrayList<FilterItem> mFilterDataArrayList = new ArrayList<>();
    private int mFilterSelPos;
    private VideoClipFxInfo mVideoClipFxInfo = new VideoClipFxInfo();

    /**
     * 道具-默认普通版，不带人脸功能
     * Props-default normal version, without face function
     */
    private int mCanUseARFaceType = HUMAN_AI_TYPE_NONE;
    //private AlertDialog mFaceUPropDialog;
    // private FaceUPropView mFaceUPropView;
    private List<Props> mPropsList = new ArrayList<>();
    private int mFaceUPropSelPos;
    private String mArSceneId = "";
    private TextView mTvBeautyA;
    private TextView mTvBeautyB;
    private TextView mShapeText2;
    private TextView mShapeText;

    private NvsCaptureVideoFx mArSceneFaceEffect;

    //选中美妆的item，此时再切换到美型需要切换到美型1的默认值
    private boolean selectMarkUp;

    //TODO zhi


    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_capture;
    }

    @Override
    protected void initViews() {


        /*
         * 页面主要布局
         * Main page layout
         */
        mRecordTypeLayout = (LinearLayout) findViewById(R.id.ll_chang_pv);
        mFlMiddleParent = findViewById(R.id.fl_middle_parent);
        mFlBottomParent = findViewById(R.id.fl_bottom_parent);

        /*
         * 美颜
         * Beauty
         */
        mBeautyView = LayoutInflater.from(this).inflate(R.layout.beauty_view, null);
        mBeautyTabButton = mBeautyView.findViewById(R.id.beauty_tab_btn);
        mVSkinBeautyLine = mBeautyView.findViewById(R.id.v_skin_beauty_line);
        mShapeTabButton = mBeautyView.findViewById(R.id.shape_tab_btn);
        mVShapeBeautyLine = mBeautyView.findViewById(R.id.v_shape_beauty_line);
        mBeautySelectRelativeLayout = (RelativeLayout) mBeautyView.findViewById(R.id.beauty_select_rl);
        mShapeSelectRelativeLayout = (RelativeLayout) mBeautyView.findViewById(R.id.shape_select_rl);
        mBeautySeekBar = mBeautyView.findViewById(R.id.beauty_sb);
        mTvBeautyA = mBeautyView.findViewById(R.id.tv_beauty_a);
        mTvBeautyB = mBeautyView.findViewById(R.id.tv_beauty_b);

        mShapeSeekBar = (MagicProgress) mBeautyView.findViewById(R.id.shape_sb);
        mShapeSeekBar.setMax(200);
        mShapeSeekBar.setAuto(false);
        mShapeSeekBar.setPointEnable(false);

        mShapeText = (TextView) mBeautyView.findViewById(R.id.shape_text);
        mShapeText2 = (TextView) mBeautyView.findViewById(R.id.shape_text2);

        mBeauty_switch = (Switch) mBeautyView.findViewById(R.id.beauty_switch);
        mBeauty_switch_text = (TextView) mBeautyView.findViewById(R.id.beauty_switch_text);
        mBeautyRecyclerView = (RecyclerView) mBeautyView.findViewById(R.id.beauty_list_rv);

        /*
         * 校色
         * School color
         */
        mAdjustColorLayout = (LinearLayout) mBeautyView.findViewById(R.id.adjust_color_layout);
        mAdjustColorSwitch = (Switch) mBeautyView.findViewById(R.id.adjust_color_switch);

        /*
         * 锐化
         * Sharpen
         */
        mSharpenLayout = (LinearLayout) mBeautyView.findViewById(R.id.sharpen_layout);
        mSharpenSwitch = (Switch) mBeautyView.findViewById(R.id.sharpen_switch);
       // mSharpenSwitch.setChecked(true);

        mBeauty_shape_switch = (Switch) mBeautyView.findViewById(R.id.beauty_shape_switch);
        mBeauty_shape_switch_text = (TextView) mBeautyView.findViewById(R.id.beauty_shape_switch_text);
        mBeautyShapeResetLayout = (LinearLayout) mBeautyView.findViewById(R.id.beauty_shape_reset_layout);
        mBeautyShapeResetIcon = (ImageView) mBeautyView.findViewById(R.id.beauty_shape_reset_icon);
        mBeautyShapeResetTxt = (TextView) mBeautyView.findViewById(R.id.beauty_shape_reset_txt);
        mShapeRecyclerView = (RecyclerView) mBeautyView.findViewById(R.id.beauty_shape_item_list);
        mShapeKindRecyclerView = (RecyclerView) mBeautyView.findViewById(R.id.rv_beauty_shape_kind_item);

        mRecordTime = (TextView) findViewById(R.id.tv_timing_num);
        mImageAutoFocusRect = (ImageView) findViewById(R.id.iv_focus);
        mDelete = (ImageView) findViewById(R.id.iv_back_delete);
        mNext = (ImageView) findViewById(R.id.iv_confirm);
        // mStartLayout = (RelativeLayout) findViewById(R.id.startLayout);
        mFlStartRecord = findViewById(R.id.fl_take_photos);
        mStartText = (TextView) findViewById(R.id.tv_video_num);
        mLiveWindow = (NvsLiveWindow) findViewById(R.id.lw_window);
        mIvExit = findViewById(R.id.iv_exit);
        mIvMore = findViewById(R.id.iv_more);
        mIvChangeCamera = findViewById(R.id.iv_rollover);

        mLlMakeupLayout = (LinearLayout) findViewById(R.id.ll_makeup);
        mTvMakeup = findViewById(R.id.tv_makeup);
        mIvMakeup = findViewById(R.id.iv_makeup);
        mTvBeauty = findViewById(R.id.tv_beauty);
        mIvBauty = findViewById(R.id.iv_beauty);
        mTvProps = findViewById(R.id.tv_props);
        mIvProps = findViewById(R.id.iv_props);
        mTvFilter = findViewById(R.id.tv_filter);
        mIvFilter = findViewById(R.id.iv_filter);
        mBeautyLayout = (LinearLayout) findViewById(R.id.ll_beauty);
        mFilterLayout = (LinearLayout) findViewById(R.id.ll_filter);
        mFuLayout = (LinearLayout) findViewById(R.id.ll_props);

        //  mTypeRightView = findViewById(R.id.rightView);
        mVideoTimeDot = findViewById(R.id.v_timing_dot);
        mIvTakePhotoBg = findViewById(R.id.iv_take_photo);
        mTvChoosePicture = findViewById(R.id.tv_take_photos);
        mTvChooseVideo = findViewById(R.id.tv_take_video);
        //mSelectLayout = (RelativeLayout) findViewById(R.id.select_layout);
        mRlPhotosLayout = (RelativeLayout) findViewById(R.id.rl_photos_container);
        mPictureCancel = findViewById(R.id.bt_delete_photos);
        mPictureOk = findViewById(R.id.bt_save_photos);
        mPictureImage = (ImageView) findViewById(R.id.iv_photos);
        initTopMoreView();
        mCaptureBeautyDialog = new AlertDialog.Builder(this).create();
        mCaptureBeautyDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mBeautyTabButton.performClick();
            }
        });
        mCaptureBeautyDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                changeCaptureDisplay(true);
            }
        });
        mCaptureBeautyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(final DialogInterface dialog) {
                MakeupData.getInstacne().clearData();
                MakeupData.getInstacne().clearPositionData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeCaptureDisplay(true);
                        closeCaptureDialogView(mCaptureBeautyDialog);
                    }
                });
            }
        });
    }

    private void initTopMoreView() {
        if (mMoreDialog == null) {
            mMoreDialog = TopMoreDialog.create(this, mStreamingContext);
            mMoreDialog.setEventListener(new TopMoreDialog.EventListener() {
                @Override
                public void onDismiss() {

                }

                @Override
                public void onDialogCancel() {
                }
            });
        }
    }

    private void searchAssetData() {
        mAssetManager = NvAssetManager.sharedInstance();
        String bundlePath = "filter";
        mAssetManager.searchReservedAssets(NvAsset.ASSET_FILTER, bundlePath);
        mAssetManager.searchLocalAssets(NvAsset.ASSET_FILTER);

        //初始化本地到具
        if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS)) {
            bundlePath = "msarface";
        } else {
            bundlePath = "arface";
        }
        mAssetManager.searchReservedAssets(NvAsset.ASSET_ARSCENE_FACE, bundlePath);
        mAssetManager.searchLocalAssets(NvAsset.ASSET_ARSCENE_FACE);
    }

    /**
     * 滤镜数据初始化
     * Filter data initialization
     */
    private void initFilterList() {
        mFilterDataArrayList.clear();
        mFilterDataArrayList = AssetFxUtil.getFilterData(this,
                getLocalData(NvAsset.ASSET_FILTER),
                null,
                true,
                false);
    }

    private void initFilterDialog() {
        mFilterDialog = new AlertDialog.Builder(this).create();
        mFilterDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if(mFilterSelPos > 0){
                    mFilterView.setSelectedPos(mFilterSelPos);
                }
                if(mCurCaptureVideoFx != null){
                    mFilterView.setIntensityLayoutVisible(View.VISIBLE);
                    mFilterView.setIntensitySeekBarProgress((int) (mCurCaptureVideoFx.getFilterIntensity()*100));
                }
            }
        });
        mFilterDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                changeCaptureDisplay(true);
            }
        });
        mFilterDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                changeCaptureDisplay(true);
                closeCaptureDialogView(mFilterDialog);
            }
        });
        mFilterView = new FilterView(this);
        for (int i = 0; i < mFilterDataArrayList.size(); i++) {
            FilterItem filterItem = mFilterDataArrayList.get(i);
            if(filterItem != null && "647136C2-D334-4FFC-8949-36F2B3CC94DC".equals(filterItem.getPackageId())){
                //默认添加水嫩MY
                mCurCaptureVideoFx = mStreamingContext.appendPackagedCaptureVideoFx(filterItem.getPackageId());
                if(mCurCaptureVideoFx != null){
                    mCurCaptureVideoFx.setFilterIntensity(0.7f);
                    mFilterSelPos = i;
                    break;
                }
            }
        }
        /*
         * 设置滤镜数据
         * Set filter data
         */
        mFilterView.initFilterRecyclerView(this);
        mFilterView.setFilterArrayList(mFilterDataArrayList);
        mFilterView.setIntensityLayoutVisible(View.INVISIBLE);
        mFilterView.setIntensityTextVisible(View.GONE);
        mFilterView.setFilterListener(new FilterView.OnFilterListener() {
            @Override
            public void onItmeClick(View v, int position) {
                int count = mFilterDataArrayList.size();
                if (position < 0 || position >= count) {
                    return;
                }
                if (mFilterSelPos == position) {
                    return;
                }
                mFilterSelPos = position;
                removeAllFilterFx();
                mFilterView.setIntensitySeekBarMaxValue(100);
                mFilterView.setIntensitySeekBarProgress(100);
                if (position == 0) {
                    mFilterView.setIntensityLayoutVisible(View.INVISIBLE);
                    mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                    mVideoClipFxInfo.setFxId(null);
                    mCurCaptureVideoFx = null;
                    return;
                }
                mFilterView.setIntensityLayoutVisible(View.VISIBLE);
                FilterItem filterItem = mFilterDataArrayList.get(position);
                int filterMode = filterItem.getFilterMode();
                if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
                    String filterName = filterItem.getFilterName();

                    if (!TextUtils.isEmpty(filterName) && filterItem.getIsCartoon()) {
                        mBeauty_switch.setChecked(false);
                        mBeauty_shape_switch.setChecked(false);
                        mCurCaptureVideoFx = mStreamingContext.appendBuiltinCaptureVideoFx("Cartoon");
                        mCurCaptureVideoFx.setBooleanVal("Stroke Only", filterItem.getStrokenOnly());
                        mCurCaptureVideoFx.setBooleanVal("Grayscale", filterItem.getGrayScale());
                    } else if (!TextUtils.isEmpty(filterName)) {
                        mCurCaptureVideoFx = mStreamingContext.appendBuiltinCaptureVideoFx(filterName);
                    }
                    mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_BUILTIN);
                    mVideoClipFxInfo.setFxId(filterName);
                } else {
                    String filterPackageId = filterItem.getPackageId();
                    if (!TextUtils.isEmpty(filterPackageId)) {
                        mCurCaptureVideoFx = mStreamingContext.appendPackagedCaptureVideoFx(filterPackageId);
                    }
                    mVideoClipFxInfo.setFxMode(VideoClipFxInfo.FXMODE_PACKAGE);
                    mVideoClipFxInfo.setFxId(filterPackageId);
                }

                mCurCaptureVideoFx.setFilterIntensity(1.0f);
            }

            @Override
            public void onMoreFilter() {
                /*
                 * 拍摄进入下载，不作比例适配
                 * Shoot into download, no proportion adaptation
                 */
                TimelineData.instance().setMakeRatio(NvAsset.AspectRatio_NoFitRatio);
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreFilter);
                bundle.putInt("assetType", NvAsset.ASSET_FILTER);
                bundle.putString("from", "capture_filter");
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, REQUEST_FILTER_LIST_CODE);
                mFilterView.setMoreFilterClickable(false);
            }

            @Override
            public void onIntensity(int value) {
                if (mCurCaptureVideoFx != null) {
                    float intensity = value / (float) 100;
                    mCurCaptureVideoFx.setFilterIntensity(intensity);
                }
            }
        });
    }

    /**
     * 初始化道具数据
     * Initialize prop data
     */
    private void initFacUPropDataList() {
        mPropsList.clear();
        String assetPath;
        if (BuildConfig.HUMAN_AI_TYPE.equals(BUILD_HUMAN_AI_TYPE_MS)) {
            assetPath = "msarface/info.json";
        } else {
            assetPath = "arface/info.json";
        }
        ArrayList<NvAsset> propsList = getLocalData(NvAsset.ASSET_ARSCENE_FACE);
        mPropsList = AssetFxUtil.getPropsList(this, propsList, assetPath);
    }

    private PropsEffectDialog mPropsDialog;

    /**
     * 初始化道具Dialog
     * Initialize prop Dialog
     */
    private void initProps() {
        mPropsDialog = PropsEffectDialog.create(this);
        mPropsDialog.setPropsData(mPropsList);
        mPropsDialog.setPropsEventListener(new PropsEffectDialog.PropsEventListener() {
            @Override
            public void onDismiss() {
                changeCaptureDisplay(true);
            }

            @Override
            public void onDialogCancel() {
                closeCaptureDialogView(mPropsDialog.getDialog());
            }

            @Override
            public void onLoadMore() {
                TimelineData.instance().setMakeRatio(NvAsset.AspectRatio_NoFitRatio);
                // mFaceUPropView.setMoreFaceUPropClickable(false);
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreFaceU);
                bundle.putInt("assetType", NvAsset.ASSET_ARSCENE_FACE);
                bundle.putString("from", "capture_props");
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, ARFACE_LIST_REQUES_CODE);
            }

            @Override
            public void onPropsSelected(Props item) {
                if (item == null) {
                    mArSceneFaceEffect.setStringVal("Scene Id", "");
                    return;
                }
                String sceneId = item.getUuid();
                showPropsToast(sceneId);
                mArSceneFaceEffect.setStringVal("Scene Id", sceneId);
            }
        });
    }

    private void showPropsToast(String sceneId) {
        NvsAssetPackageManager manager = mStreamingContext.getAssetPackageManager();
        if (manager == null) {
            return;
        }
        String packagePrompt = manager.getARSceneAssetPackagePrompt(sceneId);
        if (!TextUtils.isEmpty(packagePrompt)) {
            ToastUtil.showToastCenter(this, packagePrompt);
        }
    }

    private void initMakeupDialog() {
        mMakeUpDialog = new AlertDialog.Builder(this).create();
        mMakeUpDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                changeCaptureDisplay(true);
            }
        });
        mMakeUpDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                changeCaptureDisplay(true);
                closeCaptureDialogView(mMakeUpDialog);
            }
        });
        mMakeUpView = new MakeUpView(this);
        ArrayList<BeautyData> makeupDataList = new CaptureDataHelper().getComposeMakeupDataList(this);
        if (makeupDataList == null) {
            return;
        }
        for (BeautyData beautyData : makeupDataList) {
            if (beautyData.isBuildIn()) {
                getNvAssetManager().searchReservedAssets(NvAsset.ASSET_FILTER, beautyData.getFolderPath());
            } else {
                getNvAssetManager().searchAssetInLocalPath(NvAsset.ASSET_FILTER, beautyData.getFolderPath());
            }
        }
        mMakeUpView.setMakeupArrayList(makeupDataList);
        mMakeUpView.setOnMakeUpEventListener(new MakeUpView.MakeUpEventListener() {
            @Override
            public void onMakeupViewComposeDataChanged(int position, boolean isClearMakeup) {
                onMakeupComposeDataChanged(position, isClearMakeup);
                //选择了美妆，美型需要切换到美型1
                selectMarkUp = true;
            }

            @Override
            public void onMakeupViewDataChanged() {
                onMakeupDataChanged();
            }

            @Override
            public void removeVideoFxByName(String name) {
                removeFilterFxByName(name);
            }

            @Override
            public void onMakeUpViewDismiss() {
                closeCaptureDialogView(mMakeUpDialog);
            }
        });
    }

    private NvsCaptureVideoFx mAdjustColorFx;

    private void initBeautyRecyclerView() {
        mBeautyAdapter = new BeautyShapeAdapter(this, new CaptureDataHelper().getBeautyDataListByType(this, mCanUseARFaceType));
        checkAdjustColor();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBeautyRecyclerView.setLayoutManager(layoutManager);
        mBeautyRecyclerView.setAdapter(mBeautyAdapter);
        mBeautyAdapter.setEnable(OPEN_ALL_SWITCH);
        mBeautyAdapter.setOnItemClickListener(new BeautyShapeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position < BeautyShapeAdapter.POS_BEAUTY_ADJUSTCOLOR_3) {

                    mBeautySeekBar.setVisibility(View.VISIBLE);
                    /*
                     * 美颜程度
                     * Beauty degree
                     */
                    double level = 0.0;
                    mCurBeautyId = mBeautyAdapter.getSelectItem().beautyShapeId;
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        if (mBeautyAdapter.getSelectItem().needDefaultStrength) {
                            level = mBeautyAdapter.getSelectItem().strength;
                        } else {
                            level = mArSceneFaceEffect.getFloatVal(mCurBeautyId);
                        }
                        mBeautySeekBar.setProgress((int) (level * 100));
                    } else {
                        /*
                         * 其他
                         * other
                         */
                        level = mBeautyFx.getFloatVal(mCurBeautyId);
                        mBeautySeekBar.setProgress((int) (level * 100));
                    }
                } else if (position == BeautyShapeAdapter.POS_BEAUTY_ADJUSTCOLOR_3) {
                    if (mAdjustColorSwitch.isChecked()) {
                        checkAdjustColor();
                        mBeautySeekBar.setVisibility(View.VISIBLE);
                    } else {
                        mBeautySeekBar.setVisibility(View.INVISIBLE);
                        if (mAdjustColorFx != null) {
                            mStreamingContext.removeCaptureVideoFx(mAdjustColorFx.getIndex());
                        }
                        mAdjustColorFx = null;
                    }

                } else {
                    mBeautySeekBar.setVisibility(View.INVISIBLE);
                }

                mTvBeautyA.setVisibility(View.GONE);
                mTvBeautyB.setVisibility(View.GONE);
                mAdjustColorLayout.setVisibility(View.GONE);
                mSharpenLayout.setVisibility(View.GONE);
                if (position == BeautyShapeAdapter.POS_BEAUTY_WHITING_1) {
                    mTvBeautyA.setVisibility(View.VISIBLE);
                    mTvBeautyB.setVisibility(View.VISIBLE);
                } else if (position == BeautyShapeAdapter.POS_BEAUTY_ADJUSTCOLOR_3) {
                    mAdjustColorLayout.setVisibility(View.VISIBLE);
                } else if (position == BeautyShapeAdapter.POS_BEAUTY_SHARPEN_4) {
                    mSharpenLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void checkAdjustColor() {
        if (mAdjustColorFx == null) {
            StringBuilder sb = new StringBuilder();
            BeautyShapeDataItem selectItem = mBeautyAdapter.getItem(BeautyShapeAdapter.POS_BEAUTY_ADJUSTCOLOR_3);
            if(!TextUtils.isEmpty(selectItem.getPath())){
                mStreamingContext.getAssetPackageManager().installAssetPackage(selectItem.getPath(), null, NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, true, sb);
                mAdjustColorFx = mStreamingContext.appendPackagedCaptureVideoFx(sb.toString());
                if(mAdjustColorFx != null){
                    mAdjustColorFx.setFilterIntensity(1.0f);
                    mBeautySeekBar.setProgress(100);
                }
            }
        }
    }

    private void initMakeupViewVisible() {
        if ((mArSceneFaceEffect == null) || !initArScene) {
//            mMakeupTabButton.setVisibility(View.GONE);
            if (mShapeAdapter != null) {
                mShapeAdapter.setEnable(false);
                mShapeKindAdapter.setEnable(false);
            }
        }
    }

    /**
     * 初始化美型的各个特效的列表
     */
    private void initShapeRecyclerView() {

        mShapeAdapter = new BeautyShapeAdapter(this, new ArrayList());
        mShapeAdapter.setIsBeautyShape(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mShapeRecyclerView.setLayoutManager(linearLayoutManager);
        mShapeRecyclerView.setAdapter(mShapeAdapter);
        int space = ScreenUtils.dip2px(this, 8);
        mShapeRecyclerView.addItemDecoration(new SpaceItemDecoration(space, 0));
        mShapeAdapter.setOnItemClickListener(new BeautyShapeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position < 0 || position >= mShapeAdapter.getItemCount()) {
                    return;
                }
                //position == 0 点击的是返回按钮
                if (position == 0) {
                    //返回选择类型的列表
                    upDataSelectedKindFaceType(null, false);
                    //选中位置取消
                    mShapeAdapter.setSelectPos(-1);
                } else {
                    //设置当前选中的是哪个子item
                    if (currentSelectBeautyShapePosition == 0) {
                        currentSelectedBeautyShapeKind1ItemPosition = position;
                    } else {
                        currentSelectedBeautyShapeKind2ItemPosition = position;
                    }
                    mShapeText.setVisibility(View.VISIBLE);
                    mShapeText2.setVisibility(View.VISIBLE);
                    mShapeSeekBar.setVisibility(View.VISIBLE);
                    /*
                     * 美型程度
                     * Beauty degree
                     */
                    double level = 0.0;
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        String beautyShapeId = mShapeAdapter.getSelectItem().beautyShapeId;
                        double floatVal = mArSceneFaceEffect.getFloatVal(beautyShapeId);
                        if (floatVal >= 0) {
                            level = (Math.round(floatVal * 100)) * 0.01;
                        } else {
                            level = -Math.round((Math.abs(floatVal) * 100)) * 0.01;
                        }
                        if (mShapeIdList.contains(beautyShapeId)) {
                            mShapeSeekBar.setProgress((int) (100 - level * 100));
                        } else {
                            /*
                             * 美型特效值的范围[-1,1]
                             * Range of American special effects [-1,1]
                             */
                            mShapeSeekBar.setProgress((int) (level * 100 + 100));
                        }
                    } else {

                    }
                }
            }
        });
    }

    /**
     * 初始化美型的类型列表
     * 目前是美型1 美型2两个种类
     */
    private void initShapeKindRecyclerView() {
        mShapeKindAdapter = new BeautyShapeKindAdapter(this, CaptureDataHelper.getBeautyShapeKindList(this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mShapeKindRecyclerView.setLayoutManager(linearLayoutManager);
        mShapeKindRecyclerView.setAdapter(mShapeKindAdapter);
        int space = ScreenUtils.dip2px(this, 8);
        mShapeKindRecyclerView.addItemDecoration(new SpaceItemDecoration(space, 0));
        mShapeKindAdapter.setOnItemClickListener(new BeautyShapeKindAdapter.OnItemClickListener() {
            @Override
            public void onItemSelected(BeautyShapeDataKindItem item, int position) {
                mShapeKindAdapter.setSelectPos(position);

                //循环设置特效值值
                //更改策略
                initBeautyShapeStrategy(item.getType());
                upDateEffectValue(item.getType(), false);
                currentSelectBeautyShapePosition = position;

            }

            @Override
            public void onItemRepeatSelected(BeautyShapeDataKindItem item, int position) {
                if (item.getType() == BeautyShapeDataKindItem.Type.NORMAL) {
                    mShapeAdapter.setDataList(defaultBeautyShapeDataItemArrayList);
                    mShapeAdapter.setSelectPos(currentSelectedBeautyShapeKind1ItemPosition);
                } else {
                    mShapeAdapter.setDataList(specialBeautyShapeDataItemArrayList);
                    mShapeAdapter.setSelectPos(currentSelectedBeautyShapeKind2ItemPosition);
                }
                //修改UI
                upDataSelectedKindFaceType(item, true);
            }
        });
        //各自添加一个返回键对应的实体,使用空就ok
        defaultBeautyShapeDataItemArrayList = CaptureDataHelper.getShapeDataList(this, BeautyShapeDataKindItem.Type.NORMAL);
        defaultBeautyShapeDataItemArrayList.add(0, null);
        specialBeautyShapeDataItemArrayList = CaptureDataHelper.getShapeDataList(this, BeautyShapeDataKindItem.Type.NEW_BUILD);
        specialBeautyShapeDataItemArrayList.add(0, null);
        //默认选中第一个
        currentSelectBeautyShapePosition = 0;
        mShapeKindAdapter.setSelectPos(currentSelectBeautyShapePosition);
    }

    /**
     * 选中了美型类型之后，需要设置特效值shizhi生效
     *
     * @param type           選擇的父類型
     * @param resetToDefault 是否重置到默認值
     */
    private void upDateEffectValue(int type, boolean resetToDefault) {
        ArrayList<BeautyShapeDataItem> target;
        if (type == BeautyShapeDataKindItem.Type.NORMAL) {
            target = defaultBeautyShapeDataItemArrayList;
        } else {
            target = specialBeautyShapeDataItemArrayList;
        }
        for (BeautyShapeDataItem item : target) {
            if (null != item) {
                //是否需要重置默認值
                if (resetToDefault) {
                    item.strength = item.defaultValue;
                }
                mArSceneFaceEffect.setFloatVal(item.beautyShapeId, item.strength);
            }
        }
    }

    /**
     * 通过选中的类型，显示对应的特效内容和默认值
     *
     * @param item           选择的类型
     * @param showBeautyItem 显示美型的item
     */
    private void upDataSelectedKindFaceType(BeautyShapeDataKindItem item, boolean showBeautyItem) {
        if (showBeautyItem) {

            //显示操作各个特效的列表
            mShapeRecyclerView.setVisibility(View.VISIBLE);
            mShapeKindRecyclerView.setVisibility(View.GONE);
            //设置选中的类型
            mShapeAdapter.setSelectedKind(item);

        } else {
            //显示操作各个特效的列表
            mShapeRecyclerView.setVisibility(View.GONE);
            mShapeKindRecyclerView.setVisibility(View.VISIBLE);
        }
        onSelectShapeToChange(showBeautyItem);
    }

    /**
     * 当选择了美型中的一个去操作，显示其中的控件
     *
     * @param showBeauty 是否显示美型数据
     */
    public void onSelectShapeToChange(boolean showBeauty) {
        if (showBeauty) {
            if (mShapeSwitchIsOpen && mShapeAdapter.getSelectPos() >= 0 && mShapeAdapter.getSelectPos() <= mShapeAdapter.getItemCount()) {
                if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                    double tempLevel = mArSceneFaceEffect.getFloatVal(mShapeAdapter.getSelectItem().beautyShapeId);
                    if (mShapeIdList.contains(mShapeAdapter.getSelectItem().beautyShapeId)) {
                        mShapeSeekBar.setProgress((int) (100 - tempLevel * 100));
                    } else {
                        // 美型特效值的范围[-1,1]
                        /*
                         *美型特效值的范围[-1,1]
                         *Range of Beauty special effects [-1,1]
                         */
                        mShapeSeekBar.setProgress((int) (tempLevel * 100 + 100));
                    }
                }
                mShapeSeekBar.setVisibility(View.VISIBLE);
                mShapeText.setVisibility(View.VISIBLE);
                mShapeText2.setVisibility(View.VISIBLE);
            }
        } else {
            mShapeSeekBar.setVisibility(View.INVISIBLE);
            mShapeText.setVisibility(View.INVISIBLE);
            mShapeText2.setVisibility(View.INVISIBLE);
        }
    }


    private void shapeLayoutEnabled(Boolean isEnabled) {
        mBeautyShapeResetLayout.setEnabled(isEnabled);
        mBeautyShapeResetLayout.setClickable(isEnabled);
        mShapeAdapter.setEnable(isEnabled);
        mShapeKindAdapter.setEnable(isEnabled);
        if (isEnabled) {
            mBeautyShapeResetIcon.setAlpha(1f);
            mBeautyShapeResetTxt.setTextColor(Color.BLACK);
        } else {
            mBeautyShapeResetIcon.setAlpha(0.5f);
            mBeautyShapeResetTxt.setTextColor(getResources().getColor(R.color.ms_disable_color));
        }
    }

    /**
     * 初始化美颜特效对象
     * Initialize the beauty effect object
     */
    private void initBeautyData() {
        if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
            if (mArSceneFaceEffect == null) {
                mArSceneFaceEffect = mStreamingContext.appendBuiltinCaptureVideoFx("AR Scene");
            }
            if (mArSceneFaceEffect != null) {
                initBeautyShapeStrategy(BeautyShapeDataKindItem.Type.NORMAL);
            }
        }
    }

    /**
     * 策略初始化，默认显示美型1，显示默认策略 strategy = 0
     * 美型2 策略 strategy = 1;
     * 初始化调用，切换策略调用
     */
    private void initBeautyShapeStrategy(int strategy) {
        //这八项是两种策略都需要显示的
        mArSceneFaceEffect.setIntVal("Eye Size Warp Strategy", strategy);
        mArSceneFaceEffect.setIntVal("Face Size Warp Strategy", strategy);
        mArSceneFaceEffect.setIntVal("Face Width Warp Strategy", strategy);
        mArSceneFaceEffect.setIntVal("Face Length Warp Strategy", strategy);
        mArSceneFaceEffect.setIntVal("Forehead Height Warp Strategy", strategy);
        mArSceneFaceEffect.setIntVal("Chin Length Warp Strategy", strategy);
        mArSceneFaceEffect.setIntVal("Nose Width Warp Strategy", strategy);
        mArSceneFaceEffect.setIntVal("Mouth Size Warp Strategy", strategy);
        if (strategy == 0) {
            mArSceneFaceEffect.setIntVal("Nose Length Warp Strategy", strategy);
            mArSceneFaceEffect.setIntVal("Eye Corner Stretch Strategy", strategy);
            mArSceneFaceEffect.setIntVal("Mouth Corner Lift Strategy", strategy);
        }
    }

    private void updateTypeRightView() {
        mTypeRightView.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams layoutParams = mTypeRightView.getLayoutParams();
                layoutParams.width = mTvChoosePicture.getWidth();
                mTypeRightView.setLayoutParams(layoutParams);
            }
        });
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void initData() {
        changeAspectRatio();
        //updateTypeRightView();
        initCaptureData();
        initCapture();
        searchAssetData();
        initBeautyData();
        /*
         * 滤镜初始化
         * Filter initialization
         */
        initFilterList();
        initFilterDialog();
        /*
         * 人脸道具初始化
         * Face prop initialization
         */
        initFacUPropDataList();
        //initFacUPropDialog();
        initProps();
        /*
         * 美型初始化
         * Beauty initialization
         */
        initMakeupDialog();
        initShapeRecyclerView();
        initShapeKindRecyclerView();
        initBeautyRecyclerView();
        initMakeupViewVisible();
        mBeautyTabButton.setSelected(true);
        mVSkinBeautyLine.setBackgroundColor(getResources().getColor(R.color.menu_selected));
        mVShapeBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
        if (mCanUseARFaceType == HUMAN_AI_TYPE_MS && initArScene) {
            setBeautyShapeSwitchChecked(OPEN_ALL_SWITCH);
        }
        setBeautySwitchChecked(OPEN_ALL_SWITCH);
        if (OPEN_ALL_SWITCH) {
            if (mCanUseARFaceType == HUMAN_AI_TYPE_MS && initArScene) {
                //mArSceneFaceEffect.setBooleanVal("Default Sharpen Enabled", true);
                //  mArSceneFaceEffect.setBooleanVal("Default Beauty Enabled", true);
                mArSceneFaceEffect.setFloatVal("Beauty Whitening", 0.5);
                changeBeautyWhiteMode(mArSceneFaceEffect, true, false);
                // init shape params
                mArSceneFaceEffect.setFloatVal("Face Size Warp Degree", -0.6f);
                mArSceneFaceEffect.setFloatVal("Nose Width Warp Degree", -0.5f);
                mArSceneFaceEffect.setFloatVal("Eye Size Warp Degree", 0.7f);
            } else {
               // mBeautyFx.setBooleanVal("Default Sharpen Enabled", true);
                // mBeautyFx.setBooleanVal("Default Beauty Enabled", true);
                mBeautyFx.setFloatVal("Whitening", 0.5);
                changeBeautyWhiteMode(mBeautyFx, true, false);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListener() {
        initBeautyClickListener();
        mLiveWindow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float rectHalfWidth = mImageAutoFocusRect.getWidth() / 2;
                if (event.getX() - rectHalfWidth >= 0 && event.getX() + rectHalfWidth <= mLiveWindow.getWidth()
                        && event.getY() - rectHalfWidth >= 0 && event.getY() + rectHalfWidth <= mLiveWindow.getHeight()) {
                    mImageAutoFocusRect.setX(event.getX() - rectHalfWidth);
                    mImageAutoFocusRect.setY(event.getY() - rectHalfWidth);
                    RectF rectFrame = new RectF();
                    rectFrame.set(mImageAutoFocusRect.getX(), mImageAutoFocusRect.getY(),
                            mImageAutoFocusRect.getX() + mImageAutoFocusRect.getWidth(),
                            mImageAutoFocusRect.getY() + mImageAutoFocusRect.getHeight());
                    /*
                     * 启动自动聚焦
                     * Start autofocus
                     */
                    mImageAutoFocusRect.startAnimation(mFocusAnimation);
                    if (m_supportAutoFocus) {
                        mStreamingContext.startAutoFocus(new RectF(rectFrame));
                    }
                }
                return false;
            }
        });

        mIvExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*
         * 切换摄像头开关
         * Toggle camera switch
         */
        mIvChangeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsSwitchingCamera) {
                    return;
                }
                if (mCurrentDeviceIndex == 0) {
                    mCurrentDeviceIndex = 1;
                } else {
                    mCurrentDeviceIndex = 0;
                }
                mIsSwitchingCamera = true;
                startCapturePreview(true);
                if (mMoreDialog != null) {
                    mMoreDialog.setFlashEnable(mCurrentDeviceIndex != 1);
                }
            }
        });
        mIvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoreDialog.isShowing()) {
                    mMoreDialog.dismiss();
                } else {
                    mMoreDialog.show();
                }
            }
        });
        /*美颜*/
        /*
         * 美颜
         * Beauty
         */
        mBeautyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCaptureDisplay(false);
                showCaptureDialogView(mCaptureBeautyDialog, mBeautyView);
            }
        });

        /*
         * 美妆
         * Makeup
         */
        mLlMakeupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCaptureDisplay(false);
                showCaptureDialogView(mMakeUpDialog, mMakeUpView);
            }
        });

        mBeautyShapeResetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShapeSeekBar.setVisibility(View.INVISIBLE);
                mShapeText.setVisibility(View.INVISIBLE);
                mShapeText2.setVisibility(View.INVISIBLE);
                //resetBeautyShapeDefaultValue();
                mShapeSeekBar.setProgress(100);
                mShapeAdapter.setSelectPos(Integer.MAX_VALUE);
                //重置之后，所有的值設置成默認值
                resetBeautyShapeDefaultValue(mShapeKindAdapter.getSelectItem().getType());
            }
        });
        /*
         * 滤镜
         * Filter
         */
        mFilterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCaptureDisplay(false);
                showCaptureDialogView(mFilterDialog, mFilterView);
            }
        });
        /*道具*/
        /*
         * 道具
         * Props
         */
        mFuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * 只有美摄道具才可以使用
                 *  Only beauty photo props can be used
                 */
                if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                    if (initArScene) {
                        // showCaptureDialogView(mFaceUPropDialog, mFaceUPropView);
                        changeCaptureDisplay(false);
                        showCaptureDialogView(mPropsDialog.getDialog(), null);
                    } else {
                        // 授权过期
                        /*
                         * 授权过期
                         * License expired
                         */
                        String[] versionName = getResources().getStringArray(R.array.sdk_expire_tips);
                        Util.showDialog(CaptureActivity.this, versionName[0], versionName[1]);
                    }
                } else {
                    String[] versionName = getResources().getStringArray(R.array.sdk_version_tips);
                    Util.showDialog(CaptureActivity.this, versionName[0], versionName[1]);
                }
            }
        });
        /*
         * 开始录制
         *Start recording
         */
        mFlStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * 当前在录制状态，可停止视频录制
                 * Currently in recording state, you can stop video recording
                 */
                if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING) {
                    stopRecording();
                } else {
                    mCurRecordVideoPath = PathUtils.getRecordVideoPath();
                    if (mCurRecordVideoPath == null) {
                        return;
                    }
                    mFlStartRecord.setEnabled(false);
                    if (mRecordType == Constants.RECORD_TYPE_VIDEO) {
                        mEachRecodingVideoTime = 0;
                        //当前未在视频录制状态，则启动视频录制。此处使用带特效的录制方式
                        /*
                         * 当前未在视频录制状态，则启动视频录制。此处使用带特效的录制方式
                         * If video recording is not currently in progress, start video recording. Use the recording method with special effects here
                         */
                        if (!mStreamingContext.startRecording(mCurRecordVideoPath)) {
                            return;
                        }
                        changeRecordDisplay(RECORDING, false);
                        mRecordFileList.add(mCurRecordVideoPath);
                    } else if (mRecordType == Constants.RECORD_TYPE_PICTURE) {
                        mStreamingContext.startRecording(mCurRecordVideoPath);
                        changeRecordDisplay(RECORDING, true);
                    }
                }
            }
        });
        /*
         * 删除视频
         * Delete video
         */
        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecordTimeList.size() != 0 && mRecordFileList.size() != 0) {
                    mAllRecordingTime -= mRecordTimeList.get(mRecordTimeList.size() - 1);
                    mRecordTimeList.remove(mRecordTimeList.size() - 1);
                    PathUtils.deleteFile(mRecordFileList.get(mRecordFileList.size() - 1));
                    mRecordFileList.remove(mRecordFileList.size() - 1);
                    mRecordTime.setText(TimeFormatUtil.formatUsToString2(mAllRecordingTime));

                    if (mRecordTimeList.size() == 0) {
                        changeRecordDisplay(RECORD_DEFAULT, mRecordType == Constants.RECORD_TYPE_PICTURE);
                    } else {
                        mStartText.setText(mRecordTimeList.size() + "");
                        mRecordTime.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
        /*
         * 下一步，进入编辑
         * Next, enter edit
         */
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * mRecordFileList，视频存储列表。将拍摄的视频传到下一个页面
                 * mRecordFileList, video storage list. Send the captured video to the next page.
                 */
                ArrayList<ClipInfo> pathList = new ArrayList<>();
                for (int i = 0; i < mRecordFileList.size(); i++) {
                    ClipInfo clipInfo = new ClipInfo();
                    clipInfo.setFilePath(mRecordFileList.get(i));
                    pathList.add(clipInfo);
                }
                if (pathList.size() <= 0) {
                    return;
                }
                NvsAVFileInfo avFileInfo = mStreamingContext.getAVFileInfo(pathList.get(0).getFilePath());
                if (avFileInfo == null) {
                    return;
                }
                /*
                 * 数据清空
                 * Data clear
                 */
                TimelineData.instance().clear();
                NvsSize size = avFileInfo.getVideoStreamDimension(0);
                int rotation = avFileInfo.getVideoStreamRotation(0);
                if (rotation == NvsVideoStreamInfo.VIDEO_ROTATION_90
                        || rotation == NvsVideoStreamInfo.VIDEO_ROTATION_270) {
                    int tmp = size.width;
                    size.width = size.height;
                    size.height = tmp;
                }
                int makeRatio = size.width > size.height ? NvAsset.AspectRatio_16v9 : NvAsset.AspectRatio_9v16;
                TimelineData.instance().setVideoResolution(Util.getVideoEditResolution(makeRatio));
                TimelineData.instance().setMakeRatio(makeRatio);
                TimelineData.instance().setClipInfoData(pathList);
                mNext.setClickable(false);

                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.START_ACTIVITY_FROM_CAPTURE, true);
                AppManager.getInstance().jumpActivity(CaptureActivity.this, VideoEditActivity.class, bundle);
            }
        });

        mTvChoosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRecordType(true);
            }
        });
        mTvChooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRecordType(false);
            }
        });
        mPictureCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurRecordVideoPath != null) {
                    File file = new File(mCurRecordVideoPath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                showPictureLayout(false);
                if (mRecordTimeList.isEmpty()) {
                    mDelete.setVisibility(View.INVISIBLE);
                    mNext.setVisibility(View.INVISIBLE);
                    mStartText.setVisibility(View.INVISIBLE);
                }
            }
        });

        mPictureOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 * 拍照片
                 * Take a photo
                 */
                if (mRecordType == Constants.RECORD_TYPE_PICTURE) {
                    mAllRecordingTime += mEachRecodingImageTime;
                    mRecordTimeList.add(mEachRecodingImageTime);
                    mRecordTime.setText(TimeFormatUtil.formatUsToString2(mAllRecordingTime));
                    mStartText.setText(String.format("%d", mRecordTimeList.size()));
                    changeRecordDisplay(RECORD_FINISH, true);
                }
                String jpgPath = PathUtils.getRecordPicturePath();
                boolean save_ret = Util.saveBitmapToSD(mPictureBitmap, jpgPath);
                if (save_ret) {
                    mRecordFileList.add(jpgPath);
                }
                if (mCurRecordVideoPath != null) {
                    File file = new File(mCurRecordVideoPath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                showPictureLayout(false);
            }
        });
    }

    private void changeCaptureDisplay(boolean display) {
        if (display) {
            if (!mRecordTimeList.isEmpty()) {
                mFlMiddleParent.setVisibility(View.VISIBLE);
            }
            mIvExit.setVisibility(View.VISIBLE);
            mIvMore.setVisibility(View.VISIBLE);
            mIvChangeCamera.setVisibility(View.VISIBLE);
            mFlBottomParent.setVisibility(View.VISIBLE);
        } else {
            mIvExit.setVisibility(View.INVISIBLE);
            mIvMore.setVisibility(View.INVISIBLE);
            mIvChangeCamera.setVisibility(View.INVISIBLE);

            mFlBottomParent.setVisibility(View.INVISIBLE);
            mFlMiddleParent.setVisibility(View.INVISIBLE);
        }
    }

    private void adjustColorOrBeauty() {
        /*
         * 判断基础滤镜
         * Judging basic filters
         */
        if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
            if (mAdjustColorSwitch.isChecked()) {
                /*
                 * 基础滤镜在最前
                 * Basic filter first
                 */
                if (mBeautyAdapter.getSelectPos() == BeautyShapeAdapter.POS_BEAUTY_ADJUSTCOLOR_3) {
                    mBeautySeekBar.setVisibility(View.VISIBLE);
                    mDefaultBeautyIntensity = mArSceneFaceEffect.getFloatVal("Default Intensity");
                    mBeautySeekBar.setProgress((int) (mDefaultBeautyIntensity * 100));
                }
            }
        } else {
            if (mAdjustColorSwitch.isChecked()) {
                /*
                 * 基础滤镜在最前
                 * Basic filter first
                 */
                if (mBeautyAdapter.getSelectPos() == BeautyShapeAdapter.POS_BEAUTY_ADJUSTCOLOR_3) {
                    mBeautySeekBar.setVisibility(View.VISIBLE);
                    mDefaultBeautyIntensity = mBeautyFx.getFloatVal("Default Intensity");
                    mBeautySeekBar.setProgress((int) (mDefaultBeautyIntensity * 100));
                }
            }
        }
        /*
         * 判断美颜特效
         * Judge beauty effects
         */
        if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
            if (mBeautyAdapter.getSelectPos() >= 0 && mBeautyAdapter.getSelectPos() <= mBeautyAdapter.getItemCount()) {
                /*
                 * 不是基础滤镜在最前
                 * Not the base filter is first
                 */
                if (mBeautyAdapter.getSelectPos() < BeautyShapeAdapter.POS_BEAUTY_ADJUSTCOLOR_3) {
                    mBeautySeekBar.setVisibility(View.VISIBLE);
                    double tempLevel = mArSceneFaceEffect.getFloatVal(mBeautyAdapter.getSelectItem().beautyShapeId);
                    mBeautySeekBar.setProgress((int) (tempLevel * 100));
                }
            }
        } else {
            if (mBeautyAdapter.getSelectPos() >= 0 && mBeautyAdapter.getSelectPos() <= mBeautyAdapter.getItemCount()) {
                /*
                 * 不是基础滤镜在最前
                 * Not the base filter is first
                 */
                if (mBeautyAdapter.getSelectPos() < BeautyShapeAdapter.POS_BEAUTY_ADJUSTCOLOR_3) {
                    mBeautySeekBar.setVisibility(View.VISIBLE);
                    double tempLevel = mBeautyFx.getFloatVal(mBeautyAdapter.getSelectItem().beautyShapeId);
                    mBeautySeekBar.setProgress((int) (tempLevel * 100));
                }
            }
        }
    }

    /**
     * 美颜dialog 动作监听
     * Beauty dialog action monitoring
     */
    private void initBeautyClickListener() {
        /*
         *美颜控制开关
         *Beauty control switch
         */
        mBeautyTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeautySeekBar.setVisibility(View.INVISIBLE);
                mShapeSeekBar.setVisibility(View.INVISIBLE);
                mShapeText.setVisibility(View.INVISIBLE);
                mShapeText2.setVisibility(View.INVISIBLE);
                if (mBeautySwitchIsOpend) {
                    adjustColorOrBeauty();
                }
                mIsBeautyType = true;
                mBeautyTabButton.setSelected(true);
                mShapeTabButton.setSelected(false);
                mVSkinBeautyLine.setBackgroundColor(getResources().getColor(R.color.menu_selected));
                mVShapeBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                mBeautySelectRelativeLayout.setVisibility(View.VISIBLE);
                mShapeSelectRelativeLayout.setVisibility(View.GONE);
                /*if (OPEN_ALL_SWITCH) {
                    if ((mCanUseARFaceType == HUMAN_AI_TYPE_MS) && (mArSceneFaceEffect != null)) {
                        mArSceneFaceEffect.setBooleanVal("Beauty Effect", true);
                        mArSceneFaceEffect.setBooleanVal("Beauty Shape", false);
                    }
                }*/
            }
        });
        //美白模式A
        mTvBeautyA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                    changeBeautyWhiteMode(mArSceneFaceEffect, true, true);
                } else {
                    changeBeautyWhiteMode(mBeautyFx, true, true);
                }
            }
        });
        //美白模式B
        mTvBeautyB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                    changeBeautyWhiteMode(mArSceneFaceEffect, false, true);
                } else {
                    changeBeautyWhiteMode(mBeautyFx, false, true);
                }
            }
        });

        mShapeTabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeautySeekBar.setVisibility(View.INVISIBLE);
                mShapeSeekBar.setVisibility(View.INVISIBLE);
                mShapeText.setVisibility(View.INVISIBLE);
                mShapeText2.setVisibility(View.INVISIBLE);
                mTvBeautyA.setVisibility(View.GONE);
                mTvBeautyB.setVisibility(View.GONE);
                mBeautyTabButton.setSelected(false);
                mShapeTabButton.setSelected(true);
                mVSkinBeautyLine.setBackgroundColor(getResources().getColor(R.color.colorTranslucent));
                mVShapeBeautyLine.setBackgroundColor(getResources().getColor(R.color.menu_selected));
                mBeautySelectRelativeLayout.setVisibility(View.GONE);
                mShapeSelectRelativeLayout.setVisibility(View.VISIBLE);
                //此时切换到美型 如果之前选择过美妆，切换过来需要显示默认选中美型1效果
                if (selectMarkUp) {
                    //显示美型类型的列表
                    BeautyShapeDataKindItem defaultItem = mShapeKindAdapter.getItemByPosition(0);
                    if (null == defaultItem) {
                        return;
                    }
                    upDataSelectedKindFaceType(defaultItem, false);
                    //且显示美型1,并设置效果
                    mShapeKindAdapter.setSelectPos(0);
                    //更改策略
                    initBeautyShapeStrategy(defaultItem.getType());
                    upDateEffectValue(defaultItem.getType(), false);
                    currentSelectBeautyShapePosition = 0;

                    selectMarkUp = false;
                }
                /*if (OPEN_ALL_SWITCH) {
                    if ((mCanUseARFaceType == HUMAN_AI_TYPE_MS) && (mArSceneFaceEffect != null)) {
                        mArSceneFaceEffect.setBooleanVal("Beauty Shape", true);
                    }
                }*/
            }
        });

        mSharpenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                    if (mArSceneFaceEffect.getBooleanVal("Default Sharpen Enabled")) {
                        mArSceneFaceEffect.setBooleanVal("Default Sharpen Enabled", false);
                        ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.sharpen_close), "#CCFFFFFF", R.color.colorTranslucent);
                    } else {
                        mArSceneFaceEffect.setBooleanVal("Default Sharpen Enabled", true);
                        ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.sharpen_open), "#CCFFFFFF", R.color.colorTranslucent);
                    }
                } else {
                    if (mBeautyFx.getBooleanVal("Default Sharpen Enabled")) {
                        mBeautyFx.setBooleanVal("Default Sharpen Enabled", false);
                        ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.sharpen_close), "#CCFFFFFF", R.color.colorTranslucent);
                    } else {
                        mBeautyFx.setBooleanVal("Default Sharpen Enabled", true);
                        ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.sharpen_open), "#CCFFFFFF", R.color.colorTranslucent);
                    }
                }
            }
        });

        mAdjustColorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkAdjustColor();
                    mBeautySeekBar.setVisibility(View.VISIBLE);
                    ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.default_beauty_open), "#CCFFFFFF", R.color.colorTranslucent);
                    /*double tempLevel = mArSceneFaceEffect.getFloatVal("Default Intensity");
                    mBeautySeekBar.setProgress((int) (tempLevel * 100));*/
                    if(mAdjustColorFx != null){
                        mAdjustColorFx.setFilterIntensity(mBeautySeekBar.getProgress() * 1.0f / 100);
                    }
                } else {
                    if (mAdjustColorFx != null) {
                        mStreamingContext.removeCaptureVideoFx(mAdjustColorFx.getIndex());
                    }
                    mAdjustColorFx = null;
                    mBeautySeekBar.setVisibility(View.INVISIBLE);
                    ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.default_beauty_close), "#CCFFFFFF", R.color.colorTranslucent);
                }
            }
        });

        mBeauty_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBeautySwitchIsOpend = isChecked;
                setBeautySwitchChecked(isChecked);
            }
        });

        mBeauty_shape_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mShapeSwitchIsOpen = isChecked;
                if (mCanUseARFaceType != HUMAN_AI_TYPE_MS) {
                    mBeauty_shape_switch.setChecked(false);
                    String[] versionName = getResources().getStringArray(R.array.sdk_version_tips);
                    Util.showDialog(CaptureActivity.this, versionName[0], versionName[1]);
                } else {
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS && initArScene) {
                        setBeautyShapeSwitchChecked(isChecked);
                    } else {
                        /*
                         * 授权过期
                         * License expired
                         * */
                        String[] versionName = getResources().getStringArray(R.array.sdk_expire_tips);
                        Util.showDialog(CaptureActivity.this, versionName[0], versionName[1]);
                        mBeauty_shape_switch.setChecked(false);
                    }
                }
            }
        });
        mShapeSeekBar.setOnProgressChangeListener(new MagicProgress.OnProgressChangeListener() {
            @Override
            public void onProgressChange(int progress, boolean fromUser) {
                mShapeText2.setText((progress - 100) + "%");
                if (mShapeAdapter.getSelectPos() >= 0 && mShapeAdapter.getSelectPos() <= mShapeAdapter.getItemCount()) {
                    if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                        if (mArSceneFaceEffect == null) {
                            return;
                        }
                        boolean containsShapeId = mShapeIdList.contains(mShapeAdapter.getSelectItem().beautyShapeId);
                        float strength = containsShapeId ? ((float) (100 - progress) / 100) : ((float) (progress - 100) / 100);
                        mShapeAdapter.getSelectItem().needDefaultStrength = false;

                        mArSceneFaceEffect.setFloatVal(mShapeAdapter.getSelectItem().beautyShapeId, strength);
                        //值保存起来
                        //先确定是哪个类型下的值
                        int beautyShapeType = mShapeKindAdapter.getSelectItem().getType();
                        List<BeautyShapeDataItem> targetList;
                        if (beautyShapeType == BeautyShapeDataKindItem.Type.NORMAL) {
                            targetList = defaultBeautyShapeDataItemArrayList;
                        } else {
                            targetList = specialBeautyShapeDataItemArrayList;
                        }
                        //循环列表，然后改变这个值
                        if (null != targetList && targetList.size() > 0) {
                            for (BeautyShapeDataItem item : targetList) {
                                if (null != item && item.beautyShapeId.equals(mShapeAdapter.getSelectItem().beautyShapeId)) {
                                    item.strength = strength;
                                    break;
                                }
                            }
                        }
                    } else {

                    }
                }
            }
        });
        mBeautySeekBar.setOnSeekBarChangedListener(new HorizontalIndicatorSeekBar.OnSeekBarChangedListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                    if (mAdjustColorSwitch.isChecked() && mBeautyAdapter.getSelectPos() == BeautyShapeAdapter.POS_BEAUTY_ADJUSTCOLOR_3) {
                        // mDefaultBeautyIntensity = progress * 1.0 / 100;
                        //mArSceneFaceEffect.setFilterIntensity((float) mDefaultBeautyIntensity);
                        if (mAdjustColorFx != null) {
                            mAdjustColorFx.setFilterIntensity((float) (progress * 1.0 / 100));
                        }
                    } else if (mBeautyAdapter.getSelectPos() >= 0 && mBeautyAdapter.getSelectPos() < BeautyShapeAdapter.POS_BEAUTY_ADJUSTCOLOR_3) {
                        double tempLevel = progress * 1.0 / 100;
                        mBeautyAdapter.getSelectItem().needDefaultStrength = false;
                        mArSceneFaceEffect.setFloatVal(mBeautyAdapter.getSelectItem().beautyShapeId, tempLevel);
                    }

                } else {
                    if (mAdjustColorSwitch.isChecked() && mBeautyAdapter.getSelectPos() == BeautyShapeAdapter.POS_BEAUTY_ADJUSTCOLOR_3) {

                        // mDefaultBeautyIntensity = progress * 1.0 / 100;
                        //  mBeautyFx.setFloatVal("Default Intensity", mDefaultBeautyIntensity);
                        if (mAdjustColorFx != null) {
                            mAdjustColorFx.setFilterIntensity((float) (progress * 1.0 / 100));
                        }
                        /*
                         * 美颜
                         *Beauty
                         * */
                    } else if (mBeautyAdapter.getSelectPos() >= 0 && mBeautyAdapter.getSelectPos() < BeautyShapeAdapter.POS_BEAUTY_ADJUSTCOLOR_3) {
                        double tempLevel = progress * 1.0 / 100;
                        mBeautyFx.setFloatVal(mBeautyAdapter.getSelectItem().beautyShapeId, tempLevel);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void changeBeautyWhiteMode(NvsCaptureVideoFx videoEffect, boolean isOpen, boolean isExchange) {
        if (isExchange) {
            if (isOpen) {
                videoEffect.setStringVal("Default Beauty Lut File", "");
                videoEffect.setStringVal("Whitening Lut File", "");
                videoEffect.setBooleanVal("Whitening Lut Enabled", false);
                ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.whiteningA), "#CCFFFFFF", R.color.colorTranslucent);
                mBeautyAdapter.setWittenName(1, getResources().getString(R.string.whitening_A));
                mTvBeautyA.setBackgroundResource(R.drawable.bg_left_corners_blue63);
                mTvBeautyA.setTextColor(getResources().getColor(R.color.white));
                mTvBeautyB.setBackgroundResource(R.drawable.bg_right_corners_white);
                mTvBeautyB.setTextColor(getResources().getColor(R.color.blue_63));
            } else {
                videoEffect.setStringVal("Default Beauty Lut File", "assets:/capture/preset.mslut");
                videoEffect.setStringVal("Whitening Lut File", "assets:/capture/filter.png");
                videoEffect.setBooleanVal("Whitening Lut Enabled", true);
                ToastUtil.showToastCenterWithBg(getApplicationContext(), getResources().getString(R.string.whiteningB), "#CCFFFFFF", R.color.colorTranslucent);
                mBeautyAdapter.setWittenName(1, getResources().getString(R.string.whitening_B));

                mTvBeautyA.setBackgroundResource(R.drawable.bg_left_corners_white);
                mTvBeautyA.setTextColor(getResources().getColor(R.color.blue_63));
                mTvBeautyB.setBackgroundResource(R.drawable.bg_right_corners_blue63);
                mTvBeautyB.setTextColor(getResources().getColor(R.color.white));
            }
        } else {
            if (isOpen) {
                videoEffect.setStringVal("Default Beauty Lut File", "assets:/capture/preset.mslut");
                videoEffect.setStringVal("Whitening Lut File", "assets:/capture/filter.png");
                videoEffect.setBooleanVal("Whitening Lut Enabled", true);
                mBeautyAdapter.setWittenName(1, getResources().getString(R.string.whitening_B));
                mTvBeautyA.setBackgroundResource(R.drawable.bg_left_corners_white);
                mTvBeautyA.setTextColor(getResources().getColor(R.color.blue_63));
                mTvBeautyB.setBackgroundResource(R.drawable.bg_right_corners_blue63);
                mTvBeautyB.setTextColor(getResources().getColor(R.color.white));
            } else {
                videoEffect.setStringVal("Default Beauty Lut File", "");
                videoEffect.setStringVal("Whitening Lut File", "");
                videoEffect.setBooleanVal("Whitening Lut Enabled", false);
                mBeautyAdapter.setWittenName(1, getResources().getString(R.string.whitening_A));
                mTvBeautyA.setBackgroundResource(R.drawable.bg_left_corners_blue63);
                mTvBeautyA.setTextColor(getResources().getColor(R.color.white));
                mTvBeautyB.setBackgroundResource(R.drawable.bg_right_corners_white);
                mTvBeautyB.setTextColor(getResources().getColor(R.color.blue_63));
            }
        }
    }

    private void setBeautySwitchChecked(boolean isChecked) {
        if (isChecked) {
            //mBeautyWhiteningView.setEnabled(true);
            if (mCanUseARFaceType == HUMAN_AI_TYPE_MS && initArScene) {
                mArSceneFaceEffect.setBooleanVal("Beauty Effect", true);
                /*
                 * 基础滤镜强度
                 * Basic filter strength value
                 * */
                mDefaultBeautyIntensity = mArSceneFaceEffect.getFloatVal("Default Intensity");
            } else {
                /*
                 * 内建美颜效果
                 * Built-in beauty effect objects
                 * */
                mBeautyFx = mStreamingContext.appendBeautyCaptureVideoFx();
                mBeautyFx.setBooleanVal("Default Sharpen Enabled", false);
                /*
                 * 基础滤镜强度
                 * Basic filter strength value
                 * */
                mDefaultBeautyIntensity = mBeautyFx.getFloatVal("Default Intensity");
            }
            boolean ret = removeFilterFxByName("Cartoon");
            if (ret) {
                mFilterView.setSelectedPos(0);
                mFilterView.notifyDataSetChanged();
            }
            mBeauty_switch_text.setText(R.string.beauty_close);
            mBeauty_switch_text.setTextColor(getResources().getColor(R.color.black));
            mBeautyAdapter.setWittenName(1, getResources().getString(R.string.whitening));
        } else {
            /*
             *重置索引位置
             *Reset index position
             * */
            mBeautyAdapter.setSelectPos(Integer.MAX_VALUE);
            mAdjustColorLayout.setVisibility(View.INVISIBLE);
            mSharpenLayout.setVisibility(View.INVISIBLE);

            mBeautySeekBar.setVisibility(View.INVISIBLE);
            mBeauty_switch_text.setText(R.string.beauty_open);
            mBeauty_switch_text.setTextColor(getResources().getColor(R.color.ms_disable_color));
            mBeautyAdapter.setWittenName(1, getResources().getString(R.string.whitening));
            mTvBeautyA.setVisibility(View.GONE);
            mTvBeautyB.setVisibility(View.GONE);
            if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
                if (mArSceneFaceEffect != null) {
                    //mArSceneFaceEffect.setFloatVal("Beauty Strength", 0.5);
                    // mArSceneFaceEffect.setFloatVal("Beauty Whitening", 0);
                    // mArSceneFaceEffect.setFloatVal("Beauty Reddening", 0);
                    // mArSceneFaceEffect.setFloatVal("Default Intensity", 1.0);
                    mArSceneFaceEffect.setBooleanVal("Default Sharpen Enabled", mArSceneFaceEffect.getBooleanVal("Default Sharpen Enabled"));
                    mArSceneFaceEffect.setBooleanVal("Beauty Effect", false);
                }
            } else {
                // mBeautyFx.setFloatVal("Default Intensity", 1.0);
                mBeautyFx.setBooleanVal("Default Sharpen Enabled", false);
                removeFilterFxByName("Beauty");
                mBeautyFx = null;
            }
        }
        mBeautyAdapter.setEnable(isChecked);
        mBeauty_switch.setChecked(isChecked);
    }

    private void setBeautyShapeSwitchChecked(boolean isChecked) {
        if (isChecked) {
            mBeauty_shape_switch_text.setText(R.string.beauty_shape_close);
            mArSceneFaceEffect.setBooleanVal("Beauty Shape", true);
            mBeauty_shape_switch_text.setText(R.string.beauty_shape_close);
            boolean ret1 = removeFilterFxByName("Cartoon");
            if (ret1) {
                mFilterView.setSelectedPos(0);
                mFilterView.notifyDataSetChanged();
            }
        } else {
            /*
             * 美型功能关闭;
             *Beauty function is turned off;
             * */
            // resetBeautyShapeDefaultValue();
            mArSceneFaceEffect.setBooleanVal("Beauty Shape", false);
            mBeauty_shape_switch_text.setText(R.string.beauty_shape_open);
            mShapeAdapter.setSelectPos(Integer.MAX_VALUE);
            mShapeSeekBar.setVisibility(View.INVISIBLE);
            mShapeText.setVisibility(View.INVISIBLE);
            mShapeText2.setVisibility(View.INVISIBLE);
        }
        mBeauty_shape_switch.setChecked(isChecked);
        shapeLayoutEnabled(isChecked);
    }

    private void onMakeupDataChanged() {
        if (mArSceneFaceEffect == null) {
            return;
        }
        mArSceneFaceEffect.setBooleanVal("Beauty Effect", true);
        //mArSceneFaceEffect.setBooleanVal("Default Beauty Enabled", false);
        mArSceneFaceEffect.setFloatVal("Makeup Intensity", 1.0f);
        mArSceneFaceEffect.setIntVal("Makeup Custom Enabled Flag", MakeupData.getInstacne().getMakeupFlag());
        mArSceneFaceEffect.setArbDataVal("Makeup Custom Info", MakeupData.getInstacne().getMakeupEffectInfo());
    }

    private void onMakeupComposeDataChanged(int position, boolean isClearMakeup) {
        if (position == 0 && mArSceneFaceEffect != null) {
            if (isClearMakeup) {
                mArSceneFaceEffect.setIntVal("Makeup Custom Enabled Flag", 0);
            }
            Set<String> fxSet = MakeupData.getInstacne().getFxSet();
            for (String fx : fxSet) {
                removeFilterFxById(fx);
            }
            MakeupData.getInstacne().clearData();
            return;
        }

        if (mArSceneFaceEffect == null) {
            return;
        }
        BeautyData selectItem = mMakeUpView.getSelectItem();
        if (selectItem instanceof ComposeMakeup) {
            ComposeMakeup item = (ComposeMakeup) selectItem;
            ComposeEffectContent effectContent = item.getEffectContent();
            if (effectContent == null) {
                return;
            }
            List<BeautyFxArgs> beauty = effectContent.getBeauty();
            if (beauty != null && beauty.size() > 0) {
                if (!mBeauty_switch.isChecked()) {
                    setBeautySwitchChecked(true);
                }
            }
            setMakeupBeautyArgs(beauty);
            List<BeautyFxArgs> shape = effectContent.getShape();
            if (shape != null && shape.size() > 0) {
                if (mCanUseARFaceType == HUMAN_AI_TYPE_MS && initArScene) {
                    setBeautyShapeSwitchChecked(true);
                }
            }
            setMakeupBeautyArgs(shape);

            List<FilterArgs> filter = effectContent.getFilter();
            if (filter != null && filter.size() > 0) {
                for (FilterArgs filterArgs : filter) {
                    String packageId = filterArgs.getPackageId();
                    if (!MakeupData.getInstacne().getFxSet().contains(packageId)) {
                        if (filterArgs.getIsBuiltIn() == 1) {
                            mStreamingContext.appendBuiltinCaptureVideoFx(packageId);
                        } else {
                            mStreamingContext.appendPackagedCaptureVideoFx(packageId);
                            MakeupData.getInstacne().putFx(packageId);
                        }
                    }
                }
            } else {
                Set<String> fxSet = MakeupData.getInstacne().getFxSet();
                for (String fx : fxSet) {
                    removeFilterFxById(fx);
                }
                MakeupData.getInstacne().clearData();
            }
            NvsMakeupEffectInfo makeupEffectInfo = effectContent.parseToNvsMakeupData();

            MakeupEffectContent makeupEffectContent = item.getEffectContent();
            MakeupData.getInstacne().addMakeupArgs(makeupEffectContent);

            mArSceneFaceEffect.setBooleanVal("Beauty Effect", true);
            //mArSceneFaceEffect.setBooleanVal("Default Beauty Enabled", false);
            mArSceneFaceEffect.setFloatVal("Makeup Intensity", 1.0f);
            mArSceneFaceEffect.setIntVal("Makeup Custom Enabled Flag", MakeupData.getMakeupFlag(effectContent.getMakeupArgs()));
            mArSceneFaceEffect.setArbDataVal("Makeup Custom Info", makeupEffectInfo);

        }
    }

    private void setMakeupBeautyArgs(List<BeautyFxArgs> shape) {
        if ((shape != null) && (shape.size() > 0)) {
            for (BeautyFxArgs beautyFxArgs : shape) {
                String fxName = beautyFxArgs.getFxName();
                String value = beautyFxArgs.getValue();
                if ("true".equals(value) || "false".equals(value)) {
                    mArSceneFaceEffect.setBooleanVal(fxName, "true".equals(value) ? true : false);
                } else {
                    //json 判断是美白A还是美白B
                    if (TextUtils.equals(fxName, "Beauty Whitening A")) {
                        fxName = "Beauty Whitening";
                        changeBeautyWhiteMode(mArSceneFaceEffect, false, false);
                    } else if (TextUtils.equals(fxName, "Beauty Whitening B")) {
                        fxName = "Beauty Whitening";
                        changeBeautyWhiteMode(mArSceneFaceEffect, true, false);
                    }
                    mArSceneFaceEffect.setFloatVal(fxName, (mShapeIdList.contains(fxName)) ? -Float.parseFloat(value) : Float.parseFloat(value));
                }
                MakeupData.getInstacne().putFx(fxName);
            }
        }
    }

    /**
     * 重置为默认初始值
     * Reset to default initial value
     */
    private void resetBeautyShapeDefaultValue(int kindType) {
        if (mCanUseARFaceType == HUMAN_AI_TYPE_MS) {
           /* mArSceneFaceEffect.setFloatVal("Face Size Warp Degree", 0f);
            mArSceneFaceEffect.setFloatVal("Eye Size Warp Degree", 0f);
            mArSceneFaceEffect.setFloatVal("Nose Width Warp Degree", 0f);
            mArSceneFaceEffect.setFloatVal("Chin Length Warp Degree", 0f);
            mArSceneFaceEffect.setFloatVal("Face Length Warp Degree", 0f);
            mArSceneFaceEffect.setFloatVal("Face Width Warp Degree", 0f);
            mArSceneFaceEffect.setFloatVal("Forehead Height Warp Degree", 0f);
            mArSceneFaceEffect.setFloatVal("Nose Length Warp Degree", 0f);
            mArSceneFaceEffect.setFloatVal("Eye Corner Stretch Degree", 0f);
            mArSceneFaceEffect.setFloatVal("Mouth Size Warp Degree", 0f);
            mArSceneFaceEffect.setFloatVal("Mouth Corner Lift Degree", 0f);*/
            upDateEffectValue(kindType, true);
        } else {
            return;
        }
    }

    private void stopRecording() {
        mStreamingContext.stopRecording();
        // mStartRecordingImage.setBackgroundResource(R.mipmap.capture_recording_stop);
        /*
         * 拍视频
         * Take a video
         * */
        if (mRecordType == Constants.RECORD_TYPE_VIDEO) {
            mAllRecordingTime += mEachRecodingVideoTime;
            mRecordTimeList.add(mEachRecodingVideoTime);
            mStartText.setText(mRecordTimeList.size() + "");
            changeRecordDisplay(RECORD_FINISH, false);
        } else {
            changeRecordDisplay(RECORD_FINISH, true);
        }
    }

    private void removeAllFilterFx() {
        List<Integer> remove_list = new ArrayList<>();
        for (int i = 0; i < mStreamingContext.getCaptureVideoFxCount(); i++) {
            NvsCaptureVideoFx fx = mStreamingContext.getCaptureVideoFxByIndex(i);
            if (fx == null) {
                continue;
            }
            String name = fx.getBuiltinCaptureVideoFxName();
            if (name != null && !"Beauty".equals(name) && !"Face Effect".equals(name) && !"AR Scene".equals(name)) {
                remove_list.add(i);
                Log.e("===>", "fx name: " + name);
            }
        }
        if (!remove_list.isEmpty()) {
            //这里倒着删，否则会出现移除错误的问题。
            for (int i = remove_list.size()-1; i >= 0; i--) {
                mStreamingContext.removeCaptureVideoFx(remove_list.get(i));
            }
        }
    }

    private boolean removeFilterFxByName(String name) {
        for (int i = 0; i < mStreamingContext.getCaptureVideoFxCount(); i++) {
            NvsCaptureVideoFx fx = mStreamingContext.getCaptureVideoFxByIndex(i);
            String name1 = fx.getDescription().getName();
            if (name1.equals(name)) {
                mStreamingContext.removeCaptureVideoFx(i);
                return true;
            }
        }
        return false;
    }

    private boolean removeFilterFxById(String name) {
        for (int i = 0; i < mStreamingContext.getCaptureVideoFxCount(); i++) {
            NvsCaptureVideoFx fx = mStreamingContext.getCaptureVideoFxByIndex(i);
            String name1 = fx.getCaptureVideoFxPackageId();
            if (name1.equals(name)) {
                mStreamingContext.removeCaptureVideoFx(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 显示对话框窗口
     * Show dialog window
     */
    private void showCaptureDialogView(AlertDialog dialog, View view) {
        showCaptureDialogView(dialog, view, false);
    }

    /**
     * 显示对话框窗口
     * Show dialog window
     */
    private void showCaptureDialogView(AlertDialog dialog, View view, boolean matchParent) {
        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        /*
         * 动画时间500毫秒
         *The animation time is 500 ms
         * */
        translate.setDuration(200);
        translate.setFillAfter(false);
        //mStartLayout.startAnimation(translate);
        dialog.show();
        if (view != null) {
            dialog.setContentView(view);
        }
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        if (matchParent) {
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
        params.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.colorTranslucent));
        dialog.getWindow().setWindowAnimations(R.style.fx_dlg_style);
        //  isShowCaptureButton(false);
    }

    /**
     * 关闭对话框窗口
     * Close dialog window
     */
    private void closeCaptureDialogView(AlertDialog dialog) {
        dialog.dismiss();
        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF,
                1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        /*
         * 动画时间300毫秒
         *The animation time is 300 ms
         * */
        translate.setDuration(300);
        translate.setFillAfter(false);
    }

    /**
     * 改动拍摄屏幕比例
     **/
    private void changeAspectRatio() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager mWm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWm.getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        int ratioHeight = dm.widthPixels / 9 * 16;
        // Log.d("lhz",  "ratioHeight=" + ratioHeight + "**screenHeight=" + screenHeight);
        if (ratioHeight < screenHeight) {
            RelativeLayout.LayoutParams lvWindowParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lvWindowParams.height = ratioHeight;

            int bottomHeight = ScreenUtils.dip2px(this, 161);
            int largeHeight = screenHeight - ratioHeight;

            //Log.d("lhz", "largeHeight=" + largeHeight + "**bottomHeight=" + bottomHeight+"**");
            if (largeHeight > bottomHeight) {
                mFlBottomParent.setBackgroundColor(getResources().getColor(R.color.white));
                mIvMakeup.setImageResource(R.mipmap.capture_makeup_black);
                mTvMakeup.setTextColor(getResources().getColor(R.color.black));

                mIvBauty.setImageResource(R.mipmap.capture_beauty_black);
                mTvBeauty.setTextColor(getResources().getColor(R.color.black));

                mIvProps.setImageResource(R.mipmap.capture_props_black);
                mTvProps.setTextColor(getResources().getColor(R.color.black));

                mIvFilter.setImageResource(R.mipmap.capture_filter_black);
                mTvFilter.setTextColor(getResources().getColor(R.color.black));
            } else {
                RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                int tempHeight = bottomHeight - largeHeight;
                if (tempHeight < 10) {
                    //如果底部固定的高度比剩余的高度略低，底部布局高度降低并设置白色背景
                    mFlBottomParent.setBackgroundColor(getResources().getColor(R.color.white));
                    bottomParams.height = bottomHeight - tempHeight;
                    mIvMakeup.setImageResource(R.mipmap.capture_makeup_black);
                    mTvMakeup.setTextColor(getResources().getColor(R.color.black));

                    mIvBauty.setImageResource(R.mipmap.capture_beauty_black);
                    mTvBeauty.setTextColor(getResources().getColor(R.color.black));

                    mIvProps.setImageResource(R.mipmap.capture_props_black);
                    mTvProps.setTextColor(getResources().getColor(R.color.black));

                    mIvFilter.setImageResource(R.mipmap.capture_filter_black);
                    mTvFilter.setTextColor(getResources().getColor(R.color.black));
                } else {
                    RelativeLayout.LayoutParams middleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    middleParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    int maxTopMargin = ScreenUtils.dip2px(this, 20);
                    bottomParams.height = bottomHeight;
                    if (maxTopMargin >= largeHeight / 3) {
                        //顶部最多往下移动maxTopMargin，为了不遮盖上部的返回、切换摄像头图标
                        middleParams.bottomMargin = largeHeight / 3 * 2 + ScreenUtils.dip2px(this, 159);
                        bottomParams.bottomMargin = largeHeight / 3 * 2;
                        lvWindowParams.topMargin = largeHeight / 3;
                    } else {
                        middleParams.bottomMargin = largeHeight - maxTopMargin + ScreenUtils.dip2px(this, 159);
                        bottomParams.bottomMargin = largeHeight - maxTopMargin;
                        lvWindowParams.topMargin = maxTopMargin;
                    }
                    mFlMiddleParent.setLayoutParams(middleParams);
                }
                mFlBottomParent.setLayoutParams(bottomParams);
            }
            mLiveWindow.setLayoutParams(lvWindowParams);
        }
    }

    private void initCaptureData() {
        mStreamingContext.removeAllCaptureVideoFx();
        mFocusAnimation = new AlphaAnimation(1.0f, 0.0f);
        mFocusAnimation.setDuration(1000);
        mFocusAnimation.setFillAfter(true);
        mCanUseARFaceType = NvsStreamingContext.hasARModule();
    }

    private void initCapture() {
        if (null == mStreamingContext) {
            return;
        }
        /*
         *给Streaming Context设置回调接口
         *Set callback interface for Streaming Context
         * */
        setStreamingCallback(false);
        if (mStreamingContext.getCaptureDeviceCount() == 0) {
            return;
        }

        /*
         * 将采集预览输出连接到LiveWindow控件
         * Connect the capture preview output to the LiveWindow control
         * */
        if (!mStreamingContext.connectCapturePreviewWithLiveWindow(mLiveWindow)) {
            Log.e(TAG, "Failed to connect capture preview with livewindow!");
            return;
        }

        mCurrentDeviceIndex = 0;
        /*
         * 采集设备数量判定
         * Judging the count of collection equipment
         * */
        if (mStreamingContext.getCaptureDeviceCount() > 1) {
            mIvChangeCamera.setEnabled(true);
        } else {
            mIvChangeCamera.setEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        } else {
            try {
                startCapturePreview(false);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "startCapturePreviewException: initCapture failed,under 6.0 device may has no access to camera");
                PermissionDialog.noPermissionDialog(CaptureActivity.this);
                setCaptureViewEnable(false);
            }
        }
        setCaptureViewEnable(true);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                initArScene = bundle.getBoolean("initArScene");
            }
        }
    }

    private boolean startCapturePreview(boolean deviceChanged) {
        /*
         * 判断当前引擎状态是否为采集预览状态
         * Determine if the current engine status is the collection preview status
         * */
        int captureResolutionGrade = ParameterSettingValues.instance().getCaptureResolutionGrade();
        if (deviceChanged || getCurrentEngineState() != NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTUREPREVIEW) {
            m_supportAutoFocus = false;
            if (!mStreamingContext.startCapturePreview(mCurrentDeviceIndex, captureResolutionGrade,
                    NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_DONT_USE_SYSTEM_RECORDER |
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_CAPTURE_BUDDY_HOST_VIDEO_FRAME |
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_STRICT_PREVIEW_VIDEO_SIZE, null)) {
                Log.e(TAG, "Failed to start capture preview!");
                return false;
            }
        }
        return true;
    }


    /**
     * 获取当前引擎状态
     * Get the current engine status
     */
    private int getCurrentEngineState() {
        return mStreamingContext.getStreamingEngineState();
    }

    private void updateSettingsWithCapability(int deviceIndex) {
        /*
         * 获取采集设备能力描述对象，设置自动聚焦，曝光补偿，缩放
         * Get acquisition device capability description object, set auto focus, exposure compensation, zoom
         * */
        mCapability = mStreamingContext.getCaptureDeviceCapability(deviceIndex);
        if (null == mCapability) {
            return;
        }
        m_supportAutoFocus = mCapability.supportAutoFocus;
        if (mMoreDialog == null) {
            initTopMoreView();
        }
        mMoreDialog.checkCapability(mCapability);
    }

    private final int RECORD_DEFAULT = 0;
    private final int RECORDING = 1;
    private final int RECORD_FINISH = 2;

    private void changeRecordDisplay(int recordState, boolean isPicture) {
        //Log.d("lhz", "recordState=" + recordState);
        if (RECORD_DEFAULT == recordState) {
            //默认显示
            mIvExit.setVisibility(View.VISIBLE);
            mIvChangeCamera.setVisibility(View.VISIBLE);
            mIvMore.setVisibility(View.VISIBLE);

            if (isPicture) {
                mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_photo);
            } else {
                mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_video);  //视频类型拍摄按钮背景
            }
            mStartText.setVisibility(View.INVISIBLE);

            mLlMakeupLayout.setVisibility(View.VISIBLE);
            mBeautyLayout.setVisibility(View.VISIBLE);
            mFilterLayout.setVisibility(View.VISIBLE);
            mFuLayout.setVisibility(View.VISIBLE);

            mDelete.setVisibility(View.INVISIBLE);
            mVideoTimeDot.setVisibility(View.INVISIBLE);
            mNext.setVisibility(View.INVISIBLE);

            mTvChoosePicture.setVisibility(View.VISIBLE);
            mTvChooseVideo.setVisibility(View.VISIBLE);
        } else if (RECORDING == recordState) {
            //拍摄中
            mIvExit.setVisibility(View.INVISIBLE);
            mIvChangeCamera.setVisibility(View.INVISIBLE);
            mIvMore.setVisibility(View.INVISIBLE);

            if (isPicture) {
                mVideoTimeDot.setVisibility(View.INVISIBLE);
                mRecordTime.setVisibility(View.INVISIBLE);
            } else {
                mIvTakePhotoBg.setImageResource(R.mipmap.capture_stop_video);
                if (mFlMiddleParent.getVisibility() != View.VISIBLE) {
                    mFlMiddleParent.setVisibility(View.VISIBLE);
                }
                mVideoTimeDot.setVisibility(View.VISIBLE);
                mRecordTime.setVisibility(View.VISIBLE);
            }
            mStartText.setVisibility(View.INVISIBLE);

            mLlMakeupLayout.setVisibility(View.INVISIBLE);
            mBeautyLayout.setVisibility(View.INVISIBLE);
            mFilterLayout.setVisibility(View.INVISIBLE);
            mFuLayout.setVisibility(View.INVISIBLE);

            mDelete.setVisibility(View.INVISIBLE);
            mNext.setVisibility(View.INVISIBLE);

            mTvChoosePicture.setVisibility(View.INVISIBLE);
            mTvChooseVideo.setVisibility(View.INVISIBLE);
        } else if (RECORD_FINISH == recordState) {
            //拍摄完毕
            mIvExit.setVisibility(View.VISIBLE);
            mIvChangeCamera.setVisibility(View.VISIBLE);
            mIvMore.setVisibility(View.VISIBLE);

            mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_photo);
            mStartText.setVisibility(View.VISIBLE);

            mLlMakeupLayout.setVisibility(View.VISIBLE);
            mBeautyLayout.setVisibility(View.VISIBLE);
            mFilterLayout.setVisibility(View.VISIBLE);
            mFuLayout.setVisibility(View.VISIBLE);

            if (mFlMiddleParent.getVisibility() != View.VISIBLE) {
                mFlMiddleParent.setVisibility(View.VISIBLE);
            }
            mDelete.setVisibility(View.VISIBLE);
            mVideoTimeDot.setVisibility(View.INVISIBLE);
            mRecordTime.setVisibility(View.VISIBLE);
            mNext.setVisibility(View.VISIBLE);

            mTvChoosePicture.setVisibility(View.VISIBLE);
            mTvChooseVideo.setVisibility(View.VISIBLE);
        }
        if (mRecordTimeList.isEmpty()) {
            mRecordTime.setVisibility(View.INVISIBLE);
        }
    }


    private ArrayList<NvAsset> getLocalData(int assetType) {
        return mAssetManager.getUsableAssets(assetType, NvAsset.AspectRatio_All, 0);
    }


    @Override
    public void onClick(View view) {
    }

    @Override
    public void onCaptureDeviceCapsReady(int captureDeviceIndex) {
        if (captureDeviceIndex != mCurrentDeviceIndex) {
            return;
        }
        updateSettingsWithCapability(captureDeviceIndex);
    }

    @Override
    public void onCaptureDevicePreviewResolutionReady(int i) {
    }

    @Override
    public void onCaptureDevicePreviewStarted(int i) {
        mIsSwitchingCamera = false;
    }

    @Override
    public void onCaptureDeviceError(int i, int i1) {
        Log.e(TAG, "onCaptureDeviceError: initCapture failed,under 6.0 device may has no access to camera");
        PermissionDialog.noPermissionDialog(CaptureActivity.this);
        setCaptureViewEnable(false);
    }

    @Override
    public void onCaptureDeviceStopped(int i) {

    }

    @Override
    public void onCaptureDeviceAutoFocusComplete(int i, boolean b) {

    }

    @Override
    public void onCaptureRecordingFinished(int i) {
        /*
         *  保存到媒体库
         * Save to media library
         * */
        if (mRecordFileList != null && !mRecordFileList.isEmpty()) {
            for (String path : mRecordFileList) {
                if (path == null) {
                    continue;
                }
                if (path.endsWith(".mp4")) {
                    MediaScannerUtil.scanFile(path, "video/mp4");
                } else if (path.endsWith(".jpg")) {
                    MediaScannerUtil.scanFile(path, "image/jpg");
                }
            }
        }
    }

    @Override
    public void onCaptureRecordingError(int i) {

    }

    @Override
    public void onCaptureRecordingDuration(int i, long l) {
        /*
         * 拍视频or照片
         * Take a video or a photo
         * */
        if (mRecordType == Constants.RECORD_TYPE_VIDEO) {
            if (l >= Constants.MIN_RECORD_DURATION) {
                mFlStartRecord.setEnabled(true);
            }
            mEachRecodingVideoTime = l;
            if (mFlMiddleParent.getVisibility() != View.VISIBLE) {
                mFlMiddleParent.setVisibility(View.VISIBLE);
            }
            mRecordTime.setVisibility(View.VISIBLE);
            mRecordTime.setText(TimeFormatUtil.formatUsToString2(mAllRecordingTime + mEachRecodingVideoTime));
        } else if (mRecordType == Constants.RECORD_TYPE_PICTURE) {
            if (l > 40000) {
                stopRecording();
                takePhoto(l);
            }
        }
    }

    @Override
    public void onCaptureRecordingStarted(int i) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == REQUEST_FILTER_LIST_CODE) {
                    initFilterList();
                    mFilterView.setFilterArrayList(mFilterDataArrayList);
                    mFilterSelPos = AssetFxUtil.getSelectedFilterPos(mFilterDataArrayList, mVideoClipFxInfo);
                    mFilterView.setSelectedPos(mFilterSelPos);
                    mFilterView.notifyDataSetChanged();
                } else if (requestCode == ARFACE_LIST_REQUES_CODE) {
                    initFacUPropDataList();
                    // mFaceUPropView.setPropDataArrayList(mPropDataArrayList);
                    mPropsDialog.setPropsData(mPropsList);
                    // mFaceUPropSelPos = AssetFxUtil.getSelectedFaceUPropPos(mPropDataArrayList, mArSceneId);
                    //  mPropsDialog.selectPosition(mFaceUPropSelPos);
                    // mFaceUPropView.setSelectedPos(mFaceUPropSelPos);
                    // mFaceUPropView.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected List<String> initPermissions() {
        return Util.getAllPermissionsList();
    }

    @Override
    protected void hasPermission() {
        /*
         * 初始化拍摄
         * Initial shooting
         * */
        startCapturePreview(false);
    }

    @Override
    protected void nonePermission() {
        Log.d(TAG, "initCapture failed,above 6.0 device may has no access to camera");
        setCaptureViewEnable(false);
        PermissionDialog.noPermissionDialog(CaptureActivity.this);
    }

    @Override
    protected void noPromptPermission() {
        Logger.e(TAG, "initCapture failed,above 6.0 device has no access from user");
        setCaptureViewEnable(false);
        PermissionDialog.noPermissionDialog(CaptureActivity.this);
    }

    @Override
    protected void onDestroy() {
        if (mMoreDialog != null) {
            mMoreDialog.dismiss();
        }
        destroy();
        MakeupData.getInstacne().setComposeIndex(0);
        MakeupData.getInstacne().clearPositionData();
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mNext.setClickable(true);
        mFilterView.setMoreFilterClickable(true);
        // mFaceUPropView.setMoreFaceUPropClickable(true);
        startCapturePreview(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getCurrentEngineState() == NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTURERECORDING) {
            stopRecording();
        }
        if (mMoreDialog != null) {
            mMoreDialog.setFlashEnable(false);
        }
    }

    private void destroy() {
        if (mStreamingContext != null) {
            mStreamingContext.removeAllCaptureVideoFx();
            mStreamingContext.stop();
            setStreamingCallback(true);
            mStreamingContext = null;
        }
        mRecordTimeList.clear();
        mRecordFileList.clear();
        mFilterDataArrayList.clear();
        mPropsList.clear();
    }

    private void setStreamingCallback(boolean isDestroyCallback) {
        mStreamingContext.setCaptureDeviceCallback(isDestroyCallback ? null : this);
        mStreamingContext.setCaptureRecordingDurationCallback(isDestroyCallback ? null : this);
        mStreamingContext.setCaptureRecordingStartedCallback(isDestroyCallback ? null : this);
    }

    private void takePhoto(long time) {
        if (mCurRecordVideoPath != null) {
            NvsVideoFrameRetriever videoFrameRetriever = mStreamingContext.createVideoFrameRetriever(mCurRecordVideoPath);
            if (videoFrameRetriever != null) {
                mPictureBitmap = videoFrameRetriever.getFrameAtTimeWithCustomVideoFrameHeight(time, ScreenUtils.getScreenHeight(this));
                Log.d("takePhoto", "screen: " + ScreenUtils.getScreenWidth(this) + " " + ScreenUtils.getScreenHeight(this) + "**bitmap=" + mPictureBitmap);
                if (mPictureBitmap != null) {
                    mPictureImage.setImageBitmap(mPictureBitmap);
                    showPictureLayout(true);
                } else {
                    changeRecordDisplay(RECORD_DEFAULT, true);
                }
                videoFrameRetriever.release();
            }
        }
    }

    private void selectRecordType(boolean ivPicture) {
        int[] location = new int[2];
        mFlStartRecord.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
        float middleX = location[0] + mFlStartRecord.getWidth() / 2f;
        float targetX;
        if (ivPicture) {
            if (mRecordType == Constants.RECORD_TYPE_PICTURE) {
                return;
            }
            targetX = middleX;
            mTvChoosePicture.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mTvChooseVideo.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_photo);
            mRecordType = Constants.RECORD_TYPE_PICTURE;
        } else {
            mTvChooseVideo.getLocationInWindow(location);
            targetX = location[0] + mTvChooseVideo.getWidth() / 2f;
            mTvChooseVideo.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            mTvChoosePicture.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            mIvTakePhotoBg.setImageResource(R.mipmap.capture_take_video);
            mRecordType = Constants.RECORD_TYPE_VIDEO;
        }
        // Log.d("lhz","middleX="+middleX+"**targetX="+targetX+"**ex="+(middleX-targetX));
        ObjectAnimator animator = ObjectAnimator.ofFloat(mRecordTypeLayout, "translationX", middleX - targetX);
        animator.setDuration(300);
        animator.start();
    }

    private void showPictureLayout(boolean show) {
        TranslateAnimation topTranslate;
        if (show) {
            mRlPhotosLayout.setVisibility(View.INVISIBLE);
            topTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            topTranslate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mRlPhotosLayout.clearAnimation();
                    mIvExit.setVisibility(View.GONE);
                    mRlPhotosLayout.setVisibility(View.VISIBLE);
                    mRlPhotosLayout.setClickable(true);
                    mRlPhotosLayout.setFocusable(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {
            mFlStartRecord.setEnabled(true);
            mIvExit.setVisibility(View.VISIBLE);
            topTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);

            topTranslate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mRlPhotosLayout.clearAnimation();
                    mRlPhotosLayout.setVisibility(View.GONE);
                    mRlPhotosLayout.setClickable(false);
                    mRlPhotosLayout.setFocusable(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        topTranslate.setDuration(300);
        topTranslate.setFillAfter(true);
        mRlPhotosLayout.setAnimation(topTranslate);
    }

    public void setCaptureViewEnable(boolean enable) {
        //  mBottomLayout.setEnabled(enable);
        // mBottomLayout.setClickable(enable);
        // mFunctionButtonLayout.setEnabled(enable);
        //  mFunctionButtonLayout.setClickable(enable);
        // mRecordTypeLayout.setEnabled(enable);
        // mRecordTypeLayout.setClickable(enable);
    }
}
