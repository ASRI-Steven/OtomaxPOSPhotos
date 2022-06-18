package com.example.otomaxposphotos.database

import androidx.room.*
import com.example.otomaxposphotos.model.CartPhotos
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotosDao {

    @Query("SELECT * from photos_database")
    fun getPhotos(): Flow<List<CartPhotos>>

    @Query("SELECT * from photos_database WHERE id = :id")
    fun getPhotoById(id: Int): List<CartPhotos>

    @Query("SELECT COUNT(id) from photos_database")
    fun getTotalPhotos(): Flow<Long>

    @Insert
    suspend fun insertPhoto(cartPhotos: CartPhotos)

    @Update
    suspend fun updatePhoto(cartPhotos: CartPhotos)

    @Query("DELETE from photos_database WHERE id IN (:id)")
    fun deletePhotos(id: ArrayList<Int>)

    @Query("DELETE from photos_database")
    suspend fun deleteAllPhotos()
}