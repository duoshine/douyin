package com.duoshine.douyin.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.duoshine.douyin.R

/**
 *Created by chen on 2020
 */
class ContentAdapter : RecyclerView.Adapter<ContentAdapter.MyViewHolder>() {

    private val datas = ArrayList<String>().also {
        for (title in 0..50) {
            it.add(title.toString())
        }
    }

    private val TAG = "ContentAdapter"

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView? = null

        init {
            title = itemView.findViewById(R.id.title)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        Log.d(TAG, "onCreateViewHolder")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            Log.d(TAG, "setOnClickListener:$position")
        }
        Log.d(TAG, "onBindViewHolder")
    }
}