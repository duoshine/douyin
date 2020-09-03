package com.duoshine.douyin

import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainViewModel : ViewModel() {
    private val TAG = "MainActivity"
    private var toggle = true
    //刷新状态
    private var refreshState: MutableLiveData<RefreshState>? = null

     enum class RefreshState{
        START,SUCCESS,FAIL,COMPLETE
    }

    init {
        refreshState = MutableLiveData()
    }

    private val defaultUrls = listOf(
        "http://192.168.1.154:8080/douyin1.mp4",
        "http://192.168.1.154:8080/douyin.mp4",
        "http://192.168.1.154:8080/douyin2.mp4",
        "http://192.168.1.154:8080/douyin3.mp4",
        "http://192.168.1.154:8080/douyin4.mp4",
        "http://192.168.1.154:8080/douyin5.mp4",
        "http://192.168.1.154:8080/douyin6.mp4",
        "http://192.168.1.154:8080/douyin7.mp4",
        "http://192.168.1.154:8080/douyin8.mp4",
        "http://192.168.1.154:8080/douyin9.mp4",
        "http://192.168.1.154:8080/douyin10.mp4"
    )

    private val newUrls = listOf(
        "http://192.168.1.154:8080/douyin7.mp4",
        "http://192.168.1.154:8080/douyin1.mp4",
        "http://192.168.1.154:8080/douyin.mp4",
        "http://192.168.1.154:8080/douyin2.mp4",
        "http://192.168.1.154:8080/douyin3.mp4",
        "http://192.168.1.154:8080/douyin4.mp4",
        "http://192.168.1.154:8080/douyin5.mp4",
        "http://192.168.1.154:8080/douyin6.mp4",
        "http://192.168.1.154:8080/douyin8.mp4",
        "http://192.168.1.154:8080/douyin9.mp4",
        "http://192.168.1.154:8080/douyin10.mp4"
    )

    private val urls: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }

    fun urlsLiveData() = urls

    suspend fun requestUrls(){
        delay(1200)
        toggle = !toggle
        refreshState?.value = RefreshState.COMPLETE //先结束刷新
        delay(100)
        if (toggle) urls.value = newUrls else urls.value = defaultUrls
    }

    fun startRefresh() {
        refreshState?.value = RefreshState.START
    }

    fun getRefreshState(): MutableLiveData<RefreshState> {
        return refreshState!!
    }
}