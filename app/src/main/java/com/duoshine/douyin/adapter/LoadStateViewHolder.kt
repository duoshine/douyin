package com.duoshine.douyin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duoshine.douyin.R
import com.duoshine.douyin.data.NoMoreException
import com.duoshine.douyin.databinding.LoadStateItemBinding

class LoadStateViewHolder(
    parent: ViewGroup,
    retry: () -> Unit
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.load_state_item, parent, false)
) {
    private val binding = LoadStateItemBinding.bind(itemView)
    private val progressBar: ProgressBar = binding.progressBar
    private val errorMsg: TextView = binding.errorMsg
    private val noData: TextView = binding.noData
    private val retry: Button = binding.retryButton
        .also {
            it.setOnClickListener { retry() }
        }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            val error = loadState.error

            //当收到指定异常时表示数据已加载完毕 给用户一些提示
            if (error is NoMoreException) {
                progressBar.isVisible = loadState is LoadState.Loading
                noData.isVisible = true
                retry.isVisible = false
                errorMsg.isVisible = loadState is LoadState.Error
                return
            }
            errorMsg.text = error.localizedMessage
        }
        progressBar.isVisible = loadState is LoadState.Loading
        noData.isVisible = false
        retry.isVisible = loadState is LoadState.Error
        errorMsg.isVisible = loadState is LoadState.Error
    }
}

// Adapter that displays a loading spinner when
// state = LoadState.Loading, and an error message and retry
// button when state is LoadState.Error.
class LoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<LoadStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ) = LoadStateViewHolder(parent, retry)

    override fun onBindViewHolder(
        holder: LoadStateViewHolder,
        loadState: LoadState
    ) = holder.bind(loadState)
}
