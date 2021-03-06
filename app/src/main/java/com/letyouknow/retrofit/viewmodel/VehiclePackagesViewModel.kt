package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.VehiclePackagesData
import com.letyouknow.retrofit.repository.VehiclePackagesRepository

class VehiclePackagesViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<VehiclePackagesData>>? = null

    fun getPackages(
        context: Context,
        productId: String?,
        yearId: String?,
        makeId: String?,
        modelId: String?,
        trimId: String?,
        exteriorColorId: String?,
        interiorColorId: String?,
        zipCode: String?
    ): LiveData<ArrayList<VehiclePackagesData>>? {
        liveData = VehiclePackagesRepository.getVehiclePackagesCall(
            context,
            productId,
            yearId,
            makeId,
            modelId,
            trimId,
            exteriorColorId,
            interiorColorId,
            zipCode
        )
        return liveData
    }
}