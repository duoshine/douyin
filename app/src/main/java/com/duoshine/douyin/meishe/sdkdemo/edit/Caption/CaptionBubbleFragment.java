package com.duoshine.douyin.meishe.sdkdemo.edit.Caption;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.edit.adapter.SpaceItemDecoration;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.AssetItem;
import com.duoshine.douyin.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;

import java.util.ArrayList;

public class CaptionBubbleFragment extends Fragment {
    private RecyclerView mRvBubbleList;
    private BubbleAdapter mBubbleAdapter;
    private ArrayList<AssetItem> mAssetList;
    private ImageView mIvLoadMore;
    private TextView mTvLoadMore;
    private OnCaptionStateListener mCaptionStateListener;

    public interface OnCaptionStateListener {
        void onFragmentLoadFinished();

        void onLoadMore();

        void onItemClick(int pos);
    }

    public void setCaptionStateListener(OnCaptionStateListener stateListener) {
        this.mCaptionStateListener = stateListener;
    }

    public void setSelectedPos(int selectedPos) {
        if (mBubbleAdapter != null)
            mBubbleAdapter.setSelectedPos(selectedPos);
    }

    public void setAssetInfoList(ArrayList<AssetItem> assetItems) {
        mAssetList = assetItems;
        if (mBubbleAdapter != null) {
            mBubbleAdapter.setAssetList(assetItems);
            mBubbleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.caption_rich_word_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        if (mCaptionStateListener != null) {
            mCaptionStateListener.onFragmentLoadFinished();
        }
    }

    private void initView(View view) {
        mIvLoadMore = view.findViewById(R.id.iv_load_more);
        mTvLoadMore = view.findViewById(R.id.tv_load_more);
        mRvBubbleList = view.findViewById(R.id.rv_list);

        mIvLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCaptionStateListener != null) {
                    mCaptionStateListener.onLoadMore();
                }
            }
        });
        mTvLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCaptionStateListener != null) {
                    mCaptionStateListener.onLoadMore();
                }
            }
        });
    }

    private void initData() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRvBubbleList.setLayoutManager(layoutManager);
        mBubbleAdapter = new BubbleAdapter(getActivity());
        mBubbleAdapter.setAssetList(mAssetList);
        mRvBubbleList.setAdapter(mBubbleAdapter);
        mRvBubbleList.addItemDecoration(new SpaceItemDecoration(0, 15));
        mBubbleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (mCaptionStateListener != null) {
                    mCaptionStateListener.onItemClick(pos);
                }
            }
        });
    }

    static class BubbleAdapter extends RecyclerView.Adapter<BubbleAdapter.ViewHolder> {
        private ArrayList<AssetItem> mAssetList = new ArrayList<>();
        private Context mContext;
        private OnItemClickListener mOnItemClickListener = null;
        private int mSelectedPos = 0;

        BubbleAdapter(Context context) {
            mContext = context;
        }

        void setAssetList(ArrayList<AssetItem> assetArrayList) {
            this.mAssetList = assetArrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_simple_caption, parent, false);
            return new ViewHolder(v);
        }

        public void setSelectedPos(int selectedPos) {
            if (selectedPos >= 0 && mAssetList != null && selectedPos < mAssetList.size()) {
                notifyItemChanged(mSelectedPos);
                this.mSelectedPos = selectedPos;
                notifyItemChanged(mSelectedPos);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            AssetItem assetItem = mAssetList.get(position);
            if (assetItem == null)
                return;
            NvAsset asset = assetItem.getAsset();
            if (asset == null)
                return;
            if (assetItem.getAssetMode() == AssetItem.ASSET_NONE) {
                holder.mIvCover.setImageResource(assetItem.getImageRes());
            } else {
                RequestOptions options = new RequestOptions();
                options.centerCrop();
                options.placeholder(R.mipmap.default_caption);
                Glide.with(mContext)
                        .asBitmap()
                        .load(asset.coverUrl)
                        .apply(options)
                        .into(holder.mIvCover);
            }

            holder.mTvName.setText(asset.name);
            holder.vSelected.setVisibility(mSelectedPos == position ? View.VISIBLE : View.GONE);
            holder.mTvName.setTextColor(mSelectedPos == position ? mContext.getResources().getColor(R.color.red_ff64)
                    : mContext.getResources().getColor(R.color.white));
        }

        @Override
        public int getItemCount() {
            return mAssetList == null ? 0 : mAssetList.size();
        }

        public void setOnItemClickListener(OnItemClickListener itemClickListener) {
            this.mOnItemClickListener = itemClickListener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView mIvCover;
            View vSelected;
            TextView mTvName;

            ViewHolder(View itemView) {
                super(itemView);
                mIvCover = itemView.findViewById(R.id.iv_cover);
                vSelected = itemView.findViewById(R.id.v_select);
                mTvName = itemView.findViewById(R.id.tv_name);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickListener != null)
                            mOnItemClickListener.onItemClick(v, getAdapterPosition());
                        if (mSelectedPos == getAdapterPosition())
                            return;
                        notifyItemChanged(mSelectedPos);
                        mSelectedPos = getAdapterPosition();
                        notifyItemChanged(mSelectedPos);
                    }
                });
            }
        }
    }
}
