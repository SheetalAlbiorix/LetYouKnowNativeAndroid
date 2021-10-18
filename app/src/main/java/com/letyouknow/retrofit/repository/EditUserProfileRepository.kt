package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.letyouknow.model.EditUserProfileData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object EditUserProfileRepository {

    fun editUserProfileApiCall(
        context: Context,
        request: HashMap<String, String>
    ): MutableLiveData<EditUserProfileData> {
        val editUserProfileData = MutableLiveData<EditUserProfileData>()
        val call = RetrofitClient.apiInterface.editUserProfile(AppGlobal.getUserID(), request)

        call.enqueue(object : Callback<EditUserProfileData> {
            override fun onFailure(call: Call<EditUserProfileData>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<EditUserProfileData>,
                response: Response<EditUserProfileData>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    editUserProfileData.value = data!!
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
        return editUserProfileData
    }
}