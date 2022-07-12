package com.letyouknow.view.gallery360view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.databinding.ActivityGallery360TabBinding
import com.letyouknow.retrofit.ApiConstant
import com.letyouknow.retrofit.viewmodel.ExteriorViewModel
import com.letyouknow.retrofit.viewmodel.InteriorViewModel
import com.letyouknow.utils.Constant
import com.letyouknow.utils.Constant.Companion.ARG_IMAGE_ID
import com.letyouknow.utils.Constant.Companion.ARG_TYPE_VIEW
import com.letyouknow.view.gallery360view.gallery.ExteriorFragment
import com.letyouknow.view.gallery360view.gallery.InteriorFragment
import com.letyouknow.view.gallery360view.view360.View360Fragment
import kotlinx.android.synthetic.main.activity_gallery360_tab.*
import kotlinx.android.synthetic.main.layout_toolbar_blue.*

class Gallery360TabActivity : BaseActivity(), View.OnClickListener {
    private lateinit var gallery360ViewPagerAdapter: Gallery360PagerAdapter
    private lateinit var binding: ActivityGallery360TabBinding
    private var imageId = "0"
    lateinit var interiorViewModel: InteriorViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery360_tab)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery360_tab)
        init()
    }

    private fun init() {
        binding.title = ""
        exteriorViewModel = ViewModelProvider(this).get(ExteriorViewModel::class.java)
        interiorViewModel = ViewModelProvider(this).get(InteriorViewModel::class.java)
        ivEdit.visibility = View.INVISIBLE
        rvView.setOnClickListener(this)
        ll360View.setOnClickListener(this)
        llExterior.setOnClickListener(this)
        llInterior.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        gallery360ViewPagerAdapter = Gallery360PagerAdapter(supportFragmentManager)
        pager.adapter = gallery360ViewPagerAdapter

        if (intent.hasExtra(ARG_TYPE_VIEW) && intent.hasExtra(ARG_IMAGE_ID)) {
//            pager.currentItem =intent.getIntExtra(ARG_TYPE_VIEW,0)
            imageId = intent.getStringExtra(ARG_IMAGE_ID)!!
            getExteriorAPI(imageId)
            setTab(intent.getIntExtra(ARG_TYPE_VIEW, 0))
        } else {
            setTab(0)
        }
//        pager.isEnabled=(false)
        //backButton()
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

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rvView -> {
                Log.e("", "")
            }
            R.id.ll360View -> {
                setTab(2)
            }
            R.id.llInterior -> {
                setTab(1)
            }
            R.id.llExterior -> {
                setTab(0)
            }
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    private fun setTab(tab: Int) {
        when (tab) {
            0 -> {
                setTabVisible(
                    viewExterior,
                    tvExterior,
                    viewInterior,
                    tvInterior,
                    view360View,
                    tv360View
                )
                loadFragment(ExteriorFragment.newInstance(imageId))
            }
            1 -> {
                setTabVisible(
                    viewInterior,
                    tvInterior,
                    view360View,
                    tv360View,
                    viewExterior,
                    tvExterior
                )
                loadFragment(InteriorFragment.newInstance(imageId))
            }
            2 -> {
                setTabVisible(
                    view360View,
                    tv360View,
                    viewExterior,
                    tvExterior,
                    viewInterior,
                    tvInterior
                )
                loadFragment(View360Fragment.newInstance(imageId))
            }
        }
    }

    private fun setTabVisible(
        visibleView: View,
        tvTabActive: TextView,
        inVisibleView: View,
        tvTabDeActive: TextView,
        inVisibleView1: View,
        tvTabDeActive1: TextView
    ) {
        visibleView.visibility = View.VISIBLE
        tvTabActive.setTextColor(resources.getColor(R.color.colorPrimary))
        inVisibleView.visibility = View.GONE
        tvTabDeActive.setTextColor(resources.getColor(R.color.black))
        inVisibleView1.visibility = View.GONE
        tvTabDeActive1.setTextColor(resources.getColor(R.color.black))
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flView, fragment)
        // transaction.addToBackStack(null)
        transaction.commit()
    }

    lateinit var exteriorViewModel: ExteriorViewModel
    val arImages: ArrayList<String> = ArrayList()

    private fun getExteriorAPI(imageId: String?) {
        val request = HashMap<String, Any>()
        request[ApiConstant.ImageId] = imageId!!
        request[ApiConstant.ImageProduct] = "Splash"

        exteriorViewModel.getExterior(this, request)!!
            .observe(this, Observer { data ->
                Constant.dismissLoader()
                arImages.addAll(data)
                getInteriorAPI(imageId)
            }
            )
    }

    private fun getInteriorAPI(imageId: String?) {
        val request = HashMap<String, Any>()
        request[ApiConstant.ImageId] = imageId!!
        request[ApiConstant.ImageProduct] = ApiConstant.stillSet

        interiorViewModel.getInterior(this, request)!!
            .observe(this, Observer { data ->
                Constant.dismissLoader()
                if (arImages.size > 0)
                    arImages.addAll(arImages.size - 1, data)
                else
                    arImages.addAll(data)
            }
            )
    }
}