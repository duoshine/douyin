package com.duoshine.douyin.adapter

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.duoshine.douyin.model.Comments
import com.hyphenate.chat.EMMessage

/**
 *Created by chen on 2020
 */
class ChatDiffCallback(
    private val oldList: List<EMMessage>,
    private val newList: List<EMMessage>
) : DiffUtil.Callback() {

    private val TAG = "ChatDiffCallback"
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldContentId = oldList[oldItemPosition].msgId
        val newContentId = newList[newItemPosition].msgId
        return oldContentId == newContentId
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}