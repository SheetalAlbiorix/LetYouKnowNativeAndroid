package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.TransactionHistoryData
import com.letyouknow.retrofit.repository.TransactionHistoryRepository

class TransactionHistoryViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<TransactionHistoryData>>? = null

    fun transactionHistoryApiCall(
        context: Context
    ): LiveData<ArrayList<TransactionHistoryData>>? {
        liveData = TransactionHistoryRepository.transactionHistoryApiCall(
            context
        )
        return liveData
    }
}