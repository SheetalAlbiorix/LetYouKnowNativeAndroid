package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.UserProfileData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object UserProfileRepository {

    fun userProfileApiCall(
        context: Context
    ): MutableLiveData<UserProfileData> {
        AppGlobal.printRequestAuth("userPro req", "No Req")
        val buyerData = MutableLiveData<UserProfileData>()
        val call = RetrofitClient.apiInterface.getUserProfile()

        call.enqueue(object : Callback<UserProfileData> {
            override fun onFailure(call: Call<UserProfileData>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<UserProfileData>,
                response: Response<UserProfileData>,
            ) {
                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    // Log.v("UserProf Resp : ", Gson().toJson(response.body()))
                    buyerData.value = data!!
                } else if (response.code() == 401) {
                    // Log.v("UserProf Resp : ", response.toString())
                    Constant.dismissLoader()
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    // Log.v("UserProf Resp : ", response.toString())
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