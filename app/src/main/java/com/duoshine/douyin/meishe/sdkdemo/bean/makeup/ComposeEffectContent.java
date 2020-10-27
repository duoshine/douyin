package com.duoshine.douyin.meishe.sdkdemo.bean.makeup;

import java.util.List;

/**
 * 组合美妆的特效集合，包括美妆数据
 */
public class ComposeEffectContent extends MakeupEffectContent {
    private List<FilterArgs> filter;
    private List<BeautyFxArgs> shape;
    private List<BeautyFxArgs> beauty;

    public List<FilterArgs> getFilter() {
        return filter;
    }

    public void setFilter(List<FilterArgs> filter) {
        this.filter = filter;
    }

    public List<BeautyFxArgs> getShape() {
        return shape;
    }

    public void setShape(List<BeautyFxArgs> shape) {
        this.shape = shape;
    }

    public List<BeautyFxArgs> getBeauty() {
        return beauty;
    }

    public void setBeauty(List<BeautyFxArgs> beauty) {
        this.beauty = beauty;
    }
}
