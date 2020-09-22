package com.duoshine.douyin.data

import com.duoshine.douyin.adapter.CommentAdapter
import com.duoshine.douyin.model.CommentModel
import com.duoshine.douyin.model.Comments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.w3c.dom.Comment

class ReplyService {


    suspend fun loadComment(parentId: String): CommentModel {
        return withContext(Dispatchers.IO) {
            delay(1500)
            val data = ArrayList<Comments>()
            for (id in 1 until 10) {
                data.add(
                    Comments(
                        (id + parentId.toInt() + 5).toString(),
                        parentId,
                        "",
                        "duo_shine",
                        "当前是第${id}条回复",
                        "2020-09-03 22:00:00",
                        10,
                        false,
                        9000,
                        CommentAdapter.COMMENT_REPLY,
                        0
                    )
                )
            }
            CommentModel("988", 100, 10, false, data)
        }
    }
}	
