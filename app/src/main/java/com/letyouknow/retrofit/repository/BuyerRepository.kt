package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.BuyerInfoData
import com.letyouknow.model.SubmitPriceErrorData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object BuyerRepository {

    fun buyerApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<BuyerInfoData> {
        AppGlobal.printRequestAuth("buyer req", Gson().toJson(request))
        val buyerData = MutableLiveData<BuyerInfoData>()
        val call = RetrofitClient.apiInterface.buyer(request)

        call.enqueue(object : Callback<BuyerInfoData> {
            override fun onFailure(call: Call<BuyerInfoData>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<BuyerInfoData>,
                response: Response<BuyerInfoData>,
            ) {
                // Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    // Log.v("buyer Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    buyerData.value = data!!
                } else if (response.code() == 401) {
                    // Log.v("buyer Resp ", response.toString())
                    AppGlobal.isAuthorizationFailed(context)
                } else if (response.code() == 400) {
                    // Log.v("buyer Resp ", response.toString())
                    val dataError = Gson().fromJson(
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                        SubmitPriceErrorData::class.java
                    )
                    AppGlobal.alertError(
                        context,
                        dataError.title
                    )
                } else {
                    // Log.v("buyer Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                        AppGlobal.alertError(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                        )
                }
            }
        })
        return buyerData
    }
}