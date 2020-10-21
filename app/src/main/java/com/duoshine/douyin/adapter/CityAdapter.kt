package com.duoshine.douyin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.bumptech.glide.Glide
import com.duoshine.douyin.R
import com.duoshine.douyin.model.CityModel
import com.duoshine.douyin.util.Urls

class CityAdapter(diffCallback: DiffUtil.ItemCallback<CityModel>) :
    PagingDataAdapter<CityModel, CityAdapter.UserViewHolder>(diffCallback) {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var jzvdStd: JzvdStd? = null

        init {
            jzvdStd = itemView.findViewById(R.id.videoplayer)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_videoview_city, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val cityModel = getItem(position)
        holder.jzvdStd?.let {
            it.setUp(
                cityModel!!.videoUrl,
                cityModel.videoStar.toString(), Jzvd.SCREEN_NORMAL
            )
            Glide.with(it.context).load(cityModel.placeholderUrl)
                .into(it.posterImageView)

            it.posterImageView.setOnClickListener(View.OnClickListener { })
        }
    }

    object UserComparator : DiffUtil.ItemCallback<CityModel>() {
        override fun areItemsTheSame(oldItem: CityModel, newItem: CityModel): Boolean {
            // Id is unique.
            return oldItem.flag == newItem.flag
        }

        override fun areContentsTheSame(oldItem: CityModel, newItem: CityModel): Boolean {
            return oldItem == newItem
        }
    }
}