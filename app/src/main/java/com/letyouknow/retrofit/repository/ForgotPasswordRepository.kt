package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.utils.AppGlobal
import com.pionymessenger.utils.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object ForgotPasswordRepository {

    fun getForgotPasswordApiCall(
        context: Context,
        request: HashMap<String, String>
    ): MutableLiveData<Void> {
        AppGlobal.printRequestAuth("ForgotPass req", Gson().toJson(request))
        val getForgotPasswordData = MutableLiveData<Void>()
        val call = RetrofitClient.apiInterface.forgotPassword(request)

        call.enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Constant.dismissLoader()
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>,
            ) {

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Log.v("forgot Resp ", Gson().toJson(response.body()))
                    Constant.dismissLoader()
                    /* Toast.makeText(
                         context,
                         "You will Receive an email with resetting instruction shortly. For security reasons, this link will only remain active for the next hour",
                         Toast.LENGTH_SHORT
                     ).show()*/
                    AppGlobal.alertError(
                        context,
                        "You will Receive an email with resetting instruction shortly. For security reasons, this link will only remain active for the next hour"
                    )
                    /* Handler().postDelayed({
                         context.startActivity(
                             context.intentFor<LoginActivity>().clearTask().newTask()
                         )
                     }, 3000)*/
//                    forgotPasswordVo.value = ""
                } else {
                    Log.v("forgot Resp ", response.toString())
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    AppGlobal.alertError(
                        context,
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    )
                }
            }
        })
        return getForgotPasswordData
    }
}