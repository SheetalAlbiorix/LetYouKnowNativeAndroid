package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.LoginData
import com.letyouknow.retrofit.repository.FacebookLoginRepository

class FacebookLoginViewModel : ViewModel() {
    var loginLiveData: MutableLiveData<LoginData>? = null

    fun getLogin(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<LoginData>? {
        loginLiveData = FacebookLoginRepository.getLoginApiCall(context, request)
        return loginLiveData
    }
}