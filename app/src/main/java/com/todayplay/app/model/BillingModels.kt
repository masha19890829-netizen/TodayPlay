package com.todayplay.app.model

enum class PaidProductKind {
    Subscription,
    OneTime,
}

data class PaidProduct(
    val productId: String,
    val title: String,
    val priceLabel: String,
    val kind: PaidProductKind,
    val entitlementKey: String,
    val description: String,
)

object TodayPlayProducts {
    const val PLUS_MONTHLY = "todayplay.plus.monthly"
    const val PREMIUM_ITINERARY_ONCE = "todayplay.itinerary.premium.once"
    const val GLOBAL_CITY_PACK = "todayplay.citypack.global"
    const val PHOTO_POSITION_PACK = "todayplay.photo.positions"

    val all = listOf(
        PaidProduct(
            productId = PLUS_MONTHLY,
            title = "TodayPlay Plus 月卡",
            priceLabel = "Google Play 订阅价",
            kind = PaidProductKind.Subscription,
            entitlementKey = "plus_monthly",
            description = "解锁多候选路线、低拥挤路线、雨天备选、多人偏好平衡、高清通关卡和积分权益。",
        ),
        PaidProduct(
            productId = PREMIUM_ITINERARY_ONCE,
            title = "高级路线规划",
            priceLabel = "单次购买",
            kind = PaidProductKind.OneTime,
            entitlementKey = "premium_itinerary_once",
            description = "生成 3 条候选路线：拍照优先、低预算优先、低拥挤优先。",
        ),
        PaidProduct(
            productId = GLOBAL_CITY_PACK,
            title = "全球城市包",
            priceLabel = "单次购买",
            kind = PaidProductKind.OneTime,
            entitlementKey = "global_city_pack",
            description = "解锁精选城市路线包、节日路线、商家合作权益和运营精选榜单。",
        ),
        PaidProduct(
            productId = PHOTO_POSITION_PACK,
            title = "拍照机位包",
            priceLabel = "单次购买",
            kind = PaidProductKind.OneTime,
            entitlementKey = "photo_position_pack",
            description = "解锁拍照机位、姿势模板、最佳时间和可替代点位。",
        ),
    )
}
