package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.IsSoldRepository

class IsSoldViewModel : ViewModel() {
    var liveData: MutableLiveData<Boolean>? = null

    fun isSoldCall(
        context: Context,
        request: String
    ): LiveData<Boolean>? {
        liveData = IsSoldRepository.isSoldCall(
            context,
            request
        )
        return liveData
    }
}