package com.letyouknow.view.savedsearches

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivitySavedSearchesBinding
import kotlinx.android.synthetic.main.activity_saved_searches.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar

class SavedSearchesActivity : BaseActivity(), View.OnClickListener {
    private var arSaved = arrayListOf(false, false, false, false, false, false, false, false)
    private lateinit var adapterSaved: SavedSearchAdapter
    private lateinit var binding: ActivitySavedSearchesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_searches)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_saved_searches)
        init()
    }

    private fun init() {
        backButton()
        adapterSaved = SavedSearchAdapter(R.layout.list_item_saved_search, this)
        rvSavedSearch.adapter = adapterSaved
        adapterSaved.addAll(arSaved)
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
    }

    private var selectPos = -1

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cardSaved -> {
                val pos = v.tag as Int
                if (selectPos != -1) {
                    var data = adapterSaved.getItem(selectPos)
                    data = false
                    adapterSaved.update(selectPos, data)
                }

                var data = adapterSaved.getItem(pos)
                data = true
                adapterSaved.update(pos, data)
                selectPos = pos
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}