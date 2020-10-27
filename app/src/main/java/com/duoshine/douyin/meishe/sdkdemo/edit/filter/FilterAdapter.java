package com.duoshine.douyin.meishe.sdkdemo.edit.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.base.BaseConstants;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.FilterItem;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyj on 2017/12/19 0019.
 */

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private Context mContext;
    private Boolean mIsArface = false;
    private boolean mIsFirstLoad = true;
    private OnItemClickListener mClickListener;
    private List<FilterItem> mFilterDataList = new ArrayList<>();
    RequestOptions mOptions = new RequestOptions();
    private int mSelectPos = 0;
    private int mSpecialCount = 0;
    private final int ITEM_TYPE_NONE = 0;
    private final int ITEM_TYPE_SPLIT = 1;


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public FilterAdapter(Context context) {
        mContext = context;
        mOptions.centerCrop();
        mOptions.skipMemoryCache(false);
        mOptions.placeholder(R.mipmap.bank_thumbnail_local);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setFilterDataList(List<FilterItem> filterDataList) {
        this.mFilterDataList = filterDataList;
        mSpecialCount = 0;
        for (int i = 0; i < mFilterDataList.size(); i++) {
            if (mFilterDataList.get(i).isSpecialFilter()) {
                mSpecialCount++;
            }
        }
    }

    public int getSpecialFilterCount() {
        return mSpecialCount;
    }

    public void setSelectPos(int pos) {
        this.mSelectPos = pos;
    }

    public int getSelectPos() {
        return mSelectPos;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private View filter_item_layout;
        private View item_assetShadow;
        private ImageView item_assetImage;
        private TextView item_assetName;
        private ImageView mProp3DImage;

        public ViewHolder(View view) {
            super(view);
            filter_item_layout = view.findViewById(R.id.filter_item_layout);
            item_assetShadow = view.findViewById(R.id.assetShadow);
            item_assetName = (TextView) view.findViewById(R.id.nameAsset);
            item_assetImage = (ImageView) view.findViewById(R.id.imageAsset);
            mProp3DImage = view.findViewById(R.id.prop_3d_image);
        }
    }

    public void isArface(Boolean isArface) {
        mIsArface = isArface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == ITEM_TYPE_NONE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fx, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fx_split, parent, false);
        }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    public List<FilterItem> getFilterDataList() {
        return mFilterDataList;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mSpecialCount > 0 && position == (mSpecialCount + 1)) {
            return;
        }
        FilterItem itemData = mFilterDataList.get(position);
        if (itemData == null)
            return;

        if(itemData.getBackgroundColor() != 0) {
            holder.item_assetShadow.setBackgroundColor(itemData.getBackgroundColor());
            holder.item_assetName.setBackgroundColor(itemData.getBackgroundColor());
            if(position == 0) {
                holder.filter_item_layout.setBackgroundColor(mContext.getResources( ).getColor(R.color.msc4c4c4));
            }
        }
        String name = itemData.getFilterName();
        if (name != null && !mIsArface) {
            holder.item_assetName.setText(name);
        }
        if (mIsArface) {
            holder.item_assetName.setText("");
        }

        int filterMode = itemData.getFilterMode();
        if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
            int imageId = itemData.getImageId();
            if (imageId != 0)
                holder.item_assetImage.setImageResource(imageId);
        } else {
            String imageUrl = itemData.getImageUrl();
            if (imageUrl != null) {
                //加载图片
                Glide.with(mContext)
                        .asBitmap()
                        .load(imageUrl)
                        .apply(mOptions)
                        .into(holder.item_assetImage);
            }
        }
        if (mSelectPos == position) {
            holder.item_assetShadow.setVisibility(View.VISIBLE);
            if(!mIsFirstLoad) {
                holder.filter_item_layout.setSelected(true);
                int px = ScreenUtils.dip2px(mContext, 2.5f);
                holder.filter_item_layout.setY(px);
            }
            if(mIsFirstLoad) {
                mIsFirstLoad = false;
            }
        } else {
//            holder.item_assetImage.setAlpha(1.0f);
            holder.item_assetShadow.setVisibility(View.GONE);
            if(holder.filter_item_layout.isSelected()) {
                holder.filter_item_layout.setSelected(false);
                int px = ScreenUtils.dip2px(mContext, 10.0f);
                holder.filter_item_layout.setY(px);
            }
        }
        if ("00C96B57-3E1E-4E3D-A4D8-D1E3BB3589BA".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        } else if ("233C8731-7D9E-4D6D-85B6-87D104FC3CCF".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        } else if ("7269C2C7-6249-4ABF-9329-325898DAD9E6".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_2D);
        } else if ("11526CF9-BFA0-4A19-B7B2-1A879CF58FF1".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        } else if ("B2187FB5-A8B3-4E87-A5CD-F8EA6B3456D4".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        } else if ("7242B80E-A804-4CB5-B7DD-DFACC1B6BF6F".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        } else if ("3A66960A-E129-4040-B523-1C87544FB008".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        } else if ("DD133FD6-75F4-4584-8206-BBF257D92D44".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        } else if ("084A6EC1-43AB-40EF-BBD5-D83F692B011B".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_2D);
        } else if ("289829A7-EA10-423E-96EA-5BBB23A1B86D".equals(itemData.getPackageId())) {
            itemData.setCategoryId(BaseConstants.PROP_TYPE_3D);
        }

        if (itemData.getCategoryId() <= BaseConstants.PROP_IMAGES.length && itemData.getCategoryId() - 1 >= 0) {
            holder.mProp3DImage.setVisibility(View.VISIBLE);
            holder.mProp3DImage.setBackground(mContext.getResources().getDrawable(BaseConstants.PROP_IMAGES[itemData.getCategoryId() - 1]));
        } else {
            holder.mProp3DImage.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(view, position);
                }

//                if (mSelectPos == position) {
//                    return;
//                }
//
//                notifyItemChanged(mSelectPos);
//                mSelectPos = position;
//                notifyItemChanged(mSelectPos);
                mSelectPos = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilterDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mSpecialCount > 0 && position == (mSpecialCount + 1)) {
            return ITEM_TYPE_SPLIT;
        } else {
            return ITEM_TYPE_NONE;
        }
    }
}
