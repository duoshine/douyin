package com.duoshine.douyin.model

data class CommentModel(
    val videoId:String, //视频唯一id
    val count: Int, //总评论数
    val page: Int,//当前页
    val next: Boolean,//是否有下一页
    val comment: MutableList<Comments>//评论列表
)

data class Comments(
    val commentId: String, //当前视频评论id
    val avatar: String,//评论人头像
    val name: String,//评论人
    val content: String, //评论内容
    val date: String, //评论时间
    val replyCount: Int, //评论回复总数
    var replyExpanded:Boolean = false, //当前评论的更多回复是否展开  true为展开状态
    val starCount: Int, //赞的数量
    val reply: MutableList<CommentReply> //回复实体
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
