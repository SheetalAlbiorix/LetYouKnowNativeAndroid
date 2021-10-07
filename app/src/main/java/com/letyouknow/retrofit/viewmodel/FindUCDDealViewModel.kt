package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.FindUcdDealData
import com.letyouknow.retrofit.repository.FindUCDDealRepository

class FindUCDDealViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<FindUcdDealData>>? = null

    fun findDeal(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<ArrayList<FindUcdDealData>>? {
        liveData = FindUCDDealRepository.findUCDDealApiCall(
            context,
            request
        )
        return liveData
    }
}