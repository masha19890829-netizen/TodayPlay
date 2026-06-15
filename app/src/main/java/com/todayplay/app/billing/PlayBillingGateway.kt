package com.todayplay.app.billing

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.todayplay.app.BuildConfig
import com.todayplay.app.localization.TodayPlayLocale
import com.todayplay.app.localization.systemStrings
import com.todayplay.app.model.PaidProductKind
import com.todayplay.app.model.TodayPlayProducts

class PlayBillingGateway(
    context: Context,
    private val localeProvider: () -> TodayPlayLocale = { TodayPlayLocale.SimplifiedChinese },
    private val verificationGateway: PurchaseVerificationGateway = BackendPurchaseVerificationGateway(),
) : PurchasesUpdatedListener {
    private val applicationContext = context.applicationContext
    private val productDetailsById = mutableMapOf<String, ProductDetails>()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var messageSink: ((String) -> Unit)? = null

    private val billingClient = BillingClient.newBuilder(applicationContext)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build(),
        )
        .enableAutoServiceReconnection()
        .build()

    fun connect(onMessage: (String) -> Unit = {}) {
        messageSink = onMessage
        if (billingClient.isReady) {
            queryProductDetails(onMessage)
            return
        }
        billingClient.startConnection(
            object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        queryProductDetails(onMessage)
                    } else {
                        onMessage(copy().billingNotReadyPrefix + billingResult.debugMessage)
                    }
                }

                override fun onBillingServiceDisconnected() {
                    onMessage(copy().billingDisconnected)
                }
            },
        )
    }

    fun launchPurchase(activity: Activity, productId: String, onMessage: (String) -> Unit) {
        messageSink = onMessage
        if (!isVerificationEndpointConfigured()) {
            onMessage(copy().paymentNotOpen)
            return
        }
        if (!billingClient.isReady) {
            connect {
                if (billingClient.isReady) {
                    launchPurchase(activity, productId, onMessage)
                } else {
                    onMessage(it)
                }
            }
            return
        }
        val productDetails = productDetailsById[productId]
        if (productDetails == null) {
            queryProductDetails {
                val refreshed = productDetailsById[productId]
                if (refreshed == null) {
                    val strings = copy()
                    onMessage(strings.productMissingPrefix + productId + strings.productMissingSuffix)
                } else {
                    launchWithDetails(activity, refreshed, onMessage)
                }
            }
            return
        }
        launchWithDetails(activity, productDetails, onMessage)
    }

    fun dispose() {
        if (billingClient.isReady) billingClient.endConnection()
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        when (billingResult.responseCode) {
            BillingResponseCode.OK -> {
                val pendingPurchases = purchases.orEmpty()
                if (pendingPurchases.isEmpty()) {
                    emitMessage(copy().purchaseReturnedNoOrder)
                    return
                }
                pendingPurchases.forEach { purchase ->
                    handlePurchase(purchase)
                }
            }
            BillingResponseCode.USER_CANCELED -> {
                emitMessage(copy().purchaseCanceled)
            }
            else -> {
                emitMessage(copy().purchaseFailedPrefix + billingResult.debugMessage)
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        val pendingVerification = PendingPurchaseVerification(
            packageName = applicationContext.packageName,
            productIds = purchase.products,
            purchaseToken = purchase.purchaseToken,
            orderId = purchase.orderId,
            purchaseTime = purchase.purchaseTime,
            purchaseState = purchase.purchaseState,
            isAcknowledged = purchase.isAcknowledged,
        )
        emitMessage(copy().verificationSubmitted)
        verificationGateway.verifyPurchase(pendingVerification) { result ->
            when (result) {
                is PurchaseVerificationResult.Verified -> {
                    val strings = copy()
                    val entitlementSummary = result.entitlementKeys
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString()
                        ?: strings.entitlementFallback
                    emitMessage(strings.verifiedPrefix + entitlementSummary)
                }
                is PurchaseVerificationResult.RequiresBackend -> {
                    emitMessage(result.message + copy().requiresBackendProductPrefix + pendingVerification.productIds.joinToString() + ".")
                }
                is PurchaseVerificationResult.Failed -> {
                    emitMessage(copy().verificationFailedPrefix + result.message)
                }
            }
        }
    }

    private fun queryProductDetails(onMessage: (String) -> Unit = {}) {
        val productGroups = TodayPlayProducts.all.groupBy { product -> billingProductType(product.kind) }
        productDetailsById.clear()
        if (productGroups.isEmpty()) {
            onMessage(copy().queryCompletePrefix + "0/0.")
            return
        }

        var remainingQueries = productGroups.size
        var failureMessage: String? = null

        fun finishOneQuery(message: String? = null) {
            if (message != null && failureMessage == null) {
                failureMessage = message
            }
            remainingQueries -= 1
            if (remainingQueries == 0) {
                val failed = failureMessage
                if (failed == null) {
                    onMessage(copy().queryCompletePrefix + "${productDetailsById.size}/${TodayPlayProducts.all.size}.")
                } else {
                    onMessage(copy().queryFailedPrefix + failed)
                }
            }
        }

        productGroups.forEach { (productType, products) ->
            val productParams = products.map { product ->
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(product.productId)
                    .setProductType(productType)
                    .build()
            }
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productParams)
                .build()

            runCatching {
                billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsResult ->
                    if (billingResult.responseCode == BillingResponseCode.OK) {
                        productDetailsResult.productDetailsList.forEach { details ->
                            productDetailsById[details.productId] = details
                        }
                        finishOneQuery()
                    } else {
                        finishOneQuery(billingResult.debugMessage)
                    }
                }
            }.onFailure { error ->
                finishOneQuery(error.message ?: error::class.java.simpleName)
            }
        }
    }

    private fun billingProductType(kind: PaidProductKind): String {
        return when (kind) {
            PaidProductKind.Subscription -> ProductType.SUBS
            PaidProductKind.OneTime -> ProductType.INAPP
        }
    }

    private fun launchWithDetails(activity: Activity, productDetails: ProductDetails, onMessage: (String) -> Unit) {
        val detailsParamsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)

        val subscriptionOfferToken = productDetails.subscriptionOfferDetails
            ?.firstOrNull()
            ?.offerToken
        if (subscriptionOfferToken != null) {
            detailsParamsBuilder.setOfferToken(subscriptionOfferToken)
        }

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(detailsParamsBuilder.build()))
            .build()
        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (result.responseCode != BillingResponseCode.OK) {
            onMessage(copy().cannotOpenPurchasePrefix + result.debugMessage)
        }
    }

    private fun emitMessage(message: String) {
        val sink = messageSink ?: return
        if (Looper.myLooper() == Looper.getMainLooper()) {
            sink(message)
        } else {
            mainHandler.post { sink(message) }
        }
    }

    private fun isVerificationEndpointConfigured(): Boolean {
        return BuildConfig.BILLING_VERIFY_ENDPOINT.trim().startsWith("https://")
    }

    private fun copy() = systemStrings(localeProvider())
}
