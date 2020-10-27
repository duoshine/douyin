package com.duoshine.douyin.meishe.sdkdemo.edit.data;


import com.duoshine.douyin.meishe.sdkdemo.flipcaption.FlipCaptionDataInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.CompoundCaptionInfo;

import java.util.ArrayList;

/**
 * Created by admin on 2018/7/11.
 */

public class BackupData {
    private static BackupData mAssetDataBackup;
    private ArrayList<CaptionInfo> mCaptionArrayList;
    private ArrayList<ClipInfo> mClipInfoArray;
    private ArrayList<CompoundCaptionInfo> mCompoundCaptionList;
    private int mCaptionZVal;
    /*
     * 贴纸和字幕使用
     * For stickers and subtitles
     * */
    private long m_curSeekTimelinePos = 0;
    /*
     * 片段编辑专用
     * For clip editing
     * */
    private int mClipIndex;
    /*
     * 画中画页面使用
     * For PIP pages
     * */
    private ArrayList<String> mPicInPicVideoArray;

    /*
     * 只在EditActivity点击添加视频使用
     * Use only in EditActivity click to add video
     * */
    private ArrayList<ClipInfo> mAddClipInfoList;

    /*
     * 只在翻转字幕编辑文本时使用
     * Only used when flipping subtitles to edit text
     * */
    private ArrayList<FlipCaptionDataInfo> mFlipDataInfoList;

    public ArrayList<FlipCaptionDataInfo> getFlipDataInfoList() {
        return mFlipDataInfoList;
    }

    public ArrayList<FlipCaptionDataInfo> cloneFlipCaptionData() {
        ArrayList<FlipCaptionDataInfo> newList = new ArrayList<>();
        for (FlipCaptionDataInfo flipCaptionInfo : mFlipDataInfoList) {
            FlipCaptionDataInfo newFlipCaptionInfo = flipCaptionInfo.clone();
            newList.add(newFlipCaptionInfo);
        }
        return newList;
    }

    public void setFlipDataInfoList(ArrayList<FlipCaptionDataInfo> flipDataInfoList) {
        this.mFlipDataInfoList = flipDataInfoList;
    }

    public ArrayList<String> getPicInPicVideoArray() {
        return mPicInPicVideoArray;
    }

    public void setPicInPicVideoArray(ArrayList<String> picInPicVideoArray) {
        this.mPicInPicVideoArray = picInPicVideoArray;
    }

    public ArrayList<ClipInfo> getAddClipInfoList() {
        return mAddClipInfoList;
    }

    public void setAddClipInfoList(ArrayList<ClipInfo> addClipInfoList) {
        this.mAddClipInfoList = addClipInfoList;
    }

    public void clearAddClipInfoList() {
        mAddClipInfoList.clear();
    }

    public int getClipIndex() {
        return mClipIndex;
    }

    public void setClipIndex(int clipIndex) {
        this.mClipIndex = clipIndex;
    }

    public long getCurSeekTimelinePos() {
        return m_curSeekTimelinePos;
    }

    public void setCurSeekTimelinePos(long curSeekTimelinePos) {
        this.m_curSeekTimelinePos = curSeekTimelinePos;
    }

    public int getCaptionZVal() {
        return mCaptionZVal;
    }

    public void setCaptionZVal(int captionZVal) {
        this.mCaptionZVal = captionZVal;
    }

    public void setCaptionData(ArrayList<CaptionInfo> captionArray) {
        mCaptionArrayList = captionArray;
    }

    public ArrayList<CaptionInfo> getCaptionData() {
        return mCaptionArrayList;
    }

    public ArrayList<CaptionInfo> cloneCaptionData() {
        ArrayList<CaptionInfo> newList = new ArrayList<>();
        for (CaptionInfo captionInfo : mCaptionArrayList) {
            CaptionInfo newCaptionInfo = captionInfo.clone();
            newList.add(newCaptionInfo);
        }
        return newList;
    }

    public void setClipInfoData(ArrayList<ClipInfo> clipInfoArray) {
        this.mClipInfoArray = clipInfoArray;
    }

    public ArrayList<ClipInfo> getClipInfoData() {
        return mClipInfoArray;
    }

    public ArrayList<ClipInfo> cloneClipInfoData() {
        ArrayList<ClipInfo> newArrayList = new ArrayList<>();
        for (ClipInfo clipInfo : mClipInfoArray) {
            ClipInfo newClipInfo = clipInfo.clone();
            newArrayList.add(newClipInfo);
        }
        return newArrayList;
    }

    public ArrayList<CompoundCaptionInfo> getCompoundCaptionList() {
        return mCompoundCaptionList;
    }

    public void setCompoundCaptionList(ArrayList<CompoundCaptionInfo> compoundCaptionList) {
        this.mCompoundCaptionList = compoundCaptionList;
    }

    public ArrayList<CompoundCaptionInfo> cloneCompoundCaptionData() {
        ArrayList<CompoundCaptionInfo> newList = new ArrayList<>();
        for (CompoundCaptionInfo captionInfo : mCompoundCaptionList) {
            CompoundCaptionInfo newCaptionInfo = captionInfo.clone();
            newList.add(newCaptionInfo);
        }
        return newList;
    }

    public static BackupData init() {
        if (mAssetDataBackup == null) {
            synchronized (BackupData.class) {
                if (mAssetDataBackup == null) {
                    mAssetDataBackup = new BackupData();
                }
            }
        }
        return mAssetDataBackup;
    }

    public void clear() {
        mCaptionArrayList.clear();
        clearAddClipInfoList();
        mClipInfoArray.clear();
        mFlipDataInfoList.clear();
        if (mCompoundCaptionList != null) {
            mCompoundCaptionList.clear();
        }
        mCaptionZVal = 0;
        mClipIndex = 0;
        m_curSeekTimelinePos = 0;
    }

    public static BackupData instance() {
        if (mAssetDataBackup == null)
            mAssetDataBackup = new BackupData();
        return mAssetDataBackup;
    }

    private BackupData() {
        mCaptionArrayList = new ArrayList<>();
        mClipInfoArray = new ArrayList<>();
        mAddClipInfoList = new ArrayList<>();
        mPicInPicVideoArray = new ArrayList<>();
        mFlipDataInfoList = new ArrayList<>();
        mCompoundCaptionList = new ArrayList<>();
        mCaptionZVal = 0;
        mClipIndex = 0;
        m_curSeekTimelinePos = 0;
    }
}
