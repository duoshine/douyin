package com.duoshine.douyin.data

import com.duoshine.douyin.adapter.CommentAdapter
import com.duoshine.douyin.model.CommentModel
import com.duoshine.douyin.model.Comments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.w3c.dom.Comment

class ReplyService {

    private var count = 0

    suspend fun loadComment(parentId: String): CommentModel {
        return withContext(Dispatchers.IO) {
            delay(3000)
            val data = ArrayList<Comments>()
            for (id in count until count + 20) {
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
            count += 20
            var next = true
            if (count > 50) {
                next = false
            }
            CommentModel("988", 100, 10, next, data)
        }
    }
}	
