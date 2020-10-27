package com.duoshine.douyin.meishe.sdkdemo.bean.makeup;

import android.content.Context;

import com.duoshine.douyin.meishe.sdkdemo.utils.ColorUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.SystemUtils;

import java.io.File;
import java.util.List;

public class BaseBeautyData implements BeautyData {
    protected String folderPath;
    private String name;
    private String cover;
    protected boolean isBuildIn = true;
    protected List<Translation> translation;
    protected int backgroundColor;

    @Override
    public String getName(Context context) {
        if (translation == null || translation.isEmpty()) {
            return name;
        }
        if(!SystemUtils.isZh(context)){
            return translation.get(0).getOriginalText();
        }
        return translation.get(0).getTargetText();
    }

    @Override
    public String getImageResource() {
        return folderPath + File.separator + cover;
    }

    @Override
    public void setFolderPath(String path) {
        folderPath = path;
    }

    @Override
    public String getFolderPath() {
        return folderPath;
    }

    @Override
    public boolean isBuildIn() {
        return isBuildIn;
    }

    @Override
    public void setIsBuildIn(boolean buildIn) {
        isBuildIn = buildIn;
    }

    public String getName() {
        return name;
    }

    public String getCover() {
        return cover;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public List<Translation> getTranslation() {
        return translation;
    }

    @Override
    public int getBackgroundColor() {
        if(backgroundColor == 0) {
            backgroundColor = ColorUtil.getFilterRandomBgColor();
        }
        return backgroundColor;
    }
}
