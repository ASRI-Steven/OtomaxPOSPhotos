package com.example.otomaxposphotos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otomaxposphotos.api.PhotosApiService
import com.example.otomaxposphotos.model.Photos
import kotlinx.coroutines.launch

class AksesorisPhotosViewModel : ViewModel() {

    private val _aksesoris = MutableLiveData<Photos>()
    val aksesoris: LiveData<Photos> = _aksesoris

    init {
        getAksesorisPhotos()
    }

    fun getAksesorisPhotos() {
        viewModelScope.launch {
            _aksesoris.value = PhotosApiService().getAksesorisPhotos()
        }
    }
}