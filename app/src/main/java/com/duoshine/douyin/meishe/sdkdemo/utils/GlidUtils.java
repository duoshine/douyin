package com.duoshine.douyin.meishe.sdkdemo.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.MSApplication;

public class GlidUtils {
    public static void setImageByPath(ImageView imageView, String path) {

        RequestOptions options = new RequestOptions().centerCrop()
                .placeholder(R.drawable.bank_thumbnail_local)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        Glide.with(MSApplication.getmContext())
                .asBitmap()
                .load(path)
                .apply(options)
                .into(imageView);
    }


    public static void setImageByPathAndWidth(ImageView imageView, String path, int width) {
        RequestOptions options = new RequestOptions().centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .override(width, width);
        Glide.with(MSApplication.getmContext())
                .asBitmap()
                .load(path)
                .apply(options)
                .into(imageView);
    }
}
