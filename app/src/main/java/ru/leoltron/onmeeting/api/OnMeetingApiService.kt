package ru.leoltron.onmeeting.api

import java.net.CookieManager
import java.net.CookiePolicy

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OnMeetingApiService private constructor() {
    private val mRetrofit: Retrofit

    val api: IOnMeetingApi
        get() = mRetrofit.create(IOnMeetingApi::class.java)

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY


        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)


        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        client.cookieJar(SessionCookieJar("JSESSIONID")) //finally set the cookie handler on client

        mRetrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
    }

    companion object {

        private const val BASE_URL = "https://on-meeting.herokuapp.com"
        private var instance: OnMeetingApiService = OnMeetingApiService()

        fun getInstance(): OnMeetingApiService {
            return instance
        }
    }
}
