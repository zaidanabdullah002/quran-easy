package com.zaidan.quraneasy.feature.quran.data.local

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuranPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val KEY_SELECTED_TAB = "quran_selected_tab"
    }

    fun getSelectedTab(): Int =
        sharedPreferences.getInt(KEY_SELECTED_TAB, 0)

    fun saveSelectedTab(tab: Int) {
        sharedPreferences.edit()
            .putInt(KEY_SELECTED_TAB, tab)
            .apply()
    }
}
