package com.todayplay.app.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.todayplay.app.R
import com.todayplay.app.localization.LocalTodayPlayStrings
import com.todayplay.app.ui.components.PaperBackground
import com.todayplay.app.ui.theme.CherryPressed
import com.todayplay.app.ui.theme.GalleryWhite
import com.todayplay.app.ui.theme.InkBlack
import com.todayplay.app.ui.theme.RoseGold
import com.todayplay.app.ui.theme.WarmGray
import kotlinx.coroutines.delay

private const val SplashIntroMillis = 1900L

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val strings = LocalTodayPlayStrings.current
    var introStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        introStarted = true
        delay(SplashIntroMillis)
        onFinished()
    }
    val introAlpha by animateFloatAsState(
        targetValue = if (introStarted) 1f else 0f,
        animationSpec = tween(520),
        label = "splash-intro-alpha",
    )
    val introScale by animateFloatAsState(
        targetValue = if (introStarted) 1f else 0.94f,
        animationSpec = tween(680),
        label = "splash-intro-scale",
    )
    val transition = rememberInfiniteTransition(label = "splash")
    val floatY by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "float-y",
    )

    PaperBackground {
        Image(
            painter = painterResource(R.drawable.tp_art_splash_companion),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = introAlpha * 0.92f
                    scaleX = 1.02f
                    scaleY = 1.02f
                    translationY = floatY * 2.2f
                },
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to GalleryWhite.copy(alpha = 0.92f),
                        0.30f to GalleryWhite.copy(alpha = 0.54f),
                        0.68f to GalleryWhite.copy(alpha = 0.18f),
                        1f to GalleryWhite.copy(alpha = 0.90f),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 44.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = strings.splashProposal,
                    color = WarmGray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.graphicsLayer { alpha = introAlpha },
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = strings.appName,
                    color = InkBlack,
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.graphicsLayer {
                        alpha = introAlpha
                        scaleX = introScale
                        scaleY = introScale
                    },
                )
                Text(
                    text = strings.appEnglishName,
                    color = InkBlack,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .widthIn(max = 360.dp)
                        .graphicsLayer { alpha = introAlpha },
                )
                Spacer(Modifier.height(14.dp))
                SplashTaglineText(
                    tagline = strings.splashTagline,
                    modifier = Modifier.graphicsLayer { alpha = introAlpha },
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SplashRouteMotion(
                    progress = introAlpha,
                    drift = floatY,
                    modifier = Modifier.size(width = 240.dp, height = 110.dp),
                )
                Spacer(Modifier.height(12.dp))
                SplashStampMark(
                    progress = introAlpha,
                    drift = floatY,
                    modifier = Modifier.size(72.dp),
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = strings.splashFooter,
                    color = CherryPressed,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.graphicsLayer { alpha = introAlpha },
                )
            }
        }
    }
}

private fun balancedSplashTagline(tagline: String): String {
    return when (tagline) {
        "把普通日子，剪成一小段只属于你们的电影。" -> "把普通日子，剪成\n只属于你们的一小段电影。"
        "把普通日子，剪成一小段只屬於你們的電影。" -> "把普通日子，剪成\n只屬於你們的一小段電影。"
        else -> tagline
    }
}

@Composable
private fun SplashTaglineText(
    tagline: String,
    modifier: Modifier = Modifier,
) {
    val lines = stableSplashTaglineLines(tagline)
    if (lines.size > 1) {
        Column(
            modifier = modifier.widthIn(max = 300.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            lines.forEach { line ->
                Text(
                    text = line,
                    color = WarmGray,
                    style = if (lines.size >= 3) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
                    lineHeight = if (lines.size >= 3) 22.sp else 24.sp,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    } else {
        Text(
            text = lines.firstOrNull().orEmpty(),
            color = WarmGray,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 24.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = modifier.widthIn(max = 360.dp),
        )
    }
}

private fun stableSplashTaglineLines(tagline: String): List<String> {
    return when (tagline) {
        SimplifiedSplashTagline -> listOf(SimplifiedSplashLine1, SimplifiedSplashLine2, SimplifiedSplashLine3)
        TraditionalSplashTagline -> listOf(TraditionalSplashLine1, TraditionalSplashLine2, TraditionalSplashLine3)
        else -> tagline.split("\n").map { it.trim() }.filter { it.isNotEmpty() }.ifEmpty { listOf(tagline) }
    }
}

private const val SimplifiedSplashTagline =
    "\u628a\u666e\u901a\u65e5\u5b50\uff0c\u526a\u6210\u4e00\u5c0f\u6bb5\u53ea\u5c5e\u4e8e\u4f60\u4eec\u7684\u7535\u5f71\u3002"
private const val SimplifiedSplashLine1 =
    "\u628a\u666e\u901a\u65e5\u5b50\uff0c"
private const val SimplifiedSplashLine2 =
    "\u526a\u6210\u4e00\u5c0f\u6bb5"
private const val SimplifiedSplashLine3 =
    "\u53ea\u5c5e\u4e8e\u4f60\u4eec\u7684\u7535\u5f71\u3002"
private const val TraditionalSplashTagline =
    "\u628a\u666e\u901a\u65e5\u5b50\uff0c\u526a\u6210\u4e00\u5c0f\u6bb5\u53ea\u5c6c\u65bc\u4f60\u5011\u7684\u96fb\u5f71\u3002"
private const val TraditionalSplashLine1 =
    "\u628a\u666e\u901a\u65e5\u5b50\uff0c"
private const val TraditionalSplashLine2 =
    "\u526a\u6210\u4e00\u5c0f\u6bb5"
private const val TraditionalSplashLine3 =
    "\u53ea\u5c6c\u65bc\u4f60\u5011\u7684\u96fb\u5f71\u3002"

@Composable
private fun SplashStampMark(
    progress: Float,
    drift: Float,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .offset(y = (drift * 0.6f).dp)
            .clip(CircleShape)
            .background(GalleryWhite.copy(alpha = 0.72f))
            .graphicsLayer {
                alpha = progress
                rotationZ = -8f + progress * 8f
                scaleX = 0.82f + progress * 0.18f
                scaleY = 0.82f + progress * 0.18f
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = CherryPressed.copy(alpha = 0.58f),
                radius = size.minDimension * 0.46f,
                center = center,
                style = Stroke(width = 3.2f),
            )
            drawCircle(
                color = RoseGold.copy(alpha = 0.24f),
                radius = size.minDimension * 0.30f,
                center = center,
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TODAY",
                color = CherryPressed,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
            )
            Text(
                text = "READY",
                color = WarmGray,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
            )
        }
    }
}

@Composable
private fun SplashRouteMotion(
    progress: Float,
    drift: Float,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val left = Offset(size.width * 0.12f, size.height * 0.72f)
            val middle = Offset(size.width * 0.48f, size.height * (0.38f + drift * 0.002f))
            val right = Offset(size.width * 0.88f, size.height * 0.60f)
            val activeMiddle = Offset(
                x = left.x + (middle.x - left.x) * progress.coerceIn(0f, 1f),
                y = left.y + (middle.y - left.y) * progress.coerceIn(0f, 1f),
            )
            val activeRight = Offset(
                x = middle.x + (right.x - middle.x) * ((progress - 0.45f) / 0.55f).coerceIn(0f, 1f),
                y = middle.y + (right.y - middle.y) * ((progress - 0.45f) / 0.55f).coerceIn(0f, 1f),
            )
            drawCircle(
                color = RoseGold.copy(alpha = 0.12f * progress),
                radius = size.minDimension * 0.34f,
                center = Offset(size.width * 0.78f, size.height * 0.30f),
            )
            drawLine(
                color = GalleryWhite.copy(alpha = 0.76f * progress),
                start = left,
                end = activeMiddle,
                strokeWidth = 8f,
                cap = StrokeCap.Round,
            )
            if (progress > 0.45f) {
                drawLine(
                    color = GalleryWhite.copy(alpha = 0.76f * progress),
                    start = middle,
                    end = activeRight,
                    strokeWidth = 8f,
                    cap = StrokeCap.Round,
                )
            }
            listOf(left, middle, right).forEachIndexed { index, point ->
                val visible = progress > index * 0.24f
                if (visible) {
                    drawCircle(
                        color = if (index == 0) CherryPressed else RoseGold,
                        radius = if (index == 0) 10f else 8f,
                        center = point,
                    )
                    drawCircle(
                        color = GalleryWhite.copy(alpha = 0.9f),
                        radius = 3.5f,
                        center = point,
                    )
                }
            }
        }
    }
}
