package com.duoshine.douyin.meishe.sdkdemo.capture;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

/**
 * @author jml
 * @des 美型类型选择的适配器
 */
public class BeautyShapeKindAdapter extends RecyclerView.Adapter<BeautyShapeKindAdapter.ViewHolder> {

    private ArrayList<BeautyShapeDataKindItem> mDataList;
    private int mSelectedPos = 0;
    private Context mContext;
    private OnItemClickListener mClickListener;
    private boolean isEnable = true;


    public BeautyShapeKindAdapter(Context context, ArrayList dataList) {
        mContext = context;
        mDataList = dataList;
    }

    public void setDataList(ArrayList<BeautyShapeDataKindItem> data) {
        this.mDataList = data;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
        notifyDataSetChanged();
    }

    /**
     * 选中某一项了
     * @param pos
     */
    public void setSelectPos(int pos) {
        if(pos>=0 && pos<mDataList.size()){
            mSelectedPos = pos;
            Log.e("tell", "setSelectPos position = " + pos);
            notifyDataSetChanged();
        }
    }

    public BeautyShapeDataKindItem getSelectItem() {
        if (mDataList != null && mSelectedPos >= 0 && mSelectedPos < mDataList.size( )) {
            return mDataList.get(mSelectedPos);
        }
        return null;
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void updateDataList(ArrayList dataList) {
        mDataList.clear( );
        mDataList.addAll(dataList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext( )).inflate(R.layout.beauty_shape_kind_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final BeautyShapeDataKindItem item = mDataList.get(position);
        holder.shape_icon.setImageResource(item.getIcon());
        holder.shape_name.setText(item.getName());

        //选中的类型显示mask，选中效果
        if(mSelectedPos == position){
            holder.shape_icon_mask.setImageResource(R.mipmap.beauty_facetype_kind_edit);
            holder.shape_icon_mask.setBackground(mContext.getResources().getDrawable(R.drawable.activity_capture_beauty_kind_selected_background));
            int padding = ScreenUtils.dip2px(mContext,13);
            holder.shape_icon_mask.setPadding(padding,padding,padding,padding);
            holder.shape_icon_mask.setVisibility(View.VISIBLE);
            holder.shape_name.setTextColor(mContext.getResources().getColor(R.color.menu_selected));
        }else{
            holder.shape_icon_mask.setVisibility(View.GONE);
            holder.shape_name.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        //美型是否開啟
        if (isEnable) {
            holder.shape_name.setTextColor(Color.BLACK);
            holder.shape_icon_layout.setAlpha(1.0f);
            holder.shape_name.setAlpha(1.0f);
        } else {
            GradientDrawable background = (GradientDrawable) holder.shape_icon_layout.getBackground( );
            background.setColor(mContext.getResources( ).getColor(R.color.white));
            holder.shape_name.setTextColor(Color.BLACK);
            holder.shape_icon_layout.setAlpha(0.5f);
            holder.shape_name.setAlpha(0.5f);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                if (mClickListener != null && isEnable) {
                   if(position == mSelectedPos){
                       mClickListener.onItemRepeatSelected(item,position);
                   }else{
                       mClickListener.onItemSelected(item,position);
                   }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size( );
    }

    public BeautyShapeDataKindItem getItemByPosition(int position){
        if(position>=0 && position<mDataList.size()){
            return mDataList.get(position);
        }
        return null;
    }
    public interface OnItemClickListener {
        //第一次选中
        void onItemSelected(BeautyShapeDataKindItem item, int position);
        //第二次选择，此时显示各种调节的特效
        void onItemRepeatSelected(BeautyShapeDataKindItem item, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout shape_icon_layout;
        private ImageView shape_icon;
        private ImageView shape_icon_mask;
        private TextView shape_name;


        public ViewHolder(View view) {
            super(view);
            shape_icon_layout = (RelativeLayout) view.findViewById(R.id.shape_icon_layout);
            shape_icon = (ImageView) view.findViewById(R.id.shape_icon);
            shape_icon_mask = (ImageView) view.findViewById(R.id.shape_icon_mask);
            shape_name = (TextView) view.findViewById(R.id.shape_txt);
        }
    }

}
