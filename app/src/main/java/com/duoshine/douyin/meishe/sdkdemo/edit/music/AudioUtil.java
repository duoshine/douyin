package com.duoshine.douyin.meishe.sdkdemo.edit.music;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.duoshine.douyin.meishe.sdkdemo.utils.Constants;
import com.duoshine.douyin.meishe.sdkdemo.utils.dataInfo.MusicInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ms on 2018/7/15 0027.
 * Note: 1、获取媒体库所有音频 2、获取asset目录下的音频文件。获取到的时长是毫秒，设置到结构中会转化成微妙
 *
 * Note: 1. Get all the audio in the media library 2. Get the audio files in the asset directory. The length of time obtained is milliseconds, which will be transformed into subtlety when set into the structure
 */

public class AudioUtil {
    private static final String TAG = "AudioUtil";
    private AppCompatActivity mContext;
    private long videoMaxS = 0;
    private long videoMinS = 0;

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    /*
    *  过滤掉小于500毫秒的录音
    * Filter out recordings less than 500 milliseconds
    * */
    private static final int AUDIO_DURATION = 500;
    private static final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
    private static final String DURATION = "duration";

    public AudioUtil(AppCompatActivity context) {
        this.mContext = context;
    }

    public interface LocalMediaLoadListener {
        void loadComplete(List<MusicInfo> medias);
    }

    public void getMedias(final int type, final LocalMediaLoadListener mediaLoadListener) {
        /*
         * 媒体文件数据库字段
         * Media file database fields
         * */
        String[] PROJECTION = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                DURATION,
                MediaStore.MediaColumns.SIZE,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PROJECTION = new String[]{
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.MediaColumns.DATA,
                    MediaStore.MediaColumns.MIME_TYPE,
                    MediaStore.MediaColumns.WIDTH,
                    MediaStore.MediaColumns.HEIGHT,
                    DURATION,
                    MediaStore.MediaColumns.SIZE,
                    MediaStore.Audio.Media.TITLE};
        }


        final String[] finalPROJECTION = PROJECTION;
        mContext.getSupportLoaderManager().initLoader(type, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                CursorLoader cursorLoader = null;
                if(id == Constants.MEDIA_TYPE_AUDIO) {
                    String audio_condition = getSelectionArgsForSingleMediaCondition(getDurationCondition(0, AUDIO_DURATION));
                    String[] MEDIA_TYPE_AUDIO = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO)};
                    cursorLoader = new CursorLoader(
                            mContext, QUERY_URI, finalPROJECTION, audio_condition, MEDIA_TYPE_AUDIO
                            , ORDER_BY);
                }
                return cursorLoader;
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                List<MusicInfo> medias = new ArrayList<>();
                if (data != null) {
                    int count = data.getCount();
                    if (count > 0) {
                        data.moveToFirst();
                        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                        do {
                            String path = data.getString(data.getColumnIndexOrThrow(finalPROJECTION[1]));
                            if(!isSupport(path)) {
                                continue;
                            }

                            String pictureType = data.getString(data.getColumnIndexOrThrow(finalPROJECTION[2]));
                            int duration = data.getInt(data.getColumnIndexOrThrow(finalPROJECTION[5]));
                            Log.e("===>", "sd: mp3 duration: " + duration);

                            int size = data.getInt(data.getColumnIndexOrThrow(finalPROJECTION[6]));

                            String title = data.getString(data.getColumnIndexOrThrow(finalPROJECTION[7]));
                            String author = "null";
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                author = data.getString(data.getColumnIndexOrThrow(finalPROJECTION[8]));
                            }
                            try {
                                mmr.setDataSource(path);
                                author = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                                if(author == null || author.isEmpty()) {
                                    author = "null";
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            MusicInfo oneMedia = new MusicInfo();
                            oneMedia.setFilePath(path);
                            oneMedia.setExoplayerPath(path);
                            oneMedia.setDuration(duration * 1000);
                            oneMedia.setTitle(title);
                            oneMedia.setArtist(author);
                            oneMedia.setMimeType(type);
                            oneMedia.setTrimIn(0);
                            oneMedia.setTrimOut(oneMedia.getDuration());
                            medias.add(oneMedia);

                        } while (data.moveToNext());

                        mediaLoadListener.loadComplete(medias);
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
            }
        });
    }

    /**
     * 获取视频(最长或最小时间)
     * Get video (maximum or minimum time)
     * @param exMaxLimit
     * @param exMinLimit
     * @return
     */
    private String getDurationCondition(long exMaxLimit, long exMinLimit) {
        long maxS = videoMaxS == 0 ? Long.MAX_VALUE : videoMaxS;
        if (exMaxLimit != 0) maxS = Math.min(maxS, exMaxLimit);

        return String.format(Locale.CHINA, "%d <%s duration and duration <= %d",
                Math.max(exMinLimit, videoMinS),
                Math.max(exMinLimit, videoMinS) == 0 ? "" : "=",
                maxS);
    }

    /*
    * 查询条件(音视频)
    * Search conditions (audio and video)
    * */
    private String getSelectionArgsForSingleMediaCondition(String time_condition) {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                + " AND " + time_condition;
    }

    private Boolean isSupport(String path) {
        if(path == null || path.isEmpty()) {
            return false;
        }
        if(path.toLowerCase().endsWith(".mp3") ||
                path.toLowerCase().endsWith(".m4a") ||
                path.toLowerCase().endsWith(".aac") ||
                path.toLowerCase().endsWith(".flac")) {
            return true;
        }
        return false;
    }

    /**
     * 获取assets音乐文件列表
     * Get a list of assets music files
     */
    public List<MusicInfo> listMusicFilesFromAssets(Context context) {
        List<MusicInfo> fileList = new ArrayList<>();
        String[] path_list = null;
        try {
            path_list = context.getAssets().list("music");
            if (path_list != null) {
                for (int i = 0; i < path_list.length; ++i) {
                    String path = path_list[i];
                    if(!isSupport(path)) {
                        continue;
                    }
                    MusicInfo audioInfo = new MusicInfo();
                    audioInfo.setIsAsset(true);
                    audioInfo.setFilePath("assets:/music/" + path);
                    audioInfo.setExoplayerPath("asset:/music/" + path);
                    String tmp = path.substring(0,path.lastIndexOf("."));
                    audioInfo.setLrcPath("assets:/music/"+ tmp+".lrc");
                    audioInfo.setAssetPath("music/" + path);
                    String name = path.substring(0, path.lastIndexOf("."));

                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    Log.d(TAG, "str: " + audioInfo.getFilePath());
                    try {
                        AssetFileDescriptor afd = context.getAssets().openFd(audioInfo.getAssetPath());
                        mmr.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        if(artist == null || artist.isEmpty()) {
                            artist = "null";
                        }
                        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        Log.e("===>", "assets: mp3 duration: " + duration);

                        audioInfo.setTitle(name);
                        audioInfo.setArtist(artist);
                        audioInfo.setDuration(Integer.valueOf(duration) * 1000);
                        audioInfo.setTrimOut(audioInfo.getDuration());
                        audioInfo.setTrimIn(0);

                        fileList.add(audioInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }
}
