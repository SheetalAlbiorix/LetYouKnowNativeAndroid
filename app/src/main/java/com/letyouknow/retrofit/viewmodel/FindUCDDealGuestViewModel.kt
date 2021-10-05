package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.FindUcdDealGuestData
import com.letyouknow.retrofit.repository.FindUCDDealGuestRepository

class FindUCDDealGuestViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<FindUcdDealGuestData>>? = null

    fun findDeal(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<ArrayList<FindUcdDealGuestData>>? {
        liveData = FindUCDDealGuestRepository.findUCDDealApiCall(
            context,
            request
        )
        return liveData
    }
}