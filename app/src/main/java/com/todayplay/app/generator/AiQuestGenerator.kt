package com.todayplay.app.generator

import com.todayplay.app.model.Quest
import com.todayplay.app.model.QuestInput
import com.todayplay.app.model.RouteStop
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class AiQuestGenerator(
    private val gatewayUrl: String,
    private val fallback: LocalQuestGenerator = LocalQuestGenerator(),
) : QuestGenerator {
    private val itineraryGenerator = LocalItineraryGenerator()

    override fun generate(input: QuestInput): Quest {
        val localQuest = fallback.generate(input)
        val endpoint = gatewayUrl.normalizedGatewayEndpoint() ?: return localQuest.withFallbackTag()
        val plan = localQuest.itineraryPlan ?: return localQuest.withFallbackTag()
        val candidateStops = runCatching {
            itineraryGenerator.generateDraft(input, localQuest.questId).candidateStops
        }.getOrElse { plan.stops }.ifEmpty { plan.stops }
        if (candidateStops.isEmpty()) return localQuest.withFallbackTag()

        val payload = buildPayload(input, candidateStops)
        val response = try {
            requestGateway(endpoint, payload)
        } catch (_: SocketTimeoutException) {
            return localQuest.withFallbackTag("gateway_timeout")
        } catch (_: Exception) {
            return localQuest.withFallbackTag("android_gateway_request_failed")
        }
        if (response.optBoolean("usedFallback", true)) {
            return localQuest.withFallbackTag(response.optString("reason").takeIf { it.isNotBlank() })
        }

        val selectedStopIds = response.optJSONArray("selectedStopIds").toStringList()
            .filter { id -> candidateStops.any { stop -> stop.stopId == id } }
            .take(4)
        if (selectedStopIds.isEmpty()) return localQuest.withFallbackTag()

        val stopReasons = response.optJSONObject("stopReasons") ?: JSONObject()
        val reorderedStops = selectedStopIds.mapNotNull { id ->
            candidateStops.firstOrNull { stop -> stop.stopId == id }
        }.ifEmpty { plan.stops }
        val updatedStops = reorderedStops.mapIndexed { index, stop ->
            val aiReason = stopReasons.optString(stop.stopId).trim()
            val orderedStop = stop.copy(order = index + 1)
            if (aiReason.isNotBlank()) orderedStop.copy(whyForGroup = aiReason.take(140)) else orderedStop
        }
        val aiTitle = response.optString("title").trim().takeIf { it.isNotBlank() }?.take(42)
        val aiStory = response.optString("storySetup").trim().takeIf { it.isNotBlank() }?.take(180)
        val aiSummary = response.optString("routeSummary").trim().takeIf { it.isNotBlank() }?.take(180)
        val intentTitle = input.intentMarker("TP_INTENT_TITLE")
        val intentSummary = input.intentMarker("TP_INTENT_SUMMARY")
        val whyReasons = response.optJSONArray("whyReasons").toStringList().map { it.take(80) }.take(3)
        val updatedPlan = plan.copy(
            title = intentTitle ?: aiTitle ?: plan.title,
            stops = updatedStops,
            routeSummary = intentSummary ?: aiSummary ?: plan.routeSummary,
            candidateRouteCount = candidateStops.size,
            marketCoverageNote = "AI selected from a same-city candidate pool. POI data is still a local sample and needs official verification before launch.",
        )

        return localQuest.copy(
            title = intentTitle ?: aiTitle ?: localQuest.title,
            storySetup = intentSummary ?: aiStory ?: localQuest.storySetup,
            tags = (listOf("AI个性化", "同城校验") + localQuest.tags).distinct().take(6),
            completionSummary = whyReasons.joinToString(" / ").ifBlank { localQuest.completionSummary },
            itineraryPlan = updatedPlan,
        )
    }

    override fun previewRouteStopReplacement(record: com.todayplay.app.model.QuestRecord, stopId: String, localeCode: String) =
        fallback.previewRouteStopReplacement(record, stopId, localeCode)

    override fun replaceRouteStop(record: com.todayplay.app.model.QuestRecord, stopId: String, localeCode: String) =
        fallback.replaceRouteStop(record, stopId, localeCode)

    override fun restoreRouteStop(record: com.todayplay.app.model.QuestRecord, localeCode: String) =
        fallback.restoreRouteStop(record, localeCode)

    private fun buildPayload(input: QuestInput, stops: List<RouteStop>): JSONObject {
        return JSONObject()
            .put("freeText", input.note.orEmpty().take(600))
            .put("city", input.city.orEmpty())
            .put("relationship", input.relationship)
            .put("moods", input.moods.toJsonArray())
            .put("timeBudget", input.time)
            .put("budget", input.budget)
            .put("transportMode", input.transportMode)
            .put("localeCode", input.localeCode)
            .put(
                "candidateStops",
                JSONArray().also { array ->
                    stops.take(12).forEach { stop ->
                        array.put(
                            JSONObject()
                                .put("stopId", stop.stopId)
                                .put("name", stop.poi.name)
                                .put("city", stop.poi.city)
                                .put("district", stop.poi.district)
                                .put("tags", stop.poi.tags.take(6).toJsonArray())
                                .put("budgetLevel", stop.poi.budgetLevel)
                                .put("stayMinutes", stop.stayMinutes),
                        )
                    }
                },
            )
    }

    private fun requestGateway(endpoint: String, payload: JSONObject): JSONObject {
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = FAST_GATEWAY_CONNECT_TIMEOUT_MS
            readTimeout = FAST_GATEWAY_READ_TIMEOUT_MS
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
        }
        try {
            connection.outputStream.use { stream ->
                stream.write(payload.toString().toByteArray(Charsets.UTF_8))
            }
            val code = connection.responseCode
            val body = if (code in 200..299) {
                connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            }
            if (code !in 200..299) return JSONObject().put("usedFallback", true).put("reason", "gateway_http_$code")
            return JSONObject(body)
        } finally {
            connection.disconnect()
        }
    }

    private fun Quest.withFallbackTag(reason: String? = null): Quest {
        return copy(
            tags = (tags + fallbackTagFor(reason)).distinct().take(6),
            itineraryPlan = itineraryPlan?.copy(
                marketCoverageNote = fallbackNoteFor(reason),
            ),
        )
    }

    private fun fallbackTagFor(reason: String?): String {
        return when {
            reason == null -> "本地兜底"
            reason.contains("429") -> "AI限流"
            reason.contains("timeout") -> "AI超时"
            else -> "本地兜底"
        }
    }

    private fun fallbackNoteFor(reason: String?): String {
        return when {
            reason == null -> "Local fallback route. AI gateway was not configured or did not return a valid same-city route."
            reason.contains("429") -> "AI gateway is rate-limited by Kimi right now; showing a same-city local sample route."
            reason.contains("timeout") -> "AI gateway timed out before returning a valid same-city route; showing a local sample route."
            else -> "AI gateway returned $reason; showing a same-city local sample route."
        }
    }

    private fun String.normalizedGatewayEndpoint(): String? {
        val trimmed = trim().trimEnd('/')
        if (!trimmed.startsWith("https://") && !trimmed.startsWith("http://10.0.2.2")) return null
        return "$trimmed/ai/route/generate"
    }

    private fun List<String>.toJsonArray(): JSONArray {
        val array = JSONArray()
        forEach { array.put(it) }
        return array
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                optString(index).takeIf { it.isNotBlank() }?.let { add(it) }
            }
        }
    }

    private companion object {
        const val FAST_GATEWAY_CONNECT_TIMEOUT_MS = 800
        const val FAST_GATEWAY_READ_TIMEOUT_MS = 2200
    }
}
