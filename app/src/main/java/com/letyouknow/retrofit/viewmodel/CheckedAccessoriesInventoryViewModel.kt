package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.CheckedPackageData
import com.letyouknow.retrofit.repository.CheckedAccessoriesInventoryRepository

class CheckedAccessoriesInventoryViewModel : ViewModel() {
    var liveData: MutableLiveData<CheckedPackageData>? = null

    fun checkedAccessories(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<CheckedPackageData>? {
        liveData = CheckedAccessoriesInventoryRepository.checkedAccessoriesInventoryApiCall(
            context,
            request
        )
        return liveData
    }
}