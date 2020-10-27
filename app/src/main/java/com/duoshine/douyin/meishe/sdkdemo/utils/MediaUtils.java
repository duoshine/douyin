package com.duoshine.douyin.meishe.sdkdemo.utils;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.duoshine.douyin.meishe.sdkdemo.MSApplication;
import com.duoshine.douyin.meishe.sdkdemo.selectmedia.bean.MediaData;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ${gexinyu} on 2018/5/28.
 */

public class MediaUtils {

    /**
     * 获取视频的缩略图。先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * <p>
     * Get a thumbnail of the video. First use ThumbnailUtils to create a video thumbnail, and then use ThumbnailUtils to generate a thumbnail of a specified size.
     * If the width and height of the desired thumbnail are smaller than MICRO_KIND, the type should use MICRO_KIND as the value of kind, which will save memory.
     *
     * @param videoPath video path
     * @param width     指定输出视频缩略图的宽度；Specify the width of the output video thumbnail
     * @param height    指定输出视频缩略图的高度；Specify the height of the output video thumbnail
     * @param kind      参照MediaStore.Images(Video).Thumbnails类中的常量MINI_KIND和MICRO_KIND。其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96；
     *                  Refer to the constants MINI_KIND and MICRO_KIND in the MediaStore.Images (Video) .Thumbnails class. Among them, MINI_KIND: 512 x 384, MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图；Video thumbnail of specified size
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        /*
         * 获取视频的缩略图
         * Get thumbnail of video
         * */
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        if (bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    /*
     * 获取视频的缩略图
     * Get thumbnail of video
     * */
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap b = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            b = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();

        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

    /**
     * 读取手机中所有图片信息
     * <p>
     * Read all picture information in the phone
     */
    public static void getAllPhotoInfo(final Activity activity, final LocalMediaCallback localMediaCallback) {
        ThreadPoolUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final List<MediaData> mediaBeen = new ArrayList<>();
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                String[] projection = {
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Thumbnails.DATA,
                        MediaStore.MediaColumns.DISPLAY_NAME
                };
                /*
                 * 全部图片
                 * All pictures
                 * */
                String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?";
                /*
                 * 指定格式
                 * Specify the format
                 * */
                String[] whereArgs = {"image/jpeg", "image/png", "image/jpg"};
                /*
                 * 查询
                 * Inquire
                 * */
                Cursor mCursor = activity.getContentResolver().query(
                        mImageUri, projection, null, null,

                        null);
                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        // 获取图片的路径
                        int thumbPathIndex = mCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                        int timeIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                        int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
                        String thumbPath = mCursor.getString(thumbPathIndex);
                        Long date = mCursor.getLong(timeIndex) * 1000;
                        //适配Android Q
                        String absolutePath = mCursor.getString
                                (mCursor.getColumnIndexOrThrow(projection[1]));
                        String filepath = SdkVersionUtils.checkedAndroid_Q() ? SdkVersionUtils.getPath(id) : absolutePath;
                        /*
                         * 判断文件是否存在，存在才去加入
                         * Determine if the file exists, and then add it
                         * */
                        boolean b = FileManagerUtils.fileIsExists(absolutePath);
                        if (b) {
                            String fileName = mCursor.getString
                                    (mCursor.getColumnIndexOrThrow(projection[4]));
                            MediaData fi = new MediaData(id, MediaConstant.IMAGE, filepath, thumbPath, date, fileName, false);
                            mediaBeen.add(fi);
                        }
                    }
                    mCursor.close();
                }
                /*
                 * 更新界面
                 * Update interface
                 * */
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*
                         * 采用冒泡排序的方式排列数据
                         * Arrange data using bubble sort
                         * */
                        sortByTimeRepoList(mediaBeen);
                        localMediaCallback.onLocalMediaCallback(mediaBeen);
                    }
                });
            }
        });
    }

    /**
     * 获取手机中所有视频的信息
     * Get information about all videos in your phone
     */
    public static void getAllVideoInfos(final Activity activity, final LocalMediaCallback localMediaCallback) {
        ThreadPoolUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final List<MediaData> videoList = new ArrayList<>();
                Uri mVideoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {MediaStore.Video.Thumbnails._ID
                        , MediaStore.Video.Media._ID
                        , MediaStore.Video.Thumbnails.DATA
                        , MediaStore.Video.Media.DURATION
                        , MediaStore.Video.Media.SIZE
                        , MediaStore.Video.Media.DATE_ADDED
                        , MediaStore.Video.Media.DISPLAY_NAME
                        , MediaStore.Video.Media.DATE_MODIFIED};
                /*
                 * 全部视频
                 * All videos
                 * */
                String where = MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=? or "
                        + MediaStore.Video.Media.MIME_TYPE + "=?";
                String[] whereArgs = {"video/mp4", "video/3gp", "video/aiv", "video/rmvb", "video/vob", "video/flv",
                        "video/mkv", "video/mov", "video/mpg"};
                Cursor mCursor = activity.getContentResolver().query(mVideoUri,
                        projection, null, null, MediaStore.Video.Media.DATE_ADDED + " DESC ");

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        /*
                         * 获取视频的路径
                         * Get the path of the video
                         * */
                        int videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
                        //适配Android Q
                        String absolutePath = mCursor.getString
                                (mCursor.getColumnIndexOrThrow(projection[2]));
                        String path = SdkVersionUtils.checkedAndroid_Q() ? SdkVersionUtils.getPath(videoId) : absolutePath;

                        long duration = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        long size = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE)) / 1024; //单位kb
                        if (size < 0) {
                            /*
                             * 某些设备获取size<0，直接计算
                             * Some devices get size <0 and calculate directly
                             * */
                            Log.e("dml", "this video size < 0 " + path);
                            size = new File(path).length() / 1024;
                        }
                        String displayName = mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        /*
                         * 用于展示相册初始化界面
                         * Used to display the album initialization interface
                         * */
                        int timeIndex = mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                        Long date = mCursor.getLong(timeIndex) * 1000;
                        /*
                         * 需要判断当前文件是否存在  一定要加，不然有些文件已经不存在图片显示不出来
                         * Need to determine whether the current file exists. Be sure to add it,
                         * otherwise some files no longer exist and the picture cannot be displayed.
                         * */
                        boolean fileIsExists = FileManagerUtils.fileIsExists(absolutePath);
                        if (fileIsExists) {
                            videoList.add(new MediaData(videoId, MediaConstant.VIDEO, path, path, duration, date, displayName, false));
                        }
                    }
                    mCursor.close();
                }
                /*
                 * 更新界面
                 * Update interface
                 * */
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //...
                        localMediaCallback.onLocalMediaCallback(videoList);
                    }
                });
            }
        });
    }

    /**
     * 所有信息
     * All information
     */
    public static void getAllPhotoAndVideo(final Activity activity, final LocalMediaCallback localMediaCallback) {
        getAllPhotoInfo(activity, new LocalMediaCallback() {
            @Override
            public void onLocalMediaCallback(final List<MediaData> allPhotos) {
                getAllVideoInfos(activity, new LocalMediaCallback() {
                    @Override
                    public void onLocalMediaCallback(List<MediaData> allVideos) {
                        /*
                         * 图片
                         * image
                         * */
                        final List<MediaData> allMediaList = new ArrayList<>();

                        final List<MediaData> allPhotoList = new ArrayList<>();
                        if (allPhotos != null && allPhotos.size() > 0) {
                            allPhotoList.addAll(allPhotos);
                        }
                        /*
                         * 视频
                         * video
                         * */
                        final List<MediaData> allVideoList = new ArrayList<>();
                        if (allVideos != null && allVideos.size() > 0) {
                            allVideoList.addAll(allVideos);
                        }
                        /*
                         * 排序
                         * Sort
                         * */
                        allMediaList.addAll(allPhotoList);
                        allMediaList.addAll(allVideoList);
                        /*
                         * 采用冒泡排序的方式排列数据
                         * Arrange data using bubble sort
                         * */
                        sortByTimeRepoList(allMediaList);
                        localMediaCallback.onLocalMediaCallback(allMediaList);
                    }
                });
            }
        });
    }

    /**
     * 根据类型返回media
     * Return media by type
     *
     * @param mActivity
     * @param index
     */
    public static void getMediasByType(Activity mActivity, int index, LocalMediaCallback localMediaCallback) {
        if (index == MediaConstant.ALL_MEDIA) {
            getAllPhotoAndVideo(mActivity, localMediaCallback);
        } else if (index == MediaConstant.VIDEO) {
            getAllVideoInfos(mActivity, localMediaCallback);
        } else {
            getAllPhotoInfo(mActivity, localMediaCallback);
        }
    }

    /**
     * 根据时间进行排序
     * Sort by time
     *
     * @param itemInfoList
     */
    public static void sortByTimeRepoList(List<MediaData> itemInfoList) {
        Collections.sort(itemInfoList, new Comparator<MediaData>() {
                    @Override
                    public int compare(MediaData item1, MediaData item2) {
                        Date date1 = new Date(item1.getData() * 1000L);
                        Date date2 = new Date(item2.getData() * 1000L);
                        return date2.compareTo(date1);
                    }
                }
        );
    }

    /*
     * 获取数据库字段
     * Get database fields
     * */
    private static void getUriColumns(Uri uri) {
        Cursor cursor = MSApplication.getmContext().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String[] columns = cursor.getColumnNames();

        for (String string : columns) {
            System.out.println(cursor.getColumnIndex(string) + " = " + string);
        }
        cursor.close();
    }

    public static ListOfAllMedia groupListByTime(List<MediaData> allMediaTemp) {
        /*
         * 分组算法
         * Grouping algorithm
         * */
        Map<String, List<MediaData>> skuIdMap = new LinkedHashMap<>();
        for (MediaData mediaData : allMediaTemp) {
            String strTime = new SimpleDateFormat("yyyy年MM月dd日").format(new Date(mediaData.getData()));
            List<MediaData> tempList = skuIdMap.get(strTime);
            /*
             * 如果取不到数据,那么直接new一个空的ArrayList
             * If no data can be obtained, then directly create an empty ArrayList
             * */
            if (tempList == null) {
                tempList = new ArrayList<>();
                tempList.add(mediaData);
                skuIdMap.put(strTime, tempList);
            } else {
                tempList.add(mediaData);
            }
        }
        List<List<MediaData>> lists = new ArrayList<>();
        List<MediaData> listOfOut = new ArrayList<>();
        for (Map.Entry<String, List<MediaData>> entry : skuIdMap.entrySet()) {
            MediaData mediaData = new MediaData();
            mediaData.setData(getIntTime(entry.getKey()));
            listOfOut.add(mediaData);
            lists.add(entry.getValue());
        }
        return new ListOfAllMedia(listOfOut, lists);
    }

    private static long getIntTime(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        Date date;
        try {
            date = sdr.parse(time);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public interface LocalMediaCallback {

        void onLocalMediaCallback(List<MediaData> allMediaTemp);
    }

    public static class ListOfAllMedia {
        private List<MediaData> listOfParent;
        private List<List<MediaData>> listOfAll;

        public ListOfAllMedia(List<MediaData> listOfParent, List<List<MediaData>> listOfAll) {
            this.listOfParent = listOfParent;
            this.listOfAll = listOfAll;
        }

        public List<MediaData> getListOfParent() {
            if (listOfParent == null) {
                return new ArrayList<>();
            }
            return listOfParent;
        }

        public void setListOfParent(List<MediaData> listOfParent) {
            this.listOfParent = listOfParent;
        }

        public List<List<MediaData>> getListOfAll() {
            if (listOfAll == null) {
                return new ArrayList<>();
            }
            return listOfAll;
        }

        public void setListOfAll(List<List<MediaData>> listOfAll) {
            this.listOfAll = listOfAll;
        }
    }

}
