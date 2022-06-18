package com.example.otomaxposphotos.utils

import com.example.otomaxposphotos.model.Data
import com.example.otomaxposphotos.model.Photos

interface PhotosListener {
    fun onPhotosShowAction(isSelected: Boolean)
    fun onPhotosCountResult(result: Int)
    fun onPhotosSendCart(data: Data)
}