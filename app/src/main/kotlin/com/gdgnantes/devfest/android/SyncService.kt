package com.gdgnantes.devfest.android

import android.app.IntentService
import android.content.ContentProviderOperation
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gdgnantes.devfest.android.app.PreferencesManager
import com.gdgnantes.devfest.android.http.JsonConverters
import com.gdgnantes.devfest.android.model.Schedule
import com.gdgnantes.devfest.android.model.toContentValues
import com.gdgnantes.devfest.android.provider.ScheduleContract
import com.google.firebase.crash.FirebaseCrash
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import java.io.IOException
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

class SyncService : IntentService(TAG) {

    // NOTE Cyril
    // The current implementation of the sync mechanism is fairly simple …
    // not to say stupid^^.
    // The sync consists on heavily relying on the server response code:
    //   - a 304 response indicates the local schedule is already up-to-date
    //     and, as a consequence, nothing needs to be done locally. In that
    //     case, the sync stops immediately;
    //   - a 200 response code indicates the local schedule is outdated. The
    //     code consists on removing everything that was persisted locally
    //     and insert the newly retrieved content from the server.
    //   - other response codes are treated as errors
    //
    // Obviously, this algorithm could be improved greatly by doing diffs
    // which would avoid unnecessary insert/delete operations. The main
    // advantage of the current implementation is it keeps the code as
    // simple as possible.

    companion object {
        private const val TAG = "SyncService"

        private val AMOUNT = TimeUnit.MINUTES.toMillis(10)

        private var lastSync = -1L

        fun sync(context: Context) {
            if (lastSync > 0 && (System.currentTimeMillis() - lastSync) <= AMOUNT) {
                // No need to sync now as it's been done recently
                // Let's skip the request
                return
            }
            context.startService(Intent(context, SyncService::class.java))
        }
    }

    private interface ScheduleApi {

        interface Headers {
            companion object {
                const val ETAG = "etag"
                const val IF_NONE_MATCH = "If-None-Match"
            }
        }

        interface ResponseCodes {
            companion object {
                const val OK = 200
                const val NO_CONTENT = 304
            }
        }

        @GET("schedule")
        fun getSchedule(@Header(Headers.IF_NONE_MATCH) etag: String): Call<Schedule>
    }

    override fun onHandleIntent(intent: Intent) {
        val api: ScheduleApi = Retrofit.Builder()
                .baseUrl(AppConfig.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(JsonConverters.main))
                .build()
                .create(ScheduleApi::class.java)

        try {
            val prefs = PreferencesManager.from(this)
            val etag = prefs.scheduleETag ?: AppConfig.SEED_ETAG
            val response = api.getSchedule(etag).execute()
            val code = response.code()
            when (code) {
                ScheduleApi.ResponseCodes.OK -> {
                    contentResolver.applyBatch(ScheduleContract.AUTHORITY, buildOperations(response.body()!!))
                    lastSync = System.currentTimeMillis()
                    prefs.scheduleETag = response.headers()[ScheduleApi.Headers.ETAG]
                }
                ScheduleApi.ResponseCodes.NO_CONTENT -> return
                else -> {
                    FirebaseCrash.log("Unknown response code $code received while syncing")
                }
            }
        } catch (ioe: IOException) {
            Log.e(TAG, "A network error occurred while syncing schedule", ioe)
        } catch (re: RuntimeException) {
            Log.e(TAG, "An error occurred while syncing schedule", re)
            FirebaseCrash.report(re)
        }
    }

    private fun buildOperations(schedule: Schedule) = ArrayList<ContentProviderOperation>().apply {
        add(ContentProviderOperation.newDelete(ScheduleContract.Rooms.CONTENT_URI).build())
        add(ContentProviderOperation.newDelete(ScheduleContract.Sessions.CONTENT_URI).build())
        add(ContentProviderOperation.newDelete(ScheduleContract.SessionsSpeakers.CONTENT_URI).build())
        add(ContentProviderOperation.newDelete(ScheduleContract.Speakers.CONTENT_URI).build())

        schedule.rooms.forEach {
            add(ContentProviderOperation.newInsert(ScheduleContract.Rooms.CONTENT_URI)
                    .withValues(it.toContentValues())
                    .build())
        }

        schedule.speakers.forEach {
            add(ContentProviderOperation.newInsert(ScheduleContract.Speakers.CONTENT_URI)
                    .withValues(it.toContentValues())
                    .build())
        }

        schedule.sessions.forEach { session ->
            add(ContentProviderOperation.newInsert(ScheduleContract.Sessions.CONTENT_URI)
                    .withValues(session.toContentValues())
                    .build())

            session.speakersIds?.forEach { speakerId ->
                val values = ContentValues().apply {
                    put(ScheduleContract.SessionsSpeakers.SESSION_SPEAKER_SESSION_ID, session.id)
                    put(ScheduleContract.SessionsSpeakers.SESSION_SPEAKER_SPEAKER_ID, speakerId)
                }
                add(ContentProviderOperation.newInsert(ScheduleContract.SessionsSpeakers.CONTENT_URI)
                        .withValues(values)
                        .build())
            }
        }
    }

}