package com.duoshine.douyin.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.duoshine.douyin.model.Comments

/**
 *Created by chen on 2020
 */
class CommentDiffCallback(
    private val oldList: List<Comments>,
    private val newList: List<Comments>?
) : DiffUtil.Callback() {

    private val TAG = "CommentDiffCallback"
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldContentId = oldList[oldItemPosition].commentId
        val newContentId = newList?.get(newItemPosition)?.commentId
        Log.d(TAG, "areItemsTheSame: ---------")
        Log.d(TAG, "oldContentId:位置$oldItemPosition id:$oldContentId")
        Log.d(TAG, "newContentId:位置$newItemPosition id:$newContentId")
        Log.d(TAG, "areItemsTheSame: ---------")
        Log.d(TAG, "                                                  ")
        return oldContentId == newContentId
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList?.size ?: 0
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//        val oldComments = oldList[oldItemPosition]
//        val newComments = newList?.get(newItemPosition)
//        return oldComments.replyExpanded == newComments?.replyExpanded
        return oldList[oldItemPosition]== newList?.get(newItemPosition)
    }
}