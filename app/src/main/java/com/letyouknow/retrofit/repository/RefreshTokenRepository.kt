package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.RefreshTokenData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object RefreshTokenRepository {
    fun refresh(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<RefreshTokenData> {
        val submitDealLCDData = MutableLiveData<RefreshTokenData>()
        val call = RetrofitClient.apiInterface.refresh(request)

        call.enqueue(object : Callback<RefreshTokenData> {
            override fun onFailure(call: Call<RefreshTokenData>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<RefreshTokenData>,
                response: Response<RefreshTokenData>,
            ) {
                Log.v("DEBUG : ", response.body().toString())
                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    submitDealLCDData.value = data!!
                } else if (response.code() == 401) {
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    /*  if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                          Toast.makeText(
                              context,
                              response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                              Toast.LENGTH_LONG
                          ).show()*/
                    AppGlobal.isAuthorizationFailed(context)
                }
            }
        })
        return submitDealLCDData
    }
}