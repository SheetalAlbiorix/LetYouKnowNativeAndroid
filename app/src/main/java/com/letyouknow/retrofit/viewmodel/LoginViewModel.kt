package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.LoginRepository
import com.pionymessenger.model.LoginData

class LoginViewModel : ViewModel() {
    var loginLiveData: MutableLiveData<LoginData>? = null

    fun getUser(
        context: Context,
        request: HashMap<String, String>
    ): LiveData<LoginData>? {
        loginLiveData = LoginRepository.getLoginApiCall(context, request)
        return loginLiveData
    }
}