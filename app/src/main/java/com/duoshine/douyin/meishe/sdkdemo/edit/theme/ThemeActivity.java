package com.duoshine.douyin.meishe.sdkdemo.edit.theme;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.base.BaseActivity;
import com.duoshine.douyin.meishe.sdkdemo.download.AssetDownloadActivity;
import com.duoshine.douyin.meishe.sdkdemo.edit.VideoFragment;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.FilterItem;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.CustomTitleBar;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.InputDialog;
import com.duoshine.douyin.meishe.sdkdemo.utils.AppManager;
import com.duoshine.douyin.meishe.sdkdemo.utils.Constants;
import com.duoshine.douyin.meishe.sdkdemo.utils.TimelineUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.Util;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAssetManager;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TransitionInfo;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by yyj on 2018/5/30 0030.
 */

public class ThemeActivity extends BaseActivity {

    private static final String TAG = "ThemeActivity";
    private static final int THEMEREQUESTLIST = 101;
    private VideoFragment mVideoFragment;
    private CustomTitleBar mTitleBar;
    private RelativeLayout mBottomLayout;
    private RecyclerView mThemeRecyclerView;
    private RelativeLayout mDownloadMoreBtn;
    private ImageView mDowanloadImage;
    private TextView mDowanloadMoreText;
    private ImageView mThemeAssetFinish;

    private ThemeAdapter mThemeAdapter;
    private ArrayList<FilterItem> mThemeDataList;
    private int mSelectedPos = 0;
    private int mAssetType = NvAsset.ASSET_THEME;
    private NvAssetManager mAssetManager;
    ArrayList<CaptionInfo> mCaptionDataListClone;

    private NvsStreamingContext mStreamingContext;
    private NvsTimeline mTimeline;

    private NvsVideoTrack mVidoeTrack;
    private int mCurentClipCount = 0;

    private long mTotalDuration = 0;
    private String mThemeId = "";
    private NvsTimelineCaption mThemeCaption;
    private boolean mHasThemeTitle;
    private boolean mHasThemeTrailer;

    @Override
    protected int initRootView() {
        mStreamingContext = NvsStreamingContext.getInstance();
        return R.layout.activity_theme;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.title_bar);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mThemeRecyclerView = (RecyclerView) findViewById(R.id.theme_list);
        mDownloadMoreBtn = (RelativeLayout) findViewById(R.id.download_more_btn);
        mDowanloadImage = (ImageView) findViewById(R.id.dowanloadImage);
        mDowanloadMoreText = (TextView) findViewById(R.id.dowanloadMoreText);
        mThemeAssetFinish = (ImageView) findViewById(R.id.themeAssetFinish);
    }

    @Override
    protected void initTitle() {
        mTitleBar.setTextCenter(R.string.theme);
        mTitleBar.setBackImageVisible(View.GONE);
    }

    @Override
    protected void initData() {
        init();
        initVideoFragment();
        initThemeDataList();
        initThemeViewList();
    }

    @Override
    protected void initListener() {
        mDownloadMoreBtn.setOnClickListener(this);
        mThemeAssetFinish.setOnClickListener(this);
        mDowanloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownloadMoreBtn.callOnClick();
            }
        });
        mDowanloadMoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDownloadMoreBtn.callOnClick();
            }
        });
        mVideoFragment.setVideoFragmentCallBack(new VideoFragment.VideoFragmentListener() {
            @Override
            public void playBackEOF(NvsTimeline timeline) {
            }

            @Override
            public void playStopped(NvsTimeline timeline) {
                updateThemeCaptionAndDrawRect();
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
                /*
                * 判断若没有选中当前字幕框则选中，选中则不处理
                * Judge if the current subtitle box is not selected, select it, do not process
                * */
                boolean isInnerDrawRect = mVideoFragment.curPointIsInnerDrawRect((int) curPoint.x, (int) curPoint.y);
                if (!isInnerDrawRect)
                    return;
                new InputDialog(ThemeActivity.this, R.style.dialog, new InputDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean ok) {
                        if (ok) {
                            if (mThemeCaption == null)
                                return;
                            InputDialog d = (InputDialog) dialog;
                            String userInputText = d.getUserInputText();
                            if (mThemeCaption.getRoleInTheme() == NvsTimelineCaption.ROLE_IN_THEME_TITLE) {
                                TimelineData.instance().setThemeCptionTitle(userInputText);
                            } else if (mThemeCaption.getRoleInTheme() == NvsTimelineCaption.ROLE_IN_THEME_TRAILER) {
                                TimelineData.instance().setThemeCptionTrailer(userInputText);
                            }
                            TimelineUtil.applyTheme(mTimeline, mThemeId);
                            /*
                            * 应用主题时，需要删除字幕，然后重新添加，防止带片头主题删掉字幕
                            * When applying a theme, you need to delete the subtitles and
                            * then add them again to prevent the subtitles from deleting the subtitles.
                            * */
                            ArrayList<CaptionInfo> captionArray = TimelineData.instance().getCaptionData();
                            TimelineUtil.setCaption(mTimeline, captionArray);
                            mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_SHOW_CAPTION_POSTER);
                            updateThemeCaptionAndDrawRect();
                        }
                    }
                }).show();
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
                updateThemeCaptionAndDrawRect();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.download_more_btn:
                mDownloadMoreBtn.setClickable(false);
                Bundle bundle = new Bundle();
                bundle.putInt("titleResId", R.string.moreTheme);
                bundle.putInt("assetType", NvAsset.ASSET_THEME);
                AppManager.getInstance().jumpActivityForResult(AppManager.getInstance().currentActivity(), AssetDownloadActivity.class, bundle, THEMEREQUESTLIST);
                break;
            case R.id.themeAssetFinish:
                /*
                * 添加主题之后，判断主题中是否带有片头或者是片尾
                * After adding a theme, determine whether the theme has an intro or an intro
                * */
                int afterThemeClipsCount = mVidoeTrack.getClipCount();
                NvsVideoClip afterThemeFirstClip = mVidoeTrack.getClipByIndex(0);
                boolean hasThemeTitle = (afterThemeFirstClip.getRoleInTheme() == NvsVideoClip.ROLE_IN_THEME_TITLE);
                NvsVideoClip afterThemeLastClip = mVidoeTrack.getClipByIndex(afterThemeClipsCount - 1);
                boolean hasThemeTrailer = (afterThemeLastClip.getRoleInTheme() == NvsVideoClip.ROLE_IN_THEME_TRAILER);
                ArrayList<TransitionInfo> transitionInfoArray = TimelineData.instance().getTransitionInfoArray();

                if (mHasThemeTitle && !hasThemeTitle) {
                    /*
                    * 有片头，移除片头
                    * Has title, remove title
                    * */
                    if (transitionInfoArray != null) {
                        transitionInfoArray.remove(0);
                    }
                } else if (!mHasThemeTitle && hasThemeTitle) {
                    /*
                    * 添加片头
                    * Add title
                    * */
                    if (transitionInfoArray != null) {
                        transitionInfoArray.add(0, new TransitionInfo());
                    }
                }

                if (mHasThemeTrailer && !hasThemeTrailer) {
                    /*
                    * 移除片尾
                    * Remove end
                    * */
                    if (transitionInfoArray != null) {
                        transitionInfoArray.remove(transitionInfoArray.size() - 1);
                    }
                } else if (!mHasThemeTrailer && hasThemeTrailer) {
                    /*
                    * 添加片尾
                    * Add trailer
                    * */
                    if (transitionInfoArray != null) {
                        transitionInfoArray.add(new TransitionInfo());
                    }
                }
                TimelineData.instance().setThemeData(mThemeId);
                TimelineData.instance().setCaptionData(mCaptionDataListClone);
                if (!TextUtils.isEmpty(mThemeId)) {
                    /*
                    * 删除转场
                    * Delete transition
                    * */
                    TimelineData.instance().setTransitionInfoArray(null);
                }
                removeTimeline();
                quitActivity();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case THEMEREQUESTLIST:
                initThemeDataList();
                mThemeAdapter.setThemeDataList(mThemeDataList);
                mSelectedPos = getSelectedPos();
                mThemeAdapter.setSelectPos(mSelectedPos);
                mThemeAdapter.notifyDataSetChanged();
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
        if (mTimeline == null)
            return;
        mVidoeTrack = mTimeline.getVideoTrackByIndex(0);
        if (mVidoeTrack == null) {
            return;
        }
        mCurentClipCount = mVidoeTrack.getClipCount();
        NvsVideoClip theFirstClip = mVidoeTrack.getClipByIndex(0);
        NvsVideoClip theLastClip = mVidoeTrack.getClipByIndex(mCurentClipCount - 1);
        mHasThemeTitle = (theFirstClip.getRoleInTheme() == NvsVideoClip.ROLE_IN_THEME_TITLE);
        mHasThemeTrailer = (theLastClip.getRoleInTheme() == NvsVideoClip.ROLE_IN_THEME_TRAILER);
        mCaptionDataListClone = TimelineData.instance().cloneCaptionData();
        mThemeId = TimelineData.instance().getThemeData();
        mTotalDuration = mTimeline.getDuration();
        mThemeDataList = new ArrayList<>();
        mAssetManager = NvAssetManager.sharedInstance();
        mAssetManager.searchLocalAssets(mAssetType);
        String bundlePath = "theme";
        mAssetManager.searchReservedAssets(mAssetType, bundlePath);
    }

    private void quitActivity() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        AppManager.getInstance().finishActivity();
    }

    private ArrayList<NvAsset> getBundleData() {
        return mAssetManager.getReservedAssets(mAssetType, NvAsset.AspectRatio_All, 0);
    }

    private ArrayList<NvAsset> getLocalData() {
        return mAssetManager.getUsableAssets(mAssetType, NvAsset.AspectRatio_All, 0);
    }

    private void initVideoFragment() {
        mVideoFragment = new VideoFragment();
        mVideoFragment.setFragmentLoadFinisedListener(new VideoFragment.OnFragmentLoadFinisedListener() {
            @Override
            public void onLoadFinished() {
                mThemeAssetFinish.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mVideoFragment.seekTimeline(mStreamingContext.getTimelineCurrentPosition(mTimeline), 0);
                        updateThemeCaptionAndDrawRect();
                    }
                }, 100);
            }
        });
        /*
        * 设置主题字幕模式
        * Set theme subtitle mode
        * */
        mVideoFragment.setEditMode(Constants.EDIT_MODE_THEMECAPTION);
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

    private void initThemeViewList() {
        mThemeAdapter = new ThemeAdapter(this);
        mThemeAdapter.setThemeDataList(mThemeDataList);
        mSelectedPos = getSelectedPos();
        if (mSelectedPos >= 0) {
            mThemeAdapter.setSelectPos(mSelectedPos);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mThemeRecyclerView.setLayoutManager(linearLayoutManager);
        mThemeRecyclerView.setAdapter(mThemeAdapter);

        mThemeAdapter.setOnItemClickListener(new ThemeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position < 0 || position >= mThemeDataList.size())
                    return;
                if (mSelectedPos == position) {
                    /*
                    * 播放视频
                    * Play video
                    * */
                    mVideoFragment.playVideoButtonCilck();
                    return;
                }
                mSelectedPos = position;
                mThemeCaption = null;
                /*
                * 把片头，片尾数据置空
                * Leave the beginning and end data blank
                * */
                TimelineData.instance().setThemeCptionTitle("");
                TimelineData.instance().setThemeCptionTrailer("");
                /*
                * 应用主题
                * Apply theme
                * */
                mThemeId = mThemeDataList.get(position).getPackageId();
                TimelineUtil.applyTheme(mTimeline, mThemeId);
                mVideoFragment.updateTotalDuarationText();
                long curDuration = mTimeline.getDuration();
                if (mTotalDuration != curDuration) {
                    mTotalDuration = curDuration;
                    mVideoFragment.updateCurPlayTime(0);
                }
                /*
                * 添加主题默认片头
                * Add theme default title
                * */
                setThemeCaptionTitle();
                /*
                * 删除字幕，然后重新添加，防止带片头主题删掉字幕
                * Delete subtitles and add them again to prevent subtitles from deleting titles
                * */
                ArrayList<CaptionInfo> captionArray = TimelineData.instance().getCaptionData();
                TimelineUtil.setCaption(mTimeline, captionArray);
                /*
                 * 播放视频
                 * Play video
                 * */
                mVideoFragment.playVideoButtonCilck();
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

    private void initThemeDataList() {
        mThemeDataList.clear();

        FilterItem filterItem = new FilterItem();
        filterItem.setFilterMode(FilterItem.FILTERMODE_BUILTIN);

        if (isZh(this)){
            filterItem.setFilterName("无");
        }else {
            filterItem.setFilterName("");
        }
        //filterItem.setFilterName("无");
        filterItem.setImageId(R.mipmap.no);
        mThemeDataList.add(filterItem);

        ArrayList<NvAsset> themeList = getLocalData();
        if (isZh(this)){
            String bundlePath = "theme/info_Zh.txt";
            Util.getBundleFilterInfo(this, themeList, bundlePath);
        }else {
            String bundlePath = "theme/info.txt";
            Util.getBundleFilterInfo(this, themeList, bundlePath);
        }
        int ratio = TimelineData.instance().getMakeRatio();
        for (NvAsset asset : themeList) {
            if ((ratio & asset.aspectRatio) == 0)
                continue;

            FilterItem newFilterItem = new FilterItem();
            if (asset.isReserved()) {
                /*
                * 加载assets/theme文件夹下的图片
                * Load images in the assets / theme folder
                * */
                String coverPath = "file:///android_asset/theme/";
                coverPath += asset.uuid;
                coverPath += ".png";
                asset.coverUrl = coverPath;
            }
            newFilterItem.setFilterMode(FilterItem.FILTERMODE_PACKAGE);
            newFilterItem.setFilterName(asset.name);
            newFilterItem.setPackageId(asset.uuid);
            newFilterItem.setImageUrl(asset.coverUrl);
            mThemeDataList.add(newFilterItem);
        }
    }

    private int getSelectedPos() {
        if (mThemeDataList == null || mThemeDataList.size() == 0)
            return -1;

        if (TextUtils.isEmpty(mThemeId))
            return 0;

        for (int i = 0; i < mThemeDataList.size(); i++) {
            FilterItem assetItem = mThemeDataList.get(i);
            if (assetItem == null)
                continue;
            if (assetItem.getPackageId() != null && mThemeId.equals(assetItem.getPackageId())) {
                return i;
            }
        }
        return 0;
    }


    private void updateThemeCaptionAndDrawRect() {
        selectThemeCaption();
        mVideoFragment.setCurCaption(mThemeCaption);
        mVideoFragment.updateThemeCaptionCoordinate(mThemeCaption);
        mVideoFragment.changeThemeCaptionRectVisible();
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
        mDownloadMoreBtn.setClickable(true);
    }

    private void setThemeCaptionTitle() {
        boolean isHasThemeTitle = isHasThemeCaptionTitle();
        if (isHasThemeTitle) {
            String themeTitle = getResources().getString(R.string.theme_title);
            mTimeline.setThemeTitleCaptionText(themeTitle);
            TimelineUtil.applyTheme(mTimeline, mThemeId);
            TimelineData.instance().setThemeCptionTitle(themeTitle);
        }
    }

    private boolean isHasThemeCaptionTitle() {
        NvsTimelineCaption caption = mTimeline.getFirstCaption();
        while (caption != null) {
            if (caption.getCategory() == NvsTimelineCaption.THEME_CATEGORY
                    && caption.getRoleInTheme() == NvsTimelineCaption.ROLE_IN_THEME_TITLE) {//片头字幕
                return true;
            }
            caption = mTimeline.getNextCaption(caption);
        }
        return false;
    }

    private void selectThemeCaption() {
        long curPos = mStreamingContext.getTimelineCurrentPosition(mTimeline);
        List<NvsTimelineCaption> captionList = mTimeline.getCaptionsByTimelinePosition(curPos);
        int captionCount = captionList.size();
        if (captionCount > 0) {
            int index = -1;
            for (int i = 0; i < captionCount; i++) {
                NvsTimelineCaption caption = captionList.get(i);
                if (caption.getCategory() == NvsTimelineCaption.THEME_CATEGORY
                        && caption.getRoleInTheme() != NvsTimelineCaption.ROLE_IN_THEME_GENERAL) {//查找主题字幕
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                mThemeCaption = captionList.get(index);
            } else {
                mThemeCaption = null;
            }
        } else {
            mThemeCaption = null;
        }
    }
}
