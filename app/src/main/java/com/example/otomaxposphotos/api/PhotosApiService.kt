package com.example.otomaxposphotos.api

import android.media.Rating
import com.example.otomaxposphotos.model.Photos
import com.example.otomaxposphotos.utils.Utils
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface PhotosApiService {
    @GET("products")
    suspend fun getPhotos(@Query("page") page: Int): Photos

    @GET("search")
    suspend fun getSearchPhotos(@Query("page") page: Int, @Query("keyword") keyword: String?, @Query("category") category: String?): Photos

    @GET("filter")
    suspend fun getFilterPhotos(@Query("page") page: Int, @Query("category") category: String? = null): Photos

    @GET("categories")
    suspend fun getCategoriesPhotos(): Photos

    @GET("ring")
    suspend fun getRingPhotos(): Photos

    @GET("lebar")
    suspend fun getLebarPhotos(): Photos

    @GET("et")
    suspend fun getEtPhotos(): Photos

    @GET("pcd")
    suspend fun getPcdPhotos(): Photos

    @GET("ukuranban")
    suspend fun getUkuranbanPhotos(): Photos

    @GET("aksesoris")
    suspend fun getAksesorisPhotos(): Photos

    companion object {
        private const val BASE_URL = Utils.BASE_URL

        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val interceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        val client by lazy { OkHttpClient.Builder().addInterceptor(interceptor).build() }


        operator fun invoke(): PhotosApiService = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .client(client)
            .build()
            .create(PhotosApiService::class.java)
    }
}