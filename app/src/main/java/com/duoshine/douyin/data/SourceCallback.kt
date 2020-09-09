package com.duoshine.douyin.data

/**
 *Created by chen on 2020
 */
interface SourceCallback<T> {
    suspend fun load(): T
}