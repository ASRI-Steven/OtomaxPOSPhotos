package com.example.otomaxposphotos

import android.app.Application
import com.example.otomaxposphotos.database.PhotosDatabase

class BaseApplication : Application() {

    val database: PhotosDatabase by lazy { PhotosDatabase.getDatabase(this) }
}