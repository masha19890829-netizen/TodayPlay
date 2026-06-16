# TodayPlay AI Route Gateway

Cloudflare Worker gateway for TodayPlay V0.9.59. The Android APK never talks to Kimi directly and never contains a Kimi API key.

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
AI_GATEWAY_SHARED_SECRET=change-me-for-local-dev
```

Do not commit `.dev.vars`. The gateway must not log API keys or full free-text user input.

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

The smoke test does not require a real Kimi key. It validates request handling and fallback safety.
