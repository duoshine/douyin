package com.duoshine.douyin.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duoshine.douyin.R
import com.duoshine.douyin.model.ConversationModel
import com.hyphenate.chat.*
import com.hyphenate.chat.adapter.message.*
import kotlinx.android.synthetic.main.fragment_userinfo.*

/**
 *Created by chen on 2020
 */
class MessageAdapter(
    private val context: Context,
    private val conversation: ArrayList<ConversationModel>
) :
    RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {

    private var listener: ItemClickListener? = null

    fun setOnClickListener(listener: ItemClickListener) {
        this.listener = listener
    }

    private val TAG = "MessageAdapter"

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView? = null
        var content: TextView? = null
        var avatar_view: ImageView? = null

        init {
            username = itemView.findViewById(R.id.username)
            content = itemView.findViewById(R.id.content)
            avatar_view = itemView.findViewById(R.id.avatar_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_message_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return conversation.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val conversation = conversation[position]
        holder.username?.text = conversation.username
        val lastMessage = conversation.EMConversation.lastMessage
        val body = lastMessage.body
        holder.content?.text = getBodyType(body)

        //加载圆形头像
        Glide
            .with(context)
            .load(R.mipmap.avatar2)
            .circleCrop()
            .into(holder.avatar_view!!)

        holder.itemView.setOnClickListener {
            listener?.onClick(position)
        }
    }

    fun getBodyType(var2: EMMessageBody): String? {
        if (var2 is EMTextMessageBody) {
            return var2.message
        }
        return ""
    }
}