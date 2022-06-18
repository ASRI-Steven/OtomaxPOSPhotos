package com.example.otomaxposphotos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otomaxposphotos.api.PhotosApiService
import com.example.otomaxposphotos.model.Photos
import kotlinx.coroutines.launch

class LebarPhotosViewModel : ViewModel() {

    private val _lebar = MutableLiveData<Photos>()
    val lebar: LiveData<Photos> = _lebar

    init {
        getLebarPhotos()
    }

    fun getLebarPhotos() {
        viewModelScope.launch {
            _lebar.value = PhotosApiService().getLebarPhotos()
        }
    }
}