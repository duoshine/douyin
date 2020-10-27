package com.duoshine.douyin.meishe.sdkdemo.utils;

/**
 * Created by ms on 2018/5/29.
 */

public class MediaConstant {
    /*
    * 媒体类型
    * media type
    * */
    public static final String MEDIA_TYPE = "media_type";
    /*
    * 多选限制
    * Multiple selection restrictions
    * */
    public static final String LIMIT_COUNT_MAX = "limitMediaCountMax";
    public static final String LIMIT_COUNT_MIN = "limitMediaCountMin";

    /*
    * 音乐和视频类型
    * Music and video types
    * */
    public static final int ALL_MEDIA = 0;
    public static final int VIDEO = 1;
    public static final int IMAGE = 2;
    public static final int[] MEDIATYPECOUNT = {ALL_MEDIA, VIDEO, IMAGE};
    public static final String KEY_CLICK_TYPE = "clickType";
    public static final int TYPE_ITEMCLICK_SINGLE = 0;
    public static final int TYPE_ITEMCLICK_MULTIPLE = 1;


    public static final String SINGLE_PICTURE_PATH = "picturePath";


}
