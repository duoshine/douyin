package com.duoshine.douyin.meishe.sdkdemo.download;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.base.BaseConstants;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;

import java.io.File;
import java.util.ArrayList;

import static com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset.ASSET_ANIMATED_STICKER;
import static com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset.AspectRatio_16v9;
import static com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset.AspectRatio_NoFitRatio;
import static com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset.RatioArray;
import static com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset.RatioStringArray;


public class AssetDownloadListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<NvAsset> mAssetDataList = new ArrayList<>();
    private int[] TYPE_TEXT = new int[]{R.string.props_2d, R.string.props_3d, R.string.props_forword,
            R.string.props_back, R.string.props_eye, R.string.props_mouth, R.string.props_head,
            R.string.props_hand,
    };
    private int[] TYPE_TEXT_COLOR = new int[]{R.drawable.bg_left_top_red54, R.drawable.bg_left_top_blue63, R.drawable.bg_left_top_yellow9b,
            R.drawable.bg_left_top_blue8b, R.drawable.bg_left_top_green2b, R.drawable.bg_left_top_purple_c2, R.drawable.bg_left_top_red_ff,
            R.drawable.bg_left_top_green_af,
    };
    private Context mContext;
    //view type
    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOTER = 2;
    public static final int TYPE_PROPS = 3;//道具
    public static final int TYPE_FILTER = 4;//滤镜
    public static final int TYPE_THEME_CAPTURE = 5;//主题拍摄

    //
    /*
     * 数据加载有三种状态：LOADING--正在加载；LOADING_COMPLETE--加载完成;LOADING_END -- 加载结束
     * There are three states of data loading: LOADING means loading; LOADING_COMPLETE means loading is completed; LOADING_END means loading ends
     * */
    public static final int LOADING = 1;
    public static final int LOADING_COMPLETE = 2;
    public static final int LOADING_FAILED = 3;
    public static final int LOADING_END = 4;
    /*
     * 当前状态,默认加载完成
     * Current status, default loading is LOADING_COMPLETE
     * */
    private int currentLoadState = LOADING_COMPLETE;
    private OnDownloadClickListener mDownloadClickerListener = null;
    private int curTimelineRatio = AspectRatio_NoFitRatio;
    private int mAssetType = 0;

    public class DownloadButtonInfo {
        int buttonBackgroud;
        String buttonText;
        String buttonTextColor;

        public DownloadButtonInfo() {

        }
    }

    RequestOptions options;

    public AssetDownloadListAdapter(Context context) {
        mContext = context;
        options = new RequestOptions();
        options.centerCrop();
        options.placeholder(R.drawable.bank_thumbnail_local);
    }

    public void setAssetType(int assetType) {
        this.mAssetType = assetType;
    }

    public void setCurTimelineRatio(int curTimelineRatio) {
        this.curTimelineRatio = curTimelineRatio;
    }

    public void setAssetDatalist(ArrayList<NvAsset> assetDataList) {
        this.mAssetDataList = assetDataList;
        Log.e("Datalist", "DataCount = " + mAssetDataList.size());
    }

    public void setDownloadClickerListener(OnDownloadClickListener downloadClickerListener) {
        this.mDownloadClickerListener = downloadClickerListener;
    }

    public interface OnDownloadClickListener {
        /**
         * @param holder
         * @param pos
         */
        void onItemDownloadClick(RecyclerView.ViewHolder holder, int pos);
    }

    private int mExtraViewType = TYPE_ITEM;

    public void setExtraViewType(int type) {
        this.mExtraViewType = type;
    }

    public void updateItemProgress(String uuid, int progress) {
        if (mAssetDataList != null) {
            for (int index = 0; index < mAssetDataList.size(); ++index) {
                if (mAssetDataList.get(index).uuid == uuid) {
                    if (progress >= 0) {
                        mAssetDataList.get(index).downloadProgress = progress;
                    }
                    notifyItemChanged(index);
                    break;
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为FooterView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            //return TYPE_ITEM;
            return mExtraViewType;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*
         * 通过判断显示类型，来创建不同的View
         * Create different views by judging the display type
         * */
        RecyclerView.ViewHolder holder = null;
        View view;
        switch (viewType) {
            case TYPE_ITEM://旧的类型、
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_asset_download, parent, false);
                holder = new RecyclerViewHolder(view);
                break;
            case TYPE_FOOTER:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_asset_download_footer, parent, false);
                holder = new FootViewHolder(view);
                break;
            case TYPE_PROPS:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_asset_download_props, parent, false);
                holder = new PropsHolder(view);
                break;
            case TYPE_FILTER:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_asset_download_filter, parent, false);
                holder = new FilterHolder(view);
                break;
            case TYPE_THEME_CAPTURE:
                view = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_theme_shoot, parent, false);
                holder = new ThemeCaptureViewHolder(view);
            default:
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case TYPE_ITEM://旧的类型、
                dealOldItem(holder, position);
                break;
            case TYPE_FOOTER:
                dealFooterItem(holder);
                break;
            case TYPE_PROPS:
                dealPropsItem(holder, position);
                break;
            case TYPE_FILTER:
                dealFilterItem(holder, position);
                break;
            case TYPE_THEME_CAPTURE:
                dealThemeCaptureItem(holder, position);
                break;
            default:
                break;
        }
    }

    /**
     * 旧的item
     */
    private void dealOldItem(RecyclerView.ViewHolder holder, final int position) {
        if (mAssetDataList.size() > 0 && position < mAssetDataList.size()) {
            final NvAsset asset = mAssetDataList.get(position);
            final RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;

            Glide.with(mContext)
                    .asBitmap()
                    .load(asset.coverUrl)
                    .apply(options)
                    .into(recyclerViewHolder.mAssetCover);

            recyclerViewHolder.mAssetName.setText(asset.name);
            if (mAssetType == NvAsset.ASSET_ARSCENE_FACE) {
                if (asset.categoryId <= BaseConstants.PROP_IMAGES.length && asset.categoryId - 1 >= 0) {
                    recyclerViewHolder.assetCover_type_image.setVisibility(View.VISIBLE);
                    recyclerViewHolder.assetCover_type_image.setBackground(mContext.getResources().getDrawable(BaseConstants.PROP_IMAGES[asset.categoryId - 1]));
                } else {
                    recyclerViewHolder.assetCover_type_image.setVisibility(View.INVISIBLE);
                }
            } else {
                recyclerViewHolder.assetCover_type_image.setVisibility(View.INVISIBLE);
            }


            if (mAssetType == ASSET_ANIMATED_STICKER) {//mAssetType = 4是贴纸，贴纸无场景区分
                recyclerViewHolder.mAssetRatio.setText(R.string.asset_ratio);
            } else {
                recyclerViewHolder.mAssetRatio.setText(getAssetRatio(asset.aspectRatio));
            }

            recyclerViewHolder.mAssetSize.setText(getAssetSize(asset.remotePackageSize));
            DownloadButtonInfo buttonInfo = getDownloadButtonInfo(asset);
            recyclerViewHolder.mDownloadButton.setBackgroundResource(buttonInfo.buttonBackgroud);
            recyclerViewHolder.mDownloadButton.setText(buttonInfo.buttonText);
            recyclerViewHolder.mDownloadButton.setTextColor(Color.parseColor(buttonInfo.buttonTextColor));
            recyclerViewHolder.mDownloadProgressBar.setVisibility(View.GONE);
            recyclerViewHolder.mDownloadButton.setVisibility(View.VISIBLE);
            if (asset.downloadStatus == NvAsset.DownloadStatusFailed) {
                recyclerViewHolder.mDownloadButton.setText(R.string.retry);
                recyclerViewHolder.mDownloadButton.setTextColor(Color.parseColor("#ffffffff"));
                recyclerViewHolder.mDownloadButton.setBackgroundResource(R.drawable.download_button_shape_corner_retry);
                recyclerViewHolder.mDownloadProgressBar.setVisibility(View.GONE);
                recyclerViewHolder.mDownloadButton.setVisibility(View.VISIBLE);
            } else if (asset.downloadStatus == NvAsset.DownloadStatusFinished) {
                recyclerViewHolder.mDownloadProgressBar.setVisibility(View.GONE);
                recyclerViewHolder.mDownloadButton.setVisibility(View.VISIBLE);
            } else if (asset.downloadStatus == NvAsset.DownloadStatusInProgress) {
                recyclerViewHolder.mDownloadProgressBar.setVisibility(View.VISIBLE);
                recyclerViewHolder.mDownloadProgressBar.setProgress(asset.downloadProgress);
                recyclerViewHolder.mDownloadButton.setVisibility(View.GONE);
            }
            recyclerViewHolder.mDownloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (curTimelineRatio >= AspectRatio_16v9 && mAssetType != ASSET_ANIMATED_STICKER) {
                        if ((curTimelineRatio & asset.aspectRatio) == 0) {
                            /*
                             * 时间线比例不适配，禁止下载
                             * Timeline proportions do not fit, download is prohibited
                             * */
                            return;
                        }
                    }

                    if (asset.isUsable() && !asset.hasUpdate())
                        return;
                    if (asset.isUsable() && asset.hasRemoteAsset() && asset.hasUpdate()) {
                        File file = new File(asset.localDirPath);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    if (mDownloadClickerListener != null) {
                        mDownloadClickerListener.onItemDownloadClick(recyclerViewHolder, position);
                    }
                }
            });
        }
    }

    /**
     * footer
     *
     * @param holder
     */
    private void dealFooterItem(RecyclerView.ViewHolder holder) {
        FootViewHolder footViewHolder = (FootViewHolder) holder;
        switch (currentLoadState) {
            case LOADING:
                footViewHolder.mLoadLayout.setVisibility(View.VISIBLE);
                footViewHolder.mLoadFailTips.setVisibility(View.GONE);
                break;

            case LOADING_COMPLETE:
                footViewHolder.mLoadLayout.setVisibility(View.INVISIBLE);
                footViewHolder.mLoadFailTips.setVisibility(View.GONE);
                break;
            case LOADING_FAILED:
                footViewHolder.mLoadLayout.setVisibility(View.GONE);
                footViewHolder.mLoadFailTips.setVisibility(View.VISIBLE);
                break;
            case LOADING_END:
                footViewHolder.mLoadLayout.setVisibility(View.GONE);
                footViewHolder.mLoadFailTips.setVisibility(View.GONE);
            default:
                break;
        }
    }

    /**
     * 滤镜,目前仅仅拍摄页面用
     */
    private void dealFilterItem(RecyclerView.ViewHolder holder, final int position) {
        if (mAssetDataList.size() > 0 && position < mAssetDataList.size()) {
            final NvAsset asset = mAssetDataList.get(position);
            final FilterHolder filterHolder = (FilterHolder) holder;
            filterHolder.mTvName.setText(asset.name);
            Glide.with(mContext)
                    .asBitmap()
                    .load(asset.coverUrl)
                    .apply(options)
                    .into(filterHolder.mIvCover);
            filterHolder.mTvSize.setText(String.format(mContext.getString(R.string.down_load_size), getAssetSize(asset.remotePackageSize)));
            dealFilterUpdateDisplay(filterHolder, asset);
            if (filterHolder.mTvDownload.getVisibility() == View.VISIBLE) {
                if (!filterHolder.mTvDownload.isEnabled()) {
                    filterHolder.mTvDownload.setEnabled(true);
                }
                filterHolder.mTvDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (asset.isUsable() && !asset.hasUpdate())
                            return;
                        if (asset.isUsable() && asset.hasRemoteAsset() && asset.hasUpdate()) {
                            File file = new File(asset.localDirPath);
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                        if (mDownloadClickerListener != null) {
                            mDownloadClickerListener.onItemDownloadClick(filterHolder, position);
                        }
                    }
                });
            }
        }
    }

    /**
     * 主题拍摄
     */
    private void dealThemeCaptureItem(RecyclerView.ViewHolder holder, final int position) {
        if (mAssetDataList.size() > 0 && position < mAssetDataList.size()) {
            final NvAsset asset = mAssetDataList.get(position);
            final ThemeCaptureViewHolder themeCaptureViewHolder = (ThemeCaptureViewHolder) holder;
            themeCaptureViewHolder.mTvName.setText(asset.name);
            Glide.with(mContext)
                    .asBitmap()
                    .load(asset.coverUrl)
                    .apply(options)
                    .into(themeCaptureViewHolder.mIvCover);
            themeCaptureViewHolder.mItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDownloadClickerListener != null) {
                        mDownloadClickerListener.onItemDownloadClick(themeCaptureViewHolder, position);
                    }
                }
            });

        }
    }

    /**
     * 道具，目前仅仅拍摄页面用
     */
    private void dealPropsItem(RecyclerView.ViewHolder holder, final int position) {
        if (mAssetDataList.size() > 0 && position < mAssetDataList.size()) {
            final NvAsset asset = mAssetDataList.get(position);
            final PropsHolder propsHolder = (PropsHolder) holder;
            if (asset.categoryId <= BaseConstants.PROP_IMAGES.length && asset.categoryId - 1 >= 0) {
                propsHolder.mTvType.setText(TYPE_TEXT[asset.categoryId - 1]);
                propsHolder.mTvType.setBackgroundResource(TYPE_TEXT_COLOR[asset.categoryId - 1]);
            } else {
                propsHolder.mTvType.setText("");
            }
            propsHolder.mTvName.setText(asset.name);
            Glide.with(mContext)
                    .asBitmap()
                    .load(asset.coverUrl)
                    .apply(options)
                    .into(propsHolder.mIvCover);
            propsHolder.mTvSize.setText(String.format(mContext.getString(R.string.down_load_size), getAssetSize(asset.remotePackageSize)));
            dealFilterUpdateDisplay(propsHolder, asset);
            if (propsHolder.mTvDownload.getVisibility() == View.VISIBLE) {
                if (!propsHolder.mTvDownload.isEnabled()) {
                    propsHolder.mTvDownload.setEnabled(true);
                }

            }
        }
    }

    @Override
    public int getItemCount() {
        return mAssetDataList.size() + 1;
    }

    private String getAssetRatio(int aspectRatio) {
        String assetStrRatio = "";
        int length = RatioArray.length;
        for (int index = 0; index < length; ++index) {
            if ((aspectRatio & RatioArray[index]) != 0) {
                if (index == length - 1) {//通用类型
                    /*
                     * 通用类型
                     * General type
                     * */
                    if (aspectRatio >= RatioArray[index])
                        assetStrRatio = mContext.getResources().getString(R.string.asset_ratio);//RatioStringArray[index]
                } else {
                    /*
                     * 满足几种Ratio的素材
                     * Meets several Ratio materials
                     * */
                    assetStrRatio += RatioStringArray[index];
                    assetStrRatio += " ";
                }
            }
        }
        return assetStrRatio;
    }

    private String getAssetSize(int assetSize) {
        int totalKbSize = assetSize / 1024;
        int mbSize = totalKbSize / 1024;
        int kbSize = totalKbSize % 1024;
        float tempSize = (float) (kbSize / 1024.0);
        String packageAssetSize;
        if (mbSize > 0) {
            tempSize = (mbSize + tempSize);
            packageAssetSize = String.format("%.1f", tempSize);
            packageAssetSize = packageAssetSize + "MB";
        } else {
            packageAssetSize = String.format("%d", kbSize);
            packageAssetSize = packageAssetSize + "KB";
        }
        return packageAssetSize;
    }

    private DownloadButtonInfo getDownloadButtonInfo(NvAsset asset) {
        DownloadButtonInfo buttonInfo = new DownloadButtonInfo();
        if (curTimelineRatio >= AspectRatio_16v9
                && mAssetType != ASSET_ANIMATED_STICKER
                && (curTimelineRatio & asset.aspectRatio) == 0) {
            buttonInfo.buttonBackgroud = R.drawable.download_button_shape_corner_finished;
            buttonInfo.buttonText = mContext.getResources().getString(R.string.asset_mismatch);
            buttonInfo.buttonTextColor = "#ff928c8c";
        } else if (!asset.isUsable() && asset.hasRemoteAsset()) {
            buttonInfo.buttonBackgroud = R.drawable.download_button_shape_corner_download;
            buttonInfo.buttonText = mContext.getResources().getString(R.string.asset_download);
            buttonInfo.buttonTextColor = "#ffffffff";
        } else if (asset.isUsable() && !asset.hasUpdate()) {
            buttonInfo.buttonBackgroud = R.drawable.download_button_shape_corner_finished;
            buttonInfo.buttonText = mContext.getResources().getString(R.string.asset_downloadfinished);
            buttonInfo.buttonTextColor = "#ff909293";
        } else if (asset.isUsable() && asset.hasRemoteAsset() && asset.hasUpdate()) {
            buttonInfo.buttonBackgroud = R.drawable.download_button_shape_corner_update;
            buttonInfo.buttonText = mContext.getResources().getString(R.string.asset_update);
            buttonInfo.buttonTextColor = "#ffffffff";
        }
        return buttonInfo;
    }

    private void dealFilterUpdateDisplay(FilterHolder holder, NvAsset asset) {
        if (curTimelineRatio >= AspectRatio_16v9 && (curTimelineRatio & asset.aspectRatio) == 0) {
            //不适配
            holder.mTvDownload.setEnabled(false);
            holder.mTvDownload.setBackgroundColor(mContext.getResources().getColor(R.color.gray_8288));
            holder.mRlErrorParent.setVisibility(View.VISIBLE);
            holder.mTvTip.setText(R.string.down_load_incompatibility);
            holder.mTvTip.setTextColor(mContext.getResources().getColor(R.color.white));
            return;
        } else {
            if (holder.mRlErrorParent.getVisibility() == View.VISIBLE) {
                holder.mRlErrorParent.setVisibility(View.GONE);
            }
        }
        if (!asset.isUsable() && asset.hasRemoteAsset()) {
            if (holder.mPbDownload.getVisibility() != View.VISIBLE) {
                holder.mPbDownload.setVisibility(View.VISIBLE);
            }
            if (asset.downloadStatus == NvAsset.DownloadStatusInProgress) {
                //下载中
                holder.mTvDownload.setText(R.string.asset_downloading);
                holder.mTvDownload.setBackgroundResource(0);
                //  holder.mTvDownload.setBackgroundResource(R.drawable.bg_bottom_blue63);
                holder.mPbDownload.setProgress(asset.downloadProgress);
                if (holder.mTvDownload.isEnabled()) {
                    holder.mTvDownload.setEnabled(false);
                }
            } else {
                //下载
                holder.mTvDownload.setText(R.string.asset_download);
                holder.mTvDownload.setBackgroundResource(0);
                holder.mPbDownload.setProgress(100);
                if (!holder.mTvDownload.isEnabled()) {
                    holder.mTvDownload.setEnabled(true);
                }
                // holder.mTvDownload.setBackgroundResource(R.drawable.bg_bottom_blue63);
            }
        } else if (asset.isUsable() && !asset.hasUpdate()) {
            if (holder.mPbDownload.getVisibility() == View.VISIBLE) {
                holder.mPbDownload.setVisibility(View.GONE);
            }
            //已下载
            holder.mTvDownload.setText(R.string.asset_downloadfinished);
            holder.mTvDownload.setBackgroundResource(R.drawable.bg_bottom_red64);
            if (holder.mTvDownload.isEnabled()) {
                holder.mTvDownload.setEnabled(false);
            }
        } else if (asset.isUsable() && asset.hasRemoteAsset() && asset.hasUpdate()) {
            if (holder.mPbDownload.getVisibility() != View.VISIBLE) {
                holder.mPbDownload.setVisibility(View.VISIBLE);
            }
            if (asset.downloadStatus == NvAsset.DownloadStatusInProgress) {
                //更新中
                holder.mTvDownload.setText(R.string.asset_updating);
                //   holder.mTvDownload.setBackgroundResource(R.drawable.bg_bottom_green13);
                holder.mTvDownload.setBackgroundResource(0);
                holder.mPbDownload.setProgress(asset.downloadProgress);
                if (holder.mTvDownload.isEnabled()) {
                    holder.mTvDownload.setEnabled(false);
                }
            } else {
                //更新
                holder.mTvDownload.setText(R.string.asset_update);
                //holder.mTvDownload.setBackgroundResource(R.drawable.bg_bottom_green13);
                holder.mTvDownload.setBackgroundResource(0);
                holder.mPbDownload.setProgress(100);
                if (!holder.mTvDownload.isEnabled()) {
                    holder.mTvDownload.setEnabled(true);
                }
            }

        }
        if (asset.downloadStatus == NvAsset.DownloadStatusFailed) {
            holder.mRlErrorParent.setVisibility(View.VISIBLE);
            holder.mTvTip.setText(R.string.download_failed);
            holder.mTvTip.setTextColor(mContext.getResources().getColor(R.color.ms_red));
            holder.mPbDownload.setVisibility(View.INVISIBLE);
            if (!holder.mTvDownload.isEnabled()) {
                holder.mTvDownload.setEnabled(true);
            }
        } else if (asset.downloadStatus == NvAsset.DownloadStatusFinished) {
            holder.mTvDownload.setText(R.string.asset_downloadfinished);
            holder.mTvDownload.setBackgroundResource(R.drawable.bg_bottom_red64);
            holder.mPbDownload.setVisibility(View.GONE);
            if (holder.mTvDownload.isEnabled()) {
                holder.mTvDownload.setEnabled(false);
            }
        }

    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView mAssetCover;
        ImageView assetCover_type_image;
        TextView mAssetName;
        TextView mAssetRatio;
        TextView mAssetSize;
        Button mDownloadButton;
        DownloadProgressBar mDownloadProgressBar;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            mAssetCover = (ImageView) itemView.findViewById(R.id.assetCover);
            assetCover_type_image = (ImageView) itemView.findViewById(R.id.assetCover_type_image);
            mAssetName = (TextView) itemView.findViewById(R.id.assetName);
            mAssetRatio = (TextView) itemView.findViewById(R.id.assetRatio);
            mAssetSize = (TextView) itemView.findViewById(R.id.assetSize);
            mDownloadButton = (Button) itemView.findViewById(R.id.download_button);
            mDownloadProgressBar = (DownloadProgressBar) itemView.findViewById(R.id.downloadProgressBar);
        }
    }

    public class PropsHolder extends FilterHolder {
        TextView mTvType;


        PropsHolder(View itemView) {
            super(itemView);
            mTvType = itemView.findViewById(R.id.tv_type);
        }
    }

    public class FilterHolder extends RecyclerView.ViewHolder {
        RelativeLayout mRlErrorParent;
        ImageView mIvCover;
        TextView mTvTip;
        TextView mTvName;
        TextView mTvSize;
        TextView mTvDownload;
        ProgressBar mPbDownload;


        FilterHolder(View itemView) {
            super(itemView);
            mRlErrorParent = itemView.findViewById(R.id.rl_tip_parent);
            mIvCover = itemView.findViewById(R.id.iv_cover);
            mTvTip = itemView.findViewById(R.id.tv_tip);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvSize = itemView.findViewById(R.id.tv_size);
            mTvDownload = itemView.findViewById(R.id.tv_download);
            mPbDownload = itemView.findViewById(R.id.pb_download_bar);
            mTvDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NvAsset asset = mAssetDataList.get(getAdapterPosition());
                    if (asset.isUsable() && !asset.hasUpdate())
                        return;
                    if (asset.isUsable() && asset.hasRemoteAsset() && asset.hasUpdate()) {
                        File file = new File(asset.localDirPath);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    if (mDownloadClickerListener != null) {
                        mDownloadClickerListener.onItemDownloadClick(FilterHolder.this, getAdapterPosition());
                    }
                }
            });
        }
    }

    public class ThemeCaptureViewHolder extends RecyclerView.ViewHolder {

        View mItemLayout;
        ImageView mIvCover;
        TextView mTvName;

        public ThemeCaptureViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemLayout = itemView.findViewById(R.id.theme_item_layout);
            mIvCover = itemView.findViewById(R.id.iv_cover);
            mTvName = itemView.findViewById(R.id.tv_name);
        }
    }

    public class FootViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mLoadLayout;
        FrameLayout mLoadFailTips;

        FootViewHolder(View itemView) {
            super(itemView);
            mLoadLayout = (LinearLayout) itemView.findViewById(R.id.loadLayout);
            mLoadFailTips = (FrameLayout) itemView.findViewById(R.id.loadFailTips);
        }
    }

    /**
     * 设置上拉加载状态
     * Set pull-up loading status
     *
     * @param loadState
     */
    public void setLoadState(int loadState) {
        this.currentLoadState = loadState;
        notifyDataSetChanged();
    }

    public void updateDownloadItems() {
        notifyDataSetChanged();
    }
}
