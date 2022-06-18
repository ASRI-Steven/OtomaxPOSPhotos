package com.example.otomaxposphotos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otomaxposphotos.api.PhotosApiService
import com.example.otomaxposphotos.model.Photos
import kotlinx.coroutines.launch

class EtPhotosViewModel : ViewModel() {

    private val _et = MutableLiveData<Photos>()
    val et: LiveData<Photos> = _et

    init {
        getEtPhotos()
    }

    fun getEtPhotos() {
        viewModelScope.launch {
            _et.value = PhotosApiService().getEtPhotos()
        }
    }
}