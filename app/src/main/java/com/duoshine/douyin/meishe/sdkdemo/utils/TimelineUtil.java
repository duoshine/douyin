package com.duoshine.douyin.meishe.sdkdemo.utils;

import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;

import com.duoshine.douyin.meishe.sdkdemo.edit.data.FilterItem;
import com.duoshine.douyin.meishe.sdkdemo.edit.watermark.WaterMarkData;
import com.duoshine.douyin.meishe.sdkdemo.edit.watermark.WaterMarkUtil;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.AnimationInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.CaptionInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.ClipInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.CompoundCaptionInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.KeyFrameInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.MusicInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.RecordAudioInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.StickerInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TimelineData;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.TransitionInfo;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.VideoClipFxInfo;
import com.meicam.sdk.NvsAudioClip;
import com.meicam.sdk.NvsAudioResolution;
import com.meicam.sdk.NvsAudioTrack;
import com.meicam.sdk.NvsColor;
import com.meicam.sdk.NvsRational;
import com.meicam.sdk.NvsSize;
import com.meicam.sdk.NvsStreamingContext;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineAnimatedSticker;
import com.meicam.sdk.NvsTimelineCaption;
import com.meicam.sdk.NvsTimelineCompoundCaption;
import com.meicam.sdk.NvsTimelineVideoFx;
import com.meicam.sdk.NvsVideoClip;
import com.meicam.sdk.NvsVideoFx;
import com.meicam.sdk.NvsVideoResolution;
import com.meicam.sdk.NvsVideoTrack;
import com.meicam.sdk.NvsVideoTransition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.duoshine.douyin.meishe.sdkdemo.edit.watermark.WaterMarkConstant.WATERMARK_DYNAMICS_FXNAME;
import static com.duoshine.douyin.meishe.sdkdemo.utils.Constants.*;


/**
 * Created by admin on 2018/5/29.
 */

public class TimelineUtil {
    private static String TAG = "TimelineUtil";
    public final static long TIME_BASE = 1000000;

    /*
     * 主编辑页面时间线API
     * Main edit page timeline API
     * */
    public static NvsTimeline createTimeline() {
        NvsTimeline timeline = newTimeline(TimelineData.instance().getVideoResolution());
        if (timeline == null) {
            Log.e(TAG, "failed to create timeline");
            return null;
        }
        if (!buildVideoTrack(timeline)) {
            return timeline;
        }

        /*
         * 音乐轨道
         * Music track
         * */
        timeline.appendAudioTrack();
        /*
         * 录音轨道
         * Recording track
         * */
        timeline.appendAudioTrack();

        setTimelineData(timeline);

        return timeline;
    }

    /*
     * 片段编辑页面时间线API
     * Clip Edit Page Timeline API
     * */
    public static NvsTimeline createSingleClipTimeline(ClipInfo clipInfo, boolean isTrimClip) {
        NvsTimeline timeline = newTimeline(TimelineData.instance().getVideoResolution());
        if (timeline == null) {
            Log.e(TAG, "failed to create timeline");
            return null;
        }
        buildSingleClipVideoTrack(timeline, clipInfo, isTrimClip);
        return timeline;
    }

    /*
     * 片段编辑页面时间线扩展API
     * Clip Edit Page Timeline Extension API
     * */
    public static NvsTimeline createSingleClipTimelineExt(NvsVideoResolution videoEditRes, String filePath) {
        NvsTimeline timeline = newTimeline(videoEditRes);
        if (timeline == null) {
            Log.e(TAG, "failed to create timeline");
            return null;
        }
        buildSingleClipVideoTrackExt(timeline, filePath);
        return timeline;
    }

    public static boolean buildSingleClipVideoTrack(NvsTimeline timeline, ClipInfo clipInfo, boolean isTrimClip) {
        if (timeline == null || clipInfo == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.appendVideoTrack();
        if (videoTrack == null) {
            Log.e(TAG, "failed to append video track");
            return false;
        }
        addVideoClip(videoTrack, clipInfo, isTrimClip);
        return true;
    }

    public static boolean buildSingleClipVideoTrackExt(NvsTimeline timeline, String filePath) {
        if (timeline == null || filePath == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.appendVideoTrack();
        if (videoTrack == null) {
            Log.e(TAG, "failed to append video track");
            return false;
        }
        NvsVideoClip videoClip = videoTrack.appendClip(filePath);
        if (videoClip == null) {
            Log.e(TAG, "failed to append video clip");
            return false;
        }
        videoClip.changeTrimOutPoint(8000000, true);
        return true;
    }

    public static void setTimelineData(NvsTimeline timeline) {
        if (timeline == null)
            return;
        /*
         * 此处注意是clone一份音乐数据，因为添加主题的接口会把音乐数据删掉
         * Note here is to clone a piece of music data, because the interface of adding a theme will delete the music data
         * */
        ArrayList<MusicInfo> musicInfoClone = TimelineData.instance().cloneMusicData();
        String themeId = TimelineData.instance().getThemeData();
        applyTheme(timeline, themeId);

        if (musicInfoClone != null) {
            TimelineData.instance().setMusicList(musicInfoClone);
            buildTimelineMusic(timeline, musicInfoClone);
        }

        VideoClipFxInfo videoClipFxData = TimelineData.instance().getVideoClipFxData();
        buildTimelineFilter(timeline, videoClipFxData);
        ArrayList<TransitionInfo> transitionInfoArray = TimelineData.instance().getTransitionInfoArray();
        setTransition(timeline, transitionInfoArray);
        ArrayList<StickerInfo> stickerArray = TimelineData.instance().getStickerData();
        setSticker(timeline, stickerArray);

        ArrayList<CaptionInfo> captionArray = TimelineData.instance().getCaptionData();
        setCaption(timeline, captionArray);

        //compound caption
        ArrayList<CompoundCaptionInfo> compoundCaptionArray = TimelineData.instance().getCompoundCaptionArray();
        setCompoundCaption(timeline, compoundCaptionArray);

        ArrayList<RecordAudioInfo> recordArray = TimelineData.instance().getRecordAudioData();
        buildTimelineRecordAudio(timeline, recordArray);

        WaterMarkData waterMarkData = TimelineData.instance().getWaterMarkData();
        WaterMarkUtil.setWaterMark(timeline, waterMarkData);
        //设置动画
        Map<Integer, AnimationInfo> fxMap = TimelineData.instance().getmAnimationFxMap();
        TimelineUtil.buildTimelineAnimation(timeline, fxMap);
    }

    private static void appendFilterFx(NvsVideoClip clip, VideoClipFxInfo videoClipFxData) {
        if (videoClipFxData == null) {
            return;
        }
        String name = videoClipFxData.getFxId();
        if (TextUtils.isEmpty(name)) {
            return;
        }
        int mode = videoClipFxData.getFxMode();
        float fxIntensity = videoClipFxData.getFxIntensity();
        // 滤镜关键帧强度
        HashMap<Long, Double> keyFrameInfoMap = videoClipFxData.getKeyFrameInfoMap();
        if (mode == FilterItem.FILTERMODE_BUILTIN) { //内建特效
            NvsVideoFx builtInFx;
            if (videoClipFxData.getIsCartoon()) {
                builtInFx = clip.appendBuiltinFx("Cartoon");
                if (builtInFx != null) {
                    builtInFx.setBooleanVal("Stroke Only", videoClipFxData.getStrokenOnly());
                    builtInFx.setBooleanVal("Grayscale", videoClipFxData.getGrayScale());
                } else {
                    Logger.e(TAG, "Failed to append builtInFx-->" + "Cartoon");
                }
            } else {
                builtInFx = clip.appendBuiltinFx(name);
            }
            if (builtInFx != null) {
                builtInFx.setFilterIntensity(fxIntensity);
            } else {
                Logger.e(TAG, "Failed to append builtInFx-->" + name);
            }
            if (keyFrameInfoMap != null) {
                Set<Map.Entry<Long, Double>> entries = keyFrameInfoMap.entrySet();
                for (Map.Entry<Long, Double> entry : entries) {
                    if (builtInFx != null) {
                        builtInFx.setFloatValAtTime("Filter Intensity", entry.getValue(), entry.getKey());
                    } else {
                        Logger.e(TAG, "the fx is null when set keyFrameValue");
                    }
                }
            }
        } else {
            /*
             * 添加包裹特效
             * Add package effects
             * */
            NvsVideoFx packagedFx = clip.appendPackagedFx(name);
            if (packagedFx != null) {
                packagedFx.setFilterIntensity(fxIntensity);
            } else {
                Logger.e(TAG, "Failed to append packagedFx-->" + name);
            }
            if (keyFrameInfoMap != null) {
                Set<Map.Entry<Long, Double>> entries = keyFrameInfoMap.entrySet();
                for (Map.Entry<Long, Double> entry : entries) {
                    if (packagedFx != null) {
                        packagedFx.setFloatValAtTime("Filter Intensity", entry.getValue(), entry.getKey());
                    } else {
                        Logger.e(TAG, "the fx is null when set keyFrameValue");
                    }
                }
            }
        }
    }

    public static boolean removeTimeline(NvsTimeline timeline) {
        if (timeline == null)
            return false;

        NvsStreamingContext context = NvsStreamingContext.getInstance();
        if (context == null)
            return false;

        return context.removeTimeline(timeline);
    }

    public static boolean buildVideoTrack(NvsTimeline timeline) {
        if (timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.appendVideoTrack();
        if (videoTrack == null) {
            Log.e(TAG, "failed to append video track");
            return false;
        }

        ArrayList<ClipInfo> videoClipArray = TimelineData.instance().getClipInfoData();
        for (int i = 0; i < videoClipArray.size(); i++) {
            ClipInfo clipInfo = videoClipArray.get(i);
            addVideoClip(videoTrack, clipInfo, true);
        }
        float videoVolume = TimelineData.instance().getOriginVideoVolume();
        videoTrack.setVolumeGain(videoVolume, videoVolume);

        return true;
    }

    public static boolean reBuildVideoTrack(NvsTimeline timeline) {
        if (timeline == null) {
            return false;
        }
        int videoTrackCount = timeline.videoTrackCount();
        NvsVideoTrack videoTrack = videoTrackCount == 0 ? timeline.appendVideoTrack() : timeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            Log.e(TAG, "failed to append video track");
            return false;
        }
        videoTrack.removeAllClips();
        ArrayList<ClipInfo> videoClipArray = TimelineData.instance().getClipInfoData();
        for (int i = 0; i < videoClipArray.size(); i++) {
            ClipInfo clipInfo = videoClipArray.get(i);
            addVideoClip(videoTrack, clipInfo, true);
        }
        setTimelineData(timeline);
        float videoVolume = TimelineData.instance().getOriginVideoVolume();
        videoTrack.setVolumeGain(videoVolume, videoVolume);

        return true;
    }

    private static void addVideoClip(NvsVideoTrack videoTrack, ClipInfo clipInfo, boolean isTrimClip) {
        if (videoTrack == null || clipInfo == null)
            return;
        String filePath = clipInfo.getFilePath();
        NvsVideoClip videoClip = videoTrack.appendClip(filePath);
        if (videoClip == null) {
            Log.e(TAG, "failed to append video clip");
            return;
        }

        boolean blurFlag = ParameterSettingValues.instance().isUseBackgroudBlur();
        if (blurFlag) {
            videoClip.setSourceBackgroundMode(NvsVideoClip.ClIP_BACKGROUNDMODE_BLUR);
        }

        float brightVal = clipInfo.getBrightnessVal();
        float contrastVal = clipInfo.getContrastVal();
        float saturationVal = clipInfo.getSaturationVal();
        float vignette = clipInfo.getVignetteVal();
        float sharpen = clipInfo.getSharpenVal();
        if (brightVal >= 0 || contrastVal >= 0 || saturationVal >= 0) {
            NvsVideoFx videoFxColor = videoClip.appendBuiltinFx(Constants.FX_COLOR_PROPERTY);
            if (videoFxColor != null) {
                if (brightVal >= 0)
                    videoFxColor.setFloatVal(Constants.FX_COLOR_PROPERTY_BRIGHTNESS, brightVal);
                if (contrastVal >= 0)
                    videoFxColor.setFloatVal(Constants.FX_COLOR_PROPERTY_CONTRAST, contrastVal);
                if (saturationVal >= 0)
                    videoFxColor.setFloatVal(Constants.FX_COLOR_PROPERTY_SATURATION, saturationVal);
            }
        }
        if (vignette >= 0) {
            NvsVideoFx vignetteVideoFx = videoClip.appendBuiltinFx(Constants.FX_VIGNETTE);
            vignetteVideoFx.setFloatVal(Constants.FX_VIGNETTE_DEGREE, vignette);
        }
        if (sharpen >= 0) {
            NvsVideoFx sharpenVideoFx = videoClip.appendBuiltinFx(Constants.FX_SHARPEN);
            sharpenVideoFx.setFloatVal(Constants.FX_SHARPEN_AMOUNT, sharpen);
        }
        int videoType = videoClip.getVideoType();
        if (videoType == NvsVideoClip.VIDEO_CLIP_TYPE_IMAGE) {
            /*
             * 当前片段是图片
             * The current clip is a picture
             * */
            long trimIn = videoClip.getTrimIn();
            long trimOut = clipInfo.getTrimOut();
            if (trimOut > 0 && trimOut > trimIn) {
                videoClip.changeTrimOutPoint(trimOut, true);
            }
            int imgDisplayMode = clipInfo.getImgDispalyMode();
            if (imgDisplayMode == Constants.EDIT_MODE_PHOTO_AREA_DISPLAY) {
                /*
                 * 区域显示
                 * Area display
                 * */
                videoClip.setImageMotionMode(NvsVideoClip.IMAGE_CLIP_MOTIONMMODE_ROI);
                RectF normalStartRectF = clipInfo.getNormalStartROI();
                RectF normalEndRectF = clipInfo.getNormalEndROI();
                if (normalStartRectF != null && normalEndRectF != null) {
                    videoClip.setImageMotionROI(normalStartRectF, normalEndRectF);
                }
            } else {
                /*
                 * 全图显示
                 * Full image display
                 * */
                videoClip.setImageMotionMode(NvsVideoClip.CLIP_MOTIONMODE_LETTERBOX_ZOOMIN);
            }

            boolean isOpenMove = clipInfo.isOpenPhotoMove();
            videoClip.setImageMotionAnimationEnabled(isOpenMove);
        } else {
            /*
             * 当前片段是视频
             * The current clip is a video
             * */
            float volumeGain = clipInfo.getVolume();
            videoClip.setVolumeGain(volumeGain, volumeGain);
            float pan = clipInfo.getPan();
            float scan = clipInfo.getScan();
            videoClip.setPanAndScan(pan, scan);
            float speed = clipInfo.getSpeed();
            if (speed > 0) {
                videoClip.changeSpeed(speed);
            }
            videoClip.setExtraVideoRotation(clipInfo.getRotateAngle());
            int scaleX = clipInfo.getScaleX();
            int scaleY = clipInfo.getScaleY();
            if (scaleX >= -1 || scaleY >= -1) {
                NvsVideoFx videoFxTransform = videoClip.appendBuiltinFx(Constants.FX_TRANSFORM_2D);
                if (videoFxTransform != null) {
                    if (scaleX >= -1)
                        videoFxTransform.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_X, scaleX);
                    if (scaleY >= -1)
                        videoFxTransform.setFloatVal(Constants.FX_TRANSFORM_2D_SCALE_Y, scaleY);
                }
            }

            if (!isTrimClip) {
                /*
                 * 如果当前是裁剪页面，不裁剪片段
                 * If the page is currently cropped, the clip is not cropped
                 * */
                return;
            }
            long trimIn = clipInfo.getTrimIn();
            long trimOut = clipInfo.getTrimOut();
            if (trimIn > 0) {
                videoClip.changeTrimInPoint(trimIn, true);
            }
            if (trimOut > 0 && trimOut > trimIn) {
                videoClip.changeTrimOutPoint(trimOut, true);
            }
        }
    }

    public static boolean buildTimelineFilter(NvsTimeline timeline, VideoClipFxInfo videoClipFxData) {
        if (timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return false;
        }

        ArrayList<ClipInfo> clipInfos = TimelineData.instance().getClipInfoData();

        int videoClipCount = videoTrack.getClipCount();
        for (int i = 0; i < videoClipCount; i++) {
            NvsVideoClip clip = videoTrack.getClipByIndex(i);
            if (clip == null) {
                continue;
            }

            removeAllVideoFx(clip);
            VideoClipFxInfo clipFxData = null;
            String clipFilPath = clip.getFilePath();
            boolean isSrcVideoAsset = false;
            for (ClipInfo clipInfo : clipInfos) {
                isSrcVideoAsset = false;
                /*
                 * 过滤掉主题中自带片头或者片尾的视频
                 * Filter out videos that have their own title or trailer in the theme
                 * */

                String videoFilePath = clipInfo.getFilePath();
                if (clipFilPath.equals(videoFilePath)) {
                    isSrcVideoAsset = true;
                    clipFxData = clipInfo.getVideoClipFxInfo();
                    break;
                }
            }
            if (!isSrcVideoAsset) {
                continue;
            }
            /*
             * 添加片段滤镜特效
             * Add clip filter effects
             * */
            appendFilterFx(clip, clipFxData);
            /*
             * 添加TimeLine特效
             * Add TimeLine effects
             * */
            appendFilterFx(clip, videoClipFxData);
        }
        return true;
    }

    /**
     * Add effect for single clip.
     * Besides the clip effect,the effects in TimeLine is added by this method.
     * Remarkably, the method is used for showing effects during changing them and the {@code #clipFxData} is usually temporary.
     * Final building {@link #buildTimelineFilter(NvsTimeline, VideoClipFxInfo)}
     *
     * @param timeline   TimeLine data
     * @param clipInfo   Clip data
     * @param clipFxData Clip effect data.
     * @return
     */
    public static boolean buildSingleClipFilter(NvsTimeline timeline, ClipInfo clipInfo, VideoClipFxInfo clipFxData) {
        if (timeline == null || clipInfo == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return false;
        }

        for (int i = 0; i < videoTrack.getClipCount(); i++) {
            NvsVideoClip clip = videoTrack.getClipByIndex(i);
            if (clip == null) {
                continue;
            }

            String clipFilPath = clip.getFilePath();
            String videoFilePath = clipInfo.getFilePath();
            if (!clipFilPath.equals(videoFilePath)) {
                continue;
            }
            removeAllVideoFx(clip);

            /*
             * 添加片段滤镜特效
             * Add clip filter effects
             * */
            appendFilterFx(clip, clipFxData);
            // 添加TimeLine特效
            appendFilterFx(clip, TimelineData.instance().getVideoClipFxData());
        }
        return true;
    }

    public static boolean applyTheme(NvsTimeline timeline, String themeId) {
        if (timeline == null)
            return false;

        timeline.removeCurrentTheme();
        if (themeId == null || themeId.isEmpty())
            return false;

        /*
         * 设置主题片头和片尾
         * Set theme title and trailer
         * */
        String themeCaptionTitle = TimelineData.instance().getThemeCptionTitle();
        if (!themeCaptionTitle.isEmpty()) {
            timeline.setThemeTitleCaptionText(themeCaptionTitle);
        }
        String themeCaptionTrailer = TimelineData.instance().getThemeCptionTrailer();
        if (!themeCaptionTrailer.isEmpty()) {
            timeline.setThemeTrailerCaptionText(themeCaptionTrailer);
        }

        if (!timeline.applyTheme(themeId)) {
            Log.e(TAG, "failed to apply theme");
            return false;
        }

        timeline.setThemeMusicVolumeGain(1.0f, 1.0f);

        /*
         * 应用主题之后，要把已经应用的背景音乐去掉
         * After applying the theme, remove the background music that has been applied
         * */
        TimelineData.instance().setMusicList(null);
        TimelineUtil.buildTimelineMusic(timeline, null);
        return true;
    }

    private static boolean removeAllVideoFx(NvsVideoClip videoClip) {
        if (videoClip == null)
            return false;

        int fxCount = videoClip.getFxCount();
        for (int i = 0; i < fxCount; i++) {
            NvsVideoFx fx = videoClip.getFxByIndex(i);
            if (fx == null)
                continue;

            String name = fx.getBuiltinVideoFxName();
            Log.e("===>", "fx name: " + name);
            if (name.equals(Constants.FX_COLOR_PROPERTY) || name.equals(Constants.FX_VIGNETTE) ||
                    name.equals(Constants.FX_SHARPEN) || name.equals(Constants.FX_TRANSFORM_2D)) {
                continue;
            }
            videoClip.removeFx(i);
            i--;
        }
        return true;
    }

    /**
     * 添加全部转场特效
     * <p>
     * Add all transition effects
     *
     * @param timeline
     * @param transitionInfos
     * @return
     */
    public static boolean setTransition(NvsTimeline timeline, ArrayList<TransitionInfo> transitionInfos) {
        if (timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return false;
        }

        if (transitionInfos == null) {
            return false;
        }

        int videoClipCount = videoTrack.getClipCount();
        if (videoClipCount <= 1) {
            return false;
        }
        /*
         * 添加全部转场特效
         * Add all transition effects
         * */
        for (int i = 0, length = transitionInfos.size(); i < length; i++) {
            TransitionInfo transitionInfo = transitionInfos.get(i);
            NvsVideoTransition nvsVideoTransition = null;
            if (TextUtils.equals(transitionInfo.getTransitionId(), "theme")) {
                nvsVideoTransition = videoTrack.setPackagedTransition(i, transitionInfo.getTransitionId());
            } else {
                if (transitionInfo.getTransitionMode() == TransitionInfo.TRANSITIONMODE_BUILTIN) {
                    nvsVideoTransition = videoTrack.setBuiltinTransition(i, transitionInfo.getTransitionId());
                } else {
                    nvsVideoTransition = videoTrack.setPackagedTransition(i, transitionInfo.getTransitionId());
                }
            }
            if (nvsVideoTransition != null) {
                // FIXME: 2019/10/17 0017 临时修改转场
                nvsVideoTransition.setVideoTransitionDuration(transitionInfo.getTransitionInterval(), NvsVideoTransition.VIDEO_TRANSITION_DURATION_MATCH_MODE_NONE);
                transitionInfo.setVideoTransition(nvsVideoTransition);
//                transitionInfo.setTransitionInterval(nvsVideoTransition.getVideoTransitionDuration( ));
            }
        }

        return true;
    }

    /**
     * 指定索引位置添加转场特效
     * Add transition effects at specified index positions
     *
     * @param timeline
     * @param transitionInfo
     * @param index
     * @return
     */
    public static boolean setTransition(NvsTimeline timeline, TransitionInfo transitionInfo, int index) {
        if (timeline == null) {
            return false;
        }

        NvsVideoTrack videoTrack = timeline.getVideoTrackByIndex(0);
        if (videoTrack == null) {
            return false;
        }

        if (transitionInfo == null)
            return false;

        int videoClipCount = videoTrack.getClipCount();
        if (videoClipCount <= 1) {
            return false;
        }
        // FIXME: 2019/10/17 0017 临时修改转场
        NvsVideoTransition nvsVideoTransition = null;
        if (TextUtils.equals(transitionInfo.getTransitionId(), "theme")) {
            videoTrack.setBuiltinTransition(index, "");
//          videoTrack.setPackagedTransition(index, null);
            nvsVideoTransition = videoTrack.setPackagedTransition(index, transitionInfo.getTransitionId());
        } else {
            if (transitionInfo.getTransitionMode() == TransitionInfo.TRANSITIONMODE_BUILTIN) {
                nvsVideoTransition = videoTrack.setBuiltinTransition(index, transitionInfo.getTransitionId());
            } else {
                nvsVideoTransition = videoTrack.setPackagedTransition(index, transitionInfo.getTransitionId());
            }
        }
        if (nvsVideoTransition != null) {
            transitionInfo.setVideoTransition(nvsVideoTransition);
            nvsVideoTransition.setVideoTransitionDuration(transitionInfo.getTransitionInterval(), NvsVideoTransition.VIDEO_TRANSITION_DURATION_MATCH_MODE_NONE);
            // FIXME: 2019/10/17 0017 临时修改转场
            // transitionInfo.setTransitionInterval(nvsVideoTransition.getVideoTransitionDuration( ));
        }
        return true;
    }

    public static boolean buildTimelineMusic(NvsTimeline timeline, List<MusicInfo> musicInfos) {
        if (timeline == null) {
            return false;
        }
        NvsAudioTrack audioTrack = timeline.getAudioTrackByIndex(0);
        if (audioTrack == null) {
            return false;
        }
        if (musicInfos == null || musicInfos.isEmpty()) {
            audioTrack.removeAllClips();

            /*
             * 去掉音乐之后，要把已经应用的主题中的音乐还原
             * After removing the music, you need to restore the music in the theme you have applied
             * */
            String pre_theme_id = TimelineData.instance().getThemeData();
            if (pre_theme_id != null && !pre_theme_id.isEmpty()) {
                timeline.setThemeMusicVolumeGain(1.0f, 1.0f);
            }
            return false;
        }
        audioTrack.removeAllClips();
        for (MusicInfo oneMusic : musicInfos) {
            if (oneMusic == null) {
                continue;
            }
            NvsAudioClip audioClip = audioTrack.addClip(oneMusic.getFilePath(), oneMusic.getInPoint(), oneMusic.getTrimIn(), oneMusic.getTrimOut());
            if (audioClip != null) {
                audioClip.setFadeInDuration(oneMusic.getFadeDuration());
                if (oneMusic.getExtraMusic() <= 0 && oneMusic.getExtraMusicLeft() <= 0) {
                    audioClip.setFadeOutDuration(oneMusic.getFadeDuration());
                }
            }
            if (oneMusic.getExtraMusic() > 0) {
                for (int i = 0; i < oneMusic.getExtraMusic(); ++i) {
                    NvsAudioClip extra_clip = audioTrack.addClip(oneMusic.getFilePath(),
                            oneMusic.getOriginalOutPoint() + i * (oneMusic.getOriginalOutPoint() - oneMusic.getOriginalInPoint()),
                            oneMusic.getOriginalTrimIn(), oneMusic.getOriginalTrimOut());
                    if (extra_clip != null) {
                        extra_clip.setAttachment(Constants.MUSIC_EXTRA_AUDIOCLIP, oneMusic.getInPoint());
                        if (i == oneMusic.getExtraMusic() - 1 && oneMusic.getExtraMusicLeft() <= 0) {
                            extra_clip.setAttachment(Constants.MUSIC_EXTRA_LAST_AUDIOCLIP, oneMusic.getInPoint());
                            extra_clip.setFadeOutDuration(oneMusic.getFadeDuration());
                        }
                    }
                }
            }
            if (oneMusic.getExtraMusicLeft() > 0) {
                NvsAudioClip extra_clip = audioTrack.addClip(oneMusic.getFilePath(),
                        oneMusic.getOriginalOutPoint() + oneMusic.getExtraMusic() * (oneMusic.getOriginalOutPoint() - oneMusic.getOriginalInPoint()),
                        oneMusic.getOriginalTrimIn(),
                        oneMusic.getOriginalTrimIn() + oneMusic.getExtraMusicLeft());
                if (extra_clip != null) {
                    extra_clip.setAttachment(Constants.MUSIC_EXTRA_AUDIOCLIP, oneMusic.getInPoint());
                    extra_clip.setAttachment(Constants.MUSIC_EXTRA_LAST_AUDIOCLIP, oneMusic.getInPoint());
                    extra_clip.setFadeOutDuration(oneMusic.getFadeDuration());
                }
            }
        }
        float audioVolume = TimelineData.instance().getMusicVolume();
        audioTrack.setVolumeGain(audioVolume, audioVolume);

        /*
         * 去掉音乐之后，要把已经应用的主题中的音乐还原
         * After removing the music, you need to restore the music in the theme you have applied
         * */
        String pre_theme_id = TimelineData.instance().getThemeData();
        if (pre_theme_id != null && !pre_theme_id.isEmpty()) {
            timeline.setThemeMusicVolumeGain(0, 0);
        }
        return true;
    }

    public static void buildTimelineRecordAudio(NvsTimeline timeline, ArrayList<RecordAudioInfo> recordAudioInfos) {
        if (timeline == null) {
            return;
        }
        NvsAudioTrack audioTrack = timeline.getAudioTrackByIndex(1);
        if (audioTrack != null) {
            audioTrack.removeAllClips();
            if (recordAudioInfos != null) {
                for (int i = 0; i < recordAudioInfos.size(); ++i) {
                    RecordAudioInfo recordAudioInfo = recordAudioInfos.get(i);
                    if (recordAudioInfo == null) {
                        continue;
                    }
                    NvsAudioClip audioClip = audioTrack.addClip(recordAudioInfo.getPath(), recordAudioInfo.getInPoint(), recordAudioInfo.getTrimIn(),
                            recordAudioInfo.getOutPoint() - recordAudioInfo.getInPoint() + recordAudioInfo.getTrimIn());
                    if (audioClip != null) {
                        audioClip.setVolumeGain(recordAudioInfo.getVolume(), recordAudioInfo.getVolume());
                        if (recordAudioInfo.getFxID() != null && !recordAudioInfo.getFxID().equals(Constants.NO_FX)) {
                            audioClip.appendFx(recordAudioInfo.getFxID());
                        }
                    }
                }
            }
            float audioVolume = TimelineData.instance().getRecordVolume();
            audioTrack.setVolumeGain(audioVolume, audioVolume);
        }
    }

    public static boolean setSticker(NvsTimeline timeline, ArrayList<StickerInfo> stickerArray) {
        if (timeline == null)
            return false;

        NvsTimelineAnimatedSticker deleteSticker = timeline.getFirstAnimatedSticker();
        while (deleteSticker != null) {
            deleteSticker = timeline.removeAnimatedSticker(deleteSticker);
        }

        for (StickerInfo sticker : stickerArray) {
            long duration = sticker.getOutPoint() - sticker.getInPoint();
            boolean isCutsomSticker = sticker.isCustomSticker();
            NvsTimelineAnimatedSticker newSticker = isCutsomSticker ?
                    timeline.addCustomAnimatedSticker(sticker.getInPoint(), duration, sticker.getId(), sticker.getCustomImagePath())
                    : timeline.addAnimatedSticker(sticker.getInPoint(), duration, sticker.getId());
            if (newSticker == null)
                continue;
            newSticker.setZValue(sticker.getAnimateStickerZVal());
            newSticker.setHorizontalFlip(sticker.isHorizFlip());
            // 判断是否应用关键帧信息
            Map<Long, KeyFrameInfo> keyFrameInfoHashMap = sticker.getKeyFrameInfoHashMap();
            if (keyFrameInfoHashMap.isEmpty()) {
                PointF translation = sticker.getTranslation();
                float scaleFactor = sticker.getScaleFactor();
                float rotation = sticker.getRotation();
                newSticker.setScale(scaleFactor);
                newSticker.setRotationZ(rotation);
                newSticker.setTranslation(translation);
            } else {
                Set<Map.Entry<Long, KeyFrameInfo>> entries = keyFrameInfoHashMap.entrySet();
                for (Map.Entry<Long, KeyFrameInfo> entry : entries) {
                    newSticker.setCurrentKeyFrameTime(entry.getKey() - newSticker.getInPoint());
                    KeyFrameInfo keyFrameInfo = entry.getValue();
                    newSticker.setRotationZ(keyFrameInfo.getRotationZ());
                    newSticker.setScale(keyFrameInfo.getScaleX());
                    newSticker.setTranslation(keyFrameInfo.getTranslation());
                }
            }
            float volumeGain = sticker.getVolumeGain();
            newSticker.setVolumeGain(volumeGain, volumeGain);
        }
        return true;
    }

    public static boolean setCaption(NvsTimeline timeline, ArrayList<CaptionInfo> captionArray) {
        if (timeline == null)
            return false;

        NvsTimelineCaption deleteCaption = timeline.getFirstCaption();
        while (deleteCaption != null) {
            int capCategory = deleteCaption.getCategory();
            Logger.e(TAG, "capCategory = " + capCategory);
            int roleTheme = deleteCaption.getRoleInTheme();
            if (capCategory == NvsTimelineCaption.THEME_CATEGORY
                    && roleTheme != NvsTimelineCaption.ROLE_IN_THEME_GENERAL) {//主题字幕不作删除
                deleteCaption = timeline.getNextCaption(deleteCaption);
            } else {
                deleteCaption = timeline.removeCaption(deleteCaption);
            }
        }

        NvsTimelineCaption newCaption;
        for (CaptionInfo caption : captionArray) {
            long duration = caption.getOutPoint() - caption.getInPoint();
            if (caption.isTraditionCaption()) {
                //传统字幕
                newCaption = timeline.addCaption(caption.getText(), caption.getInPoint(),
                        duration, null);
            } else {
                //拼装字幕
                newCaption = timeline.addModularCaption(caption.getText(), caption.getInPoint(),
                        duration);
            }
            updateCaptionAttribute(newCaption, caption);
        }
        return true;
    }

    //add compound caption
    public static boolean setCompoundCaption(NvsTimeline timeline, ArrayList<CompoundCaptionInfo> captionArray) {
        if (timeline == null) {
            return false;
        }

        NvsTimelineCompoundCaption deleteCaption = timeline.getFirstCompoundCaption();
        while (deleteCaption != null) {
            deleteCaption = timeline.removeCompoundCaption(deleteCaption);
        }

        for (CompoundCaptionInfo caption : captionArray) {
            long duration = caption.getOutPoint() - caption.getInPoint();
            NvsTimelineCompoundCaption newCaption = timeline.addCompoundCaption(caption.getInPoint(),
                    duration, caption.getCaptionStyleUuid());
            updateCompoundCaptionAttribute(newCaption, caption);
        }
        return true;
    }

    //update compound caption attribute
    private static void updateCompoundCaptionAttribute(NvsTimelineCompoundCaption newCaption, CompoundCaptionInfo caption) {
        if (newCaption == null || caption == null)
            return;

        ArrayList<CompoundCaptionInfo.CompoundCaptionAttr> captionAttrList = caption.getCaptionAttributeList();
        int captionCount = newCaption.getCaptionCount();
        for (int index = 0; index < captionCount; ++index) {
            CompoundCaptionInfo.CompoundCaptionAttr captionAttr = captionAttrList.get(index);
            if (captionAttr == null) {
                continue;
            }
            NvsColor textColor = ColorUtil.colorStringtoNvsColor(captionAttr.getCaptionColor());
            if (textColor != null) {
                newCaption.setTextColor(index, textColor);
            }

            String fontName = captionAttr.getCaptionFontName();
            if (!TextUtils.isEmpty(fontName)) {
                newCaption.setFontFamily(index, fontName);
            }
            String captionText = captionAttr.getCaptionText();
            if (!TextUtils.isEmpty(captionText)) {
                newCaption.setText(index, captionText);
            }
        }

        /*
         * 放缩字幕
         * Shrink captions
         * */
        float scaleFactorX = caption.getScaleFactorX();
        float scaleFactorY = caption.getScaleFactorY();
        newCaption.setScaleX(scaleFactorX);
        newCaption.setScaleY(scaleFactorY);
        float rotation = caption.getRotation();
        /*
         * 旋转字幕
         * Spin subtitles
         * */
        newCaption.setRotationZ(rotation);
        newCaption.setZValue(caption.getCaptionZVal());
        PointF translation = caption.getTranslation();
        if (translation != null) {
            newCaption.setCaptionTranslation(translation);
        }
    }

    private static void updateCaptionAttribute(NvsTimelineCaption newCaption, CaptionInfo caption) {
        if (newCaption == null) {
            return;
        }
        if (caption == null) {
            return;
        }

        /*
         * 字幕StyleUuid需要首先设置，后面设置的字幕属性才会生效，因为字幕样式里面可能自带偏移，缩放，旋转等属性，最后设置会覆盖前面的设置的。
         *
         * The subtitle StyleUuid needs to be set first, and the subtitle properties set later will take effect,
         * because the subtitle style may have its own offset, zoom, rotation and other properties.
         * The last setting will override the previous settings.
         * */
        if (caption.isTraditionCaption()) {
            //传统字幕
            String styleUuid = caption.getCaptionStyleUuid();
            newCaption.applyCaptionStyle(styleUuid);
        } else {
            //拼装字幕
            newCaption.applyModularCaptionRenderer(caption.getRichWordUuid());
            newCaption.applyModularCaptionContext(caption.getBubbleUuid());
            if (!TextUtils.isEmpty(caption.getCombinationAnimationUuid())) {
                //优先使用组合动画，和入场、出场动画互斥
                newCaption.applyModularCaptionAnimation(caption.getCombinationAnimationUuid());
                if (caption.getCombinationAnimationDuration() >= 0) {
                    newCaption.setModularCaptionAnimationPeroid(caption.getCombinationAnimationDuration());
                }
            } else {
                newCaption.applyModularCaptionInAnimation(caption.getMarchInAnimationUuid());
                int maxDuration = (int) ((newCaption.getOutPoint() - newCaption.getInPoint()) / 1000);
                //Log.d("lhz","in,duration="+caption.getMarchInAnimationDuration()+"**out duration="+caption.getMarchOutAnimationDuration());
                if (caption.getMarchInAnimationDuration() >= 0) {
                    if (maxDuration - caption.getMarchInAnimationDuration() < 500) {
                        //如果设置的入动画时间后，剩余的默认时间小于500毫秒（出入动画默认时长500ms，不论设置不设置出动画）
                        newCaption.setModularCaptionOutAnimationDuration(maxDuration - caption.getMarchInAnimationDuration());
                    }
                    //先后顺序不可乱，因为出入动画默认时长500ms，不论设置不设置出动画
                    newCaption.setModularCaptionInAnimationDuration(caption.getMarchInAnimationDuration());
                }
                newCaption.applyModularCaptionOutAnimation(caption.getMarchOutAnimationUuid());
                if (caption.getMarchOutAnimationDuration() >= 0) {
                    if (maxDuration - caption.getMarchOutAnimationDuration() < 500) {
                        //如果设置的出动画时间后，剩余的默认时间小于500毫秒（出入动画默认时长500ms，不论设置不设置出动画）
                        newCaption.setModularCaptionInAnimationDuration(maxDuration - caption.getMarchOutAnimationDuration());
                    }
                    //先后顺序不可乱，//先后顺序不可乱，因为出入动画默认时长500ms，不论设置不设置出动画
                    newCaption.setModularCaptionOutAnimationDuration(caption.getMarchOutAnimationDuration());
                }
            }
        }
        int alignVal = caption.getAlignVal();
        if (alignVal >= 0) {
            newCaption.setTextAlignment(alignVal);
        }
        //此处注意，默认不能设置setVerticalLayout，因为一旦设置过后，应用不同的字幕样式（横、竖）就无法自动适应。
        if(CaptionInfo.O_VERTICAL == caption.getOrientationType()){
            newCaption.setVerticalLayout(true);
        }else if(CaptionInfo.O_HORIZONTAL == caption.getOrientationType()){
            newCaption.setVerticalLayout(false);
        }

        int userColorFlag = caption.getUsedColorFlag();
        if (userColorFlag == CaptionInfo.ATTRIBUTE_USED_FLAG) {
            NvsColor textColor = ColorUtil.colorStringtoNvsColor(caption.getCaptionColor());
            if (textColor != null) {
                textColor.a = caption.getCaptionColorAlpha() / 100.0f;
                newCaption.setTextColor(textColor);
            }
        }
        //需要设置为选中的背景色
        int userBackgroundFlag = caption.getUsedBackgroundFlag();
        if (userBackgroundFlag == CaptionInfo.ATTRIBUTE_USED_FLAG) {
            NvsColor backgroundColor = ColorUtil.colorStringtoNvsColor(caption.getCaptionBackground());
            if (backgroundColor != null) {
                backgroundColor.a = caption.getCaptionBackgroundAlpha() / 100.0f;
                newCaption.setBackgroundColor(backgroundColor);
            }

        }
        //设置圆角
        int userBackgroundRadiusFlag = caption.getUsedBackgroundRadiusFlag();
        if (userBackgroundRadiusFlag == CaptionInfo.ATTRIBUTE_USED_FLAG) {

            float radius = caption.getCaptionBackgroundRadius();
            newCaption.setBackgroundRadius(radius);
        }

        int usedScaleFlag = caption.getUsedScaleRotationFlag();
        if (usedScaleFlag == CaptionInfo.ATTRIBUTE_USED_FLAG) {
            /*
             * 放缩字幕
             * Shrink captions
             * */
            float scaleFactorX = caption.getScaleFactorX();
            float scaleFactorY = caption.getScaleFactorY();
            newCaption.setScaleX(scaleFactorX);
            newCaption.setScaleY(scaleFactorY);
            float rotation = caption.getRotation();
            /*
             * 旋转字幕
             * Spin subtitles
             * */
            newCaption.setRotationZ(rotation);
        }

        newCaption.setZValue(caption.getCaptionZVal());
        int usedOutlineFlag = caption.getUsedOutlineFlag();
        if (usedOutlineFlag == CaptionInfo.ATTRIBUTE_USED_FLAG) {
            boolean hasOutline = caption.isHasOutline();
            newCaption.setDrawOutline(hasOutline);
            if (hasOutline) {
                NvsColor outlineColor = ColorUtil.colorStringtoNvsColor(caption.getOutlineColor());
                if (outlineColor != null) {
                    outlineColor.a = caption.getOutlineColorAlpha() / 100.0f;
                    newCaption.setOutlineColor(outlineColor);
                    newCaption.setOutlineWidth(caption.getOutlineWidth());
                }
            }
        }

        String fontPath = caption.getCaptionFont();
        if (!fontPath.isEmpty()) {
            newCaption.setFontByFilePath(fontPath);
        }

        int usedBold = caption.getUsedIsBoldFlag();
        if (usedBold == CaptionInfo.ATTRIBUTE_USED_FLAG) {
            boolean isBold = caption.isBold();
            newCaption.setBold(isBold);
        }

        int usedItalic = caption.getUsedIsItalicFlag();
        if (usedItalic == CaptionInfo.ATTRIBUTE_USED_FLAG) {
            boolean isItalic = caption.isItalic();
            newCaption.setItalic(isItalic);
        }
        int usedShadow = caption.getUsedShadowFlag();
        if (usedShadow == CaptionInfo.ATTRIBUTE_USED_FLAG) {
            boolean isShadow = caption.isShadow();
            newCaption.setDrawShadow(isShadow);
            if (isShadow) {
                PointF offset = new PointF(7, -7);
                NvsColor shadowColor = new NvsColor(0, 0, 0, 0.5f);
                /*
                 * 字幕阴影偏移量
                 * Subtitle shadow offset
                 * */
                newCaption.setShadowOffset(offset);
                /*
                 * 字幕阴影颜色
                 * Subtitle shadow color
                 * */
                newCaption.setShadowColor(shadowColor);
            }
        }


        //        float fontSize = caption.getCaptionSize();
//        if(fontSize >= 0) {
//            newCaption.setFontSize(fontSize);
//        }
        int usedTranslationFlag = caption.getUsedTranslationFlag();
        if (usedTranslationFlag == CaptionInfo.ATTRIBUTE_USED_FLAG) {
            PointF translation = caption.getTranslation();
            if (translation != null) {
                newCaption.setCaptionTranslation(translation);
            }
        }

        /*
         * 应用字符间距
         * Apply character spacing
         * */
        int usedLetterSpacingFlag = caption.getUsedLetterSpacingFlag();
        if (usedLetterSpacingFlag == CaptionInfo.ATTRIBUTE_USED_FLAG) {
            float letterSpacing = caption.getLetterSpacing();
            newCaption.setLetterSpacing(letterSpacing);
            float lineSpacing = caption.getLineSpacing();
            newCaption.setLineSpacing(lineSpacing);
        }

        if (caption.isTraditionCaption()) {
            Map<Long, KeyFrameInfo> keyFrameInfoMap = caption.getKeyFrameInfo();
            if (keyFrameInfoMap.isEmpty()) {
                newCaption.setRotationZ(caption.getRotation());
                newCaption.setCaptionTranslation(caption.getTranslation());
                newCaption.setScaleX(caption.getScaleFactorX());
                newCaption.setScaleY(caption.getScaleFactorY());
            } else {
                Set<Long> keySet = keyFrameInfoMap.keySet();
                for (long currentTime : keySet) {
                    KeyFrameInfo keyFrameInfo = keyFrameInfoMap.get(currentTime);
                    long duration = currentTime - newCaption.getInPoint();
                    newCaption.removeKeyframeAtTime(TRANS_X, duration);
                    newCaption.removeKeyframeAtTime(TRANS_Y, duration);
                    newCaption.removeKeyframeAtTime(SCALE_X, duration);
                    newCaption.removeKeyframeAtTime(SCALE_Y, duration);
                    newCaption.removeKeyframeAtTime(ROTATION_Z, duration);

                    newCaption.setCurrentKeyFrameTime(duration);
                    newCaption.setScaleX(keyFrameInfo.getScaleX());
                    newCaption.setScaleY(keyFrameInfo.getScaleY());
                    newCaption.setCaptionTranslation(keyFrameInfo.getTranslation());
                    newCaption.setRotationZ(keyFrameInfo.getRotationZ());

                }
            }
        }

    }

    public static NvsTimeline newTimeline(NvsVideoResolution videoResolution) {
        NvsStreamingContext context = NvsStreamingContext.getInstance();
        if (context == null) {
            Log.e(TAG, "failed to get streamingContext");
            return null;
        }

        NvsVideoResolution videoEditRes = videoResolution;
        videoEditRes.imagePAR = new NvsRational(1, 1);
        NvsRational videoFps = new NvsRational(30, 1);

        NvsAudioResolution audioEditRes = new NvsAudioResolution();
        audioEditRes.sampleRate = 44100;
        audioEditRes.channelCount = 2;

        NvsTimeline timeline = context.createTimeline(videoEditRes, videoFps, audioEditRes);
        return timeline;
    }

    public static NvsSize getTimelineSize(NvsTimeline timeline) {
        NvsSize size = new NvsSize(0, 0);
        if (timeline != null) {
            NvsVideoResolution resolution = timeline.getVideoRes();
            size.width = resolution.imageWidth;
            size.height = resolution.imageHeight;
            return size;
        }
        return null;
    }

    public static void checkAndDeleteExitFX(NvsTimeline mTimeline) {
        NvsTimelineVideoFx nvsTimelineVideoFx = mTimeline.getFirstTimelineVideoFx();
        while (nvsTimelineVideoFx != null) {
            String name = nvsTimelineVideoFx.getBuiltinTimelineVideoFxName();
            if (name.equals(WATERMARK_DYNAMICS_FXNAME)) {
                mTimeline.removeTimelineVideoFx(nvsTimelineVideoFx);
                break;
            } else {
                nvsTimelineVideoFx = mTimeline.getNextTimelineVideoFx(nvsTimelineVideoFx);
            }
        }
    }

    /**
     * 构建时间线上的clip的动画特效
     *
     * @param mTimeline
     * @param animationMap
     */
    public static void buildTimelineAnimation(NvsTimeline mTimeline, Map<Integer, AnimationInfo> animationMap) {
        if (null == animationMap || null == mTimeline) {
            return;
        }
        NvsVideoTrack nvsVideoTrack = mTimeline.getVideoTrackByIndex(0);
        if (nvsVideoTrack == null) {
            Log.i(TAG, "timeline get video track is null");
            return;
        }
        Set<Integer> keySet = animationMap.keySet();
        if (null == keySet || keySet.size() == 0) {
            return;
        }
        for (Integer clipPosition : keySet) {
            NvsVideoClip nvsVideoClip = nvsVideoTrack.getClipByIndex(clipPosition);
            if (nvsVideoClip == null) {
                Log.i(TAG, "timeline get video clip is null");
                return;
            }
            AnimationInfo animationInfo = animationMap.get(clipPosition);
            nvsVideoClip.enablePropertyVideoFx(true);
            NvsVideoFx mPositionerVideoFx = nvsVideoClip.getPropertyVideoFx();
            if (mPositionerVideoFx == null) {
                return;
            }
            if (null == animationInfo) {
                return;
            }
            long in = animationInfo.getmAnimationIn();
            long out = animationInfo.getmAnimationOut();
            mPositionerVideoFx.setStringVal("Post Package Id", animationInfo.getmPackageId());
            mPositionerVideoFx.setBooleanVal("Is Post Storyboard 3D", false);
            //设置锯齿
            mPositionerVideoFx.setBooleanVal("Enable MutliSample", true);

           /* //设置背景是否旋转
            mPositionerVideoFx.setBooleanVal("Enable Background Rotation", true);

            //模糊背景
            mPositionerVideoFx.setMenuVal("Background Mode", "Blur");
            mPositionerVideoFx.setFloatVal("Background Blur Radius", 40);*/


            Log.e(TAG, "---in:" + in + "   out:" + out);
            mPositionerVideoFx.setFloatVal("Package Effect In", in);
            mPositionerVideoFx.setFloatVal("Package Effect Out", out);
        }


    }
}
