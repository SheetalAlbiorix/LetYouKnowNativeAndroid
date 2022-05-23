package com.letyouknow.view.ucd

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.letyouknow.R
import com.letyouknow.model.FindUcdDealData
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import kotlinx.android.synthetic.main.list_item_unlocked_car.view.*
import kotlinx.android.synthetic.main.progress_loading.view.*
import java.text.NumberFormat
import java.util.*

class Items_LinearRVAdapter(
    private var itemsCells: ArrayList<FindUcdDealData?>,
    var clickListener: View.OnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var mcontext: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun addData(dataViews: ArrayList<FindUcdDealData?>) {
        this.itemsCells.addAll(dataViews)
        notifyDataSetChanged()
    }

    fun addLoadingView() {
        //add loading item
        Handler().post {
            itemsCells.add(null)
            notifyItemInserted(itemsCells.size - 1)
        }
    }

    fun removeLoadingView() {
        //Remove loading item
        if (itemsCells.size != 0) {
            itemsCells.removeAt(itemsCells.size - 1)
            notifyItemRemoved(itemsCells.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        mcontext = parent.context
        return if (viewType == Constant.VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_unlocked_car, parent, false)
            ItemViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(mcontext).inflate(R.layout.progress_loading, parent, false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                view.progressbar.indeterminateDrawable.colorFilter =
                    BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP)
            } else {
                view.progressbar.indeterminateDrawable.setColorFilter(
                    Color.WHITE,
                    PorterDuff.Mode.MULTIPLY
                )
            }
            LoadingViewHolder(view)
        }
    }

    fun getItem(pos: Int): FindUcdDealData {
        return itemsCells[pos]!!
    }

    override fun getItemCount(): Int {
        return itemsCells.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemsCells[position] == null) {
            Constant.VIEW_TYPE_LOADING
        } else {
            Constant.VIEW_TYPE_ITEM
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == Constant.VIEW_TYPE_ITEM) {
            holder.itemView.tvSelectDeal.tag = position
            holder.itemView.tvSelectDeal.setOnClickListener(clickListener)

            holder.itemView.tvViewOptions.tag = position
            holder.itemView.tvViewOptions.setOnClickListener(clickListener)

            holder.itemView.tvTitle.text =
                itemsCells[position]!!.vehicleYear + " " + itemsCells[position]!!.vehicleMake + " " + itemsCells[position]!!.vehicleModel + " " + itemsCells[position]!!.vehicleTrim
            holder.itemView.tvExterior.text = itemsCells[position]!!.vehicleExteriorColor
            holder.itemView.tvExterior.text = itemsCells[position]!!.vehicleExteriorColor
            holder.itemView.tvInterior.text = itemsCells[position]!!.vehicleInteriorColor

            val currency: String =
                NumberFormat.getCurrencyInstance(Locale.US).format(itemsCells[position]!!.price)
            if (itemsCells[position]!!.msrp == 0.0f) {
                holder.itemView.tvMSRP.visibility = View.GONE
            }
            val msrpcurrency: String = NumberFormat.getCurrencyInstance(Locale.US)
                .format(itemsCells[position]!!.msrp) + " MSRP"
            holder.itemView.tvPrice.text = currency
            holder.itemView.tvMSRP.text = msrpcurrency
            if (AppGlobal.isNotEmpty(itemsCells[position]!!.miles) || AppGlobal.isNotEmpty(
                    itemsCells[position]!!.condition
                )
            ) {
                if (AppGlobal.isNotEmpty(itemsCells[position]!!.miles))
                    holder.itemView.tvDisclosure.text =
                        mcontext.getString(
                            R.string.miles_approximate_odometer_reading,
                            itemsCells[position]!!.miles
                        )
                if (AppGlobal.isNotEmpty(itemsCells[position]!!.condition)) {
                    if (TextUtils.isEmpty(itemsCells[position]!!.miles)) {
                        holder.itemView.tvDisclosure.text = itemsCells[position]!!.condition
                    } else {
                        holder.itemView.tvDisclosure.text =
                            holder.itemView.tvDisclosure.text.toString()
                                .trim() + ", " + itemsCells[position]!!.condition
                    }
                }
                holder.itemView.llDisclosure.visibility = View.VISIBLE
            } else {
                holder.itemView.llDisclosure.visibility = View.GONE
            }
            AppGlobal.strikeThrough(holder.itemView.tvMSRP)
        }
    }


}