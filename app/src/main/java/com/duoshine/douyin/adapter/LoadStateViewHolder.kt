package com.duoshine.douyin.adapter

import android.view.LayoutInflater
import android.view.View
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

class LoadStateViewHolder(
    private val retry: () -> Unit,
    itemView: View
) : RecyclerView.ViewHolder(itemView) {
    private var progressBar: ProgressBar? = null
    private var errorMsg: TextView? = null
    private var noData: TextView? = null
    private var retryBtn: Button? = null

    init {
        progressBar = itemView.findViewById(R.id.progress_bar)
        errorMsg = itemView.findViewById(R.id.error_msg)
        noData = itemView.findViewById(R.id.no_data)
        retryBtn = itemView.findViewById(R.id.retry_button)
    }

    fun bind(loadState: LoadState) {
        retryBtn?.setOnClickListener { retry() }

        if (loadState is LoadState.Error) {
            val error = loadState.error

            //当收到指定异常时表示数据已加载完毕 给用户一些提示
            if (error is NoMoreException) {
                progressBar?.isVisible = loadState is LoadState.Loading
                noData?.isVisible = true
                retryBtn?.isVisible = false
                errorMsg?.isVisible = loadState is LoadState.Error
                return
            }
            errorMsg?.text = error.localizedMessage
        }
        progressBar?.isVisible = loadState is LoadState.Loading
        noData?.isVisible = false
        retryBtn?.isVisible = loadState is LoadState.Error
        errorMsg?.isVisible = loadState is LoadState.Error
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
    ) = LoadStateViewHolder(
        retry, LayoutInflater.from(parent.context)
            .inflate(R.layout.load_state_item, parent, false)
    )

    override fun onBindViewHolder(
        holder: LoadStateViewHolder,
        loadState: LoadState
    ) = holder.bind(loadState)
}
