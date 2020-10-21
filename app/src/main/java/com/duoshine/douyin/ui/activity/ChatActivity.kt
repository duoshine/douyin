package com.duoshine.douyin.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.ChatAdapter
import com.duoshine.douyin.ui.viewmodel.ChatViewModel
import com.duoshine.douyin.widget.SoftKeyBoardListener
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import kotlinx.android.synthetic.main.activity_chat.*


/**
 * 聊天
 */

class ChatActivity : AppCompatActivity(), View.OnClickListener {


    private var messageList: MutableList<EMMessage> = ArrayList()

    private var username: String = ""

    private var viewModel: ChatViewModel? = null

    private val TAG = "ChatActivity"

    private var chatAdapter: ChatAdapter? = null

    private var msgListener: EMMessageListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        initView()
        //获取聊天记录
        getConversation()
        addMessageListener()
        addSoftKeyBoardListener()
    }

    private fun addSoftKeyBoardListener() {
        /**
        监听软键盘弹起 获取软键盘高度 将dialog移动到软键盘上方
         */
        SoftKeyBoardListener.setListener(this, object : SoftKeyBoardListener(this),
            SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
            override fun keyBoardShow(height: Int, rootViewVisibleHeight: Int) {
                Log.d(TAG, "keyBoardShow: 软键盘弹出")
//                height 为软件盘的高度   rootViewVisibleHeight为当前屏幕可是区域 不包括状态栏
                //软键盘弹出时 RecyclerView需要显示到底部
                scrollToPosition()
            }

            // height 为屏幕可视总高度 也就是软键盘隐藏后的全屏高度
            override fun keyBoardHide(height: Int) {
                Log.d(TAG, "keyBoardHide:软键盘隐藏 ")

            }
        })
    }

    //获取聊天记录
    private fun getConversation() {
        val messages = viewModel?.getAllMessage(username)
        messages?.run {
            messageList = this
            chatAdapter?.submitList(this)
        }
    }

    /**
     * 监听消息
     */
    private fun addMessageListener() {
        msgListener= object : EMMessageListener {
            override fun onMessageReceived(messages: List<EMMessage>) {
                //收到消息
                Log.d(TAG, "收到消息:$messages")
                runOnUiThread {
                    messageList.addAll(messages)
                    chatAdapter?.submitList(messageList)
                    scrollToPosition()
                }
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

    private fun initView() {
        send_msg_view.setOnClickListener(this)

        //设置聊天人
        val username = intent.getStringExtra(USERNAME_KEY) ?: "-"
        username_view.text = username
        this.username = username

        chatAdapter = ChatAdapter(this)
        val linearLayoutManager = LinearLayoutManager(this)
        //从底部填充内容，默认显示就是底部
        linearLayoutManager.stackFromEnd = true
        recycler_view.layoutManager = linearLayoutManager
        recycler_view.adapter = chatAdapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.send_msg_view -> sendMsg()
        }
    }

    //发送消息
    private fun sendMsg() {
        val msg = message_input.text.toString()
        if (TextUtils.isEmpty(msg)) {
            Toast.makeText(this, getString(R.string.chat_send_hint), Toast.LENGTH_SHORT).show()
            return
        }
        val message = viewModel?.sendMessage(username, msg)
        messageList.add(message!!)
        chatAdapter?.submitList(messageList)
        message_input.setText("")
        scrollToPosition()
    }

    /**
     * 在软键盘弹起时活着有新的消息加入适配器时 需要显示到底部
     */
    private fun scrollToPosition() {
        val position = chatAdapter!!.itemCount - 1
        if (position < 0) {
            return
        }
        recycler_view.scrollToPosition(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        EMClient.getInstance().chatManager().removeMessageListener(msgListener)
    }

    companion object {
        private val USERNAME_KEY: String = "USERNAME_KEY"

        fun start(context: Context, username: String) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(USERNAME_KEY, username)
            startActivity(context, intent, null)
        }
    }
}