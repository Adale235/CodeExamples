package com.example.moveyourfeet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.moveyourfeet.fragments.HomeFragment
import com.example.moveyourfeet.fragments.StatisticsFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
////Alexander Schröder 4schoa24///
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       var homeFragment = HomeFragment()
       var statisticsFragment = StatisticsFragment()


        navigationHandling(homeFragment, statisticsFragment)


    }

    //handling the navigation through both fragments
    private fun navigationHandling(
        homeFragment: HomeFragment,
        statisticsFragment: StatisticsFragment
    ) {
        setSupportActionBar(toolbar)

        val drawerToggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navigation_view.setNavigationItemSelectedListener {
            try {
                val fragmentInstance =
                    if (it.itemId == R.id.nav_home) homeFragment else statisticsFragment
                drawer_layout.closeDrawers()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_holder, fragmentInstance).commit()

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
        try {
            supportFragmentManager.beginTransaction().replace(R.id.fragment_holder, homeFragment)
                .commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
