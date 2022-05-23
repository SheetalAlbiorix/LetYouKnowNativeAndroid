package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.MinMSRPRangeRepository

class MinMSRPRangeViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<String>>? = null

    fun minMSRPCall(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<ArrayList<String>>? {
        liveData = MinMSRPRangeRepository.minMsrpApiCall(
            context,
            request
        )
        return liveData
    }
}