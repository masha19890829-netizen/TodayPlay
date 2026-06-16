const DEFAULT_KIMI_BASE_URL = "https://api.moonshot.cn/v1";
const DEFAULT_KIMI_MODEL = "moonshot-v1-8k";
const MAX_FREE_TEXT_LENGTH = 600;
const MAX_CANDIDATE_STOPS = 12;
const DEFAULT_KIMI_TIMEOUT_MS = 25000;
const LOCAL_ANDROID_HOSTS = new Set(["10.0.2.2", "127.0.0.1", "localhost"]);

export default {
  async fetch(request, env) {
    const url = new URL(request.url);

    if (request.method === "OPTIONS") {
      return withCors(new Response(null, { status: 204 }));
    }

    if (request.method === "GET" && url.pathname === "/health") {
      return json({ status: "ok", service: "todayplay-ai-route-gateway" });
    }

    if (request.method === "GET" && url.pathname === "/ai/provider/status") {
      return handleProviderStatus(request, env, url);
    }

    if (request.method === "POST" && url.pathname === "/ai/route/generate") {
      return handleRouteGenerate(request, env);
    }

    return json({ error: "not_found" }, 404);
  },
};

async function handleRouteGenerate(request, env) {
  if (!isAuthorized(request, env)) {
    return json({ error: "unauthorized" }, 401);
  }

  let body;
  try {
    body = await request.json();
  } catch {
    return json({ error: "invalid_json" }, 400);
  }

  const validation = validateRequest(body);
  if (!validation.ok) {
    return json({ error: validation.reason }, 400);
  }

  if (!env.KIMI_API_KEY) {
    return json(buildFallback(body, "kimi_not_configured"));
  }

  try {
    const kimiResult = await callKimi(body, env);
    const normalized = normalizeKimiResult(kimiResult, body);
    if (!normalized.ok) {
      return json(buildFallback(body, normalized.reason));
    }
    return json({
      status: "ready",
      provider: "kimi",
      usedFallback: false,
      ...normalized.value,
    });
  } catch (error) {
    return json(buildFallback(body, providerErrorReason(error)));
  }
}

async function handleProviderStatus(request, env, url) {
  const payload = {
    status: env.KIMI_API_KEY ? "configured" : "not_configured",
    provider: env.AI_PROVIDER || "kimi",
    model: env.KIMI_MODEL || DEFAULT_KIMI_MODEL,
    realCallProbe: "add ?probe=true to test Kimi without exposing secrets",
  };

  if (url.searchParams.get("probe") !== "true") {
    return json(payload);
  }

  if (!isAuthorized(request, env)) {
    return json({ error: "unauthorized" }, 401);
  }

  if (!env.KIMI_API_KEY) {
    return json({
      ...payload,
      probe: { status: "not_configured", reason: "kimi_not_configured" },
    });
  }

  try {
    await callKimiProbe(env);
    return json({
      ...payload,
      probe: { status: "ready", provider: "kimi" },
    });
  } catch (error) {
    return json({
      ...payload,
      probe: { status: "failed", reason: providerErrorReason(error) },
    });
  }
}

function isAuthorized(request, env) {
  const expected = env.AI_GATEWAY_SHARED_SECRET;
  if (!expected) return true;
  const provided = request.headers.get("x-todayplay-gateway-secret");
  if (provided === expected) return true;
  return isLocalAndroidRequest(request);
}

function isLocalAndroidRequest(request) {
  const url = new URL(request.url);
  return url.protocol === "http:" && LOCAL_ANDROID_HOSTS.has(url.hostname.toLowerCase());
}

function validateRequest(body) {
  if (!body || typeof body !== "object") return { ok: false, reason: "empty_body" };
  if (!stringValue(body.city)) return { ok: false, reason: "missing_city" };
  if (!stringValue(body.relationship)) return { ok: false, reason: "missing_relationship" };
  if (!Array.isArray(body.candidateStops) || body.candidateStops.length === 0) {
    return { ok: false, reason: "missing_candidate_stops" };
  }
  if (body.candidateStops.length > MAX_CANDIDATE_STOPS) {
    return { ok: false, reason: "too_many_candidate_stops" };
  }
  const cityKey = normalizeCity(body.city);
  const invalidStop = body.candidateStops.find((stop) => {
    return !stringValue(stop.stopId) || !stringValue(stop.name) || normalizeCity(stop.city) !== cityKey;
  });
  if (invalidStop) return { ok: false, reason: "candidate_city_mismatch" };
  if (stringValue(body.freeText) && body.freeText.length > MAX_FREE_TEXT_LENGTH) {
    return { ok: false, reason: "free_text_too_long" };
  }
  return { ok: true };
}

async function callKimi(body, env) {
  const baseUrl = (env.KIMI_BASE_URL || DEFAULT_KIMI_BASE_URL).replace(/\/$/, "");
  const model = env.KIMI_MODEL || DEFAULT_KIMI_MODEL;
  const data = await fetchKimiJson(`${baseUrl}/chat/completions`, env, {
    model,
    temperature: 0.35,
    max_tokens: 520,
    response_format: { type: "json_object" },
    messages: [
      {
        role: "system",
        content: [
          "You are TodayPlay's route planner.",
          "Return strict JSON only.",
          "You may only select stop IDs from candidateStops.",
          "Never invent places, ratings, popularity, social proof, official map data, or discounts.",
          "Keep relationship tasks respectful, public, low-pressure, and easy to exit.",
        ].join(" "),
      },
      {
        role: "user",
        content: JSON.stringify(buildSafePromptPayload(body)),
      },
    ],
  });

  const content = data?.choices?.[0]?.message?.content;
  if (!content || typeof content !== "string") {
    throw providerError("kimi_empty_content");
  }
  return parseJsonObject(content);
}

async function callKimiProbe(env) {
  const baseUrl = (env.KIMI_BASE_URL || DEFAULT_KIMI_BASE_URL).replace(/\/$/, "");
  const model = env.KIMI_MODEL || DEFAULT_KIMI_MODEL;
  const response = await fetchKimiJson(`${baseUrl}/chat/completions`, env, {
    model,
    temperature: 0,
    max_tokens: 64,
    response_format: { type: "json_object" },
    messages: [
      {
        role: "system",
        content: "Return strict JSON only.",
      },
      {
        role: "user",
        content: JSON.stringify({
          task: "TodayPlay Kimi connectivity probe",
          outputSchema: { ok: true },
        }),
      },
    ],
  });

  const content = response?.choices?.[0]?.message?.content;
  const parsed = typeof content === "string" ? parseJsonObject(content) : null;
  if (!parsed || parsed.ok !== true) {
    throw providerError("kimi_invalid_probe_json");
  }
}

async function fetchKimiJson(url, env, payload) {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort("kimi_timeout"), kimiTimeoutMs(env));
  try {
    const response = await fetch(url, {
      method: "POST",
      headers: {
        "content-type": "application/json",
        authorization: `Bearer ${env.KIMI_API_KEY}`,
      },
      body: JSON.stringify(payload),
      signal: controller.signal,
    });

    if (!response.ok) {
      throw providerError(`kimi_http_${response.status}`);
    }
    return await response.json();
  } catch (error) {
    if (error?.name === "AbortError") {
      throw providerError("kimi_timeout");
    }
    throw error;
  } finally {
    clearTimeout(timeoutId);
  }
}

function buildSafePromptPayload(body) {
  return {
    userIntent: {
      freeText: trimText(body.freeText, MAX_FREE_TEXT_LENGTH),
      city: body.city,
      relationship: body.relationship,
      moods: Array.isArray(body.moods) ? body.moods.slice(0, 8) : [],
      timeBudget: body.timeBudget || body.time || "",
      budget: body.budget || "",
      transportMode: body.transportMode || "",
      localeCode: body.localeCode || "zh-CN",
    },
    candidateStops: body.candidateStops.map((stop) => ({
      stopId: stop.stopId,
      name: stop.name,
      city: stop.city,
      district: stop.district || "",
      tags: Array.isArray(stop.tags) ? stop.tags.slice(0, 6) : [],
      budgetLevel: stop.budgetLevel || "",
      stayMinutes: Number(stop.stayMinutes || stop.estimatedStayMinutes || 30),
    })),
    outputSchema: {
      title: "short route title",
      storySetup: "one short setup sentence",
      routeSummary: "one short executable route summary",
      whyReasons: ["2-3 short reasons tied to user input"],
      selectedStopIds: ["subset of candidate stopId values"],
      stopReasons: { "stop-id": "why this stop fits" },
      safetyNote: "same-city and exit-friendly note",
    },
  };
}

function normalizeKimiResult(result, requestBody) {
  if (!result || typeof result !== "object") return { ok: false, reason: "invalid_ai_json" };
  const candidateIds = new Set(requestBody.candidateStops.map((stop) => stop.stopId));
  const selected = Array.isArray(result.selectedStopIds)
    ? result.selectedStopIds.filter((id) => typeof id === "string" && candidateIds.has(id))
    : [];
  if (selected.length === 0) return { ok: false, reason: "no_valid_stop_ids" };

  const cityKey = normalizeCity(requestBody.city);
  const selectedStops = requestBody.candidateStops.filter((stop) => selected.includes(stop.stopId));
  if (selectedStops.some((stop) => normalizeCity(stop.city) !== cityKey)) {
    return { ok: false, reason: "selected_city_mismatch" };
  }

  const stopReasons = {};
  if (result.stopReasons && typeof result.stopReasons === "object") {
    for (const id of selected) {
      if (typeof result.stopReasons[id] === "string") {
        stopReasons[id] = trimText(result.stopReasons[id], 140);
      }
    }
  }

  return {
    ok: true,
    value: {
      title: trimText(result.title, 42) || "TodayPlay route",
      storySetup: trimText(result.storySetup, 180) || "",
      routeSummary: trimText(result.routeSummary, 180) || "",
      whyReasons: Array.isArray(result.whyReasons)
        ? result.whyReasons.map((item) => trimText(item, 120)).filter(Boolean).slice(0, 3)
        : [],
      selectedStopIds: selected.slice(0, 4),
      stopReasons,
      safetyNote: trimText(result.safetyNote, 160) || "Same-city candidate stops only.",
    },
  };
}

function buildFallback(body, reason) {
  const selected = body.candidateStops.slice(0, Math.min(3, body.candidateStops.length)).map((stop) => stop.stopId);
  return {
    status: "fallback",
    provider: "local",
    usedFallback: true,
    reason,
    title: "Local route fallback",
    storySetup: "Use the app's local route generator when AI is unavailable.",
    routeSummary: "Same-city local sample route.",
    whyReasons: [
      `City stays in ${body.city}.`,
      "Only uses provided candidate stops.",
      "Keeps an exit-friendly route.",
    ],
    selectedStopIds: selected,
    stopReasons: Object.fromEntries(selected.map((id) => [id, "Selected from local candidates."])),
    safetyNote: "AI unavailable or invalid; Android should keep local fallback visible.",
  };
}

function parseJsonObject(raw) {
  const trimmed = raw.trim();
  if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
    return JSON.parse(trimmed);
  }
  const start = trimmed.indexOf("{");
  const end = trimmed.lastIndexOf("}");
  if (start >= 0 && end > start) {
    return JSON.parse(trimmed.slice(start, end + 1));
  }
  throw new Error("not_json");
}

function kimiTimeoutMs(env) {
  const parsed = Number(env.KIMI_TIMEOUT_MS);
  if (Number.isFinite(parsed) && parsed >= 1000 && parsed <= 30000) return parsed;
  return DEFAULT_KIMI_TIMEOUT_MS;
}

function providerError(reason) {
  const error = new Error(reason);
  error.reason = reason;
  return error;
}

function providerErrorReason(error) {
  return typeof error?.reason === "string" ? error.reason : "kimi_request_failed";
}

function stringValue(value) {
  return typeof value === "string" && value.trim().length > 0;
}

function trimText(value, maxLength) {
  if (typeof value !== "string") return "";
  return value.trim().slice(0, maxLength);
}

function normalizeCity(value) {
  return String(value || "").trim().toLowerCase();
}

function json(payload, status = 200) {
  return withCors(
    new Response(JSON.stringify(payload), {
      status,
      headers: { "content-type": "application/json; charset=utf-8" },
    }),
  );
}

function withCors(response) {
  const headers = new Headers(response.headers);
  headers.set("access-control-allow-origin", "*");
  headers.set("access-control-allow-methods", "GET,POST,OPTIONS");
  headers.set("access-control-allow-headers", "content-type,x-todayplay-gateway-secret");
  return new Response(response.body, { status: response.status, headers });
}
