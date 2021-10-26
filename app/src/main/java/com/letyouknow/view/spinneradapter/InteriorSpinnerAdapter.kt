package com.letyouknow.view.spinneradapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.LinearLayoutCompat
import com.letyouknow.R
import com.letyouknow.model.InteriorColorData
import kotlinx.android.synthetic.main.list_item_spinner.view.*

class InteriorSpinnerAdapter(val context: Context, var arList: ArrayList<InteriorColorData>) :
    BaseAdapter() {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = inflater.inflate(R.layout.list_item_spinner, null) as LinearLayoutCompat
        view.tvTitle.text = arList[position].interiorColor?.toUpperCase()

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = inflater.inflate(R.layout.list_item_spinner, null) as LinearLayoutCompat
        if (position == 0) {
            v.llSpinner.visibility = View.GONE
        }
        v.tvTitle.text = arList[position].interiorColor
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
