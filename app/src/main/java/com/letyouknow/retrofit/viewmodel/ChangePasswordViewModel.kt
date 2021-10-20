package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.ChangePasswordRequestData
import com.letyouknow.retrofit.repository.ChangePasswordRepository

class ChangePasswordViewModel : ViewModel() {
    var liveData: MutableLiveData<String>? = null

    fun changePasswordCall(
        context: Context,
        request: ChangePasswordRequestData
    ): LiveData<String>? {
        liveData = ChangePasswordRepository.changePasswordApiCall(
            context,
            request
        )
        return liveData
    }
}