package com.duoshine.douyin.meishe.sdkdemo.edit.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.duoshine.douyin.meishe.sdkdemo.utils.Logger;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by admin on 2018/11/28.
 */
public class ParseJsonFile {
    private static final String TAG = "ParseJsonFile";

    /**
     * Json转Java对象
     * Json to Java Object
     */
    public static <T> T fromJson(String json, Class<T> clz) {
        return new Gson().fromJson(json, clz);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return new Gson().fromJson(json, typeOfT);
    }

    public static ArrayList<FxJsonFileInfo.JsonFileInfo> readBundleFxJsonFile(Context context, String jsonFilePath) {
        String retsult = readAssetJsonFile(context, jsonFilePath);
        if (TextUtils.isEmpty(retsult)) {
            return null;
        }
        FxJsonFileInfo resultInfo = fromJson(retsult, FxJsonFileInfo.class);
        ArrayList<FxJsonFileInfo.JsonFileInfo> infoLists = resultInfo.getFxInfoList();
        return infoLists;
    }

    public static String readAssetJsonFile(Context context, String jsonFilePath) {
        if (context == null) {
            return null;
        }
        if (TextUtils.isEmpty(jsonFilePath)) {
            return null;
        }
        BufferedReader bufferedReader = null;
        StringBuilder retsult = new StringBuilder();
        try {
            InputStream inputStream = context.getAssets().open(jsonFilePath);
            if (inputStream == null)
                return null;
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String infoStrLine;
            while ((infoStrLine = bufferedReader.readLine()) != null) {
                retsult.append(infoStrLine);
            }
        } catch (Exception e) {
            Logger.e(TAG, "fail to read json" + jsonFilePath, e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                Logger.e(TAG, "fail to close bufferedReader", e);
            }
        }
        return retsult.toString();
    }

    public static String readSDJsonFile(Context context, String jsonFilePath) {
        if (context == null) {
            return null;
        }
        if (TextUtils.isEmpty(jsonFilePath)) {
            return null;
        }
        BufferedReader bufferedReader = null;
        StringBuilder retsult = new StringBuilder();
        try {
            FileInputStream inputStream = new FileInputStream(jsonFilePath);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String infoStrLine;
            while ((infoStrLine = bufferedReader.readLine()) != null) {
                retsult.append(infoStrLine);
            }
        } catch (Exception e) {
            Logger.e(TAG, "fail to read json" + jsonFilePath, e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                Logger.e(TAG, "fail to close bufferedReader", e);
            }
        }
        return retsult.toString();
    }

    public static String readSdCardJsonFile(String jsonFilePath) {
        if (TextUtils.isEmpty(jsonFilePath)) {
            return null;
        }
        BufferedReader bufferedReader = null;
        StringBuilder retsult = new StringBuilder();
        try {
            FileInputStream inputStream = new FileInputStream(new File(jsonFilePath));
            if (inputStream == null)
                return null;
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String infoStrLine;
            while ((infoStrLine = bufferedReader.readLine()) != null) {
                retsult.append(infoStrLine);
            }
        } catch (Exception e) {
            Log.e(TAG, "fail to read json" + jsonFilePath, e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "fail to close bufferedReader", e);
            }
        }
        return retsult.toString();
    }

    public static class FxJsonFileInfo {

        public ArrayList<JsonFileInfo> getFxInfoList() {
            return fxInfoList;
        }

        private ArrayList<JsonFileInfo> fxInfoList;

        public static class JsonFileInfo {
            public String getName_Zh() {
                return name_Zh;
            }

            /*
            * 素材中文名字
            * Material Chinese name
            * */
            private String name_Zh;

            public String getName() {
                return name;
            }

            /*
            * 素材英文名字
            * Material English Name
            * */
            private String name;

            public String getFxPackageId() {
                return fxPackageId;
            }

            /*
            * 素材包Id
            * Material package Id
            * */
            private String fxPackageId;

            public String getFxFileName() {
                return fxFileName;
            }

            /*
            * 素材特效包文件名
            * Material effects package name
            * */
            private String fxFileName;

            public String getFxLicFileName() {
                return fxLicFileName;
            }

            /*
            * 素材特效包授权文件名
            * Authorized file name
            * */
            private String fxLicFileName;

            public String getImageName() {
                return imageName;
            }

            /*
            * 素材封面
            * Material cover
            * */
            private String imageName;

            public String getFitRatio() {
                return fitRatio;
            }

            /*
            * 适配比例，参考NvAsset的定义
            * Adaptation ratio, refer to the definition of NVAsset
            * */
            private String fitRatio;
        }
    }
}
