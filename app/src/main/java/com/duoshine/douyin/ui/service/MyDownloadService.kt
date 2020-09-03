package com.duoshine.douyin.ui.service

import android.app.Notification
import android.util.Log
import com.duoshine.douyin.R
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util


public class MyDownloadService : DownloadService {

    companion object {
        private val FOREGROUND_NOTIFICATION_ID = 1
        private val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel"
    }

    constructor() : super(
        FOREGROUND_NOTIFICATION_ID
        , DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL
        , DOWNLOAD_NOTIFICATION_CHANNEL_ID
        , R.string.exo_download_notification_channel_name
        , 0
    )


    override fun getDownloadManager(): DownloadManager {
        // Note: This should be a singleton in your app.
        val databaseProvider = ExoDatabaseProvider(applicationContext)

        val downloadDirectory = getExternalFilesDir(null);
        // A download cache should not evict media, so should use a NoopCacheEvictor.
        val downloadCache = SimpleCache(
            downloadDirectory,
            NoOpCacheEvictor(),
            databaseProvider
        )
        val userAgent = Util.getUserAgent(this, "ExoPlayer");
        // Create a factory for reading the data from the network.
        val dataSourceFactory =
            DefaultHttpDataSourceFactory(userAgent)

        // Create the download manager.
        val downloadManager = DownloadManager(
            applicationContext,
            databaseProvider,
            downloadCache,
            dataSourceFactory
        )

        // Optionally, setters can be called to configure the download manager.
//        downloadManager.requirements = requirements
        downloadManager.maxParallelDownloads = 3
        downloadManager.addListener(TerminalStateNotificationHelper())
        return downloadManager
    }

    override fun getForegroundNotification(downloads: MutableList<Download>): Notification {
        application
        return DownloadNotificationHelper(application, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
            .buildProgressNotification(R.drawable.ic_launcher_foreground, null, null, downloads)
    }

    override fun getScheduler(): Scheduler? {
        return if (Util.SDK_INT >= 21) PlatformScheduler(this, 1) else null
    }

    private class TerminalStateNotificationHelper : DownloadManager.Listener {

        override fun onDownloadChanged(downloadManager: DownloadManager, download: Download) {
            if (download.state == Download.STATE_COMPLETED) {
                Log.d("duo_shine", "onDownloadChanged: 下载完成")
            } else if (download.state == Download.STATE_FAILED) {
                Log.d("duo_shine", "onDownloadChanged: 下载失败")
            }
        }
    }
}