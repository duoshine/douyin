package com.duoshine.douyin.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import com.duoshine.douyin.R
import com.hyphenate.chat.EMClient
import kotlinx.android.synthetic.main.activity_friend.*
import kotlin.concurrent.thread


/**
 * 显示好友列表
 */

class FriendActivity : AppCompatActivity(), View.OnClickListener, AdapterView.OnItemClickListener {

    private var adapter: ArrayAdapter<String>? = null

    private var friends: MutableList<String> = ArrayList()

    private val TAG = "FriendActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)
        initView()
    }

    override fun onResume() {
        super.onResume()
        thread {
            initFriendList()
        }
    }

    //获取好友列表
    private fun initFriendList() {
        val usernames =
            EMClient.getInstance().contactManager().allContactsFromServer
        Log.d(TAG, "获取到好友列表：$usernames")
        runOnUiThread {
            friends.clear()
            friends.addAll(usernames)
            if (adapter == null) {
                adapter = ArrayAdapter(this, R.layout.simple_list_item_1, friends)
                list_view.adapter = adapter
            } else {
                adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun initView() {
        add_friend.setOnClickListener(this)
        list_view.onItemClickListener = this
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add_friend -> startActivity(Intent(this, AddFriendActivity::class.java))
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        ChatActivity.start(this, friends[position])
    }
}