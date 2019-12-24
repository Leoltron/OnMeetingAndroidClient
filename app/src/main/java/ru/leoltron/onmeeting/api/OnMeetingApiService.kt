package ru.leoltron.onmeeting.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.leoltron.onmeeting.api.model.CardViewModel
import ru.leoltron.onmeeting.api.model.TagViewModel
import ru.leoltron.onmeeting.api.model.UserModel
import java.net.CookieManager
import java.net.CookiePolicy

class OnMeetingApiService private constructor() {
    val gson: Gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()

    var tags: ExternalList<TagViewModel> = ExternalList { api.getTags() }
    var users: ExternalList<UserModel> = ExternalList { api.getUsers() }
    var cards: ExternalList<CardViewModel> = ExternalList { api.cards }

    fun refreshAll() {
        tags.refresh()
        users.refresh()
        cards.refresh()
    }

    private val mRetrofit: Retrofit

    val api: IOnMeetingApi
        get() = mRetrofit.create(IOnMeetingApi::class.java)

    var currentUsername: String? = null

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
                .addConverterFactory(GsonConverterFactory.create(gson))
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
