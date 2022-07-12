package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.VehicleMakeData
import com.letyouknow.retrofit.repository.VehicleMakeRepository

class VehicleMakeViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<VehicleMakeData>>? = null

    fun getMake(
        context: Context,
        productId: String?,
        yearId: String?,
        zipCode: String?,
        type: Int? = 1,
        lowPrice: String? = "",
        highPrice: String? = ""
    ): LiveData<ArrayList<VehicleMakeData>>? {
        liveData = VehicleMakeRepository.getVehicleMakeApiCall(
            context, productId, yearId, zipCode,
            type,
            lowPrice,
            highPrice
        )
        return liveData
    }
}