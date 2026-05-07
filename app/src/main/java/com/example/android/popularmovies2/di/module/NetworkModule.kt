/*
 * Copyright (c) 2020. This project was submitted by Tarek Bohdima as part of the Android Developer Nanodegree At Udacity.
 * As part of Udacity Honor code, your submissions must be your own work, hence submitting this project as yours will cause you to break the Udacity Honor Code and the suspension of your account.
 * Me, the author of the project, allow you to check the code as a reference, but if you submit it, it's your own responsibility if you get expelled.
 */
package com.example.android.popularmovies2.di.module

import com.example.android.popularmovies2.di.scopes.ApplicationScope
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module(includes = [OkHttpClientModule::class])
class NetworkModule(private val baseUrl: String) {
    @ApplicationScope
    @Provides
    fun retrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(baseUrl)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()

    @ApplicationScope
    @Provides
    fun gson(): Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    @ApplicationScope
    @Provides
    fun gsonConverterFactory(gson: Gson): GsonConverterFactory = GsonConverterFactory.create(gson)
}
