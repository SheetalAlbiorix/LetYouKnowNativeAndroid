package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.FindLCDDealGuestData
import com.letyouknow.retrofit.repository.FindLCDDealGRepository

class FindLCDDealGuestViewModel : ViewModel() {
    var liveData: MutableLiveData<FindLCDDealGuestData>? = null

    fun findDeal(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<FindLCDDealGuestData>? {
        liveData = FindLCDDealGRepository.findLCDDealApiCall(
            context,
            request
        )
        return liveData
    }
}