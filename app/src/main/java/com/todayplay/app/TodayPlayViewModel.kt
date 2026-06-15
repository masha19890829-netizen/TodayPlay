package com.todayplay.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.todayplay.app.data.ProductEventLogger
import com.todayplay.app.data.QuestRepository
import com.todayplay.app.model.FeedbackReason
import com.todayplay.app.model.QuestInput
import com.todayplay.app.model.QuestRecord
import com.todayplay.app.model.RouteStopReplacementPreview
import com.todayplay.app.model.TaskStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class GenerationStatus {
    Idle,
    Generating,
    Succeeded,
    Failed,
}

data class GenerationUiState(
    val status: GenerationStatus = GenerationStatus.Idle,
    val questId: String? = null,
    val errorType: String? = null,
)

class TodayPlayViewModel(
    private val repository: QuestRepository,
) : ViewModel() {
    var latestInput by mutableStateOf<QuestInput?>(null)
        private set

    var latestRecord by mutableStateOf<QuestRecord?>(null)
        private set

    var history by mutableStateOf(repository.loadHistory())
        private set

    var generationState by mutableStateOf(GenerationUiState())
        private set

    private var generationJob: Job? = null

    fun startGeneration(input: QuestInput, source: String) {
        latestInput = input
        generationJob?.cancel()
        generationState = GenerationUiState(status = GenerationStatus.Generating)
        ProductEventLogger.track(
            "generate_start",
            mapOf(
                "source" to source,
                "relationship" to input.relationship,
            ),
        )
        generationJob = viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    repository.generate(input)
                }
            }.onSuccess { record ->
                latestRecord = record
                refreshHistory()
                generationState = GenerationUiState(
                    status = GenerationStatus.Succeeded,
                    questId = record.quest.questId,
                )
                ProductEventLogger.track(
                    "generate_complete",
                    mapOf(
                        "relationship" to input.relationship,
                        "time" to input.time,
                        "budget" to input.budget,
                    ),
                )
            }.onFailure { error ->
                if (error is CancellationException) throw error
                generationState = GenerationUiState(
                    status = GenerationStatus.Failed,
                    errorType = error::class.java.simpleName.ifBlank { "GenerationError" },
                )
                ProductEventLogger.track(
                    "generate_failed",
                    mapOf(
                        "relationship" to input.relationship,
                        "errorType" to (error::class.java.simpleName.ifBlank { "GenerationError" }),
                    ),
                )
            }
        }
    }

    fun generateFromLatestInput(): Boolean {
        val input = latestInput ?: return false
        startGeneration(input, "legacy_generate")
        return true
    }

    fun retryGeneration() {
        val input = latestInput ?: return
        startGeneration(input, "loading_retry")
    }

    fun cancelGeneration() {
        generationJob?.cancel()
        generationState = GenerationUiState()
    }

    fun resetGenerationState() {
        generationState = GenerationUiState()
    }

    fun openRecord(record: QuestRecord) {
        latestRecord = repository.findRecord(record.quest.questId) ?: record
    }

    fun saveLatest() {
        latestRecord?.let { record ->
            repository.save(record)
            refreshHistory()
            ProductEventLogger.track("save_quest", mapOf("questId" to record.quest.questId))
        }
    }

    fun clearLocalHistory() {
        repository.clearHistory()
        latestRecord = null
        refreshHistory()
        ProductEventLogger.track("clear_local_history")
    }

    fun updateTaskStatus(taskId: String, status: TaskStatus) {
        val questId = latestRecord?.quest?.questId ?: return
        val updated = repository.updateTaskStatus(questId, taskId, status) ?: return
        latestRecord = updated
        refreshHistory()
        ProductEventLogger.track(
            "task_status_update",
            mapOf(
                "questId" to questId,
                "taskId" to taskId,
                "status" to status.name,
            ),
        )
    }

    fun replaceRouteStop(stopId: String, localeCode: String): Boolean {
        val questId = latestRecord?.quest?.questId ?: return false
        val updated = repository.replaceRouteStop(questId, stopId, localeCode) ?: return false
        latestRecord = updated
        refreshHistory()
        ProductEventLogger.track(
            "route_stop_replace",
            mapOf(
                "questId" to questId,
                "stopId" to stopId,
            ),
        )
        return true
    }

    fun restoreRouteStop(localeCode: String): Boolean {
        val questId = latestRecord?.quest?.questId ?: return false
        val restored = repository.restoreRouteStop(questId, localeCode) ?: return false
        latestRecord = restored
        refreshHistory()
        ProductEventLogger.track(
            "route_stop_restore",
            mapOf("questId" to questId),
        )
        return true
    }

    fun previewRouteStopReplacement(stopId: String, localeCode: String): RouteStopReplacementPreview? {
        val questId = latestRecord?.quest?.questId ?: return null
        val preview = repository.previewRouteStopReplacement(questId, stopId, localeCode) ?: return null
        ProductEventLogger.track(
            "route_stop_replace_preview",
            mapOf(
                "questId" to questId,
                "stopId" to stopId,
                "candidatePoiId" to preview.candidatePoiId,
            ),
        )
        return preview
    }

    fun toggleFeedback(taskId: String, reason: FeedbackReason) {
        val questId = latestRecord?.quest?.questId ?: return
        val updated = repository.toggleFeedback(questId, taskId, reason) ?: return
        latestRecord = updated
        refreshHistory()
        ProductEventLogger.track(
            "task_feedback",
            mapOf(
                "questId" to questId,
                "taskId" to taskId,
                "reason" to reason.name,
            ),
        )
    }

    fun trackShareCardOpen() {
        latestRecord?.quest?.questId?.let { questId ->
            ProductEventLogger.track("share_card_open", mapOf("questId" to questId))
        }
    }

    fun trackRegenerate() {
        latestRecord?.quest?.relationship?.let { relationship ->
            ProductEventLogger.track("regenerate", mapOf("relationship" to relationship))
        }
    }

    private fun refreshHistory() {
        history = repository.loadHistory()
    }

    class Factory(
        private val repository: QuestRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TodayPlayViewModel::class.java)) {
                return TodayPlayViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
