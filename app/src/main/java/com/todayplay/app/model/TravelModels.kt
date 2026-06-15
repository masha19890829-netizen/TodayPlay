package com.todayplay.app.model

data class UserPreference(
    val userLabel: String,
    val relationship: String,
    val city: String,
    val timeWindow: String,
    val budget: String,
    val transportMode: String,
    val interests: List<String>,
    val pace: String,
    val foodPreference: String?,
    val photoPreference: String?,
)

data class GroupPreference(
    val groupLabel: String,
    val members: List<UserPreference>,
    val mergedCity: String,
    val mergedBudget: String,
    val mergedTimeWindow: String,
    val mergedTransportMode: String,
    val mergedInterests: List<String>,
    val relationshipType: String,
    val conflictNotes: List<String>,
)

data class ContentSource(
    val sourceType: String,
    val sourceName: String,
    val sourceLabel: String,
    val isMock: Boolean,
    val sourcePolicy: SourcePolicy,
)

data class SourcePolicy(
    val policyTitle: String,
    val policyNotes: List<String>,
)

data class ContentComplianceNote(
    val summary: String,
    val sourcePolicy: SourcePolicy,
    val limitations: List<String>,
)

data class POI(
    val poiId: String,
    val name: String,
    val country: String = "China",
    val city: String,
    val district: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val tags: List<String>,
    val suitableRelationships: List<String>,
    val budgetLevel: String,
    val estimatedStayMinutes: Int,
    val imagePlaceholder: String,
    val recommendationReason: String,
    val contentSource: ContentSource,
    val riskTips: List<String>,
    val globalCategory: String = "local_highlight",
    val dataFreshness: String = "mock_curated",
    val requiresOfficialVerification: Boolean = true,
)

data class CheckInTask(
    val taskId: String,
    val title: String,
    val description: String,
    val rewardPoints: Int,
    val requiresPhoto: Boolean,
    val manualCheckInAllowed: Boolean,
)

data class ExternalMapAction(
    val destinationName: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val amapUri: String,
    val fallbackUri: String,
)

data class RouteStop(
    val stopId: String,
    val order: Int,
    val poi: POI,
    val startTimeHint: String,
    val stayMinutes: Int,
    val checkInTask: CheckInTask,
    val photoSuggestion: String,
    val spendingSuggestion: String,
    val backupPlan: String,
    val navigationAction: ExternalMapAction,
    val whyForGroup: String,
)

data class RouteStopReplacementPreview(
    val stopId: String,
    val originalPoiId: String,
    val originalName: String,
    val candidatePoiId: String,
    val candidateName: String,
    val candidateDistrict: String,
    val candidateCategory: String,
    val candidateTags: List<String>,
    val stayMinutes: Int,
    val sameCategory: Boolean,
    val matchedTags: List<String>,
    val stayDeltaMinutes: Int,
    val sourceLabel: String,
)

data class RewardPoint(
    val rewardId: String,
    val questId: String,
    val taskId: String,
    val points: Int,
    val reason: String,
    val createdAt: Long = System.currentTimeMillis(),
)

data class ItineraryPlan(
    val planId: String,
    val planType: String,
    val title: String,
    val city: String,
    val relationshipType: String,
    val groupPreference: GroupPreference,
    val stops: List<RouteStop>,
    val estimatedCost: String,
    val estimatedDuration: String,
    val routeSummary: String,
    val crowdRisk: String,
    val rainBackup: String,
    val bestPhotoTime: String,
    val rewardPolicy: String,
    val complianceNote: ContentComplianceNote,
    val marketCoverageNote: String = "Local mock data only. Production coverage requires official map, merchant, and backend content providers.",
    val candidateRouteCount: Int = 1,
) {
    val totalRewardPoints: Int
        get() = stops.sumOf { it.checkInTask.rewardPoints }
}

data class ItineraryQuest(
    val quest: Quest,
    val itineraryPlan: ItineraryPlan,
)
