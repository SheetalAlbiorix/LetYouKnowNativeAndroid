package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.MinMSRPRepository

class MinMSRPViewModel : ViewModel() {
    var liveData: MutableLiveData<Double>? = null

    fun minMSRPCall(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<Double>? {
        liveData = MinMSRPRepository.minMsrpApiCall(
            context,
            request
        )
        return liveData
    }
}