#!/usr/bin/env python3
"""Audit the deployable privacy policy site for launch-locale readiness."""

from __future__ import annotations

import re
import sys
from pathlib import Path


REQUIRED_LOCALES = {
    "en": ["English", "TodayPlay helps users", "Google Play Billing"],
    "zh-CN": ["简体中文", "今天怎么玩", "本地"],
    "zh-TW": ["繁體中文", "今天怎麼玩", "本機"],
    "ja": ["日本語", "TodayPlay", "ローカル"],
    "ko": ["한국어", "TodayPlay", "로컬"],
    "es": ["Español", "TodayPlay", "local"],
}

REQUIRED_DISCLOSURES = [
    "does not request precise location",
    "does not force arrival verification",
    "does not scrape third-party platforms",
    "purchase token",
    "backend verification",
    "replace with real support email",
]

MOJIBAKE_MARKERS = [
    "锟",
    "浠婂",
    "绠€",
    "绻侀",
    "鏃ユ",
    "頃滉",
    "Espa帽",
    "relaci贸",
    "ubicaci贸",
    "鞚€",
    "銆",
]


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8-sig")
    except UnicodeDecodeError:
        return path.read_text(encoding="utf-8-sig", errors="replace")
    except FileNotFoundError:
        return ""


def status(value: bool) -> str:
    return "pass" if value else "fail"


def main() -> int:
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")

    root = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else Path.cwd().resolve()
    privacy_dir = root / "playstore" / "privacy_site"
    index = privacy_dir / "index.html"
    readme = privacy_dir / "README.md"
    headers = privacy_dir / "_headers"
    text = read_text(index)

    checks: list[tuple[str, bool, str]] = []
    checks.append(("Privacy site index exists", index.exists(), str(index)))
    checks.append(("Privacy site README exists", readme.exists(), str(readme)))
    checks.append(("Privacy site security headers exist", headers.exists(), str(headers)))

    for locale, tokens in REQUIRED_LOCALES.items():
        locale_present = f'id="{locale}"' in text and f'lang="{locale}"' in text
        tokens_present = all(token in text for token in tokens)
        checks.append((
            f"Privacy locale {locale}",
            locale_present and tokens_present,
            f"Section id/lang plus localized anchor copy and privacy body tokens: {', '.join(tokens)}",
        ))

    checks.append((
        "Required launch disclosures",
        all(token in text for token in REQUIRED_DISCLOSURES),
        "Local data deletion, no sensitive permissions, map fallback, Billing verification, mock/authorized-source policy, and placeholder-contact warning.",
    ))

    found_mojibake = [marker for marker in MOJIBAKE_MARKERS if marker in text]
    checks.append((
        "No common UTF-8 mojibake markers",
        not found_mojibake,
        "Found markers: " + ", ".join(found_mojibake) if found_mojibake else "No common mojibake markers found.",
    ))

    has_real_placeholder_warning = (
        "Production publisher name and support email must be inserted" in text
        and "Do not submit the app with placeholder contact information" in text
    )
    checks.append((
        "Placeholder contact warning",
        has_real_placeholder_warning,
        "The static privacy site must clearly block production use until real publisher/support identity is inserted.",
    ))

    anchors = re.findall(r'href="#([^"]+)"', text)
    checks.append((
        "Language navigation anchors",
        all(locale in anchors for locale in REQUIRED_LOCALES),
        "Navigation should expose every launch locale.",
    ))

    failed = [name for name, ok, _ in checks if not ok]
    print("# Privacy Site Audit")
    print()
    print(f"Project: `{root}`")
    print(f"Privacy site: `{privacy_dir}`")
    print(f"Overall: **{status(not failed)}**")
    print()
    print("| Check | Status | Detail |")
    print("| --- | --- | --- |")
    for name, ok, detail in checks:
        print(f"| {name} | {status(ok)} | {detail} |")
    print()
    if failed:
        print("## Required Fixes")
        for name in failed:
            print(f"- {name}")
        return 1

    print("## Notes")
    print("- This audit checks local deployable privacy material only; Google Play still requires a public HTTPS URL and real publisher/support identity before submission.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
