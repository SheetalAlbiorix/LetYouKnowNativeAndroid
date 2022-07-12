package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.VehicleModelData
import com.letyouknow.retrofit.repository.VehicleModelRepository

class VehicleModelViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<VehicleModelData>>? = null

    fun getModel(
        context: Context,
        productId: String?,
        yearId: String?,
        makeId: String?,
        zipCode: String?,
        type: Int? = 1,
        lowPrice: String? = "",
        highPrice: String? = ""
    ): LiveData<ArrayList<VehicleModelData>>? {
        liveData = VehicleModelRepository.getVehicleModelApiCall(
            context,
            productId,
            yearId,
            makeId,
            zipCode,
            type,
            lowPrice,
            highPrice
        )
        return liveData
    }
}