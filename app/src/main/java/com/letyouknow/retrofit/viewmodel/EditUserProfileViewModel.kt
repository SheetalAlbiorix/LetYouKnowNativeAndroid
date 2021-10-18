package com.letyouknow.retrofit.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.letyouknow.model.EditUserProfileData
import com.letyouknow.retrofit.repository.EditUserProfileRepository

class EditUserProfileViewModel : ViewModel() {
    var liveData: MutableLiveData<EditUserProfileData>? = null

    fun editUserCall(
        context: Context,
        request: HashMap<String, String>
    ): LiveData<EditUserProfileData>? {
        liveData = EditUserProfileRepository.editUserProfileApiCall(
            context,
            request
        )
        return liveData
    }
}