package com.duoshine.douyin.data

/**
 *Created by chen on 2020
 */
interface ReplyCallback<T> {
    suspend fun load(parentId:String): T
}