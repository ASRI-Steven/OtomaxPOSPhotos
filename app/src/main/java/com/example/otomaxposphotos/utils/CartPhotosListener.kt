package com.example.otomaxposphotos.utils

import com.example.otomaxposphotos.model.CartPhotos

interface CartPhotosListener {
    fun onUpdateCartPhoto(cartPhotos: CartPhotos)
    fun onDeleteCartPhotos(isSelected: Boolean, id: ArrayList<Int>)
}