package com.letyouknow.retrofit

import com.pionymessenger.model.BaseResponse
import com.pionymessenger.model.LoginData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("login")
    fun login(@Body request: HashMap<String, String>): Call<BaseResponse<LoginData>>

    /*@POST("users/")
    fun signUp(@Body request: HashMap<String, String>): Call<BaseResponse<SignupData>>

    @POST("forgotpassword")
    fun forgotPassword(@Body request: HashMap<String, String>): Call<BaseResponse<LoginData>>*/

}