package com.gdgnantes.devfest.android.provider;

import android.content.ContentResolver;
import android.net.Uri;

import com.gdgnantes.devfest.android.BuildConfig;

public final class ScheduleContract {

    private static final String VENDOR_DEVFEST = "vnd.devfest";

    private static final String PREFIX_VND_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + VENDOR_DEVFEST + ".";
    private static final String PREFIX_VND_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + VENDOR_DEVFEST + ".";

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider.schedule";

    public static final Uri CONTENT_URI = new Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(AUTHORITY)
            .build();

    private static final String PATH_ROOMS = "rooms";
    private static final String PATH_SESSIONS = "sessions";
    private static final String PATH_SESSIONS_SPEAKERS = "sessions_speakers";
    private static final String PATH_SPEAKERS = "speakers";
    private static final String PATH_WITH_SPEAKERS = "with_speakers";

    private interface RoomColumns {
        String ROOM_ID = "room_id";
        String ROOM_NAME = "room_name";
    }

    private interface SessionColumns {
        String SESSION_ID = "session_id";
        String SESSION_DESCRIPTION = "session_description";
        String SESSION_END_TIMESTAMP = "session_end_timestamp";
        String SESSION_LANGUAGE = "session_language";
        String SESSION_ROOM_ID = "session_room_id";
        String SESSION_START_TIMESTAMP = "session_start_timestamp";
        String SESSION_TITLE = "session_title";
        String SESSION_TRACK = "session_track";
        String SESSION_TYPE = "session_type";
    }

    private interface SessionsSpeakersColumns {
        String SESSION_SPEAKER_SPEAKER_ID = "session_speaker_speaker_id";
        String SESSION_SPEAKER_SESSION_ID = "session_speaker_session_id";
    }

    private interface SpeakerColumns {
        String SPEAKER_ID = "speaker_id";
        String SPEAKER_BIO = "speaker_bio";
        String SPEAKER_COMPANY = "speaker_company";
        String SPEAKER_NAME = "speaker_name";
        String SPEAKER_PHOTO_URL = "speaker_photo_url";
        String SPEAKER_SOCIAL_LINKS = "speaker_social_links";
    }

    public interface Extras {
        String COUNT_LIMIT = "count_limit";
    }

    public static final class Rooms implements RoomColumns {

        public static final Uri CONTENT_URI = ScheduleContract.CONTENT_URI.buildUpon()
                .appendPath(PATH_ROOMS)
                .build();

        public static final String CONTENT_TYPE = PREFIX_VND_DIR + "stations";
        public static final String CONTENT_ITEM_TYPE = PREFIX_VND_ITEM + "stations";

        public static Uri buildUri(String roomId) {
            return buildItemUri(CONTENT_URI, roomId);
        }

        public static String getId(Uri uri) {
            return getItemUri(uri);
        }

    }

    public static final class Sessions implements SessionColumns, RoomColumns {

        public static final Uri CONTENT_URI = ScheduleContract.CONTENT_URI.buildUpon()
                .appendPath(PATH_SESSIONS)
                .build();

        public static final String CONTENT_TYPE = PREFIX_VND_DIR + "sessions";
        public static final String CONTENT_ITEM_TYPE = PREFIX_VND_ITEM + "sessions";

        public static Uri buildUri(String sessionId) {
            return buildItemUri(CONTENT_URI, sessionId);
        }

        public static Uri buildUriWithSpeakers(String sessionId) {
            return buildUri(sessionId).buildUpon()
                    .appendPath(PATH_WITH_SPEAKERS)
                    .build();
        }

        public static String getId(Uri uri) {
            return getItemUri(uri);
        }

    }

    public static final class SessionsSpeakers implements SessionsSpeakersColumns, RoomColumns,
            SessionColumns,
            SpeakerColumns {

        public static final Uri CONTENT_URI = ScheduleContract.CONTENT_URI.buildUpon()
                .appendPath(PATH_SESSIONS_SPEAKERS)
                .build();

        public static final String CONTENT_TYPE = PREFIX_VND_DIR + "sessions_speakers";
        public static final String CONTENT_ITEM_TYPE = PREFIX_VND_ITEM + "sessions_speakers";

        public static Uri buildUri(String speakerId) {
            // HACK Cyril
            return CONTENT_URI;
        }

        public static String getId(Uri uri) {
            // HACK Cyril
            return "hello_hack";
        }

    }

    public static final class Speakers implements SpeakerColumns {

        public static final Uri CONTENT_URI = ScheduleContract.CONTENT_URI.buildUpon()
                .appendPath(PATH_SPEAKERS)
                .build();

        public static final String CONTENT_TYPE = PREFIX_VND_DIR + "speakers";
        public static final String CONTENT_ITEM_TYPE = PREFIX_VND_ITEM + "speakers";

        public static Uri buildUri(String speakerId) {
            return buildItemUri(CONTENT_URI, speakerId);
        }

        public static String getId(Uri uri) {
            return getItemUri(uri);
        }

    }

    private static Uri buildItemUri(Uri uri, String id) {
        return uri.buildUpon()
                .appendPath(id)
                .build();
    }

    private static String getItemUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }

}
