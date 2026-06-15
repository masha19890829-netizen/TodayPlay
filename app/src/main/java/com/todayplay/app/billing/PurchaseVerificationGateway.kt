package com.todayplay.app.billing

import com.todayplay.app.BuildConfig
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

data class PendingPurchaseVerification(
    val packageName: String,
    val productIds: List<String>,
    val purchaseToken: String,
    val orderId: String?,
    val purchaseTime: Long,
    val purchaseState: Int,
    val isAcknowledged: Boolean,
)

sealed class PurchaseVerificationResult {
    data class Verified(val entitlementKeys: List<String>) : PurchaseVerificationResult()
    data class RequiresBackend(val message: String) : PurchaseVerificationResult()
    data class Failed(val message: String) : PurchaseVerificationResult()
}

interface PurchaseVerificationGateway {
    fun verifyPurchase(
        pendingPurchase: PendingPurchaseVerification,
        onResult: (PurchaseVerificationResult) -> Unit,
    )
}

class BackendPurchaseVerificationGateway(
    private val endpointUrl: String = BuildConfig.BILLING_VERIFY_ENDPOINT,
) : PurchaseVerificationGateway {
    override fun verifyPurchase(
        pendingPurchase: PendingPurchaseVerification,
        onResult: (PurchaseVerificationResult) -> Unit,
    ) {
        val endpoint = endpointUrl.trim()
        if (endpoint.isEmpty()) {
            onResult(
                PurchaseVerificationResult.RequiresBackend(
                    "已捕获购买令牌，正式版需要配置 HTTPS /billing/verify 后端接口校验 Google Play 订单后再发放权益。",
                ),
            )
            return
        }
        Thread {
            onResult(verifyOverNetwork(endpoint, pendingPurchase))
        }.start()
    }

    private fun verifyOverNetwork(
        endpoint: String,
        pendingPurchase: PendingPurchaseVerification,
    ): PurchaseVerificationResult {
        return runCatching {
            val url = URL(endpoint)
            if (url.protocol != "https") {
                return PurchaseVerificationResult.Failed("验单接口必须使用 HTTPS。")
            }
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = VERIFY_CONNECT_TIMEOUT_MS
                readTimeout = VERIFY_READ_TIMEOUT_MS
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }
            val requestBody = pendingPurchase.toJson().toString()
            connection.outputStream.use { stream ->
                stream.write(requestBody.toByteArray(Charsets.UTF_8))
            }
            val responseBody = if (connection.responseCode in 200..299) {
                connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } else {
                connection.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            }
            parseVerificationResponse(connection.responseCode, responseBody)
        }.getOrElse { error ->
            PurchaseVerificationResult.Failed("验单请求失败：${error.message ?: "unknown error"}")
        }
    }

    private fun PendingPurchaseVerification.toJson(): JSONObject {
        return JSONObject()
            .put("packageName", packageName)
            .put("productIds", productIds)
            .put("purchaseToken", purchaseToken)
            .put("orderId", orderId)
            .put("purchaseTime", purchaseTime)
            .put("purchaseState", purchaseState)
            .put("isAcknowledged", isAcknowledged)
    }

    private fun parseVerificationResponse(responseCode: Int, responseBody: String): PurchaseVerificationResult {
        if (responseBody.isBlank()) {
            return PurchaseVerificationResult.Failed("验单接口返回空响应，HTTP $responseCode。")
        }
        val json = JSONObject(responseBody)
        return when (json.optString("status")) {
            "verified" -> {
                val entitlementKeys = buildList {
                    val entitlements = json.optJSONArray("entitlements")
                    if (entitlements != null) {
                        for (index in 0 until entitlements.length()) {
                            entitlements.optJSONObject(index)?.optString("key")
                                ?.takeIf { it.isNotBlank() }
                                ?.let(::add)
                        }
                    }
                }
                PurchaseVerificationResult.Verified(entitlementKeys)
            }
            "failed" -> {
                PurchaseVerificationResult.Failed(json.optString("reason", "验单失败，HTTP $responseCode。"))
            }
            else -> {
                PurchaseVerificationResult.Failed("验单接口返回未知状态，HTTP $responseCode。")
            }
        }
    }

    private companion object {
        const val VERIFY_CONNECT_TIMEOUT_MS = 8_000
        const val VERIFY_READ_TIMEOUT_MS = 10_000
    }
}
