package com.letyouknow.retrofit.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.letyouknow.retrofit.RetrofitClient
import com.pionymessenger.model.SignupData
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
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show()
                }
            }
        })
        return signupVo
    }
}