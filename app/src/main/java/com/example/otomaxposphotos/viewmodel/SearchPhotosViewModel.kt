package com.example.otomaxposphotos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.otomaxposphotos.api.PhotosApiService
import com.example.otomaxposphotos.datasource.SearchPhotosDataSource
import com.example.otomaxposphotos.model.Data
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class SearchPhotosViewModel : ViewModel() {
    fun getSearchPhotos(query: String?, category: String?): Flow<PagingData<Data>> {
        return Pager(config = PagingConfig(pageSize = 1), pagingSourceFactory = {
            SearchPhotosDataSource(PhotosApiService(), query, category)
        }).flow.cachedIn(viewModelScope).mapLatest { it -> it.filter { it.quantity != 0 } }
    }
}