package com.duoshine.douyin.meishe.sdkdemo.edit.Caption;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.CaptionColorInfo;
import com.duoshine.douyin.meishe.sdkdemo.edit.interfaces.OnItemClickListener;
import com.duoshine.douyin.meishe.sdkdemo.edit.view.RoundColorView;

import java.util.ArrayList;

/**
 * Created by admin on 2018/5/25.
 */

public class CaptionBackgroundRecyclerAdaper extends RecyclerView.Adapter<CaptionBackgroundRecyclerAdaper.ViewHolder>  {
    private ArrayList<CaptionColorInfo> captionBackgroundColorList = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mOnItemClickListener = null;
    public CaptionBackgroundRecyclerAdaper(Context context) {
        mContext = context;
    }
    public void setCaptionBackgroundColorList(ArrayList<CaptionColorInfo> captionBackgroundColorList) {
        this.captionBackgroundColorList = captionBackgroundColorList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_asset_caption_outline, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CaptionBackgroundRecyclerAdaper.ViewHolder holder, final int position) {
        final CaptionColorInfo colorInfo = captionBackgroundColorList.get(position);
        if(0 == position){
            holder.mCaptionOutlineNoColor.setVisibility(View.VISIBLE);
            holder.mCaptionOutlineColor.setVisibility(View.GONE);
        }else {
            holder.mCaptionOutlineNoColor.setVisibility(View.GONE);
            holder.mCaptionOutlineColor.setVisibility(View.VISIBLE);
            holder.mCaptionOutlineColor.setColor(Color.parseColor(colorInfo.mColorValue));
        }
        if(colorInfo.mSelected){
            holder.mSelecteItem.setVisibility(View.VISIBLE);
        }else {
            holder.mSelecteItem.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null)
                    mOnItemClickListener.onItemClick(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return captionBackgroundColorList.size();
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mCaptionOutlineNoColor;
        RoundColorView mCaptionOutlineColor;
        View mSelecteItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mCaptionOutlineNoColor = (ImageView)itemView.findViewById(R.id.captionOutlineNoColor);
            mCaptionOutlineColor = (RoundColorView)itemView.findViewById(R.id.captionOutlineColor);
            mSelecteItem = itemView.findViewById(R.id.selectedItem);
        }
    }
}
