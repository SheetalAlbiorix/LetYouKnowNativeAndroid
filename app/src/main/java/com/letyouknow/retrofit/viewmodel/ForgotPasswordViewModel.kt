package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.ForgotPasswordRepository

class ForgotPasswordViewModel : ViewModel() {
    var forgotPasswordLiveData: MutableLiveData<Void>? = null

    fun getForgotPassword(
        context: Context,
        request: HashMap<String, String>
    ): LiveData<Void>? {
        forgotPasswordLiveData = ForgotPasswordRepository.getForgotPasswordApiCall(context, request)
        return forgotPasswordLiveData
    }
}