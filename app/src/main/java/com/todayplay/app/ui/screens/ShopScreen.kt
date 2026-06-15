package com.todayplay.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.todayplay.app.BuildConfig
import com.todayplay.app.R
import com.todayplay.app.data.ProductEventLogger
import com.todayplay.app.localization.LocalTodayPlayStrings
import com.todayplay.app.model.TodayPlayProducts
import com.todayplay.app.ui.components.CuteTopBar
import com.todayplay.app.ui.components.GhostButton
import com.todayplay.app.ui.components.HeartPrimaryButton
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
import com.todayplay.app.ui.theme.WarmGray

@Composable
fun ShopScreen(
    onBack: () -> Unit,
    onStart: () -> Unit,
    onPurchase: (String) -> Unit,
) {
    val strings = LocalTodayPlayStrings.current
    val paymentsEnabled = BuildConfig.BILLING_VERIFY_ENDPOINT.trim().startsWith("https://")
    fun purchase(productId: String, name: String) {
        if (!paymentsEnabled) return
        ProductEventLogger.track("premium_purchase_click", mapOf("productId" to productId, "pack" to name))
        onPurchase(productId)
    }

    PaperBackground {
        Column(Modifier.fillMaxSize()) {
            CuteTopBar(title = strings.shopTitle, subtitle = strings.shopSubtitle, onBack = onBack, action = "心")
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val horizontalPadding = if (maxWidth < 360.dp) 14.dp else 22.dp
                LazyColumn(
                    modifier = Modifier
                        .widthIn(max = 760.dp)
                        .align(Alignment.TopCenter),
                    contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        PremiumHero()
                    }
                    item {
                        TicketCard {
                            Text(strings.billingReadyTitle, style = MaterialTheme.typography.titleLarge, color = InkBlack)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                strings.billingReadyBody,
                                style = MaterialTheme.typography.bodyMedium,
                                color = WarmGray,
                            )
                        }
                    }
                    item {
                        SectionHeader("01", strings.premiumRoutesTitle, strings.premiumRoutesEnglish)
                        Spacer(Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            PaidPackCard(
                                title = strings.multiRoutePackTitle,
                                price = strings.oneTimePurchase,
                                body = strings.multiRoutePackBody,
                                paymentsEnabled = paymentsEnabled,
                                onClick = { purchase(TodayPlayProducts.PREMIUM_ITINERARY_ONCE, strings.multiRoutePackTitle) },
                            )
                            PaidPackCard(
                                title = strings.anniversaryRouteTitle,
                                price = strings.oneTimePurchase,
                                body = strings.anniversaryRouteBody,
                                paymentsEnabled = paymentsEnabled,
                                onClick = { purchase(TodayPlayProducts.PREMIUM_ITINERARY_ONCE, strings.anniversaryRouteTitle) },
                            )
                            PaidPackCard(
                                title = strings.cityPackTitle,
                                price = strings.oneTimePurchase,
                                body = strings.cityPackBody,
                                paymentsEnabled = paymentsEnabled,
                                onClick = { purchase(TodayPlayProducts.GLOBAL_CITY_PACK, strings.cityPackTitle) },
                            )
                        }
                    }
                    item {
                        SectionHeader("02", strings.plusTitle, strings.plusEnglish)
                        Spacer(Modifier.height(10.dp))
                        SoftCard {
                            Text(strings.plusProductTitle, style = MaterialTheme.typography.headlineMedium, color = InkBlack)
                            Spacer(Modifier.height(6.dp))
                            PriceLine(strings.subscriptionPrice, strings.playConsolePriceNote)
                            Spacer(Modifier.height(14.dp))
                            strings.plusBenefits.forEach { BenefitLine(it) }
                            Spacer(Modifier.height(16.dp))
                            HeartPrimaryButton(
                                text = if (paymentsEnabled) strings.openWithGooglePlay else strings.paymentUnavailable,
                                enabled = paymentsEnabled,
                                onClick = { purchase(TodayPlayProducts.PLUS_MONTHLY, strings.plusTitle) },
                            )
                        }
                    }
                    item {
                        SectionHeader("03", strings.whyPayTitle, strings.whyPayEnglish)
                        Spacer(Modifier.height(10.dp))
                        TicketCard {
                            Text(strings.whyPayBody, style = MaterialTheme.typography.bodyLarge, color = InkBlack)
                            Spacer(Modifier.height(10.dp))
                            Text(strings.verificationBody, style = MaterialTheme.typography.bodyMedium, color = WarmGray)
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 24.dp)) {
                            GhostButton(text = strings.startFreeRoute, onClick = onStart, modifier = Modifier.weight(1f))
                            GhostButton(text = strings.backHome, onClick = onBack, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumHero() {
    val strings = LocalTodayPlayStrings.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(BlackCherry)
            .border(1.dp, RoseGold.copy(alpha = 0.55f), RoundedCornerShape(24.dp)),
    ) {
        Image(
            painter = painterResource(R.drawable.romantic_ticket),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to GalleryWhite.copy(alpha = 0.12f),
                        0.45f to BlackCherry.copy(alpha = 0.05f),
                        1f to BlackCherry.copy(alpha = 0.8f),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text("PREMIUM QUEST", color = GalleryWhite.copy(alpha = 0.74f), style = MaterialTheme.typography.labelSmall)
            Text(
                strings.premiumHeroTitle,
                color = GalleryWhite,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 29.sp,
                lineHeight = 35.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                strings.premiumHeroBody,
                color = GalleryWhite.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PriceLine(price: String, note: String) {
    Column {
        Text(price, color = CherryPressed, style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(4.dp))
        Text(
            note,
            color = WarmGray,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun BenefitLine(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text("心", color = CherryPressed, modifier = Modifier.width(24.dp), textAlign = TextAlign.Center)
        Text(text, color = InkBlack, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PaidPackCard(
    title: String,
    price: String,
    body: String,
    paymentsEnabled: Boolean,
    onClick: () -> Unit,
) {
    val strings = LocalTodayPlayStrings.current
    TicketCard {
        Text(
            title,
            color = InkBlack,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(4.dp))
        Text(price, color = CherryPressed, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text(body, color = WarmGray, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))
        GhostButton(
            text = if (paymentsEnabled) strings.connectPaymentProduct else strings.paymentUnavailable,
            enabled = paymentsEnabled,
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
