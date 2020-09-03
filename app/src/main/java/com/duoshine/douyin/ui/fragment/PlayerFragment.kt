package com.duoshine.douyin.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.duoshine.douyin.MainActivity
import com.duoshine.douyin.MainViewModel
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.PlayerAdapter
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 *Created by chen on 2020  播放视频 对于关注和推荐他们是相同
 *
 *
 * 视频预加载
 */
class PlayerFragment : BaseFragment() {
    private val TAG = "HomeFragment"
    private var mainViewModel: MainViewModel? = null
    private var adapter: PlayerAdapter? = null
    private var urls: MutableList<String>? = null
    private val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"
    private val EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE"

    private var receiver: VolumeReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        urls = ArrayList()
        receiver = VolumeReceiver()
        val filter = IntentFilter()
        filter.addAction(VOLUME_CHANGED_ACTION)
        context?.registerReceiver(receiver, filter)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
    }

    private fun initViewModel() {
        mainViewModel = ViewModelProvider(context as MainActivity).get(MainViewModel::class.java)
        mainViewModel!!.urlsLiveData()
            .observe(context as MainActivity, Observer<List<String>> { data ->
                //更新数据
                urls?.clear()
                urls?.addAll(data)
                view_pager.currentItem = 0
                adapter?.notifyDataSetChanged()
            })
        addRefreshListener()
    }

    /**
     * 监听Main底部Tab重复点击触发的刷新  和home页下拉触发的刷新
     */
    private fun addRefreshListener() {
        mainViewModel!!.getRefreshState()
            .observe(context as MainActivity, Observer<MainViewModel.RefreshState> {
                if (it == MainViewModel.RefreshState.START) {//开始刷新数据
                    //先执行个加载动画吧
                    GlobalScope.launch(Dispatchers.Main) {
                        mainViewModel!!.requestUrls()
                    }
                } else if (it == MainViewModel.RefreshState.COMPLETE) {
                    //结束加载动画

                }
            })
    }

    private fun initView() {
        view_pager.offscreenPageLimit = 2
        adapter = PlayerAdapter(view_pager, context!!, urls!!)
        view_pager.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(receiver)
    }

    /**
     * 当前fragment隐藏时调用
     */
    fun playWhenReady(hidden: Boolean) {

    }

    inner class VolumeReceiver : BroadcastReceiver() {
        private var mAudioManager: AudioManager? = null

        override fun onReceive(context: Context, intent: Intent) {
            if (VOLUME_CHANGED_ACTION == intent.action
                && (intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_MUSIC)
            ) {
                if (mAudioManager == null) {
                    mAudioManager = context.applicationContext
                        .getSystemService(Context.AUDIO_SERVICE) as AudioManager
                }
                val volume = mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
                val maxVolume = mAudioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 0
                adapter?.volumeChange(volume, maxVolume)
            }
        }
    }
}