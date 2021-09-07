package com.letyouknow.base

interface BaseViewModel {
    fun onError(message: String?)

    fun registerBus()

    fun unRegisterBus()
}