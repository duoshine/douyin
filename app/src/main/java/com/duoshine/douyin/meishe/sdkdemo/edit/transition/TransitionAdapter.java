package com.duoshine.douyin.meishe.sdkdemo.edit.transition;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.FilterItem;

import java.util.List;

public class TransitionAdapter extends RecyclerView.Adapter<TransitionAdapter.ViewHolder> {
    private Context mContext;
    private OnItemClickListener mClickListener;
    private List<FilterItem> mFilterList;
    private int mSelectPos = 0;

    public interface OnItemClickListener {

        /**
         * 条目点击
         * Item click
         * @param view
         * @param position
         */
        void onItemClick(View view, int position);

        /**
         * 条目点击，传递新转场信息，更新转场
         * Click on the item, pass the new transition information, and update the transition
         * @param filterItem
         */
        void onResetTransition(FilterItem filterItem);

        /**
         * 重复点击同一个条目(进行参数编辑)
         * Click the same entry repeatedly (for parameter editing)
         */
        void onSameItemClick(int position);
    }

    public void setSelectPos(int pos) {
        this.mSelectPos = pos;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout item_assetLayout;
        private RelativeLayout item_coverLayout;
        private ImageView item_assetImage;
        private TextView item_assetName;
        private ImageView itemEditParameterImage;

        public ViewHolder(View view) {
            super(view);
            item_assetLayout = (RelativeLayout) view.findViewById(R.id.layoutAsset);
            item_coverLayout = (RelativeLayout) view.findViewById(R.id.selected_cover_layout);
            item_assetName = (TextView) view.findViewById(R.id.nameAsset);
            item_assetImage = (ImageView) view.findViewById(R.id.imageAsset);
            itemEditParameterImage = (ImageView) view.findViewById(R.id.edit_parameter_layout);
        }
    }

    public TransitionAdapter(Context context) {
        mContext = context;
    }

    public void setFilterList(List<FilterItem> filterList) {
        this.mFilterList = filterList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transition, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        FilterItem itemData = mFilterList.get(position);
        if (itemData == null) {
            return;
        }
        if (position == 0) {
            holder.item_assetName.setText("");
            holder.itemEditParameterImage.setVisibility(View.GONE);
        } else {
            // FIXME: 2019/10/17 0017 临时修改转场
            holder.itemEditParameterImage.setVisibility(View.GONE);
        }
        String name = itemData.getFilterDesc();
        if (name != null) {
            if (mSelectPos == position) {
                holder.item_assetName.setTextColor(Color.parseColor("#4A90E2"));
            }else {
                holder.item_assetName.setTextColor(Color.parseColor("#FFFFFF"));
            }
            holder.item_assetName.setText(name);
        }
        int filterMode = itemData.getFilterMode();
        if (filterMode == FilterItem.FILTERMODE_BUILTIN) {
            int imageId = itemData.getImageId();
            if (imageId != 0) {
                holder.item_assetImage.setImageResource(imageId);
            }
        }

        String imageUrl = itemData.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            if ((filterMode == FilterItem.FILTERMODE_BUNDLE) || (filterMode == FilterItem.FILTERMODE_PACKAGE)) {
                //加载图片
                RequestOptions options = new RequestOptions();
                options.diskCacheStrategy(DiskCacheStrategy.NONE);
                options.centerCrop();
                Glide.with(mContext)
                        .asBitmap()
                        .load(imageUrl)
                        .apply(options)
                        .into(holder.item_assetImage);
            }
        }

        if (mSelectPos == position) {
            holder.item_coverLayout.setVisibility(View.VISIBLE);
            holder.item_assetLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.fx_item_radius_border_select));
        } else {
            holder.item_coverLayout.setVisibility(View.GONE);
            holder.item_assetLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.fx_item_radius_border_unselect));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectPos == position) {
                    if (mClickListener != null) {
                        mClickListener.onSameItemClick(mSelectPos);
                    }
                    return;
                }

                notifyItemChanged(mSelectPos);
                mSelectPos = position;
                notifyItemChanged(mSelectPos);

                if (mClickListener != null) {
                    mClickListener.onItemClick(view, position);
                    FilterItem item = mFilterList.get(position);
                    mClickListener.onResetTransition(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }
}
