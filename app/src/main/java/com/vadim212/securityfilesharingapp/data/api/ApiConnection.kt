package com.vadim212.securityfilesharingapp.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class ApiConnection {
    lateinit var retrofit: Retrofit

    fun initializeRetrofit(): Retrofit {
        retrofit = Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(initializeOkHttpClient())
            .build()
        return retrofit
    }

    fun initializeOkHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
            .addInterceptor(initializeHttpLogging())
            .build()
        return client
    }

    fun initializeHttpLogging(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    fun initializeApi(): ApiInterface {
        return initializeRetrofit().create(ApiInterface::class.java)
    }
}