package com.duoshine.douyin.meishe.sdkdemo.utils;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by CaoZhiChao on 2018/11/29 13:51
 */

public class BuildHelper {
    private static final String TAG = BuildHelper.class.getSimpleName();

    /**
     * Build class所有的字段属性
     * Build.BOARD : Z91
     * Build.BOOTLOADER : unknown
     * Build.BRAND : FaDi
     * Build.CPU_ABI : arm64-v8a
     * Build.CPU_ABI2 :
     * Build.DEVICE : Z91
     * Build.DISPLAY : TEST_FaDi_Z91_S100_20180108
     * Build.FINGERPRINT : FaDi/Z91/Z91:7.1.1/N6F26Q/1515397384:user/release-keys
     * Build.HARDWARE : mt6739
     * Build.HOST : 69959bbb90c6
     * Build.ID : N6F26Q
     * Build.IS_DEBUGGABLE : true
     * Build.IS_EMULATOR : false
     * Build.MANUFACTURER : FaDi
     * Build.MODEL : Z91
     * Build.PERMISSIONS_REVIEW_REQUIRED : false
     * Build.PRODUCT : Z91
     * Build.RADIO : unknown
     * Build.SERIAL : 0123456789ABCDEF
     * Build.SUPPORTED_32_BIT_ABIS : [Ljava.lang.String;@305cf5e
     * Build.SUPPORTED_64_BIT_ABIS : [Ljava.lang.String;@f5c1f3f
     * Build.SUPPORTED_ABIS : [Ljava.lang.String;@578b00c
     * Build.TAG : Build
     * Build.TAGS : release-keys
     * Build.TIME : 1515397382000
     * Build.TYPE : user
     * Build.UNKNOWN : unknown
     * Build.USER : FaDi
     * Build.VERSION.ACTIVE_CODENAMES : [Ljava.lang.String;@f4ecd55
     * Build.VERSION.ALL_CODENAMES : [Ljava.lang.String;@bdb836a
     * Build.VERSION.BASE_OS :
     * Build.VERSION.CODENAME : REL
     * Build.VERSION.INCREMENTAL : 1515397384
     * Build.VERSION.PREVIEW_SDK_INT : 0
     * Build.VERSION.RELEASE : 7.1.1
     * Build.VERSION.RESOURCES_SDK_INT : 25
     * Build.VERSION.SDK : 25
     * Build.VERSION.SDK_INT : 25
     * Build.VERSION.SECURITY_PATCH : 2017-11-05
     */
    public static void getAllBuildInformation() {
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Log.w(TAG, "Build." + field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }

        Field[] fieldsVersion = Build.VERSION.class.getDeclaredFields();
        for (Field field : fieldsVersion) {
            try {
                field.setAccessible(true);
                Log.w(TAG, "Build.VERSION." + field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /*
    * 手机制造商
    * mobile phone manufacturer
    * */
    public static String getProduct() {
        return Build.PRODUCT;
    }

    /*
    * 系统定制商
    * System Customizer
    * */
    public static String getBrand() {
        return Build.BRAND;
    }

    /*
    * 硬件制造商
    * Hardware manufacturer
    * */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /*
    *  硬件名称
    * Hardware name
    * */
    public static String getHardWare() {
        return  Build.HARDWARE;
    }

    /*
    *  型号
    * model
    * */
    public static String getMode() {
        return Build.MODEL;
    }

    /*
    * Android 系统版本
    * Android system version
    * */
    public static String getAndroidVersion() {
        return  Build.VERSION.RELEASE;
    }

    /*
    * CPU 指令集，可以查看是否支持64位
    * CPU instruction set, you can check whether it supports 64-bit
    * */
    public static String getCpuAbi() {
        return Build.CPU_ABI;
    }
}
