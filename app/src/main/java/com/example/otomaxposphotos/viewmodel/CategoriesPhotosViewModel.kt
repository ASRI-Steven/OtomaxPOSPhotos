package com.example.otomaxposphotos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otomaxposphotos.api.PhotosApiService
import com.example.otomaxposphotos.model.Photos
import kotlinx.coroutines.launch

class CategoriesPhotosViewModel : ViewModel() {

    private val _categories = MutableLiveData<Photos>()
    val categories: LiveData<Photos> = _categories

    init {
        getCategoriesPhotos()
    }

    fun getCategoriesPhotos() {
        viewModelScope.launch {
            _categories.value = PhotosApiService().getCategoriesPhotos()
        }
    }
}