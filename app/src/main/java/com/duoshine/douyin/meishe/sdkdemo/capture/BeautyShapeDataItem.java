package com.duoshine.douyin.meishe.sdkdemo.capture;

/**
 * @Class: com.meishe.sdkdemo.capture.BeautyShapeDataItem.java
 * @Time: 2019/3/22 0022 14:18
 * @author: mlj
 * @Description: 拍摄页面美颜美型数据对象类;Beauty data class for capturing video
 */
public class BeautyShapeDataItem {
    private String path;
    public String beautyShapeId;
    public double strength = 0.0d;
    public int resId;
    public boolean needDefaultStrength = false;
    public String name;
    public String type;
    public double defaultValue = 0.0d;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
