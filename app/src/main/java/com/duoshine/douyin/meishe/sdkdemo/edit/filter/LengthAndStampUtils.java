package com.duoshine.douyin.meishe.sdkdemo.edit.filter;

/**
 * @author ms
 */
public class LengthAndStampUtils {
    public static int duringToLength(long during, double pixelPerMicrosecond) {
        return (int) Math.floor(during * pixelPerMicrosecond + 0.5D);
    }
}
