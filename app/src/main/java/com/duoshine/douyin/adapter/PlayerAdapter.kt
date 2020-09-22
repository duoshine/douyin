package com.duoshine.douyin.adapter

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.duoshine.douyin.R
import com.duoshine.douyin.model.VideoModel
import com.duoshine.douyin.util.Util
import com.duoshine.douyin.widget.LikeView
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlin.math.abs

class PlayerAdapter(
    private val viewPager: ViewPager2,
    private val context: Context,
    private val urls: MutableList<String>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val videoModel = listOf(
        VideoModel(
            false,
            0,
            0,
            0,
            "原来你是我@<@抖音官方@>最想留着的幸运",
            "@duo_shine",
            "来自duo_shine的原创，快乐的一只小青蛙"
        ),
        VideoModel(false, 0, 0, 0, "原来你是我@<@抖音官方@>最想留着的幸运", "duo_shine", "来自duo_shine的原创，快乐的一只小青蛙"),
        VideoModel(false, 0, 0, 0, "原来你是我@<@抖音官方@>最想留着的幸运", "duo_shine", "来自duo_shine的原创，快乐的一只小青蛙"),
        VideoModel(false, 0, 0, 0, "原来你是我@<@抖音官方@>最想留着的幸运", "duo_shine", "来自duo_shine的原创，快乐的一只小青蛙"),
        VideoModel(false, 0, 0, 0, "原来你是我@<@抖音官方@>最想留着的幸运", "duo_shine", "来自duo_shine的原创，快乐的一只小青蛙"),
        VideoModel(false, 0, 0, 0, "原来你是我@<@抖音官方@>最想留着的幸运", "duo_shine", "来自duo_shine的原创，快乐的一只小青蛙"),
        VideoModel(false, 0, 0, 0, "原来你是我@<@抖音官方@>最想留着的幸运", "duo_shine", "来自duo_shine的原创，快乐的一只小青蛙"),
        VideoModel(false, 0, 0, 0, "原来你是我@<@抖音官方@>最想留着的幸运", "duo_shine", "来自duo_shine的原创，快乐的一只小青蛙"),
        VideoModel(false, 0, 0, 0, "原来你是我@<@抖音官方@>最想留着的幸运", "duo_shine", "来自duo_shine的原创，快乐的一只小青蛙"),
        VideoModel(false, 0, 0, 0, "原来你是我@<@抖音官方@>最想留着的幸运", "duo_shine", "来自duo_shine的原创，快乐的一只小青蛙"),
        VideoModel(false, 0, 0, 0, "原来你是我@<@抖音官方@>最想留着的幸运", "duo_shine", "来自duo_shine的原创，快乐的一只小青蛙")
    )

    private val TAG = "PlayerAdapter"

    private var mediaSource: ProgressiveMediaSource.Factory? = null

    /**
     * 在滑动开始时记录位置 滑动结束后判断是否翻页
     */
    private var pagerSelected = 0

    /**
     * 更新SeekBar的任务
     */
    private var seekBarRunnable: Runnable? = null

    /**
     * 他的作用是查询视频播放的进度更新SeekBar
     */
    private val handler: Handler = Handler()

    private var formatBuilder: StringBuilder? = null

    /**
     *  SeekBar的滑动事件执行过程中，没有执行UP 则SeekBar的进度不允许修改 否则导致闪烁
     */
    private var seekBarTouchDown = true


    private val holders: MutableMap<Int, MyViewHolder> = HashMap()

    /**
     * 1.5秒后 隐藏当前音量进度条显示 转而显示视频播放进度
     */
    private var volumeRunnable: Runnable? = null

    /**
     * SeekBar的down事件不处理进度 所以当x轴移动超过某阈值时 才认为需要移动进度
     */
    private var startX = 0f
    private var endX = 0f

    /**
     *  item的评论 分享等事件回调外部处理
     */
    private var videoControlListener: VideoControlListener? = null

    /**
     * 当前显示item索引 它可能用在外部调用停止视频时
     */
    private var currentPosition = 0

    init {
        formatBuilder = StringBuilder()


        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val holder = holders[position]
                currentPosition = position
                holder?.exoPlayer?.playWhenReady = true //它会触发onPlayerStateChanged
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        //在滑动开始的时候记录下当前的页面索引
                        pagerSelected = viewPager.currentItem
                    }
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        // 翻页结束
                        pageScrollEnd()
                    }
                }
            }
        })
    }

    /**
     * 当pager滑动结束时  上一个页面需要做一些处理 如停止视频播放等
     */
    private fun pageScrollEnd() {
        val currentItem = viewPager.currentItem
        //移动结束时判断pagerSelected是否有变化 如果变化了 则停止pagerSelected对应位置的播放
        if (pagerSelected != currentItem) {
            val holder = holders[pagerSelected]
            //暂停播放
            holder?.exoPlayer?.playWhenReady = false
            //暂停bgm旋转
            val objectAnimator = holder?.musicLayout?.tag as? ObjectAnimator
            objectAnimator?.pause()
            //暂停bgm跑马灯
            holder?.videoMusicNameView?.isSelected = false
            //如果希望每次切换之后都从头开始播放
            // holder?.exoPlayer?.seekToDefaultPosition()
        }
    }

    /**
     * 用户滑动到新的item时会触发 播放视频并且判断是否需要SeekBar加入工作
     * seconds ：总时长 单位 00:30
     */
    private fun playAndSeek(
        holder: MyViewHolder?,
        seconds: String
    ) {
        //超过30秒的视频需要显示播放进度条
        holder?.apply {
            val exoPlayer = exoPlayer!!
            //如果SeekBar处于显示状态 表示该视频>=30s 则执行查询进度并设置SeekBar 不超过30秒则不需要此任务工作
            Log.d(TAG, "启动任务")
            seekBarRunnable = Runnable {
                val currentPosition = exoPlayer.currentPosition // 获取当前进度
                val currentTime =
                    Util.getStringForTime(currentPosition, formatBuilder!!) //将当前进度毫秒数转为字符串格式
                timeBar?.text = "$currentTime / $seconds"
                //注意 只要用户在操作SeekBar 如滑动时此时手指没有UP的情况下 不允许修改SeekBar的进度，否则导致闪烁
                if (seekBarTouchDown) {
                    seekBar?.progress =
                        (exoPlayer.currentPosition * 100 / exoPlayer.duration).toInt()
                }
                handler.postDelayed(seekBarRunnable, 1000)
            }
            handler.postDelayed(seekBarRunnable, 0)
        }
    }

    /**
     * 滑动到不需要进度条的页面则清除查询视频播放的进度 因为没有SeekBar
     */
    private fun removeSeekHandler() {
        handler.removeCallbacks(seekBarRunnable)
    }

    /**
     * 重新启动任务 一般情况下它是无用的 但是当音量变化时会占用视频播放进度的显示
     * 音量变化时临时将其任务清除 并显示音量的
     */
    private fun reStartSeekHandler() {
        handler.postDelayed(seekBarRunnable, 0)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var playerView: PlayerView? = null
        var seekBar: SeekBar? = null
        var timeBar: TextView? = null
        var exoPlayer: SimpleExoPlayer? = null
        var avatar: ImageView? = null
        var defaultLike: ImageButton? = null
        var commentIcon: ImageButton? = null
        var shareIcon: ImageButton? = null
        var commentCount: TextView? = null
        var pauseView: ImageButton? = null
        var shareCount: TextView? = null
        var starCount: TextView? = null
        var authorView: TextView? = null
        var videoCopywritingView: TextView? = null
        var videoMusicNameView: TextView? = null
        var starLike: LottieAnimationView? = null
        var videoLayout: LikeView? = null
        var musicBg: ImageView? = null
        var musicLayout: FrameLayout? = null

        init {
            playerView = itemView.findViewById(R.id.player_view)
            videoLayout = itemView.findViewById(R.id.video_layout)
            timeBar = itemView.findViewById(R.id.time_bar)
            seekBar = itemView.findViewById(R.id.seekBar)
            avatar = itemView.findViewById(R.id.avatar)
            defaultLike = itemView.findViewById(R.id.default_like)
            starLike = itemView.findViewById(R.id.star_like)
            commentIcon = itemView.findViewById(R.id.comment_icon)
            commentCount = itemView.findViewById(R.id.comment_count)
            shareIcon = itemView.findViewById(R.id.share_icon)
            shareCount = itemView.findViewById(R.id.share_count)
            starCount = itemView.findViewById(R.id.star_count)
            musicBg = itemView.findViewById(R.id.music_bg)
            musicLayout = itemView.findViewById(R.id.music_layout)
            pauseView = itemView.findViewById(R.id.pause_view)
            authorView = itemView.findViewById(R.id.author_view)
            videoCopywritingView = itemView.findViewById(R.id.video_copywriting_view)
            videoMusicNameView = itemView.findViewById(R.id.video_music_name_view)

            starLike?.imageAssetsFolder = "digg_heart/images"
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (urls.size == 0) {
            1
        } else {
            2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d(TAG, "onCreateViewHolder: ${urls.size}")
        return if (viewType == 2) {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_player_item, parent, false)
            MyViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.adapter_player_follow_item, parent, false)
            FollowViewHolder(view)
        }
    }

    class FollowViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun getItemCount(): Int {
        return if (urls.size == 0) 1 else urls.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            bindRecommendHoler(holder, position)
        } else if (holder is FollowViewHolder) {

        }
    }

    private fun bindRecommendHoler(
        holder: MyViewHolder,
        position: Int
    ) {
        val exoPlayer = createExoplayer(holder, position)
        holder.playerView!!.player = exoPlayer
        exoPlayer.prepare(createMediaSource(position))
        exoPlayer.playWhenReady = position == 0
        holder.exoPlayer = exoPlayer
        //保存holder以便于在viewpager切换时得到Exoplayer的实例开始播放视频
        holders[position] = holder

        Glide
            .with(context)
            .load(R.mipmap.avatar2)
            .circleCrop()
            .into(holder.avatar!!)

        Glide
            .with(context)
            .load(R.mipmap.avatar2)
            .circleCrop()
            .into(holder.musicBg!!)

        val videoModel = videoModel[position]
//        更新点赞的信息
        holder.starCount?.text = videoModel.starCount.toString()
        if (videoModel.star) {
            holder.defaultLike?.setImageResource(R.mipmap.dli)
        } else {
            holder.defaultLike?.setImageResource(R.mipmap.cnp)
        }
//        更新评论的信息
        holder.commentCount?.text = videoModel.CommentCount.toString()
//        更新分享的信息
        holder.shareCount?.text = videoModel.shareCount.toString()
        //star动画
        holder.defaultLike?.setOnClickListener {
            //目前视频是否已经点赞
            if (videoModel.star) {
                videoModel.starCount -= 1
                holder.starCount?.text = videoModel.starCount.toString()
                videoModel.star = false
                //已经点赞
                holder.defaultLike?.setImageResource(R.mipmap.cnp)
                scaleAnimator(holder.defaultLike!!)
            } else {
                videoModel.star = true
                videoModel.starCount += 1
                holder.starCount?.text = videoModel.starCount.toString()
                //还未点赞
                holder.defaultLike?.setImageResource(R.mipmap.dli)
                holder.starLike?.visibility = View.VISIBLE
                holder.starLike?.playAnimation()
                scaleAnimator(holder.defaultLike!!)
                holder.starLike?.addAnimatorListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        holder.starLike?.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {

                    }

                })
            }
        }

//        评论的点击事件 回调外部
        holder.commentIcon?.setOnClickListener {
            scaleAnimator(holder.commentIcon!!)
            //弹出评论框  将此事件回调给View处理
            videoControlListener?.commentClick(position)
        }

        //分享的点击事件
        holder.shareIcon?.setOnClickListener {
            scaleAnimator(holder.shareIcon!!)
            // 弹出分享框  https://blog.csdn.net/qq15577969/article/details/82725897
            videoControlListener?.shareClick(position)
        }

        //视频页单击双击事件
        holder.videoLayout?.setOnLikeListener(object : LikeView.OnLikeListener {
            //            双击事件
            override fun onLikeListener() {
                //将点赞置为红星  防止重复计数
                if (!videoModel.star) {
                    videoModel.star = true
                    videoModel.starCount += 1
                    holder.starCount?.text = videoModel.starCount.toString()
                    //还未点赞
                    holder.defaultLike?.setImageResource(R.mipmap.dli)
                }
            }

            // 点击事件
            override fun onPlayOrPause() {
                val animator = holder.musicLayout?.tag as? ObjectAnimator
                //视频状态切换
                if (exoPlayer.isPlaying) {
                    exoPlayer.playWhenReady = false //停止视频播放
                    holder.pauseView?.visibility = View.VISIBLE
                    animator?.pause()//暂停音乐旋转效果
                    holder.videoMusicNameView?.isSelected = false //停止音乐跑马灯效果
                } else {
                    exoPlayer.playWhenReady = true //恢复视频播放
                    holder.pauseView?.visibility = View.INVISIBLE
                    animator?.resume() //恢复音乐旋转效果
                    holder.videoMusicNameView?.isSelected = true //开始音乐跑马灯效果
                }
            }
        })
        //视频作者 作者名称需要加粗
        holder.authorView?.text = videoModel.author
        holder.authorView?.typeface = Typeface.defaultFromStyle(Typeface.BOLD);
//        设置文案内容 文案内容是可点击的
        holder.videoCopywritingView?.text = getClickableSpan(videoModel.copywriting)
        // 去除TextView被点击时默认的背景颜色
        holder.videoCopywritingView?.highlightColor = Color.TRANSPARENT
        //设置超链接可点击
        holder.videoCopywritingView?.movementMethod = LinkMovementMethod.getInstance()
        //设置音乐名字 它支持跑马灯  默认不开启跑马灯效果
        holder.videoMusicNameView?.text = videoModel.music
        holder.videoMusicNameView?.isSelected = false
    }

    /**
     *
     * 获取可点击的SpannableString
     *      content:"原来你是我@<@id:1234456,name:抖音官方@>最想留着的幸运",
     *      @>理论是任何字符都可以替换的  理论上他的长度越长 越不容易被用户输入
     * @return
     */
    private fun getClickableSpan(content: String): SpannableString? {
        //<@ @>的内容是艾特用户
        val startIndex = content.indexOf("<@") - 1
        val endIndex = content.indexOf("@>") - 1
        val newString = content.replace(Regex("<@"), "").replace(Regex("@>"), " ")
        val spannableString = SpannableString(newString)

        //设置文字的单击事件
        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Log.d(TAG, "onClick: 跳转到个人信息")
            }

            override fun updateDrawState(ds: TextPaint) {
//                设置选中文字的颜色
                ds.color = Color.WHITE
//                设置选中文字加粗
                ds.typeface = Typeface.defaultFromStyle(Typeface.BOLD);
            }
        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableString
    }

    //    设置按钮被点击时的缩放动画
    private fun scaleAnimator(view: ImageButton) {
        val x = ObjectAnimator.ofFloat(view, "scaleX", .7f)
        val y = ObjectAnimator.ofFloat(view, "scaleY", .7f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(x, y)
        animatorSet.duration = 150
        animatorSet.start()
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                val x = ObjectAnimator.ofFloat(view, "scaleX", 1.0f)
                val y = ObjectAnimator.ofFloat(view, "scaleY", 1.0f)
                val animatorSet = AnimatorSet()
                animatorSet.playTogether(x, y)
                animatorSet.duration = 150
                animatorSet.start()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
    }

    private fun createExoplayer(holder: MyViewHolder, position: Int): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            setForegroundMode(true)
            addListener(object : Player.EventListener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == STATE_READY && playWhenReady) {//当视频播放时调用
                        holder.pauseView?.visibility = View.INVISIBLE //防止点击暂停滑动到下一个屏幕在切回来播放导致的没有隐藏
                        musicRotate(holder.musicLayout!!)  //开始播放 启动bgm旋转 动画会设置给此TAG
                        holder.videoMusicNameView?.isSelected = true //开始播放 启动bgm跑马灯

                        val duration = duration
                        val timeSeconds = Util.getStringForTime(duration)
                        if (timeSeconds >= 30) {
                            holder.seekBar?.visibility = View.VISIBLE
                            //对SeekBar添加触摸事件监听 以便于在移动进度后视频做出对应处理  计算总时长
                            val seconds = Util.getStringForTime(duration, formatBuilder!!)
                            addSeekBarListener(holder)
                            playAndSeek(holder, seconds)
                        } else {
                            holder.seekBar?.visibility = View.GONE
                            removeSeekHandler()
                        }
                    }
                }
            })
        }
    }

    /**
     * 给SeekBar添加触摸监听  在DOWN中显示加粗的SeekBar 在MOVE中移动进度 在UP时将进度条恢复默认状态
     */
    private fun addSeekBarListener(
        holder: MyViewHolder
    ) {
        val seekBar = holder.seekBar ?: return
        val timeBar = holder.timeBar ?: return
        val exoPlayer = holder.exoPlayer ?: return
        seekBar.setOnTouchListener { v, event ->
            var intercept = true
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    removeVolumeRunnable() //点击时清空音量进度隐藏的任务 防止任务正在进行时用户点击 导致任务执行后导致进度条Ui显示Bug
                    seekBarTouchDown = false
                    startX = event.x
                    //我要秀起来了 viewPager你别搞事情 把舞台交给我
                    seekBar.parent.requestDisallowInterceptTouchEvent(true)
                    /**
                     * DOWN时仅仅显示ui，不对进度做任何处理 所以将事件拦截，这样onclick无法收到事件
                     */
                    intercept = true
                    val drawable = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.seekbar_thumb,
                        null
                    )
                    val drawable1 = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.seekbar_progress_down,
                        null
                    )
                    seekBar.thumb = drawable
                    seekBar.progressDrawable = drawable1
                    //显示时间 当前进度时间：总时长  00:10 / 00:31
                    timeBar.visibility = View.VISIBLE
//                    timeBar.text = "00:00 / $seconds"
                }
                MotionEvent.ACTION_MOVE -> {
                    endX = event.x
                    val offsetX = endX - startX
                    //不拦截事件，将事件交给click等处理，只有move事件可以更新进度条
                    intercept = abs(offsetX) <= 10
                }
                else -> {
                    endX = event.x
                    seekBarTouchDown = true
                    /**
                     * UP时拦截事件，防止用户仅仅点击进度条导致的进度更新，此时仅将进度条恢复默认状态即可
                     */
                    intercept = true
                    val drawable3 = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.seekbar_thumb_transparent,
                        null
                    )
                    val drawable4 = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.seekbar_progress_default,
                        null
                    )
                    seekBar.thumb = drawable3
                    seekBar.progressDrawable = drawable4
                    //将时间显示隐藏
                    timeBar.visibility = View.GONE

                    val offsetX = endX - startX
                    //不拦截事件，将事件交给click等处理，只有move事件可以更新进度条
                    if (abs(offsetX) >= 10) { //这个10是一个阈值 防止点击时滑动进度条
                        //当UP时将视频的进度更新到SeekBar移动后的位置
                        val progress = seekBar.progress
                        val currentPosition = (progress * exoPlayer.duration) / 100
                        exoPlayer.seekTo(currentPosition)
                    }
                }
            }
            intercept
        }
    }

    /**
     * 如果不在回收视图时将exoplayer释放资源 将会在数量达到一定数量时崩溃 这个数量可能是几个或十几个
     */
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is MyViewHolder) {
            holder.exoPlayer?.release()
        }
    }

    private fun createMediaSource(position: Int): MediaSource {
        // Produces DataSource instances through which media data is loaded.
        if (mediaSource == null) {
            val dataSourceFactory = DefaultDataSourceFactory(
                context,
                com.google.android.exoplayer2.util.Util.getUserAgent(context, "DYExoPlayer")
            )
            mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
        }

        // This is the MediaSource representing the media to be played.
        return mediaSource!!.createMediaSource(Uri.parse(urls[position]))
    }

    /**
     * 音量变化时调用
     */
    fun volumeChange(volume: Int, maxVolume: Int) {
        val holder = holders[viewPager.currentItem]
        val seekBar = holder?.seekBar
        //获取当前视频的总时长 来判断SeekBar目前是否在显示
        val duration = holder?.exoPlayer?.duration ?: 0
        val timeSeconds = Util.getStringForTime(duration)
        if (timeSeconds < 30) {
            seekBar?.visibility = View.VISIBLE
        }
        //先停止视频播放进度显示的任务
        removeSeekHandler()
        //获取对应的SeekBar设置其progressDrawable的显示
        val drawable =
            ResourcesCompat.getDrawable(context.resources, R.drawable.seekbar_volume_default, null)
        seekBar?.apply {
            progressDrawable = drawable
            //更新进度显示  设置最大值
            progress = (volume * 100) / maxVolume
            hideVolumeRunnable(this, timeSeconds)
        }
    }

    /**
     * 1.5秒后 隐藏当前音量进度条显示 转而显示视频播放进度
     */
    private fun hideVolumeRunnable(seekBar: SeekBar, timeSeconds: Int) {
        removeVolumeRunnable()
        volumeRunnable = Runnable {  //这个任务不能懒加载
            val defaultDrawable = ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.seekbar_progress_default,
                null
            )
            seekBar.progressDrawable = defaultDrawable
            if (timeSeconds >= 30) {  //根据当前播放的视频的总进度 决定音量进度显示后应该启动视频播放进度的监听or还是隐藏SeekBar
                //显示过后 执行视频进度显示
                reStartSeekHandler()
            } else {
                seekBar.visibility = View.GONE
            }
        }
        handler.postDelayed(volumeRunnable, 3000)
    }

    /**
     * 在音乐播放的时候 bgm处于旋转状态   这里使用属性动画 view动画其实更加简单 不用添加tag 但是他不支持暂停 只能清除动画 不适合这个场景
     */
    private fun musicRotate(view: View) {
        val objectAnimator = view.tag as? ObjectAnimator
        if (objectAnimator != null) {  //避免重复加载动画
            objectAnimator.resume()
            return
        }
        val animator = ObjectAnimator.ofFloat(view, "rotation", 0f, +360f)
        animator?.apply {
            repeatCount = -1
            interpolator = LinearInterpolator()
            duration = 7000
            start()
        }
        view.tag = animator
    }

    /**
     * 清空隐藏音量进度条的任务 在每次启动新任务时  在任务没结束时用户点击SeekBar时都需要清空任务
     */
    private fun removeVolumeRunnable() {
        handler.removeCallbacks(volumeRunnable)
    }

    /**
     * 将评论 分享等点击事件回调出去 由外部处理
     */
    interface VideoControlListener {
        /**
         * 评论的点击事件
         */
        fun commentClick(position: Int)

        /**
         * 分享的点击事件
         */
        fun shareClick(position: Int)
    }

    fun setVideoControlListener(listener: VideoControlListener) {
        this.videoControlListener = listener
    }

    /**
     * 提供给外部暂停播放视频 在页面不显示时 或Home键按下时
     */
    fun playWhenUnready() {
        val myViewHolder = this.holders[currentPosition]
        myViewHolder?.exoPlayer?.playWhenReady = false
    }

    fun playWhenReady() {
        val myViewHolder = this.holders[currentPosition]
        myViewHolder?.exoPlayer?.playWhenReady = true
    }
}