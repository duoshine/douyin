package com.duoshine.douyin.meishe.sdkdemo.bean.makeup;

public class ComposeMakeup extends MakeupEffect<ComposeEffectContent> {
    @Override
    public void setFolderPath(String folderPath) {
        super.setFolderPath(folderPath);
        ComposeEffectContent effectContent = getEffectContent();
        if (effectContent != null) {
            effectContent.setFolderPath(folderPath);
        }
    }

    @Override
    public void setIsBuildIn(boolean buildIn) {
        super.setIsBuildIn(buildIn);
        ComposeEffectContent effectContent = getEffectContent();
        if (effectContent != null) {
            getEffectContent().setBuildIn(buildIn);
        }
    }
}
