package com.duoshine.douyin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duoshine.douyin.R;
import com.duoshine.douyin.util.Urls;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.MyViewHolder> {
    private int viewType = 0;

    public WorkAdapter(Context context,int viewType) {
        this.context = context;
        this.viewType = viewType;
    }

    public static final String TAG = "AdapterRecyclerView";
    int[] videoIndexs = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private Context context;

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater from = LayoutInflater.from(context);
        View view = null;
        if (viewType == 0) {
            view = from.inflate(R.layout.item_videoview, parent,
                    false);
        } else {
            view = from.inflate(R.layout.item_videoview_city, parent,
                    false);
        }
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        Log.i(TAG, "onBindViewHolder [" + holder.jzvdStd.hashCode() + "] position=" + position);
        holder.jzvdStd.setUp(
                Urls.videoUrls[0][position],
                Urls.videoTitles[0][position], Jzvd.SCREEN_NORMAL);
        Glide.with(holder.jzvdStd.getContext()).load(Urls.videoPosters[0][position]).into(holder.jzvdStd.posterImageView);

        holder.jzvdStd.posterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return videoIndexs.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        JzvdStd jzvdStd;

        public MyViewHolder(View itemView) {
            super(itemView);
            jzvdStd = itemView.findViewById(R.id.videoplayer);
        }
    }

}
