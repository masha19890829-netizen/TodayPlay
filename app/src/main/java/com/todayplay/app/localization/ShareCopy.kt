package com.todayplay.app.localization

import com.todayplay.app.model.Quest
import com.todayplay.app.model.CompletionCardData
import com.todayplay.app.model.TaskStatus

data class CompletionCardStrings(
    val body: String,
    val episodeTitleLabel: String,
    val completionTitleLabel: String,
    val durationLabel: String,
    val dateLabel: String,
    val relationshipValueLabel: String,
    val routeTypeLabel: String,
    val routeQuestFallback: String,
    val stopCheckInLabel: String,
    val rewardPointsLabel: String,
    val taskCompletionLabel: String,
    val keywordsLabel: String,
    val hiddenTaskLabel: String,
    val footer: String,
    val hiddenTaskStatus: (TaskStatus) -> String,
)

data class ShareStrings(
    val title: String,
    val subtitle: String,
    val inviteSectionTitle: String,
    val inviteSectionEnglish: String,
    val completionSectionTitle: String,
    val completionSectionEnglish: String,
    val sendInvite: String,
    val sendCompletion: String,
    val saveCard: String,
    val restart: String,
    val backHome: String,
    val screenshotTip: String,
    val saveToast: String,
    val chooserTitle: String,
    val inviteSubject: (Quest) -> String,
    val inviteBody: String,
    val relationshipLabel: String,
    val durationLabel: String,
    val budgetLabel: String,
    val keywordsLabel: String,
    val invitePrivacyNote: String,
    val buildInviteText: (Quest) -> String,
    val completionSubject: (CompletionCardData) -> String,
    val buildCompletionText: (CompletionCardData) -> String,
    val completionCard: CompletionCardStrings,
)

fun shareStrings(locale: TodayPlayLocale): ShareStrings = when (locale) {
    TodayPlayLocale.SimplifiedChinese -> ShareStrings(
        title = "邀请与通关卡",
        subtitle = "share",
        inviteSectionTitle = "发给 TA 的邀请卡",
        inviteSectionEnglish = "invite card",
        completionSectionTitle = "完成后的通关卡",
        completionSectionEnglish = "completion card",
        sendInvite = "发给 TA 选择",
        sendCompletion = "分享通关结果",
        saveCard = "保存通关卡",
        restart = "再开一局",
        backHome = "返回首页",
        screenshotTip = "通关卡适合截图保存。正式版会补高清图片保存和更多卡面模板。",
        saveToast = "当前版本请先截图保存，高清保存会在正式版开放",
        chooserTitle = "分享今日副本",
        inviteSubject = { quest -> "今晚要不要开一局：${quest.title}" },
        inviteBody = "我抽到了一张今晚副本，要不要一起试试？",
        relationshipLabel = "关系",
        durationLabel = "预计时长",
        budgetLabel = "预算",
        keywordsLabel = "关键词",
        invitePrivacyNote = "不公开全部任务，先把选择权交给对方。",
        buildInviteText = { quest ->
            """
            我抽到一张「今天怎么玩」副本：
            《${quest.title}》

            预计时长：${quest.duration}
            预算：${quest.budget}
            关键词：${quest.completionKeywords.joinToString(" / ")}

            要不要今晚一起试试？不喜欢我们就重抽一张。
            """.trimIndent()
        },
        completionSubject = { card -> "我们通关了：${card.title}" },
        buildCompletionText = { card ->
            """
            我们完成了一张「今天怎么玩」副本：
            《${card.title}》

            通关称号：${card.completionTitle}
            任务完成：${card.completedTaskCount}/${card.totalTaskCount}
            地点打卡：${card.checkedInStopCount}/${card.totalStopCount}
            获得积分：${card.totalRewardPoints}
            关键词：${card.keywords.joinToString(" / ")}

            ${card.summary}
            """.trimIndent()
        },
        completionCard = zhCnCompletionCard,
    )
    TodayPlayLocale.TraditionalChinese -> shareStrings(TodayPlayLocale.SimplifiedChinese).copy(
        title = "邀請與通關卡",
        inviteSectionTitle = "發給 TA 的邀請卡",
        completionSectionTitle = "完成後的通關卡",
        sendInvite = "發給 TA 選擇",
        sendCompletion = "分享通關結果",
        saveCard = "保存通關卡",
        restart = "再開一局",
        backHome = "返回首頁",
        screenshotTip = "通關卡適合截圖保存。正式版會補高清圖片保存和更多卡面模板。",
        saveToast = "目前版本請先截圖保存，高清保存會在正式版開放",
        chooserTitle = "分享今日副本",
        inviteSubject = { quest -> "今晚要不要開一局：${quest.title}" },
        inviteBody = "我抽到了一張今晚副本，要不要一起試試？",
        relationshipLabel = "關係",
        durationLabel = "預計時長",
        budgetLabel = "預算",
        keywordsLabel = "關鍵詞",
        invitePrivacyNote = "不公開全部任務，先把選擇權交給對方。",
        buildInviteText = { quest ->
            """
            我抽到一張「今天怎么玩」副本：
            《${quest.title}》

            預計時長：${quest.duration}
            預算：${quest.budget}
            關鍵詞：${quest.completionKeywords.joinToString(" / ")}

            要不要今晚一起試試？不喜歡我們就重抽一張。
            """.trimIndent()
        },
        completionSubject = { card -> "我們通關了：${card.title}" },
        buildCompletionText = { card ->
            """
            我們完成了一張「今天怎么玩」副本：
            《${card.title}》

            通關稱號：${card.completionTitle}
            任務完成：${card.completedTaskCount}/${card.totalTaskCount}
            地點打卡：${card.checkedInStopCount}/${card.totalStopCount}
            獲得積分：${card.totalRewardPoints}
            關鍵詞：${card.keywords.joinToString(" / ")}

            ${card.summary}
            """.trimIndent()
        },
        completionCard = zhTwCompletionCard,
    )
    TodayPlayLocale.English -> ShareStrings(
        title = "Invite & Completion Card",
        subtitle = "share",
        inviteSectionTitle = "Invite card",
        inviteSectionEnglish = "invite card",
        completionSectionTitle = "Completion card",
        completionSectionEnglish = "completion card",
        sendInvite = "Send invite",
        sendCompletion = "Share completion",
        saveCard = "Save card",
        restart = "Start another",
        backHome = "Back home",
        screenshotTip = "The completion card is ready for screenshots. HD export and more templates will come before production launch.",
        saveToast = "For now, take a screenshot. HD saving will open in the production version.",
        chooserTitle = "Share today's quest",
        inviteSubject = { quest -> "Want to play this route tonight: ${quest.title}" },
        inviteBody = "I drew a route quest for tonight. Want to try it together?",
        relationshipLabel = "Relationship",
        durationLabel = "Duration",
        budgetLabel = "Budget",
        keywordsLabel = "Keywords",
        invitePrivacyNote = "The full task list stays private first, so the other person can choose freely.",
        buildInviteText = { quest ->
            """
            I drew a TodayPlay route quest:
            "${quest.title}"

            Duration: ${quest.duration}
            Budget: ${quest.budget}
            Keywords: ${quest.completionKeywords.joinToString(" / ")}

            Want to try it tonight? If it does not fit, we can redraw.
            """.trimIndent()
        },
        completionSubject = { card -> "We completed: ${card.title}" },
        buildCompletionText = { card ->
            """
            We completed a TodayPlay route quest:
            "${card.title}"

            Completion title: ${card.completionTitle}
            Tasks done: ${card.completedTaskCount}/${card.totalTaskCount}
            Stop check-ins: ${card.checkedInStopCount}/${card.totalStopCount}
            Points earned: ${card.totalRewardPoints}
            Keywords: ${card.keywords.joinToString(" / ")}

            ${card.summary}
            """.trimIndent()
        },
        completionCard = enCompletionCard,
    )
    TodayPlayLocale.Japanese -> shareStrings(TodayPlayLocale.English).copy(
        title = "招待とクリアカード",
        inviteSectionTitle = "招待カード",
        completionSectionTitle = "クリアカード",
        sendInvite = "招待を送る",
        sendCompletion = "クリア結果を共有",
        saveCard = "カードを保存",
        restart = "もう一度始める",
        backHome = "ホームへ戻る",
        screenshotTip = "クリアカードはスクリーンショット保存向けです。正式版では高画質保存と追加テンプレートを用意します。",
        saveToast = "現在はスクリーンショットで保存してください。高画質保存は正式版で開放予定です。",
        chooserTitle = "今日のクエストを共有",
        inviteSubject = { quest -> "今夜このルートを試してみない？：${quest.title}" },
        inviteBody = "今夜のルートクエストを引きました。一緒に試してみる？",
        relationshipLabel = "関係",
        durationLabel = "時間",
        budgetLabel = "予算",
        keywordsLabel = "キーワード",
        invitePrivacyNote = "全タスクはまだ公開せず、相手に選ぶ余白を残します。",
        buildInviteText = { quest ->
            """
            TodayPlay のルートクエストを引きました：
            「${quest.title}」

            時間：${quest.duration}
            予算：${quest.budget}
            キーワード：${quest.completionKeywords.joinToString(" / ")}

            今夜一緒に試してみる？合わなければ引き直そう。
            """.trimIndent()
        },
        completionSubject = { card -> "クリアしました：${card.title}" },
        buildCompletionText = { card ->
            """
            TodayPlay のルートクエストをクリアしました：
            「${card.title}」

            クリア称号：${card.completionTitle}
            タスク完了：${card.completedTaskCount}/${card.totalTaskCount}
            スポット到着：${card.checkedInStopCount}/${card.totalStopCount}
            獲得ポイント：${card.totalRewardPoints}
            キーワード：${card.keywords.joinToString(" / ")}

            ${card.summary}
            """.trimIndent()
        },
        completionCard = jaCompletionCard,
    )
    TodayPlayLocale.Korean -> shareStrings(TodayPlayLocale.English).copy(
        title = "초대와 완료 카드",
        inviteSectionTitle = "초대 카드",
        completionSectionTitle = "완료 카드",
        sendInvite = "초대 보내기",
        sendCompletion = "완료 결과 공유",
        saveCard = "카드 저장",
        restart = "다시 시작",
        backHome = "홈으로",
        screenshotTip = "완료 카드는 스크린샷으로 저장하기 좋습니다. 정식 버전에서 고화질 저장과 더 많은 템플릿을 제공합니다.",
        saveToast = "지금은 스크린샷으로 저장해 주세요. 고화질 저장은 정식 버전에서 열립니다.",
        chooserTitle = "오늘의 퀘스트 공유",
        inviteSubject = { quest -> "오늘 밤 이 루트 해볼까: ${quest.title}" },
        inviteBody = "오늘 밤 루트 퀘스트를 뽑았어. 같이 해볼래?",
        relationshipLabel = "관계",
        durationLabel = "시간",
        budgetLabel = "예산",
        keywordsLabel = "키워드",
        invitePrivacyNote = "전체 미션은 먼저 공개하지 않고, 상대가 선택할 여지를 남겨둡니다.",
        buildInviteText = { quest ->
            """
            TodayPlay 루트 퀘스트를 뽑았어:
            "${quest.title}"

            시간: ${quest.duration}
            예산: ${quest.budget}
            키워드: ${quest.completionKeywords.joinToString(" / ")}

            오늘 밤 같이 해볼래? 마음에 안 들면 다시 뽑자.
            """.trimIndent()
        },
        completionSubject = { card -> "완료했어요: ${card.title}" },
        buildCompletionText = { card ->
            """
            TodayPlay 루트 퀘스트를 완료했어요:
            "${card.title}"

            완료 칭호: ${card.completionTitle}
            미션 완료: ${card.completedTaskCount}/${card.totalTaskCount}
            장소 체크인: ${card.checkedInStopCount}/${card.totalStopCount}
            획득 포인트: ${card.totalRewardPoints}
            키워드: ${card.keywords.joinToString(" / ")}

            ${card.summary}
            """.trimIndent()
        },
        completionCard = koCompletionCard,
    )
    TodayPlayLocale.Spanish -> shareStrings(TodayPlayLocale.English).copy(
        title = "Invitación y tarjeta final",
        inviteSectionTitle = "Tarjeta de invitación",
        completionSectionTitle = "Tarjeta final",
        sendInvite = "Enviar invitación",
        sendCompletion = "Compartir final",
        saveCard = "Guardar tarjeta",
        restart = "Empezar otra",
        backHome = "Volver al inicio",
        screenshotTip = "La tarjeta final está pensada para captura de pantalla. La exportación HD y más plantillas llegarán antes del lanzamiento.",
        saveToast = "Por ahora, guarda una captura. El guardado HD se abrirá en la versión final.",
        chooserTitle = "Compartir la ruta de hoy",
        inviteSubject = { quest -> "¿Probamos esta ruta hoy?: ${quest.title}" },
        inviteBody = "Me salió una ruta para hoy. ¿Quieres probarla conmigo?",
        relationshipLabel = "Relación",
        durationLabel = "Duración",
        budgetLabel = "Presupuesto",
        keywordsLabel = "Palabras clave",
        invitePrivacyNote = "La lista completa de tareas queda privada al inicio, para dejar espacio a elegir.",
        buildInviteText = { quest ->
            """
            Me salió una ruta de TodayPlay:
            "${quest.title}"

            Duración: ${quest.duration}
            Presupuesto: ${quest.budget}
            Palabras clave: ${quest.completionKeywords.joinToString(" / ")}

            ¿La probamos hoy? Si no encaja, podemos generar otra.
            """.trimIndent()
        },
        completionSubject = { card -> "Completamos: ${card.title}" },
        buildCompletionText = { card ->
            """
            Completamos una ruta de TodayPlay:
            "${card.title}"

            Título final: ${card.completionTitle}
            Tareas hechas: ${card.completedTaskCount}/${card.totalTaskCount}
            Check-ins: ${card.checkedInStopCount}/${card.totalStopCount}
            Puntos ganados: ${card.totalRewardPoints}
            Palabras clave: ${card.keywords.joinToString(" / ")}

            ${card.summary}
            """.trimIndent()
        },
        completionCard = esCompletionCard,
    )
}

private val zhCnCompletionCard = CompletionCardStrings(
    body = "我们一起，把今天过成了一部电影",
    episodeTitleLabel = "本集标题",
    completionTitleLabel = "通关称号",
    durationLabel = "通关时长",
    dateLabel = "日期",
    relationshipValueLabel = "今日关系值",
    routeTypeLabel = "路线类型",
    routeQuestFallback = "路线副本",
    stopCheckInLabel = "地点打卡",
    rewardPointsLabel = "获得积分",
    taskCompletionLabel = "任务完成",
    keywordsLabel = "关键词",
    hiddenTaskLabel = "隐藏任务",
    footer = "THANK YOU FOR PLAYING TODAY.",
    hiddenTaskStatus = { status ->
        when (status) {
            TaskStatus.Completed -> "已完成"
            TaskStatus.Skipped -> "已跳过"
            TaskStatus.Pending -> "待完成"
        }
    },
)

private val zhTwCompletionCard = zhCnCompletionCard.copy(
    body = "我們一起，把今天過成了一部電影",
    episodeTitleLabel = "本集標題",
    completionTitleLabel = "通關稱號",
    durationLabel = "通關時長",
    dateLabel = "日期",
    relationshipValueLabel = "今日關係值",
    routeTypeLabel = "路線類型",
    routeQuestFallback = "路線副本",
    stopCheckInLabel = "地點打卡",
    rewardPointsLabel = "獲得積分",
    taskCompletionLabel = "任務完成",
    keywordsLabel = "關鍵詞",
    hiddenTaskLabel = "隱藏任務",
    hiddenTaskStatus = { status ->
        when (status) {
            TaskStatus.Completed -> "已完成"
            TaskStatus.Skipped -> "已跳過"
            TaskStatus.Pending -> "待完成"
        }
    },
)

private val enCompletionCard = CompletionCardStrings(
    body = "Together, we turned today into a small movie",
    episodeTitleLabel = "Episode title",
    completionTitleLabel = "Completion title",
    durationLabel = "Duration",
    dateLabel = "Date",
    relationshipValueLabel = "Relationship value",
    routeTypeLabel = "Route type",
    routeQuestFallback = "Route quest",
    stopCheckInLabel = "Stop check-ins",
    rewardPointsLabel = "Points earned",
    taskCompletionLabel = "Tasks done",
    keywordsLabel = "Keywords",
    hiddenTaskLabel = "Hidden task",
    footer = "THANK YOU FOR PLAYING TODAY.",
    hiddenTaskStatus = { status ->
        when (status) {
            TaskStatus.Completed -> "Completed"
            TaskStatus.Skipped -> "Skipped"
            TaskStatus.Pending -> "Pending"
        }
    },
)

private val jaCompletionCard = enCompletionCard.copy(
    body = "一緒に、今日を小さな映画にしました",
    episodeTitleLabel = "タイトル",
    completionTitleLabel = "クリア称号",
    durationLabel = "時間",
    dateLabel = "日付",
    relationshipValueLabel = "関係値",
    routeTypeLabel = "ルート種別",
    routeQuestFallback = "ルートクエスト",
    stopCheckInLabel = "スポット到着",
    rewardPointsLabel = "獲得ポイント",
    taskCompletionLabel = "タスク完了",
    keywordsLabel = "キーワード",
    hiddenTaskLabel = "隠しタスク",
    hiddenTaskStatus = { status ->
        when (status) {
            TaskStatus.Completed -> "完了"
            TaskStatus.Skipped -> "スキップ"
            TaskStatus.Pending -> "未完了"
        }
    },
)

private val koCompletionCard = enCompletionCard.copy(
    body = "함께 오늘을 작은 영화처럼 만들었어요",
    episodeTitleLabel = "에피소드 제목",
    completionTitleLabel = "완료 칭호",
    durationLabel = "시간",
    dateLabel = "날짜",
    relationshipValueLabel = "관계 값",
    routeTypeLabel = "루트 유형",
    routeQuestFallback = "루트 퀘스트",
    stopCheckInLabel = "장소 체크인",
    rewardPointsLabel = "획득 포인트",
    taskCompletionLabel = "미션 완료",
    keywordsLabel = "키워드",
    hiddenTaskLabel = "숨은 미션",
    hiddenTaskStatus = { status ->
        when (status) {
            TaskStatus.Completed -> "완료"
            TaskStatus.Skipped -> "건너뜀"
            TaskStatus.Pending -> "대기 중"
        }
    },
)

private val esCompletionCard = enCompletionCard.copy(
    body = "Juntos convertimos hoy en una pequeña película",
    episodeTitleLabel = "Título",
    completionTitleLabel = "Título final",
    durationLabel = "Duración",
    dateLabel = "Fecha",
    relationshipValueLabel = "Valor de relación",
    routeTypeLabel = "Tipo de ruta",
    routeQuestFallback = "Ruta",
    stopCheckInLabel = "Check-ins",
    rewardPointsLabel = "Puntos ganados",
    taskCompletionLabel = "Tareas hechas",
    keywordsLabel = "Palabras clave",
    hiddenTaskLabel = "Tarea oculta",
    hiddenTaskStatus = { status ->
        when (status) {
            TaskStatus.Completed -> "Completada"
            TaskStatus.Skipped -> "Saltada"
            TaskStatus.Pending -> "Pendiente"
        }
    },
)
