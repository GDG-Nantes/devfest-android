package com.gdgnantes.devfest.android

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.util.SparseArray
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import com.gdgnantes.devfest.android.app.BaseActivity
import com.gdgnantes.devfest.android.app.PreferencesManager
import com.gdgnantes.devfest.android.format.text.DateTimeFormatter
import com.gdgnantes.devfest.android.support.app.FragmentStatePagerAdapter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity() {

    companion object {
        private const val FRAGMENT_FILTERS = "fragment:filters"
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var viewPager: ViewPager

    private lateinit var adapter: PagesAdapter

    override fun onCreate(inState: Bundle?) {
        super.onCreate(inState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        setTitle(R.string.agenda)

        if (supportFragmentManager.findFragmentByTag(FRAGMENT_FILTERS) == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.filters, FiltersFragment.newInstance(), FRAGMENT_FILTERS)
                    .commit()
        }

        adapter = PagesAdapter(supportFragmentManager)

        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = adapter
        viewPager.pageMargin = resources.getDimensionPixelSize(R.dimen.spacing_medium)

        drawerLayout = findViewById(R.id.drawer_layout)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(onTabSelectedListener)

        val selectedTab = PreferencesManager.from(this).selectedTab
        if (selectedTab != null) {
            val indexOfTab = AppConfig.EVENT_DATES.indexOf(selectedTab)
            if (tabLayout.selectedTabPosition != indexOfTab) {
                tabLayout.getTabAt(indexOfTab)?.select()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> openFiltersDrawer()
            R.id.action_about -> startActivity(AboutActivity.newIntent(this))
            R.id.action_show_licenses -> startActivity(LicensesActivity.newIntent(this))
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun openFiltersDrawer() {
        drawerLayout.openDrawer(Gravity.RIGHT)
    }

    private inner class PagesAdapter(private val fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        private val dates = AppConfig.EVENT_DATES
        private val fragments: SparseArray<SessionsFragment> = SparseArray()

        override fun getCount(): Int = dates.size

        override fun getFragmentTag(position: Int): String {
            return "fragment:sessions:${dates[position]}"
        }

        override fun getItem(position: Int): Fragment {
            var fragment = fragments.get(position)
            if (fragment == null) {
                fragment = fm.findFragmentByTag(getFragmentTag(position)) as SessionsFragment?
                if (fragment == null) {
                    fragment = SessionsFragment.newInstance(dates[position])
                }
                fragments.put(position, fragment)
            }
            return fragment
        }

        override fun getPageTitle(position: Int): CharSequence {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dates[position])
            return DateTimeFormatter.formatMMMMd(date)
        }
    }

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabUnselected(tab: TabLayout.Tab) {}

        override fun onTabSelected(tab: TabLayout.Tab) {
            viewPager.setCurrentItem(tab.position, true)
            PreferencesManager.from(this@MainActivity).selectedTab = AppConfig.EVENT_DATES[tab.position]
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            val fragment = adapter.getItem(tab.position)
            if (fragment is SessionsFragment) {
                fragment.scrollToTop()
            }
        }
    }

}
