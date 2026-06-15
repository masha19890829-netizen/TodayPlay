package com.todayplay.app.localization

import com.todayplay.app.model.QuestInput

data class QuickStartStrings(
    val title: String,
    val subtitle: String,
    val sectionTitle: String,
    val sectionEnglish: String,
    val intro: String,
    val generate: String,
    val items: List<QuickStartItemCopy>,
)

data class QuickStartItemCopy(
    val title: String,
    val subtitle: String,
    val tags: List<String>,
    val input: QuestInput,
)

fun quickStartStrings(locale: TodayPlayLocale): QuickStartStrings = when (locale) {
    TodayPlayLocale.SimplifiedChinese -> zhCnQuickStart
    TodayPlayLocale.TraditionalChinese -> zhTwQuickStart
    TodayPlayLocale.English -> enQuickStart
    TodayPlayLocale.Japanese -> jaQuickStart
    TodayPlayLocale.Korean -> koQuickStart
    TodayPlayLocale.Spanish -> esQuickStart
}

private val baseItems = listOf(
    QuickStartItemCopy(
        title = "东京下班后 90 分钟情侣路线",
        subtitle = "黄昏夜景、低压力互动、适合拍封面。",
        tags = listOf("情侣", "东京", "公共交通/步行"),
        input = QuestInput(
            relationship = "情侣",
            city = "东京",
            moods = listOf("有点累", "不想走太远", "想浪漫", "想拍好看的照片"),
            time = "90 分钟",
            budget = "50 元以内",
            vibe = "电影感",
            note = "需要地点、路线、导航、拍照建议和打卡积分。",
            transportMode = "公共交通/步行",
        ),
    ),
    QuickStartItemCopy(
        title = "上海暧昧不尴尬拍照路线",
        subtitle = "先散步再探店，保留边界感。",
        tags = listOf("暧昧", "上海", "拍照"),
        input = QuestInput(
            relationship = "暧昧中",
            city = "上海",
            moods = listOf("想破冰", "社恐友好", "想拍好看的照片", "想探店"),
            time = "2 小时",
            budget = "100 元以内",
            vibe = "有点暧昧",
            note = "想让今天自然一点，轻轻靠近但不尴尬。",
            transportMode = "公共交通/步行",
        ),
    ),
    QuickStartItemCopy(
        title = "巴黎低预算纪念日路线",
        subtitle = "把拍照、晚餐和路线成本控制住。",
        tags = listOf("情侣", "巴黎", "低预算"),
        input = QuestInput(
            relationship = "情侣",
            city = "巴黎",
            moods = listOf("纪念日", "不想花太多钱", "想浪漫", "想吃饭"),
            time = "2 小时",
            budget = "100 元以内",
            vibe = "甜甜的",
            note = "今天想认真纪念一下，但不要很贵。",
            transportMode = "公共交通/步行",
        ),
    ),
    QuickStartItemCopy(
        title = "首尔朋友 Citywalk + 探店路线",
        subtitle = "不只吃饭，要有拍照、挑战和小吃。",
        tags = listOf("朋友", "首尔", "Citywalk"),
        input = QuestInput(
            relationship = "朋友",
            city = "首尔",
            moods = listOf("想搞笑", "想破冰", "不想花太多钱", "想探店"),
            time = "半天",
            budget = "100 元以内",
            vibe = "搞笑的",
            note = "我们想见面，但不想只是坐着吃饭。",
            transportMode = "公共交通/步行",
        ),
    ),
    QuickStartItemCopy(
        title = "新加坡亲子家庭轻松半日游",
        subtitle = "低强度、可室内备选、照顾老人小孩。",
        tags = listOf("家人", "新加坡", "亲子友好"),
        input = QuestInput(
            relationship = "家人",
            city = "新加坡",
            moods = listOf("亲子友好", "有点累", "想散步"),
            time = "半天",
            budget = "300 元以内",
            vibe = "治愈的",
            note = "需要亲子友好、轻松、有备选路线。",
            transportMode = "按地图推荐",
        ),
    ),
    QuickStartItemCopy(
        title = "杭州独处疗愈半日线",
        subtitle = "湖边慢走、书店停靠、把今天安静存档。",
        tags = listOf("自己", "杭州", "治愈"),
        input = QuestInput(
            relationship = "一个人散心",
            city = "杭州",
            moods = listOf("有点累", "不想走太远", "想散步", "想拍好看的照片"),
            time = "半天",
            budget = "100 元以内",
            vibe = "治愈的",
            note = "想一个人出门，但希望路线温柔、轻、可随时坐下。",
            transportMode = "公共交通/步行",
        ),
    ),
    QuickStartItemCopy(
        title = "广州朋友低预算好笑路线",
        subtitle = "老街、小吃、随机挑战，不只坐着吃饭。",
        tags = listOf("朋友", "广州", "低预算"),
        input = QuestInput(
            relationship = "朋友",
            city = "广州",
            moods = listOf("想搞笑", "不想花太多钱", "想探店", "想破冰"),
            time = "2 小时",
            budget = "100 元以内",
            vibe = "搞笑的",
            note = "朋友局不要太正经，最好每一站都有一个小梗。",
            transportMode = "地铁/步行",
        ),
    ),
    QuickStartItemCopy(
        title = "台北暧昧边界感夜散步",
        subtitle = "河岸、甜点和不冒犯的聊天题。",
        tags = listOf("暧昧", "台北", "边界感"),
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
    QuickStartItemCopy(
        title = "西安家人低体力文化线",
        subtitle = "少赶路、多坐下，给长辈和小孩留余量。",
        tags = listOf("家人", "西安", "低体力"),
        input = QuestInput(
            relationship = "家人",
            city = "西安",
            moods = listOf("亲子友好", "有点累", "不想走太远", "想散步"),
            time = "半天",
            budget = "300 元以内",
            vibe = "治愈的",
            note = "希望文化感够，但不要暴走，要有休息点和室内备选。",
            transportMode = "按地图推荐",
        ),
    ),
)

private val zhCnQuickStart = QuickStartStrings(
    title = "灵感开局",
    subtitle = "quick start",
    sectionTitle = "直接选一条路线",
    sectionEnglish = "choose a route",
    intro = "不想整理偏好时，从这里直接开始。每个灵感都基于全球精选 mock 地点生成路线，正式版会接官方地图和后端内容源。",
    generate = "生成这一局",
    items = baseItems,
)

private val zhTwQuickStart = zhCnQuickStart.copy(
    title = "靈感開局",
    sectionTitle = "直接選一條路線",
    intro = "不想整理偏好時，從這裡直接開始。每個靈感都基於全球精選 mock 地點生成路線，正式版會接官方地圖和後端內容源。",
    generate = "生成這一局",
    items = listOf(
        baseItems[0].copy(title = "東京下班後 90 分鐘情侶路線", subtitle = "黃昏夜景、低壓力互動、適合拍封面。", tags = listOf("情侶", "東京", "公共交通／步行")),
        baseItems[1].copy(title = "上海曖昧不尷尬拍照路線", subtitle = "先散步再探店，保留邊界感。", tags = listOf("曖昧", "上海", "拍照")),
        baseItems[2].copy(title = "巴黎低預算紀念日路線", subtitle = "把拍照、晚餐和路線成本控制住。", tags = listOf("情侶", "巴黎", "低預算")),
        baseItems[3].copy(title = "首爾朋友 Citywalk + 探店路線", subtitle = "不只吃飯，要有拍照、挑戰和小吃。", tags = listOf("朋友", "首爾", "Citywalk")),
        baseItems[4].copy(title = "新加坡親子家庭輕鬆半日遊", subtitle = "低強度、可室內備選、照顧老人小孩。", tags = listOf("家人", "新加坡", "親子友好")),
        baseItems[5].copy(title = "杭州獨處療癒半日線", subtitle = "湖邊慢走、書店停靠、把今天安靜存檔。", tags = listOf("自己", "杭州", "療癒")),
        baseItems[6].copy(title = "廣州朋友低預算好笑路線", subtitle = "老街、小吃、隨機挑戰，不只坐著吃飯。", tags = listOf("朋友", "廣州", "低預算")),
        baseItems[7].copy(title = "台北曖昧邊界感夜散步", subtitle = "河岸、甜點和不冒犯的聊天題。", tags = listOf("曖昧", "台北", "邊界感")),
        baseItems[8].copy(title = "西安家人低體力文化線", subtitle = "少趕路、多坐下，給長輩和小孩留餘量。", tags = listOf("家人", "西安", "低體力")),
    ),
)

private val enQuickStart = QuickStartStrings(
    title = "Quick Start",
    subtitle = "route ideas",
    sectionTitle = "Pick a ready-made route",
    sectionEnglish = "choose a route",
    intro = "When you do not want to sort every preference, start here. Each idea uses curated global mock places; production will connect official maps and backend content sources.",
    generate = "Generate this route",
    items = listOf(
        baseItems[0].copy(title = "Tokyo 90-minute after-work couple route", subtitle = "Twilight views, low-pressure interaction, and a cover-photo mood.", tags = listOf("Couple", "Tokyo", "Transit / walk")),
        baseItems[1].copy(title = "Shanghai photo route for a not-awkward crush", subtitle = "Walk first, visit a spot next, and keep gentle boundaries.", tags = listOf("Crush", "Shanghai", "Photo")),
        baseItems[2].copy(title = "Paris low-budget anniversary route", subtitle = "Control photo, dinner, and route cost without losing the feeling.", tags = listOf("Couple", "Paris", "Low budget")),
        baseItems[3].copy(title = "Seoul friends citywalk + cafe route", subtitle = "Not just dinner: photos, challenges, and snacks.", tags = listOf("Friends", "Seoul", "Citywalk")),
        baseItems[4].copy(title = "Singapore easy family half-day route", subtitle = "Low intensity, indoor backup, and friendly to kids and elders.", tags = listOf("Family", "Singapore", "Kid-friendly")),
        baseItems[5].copy(title = "Hangzhou solo reset half-day route", subtitle = "A slow lakeside walk, a bookstore stop, and a quiet save point.", tags = listOf("Solo", "Hangzhou", "Reset")),
        baseItems[6].copy(title = "Guangzhou low-budget friends laugh route", subtitle = "Old streets, snacks, and tiny challenges beyond dinner.", tags = listOf("Friends", "Guangzhou", "Low budget")),
        baseItems[7].copy(title = "Taipei boundary-safe crush night walk", subtitle = "Riverside air, dessert, and questions that do not push too hard.", tags = listOf("Crush", "Taipei", "Boundaries")),
        baseItems[8].copy(title = "Xi'an low-energy family culture route", subtitle = "Less rushing, more sitting, and room for kids and elders.", tags = listOf("Family", "Xi'an", "Low energy")),
    ),
)

private val jaQuickStart = enQuickStart.copy(
    title = "クイック開始",
    subtitle = "route ideas",
    sectionTitle = "すぐ使えるルートを選ぶ",
    intro = "細かい好みを整理したくない時はここから始められます。各アイデアは世界の mock スポットを使い、本番では公式地図とバックエンドのコンテンツに接続します。",
    generate = "このルートを生成",
    items = listOf(
        baseItems[0].copy(title = "東京 仕事帰り90分カップルルート", subtitle = "夕景、低負担のやりとり、表紙写真向き。", tags = listOf("恋人", "東京", "公共交通／徒歩")),
        baseItems[1].copy(title = "上海 気まずくない写真ルート", subtitle = "まず散歩、次にお店。境界線を保てます。", tags = listOf("気になる人", "上海", "写真")),
        baseItems[2].copy(title = "パリ 低予算記念日ルート", subtitle = "写真、夕食、移動コストを抑えます。", tags = listOf("恋人", "パリ", "低予算")),
        baseItems[3].copy(title = "ソウル 友達 Citywalk + カフェ", subtitle = "食事だけでなく、写真、挑戦、軽食も。", tags = listOf("友達", "ソウル", "Citywalk")),
        baseItems[4].copy(title = "シンガポール 家族の半日ルート", subtitle = "低負担、屋内候補、子どもと年長者に配慮。", tags = listOf("家族", "シンガポール", "親子向け")),
        baseItems[5].copy(title = "杭州 ひとり癒やし半日ルート", subtitle = "湖畔をゆっくり歩き、本屋で休み、今日を静かに保存。", tags = listOf("ひとり", "杭州", "癒やし")),
        baseItems[6].copy(title = "広州 友達の低予算おもしろルート", subtitle = "古い街、小吃、小さな挑戦。食事だけで終わらせません。", tags = listOf("友達", "広州", "低予算")),
        baseItems[7].copy(title = "台北 境界線を守る夜散歩", subtitle = "川辺、デザート、押しすぎない会話。", tags = listOf("気になる人", "台北", "境界線")),
        baseItems[8].copy(title = "西安 家族の低負担カルチャールート", subtitle = "急がず、座れる時間を多めに。子どもと年長者に配慮。", tags = listOf("家族", "西安", "低負担")),
    ),
)

private val koQuickStart = enQuickStart.copy(
    title = "빠른 시작",
    subtitle = "route ideas",
    sectionTitle = "준비된 루트 선택",
    intro = "취향을 길게 정리하기 싫을 때 여기서 바로 시작하세요. 각 아이디어는 글로벌 mock 장소를 사용하며, 정식 버전은 공식 지도와 백엔드 콘텐츠 소스에 연결됩니다.",
    generate = "이 루트 생성",
    items = listOf(
        baseItems[0].copy(title = "도쿄 퇴근 후 90분 커플 루트", subtitle = "황혼 야경, 부담 낮은 상호작용, 커버 사진 분위기.", tags = listOf("연인", "도쿄", "대중교통 / 도보")),
        baseItems[1].copy(title = "상하이 어색하지 않은 사진 루트", subtitle = "먼저 산책하고, 다음은 가볍게 탐방합니다.", tags = listOf("썸", "상하이", "사진")),
        baseItems[2].copy(title = "파리 저예산 기념일 루트", subtitle = "사진, 저녁, 이동 비용을 함께 관리합니다.", tags = listOf("연인", "파리", "저예산")),
        baseItems[3].copy(title = "서울 친구 Citywalk + 카페 루트", subtitle = "밥만 먹지 않고 사진, 챌린지, 간식까지.", tags = listOf("친구", "서울", "Citywalk")),
        baseItems[4].copy(title = "싱가포르 가족 반나절 쉬운 루트", subtitle = "낮은 강도, 실내 대안, 아이와 어르신 배려.", tags = listOf("가족", "싱가포르", "키즈 친화")),
        baseItems[5].copy(title = "항저우 혼자 쉬어 가는 반나절 루트", subtitle = "호숫가 산책, 서점 휴식, 오늘의 조용한 저장점.", tags = listOf("혼자", "항저우", "힐링")),
        baseItems[6].copy(title = "광저우 친구 저예산 웃음 루트", subtitle = "오래된 거리, 간식, 작은 챌린지로 밥만 먹지 않기.", tags = listOf("친구", "광저우", "저예산")),
        baseItems[7].copy(title = "타이베이 경계감 있는 썸 밤 산책", subtitle = "강변, 디저트, 부담 주지 않는 질문.", tags = listOf("썸", "타이베이", "경계감")),
        baseItems[8].copy(title = "시안 가족 저강도 문화 루트", subtitle = "덜 서두르고 더 자주 쉬는 가족 문화 산책.", tags = listOf("가족", "시안", "저강도")),
    ),
)

private val esQuickStart = enQuickStart.copy(
    title = "Inicio rápido",
    subtitle = "route ideas",
    sectionTitle = "Elige una ruta lista",
    intro = "Si no quieres ordenar todas las preferencias, empieza aquí. Cada idea usa lugares globales mock; la versión final conectará mapas oficiales y fuentes backend.",
    generate = "Generar esta ruta",
    items = listOf(
        baseItems[0].copy(title = "Tokio: ruta de pareja de 90 min tras el trabajo", subtitle = "Atardecer, interacción ligera y ambiente de portada.", tags = listOf("Pareja", "Tokio", "Transporte / caminar")),
        baseItems[1].copy(title = "Shanghái: fotos sin incomodidad para un crush", subtitle = "Primero paseo, luego visita, con límites suaves.", tags = listOf("Crush", "Shanghái", "Fotos")),
        baseItems[2].copy(title = "París: aniversario de bajo presupuesto", subtitle = "Controla fotos, cena y costo sin perder emoción.", tags = listOf("Pareja", "París", "Bajo presupuesto")),
        baseItems[3].copy(title = "Seúl: citywalk + cafeterías con amigos", subtitle = "No solo cenar: fotos, retos y snacks.", tags = listOf("Amigos", "Seúl", "Citywalk")),
        baseItems[4].copy(title = "Singapur: medio día familiar tranquilo", subtitle = "Baja intensidad, respaldo interior y apto para niños y mayores.", tags = listOf("Familia", "Singapur", "Niños")),
        baseItems[5].copy(title = "Hangzhou: medio día solo para resetear", subtitle = "Paseo lento junto al lago, librería y un punto de calma.", tags = listOf("Solo", "Hangzhou", "Reset")),
        baseItems[6].copy(title = "Cantón: ruta barata y divertida con amigos", subtitle = "Calles antiguas, snacks y retos pequeños, no solo cenar.", tags = listOf("Amigos", "Cantón", "Bajo presupuesto")),
        baseItems[7].copy(title = "Taipéi: paseo nocturno con límites suaves", subtitle = "Río, postre y preguntas que no presionan.", tags = listOf("Crush", "Taipéi", "Límites")),
        baseItems[8].copy(title = "Xi'an: cultura familiar de baja energía", subtitle = "Menos prisa, más descansos, y espacio para niños y mayores.", tags = listOf("Familia", "Xi'an", "Baja energía")),
    ),
)
