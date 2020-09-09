package com.duoshine.douyin.test


import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.duoshine.douyin.MainViewModel
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.CommentAdapter
import com.duoshine.douyin.widget.SoftKeyBoardListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_recycler_test.*
import kotlinx.coroutines.launch


class RecyclerActivity : AppCompatActivity() {
    private val TAG = "RecyclerActivity"
    private var loadToggle = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_test)

        //设置加载的json文件所用到的资源目录
        //animation_view.imageAssetsFolder = "digg_heart_bubbles/images"
        val outMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels
        val heightPixels = outMetrics.heightPixels

        val bottomSheetDialog = BottomSheetDialog(this, R.style.commentList)
        val behavior = bottomSheetDialog.behavior
        val view =
            layoutInflater.inflate(R.layout.player_comment_list_dialog, null, false)
        val bottomSheetHeight = (heightPixels * 0.8).toInt()
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            bottomSheetHeight //设置自定义的view的高度为屏幕高度的百分之80，不设置的话其父默认是wrap_content
        )
        //设置底部bottomSheet的高度 如果不设置默认为 16:9  这里设置为屏幕高度的百分之80
        behavior.peekHeight = bottomSheetHeight

        bottomSheetDialog.setContentView(view, params)

        btn_show_dialog.setOnClickListener {
            bottomSheetDialog.show()
        }

        val adapter = CommentAdapter(this)
        val bottomSheetCommentList =
            view.findViewById<RecyclerView>(R.id.bottom_sheet_comment_list)
        bottomSheetCommentList.setHasFixedSize(true)
        bottomSheetCommentList.layoutManager = LinearLayoutManager(this)
        bottomSheetCommentList.adapter = adapter
        val mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //加载评论
        lifecycleScope.launch {
            mainViewModel.getComments(adapter.getCopyOldList()).observe(this@RecyclerActivity,
                Observer {
                    Log.d(TAG, "已更新数据")
                    if (adapter.getOldList().size == 0) {
                        adapter.submitList(it, null)
                    } else {
                        adapter.submitList(adapter.getOldList(), it)
                    }
                    loadToggle = false
                })
        }

        //监听recyclerView的滑动 满足条件出发加载更多
        bottomSheetCommentList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.layoutManager is LinearLayoutManager) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val itemCount = layoutManager.itemCount
                    val findLastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    //20  15
                    if (itemCount - findLastVisibleItemPosition <= 5 && !loadToggle) {
                        loadToggle = true
                        lifecycleScope.launch {
                            mainViewModel.getComments(adapter.getCopyOldList())
                        }
                    }
                }
            }
        })

        adapter.addReplyExpandedListener(object : CommentAdapter.AddReplyExpandedListener {
            override fun expand(position: Int, commentId: String) {
                lifecycleScope.launch {
                    mainViewModel.getCommentReply(position, commentId, adapter.getCopyOldList())
                }
            }

            override fun close(parentId: String) {
                //收起时将item数据删除，如果想提升下次打开的速度 可以安装父id 将回复缓存起来
                lifecycleScope.launch {
                    mainViewModel.replyClose(parentId, adapter.getCopyOldList())
                }
            }

            /**
             * @see commentId 为当前评论Id 根据评论id 获取此评论的更多回复 此处模拟
             */
            override fun loadReply(position: Int, commentId: String) {
                lifecycleScope.launch {
                    mainViewModel.getCommentReply(position, commentId, adapter.getCopyOldList())
                }
            }

            override fun retry() {
                loadToggle = true
                lifecycleScope.launch {
                    mainViewModel.getComments(adapter.getCopyOldList())
                }
            }
        })
        val commentDialog = BottomSheetDialog(this, R.style.commentReply)
        val commentReplyLayout =
            layoutInflater.inflate(R.layout.player_comment_reply_dialog, null, false)
        val commentInput = commentReplyLayout.findViewById<EditText>(R.id.comment_input)
        commentInput.requestFocus()
        commentDialog.setContentView(commentReplyLayout)

        //点击评论时 弹出新的dialog
        val commentView = view.findViewById<EditText>(R.id.comment_view)
        commentView.setOnClickListener {
            commentDialog.show()
        }

        val frameLayout = commentReplyLayout.parent as FrameLayout
        /**
        监听软键盘弹起 获取软键盘高度 将dialog移动到软键盘上方
         */
        SoftKeyBoardListener.setListener(this, object : SoftKeyBoardListener(this),
            SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
            override fun keyBoardShow(height: Int, rootViewVisibleHeight: Int) {
//                height 为软件盘的高度   rootViewVisibleHeight为当前屏幕可是区域 不包括状态栏
                frameLayout.translationY = (-height).toFloat()
            }

            override fun keyBoardHide(height: Int) {
                commentDialog.cancel()
                commentView.text = commentInput.text
                commentView.setSelection(commentView.text.length)
            }
        })
    }
}