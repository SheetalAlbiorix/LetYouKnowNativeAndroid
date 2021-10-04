package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.ImageUrlRepository

class ImageUrlViewModel : ViewModel() {
    var liveData: MutableLiveData<ArrayList<String>>? = null

    fun imageUrlCall(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<ArrayList<String>>? {
        liveData = ImageUrlRepository.imageUrlApiCall(
            context,
            request
        )
        return liveData
    }
}