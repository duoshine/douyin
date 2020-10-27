package com.duoshine.douyin.meishe.sdkdemo.edit.compoundcaption;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.AssetItem;
import com.duoshine.douyin.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.RoundImageView;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;

import java.util.ArrayList;

/**
 * Created by admin on 2018/5/25.
 */

public class CompoundCaptionAdaper extends RecyclerView.Adapter<CompoundCaptionAdaper.ViewHolder> {
    private ArrayList<AssetItem> mAssetList = new ArrayList<>( );
    private Context mContext;
    private OnItemClickListener mOnItemClickListener = null;
    private int mSelectedPos = -1;

    public CompoundCaptionAdaper(Context context) {
        mContext = context;
    }

    public void setAssetList(ArrayList<AssetItem> assetArrayList) {
        this.mAssetList = assetArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_asset_compound_catpion_style, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void setSelectedPos(int selectedPos) {
        this.mSelectedPos = selectedPos;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AssetItem assetItem = mAssetList.get(position);
        if (assetItem == null)
            return;
        NvAsset asset = assetItem.getAsset( );
        if (asset == null)
            return;

        RequestOptions options = new RequestOptions( );
        options.centerCrop( );
        options.placeholder(R.mipmap.default_caption);
        Glide.with(mContext)
                .asBitmap( )
                .load(asset.coverUrl)
                .apply(options)
                .into(holder.mCaptionAssetCover);

        holder.mSelecteItem.setVisibility(mSelectedPos == position ? View.VISIBLE : View.GONE);

        holder.mCaptionStyleText.setTextColor(mSelectedPos == position ? Color.parseColor("#58A8EE") : Color.parseColor("#CCFFFFFF"));
        holder.mCaptionStyleText.setText(asset.name);

        holder.itemView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, position);
                }
                if (mSelectedPos == position) {
                    return;
                }
                notifyItemChanged(mSelectedPos);
                mSelectedPos = position;
                notifyItemChanged(mSelectedPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAssetList.size( );
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RoundImageView mCaptionAssetCover;
        View mSelecteItem;
        TextView mCaptionStyleText;

        public ViewHolder(View itemView) {
            super(itemView);
            mCaptionAssetCover = (RoundImageView) itemView.findViewById(R.id.captionStyleAssetCover);
            mSelecteItem = itemView.findViewById(R.id.selectedItem);
            mCaptionStyleText = itemView.findViewById(R.id.captionStyleText);
        }
    }
}
