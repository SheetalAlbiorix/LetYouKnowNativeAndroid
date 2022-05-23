package com.letyouknow.retrofit.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.model.ChangePasswordRequestData
import com.letyouknow.model.SubmitPriceErrorData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ChangePasswordRepository {

    fun changePasswordApiCall(
        context: Context,
        request: ChangePasswordRequestData
    ): MutableLiveData<String> {
        AppGlobal.printRequestAuth("changePass req", Gson().toJson(request))
        val editUserProfileData = MutableLiveData<String>()
        val call = RetrofitClient.apiInterface.changePassword(request)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Constant.dismissLoader()
                // Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>,
            ) {

                Constant.dismissLoader()
                if (response.code() == 200 || response.code() == 201) {
                    // Log.v("changePass Resp ", response.toString())
                    editUserProfileData.value = response.body()!!.string()
                } else if (response.code() == 401) {
                    // Log.v("changePass Resp ", response.toString())
                    AppGlobal.isAuthorizationFailed(context)
                } else if (response.code() == 400) {
                    val dataError = Gson().fromJson(
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                        SubmitPriceErrorData::class.java
                    )
                    AppGlobal.alertError(
                        context,
                        "Change password only for email login"
                    )
                } else {
                    //  Log.v("changePass Resp ", response.toString())
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                        AppGlobal.alertError(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                        )
                }
            }
        })
        return editUserProfileData
    }
}