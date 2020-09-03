package com.duoshine.douyin.adapter

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.MyViewHolder>() {

    private val comments  = listOf<String>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
      return  comments.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

    }
}