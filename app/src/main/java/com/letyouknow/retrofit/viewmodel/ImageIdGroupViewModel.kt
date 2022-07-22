package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.ImageIdGroupRepository

class ImageIdGroupViewModel : ViewModel() {
    var liveData: MutableLiveData<String>? = null

    fun imageIdCall(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<String>? {
        liveData = ImageIdGroupRepository.imageIdApiCall(
            context,
            request
        )
        return liveData
    }
}