package com.gdgnantes.devfest.android

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.gdgnantes.devfest.android.features.base.app.BaseFragment
import com.gdgnantes.devfest.android.model.Session
import com.gdgnantes.devfest.android.view.inflate
import com.gdgnantes.devfest.android.viewmodel.BookmarkFilter
import com.gdgnantes.devfest.android.viewmodel.FiltersViewModel
import com.gdgnantes.devfest.android.viewmodel.TrackFilter

class FiltersFragment : BaseFragment() {

    companion object {
        fun newInstance(): FiltersFragment = FiltersFragment()
    }

    private lateinit var filtersModel: FiltersViewModel

    private var filtersAdapter: FiltersAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var clearMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filtersModel = ViewModelProviders.of(activity).get(FiltersViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.inflateMenu(R.menu.fragment_filters)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_clear -> {
                    filtersModel.clear()
                    filtersAdapter?.notifyDataSetChanged()
                    updateClearMenuItem()
                    true
                }
                else -> false
            }
        }

        clearMenuItem = toolbar.menu.findItem(R.id.action_clear)
        updateClearMenuItem()

        filtersAdapter = FiltersAdapter()

        recyclerView = view.findViewById(android.R.id.list)
        recyclerView!!.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = filtersAdapter
        }
    }

    override fun onDestroyView() {
        recyclerView = null
        super.onDestroyView()
    }

    private fun updateClearMenuItem() {
        clearMenuItem?.let {
            it.isVisible = filtersModel.hasFilters()
        }
    }

    private inner class FiltersHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val checkBox: CheckBox = view.findViewById(R.id.check_box)

        init {
            view.setOnClickListener(onItemClickListener)
        }
    }

    private inner class FiltersAdapter : RecyclerView.Adapter<FiltersHolder>() {

        val filters = listOf(
                BookmarkFilter,
                TrackFilter.get(Session.Track.Cloud),
                TrackFilter.get(Session.Track.Discovery),
                TrackFilter.get(Session.Track.Mobile),
                TrackFilter.get(Session.Track.Web))

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersHolder =
                FiltersHolder(parent.inflate(viewType, false))

        override fun getItemViewType(position: Int): Int = when (filters[position]) {
            is BookmarkFilter -> R.layout.list_item_filter_bookmarks
            else -> R.layout.list_item_filter_track
        }

        override fun getItemCount(): Int = filters.size

        override fun onBindViewHolder(holder: FiltersHolder, position: Int) {
            val filter = filters[position]
            if (filter is TrackFilter) {
                val track = filter.track
                with(holder.title) {
                    text = track.getName(context)
                    setTextColor(track.foregroundColor)
                    setBackgroundColor(track.backgroundColor)
                }
            }
            holder.checkBox.isChecked = filtersModel.isFilter(filters[position])
        }
    }

    val onItemClickListener = View.OnClickListener { view ->
        recyclerView!!.apply {
            val position = getChildLayoutPosition(view)
            if (position != RecyclerView.NO_POSITION) {
                filtersAdapter?.let {
                    filtersModel.toggleFilter(it.filters[position])
                }
                (findViewHolderForLayoutPosition(position) as FiltersHolder).checkBox.toggle()
                updateClearMenuItem()
            }
        }
    }

}
