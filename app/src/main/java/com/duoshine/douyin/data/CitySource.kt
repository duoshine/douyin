package com.duoshine.douyin.data

import android.util.Log
import androidx.paging.PagingSource
import com.duoshine.douyin.model.CityModel

class CitySource(
    private val backend: CityService
) : PagingSource<Int, CityModel>() {

    private val TAG = "CitySource"

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CityModel> {
        return try {
            //从后台或数据库异步获取数据
            val response = backend.getData()
            var nextKey = response[response.size - 1].flag
            Log.d(TAG, "从后台加载新的同城视频:$response")
            //当已经没有下一页数据时返回指定的错误 表示数据已加载完毕
            if (nextKey >= 100) {
                return LoadResult.Error(NoMoreException())
            }
            //组成成功
            LoadResult.Page(
                data = response,
                prevKey = null, // Only paging forward.
                nextKey = if (nextKey >= 100) null else nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
