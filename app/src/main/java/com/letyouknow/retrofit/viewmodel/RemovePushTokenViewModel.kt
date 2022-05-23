package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.DevicePushTokenData
import com.letyouknow.retrofit.repository.RemoveDevicePushTokenRepository

class RemovePushTokenViewModel : ViewModel() {
    var data: MutableLiveData<DevicePushTokenData>? = null

    fun pushToken(
        context: Activity,
        request: String?
    ): LiveData<DevicePushTokenData>? {
        data = RemoveDevicePushTokenRepository.pushTokenApiCall(context, request)
        return data
    }
}