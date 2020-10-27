package com.duoshine.douyin.meishe.sdkdemo.edit.data;


import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;

/**
 * Created by admin on 2018/7/10.
 */

public class AssetItem {
    /*
    * 表示无素材
    * Means no material
    * */
    public static final int ASSET_NONE = 1;
    /*
    * 下载到本地的素材
    * Download to local material
    * */
    public static final int ASSET_LOCAL = 2;
    /*
    * 内建素材素材
    * Built-in materials
    * */
    public static final int ASSET_BUILTIN = 3;

    public AssetItem(){}

    public int getImageRes() {
        return mImageRes;
    }

    public void setImageRes(int imageRes) {
        this.mImageRes = imageRes;
    }

    public int getAssetMode() {
        return mAssetMode;
    }

    public void setAssetMode(int assetMode) {
        this.mAssetMode = assetMode;
    }

    public NvAsset getAsset() {
        return mAsset;
    }

    public void setAsset(NvAsset asset) {
        this.mAsset = asset;
    }

    public String getFilterColor() {
        return mFilterColor;
    }

    public void setFilterColor(String filterColor) {
        this.mFilterColor = filterColor;
    }

    private int mImageRes;
    private int mAssetMode;
    private NvAsset mAsset;

    /*
     * 滤镜对应颜色值，douyin编辑滤镜和粒子编辑滤镜专用
     * Corresponds to the color value of the filter.
     * It is dedicated to the douyin editing filter and the particle editing filter.
     * */
    private String mFilterColor;
}
