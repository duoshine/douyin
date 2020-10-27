package com.duoshine.douyin.meishe.sdkdemo.download;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAssetManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.duoshine.douyin.meishe.sdkdemo.download.AssetDownloadListAdapter.TYPE_FILTER;
import static com.duoshine.douyin.meishe.sdkdemo.download.AssetDownloadListAdapter.TYPE_PROPS;


/**
 * Created by czl on 2018/6/25.
 * 素材下载列表Fragment
 * Download List Fragment
 */
public class AssetListFragment extends Fragment implements NvAssetManager.NvAssetManagerListener {
    private static final String TAG = "AssetListFragment";
    private static final int GETASSETLISTSUCCESS = 200;
    private static final int GETASSETLISTFAIL = 201;
    private static final int DOWNLOADASSETINPROGRESS = 202;
    private static final int mPageSize = 20;
    private int mCurrentRequestPage = 0;
    private LinearLayout mPreLoadingLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mAssetRrecyclerViewList;
    private LinearLayout mLoadFailedLayout;
    private Button mReloadAsset;
    private AssetDownloadListAdapter mAssetListAdapter;
    private NvAssetManager mAssetManager;
    private ArrayList<NvAsset> mAssetDataList = new ArrayList<>();
    private int mAssetType = 0;
    private int mCategoryId = 0;
    /*
     * 首次请求标识
     * First request identification
     * */
    private boolean mIsFirstRequest = true;
    /*
     * 刷新状态
     * Refresh status flag
     * */
    private boolean mIsRefreshFlag = false;
    /*
     * 加载状态
     * Loading state
     * */
    private boolean mIsLoadingMoreFlag = false;
    private boolean mHasNext = true;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Handler m_handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case GETASSETLISTSUCCESS:
                    if (mIsFirstRequest) {
                        mIsFirstRequest = false;
                        mPreLoadingLayout.setVisibility(View.GONE);
                        mLoadFailedLayout.setVisibility(View.GONE);
                    }
                    //refresh
                    closeRefresh();
                    if (mAssetDataList != null && mAssetDataList.size() > 0) {
                        mAssetListAdapter.setAssetDatalist(mAssetDataList);
                        if (mHasNext) {
                            mAssetListAdapter.setLoadState(AssetDownloadListAdapter.LOADING_COMPLETE);
                        } else {
                            mAssetListAdapter.setLoadState(AssetDownloadListAdapter.LOADING_END);
                        }
                    }
                    break;
                case GETASSETLISTFAIL:
                    if (mIsFirstRequest) {
                        mPreLoadingLayout.setVisibility(View.GONE);
                        mSwipeRefreshLayout.setVisibility(View.GONE);
                        mLoadFailedLayout.setVisibility(View.VISIBLE);
                    }
                    closeRefresh();
                    mAssetListAdapter.setLoadState(AssetDownloadListAdapter.LOADING_FAILED);
                    break;
                case DOWNLOADASSETINPROGRESS:
                    mAssetListAdapter.updateDownloadItems();
                    break;
            }
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View rootParent = inflater.inflate(R.layout.asset_download_list_fragment, container, false);
        mPreLoadingLayout = (LinearLayout) rootParent.findViewById(R.id.preloadingLayout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootParent.findViewById(R.id.swipe_refresh_layout);
        mAssetRrecyclerViewList = (RecyclerView) rootParent.findViewById(R.id.asset_recyclerviewList);
        mLoadFailedLayout = (LinearLayout) rootParent.findViewById(R.id.loadFailedLayout);
        mReloadAsset = (Button) rootParent.findViewById(R.id.reloadAsset);
        mAssetManager = NvAssetManager.sharedInstance();
        mAssetManager.setManagerlistener(this);

        return rootParent;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
        initData();

        //startProgressTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
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
                NvAssetManager.sharedInstance().setAssetInfoToSharedPreferences(mAssetType);
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        //stopProgressTimer();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged: " + hidden);
    }

    /*
     * 素材请求与下载
     * Material Request and Download
     * */
    @Override
    public void onRemoteAssetsChanged(boolean hasNext) {
        mHasNext = hasNext;
        Log.e(TAG, "mHasNext = " + mHasNext);
        if (!mIsLoadingMoreFlag && !mIsRefreshFlag) {
            //refresh
            ArrayList<NvAsset> arrayList = mAssetManager.getRemoteAssetsWithPage(mAssetType, NvAsset.AspectRatio_All, 0, mCurrentRequestPage, mPageSize);
            if (arrayList.size() > 0) {
                mAssetDataList = arrayList;
            }

        } else if (!mIsLoadingMoreFlag && mIsRefreshFlag) {
            //refresh
            mIsRefreshFlag = false;
            ArrayList<NvAsset> arrayList = mAssetManager.getRemoteAssetsWithPage(mAssetType, NvAsset.AspectRatio_All, 0, mCurrentRequestPage, mPageSize);
            if (arrayList.size() > 0) {
                mAssetDataList = arrayList;
            }
        } else if (!mIsRefreshFlag && mIsLoadingMoreFlag) {
            mIsLoadingMoreFlag = false;
            ArrayList<NvAsset> assetDataListPerPage = mAssetManager.getRemoteAssetsWithPage(mAssetType, NvAsset.AspectRatio_All, 0, mCurrentRequestPage, mPageSize);
            mAssetDataList.addAll(assetDataListPerPage);
        }

        //next page
        ++mCurrentRequestPage;
        Message msg = m_handler.obtainMessage();
        if (msg == null)
            msg = new Message();
        msg.what = GETASSETLISTSUCCESS;
        m_handler.sendMessage(msg);
    }

    @Override
    public void onGetRemoteAssetsFailed() {
        Message msg = m_handler.obtainMessage();
        if (msg == null)
            msg = new Message();
        msg.what = GETASSETLISTFAIL;
        m_handler.sendMessage(msg);
    }

    @Override
    public void onDownloadAssetProgress(final String uuid, final int progress) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAssetListAdapter.updateItemProgress(uuid, progress);
                }
            });
        }
    }

    @Override
    public void onDonwloadAssetFailed(String uuid) {
        for (int index = 0; index < mAssetDataList.size(); ++index) {
            if (mAssetDataList.get(index).uuid.compareTo(uuid) == 0) {
                break;
            }
        }
    }

    @Override
    public void onDonwloadAssetSuccess(final String uuid) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAssetListAdapter.updateItemProgress(uuid, -1);
                }
            });
        }
    }

    @Override
    public void onFinishAssetPackageInstallation(String uuid) {

    }

    @Override
    public void onFinishAssetPackageUpgrading(String uuid) {

    }

    private String mComeFrom;

    private void initData() {
        Bundle bundle = getArguments();
        int curRatio = NvAsset.AspectRatio_16v9;
        if (bundle != null) {
            mAssetType = bundle.getInt("assetType");
            mCategoryId = bundle.getInt("categoryId", 0);
            curRatio = bundle.getInt("ratio", NvAsset.AspectRatio_16v9);
            mComeFrom = bundle.getString("from", "");
        }
        mIsFirstRequest = true;
        /*
         * 设置刷新控件颜色
         * Set refresh control color
         * */
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor("#4DB6AC"));
        mAssetListAdapter = new AssetDownloadListAdapter(getActivity());
        mAssetListAdapter.setCurTimelineRatio(curRatio);
        mAssetListAdapter.setAssetType(mAssetType);
        if (!TextUtils.isEmpty(mComeFrom)) {
            if ("capture_props".equals(mComeFrom)) {
                mAssetListAdapter.setExtraViewType(TYPE_PROPS);
            } else if ("capture_filter".equals(mComeFrom)) {
                mAssetListAdapter.setExtraViewType(TYPE_FILTER);
            }
            mAssetRrecyclerViewList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            mAssetRrecyclerViewList.addItemDecoration(new SquareDecoration());
            mAssetRrecyclerViewList.setBackgroundColor(getActivity().getResources().getColor(R.color.white_20));
        } else {
            mAssetRrecyclerViewList.setLayoutManager(new LinearLayoutManager(getActivity()));
            mAssetRrecyclerViewList.addItemDecoration(new AssetListDecoration(getActivity(), AssetListDecoration.VERTICAL_LIST));
        }

        mAssetListAdapter.setAssetDatalist(mAssetDataList);
        mAssetRrecyclerViewList.setAdapter(mAssetListAdapter);
        /*
         * 下拉刷新
         * Pull down to refresh
         * */
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mIsRefreshFlag = true;
                mCurrentRequestPage = 0;
                assetDataRequest();
            }
        });

        /*
         *  加载更多监听
         * Add listeners for loading more
         * */
        mAssetRrecyclerViewList.addOnScrollListener(new AssetListOnScrollListener() {
            @Override
            public void onLoadMore() {
                Log.e(TAG, "mHasNext = " + mHasNext);
                if (mHasNext) {
                    mIsLoadingMoreFlag = true;
                    mAssetListAdapter.setLoadState(AssetDownloadListAdapter.LOADING);
                    assetDataRequest();
                } else {
                    /*
                     * 显示加载到底的提示
                     * Show loading tips
                     * */
                    mAssetListAdapter.setLoadState(AssetDownloadListAdapter.LOADING_END);
                }
            }
        });

        mAssetListAdapter.setDownloadClickerListener(new AssetDownloadListAdapter.OnDownloadClickListener() {
            @Override
            public void onItemDownloadClick(RecyclerView.ViewHolder holder, int pos) {
                int size = mAssetDataList.size();
                if (pos >= size)
                    return;
                if (size > 0) {
                    mAssetManager.downloadAsset(mAssetType, mAssetDataList.get(pos).uuid);
                }
            }
        });

        mReloadAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadFailedLayout.setVisibility(View.GONE);
                mPreLoadingLayout.setVisibility(View.VISIBLE);
                assetDataRequest();
            }
        });
        mAssetManager.searchLocalAssets(mAssetType);
        assetDataRequest();
    }

    /*
     * Network request
     * 网络请求
     * */
    private void assetDataRequest() {
        mAssetManager.downloadRemoteAssetsInfo(mAssetType, NvAsset.AspectRatio_All, mCategoryId, mCurrentRequestPage, mPageSize);
    }

    private void closeRefresh() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void startProgressTimer() {
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = DOWNLOADASSETINPROGRESS;
                m_handler.sendMessage(msg);
            }
        };
        mTimer.schedule(mTimerTask, 0, 300);
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
}
