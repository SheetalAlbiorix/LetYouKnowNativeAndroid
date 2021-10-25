package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.TransactionCodeData
import com.letyouknow.retrofit.repository.TransactionCodeRepository

class TransactionCodeViewModel : ViewModel() {
    var liveData: MutableLiveData<TransactionCodeData>? = null
    fun transactionCodeApiCall(
        context: Context,
        code: String?
    ): LiveData<TransactionCodeData>? {
        liveData = TransactionCodeRepository.transactionCodeApiCall(
            context,
            code
        )
        return liveData
    }
}