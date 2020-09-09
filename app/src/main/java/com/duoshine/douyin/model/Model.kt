package com.duoshine.douyin.model

data class CommentModel(
    val videoId: String, //视频唯一id
    val count: Int, //总评论数
    val page: Int,//当前页数量
    val next: Boolean,//是否有下一页
    var comment: ArrayList<Comments>?//评论列表
)
fun ArrayList<Comments>.clone(): Any {
    return this.clone()
}

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
