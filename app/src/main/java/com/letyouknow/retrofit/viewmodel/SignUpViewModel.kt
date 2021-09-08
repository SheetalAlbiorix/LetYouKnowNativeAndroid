package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.SignUpRepository
import com.pionymessenger.model.SignupData

class SignUpViewModel : ViewModel() {
    var signupLiveData: MutableLiveData<SignupData>? = null

    fun createAccount(
        context: Context,
        request: HashMap<String, String>
    ): LiveData<SignupData>? {
        signupLiveData = SignUpRepository.getSignUpApiCall(context, request)
        return signupLiveData
    }
}