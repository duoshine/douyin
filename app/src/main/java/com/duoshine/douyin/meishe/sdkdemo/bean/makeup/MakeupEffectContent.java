package com.duoshine.douyin.meishe.sdkdemo.bean.makeup;

import android.text.TextUtils;

import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsMakeupEffectInfo;

import java.io.File;
import java.util.List;

/**
 * 美妆数据集合
 */
public class MakeupEffectContent {
    protected List<MakeupArgs> makeupArgs;
    protected NvsMakeupEffectInfo mMakeupEffectInfo;
    protected String folderPath;
    private boolean isBuildIn = true;

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public boolean isBuildIn() {
        return isBuildIn;
    }

    public void setBuildIn(boolean buildIn) {
        isBuildIn = buildIn;
    }

    public List<MakeupArgs> getMakeupArgs() {
        return makeupArgs;
    }

    public void setMakeupArgs(List<MakeupArgs> makeupArgs) {
        this.makeupArgs = makeupArgs;
    }

    public NvsMakeupEffectInfo parseToNvsMakeupData() {
        if (makeupArgs == null || makeupArgs.isEmpty()) {
            return null;
        }
        NvsMakeupEffectInfo makeupEffectInfo = new NvsMakeupEffectInfo();
        int effectSize = makeupArgs.size();
        for (int index = 0; index < effectSize; index++) {
            MakeupArgs makeupArgs = this.makeupArgs.get(index);
            NvsMakeupEffectInfo.MakeupEffect makeupEffect = new NvsMakeupEffectInfo.MakeupEffect();
            NvsColor nvsColor = parseNvsColor(makeupArgs.getColor());
            if (nvsColor == null) {
                continue;
            }
            makeupEffect.color = nvsColor;
            makeupEffect.effectId = makeupArgs.getMakeupId();
            makeupEffect.intensity = makeupArgs.getIntensity();
            List<MakeupLayer> makeupLayers = makeupArgs.getMakeupLayers();
            if (makeupLayers == null || makeupLayers.isEmpty()) {
                continue;
            }

            for (int i = 0; i < makeupLayers.size(); i++) {
                MakeupLayer makeupLayer = makeupLayers.get(i);
                NvsMakeupEffectInfo.MakeupEffectLayer makeupEffectLayer = null;
                if (makeupLayer.getIsLUT() == 1) {
                    makeupEffectLayer = new NvsMakeupEffectInfo.MakeupEffectLayerLut();
                    String lutPath = folderPath + File.separator + makeupLayer.getLutFilePath();
                    if (isBuildIn()) {
                        lutPath = "assets:/" + lutPath;
                    }
                    ((NvsMakeupEffectInfo.MakeupEffectLayerLut) makeupEffectLayer).lutFilePath = lutPath;
                } else {
                    makeupEffectLayer = new NvsMakeupEffectInfo.MakeupEffectLayer3D();
                    ((NvsMakeupEffectInfo.MakeupEffectLayer3D) makeupEffectLayer).blendingMode = parseBlendingMode(makeupLayer.getBlendingMode());
                    ((NvsMakeupEffectInfo.MakeupEffectLayer3D) makeupEffectLayer).texColor = parseNvsColor(makeupLayer.getTexColor());
                    String texPath = folderPath + File.separator + makeupLayer.getTexFilePath();
                    if (isBuildIn()) {
                        texPath = "assets:/" + texPath;
                    }
                    ((NvsMakeupEffectInfo.MakeupEffectLayer3D) makeupEffectLayer).texFilePath = texPath;
                }
                makeupEffectLayer.layerId = makeupLayer.getLayerId();
                makeupEffectLayer.intensity = makeupLayer.getIntensity();
                makeupEffect.addMakeupEffectLayer(makeupEffectLayer);
            }
            makeupEffectInfo.addMakeupEffect(makeupEffect);
        }
        mMakeupEffectInfo = makeupEffectInfo;
        return makeupEffectInfo;
    }

    public NvsMakeupEffectInfo getMakeupEffectInfo() {
        if (mMakeupEffectInfo == null) {
            mMakeupEffectInfo = parseToNvsMakeupData();
        }
        return mMakeupEffectInfo;
    }

    public void clearMakeupEffectInfo() {
        mMakeupEffectInfo = null;
    }

    private NvsColor parseNvsColor(String color) {
        String[] split = color.split(",");
        if (split.length < 4) {
            return null;
        }
        NvsColor nvsColor = new NvsColor(Float.parseFloat(split[0]), Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]));
        return nvsColor;
    }

    private int parseBlendingMode(String mode) {
        if (TextUtils.isEmpty(mode)) {
            return NvsMakeupEffectInfo.MAKEUP_EFFECT_BLENDING_MODE_UNKNOWN;
        }
        mode = mode.toLowerCase();
        if (mode.equals("normal")) {
            return NvsMakeupEffectInfo.MAKEUP_EFFECT_BLENDING_MODE_NORMAL;
        } else if (mode.equals("multiply")) {
            return NvsMakeupEffectInfo.MAKEUP_EFFECT_BLENDING_MODE_MULTIPLY;
        } else if (mode.equals("subtract")) {
            return NvsMakeupEffectInfo.MAKEUP_EFFECT_BLENDING_MODE_SUBTRACT;
        } else if (mode.equals("screen")) {
            return NvsMakeupEffectInfo.MAKEUP_EFFECT_BLENDING_MODE_SCREEN;
        } else if (mode.equals("add")) {
            return NvsMakeupEffectInfo.MAKEUP_EFFECT_BLENDING_MODE_ADD;
        } else if (mode.equals("exclusion")) {
            return NvsMakeupEffectInfo.MAKEUP_EFFECT_BLENDING_MODE_EXCLUSION;
        }
        return NvsMakeupEffectInfo.MAKEUP_EFFECT_BLENDING_MODE_NORMAL;
    }

}
