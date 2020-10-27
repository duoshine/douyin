package com.duoshine.douyin.meishe.sdkdemo.edit.watermark;


import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by CaoZhiChao on 2018/10/16 15:28
 */
public interface OnItemClickListener {
    void onClick(RecyclerView.ViewHolder holder, int position, int pictureType, String picturePath, int waterMarkType, String waterMarkPicture);
}
