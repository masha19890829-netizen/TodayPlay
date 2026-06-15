package com.todayplay.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.todayplay.app.data.HomeCityThemePack
import com.todayplay.app.R
import com.todayplay.app.data.HomeRouteContentCatalog
import com.todayplay.app.localization.LocalTodayPlayStrings
import com.todayplay.app.localization.LocalizedHomeInspiration
import com.todayplay.app.localization.LocalizedHomeRelation
import com.todayplay.app.localization.TodayPlayLocale
import com.todayplay.app.localization.TodayPlayStrings
import com.todayplay.app.localization.privacyStrings
import com.todayplay.app.model.AccountSession
import com.todayplay.app.model.QuestInput
import com.todayplay.app.ui.components.GhostButton
import com.todayplay.app.ui.components.HeartPrimaryButton
import com.todayplay.app.ui.components.KawaiiChip
import com.todayplay.app.ui.components.PaperBackground
import com.todayplay.app.ui.components.SectionHeader
import com.todayplay.app.ui.components.SoftCard
import com.todayplay.app.ui.components.TicketCard
import com.todayplay.app.ui.theme.CherryPressed
import com.todayplay.app.ui.theme.GalleryWhite
import com.todayplay.app.ui.theme.InkBlack
import com.todayplay.app.ui.theme.LineBeige
import com.todayplay.app.ui.theme.RoseGold
import com.todayplay.app.ui.theme.WarmCream
import com.todayplay.app.ui.theme.WarmGray

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    selectedLocale: TodayPlayLocale,
    onStart: () -> Unit,
    onQuickStart: () -> Unit,
    onHistory: () -> Unit,
    onPrivacy: () -> Unit,
    onShop: () -> Unit,
    onInstantGenerate: (QuestInput) -> Unit,
    accountSession: AccountSession?,
    googleSignInAvailable: Boolean,
    accountBusy: Boolean,
    accountMessage: String?,
    onGoogleSignIn: () -> Unit,
    onLocalTesterSignIn: () -> Unit,
    onSignOut: () -> Unit,
) {
    val strings = LocalTodayPlayStrings.current
    PaperBackground {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val wide = maxWidth >= 840.dp && maxWidth > maxHeight
            val mediumPhone = !wide && maxWidth >= 600.dp
            val compact = maxHeight < 760.dp
            val pagePadding = if (maxWidth < 360.dp) 16.dp else 22.dp
            val heroHeight = when {
                wide -> (maxHeight - pagePadding * 2).coerceAtMost(560.dp)
                compact -> 220.dp
                else -> 280.dp
            }
            val relationOptions = homeRelationOptions(strings)
            var selectedRelation by rememberSaveable { mutableStateOf(relationOptions.first().key) }
            var selectedChannel by rememberSaveable { mutableStateOf(HomeRouteContentCatalog.CHANNEL_TODAY) }
            var selectedScenario by rememberSaveable { mutableStateOf("all") }
            var savedRouteKeys by rememberSaveable { mutableStateOf("") }
            val discoveryCopy = discoveryHomeCopy(selectedLocale)
            val feedItems = homeDiscoveryFeed(strings)
            val channelFeed = feedItems
                .filter { selectedChannel in it.channels }
                .ifEmpty { feedItems }
            val visibleFeed = channelFeed
                .filter { selectedScenario == "all" || it.scenarioKey == selectedScenario }
                .ifEmpty { channelFeed }
            val savedSet = savedRouteKeys.split("|").filter { it.isNotBlank() }.toSet()
            val toggleSaved: (String) -> Unit = { routeId ->
                savedRouteKeys = if (routeId in savedSet) {
                    savedSet.filterNot { it == routeId }.joinToString("|")
                } else {
                    (savedSet + routeId).joinToString("|")
                }
            }

            if (wide) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(pagePadding),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1.25f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        HomeDiscoveryTopBar(
                            strings = strings,
                            copy = discoveryCopy,
                            onPrivacy = onPrivacy,
                        )
                        HomeContentChannelRail(
                            locale = selectedLocale,
                            selectedChannel = selectedChannel,
                            onSelectedChannel = { selectedChannel = it },
                        )
                        HomeScenarioChips(
                            copy = discoveryCopy,
                            relations = relationOptions,
                            selectedScenario = selectedScenario,
                            onSelectedScenario = { selectedScenario = it },
                        )
                        HomeWaterfallFeed(
                            items = visibleFeed.take(8),
                            copy = discoveryCopy,
                            savedRouteKeys = savedSet,
                            onToggleSaved = toggleSaved,
                            onGenerate = onInstantGenerate,
                            forceTwoColumns = true,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(0.75f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        HomeEmotionHeroCard(
                            copy = discoveryCopy,
                            onPlanTonight = onStart,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(heroHeight),
                        )
                        HomeQuickPlanStrip(copy = discoveryCopy, onPlanTonight = onStart)
                        HomeEntryPanel(
                            strings = strings,
                            selectedLocale = selectedLocale,
                            selectedRelation = selectedRelation,
                            onSelectedRelation = { selectedRelation = it },
                            onStart = onStart,
                            onQuickStart = onQuickStart,
                            onHistory = onHistory,
                            onPrivacy = onPrivacy,
                            onShop = onShop,
                            onInstantGenerate = onInstantGenerate,
                            accountSession = accountSession,
                            googleSignInAvailable = googleSignInAvailable,
                            accountBusy = accountBusy,
                            accountMessage = accountMessage,
                            onGoogleSignIn = onGoogleSignIn,
                            onLocalTesterSignIn = onLocalTesterSignIn,
                            onSignOut = onSignOut,
                        )
                        CityThemeCards(strings = strings, onGenerate = onInstantGenerate, wide = true)
                    }
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .statusBarsPadding(),
                        contentPadding = PaddingValues(
                            start = pagePadding,
                            top = pagePadding,
                            end = pagePadding,
                            bottom = 22.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(if (compact) 12.dp else 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        item {
                            HomeDiscoveryTopBar(
                                strings = strings,
                                copy = discoveryCopy,
                                onPrivacy = onPrivacy,
                            )
                        }
                        item {
                            HomeEmotionHeroCard(
                                copy = discoveryCopy,
                                onPlanTonight = onStart,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(
                                        when {
                                            compact -> 188.dp
                                            mediumPhone -> 264.dp
                                            else -> 218.dp
                                        },
                                    ),
                            )
                        }
                        item {
                            HomeContentChannelRail(
                                locale = selectedLocale,
                                selectedChannel = selectedChannel,
                                onSelectedChannel = { selectedChannel = it },
                            )
                        }
                        item {
                            HomeWaterfallFeed(
                                items = visibleFeed.take(if (compact) 6 else 8),
                                copy = discoveryCopy,
                                savedRouteKeys = savedSet,
                                onToggleSaved = toggleSaved,
                                onGenerate = onInstantGenerate,
                                forceTwoColumns = mediumPhone,
                            )
                        }
                        item {
                            HomeScenarioChips(
                                copy = discoveryCopy,
                                relations = relationOptions,
                                selectedScenario = selectedScenario,
                                onSelectedScenario = { selectedScenario = it },
                            )
                        }
                        item {
                            HomeQuickPlanStrip(copy = discoveryCopy, onPlanTonight = onStart)
                        }
                        item {
                            HomeEntryPanel(
                                strings = strings,
                                selectedLocale = selectedLocale,
                                selectedRelation = selectedRelation,
                                onSelectedRelation = { selectedRelation = it },
                                onStart = onStart,
                                onQuickStart = onQuickStart,
                                onHistory = onHistory,
                                onPrivacy = onPrivacy,
                                onShop = onShop,
                                onInstantGenerate = onInstantGenerate,
                                accountSession = accountSession,
                                googleSignInAvailable = googleSignInAvailable,
                                accountBusy = accountBusy,
                                accountMessage = accountMessage,
                                onGoogleSignIn = onGoogleSignIn,
                                onLocalTesterSignIn = onLocalTesterSignIn,
                                onSignOut = onSignOut,
                            )
                        }
                    }
                    HomeBottomNavBar(
                        copy = discoveryCopy,
                        onHome = {},
                        onSaved = onQuickStart,
                        onPlan = onStart,
                        onHistory = onHistory,
                        onSettings = onPrivacy,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(horizontal = pagePadding, vertical = 14.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeEmotionHeroCard(
    copy: DiscoveryHomeCopy,
    onPlanTonight: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "romantic-home-hero")
    val glow by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.42f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "hero-soft-glow",
    )
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3200), RepeatMode.Reverse),
        label = "hero-route-drift",
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .background(WarmCream)
            .border(1.dp, RoseGold.copy(alpha = 0.36f), RoundedCornerShape(26.dp)),
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
                        0f to GalleryWhite.copy(alpha = 0.02f),
                        0.52f to GalleryWhite.copy(alpha = 0.04f),
                        1f to GalleryWhite.copy(alpha = 0.88f),
                    ),
                ),
        )
        Canvas(Modifier.fillMaxSize()) {
            val start = Offset(size.width * (0.18f + drift * 0.02f), size.height * 0.18f)
            val middle = Offset(size.width * 0.50f, size.height * (0.12f - drift * 0.01f))
            val end = Offset(size.width * (0.82f - drift * 0.02f), size.height * 0.20f)
            listOf(start to middle, middle to end).forEach { (a, b) ->
                drawLine(
                    color = GalleryWhite.copy(alpha = 0.22f + glow * 0.18f),
                    start = a,
                    end = b,
                    strokeWidth = 5f,
                    cap = StrokeCap.Round,
                )
            }
            listOf(start, middle, end).forEachIndexed { index, point ->
                drawCircle(
                    color = if (index == 0) CherryPressed.copy(alpha = 0.38f + glow * 0.18f) else RoseGold.copy(alpha = 0.42f),
                    radius = if (index == 1) 7f + glow * 4f else 6f,
                    center = point,
                )
                drawCircle(color = GalleryWhite.copy(alpha = 0.86f), radius = 3f, center = point)
            }
            drawCircle(
                color = CherryPressed.copy(alpha = glow * 0.22f),
                radius = 64f + glow * 18f,
                center = Offset(size.width * 0.82f, size.height * 0.25f),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(18.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(GalleryWhite.copy(alpha = 0.86f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Text(copy.heroBadge, color = CherryPressed, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                copy.heroTitle,
                color = InkBlack,
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                copy.heroSubtitle,
                color = WarmGray,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(CherryPressed)
                        .clickable { onPlanTonight() }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(copy.heroAction, color = GalleryWhite, style = MaterialTheme.typography.labelLarge)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(GalleryWhite.copy(alpha = 0.74f))
                        .border(1.dp, LineBeige, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                ) {
                    Text(copy.heroProof, color = WarmGray, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun HomeDiscoveryTopBar(
    strings: TodayPlayStrings,
    copy: DiscoveryHomeCopy,
    onPrivacy: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                strings.appName,
                style = MaterialTheme.typography.titleLarge,
                color = InkBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                copy.tonightLine,
                style = MaterialTheme.typography.labelMedium,
                color = WarmGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .height(40.dp)
                .widthIn(min = 48.dp, max = 64.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(GalleryWhite.copy(alpha = 0.78f))
                .border(1.dp, LineBeige, RoundedCornerShape(999.dp))
                .clickable { onPrivacy() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                copy.settingsIcon,
                color = InkBlack,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun HomeContentChannelRail(
    locale: TodayPlayLocale,
    selectedChannel: String,
    onSelectedChannel: (String) -> Unit,
) {
    val channels = homeContentChannels(locale)
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    LaunchedEffect(selectedChannel, locale) {
        val index = channels.indexOfFirst { it.key == selectedChannel }
        if (index > 0) {
            val target = with(density) { ((index * 120).dp - 24.dp).roundToPx() }.coerceAtLeast(0)
            scrollState.animateScrollTo(target)
        } else {
            scrollState.animateScrollTo(0)
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(end = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        channels.forEach { channel ->
            ChannelPill(
                text = channel.label,
                selected = selectedChannel == channel.key,
                onClick = { onSelectedChannel(channel.key) },
                modifier = Modifier.width(112.dp),
            )
        }
    }
}

@Composable
private fun ChannelPill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) CherryPressed else GalleryWhite.copy(alpha = 0.76f))
            .border(1.dp, if (selected) CherryPressed else LineBeige, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = if (selected) GalleryWhite else InkBlack,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HomeScenarioChips(
    copy: DiscoveryHomeCopy,
    relations: List<HomeRelationOption>,
    selectedScenario: String,
    onSelectedScenario: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        KawaiiChip(
            text = copy.recommended,
            selected = selectedScenario == "all",
            onClick = { onSelectedScenario("all") },
        )
        relations.forEach { option ->
            KawaiiChip(
                text = option.label,
                selected = selectedScenario == option.key,
                onClick = { onSelectedScenario(option.key) },
            )
        }
    }
}

@Composable
private fun HomeWaterfallFeed(
    items: List<RouteFeedItem>,
    copy: DiscoveryHomeCopy,
    savedRouteKeys: Set<String>,
    onToggleSaved: (String) -> Unit,
    onGenerate: (QuestInput) -> Unit,
    forceTwoColumns: Boolean,
) {
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val useTwoColumns = forceTwoColumns && maxWidth >= 520.dp
        if (useTwoColumns) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items.filterIndexed { index, _ -> index % 2 == 0 }.forEach { item ->
                        RouteFeedCard(
                            item = item,
                            copy = copy,
                            saved = item.id in savedRouteKeys,
                            onToggleSaved = { onToggleSaved(item.id) },
                            onGenerate = { onGenerate(item.input) },
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items.filterIndexed { index, _ -> index % 2 == 1 }.forEach { item ->
                        RouteFeedCard(
                            item = item,
                            copy = copy,
                            saved = item.id in savedRouteKeys,
                            onToggleSaved = { onToggleSaved(item.id) },
                            onGenerate = { onGenerate(item.input) },
                        )
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items.forEach { item ->
                    RouteFeedCard(
                        item = item,
                        copy = copy,
                        saved = item.id in savedRouteKeys,
                        onToggleSaved = { onToggleSaved(item.id) },
                        onGenerate = { onGenerate(item.input) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RouteFeedCard(
    item: RouteFeedItem,
    copy: DiscoveryHomeCopy,
    saved: Boolean,
    onToggleSaved: () -> Unit,
    onGenerate: () -> Unit,
) {
    TicketCard {
        RouteFeedImage(item = item)
        Spacer(Modifier.height(9.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                item.title,
                color = InkBlack,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text("心", color = CherryPressed, style = MaterialTheme.typography.titleSmall)
        }
        Spacer(Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            item.chips.take(2).forEach { chip ->
                MiniPill(text = chip)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            item.reason,
            color = WarmGray,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        FeedRouteProof(item = item)
        Spacer(Modifier.height(9.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            FeedActionButton(
                text = if (saved) copy.saved else copy.save,
                selected = saved,
                onClick = onToggleSaved,
            )
            FeedActionButton(
                text = copy.start,
                primary = true,
                onClick = onGenerate,
            )
            FeedActionButton(
                text = copy.invite,
                onClick = onGenerate,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FeedRouteProof(item: RouteFeedItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(RoseGold.copy(alpha = 0.13f))
            .border(1.dp, RoseGold.copy(alpha = 0.28f), RoundedCornerShape(13.dp))
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                item.proof,
                color = CherryPressed,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(
                "${item.routeStops.size}站",
                color = WarmGray,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Spacer(Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            item.routeStops.take(2).forEachIndexed { index, stop ->
                StopPreviewPill(index = index, stop = stop)
            }
        }
    }
}

@Composable
private fun StopPreviewPill(index: Int, stop: String) {
    Row(
        modifier = Modifier
            .widthIn(max = 210.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(GalleryWhite.copy(alpha = 0.80f))
            .padding(horizontal = 7.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (index == 0) CherryPressed else RoseGold.copy(alpha = 0.82f)),
            contentAlignment = Alignment.Center,
        ) {
            Text((index + 1).toString(), color = GalleryWhite, style = MaterialTheme.typography.labelSmall)
        }
        Text(
            stop,
            color = InkBlack,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
    }
}

@Composable
private fun RouteFeedImage(item: RouteFeedItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(item.imageHeight.coerceAtLeast(156.dp).coerceAtMost(196.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(LineBeige),
    ) {
        Image(
            painter = painterResource(item.imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to GalleryWhite.copy(alpha = 0.06f),
                        0.56f to GalleryWhite.copy(alpha = 0.02f),
                        1f to GalleryWhite.copy(alpha = 0.58f),
                    ),
                ),
        )
        RouteImagePathOverlay(modifier = Modifier.align(Alignment.TopCenter))
        Text(
            "心",
            color = CherryPressed.copy(alpha = 0.72f),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(9.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(9.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(GalleryWhite.copy(alpha = 0.88f))
                .padding(horizontal = 9.dp, vertical = 5.dp),
        ) {
            Text(
                item.imageLabel,
                color = CherryPressed,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun RouteImagePathOverlay(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "route-card-path")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "route-card-pulse",
    )
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(76.dp),
    ) {
        val points = listOf(
            Offset(size.width * 0.18f, size.height * 0.68f),
            Offset(size.width * 0.52f, size.height * 0.24f),
            Offset(size.width * 0.82f, size.height * 0.55f),
        )
        points.zipWithNext().forEach { (start, end) ->
            drawLine(
                color = GalleryWhite.copy(alpha = 0.44f + pulse * 0.16f),
                start = start,
                end = end,
                strokeWidth = 4.5f,
                cap = StrokeCap.Round,
            )
        }
        points.forEachIndexed { index, point ->
            drawCircle(
                color = if (index == 0) CherryPressed else RoseGold,
                radius = if (index == 0) 9f + pulse * 3f else 7f + pulse * 2f,
                center = point,
            )
            drawCircle(
                color = GalleryWhite.copy(alpha = 0.92f),
                radius = 3f,
                center = point,
            )
        }
    }
}

@Composable
private fun MiniPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(GalleryWhite.copy(alpha = 0.75f))
            .border(1.dp, LineBeige, RoundedCornerShape(999.dp))
            .padding(horizontal = 7.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            color = CherryPressed,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun FeedActionButton(
    text: String,
    onClick: () -> Unit,
    selected: Boolean = false,
    primary: Boolean = false,
) {
    val background = when {
        primary -> CherryPressed
        selected -> RoseGold.copy(alpha = 0.82f)
        else -> GalleryWhite.copy(alpha = 0.76f)
    }
    val foreground = if (primary) GalleryWhite else InkBlack
    Box(
        modifier = Modifier
            .height(34.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .border(1.dp, if (primary) CherryPressed else LineBeige, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = foreground,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun HomeQuickPlanStrip(copy: DiscoveryHomeCopy, onPlanTonight: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CherryPressed.copy(alpha = 0.92f))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                copy.planTonight,
                color = GalleryWhite,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                copy.planTonightHint,
                color = GalleryWhite.copy(alpha = 0.78f),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(GalleryWhite)
                .clickable { onPlanTonight() }
                .padding(horizontal = 14.dp, vertical = 9.dp),
        ) {
            Text(copy.planAction, color = CherryPressed, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun HomeBottomNavBar(
    copy: DiscoveryHomeCopy,
    onHome: () -> Unit,
    onSaved: () -> Unit,
    onPlan: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GalleryWhite.copy(alpha = 0.94f))
            .border(1.dp, LineBeige, RoundedCornerShape(20.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomNavItem(label = copy.bottomHome, icon = copy.bottomHomeIcon, selected = true, onClick = onHome, modifier = Modifier.weight(1f))
        BottomNavItem(label = copy.bottomSaved, icon = copy.bottomSavedIcon, onClick = onSaved, modifier = Modifier.weight(1f))
        BottomNavItem(label = copy.bottomPlan, icon = "+", primary = true, onClick = onPlan, modifier = Modifier.weight(1f))
        BottomNavItem(label = copy.bottomHistory, icon = copy.bottomHistoryIcon, onClick = onHistory, modifier = Modifier.weight(1f))
        BottomNavItem(label = copy.bottomSettings, icon = copy.bottomSettingsIcon, onClick = onSettings, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    primary: Boolean = false,
) {
    Column(
        modifier = modifier
            .heightIn(min = 50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                when {
                    primary -> CherryPressed
                    selected -> LineBeige.copy(alpha = 0.42f)
                    else -> GalleryWhite.copy(alpha = 0.01f)
                },
            )
            .clickable { onClick() }
            .padding(vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            icon,
            color = if (primary) GalleryWhite else InkBlack,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
        )
        Text(
            label,
            color = if (primary) GalleryWhite else WarmGray,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun HomeBrandHeader(strings: TodayPlayStrings, compact: Boolean) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "${strings.appName} /",
            style = if (compact) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.displayLarge,
            color = InkBlack,
        )
        Text(
            strings.appEnglishName,
            fontFamily = FontFamily.Serif,
            color = InkBlack,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            strings.homeSummary,
            color = WarmGray,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HomeEntryPanel(
    strings: TodayPlayStrings,
    selectedLocale: TodayPlayLocale,
    selectedRelation: String,
    onSelectedRelation: (String) -> Unit,
    onStart: () -> Unit,
    onQuickStart: () -> Unit,
    onHistory: () -> Unit,
    onPrivacy: () -> Unit,
    onShop: () -> Unit,
    onInstantGenerate: (QuestInput) -> Unit,
    accountSession: AccountSession?,
    googleSignInAvailable: Boolean,
    accountBusy: Boolean,
    accountMessage: String?,
    onGoogleSignIn: () -> Unit,
    onLocalTesterSignIn: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val relationOptions = homeRelationOptions(strings)
    val relation = relationOptions.firstOrNull { it.key == selectedRelation } ?: relationOptions.first()
    val privacyCopy = privacyStrings(selectedLocale)
    SoftCard(modifier = modifier, padding = 18.dp) {
        Text(
            strings.homeQuestion,
            style = MaterialTheme.typography.headlineMedium,
            color = InkBlack,
        )
        Spacer(Modifier.height(12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            relationOptions.forEach { option ->
                KawaiiChip(
                    text = option.label,
                    selected = selectedRelation == option.key,
                    onClick = { onSelectedRelation(option.key) },
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        RouteMiniMap(
            title = relation.subtitle,
            labels = listOf(strings.todayPicksTitle, relation.label, strings.cityThemesTitle),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(14.dp))
        HeartPrimaryButton(text = strings.generateRoute, onClick = { onInstantGenerate(relation.input) })
        Spacer(Modifier.height(10.dp))
        GhostButton(text = strings.mergePreferences, onClick = onStart, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        GhostButton(text = strings.moreIdeas, onClick = onQuickStart, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GhostButton(text = strings.history, onClick = onHistory, modifier = Modifier.weight(1f))
            GhostButton(text = strings.shop, onClick = onShop, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        GhostButton(text = privacyCopy.navLabel, onClick = onPrivacy, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(14.dp))
        AccountAccessPanel(
            accountSession = accountSession,
            googleSignInAvailable = googleSignInAvailable,
            accountBusy = accountBusy,
            accountMessage = accountMessage,
            onGoogleSignIn = onGoogleSignIn,
            onLocalTesterSignIn = onLocalTesterSignIn,
            onSignOut = onSignOut,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            strings.homePromise,
            color = CherryPressed,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AccountAccessPanel(
    accountSession: AccountSession?,
    googleSignInAvailable: Boolean,
    accountBusy: Boolean,
    accountMessage: String?,
    onGoogleSignIn: () -> Unit,
    onLocalTesterSignIn: () -> Unit,
    onSignOut: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(GalleryWhite.copy(alpha = 0.68f))
            .border(1.dp, LineBeige, RoundedCornerShape(18.dp))
            .padding(14.dp),
    ) {
        Text("Account for sharing", color = InkBlack, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        if (accountSession != null) {
            Text(
                accountSession.shareName,
                color = CherryPressed,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(accountSession.providerLabel, color = WarmGray, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(10.dp))
            GhostButton(text = "Sign out", onClick = onSignOut, modifier = Modifier.fillMaxWidth())
        } else {
            HeartPrimaryButton(
                text = if (accountBusy) "Signing in..." else "Continue with Google",
                onClick = onGoogleSignIn,
                enabled = googleSignInAvailable && !accountBusy,
            )
            Spacer(Modifier.height(8.dp))
            GhostButton(
                text = "Use local tester profile",
                onClick = onLocalTesterSignIn,
                modifier = Modifier.fillMaxWidth(),
                enabled = !accountBusy,
            )
            if (!googleSignInAvailable) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Google OAuth is not configured yet.",
                    color = CherryPressed,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        if (!accountMessage.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(accountMessage, color = WarmGray, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TodayInspirationCards(
    strings: TodayPlayStrings,
    onGenerate: (QuestInput) -> Unit,
    wide: Boolean,
) {
    Column {
        SectionHeader("01", strings.todayPicksTitle, strings.todayPicksEnglish)
        Spacer(Modifier.height(10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            homeInspirations(strings).take(if (wide) 5 else 3).forEachIndexed { index, item ->
                TicketCard(modifier = if (wide) Modifier.width(220.dp) else Modifier.fillMaxWidth()) {
                    InspirationImageHeader(index = index, item = item)
                    Spacer(Modifier.height(10.dp))
                    Text(item.title, color = InkBlack, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(3.dp))
                    Text(item.subtitle, color = WarmGray, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(8.dp))
                    RouteMiniMap(
                        title = item.input.city ?: item.input.relationship,
                        labels = item.input.moods.take(3),
                        modifier = Modifier.fillMaxWidth(),
                        compact = true,
                    )
                    Spacer(Modifier.height(10.dp))
                    GhostButton(text = strings.directGenerate, onClick = { onGenerate(item.input) }, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun InspirationImageHeader(index: Int, item: HomeInspiration) {
    val imageRes = when (index % 4) {
        0 -> R.drawable.romantic_date
        1 -> R.drawable.romantic_friend
        2 -> R.drawable.romantic_solo
        else -> R.drawable.romantic_ticket
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(118.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(LineBeige),
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(GalleryWhite.copy(alpha = 0.86f))
                .padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            Text(
                item.input.relationship,
                color = CherryPressed,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CityThemeCards(
    strings: TodayPlayStrings,
    onGenerate: (QuestInput) -> Unit,
    wide: Boolean,
) {
    Column {
        SectionHeader("02", strings.cityThemesTitle, strings.cityThemesEnglish)
        Spacer(Modifier.height(10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            HomeRouteContentCatalog.cityThemePacks.forEach { item ->
                CityThemeCard(
                    item = item,
                    generateText = strings.cityThemeGenerate,
                    onGenerate = onGenerate,
                    modifier = if (wide) Modifier.width(260.dp) else Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CityThemeCard(
    item: HomeCityThemePack,
    generateText: String,
    onGenerate: (QuestInput) -> Unit,
    modifier: Modifier = Modifier,
) {
    TicketCard(modifier = modifier) {
        Text(
            item.city,
            color = CherryPressed,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            item.title,
            color = InkBlack,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        CityThemeRouteMotif(item = item)
        Spacer(Modifier.height(8.dp))
        Text(
            item.subtitle,
            color = WarmGray,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            item.tags.joinToString(" / "),
            color = CherryPressed,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            MiniPill(text = item.mobilityPressure)
            MiniPill(text = item.contentStatus)
            MiniPill(text = item.sourceStatus)
        }
        Spacer(Modifier.height(12.dp))
        GhostButton(text = generateText, onClick = { onGenerate(item.input) }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun CityThemeRouteMotif(item: HomeCityThemePack) {
    val labels = item.tags.take(3).ifEmpty { listOf(item.city) }
    RouteMiniMap(title = item.city, labels = labels, modifier = Modifier.fillMaxWidth(), compact = true)
}

@Composable
private fun RouteMiniMap(
    title: String,
    labels: List<String>,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val visibleLabels = labels.take(3).ifEmpty { listOf(title) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(GalleryWhite.copy(alpha = 0.62f))
            .border(1.dp, RoseGold.copy(alpha = 0.32f), RoundedCornerShape(16.dp))
            .padding(if (compact) 10.dp else 12.dp),
    ) {
        Text(
            title,
            color = InkBlack,
            style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleSmall,
            maxLines = if (compact) 1 else 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(if (compact) 8.dp else 10.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (compact) 42.dp else 58.dp),
        ) {
            val points = listOf(
                Offset(size.width * 0.10f, size.height * 0.62f),
                Offset(size.width * 0.45f, size.height * 0.30f),
                Offset(size.width * 0.88f, size.height * 0.54f),
            )
            points.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = LineBeige,
                    start = start,
                    end = end,
                    strokeWidth = if (compact) 5f else 7f,
                    cap = StrokeCap.Round,
                )
            }
            points.forEachIndexed { index, point ->
                drawCircle(
                    color = if (index == 0) CherryPressed else RoseGold,
                    radius = if (index == 0) 10f else 8f,
                    center = point,
                )
                drawCircle(
                    color = GalleryWhite.copy(alpha = 0.82f),
                    radius = if (index == 0) 4f else 3f,
                    center = point,
                )
            }
        }
        Spacer(Modifier.height(if (compact) 5.dp else 8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            visibleLabels.forEach { label ->
                Text(
                    text = label,
                    color = WarmGray,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private data class HomeRelationOption(
    val key: String,
    val label: String,
    val subtitle: String,
    val input: QuestInput,
)

private data class HomeInspiration(
    val title: String,
    val subtitle: String,
    val input: QuestInput,
)

private data class HomeContentChannel(
    val key: String,
    val label: String,
)

private fun homeRelationOptions(strings: TodayPlayStrings) = listOf(
    relationOption(
        localized = strings.homeRelations[0],
        input = QuestInput(
            relationship = "情侣",
            city = "上海",
            moods = listOf("想浪漫", "不想走太远", "不想花太多钱"),
            time = "90 分钟",
            budget = "100 元以内",
            vibe = "电影感",
            note = "想要一个能马上开始、不会太累的情侣短约。",
            transportMode = "地铁/步行",
        ),
    ),
    relationOption(
        localized = strings.homeRelations[1],
        input = QuestInput(
            relationship = "暧昧中",
            city = "上海",
            moods = listOf("想破冰", "社恐友好", "想拍好看的照片"),
            time = "90 分钟",
            budget = "100 元以内",
            vibe = "有点暧昧",
            note = "轻轻靠近，但不要油腻，也不要强行推进关系。",
            transportMode = "地铁/步行",
        ),
    ),
    relationOption(
        localized = strings.homeRelations[2],
        input = QuestInput(
            relationship = "朋友",
            city = "广州",
            moods = listOf("想搞笑", "想破冰", "不想花太多钱"),
            time = "2 小时",
            budget = "100 元以内",
            vibe = "搞笑的",
            note = "想让见面有点梗，但不要太费力。",
            transportMode = "地铁/步行",
        ),
    ),
    relationOption(
        localized = strings.homeRelations[3],
        input = QuestInput(
            relationship = "家人",
            city = "深圳",
            moods = listOf("亲子友好", "有点累", "不想走太远"),
            time = "半天",
            budget = "300 元以内",
            vibe = "治愈的",
            note = "需要亲子友好、轻松、有备选路线。",
            transportMode = "公交",
        ),
    ),
    relationOption(
        localized = strings.homeRelations[4],
        input = QuestInput(
            relationship = "一个人散心",
            city = "杭州",
            moods = listOf("有点累", "不想走太远"),
            time = "1 小时",
            budget = "50 元以内",
            vibe = "治愈的",
            note = "今天想被温柔安排，但不想被教育。",
            transportMode = "地铁/步行",
        ),
    ),
)

private fun relationOption(localized: LocalizedHomeRelation, input: QuestInput) = HomeRelationOption(
    key = localized.key,
    label = localized.label,
    subtitle = localized.subtitle,
    input = input,
)

private fun homeContentChannels(locale: TodayPlayLocale): List<HomeContentChannel> = when (locale) {
    TodayPlayLocale.SimplifiedChinese -> listOf(
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_TODAY, "今天可做"),
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_DATE, "心动路线"),
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_FRIEND_LOOP, "今晚组局"),
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_SOLO_RESET, "慢慢走"),
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_CITY, "城市包"),
    )
    TodayPlayLocale.TraditionalChinese -> listOf(
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_TODAY, "今天可做"),
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_DATE, "心動路線"),
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_FRIEND_LOOP, "今晚組局"),
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_SOLO_RESET, "慢慢走"),
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_CITY, "城市包"),
    )
    else -> listOf(
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_TODAY, "Today"),
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_DATE, "Date"),
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_FRIEND_LOOP, "Friends"),
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_SOLO_RESET, "Solo"),
        HomeContentChannel(HomeRouteContentCatalog.CHANNEL_CITY, "City packs"),
    )
}

private fun homeInspirations(strings: TodayPlayStrings): List<HomeInspiration> {
    val relations = homeRelationOptions(strings)
    return listOf(
        HomeInspiration(
            title = strings.homeInspirations[0].title,
            subtitle = strings.homeInspirations[0].subtitle,
            input = relations[0].input.copy(
                moods = listOf("有点累", "不想走太远", "想浪漫"),
                time = "90 分钟",
            ),
        ),
        HomeInspiration(
            title = strings.homeInspirations[1].title,
            subtitle = strings.homeInspirations[1].subtitle,
            input = relations[0].input.copy(
                moods = listOf("不想花太多钱", "想浪漫", "想拍好看的照片"),
                budget = "50 元以内",
            ),
        ),
        HomeInspiration(
            title = strings.homeInspirations[2].title,
            subtitle = strings.homeInspirations[2].subtitle,
            input = relations[1].input,
        ),
        HomeInspiration(
            title = strings.homeInspirations[3].title,
            subtitle = strings.homeInspirations[3].subtitle,
            input = relations[2].input.copy(
                city = "广州",
                moods = listOf("想搞笑", "不想花太多钱", "想探店", "想破冰"),
                note = "朋友局要有笑点、低预算和能边走边聊的小任务。",
            ),
        ),
        HomeInspiration(
            title = strings.homeInspirations[4].title,
            subtitle = strings.homeInspirations[4].subtitle,
            input = relations[4].input.copy(
                city = "杭州",
                moods = listOf("有点累", "不想走太远", "想散步", "想拍好看的照片"),
                time = "半天",
                budget = "100 元以内",
                note = "一个人出门，想慢慢恢复一点能量。",
            ),
        ),
    )
}

private data class DiscoveryHomeCopy(
    val tonightLine: String,
    val cityChip: String,
    val settingsIcon: String,
    val heroBadge: String,
    val heroTitle: String,
    val heroSubtitle: String,
    val heroAction: String,
    val heroProof: String,
    val recommended: String,
    val save: String,
    val saved: String,
    val start: String,
    val invite: String,
    val planTonight: String,
    val planTonightHint: String,
    val planAction: String,
    val bottomHome: String,
    val bottomSaved: String,
    val bottomPlan: String,
    val bottomHistory: String,
    val bottomSettings: String,
    val bottomHomeIcon: String,
    val bottomSavedIcon: String,
    val bottomHistoryIcon: String,
    val bottomSettingsIcon: String,
)

private data class RouteFeedItem(
    val id: String,
    val scenarioKey: String,
    val title: String,
    val reason: String,
    val chips: List<String>,
    val routeStops: List<String>,
    val proof: String,
    val channels: Set<String>,
    val imageLabel: String,
    val imageRes: Int,
    val imageHeight: Dp,
    val input: QuestInput,
)

private fun discoveryHomeCopy(locale: TodayPlayLocale): DiscoveryHomeCopy = when (locale) {
    TodayPlayLocale.SimplifiedChinese -> DiscoveryHomeCopy(
        tonightLine = "今晚想出去，就先刷一条能玩的路线",
        cityChip = "城市路线包",
        settingsIcon = "设置",
        heroBadge = "下班后的心动邀请",
        heroTitle = "今晚，别只聊天",
        heroSubtitle = "先看一张图，再选一条能一起走的路线",
        heroAction = "给我一条心动线",
        heroProof = "同城 · 2站 · 可撤退",
        recommended = "推荐",
        save = "收藏",
        saved = "已收藏",
        start = "开始",
        invite = "邀请",
        planTonight = "帮我安排今晚",
        planTonightHint = "只选一个场景，马上出路线",
        planAction = "安排",
        bottomHome = "首页",
        bottomSaved = "收藏",
        bottomPlan = "生成",
        bottomHistory = "历史",
        bottomSettings = "设置",
        bottomHomeIcon = "首",
        bottomSavedIcon = "藏",
        bottomHistoryIcon = "史",
        bottomSettingsIcon = "设",
    )
    TodayPlayLocale.TraditionalChinese -> DiscoveryHomeCopy(
        tonightLine = "今晚想出去，就先刷一條能玩的路線",
        cityChip = "城市路線包",
        settingsIcon = "設定",
        heroBadge = "下班後的心動邀請",
        heroTitle = "今晚，別只聊天",
        heroSubtitle = "先看一張圖，再選一條能一起走的路線",
        heroAction = "給我一條心動線",
        heroProof = "同城 · 2站 · 可撤退",
        recommended = "推薦",
        save = "收藏",
        saved = "已收藏",
        start = "開始",
        invite = "邀請",
        planTonight = "幫我安排今晚",
        planTonightHint = "只選一個場景，馬上出路線",
        planAction = "安排",
        bottomHome = "首頁",
        bottomSaved = "收藏",
        bottomPlan = "生成",
        bottomHistory = "歷史",
        bottomSettings = "設定",
        bottomHomeIcon = "首",
        bottomSavedIcon = "藏",
        bottomHistoryIcon = "史",
        bottomSettingsIcon = "設",
    )
    else -> DiscoveryHomeCopy(
        tonightLine = "Browse something you can actually do tonight",
        cityChip = "City packs",
        settingsIcon = "Set",
        heroBadge = "After-work date spark",
        heroTitle = "Don't just chat tonight",
        heroSubtitle = "Start with a feeling, then pick a route",
        heroAction = "Find a date route",
        heroProof = "Same city · 2 stops",
        recommended = "For you",
        save = "Save",
        saved = "Saved",
        start = "Start",
        invite = "Invite",
        planTonight = "Plan tonight",
        planTonightHint = "Pick one scene and get a route",
        planAction = "Plan",
        bottomHome = "Home",
        bottomSaved = "Saved",
        bottomPlan = "Plan",
        bottomHistory = "History",
        bottomSettings = "Settings",
        bottomHomeIcon = "Home",
        bottomSavedIcon = "Save",
        bottomHistoryIcon = "Hist",
        bottomSettingsIcon = "Set",
    )
}

private fun homeDiscoveryFeed(strings: TodayPlayStrings): List<RouteFeedItem> {
    val relations = homeRelationOptions(strings)
    val inspirations = homeInspirations(strings)
    val cityThemes = HomeRouteContentCatalog.cityThemePacks
    val datePacks = HomeRouteContentCatalog.dateRoutePacks
    val friendPacks = HomeRouteContentCatalog.friendLoopPacks
    val soloPacks = HomeRouteContentCatalog.soloResetPacks
    val imageCycle = listOf(
        R.drawable.romantic_date,
        R.drawable.romantic_friend,
        R.drawable.romantic_solo,
        R.drawable.romantic_ticket,
    )
    val heightCycle = listOf(148.dp, 116.dp, 136.dp, 164.dp)

    val inspirationItems = inspirations.mapIndexed { index, item ->
        val relationIndex = when (index) {
            0, 1 -> 0
            2 -> 1
            3 -> 2
            else -> 4
        }.coerceAtMost(relations.lastIndex)
        RouteFeedItem(
            id = "inspiration-$index",
            scenarioKey = relations[relationIndex].key,
            title = item.title,
            reason = item.subtitle,
            chips = item.input.feedChips(),
            routeStops = HomeRouteContentCatalog.routeStopsFor(item.input),
            proof = HomeRouteContentCatalog.routeProofFor(item.input),
            channels = HomeRouteContentCatalog.inspirationChannels,
            imageLabel = item.input.city ?: relations[relationIndex].label,
            imageRes = imageCycle[index % imageCycle.size],
            imageHeight = heightCycle[index % heightCycle.size],
            input = item.input,
        )
    }

    fun scenarioKeyFor(input: QuestInput): String {
        return when {
            input.relationship.contains("暧昧") -> relations.getOrNull(1)?.key
            input.relationship.contains("情侣") -> relations.getOrNull(0)?.key
            input.relationship.contains("朋友") -> relations.getOrNull(2)?.key
            input.relationship.contains("家") || input.relationship.contains("亲子") -> relations.getOrNull(3)?.key
            input.relationship.contains("一个人") -> relations.getOrNull(4)?.key
            else -> null
        } ?: relations.first().key
    }

    fun routePackItems(
        packs: List<HomeCityThemePack>,
        idPrefix: String,
        channels: Set<String>,
        imageOffset: Int,
    ): List<RouteFeedItem> = packs.mapIndexed { index, item ->
        RouteFeedItem(
            id = "$idPrefix-${item.city}-$index",
            scenarioKey = scenarioKeyFor(item.input),
            title = item.title,
            reason = item.subtitle,
            chips = (listOf(item.city) + item.tags).take(3),
            routeStops = HomeRouteContentCatalog.routeStopsFor(item.input),
            proof = HomeRouteContentCatalog.routeProofFor(item.input),
            channels = channels,
            imageLabel = item.city,
            imageRes = imageCycle[(index + imageOffset) % imageCycle.size],
            imageHeight = heightCycle[(index + imageOffset) % heightCycle.size],
            input = item.input,
        )
    }

    val dateItems = routePackItems(
        packs = datePacks,
        idPrefix = "date",
        channels = HomeRouteContentCatalog.datePackChannels,
        imageOffset = 0,
    )
    val friendItems = routePackItems(
        packs = friendPacks,
        idPrefix = "friend-loop",
        channels = HomeRouteContentCatalog.friendLoopChannels,
        imageOffset = 1,
    )
    val soloItems = routePackItems(
        packs = soloPacks,
        idPrefix = "solo-reset",
        channels = HomeRouteContentCatalog.soloResetChannels,
        imageOffset = 2,
    )

    val cityItems = cityThemes.take(5).mapIndexed { index, item ->
        val scenarioKey = when (index) {
            0 -> relations.getOrNull(2)?.key
            1 -> relations.getOrNull(4)?.key
            2 -> relations.getOrNull(1)?.key
            3 -> relations.getOrNull(0)?.key
            else -> relations.getOrNull(3)?.key
        } ?: relations.first().key
        RouteFeedItem(
            id = "city-${item.city}-$index",
            scenarioKey = scenarioKey,
            title = item.title,
            reason = item.subtitle,
            chips = (listOf(item.city) + item.tags).take(3),
            routeStops = HomeRouteContentCatalog.routeStopsFor(item.input),
            proof = HomeRouteContentCatalog.routeProofFor(item.input),
            channels = HomeRouteContentCatalog.cityPackChannels,
            imageLabel = item.city,
            imageRes = imageCycle[(index + 1) % imageCycle.size],
            imageHeight = heightCycle[(index + 2) % heightCycle.size],
            input = item.input,
        )
    }

    return (
        dateItems.take(1) +
            friendItems.take(1) +
            soloItems.take(1) +
            inspirationItems.take(2) +
            cityItems.take(3) +
            dateItems.drop(1) +
            friendItems.drop(1) +
            soloItems.drop(1) +
            inspirationItems.drop(2)
        ).distinctBy { it.id }
}

private fun QuestInput.feedChips(): List<String> = listOfNotNull(
    time.takeIf { it.isNotBlank() },
    budget.takeIf { it.isNotBlank() },
    relationship.takeIf { it.isNotBlank() },
)

private fun Dp.coerceAtMost(maximumValue: Dp): Dp = if (this > maximumValue) maximumValue else this
