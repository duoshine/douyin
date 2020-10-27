package com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo;

/**
 * @author :Jml
 * @date :2020/8/26 15:27
 * @des : 视频片段上的动画特效信息
 */
public class AnimationInfo {
    private String mPackageId;
    private long mAnimationIn;
    private long mAnimationOut;
    private int mAssetType;

    public int getmAssetType() {
        return mAssetType;
    }

    public void setmAssetType(int mAssetType) {
        this.mAssetType = mAssetType;
    }

    public void setmPackageId(String mPackageId) {
        this.mPackageId = mPackageId;
    }

    public void setmAnimationIn(long mAnimationIn) {
        this.mAnimationIn = mAnimationIn;
    }

    public void setmAnimationOut(long mAnimationOut) {
        this.mAnimationOut = mAnimationOut;
    }

    public String getmPackageId() {
        return mPackageId;
    }

    public long getmAnimationIn() {
        return mAnimationIn;
    }

    public long getmAnimationOut() {
        return mAnimationOut;
    }
}
