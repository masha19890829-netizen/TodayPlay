#!/usr/bin/env python3
"""Audit the active generated-route display copy path.

The route generator still contains legacy literals kept for compatibility while
the active result flow uses safe display-copy adapters. This audit checks that
visible generated fields are wired to the safe path and that the safe display
copy block does not contain obvious mojibake fragments.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path


GENERATOR = "app/src/main/java/com/todayplay/app/generator/LocalItineraryGenerator.kt"

REQUIRED_ACTIVE_CALLS = [
    "copy.safePlanTypeFor",
    "copy.safeTitleFor",
    "copy.safeRouteSummaryFor",
    "copy.safeEstimateCost",
    "copy.safeCrowdRiskFor",
    "copy.safeRainBackupFor",
    "copy.safeBestPhotoTimeFor",
    "copy.safeRewardPolicy",
    "copy.safeComplianceSummary",
    "copy.safeComplianceLimitations",
    "copy.safeLocalizeSourcePolicy",
    "copy.safeCoverageNoteFor",
    "copy.safeDefaultCity",
    "copy.safeDefaultTransport",
    "copy.safePrimaryUserLabel",
    "copy.safeCompanionUserLabel",
    "copy.safeGroupLabelFor",
    "copy.safeConflictNotes",
    "copy.safeCheckInTitle",
    "copy.safeCheckInDescriptionFor",
    "copy.safePhotoSuggestionFor",
    "copy.safeSpendingSuggestionFor",
    "copy.safeBackupPlanFor",
    "copy.safeWhyForGroup",
]

REQUIRED_POI_CALLS = [
    "safePoiName(poi)",
    "safeRecommendationFor(poi)",
    "safeRiskTipsFor(poi)",
    "safeLocalizedSourceLabel()",
]

FORBIDDEN_ACTIVE_CALLS = [
    "copy.planTypeFor",
    "copy.titleFor",
    "copy.routeSummaryFor",
    "copy.estimateCost",
    "copy.crowdRiskFor",
    "copy.rainBackupFor",
    "copy.bestPhotoTimeFor",
    "copy.rewardPolicy",
    "copy.complianceSummary",
    "copy.complianceLimitations",
    "copy.localizeSourcePolicy",
    "copy.coverageNoteFor",
    "copy.defaultCity",
    "copy.defaultTransport",
    "copy.primaryUserLabel",
    "copy.companionUserLabel",
    "copy.groupLabelFor",
    "copy.conflictNotes",
    "copy.checkInTitle",
    "copy.checkInDescriptionFor",
    "copy.photoSuggestionFor",
    "copy.spendingSuggestionFor",
    "copy.backupPlanFor",
    "copy.whyForGroup",
]

FORBIDDEN_POI_CALLS = [
    "poiName(poi)",
    "recommendationFor(poi)",
    "riskTipsFor(poi)",
    "sourceLabel = localizedSourceLabel()",
]

SAFE_COPY_TOKENS = [
    "val safeDefaultCity",
    "val safeRewardPolicy",
    "fun safeCoverageNoteFor",
    "fun safePlanTypeFor",
    "fun safeRouteSummaryFor",
    "fun safeCheckInTitle",
    "fun safePoiName",
    "fun safeRecommendationFor",
    "fun safeRiskTipsFor",
    "fun safeLocalizedSourceLabel",
]

# These fragments are common symptoms of double-decoded Chinese/Japanese/Korean
# text in the legacy literals. Keep this check scoped to the safe copy block.
MOJIBAKE_FRAGMENTS = [
    "\u951b",  # 锛
    "\u9287",  # 銇
    "\u979a",  # 鞚
    "\u9428",  # 鐨
    "\u6d93",  # 涓
    "\u6bf5",  # 毵
    "\u9779",  # 靹
    "\u9803",  # 頃
]


def read_text(path: Path) -> str:
    return path.read_text(encoding="utf-8-sig")


def status(ok: bool) -> str:
    return "pass" if ok else "fail"


def section_between(text: str, start: str, end: str) -> str:
    start_index = text.find(start)
    end_index = text.find(end, start_index + len(start)) if start_index >= 0 else -1
    if start_index < 0 or end_index < 0:
        return ""
    return text[start_index:end_index]


def main() -> int:
    root = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else Path.cwd().resolve()
    generator_path = root / GENERATOR
    text = read_text(generator_path)

    active_flow = section_between(text, "class LocalItineraryGenerator", "private fun normalizeInterest")
    poi_flow = section_between(text, "fun localizePoi(poi: POI): POI", "private fun poiName")
    safe_copy = section_between(text, "val safeDefaultCity", "companion object")

    active_required_missing = [token for token in REQUIRED_ACTIVE_CALLS if token not in active_flow]
    active_forbidden_present = [token for token in FORBIDDEN_ACTIVE_CALLS if token in active_flow]
    poi_required_missing = [token for token in REQUIRED_POI_CALLS if token not in poi_flow]
    poi_forbidden_present = [token for token in FORBIDDEN_POI_CALLS if token in poi_flow]
    safe_copy_missing = [token for token in SAFE_COPY_TOKENS if token not in safe_copy]
    mojibake_present = [token for token in MOJIBAKE_FRAGMENTS if token in safe_copy]

    checks: list[tuple[str, bool, str, str]] = [
        (
            "Generator file exists",
            generator_path.exists(),
            str(generator_path),
            "Keep route generation in the expected Kotlin source file.",
        ),
        (
            "Active flow uses safe display copy",
            not active_required_missing,
            ", ".join(active_required_missing) if active_required_missing else "all required safe calls found",
            "Wire every visible generated route field through safe display-copy helpers.",
        ),
        (
            "Active flow avoids legacy display copy",
            not active_forbidden_present,
            ", ".join(active_forbidden_present) if active_forbidden_present else "no forbidden legacy calls found",
            "Do not call mojibake-prone legacy display-copy helpers from the active result flow.",
        ),
        (
            "POI localization uses safe display copy",
            not poi_required_missing,
            ", ".join(poi_required_missing) if poi_required_missing else "all required POI safe calls found",
            "Route POI display fields should use safe copy helpers before cards are built.",
        ),
        (
            "POI localization avoids legacy display copy",
            not poi_forbidden_present,
            ", ".join(poi_forbidden_present) if poi_forbidden_present else "no forbidden POI legacy calls found",
            "Do not use mojibake-prone legacy POI helpers in the selected POI display path.",
        ),
        (
            "Safe copy adapter coverage",
            not safe_copy_missing,
            ", ".join(safe_copy_missing) if safe_copy_missing else "safe copy helpers present",
            "Keep clean multilingual helpers for visible generated route result fields.",
        ),
        (
            "Safe copy block mojibake scan",
            not mojibake_present,
            ", ".join(mojibake_present) if mojibake_present else "no obvious mojibake fragments in safe copy block",
            "Replace corrupted text fragments in the active safe display-copy adapter.",
        ),
    ]

    overall = "pass" if all(item[1] for item in checks) else "fail"
    print("# Generated Route Display Audit")
    print()
    print(f"- Project: `{root}`")
    print(f"- Overall: `{overall}`")
    print()
    print("| Check | Status | Evidence | Required action |")
    print("| --- | --- | --- | --- |")
    for name, ok, evidence, action in checks:
        print(f"| {name} | `{status(ok)}` | {evidence} | {action} |")

    return 0 if overall == "pass" else 1


if __name__ == "__main__":
    raise SystemExit(main())
