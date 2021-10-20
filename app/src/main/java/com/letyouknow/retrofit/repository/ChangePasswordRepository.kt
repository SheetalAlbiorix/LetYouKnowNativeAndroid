package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.ChangePasswordRequestData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ChangePasswordRepository {

    fun changePasswordApiCall(
        context: Context,
        request: ChangePasswordRequestData
    ): MutableLiveData<String> {
        val editUserProfileData = MutableLiveData<String>()
        val call = RetrofitClient.apiInterface.changePassword(request)

        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<String>,
                response: Response<String>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                Constant.dismissLoader()
                if (response.code() == 200 || response.code() == 201) {
                    editUserProfileData.value = data!!
                } else if (response.code() == 401) {
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                        Toast.makeText(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                            Toast.LENGTH_LONG
                        ).show()
                }
            }
        })
        return editUserProfileData
    }
}