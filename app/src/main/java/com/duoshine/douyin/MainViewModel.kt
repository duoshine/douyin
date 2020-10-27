package com.duoshine.douyin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.duoshine.douyin.adapter.CommentAdapter
import com.duoshine.douyin.data.CommentService
import com.duoshine.douyin.data.CommentSource
import com.duoshine.douyin.data.ReplyService
import com.duoshine.douyin.data.ReplySource
import com.duoshine.douyin.model.Comments
import kotlinx.coroutines.delay
import java.util.ArrayList

class MainViewModel : ViewModel() {
    private val TAG = "MainViewModel"
    private var toggle = true

    //刷新状态
    private var refreshState: MutableLiveData<RefreshState>? = null
    private var followRefreshState: MutableLiveData<RefreshState>? = null
    private var comments: MutableLiveData<MutableList<Comments>>? = null

    private val commentSource: CommentSource? by lazy {
        CommentSource(CommentService())
    }

    private val replySource: ReplySource? by lazy {
        ReplySource(ReplyService())
    }

    enum class RefreshState {
        START, SUCCESS, FAIL, COMPLETE
    }

    /**
     * 将打开的回复关闭
     */
    fun replyClose(
        parentId: String,
        oldList: ArrayList<Comments>
    ): MutableLiveData<MutableList<Comments>> {
        val listIterator = oldList.listIterator()
        while (listIterator.hasNext()) {
            val next = listIterator.next()
            if (next.parentId == parentId) {
                listIterator.remove()
            }
            if (next.commentId == parentId) {
                next.replyExpanded = false
            }
        }
        //  加载评论
        comments?.value = oldList
        return comments!!
    }

    /**
     * 获取评论
     */
    suspend fun getComments(oldList: ArrayList<Comments>): MutableLiveData<MutableList<Comments>> {
        val commentModel = commentSource!!.load()
        oldList.addAll(commentModel.comment!!)
        //  加载评论
        comments?.value = oldList
        return comments!!
    }

    /**
     * 追加评论
     */
    fun addComment(
        oldList: ArrayList<Comments>,
        comments: Comments
    ): MutableLiveData<MutableList<Comments>> {
        oldList.add(0, comments)
        //  加载评论
        this.comments?.value = oldList
        return this.comments!!
    }

    /**
     * 获取评论的回复 根据评论id取得该评论的回复
     * 根据评论的item位置插入回复
     */
    suspend fun getCommentReply(
        position: Int,
        parentId: String,
        oldList: ArrayList<Comments>
    ): MutableLiveData<MutableList<Comments>> {
        var oldComment = oldList[position]
        var insetPosition = position
        if (oldComment.itemType == CommentAdapter.COMMENT_REPLY_LOADING) {
            oldComment.itemState = 1
        } else {
            insetPosition += 1
            oldComment.replyExpanded = true //这个评论的回复目前处于打开状态
            //构建一个加载状态的item
            oldComment = Comments(
                "9999", parentId, "",
                "", "", "", 1, false, 1, CommentAdapter.COMMENT_REPLY_LOADING, 1
            )
            oldList.add(insetPosition, oldComment)
        }

        comments?.value = oldList //先显示一个加载中的状态

        //加载新数据
        val commentModel = replySource?.load(parentId)
        //加载成功后 将加载中的item状态改变 根据后台是否有下一页数据来决定 有就显示加载更多 没有就显示收起
        oldComment.itemState = if (commentModel?.next == true) 3 else 2
        val newComments = commentModel!!.comment!!
        oldList.addAll(insetPosition, newComments)
        //  加载评论
        comments?.value = oldList
        return comments!!
    }

    init {
        refreshState = MutableLiveData()
        followRefreshState = MutableLiveData()
        comments = MutableLiveData()
    }

    /* private val defaultUrls = listOf(
         "http://192.168.11.193:8080/douyin1.mp4",
         "http://192.168.11.193:8080/douyin.mp4",
         "http://192.168.11.193:8080/douyin2.mp4",
         "http://192.168.11.193:8080/douyin3.mp4",
         "http://192.168.11.193:8080/douyin4.mp4",
         "http://192.168.11.193:8080/douyin5.mp4",
         "http://192.168.11.193:8080/douyin6.mp4",
         "http://192.168.11.193:8080/douyin7.mp4",
         "http://192.168.11.193:8080/douyin8.mp4",
         "http://192.168.11.193:8080/douyin9.mp4",
         "http://192.168.11.193:8080/douyin10.mp4"
     )

     private val newUrls = listOf(
         "http://192.168.11.193:8080/douyin7.mp4",
         "http://192.168.11.193:8080/douyin1.mp4",
         "http://192.168.11.193:8080/douyin.mp4",
         "http://192.168.11.193:8080/douyin2.mp4",
         "http://192.168.11.193:8080/douyin3.mp4",
         "http://192.168.11.193:8080/douyin4.mp4",
         "http://192.168.11.193:8080/douyin5.mp4",
         "http://192.168.11.193:8080/douyin6.mp4",
         "http://192.168.11.193:8080/douyin8.mp4",
         "http://192.168.11.193:8080/douyin9.mp4",
         "http://192.168.11.193:8080/douyin10.mp4"
     )*/

    //    本地视频
    private val defaultUrls = listOf(
        "file:///android_asset/douyin1.mp4",
        "file:///android_asset/douyin.mp4",
        "file:///android_asset/douyin2.mp4",
        "file:///android_asset/douyin3.mp4",
        "file:///android_asset/douyin4.mp4",
        "file:///android_asset/douyin5.mp4",
        "file:///android_asset/douyin6.mp4",
        "file:///android_asset/douyin7.mp4",
        "file:///android_asset/douyin8.mp4",
        "file:///android_asset/douyin9.mp4",
        "file:///android_asset/douyin10.mp4"
    )

    private val newUrls = listOf(
        "file:///android_asset/douyin7.mp4",
        "file:///android_asset/douyin1.mp4",
        "file:///android_asset/douyin.mp4",
        "file:///android_asset/douyin2.mp4",
        "file:///android_asset/douyin3.mp4",
        "file:///android_asset/douyin4.mp4",
        "file:///android_asset/douyin5.mp4",
        "file:///android_asset/douyin6.mp4",
        "file:///android_asset/douyin8.mp4",
        "file:///android_asset/douyin9.mp4",
        "file:///android_asset/douyin10.mp4"
    )

    private val urls: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }

    private val followUrls: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>()
    }

    fun urlsLiveData(videoType: Int) = if (videoType == 0) followUrls else urls

    suspend fun requestUrls() {
        delay(1200)
        toggle = !toggle
        refreshState?.value = RefreshState.COMPLETE //先结束刷新
        if (toggle) urls.value = newUrls else urls.value = defaultUrls
    }

    fun startRefresh(pageIndex: Int?) {
        if (pageIndex == 0) {
            followRefreshState?.value = RefreshState.START
        } else if (pageIndex == 1) {
            refreshState?.value = RefreshState.START
        }
    }

    fun getRefreshState(): MutableLiveData<RefreshState> {
        return refreshState!!
    }

    fun getFollowRefreshState(): MutableLiveData<RefreshState> {
        return followRefreshState!!
    }

    suspend fun requestFollowUrls() {
        delay(1200)
        followRefreshState?.value = RefreshState.COMPLETE //先结束刷新
        followUrls.value = listOf()
    }
}
