package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.DevicePushTokenData
import com.letyouknow.retrofit.repository.AddDevicePushTokenRepository

class AddPushTokenViewModel : ViewModel() {
    var data: MutableLiveData<DevicePushTokenData>? = null

    fun pushToken(
        context: Activity,
        request: HashMap<String, Any>
    ): LiveData<DevicePushTokenData>? {
        data = AddDevicePushTokenRepository.pushTokenApiCall(context, request)
        return data
    }
}