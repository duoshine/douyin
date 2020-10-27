package com.duoshine.douyin.meishe.sdkdemo.capture;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.BeautyData;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;


public class MakeupAdapter extends RecyclerView.Adapter<MakeupAdapter.ViewHolder> {

    public static final int MAKE_UP_RANDOM_BG_TYPE = 101;
    public static final int MAKE_UP_WHITE_BG_TYPE = 102;
    public static final int MAKE_UP_ROUND_ICON_TYPE = 103;
    private List<BeautyData> mDataList;
    private int mSelectedPos = Integer.MAX_VALUE;
    private Context mContext;
    private OnItemClickListener mClickListener;
    private boolean mIsEnable = true;
    private boolean mIsFirstLoad = true;
    private int mViewType = MAKE_UP_RANDOM_BG_TYPE;
    RequestOptions mOptions = RequestOptions.bitmapTransform(new CircleCrop());
    public MakeupAdapter(Context context, ArrayList dataList) {
        mContext = context;
        mDataList = dataList;
    }

    public void setDataList(List<BeautyData> data, int viewType) {
        this.mDataList = data;
        this.mViewType = viewType;
    }

    public List<BeautyData> getDataList() {
        return mDataList;
    }

    public void setEnable(boolean enable) {
        mIsEnable = enable;
        notifyDataSetChanged( );
    }

    public void setSelectPos(int pos) {
        mSelectedPos = pos;
        notifyDataSetChanged( );
    }

    public int getSelectPos() {
        return mSelectedPos;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public BeautyData getSelectItem() {
        if (mDataList != null && mSelectedPos >= 0 && mSelectedPos < mDataList.size( )) {
            return mDataList.get(mSelectedPos);
        }
        return null;
    }

    public void updateDataList(ArrayList dataList) {
        mDataList.clear( );
        mDataList.addAll(dataList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == MAKE_UP_ROUND_ICON_TYPE){
            view = LayoutInflater.from(parent.getContext( )).inflate(R.layout.item_make_up_round_icon, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext( )).inflate(R.layout.item_make_up, parent, false);
        }

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return mViewType;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        BeautyData item = mDataList.get(position);
        String path = (String) item.getImageResource( );
        if (item.isBuildIn( )) {
            path = "file:///android_asset/" + path;
        }
        if(holder.getItemViewType() == MAKE_UP_RANDOM_BG_TYPE && item.getBackgroundColor() != 0) {
            holder.makeup_text.setBackgroundColor(item.getBackgroundColor());
            holder.makeup_imageMask.setBackgroundColor(item.getBackgroundColor());
            if(position == 0) {
                holder.makeup_item_layout.setBackgroundColor(mContext.getResources( ).getColor(R.color.msc4c4c4));
            }
        } else {
            holder.makeup_text.setBackgroundColor(mContext.getResources( ).getColor(R.color.nv_colorTranslucent));
            holder.makeup_item_layout.setBackgroundColor(mContext.getResources( ).getColor(R.color.white));
        }
        RequestBuilder requestBuilder = Glide.with(mContext.getApplicationContext( )).load(path);
        if(holder.getItemViewType() == MAKE_UP_ROUND_ICON_TYPE) {
            requestBuilder.apply(mOptions);
            holder.makeup_imageMask.setBackgroundResource(R.drawable.blue_thumb);
        }
        requestBuilder.into(holder.makeup_imageAsset);
        holder.makeup_text.setText(item.getName(mContext));
        if (mIsEnable) {
            if (mSelectedPos == position) {
                holder.makeup_text.setTextColor(Color.parseColor("#CC4A90E2"));
                holder.makeup_text.setAlpha(1.0f);
                holder.makeup_imageMask.setVisibility(View.VISIBLE);
                if(!mIsFirstLoad && mViewType == MAKE_UP_RANDOM_BG_TYPE) {
                    holder.makeup_item_layout.setSelected(true);
                    int px = ScreenUtils.dip2px(mContext, 2.5f);
                    holder.makeup_item_layout.setY(px);
                }
                if(mIsFirstLoad) {
                    mIsFirstLoad = false;
                }
            } else {
                if(holder.makeup_item_layout.isSelected()) {
                    holder.makeup_item_layout.setSelected(false);
                    int px = ScreenUtils.dip2px(mContext, 10.0f);
                    holder.makeup_item_layout.setY(px);
                }
                if(holder.getItemViewType() == MAKE_UP_RANDOM_BG_TYPE) {
                    holder.makeup_text.setTextColor(Color.WHITE);
                    holder.makeup_text.setAlpha(1.0f);
                } else {
                    holder.makeup_text.setTextColor(Color.BLACK);
                    holder.makeup_text.setAlpha(0.8f);
                }
                holder.makeup_imageMask.setVisibility(View.GONE);
            }
        } else {
            holder.makeup_text.setTextColor(mContext.getResources( ).getColor(R.color.ms_disable_color));
            holder.makeup_item_layout.setAlpha(0.5f);
            holder.makeup_text.setAlpha(0.5f);
            holder.makeup_imageAsset.setAlpha(0.5f);
            holder.makeup_imageMask.setVisibility(View.GONE);
        }

        holder.makeup_item_layout.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                if (!mIsEnable) {
                    return;
                }
                if (mClickListener != null) {
                    mSelectedPos = position;
                    notifyDataSetChanged( );
                    mClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size( );
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private View makeup_item_layout;
        private ImageView makeup_imageAsset;
        private ImageView makeup_imageMask;
        private TextView makeup_text;

        public ViewHolder(View view) {
            super(view);
            makeup_item_layout = view.findViewById(R.id.makeup_item_layout);
            makeup_imageAsset = (ImageView) view.findViewById(R.id.makeup_imageAsset);
            makeup_imageMask = (ImageView) view.findViewById(R.id.makeup_icon_mask);
            makeup_text = (TextView) view.findViewById(R.id.makeup_text);
        }
    }

}
