package com.letyouknow.view.unlockedcardeal

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityUnlockedCarDealBinding
import kotlinx.android.synthetic.main.activity_unlocked_car_deal.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar

class UnlockedCarDealActivity : BaseActivity(), View.OnClickListener {
    private lateinit var adapterUnlockedCarDeal: UnlockedCarDealAdapter
    private var arUnlocked =
        arrayListOf(false, false, false, false, false, false, false, false, false)

    private lateinit var binding: ActivityUnlockedCarDealBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlocked_car_deal)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_unlocked_car_deal)
        init()
    }

    private fun init() {
        backButton()
        adapterUnlockedCarDeal = UnlockedCarDealAdapter(R.layout.list_item_unlocked_car, this)
        rvUnlockedCar.adapter = adapterUnlockedCarDeal
        adapterUnlockedCarDeal.addAll(arUnlocked)
    }

    private fun backButton() {
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setTitleTextColor(resources.getColor(R.color.black))

        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }


    override fun getViewActivity(): Activity? {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
        TODO("Not yet implemented")
    }

    private var selectPos = -1
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cardUnlocked -> {
                val pos = v.tag as Int
                if (selectPos != -1) {
                    var data = adapterUnlockedCarDeal.getItem(selectPos)
                    data = false
                    adapterUnlockedCarDeal.update(selectPos, data)
                }

                var data = adapterUnlockedCarDeal.getItem(pos)
                data = true
                adapterUnlockedCarDeal.update(pos, data)
                selectPos = pos
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}