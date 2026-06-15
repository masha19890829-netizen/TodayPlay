package com.todayplay.app.ui.screens

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.todayplay.app.BuildConfig
import com.todayplay.app.localization.LocalTodayPlayLocale
import com.todayplay.app.localization.TodayPlayLocale
import com.todayplay.app.localization.privacyStrings
import com.todayplay.app.ui.components.CuteTopBar
import com.todayplay.app.ui.components.GhostButton
import com.todayplay.app.ui.components.HeartPrimaryButton
import com.todayplay.app.ui.components.InfoLine
import com.todayplay.app.ui.components.KawaiiChip
import com.todayplay.app.ui.components.PaperBackground
import com.todayplay.app.ui.components.SectionHeader
import com.todayplay.app.ui.components.TicketCard
import com.todayplay.app.ui.theme.CherryPressed
import com.todayplay.app.ui.theme.InkBlack
import com.todayplay.app.ui.theme.WarmGray

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrivacyScreen(
    selectedLocale: TodayPlayLocale,
    onLocaleSelected: (TodayPlayLocale) -> Unit,
    historyCount: Int,
    onBack: () -> Unit,
    onClearLocalHistory: () -> Unit,
) {
    val copy = privacyStrings(LocalTodayPlayLocale.current)
    val uriHandler = LocalUriHandler.current
    var cleared by remember { mutableStateOf(false) }
    val configuredSupportEmail = BuildConfig.SUPPORT_EMAIL.trim().takeIf { it.isNotBlank() }
    val configuredPrivacyPolicyUrl = BuildConfig.PRIVACY_POLICY_URL.trim()
        .takeIf { it.startsWith("https://") }
    val supportEmail = configuredSupportEmail ?: copy.notConfigured
    val privacyPolicyUrl = configuredPrivacyPolicyUrl ?: copy.notConfigured

    PaperBackground {
        Column(Modifier.fillMaxSize()) {
            CuteTopBar(title = copy.title, subtitle = copy.subtitle, onBack = onBack, action = "心")
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    SectionHeader("00", copy.languageTitle, "settings")
                }
                item {
                    TicketCard {
                        Text(copy.languageTitle, style = MaterialTheme.typography.titleLarge, color = InkBlack)
                        Spacer(Modifier.height(8.dp))
                        Text(copy.languageBody, style = MaterialTheme.typography.bodyMedium, color = WarmGray)
                        Spacer(Modifier.height(12.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            TodayPlayLocale.entries.forEach { locale ->
                                KawaiiChip(
                                    text = locale.nativeLabel,
                                    selected = selectedLocale == locale,
                                    onClick = { onLocaleSelected(locale) },
                                )
                            }
                        }
                    }
                }
                item {
                    SectionHeader("01", copy.localDataTitle, "local storage")
                }
                item {
                    TicketCard {
                        Text(copy.localDataTitle, style = MaterialTheme.typography.titleLarge, color = InkBlack)
                        Spacer(Modifier.height(8.dp))
                        Text(copy.localDataBody, style = MaterialTheme.typography.bodyMedium, color = WarmGray)
                        Spacer(Modifier.height(12.dp))
                        InfoLine("Status", copy.historyCount(historyCount))
                        if (cleared) {
                            Spacer(Modifier.height(8.dp))
                            Text(copy.clearDone, style = MaterialTheme.typography.bodyMedium, color = CherryPressed)
                        }
                    }
                }
                item {
                    TicketCard {
                        Text(copy.supportTitle, style = MaterialTheme.typography.titleLarge, color = InkBlack)
                        Spacer(Modifier.height(10.dp))
                        ContactLine(label = copy.supportEmailLabel, value = supportEmail)
                        Spacer(Modifier.height(10.dp))
                        ContactLine(label = copy.privacyPolicyLabel, value = privacyPolicyUrl)
                        Spacer(Modifier.height(12.dp))
                        GhostButton(
                            text = copy.contactSupport,
                            onClick = { configuredSupportEmail?.let { uriHandler.openUri("mailto:$it") } },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = configuredSupportEmail != null,
                        )
                        Spacer(Modifier.height(8.dp))
                        GhostButton(
                            text = copy.openPrivacyPolicy,
                            onClick = { configuredPrivacyPolicyUrl?.let(uriHandler::openUri) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = configuredPrivacyPolicyUrl != null,
                        )
                    }
                }
                item {
                    TicketCard {
                        Text(copy.safetyTitle, style = MaterialTheme.typography.titleLarge, color = InkBlack)
                        Spacer(Modifier.height(8.dp))
                        copy.safetyItems.forEach { item ->
                            Text("• $item", style = MaterialTheme.typography.bodyMedium, color = WarmGray)
                            Spacer(Modifier.height(6.dp))
                        }
                    }
                }
                item {
                    HeartPrimaryButton(
                        text = copy.clearLocalHistory,
                        onClick = {
                            onClearLocalHistory()
                            cleared = true
                        },
                    )
                    Spacer(Modifier.height(10.dp))
                    GhostButton(text = copy.backHome, onClick = onBack, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun ContactLine(label: String, value: String) {
    Text(label, style = MaterialTheme.typography.labelLarge, color = WarmGray)
    Spacer(Modifier.height(4.dp))
    Text(
        text = value,
        style = MaterialTheme.typography.bodyMedium,
        color = InkBlack,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis,
    )
}
