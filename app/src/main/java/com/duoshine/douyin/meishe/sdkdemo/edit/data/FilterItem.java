package com.duoshine.douyin.meishe.sdkdemo.edit.data;

import android.util.Log;

import java.io.Serializable;

public class FilterItem implements Serializable {
    /**
     * 内建特效
     * Built-in effects
     */
    public static int FILTERMODE_BUILTIN = 0;
    /**
     * Asset中预装
     * Pre-installed in Asset
     */
    public static int FILTERMODE_BUNDLE = 1;
    /**
     * 包裹特效
     * Package effects
     */
    public static int FILTERMODE_PACKAGE = 2;

    private String m_filterName;
    private int m_filterMode;
    private String m_filterId;
    private int m_imageId;
    private String m_imageUrl;
    private String m_packageId;
    private String m_assetDescription;
    private boolean m_isSpecialFilter = false;

    /**
     * 为了兼容旧版本，该字段主要用于转场国际化使用
     * For compatibility with older versions, this field is mainly used for transition internationalization
     */
    private String m_filterDesc;
    private int categoryId = -1;

    /*
    * 粒子类型
    * Particle type
    * */
    private int particleType = 0;

    /*
    * 用于特殊漫画的字段
    * Fields for special comics
    * */
    private boolean isCartoon = false;
    private boolean isStrokenOnly = true;
    private boolean isGrayScale = true;

    private int backgroundColor;

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /*
    * 下载
    * download
    * */
    public int downloadProgress = 0;
    public int downloadStatus = 0;

    public FilterItem() {
        m_filterId = null;
        m_filterName = null;
        m_filterMode = FILTERMODE_BUILTIN;
        m_imageId = 0;
        m_imageUrl = null;
        m_packageId = null;
        m_assetDescription = null;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setFilterName(String name) {
        m_filterName = name;
    }

    public String getFilterName() {
        return m_filterName;
    }

    public void setFilterMode(int mode) {
        if (mode != FILTERMODE_BUILTIN && mode != FILTERMODE_BUNDLE && mode != FILTERMODE_PACKAGE) {
            Log.e("", "invalid mode data");
            return;
        }
        m_filterMode = mode;
    }

    public int getFilterMode() {
        return m_filterMode;
    }

    public void setFilterId(String fxId) {
        m_filterId = fxId;
    }

    public String getFilterId() {
        return m_filterId;
    }

    public void setImageId(int imageId) {
        m_imageId = imageId;
    }

    public int getImageId() {
        return m_imageId;
    }

    public void setImageUrl(String imageUrl) {
        m_imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return m_imageUrl;
    }

    public void setPackageId(String packageId) {
        m_packageId = packageId;
    }

    public String getPackageId() {
        return m_packageId;
    }

    public String getAssetDescription() {
        return m_assetDescription;
    }

    public void setAssetDescription(String m_assetDescription) {
        this.m_assetDescription = m_assetDescription;
    }

    public String getFilterDesc() {
        return m_filterDesc;
    }

    public void setFilterDesc(String m_filterDesc) {
        this.m_filterDesc = m_filterDesc;
    }

    public void setStrokenOnly(boolean strokenOnly) {
        isStrokenOnly = strokenOnly;
    }

    public boolean getStrokenOnly() {
        return isStrokenOnly;
    }

    public void setGrayScale(boolean grayScale) {
        isGrayScale = grayScale;
    }

    public boolean getGrayScale() {
        return isGrayScale;
    }

    public void setIsCartoon(boolean isCartoon) {
        this.isCartoon = isCartoon;
    }

    public boolean getIsCartoon() {
        return isCartoon;
    }

    public int getParticleType() {
        return particleType;
    }

    public void setParticleType(int particleType) {
        this.particleType = particleType;
    }

    public boolean isSpecialFilter() {
        return m_isSpecialFilter;
    }

    public void setIsSpecialFilter(boolean m_isSpecialFilter) {
        this.m_isSpecialFilter = m_isSpecialFilter;
    }
}
