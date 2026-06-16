package com.todayplay.app.generator

import com.todayplay.app.model.Quest
import com.todayplay.app.model.QuestInput
import com.todayplay.app.model.RouteStop
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AiQuestGenerator(
    private val gatewayUrl: String,
    private val fallback: LocalQuestGenerator = LocalQuestGenerator(),
) : QuestGenerator {
    override fun generate(input: QuestInput): Quest {
        val localQuest = fallback.generate(input)
        val endpoint = gatewayUrl.normalizedGatewayEndpoint() ?: return localQuest.withFallbackTag()
        val plan = localQuest.itineraryPlan ?: return localQuest.withFallbackTag()
        val stops = plan.stops
        if (stops.isEmpty()) return localQuest.withFallbackTag()

        val payload = buildPayload(input, stops)
        val response = runCatching { requestGateway(endpoint, payload) }.getOrNull()
            ?: return localQuest.withFallbackTag()
        if (response.optBoolean("usedFallback", true)) return localQuest.withFallbackTag()

        val selectedStopIds = response.optJSONArray("selectedStopIds").toStringList()
            .filter { id -> stops.any { stop -> stop.stopId == id } }
            .take(4)
        if (selectedStopIds.isEmpty()) return localQuest.withFallbackTag()

        val stopReasons = response.optJSONObject("stopReasons") ?: JSONObject()
        val reorderedStops = selectedStopIds.mapNotNull { id ->
            stops.firstOrNull { stop -> stop.stopId == id }
        }.ifEmpty { stops }
        val updatedStops = reorderedStops.map { stop ->
            val aiReason = stopReasons.optString(stop.stopId).trim()
            if (aiReason.isNotBlank()) stop.copy(whyForGroup = aiReason.take(140)) else stop
        }
        val aiTitle = response.optString("title").trim().takeIf { it.isNotBlank() }?.take(42)
        val aiStory = response.optString("storySetup").trim().takeIf { it.isNotBlank() }?.take(180)
        val aiSummary = response.optString("routeSummary").trim().takeIf { it.isNotBlank() }?.take(180)
        val whyReasons = response.optJSONArray("whyReasons").toStringList().map { it.take(80) }.take(3)
        val updatedPlan = plan.copy(
            title = aiTitle ?: plan.title,
            stops = updatedStops,
            routeSummary = aiSummary ?: plan.routeSummary,
            candidateRouteCount = stops.size,
            marketCoverageNote = "AI assisted from local candidate stops. POI data is still a local sample and needs official verification before launch.",
        )

        return localQuest.copy(
            title = aiTitle ?: localQuest.title,
            storySetup = aiStory ?: localQuest.storySetup,
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
            connectTimeout = 7000
            readTimeout = 10000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
        }
        connection.outputStream.use { stream ->
            stream.write(payload.toString().toByteArray(Charsets.UTF_8))
        }
        val code = connection.responseCode
        val body = if (code in 200..299) {
            connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } else {
            connection.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
        }
        connection.disconnect()
        if (code !in 200..299) return JSONObject().put("usedFallback", true).put("reason", "gateway_http_$code")
        return JSONObject(body)
    }

    private fun Quest.withFallbackTag(): Quest {
        return copy(
            tags = (tags + "本地兜底").distinct().take(6),
            itineraryPlan = itineraryPlan?.copy(
                marketCoverageNote = "Local fallback route. AI gateway was not configured or did not return a valid same-city route.",
            ),
        )
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
}
