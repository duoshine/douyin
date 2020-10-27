package com.duoshine.douyin.meishe.sdkdemo.edit.anim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.utils.ScreenUtils;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoTrack;

import java.util.ArrayList;

/**
 * Created by jml on 2020/08/24
 */

public class AnimationClipAdapter extends RecyclerView.Adapter<AnimationClipAdapter.ViewHolder> {
    private Context mContext;
    private OnItemClickListener mClickListener;
    private ArrayList<ClipInfo> clipInfoList = new ArrayList<>();
    private int mSelectPos = 0;
    RequestOptions mOptions = new RequestOptions();
    private NvsTimeline mTimeline;

    public void setTimeLine(NvsTimeline mTimeline) {
        this.mTimeline = mTimeline;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public AnimationClipAdapter(Context context) {
        mContext = context;
        mOptions.centerCrop();
        mOptions.skipMemoryCache(false);
        mOptions.placeholder(R.mipmap.default_filter);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setClipInfoList(ArrayList<ClipInfo> clipInfoList) {
        this.clipInfoList = clipInfoList;
        notifyDataSetChanged();
    }
    public void setSelectPos(int pos) {
        this.mSelectPos = pos;
        notifyDataSetChanged();
    }

    /**
     * 设置选中的片段 上 设置动画和时长 及动画类型
     * @param selectedPosition    片段的index
     * @param mAnimationDuration  动画时长
     * @param animationType       动画类型
     */
    public void setmAnimationDuration(int selectedPosition ,long mAnimationDuration,int animationType) {
        if(null != clipInfoList &&clipInfoList.size()>selectedPosition && selectedPosition>=0){
            ClipInfo clipInfo = clipInfoList.get(selectedPosition);
            clipInfo.setmAnimationDuration(mAnimationDuration);
            clipInfo.setmAnimationType(animationType);
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout item_assetLayout;
        private ImageView item_assetImage;
        private TextView item_assetName;
        private View view_frame;
        public ViewHolder(View view) {
            super(view);
            item_assetLayout = (FrameLayout) view.findViewById(R.id.assetItem);
            item_assetName = (TextView) view.findViewById(R.id.nameAsset);
            item_assetImage = (ImageView) view.findViewById(R.id.imageAsset);
            view_frame = (View) view.findViewById(R.id.view_frame);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_animation_clip, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ClipInfo itemData = clipInfoList.get(position);
        long currentItemDuration =0;
        if(null != mTimeline){
            NvsVideoTrack videoTrack = mTimeline.getVideoTrackByIndex(0);
            if(null != videoTrack){
                NvsVideoClip clip =videoTrack .getClipByIndex(position);
                if(null != clip){
                    long clipInPoint = clip.getInPoint();
                    long clipOutPoint = clip.getOutPoint();
                    currentItemDuration = clipOutPoint-clipInPoint;
                    holder.item_assetName.setText((currentItemDuration / 1000000)+"s");
                }
            }
        }
        String imageUrl = itemData.getFilePath();
        if (imageUrl != null) {
            //加载图片
            Glide.with(mContext)
                    .asBitmap()
                    .load(imageUrl)
                    .apply(mOptions)
                    .into(holder.item_assetImage);
        }

        if(mSelectPos == position) {
            holder.item_assetLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.activity_animation_clip_selected));
        } else {
            holder.item_assetLayout.setBackground(ContextCompat.getDrawable(mContext, R.drawable.activity_animation_clip_unselected));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mClickListener != null) {
                    mClickListener.onItemClick(view,position);
                }
                if(mSelectPos == position)
                    return;
                notifyItemChanged(mSelectPos);
                mSelectPos = position;
                notifyItemChanged(mSelectPos);
            }
        });
        //设置动画时长对应的蒙层
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.view_frame.getLayoutParams();
        int itemWidth = ScreenUtils.dip2px(mContext,78);
        int viewWidth = (int) (itemData.getmAnimationDuration()*1.0f/currentItemDuration*itemWidth);
        lp.width = viewWidth;
        lp.height = FrameLayout.LayoutParams.MATCH_PARENT;
        if(itemData.getmAnimationType() == NvAsset.ASSET_ANIMATION_OUT){
            lp.leftMargin = itemWidth - viewWidth;
            holder.view_frame.setBackground(mContext.getResources().getDrawable(R.drawable.activity_animation_clip_selected_animation));
        }else{
            lp.leftMargin = 0;
            holder.view_frame.setBackground(mContext.getResources().getDrawable(R.drawable.activity_animation_clip_selected_animation_right));
        }

        holder.view_frame.setLayoutParams(lp);

    }

    @Override
    public int getItemCount() {
        return clipInfoList.size();
    }
}
