package com.duoshine.douyin.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage


/**
 *Created by chen on 2020
 */
class ChatViewModel : ViewModel() {

    private val TAG = "MessageViewModel"

    private val messageLiveData: MutableLiveData<Int> = MutableLiveData()

    companion object {
        val START = 0
        val SUCCESS = 1
        val FAIL = 2
    }

    /**
     * 发送消息
     * username：发送至用户
     * msg:发送内容
     */
    fun sendMessage(username: String, msg: String):EMMessage {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        val message = EMMessage.createTxtSendMessage(msg, username)
        Log.d(TAG, "消息id：${message.msgId}")
        //它需要在哎sendMessage前调用
        message.setMessageStatusCallback(object : EMCallBack {
            override fun onSuccess() {
                Log.d(TAG, "发送消息至${username}成功")
            }

            override fun onProgress(p0: Int, p1: String?) {

            }

            override fun onError(p0: Int, p1: String?) {
                Log.d(TAG, "发送消息至${username}失败:$p1")
            }
        })
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message)
        return message
    }

    fun getMessageLiveData() = messageLiveData

    /**
     * 获取聊天记录
     */
    fun getAllMessage(username: String): MutableList<EMMessage>? {
        //先获取最后一条消息  获取其消息id 再根据消息id获取更多消息
        val conversation =
            EMClient.getInstance().chatManager().getConversation(username)
        val lastMessage = conversation.lastMessage
        val all = conversation.loadMoreMsgFromDB(lastMessage.msgId, 50)
        all.add(lastMessage)
        //获取此会话的所有消息
        return all
    }
}