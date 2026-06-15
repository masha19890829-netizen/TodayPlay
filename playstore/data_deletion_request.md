# TodayPlay Data Deletion Plan

Current app state:
- V0.9.41 has an in-memory account session scaffold for share attribution.
- Google sign-in is disabled until `GOOGLE_WEB_CLIENT_ID` and backend token verification are configured.
- No backend user profile.
- No server-side route history.
- No mandatory photo upload.
- Route history, task status, check-in state, and local points are stored on the user's device.
- Trial account state is not persisted; closing the app clears local tester or pending Google profile state.

## Current Deletion Method

Because current user data is local-only, users can delete route history directly inside the app:

```text
TodayPlay -> Privacy & Local Data -> Clear local history
```

This removes local route history, task status, local check-in state, feedback reasons, and local points stored by the app for completion cards and history replay.

Users can also delete all app data through Android system settings:

```text
Android Settings -> Apps -> TodayPlay -> Storage -> Clear storage
```

This removes local route history, task status, local check-in state, local points, and local preferences stored by the app.

## Future In-App Deletion Requirement

Before enabling persistent accounts, backend sync, analytics identity, or photo upload, extend the in-app privacy entry:

```text
TodayPlay -> Privacy & Local Data -> Clear local history
TodayPlay -> Privacy & Local Data -> Request account/data deletion
```

The account/data deletion flow should:
- Verify the user's identity.
- Delete or anonymize server-side route history.
- Delete uploaded media.
- Revoke refresh tokens or sessions.
- Remove analytics/user identifiers where technically possible.
- Keep only legally required billing records.
- Send a completion confirmation.

## Google Play Disclosure

If the app later lets users create accounts, Google Play may require a public data deletion URL. Use the deployed privacy site as the base and add:

```text
https://YOUR_DOMAIN/privacy/todayplay/delete-data
```

Do not claim a backend deletion URL exists until the account/backend system actually exists.
