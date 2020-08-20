package com.degradators.degradators

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.degradators.degradators.databinding.ActivityMainBinding
import com.degradators.degradators.databinding.NavHeaderMainBinding
import com.degradators.degradators.ui.MainViewModel
import com.degradators.degradators.ui.addArticles.AddArticleActivity
import com.degradators.degradators.ui.login.LoginActivity
import com.degradators.degradators.ui.main.BaseActivity
import com.degradators.degradators.ui.main.SectionsPagerAdapter
import com.degradators.degradators.ui.userMenu.MyListFragment
import com.degradators.degradators.ui.utils.loadImage
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_home.*

const val MESSAGEIDS = "messageIds"

class MainActivity : BaseActivity<MainViewModel>(),
    NavigationView.OnNavigationItemSelectedListener {

    override val viewModel: MainViewModel by viewModels { factory }
    lateinit var binding: ActivityMainBinding
    private val SIGN_IN = 9002
    private val ADD_ARTICLE_ACTIVITY = 1000
    private var isLogin = false

    lateinit var sectionsPagerAdapter: SectionsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        addNavigationHeader()

        fab.setOnClickListener {
            val intent = Intent(this, AddArticleActivity::class.java)
            startActivityForResult(intent, ADD_ARTICLE_ACTIVITY)
        }
    }

    private fun addNavigationHeader() {
        val navHeaderMainBinding: NavHeaderMainBinding =
            DataBindingUtil.inflate(
                layoutInflater, R.layout.nav_header_main, binding
                    .navView, false
            )
        binding.navView.addHeaderView(navHeaderMainBinding.root)
        navHeaderMainBinding.apply {
            lifecycleOwner = this@MainActivity
            this.mainViewModel = viewModel.apply {

                navigationHeader.setOnClickListener {
                    if (!isLogin) {
                        drawer_layout.closeDrawers()
                        startActivityForResult(
                            Intent(this@MainActivity, LoginActivity::class.java),
                            SIGN_IN
                        )
                    }
                }
            }
        }
        observeEvents(navHeaderMainBinding)
    }

    private fun observeEvents(navHeaderMainBinding: NavHeaderMainBinding){
        changeHeaderSize(navHeaderMainBinding)
        viewModel.userUrl.observe(this, Observer {url ->
            navHeaderMainBinding.userPhotoLogIn.let {
                it.loadImage(
                    url?: "",
                    R.mipmap.ic_launcher_round
                )
            }

        })
    }

    private fun changeHeaderSize(navHeaderMainBinding: NavHeaderMainBinding) {
        viewModel.userSignedIn.observe(this, Observer {
            navHeaderMainBinding.navigationHeader.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            isLogin = it
            binding.navView.menu.findItem(R.id.nav_my_list).isVisible = it
            //TODO doesn't hide
            binding.navView.menu.findItem(R.id.nav_Logout).isVisible = it
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.nav_posts -> {
                drawer_layout.closeDrawers()
                supportFragmentManager.popBackStack()
                tabs.visibility = View.VISIBLE
                view_pager.visibility = View.VISIBLE
                user_frame.visibility = View.GONE
                return true
            }
            R.id.nav_my_list -> {
                drawer_layout.closeDrawers()
                val array = viewModel.messageIds.value
                val myListFragment = MyListFragment().apply {
                    arguments = Bundle().apply {
                        putStringArray(MESSAGEIDS, array?.toTypedArray())
                    }
                }
                tabs.visibility = View.GONE
                view_pager.visibility = View.GONE
                user_frame.visibility = View.VISIBLE
                supportFragmentManager.beginTransaction()
                    .replace(R.id.user_frame, myListFragment)
                    .commitAllowingStateLoss()
                return true
            }
            R.id.nav_Logout -> {
                drawer_layout.closeDrawers()
                viewModel.logout()
                return true
            }

            else -> super.onNavigateUp()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN) {
            if (resultCode == RESULT_OK) {
                viewModel.isLogin()
            }
        }
        if(requestCode == ADD_ARTICLE_ACTIVITY){
            sectionsPagerAdapter.notifyDataSetChanged()
        }
    }
}
