package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.UserProfileData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object UserProfileRepository {

    fun userProfileApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): MutableLiveData<UserProfileData> {
        val buyerData = MutableLiveData<UserProfileData>()
        val call = RetrofitClient.apiInterface.getUserProfile()

        call.enqueue(object : Callback<UserProfileData> {
            override fun onFailure(call: Call<UserProfileData>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<UserProfileData>,
                response: Response<UserProfileData>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    buyerData.value = data!!
                } else if (response.code() == 401) {
                    AppGlobal.isAuthorizationFailed(context)
                } else {
                    Constant.dismissLoader()
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
        return buyerData
    }
}