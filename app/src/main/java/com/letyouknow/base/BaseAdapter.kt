package com.logispeed.ui.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letyouknow.LetYouKnowApp
import com.logispeed.data.prefs.AppPreferencesHelper

abstract class BaseAdapter<T>(val layout: Int = 0) :
    RecyclerView.Adapter<BaseAdapter.ViewHolder>() {
    val TAG = javaClass.canonicalName
    val list = ArrayList<T>()
    private var mOnBind: OnBind<T>? = null
    var pref: AppPreferencesHelper? = LetYouKnowApp.getInstance()!!.getAppPreferencesHelper()

    fun setOnBinding(mOnBind: OnBind<T>) {
        this.mOnBind = mOnBind
    }

    interface OnBind<in T> {
        fun onBind(view: View, position: Int, item: T)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ViewHolder(v, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payload: MutableList<Any>) {
        if (payload.isEmpty()) {
            return super.onBindViewHolder(holder, position, payload)
        } else {
            mOnBind?.onBind(holder.getBindView(), position, list[position])
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mOnBind?.onBind(holder.getBindView(), position, list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val view: View, val context: Context) : RecyclerView.ViewHolder(view) {
        fun getBindView(): View {
            return view
        }
    }

    fun addAll(dataList: ArrayList<T>) {

        list.clear()
        list.addAll(dataList)
        notifyDataSetChanged()
    }

    fun getAll(): ArrayList<T> {
        return list
    }

    fun getItem(position: Int): T {
        return list[position]
    }

    fun update(position: Int, model: T) {
        if (list != null) {
            list[position] = model
            notifyDataSetChanged()
        }
    }

    fun notifyData() {
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        list.removeAt(position)
        notifyDataSetChanged()
    }

    fun removeItem(item: T/*, position: Int*/) {
        list.remove(item)
//        notifyItemChanged(position)
        notifyDataSetChanged()
    }

    fun removeAll() {
        list.clear()
        notifyDataSetChanged()
    }

}
