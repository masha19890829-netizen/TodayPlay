import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import test from "node:test";

import worker from "../worker.js";

async function request(path, options = {}, env = {}) {
  const response = await worker.fetch(new Request(`https://billing.test${path}`, options), env);
  const payload = await response.json();
  return { response, payload };
}

function postJson(path, body, env = {}) {
  return request(path, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  }, env);
}

const validPurchase = {
  packageName: "com.todayplay.app",
  productIds: ["todayplay.plus.monthly"],
  purchaseToken: "test-purchase-token",
  orderId: "GPA.0000-0000-0000-00000",
  purchaseTime: 1710000000000,
  purchaseState: 1,
  isAcknowledged: false,
};

test("health endpoint returns service identity", async () => {
  const { response, payload } = await request("/health");

  assert.equal(response.status, 200);
  assert.equal(payload.status, "ok");
  assert.equal(payload.service, "todayplay-billing-verify");
});

test("verify rejects package mismatch before Google API calls", async () => {
  const { response, payload } = await postJson("/billing/verify", {
    ...validPurchase,
    packageName: "com.bad.actor",
  });

  assert.equal(response.status, 200);
  assert.equal(payload.status, "failed");
  assert.match(payload.reason.toLowerCase(), /package name mismatch/);
});

test("verify rejects missing purchase token or product id", async () => {
  const { response, payload } = await postJson("/billing/verify", {
    packageName: "com.todayplay.app",
    productIds: [],
    purchaseToken: "",
  });

  assert.equal(response.status, 200);
  assert.equal(payload.status, "failed");
  assert.match(payload.reason.toLowerCase(), /missing purchase token/);
});

test("verify rejects unknown product id before Google API calls", async () => {
  const { response, payload } = await postJson("/billing/verify", {
    ...validPurchase,
    productIds: ["todayplay.unknown"],
  });

  assert.equal(response.status, 200);
  assert.equal(payload.status, "failed");
  assert.match(payload.reason.toLowerCase(), /unknown product id/);
});

test("verify does not fake success when Google secrets are absent", async () => {
  const { response, payload } = await postJson("/billing/verify", validPurchase);

  assert.equal(response.status, 500);
  assert.equal(payload.status, "failed");
  assert.match(payload.reason.toLowerCase(), /service account secrets/);
  assert.doesNotMatch(JSON.stringify(payload), /test-purchase-token/);
});

test("entitlements endpoint remains blocked without authenticated durable storage", async () => {
  const { response, payload } = await request("/entitlements");

  assert.equal(response.status, 501);
  assert.equal(payload.status, "not_implemented");
  assert.match(payload.reason.toLowerCase(), /authenticated user identity/);
  assert.match(payload.reason.toLowerCase(), /durable storage/);
});

test("RTDN endpoint remains blocked until Pub/Sub verification exists", async () => {
  const { response, payload } = await postJson("/billing/notifications", {
    message: { data: "placeholder" },
  });

  assert.equal(response.status, 501);
  assert.equal(payload.status, "not_implemented");
  assert.match(payload.reason.toLowerCase(), /pub\/sub verification/);
  assert.match(payload.reason.toLowerCase(), /persistent entitlement updates/);
});

test("worker source keeps Google Play APIs and token-safety boundaries", async () => {
  const source = await readFile(new URL("../worker.js", import.meta.url), "utf8");
  const readme = await readFile(new URL("../README.md", import.meta.url), "utf8");
  const sourceLower = source.toLowerCase();
  const readmeLower = readme.toLowerCase();

  for (const productId of [
    "todayplay.plus.monthly",
    "todayplay.itinerary.premium.once",
    "todayplay.citypack.global",
    "todayplay.photo.positions",
  ]) {
    assert.match(source, new RegExp(productId.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")));
    assert.match(readme, new RegExp(productId.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")));
  }

  assert.match(sourceLower, /purchases\/products/);
  assert.match(sourceLower, /purchases\/subscriptionsv2/);
  assert.match(sourceLower, /androidpublisher/);
  assert.match(sourceLower, /safeerror/);
  assert.doesNotMatch(sourceLower, /console\.log/);
  assert.match(readmeLower, /do not log purchase tokens/);
  assert.match(readmeLower, /do not return raw google purchase payloads/);
});

test("backend entitlement keys stay aligned with Android catalog", async () => {
  const source = await readFile(new URL("../worker.js", import.meta.url), "utf8");
  const readme = await readFile(new URL("../README.md", import.meta.url), "utf8");
  const androidCatalog = await readFile(
    new URL("../../../app/src/main/java/com/todayplay/app/model/BillingModels.kt", import.meta.url),
    "utf8",
  );

  for (const entitlementKey of [
    "plus_monthly",
    "premium_itinerary_once",
    "global_city_pack",
    "photo_position_pack",
  ]) {
    assert.match(source, new RegExp(entitlementKey));
    assert.match(readme, new RegExp(entitlementKey));
    assert.match(androidCatalog, new RegExp(entitlementKey));
  }
});
