package com.duoshine.douyin.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.CityAdapter
import com.duoshine.douyin.adapter.LoadStateAdapter
import com.duoshine.douyin.ui.viewmodel.CityViewModel
import kotlinx.android.synthetic.main.fragment_city.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 *Created by chen on 2020 同城
 */
class CityFragment : BaseFragment() {

    private val TAG = "CityFragment"

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_city, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initJzvd()
    }

    private fun initJzvd() {
        val viewModel = ViewModelProvider(this).get(CityViewModel::class.java)
        val gridLayoutManager = GridLayoutManager(context, 2)

        /**
         * 使用RecyclerView的网格布局加多条目展示
         */
        gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val itemCount = recyclerView.adapter?.itemCount ?: -1
                if (itemCount == -1) {
                    return 1
                }
                //最后一个item占用全部
                if (position == itemCount - 1) {
                    return gridLayoutManager.spanCount
                }
                return 1
            }
        }
        recyclerView.layoutManager = gridLayoutManager


        val adapterVideoList = CityAdapter(CityAdapter.UserComparator)

        val parent = parentFragment as? UserInfoFragment
        parent?.addRecyclerViewScrollListener(recyclerView)

        lifecycleScope.launch {
            viewModel.flow.collectLatest { pagingData ->
                adapterVideoList.submitData(pagingData)
            }
        }

        /**
         *添加footer
         */
        val withLoadStateFooter =
            adapterVideoList.withLoadStateFooter(footer = LoadStateAdapter {
                //请求出错后重试
                Log.d("duo_shine", "retry")
            })

        recyclerView.adapter = withLoadStateFooter
    }
}