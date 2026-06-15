package com.todayplay.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.todayplay.app.localization.LocalTodayPlayLocale
import com.todayplay.app.localization.QuickStartItemCopy
import com.todayplay.app.localization.quickStartStrings
import com.todayplay.app.model.QuestInput
import com.todayplay.app.ui.components.CuteTopBar
import com.todayplay.app.ui.components.GhostButton
import com.todayplay.app.ui.components.PaperBackground
import com.todayplay.app.ui.components.SectionHeader
import com.todayplay.app.ui.components.TicketCard
import com.todayplay.app.ui.theme.CherryPressed
import com.todayplay.app.ui.theme.InkBlack
import com.todayplay.app.ui.theme.WarmGray

@Composable
fun QuickStartScreen(
    onBack: () -> Unit,
    onGenerate: (QuestInput) -> Unit,
) {
    val copy = quickStartStrings(LocalTodayPlayLocale.current)
    PaperBackground {
        Column(Modifier.fillMaxSize()) {
            CuteTopBar(title = copy.title, subtitle = copy.subtitle, onBack = onBack, action = "心")
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val horizontalPadding = if (maxWidth < 360.dp) 16.dp else 22.dp
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    item {
                        SectionHeader("01", copy.sectionTitle, copy.sectionEnglish)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            copy.intro,
                            color = WarmGray,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    item {
                        QuickStartGrid(
                            wide = maxWidth >= 720.dp,
                            items = copy.items,
                            generateText = copy.generate,
                            onGenerate = onGenerate,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickStartGrid(
    wide: Boolean,
    items: List<QuickStartItemCopy>,
    generateText: String,
    onGenerate: (QuestInput) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items.forEach { item ->
            QuickStartCard(
                item = item,
                generateText = generateText,
                onGenerate = onGenerate,
                modifier = if (wide) Modifier.width(340.dp) else Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun QuickStartCard(
    item: QuickStartItemCopy,
    generateText: String,
    onGenerate: (QuestInput) -> Unit,
    modifier: Modifier = Modifier,
) {
    TicketCard(modifier = modifier) {
        Text(
            item.title,
            style = MaterialTheme.typography.titleLarge,
            color = InkBlack,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            item.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = WarmGray,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            item.tags.joinToString(" / "),
            color = CherryPressed,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(14.dp))
        GhostButton(text = generateText, onClick = { onGenerate(item.input) }, modifier = Modifier.fillMaxWidth())
    }
}
