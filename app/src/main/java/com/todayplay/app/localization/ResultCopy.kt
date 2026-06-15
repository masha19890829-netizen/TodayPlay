package com.todayplay.app.localization

import com.todayplay.app.model.FeedbackReason

data class ResultStrings(
    val topTitle: String,
    val topSubtitle: String,
    val saveAction: String,
    val photoMissionTitle: String,
    val endingRitualTitle: String,
    val shareCta: String,
    val regenerate: String,
    val backHome: String,
    val routeTitleLabel: String,
    val heroBody: String,
    val summaryTitle: String,
    val summaryEnglish: String,
    val relationshipLabel: String,
    val durationLabel: String,
    val budgetLabel: String,
    val awkwardLabel: String,
    val energyLabel: String,
    val intimacyLabel: String,
    val overviewTitle: String,
    val overviewEnglish: String,
    val routeTypeLabel: String,
    val cityLabel: String,
    val candidateStopsLabel: String,
    val candidatesLabel: String,
    val estimatedCostLabel: String,
    val bestPhotoLabel: String,
    val crowdRiskLabel: String,
    val checkInProgressLabel: String,
    val currentPointsLabel: String,
    val dataAvailabilityTitle: String,
    val availableRouteGeneration: String,
    val availableManualCheckIn: String,
    val availableExternalMaps: String,
    val localHistoryPoints: String,
    val needsLiveGlobalSearch: String,
    val needsOfficialHours: String,
    val needsPhotoUpload: String,
    val globalContentNotConfigured: String,
    val whyRecommendedPrefix: String,
    val whyForGroupPrefix: String,
    val photoSuggestionPrefix: String,
    val spendingSuggestionPrefix: String,
    val riskTipsPrefix: String,
    val backupPlanPrefix: String,
    val officialVerificationNeeded: String,
    val sourceVerified: String,
    val sourcePrefix: String,
    val openMap: String,
    val photoUploadDisabled: String,
    val howToPrefix: String,
    val tipsPrefix: String,
    val completed: String,
    val checkIn: String,
    val skipped: String,
    val skip: String,
    val hiddenCompleted: String,
    val completeHidden: String,
    val hiddenHowToPrefix: String,
    val dialogueTitle: String,
    val dialogueEnglish: String,
    val backupTitle: String,
    val backupEnglish: String,
    val tooTiredPlan: String,
    val tooExpensivePlan: String,
    val noPhotoPlan: String,
    val unsafePlacePlan: String,
    val keywordsPrefix: String,
    val intimacyCrush: String,
    val intimacyCouple: String,
    val intimacySolo: String,
    val intimacyDefault: String,
) {
    fun progress(completed: Int, total: Int): String = when (this) {
        zhCnResult, zhTwResult -> "${if (this === zhTwResult) "通關進度" else "通关进度"} $completed/$total"
        else -> "Progress $completed/$total"
    }

    fun candidateCount(count: Int): String = "$count $candidatesLabel"

    fun stopLabel(order: Int): String = when (this) {
        zhCnResult -> "站 $order"
        zhTwResult -> "站 $order"
        jaResult -> "Stop $order"
        koResult -> "$order 번째"
        esResult -> "Parada $order"
        else -> "Stop $order"
    }

    fun stopMeta(startTime: String, minutes: Int, country: String, city: String, district: String): String = when (this) {
        zhCnResult -> "$startTime / 停留 $minutes 分钟 / $country · $city · $district"
        zhTwResult -> "$startTime / 停留 $minutes 分鐘 / $country · $city · $district"
        jaResult -> "$startTime / 滞在 $minutes 分 / $country · $city · $district"
        koResult -> "$startTime / 체류 $minutes 분 / $country · $city · $district"
        esResult -> "$startTime / $minutes min / $country · $city · $district"
        else -> "$startTime / $minutes min / $country · $city · $district"
    }

    fun sourceLine(sourceLabel: String, requiresVerification: Boolean): String {
        val status = if (requiresVerification) officialVerificationNeeded else sourceVerified
        return "$sourcePrefix$sourceLabel; $status"
    }

    fun checkedIn(points: Int): String = when (this) {
        zhCnResult -> "已打卡 +$points"
        zhTwResult -> "已打卡 +$points"
        jaResult -> "チェックイン済み +$points"
        koResult -> "체크인 완료 +$points"
        esResult -> "Check-in hecho +$points"
        else -> "Checked in +$points"
    }

    fun manualCheckIn(points: Int): String = when (this) {
        zhCnResult -> "手动打卡 +$points"
        zhTwResult -> "手動打卡 +$points"
        jaResult -> "手動チェックイン +$points"
        koResult -> "수동 체크인 +$points"
        esResult -> "Check-in manual +$points"
        else -> "Manual check-in +$points"
    }

    fun feedbackLabel(reason: FeedbackReason): String = when (reason) {
        FeedbackReason.TooAwkward -> when (this) {
            zhCnResult -> "太尬"
            zhTwResult -> "太尷尬"
            jaResult -> "気まずい"
            koResult -> "어색함"
            esResult -> "Incómodo"
            else -> "Awkward"
        }
        FeedbackReason.TooExpensive -> when (this) {
            zhCnResult -> "太贵"
            zhTwResult -> "太貴"
            jaResult -> "高すぎる"
            koResult -> "비쌈"
            esResult -> "Caro"
            else -> "Too pricey"
        }
        FeedbackReason.TooFar -> when (this) {
            zhCnResult -> "太远"
            zhTwResult -> "太遠"
            jaResult -> "遠すぎる"
            koResult -> "멀다"
            esResult -> "Lejos"
            else -> "Too far"
        }
        FeedbackReason.TooTiring -> when (this) {
            zhCnResult -> "太累"
            zhTwResult -> "太累"
            jaResult -> "疲れる"
            koResult -> "피곤함"
            esResult -> "Cansa"
            else -> "Too tiring"
        }
    }

    fun feedbackRecorded(labels: String): String = when (this) {
        zhCnResult -> "已记录反馈：$labels。后续生成会优先避开这类任务。"
        zhTwResult -> "已記錄回饋：$labels。後續生成會優先避開這類任務。"
        jaResult -> "フィードバックを記録しました：$labels。次回以降はこの種類を避けやすくします。"
        koResult -> "피드백을 기록했습니다: $labels. 다음 생성에서 이런 유형을 우선 피합니다."
        esResult -> "Feedback guardado: $labels. Las próximas rutas evitarán este tipo de tarea."
        else -> "Feedback saved: $labels. Future routes will avoid this kind of task first."
    }

    fun intimacyFor(relationship: String): String = when (relationship) {
        "暧昧中" -> intimacyCrush
        "情侣" -> intimacyCouple
        "一个人散心" -> intimacySolo
        else -> intimacyDefault
    }
}

fun resultStrings(locale: TodayPlayLocale): ResultStrings = when (locale) {
    TodayPlayLocale.SimplifiedChinese -> zhCnResult
    TodayPlayLocale.TraditionalChinese -> zhTwResult
    TodayPlayLocale.English -> enResult
    TodayPlayLocale.Japanese -> jaResult
    TodayPlayLocale.Korean -> koResult
    TodayPlayLocale.Spanish -> esResult
}

private val zhCnResult = ResultStrings(
    topTitle = "路线副本",
    topSubtitle = "itinerary quest",
    saveAction = "收藏",
    photoMissionTitle = "今日照片 / PHOTO MISSION",
    endingRitualTitle = "结尾仪式 / ENDING RITUAL",
    shareCta = "生成邀请 / 通关卡",
    regenerate = "不喜欢，重抽",
    backHome = "回到首页",
    routeTitleLabel = "路线标题",
    heroBody = "地点、路线、导航、打卡和互动任务，都整理成今天的一条路。",
    summaryTitle = "15 秒摘要",
    summaryEnglish = "quick brief",
    relationshipLabel = "关系",
    durationLabel = "时长",
    budgetLabel = "预算",
    awkwardLabel = "尴尬指数",
    energyLabel = "能量消耗",
    intimacyLabel = "亲密强度",
    overviewTitle = "路线概览",
    overviewEnglish = "itinerary",
    routeTypeLabel = "路线类型",
    cityLabel = "城市",
    candidateStopsLabel = "候选地点",
    candidatesLabel = "个样例候选",
    estimatedCostLabel = "预计花费",
    bestPhotoLabel = "最佳拍照",
    crowdRiskLabel = "拥挤风险",
    checkInProgressLabel = "打卡进度",
    currentPointsLabel = "当前积分",
    dataAvailabilityTitle = "数据可用性",
    availableRouteGeneration = "可用：路线生成",
    availableManualCheckIn = "可用：手动打卡",
    availableExternalMaps = "可用：外部地图",
    localHistoryPoints = "本地：历史/积分",
    needsLiveGlobalSearch = "需接入：全球实时搜索",
    needsOfficialHours = "需接入：官方营业时间",
    needsPhotoUpload = "需接入：照片上传",
    globalContentNotConfigured = "未配置：全球内容服务",
    whyRecommendedPrefix = "为什么推荐：",
    whyForGroupPrefix = "适合你们：",
    photoSuggestionPrefix = "拍照建议：",
    spendingSuggestionPrefix = "消费建议：",
    riskTipsPrefix = "避坑提示：",
    backupPlanPrefix = "备选方案：",
    officialVerificationNeeded = "正式版需官方数据核验",
    sourceVerified = "已核验",
    sourcePrefix = "来源：",
    openMap = "打开地图",
    photoUploadDisabled = "照片上传暂未开放",
    howToPrefix = "怎么做：",
    tipsPrefix = "Tips：",
    completed = "已完成",
    checkIn = "打卡",
    skipped = "已跳过",
    skip = "跳过",
    hiddenCompleted = "隐藏任务已完成",
    completeHidden = "完成隐藏任务",
    hiddenHowToPrefix = "完成方式：",
    dialogueTitle = "不会冷场的 5 句话",
    dialogueEnglish = "dialogue",
    backupTitle = "备用方案",
    backupEnglish = "plan b",
    tooTiredPlan = "太累：只做 Act 01 和隐藏任务，通关仍然有效。",
    tooExpensivePlan = "太贵：把消费任务改成便利店热饮或 0 元照片收集。",
    noPhotoPlan = "对方不想拍照：改成记录一句今天的台词，不需要出镜。",
    unsafePlacePlan = "地点不舒服：只去明亮、安全、你们都愿意停留的地方。",
    keywordsPrefix = "本局关键词：",
    intimacyCrush = "轻微升温，可随时跳过",
    intimacyCouple = "温柔靠近，不强行表演",
    intimacySolo = "自我照顾",
    intimacyDefault = "低压力陪伴",
)

private val zhTwResult = zhCnResult.copy(
    topTitle = "路線副本",
    saveAction = "收藏",
    photoMissionTitle = "今日照片 / PHOTO MISSION",
    endingRitualTitle = "結尾儀式 / ENDING RITUAL",
    shareCta = "生成邀請 / 通關卡",
    regenerate = "不喜歡，重抽",
    backHome = "回到首頁",
    routeTitleLabel = "路線標題",
    heroBody = "地點、路線、導航、打卡和互動任務，都整理成今天的一條路。",
    summaryTitle = "15 秒摘要",
    relationshipLabel = "關係",
    durationLabel = "時長",
    budgetLabel = "預算",
    awkwardLabel = "尷尬指數",
    energyLabel = "能量消耗",
    intimacyLabel = "親密強度",
    overviewTitle = "路線概覽",
    routeTypeLabel = "路線類型",
    candidateStopsLabel = "候選地點",
    candidatesLabel = "個樣例候選",
    estimatedCostLabel = "預計花費",
    bestPhotoLabel = "最佳拍照",
    crowdRiskLabel = "擁擠風險",
    checkInProgressLabel = "打卡進度",
    currentPointsLabel = "目前積分",
    dataAvailabilityTitle = "資料可用性",
    availableRouteGeneration = "可用：路線生成",
    availableManualCheckIn = "可用：手動打卡",
    availableExternalMaps = "可用：外部地圖",
    localHistoryPoints = "本地：歷史/積分",
    needsLiveGlobalSearch = "需接入：全球即時搜索",
    needsOfficialHours = "需接入：官方營業時間",
    needsPhotoUpload = "需接入：照片上傳",
    globalContentNotConfigured = "未配置：全球內容服務",
    whyRecommendedPrefix = "為什麼推薦：",
    whyForGroupPrefix = "適合你們：",
    photoSuggestionPrefix = "拍照建議：",
    spendingSuggestionPrefix = "消費建議：",
    riskTipsPrefix = "避坑提示：",
    backupPlanPrefix = "備選方案：",
    officialVerificationNeeded = "正式版需官方資料核驗",
    sourceVerified = "已核驗",
    sourcePrefix = "來源：",
    openMap = "開啟地圖",
    photoUploadDisabled = "照片上傳暫未開放",
    howToPrefix = "怎麼做：",
    completed = "已完成",
    checkIn = "打卡",
    skipped = "已跳過",
    skip = "跳過",
    hiddenCompleted = "隱藏任務已完成",
    completeHidden = "完成隱藏任務",
    hiddenHowToPrefix = "完成方式：",
    dialogueTitle = "不會冷場的 5 句話",
    backupTitle = "備用方案",
    tooTiredPlan = "太累：只做 Act 01 和隱藏任務，通關仍然有效。",
    tooExpensivePlan = "太貴：把消費任務改成便利店熱飲或 0 元照片收集。",
    noPhotoPlan = "對方不想拍照：改成記錄一句今天的台詞，不需要出鏡。",
    unsafePlacePlan = "地點不舒服：只去明亮、安全、你們都願意停留的地方。",
    keywordsPrefix = "本局關鍵詞：",
    intimacyCrush = "輕微升溫，可隨時跳過",
    intimacyCouple = "溫柔靠近，不強行表演",
    intimacySolo = "自我照顧",
    intimacyDefault = "低壓力陪伴",
)

private val enResult = zhCnResult.copy(
    topTitle = "Route Quest",
    saveAction = "Save",
    photoMissionTitle = "PHOTO MISSION",
    endingRitualTitle = "ENDING RITUAL",
    shareCta = "Create invite / completion card",
    regenerate = "Not this one",
    backHome = "Home",
    routeTitleLabel = "Route title",
    heroBody = "Places, route, maps, check-ins, and interaction tasks are organized into one plan for today.",
    summaryTitle = "15-second brief",
    relationshipLabel = "Relationship",
    durationLabel = "Duration",
    budgetLabel = "Budget",
    awkwardLabel = "Awkwardness",
    energyLabel = "Energy",
    intimacyLabel = "Closeness",
    overviewTitle = "Route overview",
    routeTypeLabel = "Route type",
    cityLabel = "City",
    candidateStopsLabel = "Candidate stops",
    candidatesLabel = "sample candidates",
    estimatedCostLabel = "Estimated cost",
    bestPhotoLabel = "Best photo time",
    crowdRiskLabel = "Crowd risk",
    checkInProgressLabel = "Check-ins",
    currentPointsLabel = "Current points",
    dataAvailabilityTitle = "Data availability",
    availableRouteGeneration = "Available: route generation",
    availableManualCheckIn = "Available: manual check-in",
    availableExternalMaps = "Available: external maps",
    localHistoryPoints = "Local: history / points",
    needsLiveGlobalSearch = "Needs: live global search",
    needsOfficialHours = "Needs: official hours",
    needsPhotoUpload = "Needs: photo upload",
    globalContentNotConfigured = "Not configured: global content service",
    whyRecommendedPrefix = "Why recommended: ",
    whyForGroupPrefix = "Why it fits: ",
    photoSuggestionPrefix = "Photo idea: ",
    spendingSuggestionPrefix = "Spending tip: ",
    riskTipsPrefix = "Risk tips: ",
    backupPlanPrefix = "Backup: ",
    officialVerificationNeeded = "production needs official verification",
    sourceVerified = "verified",
    sourcePrefix = "Source: ",
    openMap = "Open map",
    photoUploadDisabled = "Photo upload not open yet",
    howToPrefix = "How to: ",
    tipsPrefix = "Tips: ",
    completed = "Completed",
    checkIn = "Check in",
    skipped = "Skipped",
    skip = "Skip",
    hiddenCompleted = "Hidden task completed",
    completeHidden = "Complete hidden task",
    hiddenHowToPrefix = "How to complete: ",
    dialogueTitle = "5 lines that keep it easy",
    backupTitle = "Backup plan",
    tooTiredPlan = "Too tired: do only Act 01 and the hidden task. Completion still counts.",
    tooExpensivePlan = "Too pricey: replace spending tasks with a convenience-store drink or a free photo collection.",
    noPhotoPlan = "If they do not want photos: record one line from today instead. No one needs to appear on camera.",
    unsafePlacePlan = "If a place feels wrong: only stay somewhere bright, safe, and comfortable for everyone.",
    keywordsPrefix = "Keywords: ",
    intimacyCrush = "Gentle warmth, skippable anytime",
    intimacyCouple = "Soft closeness, no forced performance",
    intimacySolo = "Self-care",
    intimacyDefault = "Low-pressure companionship",
)

private val jaResult = enResult.copy(
    topTitle = "ルートクエスト",
    saveAction = "保存",
    photoMissionTitle = "写真ミッション",
    endingRitualTitle = "締めくくり",
    shareCta = "招待 / 完了カードを作成",
    regenerate = "別の案にする",
    backHome = "ホームへ",
    routeTitleLabel = "ルート名",
    heroBody = "場所、ルート、地図、チェックイン、交流タスクを今日の計画にまとめます。",
    summaryTitle = "15秒サマリー",
    relationshipLabel = "関係",
    durationLabel = "時間",
    budgetLabel = "予算",
    awkwardLabel = "気まずさ",
    energyLabel = "体力",
    intimacyLabel = "距離感",
    overviewTitle = "ルート概要",
    routeTypeLabel = "ルート種別",
    cityLabel = "都市",
    candidateStopsLabel = "候補スポット",
    candidatesLabel = "件のサンプル候補",
    estimatedCostLabel = "想定費用",
    bestPhotoLabel = "写真のおすすめ時間",
    crowdRiskLabel = "混雑リスク",
    checkInProgressLabel = "チェックイン",
    currentPointsLabel = "現在のポイント",
    dataAvailabilityTitle = "データ可用性",
    availableRouteGeneration = "利用可：ルート生成",
    availableManualCheckIn = "利用可：手動チェックイン",
    availableExternalMaps = "利用可：外部地図",
    localHistoryPoints = "ローカル：履歴 / ポイント",
    needsLiveGlobalSearch = "要接続：世界リアルタイム検索",
    needsOfficialHours = "要接続：公式営業時間",
    needsPhotoUpload = "要接続：写真アップロード",
    globalContentNotConfigured = "未設定：グローバルコンテンツ",
    whyRecommendedPrefix = "おすすめ理由：",
    whyForGroupPrefix = "合う理由：",
    photoSuggestionPrefix = "写真案：",
    spendingSuggestionPrefix = "消費の目安：",
    riskTipsPrefix = "注意点：",
    backupPlanPrefix = "代替案：",
    officialVerificationNeeded = "本番では公式確認が必要",
    sourceVerified = "確認済み",
    sourcePrefix = "出典：",
    openMap = "地図を開く",
    photoUploadDisabled = "写真アップロードは未開放",
    howToPrefix = "やり方：",
    completed = "完了",
    checkIn = "チェックイン",
    skipped = "スキップ済み",
    skip = "スキップ",
    hiddenCompleted = "隠しタスク完了",
    completeHidden = "隠しタスクを完了",
    hiddenHowToPrefix = "完了方法：",
    dialogueTitle = "会話が止まらない5つの言葉",
    backupTitle = "代替プラン",
    tooTiredPlan = "疲れたら：Act 01 と隠しタスクだけでも完了扱いです。",
    tooExpensivePlan = "高すぎたら：消費タスクをコンビニの温かい飲み物や無料写真集めに変更。",
    noPhotoPlan = "写真が苦手なら：顔を出さず、今日の一言を記録します。",
    unsafePlacePlan = "場所が合わなければ：明るく安全で、全員が落ち着ける場所だけにします。",
    keywordsPrefix = "キーワード：",
    intimacyCrush = "少し温まる、いつでもスキップ可",
    intimacyCouple = "やさしく近づく、無理に演じない",
    intimacySolo = "セルフケア",
    intimacyDefault = "低負担の付き添い",
)

private val koResult = enResult.copy(
    topTitle = "루트 퀘스트",
    saveAction = "저장",
    photoMissionTitle = "사진 미션",
    endingRitualTitle = "마무리 의식",
    shareCta = "초대 / 완료 카드 만들기",
    regenerate = "다른 루트",
    backHome = "홈으로",
    routeTitleLabel = "루트 제목",
    heroBody = "장소, 경로, 지도, 체크인, 상호작용 미션을 오늘의 계획으로 정리합니다.",
    summaryTitle = "15초 요약",
    relationshipLabel = "관계",
    durationLabel = "시간",
    budgetLabel = "예산",
    awkwardLabel = "어색함",
    energyLabel = "에너지",
    intimacyLabel = "친밀도",
    overviewTitle = "루트 개요",
    routeTypeLabel = "루트 유형",
    cityLabel = "도시",
    candidateStopsLabel = "후보 장소",
    candidatesLabel = "개 샘플 후보",
    estimatedCostLabel = "예상 비용",
    bestPhotoLabel = "사진 추천 시간",
    crowdRiskLabel = "혼잡 위험",
    checkInProgressLabel = "체크인",
    currentPointsLabel = "현재 포인트",
    dataAvailabilityTitle = "데이터 사용 가능성",
    availableRouteGeneration = "사용 가능: 루트 생성",
    availableManualCheckIn = "사용 가능: 수동 체크인",
    availableExternalMaps = "사용 가능: 외부 지도",
    localHistoryPoints = "로컬: 기록 / 포인트",
    needsLiveGlobalSearch = "필요: 글로벌 실시간 검색",
    needsOfficialHours = "필요: 공식 영업시간",
    needsPhotoUpload = "필요: 사진 업로드",
    globalContentNotConfigured = "미설정: 글로벌 콘텐츠 서비스",
    whyRecommendedPrefix = "추천 이유: ",
    whyForGroupPrefix = "어울리는 이유: ",
    photoSuggestionPrefix = "사진 제안: ",
    spendingSuggestionPrefix = "소비 제안: ",
    riskTipsPrefix = "주의점: ",
    backupPlanPrefix = "대안: ",
    officialVerificationNeeded = "정식 버전은 공식 검증 필요",
    sourceVerified = "검증됨",
    sourcePrefix = "출처: ",
    openMap = "지도 열기",
    photoUploadDisabled = "사진 업로드는 아직 열리지 않음",
    howToPrefix = "방법: ",
    completed = "완료",
    checkIn = "체크인",
    skipped = "건너뜀",
    skip = "건너뛰기",
    hiddenCompleted = "숨은 미션 완료",
    completeHidden = "숨은 미션 완료하기",
    hiddenHowToPrefix = "완료 방법: ",
    dialogueTitle = "어색하지 않은 5가지 말",
    backupTitle = "대안 플랜",
    tooTiredPlan = "너무 피곤하면: Act 01과 숨은 미션만 해도 완료로 인정됩니다.",
    tooExpensivePlan = "너무 비싸면: 소비 미션을 편의점 따뜻한 음료나 무료 사진 모으기로 바꿉니다.",
    noPhotoPlan = "사진이 싫다면: 얼굴 없이 오늘의 한 문장을 기록합니다.",
    unsafePlacePlan = "장소가 불편하면: 밝고 안전하며 모두가 머물고 싶은 곳만 갑니다.",
    keywordsPrefix = "키워드: ",
    intimacyCrush = "가볍게 가까워짐, 언제든 건너뛰기 가능",
    intimacyCouple = "부드럽게 가까이, 억지 연출 없음",
    intimacySolo = "셀프 케어",
    intimacyDefault = "부담 낮은 동행",
)

private val esResult = enResult.copy(
    topTitle = "Ruta Quest",
    saveAction = "Guardar",
    photoMissionTitle = "MISIÓN DE FOTO",
    endingRitualTitle = "RITUAL FINAL",
    shareCta = "Crear invitación / tarjeta final",
    regenerate = "Otra ruta",
    backHome = "Inicio",
    routeTitleLabel = "Título de ruta",
    heroBody = "Lugares, ruta, mapas, check-ins y tareas de interacción en un solo plan para hoy.",
    summaryTitle = "Resumen de 15 segundos",
    relationshipLabel = "Relación",
    durationLabel = "Duración",
    budgetLabel = "Presupuesto",
    awkwardLabel = "Incomodidad",
    energyLabel = "Energía",
    intimacyLabel = "Cercanía",
    overviewTitle = "Resumen de ruta",
    routeTypeLabel = "Tipo de ruta",
    cityLabel = "Ciudad",
    candidateStopsLabel = "Lugares candidatos",
    candidatesLabel = "candidatos de muestra",
    estimatedCostLabel = "Costo estimado",
    bestPhotoLabel = "Mejor hora para fotos",
    crowdRiskLabel = "Riesgo de aforo",
    checkInProgressLabel = "Check-ins",
    currentPointsLabel = "Puntos actuales",
    dataAvailabilityTitle = "Disponibilidad de datos",
    availableRouteGeneration = "Disponible: generar ruta",
    availableManualCheckIn = "Disponible: check-in manual",
    availableExternalMaps = "Disponible: mapas externos",
    localHistoryPoints = "Local: historial / puntos",
    needsLiveGlobalSearch = "Requiere: búsqueda global en vivo",
    needsOfficialHours = "Requiere: horarios oficiales",
    needsPhotoUpload = "Requiere: subir fotos",
    globalContentNotConfigured = "Sin configurar: contenido global",
    whyRecommendedPrefix = "Por qué se recomienda: ",
    whyForGroupPrefix = "Por qué encaja: ",
    photoSuggestionPrefix = "Idea de foto: ",
    spendingSuggestionPrefix = "Consejo de gasto: ",
    riskTipsPrefix = "Riesgos: ",
    backupPlanPrefix = "Alternativa: ",
    officialVerificationNeeded = "producción requiere verificación oficial",
    sourceVerified = "verificado",
    sourcePrefix = "Fuente: ",
    openMap = "Abrir mapa",
    photoUploadDisabled = "Subida de fotos no disponible aún",
    howToPrefix = "Cómo hacerlo: ",
    completed = "Completado",
    checkIn = "Check-in",
    skipped = "Saltado",
    skip = "Saltar",
    hiddenCompleted = "Tarea secreta completada",
    completeHidden = "Completar tarea secreta",
    hiddenHowToPrefix = "Cómo completar: ",
    dialogueTitle = "5 frases para no quedarse sin tema",
    backupTitle = "Plan alternativo",
    tooTiredPlan = "Si hay cansancio: haz solo Act 01 y la tarea secreta. Aún cuenta como completado.",
    tooExpensivePlan = "Si es caro: cambia la tarea de gasto por una bebida barata o una colección de fotos gratis.",
    noPhotoPlan = "Si no quieren fotos: registra una frase del día sin salir en cámara.",
    unsafePlacePlan = "Si el lugar no se siente bien: quédate solo en sitios luminosos, seguros y cómodos.",
    keywordsPrefix = "Palabras clave: ",
    intimacyCrush = "Calidez suave, se puede saltar",
    intimacyCouple = "Cercanía amable, sin actuar de más",
    intimacySolo = "Autocuidado",
    intimacyDefault = "Compañía sin presión",
)
