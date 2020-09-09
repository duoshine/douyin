package com.duoshine.douyin.data

import com.duoshine.douyin.model.CommentModel

class ReplySource(
    private val backend: ReplyService
) : ReplyCallback<CommentModel> {
    override suspend fun load(parentId:String): CommentModel {
        //从后台或数据库异步获取数据
		return backend.loadComment(parentId)
    }
}
