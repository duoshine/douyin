package com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo;

import com.duoshine.douyin.meishe.sdkdemo.edit.watermark.WaterMarkData;
import com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset;
import com.google.gson.Gson;
import com.meicam.sdk.NvsVideoResolution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.duoshine.douyin.meishe.sdkdemo.utils.Constants.VIDEOVOLUME_DEFAULTVALUE;


public class TimelineData {

    private static TimelineData m_timelineData;
    NvsVideoResolution m_videoResolution;
    private ArrayList<StickerInfo> m_stickerArray;
    private ArrayList<CaptionInfo> m_captionArray;
    private ArrayList<CompoundCaptionInfo> m_compoundCaptionArray;
    private ArrayList<RecordAudioInfo> m_recordAudioArray;

    private ArrayList<ClipInfo> m_clipInfoArray;
    /**
     * 时间线滤镜- 编辑滤镜模块保存的滤镜数据
     */
    private VideoClipFxInfo m_videoClipFxInfo;
    //mimo
    private long mCurrentTimeLinePosition;
    private int mCaptionZVal;
    /*
    * 主题包ID
    * Theme package ID
    * */
    private String m_themeId;
    /*
     * 动画 数据结构
     * key - 对应clip de index
     * value - 对应clip上的fxId
     * 编辑这个数据结构的地方有 动画页面 编辑页面删除移动复制
     * */
    private ConcurrentHashMap<Integer,AnimationInfo> mAnimationFxMap = new ConcurrentHashMap<>();
    /*
    * 主题字幕片头
    * Theme title
    * */
    private String m_themeCptionTitle = "";
    /*
    * 主题字幕片尾
    *  Theme trailer;
    * */
    private String m_themeCptionTrailer = "";//

    private float m_musicVolume = VIDEOVOLUME_DEFAULTVALUE;
    private float m_originVideoVolume = VIDEOVOLUME_DEFAULTVALUE;
    private float m_recordVolume = VIDEOVOLUME_DEFAULTVALUE;
    /*
    * 默认值，不作比例适配
    * Default value, no scaling
    * */
    private int m_makeRatio = NvAsset.AspectRatio_NoFitRatio;//
    private List<MusicInfo> m_musicList = null;
    private WaterMarkData waterMarkData;

    /*
    * 转场列表
    * Transition list
    * */
    private ArrayList<TransitionInfo> mTransitionInfoArray = new ArrayList<>();

    public void setCaptionZVal(int captionZVal) {
        this.mCaptionZVal = captionZVal;
    }

    public int getCaptionZVal() {
        return mCaptionZVal;
    }

    public ArrayList<TransitionInfo> getTransitionInfoArray() {
        return mTransitionInfoArray;
    }

    public void setTransitionInfoArray(ArrayList<TransitionInfo> transitionInfoArray) {
        this.mTransitionInfoArray = transitionInfoArray;
    }

    public String getThemeCptionTitle() {
        return m_themeCptionTitle;
    }

    public void setThemeCptionTitle(String themeCptionTitle) {
        this.m_themeCptionTitle = themeCptionTitle;
    }

    public String getThemeCptionTrailer() {
        return m_themeCptionTrailer;
    }

    public void setThemeCptionTrailer(String themeCptionTrailer) {
        this.m_themeCptionTrailer = themeCptionTrailer;
    }

    public WaterMarkData getWaterMarkData() {
        return waterMarkData;
    }

    public void setWaterMarkData(WaterMarkData waterMarkData) {
        this.waterMarkData = waterMarkData;
    }

    public void cleanWaterMarkData() {
        this.waterMarkData = null;
    }

    public float getMusicVolume() {
        return m_musicVolume;
    }

    public void setMusicVolume(float musicVolume) {
        this.m_musicVolume = musicVolume;
    }

    public float getOriginVideoVolume() {
        return m_originVideoVolume;
    }

    public void setOriginVideoVolume(float originVideoVolume) {
        this.m_originVideoVolume = originVideoVolume;
    }

    public float getRecordVolume() {
        return m_recordVolume;
    }

    public void setRecordVolume(float recordVolume) {
        this.m_recordVolume = recordVolume;
    }

    public int getMakeRatio() {
        return m_makeRatio;
    }

    public void setMakeRatio(int makeRatio) {
        this.m_makeRatio = makeRatio;
    }

    public void setVideoResolution(NvsVideoResolution resolution) {
        m_videoResolution = resolution;
    }

    public NvsVideoResolution getVideoResolution() {
        return m_videoResolution;
    }

    public NvsVideoResolution cloneVideoResolution() {
        if (m_videoResolution == null)
            return null;

        NvsVideoResolution resolution = new NvsVideoResolution();
        resolution.imageWidth = m_videoResolution.imageWidth;
        resolution.imageHeight = m_videoResolution.imageHeight;
        return resolution;
    }

    public void setClipInfoData(ArrayList<ClipInfo> clipInfoArray) {
        this.m_clipInfoArray = clipInfoArray;
    }

    public ArrayList<ClipInfo> getClipInfoData() {
        return m_clipInfoArray;
    }

    public ArrayList<ClipInfo> cloneClipInfoData() {
        ArrayList<ClipInfo> newArrayList = new ArrayList<>();
        for (ClipInfo clipInfo : m_clipInfoArray) {
            ClipInfo newClipInfo = clipInfo.clone();
            newArrayList.add(newClipInfo);
        }
        return newArrayList;
    }

    public void resetClipTrimInfo() {
        for (int i = 0; i < m_clipInfoArray.size(); i++) {
            ClipInfo clipInfo = m_clipInfoArray.get(i);
            clipInfo.changeTrimIn(-1);
            clipInfo.changeTrimOut(-1);
        }
    }

    public void clear() {
        if (m_clipInfoArray != null) {
            m_clipInfoArray.clear();
        }

        if (mTransitionInfoArray != null) {
            mTransitionInfoArray.clear();
            mTransitionInfoArray = null;
        }

        if (m_captionArray != null) {
            m_captionArray.clear();
        }

        if (m_stickerArray != null) {
            m_stickerArray.clear();
        }

        if (m_recordAudioArray != null) {
            m_recordAudioArray.clear();
        }

        if (m_compoundCaptionArray != null) {
            m_compoundCaptionArray.clear();
        }

        if (m_musicList != null) {
            m_musicList.clear();
        }
        m_musicVolume = VIDEOVOLUME_DEFAULTVALUE;
        m_originVideoVolume = VIDEOVOLUME_DEFAULTVALUE;
        m_recordVolume = VIDEOVOLUME_DEFAULTVALUE;
        m_videoResolution = null;
        m_videoClipFxInfo = null;
        m_themeId = "";
        mAnimationFxMap.clear();
        cleanWaterMarkData();
    }

    public void addClip(ClipInfo clipInfo) {
        m_clipInfoArray.add(clipInfo);
    }

    public void removeClip(int index) {
        if (index < m_clipInfoArray.size()) {
            m_clipInfoArray.remove(index);
        }
    }

    public int getClipCount() {
        return m_clipInfoArray.size();
    }

    public void setMusicList(ArrayList<MusicInfo> musicInfos) {
        if (musicInfos == null) {
            if (m_musicList != null) {
                m_musicList.clear();
            }
        }
        m_musicList = musicInfos;
    }
    public void setCurSeekTimelinePos(long currentPosition) {
        mCurrentTimeLinePosition = currentPosition;
    }

    public long getCurSeekTimelinePos() {
        return mCurrentTimeLinePosition;
    }

    public List<MusicInfo> getMusicData() {
        return m_musicList;
    }

    public ArrayList<MusicInfo> cloneMusicData() {
        if (m_musicList == null)
            return null;

        ArrayList<MusicInfo> musicInfos = new ArrayList<>();
        for (MusicInfo musicInfo : m_musicList) {
            musicInfos.add(musicInfo.clone());
        }
        return musicInfos;
    }

    public void setVideoClipFxData(VideoClipFxInfo videoClipFxInfo) {
        m_videoClipFxInfo = videoClipFxInfo;
    }

    public VideoClipFxInfo getVideoClipFxData() {
        return m_videoClipFxInfo;
    }

    public VideoClipFxInfo cloneVideoClipFxData() {
        if (m_videoClipFxInfo == null)
            return null;

        VideoClipFxInfo videoClipFxInfo = new VideoClipFxInfo();
        videoClipFxInfo.setFxId(m_videoClipFxInfo.getFxId());
        videoClipFxInfo.setFxMode(m_videoClipFxInfo.getFxMode());
        return videoClipFxInfo;
    }

    public ArrayList<TransitionInfo> cloneTransitionsData() {
        if (mTransitionInfoArray == null) {
            return null;
        }
        ArrayList<TransitionInfo> cloneDataArray = new ArrayList<>();
        for (TransitionInfo transitionInfo : mTransitionInfoArray) {
            cloneDataArray.add(transitionInfo.copySelf());
        }
        return cloneDataArray;
    }

    public String getThemeData() {
        return m_themeId;
    }

    public void setThemeData(String themeData) {
        this.m_themeId = themeData;
    }

    public ConcurrentHashMap<Integer, AnimationInfo> getmAnimationFxMap() {
        return mAnimationFxMap;
    }

    public void setmAnimationFxMap(ConcurrentHashMap<Integer, AnimationInfo> mAnimationFxMap) {
        this.mAnimationFxMap = mAnimationFxMap;
    }

    public void addCaption(CaptionInfo caption) {
        m_captionArray.add(caption);
    }

    public void setCaptionData(ArrayList<CaptionInfo> captionArray) {
        m_captionArray = captionArray;
    }

    public ArrayList<CaptionInfo> getCaptionData() {
        return m_captionArray;
    }

    public ArrayList<CaptionInfo> cloneCaptionData() {
        ArrayList<CaptionInfo> newList = new ArrayList<>();
        for (CaptionInfo captionInfo : m_captionArray) {
            CaptionInfo newCaptionInfo = captionInfo.clone();
            newList.add(newCaptionInfo);
        }
        return newList;
    }

    public ArrayList<CompoundCaptionInfo> getCompoundCaptionArray() {
        return m_compoundCaptionArray;
    }

    public void setCompoundCaptionArray(ArrayList<CompoundCaptionInfo> compoundCaptionArray) {
        this.m_compoundCaptionArray = compoundCaptionArray;
    }

    public ArrayList<CompoundCaptionInfo> cloneCompoundCaptionData() {
        ArrayList<CompoundCaptionInfo> newList = new ArrayList<>();
        for (CompoundCaptionInfo captionInfo : m_compoundCaptionArray) {
            CompoundCaptionInfo newCaptionInfo = captionInfo.clone();
            newList.add(newCaptionInfo);
        }
        return newList;
    }

    public void setStickerData(ArrayList<StickerInfo> stickerArray) {
        m_stickerArray = stickerArray;
    }

    public ArrayList<StickerInfo> getStickerData() {
        return m_stickerArray;
    }

    public ArrayList<StickerInfo> cloneStickerData() {
        ArrayList<StickerInfo> newList = new ArrayList<>();
        for (StickerInfo stickerInfo : m_stickerArray) {
            StickerInfo newStickerInfo = stickerInfo.clone();
            newList.add(newStickerInfo);
        }
        return newList;
    }

    public ArrayList<RecordAudioInfo> getRecordAudioData() {
        return m_recordAudioArray;
    }

    public void setRecordAudioData(ArrayList<RecordAudioInfo> recordAudioArray) {
        this.m_recordAudioArray = recordAudioArray;
    }

    private TimelineData() {
        m_captionArray = new ArrayList<>();
        m_stickerArray = new ArrayList<>();
        m_clipInfoArray = new ArrayList<>();
        m_compoundCaptionArray = new ArrayList<>();
        m_videoResolution = new NvsVideoResolution();
    }

    public static TimelineData init() {
        if (m_timelineData == null) {
            synchronized (TimelineData.class) {
                if (m_timelineData == null) {
                    m_timelineData = new TimelineData();
                }
            }
        }
        return m_timelineData;
    }

    public static TimelineData instance() {
        if (m_timelineData == null) {
            m_timelineData = new TimelineData();
        }
        return m_timelineData;
    }

    /**
     * 转化为json串
     * @return
     */
    public static String toJson() {
        Gson gson = new Gson();
        return gson.toJson(m_timelineData);
    }

    /**
     * 将json串反序列化为TimelineData单例
     * @param jsonString
     * @return
     */
    public static TimelineData fromJson (String jsonString) {
        Gson gson = new Gson();
        m_timelineData = gson.fromJson(jsonString, TimelineData.class);
        return m_timelineData;
    }
}
