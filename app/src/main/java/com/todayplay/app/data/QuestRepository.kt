package com.todayplay.app.data

import com.todayplay.app.generator.QuestGenerator
import com.todayplay.app.model.FeedbackReason
import com.todayplay.app.model.QuestInput
import com.todayplay.app.model.QuestProgress
import com.todayplay.app.model.QuestRecord
import com.todayplay.app.model.RewardPoint
import com.todayplay.app.model.RouteStopReplacementPreview
import com.todayplay.app.model.TaskStatus
import java.util.UUID

class QuestRepository(
    private val store: QuestHistoryStore,
    private val generator: QuestGenerator,
) {
    fun loadHistory(): List<QuestRecord> = store.loadRecords()

    fun generate(input: QuestInput): QuestRecord {
        val record = QuestRecord(quest = generator.generate(input))
        upsert(record)
        return record
    }

    fun save(record: QuestRecord) {
        upsert(record)
    }

    fun clearHistory() {
        store.clearRecords()
    }

    fun updateTaskStatus(questId: String, taskId: String, status: TaskStatus): QuestRecord? {
        val current = findRecord(questId) ?: return null
        val updatedStatuses = current.progress.taskStatuses + (taskId to status)
        val updatedRewards = rewardPointsForStatus(current, taskId, status)
        val updated = current.copy(
            progress = current.progress.copy(
                taskStatuses = updatedStatuses,
                rewardPoints = updatedRewards,
                updatedAt = System.currentTimeMillis(),
                completedAt = completionTimeFor(current, updatedStatuses),
            ),
        )
        upsert(updated)
        return updated
    }

    fun replaceRouteStop(questId: String, stopId: String, localeCode: String): QuestRecord? {
        val current = findRecord(questId) ?: return null
        val updated = generator.replaceRouteStop(current, stopId, localeCode) ?: return null
        upsert(updated)
        return updated
    }

    fun restoreRouteStop(questId: String, localeCode: String): QuestRecord? {
        val current = findRecord(questId) ?: return null
        val updated = generator.restoreRouteStop(current, localeCode) ?: return null
        upsert(updated)
        return updated
    }

    fun previewRouteStopReplacement(
        questId: String,
        stopId: String,
        localeCode: String,
    ): RouteStopReplacementPreview? {
        val current = findRecord(questId) ?: return null
        return generator.previewRouteStopReplacement(current, stopId, localeCode)
    }

    fun toggleFeedback(questId: String, taskId: String, reason: FeedbackReason): QuestRecord? {
        val current = findRecord(questId) ?: return null
        val currentReasons = current.progress.feedbackFor(taskId)
        val updatedReasonsForTask = if (reason in currentReasons) currentReasons - reason else currentReasons + reason
        val updatedFeedback = if (updatedReasonsForTask.isEmpty()) {
            current.progress.feedbackReasons - taskId
        } else {
            current.progress.feedbackReasons + (taskId to updatedReasonsForTask)
        }
        val updated = current.copy(
            progress = current.progress.copy(
                feedbackReasons = updatedFeedback,
                updatedAt = System.currentTimeMillis(),
            ),
        )
        upsert(updated)
        return updated
    }

    fun findRecord(questId: String): QuestRecord? {
        return loadHistory().firstOrNull { it.quest.questId == questId }
    }

    private fun upsert(record: QuestRecord) {
        val records = (listOf(record) + loadHistory().filterNot { it.quest.questId == record.quest.questId })
            .take(MAX_RECORDS)
        store.saveRecords(records)
    }

    private fun completionTimeFor(record: QuestRecord, statuses: Map<String, TaskStatus>): Long? {
        val currentCompletedAt = record.progress.completedAt
        if (currentCompletedAt != null) return currentCompletedAt
        val mainTaskIds = record.quest.tasks.map { it.taskId }
        if (mainTaskIds.isEmpty()) return null
        val allMainTasksResolved = mainTaskIds.all { taskId ->
            val status = statuses[taskId] ?: TaskStatus.Pending
            status == TaskStatus.Completed || status == TaskStatus.Skipped
        }
        return if (allMainTasksResolved) System.currentTimeMillis() else null
    }

    private fun rewardPointsForStatus(record: QuestRecord, taskId: String, status: TaskStatus): List<RewardPoint> {
        val existing = record.progress.rewardPoints.filterNot { it.taskId == taskId }
        if (status != TaskStatus.Completed) return existing

        val stop = record.quest.itineraryPlan?.stops?.firstOrNull { it.checkInTask.taskId == taskId } ?: return record.progress.rewardPoints
        val reward = RewardPoint(
            rewardId = UUID.randomUUID().toString(),
            questId = record.quest.questId,
            taskId = taskId,
            points = stop.checkInTask.rewardPoints,
            reason = "完成 ${stop.poi.name} 打卡",
        )
        return existing + reward
    }

    private companion object {
        const val MAX_RECORDS = 50
    }
}
