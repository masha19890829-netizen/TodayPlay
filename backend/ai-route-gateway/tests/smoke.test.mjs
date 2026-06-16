import worker from "../worker.js";

const requestBody = {
  freeText: "Shanghai, after work, low pressure, 100 CNY",
  city: "Shanghai",
  relationship: "date",
  moods: ["low pressure", "chat"],
  timeBudget: "90 minutes",
  budget: "100 CNY",
  transportMode: "walk / metro",
  localeCode: "zh-CN",
  candidateStops: [
    {
      stopId: "stop-1",
      name: "Wukang Road Citywalk",
      city: "Shanghai",
      district: "Xuhui",
      tags: ["walk", "photo"],
      budgetLevel: "low",
      stayMinutes: 35
    },
    {
      stopId: "stop-2",
      name: "The Bund skyline walk",
      city: "Shanghai",
      district: "Huangpu",
      tags: ["view", "chat"],
      budgetLevel: "free",
      stayMinutes: 40
    }
  ]
};

await testFallbackWithoutKimiKey();
await testRemoteSecretStillRejectsMissingHeader();
await testLocalAndroidHostCanUseSecretlessDebugRequest();
await testKimiSuccessPath();
await testProviderProbePath();

console.log("ai-route-gateway smoke test passed");

async function testFallbackWithoutKimiKey() {
  const response = await generate("https://gateway.test", {});
  if (response.status !== 200) {
    throw new Error(`Expected 200, got ${response.status}`);
  }

  const payload = await response.json();
  if (payload.usedFallback !== true) {
    throw new Error("Smoke test should fallback without KIMI_API_KEY");
  }
  if (!Array.isArray(payload.selectedStopIds) || payload.selectedStopIds.length === 0) {
    throw new Error("Fallback must return selectedStopIds");
  }
}

async function testRemoteSecretStillRejectsMissingHeader() {
  const response = await generate("https://gateway.test", { AI_GATEWAY_SHARED_SECRET: "secret" });
  if (response.status !== 401) {
    throw new Error(`Expected remote secret request to reject, got ${response.status}`);
  }
}

async function testLocalAndroidHostCanUseSecretlessDebugRequest() {
  const response = await generate("http://10.0.2.2:8787", { AI_GATEWAY_SHARED_SECRET: "secret" });
  if (response.status !== 200) {
    throw new Error(`Expected local Android debug request to pass, got ${response.status}`);
  }
}

async function testKimiSuccessPath() {
  const restoreFetch = mockKimiFetch({
    title: "Quiet Shanghai Walk",
    storySetup: "A gentle after-work route.",
    routeSummary: "Start with a walk, then pick the skyline.",
    whyReasons: ["Fits the time budget.", "Keeps the plan low pressure."],
    selectedStopIds: ["stop-2", "stop-1"],
    stopReasons: {
      "stop-1": "Easy photo walk.",
      "stop-2": "Open public view."
    },
    safetyNote: "Same-city candidate stops only."
  });

  try {
    const response = await generate("http://10.0.2.2:8787", {
      KIMI_API_KEY: "test-key",
      AI_GATEWAY_SHARED_SECRET: "secret"
    });
    const payload = await response.json();
    if (payload.usedFallback !== false) {
      throw new Error(`Expected Kimi success, got fallback: ${payload.reason}`);
    }
    if (payload.selectedStopIds.join(",") !== "stop-2,stop-1") {
      throw new Error("Kimi selected stop order was not preserved");
    }
  } finally {
    restoreFetch();
  }
}

async function testProviderProbePath() {
  const restoreFetch = mockKimiFetch({ ok: true });
  try {
    const response = await worker.fetch(
      new Request("http://127.0.0.1:8787/ai/provider/status?probe=true"),
      { KIMI_API_KEY: "test-key", AI_GATEWAY_SHARED_SECRET: "secret" }
    );
    const payload = await response.json();
    if (payload.probe?.status !== "ready") {
      throw new Error(`Expected provider probe to be ready, got ${payload.probe?.status}`);
    }
  } finally {
    restoreFetch();
  }
}

function generate(baseUrl, env) {
  return worker.fetch(
    new Request(`${baseUrl}/ai/route/generate`, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify(requestBody)
    }),
    env
  );
}

function mockKimiFetch(jsonContent) {
  const originalFetch = globalThis.fetch;
  globalThis.fetch = async (url, options) => {
    if (!String(url).endsWith("/chat/completions")) {
      throw new Error(`Unexpected fetch URL: ${url}`);
    }
    const requestPayload = JSON.parse(options.body);
    if (!options.headers.authorization?.startsWith("Bearer ")) {
      throw new Error("Kimi request did not include bearer auth");
    }
    if (requestPayload.response_format?.type !== "json_object") {
      throw new Error("Kimi request did not ask for JSON object output");
    }
    return new Response(
      JSON.stringify({
        choices: [{ message: { content: JSON.stringify(jsonContent) } }]
      }),
      { status: 200, headers: { "content-type": "application/json" } }
    );
  };
  return () => {
    globalThis.fetch = originalFetch;
  };
}
