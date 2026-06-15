#!/usr/bin/env python3
"""Audit launch-locale source files for UTF-8 and common mojibake markers."""

from __future__ import annotations

import sys
from pathlib import Path


CHECK_GLOBS = [
    "app/src/main/java/com/todayplay/app/localization/*.kt",
    "app/src/main/java/com/todayplay/app/ui/screens/*.kt",
    "app/src/main/res/values*/strings.xml",
    "playstore/localized/*/store_listing.md",
    "playstore/privacy_site/index.html",
]

MOJIBAKE_MARKERS = [
    "\ufffd",
    "锟",
    "浠婂",
    "绠€",
    "绻侀",
    "鏃ユ",
    "頃滉",
    "Espa帽",
    "relaci贸",
    "ubicaci贸",
    "verificaci贸",
    "pol铆tica",
    "p煤blica",
    "鞚€",
    "銆",
    "鈾",
    "鈥",
]

REQUIRED_TOKENS = {
    "app/src/main/java/com/todayplay/app/localization/TodayPlayLocale.kt": [
        "简体中文",
        "繁體中文",
        "日本語",
        "한국어",
        "Español",
    ],
    "app/src/main/java/com/todayplay/app/localization/PrivacyCopy.kt": [
        "隐私与本地数据",
        "隱私與本機資料",
        "プライバシー",
        "개인정보",
        "Privacidad",
    ],
    "playstore/privacy_site/index.html": [
        "简体中文",
        "繁體中文",
        "日本語",
        "한국어",
        "Español",
    ],
}


def read_utf8(path: Path) -> tuple[str, str | None]:
    try:
        return path.read_text(encoding="utf-8-sig"), None
    except UnicodeDecodeError as exc:
        return "", str(exc)


def status(value: bool) -> str:
    return "pass" if value else "fail"


def main() -> int:
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")

    root = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else Path.cwd().resolve()
    files: list[Path] = []
    for pattern in CHECK_GLOBS:
        files.extend(sorted(root.glob(pattern)))
    files = sorted(set(files))

    checks: list[tuple[str, bool, str]] = []
    bad_files: list[str] = []
    marker_hits: list[str] = []

    for path in files:
        rel = path.relative_to(root).as_posix()
        text, decode_error = read_utf8(path)
        if decode_error:
            bad_files.append(f"{rel}: {decode_error}")
            continue
        hits = [marker for marker in MOJIBAKE_MARKERS if marker in text]
        if hits:
            marker_hits.append(f"{rel}: {', '.join(hits)}")

    for rel, tokens in REQUIRED_TOKENS.items():
        path = root / rel
        text, decode_error = read_utf8(path)
        checks.append((
            f"Required localized tokens in {rel}",
            not decode_error and all(token in text for token in tokens),
            ", ".join(tokens),
        ))

    checks.extend([
        (
            "UTF-8 decoding",
            not bad_files,
            "All checked files decode as UTF-8." if not bad_files else "; ".join(bad_files),
        ),
        (
            "No common mojibake markers",
            not marker_hits,
            "No common mojibake markers found." if not marker_hits else "; ".join(marker_hits),
        ),
        (
            "Checked file coverage",
            len(files) >= 12,
            f"Checked {len(files)} localized/source/listing files.",
        ),
    ])

    failed = [name for name, ok, _ in checks if not ok]
    print("# Localized Source Encoding Audit")
    print()
    print(f"Project: `{root}`")
    print(f"Overall: **{status(not failed)}**")
    print()
    print("| Check | Status | Detail |")
    print("| --- | --- | --- |")
    for name, ok, detail in checks:
        print(f"| {name} | {status(ok)} | {detail} |")
    print()
    print("## Scope")
    for pattern in CHECK_GLOBS:
        print(f"- `{pattern}`")
    print()
    if failed:
        print("## Required Fixes")
        for name in failed:
            print(f"- {name}")
        return 1

    print("## Notes")
    print("- This audit validates source encoding and obvious mojibake markers; it does not replace native-speaker translation review.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
