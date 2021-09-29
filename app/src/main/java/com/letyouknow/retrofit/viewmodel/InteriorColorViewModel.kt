package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.InteriorColorData
import com.letyouknow.retrofit.repository.InteriorColorRepository

class InteriorColorViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<InteriorColorData>>? = null

    fun getInteriorColor(
        context: Context,
        productId: String?,
        yearId: String?,
        makeId: String?,
        modelId: String?,
        trimId: String?,
        exteriorColorId: String?,
        zipCode: String?
    ): LiveData<ArrayList<InteriorColorData>>? {
        liveData = InteriorColorRepository.getInteriorColorCall(
            context,
            productId,
            yearId,
            makeId,
            modelId,
            trimId,
            exteriorColorId,
            zipCode
        )
        return liveData
    }
}