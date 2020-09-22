package com.duoshine.douyin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.duoshine.douyin.R

/**
 *Created by chen on 2020
 */
class EmojiAdapter(
    private val context: Context,
    private val emojis: MutableList<MutableList<Int>>,
    private val layoutInflater: LayoutInflater
) :
    RecyclerView.Adapter<EmojiAdapter.MyViewHolder>() {
    private val TAG = "EmojiAdapter"

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var emojiListPagerItem: RecyclerView? = null

        init {
            emojiListPagerItem = itemView.findViewById(R.id.emoji_list_pager_item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = layoutInflater.inflate(R.layout.emoji_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return emojis.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.emojiListPagerItem?.layoutManager = GridLayoutManager(context, 7)
        holder.emojiListPagerItem?.adapter = EmojiRecyclerAdapter(position)
    }

    private var listener: ItemClickListener? = null

    interface ItemClickListener {
        fun click(position: Int, position1: Int)
    }

    fun setItemClickListener(listener: ItemClickListener) {
        this.listener = listener
    }

    inner class EmojiRecyclerAdapter(private val parentPosition:Int) : RecyclerView.Adapter<EmojiRecyclerAdapter.MyViewHolder>() {
        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var emoji_icon:ImageButton? = null

            init {
                emoji_icon =  itemView.findViewById(R.id.emoji_icon)
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): EmojiRecyclerAdapter.MyViewHolder {
            val view = layoutInflater.inflate(R.layout.emoji_pager_item, parent, false)
            return MyViewHolder(view)
        }

        override fun getItemCount(): Int {
            return emojis[parentPosition].size
        }

        override fun onBindViewHolder(holder: EmojiRecyclerAdapter.MyViewHolder, position: Int) {
            holder.emoji_icon?.setImageResource(emojis[parentPosition][position])
            holder.emoji_icon?.setOnClickListener {
                listener?.click(parentPosition,position)
            }
        }
    }
}