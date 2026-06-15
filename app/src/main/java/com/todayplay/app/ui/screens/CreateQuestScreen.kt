package com.todayplay.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.todayplay.app.data.GlobalPoiMockData
import com.todayplay.app.localization.LocalTodayPlayLocale
import com.todayplay.app.localization.systemStrings
import com.todayplay.app.model.QuestInput
import com.todayplay.app.ui.components.CuteTopBar
import com.todayplay.app.ui.components.GhostButton
import com.todayplay.app.ui.components.HeartPrimaryButton
import com.todayplay.app.ui.components.KawaiiChip
import com.todayplay.app.ui.components.PaperBackground
import com.todayplay.app.ui.components.SoftCard
import com.todayplay.app.ui.components.TextInputCard
import com.todayplay.app.ui.components.TicketCard
import com.todayplay.app.ui.theme.CherryPressed
import com.todayplay.app.ui.theme.InkBlack
import com.todayplay.app.ui.theme.LineBeige
import com.todayplay.app.ui.theme.RoseGold
import com.todayplay.app.ui.theme.WarmGray

@Composable
fun CreateQuestScreen(
    onBack: () -> Unit,
    onGenerate: (QuestInput) -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val systemCopy = systemStrings(LocalTodayPlayLocale.current)
    var stepIndex by rememberSaveable { mutableIntStateOf(0) }
    var relationship by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var customCity by rememberSaveable { mutableStateOf("") }
    var timeWindow by rememberSaveable { mutableStateOf("") }
    var budget by rememberSaveable { mutableStateOf("") }
    var transportMode by rememberSaveable { mutableStateOf("") }
    var interests by rememberSaveable { mutableStateOf(listOf<String>()) }
    var noAwkwardMode by rememberSaveable { mutableStateOf(true) }
    var note by rememberSaveable { mutableStateOf("") }

    val totalSteps = 7
    val selectedCity = city.ifBlank { customCity.trim() }

    fun advance() {
        if (stepIndex < totalSteps - 1) stepIndex += 1
    }

    fun retreat() {
        if (stepIndex > 0) stepIndex -= 1 else onBack()
    }

    fun toggleInterest(label: String) {
        interests = if (label in interests) {
            interests - label
        } else {
            (interests + label).take(4)
        }
    }

    fun generate() {
        val missing = when {
            relationship.isBlank() -> systemCopy.missingRelationship
            selectedCity.isBlank() -> systemCopy.missingCity
            timeWindow.isBlank() -> systemCopy.missingTime
            budget.isBlank() -> systemCopy.missingBudget
            transportMode.isBlank() -> systemCopy.missingTransport
            interests.isEmpty() -> systemCopy.missingInterests
            else -> null
        }
        if (missing != null) {
            Toast.makeText(context, systemCopy.missingRequired(missing), Toast.LENGTH_SHORT).show()
            return
        }
        val moods = buildList {
            addAll(interests)
            if (noAwkwardMode) add("社恐友好")
            if (relationship == "暧昧中") add("低压力破冰")
        }
        onGenerate(
            QuestInput(
                relationship = relationship,
                city = selectedCity,
                moods = moods,
                time = timeWindow,
                budget = budget,
                vibe = vibeFor(interests, relationship),
                note = buildList {
                    add("请生成地点、路线、导航入口、打卡任务、避坑提示和奖励积分。")
                    add("如果城市没有本地样例，请使用全球热门样例并标注需要官方数据核验。")
                    if (noAwkwardMode) add("互动任务保持低压力、可跳过、尊重边界。")
                    note.takeIf { it.isNotBlank() }?.let { add(it) }
                }.joinToString(" "),
                transportMode = transportMode,
            ),
        )
    }

    PaperBackground {
        Column(Modifier.fillMaxSize()) {
            CuteTopBar(title = "路线条件卡", subtitle = "card flow", onBack = ::retreat, action = "${stepIndex + 1}/$totalSteps")
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val horizontalPadding = if (maxWidth < 360.dp) 14.dp else 22.dp
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item {
                        StepProgress(stepIndex = stepIndex, totalSteps = totalSteps, modifier = Modifier.widthIn(max = 720.dp))
                    }
                    item {
                        when (stepIndex) {
                            0 -> RelationshipStep(
                                selected = relationship,
                                onSelected = {
                                    relationship = it
                                    advance()
                                },
                            )
                            1 -> CityStep(
                                selected = city,
                                customCity = customCity,
                                onCitySelected = {
                                    city = it
                                    customCity = ""
                                    advance()
                                },
                                onCustomCityChange = {
                                    customCity = it
                                    city = ""
                                },
                                onContinue = {
                                    if (selectedCity.isNotBlank()) advance()
                                },
                            )
                            2 -> OptionStep(
                                eyebrow = "TIME",
                                title = "今天有多久？",
                                subtitle = "路线站点数量会跟着时间变化。",
                                options = timeOptions(),
                                selected = timeWindow,
                                onSelected = {
                                    timeWindow = it
                                    advance()
                                },
                            )
                            3 -> OptionStep(
                                eyebrow = "BUDGET",
                                title = "人均预算大概多少？",
                                subtitle = "系统会优先避开不适合预算的路线。",
                                options = budgetOptions(),
                                selected = budget,
                                onSelected = {
                                    budget = it
                                    advance()
                                },
                            )
                            4 -> OptionStep(
                                eyebrow = "TRANSPORT",
                                title = "更想怎么过去？",
                                subtitle = "正式版会接地图路线 API 计算真实距离和耗时。",
                                options = transportOptions(),
                                selected = transportMode,
                                onSelected = {
                                    transportMode = it
                                    advance()
                                },
                            )
                            5 -> InterestStep(
                                selected = interests,
                                onToggle = ::toggleInterest,
                                onContinue = {
                                    if (interests.isNotEmpty()) advance()
                                },
                            )
                            else -> FinalStep(
                                relationship = relationship,
                                city = selectedCity,
                                timeWindow = timeWindow,
                                budget = budget,
                                transportMode = transportMode,
                                interests = interests,
                                noAwkwardMode = noAwkwardMode,
                                note = note,
                                onToggleComfort = { noAwkwardMode = !noAwkwardMode },
                                onNoteChange = { if (it.length <= 90) note = it },
                                onGenerate = ::generate,
                            )
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .widthIn(max = 720.dp)
                                .padding(bottom = 22.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            GhostButton(text = if (stepIndex == 0) "返回" else "上一张", onClick = ::retreat, modifier = Modifier.weight(1f))
                            if (stepIndex in 1 until totalSteps - 1) {
                                GhostButton(text = "跳到下一张", onClick = ::advance, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepProgress(stepIndex: Int, totalSteps: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        repeat(totalSteps) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (index == stepIndex) 22.dp else 10.dp)
                    .background(
                        color = if (index <= stepIndex) CherryPressed else LineBeige,
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Composable
private fun StepShell(
    eyebrow: String,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(Modifier.widthIn(max = 720.dp)) {
        val compact = maxWidth < 360.dp
        SoftCard(padding = if (compact) 16.dp else 22.dp) {
            Text(eyebrow, color = RoseGold, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                color = InkBlack,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))
            Text(subtitle, color = WarmGray, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(if (compact) 14.dp else 18.dp))
            content()
        }
    }
}

@Composable
private fun RelationshipStep(selected: String, onSelected: (String) -> Unit) {
    StepShell(
        eyebrow = "WHO",
        title = "今天和谁一起？",
        subtitle = "关系会影响路线节奏、互动任务和拍照建议。",
    ) {
        FlowChoiceGrid(
            options = listOf("情侣", "暧昧中", "朋友", "家人", "一个人散心"),
            selected = listOf(selected),
            onClick = onSelected,
        )
    }
}

@Composable
private fun CityStep(
    selected: String,
    customCity: String,
    onCitySelected: (String) -> Unit,
    onCustomCityChange: (String) -> Unit,
    onContinue: () -> Unit,
) {
    StepShell(
        eyebrow = "CITY",
        title = "想去哪个城市？",
        subtitle = "先支持全球城市输入；当前本地样例覆盖部分热门城市。",
    ) {
        FlowChoiceGrid(
            options = GlobalPoiMockData.citySuggestions,
            selected = listOf(selected),
            onClick = onCitySelected,
        )
        Spacer(Modifier.height(14.dp))
        TextInputCard(
            value = customCity,
            onValueChange = onCustomCityChange,
            placeholder = "也可以手动输入：大阪、洛杉矶、广州塔周边...",
        )
        Spacer(Modifier.height(14.dp))
        HeartPrimaryButton(text = "使用这个城市", onClick = onContinue)
    }
}

@Composable
private fun OptionStep(
    eyebrow: String,
    title: String,
    subtitle: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
) {
    StepShell(eyebrow = eyebrow, title = title, subtitle = subtitle) {
        FlowChoiceGrid(options = options, selected = listOf(selected), onClick = onSelected)
    }
}

@Composable
private fun InterestStep(
    selected: List<String>,
    onToggle: (String) -> Unit,
    onContinue: () -> Unit,
) {
    StepShell(
        eyebrow = "NEEDS",
        title = "今天最想要什么？",
        subtitle = "最多选 4 个，系统会把多人需求合并成路线。",
    ) {
        FlowChoiceGrid(
            options = interestOptions(),
            selected = selected,
            onClick = onToggle,
        )
        Spacer(Modifier.height(14.dp))
        HeartPrimaryButton(
            text = if (selected.isEmpty()) "先选 1 个偏好" else "继续生成摘要",
            onClick = onContinue,
        )
    }
}

@Composable
private fun FinalStep(
    relationship: String,
    city: String,
    timeWindow: String,
    budget: String,
    transportMode: String,
    interests: List<String>,
    noAwkwardMode: Boolean,
    note: String,
    onToggleComfort: () -> Unit,
    onNoteChange: (String) -> Unit,
    onGenerate: () -> Unit,
) {
    StepShell(
        eyebrow = "READY",
        title = "确认今天的路线方向",
        subtitle = "生成后会得到路线概览、地图入口、每站任务、打卡积分和通关卡。",
    ) {
        SummaryLine("同行", relationship)
        SummaryLine("城市", city)
        SummaryLine("时间", timeWindow)
        SummaryLine("预算", budget)
        SummaryLine("交通", transportMode)
        SummaryLine("偏好", interests.joinToString(" / "))
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("低压力互动", color = InkBlack, style = MaterialTheme.typography.titleMedium)
                Text("默认减少尴尬任务，不做人脸识别，不强制定位。", color = WarmGray, style = MaterialTheme.typography.bodyMedium)
            }
            KawaiiChip(text = if (noAwkwardMode) "打开" else "关闭", selected = noAwkwardMode, onClick = onToggleComfort)
        }
        Spacer(Modifier.height(14.dp))
        TextInputCard(
            value = note,
            onValueChange = onNoteChange,
            placeholder = "补一句：想避开排队、要适合拍照、最好有晚餐...",
            minLines = 3,
        )
        Spacer(Modifier.height(6.dp))
        Text("${note.length}/90", color = WarmGray, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        HeartPrimaryButton(text = "生成全球路线副本", onClick = onGenerate)
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun FlowChoiceGrid(
    options: List<String>,
    selected: List<String>,
    onClick: (String) -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val compact = maxWidth < 360.dp
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { option ->
                ChoiceCard(
                    label = option,
                    selected = option in selected,
                    onClick = { onClick(option) },
                    modifier = if (compact) Modifier.fillMaxWidth() else Modifier.widthIn(min = 132.dp, max = 196.dp),
                )
            }
        }
    }
}

@Composable
private fun ChoiceCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TicketCard(
        modifier = modifier
            .clickable { onClick() },
    ) {
        Text(
            text = label,
            color = if (selected) CherryPressed else InkBlack,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (selected) "已选择" else cardHint(label),
            color = WarmGray,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.Top) {
        Text(label, color = CherryPressed, style = MaterialTheme.typography.labelLarge, modifier = Modifier.widthIn(min = 54.dp))
        Text(value.ifBlank { "未选择" }, color = InkBlack, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
}

private fun timeOptions() = listOf("90 分钟", "2 小时", "半天", "一整天", "周末两天")

private fun budgetOptions() = listOf("0 元也可以", "50 元以内", "100 元以内", "300 元以内", "不设上限")

private fun transportOptions() = listOf("公共交通/步行", "打车", "自驾", "骑行", "按地图推荐")

private fun interestOptions() = listOf(
    "拍照打卡",
    "吃饭探店",
    "Citywalk",
    "看展文化",
    "低预算",
    "轻松不累",
    "夜景浪漫",
    "亲子友好",
    "朋友搞笑",
    "经典景点",
    "小众路线",
    "雨天备选",
)

private fun vibeFor(interests: List<String>, relationship: String): String {
    return when {
        "夜景浪漫" in interests || relationship == "情侣" -> "电影感路线"
        "吃饭探店" in interests -> "本地生活探店"
        "看展文化" in interests -> "文化路线"
        "朋友搞笑" in interests -> "朋友挑战"
        "亲子友好" in interests -> "家庭陪伴"
        else -> "轻松探索"
    }
}

private fun cardHint(label: String): String {
    return when {
        label.contains("情侣") -> "浪漫但不尬"
        label.contains("朋友") -> "轻松有梗"
        label.contains("家人") -> "安全舒适"
        label.contains("散心") -> "自我照顾"
        label.contains("分钟") || label.contains("小时") || label.contains("半天") || label.contains("一整天") -> "控制站点"
        label.contains("元") || label.contains("上限") -> "匹配消费"
        label.contains("公共") || label.contains("打车") || label.contains("自驾") || label.contains("骑行") -> "影响动线"
        else -> "点选进入"
    }
}
