package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.VehicleAccessoriesData
import com.letyouknow.retrofit.repository.VehicleOptionalAccessoriesRepository

class VehicleOptionalViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<VehicleAccessoriesData>>? = null

    fun getOptional(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<ArrayList<VehicleAccessoriesData>>? {
        liveData = VehicleOptionalAccessoriesRepository.getOptionalCall(
            context,
            request
        )
        return liveData
    }
}