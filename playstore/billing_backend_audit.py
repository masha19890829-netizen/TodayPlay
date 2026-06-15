#!/usr/bin/env python3
"""Audit TodayPlay Billing verification backend boundary."""

from __future__ import annotations

import sys
from pathlib import Path


PRODUCT_IDS = [
    "todayplay.plus.monthly",
    "todayplay.itinerary.premium.once",
    "todayplay.citypack.global",
    "todayplay.photo.positions",
]


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8-sig")
    except FileNotFoundError:
        return ""


def status(value: bool) -> str:
    return "pass" if value else "fail"


def main() -> int:
    root = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else Path.cwd().resolve()
    worker = root / "backend/billing-verify-worker/worker.js"
    readme = root / "backend/billing-verify-worker/README.md"
    package_json = root / "backend/billing-verify-worker/package.json"
    contract_test = root / "backend/billing-verify-worker/tests/contract.test.mjs"
    deployment_doc = root / "playstore/billing_backend_deployment.md"
    client_gateway = root / "app/src/main/java/com/todayplay/app/billing/PurchaseVerificationGateway.kt"
    play_billing_gateway = root / "app/src/main/java/com/todayplay/app/billing/PlayBillingGateway.kt"
    app_build = root / "app/build.gradle.kts"
    release_template = root / "release_config.template.properties"
    release_config = root / "release_config.properties"

    worker_text = read_text(worker)
    worker_lower = worker_text.lower()
    readme_lower = read_text(readme).lower()
    package_lower = read_text(package_json).lower()
    test_lower = read_text(contract_test).lower()
    deployment_lower = read_text(deployment_doc).lower()
    client_text = read_text(client_gateway)
    play_billing_text = read_text(play_billing_gateway)
    build_text = read_text(app_build)
    template_text = read_text(release_template)
    local_config_text = read_text(release_config)
    billing_endpoint_is_safely_empty = (
        'BILLING_VERIFY_ENDPOINT", "\\"\\""' in build_text
        or (
            "release_config.properties" in build_text
            and 'releaseConfigValue("BILLING_VERIFY_ENDPOINT")' in build_text
            and "BILLING_VERIFY_ENDPOINT=" in template_text
            and (
                not release_config.exists()
                or "BILLING_VERIFY_ENDPOINT=https://" not in local_config_text
            )
        )
    )

    checks = [
        (
            "Billing worker endpoints",
            worker.exists()
            and '"/health"' in worker_text
            and '"/billing/verify"' in worker_text
            and '"/entitlements"' in worker_text
            and '"/billing/notifications"' in worker_text,
            str(worker),
            "Provide health, verification, entitlement, and RTDN endpoints.",
        ),
        (
            "Google Play verification APIs",
            "androidpublisher.googleapis.com" in worker_lower
            and "purchases/products" in worker_lower
            and "purchases/subscriptionsv2" in worker_lower
            and "oauth2.googleapis.com/token" in worker_lower,
            str(worker),
            "Verify one-time products and subscriptions through Google Play Developer API.",
        ),
        (
            "Product catalog alignment",
            all(product_id in worker_text for product_id in PRODUCT_IDS)
            and all(product_id in read_text(readme) for product_id in PRODUCT_IDS),
            f"{worker}; {readme}",
            "Keep backend product IDs aligned with Android product constants and Play Console setup.",
        ),
        (
            "Secret boundary",
            "GOOGLE_SERVICE_ACCOUNT_EMAIL" in worker_text
            and "GOOGLE_PRIVATE_KEY" in worker_text
            and "Do not commit" in read_text(readme)
            and "BILLING_VERIFY_ENDPOINT" in build_text
            and billing_endpoint_is_safely_empty,
            f"{worker}; {readme}; {app_build}; {release_template}",
            "Keep Google service-account credentials server-side and the Android endpoint empty or HTTPS-configured through ignored release config.",
        ),
        (
            "No fake entitlements",
            "This skeleton intentionally does not fake successful verification" in read_text(readme)
            and "verifiedEntitlements.length === 0" in worker_text
            and "failed(\"Purchase was not verified" in worker_text,
            f"{worker}; {readme}",
            "Never return paid entitlements unless Google Play verifies the purchase.",
        ),
        (
            "Purchase token safety",
            "console.log" not in worker_lower
            and "safeError" in worker_text
            and "Do not log purchase tokens" in read_text(readme)
            and "purchaseToken" in client_text
            and "url.protocol != \"https\"" in client_text
            and "BILLING_VERIFY_ENDPOINT.trim().startsWith(\"https://\")" in play_billing_text,
            f"{worker}; {readme}; {client_gateway}; {play_billing_gateway}",
            "Do not log purchase tokens, redact long tokens in errors, and enforce HTTPS on the client.",
        ),
        (
            "Billing backend contract tests",
            package_json.exists()
            and contract_test.exists()
            and "node --test" in package_lower
            and "/billing/verify" in test_lower
            and "/entitlements" in test_lower
            and "/billing/notifications" in test_lower
            and "does not fake success" in test_lower,
            str(contract_test),
            "Keep local Worker tests for verification failure paths, RTDN placeholder, and token safety.",
        ),
        (
            "RTDN and durable storage gaps documented",
            "rtdn" in deployment_lower
            and "durable entitlement database" in deployment_lower
            and "refund" in deployment_lower
            and "cancellation" in deployment_lower
            and "chargeback" in deployment_lower,
            str(deployment_doc),
            "Keep remaining production Billing gaps visible until they are implemented.",
        ),
    ]

    overall = "pass" if all(item[1] for item in checks) else "fail"
    print("# Billing Backend Audit")
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
