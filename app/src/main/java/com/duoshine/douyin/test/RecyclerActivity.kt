package com.duoshine.douyin.test


import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.duoshine.douyin.MainViewModel
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.CommentAdapter
import com.duoshine.douyin.adapter.EmojiAdapter
import com.duoshine.douyin.constants.EmojiConstants
import com.duoshine.douyin.model.Comments
import com.duoshine.douyin.widget.EmojiTabLayout
import com.duoshine.douyin.widget.FullHeightSpan
import com.duoshine.douyin.widget.SoftKeyBoardListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_recycler_test.*
import kotlinx.coroutines.launch


class RecyclerActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "RecyclerActivity"

    /**
     * 下拉刷新时使用 防止多触发 下拉刷新结果只会 才会触发新的刷新
     */
    private var loadToggle = false

    /**
     * 软件当前状态 false = 显示键盘  true  = 显示表情
     */
    private var keyBoardOrEmojiToggle = false

    private var commentInput: EditText? = null
    private var mainViewModel: MainViewModel? = null
    private var commentAdapter: CommentAdapter? = null

    private var bottomSheetDialog: BottomSheetDialog? = null
    private var commentInputDialog: BottomSheetDialog? = null
    private var defaultEmojiList: GridLayout? = null
    private var emojiListLayout: LinearLayout? = null
    private var emojiKeyboardToggle: ImageButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_test)






        //设置加载的json文件所用到的资源目录
        animation_view.imageAssetsFolder = "digg_heart_bubbles/images"

        val outMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels
        val heightPixels = outMetrics.heightPixels

        bottomSheetDialog = BottomSheetDialog(this, R.style.commentList)
        val behavior = bottomSheetDialog!!.behavior
        val view =
            layoutInflater.inflate(R.layout.player_comment_list_dialog, null, false)
        val bottomSheetHeight = (heightPixels * 0.8).toInt()
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            bottomSheetHeight //设置自定义的view的高度为屏幕高度的百分之80，不设置的话其父默认是wrap_content
        )
        //设置底部bottomSheet的高度 如果不设置默认为 16:9  这里设置为屏幕高度的百分之80
        behavior.peekHeight = bottomSheetHeight

        bottomSheetDialog!!.setContentView(view, params)

        btn_show_dialog.setOnClickListener {
            bottomSheetDialog!!.show()
        }

        commentAdapter = CommentAdapter(this)
        val bottomSheetCommentList =
            view.findViewById<RecyclerView>(R.id.bottom_sheet_comment_list)
        view.findViewById<ImageButton>(R.id.comment_list_submit).setOnClickListener(this)
        val bottomSheetComment = view.findViewById<TextView>(R.id.bottom_sheet_comment)
        view.findViewById<ImageButton>(R.id.comment_list_emoji).setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.bottom_sheet_close).setOnClickListener(this)
        bottomSheetCommentList.setHasFixedSize(true)
        bottomSheetCommentList.layoutManager = LinearLayoutManager(this)
        bottomSheetCommentList.adapter = commentAdapter
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //加载评论
        lifecycleScope.launch {
            mainViewModel!!.getComments(commentAdapter!!.getCopyOldList())
                .observe(this@RecyclerActivity,
                    Observer {
                        Log.d(TAG, "已更新数据")
                        if (commentAdapter!!.getOldList().size == 0) {
                            commentAdapter!!.submitList(it, null)
                        } else {
                            commentAdapter!!.submitList(commentAdapter!!.getOldList(), it)
                        }
                        bottomSheetComment.text = "${it.size} 条评论"
                        loadToggle = false
                    })
        }

        //监听recyclerView的滑动 满足条件触发加载更多
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
                            mainViewModel?.getComments(commentAdapter!!.getCopyOldList())
                        }
                    }
                }
            }
        })

        commentAdapter!!.addReplyExpandedListener(object : CommentAdapter.AddReplyExpandedListener {
            override fun expand(position: Int, commentId: String) {
                lifecycleScope.launch {
                    mainViewModel?.getCommentReply(
                        position,
                        commentId,
                        commentAdapter!!.getCopyOldList()
                    )
                }
            }

            override fun close(parentId: String) {
                //收起时将item数据删除，如果想提升下次打开的速度 可以安装父id 将回复缓存起来
                lifecycleScope.launch {
                    mainViewModel?.replyClose(parentId, commentAdapter!!.getCopyOldList())
                }
            }

            /**
             * @see commentId 为当前评论Id 根据评论id 获取此评论的更多回复 此处模拟
             */
            override fun loadReply(position: Int, commentId: String) {
                lifecycleScope.launch {
                    mainViewModel?.getCommentReply(
                        position,
                        commentId,
                        commentAdapter!!.getCopyOldList()
                    )
                }
            }

            override fun retry() {
                loadToggle = true
                lifecycleScope.launch {
                    mainViewModel?.getComments(commentAdapter!!.getCopyOldList())
                }
            }

            /**
             * 回复
             */
            override fun replyClick(position: Int, replyPerson: String) {
               //todo
            }
        })
        commentInputDialog = BottomSheetDialog(this, R.style.commentReply)
        /**
         * 在软键盘弹起时 禁止通过手指拖动dialog
         */
        commentInputDialog!!.behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //禁止拖拽，
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    //设置为收缩状态
                    commentInputDialog!!.behavior.state = BottomSheetBehavior.STATE_COLLAPSED;
                }
            }
        })


        val commentReplyLayout =
            layoutInflater.inflate(R.layout.player_comment_reply_dialog, null, false)
        commentInput = commentReplyLayout.findViewById<EditText>(R.id.comment_input)

        val emojiList = commentReplyLayout.findViewById<ViewPager2>(R.id.emoji_list)
        defaultEmojiList = commentReplyLayout.findViewById<GridLayout>(R.id.default_list)
        emojiKeyboardToggle =
            commentReplyLayout.findViewById<ImageButton>(R.id.emoji_keyboard_toggle)
        //点击评论时 弹出新的dialog
        val commentView = view.findViewById<EditText>(R.id.comment_view)
        val pagerTabLayout = commentReplyLayout.findViewById<EmojiTabLayout>(R.id.pager_tab_layout)
        emojiListLayout =
            commentReplyLayout.findViewById<LinearLayout>(R.id.emoji_list_layout)

        commentInputDialog!!.setContentView(commentReplyLayout)
        //  切换软键盘和表情列表
        emojiKeyboard(emojiKeyboardToggle!!, defaultEmojiList!!, emojiListLayout!!)
        commentView.setOnClickListener {
            commentInput?.requestFocus() //请求焦点 让EditText默认获取节点 以弹出软键盘
            commentInputDialog!!.show()
        }

        initDefaultEmojiList(commentReplyLayout)

        val frameLayout = commentReplyLayout.parent as FrameLayout

//        监听bottomSheetDialog窗口的隐藏事件  隐藏时将输入框的值取出
        commentInputDialog!!.setOnCancelListener {
            commentView.text = commentInput?.text
            commentView.setSelection(commentView.text.length)
            emojiListLayout!!.requestFocus()
        }

        /**
        监听软键盘弹起 获取软键盘高度 将dialog移动到软键盘上方
         */
        SoftKeyBoardListener.setListener(this, object : SoftKeyBoardListener(this),
            SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
            override fun keyBoardShow(height: Int, rootViewVisibleHeight: Int) {
//                height 为软件盘的高度   rootViewVisibleHeight为当前屏幕可是区域 不包括状态栏
                frameLayout.translationY = (-height).toFloat()
                if (keyBoardOrEmojiToggle) {//如果键盘弹出时显示的是表情 则表情需要隐藏
                    emojiListLayout?.visibility = View.GONE
                    defaultEmojiList?.visibility = View.VISIBLE
                    keyBoardOrEmojiToggle = false
                }
            }

            // height 为屏幕可视总高度 也就是软键盘隐藏后的全屏高度
            override fun keyBoardHide(height: Int) {
                Log.d(TAG, "keyBoardHide: ")
                //隐藏软键盘可能不是dialog被取消  二十
                if (keyBoardOrEmojiToggle) { //切换到表情显示时  软件盘隐藏后 显示表情列表 并将dialog移动到屏幕底部
                    defaultEmojiList?.visibility = View.GONE
                    emojiListLayout?.visibility = View.VISIBLE
                } else {
                    commentInputDialog!!.cancel() //另一种情况为人为关闭软键盘 那么直接人为是用户输入结束
                }
                frameLayout.translationY =
                    (height - frameLayout.bottom).toFloat() //在软键盘隐藏后改变显示位置到底部 防止下一次显示的错误位置
            }
        })

        val emojiAdapter = EmojiAdapter(this, EmojiConstants.emojiList, layoutInflater)
        emojiList.adapter = emojiAdapter

        //监听ViewPager滑动 移动TabLayout
        emojiList.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                pagerTabLayout.setScrollPosition(
                    position,
                    positionOffset,
                    true
                ) //设置为true可以触发drawable的状态
            }
        })

        //添加EmoJi列表的点击事件 当用户点击时将表情添加至EditText中
        emojiAdapter.setItemClickListener(object : EmojiAdapter.ItemClickListener {
            override fun click(parentPosition: Int, position: Int) {
                append(EmojiConstants.emojiList[parentPosition][position])
            }
        })
    }

    private fun initDefaultEmojiList(commentReplyLayout: View) {
        commentReplyLayout.findViewById<ImageButton>(R.id.default_list_0).setOnClickListener(this)
        commentReplyLayout.findViewById<ImageButton>(R.id.default_list_1).setOnClickListener(this)
        commentReplyLayout.findViewById<ImageButton>(R.id.default_list_2).setOnClickListener(this)
        commentReplyLayout.findViewById<ImageButton>(R.id.default_list_3).setOnClickListener(this)
        commentReplyLayout.findViewById<ImageButton>(R.id.default_list_4).setOnClickListener(this)
        commentReplyLayout.findViewById<ImageButton>(R.id.default_list_5).setOnClickListener(this)
        commentReplyLayout.findViewById<ImageButton>(R.id.default_list_6).setOnClickListener(this)
        commentReplyLayout.findViewById<ImageButton>(R.id.default_list_7).setOnClickListener(this)
        commentReplyLayout.findViewById<ImageButton>(R.id.comment_submit).setOnClickListener(this)
    }

    /**
     * 点击切换表情列表和软键盘
     */
    private fun emojiKeyboard(
        emojiKeyboardToggle: ImageButton,
        defaultEmojiList: GridLayout,
        emojiListLayout: LinearLayout
    ) {
        emojiKeyboardToggle.setOnClickListener {
            if (!keyBoardOrEmojiToggle) {//表情
                //隐藏软键盘
                closeKeyboard()
                keyBoardOrEmojiToggle = true
                emojiKeyboardToggle.setImageResource(R.mipmap.cqb)
            } else {//键盘输入
                commentInput?.requestFocus() //请求焦点 让EditText默认获取节点 以弹出软键盘
                //切换到键盘显示 dialog存在 显示默认的表情
                emojiListLayout.visibility = View.GONE
                defaultEmojiList.visibility = View.VISIBLE
                keyBoardOrEmojiToggle = false
                //显示软键盘
                showKeyBoard()
                emojiKeyboardToggle.setImageResource(R.mipmap.bxy)
            }
        }
    }

    private fun append(id: Int) {
        //根据资源id获取占位符
        val placeholder = EmojiConstants.resIdMap[id]
        // 随机产生1至9的整数
        try {
            val resourceId: Int = id
            // 根据资源ID获得资源图像的Bitmap对象
            val bitmap = BitmapFactory.decodeResource(
                resources,
                resourceId
            )
            // 根据Bitmap对象创建ImageSpan对象
            val imageSpan = FullHeightSpan(this, bitmap)
            // 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
            val spannableString = SpannableString(placeholder) //d是占位符 表情会占用一个字符 在下面会使用表情替换该字符
            spannableString.setSpan(
                imageSpan, 0, 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // 将随机获得的图像追加到EditText控件的最后
            val editTable = commentInput!!.text
            editTable.insert(commentInput?.selectionStart!!, spannableString)
        } catch (e: Exception) {
        }
    }

    /**
     * 关闭软键盘
     */
    fun closeKeyboard() {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(commentInput?.windowToken, 0)
    }

    fun showKeyBoard() {
        // 弹出软键盘
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(commentInput, 0)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.default_list_0 -> append(EmojiConstants.defaultEmojiList[0])
            R.id.default_list_1 -> append(EmojiConstants.defaultEmojiList[1])
            R.id.default_list_2 -> append(EmojiConstants.defaultEmojiList[2])
            R.id.default_list_3 -> append(EmojiConstants.defaultEmojiList[3])
            R.id.default_list_4 -> append(EmojiConstants.defaultEmojiList[4])
            R.id.default_list_5 -> append(EmojiConstants.defaultEmojiList[5])
            R.id.default_list_6 -> append(EmojiConstants.defaultEmojiList[6])
            R.id.default_list_7 -> append(EmojiConstants.defaultEmojiList[7])
            R.id.comment_submit, R.id.comment_list_submit -> submitComment()  //消息发送后dialog就需要隐藏
            R.id.comment_list_emoji -> showInputDialog()
            R.id.bottom_sheet_close -> hideDialog()
        }
    }

    private fun hideDialog() {
        bottomSheetDialog?.cancel()
    }

    private fun showInputDialog() {
        keyBoardOrEmojiToggle = true
        emojiKeyboardToggle?.setImageResource(R.mipmap.cqb)
        defaultEmojiList?.visibility = View.GONE
        emojiListLayout?.visibility = View.VISIBLE
        commentInputDialog?.show()
    }

    /**
     * 发送评论  发送至后台并更新本地显示
     */
    private fun submitComment() {
        val comment = commentInput?.text.toString().trim()
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "不可以发送空消息哦", Toast.LENGTH_SHORT).show()
            return
        }
        commentInputDialog?.cancel()
        mainViewModel?.addComment(
            commentAdapter!!.getCopyOldList(),
            Comments(
                "1",
                null,
                "",
                "duo_shine",
                comment,
                "2020-09-13 18:33:00",
                0,
                false,
                0,
                CommentAdapter.COMMENT,
                1
            )
        )
    }
}
