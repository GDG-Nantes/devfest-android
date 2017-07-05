package com.gdgnantes.devfest.android

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gdgnantes.devfest.android.app.BaseFragment
import com.gdgnantes.devfest.android.format.text.DateTimeFormatter
import com.gdgnantes.devfest.android.view.bind
import com.gdgnantes.devfest.android.viewmodel.SessionsViewModel
import kotlin.LazyThreadSafetyMode.NONE


class SessionsFragment : BaseFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SessionsAdapter

    companion object {
        private const val ARG_DATE = "arg:date"

        fun newInstance(date: String): SessionsFragment = SessionsFragment().apply {
            arguments = Bundle()
            arguments.putString(ARG_DATE, date)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val date = arguments.getString(ARG_DATE)
        val model = ViewModelProviders.of(this).get(SessionsViewModel::class.java)
        model.getSessions(date).observe(this, Observer {
            adapter.sessions = it!!
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sessions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SessionsAdapter(context)

        recyclerView = view.findViewById<RecyclerView>(android.R.id.list)
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter
    }

    fun scrollToTop() {
        recyclerView.smoothScrollToPosition(0)
    }

    private inner class SessionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener(onItemClickListener)
        }

        val title: TextView by view.bind<TextView>(R.id.title)
        val subtitle: TextView by view.bind<TextView>(R.id.subtitle)
    }

    private inner class SessionsAdapter(
            val context: Context) : RecyclerView.Adapter<SessionViewHolder>() {

        var sessions: List<SessionsViewModel.Data> = emptyList()
            set(sessions) {
                field = sessions
                notifyDataSetChanged()
            }

        override fun onBindViewHolder(holder: SessionViewHolder?, position: Int) {
            val session = sessions.get(position)
            holder!!.title.text = session.session.title
            holder.subtitle.text = getString(R.string.session_subtitle, session.room.name, DateTimeFormatter.formatHHmm(session.session.startTimestamp))
        }

        override fun getItemCount(): Int = sessions.size

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SessionViewHolder {
            return SessionViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_session, parent, false))
        }
    }

    val onItemClickListener = View.OnClickListener { view ->
        val position = recyclerView.getChildAdapterPosition(view)
        if (position != -1) {
            startActivity(SessionActivity.newIntent(context, adapter.sessions[position].session.id))
        }
    }

}
