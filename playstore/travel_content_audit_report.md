# Travel Content Audit

- Project: `D:\AppStore\nemu\real`
- Overall: `pass`

| Check | Status | Evidence | Required action |
| --- | --- | --- | --- |
| Android travel endpoint config | `pass` | TRAVEL_CONTENT_BASE_URL=None, releaseConfigTemplate=True | Add BuildConfig.TRAVEL_CONTENT_BASE_URL and keep it empty or HTTPS-configured through ignored release config. |
| Endpoint disabled for prototype | `pass` | TRAVEL_CONTENT_BASE_URL=None, localConfigPresent=False | Do not enable global live search until provider contracts, backend auth, and privacy review are complete. |
| Repository factory boundary | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\data\TravelContentRepository.kt | Keep remote travel content behind a factory with local mock fallback. |
| Android remote travel HTTPS client | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\data\TravelContentRepository.kt | When TRAVEL_CONTENT_BASE_URL is configured, Android should call the backend over HTTPS with short timeouts. |
| Android remote response safety | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\data\TravelContentRepository.kt | Remote content must be verified, non-mock, and safely fall back to local mock content on failures. |
| Android remote source metadata parsing | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\data\TravelContentRepository.kt | Remote POIs must preserve source policy, risk tips, and official-verification metadata. |
| Generator uses repository factory | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\generator\LocalItineraryGenerator.kt | Route generation should not instantiate the mock catalog directly. |
| Result page shows disabled live-search state | `pass` | D:\AppStore\nemu\real\app\src\main\java\com\todayplay\app\ui\screens\QuestResultScreen.kt | Show users that global live search is not configured in the prototype. |
| Backend travel worker skeleton | `pass` | D:\AppStore\nemu\real\backend\travel-content-worker\worker.js | Provide route-content backend endpoints before enabling production global search. |
| Provider adapter boundary | `pass` | D:\AppStore\nemu\real\backend\travel-content-worker\adapters\not-configured-provider.js | Keep provider integrations behind adapter functions so official APIs and licensed providers can be added without changing route contracts. |
| Travel worker contract tests | `pass` | D:\AppStore\nemu\real\backend\travel-content-worker\tests\contract.test.mjs | Keep no-dependency contract tests for travel-content endpoints and source-policy behavior. |
| Provider adapter contract document | `pass` | D:\AppStore\nemu\real\backend\travel-content-worker\contracts\provider-adapter-contract.md | Document every production provider adapter input/output field and unsupported claim boundary. |
| Provider adapter JSON schema | `pass` | D:\AppStore\nemu\real\backend\travel-content-worker\contracts\provider-adapter-contract.schema.json | Keep a machine-readable adapter schema before enabling any live global content provider. |
| No scraping policy | `pass` | D:\AppStore\nemu\real\playstore\travel_content_backend_contract.md | Keep the no-scraping policy present in contract, backend README, and worker responses. |
| Authorized source terms | `pass` | D:\AppStore\nemu\real\playstore\travel_content_backend_contract.md | Document official APIs, licensed datasets, merchant partnerships, and user-authorized links. |
