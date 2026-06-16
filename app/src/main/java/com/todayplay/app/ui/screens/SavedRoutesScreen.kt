package com.todayplay.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.todayplay.app.localization.LocalTodayPlayLocale
import com.todayplay.app.localization.TodayPlayLocale
import com.todayplay.app.model.QuestRecord
import com.todayplay.app.ui.components.CuteTopBar
import com.todayplay.app.ui.components.GhostButton
import com.todayplay.app.ui.components.HeartPrimaryButton
import com.todayplay.app.ui.components.InfoLine
import com.todayplay.app.ui.components.PaperBackground
import com.todayplay.app.ui.components.TicketCard
import com.todayplay.app.ui.theme.CherryPressed
import com.todayplay.app.ui.theme.InkBlack
import com.todayplay.app.ui.theme.WarmGray

@Composable
fun SavedRoutesScreen(
    records: List<QuestRecord>,
    onBack: () -> Unit,
    onOpen: (QuestRecord) -> Unit,
    onReplay: (QuestRecord) -> Unit,
    onStart: () -> Unit,
) {
    val copy = savedRoutesCopy(LocalTodayPlayLocale.current)
    PaperBackground {
        Column(Modifier.fillMaxSize()) {
            CuteTopBar(title = copy.title, subtitle = copy.subtitle, onBack = onBack, action = "夹")
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                if (records.isEmpty()) {
                    item {
                        TicketCard {
                            Text(copy.emptyTitle, style = MaterialTheme.typography.titleLarge, color = InkBlack)
                            Spacer(Modifier.height(8.dp))
                            Text(copy.emptyBody, style = MaterialTheme.typography.bodyMedium, color = WarmGray)
                            Spacer(Modifier.height(16.dp))
                            HeartPrimaryButton(text = copy.emptyAction, onClick = onStart)
                        }
                    }
                } else {
                    items(records.size) { index ->
                        val record = records[index]
                        val quest = record.quest
                        val plan = quest.itineraryPlan
                        TicketCard {
                            Text(copy.badge(index + 1), color = CherryPressed, style = MaterialTheme.typography.labelSmall)
                            Text(
                                quest.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = InkBlack,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(Modifier.height(8.dp))
                            InfoLine(copy.cityLabel, quest.city ?: plan?.city ?: copy.unknown)
                            InfoLine(copy.relationshipLabel, quest.relationship)
                            InfoLine(copy.durationLabel, quest.duration)
                            plan?.let {
                                InfoLine(copy.stopsLabel, "${it.stops.size}")
                                InfoLine(copy.fitLabel, it.routeSummary)
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                GhostButton(text = copy.openAction, onClick = { onOpen(record) }, modifier = Modifier.weight(1f))
                                HeartPrimaryButton(text = copy.replayAction, onClick = { onReplay(record) }, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class SavedRoutesCopy(
    val title: String,
    val subtitle: String,
    val emptyTitle: String,
    val emptyBody: String,
    val emptyAction: String,
    val cityLabel: String,
    val relationshipLabel: String,
    val durationLabel: String,
    val stopsLabel: String,
    val fitLabel: String,
    val openAction: String,
    val replayAction: String,
    val unknown: String,
) {
    fun badge(index: Int): String = "SAVED ${index.toString().padStart(2, '0')}"
}

private fun savedRoutesCopy(locale: TodayPlayLocale): SavedRoutesCopy {
    return when (locale) {
        TodayPlayLocale.SimplifiedChinese,
        TodayPlayLocale.TraditionalChinese -> SavedRoutesCopy(
            title = "收藏的路线",
            subtitle = "再走一次的路线夹",
            emptyTitle = "还没有收藏路线",
            emptyBody = "从首页开始一条路线，生成或保存后会出现在这里。",
            emptyAction = "去首页找一条",
            cityLabel = "城市",
            relationshipLabel = "关系",
            durationLabel = "时长",
            stopsLabel = "站点",
            fitLabel = "适合你的理由",
            openAction = "打开",
            replayAction = "再来一版",
            unknown = "同城",
        )
        else -> SavedRoutesCopy(
            title = "Saved routes",
            subtitle = "Routes worth trying again",
            emptyTitle = "No saved routes yet",
            emptyBody = "Start a route from Home. Generated or saved routes will appear here.",
            emptyAction = "Find a route",
            cityLabel = "City",
            relationshipLabel = "Relation",
            durationLabel = "Time",
            stopsLabel = "Stops",
            fitLabel = "Why it fits",
            openAction = "Open",
            replayAction = "Try again",
            unknown = "Same city",
        )
    }
}
