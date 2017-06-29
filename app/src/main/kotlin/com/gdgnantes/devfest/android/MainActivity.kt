package com.gdgnantes.devfest.android

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.util.SparseArray
import android.view.Menu
import android.view.MenuItem
import com.gdgnantes.devfest.android.app.BaseActivity
import com.gdgnantes.devfest.android.format.text.DateTimeFormatter
import com.gdgnantes.devfest.android.support.app.FragmentStatePagerAdapter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity() {

    companion object {
        const val PREFS_NAME = "main"

        const val PREFS_SELECTED_TAB = "prefs:selectedTab"
    }

    private lateinit var viewPager: ViewPager
    private lateinit var adapter: PagesAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(inState: Bundle?) {
        super.onCreate(inState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        setTitle(R.string.agenda)

        adapter = PagesAdapter(supportFragmentManager)
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = adapter
        viewPager.pageMargin = resources.getDimensionPixelSize(R.dimen.spacing_medium)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(onTabSelectedListener)

        val selectedTabTag = sharedPreferences.getString(PREFS_SELECTED_TAB, null)
        if (selectedTabTag != null) {
            val indexOfTab = AppConfig.EVENT_DATES.indexOf(selectedTabTag)
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
            R.id.action_filter ->
                Snackbar.make(findViewById(R.id.root), "Filtering is not implemented yet…", Snackbar.LENGTH_SHORT).show()
            R.id.action_about ->
                startActivity(AboutActivity.newIntent(this))
            R.id.action_show_licenses ->
                startActivity(LicensesActivity.newIntent(this))
            else -> return super.onOptionsItemSelected(item)
        }
        return true
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
            sharedPreferences.edit()
                    .putString(PREFS_SELECTED_TAB, AppConfig.EVENT_DATES[tab.position])
                    .apply()
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            val fragment = adapter.getItem(tab.position)
            if (fragment is SessionsFragment) {
                fragment.scrollToTop()
            }
        }
    }

}
