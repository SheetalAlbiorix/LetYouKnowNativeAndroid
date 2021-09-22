package com.letyouknow.retrofit.repository

import android.content.Context
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.letyouknow.retrofit.RetrofitClient
import com.letyouknow.view.login.LoginActivity
import com.pionymessenger.utils.Constant
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object ForgotPasswordRepository {

    fun getForgotPasswordApiCall(
        context: Context,
        request: HashMap<String, String>
    ): MutableLiveData<Void> {
        val forgotPasswordVo = MutableLiveData<Void>()
        val call = RetrofitClient.apiInterface.forgotPassword(request)

        call.enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.v("DEBUG : ", t.message.toString())
            }

            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>,
            ) {
                Log.v("DEBUG : ", response.body().toString())

                val data = response.body()
                if (response.code() == 200 || response.code() == 201) {
                    Constant.dismissLoader()
                    Toast.makeText(
                        context,
                        "You will Receive an email with resetting instruction shortly. For security reasons, this link will only remain active for the next hour",
                        Toast.LENGTH_SHORT
                    ).show()
                    Handler().postDelayed({
                        context.startActivity(
                            context.intentFor<LoginActivity>().clearTask().newTask()
                        )
                    }, 3000)
//                    forgotPasswordVo.value = ""
                } else {
                    Constant.dismissLoader()
                    response.errorBody()?.source()?.buffer?.snapshot()?.utf8()
                    Toast.makeText(
                        context,
                        response.errorBody()?.source()?.buffer?.snapshot()?.utf8(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        return forgotPasswordVo
    }
}