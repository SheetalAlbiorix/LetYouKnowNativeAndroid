package com.letyouknow.retrofit

import android.util.Log
import com.google.gson.GsonBuilder
import com.letyouknow.LetYouKnowApp
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


object GZipRetrofitClient {

    //    private const val MainServer = "https://lykbuyerapi.azurewebsites.net/api/"
    private const val MainServer = "https://lykbuyerwebapidemo.azurewebsites.net/api/"

    const val ImageMainServer = "http://api.drfriday.in/";
    const val paymentToken =
        "pk_test_51HaDBECeSnBm0gpFvqOxWxW9jMO18C1lEIK5mcWf6ZWMN4w98xh8bPplgB8TOLdhutqGFUYtEHCVXh2nHWgnYTDw00Pe7zmGIA"

    private val retrofitClient: Retrofit.Builder by lazy {


        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader(
                        "Authorization",
                        "Bearer ${
                            if (LetYouKnowApp.getInstance()?.getAppPreferencesHelper()
                                    ?.isPayment() == false
                            )
                                LetYouKnowApp.getInstance()?.getAppPreferencesHelper()
                                    ?.getUserData()?.authToken
                            else paymentToken
                        }"
                    )
                    .build()
                return chain.proceed(newRequest)
            }

        }).build()
        Log.e(
            "Header",
            LetYouKnowApp.getInstance()?.getAppPreferencesHelper()?.getUserData()?.authToken!!
        )
        //Encryption Interceptor
        //Encryption Interceptor
        /*  val gzipInterceptor = GzipInterceptor(LetYouKnowApp.getInstance()?.getAppPreferencesHelper()?.getUserData()?.authToken)
          val okHttpClient: OkHttpClient = OkHttpClient()
              .newBuilder() //httpLogging interceptor for logging network requests
              .addInterceptor(gzipInterceptor)
              .build()
  */

        val gson = GsonBuilder()
            .setLenient()
            .create()
        Retrofit.Builder()
            .baseUrl(MainServer)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    val apiInterface: ApiInterface by lazy {
        retrofitClient
            .build()
            .create(ApiInterface::class.java)
    }
}
