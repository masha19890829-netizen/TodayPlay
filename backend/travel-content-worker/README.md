# TodayPlay Travel Content Worker

Minimal backend skeleton for global travel content, POI search, route planning, authorized share-link import, and source-policy disclosure.

This worker is intentionally conservative. It does not scrape third-party platforms, does not return fake social heat, does not include provider API keys, and does not pretend global search is live before official providers or licensed datasets are connected.

## Endpoints

### `GET /health`

Returns service health.

### `GET /travel/sources/policies`

Returns the current source policy list used by the client, Play review notes, and future provider integrations.

### `GET /travel/cities/search`

Currently returns `501 not_configured`.

Production requirements:

- Official city/POI provider or licensed dataset.
- Official APIs should be connected through server-side provider adapters only.
- Locale-aware names for `zh-CN`, `zh-TW`, `en`, `ja`, `ko`, and `es`.
- Coverage level per city.
- No provider secrets in the Android app.

### `POST /travel/poi/search`

Currently returns `501 not_configured`.

Production requirements:

- Every POI must include `ContentSource`, `SourcePolicy`, `requiresOfficialVerification`, freshness metadata, risk tips, and image license metadata.
- Social platform inspiration can be used only through official API terms, user authorization, operator notes, or licensed data.
- Do not display third-party images unless the license is explicit.

### `POST /travel/itinerary/plan`

Currently returns `501 not_configured`.

Production requirements:

- Use verified POI inputs.
- Return route stops, map actions, check-in tasks, reward policy, rain backup, crowd-risk caveats, and source-policy notes.
- Keep paid entitlements separate from route planning and verify payments through the Billing backend.

### `POST /travel/content/import-link`

Currently returns `202 manual_review`.

Production requirements:

- Only process links when the user explicitly confirms authorization.
- Respect each platform's official rules.
- Store source labels and compliance status.
- If automated extraction is not allowed, save the user note only.

## Required Secrets

The skeleton has no provider secrets. Production adapters should use server-side environment variables only, for example:

```text
TODAYPLAY_ALLOWED_ORIGIN=https://your-public-site.example
MAP_PROVIDER_API_KEY=server-side-only
LICENSED_POI_PROVIDER_KEY=server-side-only
```

Never put these values into Android `BuildConfig`.

## Provider Adapter Boundary

Current adapter:

- `adapters/not-configured-provider.js`

It deliberately returns `not_configured` for city search, POI search, and itinerary planning until a real authorized provider adapter is connected.

Future adapters should be added beside it, for example:

- `adapters/official-map-provider.js`
- `adapters/licensed-poi-provider.js`
- `adapters/merchant-catalog-provider.js`
- `adapters/user-authorized-link-provider.js`

Every adapter must return source metadata compatible with `ContentSource` and `SourcePolicy`, and must preserve the no-scraping rule.

Adapter contract files:

- `contracts/provider-adapter-contract.md`
- `contracts/provider-adapter-contract.schema.json`

The contract requires every production adapter response to carry POI, route-stop, itinerary, `ContentSource`, `SourcePolicy`, image-license, freshness, and compliance-note fields. The JSON schema is intentionally used as a static release gate first; runtime schema validation should be added before enabling a live provider.

## Contract Tests

Run:

```text
npm test
```

The tests call the Worker directly and verify:

- `/health`
- `/travel/sources/policies`
- `/travel/cities/search`
- `/travel/poi/search`
- `/travel/itinerary/plan`
- `/travel/content/import-link`
- no-scraping and authorized-source policy signals

## Deployment Checklist

1. Deploy this worker or port the same contract to your backend.
2. Test `/health`.
3. Test `/travel/sources/policies`.
4. Connect one authorized POI provider adapter.
5. Add durable source metadata and cache expiry.
6. Add request authentication and rate limits.
7. Point Android `TRAVEL_CONTENT_BASE_URL` to the deployed HTTPS base URL.
8. Update Play Data Safety and privacy policy before enabling personalized recommendations, account sync, photo upload, or precise location.

## Security Notes

- No scraping by default.
- No raw third-party content logs.
- No API keys in Android.
- No precise location collection in the current Android prototype.
- Keep mock data labeled as mock in responses and UI.
