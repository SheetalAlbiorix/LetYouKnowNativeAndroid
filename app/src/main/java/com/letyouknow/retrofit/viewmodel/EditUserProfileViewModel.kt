package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.EditUserProfileData
import com.letyouknow.retrofit.repository.EditUserProfileRepository

class EditUserProfileViewModel : ViewModel() {
    var liveData: MutableLiveData<EditUserProfileData>? = null

    fun buyerCall(
        context: Context,
        request: HashMap<String, Any>
    ): LiveData<EditUserProfileData>? {
        liveData = EditUserProfileRepository.editUserProfileApiCall(
            context,
            request
        )
        return liveData
    }
}