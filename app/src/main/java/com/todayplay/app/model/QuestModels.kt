package com.todayplay.app.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class QuestInput(
    val relationship: String,
    val city: String?,
    val moods: List<String>,
    val time: String,
    val budget: String,
    val vibe: String,
    val note: String?,
    val transportMode: String = "地铁/步行",
    val localeCode: String = "zh-CN",
)

data class Quest(
    val questId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val title: String,
    val storySetup: String,
    val relationship: String,
    val city: String?,
    val duration: String,
    val budget: String,
    val tags: List<String>,
    val tasks: List<QuestTask>,
    val hiddenTask: QuestTask,
    val conversationPrompts: List<String>,
    val photoMission: String,
    val endingRitual: String,
    val completionTitle: String,
    val completionKeywords: List<String>,
    val completionSummary: String,
    val itineraryPlan: ItineraryPlan? = null,
)

data class QuestTask(
    val title: String,
    val description: String,
    val howToComplete: String,
    val cuteTip: String,
    val estimatedTime: String,
    val difficultyHearts: Int,
    val taskId: String = "",
)

enum class TaskStatus {
    Pending,
    Completed,
    Skipped,
}

enum class FeedbackReason(val label: String) {
    TooAwkward("太尬"),
    TooExpensive("太贵"),
    TooFar("太远"),
    TooTiring("太累");

    companion object {
        fun fromLabel(label: String): FeedbackReason? = entries.firstOrNull { it.label == label }
        fun fromStoredValue(value: String): FeedbackReason? = entries.firstOrNull { it.name == value || it.label == value }
    }
}

data class QuestProgress(
    val taskStatuses: Map<String, TaskStatus> = emptyMap(),
    val feedbackReasons: Map<String, Set<FeedbackReason>> = emptyMap(),
    val rewardPoints: List<RewardPoint> = emptyList(),
    val localPhotoPreviewUris: Map<String, String> = emptyMap(),
    val lastRouteStopRestore: RouteStopRestoreSnapshot? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
) {
    fun statusFor(taskId: String): TaskStatus = taskStatuses[taskId] ?: TaskStatus.Pending

    fun feedbackFor(taskId: String): Set<FeedbackReason> = feedbackReasons[taskId] ?: emptySet()

    fun completedMainTaskCount(taskIds: List<String>): Int {
        return taskIds.count { statusFor(it) == TaskStatus.Completed }
    }
}

data class RouteStopRestoreSnapshot(
    val stopId: String,
    val order: Int,
    val previousStop: RouteStop,
    val restoredStatus: TaskStatus?,
    val restoredFeedbackReasons: Set<FeedbackReason> = emptySet(),
    val restoredRewardPoints: List<RewardPoint> = emptyList(),
    val replacedStopName: String,
    val createdAt: Long = System.currentTimeMillis(),
)

data class QuestRecord(
    val quest: Quest,
    val progress: QuestProgress = QuestProgress(),
)

data class CompletionCardData(
    val questId: String,
    val title: String,
    val completionTitle: String,
    val duration: String,
    val dateLabel: String,
    val completedTaskCount: Int,
    val totalTaskCount: Int,
    val hiddenTaskStatus: TaskStatus,
    val keywords: List<String>,
    val summary: String,
    val relationship: String,
    val budget: String,
    val checkedInStopCount: Int,
    val totalStopCount: Int,
    val totalRewardPoints: Int,
    val planType: String?,
) {
    val hiddenTaskLabel: String
        get() = when (hiddenTaskStatus) {
            TaskStatus.Completed -> "已完成"
            TaskStatus.Skipped -> "已跳过"
            TaskStatus.Pending -> "待完成"
        }

    companion object {
        fun from(record: QuestRecord): CompletionCardData {
            val quest = record.quest
            val taskIds = quest.tasks.map { it.taskId }
            return CompletionCardData(
                questId = quest.questId,
                title = quest.title,
                completionTitle = quest.completionTitle,
                duration = quest.duration,
                dateLabel = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date(quest.createdAt)),
                completedTaskCount = record.progress.completedMainTaskCount(taskIds),
                totalTaskCount = taskIds.size,
                hiddenTaskStatus = record.progress.statusFor(quest.hiddenTask.taskId),
                keywords = quest.completionKeywords,
                summary = quest.completionSummary,
                relationship = quest.relationship,
                budget = quest.budget,
                checkedInStopCount = quest.itineraryPlan?.stops?.count { stop ->
                    record.progress.statusFor(stop.checkInTask.taskId) == TaskStatus.Completed
                } ?: 0,
                totalStopCount = quest.itineraryPlan?.stops?.size ?: 0,
                totalRewardPoints = record.progress.rewardPoints.sumOf { it.points },
                planType = quest.itineraryPlan?.planType,
            )
        }
    }
}
