package com.duoshine.douyin.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.ItemClickListener
import com.duoshine.douyin.adapter.MessageAdapter
import com.duoshine.douyin.model.ConversationModel
import com.duoshine.douyin.ui.activity.ChatActivity
import com.duoshine.douyin.ui.activity.FriendActivity
import com.duoshine.douyin.ui.viewmodel.MessageViewModel
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import kotlinx.android.synthetic.main.fragment_message.*


/**
 *Created by chen on 2020  消息
 */
class MessageFragment : BaseFragment(), View.OnClickListener {

    private var msgListener: EMMessageListener? = null

    private val TAG = "MessageFragment"

    private var viewModel: MessageViewModel? = null

    private val conversation: ArrayList<ConversationModel> = ArrayList()

    private var conversationAdapter: MessageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
//        监听接收消息
        initMessageListener()
//        获取会话监听
        addConversationListener()
    }

    private fun addConversationListener() {
        //获取所有会话
        viewModel?.conversations()?.observe(this, Observer {
            conversation.clear()
            conversation.addAll(it)
            conversationAdapter?.notifyDataSetChanged()
        })
        viewModel?.getAllConversations()
    }

    private fun initView() {
        //好友
        friend.setOnClickListener(this)

        recycler_view.layoutManager = LinearLayoutManager(context)
        conversationAdapter = MessageAdapter(context!!, conversation)
        recycler_view.adapter = conversationAdapter

        //item的点击事件
        conversationAdapter?.setOnClickListener(object : ItemClickListener {
            override fun onClick(position: Int) {
                //获取from用户名
                val username = conversation[position].username
                ChatActivity.start(context!!, username)
            }
        })
    }

    //监听消息
    private fun initMessageListener() {
        msgListener = object : EMMessageListener {
            override fun onMessageReceived(messages: List<EMMessage>) {
                //收到消息
                Log.d(TAG, "收到消息:$messages")
                //获取到新信息后 更新会话
                viewModel?.getAllConversations()
            }

            override fun onCmdMessageReceived(messages: List<EMMessage>) {
                //收到透传消息
                Log.d(TAG, "收到透传消息:$messages")
            }

            override fun onMessageRead(messages: List<EMMessage>) {
                //收到已读回执
                Log.d(TAG, "收到已读回执:$messages")
            }

            override fun onMessageDelivered(messages: List<EMMessage>) {
                //收到已送达回执
                Log.d(TAG, "收到已送达回执:$messages")
            }

            override fun onMessageRecalled(messages: List<EMMessage>) {
                //消息被撤回
                Log.d(TAG, "消息被撤回:$messages")
            }

            override fun onMessageChanged(message: EMMessage, change: Any) {
                //消息状态变动
                Log.d(TAG, "消息状态变动:$message")
            }
        }
        EMClient.getInstance().chatManager().addMessageListener(msgListener)
    }

    override fun onDestroy() {
        super.onDestroy()
//        记得在不需要的时候移除listener，如在activity的onDestroy()时
        EMClient.getInstance().chatManager().removeMessageListener(msgListener)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.friend -> startActivity(Intent(context, FriendActivity::class.java))
        }
    }
}