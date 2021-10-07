package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.LYKDollarRepository

class LYKDollarViewModel : ViewModel() {
    var liveData: MutableLiveData<String>? = null

    fun getDollar(
        context: Context,
        dealerId: String?
    ): LiveData<String>? {
        liveData = LYKDollarRepository.getDollarApiCall(context, dealerId)
        return liveData
    }
}