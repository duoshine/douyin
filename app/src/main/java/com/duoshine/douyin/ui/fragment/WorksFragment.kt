package com.duoshine.douyin.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.ContentAdapter
import com.duoshine.douyin.widget.CRecyclerView
import kotlinx.android.synthetic.main.works_fragment.*

//作品
class WorksFragment : Fragment() {
    private val TAG = "WorksFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.works_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(context)
        recycler_view.layoutManager = layoutManager
        val adapter = ContentAdapter()
        recycler_view.adapter = adapter

        val parent = parentFragment as? UserInfoFragment
        parent?.addRecyclerViewScrollListener(recycler_view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
       recycler_view.clearOnScrollListeners()
    }
}