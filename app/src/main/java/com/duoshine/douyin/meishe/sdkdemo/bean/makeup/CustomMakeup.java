package com.duoshine.douyin.meishe.sdkdemo.bean.makeup;
import java.util.ArrayList;

public class CustomMakeup extends BaseBeautyData {
    private ArrayList<MakeupEffect<MakeupEffectContent>> effectList;
    public ArrayList<MakeupEffect<MakeupEffectContent>> getEffectList() {
        return effectList;
    }

    @Override
    public void setFolderPath(String folderPath) {
        super.setFolderPath(folderPath);
        if (effectList != null && effectList.size() > 0) {
            for (MakeupEffect<MakeupEffectContent> makeupEffect : effectList) {
                makeupEffect.getEffectContent().setFolderPath(folderPath);
            }
        }
    }

    @Override
    public void setIsBuildIn(boolean buildIn) {
        super.setIsBuildIn(buildIn);
        if (effectList != null && effectList.size() > 0) {
            for (MakeupEffect<MakeupEffectContent> makeupEffect : effectList) {
                makeupEffect.getEffectContent().setBuildIn(buildIn);
            }
        }
    }
}
