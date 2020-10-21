package com.duoshine.douyin.ui.activity

import android.os.Bundle
import android.system.Os.accept
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.duoshine.douyin.R
import com.hyphenate.EMContactListener
import com.hyphenate.chat.EMClient
import kotlinx.android.synthetic.main.activity_add_friend.*


/**
 * 添加好友
 */
class AddFriendActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "AddFriendActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)
        initView()
        addUserListener()
    }

    /**
     * 添加好友的监听
     */
    private fun addUserListener() {
        EMClient.getInstance().contactManager().setContactListener(object : EMContactListener {
            override fun onContactInvited(username: String, reason: String) {
                //收到好友邀请
                runOnUiThread {
                    add_user_layout.visibility = View.VISIBLE
                    username1.text = username
                    reason_view.text = reason
                }
            }

            override fun onContactDeleted(username: String) {
                //被删除时回调此方法
            }

            override fun onFriendRequestAccepted(p0: String?) {
                //好友请求被同意
            }

            override fun onContactAdded(username: String) {
                //增加了联系人时回调此方法
            }

            override fun onFriendRequestDeclined(p0: String?) {
                //好友请求被拒绝
            }
        })
    }

    private fun initView() {
        //查找好友
        add_friend_search.setOnClickListener(this)
        //添加好友
        add_friend_view.setOnClickListener(this)
        //同意好友请求
        add.setOnClickListener(this)

        //加载圆形头像
        Glide
            .with(this)
            .load(R.mipmap.avatar2)
            .circleCrop()
            .into(avatar_view)
        Glide
            .with(this)
            .load(R.mipmap.avatar1)
            .circleCrop()
            .into(avatar_view1)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add_friend_search -> searchUser()
            R.id.add_friend_view -> addUser()
            R.id.add -> accept()
        }
    }

    /**
     * 同意好友申请
     */
    private fun accept() {
        EMClient.getInstance().contactManager().acceptInvitation(username1.text.toString())
        Toast.makeText(this, "已同意", Toast.LENGTH_SHORT).show()
    }

    //添加用户
    private fun addUser() {
        Log.d(TAG, "添加好友：${username.text}")
        //参数为要添加的好友的username和添加理由
        EMClient.getInstance().contactManager().addContact(username.text.toString(), "你好，交个朋友吧！")
        Toast.makeText(this, "好友请求已发送", Toast.LENGTH_SHORT).show()
    }

    //    搜索用户
    private fun searchUser() {
        val userName = add_friend_input.text.toString().trim()
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, getString(R.string.add_friend_input_check), Toast.LENGTH_SHORT)
                .show()
            return
        }
        //显示View
        search_result_layout.visibility = View.VISIBLE
        //将搜索的用户名直接设置为搜索结果 环信不提供查找用户的功能，这里模拟搜索结果
        username.text = userName
    }
}