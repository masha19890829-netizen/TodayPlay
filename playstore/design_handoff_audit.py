from __future__ import annotations

import struct
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]


REQUIRED_FILES = [
    "DESIGN_INTERACTION_BLUEPRINT_V0_9_71_2026-06-20.md",
    "DESIGN_INTERACTION_BLUEPRINT_V0_9_73_2026-06-23.md",
    "DESIGN_INTERACTION_BLUEPRINT_V0_9_74_2026-06-24.md",
    "VERSION_ITERATION_PLAN_V0_9_75_2026-06-30.md",
    "TEAM_SYNC_V0_9_75_2026-06-30.md",
    "docs/design/v0.9.71/DESIGN_REVIEW_V0_9_71_2026-06-20.md",
    "docs/design/v0.9.71/MOTION_INTERACTION_SPEC_V0_9_71_2026-06-20.md",
    "docs/design/v0.9.71/ANDROID_HANDOFF_V0_9_71_2026-06-20.md",
    "docs/design/v0.9.73/DESIGN_REVIEW_V0_9_73_2026-06-23.md",
    "docs/design/v0.9.73/MOTION_INTERACTION_SPEC_V0_9_73_2026-06-23.md",
    "docs/design/v0.9.73/ANDROID_HANDOFF_V0_9_73_2026-06-23.md",
    "docs/design/v0.9.73/todayplay-v0.9.73-quiet-waterfall-board.svg",
    "docs/design/v0.9.73/todayplay-v0.9.73-quiet-waterfall-board.png",
    "docs/design/v0.9.74/DESIGN_REVIEW_V0_9_74_2026-06-24.md",
    "docs/design/v0.9.74/MOTION_INTERACTION_SPEC_V0_9_74_2026-06-24.md",
    "docs/design/v0.9.74/ANDROID_HANDOFF_V0_9_74_2026-06-24.md",
    "docs/design/v0.9.74/QA_ACCEPTANCE_V0_9_74_2026-06-24.md",
    "docs/design/v0.9.74/todayplay-v0.9.74-silent-route-poster-board.svg",
    "docs/design/v0.9.74/todayplay-v0.9.74-silent-route-poster-board.png",
    "docs/design/v0.9.75/V0_9_75_UI_CREATIVE_BRIEF_2026-06-30.md",
    "docs/design/v0.9.71/todayplay-v0.9.71-design-board.svg",
    "docs/design/v0.9.71/todayplay-v0.9.71-design-board.png",
    "docs/design/v0.9.71/todayplay-v0.9.71-responsive-states.svg",
    "docs/design/v0.9.71/todayplay-v0.9.71-responsive-states.png",
    "docs/design/v0.9.71/screens/todayplay-v0.9.71-screen-handoff.svg",
    "docs/design/v0.9.71/screens/todayplay-v0.9.71-screen-handoff.png",
    "docs/design/v0.9.71/screens/todayplay-v0.9.71-home-default.png",
    "docs/design/v0.9.71/screens/todayplay-v0.9.71-home-filtered.png",
    "docs/design/v0.9.71/screens/todayplay-v0.9.71-card-detail.png",
    "docs/design/v0.9.71/screens/todayplay-v0.9.71-input-focus.png",
    "docs/design/v0.9.71/screens/todayplay-v0.9.71-generation-keyframe.png",
    "docs/design/v0.9.71/screens/todayplay-v0.9.71-result-first-screen.png",
]


REQUIRED_TOKENS = {
    "DESIGN_INTERACTION_BLUEPRINT_V0_9_71_2026-06-20.md": [
        "今天想出去，先刷一张能玩的路线卡",
        "首屏除卡片外总文字控制在 40 个中文字符以内",
        "折叠屏和小屏禁止出现单字独占一行",
    ],
    "docs/design/v0.9.71/MOTION_INTERACTION_SPEC_V0_9_71_2026-06-20.md": [
        "不追求炫技",
        "筛选同城",
        "不做重视频",
    ],
    "docs/design/v0.9.71/ANDROID_HANDOFF_V0_9_71_2026-06-20.md": [
        "WaterfallRouteCard",
        "PaperRevealTransition",
        "结果页继承候选卡标题、城市、站点",
    ],
    "DESIGN_INTERACTION_BLUEPRINT_V0_9_73_2026-06-23.md": [
        "首屏非卡片文字总量不超过 28 个中文字符",
        "每张首屏卡最多 6 个可见信息点",
        "生成后出现 3 张候选路线卡",
        "任何 320dp、小屏、折叠屏外屏不得出现单字独占一行",
        "V0.9.73 不允许只验证首页",
    ],
    "docs/design/v0.9.73/DESIGN_REVIEW_V0_9_73_2026-06-23.md": [
        "Quiet Waterfall",
        "卡片字段从“信息完整”改为“先吸引点击”",
        "不让卡片同时显示 3 个等权文字按钮",
    ],
    "docs/design/v0.9.73/MOTION_INTERACTION_SPEC_V0_9_73_2026-06-23.md": [
        "不使用转圈 loading",
        "听懂今天",
        "筛同城站点",
        "剪成路线",
    ],
    "docs/design/v0.9.73/ANDROID_HANDOFF_V0_9_73_2026-06-23.md": [
        "QuietRouteCard",
        "OneSentencePromptSheet",
        "PaperRouteRevealTransition",
        "LiveRouteFirstScreen",
        "结果页存在宽屏",
    ],
    "docs/design/v0.9.73/todayplay-v0.9.73-quiet-waterfall-board.svg": [
        "少字、轻转场、静默瀑布流",
        "自己说一句",
        "MAP ROUTE",
    ],
    "DESIGN_INTERACTION_BLUEPRINT_V0_9_74_2026-06-24.md": [
        "Silent Route Poster / 静默路线海报",
        "点一张，今天就走。",
        "每张首屏卡最多 5 个可见信息点",
        "不直接进入结果页",
        "开发准入",
    ],
    "docs/design/v0.9.74/DESIGN_REVIEW_V0_9_74_2026-06-24.md": [
        "把路线卡从“信息卡”改成“可点击的路线封面”",
        "小屏首张卡高度控制在 360-390dp",
        "不在地图里放长时间句",
    ],
    "docs/design/v0.9.74/MOTION_INTERACTION_SPEC_V0_9_74_2026-06-24.md": [
        "Silent Route Poster Motion",
        "不使用转圈 loading",
        "听懂今天",
        "筛同城点",
        "成路线",
    ],
    "docs/design/v0.9.74/ANDROID_HANDOFF_V0_9_74_2026-06-24.md": [
        "SilentRoutePosterHome",
        "SilentRoutePosterCard",
        "OneLineRewriteSheet",
        "AiCandidatePosterCard",
        "LiveRouteMapFirstScreen",
    ],
    "docs/design/v0.9.74/QA_ACCEPTANCE_V0_9_74_2026-06-24.md": [
        "小屏和横屏第一眼也要完整看见可点的路线卡",
        "不允许出现单字孤行、乱码、半截字、中英残片省略",
        "visual_qa_audit.py --version v0.9.74",
    ],
    "docs/design/v0.9.74/todayplay-v0.9.74-silent-route-poster-board.svg": [
        "Silent Route Poster / 静默路线海报",
        "01 HOME",
        "02 ONE LINE",
        "03 CANDIDATES",
        "04 MAP FIRST",
    ],
    "VERSION_ITERATION_PLAN_V0_9_75_2026-06-30.md": [
        "一句话，把今天剪成可变路线",
        "Private Cinema Ticket / 今日私人电影票",
        "3 张候选路线海报",
        "路线必须继承",
        "visual_qa_audit.py --version v0.9.75",
    ],
    "TEAM_SYNC_V0_9_75_2026-06-30.md": [
        "V0.9.75 以“一句话，把今天剪成可变路线”为主线",
        "产品组汇报",
        "UI 组汇报",
        "QA 组汇报",
        "开发准入条件",
    ],
    "docs/design/v0.9.75/V0_9_75_UI_CREATIVE_BRIEF_2026-06-30.md": [
        "Private Cinema Ticket / 今日私人电影票",
        "禁止单字孤行",
        "三段显影",
        "折叠内屏",
        "设计交付清单",
    ],
}


def png_size(path: Path) -> tuple[int, int] | None:
    data = path.read_bytes()
    if data[:8] != b"\x89PNG\r\n\x1a\n":
        return None
    if data[12:16] != b"IHDR":
        return None
    return struct.unpack(">II", data[16:24])


def main() -> int:
    rows: list[tuple[str, str, str]] = []
    ok = True

    for relative in REQUIRED_FILES:
        path = ROOT / relative
        if not path.exists():
            rows.append((relative, "fail", "missing"))
            ok = False
            continue
        if path.suffix.lower() == ".png":
            size = png_size(path)
            if size is None:
                rows.append((relative, "fail", "invalid PNG signature"))
                ok = False
            else:
                width, height = size
                if width < 400 or height < 800:
                    rows.append((relative, "fail", f"too small: {width}x{height}"))
                    ok = False
                else:
                    rows.append((relative, "pass", f"{width}x{height}"))
        else:
            text = path.read_text(encoding="utf-8")
            text_for_mojibake_scan = text.replace("`�`", "").replace("`ï¿½`", "").replace("`����`", "")
            if any(marker in text_for_mojibake_scan for marker in ["锛", "鈥", "�", "涓", "鐨"]):
                rows.append((relative, "fail", "mojibake marker found"))
                ok = False
            else:
                rows.append((relative, "pass", "text ok"))

    for relative, tokens in REQUIRED_TOKENS.items():
        text = (ROOT / relative).read_text(encoding="utf-8")
        missing = [token for token in tokens if token not in text]
        if missing:
            rows.append((relative, "fail", "missing tokens: " + ", ".join(missing)))
            ok = False
        else:
            rows.append((relative, "pass", "required design tokens present"))

    print("# TodayPlay Design Handoff Audit\n")
    print(f"- Project: `{ROOT}`")
    print(f"- Overall: `{'pass' if ok else 'fail'}`\n")
    print("| Asset | Status | Detail |")
    print("| --- | --- | --- |")
    for asset, status, detail in rows:
        print(f"| `{asset}` | `{status}` | {detail} |")

    return 0 if ok else 1


if __name__ == "__main__":
    raise SystemExit(main())
