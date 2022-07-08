package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.CalculateTaxData
import com.letyouknow.retrofit.repository.RebateRepository

class RebateViewModel : ViewModel() {
    var data: MutableLiveData<CalculateTaxData>? = null

    fun rebateApi(
        context: Activity,
        request: HashMap<String, Any>
    ): LiveData<CalculateTaxData>? {
        data = RebateRepository.rebateApiCall(context, request)
        return data
    }
}