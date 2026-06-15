import {
  importAuthorizedLink,
  planItinerary,
  searchCities,
  searchPois,
  sourcePolicies,
} from "./adapters/not-configured-provider.js";

const SERVICE_NAME = "todayplay-travel-content";

export default {
  async fetch(request, env) {
    const url = new URL(request.url);

    if (request.method === "OPTIONS") {
      return withCors(new Response(null, { status: 204 }), env);
    }

    try {
      if (request.method === "GET" && url.pathname === "/health") {
        return json({ status: "ok", service: SERVICE_NAME }, 200, env);
      }

      if (request.method === "GET" && url.pathname === "/travel/sources/policies") {
        return json({ policies: sourcePolicies }, 200, env);
      }

      if (request.method === "GET" && url.pathname === "/travel/cities/search") {
        return json(searchCities(), 501, env);
      }

      if (request.method === "POST" && url.pathname === "/travel/poi/search") {
        await readJson(request);
        return json(searchPois(), 501, env);
      }

      if (request.method === "POST" && url.pathname === "/travel/itinerary/plan") {
        await readJson(request);
        return json(planItinerary(), 501, env);
      }

      if (request.method === "POST" && url.pathname === "/travel/content/import-link") {
        await readJson(request);
        return json(importAuthorizedLink(), 202, env);
      }

      return json({ status: "failed", reason: "Not found" }, 404, env);
    } catch (error) {
      return json({ status: "failed", reason: safeError(error) }, 500, env);
    }
  },
};

async function readJson(request) {
  try {
    return await request.json();
  } catch {
    throw new Error("Invalid JSON request body");
  }
}

function json(payload, status = 200, env = {}) {
  return withCors(
    new Response(JSON.stringify(payload, null, 2), {
      status,
      headers: {
        "Content-Type": "application/json; charset=utf-8",
        "Cache-Control": "no-store",
      },
    }),
    env,
  );
}

function withCors(response, env = {}) {
  const allowedOrigin = env.TODAYPLAY_ALLOWED_ORIGIN || "";
  if (allowedOrigin) {
    response.headers.set("Access-Control-Allow-Origin", allowedOrigin);
    response.headers.set("Vary", "Origin");
  }
  response.headers.set("Access-Control-Allow-Methods", "GET,POST,OPTIONS");
  response.headers.set("Access-Control-Allow-Headers", "Content-Type,Authorization");
  return response;
}

function safeError(error) {
  return error instanceof Error ? error.message : "Unknown error";
}
