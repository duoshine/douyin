package com.duoshine.douyin.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import cn.sharesdk.onekeyshare.OnekeyShare
import com.duoshine.douyin.MainActivity
import com.duoshine.douyin.MainViewModel
import com.duoshine.douyin.R
import com.duoshine.douyin.adapter.CommentAdapter
import com.duoshine.douyin.adapter.EmojiAdapter
import com.duoshine.douyin.adapter.PlayerAdapter
import com.duoshine.douyin.constants.EmojiConstants
import com.duoshine.douyin.model.Comments
import com.duoshine.douyin.util.ScreenUtil
import com.duoshine.douyin.widget.EmojiTabLayout
import com.duoshine.douyin.widget.FullHeightSpan
import com.duoshine.douyin.widget.SoftKeyBoardListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mob.MobSDK
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 *Created by chen on 2020  播放视频 对于关注和推荐他们是相同
 *
 *
 * 视频预加载
 */
class PlayerFragment : BaseFragment(), View.OnClickListener {
    private val TAG = "PlayerFragment"
    private var mainViewModel: MainViewModel? = null
    private var adapter: PlayerAdapter? = null
    private var urls: MutableList<String>? = null
    private val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"
    private val EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE"

    //    显示评论列表的弹窗
    private var bottomSheetDialog: BottomSheetDialog? = null

    //    接收用户输入评论的弹窗
    private var commentInputDialog: BottomSheetDialog? = null

    /**
     * 下拉刷新时使用 防止多触发 下拉刷新结果只会 才会触发新的刷新
     */
    private var loadToggle = false

    //    评论列表的输入框 仅用于接收和显示评论 本身没有输入焦点
    private var commentListInput: EditText? = null

    //    评论输入的控件 它用来接收用户的输入并在软键盘关闭时将结果传递 commentListInput
    private var commentInput: EditText? = null

    //    表情和软件切换的按钮
    private var emojiKeyboardToggle: ImageButton? = null

    //    显示表情列表的父层容器 它仅用于显示和隐藏
    private var emojiListLayout: LinearLayout? = null

    //    显示默认的软键盘输入 是输入评论的默认弹窗
    private var defaultEmojiList: GridLayout? = null

    //    显示评论的适配器
    private var commentAdapter: CommentAdapter? = null

    /**
     * 软件当前状态 false = 显示键盘  true  = 显示表情
     */
    private var keyBoardOrEmojiToggle = false


    private var activity: MainActivity? = null


    private var receiver: VolumeReceiver? = null

    /**
     * 页面你加载视频的类型 推荐还是关注
     * 0 = 关注
     * 1 = 推荐
     */
    private var videoType: Int = 0

    private var refreshObserver: Observer<MainViewModel.RefreshState>? = null
    private var dataObserver: Observer<List<String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
//        shareSDK 在调用分享之前调用即可
        MobSDK.submitPolicyGrantResult(true, null);
        //获取当前页面你加载视频的类型 推荐还是关注
        videoType = this.arguments?.getInt("type") ?: 0
        urls = ArrayList()
        receiver = VolumeReceiver()
        val filter = IntentFilter()
        filter.addAction(VOLUME_CHANGED_ACTION)
        context?.registerReceiver(receiver, filter)
        activity = context as? MainActivity
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(activity!!).get(MainViewModel::class.java)
        initView()
        initViewModel()
    }

    private fun initViewModel() {
        //监听数据变化
        dataObserver = Observer<List<String>> { data ->
            //更新数据
            urls?.clear()
            urls?.addAll(data)
            view_pager.currentItem = 0
            adapter?.notifyDataSetChanged()
        }

        mainViewModel?.urlsLiveData(videoType)
            ?.observe(activity!!, dataObserver!!)
        addRefreshListener()
    }

    /**
     * 监听Main底部Tab重复点击触发的刷新  和home页下拉触发的刷新
     */
    private fun addRefreshListener() {
        Log.d(TAG, "请求数据监听: $videoType")
        if (videoType == 1) {
            refreshObserver = Observer<MainViewModel.RefreshState> {
                if (it == MainViewModel.RefreshState.START) {//开始刷新数据
                    //先执行个加载动画吧
                    GlobalScope.launch(Dispatchers.Main) {
                        //根据type的类型加载不同的视频
                        if (videoType == 0) { //关注
                            mainViewModel!!.requestFollowUrls()
                        } else { //推荐
                            mainViewModel!!.requestUrls()
                        }
                    }
                } else if (it == MainViewModel.RefreshState.COMPLETE) {
                    //结束加载动画
                }
            }
            //监听刷新启动与结束的状态
            mainViewModel?.getRefreshState()
                ?.observe(activity!!, refreshObserver!!)
        } else if (videoType == 0) {
            refreshObserver = Observer<MainViewModel.RefreshState> {
                if (it == MainViewModel.RefreshState.START) {//开始刷新数据
                    Log.d(TAG, "addRefreshListener: $videoType")
                    //先执行个加载动画吧
                    GlobalScope.launch(Dispatchers.Main) {
                        //根据type的类型加载不同的视频
                        if (videoType == 0) { //关注
                            mainViewModel!!.requestFollowUrls()
                        } else { //推荐
                            mainViewModel!!.requestUrls()
                        }
                    }
                } else if (it == MainViewModel.RefreshState.COMPLETE) {
                    //结束加载动画
                }
            }
            //监听刷新启动与结束的状态
            mainViewModel?.getFollowRefreshState()
                ?.observe(activity!!, refreshObserver!!)
        }

        mainViewModel?.startRefresh(videoType)
    }

    private fun initView() {
        view_pager.offscreenPageLimit = 2
        adapter = PlayerAdapter(view_pager, context!!, urls!!)
        view_pager.adapter = adapter
//        构建显示评论列表的底部弹窗
        initCommentListDialog()
//        构建输入评论的弹窗
        initCommentInputDialog()

        // 监听视频的评论点击等事件 以做出处理
        adapter?.setVideoControlListener(object : PlayerAdapter.VideoControlListener {
            //            评论的点击事件
            override fun commentClick(position: Int) {
                bottomSheetDialog?.show()
            }

            override fun shareClick(position: Int) {
                showShare()
            }

        })
    }

    /**
     *调用shareSDK的分享， 每个平台的key都需要申请太麻烦 这里直接显示个Ui 实际上不具备分享功能
     */
    private fun showShare() {
        val oks = OnekeyShare()
/*        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle("share")
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl("http://sharesdk.cn")
        // text是分享文本，所有平台都需要这个字段
        oks.text = "我是分享文本"
        // setImageUrl是网络图片的url
        oks.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png")
        // url在微信、Facebook等平台中使用
//            oks.setUrl("http://sharesdk.cn")*/
        // 启动分享GUI
        oks.show(MobSDK.getContext())
    }

    //    它用来接收用户输入评论  todo
    private fun initCommentInputDialog() {
        commentInputDialog = BottomSheetDialog(context!!, R.style.commentReply)
        /**
         * 实现此监听的木事禁止通过手指拖动dialog
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
        defaultEmojiList = commentReplyLayout.findViewById(R.id.default_list)
        emojiKeyboardToggle =
            commentReplyLayout.findViewById<ImageButton>(R.id.emoji_keyboard_toggle)
        emojiKeyboardToggle!!.setOnClickListener(this)

        val pagerTabLayout = commentReplyLayout.findViewById<EmojiTabLayout>(R.id.pager_tab_layout)
        emojiListLayout =
            commentReplyLayout.findViewById<LinearLayout>(R.id.emoji_list_layout)

        commentInputDialog!!.setContentView(commentReplyLayout)
        //初始化默认的表情
        initDefaultEmojiList(commentReplyLayout)

//       弹窗的直接父级 使用它将弹窗跟随软件做平移动画
        val frameLayout = commentReplyLayout.parent as FrameLayout

//        监听bottomSheetDialog窗口的隐藏事件  隐藏时将输入框的值取出
        commentInputDialog!!.setOnCancelListener {
            commentListInput?.text = commentInput?.text
            commentListInput?.setSelection(commentListInput!!.text.length)
            emojiListLayout!!.requestFocus()
        }

        /**
        监听软键盘弹起 获取软键盘高度 将dialog移动到软键盘上方
         */
        SoftKeyBoardListener.setListener(activity, object : SoftKeyBoardListener(activity),
            SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
            override fun keyBoardShow(height: Int, rootViewVisibleHeight: Int) {
                Log.d(TAG, "keyBoardShow: 软键盘弹出")
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
                Log.d(TAG, "keyBoardHide:软键盘隐藏 ")
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

//        显示表情列表
        val emojiAdapter = EmojiAdapter(context!!, EmojiConstants.emojiList, layoutInflater)
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

    //将表情插入评论的光标位置  这个函数提供了表情转文本显示的功能
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
            val imageSpan = FullHeightSpan(context, bitmap)
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

    //    在软件盘与表情之前切换显示
    private fun listOrKeyKeyboardToggle() {
        if (!keyBoardOrEmojiToggle) {//表情
            //隐藏软键盘
            closeKeyboard()
            keyBoardOrEmojiToggle = true
            emojiKeyboardToggle?.setImageResource(R.mipmap.cqb)
        } else {//键盘输入
            commentInput?.requestFocus() //请求焦点 让EditText默认获取节点 以弹出软键盘
            //切换到键盘显示 dialog存在 显示默认的表情
            emojiListLayout?.visibility = View.GONE
            defaultEmojiList?.visibility = View.VISIBLE
            keyBoardOrEmojiToggle = false
            //显示软键盘
            showKeyBoard()
            emojiKeyboardToggle?.setImageResource(R.mipmap.bxy)
        }
    }

    /**
     * 显示视频评论列表 占比80%屏幕 对应一个新的视频 仅应更新它的适配器
     */
    private fun initCommentListDialog() {
        bottomSheetDialog = BottomSheetDialog(context!!, R.style.commentList)
        val behavior = bottomSheetDialog!!.behavior
        val view =
            layoutInflater.inflate(R.layout.player_comment_list_dialog, null, false)
        //获取屏幕高度 设置给弹窗
        val heightPixels = ScreenUtil.getScreenHeight(context!!)
        val bottomSheetHeight = (heightPixels * 0.8).toInt()
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            bottomSheetHeight //设置自定义的view的高度为屏幕高度的百分之80，不设置的话其父默认是wrap_content
        )
        //设置底部bottomSheet的高度 如果不设置默认为 16:9  这里设置为屏幕高度的百分之80
        behavior.peekHeight = bottomSheetHeight
        bottomSheetDialog!!.setContentView(view, params)

        commentAdapter = CommentAdapter(context!!) //用来显示评论
        val bottomSheetCommentList =
            view.findViewById<RecyclerView>(R.id.bottom_sheet_comment_list)
        view.findViewById<ImageButton>(R.id.comment_list_submit).setOnClickListener(this)
        val bottomSheetComment = view.findViewById<TextView>(R.id.bottom_sheet_comment)
        view.findViewById<ImageButton>(R.id.comment_list_emoji).setOnClickListener(this)
        view.findViewById<ImageButton>(R.id.bottom_sheet_close).setOnClickListener(this)
        //点击评论时 弹出新的dialog
        commentListInput = view.findViewById(R.id.comment_view)
        commentListInput!!.setOnClickListener(this)

        bottomSheetCommentList.layoutManager = LinearLayoutManager(context!!)
        bottomSheetCommentList.adapter = commentAdapter

        //加载评论
        lifecycleScope.launch {
            mainViewModel!!.getComments(commentAdapter!!.getCopyOldList())
                .observe(activity!!,
                    Observer {
                        if (it.size != 0) {
                            val oldList = commentAdapter!!.getOldList()
                            if (oldList.size == 0) {
                                commentAdapter!!.submitList(it, null)
                            } else {
                                commentAdapter!!.submitList(oldList, it)
                            }
                            bottomSheetComment.text = "${it.size} 条评论"
                        }
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
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            //评论提交
            R.id.comment_submit,
            R.id.comment_list_submit -> submitComment()  //消息发送后dialog就需要隐藏
            //显示表情输入
            R.id.comment_list_emoji -> showInputDialog()
            //关闭评论弹窗
            R.id.bottom_sheet_close -> hideDialog()
            // 评论列表底部的输入框 它没有输入功能 仅用于接收输入弹窗返回的数据
            R.id.comment_view -> showCommentInputDialog()
            //在软件盘与表情之间切换
            R.id.emoji_keyboard_toggle -> listOrKeyKeyboardToggle()
            R.id.default_list_0 -> append(EmojiConstants.defaultEmojiList[0])
            R.id.default_list_1 -> append(EmojiConstants.defaultEmojiList[1])
            R.id.default_list_2 -> append(EmojiConstants.defaultEmojiList[2])
            R.id.default_list_3 -> append(EmojiConstants.defaultEmojiList[3])
            R.id.default_list_4 -> append(EmojiConstants.defaultEmojiList[4])
            R.id.default_list_5 -> append(EmojiConstants.defaultEmojiList[5])
            R.id.default_list_6 -> append(EmojiConstants.defaultEmojiList[6])
            R.id.default_list_7 -> append(EmojiConstants.defaultEmojiList[7])
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
            Toast.makeText(context, "不可以发送空消息哦", Toast.LENGTH_SHORT).show()
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

    //    显示评论的输入框
    private fun showCommentInputDialog() {
        commentInput?.requestFocus() //请求焦点 让EditText默认获取节点 以弹出软键盘
        commentInputDialog!!.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(receiver)
    }

    inner class VolumeReceiver : BroadcastReceiver() {
        private var mAudioManager: AudioManager? = null

        override fun onReceive(context: Context, intent: Intent) {
            if (VOLUME_CHANGED_ACTION == intent.action
                && (intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_MUSIC)
            ) {
                if (mAudioManager == null) {
                    mAudioManager = context.applicationContext
                        .getSystemService(Context.AUDIO_SERVICE) as AudioManager
                }
                val volume = mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
                val maxVolume = mAudioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 0
                adapter?.volumeChange(volume, maxVolume)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //恢复播放
        playWhenReady()
    }

    override fun onPause() {
        super.onPause()
        //暂停播放
        playWhenUnready()
    }

    /**
     * 恢复播放
     */
    fun playWhenReady() {
        //只有为false才执行 false表示Home页处于显示状态
        val hidden = parentFragment?.isHidden
        if (hidden == false) {
            adapter?.playWhenReady()
        }
    }

    /**
     * 暂停播放
     */
    fun playWhenUnready() {
        adapter?.playWhenUnready()
    }

    /**
     * 关闭软键盘
     */
    fun closeKeyboard() {
        val imm: InputMethodManager =
            context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(commentInput?.windowToken, 0)
    }

    fun showKeyBoard() {
        // 弹出软键盘
        val imm =
            context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(commentInput, 0)
    }

    companion object {
        /**
         * type 表示该fragment的加载视频类型  推荐 or 关注
         *  0：关注
         *  1：推荐
         */
        fun getPlayerFragment(type: Int): PlayerFragment {
            val playerFragment = PlayerFragment()
            val bundle = Bundle()
            bundle.putInt("type", type)
            playerFragment.arguments = bundle
            return playerFragment
        }
    }
}