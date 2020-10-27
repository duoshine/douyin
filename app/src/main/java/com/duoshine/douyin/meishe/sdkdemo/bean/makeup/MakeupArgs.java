package com.duoshine.douyin.meishe.sdkdemo.bean.makeup;

import java.util.List;

public class MakeupArgs {
    private String makeupValueStr;
    private String makeupId;
    private float intensity;
    private String color;
    private List<MakeupLayer> makeupLayers;
    private List<RecommendColor> makeupRecommendColors;

    public String getMakeupValueStr() {
        return makeupValueStr;
    }

    public void setMakeupValueStr(String makeupValueStr) {
        this.makeupValueStr = makeupValueStr;
    }

    public String getMakeupId() {
        return makeupId;
    }

    public void setMakeupId(String makeupId) {
        this.makeupId = makeupId;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<MakeupLayer> getMakeupLayers() {
        return makeupLayers;
    }

    public void setMakeupLayers(List<MakeupLayer> makeupLayers) {
        this.makeupLayers = makeupLayers;
    }

    public List<RecommendColor> getMakeupRecommendColors() {
        return makeupRecommendColors;
    }

    public void setMakeupRecommendColors(List<RecommendColor> makeupRecommandColors) {
        this.makeupRecommendColors = makeupRecommandColors;
    }

    public class RecommendColor {
        private String makeupColor;

        public String getMakeupColor() {
            return makeupColor;
        }

        public void setMakeupColor(String makeupColor) {
            this.makeupColor = makeupColor;
        }
    }

}
