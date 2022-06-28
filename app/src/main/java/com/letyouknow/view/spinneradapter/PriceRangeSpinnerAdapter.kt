package com.letyouknow.view.spinneradapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.LinearLayoutCompat
import com.letyouknow.R
import com.letyouknow.model.PriceRangeData
import kotlinx.android.synthetic.main.list_item_spinner.view.*
import java.text.NumberFormat
import java.util.*


class PriceRangeSpinnerAdapter(val context: Context, var arList: ArrayList<PriceRangeData>) :
    BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = inflater.inflate(R.layout.list_item_spinner, null) as LinearLayoutCompat
        var str = ""
        if (position == 0)
            str = "ANY PRICE"
        else {
            str = if (!TextUtils.isEmpty(arList[position].lowerBorder)) {
                NumberFormat.getCurrencyInstance(Locale.US)
                    .format(arList[position].lowerBorder?.toDouble()) + " - "
            } else {
                "Under "
            }

            str = if (!TextUtils.isEmpty(arList[position].upperBorder)) {
                str + NumberFormat.getCurrencyInstance(Locale.US)
                    .format(arList[position].upperBorder?.toDouble())
            } else {
                "Over " + str.replace("- ", "")
            }
        }

        view.tvTitle.text = str.toUpperCase()
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = inflater.inflate(R.layout.list_item_spinner, null) as LinearLayoutCompat
        /* if (position == 0) {
             v.llSpinner.visibility = View.GONE
 //            v.tvTitle.height=0
         }
 */
        var str = ""
        if (position == 0)
            str = "ANY PRICE"
        else {
            str = if (!TextUtils.isEmpty(arList[position].lowerBorder)) {
                NumberFormat.getCurrencyInstance(Locale.US)
                    .format(arList[position].lowerBorder?.toDouble()) + " - "
            } else {
                "Under "
            }

            str = if (!TextUtils.isEmpty(arList[position].upperBorder)) {
                str + NumberFormat.getCurrencyInstance(Locale.US)
                    .format(arList[position].upperBorder?.toDouble())
            } else {
                "Over " + str.replace(" - ", "")
            }
        }

        v.tvTitle.text = str

        return v!!
    }

    override fun getItem(position: Int): Any? {
        return arList[position]
    }

    override fun getCount(): Int {
        return arList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun removeItem(pos: Int?) {
        arList.removeAt(pos!!)
    }

}
