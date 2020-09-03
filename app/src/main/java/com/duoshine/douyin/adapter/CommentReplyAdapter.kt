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
import com.duoshine.douyin.model.CommentModel
import com.duoshine.douyin.model.CommentReplyModel
import com.duoshine.douyin.model.Comments


class CommentReplyAdapter(private val context: Context) :
    RecyclerView.Adapter<CommentReplyAdapter.MyViewHolder>() {
    private var layoutInflater: LayoutInflater? = null
    private val TAG = "CommentReplyAdapter"

    private val comments = ArrayList<Comments>().apply {
        val comment = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 20, 9000, ArrayList<CommentReplyModel>()
        )
        add(comment)
        add(comment)
        add(comment)
        add(comment)
        add(comment)
        add(comment)
        add(comment)
        add(comment)
        add(comment)
        add(comment)
    }

    private val commentModel: CommentModel = CommentModel("0", 20, 1, true, comments)

    init {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentReplyAdapter.MyViewHolder {
        val view = layoutInflater?.inflate(R.layout.adapter_comment_reply_item, parent, false)
        return MyViewHolder(view!!)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        comments[position].apply {
            holder.commentUser?.text = name
            holder.commentContent?.text = content
            holder.commentDate?.text = date
            holder.commentStarCount?.text = starCount.toString()
            Glide
                .with(context)
                .load(R.mipmap.ic_launcher)
                .circleCrop()
                .into(holder.commentAvatar!!)
        }
        holder.itemView.setOnClickListener {
            Log.d(TAG, "我被点击啦$position")
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var commentAvatar: ImageView? = null
        var commentUser: TextView? = null
        var commentContent: TextView? = null
        var commentDate: TextView? = null
        var commentReply: TextView? = null
        var commentStarIcon: ImageView? = null
        var commentStarCount: TextView? = null

        init {
            commentAvatar = itemView.findViewById(R.id.comment_avatar)
            commentUser = itemView.findViewById(R.id.comment_user)
            commentContent = itemView.findViewById(R.id.comment_content)
            commentDate = itemView.findViewById(R.id.comment_date)
            commentReply = itemView.findViewById(R.id.comment_reply)
            commentStarIcon = itemView.findViewById(R.id.comment_star_icon)
            commentStarCount = itemView.findViewById(R.id.comment_star_count)
        }
    }

    private var expandedReplyListener : ExpandedReplyListener? = null

    //展开更多回复功能 由外部维护 将此点击事件回调
    interface ExpandedReplyListener{
        fun onClick(position:Int,recyclerView: RecyclerView)
    }

    fun setExpandedReplyListener(listener : ExpandedReplyListener) {
        expandedReplyListener = listener
    }
}