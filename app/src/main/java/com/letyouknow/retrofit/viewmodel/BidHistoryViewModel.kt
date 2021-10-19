package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.BidPriceData
import com.letyouknow.retrofit.repository.BidHistoryRepository

class BidHistoryViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<BidPriceData>>? = null

    fun bidHistoryApiCall(
        context: Context
    ): LiveData<ArrayList<BidPriceData>>? {
        liveData = BidHistoryRepository.bidHistoryApiCall(
            context
        )
        return liveData
    }
}