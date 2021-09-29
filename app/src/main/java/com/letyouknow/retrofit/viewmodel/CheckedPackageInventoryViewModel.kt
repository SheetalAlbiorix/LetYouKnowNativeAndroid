package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.CheckedPackageData
import com.letyouknow.retrofit.repository.CheckedPackageInventoryRepository

class CheckedPackageInventoryViewModel : ViewModel() {
    var liveData: MutableLiveData<CheckedPackageData>? = null

    fun checkedPackage(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<CheckedPackageData>? {
        liveData =
            CheckedPackageInventoryRepository.checkedPackageInventoryApiCall(context, request)
        return liveData
    }
}