package com.example.otomaxposphotos.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.otomaxposphotos.api.PhotosApiService
import com.example.otomaxposphotos.model.Data

class FilterPhotosDataSource(val api: PhotosApiService, val category: String) : PagingSource<Int, Data>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = api.getFilterPhotos(nextPageNumber, category)

            LoadResult.Page(
                data = response.data,
                prevKey = if (nextPageNumber > 0) nextPageNumber - 1 else null,
                nextKey = if (
                    nextPageNumber < response.totalPages!! &&
                    response.data[0].quantity != 0 &&
                    response.data[1].quantity != 0 &&
                    response.data[2].quantity != 0 &&
                    response.data[3].quantity != 0 &&
                    response.data[4].quantity != 0 &&
                    response.data[5].quantity != 0 &&
                    response.data[6].quantity != 0 &&
                    response.data[7].quantity != 0 &&
                    response.data[8].quantity != 0 &&
                    response.data[9].quantity != 0
                ) {
                    nextPageNumber + 1
                } else if (
                    nextPageNumber < response.totalPages!! &&
                    response.data[0].quantity == 0 &&
                    response.data[1].quantity == 0 &&
                    response.data[2].quantity == 0 &&
                    response.data[3].quantity == 0 &&
                    response.data[4].quantity == 0 &&
                    response.data[5].quantity == 0 &&
                    response.data[6].quantity == 0 &&
                    response.data[7].quantity == 0 &&
                    response.data[8].quantity == 0 &&
                    response.data[9].quantity == 0
                ) {
                    null
                } else {
                    null
                }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Data>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1) ?:
            state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}