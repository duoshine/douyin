package com.duoshine.douyin.meishe.sdkdemo.capture;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;

import java.util.ArrayList;


public class BeautyShapeAdapter extends RecyclerView.Adapter<BeautyShapeAdapter.ViewHolder> {

    private ArrayList<BeautyShapeDataItem> mDataList;
    private int mSelectedPos = Integer.MAX_VALUE;
    private Context mContext;
    private OnItemClickListener mClickListener;
    private boolean mIsEnable = true;
    //判断是否是美型
    private boolean isBeautyShape = false;

    public static final int POS_BEAUTY_STRENGTH_0 = 0;
    public static final int POS_BEAUTY_WHITING_1 = 1;
    public static final int POS_BEAUTY_REDDING_2 = 2;
    public static final int POS_BEAUTY_ADJUSTCOLOR_3 = 3;
    public static final int POS_BEAUTY_SHARPEN_4 = 4;
    private boolean needFirstBack = false;
    /**
     * 两种类型
     * 1.美型的类型，点击返回选择类型的列表
     * 2.美型的item操作，点击设置值
     */
    public static final int TYPE_KIND_BACK = 1;
    public static final int TYPE_KIND_ITEM = 2;
    //选中的是哪个类型
    private BeautyShapeDataKindItem shapeDataKindItem;

    public BeautyShapeAdapter(Context context, ArrayList dataList) {
        mContext = context;
        mDataList = dataList;
    }

    public void setIsBeautyShape(boolean isBeautyShape) {
        this.isBeautyShape = isBeautyShape;
    }

    public void setDataList(ArrayList<BeautyShapeDataItem> data) {

        this.mDataList = data;
    }

    public void setEnable(boolean enable) {
        mIsEnable = enable;
        notifyDataSetChanged();
    }

    public void setSelectPos(int pos) {
        mSelectedPos = pos;
        Log.e("tell", "setSelectPos position = " + pos);
        notifyDataSetChanged();
    }

    public int getSelectPos() {
        return mSelectedPos;
    }

    public BeautyShapeDataItem getSelectItem() {
        if (mDataList != null && mSelectedPos >= 0 && mSelectedPos < mDataList.size()) {
            return mDataList.get(mSelectedPos);
        }
        return null;
    }

    public BeautyShapeDataItem getItem(int pos) {
        if (mDataList != null && pos >= 0 && pos < mDataList.size()) {
            return mDataList.get(pos);
        }
        return null;
    }

    public void setWittenName(int pos, String newName) {
        if (mDataList != null && pos >= 0 && pos < mDataList.size()) {
            BeautyShapeDataItem item = mDataList.get(pos);
            if (item == null) {
                return;
            }
            item.name = newName;
            notifyItemChanged(pos);
        }
    }

    /**
     * 设置选中的美型类型
     *
     * @param shapeDataKindItem
     */
    public void setSelectedKind(BeautyShapeDataKindItem shapeDataKindItem) {
        this.shapeDataKindItem = shapeDataKindItem;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && null != shapeDataKindItem) {
            return TYPE_KIND_BACK;
        } else {
            return TYPE_KIND_ITEM;
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void updateDataList(ArrayList dataList) {
        mDataList.clear();
        mDataList.addAll(dataList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.beauty_shape_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final int itemType = getItemViewType(position);
        if (itemType == TYPE_KIND_BACK) {
            //返回键，
            if (null != shapeDataKindItem) {
                int padding = ScreenUtils.dip2px(mContext, 10);
                holder.shape_icon.setPadding(padding, padding, padding, padding);
                holder.shape_icon.setImageResource(R.mipmap.makeup_back);
                holder.shape_name.setText(shapeDataKindItem.getName());
                holder.shape_name.setTextColor(Color.WHITE);
                holder.shape_icon_layout.setBackground(mContext.getResources().getDrawable(R.drawable.activity_capture_beauty_kind_selected_background));
                //设置点击事件
                holder.shape_icon_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mClickListener) {
                            mClickListener.onItemClick(v, position);
                        }
                    }
                });
            }
        } else {
            BeautyShapeDataItem item = mDataList.get(position);
            holder.shape_icon.setImageResource(item.resId);
            holder.shape_name.setText(item.name);
            if (mIsEnable) {
                holder.shape_name.setTextColor(mContext.getResources().getColor(R.color.black_alfph));
            } else {
                holder.shape_name.setTextColor(mContext.getResources().getColor(R.color.ms_disable_color));
            }
            if (mIsEnable && mSelectedPos == position) {
                holder.shape_icon.setSelected(true);
                holder.shape_name.setTextColor(Color.parseColor("#CC4A90E2"));
                holder.shape_icon_layout.setAlpha(1.0f);
                holder.shape_name.setAlpha(1.0f);
            } else {
                holder.shape_icon.setSelected(false);
                if (mIsEnable && mSelectedPos != position) {
                    if (isBeautyShape) {
                        holder.shape_name.setTextColor(mContext.getResources().getColor(R.color.black_alfph));
                    } else {
                        holder.shape_name.setTextColor(Color.BLACK);
                    }
                    holder.shape_icon_layout.setAlpha(1.0f);
                    holder.shape_name.setAlpha(0.8f);

                } else if (!mIsEnable) {
                    holder.shape_name.setTextColor(Color.BLACK);
                    holder.shape_icon_layout.setAlpha(0.5f);
                    holder.shape_name.setAlpha(0.5f);
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mIsEnable) {
                        return;
                    }
                    if (mClickListener != null) {
                        notifyItemChanged(mSelectedPos);
                        Log.e("tell", "onClick position = " + position);
                        mSelectedPos = position;
                        notifyItemChanged(mSelectedPos);
                        mClickListener.onItemClick(v, position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout shape_icon_layout;
        private ImageView shape_icon;
        private TextView shape_name;


        public ViewHolder(View view) {
            super(view);
            shape_icon_layout = (RelativeLayout) view.findViewById(R.id.shape_icon_layout);
            shape_icon = (ImageView) view.findViewById(R.id.shape_icon);
            shape_name = (TextView) view.findViewById(R.id.shape_txt);
        }
    }

}
