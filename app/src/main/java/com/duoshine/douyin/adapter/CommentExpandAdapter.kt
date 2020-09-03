package com.duoshine.douyin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.duoshine.douyin.R
import com.duoshine.douyin.model.CommentModel
import com.duoshine.douyin.model.Comments

/**
 *Created by chen on 2020
 */
class CommentExpandAdapter(private val context: Context, private val groups: ArrayList<Comments>) :
    BaseExpandableListAdapter() {
    private var mInflater: LayoutInflater? = null

    init {
        mInflater = LayoutInflater.from(context)
    }

    //获取组元素数目
    override fun getGroupCount(): Int {
        return groups.size
    }

    //获取子元素数目
    override fun getChildrenCount(groupPosition: Int): Int {
        return if (groups.size == 0) {
            0
        } else {
            groups[groupPosition].reply.size
        }
    }

    //获取组元素对象
    override fun getGroup(groupPosition: Int): Any? {
        return groups[groupPosition]
    }

    //获取子元素对象
    override fun getChild(i: Int, i1: Int): Any? {
        return null
    }

    //获取组元素Id
    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    //获取子元素Id
    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    //加载并显示组元素
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        viewGroup: ViewGroup?
    ): View? {
        var view: View? = null
        var groupHolder: GroupHolder? = null
        if (convertView != null) {
            view = convertView
            groupHolder = view.tag as GroupHolder
        } else {
            view = mInflater?.inflate(R.layout.adapter_comment_item, viewGroup, false)
            groupHolder = GroupHolder()
            groupHolder.commentAvatar = view!!.findViewById(R.id.comment_avatar)
            groupHolder.commentUser = view.findViewById(R.id.comment_user)
            groupHolder.commentContent = view.findViewById(R.id.comment_content)
            groupHolder.commentDate = view.findViewById(R.id.comment_date)
            groupHolder.commentReply = view.findViewById(R.id.comment_reply)
            groupHolder.commentStarIcon = view.findViewById(R.id.comment_star_icon)
            groupHolder.commentStarCount = view.findViewById(R.id.comment_star_count)
            groupHolder.commentReplyExpanded = view.findViewById(R.id.comment_reply_expanded)
            view.tag = groupHolder
        }

        groups[groupPosition].apply {
            groupHolder.commentUser?.text = name
            groupHolder.commentContent?.text = content
            groupHolder.commentDate?.text = date
            groupHolder.commentStarCount?.text = starCount.toString()
            //如果有评论回复 需要显示展开动作
            if (replyCount > 0) {
                groupHolder.commentReplyExpanded?.visibility = View.VISIBLE
                groupHolder.commentReplyExpanded?.text = "展开${replyCount}条回复"
            } else {
                groupHolder.commentReplyExpanded?.visibility = View.GONE
            }
            Glide
                .with(context)
                .load(R.mipmap.ic_launcher)
                .circleCrop()
                .into(groupHolder.commentAvatar!!)
        }
        return view
    }

    //加载子元素并显示
    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        viewGroup: ViewGroup?
    ): View? {
        var view: View? = null
        var childHolder: ChildHolder? = null
        if (convertView != null) {
            view = convertView
            childHolder = view.tag as ChildHolder
        } else {
            view = mInflater?.inflate(R.layout.adapter_comment_item, viewGroup, false)
            childHolder = ChildHolder()
            childHolder.commentAvatar = view!!.findViewById(R.id.comment_avatar)
            childHolder.commentUser = view.findViewById(R.id.comment_user)
            childHolder.commentContent = view.findViewById(R.id.comment_content)
            childHolder.commentDate = view.findViewById(R.id.comment_date)
            childHolder.commentReply = view.findViewById(R.id.comment_reply)
            childHolder.commentStarIcon = view.findViewById(R.id.comment_star_icon)
            childHolder.commentStarCount = view.findViewById(R.id.comment_star_count)
            view.tag = childHolder
        }

        groups[groupPosition].apply {
            childHolder.commentUser?.text = name
            childHolder.commentContent?.text = content
            childHolder.commentDate?.text = date
            childHolder.commentStarCount?.text = starCount.toString()
            Glide
                .with(context)
                .load(R.mipmap.ic_launcher)
                .circleCrop()
                .into(childHolder.commentAvatar!!)
        }
        return view
    }

    override fun isChildSelectable(i: Int, i1: Int): Boolean {
        return true
    }

    //组holder
    class GroupHolder {
        var commentAvatar: ImageView? = null
        var commentUser: TextView? = null
        var commentContent: TextView? = null
        var commentDate: TextView? = null
        var commentReply: TextView? = null
        var commentStarIcon: ImageView? = null
        var commentStarCount: TextView? = null
        var commentReplyExpanded: TextView? = null
    }

    //子holder
    class ChildHolder() {
        var commentAvatar: ImageView? = null
        var commentUser: TextView? = null
        var commentContent: TextView? = null
        var commentDate: TextView? = null
        var commentReply: TextView? = null
        var commentStarIcon: ImageView? = null
        var commentStarCount: TextView? = null
    }
}