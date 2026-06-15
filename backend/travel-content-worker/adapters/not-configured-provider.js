export const sourcePolicies = [
  {
    sourceName: "TodayPlay operator catalog",
    allowedUses: ["route_planning", "internal_testing", "store_listing_screenshots"],
    forbiddenUses: ["claiming social-platform popularity", "displaying unlicensed third-party images"],
    dataRetention: "Provider cache must expire within the provider contract window.",
    policyNotes: [
      "No scraping.",
      "No client-side or server-side scraping without explicit platform permission.",
      "Use official APIs, licensed datasets, merchant partnerships, or user-authorized links.",
      "Every production POI must carry ContentSource and SourcePolicy metadata.",
    ],
  },
];

export function searchCities() {
  return notConfigured("City search requires an authorized provider adapter.");
}

export function searchPois() {
  return notConfigured("POI search requires official APIs, licensed data, or merchant content.");
}

export function planItinerary() {
  return notConfigured("Route planning requires verified POI inputs and source-policy checks.");
}

export function importAuthorizedLink() {
  return {
    status: "manual_review",
    reason: "Share-link import requires platform-rule review and user authorization. Content is not extracted by this skeleton.",
  };
}

export function notConfigured(reason) {
  return {
    status: "not_configured",
    reason,
    compliance: {
      noScraping: true,
      requiresOfficialApiOrLicense: true,
      requiresContentSource: true,
      requiresSourcePolicy: true,
      requiresOfficialVerification: true,
    },
  };
}
