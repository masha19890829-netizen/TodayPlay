from __future__ import annotations

import struct
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]


REQUIRED_FILES = [
    "DESIGN_INTERACTION_BLUEPRINT_V0_9_71_2026-06-20.md",
    "docs/design/v0.9.71/DESIGN_REVIEW_V0_9_71_2026-06-20.md",
    "docs/design/v0.9.71/MOTION_INTERACTION_SPEC_V0_9_71_2026-06-20.md",
    "docs/design/v0.9.71/ANDROID_HANDOFF_V0_9_71_2026-06-20.md",
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
            if any(marker in text for marker in ["锛", "鈥", "�", "涓", "鐨"]):
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
