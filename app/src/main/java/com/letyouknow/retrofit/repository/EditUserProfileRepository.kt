package com.letyouknow.retrofit.repository

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
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
        AppGlobal.printRequestAuth("editUser req", Gson().toJson(request))
        val editUserProfileData = MutableLiveData<EditUserProfileData>()
        val call = RetrofitClient.apiInterface.editUserProfile(AppGlobal.getUserID(), request)

        call.enqueue(object : Callback<EditUserProfileData> {
            override fun onFailure(call: Call<EditUserProfileData>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<EditUserProfileData>,
                response: Response<EditUserProfileData>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Log.v("editUser Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    editUserProfileData.value = data!!
                } else if (response.code() == 401) {
                    Log.v("editUser Resp ", response.toString())
                    Constant.dismissLoader()
                    AppGlobal.isAuthorizationFailed(context)
                } else if (response.code() == 400) {
                    Log.v("editUser Resp ", response.toString())
                    Constant.dismissLoader()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null)
                        AppGlobal.alertError(
                            context,
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                        )
                } else {
                    Log.v("editUser Resp ", response.toString())
                    Constant.dismissLoader()
                    if (response.errorBody()?.source()?.buffer?.snapshot()?.utf8() != null) {
                        val msgList = Gson().fromJson(
                            response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                            EditUserProfileData::class.java
                        )
                        if (!msgList.DuplicateEmail.isNullOrEmpty()) {
                            var isFirst = true
                            var msg = ""
                            for (i in 0 until msgList.DuplicateEmail.size) {
                                if (isFirst) {
                                    msg = msgList.DuplicateEmail[i]
                                    isFirst = false
                                } else {
                                    msg = msg + "\n" + msgList.DuplicateEmail[i]
                                }
                            }
                            if (!TextUtils.isEmpty(msg))
                                AppGlobal.alertError(
                                    context,
                                    msg
                                )
                        } else {
                            AppGlobal.alertError(
                                context,
                                response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                            )
                        }
                    }
                }
            }
        })
        return editUserProfileData
    }
}