package com.letyouknow.view.ucd.ucdstep1

import android.app.Activity
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.letyouknow.R
import com.letyouknow.model.FindUCDMainData
import com.letyouknow.model.FindUcdDealData
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.ImageIdGroupViewModel
import com.letyouknow.retrofit.viewmodel.ImageUrlViewModel
import com.letyouknow.utils.AppGlobal
import com.letyouknow.utils.Constant
import com.letyouknow.view.ucd.UCDStep1InnerAdapter
import kotlinx.android.synthetic.main.list_item_ucd_step1.view.*
import kotlinx.android.synthetic.main.progress_loading.view.*

class UCDStep1ListAdapter(
    private var itemsCells: ArrayList<FindUCDMainData?>,
    var clickListener: View.OnClickListener,
    var isShowPriceBid: Boolean?,
    var zipCode: String?,
    var radius: String?,
    var activity: Activity?,
    var lifeCycleOwner: LifecycleOwner?,
    var viewModelStoreOwner: ViewModelStoreOwner,
    var extColorID: String?,
    var extColorStr: String?,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var mcontext: Context

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun addData(dataViews: ArrayList<FindUCDMainData?>) {
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
                .inflate(R.layout.list_item_ucd_step1, parent, false)
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

    fun updateItem(pos: Int, data: FindUCDMainData) {
        itemsCells[pos] = data
        notifyItemChanged(pos)
    }

    fun getItem(pos: Int): FindUCDMainData {
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
            holder.itemView.run {
                tvZipCode.text = zipCode
                tvRadius.text = radius
//                if (TextUtils.isEmpty(itemsCells[position]?.imgUrl))
                callImageIdAPI(position, itemsCells[position]?.arUCD!!, ivCar)


                val adapterUCD =
                    UCDStep1InnerAdapter(
                        itemsCells[position]?.arUCD!!,
                        clickListener,
                        true,
                        itemsCells[position]?.imageID,
                        zipCode,
                        radius,
                        lifeCycleOwner,
                        activity,
                        viewModelStoreOwner
                    )
                adapterUCD.notifyDataSetChanged()
                rvUnlockedCar.adapter = adapterUCD
            }
        }
    }


    private fun callImageIdAPI(pos: Int, findData: ArrayList<FindUcdDealData?>, ivCar: ImageView) {
        var imageId = ""
        val imageIdViewModel: ImageIdGroupViewModel =
            ViewModelProvider(viewModelStoreOwner)[ImageIdGroupViewModel::class.java]
        if (Constant.isOnline(mcontext)) {
            /*  if (!Constant.isInitProgress()) {
                  Constant.showLoader(this)
              } else if (Constant.isInitProgress() && !Constant.progress.isShowing) {
                  Constant.showLoader(this)
              }*/
            val request = HashMap<String, Any>()
            findData[0]?.run {
                request[ApiConstant.vehicleYearID] = yearId!!
                request[ApiConstant.vehicleMakeID] = makeId!!
                request[ApiConstant.vehicleModelID] = modelId!!
                request[ApiConstant.vehicleTrimID] = trimId!!
            }
            Log.e("ImageId Request", Gson().toJson(request))
            imageIdViewModel.imageIdCall(activity!!, request)!!
                .observe(lifeCycleOwner!!, androidx.lifecycle.Observer { data ->
//                    Constant.dismissLoader()
                    imageId = data
                    callImageUrlAPI(pos, data, findData, ivCar)

                }
                )

        } else {
            Toast.makeText(mcontext, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun callImageUrlAPI(
        pos: Int,
        imageId: String,
        findData: ArrayList<FindUcdDealData?>,
        ivCar: ImageView
    ) {
        var imgUrl = ""
        val imageUrlViewModel: ImageUrlViewModel =
            ViewModelProvider(viewModelStoreOwner)[ImageUrlViewModel::class.java]
        if (Constant.isOnline(mcontext)) {

            val request = HashMap<String, Any>()

            request[ApiConstant.ImageId] = imageId
            if (extColorID == "0") {
                request[ApiConstant.ImageProduct] = "Splash"
            } else {
                request[ApiConstant.ImageProduct] = "MultiAngle"
            }
            request[ApiConstant.ExteriorColor] = extColorStr!!
            imageUrlViewModel.imageUrlCall(activity!!, request)!!
                .observe(lifeCycleOwner!!, androidx.lifecycle.Observer { data ->
                    if (data.isNotEmpty()) {
                        imgUrl = data[0]
                    }
                    val mainData = FindUCDMainData()
                    mainData.position = pos
                    mainData.imageID = imageId
                    mainData.imgUrl = imgUrl
                    mainData.arUCD = findData
                    itemsCells[pos] = mainData
//                    updateItem(pos,mainData)
                    AppGlobal.loadImageUrl(mcontext, ivCar, itemsCells[pos]?.imgUrl!!)
                }
                )
        } else {
            Toast.makeText(mcontext, Constant.noInternet, Toast.LENGTH_SHORT).show()
        }

    }
}

