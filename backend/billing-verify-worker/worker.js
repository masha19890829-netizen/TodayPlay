const PACKAGE_NAME_DEFAULT = "com.todayplay.app";

const PRODUCT_CATALOG = {
  "todayplay.plus.monthly": {
    type: "subscription",
    entitlementKey: "plus_monthly",
  },
  "todayplay.itinerary.premium.once": {
    type: "one_time",
    entitlementKey: "premium_itinerary_once",
  },
  "todayplay.citypack.global": {
    type: "one_time",
    entitlementKey: "global_city_pack",
  },
  "todayplay.photo.positions": {
    type: "one_time",
    entitlementKey: "photo_position_pack",
  },
};

export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    try {
      if (request.method === "GET" && url.pathname === "/health") {
        return json({ status: "ok", service: "todayplay-billing-verify" });
      }
      if (request.method === "POST" && url.pathname === "/billing/verify") {
        return json(await verifyBilling(request, env));
      }
      if (request.method === "GET" && url.pathname === "/entitlements") {
        return json(
          {
            status: "not_implemented",
            reason: "Production entitlements require authenticated user identity and durable storage.",
          },
          501,
        );
      }
      if (request.method === "POST" && url.pathname === "/billing/notifications") {
        return json(
          {
            status: "not_implemented",
            reason: "Production RTDN handling requires Pub/Sub verification and persistent entitlement updates.",
          },
          501,
        );
      }
      return json({ status: "failed", reason: "Not found" }, 404);
    } catch (error) {
      return json({ status: "failed", reason: safeError(error) }, 500);
    }
  },
};

async function verifyBilling(request, env) {
  const body = await readJson(request);
  const expectedPackageName = env.TODAYPLAY_PACKAGE_NAME || PACKAGE_NAME_DEFAULT;
  const packageName = String(body.packageName || "");
  const purchaseToken = String(body.purchaseToken || "");
  const productIds = Array.isArray(body.productIds) ? body.productIds.map(String) : [];

  if (packageName !== expectedPackageName) {
    return failed("Package name mismatch");
  }
  if (!purchaseToken || productIds.length === 0) {
    return failed("Missing purchase token or product id");
  }

  const products = productIds.map((productId) => ({ productId, product: PRODUCT_CATALOG[productId] }));
  const unknown = products.find((entry) => !entry.product);
  if (unknown) {
    return failed("Unknown product id");
  }

  assertGoogleSecrets(env);
  const accessToken = await getGoogleAccessToken(env);
  const verifiedEntitlements = [];

  for (const entry of products) {
    if (entry.product.type === "subscription") {
      const subscription = await fetchSubscriptionPurchaseV2({
        accessToken,
        packageName,
        purchaseToken,
      });
      if (isSubscriptionActive(subscription)) {
        verifiedEntitlements.push(entitlement(entry));
      }
    } else {
      const purchase = await fetchProductPurchase({
        accessToken,
        packageName,
        productId: entry.productId,
        purchaseToken,
      });
      if (isProductPurchased(purchase)) {
        verifiedEntitlements.push(entitlement(entry));
      }
    }
  }

  if (verifiedEntitlements.length === 0) {
    return failed("Purchase was not verified as active or purchased");
  }

  return {
    status: "verified",
    entitlements: verifiedEntitlements,
  };
}

async function fetchProductPurchase({ accessToken, packageName, productId, purchaseToken }) {
  const url = new URL(
    `https://androidpublisher.googleapis.com/androidpublisher/v3/applications/${encodeURIComponent(packageName)}/purchases/products/${encodeURIComponent(productId)}/tokens/${encodeURIComponent(purchaseToken)}`,
  );
  return googleGet(url, accessToken);
}

async function fetchSubscriptionPurchaseV2({ accessToken, packageName, purchaseToken }) {
  const url = new URL(
    `https://androidpublisher.googleapis.com/androidpublisher/v3/applications/${encodeURIComponent(packageName)}/purchases/subscriptionsv2/tokens/${encodeURIComponent(purchaseToken)}`,
  );
  return googleGet(url, accessToken);
}

async function googleGet(url, accessToken) {
  const response = await fetch(url, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${accessToken}`,
      Accept: "application/json",
    },
  });
  const text = await response.text();
  const payload = text ? JSON.parse(text) : {};
  if (!response.ok) {
    throw new Error(`Google Play API verification failed with HTTP ${response.status}`);
  }
  return payload;
}

function isProductPurchased(purchase) {
  return purchase.purchaseState === 0 && purchase.consumptionState !== 1;
}

function isSubscriptionActive(subscription) {
  return subscription.subscriptionState === "SUBSCRIPTION_STATE_ACTIVE" ||
    subscription.subscriptionState === "SUBSCRIPTION_STATE_IN_GRACE_PERIOD";
}

function entitlement(entry) {
  return {
    key: entry.product.entitlementKey,
    productId: entry.productId,
    source: "google_play",
  };
}

async function getGoogleAccessToken(env) {
  const now = Math.floor(Date.now() / 1000);
  const header = base64UrlJson({ alg: "RS256", typ: "JWT" });
  const claim = base64UrlJson({
    iss: env.GOOGLE_SERVICE_ACCOUNT_EMAIL,
    scope: "https://www.googleapis.com/auth/androidpublisher",
    aud: "https://oauth2.googleapis.com/token",
    exp: now + 3600,
    iat: now,
  });
  const unsignedJwt = `${header}.${claim}`;
  const key = await importPrivateKey(env.GOOGLE_PRIVATE_KEY);
  const signature = await crypto.subtle.sign(
    "RSASSA-PKCS1-v1_5",
    key,
    new TextEncoder().encode(unsignedJwt),
  );
  const jwt = `${unsignedJwt}.${base64UrlBytes(new Uint8Array(signature))}`;
  const response = await fetch("https://oauth2.googleapis.com/token", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: new URLSearchParams({
      grant_type: "urn:ietf:params:oauth:grant-type:jwt-bearer",
      assertion: jwt,
    }),
  });
  const payload = await response.json();
  if (!response.ok || !payload.access_token) {
    throw new Error("Unable to obtain Google access token");
  }
  return payload.access_token;
}

async function importPrivateKey(pem) {
  const normalized = String(pem || "").replace(/\\n/g, "\n");
  const base64 = normalized
    .replace("-----BEGIN PRIVATE KEY-----", "")
    .replace("-----END PRIVATE KEY-----", "")
    .replace(/\s+/g, "");
  const raw = Uint8Array.from(atob(base64), (char) => char.charCodeAt(0));
  return crypto.subtle.importKey(
    "pkcs8",
    raw,
    {
      name: "RSASSA-PKCS1-v1_5",
      hash: "SHA-256",
    },
    false,
    ["sign"],
  );
}

function assertGoogleSecrets(env) {
  if (!env.GOOGLE_SERVICE_ACCOUNT_EMAIL || !env.GOOGLE_PRIVATE_KEY) {
    throw new Error("Google Play service account secrets are not configured");
  }
}

async function readJson(request) {
  try {
    return await request.json();
  } catch {
    throw new Error("Invalid JSON request body");
  }
}

function failed(reason) {
  return { status: "failed", reason };
}

function json(payload, status = 200) {
  return new Response(JSON.stringify(payload, null, 2), {
    status,
    headers: {
      "Content-Type": "application/json; charset=utf-8",
      "Cache-Control": "no-store",
    },
  });
}

function base64UrlJson(value) {
  return base64UrlBytes(new TextEncoder().encode(JSON.stringify(value)));
}

function base64UrlBytes(bytes) {
  let binary = "";
  bytes.forEach((byte) => {
    binary += String.fromCharCode(byte);
  });
  return btoa(binary).replace(/\+/g, "-").replace(/\//g, "_").replace(/=+$/g, "");
}

function safeError(error) {
  const message = error && error.message ? String(error.message) : "unknown error";
  return message.replace(/[A-Za-z0-9_-]{80,}/g, "[redacted]");
}
