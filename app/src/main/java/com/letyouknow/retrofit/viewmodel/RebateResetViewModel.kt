package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.CalculateTaxData
import com.letyouknow.retrofit.repository.RebateResetRepository

class RebateResetViewModel : ViewModel() {
    var data: MutableLiveData<CalculateTaxData>? = null

    fun rebateResetApi(
        context: Activity,
        request: HashMap<String, Any>
    ): LiveData<CalculateTaxData>? {
        data = RebateResetRepository.rebateResetApiCall(context, request)
        return data
    }
}