package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.RebateCheckedData
import com.letyouknow.retrofit.repository.RebateCheckedRepository

class RebateCheckedViewModel : ViewModel() {
    var data: MutableLiveData<RebateCheckedData>? = null

    fun rebateCheckApi(
        context: Activity,
        request: HashMap<String, Any>
    ): LiveData<RebateCheckedData>? {
        data = RebateCheckedRepository.rebateCheckApiCall(context, request)
        return data
    }
}