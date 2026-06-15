package com.todayplay.app.data

import android.util.Log

object ProductEventLogger {
    fun track(name: String, properties: Map<String, String> = emptyMap()) {
        val payload = properties
            .filterKeys { it in SAFE_PROPERTY_KEYS }
            .entries
            .joinToString(prefix = "{", postfix = "}") { entry ->
                "${entry.key}=${entry.value.take(MAX_VALUE_LENGTH)}"
            }
        Log.d("TodayPlayEvent", "$name $payload")
    }

    private val SAFE_PROPERTY_KEYS = setOf(
        "source",
        "relationship",
        "time",
        "budget",
        "questId",
        "taskId",
        "status",
        "reason",
        "pack",
    )

    private const val MAX_VALUE_LENGTH = 48
}
