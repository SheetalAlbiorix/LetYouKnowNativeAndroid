package com.letyouknow.retrofit

import com.pionymessenger.model.LoginData
import com.pionymessenger.model.SignupData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("auth/login")
    fun login(@Body request: HashMap<String, String>): Call<LoginData>

    @POST("userprofile")
    fun signUp(@Body request: HashMap<String, String>): Call<SignupData>

    /*  @POST("forgotpassword")
     fun forgotPassword(@Body request: HashMap<String, String>): Call<BaseResponse<LoginData>>*/

}