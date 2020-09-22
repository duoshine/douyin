package com.duoshine.douyin.data

import com.duoshine.douyin.adapter.CommentAdapter
import com.duoshine.douyin.model.CommentModel
import com.duoshine.douyin.model.Comments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.w3c.dom.Comment

class CommentService {

    private var count = 0

    suspend fun loadComment(): CommentModel {
        return withContext(Dispatchers.IO) {
            delay(100)
            val data = ArrayList<Comments>()
            var next = false
            if (count < 1) {
                next = true
                data.add(
                    Comments(
                        count.toString(), null, "", "duo_shine", "你笑起来真好看\uE056",
                        "2020-09-03 22:00:00", 10, false, 9000, CommentAdapter.COMMENT, 0
                    )
                )
            } else {
                next = false
            }
            count++
            CommentModel("988", 100, 10, next, data)
        }
    }
}	
