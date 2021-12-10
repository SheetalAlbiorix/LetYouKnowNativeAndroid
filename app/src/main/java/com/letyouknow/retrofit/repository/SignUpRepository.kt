package com.letyouknow.retrofit.repository

import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.model.SignupData
import com.pionymessenger.model.SignupDataError
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object SignUpRepository {
    fun getSignUpApiCall(
        context: Context,
        request: HashMap<String, String>
    ): MutableLiveData<SignupData> {
        val signupVo = MutableLiveData<SignupData>()
        val call = RetrofitClient.apiInterface.signUp(request)

        call.enqueue(object : Callback<SignupData> {
            override fun onFailure(call: Call<SignupData>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<SignupData>,
                response: Response<SignupData>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    signupVo.value = data!!
                } else {
                    Constant.dismissLoader()
                    val data = response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    var signupVoError = SignupDataError()
                    signupVoError = Gson().fromJson(data, SignupDataError::class.java)

                    var email = ""
                    var userName = ""
                    var errorMSG = ""
                    if (signupVoError.DuplicateEmail?.size != 0) {
                        email = signupVoError?.DuplicateEmail!![0]
                    }
                    if (signupVoError.DuplicateUserName?.size != 0) {
                        userName = signupVoError?.DuplicateUserName!![0]
                    }
                    errorMSG = email + "\n" + userName

                    if (!TextUtils.isEmpty(errorMSG))
                        AppGlobal.alertError(
                            context,
                            errorMSG
                        )
                }
            }
        })
        return signupVo
    }
}