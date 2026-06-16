package com.todayplay.app.generator

import com.todayplay.app.data.QuestContentPools
import com.todayplay.app.model.Quest
import com.todayplay.app.model.QuestInput
import com.todayplay.app.model.QuestRecord
import com.todayplay.app.model.QuestTask
import com.todayplay.app.model.RouteStopReplacementPreview
import java.util.UUID

class LocalQuestGenerator : QuestGenerator {
    private val itineraryGenerator = LocalItineraryGenerator()

    override fun generate(input: QuestInput): Quest {
        val questId = UUID.randomUUID().toString()
        val createdAt = System.currentTimeMillis()
        val city = input.city?.trim()?.takeIf { it.isNotEmpty() }
        val place = city?.let { "$it 的一个温柔角落" } ?: listOf(
            "附近的一条街",
            "一家没去过的小店",
            "窗边的位置",
            "楼下刚好有光的地方",
            "你们都觉得安全舒服的角落",
        ).random()

        val generatedTitle = QuestContentPools.titleTemplates
            .weightedFor(input)
            .random()
            .replace("{city}", city ?: "附近")
            .replace("{place}", place)

        val title = input.intentMarker("TP_INTENT_TITLE") ?: generatedTitle
        val intentSummary = input.intentMarker("TP_INTENT_SUMMARY")
        val storySetup = intentSummary ?: buildStorySetup(input, place)
        val tasks = buildTaskCandidates(input).shuffled().take(3).mapIndexed { index, task ->
            task.copy(
                cuteTip = QuestContentPools.cuteTips.random(),
                taskId = "$questId-act-${index + 1}",
            )
        }
        val hiddenTask = QuestContentPools.hiddenTasks.random().copy(
            cuteTip = QuestContentPools.cuteTips.random(),
            taskId = "$questId-hidden",
        )

        val tags = buildTags(input)
        val keywords = (tags + listOf("温柔", "笑出声", "有点像电影", "不赶路", "小小冒险"))
            .distinct()
            .shuffled()
            .take(3)
        val itineraryPlan = itineraryGenerator.generate(input, questId)

        return Quest(
            questId = questId,
            createdAt = createdAt,
            title = title,
            storySetup = storySetup,
            relationship = input.relationship,
            city = city,
            duration = input.time,
            budget = input.budget,
            tags = tags,
            tasks = tasks,
            hiddenTask = hiddenTask,
            conversationPrompts = QuestContentPools.conversationPrompts.shuffled().take(5),
            photoMission = choosePhotoMission(input),
            endingRitual = QuestContentPools.endingRituals.random(),
            completionTitle = QuestContentPools.completionTitles.random(),
            completionKeywords = keywords,
            completionSummary = intentSummary ?: QuestContentPools.completionSummaries.random(),
            itineraryPlan = itineraryPlan,
        )
    }

    override fun replaceRouteStop(record: QuestRecord, stopId: String, localeCode: String): QuestRecord? {
        return itineraryGenerator.replaceStop(record, stopId, localeCode)
    }

    override fun restoreRouteStop(record: QuestRecord, localeCode: String): QuestRecord? {
        return itineraryGenerator.restoreStop(record, localeCode)
    }

    override fun previewRouteStopReplacement(
        record: QuestRecord,
        stopId: String,
        localeCode: String,
    ): RouteStopReplacementPreview? {
        return itineraryGenerator.previewStopReplacement(record, stopId, localeCode)
    }

    private fun List<String>.weightedFor(input: QuestInput): List<String> {
        val extra = buildList {
            if (input.relationship == "情侣" && "想浪漫" in input.moods) {
                addAll(listOf("把普通傍晚偷渡成电影", "两个人的心动证据采集", "三枚心动碎片收集令"))
            }
            if ("不想花太多钱" in input.moods || input.budget.startsWith("0") || input.budget.startsWith("50")) {
                addAll(listOf("你们的低预算浪漫逃跑计划", "便利店也会发光的一天", "不费力也很可爱的约会局"))
            }
            if ("有点累" in input.moods) {
                addAll(listOf("把累累的一天变成软软的", "安静陪伴也算盛大冒险"))
            }
            if (input.relationship == "暧昧中") add("暧昧升温小剧场")
            if (input.relationship == "一个人散心") add("给未来自己的温柔补给站")
        }
        return this + extra
    }

    private fun buildTaskCandidates(input: QuestInput): List<QuestTask> {
        val candidates = mutableListOf<QuestTask>()
        candidates += QuestContentPools.commonTasks

        when (input.relationship) {
            "情侣" -> candidates += QuestContentPools.romanticTasks
            "暧昧中" -> candidates += QuestContentPools.iceBreakTasks
            "朋友" -> candidates += QuestContentPools.friendTasks
            "家人" -> candidates += QuestContentPools.familyTasks
            "一个人散心" -> candidates += QuestContentPools.soloTasks
        }
        if ("想浪漫" in input.moods || input.vibe == "电影感" || input.vibe == "甜甜的") {
            candidates += QuestContentPools.romanticTasks
        }
        if ("有点累" in input.moods || input.vibe == "安静陪伴" || input.vibe == "治愈的") {
            candidates += QuestContentPools.chillTasks
        }
        if ("想破冰" in input.moods || input.relationship == "暧昧中") {
            candidates += QuestContentPools.iceBreakTasks
        }
        if (input.budget.startsWith("0") || "不想花太多钱" in input.moods) {
            candidates += QuestContentPools.commonTasks.filter {
                it.title.contains("不花钱") || it.title.contains("一条街") || it.title.contains("窗口")
            }
        }
        if ("不想走太远" in input.moods) {
            candidates += QuestContentPools.commonTasks.filter {
                it.title.contains("一条街") || it.title.contains("窗口") || it.title.contains("沉默")
            }
        }
        if ("社恐友好" in input.moods) {
            candidates.removeAll { it.description.contains("陌生") }
        }
        return candidates.distinctBy { it.title }
    }

    private fun buildStorySetup(input: QuestInput, place: String): String {
        val note = input.note?.trim()?.takeIf { it.isNotEmpty() }?.let {
            "开局备注是：“$it”。"
        } ?: ""

        return when (input.relationship) {
            "情侣" -> "今天你们不是普通出门，而是两位正在 $place 寻找“今日心动证据”的临时搭档。任务不难，但需要一点点认真和一点点不要脸的可爱。$note"
            "暧昧中" -> "今天的副本重点是自然、轻松、尊重边界。你们只需要在 $place 完成几个不冒犯的小互动，让气氛慢慢升温。$note"
            "朋友" -> "今天你们是临时生活玩家，目标是在 $place 把见面从“吃什么”升级成“笑出声一次”。所有任务低压力，可随时改规则。$note"
            "家人" -> "今天的主线是陪伴，不追求效率。你们会在 $place 收集一点回忆、一点感谢和一点轻松的日常感。$note"
            else -> "今天你是自己的温柔队友，在 $place 给生活开一个小小存档。任务轻、节奏慢，重点是把自己照顾好。$note"
        }
    }

    private fun buildTags(input: QuestInput): List<String> {
        val tags = mutableListOf(input.vibe)
        if ("想浪漫" in input.moods) tags += "心动证据"
        if ("有点累" in input.moods) tags += "低电量友好"
        if ("想搞笑" in input.moods) tags += "笑出声"
        if ("社恐友好" in input.moods) tags += "社恐友好"
        if ("不想走太远" in input.moods) tags += "近距离"
        if (input.budget.startsWith("0") || input.budget.startsWith("50")) tags += "低预算"
        if ("想拍好看的照片" in input.moods) tags += "适合拍照"
        if (input.relationship == "暧昧中") tags += "边界感"
        if (input.relationship == "一个人散心") tags += "自我照顾"
        return tags.distinct().take(5)
    }

    private fun choosePhotoMission(input: QuestInput): String {
        return when {
            "想拍好看的照片" in input.moods || input.vibe == "电影感" ->
                "拍下今天最像电影的一秒，不需要露脸，只要光和气氛在。"
            input.relationship == "家人" ->
                "拍一张能代表陪伴的照片，可以是一起走路的影子、桌上的杯子或整理好的小角落。"
            input.relationship == "一个人散心" ->
                "拍下今天的存档点：窗边、桌面、路灯或让你觉得安心的一角天空。"
            else -> QuestContentPools.photoMissions.random()
        }
    }
}
