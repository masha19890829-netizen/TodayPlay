package com.todayplay.app.data

import android.content.Context
import com.todayplay.app.model.CheckInTask
import com.todayplay.app.model.ContentComplianceNote
import com.todayplay.app.model.ContentSource
import com.todayplay.app.model.ExternalMapAction
import com.todayplay.app.model.FeedbackReason
import com.todayplay.app.model.GroupPreference
import com.todayplay.app.model.ItineraryPlan
import com.todayplay.app.model.POI
import com.todayplay.app.model.Quest
import com.todayplay.app.model.QuestProgress
import com.todayplay.app.model.QuestRecord
import com.todayplay.app.model.QuestTask
import com.todayplay.app.model.RewardPoint
import com.todayplay.app.model.RouteStop
import com.todayplay.app.model.RouteStopRestoreSnapshot
import com.todayplay.app.model.SourcePolicy
import com.todayplay.app.model.TaskStatus
import com.todayplay.app.model.UserPreference
import org.json.JSONArray
import org.json.JSONObject

class QuestHistoryStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun loadRecords(): List<QuestRecord> {
        val raw = preferences.getString(KEY_RECORDS, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    add(array.getJSONObject(index).toQuestRecord())
                }
            }
        }.getOrElse { emptyList() }
    }

    fun saveRecords(records: List<QuestRecord>) {
        val array = JSONArray()
        records.take(MAX_RECORDS).forEach { record ->
            array.put(record.toJson())
        }
        preferences.edit().putString(KEY_RECORDS, array.toString()).apply()
    }

    fun clearRecords() {
        preferences.edit().remove(KEY_RECORDS).apply()
    }

    private fun QuestRecord.toJson(): JSONObject {
        return JSONObject()
            .put("quest", quest.toJson())
            .put("progress", progress.toJson())
    }

    private fun JSONObject.toQuestRecord(): QuestRecord {
        return QuestRecord(
            quest = getJSONObject("quest").toQuest(),
            progress = optJSONObject("progress")?.toQuestProgress() ?: QuestProgress(),
        )
    }

    private fun Quest.toJson(): JSONObject {
        return JSONObject()
            .put("questId", questId)
            .put("createdAt", createdAt)
            .put("title", title)
            .put("storySetup", storySetup)
            .put("relationship", relationship)
            .putNullable("city", city)
            .put("duration", duration)
            .put("budget", budget)
            .put("tags", tags.toStringJsonArray())
            .put("tasks", tasks.toTaskJsonArray())
            .put("hiddenTask", hiddenTask.toJson())
            .put("conversationPrompts", conversationPrompts.toStringJsonArray())
            .put("photoMission", photoMission)
            .put("endingRitual", endingRitual)
            .put("completionTitle", completionTitle)
            .put("completionKeywords", completionKeywords.toStringJsonArray())
            .put("completionSummary", completionSummary)
            .putNullable("itineraryPlan", itineraryPlan?.toJson())
    }

    private fun JSONObject.toQuest(): Quest {
        return Quest(
            questId = optString("questId").ifBlank { "legacy-${optString("title").hashCode()}-${optLong("createdAt", 0L)}" },
            createdAt = optLong("createdAt", System.currentTimeMillis()),
            title = optString("title"),
            storySetup = optString("storySetup"),
            relationship = optString("relationship"),
            city = nullableString("city"),
            duration = optString("duration"),
            budget = optString("budget"),
            tags = optJSONArray("tags").toStringList(),
            tasks = optJSONArray("tasks").toTaskList(),
            hiddenTask = getJSONObject("hiddenTask").toQuestTask(),
            conversationPrompts = optJSONArray("conversationPrompts").toStringList(),
            photoMission = optString("photoMission"),
            endingRitual = optString("endingRitual"),
            completionTitle = optString("completionTitle"),
            completionKeywords = optJSONArray("completionKeywords").toStringList(),
            completionSummary = optString("completionSummary"),
            itineraryPlan = optJSONObject("itineraryPlan")?.toItineraryPlan(),
        )
    }

    private fun QuestTask.toJson(): JSONObject {
        return JSONObject()
            .put("taskId", taskId)
            .put("title", title)
            .put("description", description)
            .put("howToComplete", howToComplete)
            .put("cuteTip", cuteTip)
            .put("estimatedTime", estimatedTime)
            .put("difficultyHearts", difficultyHearts)
    }

    private fun JSONObject.toQuestTask(): QuestTask {
        return QuestTask(
            title = optString("title"),
            description = optString("description"),
            howToComplete = optString("howToComplete"),
            cuteTip = optString("cuteTip"),
            estimatedTime = optString("estimatedTime"),
            difficultyHearts = optInt("difficultyHearts", 1),
            taskId = optString("taskId").ifBlank { "legacy-task-${optString("title").hashCode()}" },
        )
    }

    private fun QuestProgress.toJson(): JSONObject {
        val statuses = JSONObject()
        taskStatuses.forEach { (taskId, status) ->
            statuses.put(taskId, status.name)
        }

        val feedback = JSONObject()
        feedbackReasons.forEach { (taskId, reasons) ->
            val array = JSONArray()
            reasons.forEach { reason -> array.put(reason.name) }
            feedback.put(taskId, array)
        }

        return JSONObject()
            .put("taskStatuses", statuses)
            .put("feedbackReasons", feedback)
            .put("rewardPoints", rewardPoints.toRewardPointJsonArray())
            .put("localPhotoPreviewUris", localPhotoPreviewUris.toJsonObject())
            .putNullable("lastRouteStopRestore", lastRouteStopRestore?.toJson())
            .put("updatedAt", updatedAt)
            .putNullable("completedAt", completedAt)
    }

    private fun JSONObject.toQuestProgress(): QuestProgress {
        val statusesJson = optJSONObject("taskStatuses") ?: JSONObject()
        val statuses = buildMap {
            statusesJson.keys().forEach { taskId ->
                val status = runCatching { TaskStatus.valueOf(statusesJson.optString(taskId)) }.getOrNull()
                if (status != null) put(taskId, status)
            }
        }

        val feedbackJson = optJSONObject("feedbackReasons") ?: JSONObject()
        val feedback = buildMap {
            feedbackJson.keys().forEach { taskId ->
                val array = feedbackJson.optJSONArray(taskId) ?: JSONArray()
                val reasons = buildSet {
                    for (index in 0 until array.length()) {
                        FeedbackReason.fromStoredValue(array.optString(index))?.let { add(it) }
                    }
                }
                if (reasons.isNotEmpty()) put(taskId, reasons)
            }
        }

        return QuestProgress(
            taskStatuses = statuses,
            feedbackReasons = feedback,
            rewardPoints = optJSONArray("rewardPoints").toRewardPointList(),
            localPhotoPreviewUris = optJSONObject("localPhotoPreviewUris").toStringMap(),
            lastRouteStopRestore = optJSONObject("lastRouteStopRestore")?.toRouteStopRestoreSnapshot(),
            updatedAt = optLong("updatedAt", System.currentTimeMillis()),
            completedAt = nullableLong("completedAt"),
        )
    }

    private fun ItineraryPlan.toJson(): JSONObject {
        return JSONObject()
            .put("planId", planId)
            .put("planType", planType)
            .put("title", title)
            .put("city", city)
            .put("relationshipType", relationshipType)
            .put("groupPreference", groupPreference.toJson())
            .put("stops", stops.toRouteStopJsonArray())
            .put("estimatedCost", estimatedCost)
            .put("estimatedDuration", estimatedDuration)
            .put("routeSummary", routeSummary)
            .put("crowdRisk", crowdRisk)
            .put("rainBackup", rainBackup)
            .put("bestPhotoTime", bestPhotoTime)
            .put("rewardPolicy", rewardPolicy)
            .put("complianceNote", complianceNote.toJson())
            .put("marketCoverageNote", marketCoverageNote)
            .put("candidateRouteCount", candidateRouteCount)
    }

    private fun JSONObject.toItineraryPlan(): ItineraryPlan {
        return ItineraryPlan(
            planId = optString("planId"),
            planType = optString("planType"),
            title = optString("title"),
            city = optString("city"),
            relationshipType = optString("relationshipType"),
            groupPreference = getJSONObject("groupPreference").toGroupPreference(),
            stops = optJSONArray("stops").toRouteStopList(),
            estimatedCost = optString("estimatedCost"),
            estimatedDuration = optString("estimatedDuration"),
            routeSummary = optString("routeSummary"),
            crowdRisk = optString("crowdRisk"),
            rainBackup = optString("rainBackup"),
            bestPhotoTime = optString("bestPhotoTime"),
            rewardPolicy = optString("rewardPolicy"),
            complianceNote = getJSONObject("complianceNote").toContentComplianceNote(),
            marketCoverageNote = optString("marketCoverageNote", "Local mock data only. Production coverage requires official map, merchant, and backend content providers."),
            candidateRouteCount = optInt("candidateRouteCount", 1),
        )
    }

    private fun UserPreference.toJson(): JSONObject {
        return JSONObject()
            .put("userLabel", userLabel)
            .put("relationship", relationship)
            .put("city", city)
            .put("timeWindow", timeWindow)
            .put("budget", budget)
            .put("transportMode", transportMode)
            .put("interests", interests.toStringJsonArray())
            .put("pace", pace)
            .putNullable("foodPreference", foodPreference)
            .putNullable("photoPreference", photoPreference)
    }

    private fun JSONObject.toUserPreference(): UserPreference {
        return UserPreference(
            userLabel = optString("userLabel"),
            relationship = optString("relationship"),
            city = optString("city"),
            timeWindow = optString("timeWindow"),
            budget = optString("budget"),
            transportMode = optString("transportMode"),
            interests = optJSONArray("interests").toStringList(),
            pace = optString("pace"),
            foodPreference = nullableString("foodPreference"),
            photoPreference = nullableString("photoPreference"),
        )
    }

    private fun GroupPreference.toJson(): JSONObject {
        return JSONObject()
            .put("groupLabel", groupLabel)
            .put("members", members.toUserPreferenceJsonArray())
            .put("mergedCity", mergedCity)
            .put("mergedBudget", mergedBudget)
            .put("mergedTimeWindow", mergedTimeWindow)
            .put("mergedTransportMode", mergedTransportMode)
            .put("mergedInterests", mergedInterests.toStringJsonArray())
            .put("relationshipType", relationshipType)
            .put("conflictNotes", conflictNotes.toStringJsonArray())
    }

    private fun JSONObject.toGroupPreference(): GroupPreference {
        return GroupPreference(
            groupLabel = optString("groupLabel"),
            members = optJSONArray("members").toUserPreferenceList(),
            mergedCity = optString("mergedCity"),
            mergedBudget = optString("mergedBudget"),
            mergedTimeWindow = optString("mergedTimeWindow"),
            mergedTransportMode = optString("mergedTransportMode"),
            mergedInterests = optJSONArray("mergedInterests").toStringList(),
            relationshipType = optString("relationshipType"),
            conflictNotes = optJSONArray("conflictNotes").toStringList(),
        )
    }

    private fun RouteStop.toJson(): JSONObject {
        return JSONObject()
            .put("stopId", stopId)
            .put("order", order)
            .put("poi", poi.toJson())
            .put("startTimeHint", startTimeHint)
            .put("stayMinutes", stayMinutes)
            .put("checkInTask", checkInTask.toJson())
            .put("photoSuggestion", photoSuggestion)
            .put("spendingSuggestion", spendingSuggestion)
            .put("backupPlan", backupPlan)
            .put("navigationAction", navigationAction.toJson())
            .put("whyForGroup", whyForGroup)
    }

    private fun JSONObject.toRouteStop(): RouteStop {
        return RouteStop(
            stopId = optString("stopId"),
            order = optInt("order"),
            poi = getJSONObject("poi").toPoi(),
            startTimeHint = optString("startTimeHint"),
            stayMinutes = optInt("stayMinutes"),
            checkInTask = getJSONObject("checkInTask").toCheckInTask(),
            photoSuggestion = optString("photoSuggestion"),
            spendingSuggestion = optString("spendingSuggestion"),
            backupPlan = optString("backupPlan"),
            navigationAction = getJSONObject("navigationAction").toExternalMapAction(),
            whyForGroup = optString("whyForGroup"),
        )
    }

    private fun POI.toJson(): JSONObject {
        return JSONObject()
            .put("poiId", poiId)
            .put("name", name)
            .put("country", country)
            .put("city", city)
            .put("district", district)
            .put("address", address)
            .put("latitude", latitude)
            .put("longitude", longitude)
            .put("tags", tags.toStringJsonArray())
            .put("suitableRelationships", suitableRelationships.toStringJsonArray())
            .put("budgetLevel", budgetLevel)
            .put("estimatedStayMinutes", estimatedStayMinutes)
            .put("imagePlaceholder", imagePlaceholder)
            .put("recommendationReason", recommendationReason)
            .put("contentSource", contentSource.toJson())
            .put("riskTips", riskTips.toStringJsonArray())
            .put("globalCategory", globalCategory)
            .put("dataFreshness", dataFreshness)
            .put("requiresOfficialVerification", requiresOfficialVerification)
    }

    private fun JSONObject.toPoi(): POI {
        return POI(
            poiId = optString("poiId"),
            name = optString("name"),
            country = optString("country", "China"),
            city = optString("city"),
            district = optString("district"),
            address = optString("address"),
            latitude = optDouble("latitude"),
            longitude = optDouble("longitude"),
            tags = optJSONArray("tags").toStringList(),
            suitableRelationships = optJSONArray("suitableRelationships").toStringList(),
            budgetLevel = optString("budgetLevel"),
            estimatedStayMinutes = optInt("estimatedStayMinutes"),
            imagePlaceholder = optString("imagePlaceholder"),
            recommendationReason = optString("recommendationReason"),
            contentSource = getJSONObject("contentSource").toContentSource(),
            riskTips = optJSONArray("riskTips").toStringList(),
            globalCategory = optString("globalCategory", "local_highlight"),
            dataFreshness = optString("dataFreshness", "mock_curated"),
            requiresOfficialVerification = optBoolean("requiresOfficialVerification", true),
        )
    }

    private fun CheckInTask.toJson(): JSONObject {
        return JSONObject()
            .put("taskId", taskId)
            .put("title", title)
            .put("description", description)
            .put("rewardPoints", rewardPoints)
            .put("requiresPhoto", requiresPhoto)
            .put("manualCheckInAllowed", manualCheckInAllowed)
    }

    private fun JSONObject.toCheckInTask(): CheckInTask {
        return CheckInTask(
            taskId = optString("taskId"),
            title = optString("title"),
            description = optString("description"),
            rewardPoints = optInt("rewardPoints"),
            requiresPhoto = optBoolean("requiresPhoto"),
            manualCheckInAllowed = optBoolean("manualCheckInAllowed", true),
        )
    }

    private fun ExternalMapAction.toJson(): JSONObject {
        return JSONObject()
            .put("destinationName", destinationName)
            .put("latitude", latitude)
            .put("longitude", longitude)
            .put("address", address)
            .put("amapUri", amapUri)
            .put("fallbackUri", fallbackUri)
    }

    private fun JSONObject.toExternalMapAction(): ExternalMapAction {
        return ExternalMapAction(
            destinationName = optString("destinationName"),
            latitude = optDouble("latitude"),
            longitude = optDouble("longitude"),
            address = optString("address"),
            amapUri = optString("amapUri"),
            fallbackUri = optString("fallbackUri"),
        )
    }

    private fun ContentSource.toJson(): JSONObject {
        return JSONObject()
            .put("sourceType", sourceType)
            .put("sourceName", sourceName)
            .put("sourceLabel", sourceLabel)
            .put("isMock", isMock)
            .put("sourcePolicy", sourcePolicy.toJson())
    }

    private fun JSONObject.toContentSource(): ContentSource {
        return ContentSource(
            sourceType = optString("sourceType"),
            sourceName = optString("sourceName"),
            sourceLabel = optString("sourceLabel"),
            isMock = optBoolean("isMock", true),
            sourcePolicy = getJSONObject("sourcePolicy").toSourcePolicy(),
        )
    }

    private fun SourcePolicy.toJson(): JSONObject {
        return JSONObject()
            .put("policyTitle", policyTitle)
            .put("policyNotes", policyNotes.toStringJsonArray())
    }

    private fun JSONObject.toSourcePolicy(): SourcePolicy {
        return SourcePolicy(
            policyTitle = optString("policyTitle"),
            policyNotes = optJSONArray("policyNotes").toStringList(),
        )
    }

    private fun ContentComplianceNote.toJson(): JSONObject {
        return JSONObject()
            .put("summary", summary)
            .put("sourcePolicy", sourcePolicy.toJson())
            .put("limitations", limitations.toStringJsonArray())
    }

    private fun JSONObject.toContentComplianceNote(): ContentComplianceNote {
        return ContentComplianceNote(
            summary = optString("summary"),
            sourcePolicy = getJSONObject("sourcePolicy").toSourcePolicy(),
            limitations = optJSONArray("limitations").toStringList(),
        )
    }

    private fun RewardPoint.toJson(): JSONObject {
        return JSONObject()
            .put("rewardId", rewardId)
            .put("questId", questId)
            .put("taskId", taskId)
            .put("points", points)
            .put("reason", reason)
            .put("createdAt", createdAt)
    }

    private fun JSONObject.toRewardPoint(): RewardPoint {
        return RewardPoint(
            rewardId = optString("rewardId"),
            questId = optString("questId"),
            taskId = optString("taskId"),
            points = optInt("points"),
            reason = optString("reason"),
            createdAt = optLong("createdAt", System.currentTimeMillis()),
        )
    }

    private fun RouteStopRestoreSnapshot.toJson(): JSONObject {
        val feedback = JSONArray()
        restoredFeedbackReasons.forEach { reason -> feedback.put(reason.name) }
        return JSONObject()
            .put("stopId", stopId)
            .put("order", order)
            .put("previousStop", previousStop.toJson())
            .putNullable("restoredStatus", restoredStatus?.name)
            .put("restoredFeedbackReasons", feedback)
            .put("restoredRewardPoints", restoredRewardPoints.toRewardPointJsonArray())
            .put("replacedStopName", replacedStopName)
            .put("createdAt", createdAt)
    }

    private fun JSONObject.toRouteStopRestoreSnapshot(): RouteStopRestoreSnapshot {
        val feedbackArray = optJSONArray("restoredFeedbackReasons") ?: JSONArray()
        val feedback = buildSet {
            for (index in 0 until feedbackArray.length()) {
                FeedbackReason.fromStoredValue(feedbackArray.optString(index))?.let { add(it) }
            }
        }
        val status = nullableString("restoredStatus")?.let { stored ->
            runCatching { TaskStatus.valueOf(stored) }.getOrNull()
        }
        return RouteStopRestoreSnapshot(
            stopId = optString("stopId"),
            order = optInt("order"),
            previousStop = getJSONObject("previousStop").toRouteStop(),
            restoredStatus = status,
            restoredFeedbackReasons = feedback,
            restoredRewardPoints = optJSONArray("restoredRewardPoints").toRewardPointList(),
            replacedStopName = optString("replacedStopName"),
            createdAt = optLong("createdAt", System.currentTimeMillis()),
        )
    }

    private fun List<String>.toStringJsonArray(): JSONArray {
        val array = JSONArray()
        forEach { array.put(it) }
        return array
    }

    private fun List<QuestTask>.toTaskJsonArray(): JSONArray {
        val array = JSONArray()
        forEach { array.put(it.toJson()) }
        return array
    }

    private fun List<RouteStop>.toRouteStopJsonArray(): JSONArray {
        val array = JSONArray()
        forEach { array.put(it.toJson()) }
        return array
    }

    private fun List<UserPreference>.toUserPreferenceJsonArray(): JSONArray {
        val array = JSONArray()
        forEach { array.put(it.toJson()) }
        return array
    }

    private fun List<RewardPoint>.toRewardPointJsonArray(): JSONArray {
        val array = JSONArray()
        forEach { array.put(it.toJson()) }
        return array
    }

    private fun Map<String, String>.toJsonObject(): JSONObject {
        val json = JSONObject()
        forEach { (key, value) -> json.put(key, value) }
        return json
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                add(optString(index))
            }
        }
    }

    private fun JSONArray?.toTaskList(): List<QuestTask> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                add(getJSONObject(index).toQuestTask())
            }
        }
    }

    private fun JSONArray?.toRouteStopList(): List<RouteStop> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                add(getJSONObject(index).toRouteStop())
            }
        }
    }

    private fun JSONArray?.toUserPreferenceList(): List<UserPreference> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                add(getJSONObject(index).toUserPreference())
            }
        }
    }

    private fun JSONArray?.toRewardPointList(): List<RewardPoint> {
        if (this == null) return emptyList()
        return buildList {
            for (index in 0 until length()) {
                add(getJSONObject(index).toRewardPoint())
            }
        }
    }

    private fun JSONObject?.toStringMap(): Map<String, String> {
        if (this == null) return emptyMap()
        return buildMap {
            keys().forEach { key ->
                put(key, optString(key))
            }
        }
    }

    private fun JSONObject.putNullable(key: String, value: String?): JSONObject {
        return put(key, value ?: JSONObject.NULL)
    }

    private fun JSONObject.putNullable(key: String, value: Long?): JSONObject {
        return put(key, value ?: JSONObject.NULL)
    }

    private fun JSONObject.putNullable(key: String, value: JSONObject?): JSONObject {
        return put(key, value ?: JSONObject.NULL)
    }

    private fun JSONObject.nullableString(key: String): String? {
        return if (has(key) && !isNull(key)) optString(key) else null
    }

    private fun JSONObject.nullableLong(key: String): Long? {
        return if (has(key) && !isNull(key)) optLong(key) else null
    }

    private companion object {
        const val PREFERENCES_NAME = "today_play_history"
        const val KEY_RECORDS = "quest_records"
        const val MAX_RECORDS = 50
    }
}
