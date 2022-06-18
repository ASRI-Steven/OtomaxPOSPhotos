package com.example.otomaxposphotos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otomaxposphotos.api.PhotosApiService
import com.example.otomaxposphotos.model.Photos
import kotlinx.coroutines.launch

class RingPhotosViewModel : ViewModel() {

    private val _ring = MutableLiveData<Photos>()
    val ring: LiveData<Photos> = _ring

    init {
        getRingPhotos()
    }

    fun getRingPhotos() {
        viewModelScope.launch {
            _ring.value = PhotosApiService().getRingPhotos()
        }
    }
}