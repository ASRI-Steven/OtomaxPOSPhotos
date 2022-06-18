package com.example.otomaxposphotos.model

data class Data (
    val id: Int?,
    val name: String?,
    var buyprice: String?,
    var sellprice: String?,
    val image: String?,
    val quantity: Int?,
    var isSelected: Boolean? = false
)