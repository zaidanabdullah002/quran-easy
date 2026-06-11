package com.zaidan.quraneasy.feature.feeling.data

import android.content.Context
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingCatalog
import com.zaidan.quraneasy.feature.feeling.presentation.FeelingEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeelingLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun loadCatalog(): List<FeelingEntry> {
        val rawJson = context.resources
            .openRawResource(com.zaidan.quraneasy.feature_feeling.R.raw.feelings)
            .bufferedReader()
            .use { it.readText() }
        return FeelingCatalog.load(rawJson)
    }
}

