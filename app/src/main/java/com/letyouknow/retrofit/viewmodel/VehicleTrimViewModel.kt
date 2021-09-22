package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.VehicleTrimData
import com.letyouknow.retrofit.repository.VehicleTrimRepository

class VehicleTrimViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<VehicleTrimData>>? = null

    fun getTrim(
        context: Context,
        productId: String?,
        yearId: String?,
        makeId: String?,
        modelId: String?
    ): LiveData<ArrayList<VehicleTrimData>>? {
        liveData =
            VehicleTrimRepository.getVehicleTrimApiCall(context, productId, yearId, makeId, modelId)
        return liveData
    }
}