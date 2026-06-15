package com.todayplay.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.todayplay.app.localization.LocalTodayPlayLocale
import com.todayplay.app.localization.historyStrings
import com.todayplay.app.model.CompletionCardData
import com.todayplay.app.model.QuestRecord
import com.todayplay.app.ui.components.CuteTopBar
import com.todayplay.app.ui.components.GhostButton
import com.todayplay.app.ui.components.HeartPrimaryButton
import com.todayplay.app.ui.components.InfoLine
import com.todayplay.app.ui.components.PaperBackground
import com.todayplay.app.ui.components.SectionHeader
import com.todayplay.app.ui.components.TicketCard
import com.todayplay.app.ui.theme.CherryPressed
import com.todayplay.app.ui.theme.InkBlack
import com.todayplay.app.ui.theme.WarmGray

@Composable
fun HistoryScreen(
    records: List<QuestRecord>,
    onBack: () -> Unit,
    onOpen: (QuestRecord) -> Unit,
    onReplay: (QuestRecord) -> Unit,
    onStart: () -> Unit,
) {
    val copy = historyStrings(LocalTodayPlayLocale.current)
    PaperBackground {
        Column(Modifier.fillMaxSize()) {
            CuteTopBar(title = copy.title, subtitle = copy.subtitle, onBack = onBack, action = "心")
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    SectionHeader("00", copy.sectionTitle, copy.sectionEnglish)
                }
                if (records.isEmpty()) {
                    item {
                        TicketCard {
                            Text(copy.emptyTitle, style = MaterialTheme.typography.titleLarge, color = InkBlack)
                            Spacer(Modifier.height(8.dp))
                            Text(copy.emptyBody, style = MaterialTheme.typography.bodyMedium, color = WarmGray)
                            Spacer(Modifier.height(16.dp))
                            HeartPrimaryButton(text = copy.startFirst, onClick = onStart)
                        }
                    }
                } else {
                    items(records.size) { index ->
                        val record = records[index]
                        val quest = record.quest
                        val cardData = CompletionCardData.from(record)
                        TicketCard {
                            Text("NO. ${(index + 1).toString().padStart(2, '0')}", color = CherryPressed, style = MaterialTheme.typography.labelSmall)
                            Text(quest.title, style = MaterialTheme.typography.titleLarge, color = InkBlack)
                            Spacer(Modifier.height(8.dp))
                            InfoLine(copy.relationshipLabel, quest.relationship)
                            quest.itineraryPlan?.let { plan ->
                                InfoLine(copy.routeLabel, plan.planType)
                                InfoLine(copy.stopCheckInLabel, "${cardData.checkedInStopCount}/${cardData.totalStopCount}")
                                InfoLine(copy.pointsLabel, "${cardData.totalRewardPoints}")
                            }
                            InfoLine(copy.durationLabel, quest.duration)
                            InfoLine(copy.completionLabel, "${cardData.completedTaskCount}/${cardData.totalTaskCount}")
                            InfoLine(copy.hiddenTaskLabel, cardData.hiddenTaskLabel)
                            InfoLine(copy.keywordsLabel, quest.completionKeywords.joinToString(" / "))
                            Spacer(Modifier.height(8.dp))
                            Text(copy.openHint, color = CherryPressed, style = MaterialTheme.typography.bodyMedium)
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
