# TodayPlay AI Route Gateway

Cloudflare Worker gateway for TodayPlay V0.9.60. The Android APK never talks to Kimi directly and never contains a Kimi API key.

## Endpoints

- `GET /health`
- `GET /ai/provider/status`
- `POST /ai/route/generate`

## Environment

Copy `.dev.vars.example` to `.dev.vars` for local development.

```text
AI_PROVIDER=kimi
KIMI_API_KEY=sk-...
KIMI_BASE_URL=https://api.moonshot.cn/v1
KIMI_MODEL=moonshot-v1-8k
KIMI_TIMEOUT_MS=25000
AI_GATEWAY_SHARED_SECRET=change-me-for-local-dev
```

Do not commit `.dev.vars`. The gateway must not log API keys or full free-text user input.

## Local Android Emulator Run

```powershell
npm install
npm run dev:android
```

Use this Android base URL for the emulator:

```text
http://10.0.2.2:8787
```

`dev:android` listens on `0.0.0.0:8787`, while the Android emulator reaches the host machine through `10.0.2.2`. For local HTTP requests to `10.0.2.2`, `127.0.0.1`, or `localhost`, the worker allows debug calls even when `AI_GATEWAY_SHARED_SECRET` is set. Deployed HTTPS hosts still require the configured gateway auth behavior.

Android builds targeting modern SDKs also need cleartext HTTP explicitly allowed, or a local HTTPS/tunnel URL, before `http://10.0.2.2:8787` can be called from the app.

## Real Kimi Probe

After starting the worker with `.dev.vars`, call:

```text
GET http://127.0.0.1:8787/ai/provider/status?probe=true
```

The response returns `probe.status` as `ready`, `failed`, or `not_configured`. Failure reasons are sanitized, for example `kimi_http_401`, `kimi_timeout`, or `kimi_invalid_probe_json`.

## Request

```json
{
  "freeText": "I am in Shanghai after work and want a low-pressure date route.",
  "city": "Shanghai",
  "relationship": "date",
  "moods": ["low pressure", "chat"],
  "timeBudget": "90 minutes",
  "budget": "100 CNY",
  "transportMode": "walk / metro",
  "localeCode": "zh-CN",
  "candidateStops": [
    {
      "stopId": "stop-1",
      "name": "Wukang Road Citywalk",
      "city": "Shanghai",
      "district": "Xuhui",
      "tags": ["walk", "photo"],
      "budgetLevel": "low",
      "stayMinutes": 35
    }
  ]
}
```

## Response

```json
{
  "status": "ready",
  "provider": "kimi",
  "usedFallback": false,
  "title": "After-work low-pressure city route",
  "storySetup": "A route designed around the user's current mood.",
  "routeSummary": "Two nearby stops, easy to leave early.",
  "whyReasons": ["Uses the requested city.", "Keeps the route short."],
  "selectedStopIds": ["stop-1"],
  "stopReasons": {
    "stop-1": "Public, easy to start a conversation."
  },
  "safetyNote": "Only selected from provided candidate stops."
}
```

If Kimi is not configured, times out, returns malformed JSON, invents stop IDs, or crosses city boundaries, the gateway returns `usedFallback: true`. Android must still use its local route generator.

## Local Smoke Test

```powershell
npm install
npm test
```

The smoke test does not require a real Kimi key. It validates request handling, fallback safety, local Android debug authorization, the Kimi response normalization path, and the provider probe shape.
