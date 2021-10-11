package com.letyouknow.utils

import androidx.databinding.BaseObservable
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class AppGlobalBindData : BaseObservable() {
    companion object {
        fun getFormattedAmount(amount: Float?): String? {
            return "$" + NumberFormat.getNumberInstance(Locale.US)
                .format(DecimalFormat("##.##").format(amount?.toDouble()))
        }
    }

}