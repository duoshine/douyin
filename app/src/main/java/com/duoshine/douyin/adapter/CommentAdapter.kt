package com.duoshine.douyin.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.duoshine.douyin.R
import com.duoshine.douyin.constants.EmojiConstants
import com.duoshine.douyin.model.CommentModel
import com.duoshine.douyin.model.Comments
import com.duoshine.douyin.widget.FullHeightSpan
import com.google.gson.Gson
import kotlin.collections.ArrayList


class CommentAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var layoutInflater: LayoutInflater? = null
    private val TAG = "CommentAdapter"
    private var comments: ArrayList<Comments>? = null
    private var diffUtil: DiffUtil.DiffResult? = null

    companion object {
        val COMMENT = 1
        val COMMENT_REPLY = 2
        val COMMENT_LOADING = 3
        val COMMENT_REPLY_LOADING = 4
    }

    init {
        layoutInflater = LayoutInflater.from(context)
        comments = ArrayList()
    }

    override fun getItemViewType(position: Int): Int {
        return when (comments!![position].itemType) {
            COMMENT -> {
                COMMENT  //一级评论
            }
            COMMENT_REPLY -> {
                COMMENT_REPLY  //二级回复
            }
            COMMENT_LOADING -> {
                COMMENT_LOADING  //评论加载
            }
            else -> {
                COMMENT_REPLY_LOADING  //回复加载
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            COMMENT -> {
                val view = layoutInflater?.inflate(R.layout.adapter_comment_item, parent, false)
                MyViewHolder(view!!)
            }
            COMMENT_REPLY -> {
                val view =
                    layoutInflater?.inflate(R.layout.adapter_comment_reply_item, parent, false)
                ReplyViewHolder(view!!)
            }
            COMMENT_LOADING -> {
                val view =
                    layoutInflater?.inflate(R.layout.adapter_comment_loading_item, parent, false)
                CommentLoadingViewHolder(view!!)
            }
            else -> {
                val view =
                    layoutInflater?.inflate(
                        R.layout.adapter_comment_reply_loading_item,
                        parent,
                        false
                    )
                ReplyLoadingViewHolder(view!!)
            }
        }
    }

    fun submitList(
        oldList: List<Comments>,
        newList: List<Comments>?
    ) {
        diffUtil = DiffUtil.calculateDiff(CommentDiffCallback(oldList, newList))
        if (newList != null) {
            setOldList(newList)
        } else {
            setOldList(oldList)
        }
        diffUtil?.dispatchUpdatesTo(this)
    }

    private fun setOldList(oldList: List<Comments>) {
        comments!!.clear()
        comments!!.addAll(oldList)
    }

    fun getOldList() = comments!!

    fun getCopyOldList(): ArrayList<Comments> {
        val oldModel = CommentModel("", 1, 1, true, null)
        oldModel.comment = comments!!
        val stringProject = Gson().toJson(oldModel, CommentModel::class.java)
        val oldModel1 = Gson().fromJson(stringProject, CommentModel::class.java)
        return oldModel1.comment!!
    }

    override fun getItemCount(): Int {
        return comments!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val comment = comments!![position]
        when (holder) {
            is MyViewHolder -> bindComment(holder, comment)
            is ReplyViewHolder -> bindReply(holder, comment)
            is CommentLoadingViewHolder -> bindCommentLoading(holder, comment)
            is ReplyLoadingViewHolder -> bindReplyLoading(holder, comment, position)
        }
    }

    /**
     * 0 不显示
     * 1 加载中
     * 2 加载出错
     * 3 已经没有更多了
     */
    private fun bindCommentLoading(
        holder: CommentLoadingViewHolder,
        comment: Comments
    ) {
        val itemState = comment.itemState
        when (itemState) {
            0 -> {
                holder.moreReply?.visibility = View.GONE
                holder.animation_view?.visibility = View.GONE
            }
            1 -> {
                holder.moreReply?.visibility = View.GONE
                holder.animation_view?.visibility = View.VISIBLE
                holder.animation_view?.playAnimation()
            }
            2 -> {
                holder.moreReply?.visibility = View.VISIBLE
                holder.animation_view?.visibility = View.GONE
                holder.moreReply?.text = "加载出错，请重试"
            }
            3 -> {
                holder.moreReply?.visibility = View.VISIBLE
                holder.animation_view?.visibility = View.GONE
                holder.moreReply?.text = "已经没有更多了"
            }
        }

        holder.moreReply?.setOnClickListener {
            if (itemState == 2) {
                //收起  获取父id 删除所有满足的子
                addReplyExpandedListener?.retry()
            }
        }
    }

    /**
     * 0.不显示
     * 1.加载中
     * 2.收起
     * 3.加载更多
     *
     */
    private fun bindReplyLoading(holder: ReplyLoadingViewHolder, comment: Comments, position: Int) {
        val itemState = comment.itemState
        when (itemState) {
            0 -> {
                holder.moreReply?.visibility = View.GONE
                holder.animation_view?.visibility = View.GONE
            }
            1 -> {
                holder.moreReply?.visibility = View.GONE
                holder.animation_view?.visibility = View.VISIBLE
                holder.animation_view?.playAnimation()
            }
            2 -> {
                holder.moreReply?.visibility = View.VISIBLE
                holder.animation_view?.visibility = View.GONE
                holder.moreReply?.text = "收起"
            }
            3 -> {
                holder.moreReply?.visibility = View.VISIBLE
                holder.animation_view?.visibility = View.GONE
                holder.moreReply?.text = "展开更多回复"
            }
        }

        holder.moreReply?.setOnClickListener {
            val parentId = comment.parentId!!
            if (itemState == 2) {
                //收起  获取父id 删除所有满足的子
                addReplyExpandedListener?.close(parentId)
            } else if (itemState == 3) {
                //展开更多回复
                addReplyExpandedListener?.loadReply(position, parentId)
            }
        }
    }

    private fun bindReply(
        holder: ReplyViewHolder,
        comment: Comments
    ) {
        comment.apply {
            holder.commentUser?.text = name
            holder.commentContent?.text = content
            holder.commentDate?.text = date
            holder.commentStarCount?.text = starCount.toString()
            Glide
                .with(context)
                .load(R.mipmap.avatar2)
                .circleCrop()
                .into(holder.commentAvatar!!)

            holder.commentReply?.setOnClickListener {
                addReplyExpandedListener?.replyClick(holder.bindingAdapterPosition, name)
            }
        }
    }

    private fun bindComment(holder: MyViewHolder, comment: Comments) {
        comment.apply {
            holder.commentUser?.text = name
            holder.commentContent?.setText(content)

            //从表情中查找对应的编码后转换并显示
            val codeMap = EmojiConstants.codeMap
            for ((index, value) in content.withIndex()) {
                val exist = codeMap.containsKey(value.toString())
                Log.d(TAG, "append: 执行位置$exist")
                if (exist) {
                    val resId = codeMap[value.toString()]
                    append(resId!!, holder.commentContent!!, index)
                }
            }

            holder.commentDate?.text = date
            holder.commentStarCount?.text = starCount.toString()
            //如果有评论回复 需要显示展开动作
            if (replyCount > 0 && !replyExpanded) {
                holder.commentReplyExpanded?.visibility = View.VISIBLE
                holder.commentReplyExpanded?.text = "展开${replyCount}条回复"
            } else {
                holder.commentReplyExpanded?.visibility = View.GONE
            }
            Glide
                .with(context)
                .load(R.mipmap.avatar1)
                .circleCrop()
                .into(holder.commentAvatar!!)
            //展开回复的点击事件 加载此评论的回复
            holder.commentReplyExpanded?.setOnClickListener {
                val newPosition = holder.adapterPosition
                addReplyExpandedListener?.expand(newPosition, commentId)
            }

            holder.commentReply?.setOnClickListener {
                addReplyExpandedListener?.replyClick(holder.bindingAdapterPosition, name)
            }
        }
    }

    private fun append(id: Int, view: EditText, where: Int) {
        // 随机产生1至9的整数
        try {
            val resourceId: Int = id
            // 根据资源ID获得资源图像的Bitmap对象
            val bitmap = BitmapFactory.decodeResource(
                context.resources,
                resourceId
            )
            // 根据Bitmap对象创建ImageSpan对象
            val imageSpan = FullHeightSpan(context, bitmap)
            // 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
            val spannableString = SpannableString(where.toString()) //d是占位符 表情会占用一个字符 在下面会使用表情替换该字符
            // 用ImageSpan对象替换face,注意这里的0，1表示的是f的长度，因为图片名是f1...f2,如果是face1,face2那么这里就是0，4
            spannableString.setSpan(
                imageSpan, 0, 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // 将像追加到控件
            val editTable = view.text
            editTable.replace(where, where + 1, spannableString)
        } catch (e: Exception) {
            Log.d(TAG, "append: $e")
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var commentAvatar: ImageView? = null
        var commentUser: TextView? = null
        var commentContent: EditText? = null
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

    class ReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    class ReplyLoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var moreReply: TextView? = null
        var animation_view: LottieAnimationView? = null

        init {
            moreReply = itemView.findViewById(R.id.more_reply)

            animation_view = itemView.findViewById(R.id.animation_view)
            //设置加载的json文件所用到的资源目录
            animation_view!!.imageAssetsFolder = "images/"
        }
    }

    class CommentLoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var moreReply: TextView? = null
        var animation_view: LottieAnimationView? = null

        init {
            moreReply = itemView.findViewById(R.id.more_reply)

            animation_view = itemView.findViewById(R.id.animation_view)
            //设置加载的json文件所用到的资源目录
            animation_view!!.imageAssetsFolder = "images/"
        }
    }

    private var addReplyExpandedListener: AddReplyExpandedListener? = null

    interface AddReplyExpandedListener {
        /**
         * 当回复被展开时
         */
        fun expand(position: Int, commentId: String)

        /**
         * 当回复被收起时
         */
        fun close(parentId: String)

        /**
         * 加载更多回复
         */
        fun loadReply(position: Int, commentId: String)

        /**
         * 上一次加载出错 手动点击重新加载
         */
        fun retry()

        /**
         * 回复被点击时
         * replyPerson  回复某人的评论
         */
        fun replyClick(position: Int, replyPerson: String)
    }

    fun addReplyExpandedListener(listener: AddReplyExpandedListener) {
        addReplyExpandedListener = listener
    }
}