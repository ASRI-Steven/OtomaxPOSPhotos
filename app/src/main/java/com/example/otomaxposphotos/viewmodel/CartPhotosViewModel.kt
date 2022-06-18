package com.example.otomaxposphotos.viewmodel

import androidx.lifecycle.*
import com.example.otomaxposphotos.database.PhotosDao
import com.example.otomaxposphotos.model.Data
import com.example.otomaxposphotos.model.CartPhotos
import com.example.otomaxposphotos.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartPhotosViewModel(private val photosDao: PhotosDao) : ViewModel() {

    val cartPhotos: LiveData<List<CartPhotos>> = photosDao.getPhotos().asLiveData()
    val totalPhotos: LiveData<Long> = photosDao.getTotalPhotos().asLiveData()

    fun getPhotosById(id: Int?): List<CartPhotos> {
        return photosDao.getPhotoById(id!!)
    }

    fun insertPhotos(data: Data) {
        val buyprice = Utils.convertNumber(data.buyprice!!)
        val sellprice = Utils.convertNumber(data.sellprice!!)
        val cartPhotos = CartPhotos(
            data.id,
            data.name,
            buyprice,
            sellprice,
            buyprice,
            sellprice,
            1
        )

        viewModelScope.launch(Dispatchers.IO) {
            photosDao.insertPhoto(cartPhotos)
        }
    }

    fun updatePhoto(cartPhotos: CartPhotos) {
        viewModelScope.launch(Dispatchers.IO) {
            photosDao.updatePhoto(cartPhotos)
        }
    }

    fun deletePhotos(id: ArrayList<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            photosDao.deletePhotos(id)
        }
    }

    fun deleteAllPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            photosDao.deleteAllPhotos()
        }
    }
}

class CartPhotosViewModelFactory(private val photosDao: PhotosDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartPhotosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartPhotosViewModel(photosDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}