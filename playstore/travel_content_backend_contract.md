# TodayPlay Travel Content Backend Contract

Status: production contract draft for V0.9.1  
Client package: `com.todayplay.app`  
Scope: global POI search, authorized social-content inspiration, route planning, map actions, check-in rewards

## Release Truth

The Android client currently uses a local mock POI catalog to validate the product loop. It must not claim real-time global coverage, official social-platform heat, verified opening hours, or merchant availability until those signals are returned by an authorized backend provider.

Production content must come from one of these sources:

- Official map or POI APIs with a valid commercial agreement.
- Licensed POI, event, travel, photo-position, or local-life datasets.
- Merchant or destination partnerships.
- User-authorized share links submitted by the user.
- Human-operated TodayPlay content tools with stored source and license metadata.

Forbidden production behavior:

- No client-side scraping of Xiaohongshu, TikTok, Douyin, Instagram, Google Maps, Amap, Meituan, Dianping, or similar services.
- No API keys or provider secrets in the Android app.
- No unlicensed third-party images.
- No fake source labels such as "recommended by Xiaohongshu" unless the source is authorized and verifiable.
- No precise-location collection unless the user explicitly opts in and the Data Safety form is updated.

## Source Policy

Every POI, route, and imported content item must carry source metadata.

```json
{
  "sourceType": "official_api | licensed_dataset | merchant_partner | user_authorized_link | operator_curated | mock",
  "sourceName": "Provider or internal catalog name",
  "sourceLabel": "Reader-facing source label",
  "isMock": false,
  "licenseId": "optional-license-or-contract-id",
  "retrievedAt": "2026-06-08T10:00:00Z",
  "expiresAt": "2026-06-09T10:00:00Z",
  "requiresOfficialVerification": true,
  "policyNotes": [
    "No unauthorized scraping",
    "Third-party image display requires a license or official API response"
  ]
}
```

## Endpoints

### `GET /travel/cities/search`

Purpose: autocomplete global city names and supported regions.

Query:

- `query`: user-entered city or destination text.
- `locale`: `zh-CN`, `zh-TW`, `en`, `ja`, `ko`, or `es`.

Response:

```json
{
  "items": [
    {
      "cityId": "tokyo-jp",
      "name": "Tokyo",
      "country": "Japan",
      "localizedName": "Tokyo",
      "coverageLevel": "sample | partner | official | full",
      "availablePlanTypes": ["after_work_90", "half_day", "one_day", "weekend"]
    }
  ],
  "sourcePolicy": {}
}
```

### `POST /travel/poi/search`

Purpose: return candidate POIs for a group preference.

Request:

```json
{
  "locale": "en",
  "cityOrQuery": "Tokyo",
  "relationshipType": "couple",
  "timeWindow": "after_work_90",
  "budget": "medium",
  "transportMode": "public_transit_walk",
  "interests": ["photo", "coffee", "citywalk"],
  "avoid": ["crowded", "expensive"],
  "limit": 20
}
```

Response:

```json
{
  "pois": [
    {
      "poiId": "provider:tokyo-shibuya-sky",
      "name": "Shibuya Sky",
      "country": "Japan",
      "city": "Tokyo",
      "district": "Shibuya",
      "address": "2-24-12 Shibuya, Tokyo",
      "latitude": 35.6580,
      "longitude": 139.7020,
      "tags": ["photo", "night_view", "couple"],
      "suitableRelationships": ["couple", "friends", "family"],
      "budgetLevel": "medium",
      "estimatedStayMinutes": 75,
      "image": {
        "url": null,
        "placeholder": "tokyo_shibuya_sky",
        "license": "none"
      },
      "recommendationReason": "Good city view and simple route structure.",
      "riskTips": ["Tickets and opening hours require verification."],
      "globalCategory": "classic_city_view",
      "dataFreshness": "provider_cache_24h",
      "requiresOfficialVerification": true,
      "contentSource": {}
    }
  ],
  "sourcePolicy": {},
  "coverageNote": "Results are from licensed provider data and operator-curated notes."
}
```

### `POST /travel/itinerary/plan`

Purpose: generate an executable route quest from preferences and candidate POIs.

Request:

```json
{
  "locale": "en",
  "groupPreference": {
    "groupLabel": "Couple",
    "relationshipType": "couple",
    "mergedCity": "Tokyo",
    "mergedBudget": "medium",
    "mergedTimeWindow": "after_work_90",
    "mergedTransportMode": "public_transit_walk",
    "mergedInterests": ["photo", "coffee", "romantic"],
    "members": [
      {
        "userLabel": "Planner",
        "interests": ["photo", "coffee"],
        "pace": "easy"
      },
      {
        "userLabel": "Companion",
        "interests": ["romantic", "walking"],
        "pace": "easy"
      }
    ]
  },
  "candidatePoiIds": ["provider:tokyo-shibuya-sky"],
  "planType": "after_work_90"
}
```

Response:

```json
{
  "itineraryPlan": {
    "planId": "plan_01JX",
    "planType": "after_work_90",
    "title": "Tokyo sunset route",
    "city": "Tokyo",
    "relationshipType": "couple",
    "estimatedCost": "JPY 3,000-6,000 per person",
    "estimatedDuration": "90 minutes",
    "routeSummary": "One view stop, one cafe backup, one light interaction task.",
    "crowdRisk": "Medium, verify live conditions before departure.",
    "rainBackup": "Switch to indoor mall or cafe route.",
    "bestPhotoTime": "30 minutes before sunset",
    "rewardPolicy": "Local reward ledger only until backend entitlements are live.",
    "marketCoverageNote": "Requires official provider verification for production.",
    "candidateRouteCount": 3,
    "stops": []
  },
  "contentComplianceNote": {}
}
```

### `POST /travel/content/import-link`

Purpose: accept a user-pasted share link and process it on the backend only where platform rules allow it.

Request:

```json
{
  "locale": "zh-CN",
  "url": "https://example.com/shared-post",
  "userConfirmedAuthorization": true
}
```

Response:

```json
{
  "status": "accepted | rejected | manual_review",
  "reason": "Platform does not allow automated extraction, saved as user note only.",
  "normalizedContent": {
    "title": "optional user-visible title",
    "poiHints": [],
    "sourcePolicy": {}
  }
}
```

### `GET /travel/sources/policies`

Purpose: expose current provider, license, and freshness policy to the client and review team.

Response:

```json
{
  "policies": [
    {
      "sourceName": "TodayPlay operator catalog",
      "allowedUses": ["route_planning", "store_listing_screenshots"],
      "forbiddenUses": ["claiming social-platform popularity"],
      "dataRetention": "Provider cache expires within 24 hours unless licensed otherwise."
    }
  ]
}
```

## Android Client Boundary

The Android app should call a `TravelContentRepository` abstraction:

- `LocalMockTravelContentRepository`: current offline catalog for internal testing.
- `RemoteTravelContentRepository`: future HTTPS client using the endpoints above.
- The route generator should depend on the interface, not directly on a mock catalog.
- The app should show a data availability notice whenever results are mock, unverified, or incomplete.

## Privacy And Safety

- Do not request location permission for the current manual-check-in prototype.
- Photo check-in remains local preview/mock until a privacy-reviewed upload flow exists.
- No face recognition, address book, camera, album, or precise-location permission should be added without a new Data Safety review.
- User route history, preferences, reward ledger, and imported links need deletion/export support when accounts are introduced.
- Backend requests must use HTTPS and must not log notes, private dialogue, purchase tokens, exact photos, or full share-link content beyond what is necessary for compliance and debugging.

## Production Readiness Checklist

- Deploy global POI/search backend with provider secrets stored server-side.
- Sign contracts or confirm official API terms for every provider.
- Store source metadata and license/freshness fields with every returned item.
- Add automated tests that reject `isMock=true` data in production API responses unless the response is explicitly labeled as sample content.
- Update Play Data Safety and privacy policy before enabling location, photo upload, account sync, or personalized recommendation.
- Keep Google Play Billing entitlements separate from route content and verify all purchases server-side.
