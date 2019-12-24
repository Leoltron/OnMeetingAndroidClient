package ru.leoltron.onmeeting.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import ru.leoltron.onmeeting.api.model.*

interface IOnMeetingApi {

    @get:GET("/api/card/getParticipating")
    val cards: Call<List<CardViewModel>>

    @GET("/api/user/get")
    fun getUser(@Query("name") name: String): Call<UserModel>

    @POST("/login")
    fun login(@Query("username") username: String, @Query("password") password: String): Call<ResponseBody>

    @POST("/register")
    fun register(@Query("username") username: String, @Query("password") password: String): Call<ResponseBody>

    @GET("/principal")
    fun currentPrincipal(): Call<PrincipalData>

    @POST("/api/card/add")
    fun addCard(@Body body: CardAddOrEditModel): Call<ResponseBody>

    @PATCH("/api/card/{id}/edit")
    fun editCard(@Path("id") id: Int, @Body body: CardAddOrEditModel): Call<ResponseBody>

    @GET("/api/tag/getAll")
    fun getTags(): Call<List<TagViewModel>>

    @POST("/api/tag/add")
    fun addTag(@Body tagModel: TagModel): Call<ResponseBody>

    @GET("/api/user/getAll")
    fun getUsers(): Call<List<UserModel>>

    @DELETE("/api/card/{id}/delete")
    fun deleteCard(@Path("id") id: Int): Call<ResponseBody>

}
