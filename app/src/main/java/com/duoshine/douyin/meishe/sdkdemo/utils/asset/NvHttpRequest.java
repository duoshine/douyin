package com.duoshine.douyin.meishe.sdkdemo.utils.asset;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.duoshine.douyin.meishe.sdkdemo.MSApplication;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset.ASSET_CAPTION_ANIMATION;
import static com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset.ASSET_CAPTION_OUT_ANIMATION;
import static com.duoshine.douyin.meishe.sdkdemo.utils.asset.NvAsset.ASSET_CAPTION_RICH_WORD;


/**
 * Created by shizhouhu on 2018/6/14.
 */

public class NvHttpRequest {
    private static final String TAG = "NvHttpRequest ";
    public static final int NONETWORK = 0;
    /**
     * wifi连接
     * wifi connection
     */
    public static final int WIFI = 1;
    /**
     * 移动网络
     * mobile network
     */
    public static final int NOWIFI = 2;

    /**
     * 检验网络连接类型，判断是否是wifi连接
     * Check the network connection type to determine whether it is a wifi connection
     *
     * @param context
     * @return the network connection type
     */
    public static int checkNetWorkType(Context context) {

        if (!checkNetWork(context)) {
            return NvHttpRequest.NONETWORK;
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            return NvHttpRequest.WIFI;
        else
            return NvHttpRequest.NOWIFI;
    }

    /**
     * 检测网络是否连接
     * <p>
     * Check if the network is connected
     *
     * @param context
     * @return
     */
    public static boolean checkNetWork(Context context) {
        /*
         * 获得连接设备管理器
         * Get Connected Device Manager
         * */
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return (ni != null && ni.isAvailable());
    }

    private static final Gson m_gson = new Gson();
    private static NvHttpRequest m_instance = null;
    private OkHttpClient m_httpClient = null;

    public static NvHttpRequest sharedInstance() {
        if (m_instance == null)
            m_instance = new NvHttpRequest();
        return m_instance;
    }

    private NvHttpRequest() {
        m_httpClient = new OkHttpClient();
    }

    public void getAssetList(int assetType, int aspectRatio, int categoryId, int page, int pageSize, String sdkVersion, NvHttpRequestListener requestListener) {
        Request.Builder requenstBuilder = new Request.Builder();
        requenstBuilder.method("GET", null);
        requenstBuilder.url(getRequestParam(assetType, aspectRatio, categoryId, page, pageSize,sdkVersion).toString());
        Request httpRequest = requenstBuilder.build();
        final NvHttpRequestListener localRequestListener = requestListener;
        final int localAssetsType = assetType;
        final int localPage = page;
        final int localPageSize = pageSize;
        m_httpClient.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (localRequestListener != null) {
                    localRequestListener.onGetAssetListFailed(e, localAssetsType);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    String strResult = response.body().string();
                    if (localRequestListener != null) {
                        response.body().close();
                        NvAssetResponseInfo responseInfo = fromJson(strResult, NvAssetResponseInfo.class);
                        if (responseInfo.getErrNo() == 0) {
                            localRequestListener.onGetAssetListSuccess(responseInfo.getList(), localAssetsType, responseInfo.getHasNext());
                        } else {
                            localRequestListener.onGetAssetListFailed(null, localAssetsType);
                        }
                    }
                }
            }
        });
    }


    /**
     * Json转Java对象
     * <p>
     * Json to Java Object
     */
    private static <T> T fromJson(String json, Class<T> clz) {
        return m_gson.fromJson(json, clz);
    }

    public static class NvAssetResponseInfo {
        private int errNo;
        private boolean hasNext;
        private ArrayList<NvAssetInfo> list;

        public int getErrNo() {
            return errNo;
        }

        public boolean getHasNext() {
            return hasNext;
        }

        public ArrayList<NvAssetInfo> getList() {
            return list;
        }
    }

    public class NvAssetInfo {
        private String id;
        private int category;
        private String name;
        private String desc;
        private String tags;
        private int version;
        private String minAppVersion;
        private String packageUrl;
        private int packageSize;
        private String coverUrl;
        private int supportedAspectRatio;

        public String getId() {
            return id;
        }


        public int getCategory() {
            return category;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }

        public String getTags() {
            return tags;
        }

        public int getVersion() {
            return version;
        }

        public String getMinAppVersion() {
            return minAppVersion;
        }

        public String getPackageUrl() {
            return packageUrl;
        }

        public int getPackageSize() {
            return packageSize;
        }

        public String getCoverUrl() {
            return coverUrl;
        }

        public int getSupportedAspectRatio() {
            return supportedAspectRatio;
        }
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    private HttpUrl getRequestParam(int assetType, int aspectRatio, int categoryId, int page, int pageSize, String sdkVersion) {
        try {
            HttpUrl.Builder builder = new HttpUrl.Builder();
            builder.scheme("https");
            builder.host("vsapi.meishesdk.com");
            builder.addPathSegment("materialinfo/index.php");
            builder.addQueryParameter("command", "listMaterial");
            builder.addQueryParameter("acceptAspectRatio", String.valueOf(aspectRatio));
            /*
             * 自定义贴纸category 特殊处理
             * Custom sticker category special treatment
             * */
            if (assetType == NvAsset.ASSET_CUSTOM_ANIMATED_STICKER) {
                builder.addQueryParameter("category", String.valueOf(NvAsset.NV_CATEGORY_ID_CUSTOM));
            } else if(assetType == NvAsset.ASSET_FILTER){
                builder.addQueryParameter("includeCategories","1,2,3,4,5,6,7" );
            }else {
                builder.addQueryParameter("category", String.valueOf(categoryId));
            }
            builder.addQueryParameter("sdkVersion", sdkVersion);
            builder.addQueryParameter("page", String.valueOf(page));
            builder.addQueryParameter("pageSize", String.valueOf(pageSize));

            if (isZh(MSApplication.getmContext())) {
                builder.addQueryParameter("lang", "zh_CN");
            } else {
                builder.addQueryParameter("lang", "en");
            }

            if (assetType == NvAsset.ASSET_THEME) {
                builder.addQueryParameter("type", String.valueOf(1));
            } else if (assetType == NvAsset.ASSET_FILTER || assetType == NvAsset.ASSET_ANIMATION_IN
                                                            || assetType == NvAsset.ASSET_ANIMATION_OUT
                                                            || assetType == NvAsset.ASSET_ANIMATION_COMPANY) {
                builder.addQueryParameter("type", String.valueOf(2));
            } else if (assetType == NvAsset.ASSET_CAPTION_STYLE || assetType == ASSET_CAPTION_RICH_WORD ||
                    assetType == NvAsset.ASSET_CAPTION_BUBBLE || assetType == ASSET_CAPTION_ANIMATION ||
                    assetType == NvAsset.ASSET_CAPTION_IN_ANIMATION || assetType == ASSET_CAPTION_OUT_ANIMATION) {
                builder.addQueryParameter("type", String.valueOf(3));
            } else if (assetType == NvAsset.ASSET_ANIMATED_STICKER) {
                builder.addQueryParameter("type", String.valueOf(4));
            } else if (assetType == NvAsset.ASSET_VIDEO_TRANSITION) {
                builder.addQueryParameter("type", String.valueOf(5));
            } else if (assetType == NvAsset.ASSET_CUSTOM_ANIMATED_STICKER) {
                builder.addQueryParameter("type", String.valueOf(4));
            } else if (assetType == NvAsset.ASSET_CAPTURE_SCENE) {
                builder.addQueryParameter("type", String.valueOf(8));
            } else if (assetType == NvAsset.ASSET_PARTICLE) {
                builder.addQueryParameter("type", String.valueOf(9));
            } else if (assetType == NvAsset.ASSET_FACE_STICKER) {
                builder.addQueryParameter("type", String.valueOf(10));
            } else if (assetType == NvAsset.ASSET_FACE1_STICKER) {
                builder.addQueryParameter("type", String.valueOf(11));
            } else if (assetType == NvAsset.ASSET_SUPER_ZOOM) {
                builder.addQueryParameter("type", String.valueOf(13));
            } else if (assetType == NvAsset.ASSET_FONT) {
                builder.addQueryParameter("type", String.valueOf(6));
            } else if (assetType == NvAsset.ASSET_ARSCENE_FACE) {
                builder.addQueryParameter("type", String.valueOf(14));
            } else if (assetType == NvAsset.ASSET_COMPOUND_CAPTION) {
                builder.addQueryParameter("type", String.valueOf(15));
            }
            return builder.build();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return null;
    }

    private FormBody makeRequestParam(int assetType, int aspectRatio, int categoryId, int page, int pageSize) {
        FormBody.Builder builder = new FormBody.Builder();

        builder.add("command", "listMaterial");
        builder.add("acceptAspectRatio", String.valueOf(aspectRatio));
        builder.add("category", String.valueOf(categoryId));
        builder.add("page", String.valueOf(page));
        builder.add("pageSize", String.valueOf(pageSize));
        builder.add("lang", "zh_CN");
        if (assetType == NvAsset.ASSET_THEME) {
            builder.add("type", String.valueOf(1));
        } else if (assetType == NvAsset.ASSET_FILTER) {
            builder.add("type", String.valueOf(2));
        } else if (assetType == NvAsset.ASSET_CAPTION_STYLE) {
            builder.add("type", String.valueOf(3));
        } else if (assetType == NvAsset.ASSET_ANIMATED_STICKER) {
            builder.add("type", String.valueOf(4));
        } else if (assetType == NvAsset.ASSET_VIDEO_TRANSITION) {
            builder.add("type", String.valueOf(5));
        } else if (assetType == NvAsset.ASSET_CAPTURE_SCENE) {
            builder.add("type", String.valueOf(8));
        } else if (assetType == NvAsset.ASSET_PARTICLE) {
            builder.add("type", String.valueOf(9));
        } else if (assetType == NvAsset.ASSET_FACE_STICKER) {
            builder.add("type", String.valueOf(10));
        } else if (assetType == NvAsset.ASSET_FACE1_STICKER) {
            builder.add("type", String.valueOf(11));
        } else if (assetType == NvAsset.ASSET_SUPER_ZOOM) {
            builder.add("type", String.valueOf(13));
        } else if (assetType == NvAsset.ASSET_FONT) {
            builder.add("type", String.valueOf(6));
        } else if (assetType == NvAsset.ASSET_ARSCENE_FACE) {
            builder.add("type", String.valueOf(14));
        } else if (assetType == NvAsset.ASSET_COMPOUND_CAPTION) {
            builder.add("type", String.valueOf(15));
        }

        return builder.build();
    }

    public void downloadAsset(String srcFileUrl, final String assetDownloadDir, String desFilePath, final String assetSuffixName, NvHttpRequestListener downloadListener, int assetType, String downloadId) {
        final NvHttpRequestListener localDownloadListener = downloadListener;
        final String desFileUrl = desFilePath;
        final int localAssetType = assetType;
        final String localDownloadId = downloadId;
        Request httpRequenst = null;
        try {
            httpRequenst = new Request.Builder().url(srcFileUrl).build();
        } catch (Exception e) {
            if (localDownloadListener != null) {
                localDownloadListener.onDonwloadAssetFailed(e, localAssetType, localDownloadId);
            }
        }

        m_httpClient.newCall(httpRequenst).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                File localFile = new File(desFileUrl);
                if (localFile.exists()) {
                    localFile.delete();
                }
                if (localDownloadListener != null) {
                    localDownloadListener.onDonwloadAssetFailed(e, localAssetType, localDownloadId);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    File file = new File(desFileUrl);
                    RandomAccessFile accessFile = null;
                    InputStream httpInputStream = null;
                    try {
                        accessFile = new RandomAccessFile(file, "rw");
                        long contentLength = response.body().contentLength();
                        httpInputStream = response.body().byteStream();
                        byte[] byteStreamArray = new byte[4096];
                        int len, totalLen = 0;
                        while ((len = httpInputStream.read(byteStreamArray)) != -1) {
                            totalLen += len;
                            accessFile.write(byteStreamArray, 0, len);
                            //计算百分比
                            int progress = (int) (totalLen * 100 / contentLength);
                            if (localDownloadListener != null) {
                                localDownloadListener.onDonwloadAssetProgress(progress, localAssetType, localDownloadId);
                            }
                        }
                        /*
                         *  替换文件后缀名称
                         * Replace file suffix name
                         * */
                        String name = file.getName();
                        String newName = name.replace(".tmp", assetSuffixName);
                        File newFile = new File(assetDownloadDir, newName);
                        file.renameTo(newFile);
                        //
                        if (localDownloadListener != null) {
                            localDownloadListener.onDonwloadAssetSuccess(true, newFile.getAbsolutePath(), localAssetType, localDownloadId);
                        }
                    } catch (FileNotFoundException e) {
                        if (file.exists()) {
                            file.delete();
                        }
                        e.printStackTrace();
                        if (localDownloadListener != null) {
                            localDownloadListener.onDonwloadAssetFailed(e, localAssetType, localDownloadId);
                        }
                    } catch (IOException e) {
                        if (file.exists()) {
                            file.delete();
                        }
                        e.printStackTrace();
                        if (localDownloadListener != null) {
                            localDownloadListener.onDonwloadAssetFailed(e, localAssetType, localDownloadId);
                        }
                    } finally {
                        try {
                            response.body().close();
                            if (httpInputStream != null)
                                httpInputStream.close();
                            if (accessFile != null)
                                accessFile.close();
                        } catch (IOException e) {
                            if (file.exists()) {
                                file.delete();
                            }
                            e.printStackTrace();
                            if (localDownloadListener != null) {
                                localDownloadListener.onDonwloadAssetFailed(e, localAssetType, localDownloadId);
                            }
                        }
                    }

                } else {
                    Log.e(TAG, "服务器端错误");
                }
            }
        });
    }

    public interface NvHttpRequestListener {
        void onGetAssetListSuccess(ArrayList responseArrayList, int assetType, boolean hasNext);

        void onGetAssetListFailed(IOException e, int assetType);

        void onDonwloadAssetProgress(int progress, int assetType, String downloadId);

        void onDonwloadAssetSuccess(boolean success, String downloadPath, int assetType, String downloadId);

        void onDonwloadAssetFailed(Exception e, int assetType, String downloadId);
    }
}
