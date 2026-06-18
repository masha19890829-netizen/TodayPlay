package com.todayplay.app.generator

import com.todayplay.app.model.QuestInput
import java.util.Locale
import kotlin.math.abs

data class RouteIntent(
    val intentId: String,
    val rawText: String,
    val localeCode: String,
    val city: String,
    val relationship: String,
    val moodTags: List<String>,
    val primaryGoal: String,
    val timeBudget: String,
    val moneyBudget: String,
    val mobility: String,
    val transportMode: String,
    val indoorPreference: String,
    val avoidances: List<String>,
    val wantsCinema: Boolean,
    val confidence: Float,
    val missingFields: List<String>,
)

data class CandidateRouteCard(
    val cardId: String,
    val title: String,
    val subtitle: String,
    val strategy: String,
    val strategyLabel: String,
    val evidenceSignals: List<String>,
    val stopPreview: List<String>,
    val estimatedDuration: String,
    val budgetLabel: String,
    val mobilityLabel: String,
    val whyThisFits: String,
    val tradeoff: String,
    val sourceNote: String,
    val input: QuestInput,
)

object RouteIntentInterpreter {
    fun interpret(
        rawText: String,
        selectedChips: Set<String>,
        selectedCity: String,
        localeCode: String,
    ): RouteIntent {
        val text = rawText.trim()
        val haystack = (text + " " + selectedChips.joinToString(" ")).lowercase(Locale.ROOT)
        val wantsCinema = hasAny(haystack, "电影", "时光电影", "取景", "名场面", "片段", "cinema", "film", "movie") || "movie" in selectedChips
        val city = cityFromText(text) ?: selectedCity.ifBlank { "上海" }
        val relationship = when {
            hasAny(haystack, "朋友", "friend", "组局", "聚会") || "friends" in selectedChips -> "朋友"
            hasAny(haystack, "约会", "情侣", "暧昧", "crush", "date") || "date" in selectedChips -> "约会"
            hasAny(haystack, "家人", "亲子", "孩子", "family") || "family" in selectedChips -> "家人"
            hasAny(haystack, "自己", "一个人", "独处", "solo") || "solo" in selectedChips -> "一个人散心"
            else -> "朋友"
        }
        val timeBudget = when {
            hasAny(haystack, "30", "半小时") -> "30 分钟"
            hasAny(haystack, "90", "1.5", "一个半") -> "90 分钟"
            hasAny(haystack, "半天", "half day") || "half-day" in selectedChips -> "半天"
            hasAny(haystack, "今晚", "下班", "晚上", "night") || "tonight" in selectedChips -> "今晚 2 小时"
            hasAny(haystack, "2小时", "2 小时", "two hours") || "2h" in selectedChips -> "2 小时"
            else -> "90 分钟"
        }
        val moneyBudget = when {
            hasAny(haystack, "不花钱", "免费", "低预算", "省钱") || "low-budget" in selectedChips -> "100 元以内"
            hasAny(haystack, "100", "一百") -> "100 元以内"
            hasAny(haystack, "300", "三百") -> "300 元以内"
            hasAny(haystack, "贵一点", "愿意消费") -> "愿意消费"
            else -> "150 元以内"
        }
        val mobility = when {
            hasAny(haystack, "少走", "不想走", "不累", "别太累", "低体力") || "less-walk" in selectedChips -> "少走路"
            hasAny(haystack, "citywalk", "散步", "走走") -> "可步行"
            hasAny(haystack, "打车", "taxi") -> "可打车"
            else -> "普通"
        }
        val indoorPreference = when {
            hasAny(haystack, "室内", "雨", "下雨", "商场") || "rainy" in selectedChips -> "室内优先"
            else -> "室外可接受"
        }
        val primaryGoal = when {
            wantsCinema -> "电影感打卡"
            hasAny(haystack, "聊天", "聊聊") || "chat" in selectedChips -> "轻松聊天"
            hasAny(haystack, "拍照", "打卡") || "photo" in selectedChips -> "拍照出片"
            hasAny(haystack, "吃", "晚饭", "小吃") -> "吃喝逛"
            hasAny(haystack, "安静", "治愈", "放空") || "quiet" in selectedChips -> "安静恢复"
            hasAny(haystack, "热闹", "好玩", "新鲜") || "lively" in selectedChips -> "热闹探索"
            else -> "轻松安排"
        }
        val moodTags = buildList {
            if (hasAny(haystack, "累", "不累", "低体力") || "less-walk" in selectedChips) add("有点累")
            if (hasAny(haystack, "安静", "放空", "治愈") || "quiet" in selectedChips) add("想安静")
            if (hasAny(haystack, "热闹", "朋友", "组局") || "lively" in selectedChips) add("想热闹")
            if (hasAny(haystack, "拍照", "出片") || "photo" in selectedChips) add("想拍照")
            if (wantsCinema) add("电影感")
            if (hasAny(haystack, "聊天", "聊聊") || "chat" in selectedChips) add("想聊天")
            if (indoorPreference == "室内优先") add("室内优先")
            if (moneyBudget.contains("100")) add("低预算")
        }.ifEmpty { listOf("想省心", primaryGoal) }.distinct()

        val avoidances = buildList {
            if (hasAny(haystack, "不要太远", "别太远", "近一点")) add("不要太远")
            if (hasAny(haystack, "别太累", "不想太累", "少走")) add("不要太累")
            if (hasAny(haystack, "不要排队", "不排队")) add("不要排队")
            if (hasAny(haystack, "别太贵", "省钱", "低预算")) add("不要太贵")
            if (hasAny(haystack, "不要酒吧", "不喝酒")) add("不要酒吧")
        }
        val missing = buildList {
            if (text.isBlank()) add("一句今天的需求")
            if (city.isBlank()) add("城市")
        }
        val confidence = when {
            text.length >= 12 && missing.isEmpty() -> 0.86f
            text.isNotBlank() -> 0.72f
            else -> 0.56f
        }
        return RouteIntent(
            intentId = "intent-${abs((text + selectedChips.joinToString()).hashCode())}",
            rawText = text.ifBlank { "想快速安排今天" },
            localeCode = localeCode,
            city = city,
            relationship = relationship,
            moodTags = moodTags,
            primaryGoal = primaryGoal,
            timeBudget = timeBudget,
            moneyBudget = moneyBudget,
            mobility = mobility,
            transportMode = if (mobility == "可打车") "打车/步行" else "地铁/步行",
            indoorPreference = indoorPreference,
            avoidances = avoidances,
            wantsCinema = wantsCinema,
            confidence = confidence,
            missingFields = missing,
        )
    }

    fun buildCandidateCards(intent: RouteIntent): List<CandidateRouteCard> {
        val specs = listOf(
            CardSpec("fit", "最贴合", "按你刚才说的状态安排", listOf(intent.primaryGoal, intent.mobility)),
            CardSpec("quiet", "更安静", "少一点打扰，多一点余地", listOf("安静", "少走路", "低压力")),
            CardSpec("lively", "更热闹", "适合朋友临时起兴", listOf("热闹", "拍照", "吃喝逛")),
            CardSpec("budget", "更省钱", "把消费压低，保留好玩感", listOf("低预算", "免费散步", "小店")),
            CardSpec("short", "少走路", "区域更集中，随时能撤退", listOf("少走路", "近一点", "坐下聊")),
            CardSpec("surprise", "小惊喜", "在约束里留一点新鲜感", listOf("新鲜", "轻冒险", "有记忆点")),
        )
        val expandedSpecs = specs.take(5) +
            listOfNotNull(
                CardSpec("cinema", "时光电影", "把今天剪成一段城市片段", listOf("电影感", "取景灵感", "票根收尾"))
                    .takeIf { intent.wantsCinema },
            ) +
            CardSpec("indoor", "室内优先", "下雨或太热也能继续玩", listOf("室内优先", "少走路", "稳妥")) +
            specs.drop(5)
        return expandedSpecs.mapIndexed { index, spec ->
            val title = when (spec.id) {
                "fit" -> "${intent.primaryGoal}局"
                "quiet" -> "安静恢复路线"
                "lively" -> if (intent.relationship == "朋友") "今晚组局路线" else "热闹一点路线"
                "budget" -> "低预算轻玩法"
                "short" -> "少走路路线"
                "cinema" -> "时光电影路线"
                else -> "今天的小惊喜"
            }
            val resolvedTitle = if (spec.id == "indoor") "室内稳妥路线" else title
            val stopPreview = stopPreviewFor(intent, spec.id)
            val input = intent.toQuestInput(
                title = resolvedTitle,
                strategy = spec.id,
                summary = "${spec.subtitle}：${stopPreview.joinToString(" → ")}",
                moodExtras = spec.moodTags,
            )
            CandidateRouteCard(
                cardId = "card-${intent.intentId}-${spec.id}",
                title = resolvedTitle,
                subtitle = spec.subtitle,
                strategy = spec.id,
                strategyLabel = spec.label,
                evidenceSignals = intent.evidenceSignalsFor(spec.id),
                stopPreview = stopPreview,
                estimatedDuration = durationFor(intent, spec.id),
                budgetLabel = budgetFor(intent, spec.id),
                mobilityLabel = mobilityFor(intent, spec.id),
                whyThisFits = whyFor(intent, spec.id),
                tradeoff = tradeoffFor(spec.id),
                sourceNote = if (spec.id == "cinema") {
                    "电影感样例路线；真实取景地关系需来源核验，不代表官方授权"
                } else {
                    "本地样例地点 + AI意图结构，出发前需确认营业时间"
                },
                input = input,
            )
        }
    }

    private fun RouteIntent.toQuestInput(
        title: String,
        strategy: String,
        summary: String,
        moodExtras: List<String>,
    ): QuestInput {
        val noteLines = listOf(
            rawText,
            "TP_INTENT_TITLE=$title",
            "TP_INTENT_SUMMARY=$summary",
            "TP_INTENT_STRATEGY=$strategy",
            "TP_INTENT_STRATEGY_LABEL=${labelForStrategy(strategy)}",
            "TP_INTENT_SIGNALS=${evidenceSignalsFor(strategy).joinToString("|")}",
            "TP_INTENT_REASON=${whyFor(this, strategy)}",
            "TP_INTENT_TRADEOFF=${tradeoffFor(strategy)}",
            "TP_INTENT_SOURCE=${sourceNoteFor(strategy)}",
            "TP_INTENT_GOAL=$primaryGoal",
            "TP_INTENT_INDOOR=$indoorPreference",
            "TP_INTENT_CINEMA=${if (strategy == "cinema") "true" else "false"}",
            "TP_INTENT_AVOID=${avoidances.joinToString("/")}",
        )
        return QuestInput(
            relationship = relationship,
            city = city,
            moods = (moodTags + moodExtras).distinct().take(7),
            time = durationFor(this, strategy),
            budget = budgetFor(this, strategy),
            vibe = primaryGoal,
            note = noteLines.joinToString("\n"),
            transportMode = transportMode,
            localeCode = localeCode,
        )
    }

    private fun stopPreviewFor(intent: RouteIntent, strategy: String): List<String> {
        return when (strategy) {
            "quiet" -> listOf("安静咖啡", "短距离散步", "可提前结束")
            "lively" -> listOf("热闹小店", "夜景拍照", "收尾聊天")
            "budget" -> listOf("免费街区", "低消费补给", "城市照片点")
            "short" -> listOf("同一区域", "坐下休息", "轻松收尾")
            "cinema" -> listOf("胶片开场", "名场面感", "票根收尾")
            "indoor" -> listOf("室内备选", "坐下补给", "雨天收尾")
            "surprise" -> listOf("没去过的小店", "城市转角", "今日记忆点")
            else -> when {
                intent.primaryGoal.contains("拍照") -> listOf("出片点", "咖啡补给", "夜景收尾")
                intent.primaryGoal.contains("聊天") -> listOf("先坐下", "短散步", "轻松收尾")
                intent.primaryGoal.contains("吃") -> listOf("小吃补给", "街区散步", "甜点收尾")
                else -> listOf("第一站", "轻任务", "收尾点")
            }
        }
    }

    private fun durationFor(intent: RouteIntent, strategy: String): String {
        return when (strategy) {
            "short" -> if (intent.timeBudget.contains("30")) "30 分钟" else "90 分钟"
            "lively" -> if (intent.timeBudget.contains("今晚")) "今晚 2 小时" else "2 小时"
            "cinema" -> if (intent.timeBudget.contains("30")) "90 分钟" else intent.timeBudget
            else -> intent.timeBudget
        }
    }

    private fun budgetFor(intent: RouteIntent, strategy: String): String {
        return when (strategy) {
            "budget" -> "100 元以内"
            "cinema" -> if (intent.moneyBudget == "愿意消费") "300 元以内" else intent.moneyBudget
            "quiet", "short" -> if (intent.moneyBudget == "愿意消费") "300 元以内" else intent.moneyBudget
            else -> intent.moneyBudget
        }
    }

    private fun mobilityFor(intent: RouteIntent, strategy: String): String {
        return when (strategy) {
            "short", "quiet" -> "少走路"
            "cinema" -> if (intent.mobility == "少走路") "少走路" else "可步行"
            "lively" -> if (intent.mobility == "少走路") "普通" else intent.mobility
            else -> intent.mobility
        }
    }

    private fun whyFor(intent: RouteIntent, strategy: String): String {
        return when (strategy) {
            "quiet" -> "你提到${intent.moodTags.joinToString("、")}，所以先降低移动和社交压力。"
            "lively" -> "保留${intent.relationship}一起玩的能量，路线更适合拍照和临时聊天。"
            "budget" -> "预算被放在硬约束里，优先公共空间和低消费补给点。"
            "short" -> "把站点压在更小区域里，避免出门后越走越累。"
            "cinema" -> "你想要电影感，所以优先选择有镜头感的街区、室内、夜景或票根式收尾。"
            "surprise" -> "不突破预算和城市边界，但给今天留一个没预设的小转角。"
            else -> "按${intent.city}、${intent.relationship}、${intent.timeBudget}和${intent.primaryGoal}一起平衡。"
        }
    }

    private fun tradeoffFor(strategy: String): String {
        return when (strategy) {
            "quiet" -> "更舒服，但刺激感会少一点"
            "lively" -> "更有气氛，但可能比安静路线多一点人"
            "budget" -> "更省钱，但消费型体验会减少"
            "short" -> "更轻松，但探索范围更小"
            "cinema" -> "更有记忆点，但真实取景关系需要来源核验"
            "surprise" -> "更有新鲜感，但需要出发前再确认营业"
            else -> "最稳妥，但惊喜感适中"
        }
    }

    private fun sourceNoteFor(strategy: String): String {
        return if (strategy == "cinema") {
            "电影感样例路线；真实取景地关系需来源核验，不代表官方授权"
        } else {
            "本地样例地点 + AI意图结构；出发前需确认营业时间"
        }
    }

    private fun RouteIntent.evidenceSignalsFor(strategy: String): List<String> {
        val base = listOf(
            city,
            relationship,
            durationFor(this, strategy),
            budgetFor(this, strategy),
            mobilityFor(this, strategy),
        )
        val extras = when (strategy) {
            "quiet" -> listOf("降低社交压力")
            "lively" -> listOf("朋友局更有氛围")
            "budget" -> listOf("优先低消费")
            "short" -> listOf("缩小移动半径")
            "indoor" -> listOf("室内/雨天优先")
            "cinema" -> listOf("电影感镜头")
            else -> listOf(primaryGoal)
        }
        return (base + extras)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .take(7)
    }

    private fun labelForStrategy(strategy: String): String {
        return when (strategy) {
            "fit" -> "最贴合"
            "quiet" -> "更安静"
            "lively" -> "更热闹"
            "budget" -> "更省钱"
            "short" -> "少走路"
            "surprise" -> "小惊喜"
            "cinema" -> "时光电影"
            "indoor" -> "室内优先"
            else -> strategy
        }
    }

    private data class CardSpec(
        val id: String,
        val label: String,
        val subtitle: String,
        val moodTags: List<String>,
    )

    private fun cityFromText(text: String): String? {
        val known = listOf("上海", "深圳", "北京", "广州", "杭州", "成都", "香港", "台北", "东京", "首尔")
        return known.firstOrNull { text.contains(it) }
    }

    private fun hasAny(text: String, vararg tokens: String): Boolean {
        return tokens.any { token -> text.contains(token.lowercase(Locale.ROOT)) }
    }
}
