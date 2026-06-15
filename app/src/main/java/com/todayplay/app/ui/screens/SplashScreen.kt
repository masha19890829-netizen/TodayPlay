package com.todayplay.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.todayplay.app.localization.LocalTodayPlayStrings
import com.todayplay.app.ui.components.PaperBackground
import com.todayplay.app.ui.theme.CherryPressed
import com.todayplay.app.ui.theme.DustPink
import com.todayplay.app.ui.theme.InkBlack
import com.todayplay.app.ui.theme.RoseGold
import com.todayplay.app.ui.theme.GalleryWhite
import com.todayplay.app.ui.theme.WarmGray
import kotlinx.coroutines.delay

private const val SplashIntroMillis = 1600L

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SplashRouteMotion(
                progress = introAlpha,
                drift = floatY,
                modifier = Modifier.size(width = 210.dp, height = 96.dp),
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = strings.splashProposal,
                color = WarmGray,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.graphicsLayer { alpha = introAlpha },
            )
            Spacer(Modifier.height(24.dp))
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
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = introAlpha },
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = strings.splashTagline,
                color = WarmGray,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { alpha = introAlpha },
            )
            Spacer(Modifier.height(44.dp))
            Text(
                text = "心",
                color = DustPink,
                fontSize = 32.sp,
                modifier = Modifier
                    .offset(y = floatY.dp)
                    .graphicsLayer { alpha = introAlpha },
            )
            Text(
                text = strings.splashFooter,
                color = CherryPressed,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.graphicsLayer { alpha = introAlpha },
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
