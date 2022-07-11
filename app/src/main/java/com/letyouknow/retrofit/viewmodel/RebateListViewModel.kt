package com.letyouknow.retrofit.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.RebateListData
import com.letyouknow.retrofit.repository.RebateListRepository

class RebateListViewModel : ViewModel() {
    var data: MutableLiveData<ArrayList<RebateListData>>? = null

    fun rebateListApi(
        context: Activity,
        request: HashMap<String, Any>
    ): LiveData<ArrayList<RebateListData>>? {
        data = RebateListRepository.rebateListApiCall(context, request)
        return data
    }
}