package com.todayplay.app.generator

import com.todayplay.app.model.QuestInput

internal fun QuestInput.intentMarker(key: String): String? {
    val markerPrefix = "$key="
    return note
        ?.lineSequence()
        ?.map { it.trim() }
        ?.firstOrNull { it.startsWith(markerPrefix) }
        ?.substringAfter(markerPrefix)
        ?.trim()
        ?.takeIf { it.isNotBlank() }
}
