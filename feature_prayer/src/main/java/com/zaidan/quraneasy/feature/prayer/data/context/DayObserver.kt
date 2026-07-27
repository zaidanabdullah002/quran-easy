package com.zaidan.quraneasy.feature.prayer.data.context

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

interface DayObserver {
    fun observeDate(): Flow<String>
}

@Singleton
class AndroidDayObserver @Inject constructor(
    @ApplicationContext private val context: Context
) : DayObserver {
    override fun observeDate(): Flow<String> = callbackFlow {
        fun emitToday() {
            trySend(SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date()))
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) = emitToday()
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        emitToday()

        // The broadcasts are immediate; this inexpensive guard also covers devices that defer them.
        val guard = launch {
            while (isActive) {
                delay(30_000)
                emitToday()
            }
        }
        awaitClose {
            guard.cancel()
            context.unregisterReceiver(receiver)
        }
    }.distinctUntilChanged()
}
