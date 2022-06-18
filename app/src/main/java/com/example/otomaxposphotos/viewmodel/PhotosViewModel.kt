package com.example.otomaxposphotos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.otomaxposphotos.api.PhotosApiService
import com.example.otomaxposphotos.datasource.PhotosDataSource
import kotlinx.coroutines.flow.mapLatest

class PhotosViewModel : ViewModel() {
    val photos = Pager(config = PagingConfig(pageSize = 1), pagingSourceFactory = {
        PhotosDataSource(PhotosApiService())
    }).flow.cachedIn(viewModelScope).mapLatest { it -> it.filter { it.quantity != 0 } }

    private val _isSelect = MutableLiveData<Boolean>(false)
    val isSelect: LiveData<Boolean> = _isSelect

    fun changeSelect(select: Boolean) {
        _isSelect.value?.let {
            _isSelect.value = select
        }
    }
}