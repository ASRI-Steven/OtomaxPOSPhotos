package com.example.otomaxposphotos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otomaxposphotos.api.PhotosApiService
import com.example.otomaxposphotos.model.Photos
import kotlinx.coroutines.launch

class UkuranbanPhotosViewModel : ViewModel() {

    private val _ukuranban = MutableLiveData<Photos>()
    val ukuranban: LiveData<Photos> = _ukuranban

    init {
        getUkuranbanPhotos()
    }

    fun getUkuranbanPhotos() {
        viewModelScope.launch {
            _ukuranban.value = PhotosApiService().getUkuranbanPhotos()
        }
    }
}