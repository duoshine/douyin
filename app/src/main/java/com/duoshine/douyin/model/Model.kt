package com.duoshine.douyin.model

import com.hyphenate.chat.EMConversation

data class CommentModel(
    val videoId: String, //视频唯一id
    val count: Int, //总评论数
    val page: Int,//当前页数量
    val next: Boolean,//是否有下一页
    var comment: ArrayList<Comments>?//评论列表
)

data class Comments(
    val commentId: String, //当前评论id
    var parentId: String?,//父评论id 如果存在 说明是回复
    val avatar: String,//评论人头像
    val name: String,//评论人
    val content: String, //评论内容
    val date: String, //评论时间
    val replyCount: Int, //评论回复总数
    var replyExpanded: Boolean = false, //当前评论的更多回复是否展开  true为展开状态
    val starCount: Int, //赞的数量
    var itemType: Int, //item的类型 评论 回复 评论加载中 回复加载中。。。
    var itemState: Int = 1 //最后一个item的显示状态 1表示不显示  2表示没有更多了 3表示还可以继续加载更多
)


//每个人都有唯一的抖音号  可以使用抖音号来请求
data class CommentReplyModel(
    val count: Int, //总回复数
    val page: Int,//当前页
    val next: Boolean,//是否有下一页
    val comment: MutableList<CommentReply>//回复列表
)


data class CommentReply(
    val avatar: String,//回复人头像
    val name: String,//回复人名称
    val content: String,//回复内容
    val date: String//回复时间
)

//video信息 不应该分开编写这个实体
data class VideoModel(
    var star: Boolean, //是否点赞
    var starCount: Int,//赞总数
    var CommentCount: Int,//评论总数
    var shareCount: Int,//分享总数
    var copywriting: String, //文案
    var author: String, //作者
    var music: String //视频bgm
)

//同城
data class CityModel(
    var videoUrl: String, //视频的url
    var placeholderUrl: String, //视频封面占位符图片url
    var videoStar: Int, //视频点赞数
    var flag: Int//视频flag 用于diffutil 这里模拟的数据可能url都是一致的
)

//会话
data class ConversationModel(
    var username: String, //会话人
    var EMConversation: EMConversation  //代表和一个用户的对话，包含发送和接收的消息 下面的示例会取得对话中未读的消息数：
)

//聊天消息的类型
enum class Chat {
    //发送 接收
    SEND, RECEIVER
}

//聊天消息
data class ChatMessageModel(
    var id: Int, //消息id
    var msg: String, //消息内容
    var type: Chat, //消息类型
    var time: String //发送消息的时间
)
