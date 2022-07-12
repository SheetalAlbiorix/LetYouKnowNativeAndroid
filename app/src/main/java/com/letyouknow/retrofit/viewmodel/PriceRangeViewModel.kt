package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.PriceRangeData
import com.letyouknow.retrofit.repository.PriceRangeRepository

class PriceRangeViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<PriceRangeData>>? = null

    fun getPriceRange(
        context: Context
    ): LiveData<ArrayList<PriceRangeData>>? {
        liveData = PriceRangeRepository.getPriceRangeApiCall(context)
        return liveData
    }
}