package com.todayplay.app.data

import com.todayplay.app.model.QuestInput

data class CityRoutePackPreview(
    val city: String,
    val stopNames: List<String>,
    val defaultFitCue: String,
    val relationshipCues: Map<String, String> = emptyMap(),
)

data class HomeCityThemePack(
    val city: String,
    val title: String,
    val subtitle: String,
    val tags: List<String>,
    val mobilityPressure: String,
    val contentStatus: String,
    val sourceStatus: String,
    val stopNames: List<String>,
    val fitCue: String,
    val input: QuestInput,
)

object HomeRouteContentCatalog {
    const val CHANNEL_TODAY = "today"
    const val CHANNEL_DATE = "date"
    const val CHANNEL_FRIEND_LOOP = "friend-loop"
    const val CHANNEL_SOLO_RESET = "solo-reset"
    const val CHANNEL_CITY = "city"
    const val CHANNEL_RELATIONSHIP = "relationship"

    val inspirationChannels: Set<String> = setOf(CHANNEL_TODAY, CHANNEL_RELATIONSHIP)
    val datePackChannels: Set<String> = setOf(CHANNEL_TODAY, CHANNEL_DATE, CHANNEL_RELATIONSHIP)
    val friendLoopChannels: Set<String> = setOf(CHANNEL_TODAY, CHANNEL_FRIEND_LOOP, CHANNEL_RELATIONSHIP)
    val soloResetChannels: Set<String> = setOf(CHANNEL_TODAY, CHANNEL_SOLO_RESET, CHANNEL_RELATIONSHIP)
    val cityPackChannels: Set<String> = setOf(CHANNEL_TODAY, CHANNEL_CITY)

    val dateRoutePacks = listOf(
        HomeCityThemePack(
            city = "上海",
            title = "下班 90 分钟心动线",
            subtitle = "公开场所、好开口，适合刚开始认真聊天。",
            tags = listOf("心动", "90 分钟", "公开场所"),
            mobilityPressure = "同区轻走",
            contentStatus = "运营样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("武康路 Citywalk", "外滩黄昏观景线"),
            fitCue = "低压力",
            input = QuestInput(
                relationship = "暧昧中",
                city = "上海",
                moods = listOf("想破冰", "社恐友好", "想浪漫", "不想走太远"),
                time = "90 分钟",
                budget = "100 元以内",
                vibe = "克制浪漫",
                note = "暧昧对象下班后见面，要公开、自然、可提前结束。",
                transportMode = "地铁/步行",
            ),
        ),
        HomeCityThemePack(
            city = "深圳",
            title = "深圳湾日落约会",
            subtitle = "日落、散步和一张不尴尬的合照。",
            tags = listOf("情侣", "日落", "拍照"),
            mobilityPressure = "近距离",
            contentStatus = "运营样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("深圳湾公园日落段", "华侨城创意文化园"),
            fitCue = "光线好",
            input = QuestInput(
                relationship = "情侣",
                city = "深圳",
                moods = listOf("想浪漫", "想拍好看的照片", "不想花太多钱"),
                time = "2 小时",
                budget = "100 元以内",
                vibe = "轻松浪漫",
                note = "情侣想要一条不用太费力但有一点仪式感的日落路线。",
                transportMode = "公共交通/步行",
            ),
        ),
        HomeCityThemePack(
            city = "台北",
            title = "边界感夜散步",
            subtitle = "河岸、甜点和不过分冒进的聊天题。",
            tags = listOf("暧昧", "夜晚", "边界感"),
            mobilityPressure = "短线步行",
            contentStatus = "待核验样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("大稻埕迪化街轻探店线", "淡水河岸黄昏散步线"),
            fitCue = "公开场所",
            input = QuestInput(
                relationship = "暧昧中",
                city = "台北",
                moods = listOf("想破冰", "社恐友好", "想浪漫", "不想走太远"),
                time = "90 分钟",
                budget = "100 元以内",
                vibe = "有点暧昧",
                note = "想轻轻升温，但每一步都要自然、可退、舒服。",
                transportMode = "公共交通/步行",
            ),
        ),
        HomeCityThemePack(
            city = "杭州",
            title = "湖边慢走约会",
            subtitle = "湖岸、书店和甜点，适合安静但不冷场的傍晚。",
            tags = listOf("心动", "湖边", "慢节奏"),
            mobilityPressure = "近距离",
            contentStatus = "运营样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("西湖湖边独处慢走线", "小河直街书店咖啡线"),
            fitCue = "安静好聊",
            input = QuestInput(
                relationship = "情侣",
                city = "杭州",
                moods = listOf("想浪漫", "想散步", "不想走太远", "想拍好看的照片"),
                time = "100 分钟",
                budget = "100 元以内",
                vibe = "安静浪漫",
                note = "情侣傍晚想慢慢走，最好有湖边、书店和可以停下来的小店。",
                transportMode = "公共交通/步行",
            ),
        ),
        HomeCityThemePack(
            city = "西安",
            title = "城墙边黄昏心动线",
            subtitle = "文化感、夜景和一段不赶路的并肩散步。",
            tags = listOf("心动", "黄昏", "城市记忆"),
            mobilityPressure = "短线步行",
            contentStatus = "待核验样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("西安城墙南门慢走线", "大唐不夜城夜景轻逛线"),
            fitCue = "有记忆点",
            input = QuestInput(
                relationship = "暧昧中",
                city = "西安",
                moods = listOf("想破冰", "想浪漫", "想拍好看的照片", "不想走太远"),
                time = "100 分钟",
                budget = "100 元以内",
                vibe = "温柔有故事",
                note = "刚开始靠近的人想要一条有城市记忆、但不太冒进的夜景路线。",
                transportMode = "公共交通/步行",
            ),
        ),
    )

    val friendLoopPacks = listOf(
        HomeCityThemePack(
            city = "广州",
            title = "老街小吃朋友局",
            subtitle = "低预算、好聊天，每一站都有轻任务。",
            tags = listOf("朋友", "小吃", "低预算"),
            mobilityPressure = "同区轻走",
            contentStatus = "运营样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("永庆坊老街小吃线", "沙面岛低强度散步线"),
            fitCue = "好聊天",
            input = QuestInput(
                relationship = "朋友",
                city = "广州",
                moods = listOf("想搞笑", "不想花太多钱", "想探店", "想破冰"),
                time = "2 小时",
                budget = "100 元以内",
                vibe = "搞笑的",
                note = "朋友局要有笑点、低预算和能边走边聊的小任务。",
                transportMode = "地铁/步行",
            ),
        ),
        HomeCityThemePack(
            city = "上海",
            title = "拍照挑战朋友局",
            subtitle = "两站轻走，边拍边笑，不用一个人硬做攻略。",
            tags = listOf("朋友", "拍照", "轻任务"),
            mobilityPressure = "同区轻走",
            contentStatus = "运营样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("武康路 Citywalk", "外滩黄昏观景线"),
            fitCue = "低压力",
            input = QuestInput(
                relationship = "朋友",
                city = "上海",
                moods = listOf("想搞笑", "想拍好看的照片", "不想走太远"),
                time = "2 小时",
                budget = "100 元以内",
                vibe = "有趣的",
                note = "朋友见面不想只吃饭聊天，想要拍照挑战和轻松散步。",
                transportMode = "地铁/步行",
            ),
        ),
        HomeCityThemePack(
            city = "深圳",
            title = "周末半日灵感局",
            subtitle = "先看展感城市线，再去海边收尾。",
            tags = listOf("朋友", "半日", "城市感"),
            mobilityPressure = "近距离",
            contentStatus = "运营样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("华侨城创意文化园", "深圳湾公园日落段"),
            fitCue = "近距离",
            input = QuestInput(
                relationship = "朋友",
                city = "深圳",
                moods = listOf("想探店", "想拍好看的照片", "不想太累"),
                time = "半天",
                budget = "200 元以内",
                vibe = "轻松有趣",
                note = "朋友周末想见面，需要一条不用抢票也能走起来的半日路线。",
                transportMode = "公共交通/步行",
            ),
        ),
        HomeCityThemePack(
            city = "杭州",
            title = "聊天慢组局",
            subtitle = "安静小店和湖边慢走，适合想认真聊聊的朋友。",
            tags = listOf("朋友", "聊天", "低压力"),
            mobilityPressure = "短线步行",
            contentStatus = "运营样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("小河直街书店咖啡线", "西湖湖边独处慢走线"),
            fitCue = "好聊天",
            input = QuestInput(
                relationship = "朋友",
                city = "杭州",
                moods = listOf("想探店", "不想太累", "想散步", "想认真聊天"),
                time = "90 分钟",
                budget = "100 元以内",
                vibe = "安静好聊",
                note = "朋友想见面，但不想只坐着吃饭，需要一条边走边聊的低压力路线。",
                transportMode = "公共交通/步行",
            ),
        ),
        HomeCityThemePack(
            city = "西安",
            title = "夜景拍拍朋友局",
            subtitle = "城墙和夜景两站，适合边走边拍、边聊边笑。",
            tags = listOf("朋友", "夜景", "拍照"),
            mobilityPressure = "短线步行",
            contentStatus = "待核验样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("西安城墙南门慢走线", "大唐不夜城夜景轻逛线"),
            fitCue = "有话题",
            input = QuestInput(
                relationship = "朋友",
                city = "西安",
                moods = listOf("想拍好看的照片", "想搞笑", "想散步", "不想太累"),
                time = "2 小时",
                budget = "100 元以内",
                vibe = "轻松有记忆点",
                note = "朋友临时组局，想要夜景、合照和不会太费力的城市路线。",
                transportMode = "公共交通/步行",
            ),
        ),
    )

    val soloResetPacks = listOf(
        HomeCityThemePack(
            city = "杭州",
            title = "湖边独处疗愈线",
            subtitle = "慢走、书店、安静存档，适合给自己回一点电。",
            tags = listOf("独处", "疗愈", "慢节奏"),
            mobilityPressure = "近距离",
            contentStatus = "运营样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("西湖湖边独处慢走线", "小河直街书店咖啡线"),
            fitCue = "可暂停",
            input = QuestInput(
                relationship = "一个人散心",
                city = "杭州",
                moods = listOf("有点累", "不想走太远", "想散步", "想拍好看的照片"),
                time = "半天",
                budget = "100 元以内",
                vibe = "疗愈的",
                note = "一个人出门，想慢慢恢复一点能量。",
                transportMode = "公共交通/步行",
            ),
        ),
        HomeCityThemePack(
            city = "上海",
            title = "一个人下班放空",
            subtitle = "不社交、不赶路，走完就算今天完成了。",
            tags = listOf("独处", "下班后", "低压力"),
            mobilityPressure = "短线步行",
            contentStatus = "运营样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("武康路 Citywalk", "外滩黄昏观景线"),
            fitCue = "低压力",
            input = QuestInput(
                relationship = "一个人散心",
                city = "上海",
                moods = listOf("有点累", "不想说话", "想散步", "不想走太远"),
                time = "90 分钟",
                budget = "50 元以内",
                vibe = "安静的",
                note = "下班后一个人放空，路线要短、安静、可随时结束。",
                transportMode = "地铁/步行",
            ),
        ),
        HomeCityThemePack(
            city = "台北",
            title = "河岸慢慢走",
            subtitle = "低预算、低打扰，给自己一段不被催的时间。",
            tags = listOf("独处", "河岸", "可暂停"),
            mobilityPressure = "短线步行",
            contentStatus = "待核验样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("大稻埕迪化街轻探店线", "淡水河岸黄昏散步线"),
            fitCue = "可暂停",
            input = QuestInput(
                relationship = "一个人散心",
                city = "台北",
                moods = listOf("有点累", "想散步", "不想花太多钱", "不想被打扰"),
                time = "90 分钟",
                budget = "50 元以内",
                vibe = "安静的",
                note = "一个人想慢慢走，最好有河岸、甜点和可暂停的节奏。",
                transportMode = "公共交通/步行",
            ),
        ),
        HomeCityThemePack(
            city = "广州",
            title = "傍晚放空线",
            subtitle = "老街、树影和低强度散步，把今天慢慢收住。",
            tags = listOf("独处", "放空", "低强度"),
            mobilityPressure = "同区轻走",
            contentStatus = "运营样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("沙面岛低强度散步线", "永庆坊老街小吃线"),
            fitCue = "低打扰",
            input = QuestInput(
                relationship = "一个人散心",
                city = "广州",
                moods = listOf("有点累", "想散步", "不想被打扰", "不想花太多钱"),
                time = "80 分钟",
                budget = "50 元以内",
                vibe = "松弛的",
                note = "一个人下班后想放空，路线要安全、低强度、能随时停下。",
                transportMode = "地铁/步行",
            ),
        ),
        HomeCityThemePack(
            city = "西安",
            title = "城墙边慢走",
            subtitle = "黄昏时慢慢走，给自己留一段有历史感的安静。",
            tags = listOf("独处", "慢走", "有历史感"),
            mobilityPressure = "短线步行",
            contentStatus = "待核验样例",
            sourceStatus = "本地样例 POI",
            stopNames = listOf("西安城墙南门慢走线", "大唐不夜城夜景轻逛线"),
            fitCue = "可暂停",
            input = QuestInput(
                relationship = "一个人散心",
                city = "西安",
                moods = listOf("有点累", "想散步", "想拍好看的照片", "不想走太远"),
                time = "100 分钟",
                budget = "50 元以内",
                vibe = "安静有历史感",
                note = "一个人想慢慢走完一段有城市记忆的路线，不赶路，不被催。",
                transportMode = "公共交通/步行",
            ),
        ),
    )

    val cityThemePacks = listOf(
        friendLoopPacks[0],
        soloResetPacks[0],
        dateRoutePacks[2],
    )

    val allRoutePacks: List<HomeCityThemePack> = dateRoutePacks + friendLoopPacks + soloResetPacks + cityThemePacks

    private val cityRoutePackPreviews = listOf(
        CityRoutePackPreview(
            city = "上海",
            stopNames = listOf("武康路 Citywalk", "外滩黄昏观景线"),
            defaultFitCue = "低压力",
            relationshipCues = mapOf(
                "暧昧" to "公开场所",
                "情侣" to "低压力",
                "朋友" to "低压力",
                "一个人" to "低压力",
            ),
        ),
        CityRoutePackPreview(
            city = "深圳",
            stopNames = listOf("深圳湾公园日落段", "华侨城创意文化园"),
            defaultFitCue = "近距离",
            relationshipCues = mapOf(
                "情侣" to "光线好",
                "朋友" to "近距离",
                "家" to "低强度",
                "亲子" to "低强度",
            ),
        ),
        CityRoutePackPreview(
            city = "广州",
            stopNames = listOf("永庆坊老街小吃线", "沙面岛低强度散步线"),
            defaultFitCue = "好聊天",
            relationshipCues = mapOf("朋友" to "好聊天"),
        ),
        CityRoutePackPreview(
            city = "杭州",
            stopNames = listOf("西湖湖边独处慢走线", "小河直街书店咖啡线"),
            defaultFitCue = "可暂停",
            relationshipCues = mapOf("一个人" to "可暂停"),
        ),
        CityRoutePackPreview(
            city = "台北",
            stopNames = listOf("大稻埕迪化街轻探店线", "淡水河岸黄昏散步线"),
            defaultFitCue = "公开场所",
            relationshipCues = mapOf(
                "暧昧" to "公开场所",
                "一个人" to "可暂停",
            ),
        ),
    )

    fun routeStopsFor(input: QuestInput): List<String> {
        val pack = packFor(input)
        if (pack != null) return pack.stopNames

        val preview = previewFor(input)
        if (preview != null) {
            if (preview.city == "深圳" && input.relationship.contains("家")) {
                return listOf("深圳博物馆历史民俗馆", "深圳湾公园日落段")
            }
            return preview.stopNames
        }
        return listOfNotNull(
            input.city?.takeIf { it.isNotBlank() },
            input.relationship.takeIf { it.isNotBlank() },
        ).ifEmpty { listOf("同城路线脚本") }
    }

    fun routeProofFor(input: QuestInput): String {
        val cityName = input.city?.takeIf { it.isNotBlank() } ?: "同城"
        val pack = packFor(input)
        val preview = previewFor(input)
        val cue = pack?.fitCue
            ?: preview?.relationshipCues
                ?.firstNotNullOfOrNull { (keyword, fitCue) ->
                    fitCue.takeIf { input.relationship.contains(keyword) }
                }
            ?: preview?.defaultFitCue
            ?: fallbackFitCue(input)
        return "$cityName · 同城校验 · $cue"
    }

    private fun packFor(input: QuestInput): HomeCityThemePack? {
        return allRoutePacks.firstOrNull { pack ->
            pack.input.city == input.city &&
                pack.input.relationship == input.relationship &&
                pack.input.note == input.note
        }
    }

    private fun previewFor(input: QuestInput): CityRoutePackPreview? {
        val cityName = input.city.orEmpty()
        return cityRoutePackPreviews.firstOrNull { preview -> cityName.contains(preview.city) }
    }

    private fun fallbackFitCue(input: QuestInput): String {
        return when {
            input.relationship.contains("暧昧") -> "公开场所"
            input.relationship.contains("家") -> "低强度"
            input.relationship.contains("朋友") -> "好聊天"
            input.relationship.contains("一个人") -> "可暂停"
            else -> "低压力"
        }
    }
}
