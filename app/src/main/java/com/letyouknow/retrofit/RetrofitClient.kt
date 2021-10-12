package com.letyouknow.retrofit

import android.util.Log
import com.letyouknow.LetYouKnowApp
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

//    private const val MainServer = "https://lykbuyerapi.azurewebsites.net/api/"
private const val MainServer = "https://lykbuyerwebapidemo.azurewebsites.net/api/"

    const val ImageMainServer = "http://api.drfriday.in/";
    const val paymentToken =
        "pk_test_51HaDBECeSnBm0gpFvqOxWxW9jMO18C1lEIK5mcWf6ZWMN4w98xh8bPplgB8TOLdhutqGFUYtEHCVXh2nHWgnYTDw00Pe7zmGIA"

    private val retrofitClient: Retrofit.Builder by lazy {

        /*val levelType: Level = if (BuildConfig.BUILD_TYPE.contentEquals("debug"))
            Level.BODY else Level.NONE*/

        val logging = HttpLoggingInterceptor()
        //logging.setLevel(levelType)

        /*val okhttpClient = OkHttpClient.Builder()
        okhttpClient.addInterceptor(logging)*/

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

            /* @Throws(IOException::class)
             fun intercept(chain: Interceptor.Chain): Response? {
                 val newRequest: Request = chain.request().newBuilder()
                     .addHeader("Authorization", "Bearer $token")
                     .build()
                 return chain.proceed(newRequest)
             }*/
        }).build()
        Log.e(
            "Header",
            LetYouKnowApp.getInstance()?.getAppPreferencesHelper()?.getUserData()?.authToken!!
        )

        Retrofit.Builder()
            .baseUrl(MainServer)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
    }

    val apiInterface: ApiInterface by lazy {
        retrofitClient
            .build()
            .create(ApiInterface::class.java)
    }
}
