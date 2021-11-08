package com.letyouknow.view.dashboard

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.letyouknow.R
import com.letyouknow.base.BaseActivity
import com.letyouknow.model.DrawerData
import com.letyouknow.model.LoginData
import com.letyouknow.utils.AppGlobal
import com.letyouknow.view.account.AccountFragment
import com.letyouknow.view.bidhistory.BidHistoryActivity
import com.letyouknow.view.dashboard.drawer.DrawerListAdapter
import com.letyouknow.view.dealnearyou.OneDealNearYouFragment
import com.letyouknow.view.home.HomeFragment
import com.letyouknow.view.howitworkhelp.HowItWorkHelpWebViewActivity
import com.letyouknow.view.login.LoginActivity
import com.letyouknow.view.submitprice.SubmitYourPriceFragment
import com.letyouknow.view.transaction_history.TransactionHistoryActivity
import com.pionymessenger.utils.Constant
import com.pionymessenger.utils.Constant.Companion.ARG_TITLE
import com.pionymessenger.utils.Constant.Companion.ARG_WEB_URL
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_logout.*
import kotlinx.android.synthetic.main.layout_nav_drawer.*
import org.jetbrains.anko.*


class MainActivity : BaseActivity(),
    View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var adapterDrawer: DrawerListAdapter
    private var arDrawer: ArrayList<DrawerData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
            ivEdit.visibility = View.GONE
            toolbar.backgroundColor = resources.getColor(R.color.colord3e6ff)
            toolbar.elevation = 0f
        } else {
            ivEdit.visibility = View.INVISIBLE
            toolbar.backgroundColor = resources.getColor(R.color.white)
            toolbar.elevation = 8f
        }
    }

    fun setVisibleLogoutImg(isVisible: Boolean) {
        ivLogOut.visibility = if (isVisible) View.GONE else View.GONE
    }

    private fun init() {
        userData = pref?.getUserData()!!
        main = this
        setSupportActionBar(toolbar)
        ivMenu.setOnClickListener(this)
        ivCloseDrawer.setOnClickListener(this)
        ivLogOut.setOnClickListener(this)
        ivEdit.setOnClickListener(this)
        llLogout.setOnClickListener(this)
        setDrawerData()
        setNavDrawerData()
        bottomNavigation.setOnNavigationItemSelectedListener(this)

    }

    private fun setDrawerData() {
//        arDrawer.add(DrawerData(1, R.drawable.ic_account, R.drawable.ic_account_white, "Account"))
        arDrawer.add(
            DrawerData(
                2,
                R.drawable.ic_bid_history,
                R.drawable.ic_bid_history_white,
                "Price Bid History"
            )
        )
        arDrawer.add(
            DrawerData(
                3,
                R.drawable.ic_transaction_history_white,
                R.drawable.ic_transaction_history,
                "Transaction History"
            )
        )
        /* arDrawer.add(
             DrawerData(
                 4,
                 R.drawable.ic_fav,
                 R.drawable.ic_fav_white,
                 "Favourite Searches"
             )
         )*/
        arDrawer.add(
            DrawerData(
                5,
                R.drawable.ic_how_work,
                R.drawable.ic_how_work_white,
                "How it works"
            )
        )
        arDrawer.add(
            DrawerData(
                6,
                R.drawable.ic_contact,
                R.drawable.ic_contact_white,
                "Help"
            )
        )
        arDrawer.add(
            DrawerData(
                7,
                R.drawable.ic_terms_conditions,
                R.drawable.ic_terms_conditions_white,
                "Terms & Conditions"
            )
        )
        arDrawer.add(
            DrawerData(
                8,
                R.drawable.ic_privacy_policy,
                R.drawable.ic_privacy_policy_white,
                "Privacy Policy"
            )
        )
//        arDrawer.add(DrawerData(8, R.drawable.ic_logout, R.drawable.ic_logout, "Logout"))
    }

    private lateinit var userData: LoginData

    private fun setNavDrawerData() {
        rvNavigation.layoutManager = LinearLayoutManager(this)
        rvNavigation.setHasFixedSize(true)
        adapterDrawer = DrawerListAdapter(R.layout.list_item_drawer, this)
        rvNavigation.adapter = adapterDrawer
        adapterDrawer.addAll(arDrawer)

        tvUserName.text = userData.firstName + " " + userData.lastName
        tvUserEmail.text = userData.userName
//        loadFragment(HomeFragment(), getString(R.string.search_deals_title))
        loadFragment(SubmitYourPriceFragment(), getString(R.string.submit_your_price))
    }

    override fun getViewActivity(): Activity {
        return this
    }

    override fun onNetworkStateChange(isConnect: Boolean) {
    }

    private fun loadFragment(fragment: Fragment, title: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flContainer, fragment)
        // transaction.addToBackStack(null)
        transaction.commit()
        setTitle(title)
    }


    override fun onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer(Gravity.RIGHT)
            return
        }
        val item: MenuItem = bottomNavigation.menu.findItem(R.id.itemBottom1)
        if (item.isChecked) {
            val fragment = supportFragmentManager.findFragmentById(R.id.flContainer)
           /* if (fragment is DealSummeryActivity || fragment is DealSummeryStep2Fragment) {
                loadFragment(HomeFragment(), getString(R.string.search_deals_title))
            } else {*/
                super.onBackPressed()
//            }
        } else {
            if (selectDrawerPos != -1) {
                val data = adapterDrawer.getItem(selectDrawerPos)
                data.isSelect = false
                adapterDrawer.update(selectDrawerPos, data)
            }
            selectDrawerPos = -1
            loadFragment(SubmitYourPriceFragment(), getString(R.string.submit_your_price))
            item.isChecked = true
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
                    /* 0 -> {
                     bottomNavigation.selectedItemId = R.id.itemBottom4
                     loadFragment(AccountFragment(), getString(R.string.account))
                 }*/
                    0 -> {
                        startActivity<BidHistoryActivity>()
                    }
                    1 -> {
                        startActivity<TransactionHistoryActivity>()
                    }
                    2 -> {
                        startActivity<HowItWorkHelpWebViewActivity>(
                            ARG_TITLE to data.title,
                            ARG_WEB_URL to Constant.HOW_IT_WORKS
                        )
//                        AppGlobal.dialogWebView(this, Constant.HOW_IT_WORKS)
                    }
                    3 -> {
                        startActivity<HowItWorkHelpWebViewActivity>(
                            ARG_TITLE to data.title,
                            ARG_WEB_URL to Constant.HELP
                        )
//                        AppGlobal.dialogWebView(this, Constant.FAQ)
                    }

                    4 -> {
                        AppGlobal.dialogWebView(this, Constant.TERMS_CONDITIONS_LINK)
                    }
                    5 -> {
                        AppGlobal.dialogWebView(this, Constant.PRIVACY_POLICY_LINK)
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
            R.id.ivEdit -> {
                loadFragment(HomeFragment(), getString(R.string.search_deals_title))
            }
            R.id.ivLogOut -> {
                popupLogout()
            }
            R.id.llLogout -> {
                drawer.closeDrawer(Gravity.RIGHT)
                popupLogout()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.itemBottom1 -> {
                loadFragment(SubmitYourPriceFragment(), getString(R.string.submit_your_price))
            }
            R.id.itemBottom2 -> {
                loadFragment(OneDealNearYouFragment(), getString(R.string.one_deal_near_you))
            }
            R.id.itemBottom3 -> {
                loadFragment(HomeFragment(), getString(R.string.search_deals_title))
            }
            R.id.itemBottom4 -> {
                loadFragment(AccountFragment(), getString(R.string.account))
            }
        }
        return true
    }

    private fun popupLogout() {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_logout)

        dialog.run {
            tvLogOut.setOnClickListener {
                pref?.setLogOutData()
                startActivity(intentFor<LoginActivity>().clearTask().newTask())
                dialog.dismiss()
            }
            tvCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        setLayoutParam(dialog)
        dialog.show()
    }

    private fun setLayoutParam(dialog: Dialog) {
        val layoutParams: WindowManager.LayoutParams = dialog.window?.attributes!!
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.attributes = layoutParams
    }

    /*  private fun setupViewPager(viewPager: ViewPager) {



          getString(R.string.account)
          val adapter = MainViewPagerAdapter(supportFragmentManager)
          accountFragment = AccountFragment()
          homeFragment = HomeFragment()
          submitYourPriceFragment = SubmitYourPriceFragment()
          oneDealNearYouFragment = OneDealNearYouFragment()
          adapter.addFragment(submitYourPriceFragment,getString(R.string.submit_your_price))
          adapter.addFragment(oneDealNearYouFragment,getString(R.string.one_deal_near_you))
          adapter.addFragment(homeFragment)
          adapter.addFragment(accountFragment)
          viewPager.adapter = adapter
      }*/
}