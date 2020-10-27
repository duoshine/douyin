package com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo;

import android.graphics.Bitmap;
import android.graphics.PointF;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class StickerInfo {
    private String m_id;
    private String m_imagePath;
    private String m_fileUrl;
    private Bitmap m_image;
    private String m_packagePath;
    private float m_scaleFactor;
    private float m_rotation;
    private PointF m_translation;
    private int m_animateStickerZVal;
    private boolean m_horizFlip;
    private long m_inPoint;
    private long m_outPoint;
    private float m_volumeGain;
    private long m_duration;
    /*
     * 是否是自定义贴纸使用
     * Whether it is a custom sticker
     * */
    private boolean isCustomSticker;
    /*
     * 自定义贴纸图片路径
     * Custom sticker picture path
     * */
    private String m_customImagePath;

    /**
     * 关键帧
     */
    private Map<Long, KeyFrameInfo> mKeyFrameInfoHashMap = new TreeMap<>(
            new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {
                    // 时间升序排列
                    return o1.compareTo(o2);
                }
            }
    );

    public void putKeyFrameInfo(long keyFrameTime, KeyFrameInfo stickerKeyFrameInfo) {
        if (mKeyFrameInfoHashMap != null) {
            mKeyFrameInfoHashMap.put(keyFrameTime, stickerKeyFrameInfo);
        }
    }

    public void setKeyFrameInfoHashMap(Map<Long, KeyFrameInfo> keyFrameInfoMap) {
        this.mKeyFrameInfoHashMap = keyFrameInfoMap;
    }

    public Map<Long, KeyFrameInfo> getKeyFrameInfoHashMap() {
        return mKeyFrameInfoHashMap;
    }

    public String getCustomImagePath() {
        return m_customImagePath;
    }

    public void setCustomImagePath(String customImagePath) {
        this.m_customImagePath = customImagePath;
    }

    public boolean isCustomSticker() {
        return isCustomSticker;
    }

    public void setCustomSticker(boolean customSticker) {
        isCustomSticker = customSticker;
    }

    public float getVolumeGain() {
        return m_volumeGain;
    }

    public void setVolumeGain(float volumeGain) {
        this.m_volumeGain = volumeGain;
    }

    public long getInPoint() {
        return m_inPoint;
    }

    public void setInPoint(long inPoint) {
        this.m_inPoint = inPoint;
    }

    public long getOutPoint() {
        return m_outPoint;
    }

    public void setOutPoint(long outPoint) {
        this.m_outPoint = outPoint;
    }

    public StickerInfo() {
        m_scaleFactor = 1;
        m_rotation = 0;
        m_translation = null;
        m_imagePath = null;
        m_fileUrl = null;
        m_image = null;
        m_packagePath = null;
        m_animateStickerZVal = 0;
        m_horizFlip = false;
        m_volumeGain = 1.0f;
        isCustomSticker = false;
        m_customImagePath = "";
    }

    public long getDuration() {
        return m_duration;
    }

    public void setDuration(long duration) {
        this.m_duration = duration;
    }

    public boolean isHorizFlip() {
        return m_horizFlip;
    }

    public void setHorizFlip(boolean horizFlip) {
        this.m_horizFlip = horizFlip;
    }

    public int getAnimateStickerZVal() {
        return m_animateStickerZVal;
    }

    public void setAnimateStickerZVal(int animateStickerZVal) {
        this.m_animateStickerZVal = animateStickerZVal;
    }

    public void setId(String id) {
        m_id = id;
    }

    public String getId() {
        return m_id;
    }

    public void setPackagePath(String path) {
        m_packagePath = path;
    }

    public String getPackagePath() {
        return m_packagePath;
    }

    public void setImagePath(String filePath) {
        m_imagePath = filePath;
    }

    public String getImagePath() {
        return m_imagePath;
    }

    public void setFileUrl(String url) {
        m_fileUrl = url;
    }

    public String getFileUrl() {
        return m_fileUrl;
    }

    public void setImage(Bitmap bitmap) {
        m_image = bitmap;
    }

    public Bitmap getImage() {
        return m_image;
    }

    public void setScaleFactor(float value) {
        m_scaleFactor = value;
    }

    public float getScaleFactor() {
        return m_scaleFactor;
    }

    public void setRotation(float value) {
        m_rotation = value;
    }

    public float getRotation() {
        return m_rotation;
    }

    public void setTranslation(PointF point) {
        m_translation = point;
    }

    public PointF getTranslation() {
        return m_translation;
    }

    public StickerInfo clone() {
        StickerInfo newStickerInfo = new StickerInfo();
        newStickerInfo.setId(this.getId());
        newStickerInfo.setFileUrl(this.getFileUrl());
        newStickerInfo.setImage(this.getImage());
        newStickerInfo.setImagePath(this.getImagePath());
        newStickerInfo.setPackagePath(this.getPackagePath());
        newStickerInfo.setRotation(this.getRotation());
        newStickerInfo.setScaleFactor(this.getScaleFactor());
        newStickerInfo.setTranslation(this.getTranslation());

        //copy data
        newStickerInfo.setAnimateStickerZVal(this.getAnimateStickerZVal());
        newStickerInfo.setInPoint(this.getInPoint());
        newStickerInfo.setOutPoint(this.getOutPoint());
        newStickerInfo.setHorizFlip(this.isHorizFlip());
        newStickerInfo.setVolumeGain(this.getVolumeGain());
        newStickerInfo.setDuration(this.getDuration());
        newStickerInfo.setKeyFrameInfoHashMap(this.getKeyFrameInfoHashMap());
        /*
         * 自定义贴纸数据
         * Custom sticker data
         * */
        newStickerInfo.setCustomSticker(this.isCustomSticker());
        if (this.isCustomSticker()) {
            newStickerInfo.setCustomImagePath(this.getCustomImagePath());
        }
        return newStickerInfo;
    }
}
