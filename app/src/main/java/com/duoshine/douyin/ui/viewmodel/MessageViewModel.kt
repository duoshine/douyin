package com.duoshine.douyin.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.duoshine.douyin.model.ConversationModel
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage


/**
 *Created by chen on 2020
 */
class MessageViewModel : ViewModel() {
    private val TAG = "MessageViewModel"

    private var conversation: MutableLiveData<ArrayList<ConversationModel>>? = null

    init {
        conversation = MutableLiveData<ArrayList<ConversationModel>>()
    }

    /**
     * 发送消息
     * userId：发送至用户
     * msg:发送内容
     */
    fun sendMessage(userId: String, msg: String) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        val message = EMMessage.createTxtSendMessage("长江长江，我是黄河", "chenad")
        //它需要在哎sendMessage前调用
        message.setMessageStatusCallback(object : EMCallBack {
            override fun onSuccess() {
                Log.d(TAG, "发送消息至${userId}成功")
            }

            override fun onProgress(p0: Int, p1: String?) {

            }

            override fun onError(p0: Int, p1: String?) {
                Log.d(TAG, "发送消息至${userId}失败:$p1")
            }
        })
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message)
    }

    /**
     * 获取所有聊天记录
     */
    fun getAllMessage(username: String) {
        val conversation =
            EMClient.getInstance().chatManager().getConversation(username)
        //获取此会话的所有消息
        val messages = conversation.allMessages
        Log.d(TAG, "获取此会话所有聊天记录:$messages")
    }

    /**
     * 获取所有会话
     */
    fun getAllConversations() {
        val conversations =
            EMClient.getInstance().chatManager().allConversations
        val iterator = conversations.iterator()
        val list = ArrayList<ConversationModel>()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val key = next.key
            val value = next.value
            list.add(ConversationModel(key, value))
        }
        //更新
        conversation?.postValue(list)
    }

    fun conversations() = conversation
}