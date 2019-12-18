package ru.leoltron.onmeeting.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import ru.leoltron.onmeeting.api.model.CardViewModel
import ru.leoltron.onmeeting.api.model.PrincipalData
import ru.leoltron.onmeeting.api.model.UserModel

interface IOnMeetingApi {

    @get:GET("/api/card/getParticipating")
    val participating: Call<List<CardViewModel>>

    @GET("/api/user/get")
    fun getUser(@Query("name") name: String): Call<UserModel>

    @POST("/login")
    fun login(@Query("username") username: String, @Query("password") password: String): Call<ResponseBody>

    @POST("/register")
    fun register(@Query("username") username: String, @Query("password") password: String): Call<ResponseBody>

    @GET("/principal")
    fun currentPrincipal(): Call<PrincipalData>
}
