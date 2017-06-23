package com.gdgnantes.devfest.android

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ShareCompat
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.gdgnantes.devfest.android.app.BaseFragment
import com.gdgnantes.devfest.android.format.text.DateTimeFormatter
import com.gdgnantes.devfest.android.graphics.RoundedTransformation
import com.gdgnantes.devfest.android.util.Tags
import com.gdgnantes.devfest.android.view.inflate
import com.gdgnantes.devfest.android.viewmodel.SessionViewModel
import com.gdgnantes.devfest.android.widget.ScrollView
import com.squareup.picasso.Picasso


class SessionFragment : BaseFragment() {

    companion object {
        private const val ARG_SESSION_ID = "arg:sessionId"

        fun newInstance(sessionId: String): SessionFragment = SessionFragment().apply {
            arguments = Bundle()
            arguments.putString(ARG_SESSION_ID, sessionId)
        }
    }

    private val tempRect = Rect()

    private lateinit var sessionId: String

    private var title: String? = null
    private var displayingTitle: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionId = arguments.getString(ARG_SESSION_ID)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_session, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btn_bookmark).setOnClickListener {
            Snackbar.make(view.findViewById<View>(R.id.root), "Not implemented yet…", Snackbar.LENGTH_SHORT).show()
        }

        view.findViewById<ScrollView>(R.id.scroll_view).onScrollChangeListener = { _, _, _ ->
            val titleView = view.findViewById<View>(R.id.title)
            titleView.getDrawingRect(tempRect)
            view.findViewById<ViewGroup>(R.id.root).offsetDescendantRectToMyCoords(titleView, tempRect)

            val oldDisplayingTitle = displayingTitle
            val dimension = if (displayingTitle) tempRect.top else tempRect.bottom

            displayingTitle = dimension < 0 && !title.isNullOrEmpty()

            if (oldDisplayingTitle != displayingTitle) {
                if (displayingTitle) {
                    (activity as AppCompatActivity).supportActionBar?.setTitle(title)
                } else {
                    (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.sessionDetails)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val data = ViewModelProviders.of(this).get(SessionViewModel::class.java)
        data.getSession(sessionId).observe(this, Observer {

            title = it!!.session.title

            val view = view!!

            view.findViewById<TextView>(R.id.title).text = it.session.title
            view.findViewById<TextView>(R.id.information).text = getString(R.string.session_info,
                    DateTimeFormatter.formatEEEEMMMMd(it.session.startTimestamp),
                    DateTimeFormatter.formatHHmm(it.session.startTimestamp),
                    DateTimeFormatter.formatHHmm(it.session.endTimestamp),
                    it.room.name)
            view.findViewById<TextView>(R.id.description).text = it.session.description

            val tagsContainer = view.findViewById<ViewGroup>(R.id.tags_container)
            it.speakers.flatMap { it.tags ?: emptyList() }
                    .distinct()
                    .forEach {
                        val tagView = layoutInflater.inflate<TextView>(R.layout.fragment_session_tag, tagsContainer, false)
                        tagView.text = it
                        tagView.setBackgroundColor(Tags.colorForTag(it))
                        tagsContainer.addView(tagView)
                    }

            val speakersContainer: ViewGroup = view.findViewById(R.id.speakers_container)
            it.speakers.forEach {
                val speakerView = layoutInflater.inflate(R.layout.fragment_session_speaker, speakersContainer, false)
                speakerView.findViewById<TextView>(R.id.name).text = it.name
                speakerView.findViewById<TextView>(R.id.company).text = it.company
                speakerView.findViewById<TextView>(R.id.description).text = it.bio

                Picasso.with(context)
                        .load(it.photoUrl)
                        .fit()
                        .transform(RoundedTransformation())
                        .into(speakerView.findViewById<ImageView>(R.id.thumbnail))

                speakersContainer.addView(speakerView)
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_session, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_share -> shareSession()
        else -> super.onOptionsItemSelected(item)
    }

    private fun shareSession(): Boolean {
        if (!title.isNullOrEmpty()) {
            ShareCompat.IntentBuilder.from(activity)
                    .setType("text/plain")
                    .setSubject(getString(R.string.share_title))
                    .setText(getString(R.string.share_text, title))
                    .startChooser()
            return true
        }
        return false
    }

}
