package com.duoshine.douyin.meishe.sdkdemo.utils;

import android.graphics.Color;

import com.meicam.sdk.NvsColor;

/**
 * Created by admin on 2018/7/9.
 */

public class ColorUtil {
    public static final int SOUL_COLOR = Color.parseColor("#8000abfc");
    public static final int IMAGE_COLOR = Color.parseColor("#8000fce0");
    public static final int SHAKE_COLOR = Color.parseColor("#80fcb600");
    public static final int WAVE_COLOR = Color.parseColor("#50f8fc00");
    public static final int BLACK_MAGIC_COLOR = Color.parseColor("#805c00fc");
    public static final int HALLUCINATION_COLOR = Color.parseColor("#80ff4d97");
    public static final int ZOOM_COLOR = Color.parseColor("#800B1746");
    public static final int NEON_COLOR = Color.parseColor("#8032CD32");
    public static final int FLICKER_AND_WHITE_COLOR = Color.parseColor("#80FF0000");
    public static final int FILTER_BG_0 = Color.parseColor("#CFC1FF");
    public static final int FILTER_BG_1 = Color.parseColor("#C1DEFF");
    public static final int FILTER_BG_2 = Color.parseColor("#FFC1C1");
    public static final int FILTER_BG_3 = Color.parseColor("#C1CBFF");
    public static final int FILTER_BG_NO_SELECTED = Color.parseColor("#C4C4C4");
    public static final int MAKEUP_DEFAULT_TEXT_BG = Color.parseColor("#80FFFFFF");
    private final static int[] FILTER_BG_COLORS = {FILTER_BG_0, FILTER_BG_1, FILTER_BG_2, FILTER_BG_3};

    public static String[] code = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
    public static NvsColor colorStringtoNvsColor(String colorString) {
        if(colorString == null || (colorString != null && colorString.isEmpty()))
            return null;
        NvsColor color = new NvsColor(1, 1, 1, 1);
        int hexColocr = Color.parseColor(colorString);
        color.a = (float) ((hexColocr & 0xff000000) >>> 24) / 0xFF;
        color.r = (float) ((hexColocr & 0x00ff0000) >> 16) / 0xFF;
        color.g = (float) ((hexColocr & 0x0000ff00) >> 8) / 0xFF;
        color.b = (float) ((hexColocr) & 0x000000ff) / 0xFF;
        return color;
    }
    public static int[] nvsColortoRgba(NvsColor color) {
        int rgba[] = {255, 255, 255, 255};
        if(color == null)
            return rgba;
        int red = (int) Math.floor(color.r * 255 + 0.5D);
        int green = (int) Math.floor(color.g * 255 + 0.5D);
        int blue = (int) Math.floor(color.b * 255 + 0.5D);
        int alpha = (int) Math.floor(color.a * 255 + 0.5D);
        rgba[0] = alpha;
        rgba[1] = red;
        rgba[2] = green;
        rgba[3] = blue;
        for(int i=0;i < rgba.length;i++) {
            if (rgba[i] < 0) {
                rgba[i] = 0;
            } else if (rgba[i] > 255) {
                rgba[i] = 255;
            }
        }
        return rgba;
    }

    public static String nvsColorToHexString(NvsColor color) {
        int rgba[] = nvsColortoRgba(color);
        String hexCode="#";
        for(int i=0;i<rgba.length;i++){
            int rgbItem = rgba[i];
            int lCode = rgbItem / 16;
            int rCode = rgbItem % 16;
            hexCode += code[lCode];
            hexCode += code[rCode];
        }
        return hexCode;
    }


    public static String intColorToHexString(int color) {
        StringBuilder sb = new StringBuilder();
        sb.append("#").
                append(toHexString(Color.alpha(color))).
                append(toHexString(Color.red(color))).
                append(toHexString(Color.green(color))).
                append(toHexString(Color.blue(color)));
        return sb.toString();
    }

    private static String toHexString(int num) {
        int lCode = num / 16;
        int rCode = num % 16;
        return code[lCode]+ code[rCode]+ "";
    }

    public static int getFilterRandomBgColor() {
        int index = (int) (Math.random() * FILTER_BG_COLORS.length);
        return FILTER_BG_COLORS[index];
    }
}
