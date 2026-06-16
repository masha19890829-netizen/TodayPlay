package com.todayplay.app.generator

import com.todayplay.app.data.TravelContentRepository
import com.todayplay.app.data.TravelContentRepositoryFactory
import com.todayplay.app.model.CheckInTask
import com.todayplay.app.model.ContentComplianceNote
import com.todayplay.app.model.ExternalMapAction
import com.todayplay.app.model.GroupPreference
import com.todayplay.app.model.ItineraryPlan
import com.todayplay.app.model.POI
import com.todayplay.app.model.QuestInput
import com.todayplay.app.model.QuestRecord
import com.todayplay.app.model.RouteStop
import com.todayplay.app.model.RouteStopReplacementPreview
import com.todayplay.app.model.RouteStopRestoreSnapshot
import com.todayplay.app.model.SourcePolicy
import com.todayplay.app.model.UserPreference
import java.net.URLEncoder
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max

data class ItineraryDraft(
    val plan: ItineraryPlan,
    val candidateStops: List<RouteStop>,
    val candidatePoiCount: Int,
)

class LocalItineraryGenerator(
    private val contentRepository: TravelContentRepository = TravelContentRepositoryFactory.create(),
) {
    fun generate(input: QuestInput, questId: String): ItineraryPlan {
        return generateDraft(input, questId).plan
    }

    fun generateDraft(input: QuestInput, questId: String): ItineraryDraft {
        val copy = ItineraryCopy.forLocale(input.localeCode)
        val groupPreference = input.toGroupPreference(copy)
        val contentResult = contentRepository.searchPois(
            cityOrQuery = groupPreference.mergedCity,
            interests = groupPreference.mergedInterests,
            relationship = groupPreference.relationshipType,
        )
        val routeCity = resolveRouteCity(groupPreference.mergedCity, contentResult.pois)
        val routeGroupPreference = groupPreference.withRouteCity(routeCity)
        val candidates = contentResult.pois.sameCityOnly(routeCity)
        val rankedCandidates = rankCandidatesForInput(input, routeGroupPreference, candidates)
        val stopCount = stopCountFor(routeGroupPreference).coerceAtMost(candidates.size)
        val selectedPois = rankedCandidates.take(stopCount).map { copy.localizePoi(it) }
        val stops = selectedPois.mapIndexed { index, poi ->
            poi.toRouteStop(
                questId = questId,
                order = index + 1,
                groupPreference = routeGroupPreference,
                copy = copy,
                startTimeHint = startTimeHint(index, routeGroupPreference.mergedTimeWindow),
            )
        }
        val planType = copy.safePlanTypeFor(routeGroupPreference)
        val intentTitle = input.intentMarker("TP_INTENT_TITLE")
        val intentSummary = input.intentMarker("TP_INTENT_SUMMARY")
        val candidateStops = rankedCandidates.take(12).mapIndexed { index, poi ->
            copy.localizePoi(poi).toRouteStop(
                questId = questId,
                order = index + 1,
                groupPreference = routeGroupPreference,
                copy = copy,
                startTimeHint = startTimeHint(index, routeGroupPreference.mergedTimeWindow),
            )
        }

        val plan = ItineraryPlan(
            planId = "plan-$questId",
            planType = planType,
            title = intentTitle ?: copy.safeTitleFor(routeGroupPreference, selectedPois, planType),
            city = routeCity,
            relationshipType = routeGroupPreference.relationshipType,
            groupPreference = routeGroupPreference,
            stops = stops,
            estimatedCost = copy.safeEstimateCost(selectedPois, routeGroupPreference.mergedBudget),
            estimatedDuration = routeGroupPreference.mergedTimeWindow,
            routeSummary = intentSummary ?: copy.safeRouteSummaryFor(routeGroupPreference, selectedPois),
            crowdRisk = copy.safeCrowdRiskFor(selectedPois),
            rainBackup = copy.safeRainBackupFor(selectedPois),
            bestPhotoTime = copy.safeBestPhotoTimeFor(routeGroupPreference),
            rewardPolicy = copy.safeRewardPolicy,
            complianceNote = ContentComplianceNote(
                summary = copy.safeComplianceSummary,
                sourcePolicy = copy.safeLocalizeSourcePolicy(contentResult.sourcePolicy),
                limitations = copy.safeComplianceLimitations,
            ),
            marketCoverageNote = copy.safeCoverageNoteFor(
                isMock = contentResult.isMock,
                rawNote = contentResult.coverageNote,
            ),
            candidateRouteCount = candidates.size,
        )
        return ItineraryDraft(
            plan = plan,
            candidateStops = candidateStops,
            candidatePoiCount = candidates.size,
        )
    }

    fun previewStopReplacement(record: QuestRecord, stopId: String, localeCode: String): RouteStopReplacementPreview? {
        val match = selectReplacement(record, stopId, localeCode) ?: return null
        return RouteStopReplacementPreview(
            stopId = match.currentStop.stopId,
            originalPoiId = match.currentStop.poi.poiId,
            originalName = match.currentStop.poi.name,
            candidatePoiId = match.replacement.poiId,
            candidateName = match.replacement.name,
            candidateDistrict = match.replacement.district,
            candidateCategory = match.replacement.globalCategory,
            candidateTags = match.replacement.tags.take(4),
            stayMinutes = match.replacement.estimatedStayMinutes,
            sameCategory = match.sameCategory,
            matchedTags = match.matchedTags.take(3),
            stayDeltaMinutes = match.stayDeltaMinutes,
            sourceLabel = match.replacement.contentSource.sourceLabel,
        )
    }

    fun replaceStop(record: QuestRecord, stopId: String, localeCode: String): QuestRecord? {
        val match = selectReplacement(record, stopId, localeCode) ?: return null
        val quest = record.quest
        val plan = match.plan
        val currentStop = match.currentStop
        val copy = match.copy
        val replacement = match.replacement

        val replacementStop = replacement.toRouteStop(
            questId = quest.questId,
            order = currentStop.order,
            groupPreference = plan.groupPreference,
            copy = copy,
            startTimeHint = currentStop.startTimeHint,
        )
        val updatedStops = plan.stops.map { stop ->
            if (stop.stopId == currentStop.stopId) replacementStop else stop
        }
        val updatedPois = updatedStops.map { it.poi }
        val updatedPlanType = copy.safePlanTypeFor(plan.groupPreference)
        val updatedPlan = plan.copy(
            planType = updatedPlanType,
            title = plan.title,
            stops = updatedStops,
            estimatedCost = copy.safeEstimateCost(updatedPois, plan.groupPreference.mergedBudget),
            routeSummary = copy.safeRouteSummaryFor(plan.groupPreference, updatedPois),
            crowdRisk = copy.safeCrowdRiskFor(updatedPois),
            rainBackup = copy.safeRainBackupFor(updatedPois),
            candidateRouteCount = match.candidateCount,
        )
        val clearedTaskId = currentStop.checkInTask.taskId
        val updatedProgress = record.progress.copy(
            taskStatuses = record.progress.taskStatuses - clearedTaskId,
            feedbackReasons = record.progress.feedbackReasons - clearedTaskId,
            rewardPoints = record.progress.rewardPoints.filterNot { reward -> reward.taskId == clearedTaskId },
            lastRouteStopRestore = RouteStopRestoreSnapshot(
                stopId = currentStop.stopId,
                order = currentStop.order,
                previousStop = currentStop,
                restoredStatus = record.progress.taskStatuses[clearedTaskId],
                restoredFeedbackReasons = record.progress.feedbackReasons[clearedTaskId].orEmpty(),
                restoredRewardPoints = record.progress.rewardPoints.filter { reward -> reward.taskId == clearedTaskId },
                replacedStopName = replacement.name,
            ),
            updatedAt = System.currentTimeMillis(),
        )
        return record.copy(
            quest = quest.copy(itineraryPlan = updatedPlan),
            progress = updatedProgress,
        )
    }

    fun restoreStop(record: QuestRecord, localeCode: String): QuestRecord? {
        val quest = record.quest
        val plan = quest.itineraryPlan ?: return null
        val restore = record.progress.lastRouteStopRestore ?: return null
        val currentStop = plan.stops.firstOrNull { stop ->
            stop.stopId == restore.stopId || stop.order == restore.order
        } ?: return null
        val copy = ItineraryCopy.forLocale(localeCode)
        val updatedStops = plan.stops.map { stop ->
            if (stop.stopId == currentStop.stopId || stop.order == restore.order) restore.previousStop else stop
        }
        val updatedPois = updatedStops.map { it.poi }
        val updatedPlanType = copy.safePlanTypeFor(plan.groupPreference)
        val updatedPlan = plan.copy(
            planType = updatedPlanType,
            title = plan.title,
            stops = updatedStops,
            estimatedCost = copy.safeEstimateCost(updatedPois, plan.groupPreference.mergedBudget),
            routeSummary = copy.safeRouteSummaryFor(plan.groupPreference, updatedPois),
            crowdRisk = copy.safeCrowdRiskFor(updatedPois),
            rainBackup = copy.safeRainBackupFor(updatedPois),
        )
        val restoredTaskId = restore.previousStop.checkInTask.taskId
        val baseStatuses = record.progress.taskStatuses - currentStop.checkInTask.taskId
        val restoredStatuses = if (restore.restoredStatus == null) {
            baseStatuses
        } else {
            baseStatuses + (restoredTaskId to restore.restoredStatus)
        }
        val baseFeedback = record.progress.feedbackReasons - currentStop.checkInTask.taskId
        val restoredFeedback = if (restore.restoredFeedbackReasons.isEmpty()) {
            baseFeedback
        } else {
            baseFeedback + (restoredTaskId to restore.restoredFeedbackReasons)
        }
        val restoredProgress = record.progress.copy(
            taskStatuses = restoredStatuses,
            feedbackReasons = restoredFeedback,
            rewardPoints = record.progress.rewardPoints.filterNot { reward ->
                reward.taskId == currentStop.checkInTask.taskId
            } + restore.restoredRewardPoints,
            lastRouteStopRestore = null,
            updatedAt = System.currentTimeMillis(),
        )
        return record.copy(
            quest = quest.copy(itineraryPlan = updatedPlan),
            progress = restoredProgress,
        )
    }

    private fun selectReplacement(record: QuestRecord, stopId: String, localeCode: String): ReplacementMatch? {
        val plan = record.quest.itineraryPlan ?: return null
        val currentStop = plan.stops.firstOrNull { stop ->
            stop.stopId == stopId || stop.checkInTask.taskId == stopId
        } ?: return null
        val copy = ItineraryCopy.forLocale(localeCode)
        val contentResult = contentRepository.searchPois(
            cityOrQuery = plan.city,
            interests = plan.groupPreference.mergedInterests,
            relationship = plan.relationshipType,
        )
        val usedPoiIds = plan.stops.map { it.poi.poiId }.toSet()
        val ranked = contentResult.pois
            .asSequence()
            .filterNot { candidate -> candidate.poiId in usedPoiIds }
            .map { candidate ->
                ReplacementCandidate(
                    poi = candidate,
                    sameCategory = candidate.globalCategory == currentStop.poi.globalCategory,
                    matchedTags = candidate.tags.filter { tag ->
                        tag in plan.groupPreference.mergedInterests || tag in currentStop.poi.tags
                    },
                    stayDeltaMinutes = abs(candidate.estimatedStayMinutes - currentStop.stayMinutes),
                )
            }
            .sortedWith(
                compareByDescending<ReplacementCandidate> { candidate ->
                    if (candidate.sameCategory) 3 else 0
                }.thenByDescending { candidate ->
                    candidate.matchedTags.size
                }.thenBy { candidate ->
                    candidate.stayDeltaMinutes
                },
            )
            .firstOrNull()
            ?: return null

        return ReplacementMatch(
            plan = plan,
            currentStop = currentStop,
            replacement = copy.localizePoi(ranked.poi),
            candidateCount = contentResult.pois.size,
            sameCategory = ranked.sameCategory,
            matchedTags = ranked.matchedTags,
            stayDeltaMinutes = ranked.stayDeltaMinutes,
            copy = copy,
        )
    }

    private fun QuestInput.toGroupPreference(copy: ItineraryCopy): GroupPreference {
        val cityValue = city?.trim()?.takeIf { it.isNotEmpty() } ?: copy.safeDefaultCity
        val transport = transportMode.ifBlank { copy.safeDefaultTransport }
        val interests = (moods + vibe)
            .map { normalizeInterest(it) }
            .distinct()
            .ifEmpty { listOf("轻松", "散步") }
        val primary = UserPreference(
            userLabel = copy.safePrimaryUserLabel,
            relationship = relationship,
            city = cityValue,
            timeWindow = time,
            budget = budget,
            transportMode = transport,
            interests = interests,
            pace = if ("想轻松" in moods || "有点累" in moods) "轻松" else "标准",
            foodPreference = interests.firstOrNull { it.contains("吃饭") || it.contains("探店") || it.contains("咖啡") },
            photoPreference = interests.firstOrNull { it.contains("拍照") || it.contains("打卡") || it.contains("夜景") },
        )
        val companion = primary.copy(
            userLabel = copy.safeCompanionUserLabel,
            interests = interestsForCompanion(relationship, interests),
        )
        return GroupPreference(
            groupLabel = copy.safeGroupLabelFor(relationship),
            members = listOf(primary, companion),
            mergedCity = cityValue,
            mergedBudget = budget,
            mergedTimeWindow = time,
            mergedTransportMode = transport,
            mergedInterests = (primary.interests + companion.interests).distinct(),
            relationshipType = relationship,
            conflictNotes = copy.safeConflictNotes(primary, companion),
        )
    }

    private fun stopCountFor(groupPreference: GroupPreference): Int {
        return when {
            groupPreference.mergedTimeWindow.contains("30") || groupPreference.mergedTimeWindow.contains("90") -> 2
            groupPreference.mergedTimeWindow.contains("半天") -> 3
            groupPreference.mergedTimeWindow.contains("一整天") || groupPreference.mergedTimeWindow.contains("一天") -> 4
            else -> 3
        }
    }

    private fun rankCandidatesForInput(
        input: QuestInput,
        groupPreference: GroupPreference,
        candidates: List<POI>,
    ): List<POI> {
        if (candidates.isEmpty()) return emptyList()
        val intentText = listOfNotNull(
            input.note,
            input.relationship,
            input.vibe,
            input.time,
            input.budget,
            input.transportMode,
            input.moods.joinToString(" "),
        ).joinToString(" ").lowercase(Locale.ROOT)
        val seed = stableSeedFor(input, groupPreference)
        val ranked = candidates.mapIndexed { index, poi ->
            ScoredPoi(
                poi = poi,
                score = candidateScore(poi, intentText, input, groupPreference) + stableTie(seed, poi, index),
            )
        }
            .sortedByDescending { scored -> scored.score }
            .map { scored -> scored.poi }
        return diversifyByCategory(ranked)
    }

    private fun candidateScore(
        poi: POI,
        intentText: String,
        input: QuestInput,
        groupPreference: GroupPreference,
    ): Double {
        var score = 0.0
        val relationship = groupPreference.relationshipType
        if (relationship in poi.suitableRelationships || "all" in poi.suitableRelationships) score += 28.0
        score += groupPreference.mergedInterests.count { interest -> poi.matchesInterest(interest) } * 7.0

        if (intentText.hasAny("聊天", "聊聊", "chat") && poi.matchesAny("聊天", "咖啡", "安静", "cafe_chat", "quiet")) {
            score += 22.0
        }
        if (intentText.hasAny("安静", "治愈", "放空", "quiet")) {
            if (poi.matchesAny("安静", "阅读", "公园", "quiet", "library")) score += 24.0
            if (poi.matchesAny("热闹", "lively")) score -= 10.0
        }
        if (intentText.hasAny("热闹", "组局", "朋友", "lively") && poi.matchesAny("热闹", "吃喝逛", "夜间", "朋友", "lively", "food")) {
            score += 22.0
        }
        if (intentText.hasAny("拍照", "出片", "photo") && poi.matchesAny("拍照", "夜景", "建筑", "photo", "city_view")) {
            score += 20.0
        }
        if (intentText.hasAny("室内", "下雨", "雨天", "indoor")) {
            if (poi.matchesAny("室内优先", "室内", "下雨天", "indoor", "museum", "library")) score += 24.0
            if (poi.matchesAny("海边", "公园", "户外")) score -= 8.0
        }
        if (intentText.hasAny("少走", "不想走", "别太累", "不累", "低体力")) {
            if (poi.estimatedStayMinutes <= 60) score += 12.0
            if (poi.matchesAny("少走路", "低体力", "坐下", "咖啡", "quiet")) score += 20.0
            if (poi.estimatedStayMinutes >= 90) score -= 9.0
        }
        if (intentText.hasAny("低预算", "省钱", "不花钱", "100 元以内", "100元以内")) {
            if (poi.budgetLevel.startsWith("0") || poi.budgetLevel.contains("100")) score += 18.0
            if (poi.budgetLevel.contains("300")) score -= 9.0
        }
        if (intentText.hasAny("小惊喜", "新鲜", "surprise") && poi.matchesAny("小惊喜", "探店", "surprise")) {
            score += 16.0
        }
        if ((input.time.contains("30") || input.time.contains("90")) && poi.estimatedStayMinutes <= 60) {
            score += 8.0
        }
        return score
    }

    private fun diversifyByCategory(ranked: List<POI>): List<POI> {
        val selected = mutableListOf<POI>()
        val deferred = mutableListOf<POI>()
        val categoryCounts = mutableMapOf<String, Int>()
        ranked.forEach { poi ->
            val category = poi.globalCategory
            val count = categoryCounts[category] ?: 0
            if (count == 0 || selected.size < 2) {
                selected += poi
                categoryCounts[category] = count + 1
            } else {
                deferred += poi
            }
        }
        return selected + deferred
    }

    private fun stableSeedFor(input: QuestInput, groupPreference: GroupPreference): Int {
        return listOf(
            groupPreference.mergedCity,
            input.relationship,
            input.time,
            input.budget,
            input.vibe,
            input.moods.joinToString("|"),
            input.note.orEmpty().take(120),
        ).joinToString("#").hashCode()
    }

    private fun stableTie(seed: Int, poi: POI, index: Int): Double {
        val hash = "$seed-${poi.poiId}-$index".hashCode() and Int.MAX_VALUE
        return (hash % 1000) / 1000.0
    }

    private fun resolveRouteCity(requestedCity: String, candidates: List<POI>): String {
        val trimmed = requestedCity.trim()
        if (trimmed.isBlank() || trimmed.isBroadCityLabel()) {
            return candidates.firstOrNull()?.city ?: copyFallbackCity
        }
        return trimmed
    }

    private fun GroupPreference.withRouteCity(routeCity: String): GroupPreference {
        return copy(
            mergedCity = routeCity,
            members = members.map { member -> member.copy(city = routeCity) },
        )
    }

    private fun List<POI>.sameCityOnly(routeCity: String): List<POI> {
        val normalizedRouteCity = routeCity.normalizedCityKey()
        return filter { poi -> poi.city.normalizedCityKey() == normalizedRouteCity }
    }

    private fun String.isBroadCityLabel(): Boolean {
        val value = trim().lowercase(Locale.ROOT)
        return value.isBlank() ||
            value.contains("全球") ||
            value.contains("世界") ||
            value.contains("global") ||
            value.contains("worldwide") ||
            value.contains("popular cities") ||
            value.contains("ciudades populares")
    }

    private fun String.normalizedCityKey(): String = trim().lowercase(Locale.ROOT)

    private val copyFallbackCity: String
        get() = "上海"

    private fun POI.toRouteStop(
        questId: String,
        order: Int,
        groupPreference: GroupPreference,
        copy: ItineraryCopy,
        startTimeHint: String,
    ): RouteStop {
        val taskId = "$questId-stop-$order"
        return RouteStop(
            stopId = "stop-$taskId",
            order = order,
            poi = this,
            startTimeHint = startTimeHint,
            stayMinutes = estimatedStayMinutes,
            checkInTask = CheckInTask(
                taskId = taskId,
                title = copy.safeCheckInTitle(name),
                description = copy.safeCheckInDescriptionFor(groupPreference.relationshipType),
                rewardPoints = rewardFor(order),
                requiresPhoto = false,
                manualCheckInAllowed = true,
            ),
            photoSuggestion = copy.safePhotoSuggestionFor(groupPreference.relationshipType, this),
            spendingSuggestion = copy.safeSpendingSuggestionFor(this, groupPreference.mergedBudget),
            backupPlan = copy.safeBackupPlanFor(this),
            navigationAction = ExternalMapAction(
                destinationName = name,
                latitude = latitude,
                longitude = longitude,
                address = address,
                amapUri = amapUri(name, latitude, longitude),
                fallbackUri = webMapUri(name, latitude, longitude),
            ),
            whyForGroup = copy.safeWhyForGroup(groupPreference, this),
        )
    }

    private fun normalizeInterest(raw: String): String {
        return when {
            raw.contains("浪漫") || raw.contains("情侣") || raw.contains("暧昧") -> "情侣"
            raw.contains("朋友") || raw.contains("搞笑") -> "朋友"
            raw.contains("亲子") || raw.contains("家人") -> "家人"
            raw.contains("拍") || raw.contains("照片") || raw.contains("打卡") -> "拍照"
            raw.contains("吃") || raw.contains("餐") -> "吃饭"
            raw.contains("咖啡") -> "咖啡"
            raw.contains("展") || raw.contains("文化") -> "看展"
            raw.contains("店") -> "探店"
            raw.contains("走") || raw.contains("散步") || raw.contains("Citywalk", ignoreCase = true) -> "Citywalk"
            raw.contains("省钱") || raw.contains("低预算") -> "低预算"
            raw.contains("轻松") || raw.contains("累") || raw.contains("安静") -> "轻松"
            raw.contains("夜景") -> "夜景"
            else -> raw
        }
    }

    private fun POI.matchesInterest(interest: String): Boolean {
        val value = interest.lowercase(Locale.ROOT)
        return tags.any { tag ->
            val normalizedTag = tag.lowercase(Locale.ROOT)
            normalizedTag.contains(value) || value.contains(normalizedTag)
        } || globalCategory.lowercase(Locale.ROOT).contains(value)
    }

    private fun POI.matchesAny(vararg tokens: String): Boolean {
        val searchable = (tags + listOf(name, district, globalCategory, recommendationReason))
            .joinToString(" ")
            .lowercase(Locale.ROOT)
        return tokens.any { token -> searchable.contains(token.lowercase(Locale.ROOT)) }
    }

    private fun String.hasAny(vararg tokens: String): Boolean {
        return tokens.any { token -> contains(token.lowercase(Locale.ROOT)) }
    }

    private fun interestsForCompanion(relationship: String, interests: List<String>): List<String> {
        return when (relationship) {
            "情侣", "暧昧中" -> (interests + listOf("拍照", "轻松", "情侣")).distinct()
            "朋友" -> (interests + listOf("朋友", "探店", "Citywalk")).distinct()
            "家人" -> (interests + listOf("家人", "亲子友好", "轻松")).distinct()
            else -> interests
        }
    }

    private fun startTimeHint(index: Int, timeWindow: String): String {
        val baseHour = if (timeWindow.contains("下班") || timeWindow.contains("90") || timeWindow.contains("1 小时")) 19 else 14
        val hour = baseHour + index
        return "${hour.toString().padStart(2, '0')}:00"
    }

    private fun rewardFor(order: Int): Int = max(20, 35 - (order - 1) * 5)

    private fun amapUri(name: String, lat: Double, lng: Double): String {
        val encodedName = URLEncoder.encode(name, "UTF-8")
        return "androidamap://viewMap?sourceApplication=todayplay&poiname=$encodedName&lat=$lat&lon=$lng&dev=0"
    }

    private fun webMapUri(name: String, lat: Double, lng: Double): String {
        val encodedName = URLEncoder.encode(name, "UTF-8")
        return "https://www.google.com/maps/search/?api=1&query=$lat,$lng&query_place_id=${encodedName.lowercase(Locale.ROOT)}"
    }
}

private data class ScoredPoi(
    val poi: POI,
    val score: Double,
)

private data class ReplacementCandidate(
    val poi: POI,
    val sameCategory: Boolean,
    val matchedTags: List<String>,
    val stayDeltaMinutes: Int,
)

private data class ReplacementMatch(
    val plan: ItineraryPlan,
    val currentStop: RouteStop,
    val replacement: POI,
    val candidateCount: Int,
    val sameCategory: Boolean,
    val matchedTags: List<String>,
    val stayDeltaMinutes: Int,
    val copy: ItineraryCopy,
)

private enum class RouteLocale {
    ZhCn,
    ZhTw,
    En,
    Ja,
    Ko,
    Es,
}

private class ItineraryCopy(private val locale: RouteLocale) {
    val defaultCity: String
        get() = when (locale) {
            RouteLocale.ZhTw -> "全球熱門城市"
            RouteLocale.En -> "Global popular cities"
            RouteLocale.Ja -> "世界の人気都市"
            RouteLocale.Ko -> "전 세계 인기 도시"
            RouteLocale.Es -> "ciudades populares globales"
            RouteLocale.ZhCn -> "全球热门城市"
        }

    val defaultTransport: String
        get() = when (locale) {
            RouteLocale.ZhTw -> "公共交通／步行"
            RouteLocale.En -> "Public transit / walking"
            RouteLocale.Ja -> "公共交通／徒歩"
            RouteLocale.Ko -> "대중교통 / 도보"
            RouteLocale.Es -> "Transporte público / caminar"
            RouteLocale.ZhCn -> "公共交通/步行"
        }

    val primaryUserLabel: String
        get() = when (locale) {
            RouteLocale.ZhTw -> "發起人"
            RouteLocale.En -> "Planner"
            RouteLocale.Ja -> "計画する人"
            RouteLocale.Ko -> "제안한 사람"
            RouteLocale.Es -> "Organizador"
            RouteLocale.ZhCn -> "发起人"
        }

    val companionUserLabel: String
        get() = when (locale) {
            RouteLocale.ZhTw -> "同行人"
            RouteLocale.En -> "Companion"
            RouteLocale.Ja -> "同行者"
            RouteLocale.Ko -> "동행자"
            RouteLocale.Es -> "Acompañante"
            RouteLocale.ZhCn -> "同行人"
        }

    val rewardPolicy: String
        get() = when (locale) {
            RouteLocale.ZhTw -> "每完成一個地點打卡可獲得 20-35 積分。目前為本地積分記錄，正式權益兌換必須由服務端校驗。"
            RouteLocale.En -> "Each stop check-in grants 20-35 local points. Real benefit redemption must be verified by the backend."
            RouteLocale.Ja -> "各スポットのチェックインで 20-35 ローカルポイントを獲得できます。実際の特典交換にはサーバー検証が必要です。"
            RouteLocale.Ko -> "각 장소 체크인마다 20-35 로컬 포인트를 받을 수 있습니다. 실제 혜택 교환은 서버 검증이 필요합니다."
            RouteLocale.Es -> "Cada check-in otorga 20-35 puntos locales. El canje real de beneficios debe verificarse en el backend."
            RouteLocale.ZhCn -> "每完成一个地点打卡可获得 20-35 积分。当前为本地积分记录，正式权益兑换必须由服务端校验。"
        }

    val complianceSummary: String
        get() = when (locale) {
            RouteLocale.ZhTw -> "V0.8 使用全球 mock POI 目錄驗證產品結構；正式全球搜索必須接官方 API、授權內容源或後端服務。"
            RouteLocale.En -> "V0.8 uses a global mock POI catalog to validate the product structure. Production global search requires official APIs, licensed sources, or backend services."
            RouteLocale.Ja -> "V0.8 はグローバル mock POI カタログで構造を検証します。本番の検索には公式 API、ライセンス済みデータ、またはバックエンドが必要です。"
            RouteLocale.Ko -> "V0.8은 글로벌 mock POI 카탈로그로 제품 구조를 검증합니다. 실제 글로벌 검색은 공식 API, 라이선스 데이터 또는 백엔드가 필요합니다."
            RouteLocale.Es -> "V0.8 usa un catálogo global mock de POI para validar la estructura. La búsqueda global real requiere APIs oficiales, fuentes con licencia o backend."
            RouteLocale.ZhCn -> "V0.8 使用全球 mock POI 目录验证产品结构；正式全球搜索必须接官方 API、授权内容源或后端服务。"
        }

    val complianceLimitations: List<String>
        get() = when (locale) {
            RouteLocale.ZhTw -> listOf("目前不是全球全量資料庫，只是可運行樣例和正式資料結構。", "社交熱度、營業時間、票價、人流和圖片都需要官方或授權資料源。", "地圖跳轉只打開外部地圖，不讀取定位，也不做強制到店校驗。")
            RouteLocale.En -> listOf("This is not a full global database; it is a runnable sample and production data structure.", "Social heat, opening hours, prices, crowd levels, and images need authorized providers.", "Map actions only open external maps; the app does not read location or force arrival verification.")
            RouteLocale.Ja -> listOf("これは完全な世界データベースではなく、実行可能なサンプルと本番向け構造です。", "SNS 熱度、営業時間、料金、混雑、画像は公式または許諾済みデータが必要です。", "地図は外部アプリを開くだけで、位置情報取得や強制到着確認は行いません。")
            RouteLocale.Ko -> listOf("전체 글로벌 데이터베이스가 아니라 실행 가능한 샘플과 상용 구조입니다.", "소셜 인기, 영업시간, 가격, 혼잡도, 이미지는 공식 또는 라이선스 데이터가 필요합니다.", "지도는 외부 앱을 열 뿐이며 위치를 읽거나 강제 도착 검증을 하지 않습니다.")
            RouteLocale.Es -> listOf("No es una base global completa; es una muestra ejecutable y una estructura de producción.", "Popularidad social, horarios, precios, aforo e imágenes requieren proveedores autorizados.", "Los mapas solo abren apps externas; no se lee ubicación ni se fuerza verificación de llegada.")
            RouteLocale.ZhCn -> listOf("当前不是全球全量数据库，只是可运行样例和正式数据结构。", "社交平台热度、营业时间、票价、人流和图片都需要官方或授权数据源。", "地图跳转只打开外部地图，不读取用户定位，也不做强制到店校验。")
        }

    val marketCoverageNote: String
        get() = when (locale) {
            RouteLocale.ZhTw -> "客戶端支援全球城市/關鍵詞輸入；正式覆蓋需要後端 POI 搜索供應商與授權內容聚合。"
            RouteLocale.En -> "The client supports global city/query input. Production coverage requires backend POI search providers and licensed content aggregation."
            RouteLocale.Ja -> "クライアントは世界の都市名／キーワード入力に対応します。本番カバーにはバックエンド POI 検索と許諾済みコンテンツ集約が必要です。"
            RouteLocale.Ko -> "클라이언트는 글로벌 도시/검색어 입력을 지원합니다. 실제 서비스 범위는 백엔드 POI 검색 제공자와 라이선스 콘텐츠 집계가 필요합니다."
            RouteLocale.Es -> "El cliente admite ciudades y búsquedas globales. La cobertura real requiere proveedores POI backend y contenido con licencia."
            RouteLocale.ZhCn -> "客户端支持全球城市/关键词输入；正式覆盖需要后端 POI 搜索供应商与授权内容聚合。"
        }

    fun coverageNoteFor(isMock: Boolean, rawNote: String): String {
        if (locale == RouteLocale.En && rawNote.isNotBlank()) return rawNote
        return if (isMock) localizedMockCoverageNote() else localizedRemoteCoverageNote(rawNote)
    }

    private fun localizedMockCoverageNote(): String {
        return when (locale) {
            RouteLocale.ZhTw -> "全球即時搜尋尚未配置；目前使用本地 mock 地點庫驗證路線結構。正式覆蓋必須接入官方 API、授權資料、商家合作、用戶授權分享連結或後端聚合服務。"
            RouteLocale.En -> "Global live search is not configured yet. The app is using a local mock catalog for route structure testing. Production global search must use official APIs, licensed data, merchant partnerships, user-authorized links, or backend aggregation."
            RouteLocale.Ja -> "グローバルのライブ検索はまだ設定されていません。現在はローカルmockカタログでルート構造を検証しています。本番検索には公式API、ライセンス済みデータ、店舗連携、ユーザー承認済み共有リンク、またはバックエンド集約が必要です。"
            RouteLocale.Ko -> "글로벌 실시간 검색은 아직 설정되지 않았습니다. 현재는 로컬 mock 카탈로그로 경로 구조를 검증합니다. 정식 글로벌 검색에는 공식 API, 라이선스 데이터, 제휴 매장, 사용자 승인 공유 링크 또는 백엔드 집계가 필요합니다."
            RouteLocale.Es -> "La busqueda global en vivo aun no esta configurada. La app usa un catalogo local mock para probar la estructura de rutas. La busqueda real debe usar APIs oficiales, datos con licencia, alianzas comerciales, enlaces autorizados por usuarios o agregacion backend."
            RouteLocale.ZhCn -> "全球实时搜索尚未配置；当前使用本地 mock 地点库验证路线结构。正式覆盖必须接入官方 API、授权数据、商家合作、用户授权分享链接或后端聚合服务。"
        }
    }

    private fun localizedRemoteCoverageNote(rawNote: String): String {
        return when (locale) {
            RouteLocale.ZhTw -> "遠端旅行內容端點已配置；所有回應仍需後端完成來源政策、授權、時效與圖片許可校驗。"
            RouteLocale.En -> rawNote.ifBlank { "Remote travel content is configured. Continue verifying source policy, licensing, freshness, and image metadata." }
            RouteLocale.Ja -> "リモート旅行コンテンツのエンドポイントは設定済みです。すべてのレスポンスはバックエンドで出典ポリシー、ライセンス、鮮度、画像許諾を検証する必要があります。"
            RouteLocale.Ko -> "원격 여행 콘텐츠 엔드포인트가 설정되었습니다. 모든 응답은 백엔드에서 출처 정책, 라이선스, 최신성, 이미지 권한을 계속 검증해야 합니다."
            RouteLocale.Es -> "El endpoint remoto de contenido de viaje esta configurado. Las respuestas aun deben verificarse en backend por politica de fuente, licencia, vigencia y permisos de imagen."
            RouteLocale.ZhCn -> "远端旅行内容端点已配置；所有响应仍需后端完成来源政策、授权、时效和图片许可校验。"
        }
    }

    fun groupLabelFor(relationship: String): String {
        return when (relationship) {
            "情侣" -> t("双人情侣旅行", "雙人情侶旅行", "Couple route", "カップルルート", "커플 루트", "Ruta de pareja")
            "暧昧中" -> t("低压力暧昧同行", "低壓力曖昧同行", "Low-pressure crush route", "低圧の初期デート", "부담 낮은 썸 루트", "Ruta suave para crush")
            "朋友" -> t("朋友 Citywalk", "朋友 Citywalk", "Friends citywalk", "友達 citywalk", "친구 citywalk", "Citywalk con amigos")
            "家人" -> t("亲子家庭出行", "親子家庭出行", "Family route", "家族ルート", "가족 루트", "Ruta familiar")
            else -> t("今日探索路线", "今日探索路線", "Today's route", "今日のルート", "오늘의 루트", "Ruta de hoy")
        }
    }

    fun conflictNotes(primary: UserPreference, companion: UserPreference): List<String> = buildList {
        if (primary.budget.contains("0") || primary.budget.contains("50")) {
            add(t("预算偏低，优先安排低消费地点和免费拍照点。", "預算偏低，優先安排低消費地點和免費拍照點。", "Budget is low, so the route favors low-cost stops and free photo spots.", "予算低めのため、低消費スポットと無料撮影ポイントを優先します。", "예산이 낮아 저비용 장소와 무료 사진 포인트를 우선합니다.", "Presupuesto bajo: se priorizan paradas económicas y fotos gratis."))
        }
        if (primary.timeWindow.contains("30") || primary.timeWindow.contains("90")) {
            add(t("时间较短，路线控制在 2 个核心点。", "時間較短，路線控制在 2 個核心點。", "Time is short, so the route stays around two key stops.", "時間が短いため、主要スポットは 2 か所に絞ります。", "시간이 짧아 핵심 장소 2곳 중심으로 구성합니다.", "Tiempo limitado: la ruta se concentra en dos paradas clave."))
        }
        if ("拍照" in companion.interests && "轻松" in primary.interests) {
            add(t("兼顾拍照和体力，优先选择步行压力小的点。", "兼顧拍照和體力，優先選擇步行壓力小的點。", "Photo needs and energy are balanced with easier walking stops.", "写真と体力のバランスを取り、歩行負担の少ない場所を優先します。", "사진과 체력을 함께 고려해 걷기 부담이 낮은 곳을 우선합니다.", "Se equilibra foto y energía con paradas de caminata ligera."))
        }
    }

    fun planTypeFor(groupPreference: GroupPreference): String {
        return when {
            groupPreference.mergedTimeWindow.contains("90") -> t("下班后 90 分钟", "下班後 90 分鐘", "90 minutes after work", "仕事後90分", "퇴근 후 90분", "90 minutos después del trabajo")
            groupPreference.relationshipType == "情侣" -> t("周末情侣路线", "週末情侶路線", "Weekend couple route", "週末カップルルート", "주말 커플 루트", "Ruta de pareja de fin de semana")
            groupPreference.relationshipType == "朋友" -> t("朋友 Citywalk", "朋友 Citywalk", "Friends citywalk", "友達 citywalk", "친구 citywalk", "Citywalk con amigos")
            groupPreference.relationshipType == "家人" -> t("亲子家庭路线", "親子家庭路線", "Family route", "家族ルート", "가족 루트", "Ruta familiar")
            groupPreference.mergedTimeWindow.contains("半天") -> t("半日游", "半日遊", "Half-day route", "半日ルート", "반나절 루트", "Ruta de medio día")
            groupPreference.mergedTimeWindow.contains("一天") || groupPreference.mergedTimeWindow.contains("一整天") -> t("一日游", "一日遊", "Full-day route", "1日ルート", "하루 루트", "Ruta de un día")
            else -> t("今日路线副本", "今日路線副本", "Today's route quest", "今日のルートクエスト", "오늘의 루트 퀘스트", "Ruta quest de hoy")
        }
    }

    fun titleFor(groupPreference: GroupPreference, pois: List<POI>, planType: String): String {
        val city = pois.firstOrNull()?.city ?: groupPreference.mergedCity
        val district = pois.firstOrNull()?.district ?: t("精选", "精選", "curated", "厳選", "큐레이션", "selección")
        return when (locale) {
            RouteLocale.En -> "$city $district $planType"
            RouteLocale.Ja -> "$city $district $planType"
            RouteLocale.Ko -> "$city $district $planType"
            RouteLocale.Es -> "$planType · $city $district"
            else -> "$city $district $planType"
        }
    }

    fun routeSummaryFor(groupPreference: GroupPreference, pois: List<POI>): String {
        val names = pois.joinToString(" -> ") { it.name }
        return when (locale) {
            RouteLocale.ZhTw -> "根據${groupPreference.relationshipType}、${groupPreference.mergedTimeWindow}、${groupPreference.mergedBudget}和${groupPreference.mergedTransportMode}，整理出：$names。"
            RouteLocale.En -> "Based on ${groupPreference.relationshipType}, ${groupPreference.mergedTimeWindow}, ${groupPreference.mergedBudget}, and ${groupPreference.mergedTransportMode}, the route is: $names."
            RouteLocale.Ja -> "${groupPreference.relationshipType}、${groupPreference.mergedTimeWindow}、${groupPreference.mergedBudget}、${groupPreference.mergedTransportMode}をもとに、$names を組みました。"
            RouteLocale.Ko -> "${groupPreference.relationshipType}, ${groupPreference.mergedTimeWindow}, ${groupPreference.mergedBudget}, ${groupPreference.mergedTransportMode} 기준으로 $names 루트를 구성했습니다."
            RouteLocale.Es -> "Según ${groupPreference.relationshipType}, ${groupPreference.mergedTimeWindow}, ${groupPreference.mergedBudget} y ${groupPreference.mergedTransportMode}, la ruta es: $names."
            RouteLocale.ZhCn -> "根据${groupPreference.relationshipType}、${groupPreference.mergedTimeWindow}、${groupPreference.mergedBudget}和${groupPreference.mergedTransportMode}，整理出：$names。"
        }
    }

    fun estimateCost(pois: List<POI>, budget: String): String {
        val base = when {
            budget.contains("0") -> t("0-80 / 人", "0-80 / 人", "0-80 per person", "1人 0-80", "1인 0-80", "0-80 por persona")
            budget.contains("50") -> t("50-120 / 人", "50-120 / 人", "50-120 per person", "1人 50-120", "1인 50-120", "50-120 por persona")
            budget.contains("100") -> t("80-180 / 人", "80-180 / 人", "80-180 per person", "1人 80-180", "1인 80-180", "80-180 por persona")
            budget.contains("300") -> t("150-320 / 人", "150-320 / 人", "150-320 per person", "1人 150-320", "1인 150-320", "150-320 por persona")
            else -> t("按现场消费", "按現場消費", "depends on on-site spending", "現地消費による", "현장 소비 기준", "según consumo local")
        }
        val cities = pois.map { it.city }.distinct().joinToString(" / ")
        return when (locale) {
            RouteLocale.ZhTw -> "$base，$cities 的真實價格需由地圖、商家或票務 API 核驗。"
            RouteLocale.En -> "$base. Real prices in $cities must be verified by maps, merchants, or ticketing APIs."
            RouteLocale.Ja -> "$base。$cities の実価格は地図、店舗、チケット API で確認が必要です。"
            RouteLocale.Ko -> "$base. $cities 실제 가격은 지도, 매장 또는 티켓 API로 확인해야 합니다."
            RouteLocale.Es -> "$base. Los precios reales en $cities deben verificarse con mapas, comercios o APIs de tickets."
            RouteLocale.ZhCn -> "$base，$cities 的真实价格需由地图、商家或票务 API 核验。"
        }
    }

    fun crowdRiskFor(pois: List<POI>): String {
        val highRisk = pois.any { poi -> poi.riskTips.any { it.contains("人流") || it.contains("拥挤") || it.contains("排队") } }
        return if (highRisk) {
            t("中到高：热门机位、节假日和傍晚时段可能拥挤。", "中到高：熱門機位、節假日和傍晚時段可能擁擠。", "Medium to high: popular photo spots, holidays, and dusk may be crowded.", "中〜高：人気撮影ポイント、休日、夕方は混雑しやすいです。", "중간~높음: 인기 촬영 포인트, 휴일, 저녁 시간은 혼잡할 수 있습니다.", "Media a alta: puntos populares, festivos y atardecer pueden estar concurridos.")
        } else {
            t("低到中：仍建议核验当地节假日、天气和营业时间。", "低到中：仍建議核驗當地節假日、天氣和營業時間。", "Low to medium: still verify local holidays, weather, and opening hours.", "低〜中：現地の祝日、天気、営業時間は確認してください。", "낮음~중간: 현지 휴일, 날씨, 영업시간 확인이 필요합니다.", "Baja a media: confirma festivos, clima y horarios locales.")
        }
    }

    fun rainBackupFor(pois: List<POI>): String {
        val indoor = pois.firstOrNull { poi -> poi.tags.any { it.contains("室内") || it.contains("雨天") || it.contains("商场") } }
        return if (indoor != null) {
            when (locale) {
                RouteLocale.ZhTw -> "下雨時優先切換到 ${indoor.name}，減少戶外停留。"
                RouteLocale.En -> "If it rains, switch first to ${indoor.name} and reduce outdoor time."
                RouteLocale.Ja -> "雨の場合はまず ${indoor.name} に切り替え、屋外滞在を短くします。"
                RouteLocale.Ko -> "비가 오면 먼저 ${indoor.name}로 전환하고 야외 시간을 줄입니다."
                RouteLocale.Es -> "Si llueve, cambia primero a ${indoor.name} y reduce el tiempo al aire libre."
                RouteLocale.ZhCn -> "下雨时优先切换到 ${indoor.name}，减少户外停留。"
            }
        } else {
            t("下雨时缩短户外拍照，改成咖啡店、商圈、书店、博物馆或室内展览。", "下雨時縮短戶外拍照，改成咖啡店、商圈、書店、博物館或室內展覽。", "On rainy days, shorten outdoor photos and switch to cafes, malls, bookstores, museums, or indoor exhibitions.", "雨の日は屋外撮影を短くし、カフェ、商業施設、書店、博物館、屋内展示に切り替えます。", "비 오는 날은 야외 촬영을 줄이고 카페, 쇼핑몰, 서점, 박물관, 실내 전시로 전환합니다.", "Con lluvia, reduce fotos al aire libre y cambia a cafés, centros comerciales, librerías, museos o exposiciones interiores.")
        }
    }

    fun bestPhotoTimeFor(groupPreference: GroupPreference): String {
        return when {
            groupPreference.mergedInterests.any { it.contains("夜景") } -> "19:00-21:00"
            groupPreference.relationshipType == "情侣" -> t("日落前 40 分钟到蓝调时刻", "日落前 40 分鐘到藍調時刻", "40 minutes before sunset through blue hour", "日没40分前からブルーアワー", "일몰 40분 전부터 블루아워", "40 minutos antes del atardecer hasta la hora azul")
            else -> t("下午 16:00-18:00 光线更柔和", "下午 16:00-18:00 光線更柔和", "16:00-18:00, when the light is softer", "16:00-18:00 の柔らかい光", "16:00-18:00 부드러운 빛", "16:00-18:00, luz más suave")
        }
    }

    fun checkInTitle(name: String): String = when (locale) {
        RouteLocale.ZhTw -> "在 $name 完成打卡"
        RouteLocale.En -> "Check in at $name"
        RouteLocale.Ja -> "$name でチェックイン"
        RouteLocale.Ko -> "$name 체크인"
        RouteLocale.Es -> "Check-in en $name"
        RouteLocale.ZhCn -> "在 $name 完成打卡"
    }

    fun checkInDescriptionFor(relationship: String): String {
        return when (relationship) {
            "情侣", "暧昧中" -> t("拍一张不露脸氛围照或手动确认到达，获得心动积分。", "拍一張不露臉氛圍照或手動確認到達，獲得心動積分。", "Take a no-face mood photo or confirm arrival manually to earn points.", "顔を出さない雰囲気写真、または手動到着確認でポイント獲得。", "얼굴 없는 분위기 사진 또는 수동 도착 확인으로 포인트를 받습니다.", "Toma una foto de ambiente sin rostro o confirma llegada para ganar puntos.")
            "朋友" -> t("完成一张搞笑合照、店门口打卡或手动确认到达。", "完成一張搞笑合照、店門口打卡或手動確認到達。", "Take a funny group shot, storefront check-in, or manual arrival confirmation.", "面白い集合写真、店頭チェックイン、または手動到着確認をします。", "재미있는 단체 사진, 매장 앞 체크인 또는 수동 도착 확인을 합니다.", "Haz una foto divertida, check-in en la entrada o confirmación manual.")
            "家人" -> t("记录一张陪伴照片或手动确认到达，给今天留个存档。", "記錄一張陪伴照片或手動確認到達，給今天留個存檔。", "Save a family moment or confirm arrival manually to archive the day.", "一緒に過ごす写真、または手動到着確認で今日を記録します。", "함께한 사진 또는 수동 도착 확인으로 오늘을 기록합니다.", "Guarda una foto de compañía o confirma llegada para archivar el día.")
            else -> t("到达后手动打卡，记录今天的路线进度。", "到達後手動打卡，記錄今天的路線進度。", "Check in manually after arrival to record route progress.", "到着後に手動チェックインしてルート進行を記録します。", "도착 후 수동 체크인으로 루트 진행을 기록합니다.", "Haz check-in manual al llegar para registrar el progreso.")
        }
    }

    fun photoSuggestionFor(relationship: String, poi: POI): String {
        val relationSuggestion = when (relationship) {
            "情侣", "暧昧中" -> t("拍影子、背影、两杯饮料或路灯下的同框。", "拍影子、背影、兩杯飲料或路燈下的同框。", "Try shadows, backs, two drinks, or a shared frame under streetlights.", "影、後ろ姿、2つの飲み物、街灯下の同じフレームがおすすめ。", "그림자, 뒷모습, 음료 두 잔, 가로등 아래 같은 프레임을 찍어보세요.", "Prueba sombras, espaldas, dos bebidas o un encuadre bajo faroles.")
            "朋友" -> t("每个人模仿一个路人海报动作，选最离谱的一张当封面。", "每個人模仿一個路人海報動作，選最離譜的一張當封面。", "Everyone copies a street-poster pose; keep the most absurd one as cover.", "全員で街ポスター風ポーズを真似し、一番変な1枚を表紙に。", "각자 포스터 포즈를 따라 하고 가장 웃긴 사진을 커버로 남기세요.", "Cada persona imita una pose de póster; guarda la más absurda.")
            "家人" -> t("拍走路背影、桌面小物或大家都舒服的自然合照。", "拍走路背影、桌面小物或大家都舒服的自然合照。", "Capture walking backs, table details, or a relaxed natural group photo.", "歩く後ろ姿、テーブルの小物、自然な集合写真を撮ります。", "걷는 뒷모습, 테이블 소품, 편안한 자연 단체 사진을 남기세요.", "Captura espaldas caminando, detalles de mesa o una foto natural.")
            else -> t("拍一张能证明今天来过这里的小证据。", "拍一張能證明今天來過這裡的小證據。", "Take one small proof that you were here today.", "今日ここに来た小さな証拠を1枚撮ります。", "오늘 이곳에 온 작은 증거를 하나 남기세요.", "Toma una pequeña prueba de que estuviste aquí hoy.")
        }
        return when (locale) {
            RouteLocale.ZhTw -> "$relationSuggestion 推薦機位：${poi.imagePlaceholder}。"
            RouteLocale.En -> "$relationSuggestion Suggested shot: ${poi.imagePlaceholder}."
            RouteLocale.Ja -> "$relationSuggestion おすすめ構図：${poi.imagePlaceholder}。"
            RouteLocale.Ko -> "$relationSuggestion 추천 구도: ${poi.imagePlaceholder}."
            RouteLocale.Es -> "$relationSuggestion Toma sugerida: ${poi.imagePlaceholder}."
            RouteLocale.ZhCn -> "$relationSuggestion 推荐机位：${poi.imagePlaceholder}。"
        }
    }

    fun spendingSuggestionFor(poi: POI, budget: String): String {
        return if (budget.contains("0") || budget.contains("50")) {
            t("优先免费散步和小额饮品，不为了打卡强消费。", "優先免費散步和小額飲品，不為了打卡強消費。", "Prioritize free walking and small drinks; do not force spending for check-ins.", "無料散歩と少額ドリンクを優先し、チェックインのための無理な消費はしません。", "무료 산책과 소액 음료를 우선하고 체크인을 위해 무리한 소비를 하지 않습니다.", "Prioriza caminatas gratis y bebidas pequeñas; no fuerces consumo por check-in.")
        } else {
            when (locale) {
                RouteLocale.ZhTw -> "可把消費集中在這一站，其他站以散步和拍照為主。地點預算等級：${poi.budgetLevel}。"
                RouteLocale.En -> "Put most spending at this stop; keep other stops for walking and photos. Budget level: ${poi.budgetLevel}."
                RouteLocale.Ja -> "消費はこのスポットに集中し、他は散歩と写真中心に。予算レベル：${poi.budgetLevel}。"
                RouteLocale.Ko -> "소비는 이 장소에 집중하고 다른 장소는 산책과 사진 중심으로 구성합니다. 예산 등급: ${poi.budgetLevel}."
                RouteLocale.Es -> "Concentra el gasto en esta parada y deja las demás para caminar y fotos. Nivel de presupuesto: ${poi.budgetLevel}."
                RouteLocale.ZhCn -> "可把消费集中在这一站，其他站以散步和拍照为主。地点预算等级：${poi.budgetLevel}。"
            }
        }
    }

    fun backupPlanFor(poi: POI): String {
        val risk = poi.riskTips.firstOrNull()
        return if (risk != null) {
            when (locale) {
                RouteLocale.ZhTw -> "如果遇到：$risk，就縮短停留或切換到附近室內備選。"
                RouteLocale.En -> "If this happens: $risk, shorten the stay or switch to a nearby indoor backup."
                RouteLocale.Ja -> "$risk の場合は滞在を短くし、近くの屋内候補に切り替えます。"
                RouteLocale.Ko -> "$risk 상황이면 머무는 시간을 줄이거나 근처 실내 대안으로 전환합니다."
                RouteLocale.Es -> "Si ocurre: $risk, acorta la parada o cambia a una alternativa interior cercana."
                RouteLocale.ZhCn -> "如果遇到：$risk，就缩短停留或切换到附近室内备选。"
            }
        } else {
            t("如果不适合停留，就只完成手动打卡并前往下一站。", "如果不適合停留，就只完成手動打卡並前往下一站。", "If it is not comfortable to stay, check in manually and move to the next stop.", "滞在しづらい場合は手動チェックインだけ行い、次へ進みます。", "머물기 어렵다면 수동 체크인만 하고 다음 장소로 이동합니다.", "Si no conviene quedarse, haz check-in manual y sigue a la próxima parada.")
        }
    }

    fun whyForGroup(groupPreference: GroupPreference, poi: POI): String {
        val matchedTags = when (locale) {
            RouteLocale.ZhCn -> poi.tags.filter { it in groupPreference.mergedInterests }.ifEmpty { poi.tags.take(2) }
            else -> poi.tags.take(2)
        }
        val matched = matchedTags.joinToString(" / ")
        return when (locale) {
            RouteLocale.ZhTw -> "匹配 ${groupPreference.groupLabel} 的 $matched 需求。"
            RouteLocale.En -> "Matches the group's $matched needs."
            RouteLocale.Ja -> "グループの $matched ニーズに合います。"
            RouteLocale.Ko -> "그룹의 $matched 요구와 맞습니다."
            RouteLocale.Es -> "Encaja con las necesidades de $matched del grupo."
            RouteLocale.ZhCn -> "匹配 ${groupPreference.groupLabel} 的 $matched 需求。"
        }
    }

    fun localizePoi(poi: POI): POI {
        if (locale == RouteLocale.ZhCn) return poi
        return poi.copy(
            name = safePoiName(poi),
            country = localizedCountryFor(poi),
            city = localizedCityFor(poi),
            district = localizedDistrictFor(poi),
            address = localizedAddressFor(poi),
            tags = localizedTagsFor(poi.tags),
            recommendationReason = safeRecommendationFor(poi),
            riskTips = safeRiskTipsFor(poi),
            contentSource = poi.contentSource.copy(
                sourceLabel = safeLocalizedSourceLabel(),
                sourcePolicy = safeLocalizeSourcePolicy(poi.contentSource.sourcePolicy),
            ),
        )
    }

    fun localizeSourcePolicy(policy: SourcePolicy): SourcePolicy {
        if (locale == RouteLocale.ZhCn) return policy
        return SourcePolicy(
            policyTitle = when (locale) {
                RouteLocale.ZhTw -> "內容來源政策"
                RouteLocale.En -> "Content source policy"
                RouteLocale.Ja -> "コンテンツソースポリシー"
                RouteLocale.Ko -> "콘텐츠 출처 정책"
                RouteLocale.Es -> "Politica de fuentes de contenido"
                RouteLocale.ZhCn -> policy.policyTitle
            },
            policyNotes = localizedSourcePolicyNotes(),
        )
    }

    private fun localizedCountryFor(poi: POI): String {
        return when (locale) {
            RouteLocale.ZhTw -> countryNamesZhTw[poi.country] ?: poi.country
            RouteLocale.En -> poi.country
            RouteLocale.Ja -> countryNamesJa[poi.country] ?: poi.country
            RouteLocale.Ko -> countryNamesKo[poi.country] ?: poi.country
            RouteLocale.Es -> countryNamesEs[poi.country] ?: poi.country
            RouteLocale.ZhCn -> poi.country
        }
    }

    private fun localizedCityFor(poi: POI): String {
        return when (locale) {
            RouteLocale.ZhTw -> cityNamesZhTw[poi.city] ?: poi.city
            RouteLocale.En -> cityNamesEn[poi.city] ?: poi.city
            RouteLocale.Ja -> cityNamesJa[poi.city] ?: (cityNamesEn[poi.city] ?: poi.city)
            RouteLocale.Ko -> cityNamesKo[poi.city] ?: (cityNamesEn[poi.city] ?: poi.city)
            RouteLocale.Es -> cityNamesEs[poi.city] ?: (cityNamesEn[poi.city] ?: poi.city)
            RouteLocale.ZhCn -> poi.city
        }
    }

    private fun localizedDistrictFor(poi: POI): String {
        val key = "${poi.city}:${poi.district}"
        return when (locale) {
            RouteLocale.ZhTw -> districtNamesZhTw[key] ?: poi.district
            RouteLocale.En -> districtNamesEn[key] ?: poi.district
            RouteLocale.Ja -> districtNamesJa[key] ?: (districtNamesEn[key] ?: poi.district)
            RouteLocale.Ko -> districtNamesKo[key] ?: (districtNamesEn[key] ?: poi.district)
            RouteLocale.Es -> districtNamesEs[key] ?: (districtNamesEn[key] ?: poi.district)
            RouteLocale.ZhCn -> poi.district
        }
    }

    private fun localizedAddressFor(poi: POI): String {
        val city = localizedCityFor(poi)
        val district = localizedDistrictFor(poi)
        val name = safePoiName(poi)
        return when (locale) {
            RouteLocale.ZhTw -> addressNamesZhTw[poi.poiId] ?: "$city $district $name"
            RouteLocale.En -> addressNamesEn[poi.poiId] ?: "$name, $district, $city"
            RouteLocale.Ja -> addressNamesJa[poi.poiId] ?: "$city $district $name"
            RouteLocale.Ko -> addressNamesKo[poi.poiId] ?: "$city $district $name"
            RouteLocale.Es -> addressNamesEs[poi.poiId] ?: "$name, $district, $city"
            RouteLocale.ZhCn -> poi.address
        }
    }

    private fun localizedTagsFor(tags: List<String>): List<String> {
        return tags.map { tag ->
            when (locale) {
                RouteLocale.ZhTw -> tagNamesZhTw[tag] ?: tag
                RouteLocale.En -> tagNamesEn[tag] ?: tag
                RouteLocale.Ja -> tagNamesJa[tag] ?: (tagNamesEn[tag] ?: tag)
                RouteLocale.Ko -> tagNamesKo[tag] ?: (tagNamesEn[tag] ?: tag)
                RouteLocale.Es -> tagNamesEs[tag] ?: (tagNamesEn[tag] ?: tag)
                RouteLocale.ZhCn -> tag
            }
        }.distinct()
    }

    private fun localizedSourceLabel(): String {
        return when (locale) {
            RouteLocale.ZhTw -> "本地 mock 精選地點庫"
            RouteLocale.En -> "Local mock curated POI catalog"
            RouteLocale.Ja -> "ローカルmock厳選POIカタログ"
            RouteLocale.Ko -> "로컬 mock 엄선 POI 카탈로그"
            RouteLocale.Es -> "Catalogo local mock de POI curados"
            RouteLocale.ZhCn -> "本地 mock 精选地点库"
        }
    }

    private fun localizedSourcePolicyNotes(): List<String> {
        return when (locale) {
            RouteLocale.ZhTw -> listOf(
                "目前不抓取第三方平台內容。",
                "正式全球搜尋需要官方 API、授權資料源或後端聚合。",
                "mock 熱度、圖片和價格不可當成真實平台背書。",
            )
            RouteLocale.En -> listOf(
                "The client does not scrape third-party platforms.",
                "Production global search requires official APIs, licensed sources, or backend aggregation.",
                "Mock heat, images, and prices must not be presented as real platform endorsements.",
            )
            RouteLocale.Ja -> listOf(
                "クライアントは第三者プラットフォームをスクレイピングしません。",
                "本番のグローバル検索には公式API、ライセンス済みデータ、またはバックエンド集約が必要です。",
                "mockの人気度、画像、価格を実在プラットフォームの保証として表示しません。",
            )
            RouteLocale.Ko -> listOf(
                "클라이언트는 제3자 플랫폼을 스크래핑하지 않습니다.",
                "정식 글로벌 검색에는 공식 API, 라이선스 데이터 또는 백엔드 집계가 필요합니다.",
                "mock 인기, 이미지, 가격을 실제 플랫폼 보증처럼 표시하면 안 됩니다.",
            )
            RouteLocale.Es -> listOf(
                "El cliente no extrae contenido de plataformas de terceros.",
                "La busqueda global real requiere APIs oficiales, fuentes con licencia o agregacion backend.",
                "Popularidad, imagenes y precios mock no deben presentarse como avales reales de plataformas.",
            )
            RouteLocale.ZhCn -> listOf(
                "当前不抓取第三方平台内容。",
                "正式全球搜索需要官方 API、授权数据源或后端聚合。",
                "mock 热度、图片和价格不可当成真实平台背书。",
            )
        }
    }

    private fun poiName(poi: POI): String {
        val names = mapOf(
            "sz-bay-park-sunset" to listOf("Shenzhen Bay Park Sunset Walk", "深圳湾公園サンセット", "선전만 공원 일몰 산책", "Atardecer en Shenzhen Bay Park"),
            "sz-oct-loft" to listOf("OCT Loft Creative Park", "OCT Loft クリエイティブパーク", "OCT Loft 창의문화공원", "OCT Loft Creative Park"),
            "sz-museum" to listOf("Shenzhen Museum", "深圳博物館", "선전 박물관", "Museo de Shenzhen"),
            "shanghai-bund" to listOf("The Bund skyline walk", "外灘スカイライン散歩", "와이탄 스카이라인 산책", "Paseo por The Bund"),
            "shanghai-wukang" to listOf("Wukang Road Citywalk", "武康路 Citywalk", "우캉루 Citywalk", "Citywalk por Wukang Road"),
            "beijing-forbidden-city" to listOf("Forbidden City corner route", "故宮角楼ルート", "자금성 각루 루트", "Ruta de la Ciudad Prohibida"),
            "chengdu-taikooli" to listOf("Chengdu Taikoo Li slow walk", "成都太古里ゆっくり散歩", "청두 타이쿠리 산책", "Paseo por Taikoo Li Chengdu"),
            "hongkong-victoria-harbour" to listOf("Victoria Harbour night walk", "ビクトリアハーバー夜景散歩", "빅토리아 하버 야경 산책", "Paseo nocturno por Victoria Harbour"),
            "tokyo-shibuya-sky" to listOf("Shibuya Sky dusk view", "Shibuya Sky 黄昏ビュー", "시부야 스카이 석양 전망", "Atardecer en Shibuya Sky"),
            "tokyo-asakusa" to listOf("Asakusa to Sumida River walk", "浅草から隅田川散歩", "아사쿠사-스미다강 산책", "Paseo de Asakusa al río Sumida"),
            "seoul-yeonnam" to listOf("Yeonnam-dong cafe citywalk", "延南洞カフェ Citywalk", "연남동 카페 Citywalk", "Citywalk de cafés en Yeonnam"),
            "singapore-marina-bay" to listOf("Marina Bay night walk", "Marina Bay ナイトウォーク", "마리나 베이 야경 산책", "Paseo nocturno por Marina Bay"),
            "bangkok-iconsiam" to listOf("ICONSIAM riverside route", "ICONSIAM 川沿いルート", "아이콘시암 강변 루트", "Ruta ribereña ICONSIAM"),
            "paris-seine-louvre" to listOf("Louvre to Seine dusk walk", "ルーヴルからセーヌ川の夕暮れ", "루브르-센강 석양 산책", "Ruta del Louvre al Sena"),
            "london-south-bank" to listOf("South Bank riverside walk", "South Bank 川沿い散歩", "사우스뱅크 강변 산책", "Paseo por South Bank"),
            "newyork-brooklyn-bridge-dumbo" to listOf("Brooklyn Bridge to DUMBO", "Brooklyn Bridge から DUMBO", "브루클린 브리지-DUMBO", "Brooklyn Bridge a DUMBO"),
            "barcelona-gothic-quarter" to listOf("Gothic Quarter citywalk", "ゴシック地区 Citywalk", "고딕 지구 Citywalk", "Citywalk por el Barrio Gótico"),
            "dubai-burj-khalifa-fountain" to listOf("Burj Khalifa fountain night route", "ブルジュ・ハリファ噴水夜景", "부르즈 칼리파 분수 야경", "Fuente del Burj Khalifa de noche"),
        )
        val variants = names[poi.poiId] ?: return poi.name
        return when (locale) {
            RouteLocale.En -> variants[0]
            RouteLocale.Ja -> variants[1]
            RouteLocale.Ko -> variants[2]
            RouteLocale.Es -> variants[3]
            else -> poi.name
        }
    }

    private fun recommendationFor(poi: POI): String {
        return when (locale) {
            RouteLocale.En -> "A curated mock stop for ${poi.globalCategory.replace('_', ' ')} routes. Verify hours, prices, and crowd levels with official providers before production."
            RouteLocale.Ja -> "${poi.globalCategory.replace('_', ' ')} 向けの mock スポットです。本番では営業時間、料金、混雑を公式データで確認してください。"
            RouteLocale.Ko -> "${poi.globalCategory.replace('_', ' ')} 루트를 위한 mock 장소입니다. 실제 서비스 전 영업시간, 가격, 혼잡도를 공식 데이터로 확인해야 합니다."
            RouteLocale.Es -> "Parada mock curada para rutas de ${poi.globalCategory.replace('_', ' ')}. Verifica horarios, precios y aforo con proveedores oficiales."
            else -> poi.recommendationReason
        }
    }

    private fun riskTipsFor(poi: POI): List<String> {
        return when (locale) {
            RouteLocale.En -> listOf("Opening hours and prices need official verification", "Crowd level is only a mock estimate", "Use external maps for navigation")
            RouteLocale.Ja -> listOf("営業時間と料金は公式確認が必要", "混雑度は mock 推定です", "ナビは外部地図を使用")
            RouteLocale.Ko -> listOf("영업시간과 가격은 공식 확인 필요", "혼잡도는 mock 추정입니다", "내비게이션은 외부 지도를 사용")
            RouteLocale.Es -> listOf("Horarios y precios requieren verificación oficial", "El aforo es una estimación mock", "Usa mapas externos para navegar")
            else -> poi.riskTips
        }
    }

    private val countryNamesZhTw = mapOf(
        "China" to "中國",
        "Japan" to "日本",
        "South Korea" to "韓國",
        "United States" to "美國",
        "United Kingdom" to "英國",
        "United Arab Emirates" to "阿拉伯聯合大公國",
    )

    private val countryNamesJa = mapOf(
        "China" to "中国",
        "Japan" to "日本",
        "South Korea" to "韓国",
        "United States" to "米国",
        "United Kingdom" to "英国",
        "United Arab Emirates" to "アラブ首長国連邦",
    )

    private val countryNamesKo = mapOf(
        "China" to "중국",
        "Japan" to "일본",
        "South Korea" to "한국",
        "United States" to "미국",
        "United Kingdom" to "영국",
        "United Arab Emirates" to "아랍에미리트",
    )

    private val countryNamesEs = mapOf(
        "China" to "China",
        "Japan" to "Japon",
        "South Korea" to "Corea del Sur",
        "United States" to "Estados Unidos",
        "United Kingdom" to "Reino Unido",
        "United Arab Emirates" to "Emiratos Arabes Unidos",
    )

    private val cityNamesZhTw = mapOf(
        "深圳" to "深圳",
        "上海" to "上海",
        "北京" to "北京",
        "成都" to "成都",
        "香港" to "香港",
        "东京" to "東京",
        "首尔" to "首爾",
        "新加坡" to "新加坡",
        "曼谷" to "曼谷",
        "巴黎" to "巴黎",
        "伦敦" to "倫敦",
        "纽约" to "紐約",
        "巴塞罗那" to "巴塞隆納",
        "迪拜" to "杜拜",
    )

    private val cityNamesEn = mapOf(
        "深圳" to "Shenzhen",
        "上海" to "Shanghai",
        "北京" to "Beijing",
        "成都" to "Chengdu",
        "香港" to "Hong Kong",
        "东京" to "Tokyo",
        "首尔" to "Seoul",
        "新加坡" to "Singapore",
        "曼谷" to "Bangkok",
        "巴黎" to "Paris",
        "伦敦" to "London",
        "纽约" to "New York",
        "巴塞罗那" to "Barcelona",
        "迪拜" to "Dubai",
    )

    private val cityNamesJa = mapOf(
        "深圳" to "深セン",
        "上海" to "上海",
        "北京" to "北京",
        "成都" to "成都",
        "香港" to "香港",
        "东京" to "東京",
        "首尔" to "ソウル",
        "新加坡" to "シンガポール",
        "曼谷" to "バンコク",
        "巴黎" to "パリ",
        "伦敦" to "ロンドン",
        "纽约" to "ニューヨーク",
        "巴塞罗那" to "バルセロナ",
        "迪拜" to "ドバイ",
    )

    private val cityNamesKo = mapOf(
        "深圳" to "선전",
        "上海" to "상하이",
        "北京" to "베이징",
        "成都" to "청두",
        "香港" to "홍콩",
        "东京" to "도쿄",
        "首尔" to "서울",
        "新加坡" to "싱가포르",
        "曼谷" to "방콕",
        "巴黎" to "파리",
        "伦敦" to "런던",
        "纽约" to "뉴욕",
        "巴塞罗那" to "바르셀로나",
        "迪拜" to "두바이",
    )

    private val cityNamesEs = mapOf(
        "深圳" to "Shenzhen",
        "上海" to "Shanghai",
        "北京" to "Pekin",
        "成都" to "Chengdu",
        "香港" to "Hong Kong",
        "东京" to "Tokio",
        "首尔" to "Seul",
        "新加坡" to "Singapur",
        "曼谷" to "Bangkok",
        "巴黎" to "Paris",
        "伦敦" to "Londres",
        "纽约" to "Nueva York",
        "巴塞罗那" to "Barcelona",
        "迪拜" to "Dubai",
    )

    private val districtNamesZhTw = mapOf(
        "深圳:南山" to "南山",
        "深圳:福田" to "福田",
        "上海:黄浦" to "黃浦",
        "上海:徐汇" to "徐匯",
        "北京:东城" to "東城",
        "成都:锦江" to "錦江",
        "香港:尖沙咀" to "尖沙咀",
        "东京:涩谷" to "澀谷",
        "东京:台东" to "台東",
        "首尔:麻浦" to "麻浦",
    )

    private val districtNamesEn = mapOf(
        "深圳:南山" to "Nanshan",
        "深圳:福田" to "Futian",
        "上海:黄浦" to "Huangpu",
        "上海:徐汇" to "Xuhui",
        "北京:东城" to "Dongcheng",
        "成都:锦江" to "Jinjiang",
        "香港:尖沙咀" to "Tsim Sha Tsui",
        "东京:涩谷" to "Shibuya",
        "东京:台东" to "Taito",
        "首尔:麻浦" to "Mapo",
    )

    private val districtNamesJa = mapOf(
        "深圳:南山" to "南山区",
        "深圳:福田" to "福田区",
        "上海:黄浦" to "黄浦区",
        "上海:徐汇" to "徐匯区",
        "北京:东城" to "東城区",
        "成都:锦江" to "錦江区",
        "香港:尖沙咀" to "尖沙咀",
        "东京:涩谷" to "渋谷",
        "东京:台东" to "台東",
        "首尔:麻浦" to "麻浦",
    )

    private val districtNamesKo = mapOf(
        "深圳:南山" to "난산",
        "深圳:福田" to "푸톈",
        "上海:黄浦" to "황푸",
        "上海:徐汇" to "쉬후이",
        "北京:东城" to "둥청",
        "成都:锦江" to "진장",
        "香港:尖沙咀" to "침사추이",
        "东京:涩谷" to "시부야",
        "东京:台东" to "다이토",
        "首尔:麻浦" to "마포",
    )

    private val districtNamesEs = mapOf(
        "深圳:南山" to "Nanshan",
        "深圳:福田" to "Futian",
        "上海:黄浦" to "Huangpu",
        "上海:徐汇" to "Xuhui",
        "北京:东城" to "Dongcheng",
        "成都:锦江" to "Jinjiang",
        "香港:尖沙咀" to "Tsim Sha Tsui",
        "东京:涩谷" to "Shibuya",
        "东京:台东" to "Taito",
        "首尔:麻浦" to "Mapo",
    )

    private val addressNamesZhTw = emptyMap<String, String>()
    private val addressNamesEn = emptyMap<String, String>()
    private val addressNamesJa = emptyMap<String, String>()
    private val addressNamesKo = emptyMap<String, String>()
    private val addressNamesEs = emptyMap<String, String>()

    private val tagNamesZhTw = mapOf(
        "情侣" to "情侶",
        "朋友" to "朋友",
        "家人" to "家人",
        "亲子友好" to "親子友好",
        "日落" to "日落",
        "夜景" to "夜景",
        "散步" to "散步",
        "拍照" to "拍照",
        "低预算" to "低預算",
        "轻松" to "輕鬆",
        "看展" to "看展",
        "咖啡" to "咖啡",
        "Citywalk" to "Citywalk",
        "探店" to "探店",
        "雨天备选" to "雨天備選",
        "室内" to "室內",
        "经典景点" to "經典景點",
        "文化" to "文化",
        "一日游" to "一日遊",
        "吃饭" to "吃飯",
        "纪念日" to "紀念日",
        "室内备选" to "室內備選",
    )

    private val tagNamesEn = mapOf(
        "情侣" to "couples",
        "朋友" to "friends",
        "家人" to "family",
        "亲子友好" to "kid-friendly",
        "日落" to "sunset",
        "夜景" to "night view",
        "散步" to "walking",
        "拍照" to "photos",
        "低预算" to "low budget",
        "轻松" to "easy pace",
        "看展" to "exhibitions",
        "咖啡" to "cafes",
        "Citywalk" to "citywalk",
        "探店" to "local shops",
        "雨天备选" to "rain backup",
        "室内" to "indoor",
        "经典景点" to "classic sights",
        "文化" to "culture",
        "一日游" to "day trip",
        "吃饭" to "food",
        "纪念日" to "anniversary",
        "室内备选" to "indoor backup",
    )

    private val tagNamesJa = mapOf(
        "情侣" to "カップル",
        "朋友" to "友達",
        "家人" to "家族",
        "亲子友好" to "親子向け",
        "日落" to "夕日",
        "夜景" to "夜景",
        "散步" to "散歩",
        "拍照" to "写真",
        "低预算" to "低予算",
        "轻松" to "ゆったり",
        "看展" to "展示",
        "咖啡" to "カフェ",
        "Citywalk" to "街歩き",
        "探店" to "店巡り",
        "雨天备选" to "雨の日候補",
        "室内" to "屋内",
        "经典景点" to "定番スポット",
        "文化" to "文化",
        "一日游" to "日帰り",
        "吃饭" to "食事",
        "纪念日" to "記念日",
        "室内备选" to "屋内候補",
    )

    private val tagNamesKo = mapOf(
        "情侣" to "커플",
        "朋友" to "친구",
        "家人" to "가족",
        "亲子友好" to "아이 동반",
        "日落" to "일몰",
        "夜景" to "야경",
        "散步" to "산책",
        "拍照" to "사진",
        "低预算" to "저예산",
        "轻松" to "느긋함",
        "看展" to "전시",
        "咖啡" to "카페",
        "Citywalk" to "시티워크",
        "探店" to "핫플 탐방",
        "雨天备选" to "우천 대안",
        "室内" to "실내",
        "经典景点" to "대표 명소",
        "文化" to "문화",
        "一日游" to "당일 여행",
        "吃饭" to "식사",
        "纪念日" to "기념일",
        "室内备选" to "실내 대안",
    )

    private val tagNamesEs = mapOf(
        "情侣" to "parejas",
        "朋友" to "amigos",
        "家人" to "familia",
        "亲子友好" to "apto para ninos",
        "日落" to "atardecer",
        "夜景" to "vista nocturna",
        "散步" to "paseo",
        "拍照" to "fotos",
        "低预算" to "bajo presupuesto",
        "轻松" to "ritmo relajado",
        "看展" to "exposiciones",
        "咖啡" to "cafes",
        "Citywalk" to "citywalk",
        "探店" to "tiendas locales",
        "雨天备选" to "plan con lluvia",
        "室内" to "interior",
        "经典景点" to "sitios clasicos",
        "文化" to "cultura",
        "一日游" to "excursion de un dia",
        "吃饭" to "comida",
        "纪念日" to "aniversario",
        "室内备选" to "alternativa interior",
    )

    private fun t(zhCn: String, zhTw: String, en: String, ja: String, ko: String, es: String): String {
        return when (locale) {
            RouteLocale.ZhCn -> zhCn
            RouteLocale.ZhTw -> zhTw
            RouteLocale.En -> en
            RouteLocale.Ja -> ja
            RouteLocale.Ko -> ko
            RouteLocale.Es -> es
        }
    }

    val safeDefaultCity: String
        get() = when (locale) {
            RouteLocale.ZhCn -> "全球热门城市"
            RouteLocale.ZhTw -> "全球熱門城市"
            RouteLocale.En -> "Global popular cities"
            RouteLocale.Ja -> "世界の人気都市"
            RouteLocale.Ko -> "전 세계 인기 도시"
            RouteLocale.Es -> "Ciudades populares globales"
        }

    val safeDefaultTransport: String
        get() = when (locale) {
            RouteLocale.ZhCn -> "公共交通 / 步行"
            RouteLocale.ZhTw -> "公共交通 / 步行"
            RouteLocale.En -> "Public transit / walking"
            RouteLocale.Ja -> "公共交通 / 徒歩"
            RouteLocale.Ko -> "대중교통 / 도보"
            RouteLocale.Es -> "Transporte publico / caminar"
        }

    val safePrimaryUserLabel: String
        get() = when (locale) {
            RouteLocale.ZhCn -> "发起人"
            RouteLocale.ZhTw -> "發起人"
            RouteLocale.En -> "Planner"
            RouteLocale.Ja -> "計画する人"
            RouteLocale.Ko -> "계획자"
            RouteLocale.Es -> "Organizador"
        }

    val safeCompanionUserLabel: String
        get() = when (locale) {
            RouteLocale.ZhCn -> "同行人"
            RouteLocale.ZhTw -> "同行人"
            RouteLocale.En -> "Companion"
            RouteLocale.Ja -> "同行者"
            RouteLocale.Ko -> "동행자"
            RouteLocale.Es -> "Acompanante"
        }

    val safeRewardPolicy: String
        get() = when (locale) {
            RouteLocale.ZhCn -> "每完成一个地点打卡可获得 20-35 本地积分。正式权益兑换必须由服务端校验。"
            RouteLocale.ZhTw -> "每完成一個地點打卡可獲得 20-35 本地積分。正式權益兌換必須由服務端校驗。"
            RouteLocale.En -> "Each stop check-in grants 20-35 local points. Real benefit redemption must be verified by the backend."
            RouteLocale.Ja -> "各スポットのチェックインで 20-35 ローカルポイントを獲得できます。実際の特典交換にはサーバー検証が必要です。"
            RouteLocale.Ko -> "각 장소 체크인마다 20-35 로컬 포인트를 받을 수 있습니다. 실제 혜택 교환은 서버 검증이 필요합니다."
            RouteLocale.Es -> "Cada check-in otorga 20-35 puntos locales. El canje real de beneficios debe verificarse en el backend."
        }

    val safeComplianceSummary: String
        get() = when (locale) {
            RouteLocale.ZhCn -> "当前使用全球 mock POI 目录验证产品结构；正式全球搜索必须接入官方 API、授权内容源或后端服务。"
            RouteLocale.ZhTw -> "目前使用全球 mock POI 目錄驗證產品結構；正式全球搜尋必須接入官方 API、授權內容源或後端服務。"
            RouteLocale.En -> "This build uses a global mock POI catalog to validate the product structure. Production global search requires official APIs, licensed sources, or backend services."
            RouteLocale.Ja -> "このビルドはグローバル mock POI カタログで製品構造を検証しています。本番のグローバル検索には公式 API、ライセンス済みソース、またはバックエンドサービスが必要です。"
            RouteLocale.Ko -> "이 빌드는 글로벌 mock POI 카탈로그로 제품 구조를 검증합니다. 정식 글로벌 검색에는 공식 API, 라이선스 소스 또는 백엔드 서비스가 필요합니다."
            RouteLocale.Es -> "Esta version usa un catalogo global mock de POI para validar la estructura. La busqueda global real requiere APIs oficiales, fuentes con licencia o backend."
        }

    val safeComplianceLimitations: List<String>
        get() = when (locale) {
            RouteLocale.ZhCn -> listOf("当前不是全球全量数据库，只是可运行样例和正式数据结构。", "社交热度、营业时间、价格、人流和图片都需要官方或授权数据源。", "地图跳转只打开外部地图，不读取定位，也不做强制到店校验。")
            RouteLocale.ZhTw -> listOf("目前不是全球全量資料庫，只是可運行樣例和正式資料結構。", "社交熱度、營業時間、價格、人流和圖片都需要官方或授權資料源。", "地圖跳轉只打開外部地圖，不讀取定位，也不做強制到店校驗。")
            RouteLocale.En -> listOf("This is not a full global database; it is a runnable sample and production data structure.", "Social heat, opening hours, prices, crowd levels, and images need authorized providers.", "Map actions only open external maps; the app does not read location or force arrival verification.")
            RouteLocale.Ja -> listOf("これは完全な世界データベースではなく、動作するサンプルと本番向けデータ構造です。", "SNS 人気度、営業時間、価格、混雑、画像は公式または許諾済みプロバイダーが必要です。", "地図操作は外部地図を開くだけで、位置情報取得や到着の強制確認は行いません。")
            RouteLocale.Ko -> listOf("전체 글로벌 데이터베이스가 아니라 실행 가능한 샘플과 정식 데이터 구조입니다.", "소셜 인기, 영업시간, 가격, 혼잡도, 이미지는 공식 또는 승인된 제공자가 필요합니다.", "지도 동작은 외부 지도를 열 뿐 위치를 읽거나 도착을 강제 검증하지 않습니다.")
            RouteLocale.Es -> listOf("No es una base global completa; es una muestra ejecutable y una estructura de produccion.", "Popularidad social, horarios, precios, aforo e imagenes requieren proveedores autorizados.", "Los mapas solo abren apps externas; no se lee ubicacion ni se fuerza verificacion de llegada.")
        }

    fun safeLocalizeSourcePolicy(policy: SourcePolicy): SourcePolicy {
        if (locale == RouteLocale.ZhCn) return policy
        return SourcePolicy(
            policyTitle = when (locale) {
                RouteLocale.ZhTw -> "內容來源政策"
                RouteLocale.En -> "Content source policy"
                RouteLocale.Ja -> "コンテンツソースポリシー"
                RouteLocale.Ko -> "콘텐츠 출처 정책"
                RouteLocale.Es -> "Politica de fuentes de contenido"
                RouteLocale.ZhCn -> policy.policyTitle
            },
            policyNotes = when (locale) {
                RouteLocale.ZhTw -> listOf("目前不抓取第三方平台內容。", "正式全球搜尋需要官方 API、授權資料源或後端聚合。", "mock 熱度、圖片和價格不可當成真實平台背書。")
                RouteLocale.En -> listOf("The client does not scrape third-party platforms.", "Production global search requires official APIs, licensed sources, or backend aggregation.", "Mock heat, images, and prices must not be presented as real platform endorsements.")
                RouteLocale.Ja -> listOf("クライアントは第三者プラットフォームをスクレイピングしません。", "本番のグローバル検索には公式 API、ライセンス済みデータ、またはバックエンド集約が必要です。", "mock の人気度、画像、価格を実在プラットフォームの保証として表示しません。")
                RouteLocale.Ko -> listOf("클라이언트는 제3자 플랫폼을 스크래핑하지 않습니다.", "정식 글로벌 검색에는 공식 API, 라이선스 데이터 또는 백엔드 집계가 필요합니다.", "mock 인기, 이미지, 가격을 실제 플랫폼 보증처럼 표시하면 안 됩니다.")
                RouteLocale.Es -> listOf("El cliente no extrae contenido de plataformas de terceros.", "La busqueda global real requiere APIs oficiales, fuentes con licencia o agregacion backend.", "Popularidad, imagenes y precios mock no deben presentarse como avales reales de plataformas.")
                RouteLocale.ZhCn -> policy.policyNotes
            },
        )
    }

    fun safeCoverageNoteFor(isMock: Boolean, rawNote: String): String {
        if (locale == RouteLocale.En && rawNote.isNotBlank()) return rawNote
        return if (isMock) {
            when (locale) {
                RouteLocale.ZhCn -> "全球实时搜索尚未配置；当前使用本地 mock 地点库验证路线结构。正式覆盖必须接入官方 API、授权数据、商家合作、用户授权分享链接或后端聚合服务。"
                RouteLocale.ZhTw -> "全球即時搜尋尚未配置；目前使用本地 mock 地點庫驗證路線結構。正式覆蓋必須接入官方 API、授權資料、商家合作、用戶授權分享連結或後端聚合服務。"
                RouteLocale.En -> "Global live search is not configured yet. The app is using a local mock catalog for route structure testing. Production global search must use official APIs, licensed data, merchant partnerships, user-authorized links, or backend aggregation."
                RouteLocale.Ja -> "グローバルのライブ検索はまだ設定されていません。現在はローカル mock カタログでルート構造を検証しています。本番検索には公式 API、ライセンス済みデータ、店舗連携、ユーザー承認済み共有リンク、またはバックエンド集約が必要です。"
                RouteLocale.Ko -> "글로벌 실시간 검색은 아직 설정되지 않았습니다. 현재는 로컬 mock 카탈로그로 경로 구조를 검증합니다. 정식 글로벌 검색에는 공식 API, 라이선스 데이터, 제휴 매장, 사용자 승인 공유 링크 또는 백엔드 집계가 필요합니다."
                RouteLocale.Es -> "La busqueda global en vivo aun no esta configurada. La app usa un catalogo local mock para probar la estructura de rutas. La busqueda real debe usar APIs oficiales, datos con licencia, alianzas comerciales, enlaces autorizados por usuarios o agregacion backend."
            }
        } else {
            when (locale) {
                RouteLocale.ZhCn -> "远端旅行内容端点已配置；所有响应仍需后端完成来源政策、授权、时效和图片许可校验。"
                RouteLocale.ZhTw -> "遠端旅行內容端點已配置；所有回應仍需後端完成來源政策、授權、時效與圖片許可校驗。"
                RouteLocale.En -> rawNote.ifBlank { "Remote travel content is configured. Continue verifying source policy, licensing, freshness, and image metadata." }
                RouteLocale.Ja -> "リモート旅行コンテンツのエンドポイントは設定済みです。すべてのレスポンスはバックエンドで出典ポリシー、ライセンス、鮮度、画像許諾を検証する必要があります。"
                RouteLocale.Ko -> "원격 여행 콘텐츠 엔드포인트가 설정되었습니다. 모든 응답은 백엔드에서 출처 정책, 라이선스, 최신성, 이미지 권한을 계속 검증해야 합니다."
                RouteLocale.Es -> "El endpoint remoto de contenido de viaje esta configurado. Las respuestas aun deben verificarse en backend por politica de fuente, licencia, vigencia y permisos de imagen."
            }
        }
    }

    fun safeGroupLabelFor(relationship: String): String {
        return when {
            isCouple(relationship) -> when (locale) {
                RouteLocale.ZhCn -> "双人情侣旅行"
                RouteLocale.ZhTw -> "雙人情侶旅行"
                RouteLocale.En -> "Couple route"
                RouteLocale.Ja -> "カップルルート"
                RouteLocale.Ko -> "커플 루트"
                RouteLocale.Es -> "Ruta de pareja"
            }
            isFriends(relationship) -> when (locale) {
                RouteLocale.ZhCn -> "朋友 Citywalk"
                RouteLocale.ZhTw -> "朋友 Citywalk"
                RouteLocale.En -> "Friends citywalk"
                RouteLocale.Ja -> "友達 citywalk"
                RouteLocale.Ko -> "친구 시티워크"
                RouteLocale.Es -> "Citywalk con amigos"
            }
            isFamily(relationship) -> when (locale) {
                RouteLocale.ZhCn -> "亲子家庭出行"
                RouteLocale.ZhTw -> "親子家庭出行"
                RouteLocale.En -> "Family route"
                RouteLocale.Ja -> "家族ルート"
                RouteLocale.Ko -> "가족 루트"
                RouteLocale.Es -> "Ruta familiar"
            }
            else -> when (locale) {
                RouteLocale.ZhCn -> "今日探索路线"
                RouteLocale.ZhTw -> "今日探索路線"
                RouteLocale.En -> "Today's route"
                RouteLocale.Ja -> "今日のルート"
                RouteLocale.Ko -> "오늘의 루트"
                RouteLocale.Es -> "Ruta de hoy"
            }
        }
    }

    fun safeConflictNotes(primary: UserPreference, companion: UserPreference): List<String> = buildList {
        if (primary.budget.contains("0") || primary.budget.contains("50")) add(localizedBudgetNote())
        if (primary.timeWindow.contains("30") || primary.timeWindow.contains("90")) add(localizedShortTimeNote())
        if (companion.interests.any { it.contains("拍照") || it.contains("鎷") }) add(localizedPhotoBalanceNote())
    }

    fun safePlanTypeFor(groupPreference: GroupPreference): String {
        return when {
            groupPreference.mergedTimeWindow.contains("90") -> localizedText("下班后 90 分钟", "下班後 90 分鐘", "90 minutes after work", "仕事後90分", "퇴근 후 90분", "90 minutos despues del trabajo")
            isCouple(groupPreference.relationshipType) -> localizedText("周末情侣路线", "週末情侶路線", "Weekend couple route", "週末カップルルート", "주말 커플 루트", "Ruta de pareja de fin de semana")
            isFriends(groupPreference.relationshipType) -> localizedText("朋友 Citywalk", "朋友 Citywalk", "Friends citywalk", "友達 citywalk", "친구 시티워크", "Citywalk con amigos")
            isFamily(groupPreference.relationshipType) -> localizedText("亲子家庭路线", "親子家庭路線", "Family route", "家族ルート", "가족 루트", "Ruta familiar")
            else -> localizedText("今日路线副本", "今日路線副本", "Today's route quest", "今日のルートクエスト", "오늘의 루트 퀘스트", "Ruta quest de hoy")
        }
    }

    fun safeTitleFor(groupPreference: GroupPreference, pois: List<POI>, planType: String): String {
        val city = pois.firstOrNull()?.city ?: groupPreference.mergedCity
        val district = pois.firstOrNull()?.district ?: localizedText("精选", "精選", "curated", "厳選", "엄선", "seleccion")
        return when (locale) {
            RouteLocale.Es -> "$planType: $city $district"
            else -> "$city $district $planType"
        }
    }

    fun safeRouteSummaryFor(groupPreference: GroupPreference, pois: List<POI>): String {
        val names = pois.joinToString(" -> ") { it.name }
        val label = safeGroupLabelFor(groupPreference.relationshipType)
        return when (locale) {
            RouteLocale.ZhCn -> "已根据${label}的时间、预算和出行方式整理路线：$names。"
            RouteLocale.ZhTw -> "已根據${label}的時間、預算和出行方式整理路線：$names。"
            RouteLocale.En -> "Built for $label preferences: $names."
            RouteLocale.Ja -> "$label の希望に合わせて組んだルートです：$names。"
            RouteLocale.Ko -> "$label 취향에 맞춘 경로입니다: $names."
            RouteLocale.Es -> "Ruta creada para $label: $names."
        }
    }

    fun safeEstimateCost(pois: List<POI>, budget: String): String {
        val base = when {
            budget.contains("0") -> localizedText("0-80 / 人", "0-80 / 人", "0-80 per person", "1人 0-80", "1인 0-80", "0-80 por persona")
            budget.contains("50") -> localizedText("50-120 / 人", "50-120 / 人", "50-120 per person", "1人 50-120", "1인 50-120", "50-120 por persona")
            budget.contains("100") -> localizedText("80-180 / 人", "80-180 / 人", "80-180 per person", "1人 80-180", "1인 80-180", "80-180 por persona")
            budget.contains("300") -> localizedText("150-320 / 人", "150-320 / 人", "150-320 per person", "1人 150-320", "1인 150-320", "150-320 por persona")
            else -> localizedText("按现场消费", "按現場消費", "depends on on-site spending", "現地消費による", "현장 소비에 따름", "segun consumo local")
        }
        val cities = pois.map { it.city }.distinct().joinToString(" / ")
        return when (locale) {
            RouteLocale.ZhCn -> "$base，$cities 的真实价格需由地图、商家或票务 API 校验。"
            RouteLocale.ZhTw -> "$base，$cities 的真實價格需由地圖、商家或票務 API 校驗。"
            RouteLocale.En -> "$base. Real prices in $cities must be verified by maps, merchants, or ticketing APIs."
            RouteLocale.Ja -> "$base。$cities の実価格は地図、店舗、チケット API で確認してください。"
            RouteLocale.Ko -> "$base. $cities 의 실제 가격은 지도, 매장 또는 티켓 API로 확인해야 합니다."
            RouteLocale.Es -> "$base. Los precios reales en $cities deben verificarse con mapas, comercios o APIs de tickets."
        }
    }

    fun safeCrowdRiskFor(pois: List<POI>): String {
        val highRisk = pois.any { poi -> poi.riskTips.any { it.contains("crowd", true) || it.contains("人流") || it.contains("混雑") || it.contains("혼잡") } }
        return if (highRisk) {
            localizedText("中到高：热门机位、节假日和傍晚时段可能拥挤。", "中到高：熱門機位、節假日和傍晚時段可能擁擠。", "Medium to high: popular photo spots, holidays, and dusk may be crowded.", "中から高：人気の撮影位置、休日、夕方は混雑する可能性があります。", "중간~높음: 인기 촬영 포인트, 휴일, 해질녘에는 혼잡할 수 있습니다.", "Media a alta: puntos populares, festivos y atardecer pueden estar concurridos.")
        } else {
            localizedText("低到中：仍建议核验当地节假日、天气和营业时间。", "低到中：仍建議核驗當地節假日、天氣和營業時間。", "Low to medium: still verify local holidays, weather, and opening hours.", "低から中：現地の祝日、天気、営業時間は確認してください。", "낮음~중간: 현지 휴일, 날씨, 영업시간은 확인하세요.", "Baja a media: confirma festivos, clima y horarios locales.")
        }
    }

    fun safeRainBackupFor(pois: List<POI>): String {
        val indoor = pois.firstOrNull { poi -> poi.tags.any { it.contains("indoor", true) || it.contains("室内") || it.contains("屋内") || it.contains("실내") } }
        return if (indoor != null) {
            when (locale) {
                RouteLocale.ZhCn -> "下雨时优先切换到 ${indoor.name}，减少户外停留。"
                RouteLocale.ZhTw -> "下雨時優先切換到 ${indoor.name}，減少戶外停留。"
                RouteLocale.En -> "If it rains, switch first to ${indoor.name} and reduce outdoor time."
                RouteLocale.Ja -> "雨の場合はまず ${indoor.name} に切り替え、屋外滞在を短くします。"
                RouteLocale.Ko -> "비가 오면 먼저 ${indoor.name} 으로 전환하고 야외 시간을 줄이세요."
                RouteLocale.Es -> "Si llueve, cambia primero a ${indoor.name} y reduce el tiempo al aire libre."
            }
        } else {
            localizedText("下雨时缩短户外拍照，改成咖啡店、商场、书店、博物馆或室内展览。", "下雨時縮短戶外拍照，改成咖啡店、商場、書店、博物館或室內展覽。", "On rainy days, shorten outdoor photos and switch to cafes, malls, bookstores, museums, or indoor exhibitions.", "雨の日は屋外撮影を短くし、カフェ、商業施設、書店、博物館、屋内展示に切り替えます。", "비 오는 날에는 야외 촬영을 줄이고 카페, 쇼핑몰, 서점, 박물관, 실내 전시로 전환하세요.", "Con lluvia, reduce fotos al aire libre y cambia a cafes, centros comerciales, librerias, museos o exposiciones interiores.")
        }
    }

    fun safeBestPhotoTimeFor(groupPreference: GroupPreference): String {
        return when {
            groupPreference.mergedInterests.any { it.contains("夜景") || it.contains("night", true) } -> "19:00-21:00"
            isCouple(groupPreference.relationshipType) -> localizedText("日落前 40 分钟到蓝调时刻", "日落前 40 分鐘到藍調時刻", "40 minutes before sunset through blue hour", "日没40分前からブルーアワー", "일몰 40분 전부터 블루아워", "40 minutos antes del atardecer hasta la hora azul")
            else -> localizedText("下午 16:00-18:00 光线更柔和", "下午 16:00-18:00 光線更柔和", "16:00-18:00, when the light is softer", "16:00-18:00 の柔らかい光", "16:00-18:00 부드러운 빛", "16:00-18:00, luz mas suave")
        }
    }

    fun safeCheckInTitle(name: String): String = when (locale) {
        RouteLocale.ZhCn -> "在 $name 完成打卡"
        RouteLocale.ZhTw -> "在 $name 完成打卡"
        RouteLocale.En -> "Check in at $name"
        RouteLocale.Ja -> "$name でチェックイン"
        RouteLocale.Ko -> "$name 체크인"
        RouteLocale.Es -> "Check-in en $name"
    }

    fun safeCheckInDescriptionFor(relationship: String): String {
        return when {
            isCouple(relationship) -> localizedText("拍一张不露脸氛围照或手动确认到达，获得心动积分。", "拍一張不露臉氛圍照或手動確認到達，獲得心動積分。", "Take a no-face mood photo or confirm arrival manually to earn points.", "顔を出さない雰囲気写真、または手動到着確認でポイントを獲得します。", "얼굴 없는 분위기 사진을 찍거나 수동 도착 확인으로 포인트를 받으세요.", "Toma una foto de ambiente sin rostro o confirma llegada para ganar puntos.")
            isFriends(relationship) -> localizedText("完成一张搞笑合照、店门口打卡或手动确认到达。", "完成一張搞笑合照、店門口打卡或手動確認到達。", "Take a funny group shot, storefront check-in, or manual arrival confirmation.", "面白い集合写真、店頭チェックイン、または手動到着確認をします。", "재미있는 단체 사진, 매장 앞 체크인 또는 수동 도착 확인을 하세요.", "Haz una foto divertida, check-in en la entrada o confirmacion manual.")
            isFamily(relationship) -> localizedText("记录一张陪伴照片或手动确认到达，给今天留个存档。", "記錄一張陪伴照片或手動確認到達，給今天留個存檔。", "Save a family moment or confirm arrival manually to archive the day.", "一緒に過ごす写真、または手動到着確認で今日を記録します。", "함께한 순간을 저장하거나 수동 도착 확인으로 오늘을 기록하세요.", "Guarda una foto de compania o confirma llegada para archivar el dia.")
            else -> localizedText("到达后手动打卡，记录今天的路线进度。", "到達後手動打卡，記錄今天的路線進度。", "Check in manually after arrival to record route progress.", "到着後に手動チェックインしてルート進行を記録します。", "도착 후 수동 체크인으로 경로 진행을 기록하세요.", "Haz check-in manual al llegar para registrar el progreso.")
        }
    }

    fun safePhotoSuggestionFor(relationship: String, poi: POI): String {
        val suggestion = when {
            isCouple(relationship) -> localizedText("试试影子、背影、两杯饮料或路灯下的同框。", "試試影子、背影、兩杯飲料或路燈下的同框。", "Try shadows, backs, two drinks, or a shared frame under streetlights.", "影、後ろ姿、二つの飲み物、街灯下の同じフレームがおすすめです。", "그림자, 뒷모습, 두 잔의 음료, 가로등 아래 같은 프레임을 시도해 보세요.", "Prueba sombras, espaldas, dos bebidas o un encuadre bajo faroles.")
            isFriends(relationship) -> localizedText("每个人模仿一个路人海报动作，选最离谱的一张当封面。", "每個人模仿一個路人海報動作，選最離譜的一張當封面。", "Everyone copies a street-poster pose; keep the most absurd one as cover.", "全員で街ポスター風のポーズをまねして、一番面白い一枚を表紙にします。", "각자 거리 포스터 포즈를 따라 하고 가장 웃긴 사진을 표지로 남기세요.", "Cada persona imita una pose de poster; guarda la mas absurda.")
            isFamily(relationship) -> localizedText("拍走路背影、桌面小物或大家都舒服的自然合照。", "拍走路背影、桌面小物或大家都舒服的自然合照。", "Capture walking backs, table details, or a relaxed natural group photo.", "歩く後ろ姿、テーブルの小物、自然な集合写真を撮ります。", "걷는 뒷모습, 테이블 디테일, 편안한 단체 사진을 남기세요.", "Captura espaldas caminando, detalles de mesa o una foto natural.")
            else -> localizedText("拍一张能证明今天来过这里的小证据。", "拍一張能證明今天來過這裡的小證據。", "Take one small proof that you were here today.", "今日ここに来た小さな証拠を一枚撮ります。", "오늘 이곳에 왔다는 작은 증거를 남기세요.", "Toma una pequena prueba de que estuviste aqui hoy.")
        }
        return when (locale) {
            RouteLocale.ZhCn -> "$suggestion 推荐机位：${poi.imagePlaceholder}。"
            RouteLocale.ZhTw -> "$suggestion 推薦機位：${poi.imagePlaceholder}。"
            RouteLocale.En -> "$suggestion Suggested shot: a clear landmark, table detail, or entrance sign."
            RouteLocale.Ja -> "$suggestion おすすめ構図：目印、テーブルのディテール、入口サイン。"
            RouteLocale.Ko -> "$suggestion 추천 구도: 랜드마크, 테이블 디테일 또는 입구 표지."
            RouteLocale.Es -> "$suggestion Toma sugerida: un punto visible, detalle de mesa o letrero de entrada."
        }
    }

    fun safeSpendingSuggestionFor(poi: POI, budget: String): String {
        return if (budget.contains("0") || budget.contains("50")) {
            localizedText("优先免费散步和小额饮品，不为了打卡强消费。", "優先免費散步和小額飲品，不為了打卡強消費。", "Prioritize free walking and small drinks; do not force spending for check-ins.", "無料散歩と少額ドリンクを優先し、チェックインのために無理な消費はしません。", "무료 산책과 작은 음료를 우선하고 체크인을 위해 무리한 소비를 하지 마세요.", "Prioriza caminatas gratis y bebidas pequenas; no fuerces consumo por check-in.")
        } else {
            when (locale) {
                RouteLocale.ZhCn -> "可把消费集中在这一站，其他站以散步和拍照为主。地点预算等级：${poi.budgetLevel}。"
                RouteLocale.ZhTw -> "可把消費集中在這一站，其他站以散步和拍照為主。地點預算等級：${poi.budgetLevel}。"
                RouteLocale.En -> "Put most spending at this stop; keep other stops for walking and photos. Budget level: ${poi.budgetLevel}."
                RouteLocale.Ja -> "消費はこのスポットに集中し、他は散歩と写真中心にします。予算レベル：${poi.budgetLevel}。"
                RouteLocale.Ko -> "소비는 이 장소에 집중하고 다른 곳은 산책과 사진 위주로 진행하세요. 예산 등급: ${poi.budgetLevel}."
                RouteLocale.Es -> "Concentra el gasto en esta parada y deja las demas para caminar y fotos. Nivel de presupuesto: ${poi.budgetLevel}."
            }
        }
    }

    fun safeBackupPlanFor(poi: POI): String {
        val risk = poi.riskTips.firstOrNull()
        return if (risk != null) {
            when (locale) {
                RouteLocale.ZhCn -> "如果遇到：$risk，就缩短停留或切换到附近室内备选。"
                RouteLocale.ZhTw -> "如果遇到：$risk，就縮短停留或切換到附近室內備選。"
                RouteLocale.En -> "If this happens: $risk, shorten the stay or switch to a nearby indoor backup."
                RouteLocale.Ja -> "$risk の場合は滞在を短くし、近くの屋内候補に切り替えます。"
                RouteLocale.Ko -> "$risk 상황이면 머무는 시간을 줄이거나 가까운 실내 대안으로 이동하세요."
                RouteLocale.Es -> "Si ocurre: $risk, acorta la parada o cambia a una alternativa interior cercana."
            }
        } else {
            localizedText("如果不适合停留，就只完成手动打卡并前往下一站。", "如果不適合停留，就只完成手動打卡並前往下一站。", "If it is not comfortable to stay, check in manually and move to the next stop.", "滞在しづらい場合は手動チェックインだけ行い、次へ進みます。", "머물기 어렵다면 수동 체크인만 하고 다음 장소로 이동하세요.", "Si no conviene quedarse, haz check-in manual y sigue a la proxima parada.")
        }
    }

    fun safeWhyForGroup(groupPreference: GroupPreference, poi: POI): String {
        val matched = poi.tags.take(2).joinToString(" / ")
        return when (locale) {
            RouteLocale.ZhCn -> "匹配 ${groupPreference.groupLabel} 的 $matched 需求。"
            RouteLocale.ZhTw -> "匹配 ${groupPreference.groupLabel} 的 $matched 需求。"
            RouteLocale.En -> "Matches the group's $matched needs."
            RouteLocale.Ja -> "グループの $matched ニーズに合います。"
            RouteLocale.Ko -> "그룹의 $matched 니즈와 잘 맞습니다."
            RouteLocale.Es -> "Encaja con las necesidades de $matched del grupo."
        }
    }

    fun safePoiName(poi: POI): String {
        val names = mapOf(
            "sz-bay-park-sunset" to listOf("深圳湾公园日落散步", "深圳灣公園日落散步", "Shenzhen Bay Park Sunset Walk", "深セン湾公園サンセット散歩", "선전만 공원 일몰 산책", "Atardecer en Shenzhen Bay Park"),
            "sz-oct-loft" to listOf("华侨城创意文化园", "華僑城創意文化園", "OCT Loft Creative Park", "OCT Loft クリエイティブパーク", "OCT Loft 크리에이티브 파크", "OCT Loft Creative Park"),
            "sz-museum" to listOf("深圳博物馆", "深圳博物館", "Shenzhen Museum", "深セン博物館", "선전 박물관", "Museo de Shenzhen"),
            "shanghai-bund" to listOf("外滩天际线散步", "外灘天際線散步", "The Bund skyline walk", "外灘スカイライン散歩", "와이탄 스카이라인 산책", "Paseo por The Bund"),
            "shanghai-wukang" to listOf("武康路 Citywalk", "武康路 Citywalk", "Wukang Road Citywalk", "武康路 Citywalk", "우캉루 Citywalk", "Citywalk por Wukang Road"),
            "beijing-forbidden-city" to listOf("故宫角楼路线", "故宮角樓路線", "Forbidden City corner route", "故宮角楼ルート", "자금성 모서리 루트", "Ruta de la Ciudad Prohibida"),
            "chengdu-taikooli" to listOf("成都太古里慢走", "成都太古里慢走", "Chengdu Taikoo Li slow walk", "成都太古里ゆっくり散歩", "청두 타이쿠리 산책", "Paseo por Taikoo Li Chengdu"),
            "hongkong-victoria-harbour" to listOf("维港夜景散步", "維港夜景散步", "Victoria Harbour night walk", "ビクトリアハーバー夜景散歩", "빅토리아 하버 야경 산책", "Paseo nocturno por Victoria Harbour"),
            "tokyo-shibuya-sky" to listOf("涩谷 Sky 黄昏视野", "澀谷 Sky 黃昏視野", "Shibuya Sky dusk view", "Shibuya Sky 夕暮れビュー", "시부야 스카이 황혼 전망", "Atardecer en Shibuya Sky"),
            "tokyo-asakusa" to listOf("浅草到隅田川散步", "淺草到隅田川散步", "Asakusa to Sumida River walk", "浅草から隅田川散歩", "아사쿠사-스미다강 산책", "Paseo de Asakusa al rio Sumida"),
            "seoul-yeonnam" to listOf("延南洞咖啡 Citywalk", "延南洞咖啡 Citywalk", "Yeonnam-dong cafe citywalk", "延南洞カフェ Citywalk", "연남동 카페 Citywalk", "Citywalk de cafes en Yeonnam"),
            "singapore-marina-bay" to listOf("滨海湾夜游", "濱海灣夜遊", "Marina Bay night walk", "Marina Bay ナイトウォーク", "마리나베이 야간 산책", "Paseo nocturno por Marina Bay"),
            "bangkok-iconsiam" to listOf("ICONSIAM 河岸路线", "ICONSIAM 河岸路線", "ICONSIAM riverside route", "ICONSIAM 川沿いルート", "아이콘시암 강변 루트", "Ruta riberena ICONSIAM"),
            "paris-seine-louvre" to listOf("卢浮宫到塞纳河黄昏散步", "羅浮宮到塞納河黃昏散步", "Louvre to Seine dusk walk", "ルーブルからセーヌ川夕暮れ散歩", "루브르-센강 황혼 산책", "Ruta del Louvre al Sena"),
            "london-south-bank" to listOf("南岸河边散步", "南岸河邊散步", "South Bank riverside walk", "South Bank 川沿い散歩", "사우스뱅크 강변 산책", "Paseo por South Bank"),
            "newyork-brooklyn-bridge-dumbo" to listOf("布鲁克林大桥到 DUMBO", "布魯克林大橋到 DUMBO", "Brooklyn Bridge to DUMBO", "Brooklyn Bridge から DUMBO", "브루클린 브리지-DUMBO", "Brooklyn Bridge a DUMBO"),
            "barcelona-gothic-quarter" to listOf("哥特区 Citywalk", "哥德區 Citywalk", "Gothic Quarter citywalk", "ゴシック地区 Citywalk", "고딕 지구 Citywalk", "Citywalk por el Barrio Gotico"),
            "dubai-burj-khalifa-fountain" to listOf("哈利法塔喷泉夜景", "哈里發塔噴泉夜景", "Burj Khalifa fountain night route", "ブルジュ・ハリファ噴水ナイトルート", "부르즈 칼리파 분수 야경", "Fuente del Burj Khalifa de noche"),
        )
        val variants = names[poi.poiId] ?: return poi.name
        return when (locale) {
            RouteLocale.ZhCn -> variants[0]
            RouteLocale.ZhTw -> variants[1]
            RouteLocale.En -> variants[2]
            RouteLocale.Ja -> variants[3]
            RouteLocale.Ko -> variants[4]
            RouteLocale.Es -> variants[5]
        }
    }

    fun safeRecommendationFor(poi: POI): String {
        val category = poi.globalCategory.replace('_', ' ')
        return when (locale) {
            RouteLocale.ZhCn -> "为 $category 路线精选的 mock 地点。正式上线前需用官方来源核验营业时间、价格和人流。"
            RouteLocale.ZhTw -> "為 $category 路線精選的 mock 地點。正式上線前需用官方來源核驗營業時間、價格和人流。"
            RouteLocale.En -> "A curated mock stop for $category routes. Verify hours, prices, and crowd levels with official providers before production."
            RouteLocale.Ja -> "$category ルート向けの mock スポットです。本番前に営業時間、価格、混雑を公式プロバイダーで確認してください。"
            RouteLocale.Ko -> "$category 루트용 mock 장소입니다. 정식 출시 전 영업시간, 가격, 혼잡도를 공식 제공자로 확인하세요."
            RouteLocale.Es -> "Parada mock curada para rutas de $category. Verifica horarios, precios y aforo con proveedores oficiales."
        }
    }

    fun safeRiskTipsFor(poi: POI): List<String> {
        return when (locale) {
            RouteLocale.ZhCn -> listOf("营业时间和价格需要官方校验", "人流仅为 mock 估计", "导航请使用外部地图")
            RouteLocale.ZhTw -> listOf("營業時間和價格需要官方校驗", "人流僅為 mock 估計", "導航請使用外部地圖")
            RouteLocale.En -> listOf("Opening hours and prices need official verification", "Crowd level is only a mock estimate", "Use external maps for navigation")
            RouteLocale.Ja -> listOf("営業時間と料金は公式確認が必要です", "混雑度は mock 推定です", "ナビは外部地図を使用してください")
            RouteLocale.Ko -> listOf("영업시간과 가격은 공식 확인이 필요합니다", "혼잡도는 mock 추정입니다", "길찾기는 외부 지도를 사용하세요")
            RouteLocale.Es -> listOf("Horarios y precios requieren verificacion oficial", "El aforo es una estimacion mock", "Usa mapas externos para navegar")
        }
    }

    fun safeLocalizedSourceLabel(): String {
        return when (locale) {
            RouteLocale.ZhCn -> "本地 mock 精选地点库"
            RouteLocale.ZhTw -> "本地 mock 精選地點庫"
            RouteLocale.En -> "Local mock curated POI catalog"
            RouteLocale.Ja -> "ローカル mock 厳選 POI カタログ"
            RouteLocale.Ko -> "로컬 mock 엄선 POI 카탈로그"
            RouteLocale.Es -> "Catalogo local mock de POI curados"
        }
    }

    private fun localizedText(zhCn: String, zhTw: String, en: String, ja: String, ko: String, es: String): String {
        return when (locale) {
            RouteLocale.ZhCn -> zhCn
            RouteLocale.ZhTw -> zhTw
            RouteLocale.En -> en
            RouteLocale.Ja -> ja
            RouteLocale.Ko -> ko
            RouteLocale.Es -> es
        }
    }

    private fun localizedBudgetNote(): String = localizedText(
        "预算偏低，优先安排低消费地点和免费拍照点。",
        "預算偏低，優先安排低消費地點和免費拍照點。",
        "Budget is low, so the route favors low-cost stops and free photo spots.",
        "予算が低めなので、低コストのスポットと無料撮影場所を優先します。",
        "예산이 낮아 저비용 장소와 무료 촬영 포인트를 우선합니다.",
        "Presupuesto bajo: se priorizan paradas economicas y fotos gratis.",
    )

    private fun localizedShortTimeNote(): String = localizedText(
        "时间较短，路线控制在 2 个核心点。",
        "時間較短，路線控制在 2 個核心點。",
        "Time is short, so the route stays around two key stops.",
        "時間が短いため、主要スポットを 2 か所に絞ります。",
        "시간이 짧아 핵심 장소 2곳 중심으로 구성합니다.",
        "Tiempo limitado: la ruta se concentra en dos paradas clave.",
    )

    private fun localizedPhotoBalanceNote(): String = localizedText(
        "兼顾拍照和体力，优先选择步行压力小的点。",
        "兼顧拍照和體力，優先選擇步行壓力小的點。",
        "Photo needs and energy are balanced with easier walking stops.",
        "写真と体力のバランスを取り、歩行負担の少ない場所を優先します。",
        "사진과 체력을 함께 고려해 걷기 부담이 낮은 장소를 우선합니다.",
        "Se equilibra foto y energia con paradas de caminata ligera.",
    )

    private fun isCouple(value: String): Boolean = value.contains("情侣") || value.contains("情侶") || value.contains("鎯呬荆") || value.contains("crush", true)
    private fun isFriends(value: String): Boolean = value.contains("朋友") || value.contains("鏈嬪弸") || value.contains("friend", true)
    private fun isFamily(value: String): Boolean = value.contains("家人") || value.contains("家庭") || value.contains("瀹朵汉") || value.contains("family", true)

    companion object {
        fun forLocale(code: String): ItineraryCopy {
            val locale = when (code) {
                "zh-TW" -> RouteLocale.ZhTw
                "en" -> RouteLocale.En
                "ja" -> RouteLocale.Ja
                "ko" -> RouteLocale.Ko
                "es" -> RouteLocale.Es
                else -> RouteLocale.ZhCn
            }
            return ItineraryCopy(locale)
        }
    }
}
