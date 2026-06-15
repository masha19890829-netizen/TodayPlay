#!/usr/bin/env python3
"""Audit TodayPlay travel-content production boundary."""

from __future__ import annotations

import re
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
    build_gradle = root / "app" / "build.gradle.kts"
    release_template = root / "release_config.template.properties"
    release_config = root / "release_config.properties"
    repository = root / "app/src/main/java/com/todayplay/app/data/TravelContentRepository.kt"
    generator = root / "app/src/main/java/com/todayplay/app/generator/LocalItineraryGenerator.kt"
    result_screen = root / "app/src/main/java/com/todayplay/app/ui/screens/QuestResultScreen.kt"
    result_copy = root / "app/src/main/java/com/todayplay/app/localization/ResultCopy.kt"
    contract = root / "playstore/travel_content_backend_contract.md"
    worker = root / "backend/travel-content-worker/worker.js"
    worker_readme = root / "backend/travel-content-worker/README.md"
    provider_adapter = root / "backend/travel-content-worker/adapters/not-configured-provider.js"
    provider_contract = root / "backend/travel-content-worker/contracts/provider-adapter-contract.md"
    provider_schema = root / "backend/travel-content-worker/contracts/provider-adapter-contract.schema.json"
    package_json = root / "backend/travel-content-worker/package.json"
    contract_test = root / "backend/travel-content-worker/tests/contract.test.mjs"

    build_text = read_text(build_gradle)
    template_text = read_text(release_template)
    local_config_text = read_text(release_config)
    repository_text = read_text(repository)
    generator_text = read_text(generator)
    result_text = read_text(result_screen)
    result_copy_text = read_text(result_copy)
    contract_text = read_text(contract).lower()
    worker_text = read_text(worker).lower()
    readme_text = read_text(worker_readme).lower()
    adapter_text = read_text(provider_adapter).lower()
    provider_contract_text = read_text(provider_contract).lower()
    provider_schema_text = read_text(provider_schema)
    provider_schema_lower = provider_schema_text.lower()
    package_text = read_text(package_json).lower()
    test_text = read_text(contract_test).lower()

    endpoint_match = re.search(r'TRAVEL_CONTENT_BASE_URL"\s*,\s*"\\"([^"]*)\\""', build_text)
    endpoint = endpoint_match.group(1) if endpoint_match else None
    endpoint_configured = bool(endpoint_match) or (
        'buildConfigField("String", "TRAVEL_CONTENT_BASE_URL"' in build_text
        and 'releaseConfigValue("TRAVEL_CONTENT_BASE_URL")' in build_text
    )
    endpoint_disabled = (
        endpoint == ""
        or (
            "TRAVEL_CONTENT_BASE_URL=" in template_text
            and (
                not release_config.exists()
                or "TRAVEL_CONTENT_BASE_URL=https://" not in local_config_text
            )
        )
    )

    checks = [
        (
            "Android travel endpoint config",
            endpoint_configured,
            f"TRAVEL_CONTENT_BASE_URL={endpoint!r}, releaseConfigTemplate={release_template.exists()}",
            "Add BuildConfig.TRAVEL_CONTENT_BASE_URL and keep it empty or HTTPS-configured through ignored release config.",
        ),
        (
            "Endpoint disabled for prototype",
            endpoint_disabled,
            f"TRAVEL_CONTENT_BASE_URL={endpoint!r}, localConfigPresent={release_config.exists()}",
            "Do not enable global live search until provider contracts, backend auth, and privacy review are complete.",
        ),
        (
            "Repository factory boundary",
            "TravelContentRepositoryFactory" in repository_text
            and "RemoteTravelContentRepository" in repository_text
            and "LocalMockTravelContentRepository" in repository_text,
            str(repository),
            "Keep remote travel content behind a factory with local mock fallback.",
        ),
        (
            "Android remote travel HTTPS client",
            "HttpURLConnection" in repository_text
            and 'URL("$endpoint/travel/poi/search")' in repository_text
            and 'url.protocol != "https"' in repository_text
            and "REMOTE_CONNECT_TIMEOUT_MS" in repository_text
            and "REMOTE_READ_TIMEOUT_MS" in repository_text,
            str(repository),
            "When TRAVEL_CONTENT_BASE_URL is configured, Android should call the backend over HTTPS with short timeouts.",
        ),
        (
            "Android remote response safety",
            "parsePoiSearchResponse" in repository_text
            and 'status != "verified"' in repository_text
            and "parsed.pois.isEmpty() || parsed.isMock" in repository_text
            and "withRemoteFallback" in repository_text,
            str(repository),
            "Remote content must be verified, non-mock, and safely fall back to local mock content on failures.",
        ),
        (
            "Android remote source metadata parsing",
            "ContentSource(" in repository_text
            and "toSourcePolicy" in repository_text
            and "sourcePolicy" in repository_text
            and "riskTips" in repository_text
            and "requiresOfficialVerification" in repository_text,
            str(repository),
            "Remote POIs must preserve source policy, risk tips, and official-verification metadata.",
        ),
        (
            "Generator uses repository factory",
            "TravelContentRepositoryFactory.create()" in generator_text,
            str(generator),
            "Route generation should not instantiate the mock catalog directly.",
        ),
        (
            "Result page shows disabled live-search state",
            (
                "copy.globalContentNotConfigured" in result_text
                or "未配置：全球内容服务" in result_text
            )
            and "globalContentNotConfigured" in result_copy_text
            and "TRAVEL_CONTENT_BASE_URL" in result_text,
            str(result_screen),
            "Show users that global live search is not configured in the prototype.",
        ),
        (
            "Backend travel worker skeleton",
            worker.exists()
            and provider_adapter.exists()
            and "/travel/cities/search" in worker_text
            and "/travel/poi/search" in worker_text
            and "/travel/itinerary/plan" in worker_text
            and "/travel/content/import-link" in worker_text
            and "/travel/sources/policies" in worker_text,
            str(worker),
            "Provide route-content backend endpoints before enabling production global search.",
        ),
        (
            "Provider adapter boundary",
            "not-configured-provider" in worker_text
            and "export function searchCities" in read_text(provider_adapter)
            and "export function searchPois" in read_text(provider_adapter)
            and "export function planItinerary" in read_text(provider_adapter)
            and "export function importAuthorizedLink" in read_text(provider_adapter),
            str(provider_adapter),
            "Keep provider integrations behind adapter functions so official APIs and licensed providers can be added without changing route contracts.",
        ),
        (
            "Travel worker contract tests",
            package_json.exists()
            and contract_test.exists()
            and "node --test" in package_text
            and "/travel/poi/search" in test_text
            and "/travel/itinerary/plan" in test_text
            and "no scraping" in test_text,
            str(contract_test),
            "Keep no-dependency contract tests for travel-content endpoints and source-policy behavior.",
        ),
        (
            "Provider adapter contract document",
            provider_contract.exists()
            and all(term in provider_contract_text for term in [
                "official_map_api",
                "licensed_poi_dataset",
                "merchant_catalog",
                "user_authorized_link",
                "contentsource",
                "sourcepolicy",
                "imageasset",
                "routestop",
                "itineraryplan",
                "unsupported claims",
                "release gate",
            ]),
            str(provider_contract),
            "Document every production provider adapter input/output field and unsupported claim boundary.",
        ),
        (
            "Provider adapter JSON schema",
            provider_schema.exists()
            and all(term in provider_schema_lower for term in [
                '"official_map_api"',
                '"licensed_poi_dataset"',
                '"merchant_catalog"',
                '"user_authorized_link"',
                '"contentsource"',
                '"sourcepolicy"',
                '"imageassets"',
                '"navigationaction"',
                '"compliancenote"',
                '"unsupportedclaims"',
                '"noscraping"',
            ])
            and '"const": true' in provider_schema_lower,
            str(provider_schema),
            "Keep a machine-readable adapter schema before enabling any live global content provider.",
        ),
        (
            "No scraping policy",
            "no client-side scraping" in contract_text
            and "no scraping" in readme_text
            and "no scraping" in adapter_text,
            str(contract),
            "Keep the no-scraping policy present in contract, backend README, and worker responses.",
        ),
        (
            "Authorized source terms",
            all(term in contract_text for term in ["official api", "licensed", "merchant", "user-authorized"])
            and "official apis" in readme_text
            and "provider-adapter-contract.schema.json" in readme_text
            and "licensed" in adapter_text,
            str(contract),
            "Document official APIs, licensed datasets, merchant partnerships, and user-authorized links.",
        ),
    ]

    overall = "pass" if all(item[1] for item in checks) else "fail"
    print("# Travel Content Audit")
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
