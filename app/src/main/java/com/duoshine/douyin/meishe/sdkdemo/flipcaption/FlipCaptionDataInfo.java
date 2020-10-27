package com.duoshine.douyin.meishe.sdkdemo.flipcaption;

/**
 * Created by admin on 2018/12/26.
 */

public class FlipCaptionDataInfo {
    public FlipCaptionDataInfo clone() {
        FlipCaptionDataInfo dataInfo = new FlipCaptionDataInfo();
        dataInfo.setCaptionText(this.getCaptionText());
        dataInfo.setCaptionColor(this.getCaptionColor());
        dataInfo.setSelectItem(this.isSelectItem());
        return dataInfo;
    }
    public String getCaptionText() {
        return mCaptionText;
    }

    public void setCaptionText(String captionText) {
        mCaptionText = captionText;
    }

    public String getCaptionColor() {
        return mCaptionColor;
    }

    public void setCaptionColor(String captionColor) {
        mCaptionColor = captionColor;
    }

    public boolean isSelectItem() {
        return mSelectItem;
    }

    public void setSelectItem(boolean selectItem) {
        mSelectItem = selectItem;
    }

    private String mCaptionText;
    private String mCaptionColor = "#ccffffff";
    /*
    * 是否选中字幕Item
    * Whether to select the subtitle Item
    * */
    private boolean mSelectItem;
}
