#!/usr/bin/env python3
"""Audit route-generation UX and threading safety.

The production route flow may call HTTPS travel-content services. Generation
must therefore run outside the main thread, expose visible loading/failure
states, and keep loading copy localized.
"""

from __future__ import annotations

import sys
from pathlib import Path


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8-sig")
    except FileNotFoundError:
        return ""


def status(value: bool) -> str:
    return "pass" if value else "fail"


def main() -> int:
    root = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else Path.cwd().resolve()
    app_root = root / "app/src/main/java/com/todayplay/app"
    view_model = app_root / "TodayPlayViewModel.kt"
    main_activity = app_root / "MainActivity.kt"
    loading_screen = app_root / "ui/screens/LoadingScreen.kt"
    locale_file = app_root / "localization/TodayPlayLocale.kt"

    view_model_text = read_text(view_model)
    main_text = read_text(main_activity)
    loading_text = read_text(loading_screen)
    locale_text = read_text(locale_file)

    checks = [
        (
            "Generation runs off main thread",
            "viewModelScope.launch" in view_model_text
            and "withContext(Dispatchers.IO)" in view_model_text
            and "repository.generate(input)" in view_model_text,
            str(view_model),
            "Route generation may call HTTPS content services and must run on Dispatchers.IO.",
        ),
        (
            "Generation exposes state machine",
            "enum class GenerationStatus" in view_model_text
            and "data class GenerationUiState" in view_model_text
            and "GenerationStatus.Generating" in view_model_text
            and "GenerationStatus.Succeeded" in view_model_text
            and "GenerationStatus.Failed" in view_model_text,
            str(view_model),
            "Expose explicit generating, success, and failure states for the loading UI.",
        ),
        (
            "Cancellation is not logged as failure",
            "CancellationException" in view_model_text
            and "if (error is CancellationException) throw error" in view_model_text,
            str(view_model),
            "User-cancelled generation should not be counted as a failed content request.",
        ),
        (
            "Loading screen observes ViewModel state",
            "generationState = viewModel.generationState" in main_text
            and "onRetry = viewModel::retryGeneration" in main_text
            and "viewModel.cancelGeneration()" in main_text,
            str(main_activity),
            "The loading screen should observe generation state and support retry/back actions.",
        ),
        (
            "Regenerate uses async path",
            'startGeneration(input, "result_regenerate")' in main_text
            and "viewModel.generateFromLatestInput()" not in main_text,
            str(main_activity),
            "Regeneration from the result page should use the same async route-generation path.",
        ),
        (
            "Loading UI is not a fake timer",
            "Random.nextLong" not in loading_text
            and "GenerationStatus.Succeeded" in loading_text
            and "GenerationStatus.Failed" in loading_text
            and "onRetry" in loading_text,
            str(loading_screen),
            "Loading should react to real generation completion/failure instead of ending on a random timer.",
        ),
        (
            "Loading copy is localized",
            "LocalTodayPlayStrings" in loading_text
            and "strings.loadingTitle" in loading_text
            and "strings.loadingLines" in loading_text
            and locale_text.count("loadingTitle =") >= 6
            and locale_text.count("loadingRetry =") >= 6,
            str(locale_file),
            "The core generation screen needs localized loading, failure, retry, and back copy for all launch locales.",
        ),
    ]

    overall = "pass" if all(item[1] for item in checks) else "fail"
    print("# Generation Flow Audit")
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
