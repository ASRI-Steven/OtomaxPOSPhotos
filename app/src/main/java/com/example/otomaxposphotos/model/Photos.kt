package com.example.otomaxposphotos.model

import com.squareup.moshi.Json

data class Photos (
    val data: List<Data>,
    @Json(name = "totalpage")
    val totalPages: Int?
)