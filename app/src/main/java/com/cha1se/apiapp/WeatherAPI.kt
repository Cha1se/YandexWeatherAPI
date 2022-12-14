package com.cha1se.apiapp

import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface WeatherAPI {

    @Headers("X-Yandex-API-Key: 679fdc2d-0e28-4e5a-9988-dc71e74298b8")
    @GET("informers?lat=53.721152&lon=91.442387")
    fun getWeatherList(): Call<DataList>

    companion object {

        var base_url = "https://api.weather.yandex.ru/v2/"

        fun create(): WeatherAPI {

            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(base_url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            return retrofit.create(WeatherAPI::class.java)

        }

    }
}