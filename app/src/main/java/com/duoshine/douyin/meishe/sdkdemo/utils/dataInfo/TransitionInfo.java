package com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo;

import com.duoshine.douyin.R;
import com.meicam.sdk.NvsVideoTransition;

public class TransitionInfo {
    public static int TRANSITIONMODE_BUILTIN = 0;
    public static int TRANSITIONMODE_PACKAGE = 1;

    private int m_transitionMode;
    private String m_transitionId;

    private int m_imageId;
    private String m_imageUrl;

    private long mTransitionInterval = 1000000L;

    private NvsVideoTransition mVideoTransition;

    public long getTransitionInterval() {
        return mTransitionInterval;
    }

    public void setTransitionInterval(long transitionInterval) {
        this.mTransitionInterval = transitionInterval;
    }

    public NvsVideoTransition getVideoTransition() {
        return mVideoTransition;
    }

    public void setVideoTransition(NvsVideoTransition videoTransition) {
        this.mVideoTransition = videoTransition;
    }

    public int getM_imageId() {
        return m_imageId;
    }

    public void setM_imageId(int m_imageId) {
        this.m_imageId = m_imageId;
    }

    public String getM_imageUrl() {
        return m_imageUrl;
    }

    public void setM_imageUrl(String m_imageUrl) {
        this.m_imageUrl = m_imageUrl;
    }

    public TransitionInfo() {
        m_transitionId = "Fade";
        m_transitionMode = TRANSITIONMODE_BUILTIN;
        m_imageId = R.mipmap.fade;
        m_imageUrl = "";
    }

    public void setTransitionMode(int mode) {
        m_transitionMode = mode;
    }

    public int getTransitionMode() {
        return m_transitionMode;
    }

    public void setTransitionId(String fxId) {
        m_transitionId = fxId;
    }

    public String getTransitionId() {
        return m_transitionId;
    }

    public TransitionInfo copySelf() {
        TransitionInfo transitionInfo = new TransitionInfo();
        transitionInfo.setVideoTransition(mVideoTransition);
        transitionInfo.setTransitionInterval(mTransitionInterval);
        transitionInfo.setTransitionMode(m_transitionMode);
        transitionInfo.setTransitionId(m_transitionId);
        transitionInfo.setM_imageId(m_imageId);
        transitionInfo.setM_imageUrl(m_imageUrl);
        return transitionInfo;
    }
}
