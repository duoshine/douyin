package com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo;

import android.graphics.PointF;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by liuluwei on 2017/12/9.
 */

public class CaptionInfo {
    public static final int O_DEFAULT =  0;
    public static final int O_VERTICAL =  1;
    public static final int O_HORIZONTAL =  2;//horizontal
    /*
     * 未使用过某一属性
     * Never used a property
     * */
    public static int ATTRIBUTE_UNUSED_FLAG = 0;
    /*
     * 已经使用过某一属性
     * Already used a property
     * */
    public static int ATTRIBUTE_USED_FLAG = 1;

    /*
     * 字幕文本内容
     * Subtitle text content
     * */
    private String m_text;
    /*
     * X缩放因子
     * X scaling factor
     * */
    private float m_scaleFactorX;
    /*
     * Y缩放因子
     * Y scaling factor
     * */
    private float m_scaleFactorY;
    /*
     * 锚点
     * Anchor
     * */
    private PointF m_anchor;
    /*
     * 字幕偏移量
     * Subtitle offset
     * */
    private PointF m_translation;
    /*
     * 旋转角度
     * Rotation angle
     * */
    private float m_rotation;
    /*
     * 字间距
     * Word spacing
     * */
    private float m_letterSpacing;
    /*
     * 行间距
     * L spacing
     * */
    private float m_lineSpacing;

    /*
     * 字幕对齐值
     * Subtitle alignment value
     * */
    private int m_alignVal;

    /**
     * 字幕方向
     * 0默认,1竖版,2横版
     */
        private int orientationType;
    /*
     * 字幕入点
     * Subtitle entry point
     * */
    private long m_inPoint;
    /*
     * 字幕出点
     * Subtitle Out
     * */
    private long m_outPoint;
    /*
     * 字幕样式Uuid
     * Caption style Uuid
     * */
    private String m_captionStyleUuid;

    /*
     * 花字Uuid
     * */
    private String mRichWordUuid;
    /*
     * 气泡Uuid
     * */
    private String mBubbleUuid;
    /*
     * 是否是传统字幕Uuid
     * */
    private boolean isTraditionCaption = true;
    /*
     * 组合动画Uuid
     * */
    private String mCombinationAnimationUuid;
    /*
     * 入场动画Uuid
     * */
    private String mMarchInAnimationUuid;
    /*
     * 出场动画Uuid
     * */
    private String mMarchOutAnimationUuid;
    /*
     * 组合动画时长
     * */
    private int mCombinationAnimationDuration;
    /*
     * 入场动画时长
     * */
    private int mMarchInAnimationDuration;
    /*
     * 出场动画时长
     * */
    private int mMarchOutAnimationDuration;
    /*
     * 字幕颜色
     * Subtitle color
     * */
    private String m_captionColor;
    /*
     * 字幕颜色不透明度
     * Subtitle color opacity
     * */
    private int m_captionColorAlpha;
    /*
     * 字幕背景颜色
     * Subtitle color
     * */
    private String m_captionBackground;
    /*
     * 字幕背景圆角
     * Subtitle color
     * */
    private float m_captionBackgroundRadius;
    /*
     * 字幕背景不透明度
     * Subtitle color opacity
     * */
    private int m_captionBackgroundAlpha;
    /*
     * 是否有描边
     * Whether there is a stroke
     * */
    private boolean m_hasOutline;
    /*
     * 描边颜色
     * Stroke color
     * */
    private String m_outlineColor;
    /*
     * 描边颜色不透明度
     * Stroke color opacity
     * */
    private int m_outlineColorAlpha;
    /*
     * 描边宽度
     * Stroke width
     * */
    private float m_outlineWidth;
    /*
     * 字幕字体
     * Subtitle font
     * */
    private String m_captionFont;
    /*
     * 是否加粗
     * Whether bold
     * */
    private boolean m_isBold;
    /*
     * 是否是斜体
     * Whether it is italic
     * */
    private boolean m_isItalic;
    /*
     * 是否有阴影
     * Is there a shadow
     * */
    private boolean m_isShadow;
    /*
     * 字体大小
     * font size
     * */
    private float m_captionSize;
    /*
     * 字幕Z值
     * Subtitle Z value
     * */
    private int m_captionZVal;

    /*
     * 使用偏移标识
     * Use offset identification
     * */
    private int m_usedTranslationFlag = ATTRIBUTE_UNUSED_FLAG;
    /*
     * 使用缩放旋转标识
     * Use rotate scale logo
     * */
    private int m_usedScaleRotationFlag = ATTRIBUTE_UNUSED_FLAG;
    /*
     * 使用加粗标识
     * Use bold logo
     * */
    private int m_usedIsBoldFlag = ATTRIBUTE_UNUSED_FLAG;
    /*
     * 使用斜体标识
     * Use italics
     * */
    private int m_usedIsItalicFlag = ATTRIBUTE_UNUSED_FLAG;
    /*
     * 使用阴影标识
     * Use shadow identification
     * */
    private int m_usedShadowFlag = ATTRIBUTE_UNUSED_FLAG;
    /*
     * 使用颜色标识
     * Use color identification
     * */
    private int m_usedColorFlag = ATTRIBUTE_UNUSED_FLAG;
    /*
     * 使用背景色标识
     * Use background identification
     * */
    private int m_usedBackgroundFlag = ATTRIBUTE_UNUSED_FLAG;
    /*
     * 使用圆角标识
     * Use background identification
     * */
    private int m_usedBackgroundRadiusFlag = ATTRIBUTE_UNUSED_FLAG;
    /*
     * 使用描边标识
     * Use stroke logo
     * */
    private int m_usedOutlineFlag = ATTRIBUTE_UNUSED_FLAG;
    /*
     * 使用字间距标识
     * Use word spacing
     * */
    private int m_usedLetterSpacingFlag = ATTRIBUTE_UNUSED_FLAG;//

    public int getUsedTranslationFlag() {
        return m_usedTranslationFlag;
    }

    public void setUsedTranslationFlag(int usedTranslationFlag) {
        this.m_usedTranslationFlag = usedTranslationFlag;
    }

    public int getUsedScaleRotationFlag() {
        return m_usedScaleRotationFlag;
    }

    public void setUsedScaleRotationFlag(int usedScaleRotationFlag) {
        this.m_usedScaleRotationFlag = usedScaleRotationFlag;
    }

    public int getUsedIsBoldFlag() {
        return m_usedIsBoldFlag;
    }

    public void setUsedIsBoldFlag(int usedIsBoldFlag) {
        this.m_usedIsBoldFlag = usedIsBoldFlag;
    }

    public int getUsedIsItalicFlag() {
        return m_usedIsItalicFlag;
    }

    public void setUsedIsItalicFlag(int usedIsItalicFlag) {
        this.m_usedIsItalicFlag = usedIsItalicFlag;
    }

    public int getUsedShadowFlag() {
        return m_usedShadowFlag;
    }

    public void setUsedShadowFlag(int usedShadowFlag) {
        this.m_usedShadowFlag = usedShadowFlag;
    }

    public int getUsedColorFlag() {
        return m_usedColorFlag;
    }

    public void setUsedColorFlag(int usedColorFlag) {
        this.m_usedColorFlag = usedColorFlag;
    }

    public void setUsedBackgroundFlag(int m_usedBackgroundFlag) {
        this.m_usedBackgroundFlag = m_usedBackgroundFlag;
    }

    public int getUsedBackgroundFlag() {
        return m_usedBackgroundFlag;
    }

    public void setUsedBackgroundRadiusFlag(int m_usedBackgroundRadiusFlag) {
        this.m_usedBackgroundRadiusFlag = m_usedBackgroundRadiusFlag;
    }

    public int getUsedBackgroundRadiusFlag() {
        return m_usedBackgroundRadiusFlag;
    }

    public int getUsedOutlineFlag() {
        return m_usedOutlineFlag;
    }

    public void setUsedOutlineFlag(int usedOutlineFlag) {
        this.m_usedOutlineFlag = usedOutlineFlag;
    }
    /////////////////////////////////////////////////////////

    public int getAlignVal() {
        return m_alignVal;
    }

    public void setAlignVal(int alignVal) {
        this.m_alignVal = alignVal;
    }

    public int getOrientationType() {
        return orientationType;
    }

    public void setOrientationType(int orientationType) {
        this.orientationType = orientationType;
    }

    public int getCaptionZVal() {
        return m_captionZVal;
    }

    public void setCaptionZVal(int m_captionZVal) {
        this.m_captionZVal = m_captionZVal;
    }

    public long getInPoint() {
        return m_inPoint;
    }

    public void setInPoint(long inPoint) {
        this.m_inPoint = inPoint;
    }

    public long getOutPoint() {
        return m_outPoint;
    }

    public void setOutPoint(long outPoint) {
        this.m_outPoint = outPoint;
    }

    public String getCaptionStyleUuid() {
        return m_captionStyleUuid;
    }

    public void setCaptionStyleUuid(String captionStyleUuid) {
        this.m_captionStyleUuid = captionStyleUuid;
    }

    public String getCaptionColor() {
        return m_captionColor;
    }

    public void setCaptionColor(String captionColor) {
        this.m_captionColor = captionColor;
    }

    public int getCaptionColorAlpha() {
        return m_captionColorAlpha;
    }

    public void setCaptionColorAlpha(int captionColorAlpha) {
        this.m_captionColorAlpha = captionColorAlpha;
    }

    public String getCaptionBackground() {
        return m_captionBackground;
    }

    public void setCaptionBackground(String captionBackground) {
        this.m_captionBackground = captionBackground;
    }

    public float getCaptionBackgroundRadius() {
        return m_captionBackgroundRadius;
    }

    public void setCaptionBackgroundRadius(float m_captionBackgroundRadius) {
        this.m_captionBackgroundRadius = m_captionBackgroundRadius;
    }

    public int getCaptionBackgroundAlpha() {
        return m_captionBackgroundAlpha;
    }

    public void setCaptionBackgroundAlpha(int captionBackgroundAlpha) {
        this.m_captionBackgroundAlpha = captionBackgroundAlpha;
    }

    public boolean isHasOutline() {
        return m_hasOutline;
    }

    public void setHasOutline(boolean hasOutline) {
        this.m_hasOutline = hasOutline;
    }

    public String getOutlineColor() {
        return m_outlineColor;
    }

    public void setOutlineColor(String outlineColor) {
        this.m_outlineColor = outlineColor;
    }

    public int getOutlineColorAlpha() {
        return m_outlineColorAlpha;
    }

    public void setOutlineColorAlpha(int outlineColorAlpha) {
        this.m_outlineColorAlpha = outlineColorAlpha;
    }

    public float getOutlineWidth() {
        return m_outlineWidth;
    }

    public void setOutlineWidth(float outlineWidth) {
        this.m_outlineWidth = outlineWidth;
    }

    public String getCaptionFont() {
        return m_captionFont;
    }

    public void setCaptionFont(String captionFont) {
        this.m_captionFont = captionFont;
    }

    public boolean isBold() {
        return m_isBold;
    }

    public void setBold(boolean isBold) {
        this.m_isBold = isBold;
    }

    public boolean isItalic() {
        return m_isItalic;
    }

    public void setItalic(boolean isItalic) {
        this.m_isItalic = isItalic;
    }

    public boolean isShadow() {
        return m_isShadow;
    }

    public void setShadow(boolean isShadow) {
        this.m_isShadow = isShadow;
    }

    public float getCaptionSize() {
        return m_captionSize;
    }

    public void setCaptionSize(float captionSize) {
        this.m_captionSize = captionSize;
    }

    public float getLetterSpacing() {
        return m_letterSpacing;
    }

    public void setLetterSpacing(float letterSpacing) {
        this.m_letterSpacing = letterSpacing;
    }

    public float getLineSpacing() {
        return m_lineSpacing;
    }

    public void setLineSpacing(float lineSpacing) {
        this.m_lineSpacing = lineSpacing;
    }

    public int getUsedLetterSpacingFlag() {
        return m_usedLetterSpacingFlag;
    }

    public void setUsedLetterSpacingFlag(int usedLetterSpacintFlag) {
        this.m_usedLetterSpacingFlag = usedLetterSpacintFlag;
    }

    /**
     * 关键帧
     */
    private Map<Long, KeyFrameInfo> mKeyFrameInfoHashMap = new TreeMap<>(
            new Comparator<Long>() {
                @Override
                public int compare(Long o1, Long o2) {
                    // 时间升序排列
                    return o1.compareTo(o2);
                }
            }
    );

    public void putKeyFrameInfo(long keyFrameTime, KeyFrameInfo keyFrameInfo) {
        if (mKeyFrameInfoHashMap != null) {
            mKeyFrameInfoHashMap.put(keyFrameTime, keyFrameInfo);
        }
    }

    public Map<Long, KeyFrameInfo> getKeyFrameInfo() {
        return mKeyFrameInfoHashMap;
    }


    public CaptionInfo() {
        m_text = null;
        m_scaleFactorX = 1.0f;
        m_scaleFactorY = 1.0f;
        m_anchor = null;
        m_rotation = 0;
        m_translation = null;
        m_alignVal = -1;
        m_inPoint = 0;
        m_outPoint = 0;
        m_captionStyleUuid = "";
        m_captionColor = "";
        m_captionColorAlpha = 100;
        m_captionBackground = "";
        m_captionBackgroundAlpha = 100;
        m_captionBackgroundRadius = 0f;
        m_hasOutline = false;
        m_outlineColor = "";
        m_outlineColorAlpha = 100;
        m_outlineWidth = 8.0f;
        m_captionFont = "";
        m_isBold = true;
        m_isItalic = false;
        m_isShadow = false;
        m_captionSize = -1;
        m_captionZVal = 0;
        m_letterSpacing = 0;
        m_lineSpacing = 0;
        mKeyFrameInfoHashMap.clear();
    }

    public void setText(String text) {
        m_text = text;
    }

    public String getText() {
        return m_text;
    }

    public void setScaleFactorX(float value) {
        m_scaleFactorX = value;
    }

    public float getScaleFactorX() {
        return m_scaleFactorX;
    }

    public void setScaleFactorY(float value) {
        m_scaleFactorY = value;
    }

    public float getScaleFactorY() {
        return m_scaleFactorY;
    }

    public void setAnchor(PointF point) {
        m_anchor = point;
    }

    public PointF getAnchor() {
        return m_anchor;
    }

    public void setRotation(float value) {
        m_rotation = value;
    }

    public float getRotation() {
        return m_rotation;
    }

    public void setTranslation(PointF point) {
        m_translation = point;
    }

    public PointF getTranslation() {
        return m_translation;
    }

    public String getRichWordUuid() {
        return mRichWordUuid;
    }

    public void setRichWordUuid(String richWordUuid) {
        this.mRichWordUuid = richWordUuid;
    }

    public String getBubbleUuid() {
        return mBubbleUuid;
    }

    public void setBubbleUuid(String bubbleUuid) {
        this.mBubbleUuid = bubbleUuid;
    }

    public boolean isTraditionCaption() {
        return isTraditionCaption;
    }

    public void setTraditionCaption(boolean traditionCaption) {
        isTraditionCaption = traditionCaption;
    }

    public String getCombinationAnimationUuid() {
        return mCombinationAnimationUuid;
    }

    public void setCombinationAnimationUuid(String combinationAnimationUuid) {
        this.mCombinationAnimationUuid = combinationAnimationUuid;
    }

    public String getMarchInAnimationUuid() {
        return mMarchInAnimationUuid;
    }

    public void setMarchInAnimationUuid(String marchInAnimationUuid) {
        this.mMarchInAnimationUuid = marchInAnimationUuid;
    }

    public String getMarchOutAnimationUuid() {
        return mMarchOutAnimationUuid;
    }

    public void setMarchOutAnimationUuid(String marchOutAnimationUuid) {
        this.mMarchOutAnimationUuid = marchOutAnimationUuid;
    }

    public int getCombinationAnimationDuration() {
        return mCombinationAnimationDuration;
    }

    public void setCombinationAnimationDuration(int combinationAnimationDuration) {
        this.mCombinationAnimationDuration = combinationAnimationDuration;
    }

    public int getMarchInAnimationDuration() {
        return mMarchInAnimationDuration;
    }

    public void setMarchInAnimationDuration(int marchInAnimationDuration) {
        this.mMarchInAnimationDuration = marchInAnimationDuration;
    }

    public int getMarchOutAnimationDuration() {
        return mMarchOutAnimationDuration;
    }

    public void setMarchOutAnimationDuration(int marchOutAnimationDuration) {
        this.mMarchOutAnimationDuration = marchOutAnimationDuration;
    }

    public CaptionInfo clone() {
        CaptionInfo newCaptionInfo = new CaptionInfo();
        newCaptionInfo.setText(this.getText());
        newCaptionInfo.setAnchor(this.getAnchor());
        newCaptionInfo.setRotation(this.getRotation());
        newCaptionInfo.setScaleFactorX(this.getScaleFactorX());
        newCaptionInfo.setScaleFactorY(this.getScaleFactorY());
        newCaptionInfo.setTranslation(this.getTranslation());
        newCaptionInfo.setLetterSpacing(this.getLetterSpacing());
        newCaptionInfo.setLineSpacing(this.getLineSpacing());
        //copy data
        newCaptionInfo.setAlignVal(this.getAlignVal());
        newCaptionInfo.setInPoint(this.getInPoint());
        newCaptionInfo.setOutPoint(this.getOutPoint());
        newCaptionInfo.setCaptionZVal(this.getCaptionZVal());
        newCaptionInfo.setOrientationType(this.getOrientationType());
        newCaptionInfo.setCaptionStyleUuid(this.getCaptionStyleUuid());
        newCaptionInfo.setRichWordUuid(this.getRichWordUuid());
        newCaptionInfo.setBubbleUuid(this.getBubbleUuid());
        newCaptionInfo.setCombinationAnimationUuid(this.getCombinationAnimationUuid());
        newCaptionInfo.setMarchInAnimationUuid(this.getMarchInAnimationUuid());
        newCaptionInfo.setMarchOutAnimationUuid(this.getMarchOutAnimationUuid());
        newCaptionInfo.setCombinationAnimationDuration(this.getCombinationAnimationDuration());
        newCaptionInfo.setMarchInAnimationDuration(this.getMarchInAnimationDuration());
        newCaptionInfo.setMarchOutAnimationDuration(this.getMarchOutAnimationDuration());
        newCaptionInfo.setTraditionCaption(this.isTraditionCaption());
        newCaptionInfo.setCaptionColor(this.getCaptionColor());
        newCaptionInfo.setCaptionBackground(this.getCaptionBackground());
        newCaptionInfo.setCaptionBackgroundRadius(this.getCaptionBackgroundRadius());
        newCaptionInfo.setCaptionColorAlpha(this.getCaptionColorAlpha());
        newCaptionInfo.setCaptionBackgroundAlpha(this.getCaptionBackgroundAlpha());
        newCaptionInfo.setHasOutline(this.isHasOutline());
        newCaptionInfo.setOutlineColor(this.getOutlineColor());
        newCaptionInfo.setOutlineColorAlpha(this.getOutlineColorAlpha());
        newCaptionInfo.setOutlineWidth(this.getOutlineWidth());
        newCaptionInfo.setCaptionFont(this.getCaptionFont());
        newCaptionInfo.setBold(this.isBold());
        newCaptionInfo.setItalic(this.isItalic());
        newCaptionInfo.setShadow(this.isShadow());
//        newCaptionInfo.setCaptionSize(this.getCaptionSize());

        //控制标识
        newCaptionInfo.setUsedTranslationFlag(this.getUsedTranslationFlag());
        newCaptionInfo.setUsedScaleRotationFlag(this.getUsedScaleRotationFlag());
        newCaptionInfo.setUsedIsBoldFlag(this.getUsedIsBoldFlag());
        newCaptionInfo.setUsedIsItalicFlag(this.getUsedIsItalicFlag());
        newCaptionInfo.setUsedShadowFlag(this.getUsedShadowFlag());
        newCaptionInfo.setUsedColorFlag(this.getUsedColorFlag());
        newCaptionInfo.setUsedOutlineFlag(this.getUsedOutlineFlag());
        newCaptionInfo.setUsedLetterSpacingFlag(this.getUsedLetterSpacingFlag());
        newCaptionInfo.setUsedBackgroundFlag(this.getUsedBackgroundFlag());
        newCaptionInfo.setUsedBackgroundRadiusFlag(this.getUsedBackgroundRadiusFlag());

        Set<Map.Entry<Long, KeyFrameInfo>> entries = this.getKeyFrameInfo().entrySet();
        if (!entries.isEmpty()) {
            for (Map.Entry<Long, KeyFrameInfo> keyFrameInfoMap : entries) {
                newCaptionInfo.putKeyFrameInfo(keyFrameInfoMap.getKey(), keyFrameInfoMap.getValue());
            }
        }
        return newCaptionInfo;
    }
}
