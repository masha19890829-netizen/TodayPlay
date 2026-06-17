package com.todayplay.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.todayplay.app.GenerationStatus
import com.todayplay.app.GenerationUiState
import com.todayplay.app.R
import com.todayplay.app.localization.LocalTodayPlayStrings
import com.todayplay.app.ui.components.GhostButton
import com.todayplay.app.ui.components.HeartPrimaryButton
import com.todayplay.app.ui.theme.CherryPressed
import com.todayplay.app.ui.theme.GalleryWhite
import com.todayplay.app.ui.theme.InkBlack
import com.todayplay.app.ui.theme.LineBeige
import com.todayplay.app.ui.theme.RoseGold
import com.todayplay.app.ui.theme.TicketBeige
import com.todayplay.app.ui.theme.WarmCream
import com.todayplay.app.ui.theme.WarmGray
import kotlinx.coroutines.delay

private const val LoadingSuccessHoldMillis = 1800L

@Composable
fun LoadingScreen(
    generationState: GenerationUiState,
    onFinished: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    val strings = LocalTodayPlayStrings.current
    val lines = strings.loadingLines
    var lineIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(520)
            lineIndex = (lineIndex + 1) % lines.size
        }
    }
    LaunchedEffect(generationState.status, generationState.questId) {
        if (generationState.status == GenerationStatus.Succeeded && generationState.questId != null) {
            delay(LoadingSuccessHoldMillis)
            onFinished()
        }
    }

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.tp_art_loading_route),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            GalleryWhite.copy(alpha = 0.82f),
                            WarmCream.copy(alpha = 0.94f),
                        ),
                    ),
                ),
        )
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            val compact = maxHeight < 700.dp || maxWidth < 360.dp || maxWidth > maxHeight
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(
                        horizontal = if (compact) 20.dp else 36.dp,
                        vertical = if (compact) 20.dp else 36.dp,
                    ),
                verticalArrangement = if (compact) Arrangement.Top else Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (generationState.status == GenerationStatus.Failed) {
                        strings.loadingFailedTitle
                    } else {
                        strings.loadingTitle
                    },
                    color = InkBlack,
                    fontFamily = FontFamily.Serif,
                    fontSize = if (compact) 24.sp else 28.sp,
                    lineHeight = if (compact) 31.sp else 36.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(if (compact) 18.dp else 28.dp))
                RouteBuildAnimation(
                    active = generationState.status != GenerationStatus.Failed,
                    succeeded = generationState.status == GenerationStatus.Succeeded,
                    stageIndex = lineIndex % 4,
                    label = strings.loadingTitle,
                    modifier = Modifier.size(
                        width = if (compact) 200.dp else 230.dp,
                        height = if (compact) 128.dp else 150.dp,
                    ),
                )
                Spacer(Modifier.height(if (compact) 12.dp else 16.dp))
                LoadingStageRail(
                    activeIndex = lineIndex % 4,
                    succeeded = generationState.status == GenerationStatus.Succeeded,
                    compact = compact,
                )
                Spacer(Modifier.height(if (compact) 14.dp else 20.dp))
                Text(
                    text = if (generationState.status == GenerationStatus.Failed) {
                        strings.loadingFailedBody
                    } else {
                        lines[lineIndex]
                    },
                    color = WarmGray,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                if (generationState.status == GenerationStatus.Failed) {
                    Spacer(Modifier.height(if (compact) 16.dp else 24.dp))
                    HeartPrimaryButton(
                        text = strings.loadingRetry,
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth().widthIn(max = 520.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    GhostButton(
                        text = strings.loadingBackToEdit,
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth().widthIn(max = 520.dp),
                    )
                }
                Spacer(Modifier.height(if (compact) 30.dp else 64.dp))
                Text(
                    text = strings.loadingFooter,
                    color = WarmGray,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.heightIn(min = 18.dp),
                )
            }
        }
    }
}

@Composable
private fun RouteBuildAnimation(
    active: Boolean,
    succeeded: Boolean,
    stageIndex: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "route-loading-motion")
    val routeProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Restart),
        label = "route-loading-draw",
    )
    val bob by transition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse),
        label = "route-loading-bob",
    )
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(GalleryWhite.copy(alpha = 0.74f)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val p = if (active) routeProgress else 0.82f
            val start = Offset(size.width * 0.18f, size.height * 0.72f)
            val mid = Offset(size.width * 0.48f, size.height * (0.38f + bob * 0.003f))
            val end = Offset(size.width * 0.82f, size.height * 0.60f)
            val firstEnd = Offset(
                start.x + (mid.x - start.x) * (p / 0.55f).coerceIn(0f, 1f),
                start.y + (mid.y - start.y) * (p / 0.55f).coerceIn(0f, 1f),
            )
            val secondEnd = Offset(
                mid.x + (end.x - mid.x) * ((p - 0.55f) / 0.45f).coerceIn(0f, 1f),
                mid.y + (end.y - mid.y) * ((p - 0.55f) / 0.45f).coerceIn(0f, 1f),
            )
            drawCircle(
                color = TicketBeige.copy(alpha = if (succeeded) 0.66f else 0.48f),
                radius = size.minDimension * 0.42f,
                center = Offset(size.width * 0.72f, size.height * 0.26f),
            )
            drawCircle(
                color = RoseGold.copy(alpha = 0.12f + stageIndex * 0.025f),
                radius = size.minDimension * (0.12f + stageIndex * 0.025f),
                center = Offset(size.width * 0.30f, size.height * 0.28f),
            )
            drawLine(
                color = LineBeige.copy(alpha = 0.86f),
                start = start,
                end = end,
                strokeWidth = 7f,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = GalleryWhite.copy(alpha = 0.96f),
                start = start,
                end = firstEnd,
                strokeWidth = 9f,
                cap = StrokeCap.Round,
            )
            if (p > 0.55f) {
                drawLine(
                    color = GalleryWhite.copy(alpha = 0.96f),
                    start = mid,
                    end = secondEnd,
                    strokeWidth = 9f,
                    cap = StrokeCap.Round,
                )
            }
            listOf(start, mid, end).forEachIndexed { index, point ->
                val reached = p >= index * 0.45f
                drawCircle(
                    color = if (reached) {
                        if (index == 0) CherryPressed else RoseGold
                    } else {
                        LineBeige
                    },
                    radius = if (reached) 10f else 7f,
                    center = point,
                )
                drawCircle(
                    color = GalleryWhite.copy(alpha = 0.9f),
                    radius = 3.5f,
                    center = point,
                )
            }
        }
        Text(
            text = label,
            color = WarmGray,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 22.dp),
        )
        if (succeeded) {
            LoadingStamp(modifier = Modifier.align(Alignment.TopEnd).padding(20.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LoadingStageRail(
    activeIndex: Int,
    succeeded: Boolean,
    compact: Boolean,
) {
    val stages = listOf("理解需求", "排列路线", "检查预算", "打印票根")
    val railModifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 620.dp)
        .clip(RoundedCornerShape(18.dp))
        .background(GalleryWhite.copy(alpha = 0.66f))
        .border(1.dp, LineBeige.copy(alpha = 0.72f), RoundedCornerShape(18.dp))
        .padding(horizontal = 10.dp, vertical = 8.dp)
    if (compact) {
        FlowRow(
            modifier = railModifier,
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            stages.forEachIndexed { index, stage ->
                val active = succeeded || index == activeIndex
                Text(
                    text = stage,
                    color = if (active) CherryPressed else WarmGray,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    modifier = Modifier
                        .widthIn(min = 104.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (active) RoseGold.copy(alpha = 0.18f) else WarmCream.copy(alpha = 0.36f))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    } else {
        Row(
            modifier = railModifier,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            stages.forEachIndexed { index, stage ->
                val active = succeeded || index == activeIndex
                Text(
                    text = stage,
                    color = if (active) CherryPressed else WarmGray,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (active) RoseGold.copy(alpha = 0.18f) else WarmCream.copy(alpha = 0.36f))
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun LoadingStamp(modifier: Modifier = Modifier) {
    Text(
        text = "OK",
        color = CherryPressed,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .clip(CircleShape)
            .background(GalleryWhite.copy(alpha = 0.82f))
            .border(1.dp, CherryPressed.copy(alpha = 0.42f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .graphicsLayer { rotationZ = -9f },
    )
}
