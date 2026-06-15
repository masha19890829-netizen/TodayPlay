# TodayPlay Travel Provider Adapter Contract

Version: `2026-06-10`

This contract defines the minimum boundary for any production travel-content provider adapter. It applies to official map APIs, licensed POI datasets, merchant catalogs, operator-curated city packs, and user-authorized share-link processing.

The contract is intentionally strict. A provider adapter may return less content, but it must not return content without source, license, freshness, and compliance metadata.

## Adapter Types

Supported provider types:

- `official_map_api`
- `licensed_poi_dataset`
- `merchant_catalog`
- `operator_catalog`
- `user_authorized_link`

Unsupported provider behavior:

- No scraping.
- Client-side scraping.
- Server-side scraping without explicit platform permission.
- Claiming third-party platform heat or rankings without authorized data.
- Displaying third-party images without explicit image license metadata.
- Returning POI data without `ContentSource` and `SourcePolicy`.

## Common Request Fields

Every adapter request must accept:

- `requestId`: Stable ID for traceability.
- `locale`: One of `zh-CN`, `zh-TW`, `en`, `ja`, `ko`, `es`.
- `cityOrQuery`: Free text city, district, landmark, or route intent.
- `relationshipType`: `couple`, `crush`, `friends`, `family`, `solo`, or `group`.
- `interests`: User-selected interests such as `photo`, `food`, `walk`, `exhibition`, `budget`, `romantic`, `relaxed`.
- `budgetLevel`: Product budget bucket, not raw income.
- `timeWindow`: Duration or day-part such as `after_work_90m`, `half_day`, `full_day`, `weekend`.
- `transportMode`: `walk`, `public_transit`, `drive`, `taxi`, `mixed`.
- `sourcePolicy`: Requested source boundary and allowed use.

Do not include private notes, conversation text, raw photos, precise user location, contact data, or payment data in provider requests unless a future version explicitly adds the permission and privacy flow.

## ContentSource

Every returned POI, route stop, and image must include:

- `sourceId`: Stable provider-side or TodayPlay-side ID.
- `sourceLabel`: Human-readable source label.
- `sourceType`: One of the supported provider types.
- `sourceUrl`: Optional public URL when allowed by provider terms.
- `licenseName`: License or commercial agreement name.
- `licenseScope`: Allowed display and processing scope.
- `retrievedAt`: ISO-8601 timestamp.
- `expiresAt`: ISO-8601 timestamp or null for manually curated static content.
- `requiresOfficialVerification`: Boolean.
- `mock`: Boolean. Must be `false` for production provider data.

## SourcePolicy

Every adapter response must include:

- `noScraping`: Must be `true`.
- `allowedUses`: Examples: `route_planning`, `store_listing_screenshots`, `merchant_coupon_display`.
- `forbiddenUses`: Examples: `claim_social_heat`, `display_unlicensed_images`, `infer_private_profile`.
- `retentionPolicy`: Cache duration or deletion rule.
- `attributionRequired`: Boolean.
- `attributionText`: Required if provider requires public attribution.
- `reviewRequired`: Boolean.
- `policyNotes`: Short plain-language notes for Play review and internal QA.

## POI

Every production POI must include:

- `poiId`
- `name`
- `country`
- `city`
- `district`
- `address`
- `latitude`
- `longitude`
- `tags`
- `suitableFor`
- `budgetLevel`
- `estimatedStayMinutes`
- `bestVisitTime`
- `recommendationReason`
- `riskTips`
- `imageAssets`
- `contentSource`
- `sourcePolicy`
- `freshness`

`latitude` and `longitude` must come from an authorized source and must not be inferred from user private data.

## ImageAsset

Every image must include:

- `assetId`
- `kind`: `thumbnail`, `hero`, `map_preview`, or `operator_placeholder`.
- `url`
- `altText`
- `width`
- `height`
- `licenseName`
- `licenseHolder`
- `licenseScope`
- `expiresAt`
- `requiresAttribution`
- `attributionText`

If license metadata is missing, return an operator placeholder instead of a third-party image.

## RouteStop

Every route stop must include:

- `order`
- `poi`
- `startTimeHint`
- `stayMinutes`
- `checkInTask`
- `photoSuggestion`
- `spendingSuggestion`
- `backupPlan`
- `navigationAction`
- `whyForGroup`
- `riskTips`
- `contentSource`
- `sourcePolicy`

## ItineraryPlan

Every itinerary plan must include:

- `planId`
- `planType`
- `locale`
- `title`
- `city`
- `routeSummary`
- `estimatedCost`
- `bestPhotoTime`
- `crowdRisk`
- `candidateRouteCount`
- `stops`
- `rewardPolicy`
- `complianceNote`
- `marketCoverageNote`
- `contentSource`
- `sourcePolicy`

## Compliance Note

Every response must expose a compliance note:

- `summary`
- `mockData`
- `requiresBackend`
- `requiresOfficialApiOrLicense`
- `requiresImageLicense`
- `requiresPrivacyReview`
- `unsupportedClaims`

Unsupported claims should explicitly list anything that the UI must not say, such as:

- `real_social_platform_heat`
- `official_xiaohongshu_recommendation`
- `official_douyin_ranking`
- `live_crowd_level`
- `verified_business_hours`

## Failure Contract

If the provider is not configured, rate limited, missing license coverage, or cannot verify source policy, return:

```json
{
  "status": "not_configured",
  "reason": "Provider adapter is not configured.",
  "compliance": {
    "noScraping": true,
    "requiresOfficialApiOrLicense": true,
    "requiresContentSource": true,
    "requiresSourcePolicy": true,
    "requiresOfficialVerification": true
  }
}
```

Do not silently fall back to fake live data. Use the local mock catalog only when the Android client or backend explicitly marks the result as mock/operator-curated.

## Release Gate

Before enabling a provider adapter in production:

1. Contract test all endpoints.
2. Verify provider terms and allowed markets.
3. Verify source attribution and image license obligations.
4. Verify cache expiry and deletion behavior.
5. Verify Play Data Safety and privacy policy updates.
6. Verify no provider secrets are present in Android code or public assets.
7. Verify UI labels do not imply unsupported third-party platform endorsements.
