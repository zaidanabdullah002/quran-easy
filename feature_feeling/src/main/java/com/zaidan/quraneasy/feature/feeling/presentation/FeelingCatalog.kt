package com.zaidan.quraneasy.feature.feeling.presentation

import org.json.JSONArray
import org.json.JSONObject

data class FeelingEntry(
    val category: FeelingCategory,
    val id: String,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val verses: List<VerseRef>
)

object FeelingCatalog {
    const val defaultFeelingId = "hope"

    fun load(rawJson: String): List<FeelingEntry> {
        val array = JSONArray(rawJson)
        return buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(item.toEntry())
            }
        }
    }

    private fun JSONObject.toEntry(): FeelingEntry {
        val versesArray = getJSONArray("verses")
        val verses = buildList {
            for (index in 0 until versesArray.length()) {
                val verse = versesArray.getJSONObject(index)
                add(
                    VerseRef(
                        surah = verse.getInt("surah"),
                        ayah = verse.getInt("ayah")
                    )
                )
            }
        }
        return FeelingEntry(
            category = FeelingCategory.valueOf(optString("category", FeelingCategory.Feeling.name)),
            id = getString("id"),
            emoji = getString("emoji"),
            title = getString("title"),
            subtitle = getString("subtitle"),
            verses = verses
        )
    }
}
