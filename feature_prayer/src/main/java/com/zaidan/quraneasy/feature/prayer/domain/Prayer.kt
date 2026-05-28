package com.zaidan.quraneasy.feature.prayer.domain

data class Prayer(
    val name: String,
    val time: String,
    val completed: Boolean = false
)