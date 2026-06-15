package com.todayplay.app.generator

import com.todayplay.app.model.Quest
import com.todayplay.app.model.QuestInput
import com.todayplay.app.model.QuestRecord
import com.todayplay.app.model.RouteStopReplacementPreview

interface QuestGenerator {
    fun generate(input: QuestInput): Quest

    fun previewRouteStopReplacement(record: QuestRecord, stopId: String, localeCode: String): RouteStopReplacementPreview? = null

    fun replaceRouteStop(record: QuestRecord, stopId: String, localeCode: String): QuestRecord? = null

    fun restoreRouteStop(record: QuestRecord, localeCode: String): QuestRecord? = null
}
