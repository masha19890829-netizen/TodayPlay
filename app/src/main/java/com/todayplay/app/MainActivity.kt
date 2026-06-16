package com.todayplay.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.todayplay.app.auth.GoogleAccountGateway
import com.todayplay.app.billing.PlayBillingGateway
import com.todayplay.app.data.QuestHistoryStore
import com.todayplay.app.data.QuestRepository
import com.todayplay.app.generator.AiQuestGenerator
import com.todayplay.app.localization.LocalTodayPlayLocale
import com.todayplay.app.localization.LocalTodayPlayStrings
import com.todayplay.app.localization.TodayPlayLocale
import com.todayplay.app.localization.systemStrings
import com.todayplay.app.localization.todayPlayStrings
import com.todayplay.app.model.AccountSession
import com.todayplay.app.model.QuestInput
import com.todayplay.app.model.QuestRecord
import com.todayplay.app.ui.screens.CreateQuestScreen
import com.todayplay.app.ui.screens.HomeScreen
import com.todayplay.app.ui.screens.HistoryScreen
import com.todayplay.app.ui.screens.LoadingScreen
import com.todayplay.app.ui.screens.PrivacyScreen
import com.todayplay.app.ui.screens.QuickStartScreen
import com.todayplay.app.ui.screens.QuestResultScreen
import com.todayplay.app.ui.screens.SavedRoutesScreen
import com.todayplay.app.ui.screens.ShareCardScreen
import com.todayplay.app.ui.screens.ShopScreen
import com.todayplay.app.ui.screens.SplashScreen
import com.todayplay.app.ui.theme.TodayPlayTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodayPlayTheme {
                TodayPlayApp()
            }
        }
    }
}

private enum class AppScreen {
    Splash,
    Home,
    QuickStart,
    Saved,
    Create,
    Loading,
    Result,
    ShareCard,
    History,
    Privacy,
    Shop,
}

@Composable
private fun TodayPlayApp() {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    var screen by remember { mutableStateOf(AppScreen.Splash) }
    var selectedLocale by remember { mutableStateOf(TodayPlayLocale.SimplifiedChinese) }
    var accountSession by remember { mutableStateOf<AccountSession?>(null) }
    var accountMessage by remember { mutableStateOf<String?>(null) }
    var accountBusy by remember { mutableStateOf(false) }
    val billingGateway = remember(context) { PlayBillingGateway(context, localeProvider = { selectedLocale }) }
    val accountGateway = remember(context) { GoogleAccountGateway(context) }
    val coroutineScope = rememberCoroutineScope()
    val viewModel: TodayPlayViewModel = viewModel(
        factory = remember(context) {
            TodayPlayViewModel.Factory(
                QuestRepository(
                    store = QuestHistoryStore(context),
                    generator = AiQuestGenerator(BuildConfig.AI_ROUTE_GATEWAY_URL),
                ),
            )
        },
    )
    val systemCopy = systemStrings(selectedLocale)

    LaunchedEffect(billingGateway) {
        billingGateway.connect { message ->
            ProductStatusToast.show(context, message)
        }
    }

    DisposableEffect(billingGateway) {
        onDispose { billingGateway.dispose() }
    }

    fun startGeneration(input: QuestInput, source: String) {
        viewModel.startGeneration(input.copy(localeCode = selectedLocale.code), source)
        screen = AppScreen.Loading
    }

    fun continueAsLocalTester() {
        accountSession = AccountSession.localTester()
        accountMessage = "Local tester profile is active for share attribution."
    }

    fun signInWithGoogle() {
        if (!accountGateway.isConfigured) {
            accountMessage = "Google sign-in needs GOOGLE_WEB_CLIENT_ID in release_config.properties."
            return
        }
        coroutineScope.launch {
            accountBusy = true
            val result = accountGateway.signIn()
            accountSession = result.session ?: accountSession
            accountMessage = result.message
            accountBusy = false
        }
    }

    fun signOutAccount() {
        coroutineScope.launch {
            accountGateway.signOut()
            accountSession = null
            accountMessage = "Signed out on this device."
        }
    }

    BackHandler(enabled = screen != AppScreen.Home && screen != AppScreen.Splash) {
        when (screen) {
            AppScreen.QuickStart,
            AppScreen.Saved,
            AppScreen.Create,
            AppScreen.History,
            AppScreen.Privacy,
            AppScreen.Shop -> screen = AppScreen.Home
            AppScreen.Loading -> {
                viewModel.cancelGeneration()
                screen = AppScreen.Create
            }
            AppScreen.Result -> screen = AppScreen.Create
            AppScreen.ShareCard -> screen = AppScreen.Result
            AppScreen.Splash,
            AppScreen.Home -> Unit
        }
    }

    CompositionLocalProvider(
        LocalTodayPlayLocale provides selectedLocale,
        LocalTodayPlayStrings provides todayPlayStrings(selectedLocale),
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                (fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.985f, animationSpec = tween(260)))
                    .togetherWith(fadeOut(animationSpec = tween(150)) + scaleOut(targetScale = 1.01f, animationSpec = tween(180)))
            },
            label = "app-screen-transition",
        ) { activeScreen ->
        when (activeScreen) {
        AppScreen.Splash -> SplashScreen(onFinished = { screen = AppScreen.Home })
        AppScreen.Home -> HomeScreen(
            selectedLocale = selectedLocale,
            onStart = { screen = AppScreen.Create },
            onQuickStart = { screen = AppScreen.QuickStart },
            onSaved = { screen = AppScreen.Saved },
            onHistory = { screen = AppScreen.History },
            onPrivacy = { screen = AppScreen.Privacy },
            onShop = { screen = AppScreen.Shop },
            onInstantGenerate = { input -> startGeneration(input, "home_instant") },
            accountSession = accountSession,
            googleSignInAvailable = accountGateway.isConfigured,
            accountBusy = accountBusy,
            accountMessage = accountMessage,
            onGoogleSignIn = ::signInWithGoogle,
            onLocalTesterSignIn = ::continueAsLocalTester,
            onSignOut = ::signOutAccount,
        )
        AppScreen.QuickStart -> QuickStartScreen(
            onBack = { screen = AppScreen.Home },
            onGenerate = { input -> startGeneration(input, "quick_start") },
        )
        AppScreen.Saved -> SavedRoutesScreen(
            records = viewModel.history,
            onBack = { screen = AppScreen.Home },
            onOpen = { record ->
                viewModel.openRecord(record)
                screen = AppScreen.Result
            },
            onReplay = { record ->
                startGeneration(record.toReplayInput(selectedLocale.code), "saved_replay")
            },
            onStart = { screen = AppScreen.Home },
        )
        AppScreen.Create -> CreateQuestScreen(
            onBack = { screen = AppScreen.Home },
            onGenerate = { input -> startGeneration(input, "create_quest") },
        )
        AppScreen.Loading -> LoadingScreen(
            generationState = viewModel.generationState,
            onFinished = {
                if (viewModel.latestRecord != null) {
                    viewModel.resetGenerationState()
                    screen = AppScreen.Result
                } else {
                    screen = AppScreen.Create
                }
            },
            onRetry = viewModel::retryGeneration,
            onBack = {
                viewModel.cancelGeneration()
                screen = AppScreen.Create
            },
        )
        AppScreen.Result -> {
            val record = viewModel.latestRecord
            if (record != null) {
                QuestResultScreen(
                    record = record,
                    onUpdateTaskStatus = viewModel::updateTaskStatus,
                    onToggleFeedback = viewModel::toggleFeedback,
                    onPreviewRouteStopReplacement = { stopId ->
                        viewModel.previewRouteStopReplacement(stopId, selectedLocale.code)
                    },
                    onReplaceRouteStop = { stopId -> viewModel.replaceRouteStop(stopId, selectedLocale.code) },
                    onRestoreRouteStop = { viewModel.restoreRouteStop(selectedLocale.code) },
                    onBack = { screen = AppScreen.Create },
                    onShare = {
                        viewModel.trackShareCardOpen()
                        screen = AppScreen.ShareCard
                    },
                    onRegenerate = {
                        viewModel.trackRegenerate()
                        val input = viewModel.latestInput
                        if (input != null) {
                            startGeneration(input, "result_regenerate")
                        } else {
                            screen = AppScreen.Create
                        }
                    },
                    onSave = viewModel::saveLatest,
                    onHome = { screen = AppScreen.Home },
                )
            } else {
                screen = AppScreen.Create
            }
        }
        AppScreen.ShareCard -> {
            val record = viewModel.latestRecord
            if (record != null) {
                ShareCardScreen(
                    record = record,
                    accountSession = accountSession,
                    googleSignInAvailable = accountGateway.isConfigured,
                    accountBusy = accountBusy,
                    accountMessage = accountMessage,
                    onGoogleSignIn = ::signInWithGoogle,
                    onLocalTesterSignIn = ::continueAsLocalTester,
                    onSignOut = ::signOutAccount,
                    onBack = { screen = AppScreen.Result },
                    onRestart = { screen = AppScreen.Create },
                    onHome = { screen = AppScreen.Home },
                )
            } else {
                screen = AppScreen.Create
            }
        }
        AppScreen.History -> HistoryScreen(
            records = viewModel.history,
            onBack = { screen = AppScreen.Home },
            onOpen = { record ->
                viewModel.openRecord(record)
                screen = AppScreen.Result
            },
            onReplay = { record ->
                startGeneration(record.toReplayInput(selectedLocale.code), "history_replay")
            },
            onStart = { screen = AppScreen.Create },
        )
        AppScreen.Privacy -> PrivacyScreen(
            selectedLocale = selectedLocale,
            onLocaleSelected = { selectedLocale = it },
            historyCount = viewModel.history.size,
            onBack = { screen = AppScreen.Home },
            onClearLocalHistory = viewModel::clearLocalHistory,
        )
        AppScreen.Shop -> ShopScreen(
            onBack = { screen = AppScreen.Home },
            onStart = { screen = AppScreen.Create },
            onPurchase = { productId ->
                if (activity == null) {
                    Toast.makeText(context, systemCopy.currentPageCannotPurchase, Toast.LENGTH_SHORT).show()
                } else {
                    billingGateway.launchPurchase(activity, productId) { message ->
                        ProductStatusToast.show(context, message)
                    }
                }
            },
        )
        }
        }
    }
}

private object ProductStatusToast {
    fun show(context: android.content.Context, message: String) {
        if (message.isBlank()) return
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

private fun QuestRecord.toReplayInput(localeCode: String): QuestInput {
    val plan = quest.itineraryPlan
    val replayMoods = (quest.tags + quest.completionKeywords)
        .distinct()
        .ifEmpty { listOf("想散步", "不想走太远") }
        .take(4)
    return QuestInput(
        relationship = quest.relationship,
        city = quest.city ?: plan?.city,
        moods = replayMoods,
        time = quest.duration,
        budget = quest.budget,
        vibe = quest.tags.firstOrNull() ?: "治愈的",
        note = "沿用历史副本《${quest.title}》的关系、城市和节奏，再生成一条新的可玩路线。",
        transportMode = plan?.groupPreference?.mergedTransportMode ?: "地铁/步行",
        localeCode = localeCode,
    )
}
