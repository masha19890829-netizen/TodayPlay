import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import test from "node:test";

import worker from "../worker.js";

const env = {
  TODAYPLAY_ALLOWED_ORIGIN: "https://todayplay.example",
};

async function request(path, options = {}) {
  const response = await worker.fetch(new Request(`https://worker.test${path}`, options), env);
  const payload = await response.json();
  return { response, payload };
}

test("health endpoint returns service identity", async () => {
  const { response, payload } = await request("/health");

  assert.equal(response.status, 200);
  assert.equal(payload.status, "ok");
  assert.equal(payload.service, "todayplay-travel-content");
});

test("source policies expose no-scraping and source metadata requirements", async () => {
  const { response, payload } = await request("/travel/sources/policies");

  assert.equal(response.status, 200);
  assert.ok(Array.isArray(payload.policies));
  assert.ok(payload.policies.length > 0);

  const policyText = JSON.stringify(payload.policies).toLowerCase();
  assert.match(policyText, /no scraping/);
  assert.match(policyText, /official apis/);
  assert.match(policyText, /licensed datasets/);
  assert.match(policyText, /merchant partnerships/);
  assert.match(policyText, /user-authorized links/);
  assert.match(policyText, /contentsource/);
  assert.match(policyText, /sourcepolicy/);
});

test("city search is disabled until an authorized provider adapter exists", async () => {
  const { response, payload } = await request("/travel/cities/search?query=Tokyo&locale=en");

  assert.equal(response.status, 501);
  assert.equal(payload.status, "not_configured");
  assert.equal(payload.compliance.noScraping, true);
  assert.equal(payload.compliance.requiresOfficialApiOrLicense, true);
});

test("POI search is disabled and still requires JSON body validation", async () => {
  const { response, payload } = await request("/travel/poi/search", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      cityOrQuery: "Tokyo",
      interests: ["photo", "coffee"],
      relationshipType: "couple",
    }),
  });

  assert.equal(response.status, 501);
  assert.equal(payload.status, "not_configured");
  assert.equal(payload.compliance.requiresContentSource, true);
  assert.equal(payload.compliance.requiresSourcePolicy, true);
});

test("itinerary planning is disabled until verified POI inputs exist", async () => {
  const { response, payload } = await request("/travel/itinerary/plan", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      locale: "en",
      candidatePoiIds: [],
    }),
  });

  assert.equal(response.status, 501);
  assert.equal(payload.status, "not_configured");
  assert.equal(payload.compliance.requiresOfficialVerification, true);
});

test("authorized share-link import stays in manual review by default", async () => {
  const { response, payload } = await request("/travel/content/import-link", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      url: "https://example.com/share",
      userConfirmedAuthorization: true,
    }),
  });

  assert.equal(response.status, 202);
  assert.equal(payload.status, "manual_review");
  assert.match(payload.reason.toLowerCase(), /authorization/);
});

test("provider adapter contract documents required production fields", async () => {
  const markdown = await readFile(new URL("../contracts/provider-adapter-contract.md", import.meta.url), "utf8");
  const schema = JSON.parse(
    await readFile(new URL("../contracts/provider-adapter-contract.schema.json", import.meta.url), "utf8"),
  );

  const contractText = markdown.toLowerCase();
  for (const token of [
    "official_map_api",
    "licensed_poi_dataset",
    "merchant_catalog",
    "user_authorized_link",
    "contentsource",
    "sourcepolicy",
    "imageasset",
    "routestop",
    "itineraryplan",
    "compliance note",
    "no scraping",
    "unsupported claims",
  ]) {
    assert.match(contractText, new RegExp(token.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")));
  }

  assert.equal(schema.$defs.sourcePolicy.properties.noScraping.const, true);
  assert.deepEqual(schema.$defs.locale.enum, ["zh-CN", "zh-TW", "en", "ja", "ko", "es"]);
  assert.ok(schema.$defs.poi.required.includes("contentSource"));
  assert.ok(schema.$defs.poi.required.includes("sourcePolicy"));
  assert.ok(schema.$defs.poi.required.includes("imageAssets"));
  assert.ok(schema.$defs.routeStop.required.includes("navigationAction"));
  assert.ok(schema.$defs.itineraryPlan.required.includes("complianceNote"));
  assert.ok(schema.$defs.complianceNote.required.includes("unsupportedClaims"));
});
