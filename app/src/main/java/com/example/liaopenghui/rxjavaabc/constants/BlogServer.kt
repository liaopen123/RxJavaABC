package com.example.liaopenghui.rxjavaabc.constants

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface AppServer {
    @GET("blog/{id}")
    fun getBlog(@Path("id") id:Int):Call<ResponseBody>

    @FormUrlEncoded
    @POST("/form")
    fun testFormUrlEncoded(@Field("username") name:String,@Field("age") age:Int):Call<ResponseBody>


    @POST("/form")
    @FormUrlEncoded
    fun testFormUrlEncoded2(@FieldMap map:Map<String,String>) :Call<ResponseBody>

    @POST("/form")
    @Multipart
    fun testUpload1(@Part("name") name:RequestBody,@Part("age") age:RequestBody,@Part file:MultipartBody.Part)

    @POST("/form")
    @Multipart
    fun testUpload2(@PartMap args:Map<String,RequestBody>,  @Part file:MultipartBody.Part)

    @POST("/form")
    @Multipart
    fun testUpload3(@PartMap args:Map<String, RequestBody> )

}