package com.duoshine.douyin.meishe.sdkdemo.edit.data;


import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;

import java.io.Serializable;

/**
 * 道具bean
 */
public class Props implements Serializable {
    private String uuid;
    private String name;
    private String zh_name;

    private int categoryId = -1;
    private String cover;
    public Props(NvAsset asset){
        this.uuid = asset.uuid;
        this.name = asset.name;
        this.zh_name = asset.name;
        this.categoryId = asset.categoryId;
        this.cover = asset.coverUrl;
    }

    public Props(){}

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getZh_name() {
        return zh_name;
    }

    public void setZh_name(String zh_name) {
        this.zh_name = zh_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
