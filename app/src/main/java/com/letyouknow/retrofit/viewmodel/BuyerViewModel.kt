package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.BuyerInfoData
import com.letyouknow.retrofit.repository.BuyerRepository

class BuyerViewModel : ViewModel() {
    var liveData: MutableLiveData<BuyerInfoData>? = null

    fun buyerCall(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<BuyerInfoData>? {
        liveData = BuyerRepository.buyerApiCall(
            context,
            request
        )
        return liveData
    }
}