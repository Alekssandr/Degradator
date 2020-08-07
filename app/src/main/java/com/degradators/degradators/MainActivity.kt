package com.degradators.degradators

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.viewpager.widget.ViewPager
import com.degradators.degradators.ui.addArticles.AddArticleActivity
import com.degradators.degradators.ui.login.LoginActivity
import com.degradators.degradators.ui.main.SectionsPagerAdapter
import com.degradators.degradators.ui.userMenu.MyListFragment
import com.degradators.degradators.ui.userMenu.MyListFragment2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_home.*


class MainActivity : DaggerAppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
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

        fab.setOnClickListener {
            val intent = Intent(this, AddArticleActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, LoginActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.nav_posts -> {
                drawer_layout.closeDrawers()
                supportFragmentManager.popBackStack()
                tabs.visibility = View.VISIBLE
                view_pager.visibility = View.VISIBLE

                return true
            }
            R.id.nav_my_list -> {
                drawer_layout.closeDrawers()
                val myListFragment = MyListFragment()
                tabs.visibility = View.GONE
                view_pager.visibility = View.GONE
                supportFragmentManager.beginTransaction()
                    .replace(R.id.user_frame, myListFragment)
                    .commitAllowingStateLoss()
                return true
            }
            R.id.nav_my_list2 -> {
                drawer_layout.closeDrawers()
                val myListFragment2 = MyListFragment2()
                tabs.visibility = View.GONE
                view_pager.visibility = View.GONE
                supportFragmentManager.beginTransaction()
                    .replace(R.id.user_frame, myListFragment2)
                    .commitAllowingStateLoss()
                return true
            }
            else -> super.onNavigateUp()
        }
    }


}
