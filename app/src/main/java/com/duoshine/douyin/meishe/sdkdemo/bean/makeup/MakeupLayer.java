package com.duoshine.douyin.meishe.sdkdemo.bean.makeup;

public class MakeupLayer {
    private int layer;
    private String layerId;
    private int isLUT;
    private float intensity;
    private String texFilePath;
    private String lutFilePath;
    private String texColor;
    private String blendingMode;

    public int getLayer() {
        return layer;
    }

    public String getLayerId() {
        return layerId;
    }

    public int getIsLUT() {
        return isLUT;
    }

    public float getIntensity() {
        return intensity;
    }

    public String getTexFilePath() {
        return texFilePath;
    }

    public String getLutFilePath() {
        return lutFilePath;
    }

    public String getTexColor() {
        return texColor;
    }

    public String getBlendingMode() {
        return blendingMode;
    }

    public void setTexColor(String texColor) {
        this.texColor = texColor;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
