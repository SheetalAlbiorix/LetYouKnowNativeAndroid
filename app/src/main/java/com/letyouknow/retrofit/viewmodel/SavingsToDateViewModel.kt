package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.SavingsToDateRepository

class SavingsToDateViewModel : ViewModel() {
    var liveData: MutableLiveData<Float>? = null

    fun savingsToDateCall(
        context: Context,
    ): LiveData<Float>? {
        liveData = SavingsToDateRepository.savingstodateApiCall(
            context
        )
        return liveData
    }
}