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

    @POST("getdriverjobs")
    fun getJobByDate(@Body map: Map<String, String>): Observable<ResponseModel>

    @POST("getvehiclechecklist")
    fun getchecklist(@Body map: Map<String, String>): Observable<ResponseModel>

    @POST("getpodandsignature")
    fun getpod(@Body map: Map<String, String>): Observable<ResponseModel>

    @Multipart
    @POST("managevehiclechecklist")
    fun uploadVehicleCheckList( @PartMap map: HashMap<String,RequestBody>, @Part surveyImage: ArrayList<MultipartBody.Part>, @Part("vehicle_check_list") vehicleCheckList: JSONObject): Observable<ResponseModel>

    @Multipart
    @POST("updatepodsignature")
    fun uploadPodSign( @PartMap map: HashMap<String,RequestBody>, @Part podImage: ArrayList<MultipartBody.Part>,  @Part signImage: ArrayList<MultipartBody.Part>): Observable<ResponseModel>

    @POST("deleteimage")
    fun deleteImageFromServer(@Body map: Map<String, String>): Observable<ResponseModel>

    @POST("logOut")
    fun logOutUser(@Body map: Map<String, String>): Observable<ResponseModel>

}
