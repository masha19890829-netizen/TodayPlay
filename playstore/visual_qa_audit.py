#!/usr/bin/env python3
"""Audit TodayPlay visual QA screenshot evidence in dist.

The script intentionally uses only the Python standard library so it can run in
release folders without installing image packages.
"""

from __future__ import annotations

import argparse
import json
import re
import struct
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable


PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"
VERSION_RE = re.compile(r"v(\d+)\.(\d+)\.(\d+)", re.IGNORECASE)


@dataclass(frozen=True)
class PngInfo:
    path: Path
    width: int
    height: int
    bytes_size: int


@dataclass(frozen=True)
class CheckResult:
    key: str
    label: str
    status: str
    message: str
    path: str | None = None
    width: int | None = None
    height: int | None = None


@dataclass(frozen=True)
class EvidenceSpec:
    key: str
    label: str
    required_tokens: tuple[str, ...]
    excluded_tokens: tuple[str, ...] = ()
    orientation: str | None = None
    min_width: int = 320
    min_height: int = 320


FIXED_SCREENSHOTS = [
    EvidenceSpec(
        "fixed_01_splash",
        "01 launch / first impression",
        ("final", "splash", "art"),
    ),
    EvidenceSpec(
        "fixed_02_home",
        "02 chat-first home",
        ("final", "home", "art"),
        ("small", "fold", "foldable", "landscape", "tablet"),
        "portrait",
    ),
    EvidenceSpec(
        "fixed_03_candidate_cards",
        "03 AI candidate cards",
        ("candidate", "cards", "art"),
    ),
    EvidenceSpec(
        "fixed_04_loading",
        "04 AI loading / thinking",
        ("final", "loading", "art"),
    ),
    EvidenceSpec(
        "fixed_05_result",
        "05 generated route result",
        ("final", "result", "motion"),
    ),
    EvidenceSpec(
        "fixed_06_current_stop",
        "06 current stop actions",
        ("current", "stop", "actions"),
    ),
]

DEVICE_EVIDENCE = [
    EvidenceSpec(
        "device_normal_phone",
        "normal phone portrait evidence",
        ("final", "home", "art"),
        ("small", "fold", "foldable", "landscape", "tablet"),
        "portrait",
    ),
    EvidenceSpec(
        "device_small_screen",
        "small phone / narrow evidence",
        ("small", "home", "art"),
        ("fold", "foldable", "landscape", "tablet"),
        "portrait",
    ),
    EvidenceSpec(
        "device_foldable",
        "foldable evidence",
        ("foldable", "home", "art"),
        ("landscape",),
    ),
    EvidenceSpec(
        "device_landscape",
        "landscape evidence",
        ("landscape", "home", "art"),
        (),
        "landscape",
    ),
]


def normalize_version(value: str | None) -> str | None:
    if value is None:
        return None
    value = value.strip()
    if not value:
        return None
    return value if value.lower().startswith("v") else f"v{value}"


def version_tuple(path: Path) -> tuple[int, int, int] | None:
    match = VERSION_RE.search(path.name)
    if not match:
        return None
    return tuple(int(part) for part in match.groups())


def version_text(version: tuple[int, int, int]) -> str:
    return f"v{version[0]}.{version[1]}.{version[2]}"


def discover_version(pngs: Iterable[Path]) -> str | None:
    versions = [version_tuple(path) for path in pngs]
    versions = [version for version in versions if version is not None]
    if not versions:
        return None
    return version_text(max(versions))


def read_png_info(path: Path) -> PngInfo:
    data = path.read_bytes()
    if len(data) < 33:
        raise ValueError("file is too small to contain a PNG IHDR chunk")
    if data[:8] != PNG_SIGNATURE:
        raise ValueError("missing PNG signature")
    if data[12:16] != b"IHDR":
        raise ValueError("missing PNG IHDR chunk")
    width, height = struct.unpack(">II", data[16:24])
    if width <= 0 or height <= 0:
        raise ValueError("PNG dimensions must be greater than zero")
    return PngInfo(path=path, width=width, height=height, bytes_size=len(data))


def matching_paths(pngs: Iterable[Path], spec: EvidenceSpec) -> list[Path]:
    matches: list[Path] = []
    for path in pngs:
        name = path.name.lower()
        if all(token in name for token in spec.required_tokens) and not any(
            token in name for token in spec.excluded_tokens
        ):
            matches.append(path)
    return sorted(matches, key=lambda item: (item.stat().st_mtime, item.name), reverse=True)


def orientation_pass(info: PngInfo, orientation: str | None) -> bool:
    if orientation == "portrait":
        return info.height > info.width
    if orientation == "landscape":
        return info.width > info.height
    return True


def evaluate_spec(pngs: Iterable[Path], spec: EvidenceSpec) -> CheckResult:
    paths = matching_paths(pngs, spec)
    if not paths:
        tokens = " ".join(spec.required_tokens)
        return CheckResult(spec.key, spec.label, "fail", f"missing PNG matching: {tokens}")

    path = paths[0]
    try:
        info = read_png_info(path)
    except OSError as exc:
        return CheckResult(spec.key, spec.label, "fail", f"cannot read PNG: {exc}", str(path))
    except ValueError as exc:
        return CheckResult(spec.key, spec.label, "fail", str(exc), str(path))

    if info.width < spec.min_width or info.height < spec.min_height:
        return CheckResult(
            spec.key,
            spec.label,
            "fail",
            f"PNG is below minimum size {spec.min_width}x{spec.min_height}",
            str(path),
            info.width,
            info.height,
        )

    if not orientation_pass(info, spec.orientation):
        return CheckResult(
            spec.key,
            spec.label,
            "fail",
            f"expected {spec.orientation} orientation",
            str(path),
            info.width,
            info.height,
        )

    return CheckResult(
        spec.key,
        spec.label,
        "pass",
        "valid PNG evidence",
        str(path),
        info.width,
        info.height,
    )


def collect_pngs(dist: Path, version: str | None) -> list[Path]:
    pngs = [path for path in dist.rglob("*.png") if path.is_file()]
    if version is None:
        version = discover_version(pngs)
    if version is None:
        return pngs
    version_lower = version.lower()
    return [path for path in pngs if version_lower in path.name.lower()]


def print_markdown(root: Path, dist: Path, version: str | None, results: list[CheckResult]) -> None:
    overall = "pass" if all(result.status == "pass" for result in results) else "fail"
    print("# TodayPlay Visual QA Audit")
    print()
    print(f"- Project: `{root}`")
    print(f"- Dist: `{dist}`")
    print(f"- Version filter: `{version or 'auto/all'}`")
    print(f"- Overall: `{overall}`")
    print()
    print("| Check | Status | Dimensions | Evidence | Message |")
    print("| --- | --- | --- | --- | --- |")
    for result in results:
        dimensions = (
            f"{result.width}x{result.height}"
            if result.width is not None and result.height is not None
            else "-"
        )
        evidence = result.path or "-"
        print(
            f"| {result.label} | `{result.status}` | {dimensions} | {evidence} | {result.message} |"
        )


def parse_args(argv: list[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Check TodayPlay dist screenshots for visual QA evidence."
    )
    parser.add_argument(
        "project_root",
        nargs="?",
        default=".",
        help="Project root. Defaults to the current directory.",
    )
    parser.add_argument(
        "--dist",
        default="dist",
        help="Dist directory, relative to project root unless absolute. Defaults to dist.",
    )
    parser.add_argument(
        "--version",
        default=None,
        help="Version filter such as v0.9.63. Defaults to the highest version found in PNG names.",
    )
    parser.add_argument(
        "--json",
        action="store_true",
        help="Print JSON instead of Markdown.",
    )
    return parser.parse_args(argv)


def main(argv: list[str] | None = None) -> int:
    args = parse_args(argv or sys.argv[1:])
    root = Path(args.project_root).resolve()
    dist = Path(args.dist)
    if not dist.is_absolute():
        dist = root / dist
    dist = dist.resolve()
    version = normalize_version(args.version)

    if not dist.exists():
        result = CheckResult("dist", "dist directory", "fail", "dist directory does not exist", str(dist))
        results = [result]
    else:
        all_pngs = [path for path in dist.rglob("*.png") if path.is_file()]
        if version is None:
            version = discover_version(all_pngs)
        pngs = [path for path in all_pngs if version is None or version.lower() in path.name.lower()]
        specs = FIXED_SCREENSHOTS + DEVICE_EVIDENCE
        results = [evaluate_spec(pngs, spec) for spec in specs]

    if args.json:
        payload = {
            "project_root": str(root),
            "dist": str(dist),
            "version": version,
            "overall": "pass" if all(result.status == "pass" for result in results) else "fail",
            "checks": [result.__dict__ for result in results],
        }
        print(json.dumps(payload, ensure_ascii=False, indent=2))
    else:
        print_markdown(root, dist, version, results)

    return 0 if all(result.status == "pass" for result in results) else 1


if __name__ == "__main__":
    raise SystemExit(main())
