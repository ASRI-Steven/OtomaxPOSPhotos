package com.example.otomaxposphotos.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otomaxposphotos.api.PhotosApiService
import com.example.otomaxposphotos.model.Photos
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AksesorisPhotosViewModel : ViewModel() {

    private val _aksesoris = MutableLiveData<Photos>()
    val aksesoris: LiveData<Photos> = _aksesoris

    init {
        getAksesorisPhotos()
    }

    fun getAksesorisPhotos() {
        viewModelScope.launch {
            val response = PhotosApiService().getAksesorisPhotos()
            if (response.code() in 200..206) {
                _aksesoris.value = response.body()
            }else{
                val exception = Exception("Response code fail : ${response.code()}")
                val crashlytics = FirebaseCrashlytics.getInstance()
                crashlytics.recordException(exception)

                throw exception
            }
        }
    }
}