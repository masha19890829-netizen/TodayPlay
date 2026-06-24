package com.todayplay.app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
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
import com.todayplay.app.generator.CandidateRouteCard
import com.todayplay.app.generator.RouteIntent
import com.todayplay.app.generator.RouteIntentInterpreter
import com.todayplay.app.model.AccountSession
import com.todayplay.app.model.QuestInput
import com.todayplay.app.model.QuestRecord
import com.todayplay.app.ui.components.GhostButton
import com.todayplay.app.ui.components.HeartPrimaryButton
import com.todayplay.app.ui.components.KawaiiChip
import com.todayplay.app.ui.components.PaperBackground
import com.todayplay.app.ui.components.SectionHeader
import com.todayplay.app.ui.components.SoftCard
import com.todayplay.app.ui.components.TicketCard
import com.todayplay.app.ui.theme.BlackCherry
import com.todayplay.app.ui.theme.CherryPressed
import com.todayplay.app.ui.theme.DustPink
import com.todayplay.app.ui.theme.GalleryWhite
import com.todayplay.app.ui.theme.InkBlack
import com.todayplay.app.ui.theme.LineBeige
import com.todayplay.app.ui.theme.RoseGold
import com.todayplay.app.ui.theme.WarmCream
import com.todayplay.app.ui.theme.WarmGray
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    selectedLocale: TodayPlayLocale,
    onStart: () -> Unit,
    onQuickStart: () -> Unit,
    onSaved: () -> Unit,
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
    recentRecords: List<QuestRecord> = emptyList(),
    onReplayRecent: (QuestRecord) -> Unit = {},
) {
    V0971RouteCardHomeExperience(
        locale = selectedLocale,
        onHistory = onHistory,
        onSettings = onPrivacy,
        onSaved = onSaved,
        onGenerate = onInstantGenerate,
        recentRecords = recentRecords,
        onReplayRecent = onReplayRecent,
    )
    return

    val strings = LocalTodayPlayStrings.current
    PaperBackground {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val landscape = maxWidth > maxHeight
            val shortLandscape = landscape && maxHeight < 520.dp
            val wide = maxWidth >= 840.dp && landscape
            val mediumPhone = !wide && !shortLandscape && maxWidth >= 600.dp
            val compact = maxHeight < 760.dp
            val pagePadding = when {
                maxWidth < 360.dp -> 16.dp
                shortLandscape -> 14.dp
                else -> 22.dp
            }
            val bottomNavCompact = shortLandscape || maxHeight < 680.dp
            val useWideFeedColumns = maxWidth >= 1100.dp
            val heroHeight = when {
                shortLandscape -> 132.dp
                wide -> (maxHeight * 0.42f).coerceAtLeast(220.dp).coerceAtMost(340.dp)
                compact -> 188.dp
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
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        HomeDiscoveryTopBar(
                            strings = strings,
                            copy = discoveryCopy,
                            onPrivacy = onPrivacy,
                        )
                        AiIntentComposer(
                            copy = discoveryCopy,
                            relations = relationOptions,
                            onGenerate = onInstantGenerate,
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
                            forceTwoColumns = useWideFeedColumns,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight()
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
                            onSaved = onSaved,
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
                            bottom = if (bottomNavCompact) 12.dp else 22.dp,
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
                                            shortLandscape -> heroHeight
                                            compact -> 188.dp
                                            mediumPhone -> 264.dp
                                            else -> 218.dp
                                        },
                                    ),
                            )
                        }
                        item {
                            AiIntentComposer(
                                copy = discoveryCopy,
                                relations = relationOptions,
                                onGenerate = onInstantGenerate,
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
                                onSaved = onSaved,
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
                        onSaved = onSaved,
                        onPlan = onStart,
                        onHistory = onHistory,
                        onSettings = onPrivacy,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(
                                horizontal = pagePadding,
                                vertical = if (bottomNavCompact) 4.dp else 14.dp,
                            ),
                        compact = bottomNavCompact,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatFirstCompanionIntro(compact: Boolean) {
    val transition = rememberInfiniteTransition(label = "chat-companion-art")
    val drift by transition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "chat-companion-drift",
    )
    val breath by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(tween(2400), RepeatMode.Reverse),
        label = "chat-companion-breath",
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (compact) 86.dp else 112.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        GalleryWhite.copy(alpha = 0.88f),
                        WarmCream.copy(alpha = 0.72f),
                        RoseGold.copy(alpha = 0.18f),
                    ),
                ),
            )
            .border(1.dp, RoseGold.copy(alpha = 0.24f), RoundedCornerShape(22.dp)),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp, end = if (compact) 108.dp else 138.dp),
        ) {
            Text(
                text = if (compact) "先想一版" else "今天我先替你想一版",
                color = InkBlack,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (compact) "路线会浮现" else "说一句状态，路线会浮现。",
                color = WarmGray,
                style = MaterialTheme.typography.bodySmall,
                maxLines = if (compact) 1 else 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Image(
            painter = painterResource(R.drawable.tp_art_home_companion),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(if (compact) 104.dp else 132.dp)
                .graphicsLayer {
                    translationY = drift
                    scaleX = breath
                    scaleY = breath
                    alpha = 0.96f
                },
        )
        if (!compact) {
            RouteCardMiniVisual(
                strategy = "quiet",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 12.dp)
                    .width(160.dp)
                    .height(28.dp),
                alpha = 0.42f,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun V0971RouteCardHomeExperience(
    locale: TodayPlayLocale,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
    onSaved: () -> Unit,
    onGenerate: (QuestInput) -> Unit,
    recentRecords: List<QuestRecord>,
    onReplayRecent: (QuestRecord) -> Unit,
) {
    val strings = LocalTodayPlayStrings.current
    val copy = discoveryHomeCopy(locale)
    val relations = homeRelationOptions(strings)
    val feedItems = remember(strings) { homeDiscoveryFeed(strings) }
    var selectedScenario by rememberSaveable { mutableStateOf("all") }
    var promptOpen by rememberSaveable { mutableStateOf(false) }
    var feedShift by rememberSaveable { mutableIntStateOf(0) }
    var savedRouteKeys by rememberSaveable { mutableStateOf("") }
    val visibleFeed = remember(selectedScenario, feedItems, relations) {
        v0971FilterFeed(feedItems, selectedScenario, relations).ifEmpty { feedItems }
    }
    val shiftedFeed = remember(visibleFeed, feedShift) {
        if (visibleFeed.isEmpty()) {
            visibleFeed
        } else {
            val offset = feedShift.mod(visibleFeed.size)
            visibleFeed.drop(offset) + visibleFeed.take(offset)
        }
    }
    val savedSet = savedRouteKeys.split("|").filter { it.isNotBlank() }.toSet()
    val toggleSaved: (String) -> Unit = { routeId ->
        savedRouteKeys = if (routeId in savedSet) {
            savedSet.filterNot { it == routeId }.joinToString("|")
        } else {
            (savedSet + routeId).joinToString("|")
        }
    }

    PaperBackground {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
        ) {
            val compact = maxWidth < 390.dp || maxHeight < 700.dp
            val wide = maxWidth >= 720.dp
            val landscape = maxWidth > maxHeight
            val pagePadding = when {
                maxWidth < 360.dp -> 16.dp
                landscape -> 18.dp
                else -> 22.dp
            }
            val topFeed = shiftedFeed.take(
                when {
                    wide -> 6
                    compact -> 2
                    else -> 4
                },
            )
            val restFeed = shiftedFeed.drop(topFeed.size).take(if (wide) 8 else 6)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = pagePadding,
                    end = pagePadding,
                    top = pagePadding,
                    bottom = when {
                        promptOpen -> 220.dp
                        compact -> 26.dp
                        else -> 92.dp
                    },
                ),
                verticalArrangement = Arrangement.spacedBy(if (compact) 12.dp else 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    V0971HomeTopBar(
                        city = "同城",
                        onHistory = onHistory,
                        onSettings = onSettings,
                        compact = compact,
                    )
                }
                item {
                    V0971ScenarioChips(
                        selectedScenario = selectedScenario,
                        onSelectedScenario = { selectedScenario = it },
                        onShuffle = { feedShift += 1 },
                        compact = compact,
                    )
                }
                item {
                    V0971WaterfallFeed(
                        items = topFeed,
                        copy = copy,
                        savedRouteKeys = savedSet,
                        onToggleSaved = toggleSaved,
                        onGenerate = onGenerate,
                        forceTwoColumns = true,
                    )
                }
                if (!promptOpen) {
                    item {
                        V0972CompactPromptEntry(
                            text = "自己说一句",
                            hint = "输入一句需求，生成自己的同城路线",
                            onClick = { promptOpen = true },
                        )
                    }
                }
                if (recentRecords.isNotEmpty() && selectedScenario == "all") {
                    item {
                        V0971RecentRouteTicket(
                            record = recentRecords.first(),
                            onReplayRecent = onReplayRecent,
                        )
                    }
                }
                if (restFeed.isNotEmpty()) {
                    item {
                        V0971WaterfallFeed(
                            items = restFeed,
                            copy = copy,
                            savedRouteKeys = savedSet,
                            onToggleSaved = toggleSaved,
                            onGenerate = onGenerate,
                            forceTwoColumns = wide || !compact,
                        )
                    }
                }
            }
            if (promptOpen) {
                OneSentencePromptSheet(
                    baseInput = visibleFeed.firstOrNull()?.input ?: relations.first().input,
                    onDismiss = { promptOpen = false },
                    onGenerate = {
                        promptOpen = false
                        onGenerate(it)
                    },
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@Composable
private fun V0972CompactPromptEntry(
    text: String,
    hint: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 860.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(GalleryWhite.copy(alpha = 0.92f))
            .border(1.dp, LineBeige, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = text,
                color = InkBlack,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = hint,
                color = WarmGray,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = "发送",
            color = GalleryWhite,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(CherryPressed)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun V0971HomeTopBar(
    city: String,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
    compact: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 820.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "TodayPlay · $city",
            color = InkBlack,
            style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "历史",
            color = WarmGray,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .clickable { onHistory() }
                .padding(horizontal = 8.dp, vertical = 6.dp),
        )
        Text(
            text = "设置",
            color = WarmGray,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .clickable { onSettings() }
                .padding(horizontal = 8.dp, vertical = 6.dp),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun V0971ScenarioChips(
    selectedScenario: String,
    onSelectedScenario: (String) -> Unit,
    onShuffle: () -> Unit,
    compact: Boolean,
) {
    val chips = listOf(
        "all" to "推荐",
        "date" to "约会",
        "friends" to "朋友",
        "solo" to "独处",
        "rain" to "雨天",
        "budget" to "低预算",
        "less-walk" to "少走路",
    )
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 820.dp),
        horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 8.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        chips.forEach { (key, label) ->
            V0971FilterChip(
                text = label,
                selected = selectedScenario == key,
                onClick = { onSelectedScenario(key) },
            )
        }
        V0971FilterChip(
            text = "换一幕",
            selected = false,
            onClick = onShuffle,
        )
    }
}

@Composable
private fun V0971FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .heightIn(min = 32.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) BlackCherry else GalleryWhite.copy(alpha = 0.78f))
            .border(1.dp, if (selected) BlackCherry else LineBeige, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (selected) GalleryWhite else WarmGray,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun V0971WaterfallFeed(
    items: List<RouteFeedItem>,
    copy: DiscoveryHomeCopy,
    savedRouteKeys: Set<String>,
    onToggleSaved: (String) -> Unit,
    onGenerate: (QuestInput) -> Unit,
    forceTwoColumns: Boolean,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 860.dp),
    ) {
        val useTwoColumns = forceTwoColumns && maxWidth >= 360.dp
        if (useTwoColumns) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items.filterIndexed { index, _ -> index % 2 == 0 }.forEachIndexed { localIndex, item ->
                        QuietRouteCard(
                            item = item,
                            copy = copy,
                            saved = item.id in savedRouteKeys,
                            tall = localIndex % 2 == 0,
                            onToggleSaved = { onToggleSaved(item.id) },
                            onGenerate = { onGenerate(item.input.v0971RouteCardInput(item)) },
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items.filterIndexed { index, _ -> index % 2 == 1 }.forEachIndexed { localIndex, item ->
                        QuietRouteCard(
                            item = item,
                            copy = copy,
                            saved = item.id in savedRouteKeys,
                            tall = localIndex % 2 == 1,
                            onToggleSaved = { onToggleSaved(item.id) },
                            onGenerate = { onGenerate(item.input.v0971RouteCardInput(item)) },
                        )
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items.forEachIndexed { index, item ->
                    QuietRouteCard(
                        item = item,
                        copy = copy,
                        saved = item.id in savedRouteKeys,
                        tall = index % 2 == 0,
                        onToggleSaved = { onToggleSaved(item.id) },
                        onGenerate = { onGenerate(item.input.v0971RouteCardInput(item)) },
                    )
                }
            }
        }
    }
}

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuietRouteCard(
    item: RouteFeedItem,
    copy: DiscoveryHomeCopy,
    saved: Boolean,
    tall: Boolean,
    onToggleSaved: () -> Unit,
    onGenerate: () -> Unit,
) {
    val visualHeight = if (tall) 104.dp else 76.dp
    TicketCard {
        V0971TicketVisual(
            item = item,
            modifier = Modifier
                .fillMaxWidth()
                .height(visualHeight),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = item.title.v0971ShortTitle(),
            color = InkBlack,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = item.input.v0973CityTimeLine(),
            color = WarmGray,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            item.routeStops.take(2).forEachIndexed { index, stop ->
                V0971StopDot(index = index, text = stop)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.v0973QuietPromptLine(),
            color = CherryPressed,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(10.dp))
        V0971MiniAction(
            text = copy.start,
            primary = true,
            onClick = onGenerate,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun V0971RouteTicketCard(
    item: RouteFeedItem,
    copy: DiscoveryHomeCopy,
    saved: Boolean,
    tall: Boolean,
    onToggleSaved: () -> Unit,
    onGenerate: () -> Unit,
) {
    QuietRouteCard(
        item = item,
        copy = copy,
        saved = saved,
        tall = tall,
        onToggleSaved = onToggleSaved,
        onGenerate = onGenerate,
    )
}

@Composable
private fun V0971TicketVisual(item: RouteFeedItem, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(LineBeige)
            .border(1.dp, LineBeige, RoundedCornerShape(8.dp)),
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
                        0f to GalleryWhite.copy(alpha = 0.22f),
                        1f to BlackCherry.copy(alpha = 0.42f),
                    ),
                ),
        )
        Canvas(Modifier.fillMaxSize()) {
            val baseline = size.height * 0.62f
            val first = Offset(size.width * 0.16f, baseline)
            val second = Offset(size.width * 0.50f, size.height * 0.42f)
            val third = Offset(size.width * 0.82f, size.height * 0.55f)
            listOf(first to second, second to third).forEach { (start, end) ->
                drawLine(
                    color = GalleryWhite.copy(alpha = 0.78f),
                    start = start,
                    end = end,
                    strokeWidth = 3.2f,
                    cap = StrokeCap.Round,
                )
            }
            listOf(first, second, third).forEachIndexed { index, point ->
                drawCircle(
                    color = if (index == 0) CherryPressed else RoseGold,
                    radius = if (index == 0) 6.5f else 5.5f,
                    center = point,
                )
                drawCircle(
                    color = GalleryWhite.copy(alpha = 0.9f),
                    radius = 2.2f,
                    center = point,
                )
            }
        }
        Text(
            text = item.imageLabel,
            color = GalleryWhite,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
        )
    }
}

@Composable
private fun V0971StopDot(index: Int, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(GalleryWhite.copy(alpha = 0.78f))
            .border(1.dp, LineBeige, RoundedCornerShape(999.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (index == 0) CherryPressed else RoseGold),
            contentAlignment = Alignment.Center,
        ) {
            Text((index + 1).toString(), color = GalleryWhite, style = MaterialTheme.typography.labelSmall)
        }
        Text(
            text = text,
            color = InkBlack,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 92.dp),
        )
    }
}

@Composable
private fun V0971MiniAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
) {
    Box(
        modifier = modifier
            .heightIn(min = 34.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (primary) CherryPressed else GalleryWhite.copy(alpha = 0.78f))
            .border(1.dp, if (primary) CherryPressed else LineBeige, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (primary) GalleryWhite else WarmGray,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun V0971RecentRouteTicket(
    record: QuestRecord,
    onReplayRecent: (QuestRecord) -> Unit,
) {
    val plan = record.quest.itineraryPlan
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 820.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(GalleryWhite.copy(alpha = 0.74f))
            .border(1.dp, LineBeige, RoundedCornerShape(10.dp))
            .clickable { onReplayRecent(record) }
            .padding(14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text("最近路线", color = InkBlack, style = MaterialTheme.typography.titleSmall, maxLines = 1)
            Text(
                text = listOfNotNull(plan?.title, plan?.city, plan?.estimatedDuration)
                    .filter { it.isNotBlank() }
                    .joinToString(" · ")
                    .ifBlank { record.quest.title },
                color = WarmGray,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun V0971FloatingPromptButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(bottom = 20.dp)
            .widthIn(min = 150.dp)
            .heightIn(min = 42.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(CherryPressed)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = GalleryWhite,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun OneSentencePromptSheet(
    baseInput: QuestInput,
    onDismiss: () -> Unit,
    onGenerate: (QuestInput) -> Unit,
    modifier: Modifier = Modifier,
) {
    V0971SelfPromptPanel(
        baseInput = baseInput,
        onDismiss = onDismiss,
        onGenerate = onGenerate,
        modifier = modifier,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun V0971SelfPromptPanel(
    baseInput: QuestInput,
    onDismiss: () -> Unit,
    onGenerate: (QuestInput) -> Unit,
    modifier: Modifier = Modifier,
) {
    var freeText by rememberSaveable { mutableStateOf("") }
    var selectedChipKeys by rememberSaveable { mutableStateOf("chat|budget") }
    val chips = aiIntentChips()
    val selected = selectedChipKeys.split("|").filter { it.isNotBlank() }.toSet()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(18.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(GalleryWhite.copy(alpha = 0.96f))
            .border(1.dp, LineBeige, RoundedCornerShape(22.dp))
            .padding(18.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "自己说一句",
                    color = InkBlack,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "收起",
                    color = WarmGray,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(onClick = onDismiss)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                )
            }
            OutlinedTextField(
                value = freeText,
                onValueChange = { freeText = it.take(80) },
                placeholder = { Text("今晚两个人，少走路，100 内", color = WarmGray) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                chips.forEach { chip ->
                    V0971FilterChip(
                        text = chip.label,
                        selected = chip.key in selected,
                        onClick = {
                            selectedChipKeys = if (chip.key in selected) {
                                (selected - chip.key).joinToString("|")
                            } else {
                                (selected + chip.key).joinToString("|")
                            }
                        },
                    )
                }
            }
            V0971MiniAction(
                text = "发送",
                primary = true,
                onClick = {
                    val selectedChips = chips.filter { it.key in selected }
                    onGenerate(buildAiIntentInput(baseInput, freeText, selectedChips))
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private data class V0971Scenario(
    val key: String,
    val matcher: (RouteFeedItem, List<HomeRelationOption>) -> Boolean,
)

private fun v0971FilterFeed(
    feedItems: List<RouteFeedItem>,
    selectedScenario: String,
    relations: List<HomeRelationOption>,
): List<RouteFeedItem> {
    val scenarios = listOf(
        V0971Scenario("all") { _, _ -> true },
        V0971Scenario("date") { item, relationOptions ->
            item.scenarioKey in setOf(
                relationOptions.getOrNull(0)?.key,
                relationOptions.getOrNull(1)?.key,
            )
        },
        V0971Scenario("friends") { item, relationOptions -> item.scenarioKey == relationOptions.getOrNull(2)?.key },
        V0971Scenario("solo") { item, relationOptions -> item.scenarioKey == relationOptions.getOrNull(4)?.key },
        V0971Scenario("rain") { item, _ -> item.v0971SearchText().contains("雨") || item.v0971SearchText().contains("室内") },
        V0971Scenario("budget") { item, _ -> item.v0971SearchText().contains("低预算") || item.v0971SearchText().contains("100") || item.v0971SearchText().contains("50") },
        V0971Scenario("less-walk") { item, _ -> item.v0971SearchText().contains("少走") || item.v0971SearchText().contains("轻走") || item.v0971SearchText().contains("慢走") },
    )
    val matcher = scenarios.firstOrNull { it.key == selectedScenario }?.matcher ?: scenarios.first().matcher
    return feedItems.filter { item -> matcher(item, relations) }
}

private fun RouteFeedItem.v0971SearchText(): String {
    return buildString {
        append(title)
        append(reason)
        append(chips.joinToString(""))
        append(routeStops.joinToString(""))
        append(input.moods.joinToString(""))
        append(input.budget)
        append(input.note.orEmpty())
    }
}

private fun QuestInput.v0971MetaLine(): String {
    val cityName = city?.takeIf { it.isNotBlank() } ?: "同城"
    return listOf(cityName, time, budget).filter { it.isNotBlank() }.joinToString(" · ")
}

private fun QuestInput.v0973CityTimeLine(): String {
    val cityName = city?.takeIf { it.isNotBlank() } ?: "同城"
    return listOf(cityName, time).filter { it.isNotBlank() }.joinToString(" · ")
}

private fun RouteFeedItem.v0973QuietPromptLine(): String {
    val relationHint = input.relationship.takeIf { it.isNotBlank() }
    val reasonHint = reason.takeIf { it.isNotBlank() }
    return listOfNotNull(relationHint, reasonHint).joinToString(" · ")
}

private fun QuestInput.v0971MobilityLine(): String {
    return when {
        moods.any { it.contains("不想走") || it.contains("少走") || it.contains("累") } -> "3站 · 少走路"
        transportMode.contains("打车") -> "3站 · 可打车"
        else -> "3站 · 同区轻走"
    }
}

private fun QuestInput.v0971RouteCardInput(item: RouteFeedItem): QuestInput {
    val strategy = v0972StrategyFor(item)
    val cleanNote = note
        ?.lineSequence()
        ?.filterNot { it.trim().startsWith("TP_INTENT_") }
        ?.joinToString("\n")
        ?.takeIf { it.isNotBlank() }
    val signals = (listOfNotNull(city, item.scenarioKey) + item.chips + item.routeStops.take(2))
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .distinct()
        .take(5)
    return copy(
        note = listOfNotNull(
            cleanNote,
            "TP_INTENT_TITLE=${item.title}",
            "TP_INTENT_SUMMARY=${item.reason}",
            "TP_INTENT_STRATEGY=$strategy",
            "TP_INTENT_STRATEGY_LABEL=${item.title.v0971ShortTitle()}",
            "TP_INTENT_SIGNALS=${signals.joinToString("|")}",
            "TP_INTENT_REASON=${item.reason}",
            "TP_INTENT_TRADEOFF=${item.proof}",
            "TP_INTENT_SOURCE=${item.sourceStatus}",
            "TP_INTENT_CARD_ID=${item.id}",
            "V0.9.72 route-card-start=${item.id}",
            "V0.9.73 quiet-card-start=${item.id}",
        ).joinToString("\n"),
    )
}

private fun v0972StrategyFor(item: RouteFeedItem): String {
    val search = item.v0971SearchText().lowercase()
    return when {
        item.id.contains("solo") || item.scenarioKey.contains("solo") -> "quiet"
        item.id.contains("friend") || item.scenarioKey.contains("friend") -> "lively"
        item.id.contains("date") || item.scenarioKey.contains("date") -> "cinema"
        search.contains("rain") || search.contains("indoor") -> "indoor"
        search.contains("100") || search.contains("budget") -> "budget"
        search.contains("less") || search.contains("walk") || search.contains("short") -> "short"
        else -> "surprise"
    }
}

private fun String.v0971ShortTitle(): String {
    return if (length <= 12) this else take(11) + "…"
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChatFirstHomeExperience(
    locale: TodayPlayLocale,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
    onSaved: () -> Unit,
    onGenerate: (QuestInput) -> Unit,
    recentRecords: List<QuestRecord>,
    onReplayRecent: (QuestRecord) -> Unit,
) {
    var rawText by rememberSaveable { mutableStateOf("") }
    var selectedCity by rememberSaveable { mutableStateOf("上海") }
    var selectedChipKeys by rememberSaveable { mutableStateOf("friends|2h|less-walk") }
    var intent by remember { mutableStateOf<RouteIntent?>(null) }
    var cards by remember { mutableStateOf<List<CandidateRouteCard>>(emptyList()) }
    val selectedChips = selectedChipKeys.split("|").filter { it.isNotBlank() }.toSet()
    val quickChips = chatFirstChips()

    fun toggleChip(key: String) {
        selectedChipKeys = if (key in selectedChips) {
            (selectedChips - key).joinToString("|")
        } else {
            (selectedChips + key).joinToString("|")
        }
    }

    fun generateCards() {
        val prompt = rawText.ifBlank { "我在$selectedCity，今晚想轻松安排，不想太累" }
        val nextIntent = RouteIntentInterpreter.interpret(
            rawText = prompt,
            selectedChips = selectedChips,
            selectedCity = selectedCity,
            localeCode = locale.code,
        )
        intent = nextIntent
        cards = RouteIntentInterpreter.buildCandidateCards(nextIntent).take(6)
    }

    PaperBackground {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val wide = maxWidth >= 720.dp
            val compact = maxHeight < 720.dp || maxWidth < 390.dp
            val shortLandscape = maxWidth > maxHeight && maxHeight < 520.dp
            val useWideLayout = wide && !shortLandscape
            val pagePadding = if (maxWidth < 360.dp) 16.dp else 22.dp
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(pagePadding),
                verticalArrangement = Arrangement.spacedBy(if (compact) 12.dp else 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    ChatFirstTopBar(
                        onHistory = onHistory,
                        onSettings = onSettings,
                        onSaved = onSaved,
                        compact = compact,
                    )
                }
                if (useWideLayout && intent != null && cards.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 1080.dp),
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(
                                modifier = Modifier.weight(0.92f),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                ChatFirstComposer(
                                    rawText = rawText,
                                    onRawTextChange = {
                                        rawText = it.take(180)
                                        cards = emptyList()
                                        intent = null
                                    },
                                    selectedCity = selectedCity,
                                    onCitySelected = {
                                        selectedCity = it
                                        cards = emptyList()
                                        intent = null
                                    },
                                    quickChips = quickChips,
                                    selectedChips = selectedChips,
                                    onToggleChip = {
                                        toggleChip(it)
                                        cards = emptyList()
                                        intent = null
                                    },
                                    onGenerateCards = ::generateCards,
                                    compact = compact,
                                )
                                if (recentRecords.isNotEmpty()) {
                                    RecentIntentStrip(
                                        recentRecords = recentRecords,
                                        onReplayRecent = onReplayRecent,
                                    )
                                }
                                ChatFirstRoutePrinciples()
                            }
                            Column(
                                modifier = Modifier.weight(1.08f),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                IntentSummaryCard(intent = intent!!)
                                CandidateResultHeader(cards.size, wide = true)
                                DirectorCutPanel(intent = intent!!, cards = cards, wide = true)
                                CandidateCardGrid(
                                    cards = cards,
                                    wide = true,
                                    onSelect = { card -> onGenerate(card.input) },
                                )
                                ChatRefineBar(
                                    onRefine = { chip ->
                                        val nextText = listOf(rawText.ifBlank { intent?.rawText.orEmpty() }, chip)
                                            .filter { it.isNotBlank() }
                                            .joinToString("，")
                                        val nextChips = selectedChips + chip.refineKey()
                                        val nextIntent = RouteIntentInterpreter.interpret(
                                            rawText = nextText,
                                            selectedChips = nextChips,
                                            selectedCity = selectedCity,
                                            localeCode = locale.code,
                                        )
                                        rawText = nextText
                                        selectedChipKeys = nextChips.joinToString("|")
                                        intent = nextIntent
                                        cards = RouteIntentInterpreter.buildCandidateCards(nextIntent).take(6)
                                    },
                                )
                            }
                        }
                    }
                } else {
                    item {
                        ChatFirstComposer(
                            rawText = rawText,
                            onRawTextChange = {
                                rawText = it.take(180)
                                cards = emptyList()
                                intent = null
                            },
                            selectedCity = selectedCity,
                            onCitySelected = {
                                selectedCity = it
                                cards = emptyList()
                                intent = null
                            },
                            quickChips = quickChips,
                            selectedChips = selectedChips,
                            onToggleChip = {
                                toggleChip(it)
                                cards = emptyList()
                                intent = null
                            },
                            onGenerateCards = ::generateCards,
                            compact = compact,
                        )
                    }
                    if (recentRecords.isNotEmpty() && (intent == null || cards.isEmpty())) {
                        item {
                            RecentIntentStrip(
                                recentRecords = recentRecords,
                                onReplayRecent = onReplayRecent,
                            )
                        }
                    }
                }
                if (!useWideLayout && intent != null && cards.isNotEmpty()) {
                    item {
                        IntentSummaryCard(intent = intent!!)
                    }
                    item {
                        CandidateResultHeader(cards.size, wide = wide)
                    }
                    item {
                        DirectorCutPanel(intent = intent!!, cards = cards, wide = wide)
                    }
                    item {
                        CandidateCardGrid(
                            cards = cards,
                            wide = wide,
                            onSelect = { card -> onGenerate(card.input) },
                        )
                    }
                    item {
                        ChatRefineBar(
                            onRefine = { chip ->
                                val nextText = listOf(rawText.ifBlank { intent?.rawText.orEmpty() }, chip)
                                    .filter { it.isNotBlank() }
                                    .joinToString("，")
                                val nextChips = selectedChips + chip.refineKey()
                                val nextIntent = RouteIntentInterpreter.interpret(
                                    rawText = nextText,
                                    selectedChips = nextChips,
                                    selectedCity = selectedCity,
                                    localeCode = locale.code,
                                )
                                rawText = nextText
                                selectedChipKeys = nextChips.joinToString("|")
                                intent = nextIntent
                                cards = RouteIntentInterpreter.buildCandidateCards(nextIntent).take(6)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentIntentStrip(
    recentRecords: List<QuestRecord>,
    onReplayRecent: (QuestRecord) -> Unit,
) {
    SoftCard(padding = 14.dp) {
        Text(
            text = "继续刚才的今天",
            color = InkBlack,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        recentRecords.take(3).forEach { record ->
            val plan = record.quest.itineraryPlan
            val strategy = plan?.personalizationStrategy
                ?.takeIf { it.isNotBlank() }
                ?: record.quest.relationship
            val meta = listOfNotNull(
                record.quest.city ?: plan?.city,
                record.quest.duration.takeIf { it.isNotBlank() },
                record.quest.budget.takeIf { it.isNotBlank() },
            ).joinToString(" / ")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onReplayRecent(record) }
                    .background(RoseGold.copy(alpha = 0.11f))
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = strategy,
                    color = CherryPressed,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = record.quest.title,
                    color = InkBlack,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = meta,
                    color = WarmGray,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ChatFirstTopBar(
    onHistory: () -> Unit,
    onSettings: () -> Unit,
    onSaved: () -> Unit,
    compact: Boolean,
) {
    if (compact) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 860.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TodayPlay",
                        color = InkBlack,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "用一句话安排今天",
                        color = WarmGray,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                KawaiiChip(text = "设置", selected = false, onClick = onSettings)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                KawaiiChip(text = "历史", selected = false, onClick = onHistory)
                KawaiiChip(text = "收藏", selected = false, onClick = onSaved)
            }
        }
        return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 860.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "TodayPlay",
                color = InkBlack,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "用一句话安排今天",
                color = WarmGray,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        KawaiiChip(text = "历史", selected = false, onClick = onHistory)
        KawaiiChip(text = "收藏", selected = false, onClick = onSaved)
        KawaiiChip(text = "设置", selected = false, onClick = onSettings)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChatFirstComposer(
    rawText: String,
    onRawTextChange: (String) -> Unit,
    selectedCity: String,
    onCitySelected: (String) -> Unit,
    quickChips: List<ChatQuickChip>,
    selectedChips: Set<String>,
    onToggleChip: (String) -> Unit,
    onGenerateCards: () -> Unit,
    compact: Boolean,
) {
    SoftCard(padding = if (compact) 14.dp else 18.dp) {
        val compactChipKeys = setOf("date", "friends", "solo", "less-walk", "movie", "low-budget")
        val visibleQuickChips = if (compact) {
            quickChips.filter { it.key in compactChipKeys }
        } else {
            quickChips
        }
        if (!compact) {
            ChatFirstCompanionIntro(compact = false)
            Spacer(Modifier.height(14.dp))
        }
        Text(
            text = "今天想怎么玩？",
            color = InkBlack,
            style = if (compact) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(if (compact) 4.dp else 6.dp))
        Text(
            text = "说一句需求，我会先理解你的城市、关系、预算和体力，再给几种可选玩法。",
            color = WarmGray,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = if (compact) 1 else 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(if (compact) 10.dp else 14.dp))
        OutlinedTextField(
            value = rawText,
            onValueChange = onRawTextChange,
            placeholder = {
                Text(
                    if (compact) "今晚两人少走路" else "比如：今晚两个人，少走路",
                    color = WarmGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = if (compact) 1 else 3,
            maxLines = if (compact) 1 else 4,
            shape = RoundedCornerShape(20.dp),
            textStyle = (if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge).copy(color = InkBlack),
        )
        Spacer(Modifier.height(if (compact) 8.dp else 12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 8.dp),
            verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 8.dp),
        ) {
            listOf("上海", "深圳", "东京").forEach { city ->
                KawaiiChip(
                    text = city,
                    selected = city == selectedCity,
                    onClick = { onCitySelected(city) },
                )
            }
            KawaiiChip(text = "更多城市待验证", selected = false, onClick = {})
        }
        Spacer(Modifier.height(if (compact) 7.dp else 10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 8.dp),
            verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 8.dp),
        ) {
            visibleQuickChips.forEach { chip ->
                KawaiiChip(
                    text = chip.label,
                    selected = chip.key in selectedChips,
                    onClick = { onToggleChip(chip.key) },
                )
            }
        }
        Spacer(Modifier.height(if (compact) 10.dp else 14.dp))
        HeartPrimaryButton(
            text = "生成今天的玩法",
            onClick = onGenerateCards,
        )
    }
}

@Composable
private fun IntentSummaryCard(intent: RouteIntent) {
    TicketCard {
        Text("我理解的是", color = CherryPressed, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "${intent.city} / ${intent.relationship} / ${intent.timeBudget} / ${intent.moneyBudget}",
            color = InkBlack,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "${intent.primaryGoal}，${intent.mobility}，${intent.indoorPreference}",
            color = WarmGray,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DirectorCutPanel(
    intent: RouteIntent,
    cards: List<CandidateRouteCard>,
    wide: Boolean,
) {
    val transition = rememberInfiniteTransition(label = "director-cut-motion")
    val pulse by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.42f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "director-cut-pulse",
    )
    SoftCard(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = if (wide) 860.dp else 640.dp),
        padding = 14.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Canvas(
                modifier = Modifier
                    .size(width = 54.dp, height = 44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BlackCherry.copy(alpha = 0.94f)),
            ) {
                val stripeHeight = size.height / 5f
                repeat(5) { index ->
                    drawRect(
                        color = if (index % 2 == 0) GalleryWhite.copy(alpha = 0.82f) else RoseGold.copy(alpha = 0.68f),
                        topLeft = Offset(0f, index * stripeHeight),
                        size = androidx.compose.ui.geometry.Size(size.width, stripeHeight * 0.72f),
                    )
                }
                drawCircle(
                    color = DustPink.copy(alpha = pulse),
                    radius = size.minDimension * 0.22f,
                    center = Offset(size.width * 0.72f, size.height * 0.62f),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "导演剪辑",
                    color = InkBlack,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${intent.city} / ${intent.relationship} / ${intent.primaryGoal}",
                    color = WarmGray,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = "${cards.size} CUTS",
                color = CherryPressed,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(12.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            cards.take(if (wide) 6 else 4).forEachIndexed { index, card ->
                val alpha by animateFloatAsState(
                    targetValue = 0.78f + index * 0.035f,
                    animationSpec = tween(260),
                    label = "director-chip-alpha",
                )
                Text(
                    text = "Act ${index + 1} · ${card.strategyLabel}",
                    color = if (index == 0) GalleryWhite else CherryPressed,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (index == 0) CherryPressed.copy(alpha = alpha) else RoseGold.copy(alpha = 0.18f))
                        .border(1.dp, if (index == 0) CherryPressed else LineBeige, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "先看几种过法，选中哪张，结果页就继承那张卡的站点、顺序和理由。",
            color = WarmGray,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CandidateCardGrid(
    cards: List<CandidateRouteCard>,
    wide: Boolean,
    onSelect: (CandidateRouteCard) -> Unit,
) {
    var revealedCount by remember(cards) { mutableIntStateOf(0) }
    LaunchedEffect(cards) {
        revealedCount = 0
        cards.indices.forEach { index ->
            delay(75)
            revealedCount = index + 1
        }
    }
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = if (wide) 860.dp else 640.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        cards.forEachIndexed { index, card ->
            AnimatedVisibility(
                visible = index < revealedCount,
                enter = fadeIn(tween(180)) + slideInVertically(
                    animationSpec = tween(220),
                    initialOffsetY = { it / 5 },
                ),
            ) {
                CandidateRouteCardView(
                    card = card,
                    modifier = if (wide) {
                        Modifier.widthIn(min = 280.dp, max = 420.dp)
                    } else {
                        Modifier.fillMaxWidth()
                    },
                    onSelect = { onSelect(card) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CandidateRouteCardView(
    card: CandidateRouteCard,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit,
) {
    TicketCard(modifier = modifier.clickable { onSelect() }) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            KawaiiChip(text = card.strategyLabel, selected = true, onClick = {})
            card.evidenceSignals.take(3).forEach { signal ->
                KawaiiChip(text = signal, selected = false, onClick = {})
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = card.title,
            color = InkBlack,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = card.subtitle,
            color = WarmGray,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(10.dp))
        RouteCardMiniVisual(
            strategy = card.strategy,
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = card.stopPreview.joinToString("  /  "),
            color = InkBlack,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = card.whyThisFits,
            color = WarmGray,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            listOf(card.estimatedDuration, card.budgetLabel, card.mobilityLabel).forEach { label ->
                KawaiiChip(text = label, selected = false, onClick = {})
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = card.tradeoff,
            color = CherryPressed,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = card.sourceNote,
            color = WarmGray,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(10.dp))
        HeartPrimaryButton(text = "按这版生成", onClick = onSelect)
    }
}

@Composable
private fun CandidateResultHeader(count: Int, wide: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = if (wide) 860.dp else 640.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "为你生成了 $count 种今天的过法",
            color = InkBlack,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "每张卡会把选择策略带进路线，不只是换标题。",
            color = WarmGray,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ChatFirstRoutePrinciples() {
    SoftCard(padding = 14.dp) {
        Text(
            text = "生成前先守三条底线",
            color = InkBlack,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(8.dp))
        listOf("只选同城样例点", "按你选的卡片策略排序", "不伪造热度评分营业状态").forEach { line ->
            Text(
                text = "· $line",
                color = WarmGray,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ChatFirstEmptyPreviewPanel() {
    SoftCard(padding = 16.dp) {
        Text(
            text = "生成后会出现什么？",
            color = InkBlack,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(10.dp))
        RouteCardMiniVisual(
            strategy = "fit",
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            alpha = 0.7f,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "我会给出最贴合、更安静、低预算、少走路、室内优先等路线卡。选哪张，结果页就继承哪张的标题、站点顺序和理由。",
            color = WarmGray,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun RouteCardMiniVisual(
    strategy: String,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
) {
    val color = when (strategy) {
        "quiet" -> RoseGold
        "lively" -> CherryPressed
        "budget" -> WarmGray
        "short" -> DustPink
        "indoor" -> RoseGold
        "cinema" -> BlackCherry
        "surprise" -> BlackCherry
        else -> InkBlack
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(GalleryWhite.copy(alpha = 0.62f * alpha))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == 1) 14.dp else 10.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(color.copy(alpha = 0.78f * alpha)),
            )
            if (index < 2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(color.copy(alpha = 0.28f * alpha)),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChatRefineBar(onRefine: (String) -> Unit) {
    SoftCard(padding = 14.dp) {
        Text("再改一句", color = InkBlack, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("更安静", "少走路", "更便宜", "改室内", "更热闹").forEach { label ->
                KawaiiChip(text = label, selected = false, onClick = { onRefine(label) })
            }
        }
    }
}

private data class ChatQuickChip(
    val key: String,
    val label: String,
)

private fun chatFirstChips(): List<ChatQuickChip> = listOf(
    ChatQuickChip("date", "约会"),
    ChatQuickChip("friends", "朋友"),
    ChatQuickChip("solo", "独处"),
    ChatQuickChip("rainy", "下雨天"),
    ChatQuickChip("2h", "2 小时"),
    ChatQuickChip("low-budget", "低预算"),
    ChatQuickChip("less-walk", "少走路"),
    ChatQuickChip("photo", "拍照"),
    ChatQuickChip("movie", "时光电影"),
    ChatQuickChip("chat", "想聊天"),
)

private fun String.refineKey(): String {
    return when (this) {
        "更安静" -> "quiet"
        "少走路" -> "less-walk"
        "更便宜" -> "low-budget"
        "改室内" -> "rainy"
        "更热闹" -> "lively"
        else -> this
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
private fun AiIntentComposer(
    copy: DiscoveryHomeCopy,
    relations: List<HomeRelationOption>,
    onGenerate: (QuestInput) -> Unit,
) {
    var freeText by rememberSaveable { mutableStateOf("") }
    var selectedRelationKey by rememberSaveable { mutableStateOf(relations.firstOrNull()?.key.orEmpty()) }
    var selectedChipKeys by rememberSaveable { mutableStateOf("low-pressure|chat") }
    var pendingInput by remember { mutableStateOf<QuestInput?>(null) }
    val selectedChips = selectedChipKeys.split("|").filter { it.isNotBlank() }.toSet()
    val relation = relations.firstOrNull { it.key == selectedRelationKey } ?: relations.first()
    val quickChips = aiIntentChips()
    val confirmationRequester = remember { BringIntoViewRequester() }
    val draftInput = buildAiIntentInput(
        base = relation.input,
        freeText = freeText,
        selectedChips = quickChips.filter { it.key in selectedChips },
    )

    LaunchedEffect(pendingInput) {
        if (pendingInput != null) {
            delay(90)
            confirmationRequester.bringIntoView()
        }
    }

    TicketCard {
        Text(
            "说一句今天想怎么玩",
            color = InkBlack,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "比如：上海，下班后，想轻松聊聊，预算100内。",
            color = WarmGray,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = freeText,
            onValueChange = {
                freeText = it.take(160)
                pendingInput = null
            },
            placeholder = { Text("我在上海，和刚认识的人，下班后不想太累") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            shape = RoundedCornerShape(18.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            relations.take(4).forEach { option ->
                KawaiiChip(
                    text = option.label,
                    selected = option.key == relation.key,
                    onClick = {
                        selectedRelationKey = option.key
                        pendingInput = null
                    },
                )
            }
            quickChips.forEach { chip ->
                KawaiiChip(
                    text = chip.label,
                    selected = chip.key in selectedChips,
                    onClick = {
                        val next = if (chip.key in selectedChips) selectedChips - chip.key else selectedChips + chip.key
                        selectedChipKeys = next.joinToString("|")
                        pendingInput = null
                    },
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        HeartPrimaryButton(
            text = "看看我理解得对不对",
            onClick = { pendingInput = draftInput },
            modifier = Modifier.fillMaxWidth(),
        )
        pendingInput?.let { input ->
            Spacer(Modifier.height(12.dp))
            AiUnderstandingCard(
                input = input,
                onConfirm = { onGenerate(input) },
                onEdit = { pendingInput = null },
                modifier = Modifier.bringIntoViewRequester(confirmationRequester),
            )
        }
    }
}

@Composable
private fun AiUnderstandingCard(
    input: QuestInput,
    onConfirm: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(RoseGold.copy(alpha = 0.13f))
            .border(1.dp, RoseGold.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text("我理解的是", color = CherryPressed, style = MaterialTheme.typography.titleSmall)
        AiUnderstandingLine("城市", input.city ?: "同城路线")
        AiUnderstandingLine("关系", input.relationship)
        AiUnderstandingLine("时间", input.time)
        AiUnderstandingLine("预算", input.budget)
        AiUnderstandingLine("倾向", input.moods.take(3).joinToString(" / "))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            GhostButton(text = "改一句", onClick = onEdit, modifier = Modifier.weight(1f))
            HeartPrimaryButton(text = "就按这个生成", onClick = onConfirm, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun AiUnderstandingLine(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = WarmGray, style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(42.dp))
        Text(value, color = InkBlack, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@OptIn(ExperimentalLayoutApi::class)
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
    BoxWithConstraints(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .background(WarmCream)
            .border(1.dp, RoseGold.copy(alpha = 0.36f), RoundedCornerShape(26.dp)),
    ) {
        val compactHero = maxHeight < 170.dp
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
                .padding(if (compactHero) 14.dp else 18.dp),
        ) {
            if (!compactHero) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(GalleryWhite.copy(alpha = 0.86f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(copy.heroBadge, color = CherryPressed, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(8.dp))
            }
            Text(
                copy.heroTitle,
                color = InkBlack,
                style = if (compactHero) MaterialTheme.typography.titleLarge else MaterialTheme.typography.headlineMedium,
                maxLines = if (compactHero) 1 else 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                copy.heroSubtitle,
                color = WarmGray,
                style = MaterialTheme.typography.bodySmall,
                maxLines = if (compactHero) 1 else 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(if (compactHero) 6.dp else 12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .heightIn(min = if (compactHero) 34.dp else 38.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(CherryPressed)
                        .clickable { onPlanTonight() }
                        .padding(horizontal = 14.dp, vertical = if (compactHero) 6.dp else 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        copy.heroAction,
                        color = GalleryWhite,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                }
                Box(
                    modifier = Modifier
                        .heightIn(min = if (compactHero) 34.dp else 36.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(GalleryWhite.copy(alpha = 0.74f))
                        .border(1.dp, LineBeige, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = if (compactHero) 6.dp else 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        copy.heroProof,
                        color = WarmGray,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
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
                modifier = Modifier.widthIn(min = 104.dp, max = 140.dp),
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
            .heightIn(min = 42.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) CherryPressed else GalleryWhite.copy(alpha = 0.76f))
            .border(1.dp, if (selected) CherryPressed else LineBeige, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = if (selected) GalleryWhite else InkBlack,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
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
                maxLines = 2,
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
            maxLines = 2,
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
            .heightIn(min = 40.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .border(1.dp, if (primary) CherryPressed else LineBeige, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = foreground,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
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
                .widthIn(min = 64.dp, max = 128.dp)
                .heightIn(min = 42.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(GalleryWhite)
                .clickable { onPlanTonight() }
                .padding(horizontal = 14.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                copy.planAction,
                color = CherryPressed,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
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
    compact: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(GalleryWhite.copy(alpha = 0.94f))
            .border(1.dp, LineBeige, RoundedCornerShape(20.dp))
            .padding(
                horizontal = if (compact) 4.dp else 6.dp,
                vertical = if (compact) 4.dp else 6.dp,
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomNavItem(label = copy.bottomHome, icon = copy.bottomHomeIcon, selected = true, onClick = onHome, compact = compact, modifier = Modifier.weight(1f))
        BottomNavItem(label = copy.bottomSaved, icon = copy.bottomSavedIcon, onClick = onSaved, compact = compact, modifier = Modifier.weight(1f))
        BottomNavItem(label = copy.bottomPlan, icon = "+", primary = true, onClick = onPlan, compact = compact, modifier = Modifier.weight(1f))
        BottomNavItem(label = copy.bottomHistory, icon = copy.bottomHistoryIcon, onClick = onHistory, compact = compact, modifier = Modifier.weight(1f))
        BottomNavItem(label = copy.bottomSettings, icon = copy.bottomSettingsIcon, onClick = onSettings, compact = compact, modifier = Modifier.weight(1f))
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
    compact: Boolean = false,
) {
    Column(
        modifier = modifier
            .heightIn(min = if (compact) 36.dp else 50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                when {
                    primary -> CherryPressed
                    selected -> LineBeige.copy(alpha = 0.42f)
                    else -> GalleryWhite.copy(alpha = 0.01f)
                },
            )
            .clickable { onClick() }
            .padding(horizontal = if (compact) 6.dp else 0.dp, vertical = if (compact) 2.dp else 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (compact) {
            Text(
                text = if (primary) "+ $label" else label,
                color = if (primary) GalleryWhite else InkBlack,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        } else {
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
    onSaved: () -> Unit,
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

private data class AiIntentChip(
    val key: String,
    val label: String,
    val mood: String,
)

private fun aiIntentChips() = listOf(
    AiIntentChip("low-pressure", "低压力", "不想走太远"),
    AiIntentChip("chat", "想聊天", "想轻松聊天"),
    AiIntentChip("tired", "有点累", "有点累"),
    AiIntentChip("photo", "想拍照", "想拍好看的照片"),
    AiIntentChip("budget", "100内", "不想花太多钱"),
)

private fun buildAiIntentInput(
    base: QuestInput,
    freeText: String,
    selectedChips: List<AiIntentChip>,
): QuestInput {
    val text = freeText.trim()
    val inferredMoods = inferMoodsFromText(text)
    val chipMoods = selectedChips.map { it.mood }
    val note = text.ifBlank {
        "首页 AI 快捷输入：${selectedChips.joinToString(" / ") { it.label }}。"
    }
    return base.copy(
        city = inferCityFromText(text, base.city),
        moods = (chipMoods + inferredMoods + base.moods.take(1)).distinct().take(5),
        time = inferTimeFromText(text, base.time),
        budget = inferBudgetFromText(text, base.budget),
        vibe = inferVibeFromText(text, base.vibe),
        note = note,
    )
}

private fun inferCityFromText(text: String, fallback: String?): String? {
    val knownCities = listOf("上海", "广州", "杭州", "深圳", "北京", "成都", "南京", "重庆", "苏州", "厦门", "西安")
    return knownCities.firstOrNull { city -> text.contains(city) } ?: fallback
}

private fun inferTimeFromText(text: String, fallback: String): String {
    return when {
        text.contains("半天") -> "半天"
        text.contains("30") -> "30 分钟"
        text.contains("90") -> "90 分钟"
        text.contains("两小时") || text.contains("2小时") || text.contains("2 小时") -> "2 小时"
        text.contains("下班") || text.contains("晚上") -> "90 分钟"
        else -> fallback
    }
}

private fun inferBudgetFromText(text: String, fallback: String): String {
    val number = Regex("(\\d{1,4})\\s*(元|块|rmb|RMB)?").find(text)?.groupValues?.getOrNull(1)
    return when {
        text.contains("免费") || text.contains("花钱少") -> "50 元以内"
        number != null && (text.contains("预算") || text.contains("钱") || text.contains("内")) -> "$number 元以内"
        else -> fallback
    }
}

private fun inferMoodsFromText(text: String): List<String> = buildList {
    if (text.contains("累") || text.contains("不想太累")) add("有点累")
    if (text.contains("聊")) add("想轻松聊天")
    if (text.contains("拍")) add("想拍好看的照片")
    if (text.contains("安静") || text.contains("慢慢")) add("安静一点")
    if (text.contains("不想走") || text.contains("少走")) add("不想走太远")
}

private fun inferVibeFromText(text: String, fallback: String): String {
    return when {
        text.contains("安静") || text.contains("慢慢") || text.contains("累") -> "治愈的"
        text.contains("朋友") || text.contains("组局") -> "轻松的"
        text.contains("拍") || text.contains("电影") -> "电影感"
        else -> fallback
    }
}

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
    val contentStatus: String,
    val sourceStatus: String,
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
            contentStatus = "本地样例",
            sourceStatus = "本地样例 POI",
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
            contentStatus = item.contentStatus,
            sourceStatus = item.sourceStatus,
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
            contentStatus = item.contentStatus,
            sourceStatus = item.sourceStatus,
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
