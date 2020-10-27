package com.duoshine.douyin.meishe.sdkdemo.bean.makeup;

import android.text.TextUtils;

import com.meicam.sdk.NvsMakeupEffectInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ms
 */
public class MakeupData {
    private static volatile MakeupData sMakeupCache;
    private Map<String, NvsMakeupEffectInfo.MakeupEffect> mMakeupArgsMap;
    private Map<String, Integer> mSelectPosition;
    private Map<String, ColorData> mSelectColor;
    private int mComposeIndex = 0;


    /**
     * @param effectContent the makeup info of a selected type from selected category
     */
    public void addMakeupArgs(MakeupEffectContent effectContent) {
        if (effectContent == null) {
            return;
        }
        if (mMakeupArgsMap == null) {
            mMakeupArgsMap = new HashMap<>( );
        }

        NvsMakeupEffectInfo effectInfo = effectContent.getMakeupEffectInfo( );
        List<NvsMakeupEffectInfo.MakeupEffect> makeupEffectArray = effectInfo.getMakeupEffectArray();
        if ((makeupEffectArray == null) || (makeupEffectArray.isEmpty())) {
            return;
        }
        for (NvsMakeupEffectInfo.MakeupEffect effect : makeupEffectArray) {
            mMakeupArgsMap.put(effect.effectId, effect);
        }
    }


    /**
     * @param effectId the name of custom makeup category
     * @param position the position in the "effectId" category
     */
    public void addSelectPosition(String effectId, int position) {
        if (TextUtils.isEmpty(effectId)) {
            return;
        }
        if (mSelectPosition == null) {
            mSelectPosition = new HashMap<>( );
        }
        mSelectPosition.put(effectId, position);
    }

    /**
     * @param effectId the name of a makeup type in the selected category
     * @param color    the color for the makeup type in the selected category
     */
    public void addSelectColor(String effectId, ColorData color) {
        if (TextUtils.isEmpty(effectId)) {
            return;
        }
        if (mSelectColor == null) {
            mSelectColor = new HashMap<>( );
        }
        mSelectColor.put(effectId, color);
    }

    public void removeSelectColor(String effectId) {
        if (TextUtils.isEmpty(effectId)) {
            return;
        }
        if (mSelectColor == null) {
            return;
        }
        mSelectColor.remove(effectId);
    }

    public ColorData getColorByEffectId(String effectId) {
        if (mSelectColor == null || !mSelectColor.containsKey(effectId)) {
            return null;
        }
        return mSelectColor.get(effectId);
    }


    public int getPositionByEffectId(String effectId) {
        if (mSelectPosition == null || !mSelectPosition.containsKey(effectId)) {
            return -1;
        }
        return mSelectPosition.get(effectId);
    }

    public void clearPositionData() {
        if (mSelectPosition != null) {
            mSelectPosition.clear( );
        }
        if (mMakeupArgsMap != null) {
            mMakeupArgsMap.clear( );
        }
        if (mSelectColor != null) {
            mSelectColor.clear( );
        }
    }

    public void removeMakeupArgs(String makeupId) {
        if (mMakeupArgsMap == null || TextUtils.isEmpty(makeupId)) {
            return;
        }
        mMakeupArgsMap.remove(makeupId);
    }

    public NvsMakeupEffectInfo.MakeupEffect getMakeupEffect(String effectId) {
        if (mMakeupArgsMap == null || TextUtils.isEmpty(effectId)) {
            return null;
        }
        return mMakeupArgsMap.get(effectId);
    }

    public NvsMakeupEffectInfo getMakeupEffectInfo() {
        if (mMakeupArgsMap == null || mMakeupArgsMap.size( ) <= 0) {
            return null;
        }
        Set<String> mapKeys = mMakeupArgsMap.keySet( );
        NvsMakeupEffectInfo info = new NvsMakeupEffectInfo( );
        Object[] keys = mapKeys.toArray( );
        for (int index = 0; index < keys.length; index++) {
            info.addMakeupEffect(mMakeupArgsMap.get(keys[index]));
        }
        return info;
    }

    public static int getMakeupFlagById(String effectId) {
        int flag = 0;
        if (TextUtils.isEmpty(effectId)) {
            return flag;
        }
        if ("lip".equals(effectId)) {
            flag |= NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_LIP;
        } else if ("eyebrow".equals(effectId)) {
            flag |= NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_EYEBROW;
        } else if ("eyelash".equals(effectId)) {
            flag |= NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_EYELASH;
        } else if ("eyeshadow".equals(effectId)) {
            flag |= NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_EYESHADOW;
        } else if ("blusher".equals(effectId)) {
            flag |= NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_BLUSHER;
        } else if ("eyeliner".equals(effectId)) {
            flag |= NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_EYELINER;
        } else if ("shadow".equals(effectId)) {
            flag |= NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_SHADOW;
        } else if ("brighten".equals(effectId)) {
            flag |= NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_BRIGHTEN;
        }
        return flag;
    }

    public int getMakeupFlag() {
     /*   int flag = 0;
        if (mMakeupArgsMap == null || mMakeupArgsMap.size() <= 0) {
            return flag;
        }
        Set<String> mapKeys = mMakeupArgsMap.keySet();
        for (String key : mapKeys) {
            String effectId = mMakeupArgsMap.get(key).effectId;
            flag |= getMakeupFlagById(effectId);
        }*/
        return NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_ALL;
    }

    public static int getMakeupFlag(List<MakeupArgs> makeupArgsList) {
        int flag = 0;
        if (makeupArgsList == null || makeupArgsList.size( ) <= 0) {
            return flag;
        }
        for (MakeupArgs makeupEffect : makeupArgsList) {
            flag |= getMakeupFlagById(makeupEffect.getMakeupId( ));
        }
        return flag;
    }


    private MakeupData() {
    }

    public static MakeupData getInstacne() {
        if (sMakeupCache == null) {
            synchronized (MakeupData.class) {
                if (sMakeupCache == null) {
                    sMakeupCache = new MakeupData( );
                }
            }
        }
        return sMakeupCache;
    }

    private Set<String> mFxSet = new HashSet<>( );

    public void putFx(String fxName) {
        if (!mFxSet.contains(fxName)) {
            mFxSet.add(fxName);
        }
    }

    public void removeFx(String fxName) {
        mFxSet.remove(fxName);
    }

    public Set<String> getFxSet() {
        return mFxSet;
    }

    public void clearData() {
        mFxSet.clear( );
    }

    public int getComposeMakeupIndex() {
        return mComposeIndex;
    }

    public void setComposeIndex(int composeIndex) {
        this.mComposeIndex = composeIndex;
    }

    public static class ColorData {

        public ColorData() {
        }

        public ColorData(float colorsProgress, int colorIndex, int color) {
            this.colorsProgress = colorsProgress;
            this.colorIndex = colorIndex;
            this.color = color;
        }

        public float colorsProgress = -1F;
        public int colorIndex = -1;
        public int color = -1;
    }

}
