package com.example.otomaxposphotos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otomaxposphotos.api.PhotosApiService
import com.example.otomaxposphotos.model.Photos
import kotlinx.coroutines.launch

class PcdPhotosViewModel : ViewModel() {

    private val _pcd = MutableLiveData<Photos>()
    val pcd: LiveData<Photos> = _pcd

    init {
        getPcdPhotos()
    }

    fun getPcdPhotos() {
        viewModelScope.launch {
            _pcd.value = PhotosApiService().getPcdPhotos()
        }
    }
}