package com.duoshine.douyin.meishe.sdkdemo.utils;

import android.content.Context;

import java.util.Locale;

public class SystemUtils {

    /**
     * 获取当前语言环境是否是中文
     * Gets whether the current locale is Chinese
     * @param context
     * @return
     */
    public static boolean isZh(Context context) {
        Locale locale = null;
        try {
            locale = context.getResources( ).getConfiguration( ).locale;
        } catch (Exception e) {
            e.printStackTrace( );
            return false;
        }
        if (locale == null) {
            return false;
        }
        String language = locale.getLanguage( );
        if (language.endsWith("zh")) {
            return true;
        } else {
            return false;
        }
    }
}
