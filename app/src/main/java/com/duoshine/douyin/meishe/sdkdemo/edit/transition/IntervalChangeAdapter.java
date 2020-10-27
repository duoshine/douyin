package com.duoshine.douyin.meishe.sdkdemo.edit.transition;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.duoshine.douyin.R;


public class IntervalChangeAdapter extends RecyclerView.Adapter<IntervalChangeAdapter.ViewHolder> {
    private Context mContext;
    private OnItemClickListener mClickListener;
    private int[] mTimeSet;
    private int mSelectPos = -1;

    public interface OnItemClickListener {

        /**
         * 条目点击
         * Item click
         * @param view
         * @param position
         * @param intervalValue
         */
        void onItemClick(View view, int position, int intervalValue);
    }

    public void setSelectPos(int pos) {
        this.mSelectPos = pos;
        notifyDataSetChanged();
    }

    public int getSelectPos() {
        return mSelectPos;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView item_assetName;

        public ViewHolder(View view) {
            super(view);
            item_assetName = (TextView) view.findViewById(R.id.name);
        }
    }

    public IntervalChangeAdapter(Context context) {
        mContext = context;
    }

    public void setTimeChangeSet(int[] timeSet) {
        this.mTimeSet = timeSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_type, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        int timeValue = mTimeSet[position];
        String string = mContext.getString(R.string.text_second);
        holder.item_assetName.setText(timeValue + string);
        if (mSelectPos == position) {
            holder.item_assetName.setTextColor(Color.parseColor("#CC4A90E2"));
        } else {
            holder.item_assetName.setTextColor(Color.parseColor("#CCFFFFFF"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyItemChanged(mSelectPos);
                mSelectPos = position;
                notifyItemChanged(mSelectPos);

                if (mClickListener != null) {
                    mClickListener.onItemClick(view, position, mTimeSet[position]);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTimeSet.length;
    }
}
