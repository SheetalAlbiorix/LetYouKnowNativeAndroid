package com.letyouknow.view.unlockedcardeal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.letyouknow.R
import com.letyouknow.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_unlocked_car_deal.*

class UnlockedCarDealFragment : BaseFragment(), View.OnClickListener {
    private lateinit var adapterUnlockedCarDeal: UnlockedCarDealAdapter
    private var arUnlocked =
        arrayListOf(false, false, false, false, false, false, false, false, false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_unlocked_car_deal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        adapterUnlockedCarDeal = UnlockedCarDealAdapter(R.layout.list_item_unlocked_car, this)
        rvUnlockedCar.adapter = adapterUnlockedCarDeal
        adapterUnlockedCarDeal.addAll(arUnlocked)
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
}