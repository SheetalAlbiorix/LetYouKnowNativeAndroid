package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.retrofit.repository.NotificationOptionUpdateRepository

class NotificationOptionsUpdateViewModel : ViewModel() {
    var liveData: MutableLiveData<Boolean>? = null

    fun notificationOptionUpdateApiCall(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<Boolean>? {
        liveData = NotificationOptionUpdateRepository.notificationOptionUpdateApiCall(
            context,
            request
        )
        return liveData
    }
}