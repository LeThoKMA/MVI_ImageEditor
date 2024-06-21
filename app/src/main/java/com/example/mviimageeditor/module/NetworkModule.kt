package com.example.mviimageeditor.module

import android.content.Context
import android.preference.PreferenceManager
import com.example.mviimageeditor.utils.ACCEPT
import com.example.mviimageeditor.utils.ACCEPT_LANGUAGE
import com.example.mviimageeditor.utils.AUTHORIZATION
import com.example.mviimageeditor.utils.BASE_URL_API
import com.example.mviimageeditor.utils.CONTENT_TYPE
import com.example.mviimageeditor.utils.PREF_ACCESS_TOKEN
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    private var mToken = ""
    private const val TIME_OUT: Long = 10
    private var mLanguage = "vi"

    internal fun providePostApi(retrofit: Retrofit): Api {
        return retrofit.create(Api::class.java)
    }

    internal fun provideRetrofitInterface(context: Context): Retrofit {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        if (mToken == "") {
            mToken = prefs.getString(PREF_ACCESS_TOKEN, "") ?: ""
        }

        val httpClient = OkHttpClient.Builder()
        httpClient.protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
        httpClient.connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        httpClient.readTimeout(TIME_OUT, TimeUnit.SECONDS)

        httpClient.addInterceptor { chain ->
            val request =
                chain.request().newBuilder()
                    .addHeader(CONTENT_TYPE, "application/json")
                    .addHeader(ACCEPT, "*/*")
                    .addHeader(AUTHORIZATION, "Bearer $mToken")
                    .addHeader(ACCEPT_LANGUAGE, mLanguage)
                    .build()
            chain.proceed(request)
        }

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(logging) // <-- this is the important line!

        return Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }
}
