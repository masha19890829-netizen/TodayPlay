package com.todayplay.app.data

import com.todayplay.app.BuildConfig
import com.todayplay.app.model.ContentSource
import com.todayplay.app.model.POI
import com.todayplay.app.model.SourcePolicy
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject

data class TravelContentSearchResult(
    val pois: List<POI>,
    val sourcePolicy: SourcePolicy,
    val coverageNote: String,
    val isMock: Boolean,
)

interface TravelContentRepository {
    fun searchPois(
        cityOrQuery: String?,
        interests: List<String>,
        relationship: String,
    ): TravelContentSearchResult
}

object TravelContentConfig {
    val baseUrl: String
        get() = BuildConfig.TRAVEL_CONTENT_BASE_URL.trim()

    val isRemoteContentEnabled: Boolean
        get() = baseUrl.startsWith("https://")

    val availabilityNote: String
        get() = if (isRemoteContentEnabled) {
            "Remote travel content endpoint is configured. Responses still require backend source-policy verification."
        } else {
            "Global live search is not configured yet. The app is using a local mock catalog for route structure testing."
        }
}

object TravelContentRepositoryFactory {
    fun create(): TravelContentRepository {
        return if (TravelContentConfig.isRemoteContentEnabled) {
            RemoteTravelContentRepository(LocalMockTravelContentRepository())
        } else {
            LocalMockTravelContentRepository()
        }
    }
}

class LocalMockTravelContentRepository : TravelContentRepository {
    override fun searchPois(
        cityOrQuery: String?,
        interests: List<String>,
        relationship: String,
    ): TravelContentSearchResult {
        val pois = GlobalPoiMockData.search(
            cityOrQuery = cityOrQuery,
            interests = interests,
            relationship = relationship,
        )
        return TravelContentSearchResult(
            pois = pois,
            sourcePolicy = pois.firstOrNull()?.contentSource?.sourcePolicy ?: GlobalPoiMockData.productionSourcePolicy,
            coverageNote = TravelContentConfig.availabilityNote +
                " Production global search must use official APIs, licensed data, merchant partnerships, user-authorized links, or a backend aggregation service.",
            isMock = true,
        )
    }
}

class RemoteTravelContentRepository(
    private val fallback: TravelContentRepository,
    private val baseUrl: String = TravelContentConfig.baseUrl,
) : TravelContentRepository {
    override fun searchPois(
        cityOrQuery: String?,
        interests: List<String>,
        relationship: String,
    ): TravelContentSearchResult {
        val fallbackResult = fallback.searchPois(cityOrQuery, interests, relationship)
        val endpoint = baseUrl.trim().trimEnd('/')
        if (!endpoint.startsWith("https://")) {
            return fallbackResult.withRemoteFallback("Remote endpoint is not HTTPS.")
        }
        return runCatching {
            val response = postPoiSearch(endpoint, cityOrQuery, interests, relationship)
            val parsed = parsePoiSearchResponse(response)
            if (parsed.pois.isEmpty() || parsed.isMock) {
                fallbackResult.withRemoteFallback("Remote endpoint returned no verified production POIs.")
            } else {
                parsed
            }
        }.getOrElse { error ->
            fallbackResult.withRemoteFallback("Remote search failed safely: ${error.message ?: "unknown error"}.")
        }
    }

    private fun postPoiSearch(
        endpoint: String,
        cityOrQuery: String?,
        interests: List<String>,
        relationship: String,
    ): String {
        val url = URL("$endpoint/travel/poi/search")
        if (url.protocol != "https") {
            error("Remote travel content endpoint must use HTTPS.")
        }
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = REMOTE_CONNECT_TIMEOUT_MS
            readTimeout = REMOTE_READ_TIMEOUT_MS
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
            setRequestProperty("Accept", "application/json")
        }
        val requestBody = JSONObject()
            .put("cityOrQuery", cityOrQuery.orEmpty())
            .put("interests", JSONArray(interests))
            .put("relationshipType", relationship)
            .put("sourcePolicy", JSONObject().put("noScraping", true))
            .toString()
        connection.outputStream.use { stream ->
            stream.write(requestBody.toByteArray(Charsets.UTF_8))
        }
        val body = if (connection.responseCode in 200..299) {
            connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } else {
            connection.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
        }
        if (connection.responseCode !in 200..299) {
            error("HTTP ${connection.responseCode}: ${body.take(120)}")
        }
        return body
    }

    private fun parsePoiSearchResponse(responseBody: String): TravelContentSearchResult {
        val json = JSONObject(responseBody)
        val status = json.optString("status", "verified")
        if (status != "verified" && status != "ok") {
            error("Remote content status is $status.")
        }
        val policy = json.optJSONObject("sourcePolicy").toSourcePolicy()
        val pois = json.optJSONArray("pois").toPoiList(policy)
        val isMock = json.optBoolean("isMock", pois.any { it.contentSource.isMock })
        return TravelContentSearchResult(
            pois = pois,
            sourcePolicy = policy,
            coverageNote = json.optString(
                "coverageNote",
                "Remote travel content returned verified POI data. Continue checking provider freshness, attribution, and image license metadata.",
            ),
            isMock = isMock,
        )
    }

    private fun JSONObject?.toSourcePolicy(): SourcePolicy {
        if (this == null) {
            return SourcePolicy(
                policyTitle = "Remote source policy",
                policyNotes = listOf("Remote response did not include detailed source policy; local fallback should be preferred."),
            )
        }
        val notes = optJSONArray("policyNotes").toStringList()
            .ifEmpty { optJSONArray("allowedUses").toStringList() + optJSONArray("forbiddenUses").toStringList() }
            .ifEmpty { listOf("Remote provider returned source policy metadata.") }
        return SourcePolicy(
            policyTitle = optString("policyTitle", optString("sourceName", "Remote source policy")),
            policyNotes = notes,
        )
    }

    private fun JSONArray?.toPoiList(defaultPolicy: SourcePolicy): List<POI> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                optJSONObject(index)?.toPoi(defaultPolicy)?.let(::add)
            }
        }
    }

    private fun JSONObject.toPoi(defaultPolicy: SourcePolicy): POI? {
        val poiId = optString("poiId").takeIf { it.isNotBlank() } ?: return null
        val name = optString("name").takeIf { it.isNotBlank() } ?: return null
        val city = optString("city").takeIf { it.isNotBlank() } ?: return null
        val address = optString("address").takeIf { it.isNotBlank() } ?: return null
        val sourceJson = optJSONObject("contentSource")
        val sourcePolicy = sourceJson?.optJSONObject("sourcePolicy").toSourcePolicy()
            .takeIf { it.policyNotes.isNotEmpty() }
            ?: defaultPolicy
        val fallbackSourceLabel = sourceJson?.optString("sourceLabel")?.takeIf { it.isNotBlank() } ?: "Remote provider"
        val contentSource = ContentSource(
            sourceType = sourceJson?.optString("sourceType", "remote_provider") ?: "remote_provider",
            sourceName = sourceJson?.optString("sourceName", fallbackSourceLabel) ?: fallbackSourceLabel,
            sourceLabel = sourceJson?.optString("sourceLabel", "Remote provider") ?: "Remote provider",
            isMock = sourceJson?.optBoolean("mock", sourceJson.optBoolean("isMock", false)) ?: false,
            sourcePolicy = sourcePolicy,
        )
        return POI(
            poiId = poiId,
            name = name,
            country = optString("country", "Global"),
            city = city,
            district = optString("district", ""),
            address = address,
            latitude = optDouble("latitude"),
            longitude = optDouble("longitude"),
            tags = optJSONArray("tags").toStringList().ifEmpty { listOf("remote") },
            suitableRelationships = optJSONArray("suitableFor").toStringList()
                .ifEmpty { optJSONArray("suitableRelationships").toStringList() }
                .ifEmpty { listOf("group") },
            budgetLevel = optString("budgetLevel", "unknown"),
            estimatedStayMinutes = optInt("estimatedStayMinutes", 45).coerceAtLeast(1),
            imagePlaceholder = optString("imagePlaceholder", optJSONArray("imageAssets").firstImageAltText()),
            recommendationReason = optString("recommendationReason", "Remote provider recommendation."),
            contentSource = contentSource,
            riskTips = optJSONArray("riskTips").toStringList().ifEmpty { listOf("Verify opening hours, crowd level, and local conditions before departure.") },
            globalCategory = optString("globalCategory", "remote_highlight"),
            dataFreshness = optString("dataFreshness", optJSONObject("freshness")?.optString("retrievedAt") ?: "remote_verified"),
            requiresOfficialVerification = optBoolean("requiresOfficialVerification", true),
        )
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                optString(index).takeIf { it.isNotBlank() }?.let(::add)
            }
        }
    }

    private fun JSONArray?.firstImageAltText(): String {
        if (this == null || length() == 0) return "Remote provider image placeholder"
        return optJSONObject(0)?.optString("altText")?.takeIf { it.isNotBlank() }
            ?: "Remote provider image placeholder"
    }

    private fun TravelContentSearchResult.withRemoteFallback(reason: String): TravelContentSearchResult {
        return copy(
            coverageNote = "$reason Using local mock catalog until authenticated backend search, source policies, rate limits, and provider contracts are verified. ${coverageNote}",
            isMock = true,
        )
    }

    private companion object {
        const val REMOTE_CONNECT_TIMEOUT_MS = 2_500
        const val REMOTE_READ_TIMEOUT_MS = 3_500
    }
}
