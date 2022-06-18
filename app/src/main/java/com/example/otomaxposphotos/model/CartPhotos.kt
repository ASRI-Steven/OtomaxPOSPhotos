package com.example.otomaxposphotos.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos_database")
data class CartPhotos(
    @PrimaryKey
    val id: Int?,
    val name: String?,
    var buyprice: Long?,
    var sellprice: Long?,
    var totalBuyprice: Long?,
    var totalSellprice: Long?,
    var quantity: Int?
)