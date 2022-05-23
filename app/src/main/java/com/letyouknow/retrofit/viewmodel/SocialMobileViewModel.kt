package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.LoginData
import com.letyouknow.retrofit.repository.SocialMobileRepository

class SocialMobileViewModel : ViewModel() {
    var socialLiveData: MutableLiveData<LoginData>? = null

    fun getSocialMobile(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<LoginData>? {
        socialLiveData = SocialMobileRepository.getSocialMobileApiCall(context, request)
        return socialLiveData
    }
}