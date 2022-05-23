package com.letyouknow.view.account.viewDollar

import android.text.Html
import android.view.View
import com.letyouknow.R
import com.letyouknow.model.ReferralProgramDTO
import com.logispeed.ui.base.BaseAdapter
import kotlinx.android.synthetic.main.list_item_dollar_activity.view.*
import java.text.NumberFormat
import java.util.*

class ViewActivityAdapter(layout: Int, val clickListener: View.OnClickListener) :
    BaseAdapter<ReferralProgramDTO>(layout), BaseAdapter.OnBind<ReferralProgramDTO> {

    init {
        setOnBinding(this)
    }

    override fun onBind(view: View, position: Int, data: ReferralProgramDTO) {
        view.run {
            llActivity.tag = position
            llActivity.setOnClickListener(clickListener)
            if (data.type == "Credit")
                tvActivity.text = Html.fromHtml(
                    context.resources.getString(
                        R.string.activity_credit,
                        NumberFormat.getCurrencyInstance(Locale.US).format(data.value),
                        if (data.referralProgramType == "Check25") "Mailed" else "Credited",
                        data.timeStampFormatted,
                        if (data.userIdentifier == "System LetYouKnow") "LetYouKnow" else data.userIdentifier,
                        if (data.referralProgramType == "LYK50Dollars") "Promotion" else "Referral"
                    )
                )
            else
                tvActivity.text = Html.fromHtml(
                    context.resources.getString(
                        R.string.activity_debit,
                        NumberFormat.getCurrencyInstance(Locale.US).format(data.value),
                        data.timeStampFormatted,
                        data.transactionCode
                    )
                )
        }

    }
}


