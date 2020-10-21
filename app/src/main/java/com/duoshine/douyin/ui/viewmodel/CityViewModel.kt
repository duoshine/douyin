package com.duoshine.douyin.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.duoshine.douyin.MainViewModel
import com.duoshine.douyin.data.CityService
import com.duoshine.douyin.data.CitySource

/**
 *Created by chen on 2020
 */
class CityViewModel : ViewModel() {
     val flow = Pager(PagingConfig(pageSize = 10)) {
        CitySource(CityService())
    }.flow.cachedIn(viewModelScope)
}