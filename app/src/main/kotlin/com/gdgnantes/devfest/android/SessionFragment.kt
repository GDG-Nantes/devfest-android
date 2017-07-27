package com.gdgnantes.devfest.android

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ShareCompat
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.gdgnantes.devfest.android.app.BaseFragment
import com.gdgnantes.devfest.android.format.text.DateTimeFormatter
import com.gdgnantes.devfest.android.graphics.RoundedTransformation
import com.gdgnantes.devfest.android.model.Session
import com.gdgnantes.devfest.android.model.SocialNetwork
import com.gdgnantes.devfest.android.model.Speaker
import com.gdgnantes.devfest.android.view.inflate
import com.gdgnantes.devfest.android.viewmodel.SessionViewModel
import com.gdgnantes.devfest.android.widget.ScrollView
import com.gdgnantes.devfest.android.widget.applyText
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

    private var rootView: ViewGroup? = null
    private var titleView: TextView? = null
    private var bookmarkButton: FloatingActionButton? = null

    private var model: SessionViewModel? = null
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

        rootView = view.findViewById(R.id.root)
        titleView = view.findViewById(R.id.title)

        bookmarkButton = view.findViewById(R.id.btn_bookmark)
        bookmarkButton!!.setOnClickListener { toggleFavorite() }

        view.findViewById<ScrollView>(R.id.scroll_view).onScrollChangeListener = { _, _, _ ->
            updateTitle()
        }

        updateBookmark()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        model = ViewModelProviders.of(this).get(SessionViewModel::class.java)
        model!!.apply {
            init(sessionId)
            session.observe(this@SessionFragment, Observer {
                displaySession(it!!, view!!)
            })
        }
    }

    override fun onDestroyView() {
        rootView = null
        titleView = null
        bookmarkButton = null
        super.onDestroyView()
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
        val title = model!!.sessionTitle
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

    private fun displaySession(model: SessionViewModel.Data, view: View) {
        view.findViewById<TextView>(R.id.title).text = model.session.title

        val informationParts = ArrayList<String>()
        informationParts.add(getString(R.string.session_info_time,
                DateTimeFormatter.formatEEEEMMMMd(model.session.startTimestamp),
                DateTimeFormatter.formatHHmm(model.session.startTimestamp),
                DateTimeFormatter.formatHHmm(model.session.endTimestamp)))
        if (model.room != null) {
            informationParts.add(getString(R.string.session_info_room, model.room.name))
        }
        when (model.session.language) {
            "en" -> informationParts.add(getString(R.string.session_language_inEnglish))
            "fr" -> informationParts.add(getString(R.string.session_language_inFrench))
        }
        view.findViewById<TextView>(R.id.information).text = informationParts.joinToString("\n")

        view.findViewById<TextView>(R.id.description).applyText(model.session.description)

        with(view.findViewById<TextView>(R.id.track)) {
            if (model.session.track != null) {
                visibility = View.VISIBLE
                text = model.session.track.getName(context)
                setTextColor(model.session.track.foregroundColor)
                setBackgroundColor(model.session.track.backgroundColor)
            } else {
                visibility = View.GONE
            }
        }

        bookmarkButton?.apply {
            visibility = if (model.session.type == Session.Type.Break) View.GONE else View.VISIBLE
        }

        displaySpeakers(model.speakers, view)
    }

    private fun displaySpeakers(speakers: List<Speaker>, sessionView: View) {
        val speakersTopDivider = sessionView.findViewById<View>(R.id.speakers_top_divider)
        val speakersContainer: ViewGroup = sessionView.findViewById(R.id.speakers_container)
        speakersContainer.removeAllViews()
        speakersTopDivider.visibility = if (!speakers.isEmpty()) View.VISIBLE else View.GONE
        speakers.forEach {
            val speakerView = speakersContainer.inflate<View>(R.layout.fragment_session_speaker)
            speakerView.findViewById<TextView>(R.id.name).applyText(it.name)
            speakerView.findViewById<TextView>(R.id.company).applyText(it.company)
            speakerView.findViewById<TextView>(R.id.description).applyText(it.bio)

            Picasso.with(context)
                    .load(it.photoUrl)
                    .fit()
                    .transform(RoundedTransformation())
                    .into(speakerView.findViewById<ImageView>(R.id.thumbnail))

            displayNetworkLinks(it, speakerView)
        }
    }

    private fun displayNetworkLinks(speaker: Speaker, speakerView: View) {
        val socialLinksView = speakerView.findViewById<ViewGroup>(R.id.social_links_container)
        speaker.socialLinks.filter { it.url != null }
                .forEach { socialLink ->
                    val socialLinkView = socialLinksView.inflate<ImageButton>(R.layout.fragment_session_speaker_social_link)
                    socialLinkView.setImageDrawable(SocialNetwork.getIcon(socialLink.network, context))
                    socialLinkView.contentDescription = SocialNetwork.getName(socialLink.network, context)
                    socialLinkView.setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(socialLink.url))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT))
                    }

                    socialLinksView.visibility = View.VISIBLE
                }
    }

    private fun toggleFavorite() {
        val favoriteManager = BookmarkManager.from(context)
        if (favoriteManager.isBookmarked(sessionId)) {
            favoriteManager.unbookmark(sessionId)
        } else {
            favoriteManager.bookmark(sessionId)
        }
        updateBookmark()
    }

    private fun updateBookmark() {
        bookmarkButton!!.apply {
            val favoriteManager = BookmarkManager.from(context)
            if (favoriteManager.isBookmarked(sessionId)) {
                setImageResource(R.drawable.ic_action_unbookmark)
            } else {
                setImageResource(R.drawable.ic_action_bookmark)
            }
        }
    }

    private fun updateTitle() {
        titleView!!.getDrawingRect(tempRect)
        rootView!!.offsetDescendantRectToMyCoords(titleView, tempRect)

        val title = model!!.sessionTitle
        val oldDisplayingTitle = displayingTitle
        val dimension = if (displayingTitle) tempRect.top else tempRect.bottom

        displayingTitle = dimension < 0 && !title.isNullOrEmpty()

        if (oldDisplayingTitle != displayingTitle) {
            if (displayingTitle) {
                activity.title = title
            } else {
                activity.setTitle(R.string.session_title)
            }
        }
    }

}
