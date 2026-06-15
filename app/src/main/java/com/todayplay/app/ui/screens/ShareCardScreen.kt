package com.todayplay.app.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.todayplay.app.data.ProductEventLogger
import com.todayplay.app.localization.LocalTodayPlayLocale
import com.todayplay.app.localization.ShareStrings
import com.todayplay.app.localization.shareStrings
import com.todayplay.app.model.AccountSession
import com.todayplay.app.model.CompletionCardData
import com.todayplay.app.model.Quest
import com.todayplay.app.model.QuestRecord
import com.todayplay.app.ui.components.CuteTopBar
import com.todayplay.app.ui.components.GhostButton
import com.todayplay.app.ui.components.HeartPrimaryButton
import com.todayplay.app.ui.components.InfoLine
import com.todayplay.app.ui.components.PaperBackground
import com.todayplay.app.ui.components.SectionHeader
import com.todayplay.app.ui.components.ShareCompletionCard
import com.todayplay.app.ui.components.TicketCard
import com.todayplay.app.ui.theme.CherryPressed
import com.todayplay.app.ui.theme.InkBlack
import com.todayplay.app.ui.theme.WarmGray

@Composable
fun ShareCardScreen(
    record: QuestRecord,
    accountSession: AccountSession?,
    googleSignInAvailable: Boolean,
    accountBusy: Boolean,
    accountMessage: String?,
    onGoogleSignIn: () -> Unit,
    onLocalTesterSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onBack: () -> Unit,
    onRestart: () -> Unit,
    onHome: () -> Unit,
) {
    val context = LocalContext.current
    val quest = record.quest
    val copy = shareStrings(LocalTodayPlayLocale.current)
    val completionCardData = CompletionCardData.from(record)
    val completionFirst = completionCardData.totalTaskCount > 0 &&
        completionCardData.completedTaskCount == completionCardData.totalTaskCount

    fun shareInvite() {
        ProductEventLogger.track("system_share_invite", mapOf("questId" to quest.questId))
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, copy.inviteSubject(quest))
            putExtra(Intent.EXTRA_TEXT, copy.buildInviteText(quest).withShareAttribution(accountSession))
        }
        context.startActivity(Intent.createChooser(sendIntent, copy.chooserTitle))
    }

    fun shareCompletion() {
        ProductEventLogger.track("system_share_completion", mapOf("questId" to quest.questId))
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, copy.completionSubject(completionCardData))
            putExtra(Intent.EXTRA_TEXT, copy.buildCompletionText(completionCardData).withShareAttribution(accountSession))
        }
        context.startActivity(Intent.createChooser(sendIntent, copy.chooserTitle))
    }

    PaperBackground {
        Column(Modifier.fillMaxSize()) {
            CuteTopBar(title = copy.title, subtitle = copy.subtitle, onBack = onBack, action = "心")
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    ShareAccountSection(
                        accountSession = accountSession,
                        googleSignInAvailable = googleSignInAvailable,
                        accountBusy = accountBusy,
                        accountMessage = accountMessage,
                        onGoogleSignIn = onGoogleSignIn,
                        onLocalTesterSignIn = onLocalTesterSignIn,
                        onSignOut = onSignOut,
                    )
                }
                if (completionFirst) {
                    item { ShareCompletionSection(completionCardData, copy, onShareCompletion = ::shareCompletion, number = "01") }
                    item { ShareInviteSection(quest, copy, onShareInvite = ::shareInvite, number = "02") }
                } else {
                    item { ShareInviteSection(quest, copy, onShareInvite = ::shareInvite, number = "01") }
                    item { ShareCompletionSection(completionCardData, copy, onShareCompletion = ::shareCompletion, number = "02") }
                }
                item {
                    Box(Modifier.fillParentMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(Modifier.widthIn(max = 430.dp)) {
                            Text(copy.screenshotTip, color = WarmGray)
                            Spacer(Modifier.height(8.dp))
                            HeartPrimaryButton(
                                text = copy.saveCard,
                                onClick = {
                                    ProductEventLogger.track("save_completion_card", mapOf("questId" to quest.questId))
                                    Toast.makeText(context, copy.saveToast, Toast.LENGTH_SHORT).show()
                                },
                            )
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 24.dp)) {
                                GhostButton(copy.restart, onClick = onRestart, modifier = Modifier.weight(1f))
                                GhostButton(copy.backHome, onClick = onHome, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShareAccountSection(
    accountSession: AccountSession?,
    googleSignInAvailable: Boolean,
    accountBusy: Boolean,
    accountMessage: String?,
    onGoogleSignIn: () -> Unit,
    onLocalTesterSignIn: () -> Unit,
    onSignOut: () -> Unit,
) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        TicketCard(Modifier.widthIn(max = 430.dp)) {
            Text("ACCOUNT", fontFamily = FontFamily.Serif, fontSize = 14.sp, color = CherryPressed, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            if (accountSession != null) {
                Text(
                    accountSession.shareName,
                    color = InkBlack,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    accountSession.providerLabel,
                    color = WarmGray,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                GhostButton("Sign out", onClick = onSignOut, modifier = Modifier.fillMaxWidth())
            } else {
                Text(
                    "Add a tester or Google profile before sharing so the invite carries a recognizable sender.",
                    color = WarmGray,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(12.dp))
                HeartPrimaryButton(
                    text = if (accountBusy) "Signing in..." else "Continue with Google",
                    onClick = onGoogleSignIn,
                    enabled = googleSignInAvailable && !accountBusy,
                )
                Spacer(Modifier.height(8.dp))
                GhostButton(
                    "Use local tester profile",
                    onClick = onLocalTesterSignIn,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !accountBusy,
                )
                if (!googleSignInAvailable) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Google OAuth is not configured in this APK yet.",
                        color = CherryPressed,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            if (!accountMessage.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    accountMessage,
                    color = WarmGray,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

private fun String.withShareAttribution(accountSession: AccountSession?): String {
    if (accountSession == null) return this
    return "$this\n\nShared from TodayPlay by ${accountSession.shareName}."
}

@Composable
private fun ShareInviteSection(
    quest: Quest,
    copy: ShareStrings,
    onShareInvite: () -> Unit,
    number: String,
) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(Modifier.widthIn(max = 430.dp)) {
            SectionHeader(number, copy.inviteSectionTitle, copy.inviteSectionEnglish)
            Spacer(Modifier.height(10.dp))
            ShareInviteCard(quest, copy)
            Spacer(Modifier.height(12.dp))
            HeartPrimaryButton(text = copy.sendInvite, onClick = onShareInvite)
        }
    }
}

@Composable
private fun ShareCompletionSection(
    cardData: CompletionCardData,
    copy: ShareStrings,
    onShareCompletion: () -> Unit,
    number: String,
) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(Modifier.widthIn(max = 430.dp)) {
            SectionHeader(number, copy.completionSectionTitle, copy.completionSectionEnglish)
            Spacer(Modifier.height(10.dp))
            ShareCompletionCard(cardData = cardData, labels = copy.completionCard)
            Spacer(Modifier.height(12.dp))
            HeartPrimaryButton(text = copy.sendCompletion, onClick = onShareCompletion)
        }
    }
}

@Composable
private fun ShareInviteCard(quest: Quest, copy: ShareStrings) {
    TicketCard {
        Text("TONIGHT QUEST", fontFamily = FontFamily.Serif, fontSize = 14.sp, color = CherryPressed, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            quest.title,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = 36.sp,
            color = InkBlack,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 380.dp),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            copy.inviteBody,
            color = WarmGray,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        InfoLine(copy.relationshipLabel, quest.relationship)
        InfoLine(copy.durationLabel, quest.duration)
        InfoLine(copy.budgetLabel, quest.budget)
        InfoLine(copy.keywordsLabel, quest.completionKeywords.joinToString(" / "))
        Spacer(Modifier.height(16.dp))
        Text(
            copy.invitePrivacyNote,
            color = CherryPressed,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}
