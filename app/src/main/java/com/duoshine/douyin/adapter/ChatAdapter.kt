package com.duoshine.douyin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duoshine.douyin.AppHelper
import com.duoshine.douyin.R
import com.duoshine.douyin.model.Chat
import com.duoshine.douyin.model.ChatMessageModel
import com.duoshine.douyin.model.CommentModel
import com.duoshine.douyin.model.Comments
import com.google.gson.Gson
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMMessageBody
import com.hyphenate.chat.EMTextMessageBody
import kotlinx.android.synthetic.main.activity_add_friend.*

/**
 *Created by chen on 2020
 */
class ChatAdapter(
    private val context: Context
) : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    private val chatMessageModel: MutableList<EMMessage> = ArrayList()

    fun submitList(newList: List<EMMessage>) {
        val diffUtil = DiffUtil.calculateDiff(ChatDiffCallback(chatMessageModel, newList))
        setOldList(newList)
        diffUtil.dispatchUpdatesTo(this)
    }

//    private fun getOldMessageList() = chatMessageModel

    private fun setOldList(oldList: List<EMMessage>) {
        chatMessageModel.clear()
        chatMessageModel.addAll(oldList)
    }

/*    fun getCopyOldList(): ArrayList<Comments> {
        val oldModel = CommentModel("", 1, 1, true, null)
        oldModel.comment = comments!!
        val stringProject = Gson().toJson(oldModel, CommentModel::class.java)
        val oldModel1 = Gson().fromJson(stringProject, CommentModel::class.java)
        return oldModel1.comment!!
    }*/


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_msg: TextView? = null
        var avatar: ImageView? = null

        init {
            tv_msg = itemView.findViewById(R.id.tv_msg)
            avatar = itemView.findViewById(R.id.avatar)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatMessageModel[position].from == AppHelper.getCurrentUserName()) {
            0
        } else {
            1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return if (viewType == 0) { //发送
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_chat_send_item, parent, false)
            MyViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_chat_receiver_item, parent, false)
            MyViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return chatMessageModel.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val body = chatMessageModel[position].body
        val msg = getBodyType(body)
        holder.tv_msg?.text = msg

        if (chatMessageModel[position].from == AppHelper.getCurrentUserName()) {
            //加载头像
            Glide
                .with(context)
                .load(R.mipmap.avatar2)
                .circleCrop()
                .into(holder.avatar!!)
        } else {
            //加载头像
            Glide
                .with(context)
                .load(R.mipmap.avatar1)
                .circleCrop()
                .into(holder.avatar!!)
        }
    }

    private fun getBodyType(var2: EMMessageBody): String {
        if (var2 is EMTextMessageBody) {
            return var2.message
        }
        return ""
    }
}