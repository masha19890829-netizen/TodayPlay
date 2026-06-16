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

const response = await worker.fetch(
  new Request("https://gateway.test/ai/route/generate", {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify(requestBody)
  }),
  {}
);

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

console.log("ai-route-gateway smoke test passed");
