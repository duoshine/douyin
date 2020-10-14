package com.duoshine.douyin.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.jzvd.Jzvd
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.WorkAdapter
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
        initJzvd()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView.clearOnScrollListeners()
    }

    private fun initJzvd() {
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        val adapterVideoList = WorkAdapter(context,0)
        recyclerView.adapter = adapterVideoList
        val parent = parentFragment as? UserInfoFragment
        parent?.addRecyclerViewScrollListener(recyclerView)
        recyclerView.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {}
            override fun onChildViewDetachedFromWindow(view: View) {
                val jzvd: Jzvd = view.findViewById(R.id.videoplayer)
                if (jzvd != null && Jzvd.CURRENT_JZVD != null &&
                    jzvd.jzDataSource.containsTheUrl(Jzvd.CURRENT_JZVD.jzDataSource.currentUrl)
                ) {
                    if (Jzvd.CURRENT_JZVD != null && Jzvd.CURRENT_JZVD.screen != Jzvd.SCREEN_FULLSCREEN) {
                        Jzvd.releaseAllVideos()
                    }
                }
            }
        })
    }
}