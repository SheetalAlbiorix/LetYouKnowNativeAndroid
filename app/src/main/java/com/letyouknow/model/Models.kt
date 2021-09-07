package com.pionymessenger.model

import java.io.Serializable

data class BaseResponse<T>(
    val status: Boolean? = false,
    val message: String? = "",
    val data: T,
) : Serializable

