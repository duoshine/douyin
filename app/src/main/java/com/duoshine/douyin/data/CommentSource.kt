package com.duoshine.douyin.data

import com.duoshine.douyin.model.CommentModel

class CommentSource(
    private val backend: CommentService
) : SourceCallback<CommentModel> {
    override suspend fun load(): CommentModel {
        //从后台或数据库异步获取数据
		return backend.loadComment()
    }
}
