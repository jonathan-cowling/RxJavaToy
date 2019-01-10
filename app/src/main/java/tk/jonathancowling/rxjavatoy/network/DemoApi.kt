package tk.jonathancowling.rxjavatoy.network

import io.reactivex.Single
import retrofit2.Call
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Headers

interface DemoApi {

    @Headers("x-api-key: i7MMYzGGZI2M8lyUFIicL6jjdYmOLX5d43uRrIZF")
    @GET("documents")
    fun getDocuments() : Single<Result<String>>

    @Headers("x-api-key: i7MMYzGGZI2M8lyUFIicL6jjdYmOLX5d43uRrIZF")
    @GET("me")
    fun getProfile(): Single<Result<String>>

    @Headers("x-api-key: i7MMYzGGZI2M8lyUFIicL6jjdYmOLX5d43uRrIZF")
    @GET("documents")
    fun getDocumentsCallback(): Call<String>

    @Headers("x-api-key: i7MMYzGGZI2M8lyUFIicL6jjdYmOLX5d43uRrIZF")
    @GET("me")
    fun getProfileCallback(): Call<String>
}