package com.letyouknow.view.dashboard

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.model.DrawerData
import com.letyouknow.view.account.AccountFragment
import com.letyouknow.view.bidhistory.BidHistoryActivity
import com.letyouknow.view.dashboard.drawer.DrawerListAdapter
import com.letyouknow.view.home.HomeFragment
import com.letyouknow.view.savedsearches.SavedSearchesActivity
import com.letyouknow.view.transaction_history.TransactionHistoryActivity
import com.letyouknow.view.unlockedcardeal.UnlockedCarDealFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_nav_drawer.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.startActivity


class MainActivity : BaseActivity(),
    View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var adapterDrawer: DrawerListAdapter
    private var arDrawer: ArrayList<DrawerData> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL
        init()
    }

    companion object {
        private lateinit var main: MainActivity
        fun getInstance(): MainActivity {
            return main
        }
    }

    fun setTitle(title: String) {
        tvMainTitle.text = title
    }

    fun setVisibleEditImg(isVisible: Boolean) {
        if (isVisible) {
            ivEdit.visibility = View.VISIBLE
            toolbar.backgroundColor = resources.getColor(R.color.colord3e6ff)
            toolbar.elevation = 0f
        } else {
            ivEdit.visibility = View.INVISIBLE
            toolbar.backgroundColor = resources.getColor(R.color.white)
            toolbar.elevation = 8f
        }
    }

    private fun init() {
        main = this
        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeButtonEnabled(true)
        ivMenu.setOnClickListener(this)
        ivCloseDrawer.setOnClickListener(this)
        setDrawerData()
        setNavDrawerData()
        bottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    private fun setDrawerData() {
        arDrawer.add(DrawerData(1, 0, "Account"))
        arDrawer.add(DrawerData(2, 0, "Bid History"))
        arDrawer.add(DrawerData(3, 0, "Transaction History"))
        arDrawer.add(DrawerData(4, 0, "Favourite Searches"))
        arDrawer.add(DrawerData(5, 0, "How it works"))
        arDrawer.add(DrawerData(6, 0, "Contact Support"))
        arDrawer.add(DrawerData(7, 0, "Legal"))
    }

    private fun setNavDrawerData() {
        rvNavigation.layoutManager = LinearLayoutManager(this)
        rvNavigation.setHasFixedSize(true)
        adapterDrawer = DrawerListAdapter(R.layout.list_item_drawer, this)
        rvNavigation.adapter = adapterDrawer
        adapterDrawer.addAll(arDrawer)

        val drawerToggle =
            object : ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close) {
                override fun onDrawerClosed(drawerView: View) {
                    super.onDrawerClosed(drawerView)
                    invalidateOptionsMenu()
                }

                override fun onDrawerOpened(drawerView: View) {
                    super.onDrawerOpened(drawerView)
                    invalidateOptionsMenu()
                }
            }
//        drawer.addDrawerListener(drawerToggle)
//        drawerToggle.syncState()
//        navigationView.setNavigationItemSelectedListener(this)
        loadFragmentHome(HomeFragment(), getString(R.string.app_name))
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }


    private fun loadFragmentHome(fragment: Fragment, title: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flContainer, fragment)
        transaction.commit()
        setTitle(title)
    }

    private fun loadFragment(fragment: Fragment, title: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
        setTitle(title)
    }


    override fun onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer(Gravity.RIGHT)
        } else {
            super.onBackPressed()
        }
    }

    var selectDrawerPos = -1
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.llDrawer -> {
                val pos = v.tag as Int
                if (selectDrawerPos != -1) {
                    val data = adapterDrawer.getItem(selectDrawerPos)
                    data.isSelect = false
                    adapterDrawer.update(selectDrawerPos, data)
                }
                val data = adapterDrawer.getItem(pos)
                data.isSelect = true
                adapterDrawer.update(pos, data)
                selectDrawerPos = pos
                when (pos) {
                    0 -> {

                    }
                    1 -> {
                        startActivity<BidHistoryActivity>()
                    }
                    2 -> {
                        startActivity<TransactionHistoryActivity>()
                    }
                    3 -> {
                        startActivity<SavedSearchesActivity>()
                    }
                    4 -> {

                    }
                }
                Handler().postDelayed({
                    drawer.closeDrawer(Gravity.RIGHT)
                }, 200)

            }
            R.id.ivMenu -> {
                drawer.openDrawer(Gravity.RIGHT)
                /* if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                     drawer.closeDrawer(Gravity.RIGHT)
                 }*/
            }
            R.id.ivCloseDrawer -> {
                drawer.closeDrawer(Gravity.RIGHT)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemBottom1 -> {
                loadFragmentHome(HomeFragment(), getString(R.string.app_name))
            }
            R.id.itemBottom2 -> {

            }
            R.id.itemBottom3 -> {
                loadFragment(UnlockedCarDealFragment(), getString(R.string.unlocked_car_deals))
            }
            R.id.itemBottom4 -> {

            }
            R.id.itemBottom5 -> {
                loadFragment(AccountFragment(), getString(R.string.account))
            }
        }
        return true
    }
}