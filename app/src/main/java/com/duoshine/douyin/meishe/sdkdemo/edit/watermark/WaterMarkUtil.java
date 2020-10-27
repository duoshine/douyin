package com.duoshine.douyin.meishe.sdkdemo.edit.watermark;

import com.duoshine.douyin.meishe.sdkdemo.utils.PathUtils;
import com.meicam.sdk.NvsTimeline;
import com.meicam.sdk.NvsTimelineVideoFx;

import static com.duoshine.douyin.meishe.sdkdemo.edit.watermark.WaterMarkConstant.ASSETS_WATERMARK_PICTURE_PATH;
import static com.duoshine.douyin.meishe.sdkdemo.edit.watermark.WaterMarkConstant.DEFAULT_WATERMARK_PICTURE;
import static com.duoshine.douyin.meishe.sdkdemo.edit.watermark.WaterMarkConstant.WATERMARK_DYNAMICS_FXNAME;


/**
 * Created by CaoZhiChao on 2018/10/25 13:52
 */
public class WaterMarkUtil {
    public static void setWaterMark(NvsTimeline mTimeline, WaterMarkData waterMarkData){
        if (waterMarkData == null || mTimeline == null){
            return;
        }
        int type = waterMarkData.getWaterMarkItemData().getItemWaterMarkType();
        int pictureW = waterMarkData.getPicWidth();
        int pictureH = waterMarkData.getPicHeight();
        String waterMarkPath = waterMarkData.getWaterMarkItemData().getWaterMarkpath();
        int marginX = waterMarkData.getExcursionX();
        int marginY = waterMarkData.getExcursionY();
        if (type == WaterMarkConstant.WATERMARKTYPE_STATIC){
            mTimeline.addWatermark(waterMarkPath, pictureW,pictureH,1, NvsTimeline.NvsTimelineWatermarkPosition_TopRight , marginX, marginY);
        }else {
            mTimeline.deleteWatermark();
            String pictureName = waterMarkData.getWaterMarkItemData().getItemPicturePath();
            String dir = ASSETS_WATERMARK_PICTURE_PATH;
            if (pictureName.contains(DEFAULT_WATERMARK_PICTURE)){
                dir = PathUtils.getWatermarkCafDirectoryDir();
            }
            int sceneWidth = waterMarkData.getPointOfLiveWindow().x;
            int sceneHeight =waterMarkData.getPointOfLiveWindow().y;
            float transX = waterMarkData.getTransX();
            float transY = waterMarkData.getTransY();
            setDynamicWaterMark(mTimeline,sceneWidth,sceneHeight,waterMarkPath,pictureW,pictureH,dir,transX,transY,1);
        }
    }
    /**
     * 动态水印的默认位置是中心位置
     * The default position of the dynamic watermark is the center position
     * @param sceneWidth liveWindow宽度；liveWindow width
     * @param sceneHeight liveWindow高度；liveWindow height
     * @param fileName 文件名称，例如: 123.caf；File name, for example: 123.caf
     * @param width 动态水印宽度；Dynamic watermark width
     * @param height 动态水印高度；Dynamic watermark height
     * @param dir 文件路径；file path
     * @param transX 偏移量；Offset
     * @param transY 偏移量；Offset
     * @param scale 缩放值 ，默认为1；Scale value, default is 1
     */
    public static NvsTimelineVideoFx setDynamicWaterMark(NvsTimeline nvsTimeline, int sceneWidth, int sceneHeight, String fileName,
                                                         int width, int height, String dir, float transX, float transY, float scale){
        boolean isRepeat = true;
        int during = 2000;
        NvsTimelineVideoFx story = nvsTimeline.addBuiltinTimelineVideoFx(0, nvsTimeline.getDuration(), WATERMARK_DYNAMICS_FXNAME);
        String descString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>        " +
                "<storyboard sceneWidth=\"" + sceneWidth + "\" sceneHeight=\"" + sceneHeight + "\">           " +
                "<track source=\"" + fileName + "\" width=\"" + width + "\" height=\"" + height + "\" clipStart=\"0\" clipDuration=\""+during+"\" repeat=\""+isRepeat+"\">" +
                "<effect name=\"transform\">" +
                "<param name=\"opacity\" value=\"1\"/>" +
                "<param name=\"transX\" value=\"0\"/>" +
                "<param name=\"transY\" value=\"0\"/>" +
                "</effect></track></storyboard>";
        story.setStringVal("Resource Dir", dir);
        story.setStringVal("Description String", descString);
        story.setBooleanVal("Is Animated Sticker", true);
        /*
        * 缩放
        * zoom
        * */
        story.setFloatVal("Sticker Scale", scale);
        /*
        * 平移
        * translation
        * */
        story.setFloatVal("Sticker TransX", transX);
        story.setFloatVal("Sticker TransY", -transY);
        return story;
    }
}
