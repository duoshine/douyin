package com.duoshine.douyin.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duoshine.douyin.R
import com.duoshine.douyin.model.CommentModel
import com.duoshine.douyin.model.CommentReply
import com.duoshine.douyin.model.CommentReplyModel
import com.duoshine.douyin.model.Comments


class CommentAdapter(private val context: Context) :
    RecyclerView.Adapter<CommentAdapter.MyViewHolder>() {
    private var layoutInflater: LayoutInflater? = null
    private val TAG = "CommentAdapter"

    private val comments = ArrayList<Comments>().apply {
        val comment1 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, ArrayList<CommentReply>()
        )
        val comment2 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, ArrayList<CommentReply>()
        )
        val comment3 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, ArrayList<CommentReply>()
        )
        val comment4 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, ArrayList<CommentReply>()
        )
        val comment5 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, ArrayList<CommentReply>()
        )
        val comment6 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, ArrayList<CommentReply>()
        )
        val comment7 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, ArrayList<CommentReply>()
        )
        val comment8 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, ArrayList<CommentReply>()
        )
        val comment9 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, ArrayList<CommentReply>()
        )
        val comment10 = Comments(
            "0", "", "duo_shine", "写抖音需要自制力",
            "2020-09-03 22:00:00", 1, false, 9000, ArrayList<CommentReply>()
        )
        add(comment1)
        add(comment2)
        add(comment3)
        add(comment4)
        add(comment5)
        add(comment6)
        add(comment7)
        add(comment8)
        add(comment9)
        add(comment10)
    }

    init {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.MyViewHolder {
        val view = layoutInflater?.inflate(R.layout.adapter_comment_item, parent, false)
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
            //如果有评论回复 需要显示展开动作
            if (replyCount > 0) {
                holder.commentReplyExpanded?.visibility = View.VISIBLE
                holder.commentReplyExpanded?.text = "展开${replyCount}条回复"
            } else {
                holder.commentReplyExpanded?.visibility = View.GONE
            }
            Glide
                .with(context)
                .load(R.mipmap.ic_launcher)
                .circleCrop()
                .into(holder.commentAvatar!!)
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
        var commentReplyExpanded: TextView? = null

        init {
            commentAvatar = itemView.findViewById(R.id.comment_avatar)
            commentUser = itemView.findViewById(R.id.comment_user)
            commentContent = itemView.findViewById(R.id.comment_content)
            commentDate = itemView.findViewById(R.id.comment_date)
            commentReply = itemView.findViewById(R.id.comment_reply)
            commentStarIcon = itemView.findViewById(R.id.comment_star_icon)
            commentStarCount = itemView.findViewById(R.id.comment_star_count)
            commentReplyExpanded = itemView.findViewById(R.id.comment_reply_expanded)
        }
    }
}