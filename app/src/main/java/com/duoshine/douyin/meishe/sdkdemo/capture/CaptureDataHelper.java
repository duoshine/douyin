package com.duoshine.douyin.meishe.sdkdemo.capture;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.duoshine.douyin.R;
import com.duoshine.douyin.meishe.sdkdemo.bean.RecordInfo;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.BeautyData;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.ComposeMakeup;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.CustomMakeup;
import com.duoshine.douyin.meishe.sdkdemo.bean.makeup.NullBeautyItem;
import com.duoshine.douyin.meishe.sdkdemo.edit.data.ParseJsonFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.duoshine.douyin.meishe.sdkdemo.utils.Constants.HUMAN_AI_TYPE_MS;


/**
 * @Class: com.meishe.sdkdemo.capture.CaptureDataHelper.java
 * @Time: 2019/3/22 0022 14:20
 * @author: mlj
 * @Description: 获取美颜美型数据帮助类;Get Beauty and Beauty Data Helper
 */
public class CaptureDataHelper {
    public static final String ASSETS_MAKEUP_PATH = "beauty/makeup";
    private static final String ASSETS_MAKEUP_COMPOSE_PATH = ASSETS_MAKEUP_PATH + "/compose";
    private static final String ASSETS_MAKEUP_COSTOM_PATH = ASSETS_MAKEUP_PATH + "/custom";
    private static final String ASSETS_MAKEUP_RECORD_NAME = "/record.json";

    private static final String SD_MAKEUP_COSTOM_PATH = "makeup/custom";
    private static final String SD_MAKEUP_COMPOSE_PATH = "makeup/compose";

    /**
     * @param context
     * @param beautyType
     * @return 美颜数据集合;Beauty Data Collection
     */
    public ArrayList<BeautyShapeDataItem> getBeautyDataListByType(Context context, int beautyType) {
        ArrayList<BeautyShapeDataItem> list = new ArrayList<>();
        if (beautyType == HUMAN_AI_TYPE_MS) {
            /*
             * 磨皮
             * strength
             * */
            BeautyShapeDataItem beauty_strength = new BeautyShapeDataItem();
            beauty_strength.name = context.getResources().getString(R.string.strength);
            beauty_strength.resId = R.drawable.beauty_strength_selector;
            beauty_strength.beautyShapeId = "Beauty Strength";
            beauty_strength.strength = 0.5d;
            beauty_strength.needDefaultStrength = true;
            list.add(beauty_strength);
            /*
             * 美白
             * whitening
             * */
            BeautyShapeDataItem beauty_whitening = new BeautyShapeDataItem();
            beauty_whitening.name = context.getResources().getString(R.string.whitening);
            beauty_whitening.resId = R.drawable.beauty_white_selector;
            beauty_whitening.beautyShapeId = "Beauty Whitening";
            beauty_whitening.strength = 0d;
            beauty_whitening.needDefaultStrength = true;
            list.add(beauty_whitening);
            /*
             * 红润
             * reddening
             * */
            BeautyShapeDataItem beauty_reddening = new BeautyShapeDataItem();
            beauty_reddening.name = context.getResources().getString(R.string.ruddy);
            beauty_reddening.resId = R.drawable.beauty_reddening_selector;
            beauty_reddening.beautyShapeId = "Beauty Reddening";
            beauty_reddening.strength = 0d;
            beauty_reddening.needDefaultStrength = true;
            list.add(beauty_reddening);
            /*
             * 校色
             * School color
             * */
            BeautyShapeDataItem adjustColor = new BeautyShapeDataItem();
            adjustColor.name = context.getResources().getString(R.string.correctionColor);
            adjustColor.resId = R.drawable.beauty_adjust_selector;
            adjustColor.setPath("assets:/beauty/971C84F9-4E05-441E-A724-17096B3D1CBD.2.videofx");
            list.add(adjustColor);
            /*
             * 锐度
             * sharpness
             * */
            BeautyShapeDataItem sharpen = new BeautyShapeDataItem();
            sharpen.name = context.getResources().getString(R.string.sharpness);
            sharpen.resId = R.drawable.beauty_sharpen_selector;
            sharpen.beautyShapeId = "Default Sharpen Enabled";
            list.add(sharpen);
        } else {
            /*
             * 磨皮
             * strength
             * */
            BeautyShapeDataItem beauty_strength = new BeautyShapeDataItem();
            beauty_strength.name = context.getResources().getString(R.string.strength);
            beauty_strength.resId = R.drawable.beauty_strength_selector;
            beauty_strength.beautyShapeId = "Strength";
            beauty_strength.strength = 0.5d;
            beauty_strength.needDefaultStrength = true;
            list.add(beauty_strength);
            /*
             * 美白
             * whitening
             * */
            BeautyShapeDataItem beauty_whitening = new BeautyShapeDataItem();
            beauty_whitening.name = context.getResources().getString(R.string.whitening);
            beauty_whitening.resId = R.drawable.beauty_white_selector;
            beauty_whitening.beautyShapeId = "Whitening";
            beauty_whitening.strength = 0.5d;
            beauty_whitening.needDefaultStrength = true;
            list.add(beauty_whitening);
            /*
             * 红润
             * reddening
             * */
            BeautyShapeDataItem beauty_reddening = new BeautyShapeDataItem();
            beauty_reddening.name = context.getResources().getString(R.string.ruddy);
            beauty_reddening.resId = R.drawable.beauty_reddening_selector;
            beauty_reddening.beautyShapeId = "Reddening";
            beauty_reddening.strength = 0.5d;
            beauty_reddening.needDefaultStrength = true;
            list.add(beauty_reddening);
            /*
             * 校色
             * School color
             * */
            BeautyShapeDataItem adjustColor = new BeautyShapeDataItem();
            adjustColor.name = context.getResources().getString(R.string.correctionColor);
            adjustColor.resId = R.drawable.beauty_adjust_selector;
            adjustColor.setPath("assets:/beauty/971C84F9-4E05-441E-A724-17096B3D1CBD.2.videofx");
            list.add(adjustColor);
            /*
             * 锐度
             * sharpness
             * */
            BeautyShapeDataItem sharpen = new BeautyShapeDataItem();
            sharpen.name = context.getResources().getString(R.string.sharpness);
            sharpen.resId = R.drawable.beauty_sharpen_selector;
            sharpen.beautyShapeId = "Default Sharpen Enabled";
            list.add(sharpen);
        }
        return list;
    }

    /**
     * 获取美型类型的集合，
     *
     * @return
     */
    public static ArrayList<BeautyShapeDataKindItem> getBeautyShapeKindList(Context context) {
        ArrayList<BeautyShapeDataKindItem> list = new ArrayList<>();
        BeautyShapeDataKindItem normal = new BeautyShapeDataKindItem();
        normal.setName(context.getResources().getString(R.string.beauty_facetype_first));
        normal.setType(BeautyShapeDataKindItem.Type.NORMAL);
        normal.setIcon(R.mipmap.beauty_facetype_kind_1);
        list.add(normal);

        BeautyShapeDataKindItem newBuild = new BeautyShapeDataKindItem();
        newBuild.setName(context.getResources().getString(R.string.beauty_facetype_second));
        newBuild.setType(BeautyShapeDataKindItem.Type.NEW_BUILD);
        newBuild.setIcon(R.mipmap.beauty_facetype_kind_2);
        list.add(newBuild);
        return list;
    }

    /**
     * @param context
     * @param type    BeautyShapeDataKindItem.Type.NORMAL
     *                美型瘦脸60，大眼70，瘦鼻50
     *                BeautyShapeDataKindItem.Type.NEW_BUILD
     *                瘦脸（瘦）70，眼部（大眼）35，眼角（拉长）20，瘦鼻（变窄）70 额头（抬高）40,嘴部20，小脸15
     * @return 美型数据集合；Beauty data collection
     */
    public static ArrayList<BeautyShapeDataItem> getShapeDataList(Context context, int type) {
        ArrayList<BeautyShapeDataItem> list = new ArrayList<>();
        /*
         * 瘦脸
         * Thin face
         * */
        BeautyShapeDataItem cheek_thinning = new BeautyShapeDataItem();
        cheek_thinning.name = context.getResources().getString(R.string.cheek_thinning);
        cheek_thinning.resId = R.drawable.beauty_thin_face_selector;
        cheek_thinning.beautyShapeId = "Face Size Warp Degree";
        if (type == BeautyShapeDataKindItem.Type.NORMAL) {
            cheek_thinning.strength = -0.6d;
        } else {
            cheek_thinning.strength = -0.7d;
        }
        cheek_thinning.defaultValue = cheek_thinning.strength;
        list.add(cheek_thinning);
        // 大眼
        /*
         * 大眼
         * Eye enlarging
         * */
        BeautyShapeDataItem eye_enlarging = new BeautyShapeDataItem();
        eye_enlarging.name = context.getResources().getString(R.string.eye_enlarging);
        eye_enlarging.resId = R.drawable.beauty_big_eye_selector;
        eye_enlarging.type = "Default";
        eye_enlarging.beautyShapeId = "Eye Size Warp Degree";
        if (type == BeautyShapeDataKindItem.Type.NORMAL) {
            eye_enlarging.strength = 0.7d;
        } else {
            eye_enlarging.strength = 0.35d;
        }
        eye_enlarging.defaultValue = eye_enlarging.strength;
        list.add(eye_enlarging);

        /*
         * 下巴
         * Chin
         * */
        BeautyShapeDataItem intensity_chin = new BeautyShapeDataItem();
        intensity_chin.name = context.getResources().getString(R.string.intensity_chin);
        intensity_chin.resId = R.drawable.beauty_chin_selector;
        intensity_chin.beautyShapeId = "Chin Length Warp Degree";
        intensity_chin.type = "Custom";
        list.add(intensity_chin);
        // 小脸
        /*
         * 小脸
         * face length
         * */
        BeautyShapeDataItem intensity_xiaolian = new BeautyShapeDataItem();
        intensity_xiaolian.name = context.getResources().getString(R.string.face_small);
        intensity_xiaolian.resId = R.drawable.beauty_little_face_selector;
        intensity_xiaolian.beautyShapeId = "Face Length Warp Degree";
        intensity_xiaolian.type = "Custom";
        if (type == BeautyShapeDataKindItem.Type.NEW_BUILD) {
            intensity_xiaolian.strength = -0.15d;
            intensity_xiaolian.defaultValue = intensity_xiaolian.strength;
        }
        list.add(intensity_xiaolian);
        /*
         * 窄脸
         * face width
         * */
        BeautyShapeDataItem intensity_zhailian = new BeautyShapeDataItem();
        intensity_zhailian.name = context.getResources().getString(R.string.face_thin);
        intensity_zhailian.resId = R.drawable.beauty_narrow_face_selector;
        intensity_zhailian.beautyShapeId = "Face Width Warp Degree";
        intensity_zhailian.type = "Custom";
        list.add(intensity_zhailian);
        /*
         * 额头
         * forehead
         * */
        BeautyShapeDataItem intensity_forehead = new BeautyShapeDataItem();
        intensity_forehead.name = context.getResources().getString(R.string.intensity_forehead);
        intensity_forehead.resId = R.drawable.beauty_forehead_selector;
        intensity_forehead.type = "Custom";
        intensity_forehead.beautyShapeId = "Forehead Height Warp Degree";
        if (type == BeautyShapeDataKindItem.Type.NEW_BUILD) {
            intensity_forehead.strength = 0.40d;
            intensity_forehead.defaultValue = intensity_forehead.strength;
        }
        list.add(intensity_forehead);
        // 瘦鼻
        /*
         * 瘦鼻
         * Nose width
         * */
        BeautyShapeDataItem intensity_nose = new BeautyShapeDataItem();
        intensity_nose.name = context.getResources().getString(R.string.intensity_nose);
        intensity_nose.resId = R.drawable.beauty_thin_nose_selector;
        intensity_nose.type = "Custom";
        intensity_nose.beautyShapeId = "Nose Width Warp Degree";
        if (type == BeautyShapeDataKindItem.Type.NORMAL) {
            intensity_nose.strength = -0.5d;
        } else {
            intensity_nose.strength = -0.7d;
        }
        intensity_nose.defaultValue = intensity_nose.strength;
        list.add(intensity_nose);


        /*
         * 嘴形
         * Mouth size
         * */
        BeautyShapeDataItem intensity_mouth = new BeautyShapeDataItem();
        intensity_mouth.name = context.getResources().getString(R.string.intensity_mouth);
        intensity_mouth.resId = R.drawable.beauty_mouth_selector;
        intensity_mouth.type = "Custom";
        intensity_mouth.beautyShapeId = "Mouth Size Warp Degree";
        if (type == BeautyShapeDataKindItem.Type.NEW_BUILD) {
            intensity_mouth.strength = 0.20d;
            intensity_forehead.defaultValue = intensity_forehead.strength;
        }
        list.add(intensity_mouth);
        //策略1 ，也就是美型1才会显示下边这三项
        if (type == BeautyShapeDataKindItem.Type.NORMAL) {

            /*
             * 长鼻
             * Nose length
             * */
            BeautyShapeDataItem changbi_nose = new BeautyShapeDataItem();
            changbi_nose.name = context.getResources().getString(R.string.nose_long);
            changbi_nose.resId = R.drawable.beauty_long_nose_selector;
            changbi_nose.type = "Custom";
            changbi_nose.beautyShapeId = "Nose Length Warp Degree";
            list.add(changbi_nose);
            /*
             * 眼角
             * Eye corner
             * */
            BeautyShapeDataItem yanjiao = new BeautyShapeDataItem();
            yanjiao.name = context.getResources().getString(R.string.eye_corner);
            yanjiao.resId = R.drawable.beauty_eye_corner_selector;
            yanjiao.type = "Custom";
            yanjiao.beautyShapeId = "Eye Corner Stretch Degree";
            //策略2不显示眼角了，所以这不需要了
           /* if(type == BeautyShapeDataKindItem.Type.NEW_BUILD){
                yanjiao.strength = 0.2d;
                yanjiao.defaultValue = yanjiao.strength;
            }*/
            list.add(yanjiao);
            /*
             * 嘴角
             * Mouth corner
             * */
            BeautyShapeDataItem intensity_zuijiao = new BeautyShapeDataItem();
            intensity_zuijiao.name = context.getResources().getString(R.string.mouse_corner);
            intensity_zuijiao.resId = R.drawable.beauty_mouth_corner_selector;
            intensity_zuijiao.type = "Custom";
            intensity_zuijiao.beautyShapeId = "Mouth Corner Lift Degree";
            list.add(intensity_zuijiao);
        }
        return list;
    }


    public ArrayList<BeautyData> getCustomMakeupDataList(Context context) {
        ArrayList<BeautyData> result = getMakeupDataFromAssets(context, ASSETS_MAKEUP_COSTOM_PATH, new ParseFunction<CustomMakeup>() {
            @Override
            public CustomMakeup parseData(String data) {
                return ParseJsonFile.fromJson(data, CustomMakeup.class);
            }
        }, false);
        ArrayList<BeautyData> makeupDataFromSd = getMakeupDataFromSd(context, SD_MAKEUP_COSTOM_PATH, new ParseFunction<CustomMakeup>() {
            @Override
            public CustomMakeup parseData(String data) {
                CustomMakeup customMakeup = ParseJsonFile.fromJson(data, CustomMakeup.class);
                customMakeup.setIsBuildIn(false);
                return customMakeup;
            }
        });
        if (makeupDataFromSd != null && makeupDataFromSd.size() > 0) {
            result.addAll(makeupDataFromSd);
        }
        return result;
    }


    public ArrayList<BeautyData> getComposeMakeupDataList(Context context) {
        ArrayList<BeautyData> result = getMakeupDataFromAssets(context, ASSETS_MAKEUP_COMPOSE_PATH, new ParseFunction<ComposeMakeup>() {
            @Override
            public ComposeMakeup parseData(String data) {
                ComposeMakeup makeup = ParseJsonFile.fromJson(data, ComposeMakeup.class);
                return makeup;
            }
        }, true);
        ArrayList<BeautyData> makeupDataFromSd = getMakeupDataFromSd(context, SD_MAKEUP_COMPOSE_PATH, new ParseFunction<ComposeMakeup>() {
            @Override
            public ComposeMakeup parseData(String data) {
                ComposeMakeup makeup = ParseJsonFile.fromJson(data, ComposeMakeup.class);
                makeup.setIsBuildIn(false);
                return makeup;
            }
        });
        if (makeupDataFromSd != null && makeupDataFromSd.size() > 0) {
            result.addAll(makeupDataFromSd);
        }
        return result;
    }

    private ArrayList<BeautyData> getMakeupDataFromAssets(Context context, String folderPath, ParseFunction<? extends BeautyData> function, boolean needNull) {
        String jsonInfoFile = ParseJsonFile.readAssetJsonFile(context,
                folderPath + ASSETS_MAKEUP_RECORD_NAME);
        if (jsonInfoFile == null) {
            return null;
        }
        RecordInfo recordInfo = ParseJsonFile.fromJson(jsonInfoFile, RecordInfo.class);
        if (recordInfo == null) {
            return null;
        }
        List<RecordInfo.JsonInfo> jsonList = recordInfo.getJsonList();
        if (jsonList == null || jsonList.isEmpty()) {
            return null;
        }
        ArrayList<BeautyData> list = new ArrayList<>();
        if (needNull) {
            BeautyData nullItem = new NullBeautyItem();
            list.add(nullItem);
        }
        for (RecordInfo.JsonInfo jsonInfo : jsonList) {
            String childPath = folderPath + File.separator + jsonInfo.jsonPath;
            String jsInfo = childPath + File.separator + "info.json";
            String makeupInfoJson = ParseJsonFile.readAssetJsonFile(context,
                    jsInfo);
            if (TextUtils.isEmpty(makeupInfoJson)) {
                return null;
            }
            BeautyData item = function.parseData(makeupInfoJson);
            item.setFolderPath(childPath);
            list.add(item);
        }
        return list;
    }


    private ArrayList<BeautyData> getMakeupDataFromSd(Context context, String folderPath, ParseFunction<? extends BeautyData> function) {
        String sdPath = getSDPath();
        folderPath = sdPath + File.separator + folderPath;
        File file = new File(folderPath);
        if (!file.exists() && !file.mkdirs()) {
            return null;
        }
        String[] list = file.list();
        if (list == null || list.length <= 0) {
            return null;
        }
        String parentPath = file.getAbsolutePath();
        ArrayList<BeautyData> result = new ArrayList<>();
        for (int index = 0; index < list.length; index++) {
            String childPath = parentPath + File.separator + list[index];
            String jsInfo = childPath + File.separator + "info.json";
            String makeupInfoJson = ParseJsonFile.readSDJsonFile(context,
                    jsInfo);
            if (TextUtils.isEmpty(makeupInfoJson)) {
                return null;
            }
            BeautyData item = function.parseData(makeupInfoJson);
            item.setFolderPath(childPath);
            result.add(item);
        }
        return result;
    }

    private static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

    public interface ParseFunction<T> {
        T parseData(String data);
    }
}
