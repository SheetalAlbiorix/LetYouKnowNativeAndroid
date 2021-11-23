package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.CheckVehicleStockRepository

class CheckVehicleStockViewModel : ViewModel() {
    var liveData: MutableLiveData<Boolean>? = null

    fun checkVehicleStockCall(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<Boolean>? {
        liveData = CheckVehicleStockRepository.checkVehicleStockCall(
            context,
            request
        )
        return liveData
    }
}