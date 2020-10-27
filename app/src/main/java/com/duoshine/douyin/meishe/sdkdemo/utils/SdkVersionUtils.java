package com.duoshine.douyin.meishe.sdkdemo.utils;

import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

public class SdkVersionUtils {
    /**
     * 判断是否是Android Q版本
     *
     * @return
     */

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    public static boolean checkedAndroid_Q() {
        return Build.VERSION.SDK_INT >= 29;
    }

    public static String getPath(int id) {
        return getRealPathAndroid_Q(id);
    }

    private static String getRealPathAndroid_Q(int id) {
        return QUERY_URI.buildUpon().appendPath(id + "").build().toString();
    }
}
