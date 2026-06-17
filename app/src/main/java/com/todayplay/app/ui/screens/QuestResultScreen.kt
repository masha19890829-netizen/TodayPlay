package com.todayplay.app.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.todayplay.app.BuildConfig
import com.todayplay.app.R
import com.todayplay.app.model.FeedbackReason
import com.todayplay.app.model.ItineraryPlan
import com.todayplay.app.model.Quest
import com.todayplay.app.model.QuestRecord
import com.todayplay.app.model.QuestTask
import com.todayplay.app.model.RouteStop
import com.todayplay.app.model.RouteStopReplacementPreview
import com.todayplay.app.model.RouteStopRestoreSnapshot
import com.todayplay.app.model.TaskStatus
import com.todayplay.app.localization.LocalTodayPlayLocale
import com.todayplay.app.localization.ResultStrings
import com.todayplay.app.localization.TodayPlayLocale
import com.todayplay.app.localization.resultStrings
import com.todayplay.app.localization.systemStrings
import com.todayplay.app.navigation.MapNavigator
import com.todayplay.app.ui.components.CuteTopBar
import com.todayplay.app.ui.components.GhostButton
import com.todayplay.app.ui.components.HeartPrimaryButton
import com.todayplay.app.ui.components.InfoLine
import com.todayplay.app.ui.components.KawaiiChip
import com.todayplay.app.ui.components.PaperBackground
import com.todayplay.app.ui.components.SectionHeader
import com.todayplay.app.ui.components.SoftCard
import com.todayplay.app.ui.components.TicketCard
import com.todayplay.app.ui.theme.BlackCherry
import com.todayplay.app.ui.theme.CherryPressed
import com.todayplay.app.ui.theme.GalleryWhite
import com.todayplay.app.ui.theme.InkBlack
import com.todayplay.app.ui.theme.LineBeige
import com.todayplay.app.ui.theme.RoseGold
import com.todayplay.app.ui.theme.TicketBeige
import com.todayplay.app.ui.theme.WarmCream
import com.todayplay.app.ui.theme.WarmGray
import kotlinx.coroutines.delay

@Composable
fun QuestResultScreen(
    record: QuestRecord,
    onUpdateTaskStatus: (String, TaskStatus) -> Unit,
    onToggleFeedback: (String, FeedbackReason) -> Unit,
    onPreviewRouteStopReplacement: (String) -> RouteStopReplacementPreview?,
    onReplaceRouteStop: (String) -> Boolean,
    onRestoreRouteStop: () -> Boolean,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onRegenerate: () -> Unit,
    onTuneRoute: (String) -> Unit,
    onSave: () -> Unit,
    onHome: () -> Unit,
) {
    val quest = record.quest
    val progressState = record.progress
    val context = LocalContext.current
    val locale = LocalTodayPlayLocale.current
    val systemCopy = systemStrings(locale)
    val copy = resultStrings(locale)
    val itineraryPlan = quest.itineraryPlan
    var visibleCount by remember(quest) { mutableIntStateOf(0) }
    val routeTaskCount = itineraryPlan?.stops?.size ?: 0
    val totalSteps = quest.tasks.size + 1 + routeTaskCount
    val resolvedSteps = (quest.tasks + quest.hiddenTask).count { task ->
        progressState.statusFor(task.taskId).isResolved()
    } + (itineraryPlan?.stops?.count { stop ->
        progressState.statusFor(stop.checkInTask.taskId).isResolved()
    } ?: 0)
    val progress = (resolvedSteps.toFloat() / totalSteps.toFloat()).coerceIn(0f, 1f)

    LaunchedEffect(quest) {
        visibleCount = 0
        repeat(9) {
            delay(95)
            visibleCount += 1
        }
    }
    PaperBackground {
        Column(Modifier.fillMaxSize()) {
            CuteTopBar(title = copy.topTitle, subtitle = copy.topSubtitle, onBack = onBack, action = copy.saveAction, onAction = onSave)
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val horizontalPadding = if (maxWidth < 360.dp) 14.dp else 18.dp
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    ResultHero(quest, copy)
                }
                if (itineraryPlan != null) {
                    if (record.isTimeCinemaRoute()) {
                        item {
                            FadeItem(visibleCount >= 1) {
                                TimeCinemaTicketCard(
                                    record = record,
                                    locale = locale,
                                    onOpenMap = { stop -> MapNavigator.openInAmap(context, stop.navigationAction, systemCopy) },
                                )
                            }
                        }
                    }
                    item {
                        FadeItem(visibleCount >= 2) {
                            PersonalFitCard(record = record, locale = locale)
                        }
                    }
                    item {
                        FadeItem(visibleCount >= 3) {
                            RouteTuneCard(onTuneRoute = onTuneRoute)
                        }
                    }
                    item {
                        FadeItem(visibleCount >= 4) {
                            RouteVisualPreviewCard(
                                record = record,
                                locale = locale,
                                onOpenMap = { stop -> MapNavigator.openInAmap(context, stop.navigationAction, systemCopy) },
                            )
                        }
                    }
                    item {
                        FadeItem(visibleCount >= 5) {
                            RouteExecutionCard(
                                record = record,
                                locale = locale,
                                onOpenMap = { stop -> MapNavigator.openInAmap(context, stop.navigationAction, systemCopy) },
                                onCheckIn = { stop ->
                                    val nextStatus = if (progressState.statusFor(stop.checkInTask.taskId) == TaskStatus.Completed) {
                                        TaskStatus.Pending
                                    } else {
                                        TaskStatus.Completed
                                    }
                                    onUpdateTaskStatus(stop.checkInTask.taskId, nextStatus)
                                },
                            )
                        }
                    }
                    item {
                        FadeItem(visibleCount >= 6) {
                            MissionSummaryCard(quest = quest, progress = progress, completed = resolvedSteps, total = totalSteps, copy = copy)
                        }
                    }
                    item {
                        FadeItem(visibleCount >= 7) {
                            ItineraryOverviewCard(record, copy)
                        }
                    }
                    item {
                        FadeItem(visibleCount >= 8) {
                            RoutePlaybookCard(record, locale)
                        }
                    }
                    itineraryPlan.stops.forEach { stop ->
                        item {
                            FadeItem(visibleCount >= 9) {
                                RouteStopCard(
                                    stop = stop,
                                    status = progressState.statusFor(stop.checkInTask.taskId),
                                    restoreSnapshot = progressState.lastRouteStopRestore?.takeIf { snapshot ->
                                        snapshot.stopId == stop.stopId || snapshot.order == stop.order
                                    },
                                    copy = copy,
                                    playbookCopy = routePlaybookStrings(locale),
                                    onOpenMap = { MapNavigator.openInAmap(context, stop.navigationAction, systemCopy) },
                                    onCheckIn = {
                                        val nextStatus = if (progressState.statusFor(stop.checkInTask.taskId) == TaskStatus.Completed) {
                                            TaskStatus.Pending
                                        } else {
                                            TaskStatus.Completed
                                        }
                                        onUpdateTaskStatus(stop.checkInTask.taskId, nextStatus)
                                    },
                                    onUseSwap = {
                                        val nextStatus = if (progressState.statusFor(stop.checkInTask.taskId) == TaskStatus.Skipped) {
                                            TaskStatus.Pending
                                        } else {
                                            TaskStatus.Skipped
                                        }
                                        onUpdateTaskStatus(stop.checkInTask.taskId, nextStatus)
                                    },
                                    onPreviewReplacement = {
                                        onPreviewRouteStopReplacement(stop.stopId)
                                    },
                                    onReplaceStop = {
                                        val replaced = onReplaceRouteStop(stop.stopId)
                                        val message = if (replaced) {
                                            routePlaybookStrings(locale).stopRerollDone
                                        } else {
                                            routePlaybookStrings(locale).stopRerollUnavailable
                                        }
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    },
                                    onRestoreStop = {
                                        val restored = onRestoreRouteStop()
                                        val message = if (restored) {
                                            routePlaybookStrings(locale).stopRestoreDone
                                        } else {
                                            routePlaybookStrings(locale).stopRestoreUnavailable
                                        }
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    },
                                )
                            }
                        }
                    }
                } else {
                    item {
                        FadeItem(visibleCount >= 1) {
                            MissionSummaryCard(quest = quest, progress = progress, completed = resolvedSteps, total = totalSteps, copy = copy)
                        }
                    }
                }
                quest.tasks.forEachIndexed { index, task ->
                    item {
                        FadeItem(visibleCount >= index + 6) {
                            PlayableTaskCard(
                                index = index + 1,
                                task = task,
                                status = progressState.statusFor(task.taskId),
                                feedbackReasons = progressState.feedbackFor(task.taskId),
                                copy = copy,
                                onStatusChange = { status -> onUpdateTaskStatus(task.taskId, status) },
                                onFeedback = { reason -> onToggleFeedback(task.taskId, reason) },
                            )
                        }
                    }
                }
                item {
                    FadeItem(visibleCount >= 5) {
                        HiddenTaskCard(
                            quest = quest,
                            status = progressState.statusFor(quest.hiddenTask.taskId),
                            copy = copy,
                            onStatusChange = { status -> onUpdateTaskStatus(quest.hiddenTask.taskId, status) },
                        )
                    }
                }
                item {
                    FadeItem(visibleCount >= 6) {
                        DialogueCard(quest, copy)
                    }
                }
                item {
                    FadeItem(visibleCount >= 7) {
                        BackupPlanCard(quest, copy)
                    }
                }
                item {
                    FadeItem(visibleCount >= 8) {
                        TicketCard {
                            Text(copy.photoMissionTitle, color = CherryPressed, style = MaterialTheme.typography.labelLarge)
                            Spacer(Modifier.height(8.dp))
                            Text(quest.photoMission, color = InkBlack, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(14.dp))
                            Text(copy.endingRitualTitle, color = CherryPressed, style = MaterialTheme.typography.labelLarge)
                            Spacer(Modifier.height(8.dp))
                            Text(quest.endingRitual, color = InkBlack, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 24.dp)) {
                        HeartPrimaryButton(copy.shareCta, onClick = onShare)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            GhostButton(copy.regenerate, onClick = onRegenerate, modifier = Modifier.weight(1f))
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
private fun FadeItem(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 }),
    ) {
        content()
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun TimeCinemaTicketCard(
    record: QuestRecord,
    locale: TodayPlayLocale,
    onOpenMap: (RouteStop) -> Unit,
) {
    val plan = record.quest.itineraryPlan ?: return
    val copy = timeCinemaStrings(locale)
    val currentStop = currentRunnableStop(record) ?: plan.stops.firstOrNull() ?: return
    val sourceLabel = if (plan.stops.any { it.poi.requiresOfficialVerification || it.poi.contentSource.isMock }) {
        copy.sampleSource
    } else {
        copy.verifiedSource
    }
    val transition = rememberInfiniteTransition(label = "time-cinema-ticket")
    val glow by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.34f,
        animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Reverse),
        label = "time-cinema-ticket-glow",
    )

    SoftCard(padding = 0.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(TicketBeige.copy(alpha = 0.84f))
                .border(1.dp, RoseGold.copy(alpha = 0.45f), RoundedCornerShape(22.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(172.dp)
                    .background(BlackCherry),
            ) {
                Image(
                    painter = painterResource(R.drawable.romantic_ticket),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = 0.64f
                            scaleX = 1.03f
                            scaleY = 1.03f
                        },
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    BlackCherry.copy(alpha = 0.18f),
                                    BlackCherry.copy(alpha = 0.72f),
                                ),
                            ),
                        ),
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Text(copy.kicker, color = RoseGold, style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        plan.title,
                        color = GalleryWhite,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        lineHeight = 31.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        TimeCinemaMetaChip(plan.city)
                        TimeCinemaMetaChip(plan.estimatedDuration)
                        TimeCinemaMetaChip(compactTicketMeta(plan.estimatedCost))
                        TimeCinemaMetaChip(sourceLabel)
                    }
                }
            }

            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(GalleryWhite.copy(alpha = 0.62f))
                        .border(1.dp, LineBeige, RoundedCornerShape(18.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(copy.boardTitle, color = CherryPressed, style = MaterialTheme.typography.labelLarge)
                        Text(
                            copy.boardLine,
                            color = InkBlack,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    KawaiiChip(text = copy.currentMapAction, selected = true, onClick = { onOpenMap(currentStop) })
                }

                TimeCinemaRouteMap(
                    plan = plan,
                    currentStop = currentStop,
                    record = record,
                    glow = glow,
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(GalleryWhite.copy(alpha = 0.62f))
                        .border(1.dp, LineBeige, RoundedCornerShape(18.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    plan.stops.take(3).forEachIndexed { index, stop ->
                        TimeCinemaSceneRow(
                            scene = copy.sceneLabels.getOrElse(index) { "Scene ${index + 1}" },
                            stop = stop,
                            active = stop.stopId == currentStop.stopId,
                        )
                    }
                }

                Text(
                    copy.sourceWarning,
                    color = WarmGray,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun TimeCinemaMetaChip(text: String) {
    Text(
        text = text,
        color = GalleryWhite,
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(GalleryWhite.copy(alpha = 0.16f))
            .border(1.dp, GalleryWhite.copy(alpha = 0.24f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
    )
}

@Composable
private fun TimeCinemaRouteMap(
    plan: ItineraryPlan,
    currentStop: RouteStop,
    record: QuestRecord,
    glow: Float,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(WarmCream.copy(alpha = 0.92f))
            .border(1.dp, RoseGold.copy(alpha = 0.42f), RoundedCornerShape(20.dp)),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val roadColor = LineBeige.copy(alpha = 0.52f)
            repeat(5) { index ->
                val y = size.height * (0.18f + index * 0.15f)
                drawLine(
                    color = roadColor,
                    start = Offset(size.width * 0.06f, y),
                    end = Offset(size.width * 0.94f, y + if (index % 2 == 0) 18f else -10f),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round,
                )
            }
            repeat(4) { index ->
                val x = size.width * (0.18f + index * 0.2f)
                drawLine(
                    color = roadColor.copy(alpha = 0.34f),
                    start = Offset(x, size.height * 0.08f),
                    end = Offset(x + if (index % 2 == 0) 36f else -24f, size.height * 0.92f),
                    strokeWidth = 2f,
                    cap = StrokeCap.Round,
                )
            }
            val points = listOf(
                Offset(size.width * 0.13f, size.height * 0.70f),
                Offset(size.width * 0.36f, size.height * 0.34f),
                Offset(size.width * 0.62f, size.height * 0.55f),
                Offset(size.width * 0.86f, size.height * 0.28f),
            ).take(plan.stops.size.coerceIn(1, 4))
            points.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = BlackCherry.copy(alpha = 0.14f),
                    start = start,
                    end = end,
                    strokeWidth = 14f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = CherryPressed.copy(alpha = 0.70f),
                    start = start,
                    end = end,
                    strokeWidth = 5f,
                    cap = StrokeCap.Round,
                )
            }
            points.forEachIndexed { index, point ->
                val stop = plan.stops.getOrNull(index)
                val done = stop != null && record.progress.statusFor(stop.checkInTask.taskId).isResolved()
                val active = stop?.stopId == currentStop.stopId
                if (active) {
                    drawCircle(
                        color = CherryPressed.copy(alpha = glow),
                        radius = 26f,
                        center = point,
                    )
                }
                drawCircle(
                    color = when {
                        active -> BlackCherry
                        done -> CherryPressed
                        else -> GalleryWhite
                    },
                    radius = if (active) 18f else 14f,
                    center = point,
                )
                drawCircle(
                    color = if (active || done) GalleryWhite else RoseGold,
                    radius = 5f,
                    center = point,
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
        ) {
            Text("MAP ROUTE", color = CherryPressed, style = MaterialTheme.typography.labelSmall)
            Text(
                plan.city,
                color = InkBlack,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            plan.bestPhotoTime,
            color = WarmGray,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun TimeCinemaSceneRow(
    scene: String,
    stop: RouteStop,
    active: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .width(74.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (active) CherryPressed else TicketBeige)
                .border(1.dp, if (active) CherryPressed else RoseGold.copy(alpha = 0.42f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = scene,
                color = if (active) GalleryWhite else CherryPressed,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stop.poi.name,
                color = InkBlack,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                stop.checkInTask.title,
                color = WarmGray,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            stop.startTimeHint,
            color = RoseGold,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private data class TimeCinemaStrings(
    val kicker: String,
    val boardTitle: String,
    val boardLine: String,
    val currentMapAction: String,
    val sampleSource: String,
    val verifiedSource: String,
    val sourceWarning: String,
    val sceneLabels: List<String>,
)

private fun timeCinemaStrings(locale: TodayPlayLocale): TimeCinemaStrings {
    return when (locale) {
        TodayPlayLocale.SimplifiedChinese,
        TodayPlayLocale.TraditionalChinese -> TimeCinemaStrings(
            kicker = "TODAY WAS PLAYED / 今日电影票",
            boardTitle = "场记板",
            boardLine = "先看地图线路，再按三幕完成今天；来源状态会单独标注。",
            currentMapAction = "导航当前镜头",
            sampleSource = "电影感灵感",
            verifiedSource = "已核验来源",
            sourceWarning = "当前内容为项目本地样例或待核验电影感地点；没有授权时不使用剧照、海报、片名 Logo，也不声称官方取景地。",
            sceneLabels = listOf("Act 01", "Act 02", "Act 03"),
        )
        else -> TimeCinemaStrings(
            kicker = "TODAY WAS PLAYED / private scene",
            boardTitle = "Clapper board",
            boardLine = "Read the route map first, then play today in three scenes. This is a cinematic route, not an official filming claim.",
            currentMapAction = "Navigate scene",
            sampleSource = "Cinematic vibe",
            verifiedSource = "Verified source",
            sourceWarning = "Current content is a local sample or pending-verification cinematic place. No stills, posters, title logos, or official filming claims are used without authorization.",
            sceneLabels = listOf("Act 01", "Act 02", "Act 03"),
        )
    }
}

private fun compactTicketMeta(value: String): String {
    return value
        .substringBefore("，")
        .substringBefore(",")
        .trim()
        .takeIf { it.isNotBlank() }
        ?: value.trim().take(16)
}

private fun QuestRecord.isTimeCinemaRoute(): Boolean {
    val plan = quest.itineraryPlan
    return quest.tags.any { tag ->
        tag.contains("电影感") || tag.contains("cinema", ignoreCase = true)
    } || plan?.title?.contains("时光电影") == true ||
        plan?.stops.orEmpty().any { stop ->
            stop.poi.tags.any { tag -> tag.contains("电影感") || tag.contains("cinema", ignoreCase = true) } ||
                stop.poi.globalCategory.contains("cinema", ignoreCase = true)
        }
}

@Composable
private fun PersonalFitCard(record: QuestRecord, locale: TodayPlayLocale) {
    val plan = record.quest.itineraryPlan ?: return
    val title = when (locale) {
        TodayPlayLocale.SimplifiedChinese,
        TodayPlayLocale.TraditionalChinese -> "为什么适合你"
        else -> "Why it fits"
    }
    val english = when (locale) {
        TodayPlayLocale.SimplifiedChinese,
        TodayPlayLocale.TraditionalChinese -> "personal fit"
        else -> "personal fit"
    }
    val reasons = buildList {
        add(plan.routeSummary)
        plan.stops.take(2).forEach { stop -> add(stop.whyForGroup) }
        add(record.quest.completionSummary)
    }.map { it.trim() }.filter { it.isNotBlank() }.distinct().take(3)

    SoftCard {
        SectionHeader("FIT", title, english)
        Spacer(Modifier.height(10.dp))
        reasons.forEach { reason ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text("•", color = CherryPressed, style = MaterialTheme.typography.titleMedium)
                Text(
                    reason,
                    color = WarmGray,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(6.dp))
        }
        Text(
            cleanAiCoverageNote(locale, record.quest.tags.any { it.contains("AI") }),
            color = CherryPressed,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun aiCoverageNote(locale: TodayPlayLocale, aiAssisted: Boolean): String {
    return when (locale) {
        TodayPlayLocale.SimplifiedChinese -> if (aiAssisted) {
            "AI 已按你的输入改写路线文案；POI 数据仍为本地样例。"
        } else {
            "当前为本地兜底路线；POI 数据仍为本地样例。"
        }
        TodayPlayLocale.TraditionalChinese -> if (aiAssisted) {
            "AI 已依你的輸入改寫路線文案；POI 資料仍為本地樣例。"
        } else {
            "目前為本地備援路線；POI 資料仍為本地樣例。"
        }
        else -> if (aiAssisted) {
            "AI route text is based on your input; POI data remains a local sample."
        } else {
            "Local fallback route; POI data remains a local sample."
        }
    }
}

private fun cleanAiCoverageNote(locale: TodayPlayLocale, aiAssisted: Boolean): String {
    return when (locale) {
        TodayPlayLocale.SimplifiedChinese -> if (aiAssisted) {
            "AI 已从同城候选池按你的输入选点和排序；地点仍为本地样例，出发前请确认营业与交通。"
        } else {
            "已使用本地个性化路线引擎生成；地点仍为本地样例，出发前请确认营业与交通。"
        }
        TodayPlayLocale.TraditionalChinese -> if (aiAssisted) {
            "AI 已從同城候選池按你的輸入選點和排序；地點仍為本地樣例，出發前請確認營業與交通。"
        } else {
            "已使用本地個性化路線引擎生成；地點仍為本地樣例，出發前請確認營業與交通。"
        }
        else -> if (aiAssisted) {
            "AI selected and ordered stops from same-city candidates. POI data remains a local sample."
        } else {
            "Generated with the local personalized route engine. POI data remains a local sample."
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RouteTuneCard(onTuneRoute: (String) -> Unit) {
    SoftCard(padding = 14.dp) {
        Text("继续调味", color = InkBlack, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("更安静", "少走路", "更便宜", "改室内", "更热闹").forEach { label ->
                KawaiiChip(text = label, selected = false, onClick = { onTuneRoute(label) })
            }
        }
    }
}

@Composable
private fun ResultHero(quest: Quest, copy: ResultStrings) {
    val transition = rememberInfiniteTransition(label = "result-hero-breath")
    val zoom by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(tween(4200), RepeatMode.Reverse),
        label = "result-hero-zoom",
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(178.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(WarmCream),
    ) {
        Image(
            painter = painterResource(R.drawable.romantic_ticket),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = zoom
                    scaleY = zoom
                },
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            GalleryWhite.copy(alpha = 0.05f),
                            GalleryWhite.copy(alpha = 0.86f),
                        ),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(copy.routeTitleLabel, color = CherryPressed, style = MaterialTheme.typography.labelSmall)
            Text(
                quest.title,
                color = InkBlack,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                copy.heroBody,
                color = WarmGray,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun MissionSummaryCard(
    quest: Quest,
    progress: Float,
    completed: Int,
    total: Int,
    copy: ResultStrings,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(620),
        label = "mission-progress",
    )
    SoftCard {
        SectionHeader("00", copy.summaryTitle, copy.summaryEnglish)
        Spacer(Modifier.height(12.dp))
        Text(
            quest.storySetup,
            style = MaterialTheme.typography.bodyLarge,
            color = InkBlack,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(14.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            color = CherryPressed,
            trackColor = LineBeige,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(999.dp)),
        )
        Spacer(Modifier.height(8.dp))
        Text(copy.progress(completed, total), color = CherryPressed, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryMetricChip(copy.relationshipLabel, quest.relationship)
            SummaryMetricChip(copy.durationLabel, quest.duration)
            SummaryMetricChip(copy.budgetLabel, quest.budget)
            SummaryMetricChip(copy.awkwardLabel, "${awkwardIndex(quest)}/5")
            SummaryMetricChip(copy.energyLabel, "${energyIndex(quest)}/5")
        }
    }
}

@Composable
private fun SummaryMetricChip(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(GalleryWhite.copy(alpha = 0.72f))
            .border(1.dp, LineBeige, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(label, color = WarmGray, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(value, color = InkBlack, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun RouteVisualPreviewCard(
    record: QuestRecord,
    locale: TodayPlayLocale,
    onOpenMap: (RouteStop) -> Unit,
) {
    val plan = record.quest.itineraryPlan ?: return
    val copy = routePlaybookStrings(locale)
    val currentStop = currentRunnableStop(record) ?: plan.stops.firstOrNull() ?: return
    SoftCard {
        SectionHeader("MAP", copy.visualMapTitle, copy.visualMapEnglish)
        Spacer(Modifier.height(12.dp))
        RouteSketchMap(plan = plan, currentStop = currentStop, record = record)
        Spacer(Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            plan.stops.take(4).forEach { stop ->
                RouteStopPill(
                    order = stop.order,
                    name = stop.poi.name,
                    active = stop.stopId == currentStop.stopId,
                    done = record.progress.statusFor(stop.checkInTask.taskId).isResolved(),
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                copy.visualMapHint,
                color = WarmGray,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(min = 190.dp, max = 560.dp),
            )
            KawaiiChip(text = copy.visualOpenCurrentMap, selected = true, onClick = { onOpenMap(currentStop) })
        }
    }
}

@Composable
private fun RouteSketchMap(
    plan: ItineraryPlan,
    currentStop: RouteStop,
    record: QuestRecord,
) {
    val transition = rememberInfiniteTransition(label = "route-sketch-map")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Restart),
        label = "route-current-pulse",
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(TicketBeige.copy(alpha = 0.55f))
            .border(1.dp, RoseGold.copy(alpha = 0.35f), RoundedCornerShape(22.dp)),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val points = listOf(
                Offset(size.width * 0.12f, size.height * 0.72f),
                Offset(size.width * 0.34f, size.height * 0.38f),
                Offset(size.width * 0.58f, size.height * 0.58f),
                Offset(size.width * 0.84f, size.height * 0.25f),
            ).take(plan.stops.size.coerceIn(1, 4))
            points.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = LineBeige,
                    start = start,
                    end = end,
                    strokeWidth = 10f,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = RoseGold.copy(alpha = 0.48f),
                    start = start,
                    end = end,
                    strokeWidth = 4f,
                    cap = StrokeCap.Round,
                )
            }
            points.forEachIndexed { index, point ->
                val stop = plan.stops.getOrNull(index)
                val done = stop != null && record.progress.statusFor(stop.checkInTask.taskId).isResolved()
                val active = stop?.stopId == currentStop.stopId
                if (active) {
                    drawCircle(
                        color = CherryPressed.copy(alpha = 0.16f * (1f - pulse)),
                        radius = 18f + 12f * pulse,
                        center = point,
                    )
                }
                drawCircle(
                    color = when {
                        active -> CherryPressed
                        done -> RoseGold
                        else -> GalleryWhite
                    },
                    radius = if (active) 16f else 12f,
                    center = point,
                )
                drawCircle(
                    color = if (active || done) GalleryWhite.copy(alpha = 0.86f) else LineBeige,
                    radius = 5f,
                    center = point,
                )
            }
        }
        Text(
            plan.city,
            color = InkBlack,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(18.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            plan.bestPhotoTime,
            color = CherryPressed,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(18.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RouteStopPill(order: Int, name: String, active: Boolean, done: Boolean) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (active) CherryPressed.copy(alpha = 0.14f) else GalleryWhite.copy(alpha = 0.78f))
            .border(1.dp, if (active || done) CherryPressed.copy(alpha = 0.45f) else LineBeige, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (active || done) CherryPressed else RoseGold),
            contentAlignment = Alignment.Center,
        ) {
            Text(order.toString(), color = GalleryWhite, style = MaterialTheme.typography.labelSmall)
        }
        Text(name, color = InkBlack, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun ItineraryOverviewCard(record: QuestRecord, copy: ResultStrings) {
    val plan = record.quest.itineraryPlan ?: return
    val checkedIn = plan.stops.count { stop -> record.progress.statusFor(stop.checkInTask.taskId) == TaskStatus.Completed }
    val points = record.progress.rewardPoints.sumOf { it.points }
    SoftCard {
        SectionHeader("01", copy.overviewTitle, copy.overviewEnglish)
        Spacer(Modifier.height(12.dp))
        Text(
            plan.title,
            style = MaterialTheme.typography.headlineSmall,
            color = InkBlack,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        Text(plan.routeSummary, style = MaterialTheme.typography.bodyMedium, color = WarmGray)
        Spacer(Modifier.height(12.dp))
        InfoLine(copy.routeTypeLabel, plan.planType)
        InfoLine(copy.cityLabel, plan.city)
        InfoLine(copy.candidateStopsLabel, copy.candidateCount(plan.candidateRouteCount))
        InfoLine(copy.estimatedCostLabel, plan.estimatedCost)
        InfoLine(copy.bestPhotoLabel, plan.bestPhotoTime)
        InfoLine(copy.crowdRiskLabel, plan.crowdRisk)
        InfoLine(copy.checkInProgressLabel, "$checkedIn/${plan.stops.size}")
        InfoLine(copy.currentPointsLabel, "$points")
        Spacer(Modifier.height(14.dp))
        DataAvailabilityNotice(plan, copy)
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun RouteExecutionCard(
    record: QuestRecord,
    locale: TodayPlayLocale,
    onOpenMap: (RouteStop) -> Unit,
    onCheckIn: (RouteStop) -> Unit,
) {
    val plan = record.quest.itineraryPlan ?: return
    val copy = routePlaybookStrings(locale)
    val currentStop = currentRunnableStop(record) ?: return
    val status = record.progress.statusFor(currentStop.checkInTask.taskId)
    val checkedIn = status == TaskStatus.Completed
    val resolvedStopCount = plan.stops.count { stop ->
        record.progress.statusFor(stop.checkInTask.taskId).isResolved()
    }
    val nextStop = plan.stops
        .dropWhile { stop -> stop.stopId != currentStop.stopId }
        .drop(1)
        .firstOrNull { stop -> !record.progress.statusFor(stop.checkInTask.taskId).isResolved() }
    val allDone = resolvedStopCount == plan.stops.size

    SoftCard {
        SectionHeader("LIVE", copy.executionTitle, copy.executionEnglish)
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(if (checkedIn) RoseGold.copy(alpha = 0.18f) else TicketBeige.copy(alpha = 0.55f))
                .border(
                    1.dp,
                    if (checkedIn) CherryPressed.copy(alpha = 0.42f) else RoseGold.copy(alpha = 0.42f),
                    RoundedCornerShape(18.dp),
                )
                .padding(14.dp),
        ) {
            Text(
                if (allDone) copy.executionAllDoneLine else copy.executionReadyLine,
                color = InkBlack,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "${currentStop.order}. ${currentStop.poi.name}",
                color = InkBlack,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                currentStop.poi.district + " / " + currentStop.startTimeHint,
                color = CherryPressed,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(12.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                KawaiiChip(text = copy.executionOpenMap, selected = false, onClick = { onOpenMap(currentStop) })
                KawaiiChip(
                    text = if (checkedIn) copy.executionUndoCheckIn else copy.executionCompleteStop(currentStop.checkInTask.rewardPoints),
                    selected = true,
                    onClick = { onCheckIn(currentStop) },
                )
            }
            Spacer(Modifier.height(12.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExecutionMetricTile(copy.executionNextActionLabel, currentStop.checkInTask.title)
                ExecutionMetricTile(copy.executionRewardLabel, "+${currentStop.checkInTask.rewardPoints}")
                ExecutionMetricTile(copy.executionProgressLabel, "$resolvedStopCount/${plan.stops.size}")
                if (nextStop != null) {
                    ExecutionMetricTile(copy.executionNextStopLabel, "${nextStop.order}. ${nextStop.poi.name}")
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(currentStop.checkInTask.description, color = WarmGray, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(12.dp))
        RouteProgressTimeline(
            stops = plan.stops,
            record = record,
            currentStop = currentStop,
            copy = copy,
        )
    }
}

@Composable
private fun ExecutionMetricTile(label: String, value: String) {
    Column(
        modifier = Modifier
            .widthIn(min = 124.dp, max = 240.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(GalleryWhite.copy(alpha = 0.76f))
            .border(1.dp, LineBeige, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(label, color = WarmGray, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(4.dp))
        Text(value, color = InkBlack, style = MaterialTheme.typography.labelLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun RouteProgressTimeline(
    stops: List<RouteStop>,
    record: QuestRecord,
    currentStop: RouteStop,
    copy: RoutePlaybookStrings,
) {
    if (stops.isEmpty()) return
    val currentIndex = stops.indexOfFirst { it.stopId == currentStop.stopId }.coerceAtLeast(0)
    val windowStart = when {
        stops.size <= 3 -> 0
        currentIndex >= stops.size - 2 -> stops.size - 3
        else -> currentIndex
    }.coerceAtLeast(0)
    val visibleStops = stops.drop(windowStart).take(3)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GalleryWhite.copy(alpha = 0.62f))
            .border(1.dp, LineBeige, RoundedCornerShape(16.dp))
            .padding(12.dp),
    ) {
        Text(copy.executionTimelineLabel, color = CherryPressed, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            visibleStops.forEachIndexed { index, stop ->
                val status = record.progress.statusFor(stop.checkInTask.taskId)
                val isCurrent = stop.stopId == currentStop.stopId
                val resolved = status.isResolved()
                TimelineDot(
                    resolved = resolved,
                    current = isCurrent,
                )
                if (index < visibleStops.lastIndex) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(if (resolved) CherryPressed.copy(alpha = 0.62f) else LineBeige),
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            visibleStops.forEach { stop ->
                val status = record.progress.statusFor(stop.checkInTask.taskId)
                val isCurrent = stop.stopId == currentStop.stopId
                val stateLabel = when {
                    status.isResolved() -> copy.executionDoneLabel
                    isCurrent -> copy.executionNowLabel
                    else -> copy.executionTodoLabel
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${stop.order.toString().padStart(2, '0')} / $stateLabel",
                        color = if (isCurrent) CherryPressed else WarmGray,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        stop.poi.name,
                        color = InkBlack,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineDot(resolved: Boolean, current: Boolean) {
    val transition = rememberInfiniteTransition(label = "timeline-dot")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Restart),
        label = "timeline-dot-pulse",
    )
    Box(contentAlignment = Alignment.Center) {
        if (current) {
            Box(
                modifier = Modifier
                    .size((28 + 8 * pulse).dp)
                    .clip(CircleShape)
                    .background(CherryPressed.copy(alpha = 0.10f * (1f - pulse))),
            )
        }
        Box(
            modifier = Modifier
                .size(if (current) 22.dp else 18.dp)
                .clip(CircleShape)
                .background(
                    when {
                        resolved -> CherryPressed
                        current -> RoseGold
                        else -> GalleryWhite
                    },
                )
                .border(
                    2.dp,
                    if (current || resolved) CherryPressed.copy(alpha = 0.76f) else LineBeige,
                    CircleShape,
                ),
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun RoutePlaybookCard(record: QuestRecord, locale: TodayPlayLocale) {
    val plan = record.quest.itineraryPlan ?: return
    val copy = routePlaybookStrings(locale)
    val firstStop = plan.stops.firstOrNull()?.poi?.name ?: plan.city
    SoftCard {
        SectionHeader("03", copy.sectionTitle, copy.sectionEnglish)
        Spacer(Modifier.height(12.dp))
        Text(copy.openingLine, style = MaterialTheme.typography.bodyLarge, color = InkBlack)
        Spacer(Modifier.height(12.dp))
        InfoLine(copy.startAtLabel, firstStop)
        InfoLine(copy.photoWindowLabel, plan.bestPhotoTime)
        InfoLine(copy.rewardLabel, "${plan.totalRewardPoints}")
        InfoLine(copy.safeSwapLabel, plan.rainBackup)
        Spacer(Modifier.height(12.dp))
        Text(copy.tagsLabel, color = CherryPressed, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            record.quest.tags.take(6).forEach { tag ->
                KawaiiChip(text = tag, selected = true, onClick = {})
            }
            plan.stops.take(3).forEach { stop ->
                KawaiiChip(text = stop.poi.district, selected = false, onClick = {})
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun DataAvailabilityNotice(plan: ItineraryPlan, copy: ResultStrings) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(TicketBeige.copy(alpha = 0.48f))
            .border(1.dp, RoseGold.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        Text(copy.dataAvailabilityTitle, color = CherryPressed, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            KawaiiChip(text = copy.availableRouteGeneration, selected = true, onClick = {})
            KawaiiChip(text = copy.availableManualCheckIn, selected = true, onClick = {})
            KawaiiChip(text = copy.availableExternalMaps, selected = true, onClick = {})
            KawaiiChip(text = copy.localHistoryPoints, selected = true, onClick = {})
            KawaiiChip(text = copy.needsLiveGlobalSearch, selected = false, enabled = false, onClick = {})
            KawaiiChip(text = copy.needsOfficialHours, selected = false, enabled = false, onClick = {})
            KawaiiChip(text = copy.needsPhotoUpload, selected = false, enabled = false, onClick = {})
            if (!BuildConfig.TRAVEL_CONTENT_BASE_URL.trim().startsWith("https://")) {
                KawaiiChip(text = copy.globalContentNotConfigured, selected = false, enabled = false, onClick = {})
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(plan.complianceNote.summary, color = InkBlack, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(4.dp))
        Text(plan.marketCoverageNote, color = WarmGray, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun RouteStopCard(
    stop: RouteStop,
    status: TaskStatus,
    restoreSnapshot: RouteStopRestoreSnapshot?,
    copy: ResultStrings,
    playbookCopy: RoutePlaybookStrings,
    onOpenMap: () -> Unit,
    onCheckIn: () -> Unit,
    onUseSwap: () -> Unit,
    onPreviewReplacement: () -> RouteStopReplacementPreview?,
    onReplaceStop: () -> Unit,
    onRestoreStop: () -> Unit,
) {
    val checkedIn = status == TaskStatus.Completed
    val swapped = status == TaskStatus.Skipped
    val context = LocalContext.current
    var replacementPreview by remember(stop.stopId, stop.poi.poiId) { mutableStateOf<RouteStopReplacementPreview?>(null) }
    TicketCard {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                copy.stopLabel(stop.order),
                color = CherryPressed,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(58.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stop.poi.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = InkBlack,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(copy.stopMeta(stop.startTimeHint, stop.stayMinutes, stop.poi.country, stop.poi.city, stop.poi.district), color = WarmGray, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Text(
                    copy.whyRecommendedPrefix + stop.poi.recommendationReason,
                    color = InkBlack,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    copy.whyForGroupPrefix + stop.whyForGroup,
                    color = InkBlack,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                Text(playbookCopy.stopMissionLabel + ": " + stop.checkInTask.title, color = InkBlack, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    stop.checkInTask.description,
                    color = WarmGray,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Text(playbookCopy.stopRewardLabel + ": +" + stop.checkInTask.rewardPoints, color = CherryPressed, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(6.dp))
                Text(copy.photoSuggestionPrefix + stop.photoSuggestion, color = WarmGray, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(6.dp))
                Text(copy.spendingSuggestionPrefix + stop.spendingSuggestion, color = WarmGray, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(6.dp))
                Text(copy.riskTipsPrefix + stop.poi.riskTips.joinToString(" / "), color = CherryPressed, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(10.dp))
                StopSwapPanel(
                    backupPlan = stop.backupPlan,
                    swapped = swapped,
                    playbookCopy = playbookCopy,
                )
                replacementPreview?.let { preview ->
                    Spacer(Modifier.height(8.dp))
                    ReplacementPreviewPanel(
                        preview = preview,
                        playbookCopy = playbookCopy,
                        onConfirm = {
                            onReplaceStop()
                            replacementPreview = null
                        },
                        onCancel = {
                            replacementPreview = null
                        },
                    )
                }
                restoreSnapshot?.let { snapshot ->
                    Spacer(Modifier.height(8.dp))
                    RestoreStopPanel(
                        snapshot = snapshot,
                        playbookCopy = playbookCopy,
                        onRestore = onRestoreStop,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    copy.sourceLine(stop.poi.contentSource.sourceLabel, stop.poi.requiresOfficialVerification),
                    color = WarmGray,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            KawaiiChip(text = copy.openMap, selected = false, onClick = onOpenMap)
            KawaiiChip(
                text = if (checkedIn) copy.checkedIn(stop.checkInTask.rewardPoints) else copy.manualCheckIn(stop.checkInTask.rewardPoints),
                selected = checkedIn,
                onClick = onCheckIn,
            )
            KawaiiChip(
                text = if (swapped) playbookCopy.stopSwapSelected else playbookCopy.stopSwapAction,
                selected = swapped,
                onClick = onUseSwap,
            )
            KawaiiChip(
                text = playbookCopy.stopRerollAction,
                selected = replacementPreview != null,
                onClick = {
                    val preview = onPreviewReplacement()
                    if (preview == null) {
                        Toast.makeText(context, playbookCopy.stopRerollUnavailable, Toast.LENGTH_SHORT).show()
                    } else {
                        replacementPreview = preview
                    }
                },
            )
            KawaiiChip(text = copy.photoUploadDisabled, selected = false, enabled = false, onClick = {})
        }
    }
}

@Composable
private fun StopSwapPanel(
    backupPlan: String,
    swapped: Boolean,
    playbookCopy: RoutePlaybookStrings,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (swapped) RoseGold.copy(alpha = 0.22f) else TicketBeige.copy(alpha = 0.5f))
            .border(1.dp, RoseGold.copy(alpha = if (swapped) 0.72f else 0.35f), RoundedCornerShape(14.dp))
            .padding(12.dp),
    ) {
        Text(playbookCopy.stopSwapTitle, color = CherryPressed, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(6.dp))
        Text(backupPlan, color = InkBlack, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(6.dp))
        Text(
            if (swapped) playbookCopy.stopSwapSelectedHint else playbookCopy.stopSwapHint,
            color = WarmGray,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(6.dp))
        Text(playbookCopy.stopRerollHint, color = WarmGray, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun ReplacementPreviewPanel(
    preview: RouteStopReplacementPreview,
    playbookCopy: RoutePlaybookStrings,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(GalleryWhite.copy(alpha = 0.86f))
            .border(1.dp, CherryPressed.copy(alpha = 0.28f), RoundedCornerShape(14.dp))
            .padding(12.dp),
    ) {
        Text(playbookCopy.stopRerollPreviewTitle, color = CherryPressed, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(6.dp))
        Text(
            "${preview.originalName} -> ${preview.candidateName}",
            color = InkBlack,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "${preview.candidateDistrict} · ${preview.stayMinutes} min · ${preview.candidateCategory.replace('_', ' ')}",
            color = WarmGray,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (preview.sameCategory) {
                KawaiiChip(text = playbookCopy.stopRerollSameCategory, selected = true, onClick = {})
            }
            if (preview.matchedTags.isNotEmpty()) {
                KawaiiChip(
                    text = playbookCopy.stopRerollMatchedTagsLabel + ": " + preview.matchedTags.joinToString(" / "),
                    selected = true,
                    onClick = {},
                )
            }
            KawaiiChip(
                text = playbookCopy.stopRerollStayDeltaLabel + ": " + preview.stayDeltaMinutes + " min",
                selected = false,
                onClick = {},
            )
        }
        if (preview.candidateTags.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(preview.candidateTags.joinToString(" / "), color = WarmGray, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(8.dp))
        Text(playbookCopy.stopRerollPreviewHint, color = WarmGray, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(6.dp))
        Text(
            playbookCopy.stopRerollSourceLabel + ": " + preview.sourceLabel,
            color = WarmGray,
            style = MaterialTheme.typography.labelSmall,
        )
        Spacer(Modifier.height(10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            KawaiiChip(text = playbookCopy.stopRerollConfirmAction, selected = true, onClick = onConfirm)
            KawaiiChip(text = playbookCopy.stopRerollCancelAction, selected = false, onClick = onCancel)
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun RestoreStopPanel(
    snapshot: RouteStopRestoreSnapshot,
    playbookCopy: RoutePlaybookStrings,
    onRestore: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(RoseGold.copy(alpha = 0.16f))
            .border(1.dp, RoseGold.copy(alpha = 0.42f), RoundedCornerShape(14.dp))
            .padding(12.dp),
    ) {
        Text(playbookCopy.stopRestoreTitle, color = CherryPressed, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(6.dp))
        Text(
            playbookCopy.stopRestoreRoute(snapshot.previousStop.poi.name, snapshot.replacedStopName),
            color = InkBlack,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(6.dp))
        Text(playbookCopy.stopRestoreHint, color = WarmGray, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            KawaiiChip(text = playbookCopy.stopRestoreAction, selected = true, onClick = onRestore)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PlayableTaskCard(
    index: Int,
    task: QuestTask,
    status: TaskStatus,
    feedbackReasons: Set<FeedbackReason>,
    copy: ResultStrings,
    onStatusChange: (TaskStatus) -> Unit,
    onFeedback: (FeedbackReason) -> Unit,
) {
    val completed = status == TaskStatus.Completed
    val skipped = status == TaskStatus.Skipped
    SoftCard(padding = 16.dp) {
        Row(verticalAlignment = Alignment.Top) {
            Text(
                text = "Act ${index.toString().padStart(2, '0')}",
                color = BlackCherry,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(62.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium, color = InkBlack)
                Spacer(Modifier.height(4.dp))
                Text(task.description, style = MaterialTheme.typography.bodyMedium, color = WarmGray)
                Spacer(Modifier.height(8.dp))
                Text(copy.howToPrefix + task.howToComplete, style = MaterialTheme.typography.bodyMedium, color = InkBlack)
                Spacer(Modifier.height(6.dp))
                Text(copy.tipsPrefix + task.cuteTip, style = MaterialTheme.typography.bodyMedium, color = CherryPressed)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("心".repeat(task.difficultyHearts.coerceIn(1, 3)), color = CherryPressed)
                Text(task.estimatedTime, style = MaterialTheme.typography.labelSmall, color = WarmGray)
            }
        }
        Spacer(Modifier.height(14.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            KawaiiChip(
                text = if (completed) copy.completed else copy.checkIn,
                selected = completed,
                onClick = {
                    onStatusChange(if (completed) TaskStatus.Pending else TaskStatus.Completed)
                },
            )
            KawaiiChip(
                text = if (skipped) copy.skipped else copy.skip,
                selected = skipped,
                onClick = {
                    onStatusChange(if (skipped) TaskStatus.Pending else TaskStatus.Skipped)
                },
            )
            FeedbackReason.entries.forEach { reason ->
                KawaiiChip(
                    text = copy.feedbackLabel(reason),
                    selected = reason in feedbackReasons,
                    onClick = { onFeedback(reason) },
                )
            }
        }
        if (feedbackReasons.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            val labels = feedbackReasons.joinToString(" / ") { copy.feedbackLabel(it) }
            Text(
                copy.feedbackRecorded(labels),
                color = WarmGray,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun HiddenTaskCard(
    quest: Quest,
    status: TaskStatus,
    copy: ResultStrings,
    onStatusChange: (TaskStatus) -> Unit,
) {
    val completed = status == TaskStatus.Completed
    val skipped = status == TaskStatus.Skipped
    SoftCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.width(78.dp)) {
                Text("Secret", color = CherryPressed, fontFamily = FontFamily.Serif, style = MaterialTheme.typography.titleMedium)
                Text("Scene", color = CherryPressed, fontFamily = FontFamily.Serif, style = MaterialTheme.typography.titleMedium)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(quest.hiddenTask.title, style = MaterialTheme.typography.titleMedium, color = InkBlack)
                Spacer(Modifier.height(4.dp))
                Text(quest.hiddenTask.description, style = MaterialTheme.typography.bodyMedium, color = WarmGray)
                Spacer(Modifier.height(8.dp))
                Text(copy.hiddenHowToPrefix + quest.hiddenTask.howToComplete, style = MaterialTheme.typography.bodyMedium, color = InkBlack)
            }
            Text("心", color = CherryPressed, fontSize = 20.sp)
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(LineBeige),
        )
        Spacer(Modifier.height(10.dp))
        Text(copy.tipsPrefix + quest.hiddenTask.cuteTip, color = CherryPressed, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            KawaiiChip(
                text = if (completed) copy.hiddenCompleted else copy.completeHidden,
                selected = completed,
                onClick = { onStatusChange(if (completed) TaskStatus.Pending else TaskStatus.Completed) },
            )
            KawaiiChip(
                text = if (skipped) copy.skipped else copy.skip,
                selected = skipped,
                onClick = { onStatusChange(if (skipped) TaskStatus.Pending else TaskStatus.Skipped) },
            )
        }
    }
}

@Composable
private fun DialogueCard(quest: Quest, copy: ResultStrings) {
    SoftCard {
        SectionHeader("04", copy.dialogueTitle, copy.dialogueEnglish)
        Spacer(Modifier.height(12.dp))
        quest.conversationPrompts.forEachIndexed { index, prompt ->
            Text("${index + 1}. $prompt", style = MaterialTheme.typography.bodyMedium, color = InkBlack)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BackupPlanCard(quest: Quest, copy: ResultStrings) {
    TicketCard {
        SectionHeader("05", copy.backupTitle, copy.backupEnglish)
        Spacer(Modifier.height(12.dp))
        Text(copy.tooTiredPlan, color = InkBlack, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Text(copy.tooExpensivePlan, color = InkBlack, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Text(copy.noPhotoPlan, color = InkBlack, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Text(copy.unsafePlacePlan, color = CherryPressed, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(10.dp))
        Text(copy.keywordsPrefix + quest.tags.joinToString(" / "), color = WarmGray, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Start)
    }
}

private data class RoutePlaybookStrings(
    val sectionTitle: String,
    val sectionEnglish: String,
    val openingLine: String,
    val startAtLabel: String,
    val photoWindowLabel: String,
    val rewardLabel: String,
    val safeSwapLabel: String,
    val tagsLabel: String,
    val visualMapTitle: String,
    val visualMapEnglish: String,
    val visualMapHint: String,
    val visualOpenCurrentMap: String,
    val executionTitle: String,
    val executionEnglish: String,
    val executionReadyLine: String,
    val executionAllDoneLine: String,
    val executionCurrentStopLabel: String,
    val executionNextActionLabel: String,
    val executionRewardLabel: String,
    val executionProgressLabel: String,
    val executionNextStopLabel: String,
    val executionTimelineLabel: String,
    val executionNowLabel: String,
    val executionDoneLabel: String,
    val executionTodoLabel: String,
    val executionOpenMap: String,
    val executionUndoCheckIn: String,
    val executionCompleteStop: (points: Int) -> String,
    val stopMissionLabel: String,
    val stopRewardLabel: String,
    val stopSwapTitle: String,
    val stopSwapAction: String,
    val stopSwapSelected: String,
    val stopSwapHint: String,
    val stopSwapSelectedHint: String,
    val stopRerollAction: String,
    val stopRerollHint: String,
    val stopRerollPreviewTitle: String,
    val stopRerollConfirmAction: String,
    val stopRerollCancelAction: String,
    val stopRerollSameCategory: String,
    val stopRerollMatchedTagsLabel: String,
    val stopRerollStayDeltaLabel: String,
    val stopRerollSourceLabel: String,
    val stopRerollPreviewHint: String,
    val stopRerollDone: String,
    val stopRerollUnavailable: String,
    val stopRestoreTitle: String,
    val stopRestoreAction: String,
    val stopRestoreHint: String,
    val stopRestoreDone: String,
    val stopRestoreUnavailable: String,
    val stopRestoreRoute: (previous: String, replacement: String) -> String,
)

private fun routePlaybookStrings(locale: TodayPlayLocale): RoutePlaybookStrings {
    return when (locale) {
        TodayPlayLocale.SimplifiedChinese,
        TodayPlayLocale.TraditionalChinese -> RoutePlaybookStrings(
            sectionTitle = "\u5f00\u5c40\u6307\u5357",
            sectionEnglish = "playbook",
            openingLine = "\u5148\u4ece\u7b2c\u4e00\u7ad9\u5f00\u59cb\uff0c\u5b8c\u6210\u6bcf\u7ad9\u6253\u5361\uff1b\u5982\u679c\u6c14\u6c1b\u3001\u5929\u6c14\u6216\u4eba\u6d41\u4e0d\u5bf9\uff0c\u76f4\u63a5\u7528\u66ff\u6362\u9884\u6848\u3002",
            startAtLabel = "\u5f00\u5c40\u5730\u70b9",
            photoWindowLabel = "\u6700\u4f73\u62cd\u7167",
            rewardLabel = "\u53ef\u62ff\u79ef\u5206",
            safeSwapLabel = "\u66ff\u6362\u9884\u6848",
            tagsLabel = "\u672c\u5c40\u6807\u7b7e",
            visualMapTitle = "\u8def\u7ebf\u9884\u89c8",
            visualMapEnglish = "visual map",
            visualMapHint = "\u5148\u770b\u987a\u5e8f\u548c\u5f53\u524d\u7ad9\uff0c\u518d\u6253\u5f00\u5916\u90e8\u5730\u56fe\u5bfc\u822a\u3002",
            visualOpenCurrentMap = "\u5bfc\u822a\u5f53\u524d\u7ad9",
            executionTitle = "\u672c\u5c40\u6267\u884c",
            executionEnglish = "live mode",
            executionReadyLine = "\u5148\u5b8c\u6210\u5f53\u524d\u7ad9\uff0c\u518d\u5f80\u4e0b\u4e00\u7ad9\u8d70\uff1b\u592a\u7d2f\u3001\u592a\u8fdc\u6216\u5929\u6c14\u4e0d\u5bf9\u65f6\uff0c\u53ef\u4ee5\u6539\u7528\u66ff\u4ee3\u65b9\u6848\u3002",
            executionAllDoneLine = "\u8def\u7ebf\u7ad9\u70b9\u5df2\u5168\u90e8\u5904\u7406\uff0c\u53ef\u4ee5\u53bb\u5b8c\u6210\u9690\u85cf\u4efb\u52a1\u6216\u5206\u4eab\u901a\u5173\u5361\u3002",
            executionCurrentStopLabel = "\u5f53\u524d\u7ad9",
            executionNextActionLabel = "\u4e0b\u4e00\u6b65",
            executionRewardLabel = "\u53ef\u5f97\u79ef\u5206",
            executionProgressLabel = "\u7ad9\u70b9\u8fdb\u5ea6",
            executionNextStopLabel = "\u4e0b\u4e00\u7ad9",
            executionTimelineLabel = "\u8def\u7ebf\u65f6\u95f4\u8f74",
            executionNowLabel = "\u5f53\u524d",
            executionDoneLabel = "\u5df2\u5b8c\u6210",
            executionTodoLabel = "\u5f85\u5904\u7406",
            executionOpenMap = "\u6253\u5f00\u5730\u56fe",
            executionUndoCheckIn = "\u53d6\u6d88\u6253\u5361",
            executionCompleteStop = { points -> "\u5b8c\u6210 +$points" },
            stopMissionLabel = "\u672c\u7ad9\u4efb\u52a1",
            stopRewardLabel = "\u5b8c\u6210\u5956\u52b1",
            stopSwapTitle = "\u5355\u70b9\u66ff\u4ee3\u73a9\u6cd5",
            stopSwapAction = "\u6539\u7528\u66ff\u4ee3\u65b9\u6848",
            stopSwapSelected = "\u5df2\u7528\u66ff\u4ee3\u65b9\u6848",
            stopSwapHint = "\u7528\u4e8e\u8ddd\u79bb\u592a\u8fdc\u3001\u5929\u6c14\u4e0d\u597d\u6216\u4eba\u6d41\u592a\u591a\u65f6\uff1b\u4e0d\u8ba1\u5165\u5730\u70b9\u6253\u5361\u79ef\u5206\u3002",
            stopSwapSelectedHint = "\u8fd9\u7ad9\u5df2\u6539\u6210\u66ff\u4ee3\u73a9\u6cd5\uff1b\u672c\u5c40\u8fdb\u5ea6\u7ee7\u7eed\uff0c\u4f46\u4e0d\u53d1\u653e\u5730\u70b9\u6253\u5361\u79ef\u5206\u3002",
            stopRerollAction = "\u9884\u89c8\u65b0\u5730\u70b9",
            stopRerollHint = "\u5982\u679c\u662f\u5730\u70b9\u672c\u8eab\u4e0d\u5408\u9002\uff0c\u5148\u9884\u89c8\u540c\u57ce\u5019\u9009\uff0c\u518d\u51b3\u5b9a\u662f\u5426\u53ea\u91cd\u62bd\u8fd9\u4e00\u7ad9\u3002",
            stopRerollPreviewTitle = "\u5019\u9009\u9884\u89c8",
            stopRerollConfirmAction = "\u786e\u8ba4\u66ff\u6362",
            stopRerollCancelAction = "\u4fdd\u7559\u5f53\u524d",
            stopRerollSameCategory = "\u540c\u7c7b\u578b",
            stopRerollMatchedTagsLabel = "\u5339\u914d\u6807\u7b7e",
            stopRerollStayDeltaLabel = "\u505c\u7559\u5dee\u5f02",
            stopRerollSourceLabel = "\u6765\u6e90",
            stopRerollPreviewHint = "\u786e\u8ba4\u540e\u4f1a\u4fdd\u7559\u5176\u4ed6\u7ad9\u70b9\uff0c\u5e76\u6e05\u7406\u8fd9\u4e00\u7ad9\u65e7\u7684\u6253\u5361\u72b6\u6001\u3001\u53cd\u9988\u548c\u79ef\u5206\u3002",
            stopRerollDone = "\u5df2\u6362\u4e00\u4e2a\u540c\u57ce\u5019\u9009\u5730\u70b9",
            stopRerollUnavailable = "\u6682\u65f6\u6ca1\u6709\u53ef\u66ff\u6362\u7684\u540c\u57ce\u5019\u9009\u70b9",
            stopRestoreTitle = "\u6700\u8fd1\u4e00\u6b21\u66ff\u6362",
            stopRestoreAction = "\u6062\u590d\u4e0a\u4e00\u7ad9",
            stopRestoreHint = "\u6062\u590d\u540e\u4f1a\u628a\u4e0a\u4e00\u7ad9\u7684\u6253\u5361\u72b6\u6001\u3001\u53cd\u9988\u548c\u79ef\u5206\u4e00\u8d77\u5e26\u56de\u6765\u3002",
            stopRestoreDone = "\u5df2\u6062\u590d\u4e0a\u4e00\u7ad9",
            stopRestoreUnavailable = "\u6ca1\u6709\u53ef\u6062\u590d\u7684\u4e0a\u4e00\u7ad9",
            stopRestoreRoute = { previous, replacement -> "\u5df2\u5c06 $previous \u66ff\u6362\u4e3a $replacement\uff1b\u4f60\u8fd8\u53ef\u4ee5\u56de\u5230\u4e0a\u4e00\u7ad9\u3002" },
        )
        else -> RoutePlaybookStrings(
            sectionTitle = "Start playbook",
            sectionEnglish = "playbook",
            openingLine = "Start with the first stop, collect each check-in, and use the swap plan whenever the mood, weather, or crowd feels off.",
            startAtLabel = "Start at",
            photoWindowLabel = "Best photo window",
            rewardLabel = "Points available",
            safeSwapLabel = "Safe swap",
            tagsLabel = "This run",
            visualMapTitle = "Route preview",
            visualMapEnglish = "visual map",
            visualMapHint = "Check the order and current stop first, then open an external map when you are ready.",
            visualOpenCurrentMap = "Navigate",
            executionTitle = "Live route mode",
            executionEnglish = "next step",
            executionReadyLine = "Handle the current stop first, then move to the next one. If it feels too far, crowded, rainy, or off-mood, use the swap plan.",
            executionAllDoneLine = "Every route stop is handled. Finish the hidden task or share the completion card.",
            executionCurrentStopLabel = "Current stop",
            executionNextActionLabel = "Next action",
            executionRewardLabel = "Points",
            executionProgressLabel = "Stop progress",
            executionNextStopLabel = "Next stop",
            executionTimelineLabel = "Route timeline",
            executionNowLabel = "Now",
            executionDoneLabel = "Done",
            executionTodoLabel = "Todo",
            executionOpenMap = "Open map",
            executionUndoCheckIn = "Undo check-in",
            executionCompleteStop = { points -> "Complete +$points" },
            stopMissionLabel = "Stop mission",
            stopRewardLabel = "Reward",
            stopSwapTitle = "Single-stop swap",
            stopSwapAction = "Use swap plan",
            stopSwapSelected = "Swap plan used",
            stopSwapHint = "Use this when the stop is too far, crowded, rainy, or off-mood. It moves the run forward but does not award check-in points.",
            stopSwapSelectedHint = "This stop is resolved with the swap plan. The run continues, but this stop does not award check-in points.",
            stopRerollAction = "Preview new stop",
            stopRerollHint = "If the place itself does not fit, preview a same-city candidate before redrawing only this stop.",
            stopRerollPreviewTitle = "Replacement preview",
            stopRerollConfirmAction = "Use this stop",
            stopRerollCancelAction = "Keep current stop",
            stopRerollSameCategory = "Similar category",
            stopRerollMatchedTagsLabel = "Matches",
            stopRerollStayDeltaLabel = "Stay delta",
            stopRerollSourceLabel = "Source",
            stopRerollPreviewHint = "Confirming keeps the rest of the route and clears old check-in status, feedback, and points for this stop.",
            stopRerollDone = "Replaced with another same-city candidate.",
            stopRerollUnavailable = "No same-city replacement candidate is available yet.",
            stopRestoreTitle = "Latest replacement",
            stopRestoreAction = "Restore previous stop",
            stopRestoreHint = "Restoring brings back the previous stop's check-in status, feedback, and points.",
            stopRestoreDone = "Previous stop restored.",
            stopRestoreUnavailable = "No previous stop is available to restore.",
            stopRestoreRoute = { previous, replacement -> "$previous was replaced by $replacement. You can still go back to the previous stop." },
        )
    }
}

private fun TaskStatus.isResolved(): Boolean {
    return this == TaskStatus.Completed || this == TaskStatus.Skipped
}

private fun awkwardIndex(quest: Quest): Int {
    return when {
        quest.relationship == "暧昧中" -> 1
        "社恐友好" in quest.tags -> 1
        "搞笑" in quest.tags.joinToString("") -> 2
        else -> 2
    }
}

private fun energyIndex(quest: Quest): Int {
    return when {
        quest.duration.contains("30") -> 1
        quest.duration.contains("90") || quest.duration.contains("1 小时") -> 2
        quest.duration.contains("半天") -> 4
        else -> 3
    }
}
private fun currentRunnableStop(record: QuestRecord): RouteStop? {
    val stops = record.quest.itineraryPlan?.stops.orEmpty()
    return stops.firstOrNull { stop ->
        !record.progress.statusFor(stop.checkInTask.taskId).isResolved()
    } ?: stops.lastOrNull()
}
