package com.letyouknow.view.dashboard

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.model.DrawerData
import com.letyouknow.view.dashboard.drawer.DrawerListAdapter
import com.letyouknow.view.home.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_nav_drawer.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener,
    View.OnClickListener {
    private lateinit var adapterDrawer: DrawerListAdapter
    private var arDrawer: ArrayList<DrawerData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        setDrawerData()
        setNavDrawerData()
    }

    private fun setDrawerData() {
        arDrawer.add(DrawerData(1, 0, "Account"))
        arDrawer.add(DrawerData(1, 0, "Bid History"))
        arDrawer.add(DrawerData(1, 0, "Transaction History"))
        arDrawer.add(DrawerData(1, 0, "Favourite Searches"))
        arDrawer.add(DrawerData(1, 0, "How it works"))
        arDrawer.add(DrawerData(1, 0, "Contact Support"))
        arDrawer.add(DrawerData(1, 0, "Legal"))
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
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
//        navigationView.setNavigationItemSelectedListener(this)
        loadFragment(HomeFragment())
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.item1 -> {
                Toast.makeText(this, "Publication", Toast.LENGTH_SHORT).show()
            }

        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flContainer, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
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

                Handler().postDelayed({
                    drawer.closeDrawer(GravityCompat.START)
                }, 200)
            }
        }
    }
}