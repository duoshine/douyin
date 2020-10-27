package com.duoshine.douyin.meishe.sdkdemo.utils.asset;

/**
 * Created by shizhouhu on 2018/6/13.
 */

import com.meicam.sdk.NvsAssetPackageManager;

public class NvAsset {
    public static final int ASSET_THEME = 1;
    public static final int ASSET_FILTER = 2;
    public static final int ASSET_CAPTION_STYLE = 3;
    public static final int ASSET_ANIMATED_STICKER = 4;
    public static final int ASSET_VIDEO_TRANSITION = 5;
    public static final int ASSET_FONT = 6;
    public static final int ASSET_CAPTURE_SCENE = 8;
    public static final int ASSET_PARTICLE = 9;
    public static final int ASSET_FACE_STICKER = 10;
    public static final int ASSET_FACE1_STICKER = 11;
    public static final int ASSET_CUSTOM_ANIMATED_STICKER = 12;
    public static final int ASSET_SUPER_ZOOM = 13;
    public static final int ASSET_FACE_BUNDLE_STICKER = 14;
    public static final int ASSET_ARSCENE_FACE = 15;
    public static final int ASSET_COMPOUND_CAPTION = 16;
    public static final int ASSET_PHOTO_ALBUM = 17;
    public static final int ASSET_CAPTION_RICH_WORD = 18;
    public static final int ASSET_CAPTION_BUBBLE = 19;
    public static final int ASSET_CAPTION_ANIMATION = 20;
    public static final int ASSET_CAPTION_IN_ANIMATION = 21;
    public static final int ASSET_CAPTION_OUT_ANIMATION = 22;
    public static final int ASSET_MIMO = 23;
    public static final int ASSET_ANIMATION_IN = 24;
    public static final int ASSET_ANIMATION_OUT = 25;
    public static final int ASSET_ANIMATION_COMPANY = 26;

    /*
     * 不适配比例
     * Unfit ratio
     * */
    public static final int AspectRatio_NoFitRatio = 0;//
    public static final int AspectRatio_16v9 = 1;
    public static final int AspectRatio_1v1 = 2;
    public static final int AspectRatio_9v16 = 4;
    public static final int AspectRatio_4v3 = 8;
    public static final int AspectRatio_3v4 = 16;
    public static final int AspectRatio_18v9 = 32;
    public static final int AspectRatio_9v18 = 64;
    public static final int AspectRatio_21v9 = 512;
    public static final int AspectRatio_9v21 = 1024;
    public static final int AspectRatio_All = AspectRatio_16v9 | AspectRatio_1v1 | AspectRatio_9v16
            | AspectRatio_3v4 | AspectRatio_4v3 | AspectRatio_21v9 | AspectRatio_9v21 | AspectRatio_18v9 | AspectRatio_9v18;

    public static final int RatioArray[] = {
            AspectRatio_16v9,
            AspectRatio_1v1,
            AspectRatio_9v16,
            AspectRatio_3v4,
            AspectRatio_4v3,
            AspectRatio_21v9,
            AspectRatio_9v21,
            AspectRatio_18v9,
            AspectRatio_9v18,
            AspectRatio_All
    };

    public static final String RatioStringArray[] = {
            "16:9",
            "1:1",
            "9:16",
            "3:4",
            "4:3",
            "21:9",
            "9:21",
            "18:9",
            "9:18",
            "通用"
    };

    /*
     * 进入页面的初始状态
     * Enter the initial state of the page
     * */
    public static final int DownloadStatusNone = 0;
    /*
     * 等待状态
     * Waiting state
     * */
    public static final int DownloadStatusPending = 1;
    /*
     * 下载中
     * downloading
     * */
    public static final int DownloadStatusInProgress = 2;
    /*
     * 安装中
     * installing
     * */
    public static final int DownloadStatusDecompressing = 3;
    /*
     * 下载成功
     * download successfully
     * */
    public static final int DownloadStatusFinished = 4;
    /*
     * 下载失败
     * download failed
     * */
    public static final int DownloadStatusFailed = 5;
    /*
     * 安装失败
     * installation failed
     * */
    public static final int DownloadStatusDecompressingFailed = 6;

    public static final int NV_CATEGORY_ID_ALL = 0;
    public static final int NV_CATEGORY_ID_DOUYINFILTER = 7;
    public static final int NV_CATEGORY_ID_CUSTOM = 20000;
    /**
     * 动画子分类id
     */
    public static final int NV_CATEGORY_ID_ANIMATION_IN = 8;
    public static final int NV_CATEGORY_ID_ANIMATION_OUT = 9;
    public static final int NV_CATEGORY_ID_ANIMATION_COMPANY = 10;
    /*
     * 粒子滤镜类型：触摸
     * Particle filter type: Touch
     * */
    public static final int NV_CATEGORY_ID_PARTICLE_TOUCH_TYPE = 2;

    public String uuid = "";
    public int categoryId = 1;
    public int version = 1;
    public int aspectRatio = 0;
    public String name = "";
    public String coverUrl = "";
    public String desc = "";
    public String tags = "";
    public String minAppVersion = "";
    public String localDirPath = "";
    public String bundledLocalDirPath = "";
    public boolean isReserved = false;
    public String remotePackageUrl = "";
    public int remoteVersion = 1;
    public int downloadProgress = 0;
    public int remotePackageSize = 0;
    public int downloadStatus = 0;
    public int assetType = 0;
    public String assetDescription = "";
    public String fxFileName;

    /**
     * 判断素材是否是预装素材。
     * Determine whether the material is pre-installed.
     */
    public boolean isReserved() {
        return isReserved;
    }

    /**
     * 判断素材是否可用。说明：可用仅表示素材在本地或安装包里存在，并不表示它已安装。
     * Determine if the material is available. Note: Available only means that the material exists locally
     * or in the installation package, it does not mean that it is installed.
     */
    public boolean isUsable() {
        return !localDirPath.isEmpty() || !bundledLocalDirPath.isEmpty();
    }

    /**
     * 判断素材是否有在线素材。
     * Determine whether the material has online material.
     */
    public boolean hasRemoteAsset() {
        return !remotePackageUrl.isEmpty();
    }

    /**
     * 判断素材是否有更新。
     * Determine if the material has been updated.
     */
    public boolean hasUpdate() {
        if (!isUsable() || !hasRemoteAsset())
            return false;

        return remoteVersion > version;
    }

    /**
     * 判断素材是否正在安装。
     * Determine if the material is being installed.
     */
    public boolean isInstalling() {
        return downloadStatus == DownloadStatusDecompressing;
    }

    /**
     * 判断素材是否安装失败。
     * Determine whether the installation fails.
     */
    public boolean isInstallingFailed() {
        return downloadStatus == DownloadStatusDecompressingFailed;
    }

    /**
     * 判断素材是否安装完成。
     * Determine whether the material is installed.
     */
    public boolean isInstallingFinished() {
        return downloadStatus == DownloadStatusFinished;
    }

    /**
     * 获取SDK中的素材类型
     * Get material type in SDK
     */
    public int getPackageType() {
        if (assetType == NvAsset.ASSET_THEME) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_THEME;
        } else if (assetType == NvAsset.ASSET_FILTER) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX;
        } else if (assetType == NvAsset.ASSET_CAPTION_STYLE) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTIONSTYLE;
        } else if (assetType == NvAsset.ASSET_ANIMATED_STICKER) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER;
        } else if (assetType == NvAsset.ASSET_VIDEO_TRANSITION) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOTRANSITION;
        } else if (assetType == NvAsset.ASSET_CAPTURE_SCENE) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTURESCENE;
        } else if (assetType == NvAsset.ASSET_PARTICLE) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX;
        } else if (assetType == NvAsset.ASSET_FACE_STICKER) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTURESCENE;
        } else if (assetType == NvAsset.ASSET_CUSTOM_ANIMATED_STICKER) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ANIMATEDSTICKER;
        } else if (assetType == NvAsset.ASSET_ARSCENE_FACE) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_ARSCENE;
        } else if (assetType == NvAsset.ASSET_COMPOUND_CAPTION) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_COMPOUND_CAPTION;
        } else if (assetType == NvAsset.ASSET_CAPTION_RICH_WORD) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTION_RENDERER;
        } else if (assetType == NvAsset.ASSET_CAPTION_BUBBLE) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTION_CONTEXT;
        } else if (assetType == NvAsset.ASSET_CAPTION_ANIMATION) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTION_ANIMATION;
        }  else if (assetType == NvAsset.ASSET_CAPTION_IN_ANIMATION) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTION_IN_ANIMATION;
        } else if (assetType == NvAsset.ASSET_CAPTION_OUT_ANIMATION) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_CAPTION_OUT_ANIMATION;
        }
        else if (assetType == NvAsset.ASSET_ANIMATION_IN ||assetType == NvAsset.ASSET_ANIMATION_OUT ||assetType == NvAsset.ASSET_ANIMATION_COMPANY ) {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX;
        }
        else {
            return NvsAssetPackageManager.ASSET_PACKAGE_TYPE_THEME;
        }
    }

    public void copyAsset(NvAsset asset) {
        uuid = asset.uuid;
        categoryId = asset.categoryId;
        version = asset.version;
        aspectRatio = asset.aspectRatio;
        name = asset.name;
        coverUrl = asset.coverUrl;
        desc = asset.desc;
        tags = asset.tags;
        minAppVersion = asset.minAppVersion;
        localDirPath = asset.localDirPath;
        bundledLocalDirPath = asset.bundledLocalDirPath;
        isReserved = asset.isReserved;
        remotePackageUrl = asset.remotePackageUrl;
        remoteVersion = asset.remoteVersion;
        downloadProgress = asset.downloadProgress;
        remotePackageSize = asset.remotePackageSize;
        downloadStatus = asset.downloadStatus;
        assetType = asset.assetType;
        assetDescription = asset.assetDescription;
    }
}
