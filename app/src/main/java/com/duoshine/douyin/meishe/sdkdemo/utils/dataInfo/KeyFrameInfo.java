package com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo;

import android.graphics.PointF;

/**
 * author : lhz
 * date   : 2020/8/3
 * desc   :关键帧信息
 */
public class KeyFrameInfo {
    private float scaleX;
    private float scaleY;
    private float rotationZ;
    private PointF translation;

    public float getScaleX() {
        return scaleX;
    }

    public KeyFrameInfo setScaleX(float scaleX) {
        this.scaleX = scaleX;
        return this;
    }

    public float getScaleY() {
        return scaleY;
    }

    public KeyFrameInfo setScaleY(float scaleY) {
        this.scaleY = scaleY;
        return this;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public KeyFrameInfo setRotationZ(float rotationZ) {
        this.rotationZ = rotationZ;
        return this;
    }

    public PointF getTranslation() {
        return translation;
    }

    public KeyFrameInfo setTranslation(PointF translation) {
        this.translation = translation;
        return this;
    }
}
