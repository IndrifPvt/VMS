package com.indrif.vms.data.api

import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit

import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import retrofit2.http.POST
import retrofit2.http.Multipart
import com.google.gson.GsonBuilder
import com.indrif.vms.BuildConfig
import com.indrif.vms.models.ResponseModel
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.http.GET



interface ApiService {
    companion object Factory {
        fun create(): ApiService {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(180, TimeUnit.SECONDS)
                    .readTimeout(180, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .build()
            val gson = GsonBuilder()
                    .setLenient()
                    .create()
            return Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(BuildConfig.BASE_URL).client(okHttpClient)
                    .build().create(ApiService::class.java);
        }
    }

    @POST("login")
    fun loginUser(@Body map: Map<String, String>): Observable<ResponseModel>

    @POST("forgotPassword")
    fun resetPassword(@Body map: Map<String, String>): Observable<ResponseModel>

    @POST("changePassword")
    fun changePassword(@Body map: Map<String, String>): Observable<ResponseModel>

    @POST("logOut")
    fun logOutUser(@Body map: Map<String, String>): Observable<ResponseModel>

    @GET("getSites")
    fun getSiteList(): Observable<ResponseModel>

    @POST("userHistory")
    fun userHistory(@Body map: Map<String, String>): Observable<ResponseModel>

    @Multipart
    @POST("checkInUser")
    fun checkInUser( @PartMap map: HashMap<String,RequestBody>, @Part userImagePart: MultipartBody.Part): Observable<ResponseModel>



}
