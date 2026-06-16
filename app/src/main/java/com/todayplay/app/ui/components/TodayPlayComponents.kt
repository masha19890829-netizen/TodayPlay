package com.todayplay.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.todayplay.app.R
import com.todayplay.app.localization.CompletionCardStrings
import com.todayplay.app.model.CompletionCardData
import com.todayplay.app.model.QuestTask
import com.todayplay.app.ui.theme.BlackCherry
import com.todayplay.app.ui.theme.CherryPressed
import com.todayplay.app.ui.theme.DustPink
import com.todayplay.app.ui.theme.GalleryWhite
import com.todayplay.app.ui.theme.InkBlack
import com.todayplay.app.ui.theme.LineBeige
import com.todayplay.app.ui.theme.MistRose
import com.todayplay.app.ui.theme.PaperWhite
import com.todayplay.app.ui.theme.RoseGold
import com.todayplay.app.ui.theme.StoneTaupe
import com.todayplay.app.ui.theme.TicketBeige
import com.todayplay.app.ui.theme.WarmCream
import com.todayplay.app.ui.theme.WarmGray
import kotlinx.coroutines.delay

data class HeroSlide(
    val imageRes: Int,
    val title: String,
    val subtitle: String,
    val footer: String,
)

@Composable
fun PaperBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(GalleryWhite, PaperWhite, WarmCream),
                ),
            ),
    ) {
        PaperTexture()
        content()
    }
}

@Composable
private fun PaperTexture() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        repeat(26) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(LineBeige.copy(alpha = 0.18f)),
            )
        }
    }
}

@Composable
fun FloatingMarks() {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val transition = rememberInfiniteTransition(label = "floating-marks")
        val drift by transition.animateFloat(
            initialValue = -4f,
            targetValue = 4f,
            animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Reverse),
            label = "mark-drift",
        )
        Text(
            text = "心",
            color = DustPink.copy(alpha = 0.55f),
            fontSize = 16.sp,
            modifier = Modifier.offset(maxWidth * 0.78f, (72 + drift).dp),
        )
        Text(
            text = "光",
            color = RoseGold.copy(alpha = 0.45f),
            fontSize = 18.sp,
            modifier = Modifier.offset(maxWidth * 0.08f, (144 - drift).dp),
        )
        Text(
            text = "心",
            color = CherryPressed.copy(alpha = 0.38f),
            fontSize = 18.sp,
            modifier = Modifier.offset(maxWidth * 0.7f, maxHeight * 0.72f + drift.dp),
        )
    }
}

@Composable
fun HeroImageCard(
    modifier: Modifier = Modifier,
    overlayTitle: String,
    overlaySubtitle: String,
    footer: String? = null,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(WarmCream)
            .border(1.dp, RoseGold.copy(alpha = 0.36f), RoundedCornerShape(28.dp)),
    ) {
        Image(
            painter = painterResource(R.drawable.romantic_hero),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to GalleryWhite.copy(alpha = 0.08f),
                        0.48f to Color.Transparent,
                        1f to GalleryWhite.copy(alpha = 0.82f),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
        ) {
            Text(
                text = overlayTitle,
                color = InkBlack,
                style = MaterialTheme.typography.displayLarge,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = overlaySubtitle,
                color = InkBlack.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.weight(1f))
            if (footer != null) {
                Text(
                    text = footer,
                    color = WarmGray,
                    style = MaterialTheme.typography.labelSmall,
                    lineHeight = 14.sp,
                )
            }
        }
    }
}

@Composable
fun CinematicSlideShowCard(
    slides: List<HeroSlide>,
    modifier: Modifier = Modifier,
) {
    var index by remember { mutableIntStateOf(0) }
    val transition = rememberInfiniteTransition(label = "slideshow-zoom")
    val zoom by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.055f,
        animationSpec = infiniteRepeatable(tween(3600), RepeatMode.Reverse),
        label = "hero-zoom",
    )

    LaunchedEffect(slides.size) {
        if (slides.size > 1) {
            while (true) {
                delay(3200)
                index = (index + 1) % slides.size
            }
        }
    }

    val slide = slides[index.coerceIn(0, slides.lastIndex)]
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(WarmCream)
            .border(1.dp, RoseGold.copy(alpha = 0.36f), RoundedCornerShape(28.dp)),
    ) {
        Crossfade(
            targetState = slide,
            animationSpec = tween(700),
            label = "hero-crossfade",
        ) { activeSlide ->
            Image(
                painter = painterResource(activeSlide.imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = zoom
                        scaleY = zoom
                    },
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to GalleryWhite.copy(alpha = 0.08f),
                        0.45f to Color.Transparent,
                        1f to GalleryWhite.copy(alpha = 0.86f),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
        ) {
            Text(
                text = slide.title,
                color = InkBlack,
                style = MaterialTheme.typography.displayLarge,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = slide.subtitle,
                color = InkBlack.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                slides.forEachIndexed { dotIndex, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (dotIndex == index) 18.dp else 7.dp, 7.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                if (dotIndex == index) CherryPressed.copy(alpha = 0.82f)
                                else LineBeige.copy(alpha = 0.82f),
                            ),
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = slide.footer,
                color = WarmGray,
                style = MaterialTheme.typography.labelSmall,
                lineHeight = 14.sp,
            )
        }
    }
}

@Composable
fun CuteTopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    action: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            Text(
                text = "<",
                fontSize = 24.sp,
                color = InkBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable { onBack() },
            )
        } else {
            Spacer(Modifier.width(42.dp))
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = InkBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (action != null && onAction != null) {
            Text(
                text = action,
                color = CherryPressed,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .widthIn(min = 42.dp, max = 64.dp)
                    .clickable { onAction() },
            )
        } else {
            Text(
                text = "心",
                color = DustPink,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(42.dp),
            )
        }
    }
}

@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    padding: Dp = 18.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 760.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = GalleryWhite.copy(alpha = 0.86f)),
        border = BorderStroke(1.dp, LineBeige),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(padding),
            content = content,
        )
    }
}

@Composable
fun TicketCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 760.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(TicketBeige.copy(alpha = 0.72f))
            .border(1.dp, RoseGold.copy(alpha = 0.42f), RoundedCornerShape(18.dp))
            .padding(16.dp),
        content = content,
    )
}

@Composable
fun SectionHeader(
    number: String,
    title: String,
    english: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = number,
            color = CherryPressed,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = title,
            color = InkBlack,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "/ ${english.uppercase()}",
            color = WarmGray,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
        Spacer(Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(LineBeige),
        )
    }
}

@Composable
fun KawaiiChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val background by animateColorAsState(
        targetValue = when {
            !enabled -> LineBeige.copy(alpha = 0.62f)
            selected -> CherryPressed
            else -> GalleryWhite.copy(alpha = 0.72f)
        },
        animationSpec = tween(180),
        label = "chip-bg",
    )
    val foreground by animateColorAsState(
        targetValue = when {
            !enabled -> WarmGray.copy(alpha = 0.66f)
            selected -> GalleryWhite
            else -> WarmGray
        },
        animationSpec = tween(180),
        label = "chip-fg",
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .border(
                1.dp,
                if (selected) CherryPressed else LineBeige,
                RoundedCornerShape(999.dp),
            )
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 15.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = foreground,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun HeartPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "button-scale")
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp)
            .scale(scale),
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CherryPressed,
            contentColor = GalleryWhite,
            disabledContainerColor = MistRose,
            disabledContentColor = WarmGray,
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp, pressedElevation = 1.dp),
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 9.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = if (enabled) GalleryWhite else WarmGray,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = 50.dp),
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GalleryWhite.copy(alpha = 0.72f),
            contentColor = BlackCherry,
            disabledContainerColor = LineBeige.copy(alpha = 0.62f),
            disabledContentColor = WarmGray,
        ),
        border = BorderStroke(1.dp, LineBeige),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun TextInputCard(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                color = WarmGray,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        modifier = modifier.fillMaxWidth(),
        minLines = minLines,
        shape = RoundedCornerShape(18.dp),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = InkBlack),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = GalleryWhite.copy(alpha = 0.78f),
            unfocusedContainerColor = GalleryWhite.copy(alpha = 0.62f),
            focusedBorderColor = CherryPressed,
            unfocusedBorderColor = LineBeige,
            cursorColor = CherryPressed,
        ),
    )
}

@Composable
fun QuestTaskCard(
    index: Int,
    task: QuestTask,
    modifier: Modifier = Modifier,
) {
    SoftCard(modifier = modifier, padding = 16.dp) {
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
                Text("完成方式：${task.howToComplete}", style = MaterialTheme.typography.bodyMedium, color = InkBlack)
                Spacer(Modifier.height(6.dp))
                Text("Tips：${task.cuteTip}", style = MaterialTheme.typography.bodyMedium, color = CherryPressed)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("心".repeat(task.difficultyHearts.coerceIn(1, 3)), color = CherryPressed)
                Text(task.estimatedTime, style = MaterialTheme.typography.labelSmall, color = WarmGray)
            }
        }
    }
}

@Composable
fun ShareCompletionCard(
    cardData: CompletionCardData,
    labels: CompletionCardStrings,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "share-card-stamp")
    val stampPulse by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "stamp-pulse",
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .clip(RoundedCornerShape(24.dp))
            .background(GalleryWhite)
            .border(2.dp, TicketBeige, RoundedCornerShape(24.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("TODAY WAS", fontFamily = FontFamily.Serif, fontSize = 32.sp, color = InkBlack)
        Text("PLAYED", fontFamily = FontFamily.Serif, fontSize = 34.sp, color = InkBlack)
        Spacer(Modifier.height(6.dp))
        Text(labels.body, style = MaterialTheme.typography.bodyMedium, color = WarmGray, textAlign = TextAlign.Center)
        Spacer(Modifier.height(18.dp))
        Image(
            painter = painterResource(R.drawable.romantic_date),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp)),
        )
        Spacer(Modifier.height(14.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(labels.episodeTitleLabel, style = MaterialTheme.typography.labelSmall, color = CherryPressed)
            Text(cardData.title, style = MaterialTheme.typography.titleLarge, color = InkBlack)
            Spacer(Modifier.height(10.dp))
            InfoLine(labels.completionTitleLabel, cardData.completionTitle)
            InfoLine(labels.durationLabel, cardData.duration)
            InfoLine(labels.dateLabel, cardData.dateLabel)
            InfoLine(labels.relationshipValueLabel, "+1 心")
            if (cardData.totalStopCount > 0) {
                InfoLine(labels.routeTypeLabel, cardData.planType ?: labels.routeQuestFallback)
                InfoLine(labels.stopCheckInLabel, "${cardData.checkedInStopCount}/${cardData.totalStopCount}")
                InfoLine(labels.rewardPointsLabel, "${cardData.totalRewardPoints}")
            }
            InfoLine(labels.taskCompletionLabel, "${cardData.completedTaskCount}/${cardData.totalTaskCount}")
            InfoLine(labels.keywordsLabel, cardData.keywords.joinToString(" / "))
            InfoLine(labels.hiddenTaskLabel, labels.hiddenTaskStatus(cardData.hiddenTaskStatus))
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "“${cardData.summary}”",
            style = MaterialTheme.typography.bodyMedium,
            color = BlackCherry,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(TicketBeige.copy(alpha = 0.48f))
                .border(1.dp, RoseGold.copy(alpha = 0.7f), RoundedCornerShape(999.dp))
                .graphicsLayer {
                    scaleX = stampPulse
                    scaleY = stampPulse
                }
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("GOOD DAY CERTIFIED", color = CherryPressed, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.height(10.dp))
        Text(
            labels.footer,
            style = MaterialTheme.typography.labelSmall,
            color = WarmGray,
        )
    }
}

@Composable
fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = WarmGray, modifier = Modifier.width(78.dp))
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(LineBeige),
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = InkBlack,
            textAlign = TextAlign.End,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1.3f),
        )
    }
}

@Composable
fun IconMark(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CherryPressed,
) {
    Text(
        text = text,
        color = color,
        fontSize = 18.sp,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

@Composable
fun HeartIconImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.ic_launcher_foreground),
        contentDescription = null,
        colorFilter = ColorFilter.tint(CherryPressed),
        modifier = modifier,
    )
}
