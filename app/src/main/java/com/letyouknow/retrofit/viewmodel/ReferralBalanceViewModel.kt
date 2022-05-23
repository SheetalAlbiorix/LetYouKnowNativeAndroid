package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.ReferralBalanceRepository

class ReferralBalanceViewModel : ViewModel() {
    var liveData: MutableLiveData<Double>? = null

    fun referralBalCall(
        context: Context
    ): LiveData<Double>? {
        liveData = ReferralBalanceRepository.currentBalanceCall(
            context
        )
        return liveData
    }
}